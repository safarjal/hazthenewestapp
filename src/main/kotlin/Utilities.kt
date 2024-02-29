import kotlinx.datetime.internal.JSJoda.*
import kotlinx.datetime.internal.JSJoda.Duration
import kotlinx.html.*
import kotlinx.html.consumers.onFinalize
import kotlinx.html.dom.createTree
import kotlinx.html.org.w3c.dom.events.Event
import org.w3c.dom.*

fun <T> buildJsObject(block: T.() -> Unit): T = (js("{}") as T).apply(block)

// HTML DOM MANIP
val Document.isHidden get() = this["hidden"] as Boolean

fun replaceBoldTagWithBoldAndStar(string: String): String {
    return string.replace("<b>", "<b><span class='${CssC.INVIS}'>*</span>")
        .replace("</b>", "<span class='${CssC.INVIS}'>*</span></b>")
        .split("\n\n").joinToString("") { paragraph ->
            "<p>$paragraph</p>"
        }
}

fun String.replaceHtmlTagsWithStringSafe(): String {
    return replace("<p>", "")
        .replace("</p>", "\n\n")
        .replace("\n\n\n\n", "\n\n")
        .replace(Regex("<.*?>"), "")
}

fun replaceStarWithStarAndBoldTag(string: String): String {
    return string.replace(Regex("\\*(.*?)\\*")) {
        "<b><span class='${CssC.INVIS}'>*</span>${it.groupValues[1]}<span class='${CssC.INVIS}'>*</span></b>"
    }.split("\n\n").joinToString("") { paragraph ->
        "<p>$paragraph</p>"
    }
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

fun findInputContainer(event: Event) =
    (event.currentTarget as Element).getAncestor<HTMLElement> { it.id.startsWith(Ids.InputContainers.INPUT_CONTAINER) }!!

fun findRow(event: Event) = (event.currentTarget as Element).getAncestor<HTMLTableRowElement>()!!

var Element.visibility: Boolean
    get() = !classList.contains(CssC.INVIS)
    set(visible) {
        classList.toggle(CssC.INVIS, !visible)
    }

val HTMLTableRowElement.rowIndexWithinTableBody
    get() = (parentElement as HTMLTableSectionElement).children.asList().indexOf(this)


@HtmlTagMarker
fun FlowOrInteractiveOrPhrasingContent.dateTimeLocalInputWithFallbackGuidelines(
    classes: String? = null, block: INPUT.() -> Unit = {}
) {
    dateTimeLocalInput(classes = classes) {
        placeholder = "YYYY-MM-DDThh:mm"
        pattern = "[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}"
        block()
    }
}

@HtmlTagMarker
fun FlowOrInteractiveOrPhrasingContent.customDateTimeInput(
    isDateOnly: Boolean, classes: String? = null, block: INPUT.() -> Unit = {}
) {
    if (isDateOnly) dateInput(classes = classes, block = block)
    else dateTimeLocalInputWithFallbackGuidelines(classes = classes, block = block)
}

fun LocalDateTime.addTimeZone(tz: String?) = ZonedDateTime.of(this, ZoneId.of(tz ?: "UTC"))

fun String.getUTC(tz: String?) = LocalDateTime.parse(this).addTimeZone(tz ?: "UTC").toInstant()
//fun Instant.getLocal(tz: String = "UTC") = LocalDateTime.ofInstant(this, ZoneId.of(tz.ifEmpty { "UTC" }))

fun String.instant(timezone: String? = null, overRideTimeZone: Boolean = false): Instant {
    var tz = false
    if (!timezone.isNullOrEmpty() && timezone != "UTC" && overRideTimeZone) tz = true
    val result = when {
        isEmpty() -> Instant.EPOCH
        !contains("T") -> Instant.parse("${this}T00:00:00Z")
        tz -> getUTC(timezone)
        contains("T") -> Instant.parse("$this:00Z")
        else -> Instant.EPOCH
    }
    return result
}

fun Instant.getMillisLong() = toEpochMilli().toLong()

fun Int.leadingZero() = if (this < 10) "0$this" else toString()

//fun instant(year: Int, month: Int, day: Int, hour: Int=0, minute: Int=0): Instant =
//    ("$year-${(month+1).leadingZero()}-${day.leadingZero()}T${hour.leadingZero()}:${minute.leadingZero()}").instant()
fun makeInstant(
    year: Int, month: Int, day: Int, hour: Int = 0, minute: Int = 0, tz: Boolean = false, tzStr: String? = null
): Instant =
    ("$year-${month.leadingZero()}-${day.leadingZero()}T${hour.leadingZero()}:${minute.leadingZero()}").instant(
        tzStr, tz
    )

fun Instant.toDateInputString(isDateOnly: Boolean): String {
    val letterToTrimFrom = if (isDateOnly) 'T' else 'Z'
    val string = toString().takeWhile { it != letterToTrimFrom }
    return if (isDateOnly) string
    else string.take(16) // Drop any precision below minutes (seconds, milliseconds, etc.)
}

fun convertInputValue(value: String, isDateOnly: Boolean, timeZone: String? = null): String {
    if (value.isEmpty()) return ""
    return value.instant(timeZone) // Inverting the isDateOnly since we need to pass the existing state
        .toDateInputString(isDateOnly)
}

fun addTimeToDate(date: Instant, timeInMilliseconds: Long): Instant = date.plusMillis(timeInMilliseconds)

fun parseRange(input: String): Array<Int?> {
    val sections = input.split('-')
    val array: Array<Int?> = arrayOf()
    for (i in sections.indices) {
        array[i] = (parseDays(sections[i])?.div(MILLISECONDS_IN_A_DAY))?.toInt()
    }
    return array
}

fun parseDays(input: String): Long? {
    if (input.contains('-')) return null
    if (input.isEmpty()) return null

    val sections = input.split(':')

    val dur = Duration.ofDays(sections[0].toInt())
        .plusHours(sections.getOrNull(1)?.toInt() ?: 0)
        .plusMinutes(sections.getOrNull(2)?.toInt() ?: 0)
    return dur.toMillis().toLong()
}

fun milliToDayHrMin(numberOfMilliseconds: Long): Array<Double> {
    var inst = Duration.ofMillis(numberOfMilliseconds)
    val days = inst.toDays()
    inst = inst.minusDays(days)
    val hours = inst.toHours()
    inst = inst.minusHours(hours)
    val minutes = inst.toMinutes()
    return arrayOf(days.toDouble(), hours.toDouble(), minutes.toDouble())
}

fun daysHoursMinutesDigital(
    numberOfMilliseconds: Long,
    typeOfInput: TypesOfInputs = TypesOfInputs.DURATION,
    languageNames: String = Vls.Langs.ENGLISH
): String {
    if (languageNames == Vls.Langs.ENGLISH) {
        var isDateOnly = false
        if (typeOfInput == TypesOfInputs.DATE_ONLY) {
            isDateOnly = true
        }

        val (days, hours, minutes) = milliToDayHrMin(numberOfMilliseconds)
        var strHours = hours.toString()
        var strMinutes = minutes.toString()
        var strDays = days.toString()

        when (days) {
            0.0 -> strDays = ""
            1.0 -> strDays += " day"
            else -> strDays += " days"
        }
        when (hours) {
            0.0 -> strHours = ""
            1.0 -> strHours += " hour"
            else -> strHours += " hours"
        }
        when (minutes) {
            0.0 -> strMinutes = ""
            1.0 -> strMinutes += " minute"
            else -> strMinutes += " minutes"
        }

        var returnStatement =
            if (strDays.isNotEmpty() && strHours.isNotEmpty() && strMinutes.isNotEmpty()) "$strDays, $strHours and $strMinutes"
            else if (strDays.isNotEmpty() && strHours.isNotEmpty()) "$strDays and $strHours"
            else if (strDays.isNotEmpty() && strMinutes.isNotEmpty()) "$strDays and $strMinutes"
            else if (strHours.isNotEmpty() && strMinutes.isNotEmpty()) "$strHours and $strMinutes"
            else if (strDays.isEmpty() && strHours.isEmpty() && strMinutes.isEmpty() && !isDateOnly) {
                "0 minutes"
            } else strDays + strHours + strMinutes
        if (isDateOnly) returnStatement = if (days == 1.0) "1 day" else if (days == 0.0) "0 days" else strDays
        return returnStatement.trimEnd().trim()
    } else if (languageNames == Vls.Langs.URDU) {
        var isDateOnly = false
        if (typeOfInput == TypesOfInputs.DATE_ONLY) {
            isDateOnly = true
        }

        val (days, hours, minutes) = milliToDayHrMin(numberOfMilliseconds)

        val strHours = when (hours) {
            0.0 -> ""
            1.0 -> "$hours گھنٹہ"
            else -> "$hours گھنٹے"
        }
        val strMinutes = if (minutes == 0.0) "" else "$minutes منٹ"
        val strDays = if (days == 0.0) "" else "$days دن"
        var returnStatement = "$strDays $strHours $strMinutes"
        if (strDays.isEmpty() && strHours.isEmpty() && strMinutes.isEmpty()) returnStatement = "0 منٹ"
        if (isDateOnly) returnStatement = if (days == 0.0) "0 دن" else strDays
        return returnStatement.trim().trimEnd()
    } else if (languageNames == Vls.Langs.MMENGLISH) {
        var isDateOnly = false
        if (typeOfInput == TypesOfInputs.DATE_ONLY) {
            isDateOnly = true
        }
        if (numberOfMilliseconds == -1L) {
            return "?"
        }

        val (days, hours, minutes) = milliToDayHrMin(numberOfMilliseconds)
        var strHours = hours.toString()
        var strMinutes = minutes.toString()
        var strDays = days.toString()

        when (days) {
            0.0 -> strDays = "0${Rplc.LETTER}"
            else -> strDays += Rplc.LETTER
        }
        when (hours) {
            0.0 -> strHours = ""
            else -> strHours += "h"
        }
        when (minutes) {
            0.0 -> strMinutes = ""
            else -> strMinutes += "min"
        }

        var returnStatement =
            if (strDays.isNotEmpty() && strHours.isNotEmpty() && strMinutes.isNotEmpty()) "$strDays $strHours $strMinutes"
            else if (strDays.isNotEmpty() && strHours.isNotEmpty()) "$strDays $strHours"
            else if (strDays.isNotEmpty() && strMinutes.isNotEmpty()) "$strDays $strMinutes"
            else if (strHours.isNotEmpty() && strMinutes.isNotEmpty()) "$strHours $strMinutes"
            else if (strDays.isEmpty() && strHours.isEmpty() && strMinutes.isEmpty() && !isDateOnly) {
                "0min"
            } else strDays + strHours + strMinutes
        if (isDateOnly) returnStatement = if (days == 1.0) "1d" else if (days == 0.0) "0d" else strDays
        return returnStatement.trimEnd().trim()
    }
    return ""
}

fun localHazDatesList(hazDatesList: MutableList<Entry>, tz: String): List<LocalEntry> {
    return hazDatesList.map { entry ->
        LocalEntry(
            LocalDateTime.ofInstant(entry.startTime, ZoneId.of(tz)),
            LocalDateTime.ofInstant(entry.endTime, ZoneId.of(tz))
        )
    }
}

fun languagedDateFormat(
    date: Instant, typeOfInput: TypesOfInputs, language: String, timeZone: String = "UTC", addYear: Boolean = false
): String {
    var isDateOnly = false
    if (typeOfInput == TypesOfInputs.DATE_ONLY) {
        isDateOnly = true
    }
    val localStr = LocalDateTime.ofInstant(date, ZoneId.of(timeZone))
    val day = localStr.dayOfMonth()
    val month = localStr.month()
    var hours = localStr.hour().toInt()
    val minutesStr = localStr.minute().toInt().leadingZero()
    val year = if (addYear) localStr.year().toString() else ""

    if (language == Vls.Langs.ENGLISH) {
//         2023-04-02T00:22:00Z
        val dateStr = "$day ${month.toString().lowercase().replaceFirstChar { it.titlecase() }} $year".trim()

        var ampm = "am"
        if (hours >= 12) {
            hours -= 12
            ampm = "pm"
        }
        if (hours == 0) hours = 12

        val hoursStr: String = hours.toString()

        return if (isDateOnly) dateStr //05 Jun 2021
        else "$dateStr at $hoursStr:$minutesStr $ampm" //13 Dec at 7:30 pm
    } else if (language == Vls.Langs.URDU) {
//         val monthStr = month.displayName(TextStyle.FULL, Locale)
        val urduMonth = urduMonthNames[month.value().toInt() - 1]
        val urduDay: String = if (day == 1) "یکم" else day.toString()

        return if (isDateOnly) "$urduDay $urduMonth $year".trim()
        else { //has time too
            val ampm = when (hours) {
                in 4..11 -> "صبح" //4am-11am
                in 12..14 -> "دوپہر" //12pm-2pm
                in 15..18 -> "شام" //3pm-6pm
                else -> "رات" //7pm-3am
            }
            if (hours >= 12) hours -= 12
            if (hours == 0) hours = 12

            "$urduDay $urduMonth $ampm $hours:$minutesStr بجے".trim().trimEnd()
        }
    } else if (language == Vls.Langs.MMENGLISH) {
//         2023-04-02T00:22:00Z
        val englishMonth = englishMonthNames[month.value().toInt() - 1]
        var dayth = ""
        if (day == 1 || day == 21 || day == 31) {
            dayth = "${day}st"
        } else if (day == 2 || day == 22) {
            dayth = "${day}nd"
        } else if (day == 3 || day == 23) {
            dayth = "${day}rd"
        } else {
            dayth = "${day}th"
        }

        val dateStr = "$dayth $englishMonth $year".trim()

        var ampm = "am"
        if (hours >= 12) {
            hours -= 12
            ampm = "pm"
        }
        if (hours == 0) hours = 12

        val hoursStr: String = hours.toString()

        return if (isDateOnly) dateStr //05 Jun 2021
        else "$dateStr $hoursStr" + "." + "$minutesStr$ampm" //13 Dec at 7:30 pm
    }
    return "Error"
}

fun difference(date1: Instant, date2: Instant): Long {
    return (date2.getMillisLong() - date1.getMillisLong())
}

fun Int.getMilliDays() = Duration.ofDays(this).toMillis().toLong()
fun Long.getDays() = Duration.ofMillis(this).toDays().toInt()

fun baseStr(baseString: Strings.() -> String): OutputStringsLanguages {
    val ur = StringsOfLanguages.URDU.baseString()
    val en = StringsOfLanguages.ENGLISH.baseString()
    val mmen = StringsOfLanguages.MMENGLISH.baseString()
    return OutputStringsLanguages(ur, en, mmen)
}

fun OutputStringsLanguages.add(out: OutputStringsLanguages): OutputStringsLanguages {
    urduString += out.urduString
    englishString += out.englishString
    mmEnglishString += out.mmEnglishString
    return this
}

fun OutputStringsLanguages.addStr(string: String): OutputStringsLanguages {
    urduString += string
    englishString += string
    mmEnglishString += string
    return this
}

fun OutputStringsLanguages.addStrings(baseString: Strings.() -> String): OutputStringsLanguages {
    urduString += StringsOfLanguages.URDU.baseString()
    englishString += StringsOfLanguages.ENGLISH.baseString()
    mmEnglishString += StringsOfLanguages.MMENGLISH.baseString()
    return this
}

fun OutputStringsLanguages.replace(oldUr: String, newUr: String, oldEn: String, newEn: String): OutputStringsLanguages {
    //Todo: Check for this
    urduString = urduString.replace(oldUr, newUr)
    englishString = englishString.replace(oldEn, newEn)
    mmEnglishString = mmEnglishString.replace(oldEn, newEn)
    return this
}

fun OutputStringsLanguages.replaceDT(
    placeholder: String, date: Instant, typeOfInput: TypesOfInputs, timeZone: String
): OutputStringsLanguages {
    urduString = urduString.replace(placeholder, languagedDateFormat(date, typeOfInput, Vls.Langs.URDU, timeZone))
    englishString =
        englishString.replace(placeholder, languagedDateFormat(date, typeOfInput, Vls.Langs.ENGLISH, timeZone))
    mmEnglishString =
        mmEnglishString.replace(placeholder, languagedDateFormat(date, typeOfInput, Vls.Langs.MMENGLISH, timeZone))
    return this
}

fun OutputStringsLanguages.replaceDur(
    placeholder: String, millis: Long, typeOfInput: TypesOfInputs, letter: String = ""
): OutputStringsLanguages {
    urduString = urduString.replace(placeholder, daysHoursMinutesDigital(millis, typeOfInput, Vls.Langs.URDU))
    englishString = englishString.replace(placeholder, daysHoursMinutesDigital(millis, typeOfInput, Vls.Langs.ENGLISH))
    mmEnglishString = mmEnglishString.replace(
        placeholder, daysHoursMinutesDigital(millis, typeOfInput, Vls.Langs.MMENGLISH).replace(
            Rplc.LETTER,
            letter.ifEmpty { "d" }
        )
    )
    return this
}

// VALS TO USE
object Ids {
    const val LANGUAGE = "language"
    const val ERROR_MESSAGE = "error_message"

    object InputContainers {
        const val INPUT_CONTAINERS_CONTAINER = "input_containers_container"
        const val INPUT_CONTAINER = "input_container"
        const val INPUTS_CONTAINER_CLONE_BUTTON = "inputs_container_clone_button"
        const val INPUTS_CONTAINER_REMOVE_BUTTON = "inputs_container_remove_button"
    }

    object InputTables {
        const val HAIZ_INPUT_TABLE = "haiz_input_table"
        const val HAIZ_INPUT_TABLE_BODY = "haiz_input_table_body"
        const val HAIZ_DURATION_INPUT_TABLE = "haiz_duration_input_table"
        const val HAIZ_DURATION_INPUT_TABLE_BODY = "haiz_duration_input_table_BODY"
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
        const val CONTENT_ANSWER = "content_answer"
        const val CONTENT_URDU = "content_urdu"
        const val CONTENT_ENGLISH = "content_english"
        const val CONTENT_MMENGLISH = "content_mmenglish"
        const val CONTENT_DATES = "content_dates"
        const val CALCULATE_ALL_DIV = "calculate_all_div"
        const val CALCULATE_BUTTON = "calculate_button"
        const val COPY_BTN = "copy_button"
        const val CLIPBOARD_JS_BTN = "clipboard_js_button"
        const val COPY_TOOLTIP = "copy_tooltip"
        const val COMPARISON_CONTAINER = "comparison_container"
        const val DATES_DIFFERENCE_TABLE = "dates_difference_table"
    }

    object Inputs {
        const val MASLA_TYPE_SELECT = "masla_type_select"
        const val ZAALLA_CHECKBOX = "zaalla_checkbox"
        const val INPUT_TYPE_SELECT = "input_type_select"
        const val IS_DAYLIGHT_SAVINGS = "is_daylight_savings"
        const val SELECT_LOCALE = "select_locale"
        const val PREG_END_TIME_INPUT = "preg_end_time_input"
        const val PREG_START_TIME_INPUT = "preg_start_time_input"
        const val MUSTABEEN_CHECKBOX = "mustabeen_checkbox"
        const val AADAT_HAIZ_INPUT = "aadat_haiz_input"
        const val AADAT_TUHR_INPUT = "aadat_tuhr_input"
        const val MAWJOODA_TUHR_INPUT = "mawjooda_tuhr_input"
        const val MAWJOODA_FAASID_CHECKBOX = "mawjooda_faasid_checkbox"
        const val AADAT_NIFAS_INPUT = "aadat_nifas_input"
        const val ZAALLA_CYCLE_LENGTH = "zaalla_cycle_length"
        const val INPUT_ID = "input_id"
        const val SUBMIT = "submit"
        const val INPUT_SAAILA = "input_saaila"
        const val INPUT_QUESTION = "input_question"
    }

    object LoginLogout {
        const val USERNAME = "username"
        const val PASSWORD = "password"
        const val SUBMIT = "submit"
        const val LOGOUT_DIV = "logout_div"
        const val LOGOUT_BUTTON = "logout_button"
    }
}

object SaveMaslaId {
    const val SAVED: String = "saved"
    const val DATA_SAVED: String = "data-saved"
}

object Letters {
    const val b: String = "b"
    const val p: String = "p"
}

object CssC {
    const val INVIS = "invisible"                   // Invis. Put on any element that shouldn't show; also doable by elem.visibility
    const val LANG_INVIS = "lang-invisible"         // Invis. Put on any element that shouldn't show because of lang
    const val HIDDEN = "hidden"                     // Hidden. Put on any element that shouldn't show; but still exist and take up space

    const val ENGLISH = "english"                   // Switch. Put on any element that should only show when lang is english
    const val MMENGLISH = "mmenglish"               // Switch. Put on any element that should only show when lang is english
    const val URDU = "urdu"                         // Switch. Put on any element that should only show when lang is urdu
    const val DEV = "dev"                           // Switch. Put on any element that should only show when devmode
    const val RTL = "rtl"                           // Switch. Put on any element that should switch rtl but NOT invis

    const val NIFAS = "nifas"                       // Switch. Put on any input that only shows when Nifas
    const val ZAALLA = "zaalla"                     // Switch. Put on any input that only shows when Zaalla
    const val MUTADA = "mutada"                     // Switch. Put on any input that only shows when NOT Mubtadia
    const val DATE_OR_TIME_AADAT = "datetime_aadat" // Switch. Put on any input that only shows when NOT Duration
    const val DATETIME_ONLY = "datetime_only"       // Switch. Put on any input that only shows when Datetime
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
    const val HALF_TABLE_CELL = "half_table_cell"   // CSS Style.
    const val EMPTY_TABLE_CELL = "empty_table_cell" // CSS Style.
    const val NA_PAAKI = "na_paaki"                 // CSS Style.
    const val AYYAM_E_SHAKK = "ayyam_e_shakk"       // CSS Style.

    const val COPY_BTN = "copy_btn"
}

object Vls {                                        // Values
    object Langs {
        const val ENGLISH = "english"
        const val MMENGLISH = "mmenglish"
        const val URDU = "urdu"
    }

    //    Mutada is spelled Mutadah in data class typesOfMasla
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

object Rplc {
    const val DT1 = "date1"
    const val DT2 = "date2"
    const val DUR1 = "duration1"
    const val DUR2 = "duration2"
    const val DUR3 = "duration3"
    const val FASID = "فاسد "
    const val INVALID = "invalid "
    const val LETTER = "rplcletter"

    object Msg {
        const val NOT_BLOOD = "Not Blood"
        const val PLACEHOLDER = "Placeholder"
    }
}

const val MILLISECONDS_IN_A_DAY: Long = 86400000
//const val MILLISECONDS_IN_AN_HOUR = 3600000
//const val MILLISECONDS_IN_A_MINUTE = 60000

val NO_OUTPUT = OutputTexts(
    OutputStringsLanguages(),
    "",
    mutableListOf(),
    EndingOutputValues(null, null, mutableListOf()),
    mutableListOf()
)
val ARBITRARY_DATE = Instant.EPOCH
val englishMonthNames = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
val mmEnglishMonthNames = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
val urduMonthNames =
    arrayOf("جنوری", "فروری", "مارچ", "اپریل", "مئی", "جون", "جولائ", "اگست", "ستمبر", "اکتوبر", "نومبر", "دسمبر")

object Events {
    const val VISIBILITY_CHANGE = "visibilitychange"
}

object UnicodeChars {
    const val RED_DIAMOND = "&#9830;&#65039;"        // RED_DIAMOND

    //    const val WHITE_DIAMOND = "&#128160;"            // WHITE_DIAMOND
    const val ORANGE_DIAMOND = "&#x1F538;"           // ORANGE_DIAMOND
    const val SNOWFLAKE = "&#10052;&#65039;"     // SNOWFLAKE
    const val BLACK_SQUARE = "&#9642;"
    const val FAT_DASH = "&#x2796;"
    const val MEMO = "&#128221;"                    // MEMO
    const val HAND_WRITING = "&#9997;&#65039;"           // HAND_WRITING
    const val BLUE_SWIRL = "\uD83C\uDF00"
    const val ABACUS = "&#129518;"
//    const val TAB:String = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
}
