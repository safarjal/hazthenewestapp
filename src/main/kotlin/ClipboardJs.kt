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
    val questionTxt = inputContainer.questionText
    val saailaDetails = inputContainer.saailaDetails
    val divider = "${UnicodeChars.BLUE_SWIRL}➖➖➖➖➖➖${UnicodeChars.BLUE_SWIRL}"
    val answerTxt = answerElement.querySelector(".${Ids.Results.CONTENT_ANSWER}")?.innerHTML
        ?.replace("<p>", "")
        ?.replace("</p>", "\n\n")
        ?.replace("\n\n\n\n", "\n\n")
        ?.replace(Regex("<.*?>"), "")
    console.log("ans", answerTxt)
    return "*$dateStr*\n\n$saailaDetails\n\n$questionTxt\n\n$divider\n\n$answerTxt".replace(Regex("\\n{3,}"), "\n\n")
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
    console.log("copy text", copyTxt)

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

    console.log("clipboard", clipboard)

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