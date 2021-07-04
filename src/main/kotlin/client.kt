import kotlinx.html.js.onClickFunction
import kotlinx.html.dom.append
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.*
import org.w3c.dom.*
import kotlin.js.Date

class Entry(
    val startTime: Date,
    val endTime: Date
)

enum class DurationType {
    DAM, TUHR
}

class Duration(
    val type: DurationType,
    val timeInMilliseconds: Double
) {
    val days get() = timeInMilliseconds / 86400000
}

object Ids {
    const val TABLE = "table"
    const val START_TIME_INPUT = "start_time_input"
    const val END_TIME_INPUT = "end_time_input"
}

val table get() = document.getElementById(Ids.TABLE) as HTMLTableElement

var rowId = 0
fun getIncrementedRowId() = rowId++.toString()
fun getRowById(rowId: String) = document.getElementById(rowId) as HTMLTableRowElement

fun main() {
    window.onload = {
        document.body?.sayHello()
        checkIfOnlyOneRow()
    }
}

fun Node.sayHello() {
    append {
        div {
            table {
                id = Ids.TABLE
                tr {
                    id = getIncrementedRowId()
                    inputRow(id)
                }
            }
            button {
                +"Calculate"
                onClickFunction = { parseEntries() }
            }
        }
    }
}

fun TagConsumer<HTMLElement>.inputRow(rowId: String) {
    td { +"Start" }
    td { dateTimeLocalInput { id = Ids.START_TIME_INPUT } }
    td { +"End" }
    td { dateTimeLocalInput { id = Ids.END_TIME_INPUT } }
    td {
        button { +"Add"; onClickFunction = { addRow(rowId) } }
        button { +"Remove"; onClickFunction = { removeRow() } }
    }
}

fun addRow(rowId: String) {
    table.insertRow(getRowById(rowId).rowIndex + 1).apply {
        id = getIncrementedRowId()
        append { inputRow(id) }
    }
    checkIfOnlyOneRow()
}

fun removeRow() {
    table.removeChild(document.getElementById(rowId.toString())!!)
    checkIfOnlyOneRow()
}

fun checkIfOnlyOneRow() {
    val rows = table.rows
    (rows[0]!!.lastChild!!.lastChild as HTMLButtonElement).disabled = rows.length == 1
}

fun parseEntries() {
    try {
        val entries = table.rows.asList().map { row ->
            val startTime = (row.querySelector('#' + Ids.START_TIME_INPUT) as HTMLInputElement).value
            val endTime = (row.querySelector('#' + Ids.END_TIME_INPUT) as HTMLInputElement).value
            if (startTime.isEmpty() || endTime.isEmpty()) {
                window.alert("Please enter all the dates")
                throw NullPointerException()
            }
            Entry(
                startTime = Date(startTime),
                endTime = Date(endTime)
            )
        }
        handleEntries(entries)
    } catch (e: NullPointerException) {
        println("catching null pointer")
    }
}

fun handleEntries(entries: List<Entry>) {
    val times = entries
        .flatMap { entry -> listOf(entry.startTime, entry.endTime) }
        .map { it.getTime() }
    if (times != times.sorted()) {
        window.alert("Please enter the dates in order")
    }
    var isDam = true
    val durations = times.zipWithNext { firstTime, secondTime ->
        val type = if (isDam) DurationType.DAM else DurationType.TUHR
        isDam = !isDam
        Duration(type, timeInMilliseconds = secondTime - firstTime)
    }
    for (duration in durations) {
        println("duration type = ${duration.type}, duration days = ${duration.days}")
    }
}
