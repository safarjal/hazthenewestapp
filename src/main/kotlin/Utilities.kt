import kotlinx.browser.document
import kotlinx.html.*
import kotlinx.html.consumers.onFinalize
import kotlinx.html.dom.createTree
import org.w3c.dom.*
import kotlin.js.Date
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration


const val MILLISECONDS_IN_A_DAY:Long = 86400000

val MonthNames = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")


object Events {
    const val VISIBILITY_CHANGE = "visibilitychange"
}

object UnicodeChars {
    const val RED_CIRCLE = "&#x1F534;"
    const val WHITE_CIRCLE = "&#x26AA;"
    const val YELLOW_CIRCLE = "&#x1F7E1;"
    const val GREEN_CIRCLE = "&#x1F7E2;"
    const val BLACK_SQUARE = "&#9642;"
    const val FAT_DASH = "&#x2796;"
    const val ROSE = "&#x1F339;"
    const val RAINBOW = "&#x1F308;"

}

object Styles {
    const val TABLE_ROW_STYLE = "float: left;"
    const val TABLE_CELL_STYLE = "float: left; width:30px; height:24px; text-align:center; padding: 6px 0 0 0"
    const val TABLE_CELL_BORDER_STYLE = "float: left; width:28px; height:22px; text-align:center; padding: 6px 0 0 0; border: 1px solid black"
    const val TABLE_HEAD_STYLE = "float:left"
    const val TABLE_BODY_STYLE = "float:left"
    const val NEW_ROW = "clear:both"
    const val HALF_CELL = "float: left; width:15px; height:30px; text-align:center"
    const val EMPTY_CELL_STYLE = "float: left; width:28px; height:15px; border-left:1px solid black; border-right:1px solid black"
    const val EMPTY_HALF_CELL_STYLE = "float: left; width:15px; height:15px"
    const val NA_PAKI = "; background-color: red"
    const val AYYAAM_E_SHAKK = "; background-color: yellow"

}

val Document.isHidden get() = this["hidden"] as Boolean


private fun insertRelative(
    ownerDocument: Document,
    block: TagConsumer<HTMLElement>.() -> Unit,
    insert: (Element) -> Unit
): List<HTMLElement> = ArrayList<HTMLElement>().also { result ->
    ownerDocument.createTree().onFinalize { child, partial ->
        if (!partial) {
            result.add(child)
            insert(child)
        }
    }.block()
}

private fun Element.insertRelative(block: TagConsumer<HTMLElement>.() -> Unit, insert: (Element) -> Unit = {}) =
    insertRelative(ownerDocument!!, block, insert)
private fun Node.insertRelative(block: TagConsumer<HTMLElement>.() -> Unit, insert: (Node) -> Unit) =
    insertRelative(ownerDocument!!, block, insert)
private fun ChildNode.insertRelative(block: TagConsumer<HTMLElement>.() -> Unit, insert: (ChildNode) -> Unit) =
    insertRelative((this as Node).ownerDocument!!, block, insert)

fun Node.appendChild(block: TagConsumer<HTMLElement>.() -> Unit) = insertRelative(block) { node -> appendChild(node) }
@Suppress("MoveLambdaOutsideParentheses", "RedundantLambdaArrow")
fun Element.replaceChildren(block: TagConsumer<HTMLElement>.() -> Unit) =
    replaceChildren(*insertRelative(block).toTypedArray())
fun ChildNode.before(block: TagConsumer<HTMLElement>.() -> Unit) = insertRelative(block) { node -> before(node) }
fun ChildNode.after(block: TagConsumer<HTMLElement>.() -> Unit) = insertRelative(block) { node -> after(node) }

fun ParentNode.getChildById(id: String) = querySelector("#$id")


fun Element.replaceChildren(vararg nodes: Node) {
    asDynamic().replaceChildren.apply(this, nodes)
}


inline fun <reified T : Element> Element.getAncestor(predicate: (Element) -> Boolean = { true }): T? {
    var parent: Element? = parentElement
    while (true) {
        if (parent == null) return null
        if (parent is T && predicate(parent)) return parent
        parent = parent.parentElement
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


/* Looks like the compiler argument for opting in to experimental features
 * ('-Xopt-in=kotlin.RequiresOptIn') is not actually enforced, so suppressing the warning about it's
 * requirement here for now..
 */
@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@OptIn(ExperimentalTime::class)
fun Date.offsetLocalTimeToUtc() =
    Date(getTime() - getTimezoneOffset().toDuration(DurationUnit.MINUTES).inWholeMilliseconds)

fun parseToLocalDate(dateString: String, isDateOnly: Boolean): Date {
    val date = Date(dateString)
    return if (isDateOnly) {
        date
    } else {
        date.offsetLocalTimeToUtc()
    }
}

fun Date.toDateInputString(isDateOnly: Boolean): String {
    val letterToTrimFrom = if (isDateOnly) 'T' else 'Z'
    val string = toISOString().takeWhile { it != letterToTrimFrom }
    return if (isDateOnly) {
        string
    } else {
        // Drop any precision below minutes (seconds, milliseconds, etc.)
        string.take(16)
    }
}

fun convertInputValue(value: String, isDateOnly: Boolean): String {
    if (value.isEmpty()) return ""
    return parseToLocalDate(value, !isDateOnly) // Inverting the isDateOnly since we need to pass the existing state
        .toDateInputString(isDateOnly)
}

fun currentTimeString(isDateOnly: Boolean) = Date().offsetLocalTimeToUtc().toDateInputString(isDateOnly)


fun addTimeToDate(date: Date,timeInMilliseconds:Long):Date{
    return Date(date.getTime() + timeInMilliseconds)
}

fun parseDays(input: String): Double? {
    if (input.isEmpty()) return null
    val sections = input.split(':')
    var days = sections[0].toInt().toDouble()
    val hours = sections.getOrNull(1)?.toInt() ?: return days
    require(hours in 0 until 24) { "Invalid hours value" }
    days += hours / 24.toDouble()
    val minutes = sections.getOrNull(2)?.toInt() ?: return days
    require(minutes in 0 until 60) { "Invalid minutes value" }
    days += minutes / (24 * 60).toDouble()
    val seconds = sections.getOrNull(3)?.toInt() ?: return days
    require(seconds in 0 until 60) { "Invalid seconds value" }
    days += seconds / (24 * 60 * 60).toDouble()
    return days
}

fun daysHoursMinutesDigitalUrdu(numberOfMilliseconds:Long, isDateOnly: Boolean):String{
    println("Number Of MilliseCONDS IS $numberOfMilliseconds")
    val days:Double = kotlin.math.floor((numberOfMilliseconds/MILLISECONDS_IN_A_DAY).toDouble())
    println(days)
    var milisecsleft = numberOfMilliseconds - days*MILLISECONDS_IN_A_DAY
    val hours:Double = kotlin.math.floor((milisecsleft/(3600000)).toDouble())
    println(hours)
    milisecsleft-=hours*3600000
    val minutes = kotlin.math.floor(milisecsleft/60000)




    println("Number Of days IS $days")
    var totalMinutes:Double = days*24*60

//    var minutes:Double=(totalMinutes%60).toDouble();
    var remainingHours:Double = (totalMinutes - minutes)/60
//    var hours:Double = remainingHours % 24;
    //var days:Double = (remainingHours - hours)/24;
//    minutes=round(minutes);
//    hours=round(hours)
//    days=round(days)
//    if(minutes == 60.0){
//        minutes = 0.0
//        hours+=1
//    }
//    if(hours==24.0){
//        hours = 0.0
//        days += 1.0
//    }
    var strHours = "${hours} گھنٹے "
    var strMinutes = "${minutes} منٹ "
    var strDays = "${days} دن "

    if(hours==1.0){
        strHours = "${hours} گھنٹا "
    }

    if(hours==0.0){
        strHours = ""
    }
    if(days==0.0){
        strDays = ""
    }
    if(minutes == 0.0){
        strMinutes = ""
    }
//    if(hours<10){
//        strHours = "0${hours}";
//    }
//    if(minutes<10){
//        strMinutes = "0${minutes}";
//    }
    var returnStatement = "${strDays}${strHours}${strMinutes}"
    println(returnStatement)
    if(isDateOnly){
        returnStatement = strDays
    }
    return(returnStatement);
}

fun daysHoursMinutesDigital(numberOfMilliseconds:Long, isDateOnly: Boolean):String{
    val numberOfDays = numberOfMilliseconds/MILLISECONDS_IN_A_DAY
    var totalMinutes = numberOfDays*24*60

    var minutes=(totalMinutes%60).toDouble();
    var remainingHours = (totalMinutes - minutes)/60
    var hours = remainingHours % 24;
    var days = (remainingHours - hours)/24;
    minutes=round(minutes);
    hours=round(hours)
    days=round(days)
    if(minutes == 60.0){
        minutes = 0.0
        hours+=1.0
    }
    if(hours==24.0){
        hours = 0.0
        days += 1.0
    }

    var strHours = hours.toString()
    var strMinutes = minutes.toString()
    var strDays = days.toString()
    if(hours<10){
        strHours = "0${hours}";
    }
    if(minutes<10){
        strMinutes = "0${minutes}";
    }
    var returnStatement = "${strDays}d:${strHours}h:${strMinutes}m"
    if(isDateOnly==true){
        returnStatement = "${strDays} day(s)"
    }
    return(returnStatement);
}
 fun parseDate(date: Date, isDateOnly: Boolean):String{
  //   Sat, 05 Jun 2021 06:21:59 GMT
     var dateStr = (date.toUTCString()).dropLast(13).drop(5)
     var hours = (date.toUTCString()).dropLast(10).drop(17).toInt()
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
fun difference(date1:Date,date2:Date):Long{

    var diff = (date2.getTime()-date1.getTime()).toLong()
    return diff

}

 fun urduDateFormat(date: Date, isDateOnly: Boolean):String{
     var day = date.getUTCDate().toString()
     var month = date.getUTCMonth()
     var urduMonth = ""
     if (month == 0){
         urduMonth = "جنوری"
     }else if (month == 1){
         urduMonth = "فروری"
     }else if (month == 2){
         urduMonth = "مارچ"
     }else if (month == 3){
         urduMonth = "اپریل"
     }else if (month == 4){
         urduMonth = "مئی"
     }else if (month == 5){
         urduMonth = "جون"
     }else if (month == 6){
         urduMonth = "جولائ"
     }else if (month == 7){
         urduMonth = "اگست"
     }else if (month == 8){
         urduMonth = "ستمبر"
     }else if (month == 9){
         urduMonth = "اکتوبر"
     }else if (month == 10){
         urduMonth = "نومبر"
     }else if (month == 11){
         urduMonth = "دسمبر"
     }
     var urduDay = ""
     if(day=="1"){
         urduDay = "یکم"
     }else{
         urduDay  = "$day"
     }
     if(isDateOnly==true){
         return ("${urduDay} ${urduMonth}")
     }else{//has time too
         var hours = date.getUTCHours()
         var minutes = date.getUTCMinutes()
         var strMinutes = ""
         if(minutes <10){
             strMinutes = "0${minutes}"
         }else{
             strMinutes = minutes.toString()
         }
         var ampm=""
         if (hours == 0){
             ampm = "رات"
             hours = 12
         }else if(hours > 0 && hours<4){//1-3
             ampm = "رات"
         }else if(hours>3 && hours<12){//4-11
             ampm = "صبح"
         }else if(hours>11&&hours<15){//12-2
             ampm = "دوپہر"
             hours -= 12
             if(hours == 0){hours+=12}
         }else if(hours>14&&hours<19){//3-6
             ampm = "شام"
             hours -= 12
         }else{//7-11
             ampm = "رات"
             hours-=12
         }
         return ("${urduDay} ${urduMonth} ${ampm} ${hours}:${strMinutes} بجے")


     }

 }
