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

val table get() = document.getElementById("table") as HTMLTableElement

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
                id = "table"
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
    td {
        id = rowId + "start_date_input"
        dateTimeLocalInput {  }
    }
    td { +"End" }
    td {
        id = rowId + "end_date_input"
        dateTimeLocalInput {  }
    }
    td {
        button {
            +"Add"
            onClickFunction = { addRow(rowId) }
        }
        button {
            +"Remove"
            onClickFunction = { removeRow() }
        }
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
            val startTime = ((row.children[1] as HTMLTableCellElement).firstChild as HTMLInputElement).value
            val endTime = ((row.children[3] as HTMLTableCellElement).firstChild as HTMLInputElement).value
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
    val sortedTimes = times.sorted()
    if (times != sortedTimes) {
        window.alert("Please enter the dates in order")
    }
    val durations = mutableListOf<Duration>()
    var isDam = true
    for ((index, time) in times.dropLast(1).withIndex()) {
        val nextTime = times[index + 1]
        durations.add(Duration(if (isDam) DurationType.DAM else DurationType.TUHR,nextTime - time))
        isDam = !isDam
    }
    for (duration in durations) {
        println("duration type = ${duration.type}, duration days = ${duration.days}")
    }
}
