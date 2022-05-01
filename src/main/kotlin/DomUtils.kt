import kotlinx.html.*
import kotlinx.html.js.*
import org.w3c.dom.*

fun TagConsumer<HTMLElement>.content(classes: String? = null, block : P.() -> Unit = {}) {
    p(classes = classes) {
        id = "content"
        style = "white-space: pre-wrap;"
        block()
    }
}

fun FlowContent.timeInput(
    isDateOnlyLayout: Boolean,
    minTimeInput: String,
    maxTimeInput: String,
    indexWithinRow: Int,
    block: INPUT.() -> Unit = {}
) {
    timeInput(isDateOnlyLayout, minTimeInput, maxTimeInput) {
        onChangeFunction = { event -> setMinMaxForTimeInputsOnInput(event, indexWithinRow) }
        block()
    }
}

fun FlowContent.timeInput(
    inputContainerToCopyFrom: HTMLElement,
    timeInputToCopyFrom: HTMLInputElement,
    indexWithinRow: Int,
    block: INPUT.() -> Unit = {}
) {
    timeInput(inputContainerToCopyFrom, timeInputToCopyFrom) {
        onChangeFunction = { event -> setMinMaxForTimeInputsOnInput(event, indexWithinRow) }
        block()
    }
}

fun FlowContent.timeInput(
    isDateOnlyLayout: Boolean,
    minTimeInput: String,
    maxTimeInput: String,
    block: INPUT.() -> Unit = {}
) {
    timeInput(isDateOnlyLayout) {
        min = minTimeInput
        max = maxTimeInput
        block()
    }
}

fun FlowContent.timeInput(
    inputContainerToCopyFrom: HTMLElement,
    timeInputToCopyFrom: HTMLInputElement? = null,
    block: INPUT.() -> Unit = {}
) {
    timeInput(inputContainerToCopyFrom.isDateOnly) {
        block()
        @Suppress("NAME_SHADOWING")
        val timeInputToCopyFrom = timeInputToCopyFrom ?: inputContainerToCopyFrom.getChildById(id) as HTMLInputElement
        value = timeInputToCopyFrom.value
        min = timeInputToCopyFrom.min
        max = timeInputToCopyFrom.max
    }
}

fun FlowContent.timeInput(
    isDateOnlyLayout: Boolean,
    block: INPUT.() -> Unit = {}
) {
    customDateTimeInput(isDateOnlyLayout) {
        required = true
        onClickFunction = { event ->
            setMaxToCurrentTimeForTimeInputs(findInputContainer(event))
        }
        block()
    }
}

fun FlowContent.makeLabel(inputId: String, englishText: String, urduText: String, extraClasses: String = "", block: LABEL.() -> Unit = {}) {
    label {
        htmlFor = inputId
        classes = setOf(CssC.ENGLISH, extraClasses)
        block()
        +englishText
    }
    label {
        htmlFor = inputId
        classes = setOf(CssC.URDU, extraClasses)
        block()
        +urduText
    }
}

fun FlowContent.makeSwitch(inputId: String, block: INPUT.() -> Unit = {}) {
    label(classes = CssC.SWITCH) {
        checkBoxInput {
            id = inputId
            block()
        }
        span(classes = "${CssC.SLIDER} ${CssC.ROUND}")
    }
}

fun FlowContent.makeIkhtilafiMasla(inputId: String, englishText: String, urduText: String, extraClasses: String? = null, block: DIV.() -> Unit = {}) {
    div(classes = "${CssC.ROW} $extraClasses") {
        div {
            makeLabel(inputId, englishText, urduText)
            makeSwitch(inputId)
        }
        block()
    }
}

fun TagConsumer<HTMLElement>.makeDropdownOptions(
    isSelected: Boolean,
    optionVal: String,
    englishText: String,
    urduText: String,
    extraClasses: String = "",
    block: OPTION.() -> Unit = {}
) {
    option {
        classes = setOfNotNull(
            CssC.ENGLISH,
            extraClasses,
            if (languageSelector.value != Vls.Langs.ENGLISH) CssC.LANG_INVIS else null
        )
        selected = isSelected && languageSelector.value == Vls.Langs.ENGLISH
        value = optionVal
        id = optionVal
        block()
        +englishText
    }
    option {
        classes = setOfNotNull(
            CssC.URDU,
            extraClasses,
            if (languageSelector.value != Vls.Langs.URDU) CssC.LANG_INVIS else null
        )
        selected = isSelected && languageSelector.value == Vls.Langs.URDU
        value = optionVal
        id = optionVal
        block()
        +urduText
    }
}

fun FlowContent.makeNumberInput(inputId: String, inputVal: String?, inputRange: IntRange, block: INPUT.() -> Unit = {}) {
    input {
        id = inputId
        name = inputId
        value = inputVal.orEmpty()
        //TODO: Uncomment this later after fixing validator
        onInputFunction = { event -> (event.currentTarget as HTMLInputElement).validateAadat(inputRange) }
        block()
    }
}

private fun HTMLInputElement.validateAadat(validityRange: ClosedRange<Int>) {
    val errormessage = if(languageSelector.value == Vls.Langs.ENGLISH) { StringsOfLanguages.ENGLISH.incorrectAadat }
    else {StringsOfLanguages.URDU.incorrectAadat}
    if (value.contains("-")) {
//        println("DASH!")
        setCustomValidity(try {
            val arr = value.split("-")
//            console.log("IN THERE?", !arr.any { it.toInt() in validityRange }, !(arr.any { it.toInt() in validityRange }) )
            require( arr.all { it.toInt() in validityRange } ) { errormessage }
            ""
        } catch (e: IllegalArgumentException) {
            e.message ?: errormessage
        })
    }
    else {
        value = value.replace("[^0-9:]".toRegex(), "")
        val doubleValidityRange = validityRange.start.toDouble()..validityRange.endInclusive.toDouble()
        setCustomValidity(try {
            val days = (parseDays(value)?.div(MILLISECONDS_IN_A_DAY))?.toDouble()
            require(days == null || days in doubleValidityRange) { errormessage }
            ""
        } catch (e: IllegalArgumentException) {
            e.message ?: errormessage
        })
    }
}

fun TagConsumer<HTMLElement>.makeSpans(englishText: String, urduText: String, block: SPAN.() -> Unit = {}) {
    span(classes = "${CssC.ENGLISH} ${if (languageSelector.value == Vls.Langs.ENGLISH) "" else CssC.LANG_INVIS}") {
        block()
        +englishText
    }
    span(classes = "${CssC.URDU} ${if (languageSelector.value == Vls.Langs.URDU) "" else CssC.LANG_INVIS}") {
        block()
        +urduText
    }
}

fun FlowContent.makeTextAreaInput(inputId: String, height: String = "auto", block: TEXTAREA.() -> Unit = {}) {
    textArea {
        id = inputId
        style = "height: $height"
        block()
        onInputFunction = { event ->
            val txtarea = event.currentTarget as HTMLTextAreaElement
            txtarea.dir = "auto"
            txtarea.style.height = height
            txtarea.style.height = "${txtarea.scrollHeight + 6}px"
        }
    }
}

fun TagConsumer<HTMLElement>.oneRow(starter: Boolean = true, desc: String = "", ender: Boolean = false, block: () -> Unit = {}) {
    if (starter) div { id = "margin-cell" }                     // Empty buffer margin
    div {                                                       // Description of Inputs
        id = if (desc.isEmpty()) "empty_desc" else "desc"
        classes = if (desc.isEmpty()) emptySet() else setOf(
            CssC.TABLE_CELL,
            CssC.BORDERED,
            CssC.DESCRIPTION
        )
        +desc
    }
    block()                                                     // Row Filler
    if (ender) div { id = "formerly_half_cell" }                // Extra trailing cell to accommodate dates
}
