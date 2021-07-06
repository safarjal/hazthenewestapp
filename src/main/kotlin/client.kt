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
        form(action = "javascript:void(0);") {
            table {
                id = Ids.INPUT_TABLE
                inputRow()
            }
            submitInput {
                +"Calculate"
                id = Ids.BUTTON_CALCULATE
            }
            onSubmitFunction = { parseEntries() }
        }
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
        .dropLast(timeInputs.size - index)
        .findLast(String::isNotEmpty)
        .orEmpty()
    timeInput.max = timeEntries
        .drop(index + 1)
        .find(String::isNotEmpty)
        ?: currentTimeString()
}

private fun parseEntries() {
    val entries = inputTable.rows.asList().map { row ->
        val startTime = (row.getChildById(Ids.Row.INPUT_START_TIME) as HTMLInputElement).value
        val endTime = (row.getChildById(Ids.Row.INPUT_END_TIME) as HTMLInputElement).value

        require(startTime.isNotEmpty() && endTime.isNotEmpty())

        Entry(
            startTime = Date(startTime),
            endTime = Date(endTime)
        )
    }
    try {
        handleEntries(entries)
    } catch (e: IllegalArgumentException) {
        window.alert("Please enter the dates in order")
        return
    }
}
