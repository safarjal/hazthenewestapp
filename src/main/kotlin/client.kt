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
        const val BUTTON_REMOVE = "button_remove"
        const val BUTTON_ADD_BEFORE_CONTAINER = "button_add_before"
    }

    const val CONTENT = "content"
    const val ISTIMRAR = "istimrar"
    const val HAIZ_AADAT = "haiz_aadat"
    const val TUHR_AADAT = "tuhr_aadat"
    const val DATE_ONLY_RADIO = "date_only_radio"
    const val DATE_TIME_RADIO = "date_time_radio"
    const val DATE_AND_OR_RADIO = "date_and_or_time"
}

val inputDatesTableBody
    get() = (document.getElementById(Ids.INPUT_TABLE) as HTMLTableElement).tBodies[0] as HTMLTableSectionElement

val isDateOnly get() = (document.getElementById(Ids.DATE_ONLY_RADIO) as HTMLInputElement).checked

fun main() {
    window.onload = {
        document.body!!.addInputLayout()
        setStateForFirstRow()
    }
}

fun Node.addInputLayout() {
    append {
        h1{
            +"Mashqi Sawal"
        }
        p{
            +"""
                Please enter the start date-time for first dam in the first box, and the end date-time for that dam in
                the second box. To add another period after that, press Add. If you need to remove a period in the
                middle, click the remove button next to it. To add a spot, enter a period where the start time and the
                end time are the same. If this masla ends with istimrar, make a period that ends on today's date, then
                check the istimrar check box. Once all periods have been added, click Calculate button, to get the
                solution.
            """.trimIndent()
        }

        form(action = "javascript:void(0);") {
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
            br {}
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
            table {
                id = Ids.INPUT_TABLE
                thead {
                    tr {
                        th { +"Start Time" }
                        th { +"End Time" }
                    }
                }
                tbody {
                    inputRow(isDateOnly = IS_DEFAULT_INPUT_MODE_DATE_ONLY)
                }
            }
            label {
                htmlFor = Ids.ISTIMRAR
                +"Istimrar"
            }
            checkBoxInput {
                id = Ids.ISTIMRAR
                checked = false
            }
            br {  }
            button {
                +"Calculate"
            }
            onSubmitFunction = { parseEntries() }
        }
        p { id = Ids.CONTENT }
    }
}

private fun TagConsumer<HTMLElement>.inputRow(isDateOnly: Boolean) {
    tr {
        td {
            customDateTimeInput(isDateOnly) {
                id = Ids.Row.INPUT_START_TIME
                required = true
                onRowElementClickFunction = { row ->
                    setMinMaxForTimeInput(row.rowIndexWithinTableBody * 2)
                }
            }
        }
        td {
            customDateTimeInput(isDateOnly) {
                id = Ids.Row.INPUT_END_TIME
                required = true
                onRowElementClickFunction = { row ->
                    setMinMaxForTimeInput((row.rowIndexWithinTableBody * 2) + 1)
                }
            }
        }

        td {
            button(type = ButtonType.button) {
                +"Add"
                onRowElementClickFunction = { row ->
                    inputDatesTableBody.insert(row.rowIndexWithinTableBody + 1) { inputRow(isDateOnly) }
                    setStateForFirstRow()
                }
            }
            button(type = ButtonType.button) {
                +"Remove"
                id = Ids.Row.BUTTON_REMOVE
                onRowElementClickFunction = { row ->
                    inputDatesTableBody.removeChild(row)
                    setStateForFirstRow()
                }
            }
        }
    }
}

private fun TagConsumer<HTMLElement>.addBeforeButtonTableData() {
    td {
        id = Ids.Row.BUTTON_ADD_BEFORE_CONTAINER
        button(type = ButtonType.button) {
            +"Add Before"
            onRowElementClickFunction = { row ->
                inputDatesTableBody.insert(row.rowIndexWithinTableBody) { inputRow(isDateOnly) }
                setStateForFirstRow()
            }
        }
    }
}

private fun setStateForFirstRow() {
    updateRemoveButtonDisabledStateForFirstRow()
    ensureAddFirstButtonOnlyShownInFirstRow()
}

private fun updateRemoveButtonDisabledStateForFirstRow() {
    val inputRows = inputDatesTableBody.rows
    (inputRows[0]!!.getChildById(Ids.Row.BUTTON_REMOVE) as HTMLButtonElement).disabled = inputRows.length == 1
    (inputRows[1]?.getChildById(Ids.Row.BUTTON_REMOVE) as HTMLButtonElement?)?.disabled = false
}

private fun ensureAddFirstButtonOnlyShownInFirstRow() {
    for ((index, row) in inputDatesTableBody.rows.asList().withIndex()) {
        val addBeforeButtonContainer = row.getChildById(Ids.Row.BUTTON_ADD_BEFORE_CONTAINER)
        if (index == 0) {
            if (addBeforeButtonContainer == null) {
                row.append { addBeforeButtonTableData() }
            }
        } else {
            if (addBeforeButtonContainer != null) {
                row.removeChild(addBeforeButtonContainer)
            }
        }
    }
}

private fun setMinMaxForTimeInput(index: Int) {
    val timeInputs: List<HTMLInputElement> = inputDatesTableBody.rows.asList().flatMap { row ->
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
    for (row in inputDatesTableBody.rows.asList()) {
        val startDateInput = row.getChildById(Ids.Row.INPUT_START_TIME) as HTMLInputElement
        val endDateInput = row.getChildById(Ids.Row.INPUT_END_TIME) as HTMLInputElement

        fun convertValueToNewFormat(dateInput: HTMLInputElement): String {
            val inputDateInMilliseconds = dateInput.valueAsNumber
            if (inputDateInMilliseconds.isNaN()) return ""
            val letterToTrimFrom = if (isDateOnly) 'T' else 'Z'
            return Date(inputDateInMilliseconds).toISOString().takeWhile { it != letterToTrimFrom }
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
    val entries = inputDatesTableBody.rows.asList().map { row ->
        Entry(
            startTime = Date((row.getChildById(Ids.Row.INPUT_START_TIME) as HTMLInputElement).valueAsNumber),
            endTime = Date((row.getChildById(Ids.Row.INPUT_END_TIME) as HTMLInputElement).valueAsNumber)
        )
    }

    val istimrar: Boolean = (document.getElementById(Ids.ISTIMRAR) as HTMLInputElement).checked
    val inputtedAadatHaz:Double? = (document.getElementById(Ids.HAIZ_AADAT) as HTMLInputElement).value.toDoubleOrNull()
    val inputtedAadatTuhr:Double? = (document.getElementById(Ids.TUHR_AADAT) as HTMLInputElement).value.toDoubleOrNull()
    val output = try {
        handleEntries(entries, istimrar, inputtedAadatHaz,inputtedAadatTuhr)
    } catch (e: IllegalArgumentException) {
        window.alert("Please enter the dates in order")
        return
    }

    document.getElementById(Ids.CONTENT)!!.innerHTML = output
        .replace("\n", "<br>")
        .replace("\t", TAB)
}
