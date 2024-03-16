import kotlinx.browser.window
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.datetime.internal.JSJoda.Instant
import kotlinx.html.org.w3c.dom.events.Event
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.get

@JsModule("clipboard")
@JsNonModule
external class ClipboardJS(selector: String, options: dynamic = definedExternally) {
    fun on(event: String, callback: (e: ClipboardEvent) -> Unit)
}

external interface ClipboardEvent {
    val action: String
    val text: String
    val trigger: dynamic // Use 'dynamic' for any type
    fun clearSelection()
}

private fun getPreAnswerDetails(inputContainer: HTMLElement, answerElement: HTMLElement): String {
    val dateStr = languagedDateFormat(Instant.now(), TypesOfInputs.DATE_ONLY, languageSelected, addYear = true)
    val questionTxt = inputContainer.questionText + NEW_LINE
    val saailaDetails = inputContainer.saailaDetails + NEW_LINE
    val answerer = if (!savedDisplayName.isNullOrEmpty() && savedDisplayName != "null")
        "Answered by: $savedDisplayName$NEW_LINE" else ""
    val divider = "${UnicodeChars.BLUE_SWIRL}➖➖➖➖➖➖${UnicodeChars.BLUE_SWIRL}$NEW_LINE"

    val answerTxt = answerElement.querySelector(".${Ids.Results.CONTENT_ANSWER}")?.innerHTML
        ?.replaceHtmlTagsWithStringSafe()
    return ("*$dateStr*$NEW_LINE" +
            saailaDetails +
            questionTxt +
            answerer +
            divider +
            answerTxt).replace(Regex("\\n{3,}"), NEW_LINE)
}

private fun HTMLElement.saveStatus(): String? = dataset[SaveMaslaId.SAVED]
private fun HTMLElement.isNotSaved(): Boolean = saveStatus() == "false"
private fun HTMLElement.save(id: String) = setAttribute(SaveMaslaId.DATA_SAVED, id)

private fun HTMLElement.showTooltip() {
    style.visibility = "visible"
    style.opacity = "1"
}

private fun HTMLElement.hideTooltip() {
    style.visibility = "hidden"
    style.opacity = "0"
}

private fun saveWithoutCopy(inputContainer: HTMLElement, small: HTMLElement) {
    return saveAndCopy(inputContainer, small, null)
}
@OptIn(DelicateCoroutinesApi::class)
private fun saveAndCopy(inputContainer: HTMLElement, small: HTMLElement, preCopiedText: String?) {
    var copyTxt = preCopiedText
    var smallTxt: String
    var response: LoadData? = null
    GlobalScope.launch { response = getDataFromInputsAndSend(inputContainer) }.invokeOnCompletion {
        if (response != null) {
            val maslaId = response!!.id
            copyTxt = "_Masla Id: ${maslaId}_\n" + preCopiedText
            smallTxt = "Saved: Id #$maslaId"
            if (response!!.user_id == null) smallTxt += englishStrings.loginAgain
            inputContainer.contentContainer.save(maslaId.toString())
        } else {
            smallTxt = "Copied."
            window.alert("Masla has not been saved. However, it has been copied.")
            window.setTimeout({ small.hideTooltip() }, 5000)
        }
        if (preCopiedText?.isNotEmpty() == true) window.navigator.clipboard.writeText(copyTxt!!)
        small.textContent = smallTxt
        small.showTooltip()
    }
}

fun saveText(event: Event) {
    val answerElement =
        (event.currentTarget as HTMLElement).getAncestor<HTMLDivElement> { it.id == Ids.Results.CONTENT_WRAPPER }
    val inputContainer = findInputContainer(event)
    val tooltip = answerElement?.querySelector(".${Ids.Results.COPY_TOOLTIP}") as HTMLElement
    if (inputContainer.contentContainer.isNotSaved()) {
        saveWithoutCopy(inputContainer, tooltip)
    } else {
        tooltip.textContent = "Copied! Id: #${inputContainer.contentContainer.saveStatus()}"
        tooltip.showTooltip()
        window.setTimeout({ tooltip.hideTooltip() }, 5000)
    }
}

fun copyText(event: Event) {
    val answerElement =
        (event.currentTarget as HTMLElement).getAncestor<HTMLDivElement> { it.id == Ids.Results.CONTENT_WRAPPER }
    val inputContainer = findInputContainer(event)
    var copyTxt = getPreAnswerDetails(inputContainer, answerElement!!)

    val tooltip = answerElement.querySelector(".${Ids.Results.COPY_TOOLTIP}") as HTMLElement
    val smallTxt: String

    if (inputContainer.contentContainer.isNotSaved()) {
        saveAndCopy(inputContainer, tooltip, copyTxt)
    } else {
        val maslaId = inputContainer.contentContainer.saveStatus()
        smallTxt = "Copied! Id: #${maslaId}"
        copyTxt = "_Masla Id: ${maslaId}_\n" + copyTxt
        copyTxt.let { window.navigator.clipboard.writeText(it) }
        tooltip.textContent = smallTxt
        window.setTimeout({ tooltip.hideTooltip() }, 5000)
    }
}

fun copyClipboard(id: String) {
    val clipboard = ClipboardJS("#$id")
//    val clipboard = ClipboardJS("#${Ids.Results.COPY_BTN}", buildJsObject {
//        val text: (target: HTMLElement) -> String = { copyBtn ->
//            copyTxt
//        }
//        this.text = text
//        // Other options can be added here, e.g., container, etc.
//    })

    clipboard.on("success") { e ->
        console.info("Action: ${e.action}")
        console.info("Text: ${e.text}")
        console.info("Trigger: ${e.trigger}")

        e.clearSelection()
    }

    clipboard.on("error") { e ->
        console.error("Error: ${e}", e)
        console.error("Action: ${e.action}")
        console.error("Trigger: ${e.trigger}")
    }
}