import kotlinx.html.*
import kotlinx.html.consumers.onFinalize
import kotlinx.html.dom.createTree
import org.w3c.dom.*
import org.w3c.dom.events.Event
import kotlin.js.Date
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

// HTML DOM MANIP
val Document.isHidden get() = this["hidden"] as Boolean

fun replaceBoldTagWithBoldAndStar(string: String): String {
    return string.replace("<b>", "<b><span class='${CssC.INVIS}'>*</span>")
        .replace("</b>", "<span class='${CssC.INVIS}'>*</span></b>")
}

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

fun Element.replaceChildren(vararg nodes: Node) { asDynamic().replaceChildren.apply(this, nodes) }

inline fun <reified T : Element> Element.getAncestor(predicate: (Element) -> Boolean = { true }): T? {
    var parent: Element? = parentElement
    while (true) {
        if (parent == null) return null
        if (parent is T && predicate(parent)) return parent
        parent = parent.parentElement
    }
}

fun findInputContainer(event: Event) =
    (event.currentTarget as Element).getAncestor<HTMLElement> { it.id.startsWith(Ids.InputContainers.INPUT_CONTAINER)}!!
fun findInputContainer(element: Element) =
    element.getAncestor<HTMLElement> { it.id.startsWith(Ids.InputContainers.INPUT_CONTAINER)}!!
fun findRow(event: Event) = (event.currentTarget as Element).getAncestor<HTMLTableRowElement>()!!

var Element.visibility: Boolean
    get() = !classList.contains(CssC.INVIS)
    set(visible) { classList.toggle(CssC.INVIS, !visible) }

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
    if (isDateOnly)  dateInput(classes = classes, block = block)
    else dateTimeLocalInputWithFallbackGuidelines(classes = classes, block = block)
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
    return if (isDateOnly) date else date.offsetLocalTimeToUtc()
}

fun Date.toDateInputString(isDateOnly: Boolean): String {
    val letterToTrimFrom = if (isDateOnly) 'T' else 'Z'
    val string = toISOString().takeWhile { it != letterToTrimFrom }
    return if (isDateOnly) string
    else string.take(16) // Drop any precision below minutes (seconds, milliseconds, etc.)
}

fun convertInputValue(value: String, isDateOnly: Boolean): String {
    if (value.isEmpty()) return ""
    return parseToLocalDate(value, !isDateOnly) // Inverting the isDateOnly since we need to pass the existing state
        .toDateInputString(isDateOnly)
}

fun currentTimeString(isDateOnly: Boolean) = Date().offsetLocalTimeToUtc().toDateInputString(isDateOnly)

fun addTimeToDate(date: Date,timeInMilliseconds:Long):Date { return Date(date.getTime() + timeInMilliseconds) }

fun parseRange(input: String):Array<Int?>{
    val sections = input.split('-')
    var array:Array<Int?> = arrayOf()
    for (i in sections.indices){
        array[i]= (parseDays(sections[i])?.div(MILLISECONDS_IN_A_DAY))?.toInt()
    }
    return array
}

fun parseDays(input: String): Long? {
    if(input.contains('-')) return null
    if (input.isEmpty()) return null

    val sections = input.split(':')

    val days = sections[0].toLong()
    var millisecs:Long = days * MILLISECONDS_IN_A_DAY

    val hours = sections.getOrNull(1)?.toInt() ?: return millisecs
    require(hours in 0 until 24) { "Invalid hours value" }
    millisecs += hours * MILLISECONDS_IN_AN_HOUR

    val minutes = sections.getOrNull(2)?.toInt() ?: return millisecs
    require(minutes in 0 until 60) { "Invalid minutes value" }
    millisecs += minutes * MILLISECONDS_IN_A_MINUTE

    return millisecs
}

fun milliToDayHrMin(numberOfMilliseconds:Long): Array<Double> {
    val days:Double = kotlin.math.floor((numberOfMilliseconds / MILLISECONDS_IN_A_DAY).toDouble())
    var milisecsleft = numberOfMilliseconds - days * MILLISECONDS_IN_A_DAY
    val hours:Double = kotlin.math.floor((milisecsleft / MILLISECONDS_IN_AN_HOUR))
    milisecsleft -= hours * MILLISECONDS_IN_AN_HOUR
    val minutes = kotlin.math.floor(milisecsleft / MILLISECONDS_IN_A_MINUTE)
    return arrayOf(days, hours, minutes)
}

fun daysHoursMinutesDigital(numberOfMilliseconds:Long, typeOfInput: TypesOfInputs = TypesOfInputs.DURATION, languageNames: String = Vls.Langs.ENGLISH):String {
    if(languageNames==Vls.Langs.ENGLISH){
        var isDateOnly = false
        if(typeOfInput==TypesOfInputs.DATE_ONLY){isDateOnly=true}


        val (days, hours, minutes) = milliToDayHrMin(numberOfMilliseconds)

        var strHours = hours.toString()
        var strMinutes = minutes.toString()
        var strDays = days.toString()

        when (days) {
            1.0 -> strDays += " day"
            0.0 -> strDays = ""
            else -> strDays += " days"
        }
        when (hours) {
            1.0 -> strHours += " hour"
            0.0 -> strHours = ""
            else -> strHours += " hours"
        }
        when (minutes) {
            1.0 -> strMinutes += " minute"
            0.0 -> strMinutes = ""
            else -> strMinutes += " minutes"
        }

        var returnStatement =
            if(strDays.isNotEmpty() && strHours.isNotEmpty() && strMinutes.isNotEmpty() ) "$strDays, $strHours and $strMinutes"
            else if(strDays.isNotEmpty() && strHours.isNotEmpty()) "$strDays and $strHours"
            else if(strDays.isNotEmpty() && strMinutes.isNotEmpty()) "$strDays and $strMinutes"
            else if(strHours.isNotEmpty() && strMinutes.isNotEmpty()) "$strHours and $strMinutes"
            else if(strDays.isEmpty() && strHours.isEmpty() && strMinutes.isEmpty() && !isDateOnly) {"0 minutes"}
            else strDays + strHours + strMinutes
        if(isDateOnly) returnStatement = if (days == 1.0) "1 day" else if(days == 0.0) "0 days" else strDays
        return returnStatement.trimEnd().trim()
    }else if(languageNames==Vls.Langs.URDU){
        var isDateOnly = false
        if(typeOfInput==TypesOfInputs.DATE_ONLY){isDateOnly=true}

        val (days, hours, minutes) = milliToDayHrMin(numberOfMilliseconds)

        val strHours = when (hours) {
            1.0 -> "$hours گھنٹہ"
            0.0 -> ""
            else -> "$hours گھنٹے"
        }
        val strMinutes = if (minutes == 0.0) "" else "$minutes منٹ"
        val strDays = if (days == 0.0) "" else "$days دن"
        var returnStatement = "$strDays $strHours $strMinutes"
        if (strDays.isEmpty() && strHours.isEmpty() && strMinutes.isEmpty())  returnStatement = "0 منٹ"
        if (isDateOnly) returnStatement = if (days == 0.0) "0 دن" else strDays
        return returnStatement.trim().trimEnd()
    }
    return ""
}

 fun languagedDateFormat(date: Date, typeOfInput: TypesOfInputs, languageNames: String):String{
     var isDateOnly = false
     if(typeOfInput==TypesOfInputs.DATE_ONLY){isDateOnly=true}
     if(languageNames==Vls.Langs.ENGLISH){
         //   Sat, 05 Jun 2021 06:21:59 GMT
         var dateStr = (date.toUTCString()).dropLast(18).drop(5)
         if(dateStr.startsWith("0")) dateStr = dateStr.drop(1)
         var hours = (date.toUTCString()).dropLast(10).drop(17).toInt()
         val minutesStr = (date.toUTCString()).dropLast(7).drop(20)
         var ampm = "am"
         if (hours >=12) {
             hours -= 12
             ampm = "pm"
         }
         if (hours == 0) hours = 12

         val hoursStr:String = hours.toString()

         return if (isDateOnly) dateStr //05 Jun 2021
         else "$dateStr at $hoursStr:$minutesStr $ampm" //13 Dec at 7:30pm
     }
     else if(languageNames==Vls.Langs.URDU){
         val day = date.getUTCDate().toString()
         val month = date.getUTCMonth()
         val urduMonth = urduMonthNames[month]
         val urduDay:String = if (day == "1") "یکم" else day

         if (isDateOnly) return ("$urduDay $urduMonth")
         else { //has time too
             var hours = date.getUTCHours()
             val minutes = date.getUTCMinutes()
             val strMinutes:String = if(minutes < 10) "0${minutes}" else minutes.toString()

             val ampm = when (hours) {
                 in 4..11 -> "صبح" //4am-11am
                 in 12..14 -> "دوپہر" //12pm-2pm
                 in 15..18 -> "شام" //3pm-6pm
                 else -> "رات" //7pm-3am
             }

             if (hours >=12) hours -= 12
             if (hours == 0) hours = 12

             return ("$urduDay $urduMonth $ampm $hours:$strMinutes بجے").trim().trimEnd()
         }

     }
     return ""
 }

fun difference(date1:Date,date2:Date):Long { return (date2.getTime()-date1.getTime()).toLong() }

// VALS TO USE
object Ids {
    const val LANGUAGE = "language"

    object InputContainers {
        const val INPUT_CONTAINERS_CONTAINER = "input_containers_container"
        const val INPUT_CONTAINER = "input_container"
        const val INPUTS_CONTAINER_CLONE_BUTTON = "inputs_container_clone_button"
        const val INPUTS_CONTAINER_REMOVE_BUTTON = "inputs_container_remove_button"
        const val REACT_DIV = "react_div"
    }
    object InputTables {
        const val HAIZ_INPUT_TABLE = "haiz_input_table"
        const val HAIZ_DURATION_INPUT_TABLE = "haiz_duration_input_table"

    }
    object AddTimeToDate {
        //        const val IS_DATE_ONLY = "is_date_only_add_time_to_date"
        const val DATE_TO_ADD_TO = "date_to_add_to"
        const val TIME_TO_ADD = "time_to_add"
        const val OUTOUT_FIELD = "add_time_date_output"
    }
    object CalcDuration {
        //        const val IS_DATE_ONLY = "get_duration_is_date_only"
        const val STRT_DATE = "start_date"
        const val END_DATE = "end_date"
        const val OUTPUT_FIELD = "calc_duration_output"
    }
    object Row {
        const val INPUT_START_TIME = "input_start_time"
        const val INPUT_END_TIME = "input_end_time"
        const val BUTTONS_CONTAINER = "button_add_before_container"
        const val BUTTON_REMOVE = "button_remove"
        const val BUTTON_ADD_BEFORE = "button_add_before"
        const val INPUT_DURATION = "input_duration"
        const val INPUT_TYPE_OF_DURATION = "input_duration_type"
    }
    object Ikhtilafat {
        const val IKHTILAF1 = "ikhtilaf1"
        const val IKHTILAF2 = "ikhtilaf2"
        const val IKHTILAF3 = "ikhtilaf3"
        const val IKHTILAF4 = "ikhtilaf4"
    }
    object Results {
        const val CONTENT_CONTAINER = "content_container"
        const val CONTENT_WRAPPER = "content_wrapper"
        const val CONTENT_URDU = "content_urdu"
        const val CONTENT_ENGLISH = "content_english"
        const val CONTENT_DATES = "content_dates"
        const val CALCULATE_ALL_DIV = "calculate_all_div"
        const val CALCULATE_BUTTON = "calculate_button"
        const val COMPARISON_CONTAINER = "comparison_container"
        const val DATES_DIFFERENCE_TABLE = "dates_difference_table"
    }
    object Inputs {
        const val MASLA_TYPE_SELECT = "masla_type_select"
        const val ZAALLA_CHECKBOX = "zaalla_checkbox"
        const val INPUT_TYPE_SELECT = "input_type_select"
        const val PREG_END_TIME_INPUT = "preg_end_time_input"
        const val PREG_START_TIME_INPUT = "preg_start_time_input"
        const val MUSTABEEN_CHECKBOX = "mustabeen_checkbox"
        const val AADAT_HAIZ_INPUT = "aadat_haiz_input"
        const val AADAT_TUHR_INPUT = "aadat_tuhr_input"
        const val MAWJOODA_TUHR_INPUT = "mawjooda_tuhr_input"
        const val MAWJOODA_FASID_CHECKBOX = "mawjooda_fasid_checkbox"
        const val AADAT_NIFAS_INPUT = "aadat_nifas_input"
        const val ZAALLA_CYCLE_LENGTH = "zaalla_cycle_length"
        const val INPUT_QUESTION = "input_question"
        const val INPUT_DESCRIPTION = "input_description"
    }
}

object CssC {
    const val INVIS = "invisible"                   // Invis. Put on any element that shouldn't show; also doable by elem.visibility
    const val LANG_INVIS = "lang-invisible"         // Invis. Put on any element that shouldn't show because of lang
    const val HIDDEN = "hidden"                     // Hidden. Put on any element that shouldn't show; but still exist and take up space

    const val ENGLISH = "english"                   // Switch. Put on any element that should only show when lang is english
    const val URDU = "urdu"                         // Switch. Put on any element that should only show when lang is urdu
    const val DEV = "dev"                           // Switch. Put on any element that should only show when devmode
    const val RTL = "rtl"                           // Switch. Put on any element that should switch rtl but NOT invis

    const val NIFAS = "nifas"                       // Switch. Put on any input that only shows when Nifas
    const val ZAALLA = "zaalla"                     // Switch. Put on any input that only shows when Zaalla
    const val MUTADA = "mutada"                     // Switch. Put on any input that only shows when NOT Mubtadia
    const val DATETIME_AADAT = "datetime_aadat"     // Switch. Put on any input that only shows when NOT Duration
    const val MUSTABEEN = "mustabeen"               // Switch. Between Isqat/Wiladat
    const val NOT_MUSTABEEN = "not-mustabeen"       // Switch. Between Isqat/Wiladat
    const val TITLE_CELL = "title_cell"

    const val ROW = "row"                           // CSS Style. Make nice alternating colorful rows of inputs
    const val IKHTILAF = "ikhtilaf"                 // CSS Style. Makes the gearbox icon on the detail
    const val SLIDER = "slider"                     // CSS Style.
    const val ROUND = "round"                       // CSS Style.

    const val CALC_BTN = "calc-btn"                 // CSS Style.
    const val LEFT = "left"                         // CSS Style.
    const val RIGHT = "right"                       // CSS Style.
    const val PLUS = "plus"                         // CSS Style.
    const val MINUS = "minus"                       // CSS Style.
    const val SWITCH = "switch"                     // CSS Style.
    const val LABEL_INPUT = "label-input"           // CSS Style.
    const val CENTER = "center"                     // CSS Style.

    const val SHRUNK = "shrunk"                     // CSS Style. Shrinks Answer to desired height.
    const val GRID = "grid"
    const val COLUMN = "column"
    const val TABLE_CELL = "table_cell"             // CSS Style.
    const val DESCRIPTION = "description"
    const val MONTHS_ROW = "months_row"             // CSS Style.
    const val DATES_ROW = "dates_row"               // CSS Style.
    const val BORDERED = "bordered"                 // CSS Style.
    const val HALF_TABLE_CELL = "half_table_cell"  // CSS Style.
    const val EMPTY_TABLE_CELL = "empty_table_cell" // CSS Style.
    const val NA_PAAKI = "na_paaki"                 // CSS Style.
    const val AYYAM_E_SHAKK = "ayyam_e_shakk"       // CSS Style.
}

object Vls {                                        // Values
    object Langs {
        const val ENGLISH = "english"
        const val URDU = "urdu"
    }
    object Maslas {
        const val MUTADA = "mutada"
        const val NIFAS = "nifas"
        const val MUBTADIA = "mubtadia"
    }
    object Types {
        const val DATE_ONLY = "dateOnly"
        const val DATE_TIME = "dateTime"
        const val DURATION = "duration"
    }
    object Opts {                                   // Options for duration dropdowns
        const val DAM = "dam"
        const val TUHR = "tuhr"
        const val HAML = "haml"
        const val WILADAT = "wiladat"
    }
}

const val MILLISECONDS_IN_A_DAY:Long = 86400000
const val MILLISECONDS_IN_AN_HOUR = 3600000
const val MILLISECONDS_IN_A_MINUTE = 60000

val NO_OUTPUT = OutputTexts("","","", mutableListOf(), EndingOutputValues(null, null, mutableListOf()), mutableListOf())
val ARBITRARY_DATE = Date(0,0,0)
val englishMonthNames = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
val urduMonthNames = arrayOf("جنوری", "فروری", "مارچ", "اپریل", "مئی", "جون", "جولائ", "اگست", "ستمبر", "اکتوبر", "نومبر", "دسمبر")

object Events {
    const val VISIBILITY_CHANGE = "visibilitychange"
}

object UnicodeChars {
    const val RED_DIAMOND = "&#9830;&#65039;"        // RED_DIAMOND
    const val WHITE_DIAMOND = "&#128160;"            // WHITE_DIAMOND
    const val ORANGE_DIAMOND = "&#x1F538;"           // ORANGE_DIAMOND
    const val SNOWFLAKE = "&#10052;&#65039;"     // SNOWFLAKE
    const val BLACK_SQUARE = "&#9642;"
    const val FAT_DASH = "&#x2796;"
    const val MEMO = "&#128221;"                    // MEMO
    const val HAND_WRITING = "&#9997;&#65039;"           // HAND_WRITING
    const val BLUE_SWIRL = "\uD83C\uDF00"
    const val ABACUS = "&#129518;"
    const val TAB:String = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
}
