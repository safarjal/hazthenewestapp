import kotlinx.html.js.onClickFunction
import kotlinx.html.dom.append
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.*
import org.w3c.dom.*
import org.w3c.dom.events.Event
import kotlin.js.Date

object Ids {
    const val TABLE = "table"

    object Row {
        const val INPUT_START_TIME = "input_start_time"
        const val INPUT_END_TIME = "input_end_time"
        const val BUTTON_ADD = "button_add"
        const val BUTTON_REMOVE = "button_remove"
    }
}

val table get() = document.getElementById(Ids.TABLE) as HTMLTableElement

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
                id = Ids.TABLE
                inputRow()
            }
            button {
                +"Calculate"
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
        td {
            fun getRow(event: Event) = (event.target as Element).parentNode!!.parentNode as HTMLTableRowElement

            button {
                +"Add"
                id = Ids.Row.BUTTON_ADD
                onClickFunction = { event ->
                    table.insert(getRow(event).rowIndex + 1) { inputRow() }
                    ensureRemoveButtonDisabledOnlyForLastEntry()
                }
            }
            button {
                +"Remove"
                id = Ids.Row.BUTTON_REMOVE
                onClickFunction = { event ->
                    table.removeChild(getRow(event))
                    ensureRemoveButtonDisabledOnlyForLastEntry()
                }
            }
        }
    }
}

private fun ensureRemoveButtonDisabledOnlyForLastEntry() {
    val rows = table.rows
    (rows[0]!!.getChildById(Ids.Row.BUTTON_REMOVE) as HTMLButtonElement).disabled = rows.length == 1
}

private fun parseEntries() {
    try {
        val entries = table.rows.asList().map { row ->
            val startTime = (row.getChildById(Ids.Row.INPUT_START_TIME) as HTMLInputElement).value
            val endTime = (row.getChildById(Ids.Row.INPUT_END_TIME) as HTMLInputElement).value

            require(startTime.isNotEmpty() && endTime.isNotEmpty()) {
                window.alert("Please enter all the dates")
            }

            Entry(
                startTime = Date(startTime),
                endTime = Date(endTime)
            )
        }
        handleEntries(entries)
    } catch (e: IllegalArgumentException) {
        // The require function aborts the mapping with an IllegalArgumentException after showing
        // an alert, when encountering an empty (unset) time input field. Nothing to do here.
    }
}
