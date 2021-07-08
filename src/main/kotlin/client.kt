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

object Ids {
    const val INPUT_TABLE = "input_table"

    object Row {
        const val INPUT_START_TIME = "input_start_time"
        const val INPUT_END_TIME = "input_end_time"
        const val BUTTON_ADD = "button_add"
        const val BUTTON_REMOVE = "button_remove"
    }

    const val BUTTON_CALCULATE = "button_calculate"

    const val CONTENT = "content"
    const val ISTIMRAR = "istimrar"
}

val inputTable get() = document.getElementById(Ids.INPUT_TABLE) as HTMLTableElement

fun main() {
    window.onload = {
        document.body!!.addInputLayout()
        updateRemoveButtonDisabledStateForFirstRow()
    }
}

fun Node.addInputLayout() {
    append {
        h1{
            this.text("Mashqi Sawal")
        }
        p{
            this.text("Please enter the start date-time for first dam in the first box, and the end date-time" +
                    " for that dam in the second box. To add another period after that, press Add. If you need to" +
                    " remove a period in the middle, click the remove button next to it. To add a spot, enter a period" +
                    " where the start time and the end time are the same. If this masla ends with istimrar, make a period" +
                    " that ends on today's date, then check the istimrar check box. Once all periods have been " +
                    "added, click Calculate button, to get the solution.")
        }
        form(action = "javascript:void(0);") {
            table {
                id = Ids.INPUT_TABLE
                inputRow()
            }
            label{
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
                id = Ids.BUTTON_CALCULATE
            }
            onSubmitFunction = { parseEntries() }
        }
        p { id = Ids.CONTENT }
    }
}

fun TagConsumer<HTMLElement>.inputRow() {
    tr {
        fun getRow(event: Event) = (event.currentTarget as Element).parentNode!!.parentNode as HTMLTableRowElement

        td {
            label {
                +"Start"
                htmlFor = Ids.Row.INPUT_START_TIME
            }
            dateTimeLocalInput {
                id = Ids.Row.INPUT_START_TIME
                required = true
                onClickFunction = { event -> setMinMaxForTimeInput(getRow(event).rowIndex * 2) }
            }
        }
        td {
            label {
                +"End"
                htmlFor = Ids.Row.INPUT_END_TIME
            }
            dateTimeLocalInput {
                id = Ids.Row.INPUT_END_TIME
                required = true
                onClickFunction = { event -> setMinMaxForTimeInput((getRow(event).rowIndex * 2) + 1) }
            }
        }

        td {
            button(type = ButtonType.button) {
                +"Add"
                id = Ids.Row.BUTTON_ADD
                onClickFunction = { event ->
                    inputTable.insert(getRow(event).rowIndex + 1) { inputRow() }
                    updateRemoveButtonDisabledStateForFirstRow()
                }
            }
            button(type = ButtonType.button) {
                +"Remove"
                id = Ids.Row.BUTTON_REMOVE
                onClickFunction = { event ->
                    inputTable.removeChild(getRow(event))
                    updateRemoveButtonDisabledStateForFirstRow()
                }
            }
        }
    }
}

private fun updateRemoveButtonDisabledStateForFirstRow() {
    val inputRows = inputTable.rows
    (inputRows[0]!!.getChildById(Ids.Row.BUTTON_REMOVE) as HTMLButtonElement).disabled = inputRows.length == 1
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

private fun parseEntries() {
    val entries = inputTable.rows.asList().map { row ->
        Entry(
            startTime = Date((row.getChildById(Ids.Row.INPUT_START_TIME) as HTMLInputElement).value),
            endTime = Date((row.getChildById(Ids.Row.INPUT_END_TIME) as HTMLInputElement).value)
        )
    }

    val istimrar: Boolean = (document.getElementById(Ids.ISTIMRAR) as HTMLInputElement).checked
    val output = try {
        handleEntries(entries, istimrar)
    } catch (e: IllegalArgumentException) {
        window.alert("Please enter the dates in order")
        return
    }

    document.getElementById(Ids.CONTENT)!!.innerHTML = output.replace("\n", "<br>").replace("\t","${TAB}")
}
