import kotlinx.html.dom.append
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.*
import kotlinx.html.form
import kotlinx.html.js.*
import kotlinx.html.tr
import org.w3c.dom.*
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

    const val CONTENT = "content"
    const val ISTIMRAR = "istimrar"
    const val HAIZ_AADAT = "haiz_aadat"
    const val TUHR_AADAT = "tuhr_aadat"
    const val DATE_ONLY_RADIO = "date_only_radio"
    const val DATE_TIME_RADIO = "date_time_radio"
    const val DATE_AND_OR_RADIO = "date_and_or_time"
}

private val inputDatesRows: List<HTMLTableRowElement>
    get() {
        val inputDatesTable = document.getElementById(Ids.INPUT_TABLE) as HTMLTableElement
        val inputDatesTableBody = inputDatesTable.tBodies[0] as HTMLTableSectionElement
        @Suppress("UNCHECKED_CAST")
        return inputDatesTableBody.rows.asList() as List<HTMLTableRowElement>
    }

private val isDateOnly get() = (document.getElementById(Ids.DATE_ONLY_RADIO) as HTMLInputElement).checked

fun main() {
    window.onload = {
        document.body!!.addInputLayout()
        setStateForFirstRow()
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
            button { +"Calculate" }
            onSubmitFunction = { parseEntries() }
        }
        p { id = Ids.CONTENT }
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
        htmlFor = Ids.HAIZ_AADAT
        +("Haiz Aadat: ")
    }
    textInput {
        id = Ids.HAIZ_AADAT
    }
    label {
        htmlFor = Ids.TUHR_AADAT
        +"Tuhr Aadat: "
    }
    textInput {
        id = Ids.TUHR_AADAT
    }
}

private fun FlowContent.istimrarCheckBox() {
    label {
        htmlFor = Ids.ISTIMRAR
        +"Istimrar"
    }
    checkBoxInput {
        id = Ids.ISTIMRAR
        checked = false
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
        tbody { inputRow(IS_DEFAULT_INPUT_MODE_DATE_ONLY) }
    }
}

private fun TagConsumer<HTMLElement>.inputRow(isDateOnlyLayout: Boolean) {
    tr {
        td {
            customDateTimeInput(isDateOnlyLayout) {
                id = Ids.Row.INPUT_START_TIME
                required = true
                onRowElementClickFunction = { row ->
                    setMinMaxForTimeInput(row.rowIndexWithinTableBody * 2)
                }
            }
        }
        td {
            customDateTimeInput(isDateOnlyLayout) {
                id = Ids.Row.INPUT_END_TIME
                required = true
                onRowElementClickFunction = { row ->
                    setMinMaxForTimeInput((row.rowIndexWithinTableBody * 2) + 1)
                }
            }
        }

        td {
            id = Ids.Row.BUTTONS_CONTAINER

            button(type = ButtonType.button) {
                +"Add"
                onRowElementClickFunction = { row ->
                    row.after { inputRow(isDateOnly) }
                    setStateForFirstRow()
                }
            }
            button(type = ButtonType.button) {
                +"Remove"
                id = Ids.Row.BUTTON_REMOVE
                onRowElementClickFunction = { row ->
                    row.remove()
                    setStateForFirstRow()
                }
            }
        }
    }
}

private fun TagConsumer<HTMLElement>.addBeforeButtonTableData() {
    button(type = ButtonType.button) {
        +"Add Before"
        id = Ids.Row.BUTTON_ADD_BEFORE
        onRowElementClickFunction = { row ->
            row.before { inputRow(isDateOnly) }
            setStateForFirstRow()
        }
    }
}

private fun setStateForFirstRow() {
    updateRemoveButtonDisabledStateForFirstRow()
    ensureAddFirstButtonOnlyShownInFirstRow()
}

private fun updateRemoveButtonDisabledStateForFirstRow() {
    (inputDatesRows.first().getChildById(Ids.Row.BUTTON_REMOVE) as HTMLButtonElement).disabled =
        inputDatesRows.size == 1
    (inputDatesRows.getOrNull(1)?.getChildById(Ids.Row.BUTTON_REMOVE) as HTMLButtonElement?)?.disabled = false
}

private fun ensureAddFirstButtonOnlyShownInFirstRow() {
    for ((index, row) in inputDatesRows.withIndex()) {
        val button = row.getChildById(Ids.Row.BUTTON_ADD_BEFORE)
        if (index > 0) {
            button?.remove()
        } else if (button == null) {
            row.getChildById(Ids.Row.BUTTONS_CONTAINER)!!.append { addBeforeButtonTableData() }
        }
    }
}

private fun setMinMaxForTimeInput(index: Int) {
    val timeInputs: List<HTMLInputElement> = inputDatesRows.flatMap { row ->
        listOf(
            row.getChildById(Ids.Row.INPUT_START_TIME) as HTMLInputElement,
            row.getChildById(Ids.Row.INPUT_END_TIME) as HTMLInputElement
        )
    }
    val timeInput = timeInputs[index]
    val timeEntries = timeInputs.map(HTMLInputElement::value)
    timeInput.min = timeEntries
        .take(index)
        .findLast(String::isNotEmpty)
        .orEmpty()
    timeInput.max = timeEntries
        .drop(index + 1)
        .find(String::isNotEmpty)
        ?: currentTimeString(isDateOnly)
}

private fun onClickDateConfigurationRadioButton() {
    for (row in inputDatesRows) {
        val startDateInput = row.getChildById(Ids.Row.INPUT_START_TIME) as HTMLInputElement
        val endDateInput = row.getChildById(Ids.Row.INPUT_END_TIME) as HTMLInputElement

        fun convertValueToNewFormat(dateInput: HTMLInputElement): String {
            val inputDateInMilliseconds = dateInput.valueAsNumber
            if (inputDateInMilliseconds.isNaN()) return ""
            return Date(inputDateInMilliseconds).toDateInputString(isDateOnly)
        }

        val startDateNewValue = convertValueToNewFormat(startDateInput)
        val endDateNewValue = convertValueToNewFormat(endDateInput)

        val dateInputType = if (isDateOnly) InputType.date else InputType.dateTimeLocal
        val dateInputTypeName = dateInputType.realValue

        startDateInput.type = dateInputTypeName
        endDateInput.type = dateInputTypeName

        startDateInput.value = startDateNewValue
        endDateInput.value = endDateNewValue
    }
}

private fun parseEntries() {
    val entries = inputDatesRows.map { row ->
        Entry(
            startTime = Date((row.getChildById(Ids.Row.INPUT_START_TIME) as HTMLInputElement).valueAsNumber),
            endTime = Date((row.getChildById(Ids.Row.INPUT_END_TIME) as HTMLInputElement).valueAsNumber)
        )
    }

    val istimrar: Boolean = (document.getElementById(Ids.ISTIMRAR) as HTMLInputElement).checked
    val inputtedAadatHaz:Double? = (document.getElementById(Ids.HAIZ_AADAT) as HTMLInputElement).value.toDoubleOrNull()
    val inputtedAadatTuhr:Double? = (document.getElementById(Ids.TUHR_AADAT) as HTMLInputElement).value.toDoubleOrNull()
    val output = handleEntries(entries, istimrar, inputtedAadatHaz,inputtedAadatTuhr, isDateOnly)

    document.getElementById(Ids.CONTENT)!!.innerHTML = output
        .replace("\n", "<br>")
        .replace("\t", TAB)
}
