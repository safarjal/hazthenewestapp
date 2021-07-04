import kotlinx.html.js.onClickFunction
import kotlinx.html.dom.append
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.*
import kotlinx.html.consumers.onFinalize
import kotlinx.html.dom.createTree
import org.w3c.dom.*
import org.w3c.dom.events.Event
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

    object Row {
        const val INPUT_START_TIME = "input_start_time"
        const val INPUT_END_TIME = "input_end_time"
        const val BUTTON_ADD = "button_add"
        const val BUTTON_REMOVE = "button_remove"
    }
}

val table get() = document.getElementById(Ids.TABLE) as HTMLTableElement

private val Node.ownerDocumentExt: Document
    get() = when (this) {
        is Document -> this
        else -> ownerDocument ?: throw IllegalStateException("Node has no ownerDocument")
    }

// This is to complement the append and prepend methods, so that we don't have to depend on
// using HTMLTableElement.insertRow(), which will create an empty row that will have to be
// assigned an ID and populated, and instead can actually insert any HTML tag at any index
// of a parent node, including but not limited to a table row.
fun Node.insert(index: Int, block: TagConsumer<HTMLElement>.() -> Unit): List<HTMLElement> =
    ArrayList<HTMLElement>().also { result ->
        ownerDocumentExt.createTree().onFinalize { child, partial ->
            if (!partial) {
                result.add(child)
                insertBefore(child, childNodes[index])
            }
        }.block()
    }

fun ParentNode.getChildById(id: String) = querySelector("#$id")

fun main() {
    window.onload = {
        document.body?.sayHello()
        disableRemoveButtonForLastEntry()
    }
}

fun Node.sayHello() {
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
                    disableRemoveButtonForLastEntry()
                }
            }
            button {
                +"Remove"
                id = Ids.Row.BUTTON_REMOVE
                onClickFunction = { event ->
                    table.removeChild(getRow(event))
                    disableRemoveButtonForLastEntry()
                }
            }
        }
    }
}

fun disableRemoveButtonForLastEntry() {
    val rows = table.rows
    (rows[0]!!.getChildById(Ids.Row.BUTTON_REMOVE) as HTMLButtonElement).disabled = rows.length == 1
}

fun parseEntries() {
    try {
        val entries = table.rows.asList().map { row ->
            val startTime = (row.getChildById(Ids.Row.INPUT_START_TIME) as HTMLInputElement).value
            val endTime = (row.getChildById(Ids.Row.INPUT_END_TIME) as HTMLInputElement).value
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
        // We abort the mapping with a NullPointerException after showing an alert when
        // encountering an empty (unset) time input field. Nothing to do here.
    }
}

fun handleEntries(entries: List<Entry>) {
    val times = entries
        .flatMap { entry -> listOf(entry.startTime, entry.endTime) }
        .map { it.getTime() }
    if (times != times.sorted()) {
        window.alert("Please enter the dates in order")
        return
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
