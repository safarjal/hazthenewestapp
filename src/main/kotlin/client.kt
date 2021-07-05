import kotlinx.html.js.onClickFunction
import kotlinx.html.dom.append
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.*
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
        ensureRemoveButtonDisabledOnlyForLastEntry()
    }
}

fun Node.addInputLayout() {
    append {
        div {
            table {
                id = Ids.INPUT_TABLE
                inputRow()
            }
            button {
                +"Calculate"
                id = Ids.BUTTON_CALCULATE
                onClickFunction = { parseEntries() }
            }
        }
    }
}

fun TagConsumer<HTMLElement>.inputRow() {
    tr {
        td { +"Start" }
        td { dateTimeLocalInput { id = Ids.Row.INPUT_START_TIME } }
        td { +"End" }
        td { dateTimeLocalInput { id = Ids.Row.INPUT_END_TIME } }

        fun getRow(event: Event) = (event.currentTarget as Element).parentNode!!.parentNode as HTMLTableRowElement

        td {
            button {
                +"Add"
                id = Ids.Row.BUTTON_ADD
                onClickFunction = { event ->
                    inputTable.insert(getRow(event).rowIndex + 1) { inputRow() }
                    ensureRemoveButtonDisabledOnlyForLastEntry()
                }
            }
        }
        td {
            button {
                +"Remove"
                id = Ids.Row.BUTTON_REMOVE
                onClickFunction = { event ->
                    inputTable.removeChild(getRow(event))
                    ensureRemoveButtonDisabledOnlyForLastEntry()
                }
            }
        }
    }
}

private fun ensureRemoveButtonDisabledOnlyForLastEntry() {
    val inputRows = inputTable.rows
    (inputRows[0]!!.getChildById(Ids.Row.BUTTON_REMOVE) as HTMLButtonElement).disabled = inputRows.length == 1
}

private fun parseEntries() {
    val entries = try {
        inputTable.rows.asList().map { row ->
            val startTime = (row.getChildById(Ids.Row.INPUT_START_TIME) as HTMLInputElement).value
            val endTime = (row.getChildById(Ids.Row.INPUT_END_TIME) as HTMLInputElement).value

            require(startTime.isNotEmpty() && endTime.isNotEmpty())

            Entry(
                startTime = Date(startTime),
                endTime = Date(endTime)
            )
        }
    } catch (e: IllegalArgumentException) {
        window.alert("Please enter all the dates")
        return
    }
    try {
        handleEntries(entries)
    } catch (e: IllegalArgumentException) {
        window.alert("Please enter the dates in order")
        return
    }
}
