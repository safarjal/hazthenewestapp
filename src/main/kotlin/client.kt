import kotlinx.html.dom.append
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.*
import kotlinx.html.form
import kotlinx.html.js.*
import kotlinx.html.tr
import org.w3c.dom.*
import kotlin.js.Date

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

val inputTable get() = document.getElementById(Ids.INPUT_TABLE) as HTMLTableElement

fun main() {
    window.onload = {
        document.body!!.addInputLayout()
        onClickDateConfigurationRadioButton(
            isDateOnly = (document.getElementById(Ids.DATE_ONLY_RADIO) as HTMLInputElement).checked
        )
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
                checked = true
                onChangeFunction = { onClickDateConfigurationRadioButton(isDateOnly = false) }
            }
            label {
               htmlFor = Ids.DATE_TIME_RADIO
                +"Date and Time"
            }
            radioInput {
                id = Ids.DATE_ONLY_RADIO
                name = Ids.DATE_AND_OR_RADIO
                onChangeFunction = { onClickDateConfigurationRadioButton(isDateOnly = true) }
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
                inputRow()
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

private fun TagConsumer<HTMLElement>.inputRow() {
    tr {
        td {
            label {
                +"Start"
                htmlFor = Ids.Row.INPUT_START_TIME
            }

            dateTimeLocalInputWithFallbackGuidelines {
                id = Ids.Row.INPUT_START_TIME
                required = true
                onRowElementClickFunction = { row -> setMinMaxForTimeInput(row.rowIndex * 2) }
            }
        }
        td {
            label {
                +"End"
                htmlFor = Ids.Row.INPUT_END_TIME
            }
            dateTimeLocalInputWithFallbackGuidelines {
                id = Ids.Row.INPUT_END_TIME
                required = true
                onRowElementClickFunction = { row -> setMinMaxForTimeInput((row.rowIndex * 2) + 1) }
            }
        }

        td {
            button(type = ButtonType.button) {
                +"Add"
                onRowElementClickFunction = { row ->
                    inputTable.insert(row.rowIndex + 1) { inputRow() }
                    setStateForFirstRow()
                }
            }
            button(type = ButtonType.button) {
                +"Remove"
                id = Ids.Row.BUTTON_REMOVE
                onRowElementClickFunction = { row ->
                    inputTable.removeChild(row)
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
                inputTable.insert(row.rowIndex) { inputRow() }
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
    val inputRows = inputTable.rows
    (inputRows[0]!!.getChildById(Ids.Row.BUTTON_REMOVE) as HTMLButtonElement).disabled = inputRows.length == 1
}

private fun ensureAddFirstButtonOnlyShownInFirstRow() {
    for ((index, row) in inputTable.rows.asList().withIndex()) {
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
    val timeInputs: List<HTMLInputElement> = inputTable.rows.asList().flatMap { row ->
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
        ?: currentTimeString()
}

fun onClickDateConfigurationRadioButton(isDateOnly: Boolean) {
    for (row in inputTable.rows.asList()) {
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
    val entries = inputTable.rows.asList().map { row ->
        Entry(
            startTime = Date((row.getChildById(Ids.Row.INPUT_START_TIME) as HTMLInputElement).valueAsNumber),
            endTime = Date((row.getChildById(Ids.Row.INPUT_END_TIME) as HTMLInputElement).valueAsNumber)
        )
    }

    val isDateOnly = (document.getElementById(Ids.DATE_ONLY_RADIO) as HTMLInputElement).checked
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
