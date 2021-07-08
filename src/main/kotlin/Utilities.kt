import kotlinx.html.TagConsumer
import kotlinx.html.consumers.onFinalize
import kotlinx.html.dom.createTree
import org.w3c.dom.*
import kotlin.js.Date
import kotlin.math.round
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

private val Node.ownerDocumentExt: Document
    get() = when (this) {
        is Document -> this
        else -> ownerDocument ?: throw IllegalStateException("Node has no ownerDocument")
    }

/* This is to complement the append and prepend methods, so that we don't have to depend on
 * using HTMLTableElement.insertRow(), which will create an empty row that will have to be
 * assigned an ID and populated, and instead can actually insert any HTML tag at any index
 * of a parent node, including but not limited to a table row.
 */
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


/* Looks like the compiler argument for opting in to experimental features
 * ('-Xopt-in=kotlin.RequiresOptIn') is not actually enforced, so suppressing the warning about it's
 * requirement here for now..
 */
@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@OptIn(ExperimentalTime::class)
// This is useful for setting the value (or min/max) of a datetime-local element.
fun currentTimeString() = Date()
    .let { currentDate ->
        Date(currentDate.getTime() -
                currentDate.getTimezoneOffset().toDuration(DurationUnit.MINUTES).inWholeMilliseconds)
    }
    .toISOString().substring(0 until 16)

val MILLISECONDS_IN_A_DAY = 86400000.0
val TAB = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"

fun addTimeToDate(date: Date,timeInMilliseconds:Long):Date{
    return Date(date.getTime() + timeInMilliseconds)
}

fun daysHoursMinutesDigital(numberOfDays:Double):String{
    var totalMinutes = numberOfDays*24*60

    var minutes=totalMinutes%60;
    var remainingHours = (totalMinutes - minutes)/60
    var hours = remainingHours % 24;
    var days = (remainingHours - hours)/24;
    minutes=round(minutes);
    hours=round(hours)
    days=round(days)
    var strHours = hours.toString()
    var strMinutes = minutes.toString()
    var strDays = days.toString()
    if(hours<10){
        strHours = "0${hours}";
    }
    if(minutes<10){
        strMinutes = "0${minutes}";
    }
    var returnStatement = "${strDays}d:${strHours}h:${strMinutes}m";
    return(returnStatement);
}


