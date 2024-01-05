@file:Suppress("SpellCheckingInspection")
@file:OptIn(DelicateCoroutinesApi::class)

import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.datetime.internal.JSJoda.Instant
import kotlinx.html.*
import kotlinx.html.dom.prepend
import kotlinx.html.js.*
import kotlinx.html.org.w3c.dom.events.Event
import org.w3c.dom.*
import kotlin.js.Json
import kotlin.js.json

// MAKE ELEMENTS
fun TagConsumer<HTMLElement>.content(classes: String? = null, block : P.() -> Unit = {}) {
    p(classes = classes) {
        id = "content"
        style = "white-space: pre-wrap;"
        block()
    }
}

fun FlowContent.copyBtn(divClass:String, btnClass: String? = null) {
    div(classes = divClass) {
        button(classes = btnClass) {
            onClickFunction = { event ->
                copyText(event)
            }
            +"Save and Copy ⎙"
        }
        br()
        small(classes = btnClass)
    }
}

// Dealing with time inputs
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

fun FlowContent.pregnancyTimeInput(inputContainerToCopyFrom: HTMLElement?, inputId: String = "", block: INPUT.() -> Unit = {}) {
    var disable = true
    if (inputContainerToCopyFrom != null) { disable = !inputContainerToCopyFrom.isNifas }
    timeInput(IS_DEFAULT_INPUT_MODE_DATE_ONLY) {
        disabled = disable
        id = inputId
        name = inputId
        block()
    }
}

fun FlowContent.makeLabel(inputId: String, text: Strings.() -> String, extraClasses: String = "", block: LABEL.() -> Unit = {}) {
    label {
        htmlFor = inputId
        classes = setOf(CssC.ENGLISH, extraClasses)
        block()
        +StringsOfLanguages.ENGLISH.text()
    }
    label {
        htmlFor = inputId
        classes = setOf(CssC.URDU, extraClasses)
        block()
        +StringsOfLanguages.URDU.text()
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

fun FlowContent.makeIkhtilafiMasla(inputId: String, text: Strings.() -> String, extraClasses: String? = null, block: DIV.() -> Unit = {}) {
    div(classes = "${CssC.ROW} $extraClasses") {
        div {
            makeLabel(inputId, text)
            makeSwitch(inputId)
        }
        block()
    }
}

fun TagConsumer<HTMLElement>.makeDropdownOptions(
    isSelected: Boolean,
    optionVal: String,
    text: Strings.() -> String,
    extraClasses: String = "",
    block: OPTION.() -> Unit = {}
) {
    option {
        classes = setOfNotNull(
            CssC.ENGLISH,
            extraClasses,
            if (languageSelected != Vls.Langs.ENGLISH) CssC.LANG_INVIS else null
        )
        selected = isSelected && languageSelected == Vls.Langs.ENGLISH
        value = optionVal
        id = optionVal
        block()
        +StringsOfLanguages.ENGLISH.text()
    }
    option {
        classes = setOfNotNull(
            CssC.URDU,
            extraClasses,
            if (languageSelected != Vls.Langs.URDU) CssC.LANG_INVIS else null
        )
        selected = isSelected && languageSelected == Vls.Langs.URDU
        value = optionVal
        id = optionVal
        block()
        +StringsOfLanguages.URDU.text()
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

fun TagConsumer<HTMLElement>.makeSpans(englishText: String, urduText: String, block: SPAN.() -> Unit = {}) {
    span(classes = "${CssC.ENGLISH} ${if (languageSelected == Vls.Langs.ENGLISH) "" else CssC.LANG_INVIS}") {
        block()
        +englishText
    }
    span(classes = "${CssC.URDU} ${if (languageSelected == Vls.Langs.URDU) "" else CssC.LANG_INVIS}") {
        block()
        +urduText
    }
}

fun TagConsumer<HTMLElement>.makeSpans(text: Strings.() -> String, block: SPAN.() -> Unit = {}) {
    span(classes = "${CssC.ENGLISH} ${if (languageSelected == Vls.Langs.ENGLISH) "" else CssC.LANG_INVIS}") {
        block()
        +StringsOfLanguages.ENGLISH.text()
    }
    span(classes = "${CssC.URDU} ${if (languageSelected == Vls.Langs.URDU) "" else CssC.LANG_INVIS}") {
        block()
        +StringsOfLanguages.URDU.text()
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

fun TagConsumer<HTMLElement>.makeTzOptions() {
    dtStrings.forEach {
        option {
            value = it.tz
            id = it.tz
            +it.info
        }
    }
}

// Deal with input tables
fun TagConsumer<HTMLElement>.addBeforeButton(duration: Boolean = false) {
    button(type = ButtonType.button, classes = CssC.PLUS) {
        +"\u2795 \u25B2"
        title = "Add at Start"
        id = Ids.Row.BUTTON_ADD_BEFORE
        onClickFunction = { event ->
            val inputContainer = findInputContainer(event)
            if (duration) addBeforeDurationRow(inputContainer)
            else addBeforeInputRow(inputContainer)
        }
    }
}

private fun TR.addRemoveButtonsTableData(duration: Boolean = false) {
    td {
        id = Ids.Row.BUTTONS_CONTAINER
        addButton(duration)
        removeButton(duration)
    }
}
private fun FlowContent.addButton(duration: Boolean = false) {
    button(type = ButtonType.button, classes = CssC.PLUS) {
        +"\u2795"
        title = "Add"
        onClickFunction = { event ->
            val row = findRow(event)
            val inputContainer = findInputContainer(event)
            if (duration) {
                addDurationRow(inputContainer, row)
            } else {
                addInputRow(inputContainer, row)
            }
        }
    }
}
private fun FlowContent.removeButton(duration: Boolean = false) {
    button(type = ButtonType.button, classes = CssC.MINUS) {
        +"\u274C"
        title = "Remove"
        id = Ids.Row.BUTTON_REMOVE
        onClickFunction = { event ->
            val row = findRow(event)
            val inputContainer = findInputContainer(event)
            if (!duration) {
                updateMinMaxForTimeInputsBeforeRemovingRow(inputContainer, row.rowIndexWithinTableBody)
            }
            row.remove()
            setupFirstRow(inputContainer, duration)
        }
    }
}

// DateTime
private fun TagConsumer<HTMLElement>.inputRow(
    isDateOnlyLayout: Boolean,
    minTimeInput: String,
    maxTimeInput: String,
    disable: Boolean = false) {
    tr {
        td {
            timeInput(isDateOnlyLayout, minTimeInput, maxTimeInput, indexWithinRow = 0) {
                id = Ids.Row.INPUT_START_TIME
                disabled = disable
            }
        }
        td {
            timeInput(isDateOnlyLayout, minTimeInput, maxTimeInput, indexWithinRow = 1) {
                id = Ids.Row.INPUT_END_TIME
                disabled = disable
            }
        }
        addRemoveButtonsTableData()
    }
}
private fun TagConsumer<HTMLElement>.copyInputRow(
    inputContainerToCopyFrom: HTMLElement,
    startTimeInputToCopyFrom: HTMLInputElement,
    endTimeInputToCopyFrom: HTMLInputElement,
    disable: Boolean = false
) {
    tr {
        td {
            timeInput(inputContainerToCopyFrom, startTimeInputToCopyFrom, indexWithinRow = 0) {
                id = Ids.Row.INPUT_START_TIME
                disabled = disable
            }
        }
        td {
            timeInput(inputContainerToCopyFrom, endTimeInputToCopyFrom, indexWithinRow = 1) {
                id = Ids.Row.INPUT_END_TIME
                disabled = disable
            }
        }
        addRemoveButtonsTableData()
    }
}

// Duration
private fun TagConsumer<HTMLElement>.makeDurationSelect(
    disable: Boolean,
    selectedOption: String,
    preg: Boolean,
    mustabeen: Boolean = true) {
    select {
        id = Ids.Row.INPUT_TYPE_OF_DURATION
        name = Ids.Row.INPUT_TYPE_OF_DURATION
        disabled = disable
        onChangeFunction = { event -> onChangeDurationOption(event) }
        makeDropdownOptions(selectedOption == Vls.Opts.DAM, Vls.Opts.DAM, Strings::dam)
        makeDropdownOptions(selectedOption == Vls.Opts.TUHR, Vls.Opts.TUHR, Strings::tuhr)
        makeDropdownOptions(selectedOption == Vls.Opts.HAML, Vls.Opts.HAML, Strings::pregduration,
            CssC.NIFAS + " " + if (!preg) CssC.INVIS else null
        )
        // Wiladat
        makeDropdownOptions(
            selectedOption == Vls.Opts.WILADAT && mustabeen,
            Vls.Opts.WILADAT, Strings::birthduration,
            CssC.NIFAS + " " + CssC.MUSTABEEN + " " + if (!preg || !mustabeen) CssC.INVIS else null
        )
        // Isqaat
        makeDropdownOptions(
            selectedOption == Vls.Opts.WILADAT && !mustabeen,
            Vls.Opts.WILADAT, Strings::isqat,
            CssC.NIFAS + " " + CssC.NOT_MUSTABEEN + " " + if (!preg || mustabeen) CssC.INVIS else null
        )
    }
}

private fun TagConsumer<HTMLElement>.copyDurationInputRow(
    aadat: String,
    selectedOption: String,
    disable: Boolean,
    preg: Boolean,
    mustabeen: Boolean) {
    tr {
        td {
            makeNumberInput(Ids.Row.INPUT_DURATION, aadat, (0..10000)) {
                required = true
                disabled = disable
            }
        }
        td {
            makeDurationSelect(disable, selectedOption, preg, mustabeen)
        }
        addRemoveButtonsTableData(true)
    }
}
private fun TagConsumer<HTMLElement>.durationInputRow(
    lastWasDam: Boolean,
    disable: Boolean,
    preg: Boolean = false,
    mustabeen: Boolean = true) {
    tr {
        td {
            makeNumberInput(Ids.Row.INPUT_DURATION, "", (0..10000)) {
                disabled = disable
                required = true
            }
        }
        td { makeDurationSelect(disable, if (lastWasDam) Vls.Opts.TUHR else Vls.Opts.DAM, preg, mustabeen) }
        addRemoveButtonsTableData(true)
    }
}

// Cloning
private fun addTheRemoveInputsContainerButton(inputContainer: HTMLElement) {
    inputContainer.inputsContainerCloneButton.before {
        button(type = ButtonType.button, classes = "${CssC.MINUS} ${CssC.DEV}") {
            +"\u274C"
            id = Ids.InputContainers.INPUTS_CONTAINER_REMOVE_BUTTON
            style = "float: right"
            onClickFunction = { event ->
                removeInputsContainer(findInputContainer(event))
            }
        }
    }
}

private fun TagConsumer<HTMLElement>.calcAllBtn(text: String) {
    button {
        classes = setOf(
            CssC.CALC_BTN,
            CssC.ENGLISH,
            if (languageSelected == Vls.Langs.ENGLISH) "" else CssC.INVIS
        )
        +text
        onClickFunction = { calcAll() }
    }
    button {
        classes = setOf(
            CssC.CALC_BTN,
            CssC.URDU,
            if (languageSelected == Vls.Langs.URDU) "" else CssC.INVIS
        )
        +text
        onClickFunction = { calcAll() }
    }
}

// Compare Table
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

// DEAL-WITH-DOM FUNCTIONS
// All maslas are the same
fun setOptionInSelect(selectElement: HTMLSelectElement, selectedOption: String = selectElement.value) {
    // Selects the language appropriate option
    selectElement.children
        .asList()
        .map { it as HTMLOptionElement }
        .firstOrNull { option ->
            option.value == selectedOption && option.classList.contains(languageSelected) }
        ?.selected = true
}
fun maslaChanging(event: Event) {
    val selectedOption = (event.currentTarget as HTMLSelectElement).value
    inputsContainers.forEach {
        setOptionInSelect(it.maslaSelect, selectedOption)
        disableTree(it)
    }
}

// Invising Tree
private fun disableByClass(classSelector: String, inputContainer: HTMLElement, disable: Boolean, classInvis: String = CssC.INVIS) {
    inputContainer.getElementsByClassName(classSelector)
        .asList()
        .forEach { row ->
            row.classList.toggle(classInvis, disable)
            row.querySelectorAll("input")
                .asList()
                .map { input ->
                    input as HTMLInputElement
                    input.disabled = disable
                    if (disable) input.value = ""
                }
        }
}
private fun disableByMasla(inputContainer: HTMLElement) {
    disableByClass(CssC.NIFAS, inputContainer, !inputContainer.isNifas)
    disableByClass(CssC.MUTADA, inputContainer, inputContainer.isMubtadia)
}
fun disableTree(inputContainer: HTMLElement) {
    val isNifas = inputContainer.isNifas
    val isMubtadia = inputContainer.isMubtadia
    val isDateOrTime = !inputContainer.isDuration
    val isMustabeen  = inputContainer.isMustabeen

    disableByClass(CssC.DATE_OR_TIME_AADAT, inputContainer, !isDateOrTime)
    disableByMasla(inputContainer)
    disableByClass("${CssC.DATE_OR_TIME_AADAT} ${CssC.NIFAS}", inputContainer, !isNifas || !isDateOrTime)
    disableByClass("${CssC.DATE_OR_TIME_AADAT} ${CssC.MUTADA}", inputContainer, isMubtadia || !isDateOrTime)
    disableByClass("${CssC.NIFAS} ${CssC.MUSTABEEN}", inputContainer, !isNifas || !isMustabeen)
    disableByClass("${CssC.NIFAS} ${CssC.NOT_MUSTABEEN}", inputContainer, !isNifas || isMustabeen)
    disableByClass(CssC.DATETIME_ONLY, inputContainer, !inputContainer.isDateTime)

    val mawjoodaFaasidCheck = inputContainer.getChildById(Ids.Inputs.MAWJOODA_FAASID_CHECKBOX) as HTMLInputElement
    if (inputContainer.isMubtadia) {
        mawjoodaFaasidCheck.checked = true
        mawjoodaFaasidCheck.disabled = true
    }

    disableByClass(CssC.ZAALLA, inputContainer, !inputContainer.isZaalla)
}

// Ensure Aadaat in Range
private fun HTMLInputElement.validateAadat(validityRange: ClosedRange<Int>) {
    val errormessage = if(languageSelected == Vls.Langs.ENGLISH) { StringsOfLanguages.ENGLISH.incorrectAadat }
    else {StringsOfLanguages.URDU.incorrectAadat}
    if (value.contains("-") && devmode) {
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
            val days = (parseDays(value)?.getDays())?.toDouble()
            require(days == null || days in doubleValidityRange) { errormessage }
            ""
        } catch (e: IllegalArgumentException) {
            e.message ?: errormessage
        })
    }
}

// Switch Between Wilaadat/Isqaat - Beauty Only
fun switchWiladatIsqat(inputContainer: HTMLElement) {
    disableByClass(CssC.MUSTABEEN, inputContainer, !inputContainer.isMustabeen)
    disableByClass(CssC.NOT_MUSTABEEN, inputContainer, inputContainer.isMustabeen)

    // Get All Wiladat/Isqat Selected. Should be one, but maybe someone has messed up so allow for more.
    val wiladatSelect = inputContainer.haizDurationInputTable.querySelectorAll("select")
        .asList()
        .map { it as HTMLSelectElement }
        .filter { select -> select.value == Vls.Opts.WILADAT }

    // If mustabeen, select wiladat
    if (inputContainer.isMustabeen) wiladatSelect.forEach { select ->
        select.children.asList().map { it as HTMLOptionElement }
            .first { option -> option.classList.contains(CssC.MUSTABEEN) }
            .selected = true
    }
    // If !mustabeen, select isqaat
    else wiladatSelect.forEach { select ->
        select.children.asList().map { it as HTMLOptionElement }
            .first { option -> option.classList.contains(CssC.NOT_MUSTABEEN) }
            .selected = true
    }
}

// Ensure only two of AadatHaiz, AadatTuhr, CycleLength active at a time
fun onlyTwo(event: Event) {
    val inputContainer = findInputContainer(event)
    val inputsList = listOf(inputContainer.aadatHaz, inputContainer.aadatTuhr, inputContainer.cycleLength)
    var inputsInUse = 0
    inputsList.forEach { if (it.value.isNotEmpty()) inputsInUse += 1 }
    if (inputsInUse == 2) inputsList.first { it.value.isEmpty() }.disabled = true
    else if (inputsInUse < 2) {
        inputsList.forEach { it.disabled = false }
        disableByClass(CssC.ZAALLA, inputContainer, !inputContainer.isZaalla)
    }
}

// DEALING WITH THE INPUT TABLES

// Switching Date/Duration Inputs
private fun disableDateTable(inputContainer: HTMLElement, disable: Boolean = inputContainer.isDuration) {
    for (timeInput in inputContainer.timeInputsGroups) {
        for (input in timeInput) {
            input.disabled = disable
        }
    }
    for (durationInput in inputContainer.durationInputsGroups) {
        for (input in durationInput) {
            input.asDynamic().disabled = !disable
        }
    }
//    disableByClass(CssC.DATETIME_AADAT, CssC.DUR_INVIS, inputContainer, disable)
    disableTree(inputContainer)
}
private fun switchToDurationTable(inputContainer: HTMLElement, isDuration: Boolean = inputContainer.isDuration) {
    disableDateTable(inputContainer, isDuration)
    inputContainer.haizInputTable.visibility = !isDuration
    inputContainer.haizDurationInputTable.visibility = isDuration
}
private fun typeChanging(
    inputContainer: HTMLElement,
    selectedOption: String,
    isDateOnly: Boolean,
    isDateTime: Boolean,
    timeZone: String? = null) {
    setOptionInSelect(inputContainer.typeSelect, selectedOption)

    for (timeInput in inputContainer.timeInputsGroups.flatten()) {
        val newValue = convertInputValue(timeInput.value, isDateOnly, timeZone)
        val newMin = convertInputValue(timeInput.min, isDateOnly, timeZone)
        val newMax = convertInputValue(timeInput.max, isDateOnly, timeZone)

        val dateInputType = if (isDateOnly) InputType.date else InputType.dateTimeLocal
        timeInput.type = dateInputType.realValue

        timeInput.value = newValue
        timeInput.min = newMin
        timeInput.max = newMax
    }
    if (isDateTime) {
        setMaxToCurrentTimeForTimeInputs(inputContainer)
    }
    switchToDurationTable(inputContainer)
}
fun onClickTypeConfigurationSelectDropdown(event: Event) {
    val selected = (event.currentTarget as HTMLSelectElement).value
    val inputContainer = findInputContainer(event)
    val isDateOnly = inputContainer.isDateOnly
    val isDateTime = inputContainer.isDateTime
    val timeZone =  if (inputContainer.timezoneSelect.disabled) null else inputContainer.timezoneSelect.value
    // Ensure all input containers are same type
    inputsContainers.forEach { typeChanging(it, selected, isDateOnly, isDateTime, timeZone) }
}

// Add new rows at the start
private fun addBeforeDurationRow(inputContainer: HTMLElement) {
    val firstIsDam = inputContainer.haizDurationInputDatesRows.first().damOrTuhr in setOf(Vls.Opts.DAM, Vls.Opts.WILADAT)
    inputContainer.hazDurationInputTableBody.prepend {
        durationInputRow(firstIsDam, false, inputContainer.isNifas, inputContainer.isMustabeen)
    }
    setupFirstRow(inputContainer, true)
}
private fun addBeforeInputRow(inputContainer: HTMLElement) {
    val row = inputContainer.hazInputTableBody.firstChild as HTMLTableRowElement

    inputContainer.hazInputTableBody.prepend {
        inputRow(
            inputContainer.isDateOnly,
            minTimeInput = "",
            maxTimeInput = row.startTimeInput.run { value.takeUnless(String::isEmpty) ?: max }
        )
    }
    setupRows(inputContainer)
}

// Add new rows after
private fun addDurationRow(inputContainer: HTMLElement, row: HTMLTableRowElement) {
    val rowIsDam = row.damOrTuhr in setOf(Vls.Opts.DAM, Vls.Opts.HAML)
    row.after {
        durationInputRow(rowIsDam, false, inputContainer.isNifas, inputContainer.isMustabeen)
    }
    setupFirstRow(inputContainer, true)
}
private fun addInputRow(inputContainer: HTMLElement, row: HTMLTableRowElement) {
    row.after {
        inputRow(
            inputContainer.isDateOnly,
            minTimeInput = row.endTimeInput.run { value.takeUnless(String::isEmpty) ?: min },
            maxTimeInput = row.endTimeInput.max
        )
    }
    setupRows(inputContainer)
}

// Set up the changing rows
fun setupRows(inputContainer: HTMLElement) {
    setMaxToCurrentTimeForTimeInputs(inputContainer)
    setupFirstRow(inputContainer, false)
    setupFirstRow(inputContainer, true)
}
private fun setupFirstRow(inputContainer: HTMLElement, duration: Boolean = false) {
    val inputDatesRows = if (duration) inputContainer.haizDurationInputDatesRows else inputContainer.haizInputDatesRows
    inputDatesRows.first().removeButton.visibility = inputDatesRows.size != 1
    inputDatesRows.getOrNull(1)?.removeButton?.visibility = true
}

// Starting tables from scratch or by copying them
fun TagConsumer<HTMLElement>.startInputRow(inputContainerToCopyFrom: HTMLElement? = null, isDuration: Boolean = false) {
    if (inputContainerToCopyFrom != null) {
        for (inputDateRow in inputContainerToCopyFrom.haizInputDatesRows) {
            copyInputRow(inputContainerToCopyFrom, inputDateRow.startTimeInput, inputDateRow.endTimeInput, isDuration)
        }
    } else {
        inputRow(
            isDateOnlyLayout = IS_DEFAULT_INPUT_MODE_DATE_ONLY,
            minTimeInput = "",
            maxTimeInput = "", //currentTimeString(IS_DEFAULT_INPUT_MODE_DATE_ONLY)
            disable = isDuration
        )
    }
}
fun TagConsumer<HTMLElement>.startDurationInputRow(inputContainerToCopyFrom: HTMLElement? = null, isDuration: Boolean = false) {
    if (inputContainerToCopyFrom != null) {
        for (inputDateRow in inputContainerToCopyFrom.haizDurationInputDatesRows) {
            copyDurationInputRow(
                aadat = inputDateRow.durationInput.value,
                selectedOption = inputDateRow.damOrTuhr,
                disable = !isDuration,
                preg = inputContainerToCopyFrom.isNifas,
                mustabeen = inputContainerToCopyFrom.isMustabeen
            )
        }
    } else { durationInputRow(false, !isDuration) }
}

// Change Duration Option
private fun onChangeDurationOption(event: Event) {
    val row = findRow(event)
    val pregOct = (event.target as HTMLSelectElement).value in setOf(Vls.Opts.HAML, Vls.Opts.WILADAT)
    row.durationInput.value = if (pregOct) "0" else row.durationInput.value
    row.durationInput.disabled = (event.target as HTMLSelectElement).value in setOf(Vls.Opts.HAML, Vls.Opts.WILADAT)
}

// Dealing with DateTime Inputs Max/Min Times
fun setMaxToCurrentTimeForTimeInputs(inputContainer: HTMLElement) {
//    val currentTime = currentTimeString(inputContainer.isDateOnly)
    for (timeInputsGroup in inputContainer.timeInputsGroups) {
        for (timeInput in timeInputsGroup.asReversed()) {
//            timeInput.max = currentTime
            if (timeInput.value.isNotEmpty()) break
        }
    }
}
private fun setMinMaxForTimeInputsOnInput(event: Event, indexWithinRow: Int) {
    setMinMaxForTimeInputsOnInput(
        findInputContainer(event),
        (findRow(event).rowIndexWithinTableBody * 2) + indexWithinRow
    )
}
private fun setMinMaxForTimeInputsOnInput(inputContainer: HTMLElement, index: Int) {
    val timeInputs = inputContainer.haizTimeInputs
    val timeInput = timeInputs[index]
    val min: String
    val max: String
    val previousTimeInputs: List<HTMLInputElement>
    val nextTimeInputs: List<HTMLInputElement>
    val value = timeInput.value
    if (value.isEmpty()) {
        min = timeInput.min
        max = timeInput.max
        previousTimeInputs = timeInputs.take(index + 1)
        nextTimeInputs = timeInputs.drop(index)
    } else {
        min = value
        max = value
        previousTimeInputs = timeInputs.take(index)
        nextTimeInputs = timeInputs.drop(index + 1)
    }
    for (previousTimeInput in previousTimeInputs.asReversed()) {
        previousTimeInput.max = max
        if (previousTimeInput.value.isNotEmpty()) break
    }
    for (nextTimeInput in nextTimeInputs) {
        nextTimeInput.min = min
        if (nextTimeInput.value.isNotEmpty()) break
    }
}
private fun updateMinMaxForTimeInputsBeforeRemovingRow(inputContainer: HTMLElement, rowIndex: Int) {
    val timeInputs = inputContainer.haizTimeInputs
    val startDateIndex = rowIndex * 2
    val endDateIndex = startDateIndex + 1
    val min = timeInputs[startDateIndex].min
    val max = timeInputs[endDateIndex].max
    val previousTimeInputs = timeInputs.take(startDateIndex)
    val nextTimeInputs = timeInputs.drop(endDateIndex + 1)
    for (previousTimeInput in previousTimeInputs.asReversed()) {
        previousTimeInput.max = max
        if (previousTimeInput.value.isNotEmpty()) break
    }
    for (nextTimeInput in nextTimeInputs) {
        nextTimeInput.min = min
        if (nextTimeInput.value.isNotEmpty()) break
    }
}

// CLONING
fun cloneInputsContainer(inputsContainerToCopyFrom: HTMLElement) {
    if (inputsContainers.size == 1) {
        addTheRemoveInputsContainerButton(inputsContainerToCopyFrom)
    }
    val clonedInputsContainer = inputsContainerToCopyFrom.after {
        inputFormDiv(inputsContainerToCopyFrom)
    }.single()

    addCalcAllButtonIfNeeded()

    // Make sure all invises are maintained
    languageChange()
    disableTree(clonedInputsContainer)
    shrinkAnswer(true)
    disableTime()
//    switchWiladatIsqat(clonedInputsContainer)  TODO: DOES WEIRDNESS. FIX
    setupFirstRow(clonedInputsContainer, inputsContainerToCopyFrom.isDuration)
}

fun removeInputsContainer(inputsContainer: HTMLElement) {
    inputsContainer.remove()
    inputsContainers.singleOrNull()?.inputsContainerRemoveButton?.remove()
    if (inputsContainers.size == 1) calculateAllDiv.replaceChildren {  }
    shrinkAnswer(false)
    disableTime()
}

private fun shrinkAnswer(shrink: Boolean = true) {
    inputsContainers.forEach { it.contentContainer.classList.toggle(CssC.SHRUNK, shrink) }
}

private fun addCalcAllButtonIfNeeded() { calculateAllDiv.replaceChildren { calcAllBtn("Calculate All") } }

// Disable Date-Time Option
private fun disableOpt(inputsContainer: HTMLElement, selectId: String, optionVal: String, disable: Boolean) {
    inputsContainer
        .getChildById(selectId)!!
        .children
        .asList()
        .map { it as HTMLOptionElement }
        .filter { it.value == optionVal }
        .forEach { it.disabled = disable }
}
private fun disableTime() {
    if (inputsContainers.size == 1) {
        disableOpt(inputsContainers.first(), Ids.Inputs.INPUT_TYPE_SELECT, Vls.Types.DATE_TIME, false)
    }
    else {
        inputsContainers.forEach { inputsContainer ->
            disableOpt(inputsContainer, Ids.Inputs.INPUT_TYPE_SELECT, Vls.Types.DATE_TIME, true)
            if (inputsContainer.isDateTime) inputsContainer.typeSelect.value = Vls.Types.DATE_ONLY
        }
    }
}

// Copy Answer

// ANSWER
//private fun getNow(): String {
//    var dateStr = ""
//    val now = Date.now()
//    val now = Instant.now()
//    val date = languagedDateFormat(now, TypesOfInputs.DATE_ONLY, languageSelected)
//    val day = now.
//    val day = Date(now).getDate()
//    val month = Date(now).getMonth()
//    if (languageSelected == Vls.Langs.URDU){
//        val urduMonth = urduMonthNames[month]
//        val urduDay:String = if (day == 1) "یکم" else day.toString()
//        dateStr = "$urduDay $urduMonth ${Date(now).getFullYear()}"
//    }else if(languageSelected == Vls.Langs.ENGLISH){
//        dateStr = Date(now).toDateString().drop(4)
//    }
//    return date
//}

private fun copyText(event: Event) {
    val div = (event.currentTarget as HTMLElement).getAncestor<HTMLDivElement> { it.id == Ids.Results.CONTENT_WRAPPER }
    val inputContainer = findInputContainer(event)

    val dateStr = languagedDateFormat(Instant.now(), TypesOfInputs.DATE_ONLY, languageSelected, addYear = true)
    val questionTxt = inputContainer.questionText
    val maslaTitle = inputContainer.titleText
    val divider = "${UnicodeChars.BLUE_SWIRL}➖➖➖➖➖➖${UnicodeChars.BLUE_SWIRL}"
    val answerTxt = div?.querySelector("p")?.textContent
    var copyTxt = "*$dateStr*\n\n" +
            "$maslaTitle\n\n" +
            "$questionTxt\n\n" +
            "$divider\n\n" +
            "$answerTxt"

    val small = div?.querySelector("small")
    var smallTxt: String = "Not Copied :("

    if (inputContainer.contentContainer.dataset["saved"] == "false") {
        var response:Json? = null;
        val job = GlobalScope.launch { response = getDataFromInputsAndSend(inputContainer)
        console.log(response)}
        job.invokeOnCompletion {
            console.log("Promise?")
            if (response != null && response!!["id"] != null) {
                copyTxt = "_Masla Id: ${response!!["id"]}_\n" + copyTxt
                smallTxt = " Saved and Copied "
                inputContainer.contentContainer.setAttribute("data-saved", "true")
            } else {
                smallTxt = " Copied "
                window.alert("Masla has not been saved. However, it has copied.")
            }

            copyTxt.let { window.navigator.clipboard.writeText(it) }
            small?.innerHTML?.let { small.innerHTML = smallTxt }
            window.setTimeout({ small?.innerHTML = "" }, 5000)
        }
    } else {
        smallTxt = "Copied!"

        copyTxt.let { window.navigator.clipboard.writeText(it) }
        small?.innerHTML?.let { small.innerHTML = smallTxt }
        window.setTimeout({ small?.innerHTML = "" }, 5000)
    }
}

// COMPARE
private fun calcAll() {
    inputsContainers.forEach { parseEntries(it) }
    compareResults()
}

// VALS
private val HTMLElement.haizInputTable get() = getChildById(Ids.InputTables.HAIZ_INPUT_TABLE) as HTMLTableElement
private val HTMLElement.haizDurationInputTable get() = getChildById(Ids.InputTables.HAIZ_DURATION_INPUT_TABLE) as HTMLTableElement
private val HTMLTableRowElement.removeButton get() = getChildById(Ids.Row.BUTTON_REMOVE) as HTMLButtonElement

val HTMLElement.inputID get() = (getChildById(Ids.Inputs.INPUT_ID) as HTMLInputElement).value
val HTMLElement.titleText get() = (getChildById(Ids.Inputs.INPUT_TITLE) as HTMLInputElement).value
val HTMLElement.questionText get() = (getChildById(Ids.Inputs.INPUT_QUESTION) as HTMLTextAreaElement).value

private val calculateAllDiv get() = document.getElementById(Ids.Results.CALCULATE_ALL_DIV) as HTMLDivElement
private val HTMLElement.inputsContainerCloneButton get() = getChildById(Ids.InputContainers.INPUTS_CONTAINER_CLONE_BUTTON) as HTMLButtonElement
private val HTMLElement.inputsContainerRemoveButton get() = getChildById(Ids.InputContainers.INPUTS_CONTAINER_REMOVE_BUTTON) as HTMLButtonElement
