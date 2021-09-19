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

private const val IS_DEFAULT_INPUT_MODE_DATE_ONLY = false

object Ids {
    const val INPUT_TABLE = "input_table"

    object Row {
        const val INPUT_START_TIME = "input_start_time"
        const val INPUT_END_TIME = "input_end_time"
        const val BUTTONS_CONTAINER = "button_add_before_container"
        const val BUTTON_REMOVE = "button_remove"
        const val BUTTON_ADD_BEFORE = "button_add_before"
    }

    const val CONTENT_ENG = "content_eng"
    const val CONTENT_URDU = "content_urdu"
    const val INPUT_CONTAINER_PREFIX = "input_container"
    const val INPUT_CONTAINER_PRIMARY = "${INPUT_CONTAINER_PREFIX}_primary"
    const val INPUT_CONTAINER_SECONDARY = "${INPUT_CONTAINER_PREFIX}_secondary"
    const val ISTIMRAR_CHECKBOX = "istimrar_checkbox"
    const val AADAT_HAIZ_INPUT = "aadat_haiz_input"
    const val AADAT_TUHR_INPUT = "aadat_tuhr_input"
    const val DATE_ONLY_RADIO = "date_only_radio"
    const val DATE_TIME_RADIO = "date_time_radio"
    const val DATE_AND_OR_RADIO = "date_and_or_time"
}

private val contentEnglishElement get() = document.getElementById(Ids.CONTENT_ENG) as HTMLParagraphElement
private val contentUrduElement get() = document.getElementById(Ids.CONTENT_URDU) as HTMLParagraphElement

private val primaryInputsContainer get() = document.getElementById(Ids.INPUT_CONTAINER_PRIMARY) as HTMLElement
private val secondaryInputsContainer get() = document.getElementById(Ids.INPUT_CONTAINER_SECONDARY) as HTMLElement?

private val HTMLElement.isDateOnly get() = (getChildById(Ids.DATE_ONLY_RADIO) as HTMLInputElement).checked
private val HTMLElement.isIstimrar get() = (getChildById(Ids.ISTIMRAR_CHECKBOX) as HTMLInputElement).checked
private val HTMLElement.aadatHazString get() = (getChildById(Ids.AADAT_HAIZ_INPUT) as HTMLInputElement).value
private val HTMLElement.aadatTuhrString get() = (getChildById(Ids.AADAT_TUHR_INPUT) as HTMLInputElement).value
private val HTMLElement.aadatHaz get() = aadatHazString.takeUnless(String::isEmpty)?.toDouble()
private val HTMLElement.aadatTuhr get() = aadatTuhrString.takeUnless(String::isEmpty)?.toDouble()

private val HTMLElement.inputDatesRows: List<HTMLTableRowElement>
    get() {
        val inputDatesTable = getChildById(Ids.INPUT_TABLE) as HTMLTableElement
        val inputDatesTableBody = inputDatesTable.tBodies[0] as HTMLTableSectionElement
        @Suppress("UNCHECKED_CAST")
        return inputDatesTableBody.rows.asList() as List<HTMLTableRowElement>
    }

private val HTMLTableRowElement.startTimeInput get() = getChildById(Ids.Row.INPUT_START_TIME) as HTMLInputElement
private val HTMLTableRowElement.endTimeInput get() = getChildById(Ids.Row.INPUT_END_TIME) as HTMLInputElement
private val HTMLTableRowElement.buttonsContainer get() = getChildById(Ids.Row.BUTTONS_CONTAINER)!!
private val HTMLTableRowElement.removeButton get() = getChildById(Ids.Row.BUTTON_REMOVE) as HTMLButtonElement
private val HTMLTableRowElement.addBeforeButton get() = getChildById(Ids.Row.BUTTON_ADD_BEFORE) as HTMLButtonElement?

private val HTMLElement.timeInputs get() = inputDatesRows.flatMap { row -> listOf(row.startTimeInput, row.endTimeInput) }

fun main() {
    window.onload = {
        document.body!!.addInputLayout()
        setupRows(primaryInputsContainer)
        document.addEventListener(Events.VISIBILITY_CHANGE, {
            if (!document.isHidden) {
                setMaxToCurrentTimeForTimeInputs(primaryInputsContainer)
            }
        })
    }
}

fun Node.addInputLayout() {
    append {
        headers()
        div {
            inputFormDiv {
                id = Ids.INPUT_CONTAINER_PRIMARY
            }
        }
        content {
            id = Ids.CONTENT_ENG
        }
        content {
            id = Ids.CONTENT_URDU
            dir = Dir.rtl
//            style += "font-family: Helvetica"
        }
    }
}

private fun TagConsumer<HTMLElement>.headers() {
    h1 {
        +"Mashqi Sawal"
        // TODO: Add toggle button for Zallah to handle this
        onClickFunction = { toggleSecondaryInputsContainer() }
    }
    p {
        +"""
            Please enter the start date-time for first dam in the first box, and the end date-time for that dam in the
            second box. To add another period after that, press Add. If you need to remove a period in the middle, click
            the remove button next to it. To add a spot, enter a period where the start time and the end time are the
            same. If this masla ends with istimrar, make a period that ends on today's date, then check the istimrar
            check box. Once all periods have been added, click Calculate button, to get the solution.
        """.trimIndent()
    }
}

private fun toggleSecondaryInputsContainer() {
    if (secondaryInputsContainer?.remove() != null) return

    primaryInputsContainer.after {
        inputFormDiv(inputContainerToCopyFrom = primaryInputsContainer) {
            id = Ids.INPUT_CONTAINER_SECONDARY
        }
    }
    setupRows(secondaryInputsContainer!!)
}

private fun TagConsumer<HTMLElement>.inputFormDiv(
    inputContainerToCopyFrom: HTMLElement? = null,
    block : DIV.() -> Unit = {}
) {
    div {
        style = "width:50%; float: left;"
        inputForm(inputContainerToCopyFrom)
        block()
    }
}

private fun TagConsumer<HTMLElement>.inputForm(inputContainerToCopyFrom: HTMLElement?) {
    form(action = "javascript:void(0);") {
        dateConfigurationRadioButtons(inputContainerToCopyFrom)
        br()
        aadatInputs(inputContainerToCopyFrom)
        datesInputTable(inputContainerToCopyFrom)
        istimrarCheckBox(inputContainerToCopyFrom)
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
    numberInput {
        id = Ids.AADAT_HAIZ_INPUT
        step = "any"
        value = inputContainerToCopyFrom?.aadatHazString.orEmpty()
    }
    label {
        htmlFor = Ids.AADAT_TUHR_INPUT
        +"Tuhr Aadat: "
    }
    numberInput {
        id = Ids.AADAT_TUHR_INPUT
        step = "any"
        value = inputContainerToCopyFrom?.aadatTuhrString.orEmpty()
    }
}

private fun FlowContent.istimrarCheckBox(inputContainerToCopyFrom: HTMLElement?) {
    label {
        htmlFor = Ids.ISTIMRAR_CHECKBOX
        +"Istimrar"
    }
    checkBoxInput {
        id = Ids.ISTIMRAR_CHECKBOX
        checked = inputContainerToCopyFrom?.isIstimrar == true
    }
}

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

private fun TagConsumer<HTMLElement>.datesInputTable(inputContainerToCopyFrom: HTMLElement?) {
    table {
        id = Ids.INPUT_TABLE
        thead {
            tr {
                th { +"Start Time" }
                th { +"End Time" }
            }
        }
        tbody {
            if (inputContainerToCopyFrom != null) {
                val isDateOnly = inputContainerToCopyFrom.isDateOnly
                for (inputDateRow in inputContainerToCopyFrom.inputDatesRows) {
                    inputRow(isDateOnly, inputDateRow.startTimeInput, inputDateRow.endTimeInput)
                }
            } else {
                inputRow(
                    isDateOnlyLayout = IS_DEFAULT_INPUT_MODE_DATE_ONLY,
                    minTimeInput = "",
                    maxTimeInput = currentTimeString(IS_DEFAULT_INPUT_MODE_DATE_ONLY)
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
    isDateOnlyLayout: Boolean,
    startTimeInputToCopyFrom: HTMLInputElement,
    endTimeInputToCopyFrom: HTMLInputElement
) {
    tr {
        td {
            timeInput(isDateOnlyLayout, startTimeInputToCopyFrom, indexWithinRow = 0) {
                id = Ids.Row.INPUT_START_TIME
            }
        }
        td {
            timeInput(isDateOnlyLayout, endTimeInputToCopyFrom, indexWithinRow = 1) {
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
    timeInput(isDateOnlyLayout, indexWithinRow) {
        min = minTimeInput
        max = maxTimeInput
        block()
    }
}

private fun FlowContent.timeInput(
    isDateOnlyLayout: Boolean,
    timeInputToCopyFrom: HTMLInputElement,
    indexWithinRow: Int,
    block: INPUT.() -> Unit = {}
) {
    timeInput(isDateOnlyLayout, indexWithinRow) {
        value = timeInputToCopyFrom.value
        min = timeInputToCopyFrom.min
        max = timeInputToCopyFrom.max
        block()
    }
}

private fun FlowContent.timeInput(
    isDateOnlyLayout: Boolean,
    indexWithinRow: Int,
    block: INPUT.() -> Unit = {}
) {
    customDateTimeInput(isDateOnlyLayout) {
        required = true
        onClickFunction = { event ->
            setMaxToCurrentTimeForTimeInputs(findInputContainer(event))
        }
        onChangeFunction = { event ->
            setMinMaxForTimeInputsOnInput(
                findInputContainer(event),
                (findRow(event).rowIndexWithinTableBody * 2) + indexWithinRow
            )
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
    (event.currentTarget as Element).getAncestor<HTMLElement> { it.id.startsWith(Ids.INPUT_CONTAINER_PREFIX)}!!
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
    val inputDatesRows = inputContainer.inputDatesRows
    inputDatesRows.first().removeButton.disabled = inputDatesRows.size == 1
    inputDatesRows.getOrNull(1)?.removeButton?.disabled = false
}

private fun ensureAddFirstButtonOnlyShownInFirstRow(inputContainer: HTMLElement) {
    for ((index, row) in inputContainer.inputDatesRows.withIndex()) {
        if (index > 0) {
            row.addBeforeButton?.remove()
        } else if (row.addBeforeButton == null) {
            row.buttonsContainer.append { addBeforeButton() }
        }
    }
}

private fun setMaxToCurrentTimeForTimeInputs(inputContainer: HTMLElement) {
    val currentTime = currentTimeString(inputContainer.isDateOnly)
    for (timeInput in inputContainer.timeInputs.asReversed()) {
        timeInput.max = currentTime
        if (timeInput.value.isNotEmpty()) break
    }
}

private fun setMinMaxForTimeInputsOnInput(inputContainer: HTMLElement, index: Int) {
    val timeInputs = inputContainer.timeInputs
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
    val timeInputs = inputContainer.timeInputs
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
    for (timeInput in inputContainer.timeInputs) {
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
    println("Calculate button was clicked")
    val entries = inputContainer.inputDatesRows.map { row ->
        Entry(
            startTime = Date(row.startTimeInput.valueAsNumber),
            endTime = Date(row.endTimeInput.valueAsNumber)
        )
    }

    //these are arbitrary values. we must form proper inputs.
    val isPregnancy = false
    val pregnancy = Pregnancy(Date(2020,3,1),Date(2020,11,13), 31, true)

    val output = with(inputContainer) { handleEntries(entries, isIstimrar, aadatHaz, aadatTuhr, isDateOnly, isPregnancy, pregnancy) }
    contentEnglishElement.innerHTML = output.englishText
    contentUrduElement.innerHTML = output.urduText
}
