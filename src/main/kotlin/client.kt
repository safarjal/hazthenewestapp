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

const val IS_DEFAULT_INPUT_MODE_DATE_ONLY = false

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
    const val ISTIMRAR_CHECKBOX = "istimrar_checkbox"
    const val HAIZ_AADAT_INPUT = "haiz_aadat_input"
    const val TUHR_AADAT_INPUT = "tuhr_aadat_input"
    const val DATE_ONLY_RADIO = "date_only_radio"
    const val DATE_TIME_RADIO = "date_time_radio"
    const val DATE_AND_OR_RADIO = "date_and_or_time"
}

private val contentEnglishElement get() = document.getElementById(Ids.CONTENT_ENG) as HTMLParagraphElement
private val contentUrduElement get() = document.getElementById(Ids.CONTENT_URDU) as HTMLParagraphElement

private val isDateOnly get() = (document.getElementById(Ids.DATE_ONLY_RADIO) as HTMLInputElement).checked
private val isIstimrar get() = (document.getElementById(Ids.ISTIMRAR_CHECKBOX) as HTMLInputElement).checked
private val aadatHaz get() = (document.getElementById(Ids.HAIZ_AADAT_INPUT) as HTMLInputElement).value
    .takeUnless(String::isEmpty)?.toDouble()
private val aadatTuhr get() = (document.getElementById(Ids.TUHR_AADAT_INPUT) as HTMLInputElement).value
    .takeUnless(String::isEmpty)?.toDouble()

private val inputDatesRows: List<HTMLTableRowElement>
    get() {
        val inputDatesTable = document.getElementById(Ids.INPUT_TABLE) as HTMLTableElement
        val inputDatesTableBody = inputDatesTable.tBodies[0] as HTMLTableSectionElement
        @Suppress("UNCHECKED_CAST")
        return inputDatesTableBody.rows.asList() as List<HTMLTableRowElement>
    }

private val HTMLTableRowElement.startTimeInput get() = getChildById(Ids.Row.INPUT_START_TIME) as HTMLInputElement
private val HTMLTableRowElement.endTimeInput get() = getChildById(Ids.Row.INPUT_END_TIME) as HTMLInputElement
private val HTMLTableRowElement.buttonsContainer get() = getChildById(Ids.Row.BUTTONS_CONTAINER)!!
private val HTMLTableRowElement.removeButton get() = getChildById(Ids.Row.BUTTON_REMOVE) as HTMLButtonElement
private val HTMLTableRowElement.addBeforeButton get() = getChildById(Ids.Row.BUTTON_ADD_BEFORE) as HTMLButtonElement?

private val timeInputs get() = inputDatesRows.flatMap { row -> listOf(row.startTimeInput, row.endTimeInput) }

fun main() {
    window.onload = {
        document.body!!.addInputLayout()
        setInitialStateForInputTable()
    }
}

fun Node.addInputLayout() {
    append {
        headers()

        form(action = "javascript:void(0);") {
            dateConfigurationRadioButtons()
            br()
            aadatInputs()
            datesInputTable()
            istimrarCheckBox()
            br()
            calculateButton()
            onSubmitFunction = { parseEntries() }
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

private fun FlowContent.dateConfigurationRadioButtons() {
    radioInput {
        id = Ids.DATE_TIME_RADIO
        name = Ids.DATE_AND_OR_RADIO
        checked = !IS_DEFAULT_INPUT_MODE_DATE_ONLY
        onChangeFunction = { onClickDateConfigurationRadioButton() }
    }
    label {
        htmlFor = Ids.DATE_TIME_RADIO
        +"Date and Time"
    }
    radioInput {
        id = Ids.DATE_ONLY_RADIO
        name = Ids.DATE_AND_OR_RADIO
        checked = IS_DEFAULT_INPUT_MODE_DATE_ONLY
        onChangeFunction = { onClickDateConfigurationRadioButton() }
    }
    label {
        htmlFor = Ids.DATE_ONLY_RADIO
        +"Date only"
    }
}

private fun FlowContent.aadatInputs() {
    label {
        htmlFor = Ids.HAIZ_AADAT_INPUT
        +("Haiz Aadat: ")
    }
    numberInput {
        id = Ids.HAIZ_AADAT_INPUT
        step = "any"
    }
    label {
        htmlFor = Ids.TUHR_AADAT_INPUT
        +"Tuhr Aadat: "
    }
    numberInput {
        id = Ids.TUHR_AADAT_INPUT
        step = "any"
    }
}

private fun FlowContent.istimrarCheckBox() {
    label {
        htmlFor = Ids.ISTIMRAR_CHECKBOX
        +"Istimrar"
    }
    checkBoxInput {
        id = Ids.ISTIMRAR_CHECKBOX
        checked = false
    }
}

private fun FlowContent.calculateButton() {
    button {
        +"Calculate"
        onClickFunction = { setMaxToCurrentTimeForTimeInputs() }
    }
}

private fun TagConsumer<HTMLElement>.content(block : P.() -> Unit = {}) {
    p {
        style = "white-space: pre-wrap;"
        block()
    }
}

private fun TagConsumer<HTMLElement>.datesInputTable() {
    table {
        id = Ids.INPUT_TABLE
        thead {
            tr {
                th { +"Start Time" }
                th { +"End Time" }
            }
        }
        tbody {
            inputRow(
                isDateOnlyLayout = IS_DEFAULT_INPUT_MODE_DATE_ONLY,
                minTimeInput = "",
                maxTimeInput = currentTimeString(IS_DEFAULT_INPUT_MODE_DATE_ONLY)
            )
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
        td {
            id = Ids.Row.BUTTONS_CONTAINER
            addButton()
            removeButton()
        }
    }
}

private fun FlowContent.timeInput(
    isDateOnlyLayout: Boolean,
    minTimeInput: String,
    maxTimeInput: String,
    indexWithinRow: Int,
    block: INPUT.() -> Unit = {}
) {
    customDateTimeInput(isDateOnlyLayout) {
        required = true
        min = minTimeInput
        max = maxTimeInput
        onClickFunction = {
            setMaxToCurrentTimeForTimeInputs()
        }
        onChangeFunction = { event ->
            setMinMaxForTimeInputsOnInput((findRow(event).rowIndexWithinTableBody * 2) + indexWithinRow)
        }
        block()
    }
}

private fun FlowContent.addButton() {
    button(type = ButtonType.button) {
        +"Add"
        onClickFunction = { event ->
            val row = findRow(event)
            row.after {
                inputRow(
                    isDateOnly,
                    minTimeInput = row.endTimeInput.run { value.takeUnless(String::isEmpty) ?: min },
                    maxTimeInput = row.endTimeInput.max
                )
            }
            setStateForFirstRow()
        }
    }
}

private fun FlowContent.removeButton() {
    button(type = ButtonType.button) {
        +"Remove"
        id = Ids.Row.BUTTON_REMOVE
        onClickFunction = { event ->
            val row = findRow(event)
            updateMinMaxForTimeInputsBeforeRemovingRow(row.rowIndexWithinTableBody)
            row.remove()
            setStateForFirstRow()
        }
    }
}

private fun TagConsumer<HTMLElement>.addBeforeButton() {
    button(type = ButtonType.button) {
        +"Add Before"
        id = Ids.Row.BUTTON_ADD_BEFORE
        onClickFunction = { event ->
            val row = findRow(event)
            row.before {
                inputRow(
                    isDateOnly,
                    minTimeInput = row.startTimeInput.min,
                    maxTimeInput = row.startTimeInput.run { value.takeUnless(String::isEmpty) ?: max }
                )
            }
            setStateForFirstRow()
        }
    }
}

private fun findRow(event: Event) = (event.currentTarget as Element).getAncestor<HTMLTableRowElement>()!!

private fun setInitialStateForInputTable() {
    setStateForFirstRow()
    setMaxToCurrentTimeForTimeInputs()
}

private fun setStateForFirstRow() {
    updateRemoveButtonDisabledStateForFirstRow()
    ensureAddFirstButtonOnlyShownInFirstRow()
}

private fun updateRemoveButtonDisabledStateForFirstRow() {
    inputDatesRows.first().removeButton.disabled = inputDatesRows.size == 1
    inputDatesRows.getOrNull(1)?.removeButton?.disabled = false
}

private fun ensureAddFirstButtonOnlyShownInFirstRow() {
    for ((index, row) in inputDatesRows.withIndex()) {
        if (index > 0) {
            row.addBeforeButton?.remove()
        } else if (row.addBeforeButton == null) {
            row.buttonsContainer.append { addBeforeButton() }
        }
    }
}

private fun setMaxToCurrentTimeForTimeInputs() {
    val currentTime = currentTimeString(isDateOnly)
    for (timeInput in timeInputs.asReversed()) {
        timeInput.max = currentTime
        if (timeInput.value.isNotEmpty()) break
    }
}

private fun setMinMaxForTimeInputsOnInput(index: Int) {
    val timeInputs = timeInputs
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

private fun updateMinMaxForTimeInputsBeforeRemovingRow(rowIndex: Int) {
    val timeInputs = timeInputs
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

private fun onClickDateConfigurationRadioButton() {
    for (timeInput in timeInputs) {
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

private fun parseEntries() {
    val entries = inputDatesRows.map { row ->
        Entry(
            startTime = Date(row.startTimeInput.valueAsNumber),
            endTime = Date(row.endTimeInput.valueAsNumber)
        )
    }
    val output = handleEntries(entries, isIstimrar, aadatHaz, aadatTuhr, isDateOnly)
    contentEnglishElement.innerHTML = output.englishText
    contentUrduElement.innerHTML = output.urduText
}
