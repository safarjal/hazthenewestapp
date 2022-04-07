import kotlinx.html.*
import kotlinx.html.consumers.onFinalize
import kotlinx.html.dom.createTree
import org.w3c.dom.*
import kotlin.js.Date
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration


const val MILLISECONDS_IN_A_DAY:Long = 86400000
const val TAB:String = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"

const val FORM_WIDTH_DATE_ONLY = 410
const val FORM_WIDTH_DATE_TIME = 605
const val FORM_PADDING = 8
const val FORM_BORDER = 1

val MonthNames = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")


object Events {
    const val VISIBILITY_CHANGE = "visibilitychange"
}

object UnicodeChars {
    const val RED_CIRCLE = "&#9830;&#65039;"        // RED_DIAMOND
    const val WHITE_CIRCLE = "&#128160;"            // WHITE_DIAMOND
    const val YELLOW_CIRCLE = "&#x1F538;"           // ORANGE_DIAMOND
    const val GREEN_CIRCLE = "&#10052;&#65039;"     // SNOWFLAKE
    const val BLACK_SQUARE = "&#9642;"
    const val FAT_DASH = "&#x2796;"
    const val ROSE = "&#128221;"                    // MEMO
    const val RAINBOW = "&#9997;&#65039;"           // HAND_WRITING
    const val BLUE_SWIRL = "&#127744;"
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

var Element.visibility: Boolean
    get() = !classList.contains("invisible")
    set(visible) { classList.toggle("invisible", !visible) }

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

fun parseDays(input: String): Long? {
    val millisecondsInAnHour = 3600000
    val millisecondsInAMinute = 60000
    if (input.isEmpty()) return null
    val sections = input.split(':')
    var days = sections[0].toInt()
    var millisecs:Long = days.toLong()*MILLISECONDS_IN_A_DAY
    val hours = sections.getOrNull(1)?.toInt() ?: return millisecs
    require(hours in 0 until 24) { "Invalid hours value" }
    millisecs+=hours*millisecondsInAnHour
    val minutes = sections.getOrNull(2)?.toInt() ?: return millisecs
    require(minutes in 0 until 60) { "Invalid minutes value" }
    millisecs+=minutes*millisecondsInAMinute
    if(hours!=null&&minutes!=null){
        return millisecs
    }
    return millisecs
}

fun daysHoursMinutesDigitalUrdu(numberOfMilliseconds:Long, isDateOnly: Boolean):String{
    val days:Double = kotlin.math.floor((numberOfMilliseconds/MILLISECONDS_IN_A_DAY).toDouble())
    var milisecsleft = numberOfMilliseconds - days*MILLISECONDS_IN_A_DAY
    val hours:Double = kotlin.math.floor((milisecsleft/(3600000)))
    milisecsleft-=hours*3600000
    val minutes = kotlin.math.floor(milisecsleft/60000)




//    var totalMinutes:Double = days*24*60

//    var minutes:Double=(totalMinutes%60).toDouble();
//    var remainingHours:Double = (totalMinutes - minutes)/60
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
    var strHours = "$hours گھنٹے"
    var strMinutes = "$minutes منٹ"
    var strDays = "$days دن"

    if(hours==1.0){
        strHours = "$hours گھنٹہ"
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
    var returnStatement = "${strDays} ${strHours} ${strMinutes}"
    if (strDays=="" && strHours == "" && strMinutes == ""){
        returnStatement = "0 منٹ"
    }
//    println(returnStatement)
    if(isDateOnly){
        returnStatement = strDays
    }
    return(returnStatement.trim().trimEnd())
}

fun daysHoursMinutesDigitalEnglish(numberOfMilliseconds:Long, isDateOnly: Boolean):String{
    val days:Double = kotlin.math.floor((numberOfMilliseconds/MILLISECONDS_IN_A_DAY).toDouble())
//    println(days)
    var milisecsleft = numberOfMilliseconds - days*MILLISECONDS_IN_A_DAY
    val hours:Double = kotlin.math.floor((milisecsleft/(3600000)))
//    println(hours)
    milisecsleft-=hours*3600000
    val minutes = kotlin.math.floor(milisecsleft/60000)



    var strHours = hours.toString()
    var strMinutes = minutes.toString()
    var strDays = days.toString()
    if (days==1.0){
        strDays += " day"
    }else if(days == 0.0){
        strDays = ""
    }else{
        strDays += " days"
    }
    if (hours == 1.0){
        strHours += " hour"
    }else if (hours == 0.0){
        strHours = ""
    }else{
        strHours += " hours"
    }
    if (minutes == 1.0){
        strMinutes += " minute"
    }else if (minutes == 0.0){
        strMinutes = ""
    }else{
        strMinutes += " minutes"
    }

    var returnStatement = ""
    if(strDays != ""&& strHours != "" && strMinutes!= "" ){
        returnStatement = "${strDays}, ${strHours} and ${strMinutes}"
    }else if(strDays!="" && strHours !=""){
        returnStatement = "$strDays and $strHours"
    }else if(strDays!="" && strMinutes != ""){
        returnStatement = "$strDays and $strMinutes"
    }else if(strHours!="" && strMinutes !=""){
        returnStatement = "$strHours and $strMinutes"
    }else if(strDays!=""){
        returnStatement = strDays
    }else if(strHours!=""){
        returnStatement=strHours
    }else if(strMinutes!=""){
        returnStatement=strMinutes
    }else if(strDays=="" && strHours == "" && strMinutes==""){
        returnStatement = "0 minutes"
    }else{
        returnStatement = "error!!!"
    }




    if(isDateOnly){
        if(days==1.0){
            returnStatement = "1 day"
        }else{
            returnStatement = strDays
        }
    }
    return(returnStatement.trimEnd().trim())
}
 fun englishDateFormat(date: Date, isDateOnly: Boolean):String{
  //   Sat, 05 Jun 2021 06:21:59 GMT
     var dateStr = (date.toUTCString()).dropLast(18).drop(5)
     if(dateStr.startsWith("0")){
         dateStr=dateStr.drop(1)
     }
     var hours = (date.toUTCString()).dropLast(10).drop(17).toInt()
     val minutesStr = (date.toUTCString()).dropLast(7).drop(20)
     var ampm = "am"
     if (hours >=12){
         hours-=12
         ampm = "pm"
     }
     if(hours == 0){
         hours = 12
     }
     val hoursStr:String = hours.toString()


     return if(isDateOnly){
         dateStr
         //05 Jun 2021
     }else{
         //13 Dec at 7:30pm
         "$dateStr at $hoursStr:$minutesStr $ampm"
     }
 }
fun difference(date1:Date,date2:Date):Long{
    return (date2.getTime()-date1.getTime()).toLong()

}

 fun urduDateFormat(date: Date, isDateOnly: Boolean):String{
     val day = date.getUTCDate().toString()
     val month = date.getUTCMonth()
     var urduMonth = ""
     when (month) {
         0 -> {
             urduMonth = "جنوری"
         }
         1 -> {
             urduMonth = "فروری"
         }
         2 -> {
             urduMonth = "مارچ"
         }
         3 -> {
             urduMonth = "اپریل"
         }
         4 -> {
             urduMonth = "مئی"
         }
         5 -> {
             urduMonth = "جون"
         }
         6 -> {
             urduMonth = "جولائ"
         }
         7 -> {
             urduMonth = "اگست"
         }
         8 -> {
             urduMonth = "ستمبر"
         }
         9 -> {
             urduMonth = "اکتوبر"
         }
         10 -> {
             urduMonth = "نومبر"
         }
         11 -> {
             urduMonth = "دسمبر"
         }
     }
     val urduDay:String = if(day=="1"){
         "یکم"
     }else{
         day
     }
     if(isDateOnly){
         return ("$urduDay $urduMonth")
     }else{//has time too
         var hours = date.getUTCHours()
         val minutes = date.getUTCMinutes()
         val strMinutes:String = if(minutes <10){
             "0${minutes}"
         }else{
             minutes.toString()
         }
         val ampm:String
         if (hours == 0){
             ampm = "رات"
             hours = 12
         }else if(hours in 1..3){//1-3
             ampm = "رات"
         }else if(hours in 4..11){//4-11
             ampm = "صبح"
         }else if(hours in 12..14){//12-2
             ampm = "دوپہر"
             hours -= 12
             if(hours == 0){hours+=12}
         }else if(hours in 15..18){//3-6
             ampm = "شام"
             hours -= 12
         }else{//7-11
             ampm = "رات"
             hours-=12
         }
         return ("$urduDay $urduMonth $ampm $hours:$strMinutes بجے")


     }

 }
