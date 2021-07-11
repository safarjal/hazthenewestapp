import kotlinx.html.*
import kotlinx.html.consumers.onFinalize
import kotlinx.html.dom.createTree
import kotlinx.html.js.onClickFunction
import org.w3c.dom.*
import kotlin.js.Date
import kotlin.math.round
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

const val MILLISECONDS_IN_A_DAY = 86400000.0
val TAB = "&nbsp;".repeat(8)


private fun ChildNode.insertSiblingRelative(
    block: TagConsumer<HTMLElement>.() -> Unit,
    insert: (ChildNode) -> Unit
): List<HTMLElement> = ArrayList<HTMLElement>().also { result ->
    (this as Node).ownerDocument!!.createTree().onFinalize { child, partial ->
        if (!partial) {
            result.add(child)
            insert(child)
        }
    }.block()
}

fun ChildNode.before(block: TagConsumer<HTMLElement>.() -> Unit) = insertSiblingRelative(block) { node -> before(node) }
fun ChildNode.after(block: TagConsumer<HTMLElement>.() -> Unit) = insertSiblingRelative(block) { node -> after(node) }

fun ParentNode.getChildById(id: String) = querySelector("#$id")


inline fun <reified T : Element> Element.getAncestor(): T? {
    var parent: Element? = parentElement
    while (true) {
        if (parent == null) return null
        if (parent is T) return parent
        parent = parent.parentElement
    }
}

var CommonAttributeGroupFacade.onRowElementClickFunction : (HTMLTableRowElement) -> Unit
    get() = throw UnsupportedOperationException("You can't read variable onClick")
    set(newValue) {
        onClickFunction = { event ->
            newValue((event.currentTarget as Element).getAncestor()!!)
        }
    }

val HTMLTableRowElement.rowIndexWithinTableBody get() =
    (parentElement as HTMLTableSectionElement).children.asList().indexOf(this)


@HtmlTagMarker
fun FlowOrInteractiveOrPhrasingContent.dateTimeLocalInputWithFallbackGuidelines(
    classes: String? = null,
    block: INPUT.() -> Unit = {}
) {
    dateTimeLocalInput(classes = classes) {
        placeholder = "YYYY-MM-DDThh:mm"
        pattern = "[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}"
        block()
    }
}

@HtmlTagMarker
fun FlowOrInteractiveOrPhrasingContent.customDateTimeInput(
    isDateOnly: Boolean,
    classes: String? = null,
    block: INPUT.() -> Unit = {}
) {
    if (isDateOnly) {
        dateInput(classes = classes, block = block)
    } else {
        dateTimeLocalInputWithFallbackGuidelines(classes = classes, block = block)
    }
}


fun Date.toDateInputString(isDateOnly: Boolean): String{
    val letterToTrimFrom = if (isDateOnly) 'T' else 'Z'
    return toISOString().takeWhile { it != letterToTrimFrom }
}

/* Looks like the compiler argument for opting in to experimental features
 * ('-Xopt-in=kotlin.RequiresOptIn') is not actually enforced, so suppressing the warning about it's
 * requirement here for now..
 */
@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@OptIn(ExperimentalTime::class)
// This is useful for setting the value (or min/max) of a datetime-local element.
fun currentTimeString(isDateOnly: Boolean): String {
    val currentTime = Date()
    val localTimeSetInUtc = Date(currentTime.getTime() -
            currentTime.getTimezoneOffset().toDuration(DurationUnit.MINUTES).inWholeMilliseconds)
    return localTimeSetInUtc.toDateInputString(isDateOnly)
}


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
 fun parseDate(date: Date, isDateOnly: Boolean):String{
  //   Sat, 05 Jun 2021 06:21:59 GMT
     var dateStr = (date.toUTCString()).dropLast(13).drop(5)
     println(dateStr)
     var hours = (date.toUTCString()).dropLast(10).drop(17).toInt()
     println(hours)
     var minutesStr = (date.toUTCString()).dropLast(7).drop(20)
     var hoursStr:String
     var ampm = "am"
     if (hours >=12){
         hours-=12
         ampm = "pm"
     }
     if(hours == 0){
         hours = 12
     }
     if(hours<10){
         hoursStr = "0${hours}"
     }else{
         hoursStr = hours.toString()
     }


     if(isDateOnly == true){
        return dateStr
        //05 Jun 2021
    }else{
        //05 Jun 2021 06:21
        return "${hoursStr}:${minutesStr} ${ampm}, on ${dateStr}"
    }
 }
