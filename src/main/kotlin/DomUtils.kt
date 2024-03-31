@file:Suppress("SpellCheckingInspection") @file:OptIn(DelicateCoroutinesApi::class)

import kotlinx.browser.document
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.datetime.internal.JSJoda.ZoneId
import kotlinx.html.*
import kotlinx.html.dom.prepend
import kotlinx.html.js.*
import kotlinx.html.org.w3c.dom.events.Event
import org.w3c.dom.*

// MAKE ELEMENTS
fun TagConsumer<HTMLElement>.content(idName: String? = null, classes: String? = null, block: () -> Unit = {}) {
    p(classes = classes) {
        id = idName ?: "content"
        style = "white-space: pre-wrap;"
        block()
    }
}

fun FlowContent.copyBtn(contentId: String, divClass: String, clipboard: Boolean = false) {
    div(classes = "$divClass ${CssC.COPY_BTN}") {
        button(classes = CssC.CALC_BTN) {
            id = if (clipboard) Ids.Results.CLIPBOARD_JS_BTN else Ids.Results.COPY_BTN
            if (clipboard) attributes.put("data-clipboard-target", "#$contentId")
            onClickFunction = { event ->
                if (clipboard) saveText(event) else copyText(event)
            }
            +"Save and Copy "

            val iconName = if (clipboard) "word" else "whatsapp"
            img(src = "./images/$iconName-icon.svg") {
                alt = ""
                width = "16" // Set the width as needed
                height = "16" // Set the height as needed
            }

            span(classes = Ids.Results.COPY_TOOLTIP) {
                +"Copy to clipboard"
            }
        }
    }
    if (clipboard) copyClipboard(Ids.Results.CLIPBOARD_JS_BTN)
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
    isDateOnlyLayout: Boolean, minTimeInput: String, maxTimeInput: String, block: INPUT.() -> Unit = {}
) {
    timeInput(isDateOnlyLayout) {
        min = minTimeInput
        max = maxTimeInput
        block()
    }
}

fun FlowContent.timeInput(
    inputContainerToCopyFrom: HTMLElement, timeInputToCopyFrom: HTMLInputElement? = null, block: INPUT.() -> Unit = {}
) {
    timeInput(inputContainerToCopyFrom.isDateOnly) {
        block()
        @Suppress("NAME_SHADOWING") val timeInputToCopyFrom =
            timeInputToCopyFrom ?: inputContainerToCopyFrom.getChildById(id) as HTMLInputElement
        value = timeInputToCopyFrom.value
        min = timeInputToCopyFrom.min
        max = timeInputToCopyFrom.max
    }
}

fun FlowContent.timeInput(
    isDateOnlyLayout: Boolean, block: INPUT.() -> Unit = {}
) {
    customDateTimeInput(isDateOnlyLayout) {
        required = true
        onClickFunction = { event ->
            setMaxToCurrentTimeForTimeInputs(findInputContainer(event))
        }
        block()
    }
}

fun FlowContent.pregnancyTimeInput(
    inputContainerToCopyFrom: HTMLElement?, inputId: String = "", block: INPUT.() -> Unit = {}
) {
    var disable = true
    if (inputContainerToCopyFrom != null) {
        disable = !inputContainerToCopyFrom.isNifas
    }
    timeInput(IS_DEFAULT_INPUT_MODE_DATE_ONLY) {
        disabled = disable
        id = inputId
        name = inputId
        block()
    }
}

fun FlowContent.makeLabel(
    inputId: String, text: Strings.() -> String, extraClasses: String = "", block: LABEL.() -> Unit = {}
) {
    label {
        htmlFor = inputId
        classes = setOf(CssC.ENGLISH, extraClasses)
        block()
        +StringsOfLanguages.ENGLISH.text()
    }
    label {
        htmlFor = inputId
        classes = setOf(CssC.MMENGLISH, extraClasses)
        block()
        +StringsOfLanguages.MMENGLISH.text()
    }
    label {
        htmlFor = inputId
        classes = setOf(CssC.URDU, extraClasses)
        block()
        +StringsOfLanguages.URDU.text()
    }
}

fun FlowContent.makeSwitch(inputId: String, value: Boolean, disable: Boolean? = false, block: INPUT.() -> Unit = {}) {
    label(classes = CssC.SWITCH) {
        checkBoxInput {
            id = inputId
            checked = value
            name = inputId
            disabled = isPersonalApper || disable == true
            block()
        }
        span(classes = "${CssC.SLIDER} ${CssC.ROUND}")
    }
}

fun FlowContent.makeIkhtilafiMasla(
    inputId: String,
    text: Strings.() -> String,
    value: Boolean,
    extraClasses: String? = null,
    block: DIV.() -> Unit = {}
) {
    val disable = extraClasses?.contains(CssC.DEV);
    div(classes = "${CssC.ROW} $extraClasses") {
        div {
            makeLabel(inputId, text)
            makeSwitch(inputId, value, disable)
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
            CssC.ENGLISH, extraClasses, if (languageSelected != Vls.Langs.ENGLISH) CssC.LANG_INVIS else null
        )
        selected = isSelected && languageSelected == Vls.Langs.ENGLISH
        value = optionVal
        id = optionVal
        block()
        +StringsOfLanguages.ENGLISH.text()
    }
    option {
        classes = setOfNotNull(
            CssC.MMENGLISH, extraClasses, if (languageSelected != Vls.Langs.MMENGLISH) CssC.LANG_INVIS else null
        )
        selected = isSelected && languageSelected == Vls.Langs.MMENGLISH
        value = optionVal
        id = optionVal
        block()
        +StringsOfLanguages.MMENGLISH.text()
    }
    option {
        classes = setOfNotNull(
            CssC.URDU, extraClasses, if (languageSelected != Vls.Langs.URDU) CssC.LANG_INVIS else null
        )
        selected = isSelected && languageSelected == Vls.Langs.URDU
        value = optionVal
        id = optionVal
        block()
        +StringsOfLanguages.URDU.text()
    }
}

fun FlowContent.makeNumberInput(
    inputId: String, inputVal: String?, inputRange: IntRange, block: INPUT.() -> Unit = {}
) {
    input {
        id = inputId
        name = inputId
        value = inputVal.orEmpty()
        //TODO: Uncomment this later after fixing validator
        onInputFunction = { event -> (event.currentTarget as HTMLInputElement).validateAadat(inputRange) }
        block()
    }
}

fun TagConsumer<HTMLElement>.makeSpans(
    englishText: String, mmEnglishText: String, urduText: String, block: SPAN.() -> Unit = {}
) {
    span(classes = "${CssC.ENGLISH} ${if (languageSelected == Vls.Langs.ENGLISH) "" else CssC.LANG_INVIS}") {
        block()
        +englishText
    }
    span(classes = "${CssC.MMENGLISH} ${if (languageSelected == Vls.Langs.MMENGLISH) "" else CssC.LANG_INVIS}") {
        block()
        +mmEnglishText
    }
    span(classes = "${CssC.URDU} ${if (languageSelected == Vls.Langs.URDU) "" else CssC.LANG_INVIS}") {
        block()
        +urduText
    }
}

fun TagConsumer<HTMLElement>.makeSpans(text: Strings.() -> String, block: SPAN.() -> Unit = {}) {
    makeSpans(
        StringsOfLanguages.ENGLISH.text(),
        StringsOfLanguages.MMENGLISH.text(),
        StringsOfLanguages.URDU.text(),
    ) {
        block()
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
        +if (isPersonalApper) "\uD83D\uDD0D \u25B2" else "\u2795 \u25B2"
        title = "Add at Start"
        id = Ids.Row.BUTTON_ADD_BEFORE
        onClickFunction = { event ->
            if (isPersonalApper) {
                for (element in collapsingElements) element.classList.toggle(CssC.COLLAPSE)
            } else {
                val inputContainer = findInputContainer(event)
                if (duration) addBeforeDurationRow(inputContainer)
                else addBeforeInputRow(inputContainer)
            }
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

fun addNowRow(event: Event, tz: String? = null) {
    val timeZone = if (tz == null) ZoneId.systemDefault().toString() else tz
    val inputContainer = findInputContainer(event)
    val row = inputContainer.haizInputDatesRows.last()
    val now = tzOffsetNOW.toDateInputString(inputContainer.isDateOnly)
    if (row.startTimeInput.value.isEmpty()) {
        row.startTimeInput.value = now
        row.endTimeInput.value = now
    } else if (row.endTimeInput.value.isEmpty()) {
        row.endTimeInput.value = now
    } else {
        row.after {
            inputRow(
                inputContainer.isDateOnly,
                row.endTimeInput.run { value.takeUnless(String::isEmpty) ?: min },
                now, false, false,
                SaveEntries(now, now), timeZone
            )
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
fun TagConsumer<HTMLElement>.inputRow(
    isDateOnlyLayout: Boolean,
    minTimeInput: String,
    maxTimeInput: String,
    disable: Boolean = false,
    noButtons: Boolean = false,
    values: SaveEntries? = null,
    tz: String? = null,
    block: TR.() -> Unit = {}
) {
    tr {
        td {
            timeInput(isDateOnlyLayout, minTimeInput, maxTimeInput, indexWithinRow = 0) {
                if (values != null) value = values.startTime.orEmpty()
                id = Ids.Row.INPUT_START_TIME
                disabled = disable || !values.startLessThanDaysAgo(daysAllowedPersonal, tz)
            }
        }
        td {
            timeInput(isDateOnlyLayout, minTimeInput, maxTimeInput, indexWithinRow = 1) {
                if (values != null) value = values.endTime.orEmpty()
                id = Ids.Row.INPUT_END_TIME
                disabled = disable || !values.endLessThanDaysAgo(daysAllowedPersonal, tz)
            }
        }
        if (!noButtons) {
            addRemoveButtonsTableData()
        } else {
            td { id = Ids.Row.BUTTONS_CONTAINER }
        }
        block()
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
private fun TagConsumer<HTMLElement>.makeDurationTableSelect(
    disable: Boolean,
    selectedOption: String,
    isNifaas: Boolean,
    isMustabeen: Boolean = true,
) {
    select {
        id = Ids.Row.INPUT_TYPE_OF_DURATION
        name = Ids.Row.INPUT_TYPE_OF_DURATION
        disabled = disable
        onChangeFunction = { event -> onChangeDurationOption(event) }
        makeDropdownOptions(selectedOption == Vls.Opts.DAM, Vls.Opts.DAM, Strings::dam)
        makeDropdownOptions(selectedOption == Vls.Opts.TUHR, Vls.Opts.TUHR, Strings::tuhr)
        makeDropdownOptions(
            selectedOption == Vls.Opts.HAML,
            Vls.Opts.HAML,
            Strings::pregduration,
            CssC.NIFAS + " " + if (!isNifaas) CssC.INVIS else null
        )
        // Wiladat
        makeDropdownOptions(
            selectedOption == Vls.Opts.WILADAT && isMustabeen,
            Vls.Opts.WILADAT,
            Strings::birthduration,
            CssC.NIFAS + " " + CssC.MUSTABEEN + " " + if (!isNifaas || !isMustabeen) CssC.INVIS else null
        )
        // Isqaat
        makeDropdownOptions(
            selectedOption == Vls.Opts.WILADAT && !isMustabeen,
            Vls.Opts.WILADAT,
            Strings::isqat,
            CssC.NIFAS + " " + CssC.NOT_MUSTABEEN + " " + if (!isNifaas || isMustabeen) CssC.INVIS else null
        )
    }
}

fun TagConsumer<HTMLElement>.copyDurationInputRow(
    durationValue: String,
    selectedOption: String,
    disable: Boolean,
    isNifaas: Boolean,
    mustabeen: Boolean,
    noButtons: Boolean = false
) {
    tr {
        td {
            makeNumberInput(Ids.Row.INPUT_DURATION, durationValue, (0..10000)) {
                required = true
                disabled = disable
            }
        }
        td {
            makeDurationTableSelect(disable, selectedOption, isNifaas, mustabeen)
        }
        if (!noButtons) {
            addRemoveButtonsTableData(true)
        } else {
            td { id = Ids.Row.BUTTONS_CONTAINER }
        }
    }
}

fun TagConsumer<HTMLElement>.durationInputRow(
    lastWasDam: Boolean, disable: Boolean, isNifaas: Boolean = false, isMustabeen: Boolean = true
) {
    tr {
        td {
            makeNumberInput(Ids.Row.INPUT_DURATION, "", (0..10000)) {
                disabled = disable
                required = true
            }
        }
        td {
            makeDurationTableSelect(
                disable, if (lastWasDam) Vls.Opts.TUHR else Vls.Opts.DAM, isNifaas, isMustabeen
            )
        }
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
            CssC.CALC_BTN, CssC.ENGLISH, if (languageSelected == Vls.Langs.ENGLISH) "" else CssC.INVIS
        )
        +text
        onClickFunction = { calcAll() }
    }
    button {
        classes = setOf(
            CssC.CALC_BTN, CssC.MMENGLISH, if (languageSelected == Vls.Langs.MMENGLISH) "" else CssC.INVIS
        )
        +text
        onClickFunction = { calcAll() }
    }
    button {
        classes = setOf(
            CssC.CALC_BTN, CssC.URDU, if (languageSelected == Vls.Langs.URDU) "" else CssC.INVIS
        )
        +text
        onClickFunction = { calcAll() }
    }
}

// Compare Table
fun TagConsumer<HTMLElement>.oneRow(
    starter: Boolean = true, desc: String = "", ender: Boolean = false, block: () -> Unit = {}
) {
    if (starter) div { id = "margin-cell" }                     // Empty buffer margin
    div {                                                       // Description of Inputs
        id = if (desc.isEmpty()) "empty_desc" else "desc"
        classes = if (desc.isEmpty()) emptySet() else setOf(
            CssC.TABLE_CELL, CssC.BORDERED, CssC.DESCRIPTION
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
    selectElement.children.asList().map { it as HTMLOptionElement }.firstOrNull { option ->
        option.value == selectedOption && option.classList.contains(languageSelected)
    }?.selected = true
}

fun maslaChanging(selectedMasla: String) {
    inputsContainers.forEach {
        setOptionInSelect(it.maslaSelect, selectedMasla)
        disableTree(it)
    }
}

// Invising Tree
private fun disableByClass(
    classSelector: String, inputContainer: HTMLElement, disable: Boolean, classInvis: String = CssC.INVIS
) {
    inputContainer.getElementsByClassName(classSelector).asList().forEach { row ->
        row.classList.toggle(classInvis, disable)
        row.querySelectorAll("input").asList().map { input ->
            input as HTMLInputElement
            input.disabled = isPersonalApper || disable
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
    val isMustabeen = inputContainer.isMustabeen

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
    val errormessage = if (languageSelected == Vls.Langs.ENGLISH) {
        StringsOfLanguages.ENGLISH.incorrectAadat
    } else if (languageSelected == Vls.Langs.MMENGLISH) {
        StringsOfLanguages.MMENGLISH.incorrectAadat
    } else {
        StringsOfLanguages.URDU.incorrectAadat
    }
    if (value.contains("-") && devmode) {
//        println("DASH!")
        setCustomValidity(try {
            val arr = value.split("-")
//            console.log("IN THERE?", !arr.any { it.toInt() in validityRange }, !(arr.any { it.toInt() in validityRange }) )
            require(arr.all { it.toInt() in validityRange }) { errormessage }
            ""
        } catch (e: IllegalArgumentException) {
            e.message ?: errormessage
        })
    } else {
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
    val wiladatSelect =
        inputContainer.haizDurationInputTable.querySelectorAll("select").asList().map { it as HTMLSelectElement }
            .filter { select -> select.value == Vls.Opts.WILADAT }

    // If mustabeen, select wiladat
    if (inputContainer.isMustabeen) wiladatSelect.forEach { select ->
        select.children.asList().map { it as HTMLOptionElement }
            .first { option -> option.classList.contains(CssC.MUSTABEEN) }.selected = true
    }
    // If !mustabeen, select isqaat
    else wiladatSelect.forEach { select ->
        select.children.asList().map { it as HTMLOptionElement }
            .first { option -> option.classList.contains(CssC.NOT_MUSTABEEN) }.selected = true
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

fun typeChanging(
    inputContainer: HTMLElement, selectedOption: String, timeZone: String? = null
) {

    setOptionInSelect(inputContainer.typeSelect, selectedOption)
    val isDateOnly = selectedOption == Vls.Types.DATE_ONLY
    val isDateTime = selectedOption == Vls.Types.DATE_TIME

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
    val inputsContainer = findInputContainer(event)
    val timeZoneSelector = inputsContainer.timezoneSelect
    val timeZone = if (timeZoneSelector.disabled) null else timeZoneSelector.value
    // Ensure all input containers are same type
    inputsContainers.forEach { typeChanging(it, selected, timeZone) }
}

// Add new rows at the start
private fun addBeforeDurationRow(inputContainer: HTMLElement) {
    val firstIsDam =
        inputContainer.haizDurationInputDatesRows.first().damOrTuhr in setOf(Vls.Opts.DAM, Vls.Opts.WILADAT)
    inputContainer.hazDurationInputTableBody.prepend {
        durationInputRow(firstIsDam, false, inputContainer.isNifas, inputContainer.isMustabeen)
    }
    setupFirstRow(inputContainer, true)
}

private fun addBeforeInputRow(inputContainer: HTMLElement) {
    val row = inputContainer.hazInputTableBody.firstChild as HTMLTableRowElement

    inputContainer.hazInputTableBody.prepend {
        inputRow(inputContainer.isDateOnly,
            minTimeInput = "",
            maxTimeInput = row.startTimeInput.run { value.takeUnless(String::isEmpty) ?: max })
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

fun addInputRow(inputContainer: HTMLElement, row: HTMLTableRowElement) {
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
    var inputDatesRows = if (duration) inputContainer.haizDurationInputDatesRows else inputContainer.haizInputDatesRows
//    inputDatesRows = inputDatesRows.filter { inputDatesRow -> inputDatesRow.removeButton != null }
    if (inputDatesRows.isEmpty()) {
        return
    }

    inputDatesRows.first().removeButton?.visibility = inputDatesRows.size != 1
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

fun TagConsumer<HTMLElement>.startDurationInputRow(
    inputContainerToCopyFrom: HTMLElement? = null, isDuration: Boolean = false
) {
    if (inputContainerToCopyFrom != null) {
        for (inputDateRow in inputContainerToCopyFrom.haizDurationInputDatesRows) {
            copyDurationInputRow(
                durationValue = inputDateRow.durationInput.value,
                selectedOption = inputDateRow.damOrTuhr,
                disable = !isDuration,
                isNifaas = inputContainerToCopyFrom.isNifas,
                mustabeen = inputContainerToCopyFrom.isMustabeen
            )
        }
    } else {
        durationInputRow(false, !isDuration)
    }
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
//    for (timeInputsGroup in inputContainer.timeInputsGroups) {
//        for (timeInput in timeInputsGroup.asReversed()) {
////            timeInput.max = currentTime
//            if (timeInput.value.isNotEmpty()) break
//        }
//    }
}

private fun setMinMaxForTimeInputsOnInput(event: Event, indexWithinRow: Int) {
    setMinMaxForTimeInputsOnInput(
        findInputContainer(event), (findRow(event).rowIndexWithinTableBody * 2) + indexWithinRow
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
    if (inputsContainers.size == 1) calculateAllDiv.replaceChildren { }
    shrinkAnswer(false)
    disableTime()
}

private fun shrinkAnswer(shrink: Boolean = true) {
    inputsContainers.forEach { it.contentContainer.classList.toggle(CssC.SHRUNK, shrink) }
}

private fun addCalcAllButtonIfNeeded() {
    calculateAllDiv.replaceChildren { calcAllBtn("Calculate All") }
}

// Disable Date-Time Option
private fun disableOpt(inputsContainer: HTMLElement, selectId: String, optionVal: String, disable: Boolean) {
    inputsContainer.getChildById(selectId)!!.children.asList().map { it as HTMLOptionElement }
        .filter { it.value == optionVal }.forEach { it.disabled = disable }
}

private fun disableTime() {
    if (inputsContainers.size == 1) {
        disableOpt(inputsContainers.first(), Ids.Inputs.INPUT_TYPE_SELECT, Vls.Types.DATE_TIME, false)
    } else {
        inputsContainers.forEach { inputsContainer ->
            disableOpt(inputsContainer, Ids.Inputs.INPUT_TYPE_SELECT, Vls.Types.DATE_TIME, true)
            if (inputsContainer.isDateTime) inputsContainer.typeSelect.value = Vls.Types.DATE_ONLY
        }
    }
}

// COMPARE
private fun calcAll() {
    inputsContainers.forEach { parseEntries(it) }
    compareResults()
}

// VALS
val HTMLElement.haizInputTable get() = getChildById(Ids.InputTables.HAIZ_INPUT_TABLE) as HTMLTableElement
val HTMLElement.haizDurationInputTable get() = getChildById(Ids.InputTables.HAIZ_DURATION_INPUT_TABLE) as HTMLTableElement
private val HTMLTableRowElement.removeButton get() = getChildById(Ids.Row.BUTTON_REMOVE) as HTMLButtonElement?

val HTMLElement.inputID get() = (getChildById(Ids.Inputs.INPUT_ID) as HTMLInputElement).value
val HTMLElement.saailaDetailsInput get() = (getChildById(Ids.Inputs.INPUT_SAAILA) as HTMLInputElement)
val HTMLElement.saailaDetails get() = saailaDetailsInput.value
val HTMLElement.questionTextInput get() = (getChildById(Ids.Inputs.INPUT_QUESTION) as HTMLTextAreaElement)
val HTMLElement.questionText get() = questionTextInput.value

private val calculateAllDiv get() = document.getElementById(Ids.Results.CALCULATE_ALL_DIV) as HTMLDivElement
val HTMLElement.inputsContainerMessage get() = getChildById(Ids.Results.MSG) as HTMLDivElement
private val HTMLElement.inputsContainerCloneButton get() = getChildById(Ids.InputContainers.INPUTS_CONTAINER_CLONE_BUTTON) as HTMLButtonElement
private val HTMLElement.inputsContainerRemoveButton get() = getChildById(Ids.InputContainers.INPUTS_CONTAINER_REMOVE_BUTTON) as HTMLButtonElement
