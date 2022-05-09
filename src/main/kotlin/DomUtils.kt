@file:Suppress("SpellCheckingInspection")

import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.*
import kotlinx.html.dom.prepend
import kotlinx.html.js.*
import org.w3c.dom.*
import org.w3c.dom.events.Event
import kotlin.js.Date

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
        small(classes = btnClass)
        button(classes = btnClass) {
            onClickFunction = { event -> copyText(event) }
            +"Copy ⎙"
        }
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
//        onClickFunction = { event ->
//            setMaxToCurrentTimeForTimeInputs(findInputContainer(event))
//        }
        block()
    }
}

//fun FlowContent.pregnancyTimeInput(inputContainerToCopyFrom: HTMLElement?, inputId: String = "", block: INPUT.() -> Unit = {}) {
//    var disable = true
//    if (inputContainerToCopyFrom != null) { disable = !inputContainerToCopyFrom.isNifas }
//    timeInput(IS_DEFAULT_INPUT_MODE_DATE_ONLY) {
//        disabled = disable
//        id = inputId
//        name = inputId
//        block()
//    }
//}

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
        makeDropdownOptions(selectedOption == Vls.Opts.DAM, Vls.Opts.DAM, StringsOfLanguages.ENGLISH.dam, StringsOfLanguages.URDU.dam)
        makeDropdownOptions(selectedOption == Vls.Opts.TUHR, Vls.Opts.TUHR, StringsOfLanguages.ENGLISH.tuhr, StringsOfLanguages.URDU.tuhr)
        makeDropdownOptions(
            selectedOption == Vls.Opts.HAML,
            Vls.Opts.HAML,
            StringsOfLanguages.ENGLISH.pregduration,
            StringsOfLanguages.URDU.pregduration,
            CssC.NIFAS + " " + if (!preg) CssC.INVIS else null
        )
        // Wiladat
        makeDropdownOptions(
            selectedOption == Vls.Opts.WILADAT && mustabeen,
            Vls.Opts.WILADAT,
            StringsOfLanguages.ENGLISH.birthduration,
            StringsOfLanguages.URDU.birthduration,
            CssC.NIFAS + " " + CssC.MUSTABEEN + " " + if (!preg || !mustabeen) CssC.INVIS else null
        )
        // Isqaat
        makeDropdownOptions(
            selectedOption == Vls.Opts.WILADAT && !mustabeen,
            Vls.Opts.WILADAT,
            "Isqaat",
            "Isqaat in Urdu",
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

private fun TagConsumer<HTMLElement>.calcAllBtn() {
    button {
        classes = setOf(
            CssC.CALC_BTN,
            CssC.ENGLISH,
            if (languageSelector.value == Vls.Langs.ENGLISH) "" else CssC.INVIS
        )
        +"Calculate All"
        onClickFunction = { calcAll() }
    }
    button {
        classes = setOf(
            CssC.CALC_BTN,
            CssC.URDU,
            if (languageSelector.value == Vls.Langs.URDU) "" else CssC.INVIS
        )
        +"Calculate All"
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
            option.value == selectedOption && option.classList.contains(languageSelector.value) }
        ?.selected = true
}
//fun maslaChanging(event: Event) {
//    val selectedOption = (event.currentTarget as HTMLSelectElement).value
//    inputsContainers.forEach {
//        setOptionInSelect(it.maslaSelect, selectedOption)
//        disableTree(it)
//    }
//}

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
//private fun disableByMasla(inputContainer: HTMLElement) {
//    disableByClass(CssC.NIFAS, inputContainer, !inputContainer.isNifas)
//    disableByClass(CssC.MUTADA, inputContainer, inputContainer.isMubtadia)
//}
fun disableTree(inputContainer: HTMLElement) {
//    val mawjoodaFasidCheck = inputContainer.getChildById(Ids.Inputs.MAWJOODA_FASID_CHECKBOX) as HTMLInputElement
//    if (inputContainer.isMubtadia) {
//        mawjoodaFasidCheck.checked = true
//        mawjoodaFasidCheck.disabled = true
//    }
}

// Ensure Aadaat in Range
fun HTMLInputElement.validateAadat(validityRange: ClosedRange<Int>) {
    val errormessage = if(languageSelector.value == Vls.Langs.ENGLISH) { StringsOfLanguages.ENGLISH.incorrectAadat }
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
            val days = (parseDays(value)?.div(MILLISECONDS_IN_A_DAY))?.toDouble()
            require(days == null || days in doubleValidityRange) { errormessage }
            ""
        } catch (e: IllegalArgumentException) {
            e.message ?: errormessage
        })
    }
}

// Switch Between Wilaadat/Isqaat - Beauty Only
fun switchWiladatIsqat(inputContainer: HTMLElement) {
//    disableByClass(CssC.MUSTABEEN, inputContainer, !inputContainer.isMustabeen)
//    disableByClass(CssC.NOT_MUSTABEEN, inputContainer, inputContainer.isMustabeen)
//
//    // Get All Wiladat/Isqat Selected. Should be one, but maybe someone has messed up so allow for more.
//    val wiladatSelect = inputContainer.haizDurationInputTable.querySelectorAll("select")
//        .asList()
//        .map { it as HTMLSelectElement }
//        .filter { select -> select.value == Vls.Opts.WILADAT }
//
//    // If mustabeen, select wiladat
//    if (inputContainer.isMustabeen) wiladatSelect.forEach { select ->
//        select.children.asList().map { it as HTMLOptionElement }
//            .first { option -> option.classList.contains(CssC.MUSTABEEN) }
//            .selected = true
//    }
//    // If !mustabeen, select isqaat
//    else wiladatSelect.forEach { select ->
//        select.children.asList().map { it as HTMLOptionElement }
//            .first { option -> option.classList.contains(CssC.NOT_MUSTABEEN) }
//            .selected = true
//    }
}

// DEALING WITH THE INPUT TABLES

// Switching Date/Duration Inputs
private fun disableDateTable(inputContainer: HTMLElement, disable: Boolean = inputContainer.isDuration) {
    for (timeInput in inputContainer.timeInputsGroups) {
        for (input in timeInput) {
            input!!.disabled = disable
        }
    }
    for (durationInput in inputContainer.durationInputsGroups) {
        for (input in durationInput) {
            input.asDynamic().disabled = !disable
        }
    }
    disableTree(inputContainer)
}
private fun switchToDurationTable(inputContainer: HTMLElement, isDuration: Boolean = inputContainer.isDuration) {
    disableDateTable(inputContainer, isDuration)
    inputContainer.haizInputTable.visibility = !isDuration
    inputContainer.haizDurationInputTable.visibility = isDuration
}
private fun typeChanging(inputContainer: HTMLElement, selectedOption: String, isDateOnly: Boolean) {
    setOptionInSelect(inputContainer.typeSelect, selectedOption)

    for (timeInput in inputContainer.timeInputsGroups.flatten()) {
        timeInput?.let {
            val newValue = convertInputValue(timeInput.value, isDateOnly)
            val newMin = convertInputValue(timeInput.min, isDateOnly)
            val newMax = convertInputValue(timeInput.max, isDateOnly)

            val dateInputType = if (isDateOnly) InputType.date else InputType.dateTimeLocal
            timeInput.type = dateInputType.realValue

            timeInput.value = newValue
            timeInput.min = newMin
            timeInput.max = newMax
        }
    }
    switchToDurationTable(inputContainer)
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

// Setup the changing rows
fun setupRows(inputContainer: HTMLElement) {
//    setMaxToCurrentTimeForTimeInputs(inputContainer)
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
    val clonedInputsContainer = inputsContainerToCopyFrom.after {
        inputFormDiv(inputsContainerToCopyFrom)
    }.single()
    addReact(inputsContainerToCopyFrom)

    if (inputsContainers.size == 2) {
        addTheRemoveInputsContainerButton(inputsContainerToCopyFrom)
    }
    addCalcAllButtonIfNeeded()

    // Make sure all invises are maintained
    languageChange()
//    disableTree(clonedInputsContainer)
    shrinkAnswer(true)
//    disableTime()
//    switchWiladatIsqat(clonedInputsContainer)  TODO: DOES WEIRDNESS. FIX
    setupFirstRow(clonedInputsContainer, inputsContainerToCopyFrom.isDuration)
}

fun removeInputsContainer(inputsContainer: HTMLElement) {
    inputsContainer.remove()
    inputsContainers.singleOrNull()?.inputsContainerRemoveButton?.remove()
    if (inputsContainers.size == 1) calculateAllDiv.replaceChildren {  }
    shrinkAnswer(false)
//    disableTime()
}

private fun shrinkAnswer(shrink: Boolean = true) {
    inputsContainers.forEach { it.contentContainer.classList.toggle(CssC.SHRUNK, shrink) }
}

private fun addCalcAllButtonIfNeeded() { calculateAllDiv.replaceChildren { calcAllBtn() } }

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
private fun getNow(): String {
    var dateStr = ""
    val now = Date.now()
    val day = Date(now).getDate()
    val month = Date(now).getMonth()
    if (languageSelector.value == Vls.Langs.URDU){
        val urduMonth = urduMonthNames[month]
        val urduDay:String = if (day == 1) "یکم" else day.toString()
        dateStr = "$urduDay $urduMonth ${Date(now).getFullYear()}"
    }else if(languageSelector.value == Vls.Langs.ENGLISH){
        dateStr = Date(now).toDateString().drop(4)
    }
    return dateStr
}

private fun copyText(event: Event) {
    val div = (event.currentTarget as HTMLElement).getAncestor<HTMLDivElement> { it.id == Ids.Results.CONTENT_WRAPPER }

    val dateStr = getNow()
    val questionTxt = findInputContainer(event).questionText.value
    val divider = "${UnicodeChars.BLUE_SWIRL}➖➖➖➖➖➖${ UnicodeChars.BLUE_SWIRL }"
    val answerTxt = div?.querySelector("p")?.textContent

    val copyTxt = "*${dateStr}*\n\n${questionTxt}\n\n${divider}\n\n${answerTxt}"
    copyTxt.let { window.navigator.clipboard.writeText(it) }

    val small = div?.querySelector("small")
    small?.innerHTML?.let { small.innerHTML = " Copied " }

    window.setTimeout({ if (small != null) small.innerHTML = "" }, 1000)
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

private val HTMLElement.questionText get() = (getChildById(Ids.Inputs.INPUT_QUESTION) as HTMLTextAreaElement)

private val calculateAllDiv get() = document.getElementById(Ids.Results.CALCULATE_ALL_DIV) as HTMLDivElement
private val HTMLElement.inputsContainerCloneButton get() = getChildById(Ids.InputContainers.INPUTS_CONTAINER_CLONE_BUTTON) as HTMLButtonElement
private val HTMLElement.inputsContainerRemoveButton get() = getChildById(Ids.InputContainers.INPUTS_CONTAINER_REMOVE_BUTTON) as HTMLButtonElement
