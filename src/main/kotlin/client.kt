import kotlinx.html.dom.append
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.*
import kotlinx.html.form
import kotlinx.html.js.*
import kotlinx.html.tr
import org.w3c.dom.*
import org.w3c.dom.events.Event
import kotlin.js.Date

private const val IS_DEFAULT_INPUT_MODE_DATE_ONLY = true

object Ids {
    const val HAIZ_INPUT_TABLE = "haiz_input_table"

    object Row {
        const val INPUT_START_TIME = "input_start_time"
        const val INPUT_END_TIME = "input_end_time"
        const val BUTTONS_CONTAINER = "button_add_before_container"
        const val BUTTON_REMOVE = "button_remove"
        const val BUTTON_ADD_BEFORE = "button_add_before"
    }

    const val CONTENT_ENG = "content_eng"
    const val CONTENT_URDU = "content_urdu"
    const val CONTENT_DATES = "content_dates"
    const val CONTENT_DATES_DIFFERENCE = "content_dates_difference"
    const val DATES_DIFFERENCE_TABLE = "dates_difference_table"
    const val INPUT_CONTAINERS_CONTAINER = "input_containers_container"
    const val INPUT_CONTAINER = "input_container"
    const val COMPARISON_CONTAINER = "comparison_container"
//    const val ISTIMRAR_CHECKBOX = "istimrar_checkbox"
    const val PREGNANCY_CHECKBOX = "pregnancy_checkbox"
    const val MUSTABEEN_CHECKBOX = "mustabeen_checkbox"
    const val PREG_START_TIME_INPUT = "preg_start_time_input"
    const val PREG_END_TIME_INPUT = "preg_end_time_input"
    const val AADAT_HAIZ_INPUT = "aadat_haiz_input"
    const val AADAT_TUHR_INPUT = "aadat_tuhr_input"
    const val AADAT_NIFAS_INPUT = "aadat_nifas_input"
    const val DATE_ONLY_RADIO = "date_only_radio"
    const val DATE_TIME_RADIO = "date_time_radio"
    const val DATE_AND_OR_RADIO = "date_and_or_time"
    const val INPUTS_CONTAINER_CLONE_BUTTON = "inputs_container_clone_button"
    const val INPUTS_CONTAINER_REMOVE_BUTTON = "inputs_container_remove_button"

    val pregnancyElementIds = listOf(
        PREG_START_TIME_INPUT,
        PREG_END_TIME_INPUT,
        MUSTABEEN_CHECKBOX,
        AADAT_NIFAS_INPUT
    )
}

private val inputsContainersContainer get() = document.getElementById(Ids.INPUT_CONTAINERS_CONTAINER) as HTMLElement
@Suppress("UNCHECKED_CAST")
private val inputsContainers get() = inputsContainersContainer.children.asList() as List<HTMLElement>

private val comparisonContainer get() = document.getElementById(Ids.COMPARISON_CONTAINER) as HTMLElement?
private val contentDatesDifferenceElement get() = document.getElementById(Ids.CONTENT_DATES_DIFFERENCE) as HTMLParagraphElement?
private val datesDifferenceTableElement get() = document.getElementById(Ids.DATES_DIFFERENCE_TABLE) as HTMLElement?

private val HTMLElement.isDateOnly get() = (getChildById(Ids.DATE_ONLY_RADIO) as HTMLInputElement).checked
//private val HTMLElement.isIstimrar get() = (getChildById(Ids.ISTIMRAR_CHECKBOX) as HTMLInputElement).checked
private val HTMLElement.isPregnancy get() = (getChildById(Ids.PREGNANCY_CHECKBOX) as HTMLInputElement).checked
private val HTMLElement.mustabeen get() = (getChildById(Ids.MUSTABEEN_CHECKBOX) as HTMLInputElement).checked
private val HTMLElement.pregStartTime get() = getChildById(Ids.PREG_START_TIME_INPUT) as HTMLInputElement
private val HTMLElement.pregEndTime get() = getChildById(Ids.PREG_END_TIME_INPUT) as HTMLInputElement
private val HTMLElement.aadatHaz get() = getChildById(Ids.AADAT_HAIZ_INPUT) as HTMLInputElement
private val HTMLElement.aadatTuhr get() = getChildById(Ids.AADAT_TUHR_INPUT) as HTMLInputElement
private val HTMLElement.aadatNifas get() = getChildById(Ids.AADAT_NIFAS_INPUT) as HTMLInputElement
private val HTMLElement.contentEnglishElement get() = getChildById(Ids.CONTENT_ENG) as HTMLParagraphElement
private val HTMLElement.contentUrduElement get() = getChildById(Ids.CONTENT_URDU) as HTMLParagraphElement
private val HTMLElement.contentDatesElement get() = getChildById(Ids.CONTENT_DATES) as HTMLParagraphElement
private val HTMLElement.inputsContainerCloneButton get() =
    getChildById(Ids.INPUTS_CONTAINER_CLONE_BUTTON) as HTMLButtonElement
private val HTMLElement.inputsContainerRemoveButton get() =
    getChildById(Ids.INPUTS_CONTAINER_REMOVE_BUTTON) as HTMLButtonElement

private var HTMLElement.haizDatesList: List<Entry>?
    get() = (contentDatesElement.asDynamic().haizDatesList as List<Entry>?)?.takeIf { it != undefined }
    set(value) { contentDatesElement.asDynamic().haizDatesList = value }

private val HTMLElement.pregnancyElements get() = Ids.pregnancyElementIds.map { id ->
    getChildById(id) as HTMLInputElement
}

private val HTMLElement.haizInputDatesRows: List<HTMLTableRowElement>
    get() {
        val inputDatesTable = getChildById(Ids.HAIZ_INPUT_TABLE) as HTMLTableElement
        val inputDatesTableBody = inputDatesTable.tBodies[0] as HTMLTableSectionElement
        @Suppress("UNCHECKED_CAST")
        return inputDatesTableBody.rows.asList() as List<HTMLTableRowElement>
    }

private val HTMLTableRowElement.startTimeInput get() = getChildById(Ids.Row.INPUT_START_TIME) as HTMLInputElement
private val HTMLTableRowElement.endTimeInput get() = getChildById(Ids.Row.INPUT_END_TIME) as HTMLInputElement
private val HTMLTableRowElement.buttonsContainer get() = getChildById(Ids.Row.BUTTONS_CONTAINER)!!
private val HTMLTableRowElement.removeButton get() = getChildById(Ids.Row.BUTTON_REMOVE) as HTMLButtonElement
private val HTMLTableRowElement.addBeforeButton get() = getChildById(Ids.Row.BUTTON_ADD_BEFORE) as HTMLButtonElement?

private val HTMLElement.haizTimeInputs get() = haizInputDatesRows.flatMap { row ->
    listOf(row.startTimeInput, row.endTimeInput)
}

private val HTMLElement.timeInputsGroups get() = listOf(listOf(pregStartTime, pregEndTime), haizTimeInputs)

fun main() {
    window.onload = {
        if(askPassword()){
            document.body!!.addInputLayout()
            setupRows(inputsContainers.first())
            document.addEventListener(Events.VISIBILITY_CHANGE, {
                if (!document.isHidden) {
                    setMaxToCurrentTimeForTimeInputs(inputsContainers.first())
                }
            })
        }else{
            askPassword()
        }
    }
}

fun askPassword():Boolean{
    val pass1 = "786"
    val password = window.prompt("Please enter password here. To request the password, please contact safarjal22@gmail.com", "")
    return pass1 == password
}

fun Node.addInputLayout() {
    append {
        headers()
        div {
            id = Ids.INPUT_CONTAINERS_CONTAINER
            inputFormDiv()
        }
        div {
            style = Styles.NEW_ROW
        }
    }
}

private fun TagConsumer<HTMLElement>.headers() {
    h1 {
        +"Mashqi Sawal"
    }
    p {
        +"""
            Please enter the start date-time for first dam in the first box, and the end date-time for that dam in the
            second box. To add another period after that, press Add. If you need to remove a period in the middle, click
            the remove button next to it. To add a spot, enter a period where the start time and the end time are the
            same. Once all periods have been added, click Calculate button, to get the solution.
        """.trimIndent()
    }
}

private fun removeInputsContainer(inputsContainer: HTMLElement) {
    inputsContainer.remove()
    comparisonContainer?.remove()
    inputsContainers.singleOrNull()?.inputsContainerRemoveButton?.remove()
    inputsContainersContainer.style.width = "${481*inputsContainers.size}px"
}

private fun cloneInputsContainer(inputsContainerToCopyFrom: HTMLElement) {
    comparisonContainer?.remove()
    if (inputsContainers.size == 1) {
        addRemoveInputsContainerButton(inputsContainerToCopyFrom)
    }
    val clonedInputsContainer = inputsContainerToCopyFrom.after {
        inputFormDiv(inputsContainerToCopyFrom)
    }.single()
    setupFirstRow(clonedInputsContainer)
    inputsContainersContainer.style.width = "${481*inputsContainers.size}px"
}

private fun addRemoveInputsContainerButton(inputContainer: HTMLElement) {
    inputContainer.inputsContainerCloneButton.before {
        button(type = ButtonType.button) {
            +"X"
            id = Ids.INPUTS_CONTAINER_REMOVE_BUTTON
            style = "float: right"
            onClickFunction = { event ->
                removeInputsContainer(findInputContainer(event))
            }
        }
    }
}

private fun addCompareButtonIfNeeded() {
    if (comparisonContainer != null ||
        inputsContainers.size < 2 ||
        inputsContainers.any { it.haizDatesList == null }
    ) return

    inputsContainersContainer.after {
        div {
            id = Ids.COMPARISON_CONTAINER
            button(type = ButtonType.button) {
                +"Calculate difference"
                style = "margin: 0 auto; display: block;"
                onClickFunction = { compareResults() }
            }
            content {
                id = Ids.CONTENT_DATES_DIFFERENCE
            }
            table {
                id = Ids.DATES_DIFFERENCE_TABLE
            }
        }
    }
}

private fun TagConsumer<HTMLElement>.inputFormDiv(inputContainerToCopyFrom: HTMLElement? = null) {
    div {
        id = Ids.INPUT_CONTAINER
        style = "width:464.06px; float: left; border:1px; padding:8px;"
        if (inputContainerToCopyFrom != null) {
            removeInputsContainerButton()
        }
        addInputsContainerButton()
        inputForm(inputContainerToCopyFrom)
        content()
    }
}

private fun TagConsumer<HTMLElement>.addInputsContainerButton() {
    inputsContainerAddRemoveButton {
        +"Clone"
        id = Ids.INPUTS_CONTAINER_CLONE_BUTTON
        onClickFunction = { event ->
            cloneInputsContainer(findInputContainer(event))
        }
    }
}

private fun TagConsumer<HTMLElement>.removeInputsContainerButton() {
    inputsContainerAddRemoveButton {
        +"X"
        id = Ids.INPUTS_CONTAINER_REMOVE_BUTTON
        onClickFunction = { event ->
            removeInputsContainer(findInputContainer(event))
        }
    }
}

private fun TagConsumer<HTMLElement>.inputsContainerAddRemoveButton(block : BUTTON.() -> Unit = {}) {
    button(type = ButtonType.button) {
        style = "float: right"
        block()
    }
}

private fun TagConsumer<HTMLElement>.content() {
    content {
        id = Ids.CONTENT_ENG
    }
    content {
        id = Ids.CONTENT_URDU
        dir = Dir.rtl
//            style += "font-family: Helvetica"
    }
    content {
        id = Ids.CONTENT_DATES
    }
}

private fun TagConsumer<HTMLElement>.inputForm(inputContainerToCopyFrom: HTMLElement?) {
    form(action = "javascript:void(0);") {
        dateConfigurationRadioButtons(inputContainerToCopyFrom)
        br()
        aadatInputs(inputContainerToCopyFrom)
        br()
        pregnancyCheckBox(inputContainerToCopyFrom)
        br()
        mustabeenCheckBox(inputContainerToCopyFrom)
        br()
        pregnancyStartTimeInput(inputContainerToCopyFrom)
        br()
        pregnancyEndTimeInput(inputContainerToCopyFrom)
        br()
        haizDatesInputTable(inputContainerToCopyFrom)
//        istimrarCheckBox(inputContainerToCopyFrom)
        br()
        calculateButton()
        onSubmitFunction = { event -> parseEntries(findInputContainer(event)) }
    }
}

private fun FlowContent.dateConfigurationRadioButtons(inputContainerToCopyFrom: HTMLElement?) {
    val isDateOnly = inputContainerToCopyFrom?.isDateOnly ?: IS_DEFAULT_INPUT_MODE_DATE_ONLY
    radioInput {
        id = Ids.DATE_TIME_RADIO
        name = Ids.DATE_AND_OR_RADIO
        checked = !isDateOnly
        onChangeFunction = { event -> onClickDateConfigurationRadioButton(findInputContainer(event)) }
    }
    label {
        htmlFor = Ids.DATE_TIME_RADIO
        +"Date and Time"
    }
    radioInput {
        id = Ids.DATE_ONLY_RADIO
        name = Ids.DATE_AND_OR_RADIO
        checked = isDateOnly
        onChangeFunction = { event -> onClickDateConfigurationRadioButton(findInputContainer(event)) }
    }
    label {
        htmlFor = Ids.DATE_ONLY_RADIO
        +"Date only"
    }
}

private fun FlowContent.aadatInputs(inputContainerToCopyFrom: HTMLElement?) {
    label {
        htmlFor = Ids.AADAT_HAIZ_INPUT
        +("Haiz Aadat: ")
    }
    input {
        id = Ids.AADAT_HAIZ_INPUT
        value = inputContainerToCopyFrom?.aadatHaz?.value.orEmpty()
        onInputFunction = { event -> (event.currentTarget as HTMLInputElement).validateAadat(3..10) }
    }
    br()
    label {
        htmlFor = Ids.AADAT_TUHR_INPUT
        +"Tuhr Aadat: "
    }
    input {
        id = Ids.AADAT_TUHR_INPUT
        value = inputContainerToCopyFrom?.aadatTuhr?.value.orEmpty()
        onInputFunction = { event -> (event.currentTarget as HTMLInputElement).validateAadat(15..6*30) }
    }
    br()
    label {
        htmlFor = Ids.AADAT_NIFAS_INPUT
        +"Nifas Aadat: "
    }
    input {
        id = Ids.AADAT_NIFAS_INPUT
        step = "any"
        required = true
        disabled = inputContainerToCopyFrom?.isPregnancy != true
        value = inputContainerToCopyFrom?.aadatNifas?.value.orEmpty()
        onInputFunction = { event -> (event.currentTarget as HTMLInputElement).validateAadat(1..40) }
    }
}

private fun HTMLInputElement.validateAadat(validityRange: ClosedRange<Int>) {
    value = value.replace("[^0-9:]".toRegex(), "")
    val doubleValidityRange = validityRange.start.toDouble()..validityRange.endInclusive.toDouble()
    setCustomValidity(try {
        val days = (parseDays(value)?.div(MILLISECONDS_IN_A_DAY))?.toDouble()
        require(days == null || days in doubleValidityRange) { "Aadat is incorrect" }
        ""
    } catch (e: IllegalArgumentException) {
        e.message ?: "Aadat is incorrect"
    })
}

private fun FlowContent.pregnancyCheckBox(inputContainerToCopyFrom: HTMLElement?) {
    label {
        htmlFor = Ids.PREGNANCY_CHECKBOX
        +"Pregnancy"
    }
    checkBoxInput {
        id = Ids.PREGNANCY_CHECKBOX
        checked = inputContainerToCopyFrom?.isPregnancy == true
        onChangeFunction = { event ->
            val isChecked = (event.currentTarget as HTMLInputElement).checked
            for (pregnancyElement in findInputContainer(event).pregnancyElements) {
                pregnancyElement.disabled = !isChecked
            }
        }
    }
}

private fun FlowContent.mustabeenCheckBox(inputContainerToCopyFrom: HTMLElement?) {
    label {
        htmlFor = Ids.MUSTABEEN_CHECKBOX
        +"Mustabeen ul Khilqah"
    }
    checkBoxInput {
        id = Ids.MUSTABEEN_CHECKBOX
        checked = inputContainerToCopyFrom?.mustabeen == true
        disabled = inputContainerToCopyFrom?.isPregnancy != true
    }
}

private fun FlowContent.pregnancyStartTimeInput(inputContainerToCopyFrom: HTMLElement?) {
    label {
        htmlFor = Ids.PREG_START_TIME_INPUT
        +"Pregnancy Start Time"
    }
    pregnancyTimeInput(inputContainerToCopyFrom) {
        id = Ids.PREG_START_TIME_INPUT
        onChangeFunction = { event ->
            findInputContainer(event).pregEndTime.min = (event.currentTarget as HTMLInputElement).value
        }
    }
}

private fun FlowContent.pregnancyEndTimeInput(inputContainerToCopyFrom: HTMLElement?) {
    label {
        htmlFor = Ids.PREG_END_TIME_INPUT
        +"Birth/Miscarriage time"
    }
    pregnancyTimeInput(inputContainerToCopyFrom) {
        id = Ids.PREG_END_TIME_INPUT
        onChangeFunction = { event ->
            findInputContainer(event).pregStartTime.max = (event.currentTarget as HTMLInputElement).value
        }
    }
}

private fun FlowContent.pregnancyTimeInput(inputContainerToCopyFrom: HTMLElement?, block: INPUT.() -> Unit = {}) {
    if (inputContainerToCopyFrom != null) {
        timeInput(inputContainerToCopyFrom) {
            disabled = !inputContainerToCopyFrom.isPregnancy
            block()
        }
    } else {
        timeInput(IS_DEFAULT_INPUT_MODE_DATE_ONLY) {
            disabled = true
//            max = currentTimeString(IS_DEFAULT_INPUT_MODE_DATE_ONLY)
            block()
        }
    }
}

//private fun FlowContent.istimrarCheckBox(inputContainerToCopyFrom: HTMLElement?) {
//    label {
//        htmlFor = Ids.ISTIMRAR_CHECKBOX
//        +"Istimrar"
//    }
//    checkBoxInput {
//        id = Ids.ISTIMRAR_CHECKBOX
//        checked = inputContainerToCopyFrom?.isIstimrar == true
//    }
//}

private fun FlowContent.calculateButton() {
    button {
        +"Calculate"
        onClickFunction = { event -> setMaxToCurrentTimeForTimeInputs(findInputContainer(event)) }
    }
}

private fun TagConsumer<HTMLElement>.content(block : P.() -> Unit = {}) {
    p {
        style = "white-space: pre-wrap;"
        block()
    }
}

private fun TagConsumer<HTMLElement>.haizDatesInputTable(inputContainerToCopyFrom: HTMLElement?) {
    table {
        id = Ids.HAIZ_INPUT_TABLE
        thead {
            tr {
                th { +"Start Time" }
                th { +"End Time" }
            }
        }
        tbody {
            if (inputContainerToCopyFrom != null) {
                for (inputDateRow in inputContainerToCopyFrom.haizInputDatesRows) {
                    inputRow(inputContainerToCopyFrom, inputDateRow.startTimeInput, inputDateRow.endTimeInput)
                }
            } else {
                inputRow(
                    isDateOnlyLayout = IS_DEFAULT_INPUT_MODE_DATE_ONLY,
                    minTimeInput = "",
                    maxTimeInput = ""//currentTimeString(IS_DEFAULT_INPUT_MODE_DATE_ONLY)
                )
            }
        }
    }
}

private fun TagConsumer<HTMLElement>.inputRow(isDateOnlyLayout: Boolean, minTimeInput: String, maxTimeInput: String) {
    tr {
        td {
            timeInput(isDateOnlyLayout, minTimeInput, maxTimeInput, indexWithinRow = 0) {
                id = Ids.Row.INPUT_START_TIME
            }
        }
        td {
            timeInput(isDateOnlyLayout, minTimeInput, maxTimeInput, indexWithinRow = 1) {
                id = Ids.Row.INPUT_END_TIME
            }
        }
        addRemoveButtonsTableData()
    }
}

private fun TagConsumer<HTMLElement>.inputRow(
    inputContainerToCopyFrom: HTMLElement,
    startTimeInputToCopyFrom: HTMLInputElement,
    endTimeInputToCopyFrom: HTMLInputElement
) {
    tr {
        td {
            timeInput(inputContainerToCopyFrom, startTimeInputToCopyFrom, indexWithinRow = 0) {
                id = Ids.Row.INPUT_START_TIME
            }
        }
        td {
            timeInput(inputContainerToCopyFrom, endTimeInputToCopyFrom, indexWithinRow = 1) {
                id = Ids.Row.INPUT_END_TIME
            }
        }
        addRemoveButtonsTableData()
    }
}

private fun TR.addRemoveButtonsTableData() {
    td {
        id = Ids.Row.BUTTONS_CONTAINER
        addButton()
        removeButton()
    }
}

private fun FlowContent.timeInput(
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

private fun FlowContent.timeInput(
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

private fun FlowContent.timeInput(
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

private fun FlowContent.timeInput(
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

private fun FlowContent.timeInput(
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

private fun FlowContent.removeButton() {
    button(type = ButtonType.button) {
        +"Remove"
        id = Ids.Row.BUTTON_REMOVE
        onClickFunction = { event ->
            val row = findRow(event)
            val inputContainer = findInputContainer(event)
            updateMinMaxForTimeInputsBeforeRemovingRow(inputContainer, row.rowIndexWithinTableBody)
            row.remove()
            setupFirstRow(inputContainer)
        }
    }
}

private fun FlowContent.addButton() {
    button(type = ButtonType.button) {
        +"Add"
        onClickFunction = { event ->
            val row = findRow(event)
            val inputContainer = findInputContainer(event)
            row.after {
                inputRow(
                    inputContainer.isDateOnly,
                    minTimeInput = row.endTimeInput.run { value.takeUnless(String::isEmpty) ?: min },
                    maxTimeInput = row.endTimeInput.max
                )
            }
            setupRows(inputContainer)
        }
    }
}

private fun TagConsumer<HTMLElement>.addBeforeButton() {
    button(type = ButtonType.button) {
        +"Add Before"
        id = Ids.Row.BUTTON_ADD_BEFORE
        onClickFunction = { event ->
            val row = findRow(event)
            val inputContainer = findInputContainer(event)
            row.before {
                inputRow(
                    inputContainer.isDateOnly,
                    minTimeInput = row.startTimeInput.min,
                    maxTimeInput = row.startTimeInput.run { value.takeUnless(String::isEmpty) ?: max }
                )
            }
            setupRows(inputContainer)
        }
    }
}

private fun findInputContainer(event: Event) =
    (event.currentTarget as Element).getAncestor<HTMLElement> { it.id.startsWith(Ids.INPUT_CONTAINER)}!!
private fun findRow(event: Event) = (event.currentTarget as Element).getAncestor<HTMLTableRowElement>()!!

private fun setupRows(inputContainer: HTMLElement) {
    setMaxToCurrentTimeForTimeInputs(inputContainer)
    setupFirstRow(inputContainer)
}

private fun setupFirstRow(inputContainer: HTMLElement) {
    updateRemoveButtonDisabledStateForFirstRow(inputContainer)
    ensureAddFirstButtonOnlyShownInFirstRow(inputContainer)
}

private fun updateRemoveButtonDisabledStateForFirstRow(inputContainer: HTMLElement) {
    val inputDatesRows = inputContainer.haizInputDatesRows
    inputDatesRows.first().removeButton.disabled = inputDatesRows.size == 1
    inputDatesRows.getOrNull(1)?.removeButton?.disabled = false
}

private fun ensureAddFirstButtonOnlyShownInFirstRow(inputContainer: HTMLElement) {
    for ((index, row) in inputContainer.haizInputDatesRows.withIndex()) {
        if (index > 0) {
            row.addBeforeButton?.remove()
        } else if (row.addBeforeButton == null) {
            row.buttonsContainer.append { addBeforeButton() }
        }
    }
}

private fun setMaxToCurrentTimeForTimeInputs(inputContainer: HTMLElement) {
    val currentTime = currentTimeString(inputContainer.isDateOnly)
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

private fun onClickDateConfigurationRadioButton(inputContainer: HTMLElement) {
    val isDateOnly = inputContainer.isDateOnly
    for (timeInput in inputContainer.timeInputsGroups.flatten()) {
        val newValue = convertInputValue(timeInput.value, isDateOnly)
        val newMin = convertInputValue(timeInput.min, isDateOnly)
        val newMax = convertInputValue(timeInput.max, isDateOnly)

        val dateInputType = if (isDateOnly) InputType.date else InputType.dateTimeLocal
        timeInput.type = dateInputType.realValue

        timeInput.value = newValue
        timeInput.min = newMin
        timeInput.max = newMax
    }
    if (!isDateOnly) {
        setMaxToCurrentTimeForTimeInputs(inputContainer)
    }
}

private fun parseEntries(inputContainer: HTMLElement) {

    with(inputContainer) {
        val entries = haizInputDatesRows.map { row ->
            Entry(
                startTime = Date(row.startTimeInput.valueAsNumber),
                endTime = Date(row.endTimeInput.valueAsNumber)
            )
        }
        @Suppress("UnsafeCastFromDynamic")
        val output = handleEntries(
            entries,
            parseDays(aadatHaz.value),
            parseDays(aadatTuhr.value),
            isDateOnly,
            isPregnancy,
            Pregnancy(
                pregStartTime.valueAsDate,
                pregEndTime.valueAsDate,
                parseDays(aadatNifas.value),
                mustabeen
            )
        )
        contentEnglishElement.innerHTML = output.englishText
        contentUrduElement.innerHTML = output.urduText
        contentDatesElement.innerHTML = output.haizDatesText
        haizDatesList = output.hazDatesList
    }
    addCompareButtonIfNeeded()
}

private fun compareResults() {
    val listOfLists = inputsContainers.map { it.haizDatesList!! }
    val str = getDifferenceFromMultiple(listOfLists)
    contentDatesDifferenceElement!!.innerHTML = str
    val output = generatInfoForCompareTable(listOfLists.toMutableList())
    drawCompareTable(output.headerList,output.listOfColorsOfDaysList, output.resultColors)
}

fun drawCompareTable(headerList:List<Date>, listOfColorsOfDaysList: List<List<Int>>, resultColors: List<Int>){
    val datesDifferenceTableElement = datesDifferenceTableElement!!
    datesDifferenceTableElement.style.width = "${headerList.size*30 +15}px"
    datesDifferenceTableElement.replaceChildren {
        div { id = "tHead"
            style = Styles.TABLE_HEAD_STYLE
            div { id = "monthRow"
                style =Styles.TABLE_ROW_STYLE
                for (header in headerList) {
                    val date = header.getDate()
                    div { id = "cello"
                        style = Styles.TABLE_CELL_STYLE
                        if (date == 1) {
                            +MonthNames[header.getMonth()]
                        }
                    }
                }
            }
            div{
                style = Styles.NEW_ROW
            }
            div { id = "datesRow"
                style = Styles.TABLE_ROW_STYLE
                println("Header list is $headerList")
                for (i in headerList.indices) {
                    val header = headerList[i]
                    val date = header.getDate().toString()

                    div { id = "cello"
                        style =Styles.TABLE_CELL_STYLE
                        +date
                    }
                }
            }
        }
        div{
            style = Styles.NEW_ROW
        }
        div { id = "tBody"
            style = Styles.TABLE_BODY_STYLE
            div{
                style = Styles.NEW_ROW
            }
            div { id = "emptyRow"
                style=Styles.TABLE_ROW_STYLE
                div{
                    id = "emptyHalfCellTopRow"
                    style = Styles.EMPTY_HALF_CELL_STYLE
                }
                for (day in resultColors){
                    div{
                        id = "emptyCellTopRow"
                        style = Styles.EMPTY_CELL_STYLE

                        if (day == 2) {
                            style += Styles.NA_PAKI
                        } else if(day == 1){
                            style += Styles.AYYAAM_E_SHAKK
                        }
                    }
                }
            }

            for (j in listOfColorsOfDaysList.indices) {
                val colorsOfDaysList = listOfColorsOfDaysList[j]
                div{
                    style = Styles.NEW_ROW
                }
                div { id = "sit${j+1}"
                    Styles.TABLE_ROW_STYLE
                    div { id="half_cell"
                        style = Styles.HALF_CELL
                    }

                    for (k in colorsOfDaysList.indices) {
                        val cellValue = colorsOfDaysList[k]
                        div { id = "cello"
                            style = Styles.TABLE_CELL_BORDER_STYLE +
                                    (if (cellValue == 1) Styles.NA_PAKI else "")
                            +"${k+1}"
                        }
                    }
                }
            }
        }
    }
}
