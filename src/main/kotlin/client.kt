@file:Suppress("SpellCheckingInspection") @file:OptIn(DelicateCoroutinesApi::class)

import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.datetime.internal.JSJoda.Instant
import kotlinx.datetime.internal.JSJoda.LocalDate
import kotlinx.html.classes
import kotlinx.html.js.a
import kotlinx.html.js.div
import org.w3c.dom.*

@JsModule("@js-joda/timezone")
@JsNonModule
external object JsJodaTimeZoneModule

private val jsJodaTz = JsJodaTimeZoneModule

//val version = 2
// START PROGRAM
fun main() {
    window.onload = {
        if (rootHazapp != null) {
            if (loggedIn()) {
                hazappPage()
            } else {
                loginPage()
            }
        } else mainOtherCalcs()                             // Other Calcs Page

        parseHREF()
    }
}

fun hazappPage() {
    rootHazapp!!.innerHTML = ""
    logoutDiv.innerHTML = ""
    logoutDiv.addProfileButton()
    rootHazapp.addInputLayout()
    setupRows(inputsContainers.first())
    document.addEventListener(Events.VISIBILITY_CHANGE, {
        if (!document.isHidden) {
            setMaxToCurrentTimeForTimeInputs(inputsContainers.first())
        }
    })
    if (isPersonalApper && !noUserMaslaId) {
        GlobalScope.launch { loadData(userMaslaId!!, inputsContainers.first()) }
        window.alert(
            "ANY MISSED BLEEDING/SPOTTING WILL RESULT IN AN INACCURATE RULING " +
                    "AND POTENTIALLY RESULT IN MISSED FARD SALAH!$NEW_LINE" +
                    "PLEASE REMEMBER TO REPORT ALL SPOTTING.$NEW_LINE" +
                    "IF THERE IS A SPOT IN THE PAST THAT YOU FORGOT TO RECORD, " +
                    "PLEASE CONTACT YOUR ADMIN AND THEY WILL HELP YOU UPDATE THIS"
        )
    }
    parseHREF()
}

private fun parseHREF() {
    // DEVMODE
    for (element in devElements) element.visibility = devmode

    // DEFAULT LANGUAGE
    languageSelector.onchange = { languageChange() }
    if (window.location.href.contains("lang=en")) languageSelector.value = Vls.Langs.ENGLISH
    if (window.location.href.contains("lang=mm")) languageSelector.value = Vls.Langs.MMENGLISH
    languageChange()
}

fun languageChange() {
    val lang = languageSelected
    // Invis every language dependent element based on if it DOESN'T have the selected language:
    // TODO: Make better.
    for (element in languageElements) element.classList.toggle(CssC.LANG_INVIS, !element.classList.contains(lang))

    document.body!!.classList.toggle(CssC.RTL, lang == Vls.Langs.URDU) // RTL on body if Urdu
    document.querySelectorAll("select") // Selected options don't invis so switch them.
        .asList().map { it as HTMLSelectElement }.forEach { select -> setOptionInSelect(select) }
}

fun makeRangeArray(
    aadatHaz: String, aadatTuhr: String, cycleLength: String, aadatNifas: String
): MutableList<AadatsOfHaizAndTuhr> {
    //this returns an array conating all the possibilities we want to plug in and try
    val combosToTry = mutableListOf<AadatsOfHaizAndTuhr>() //this is what we will output
    val aadatHaizList = mutableListOf<Int>() //this is all the haiz aadat possibilities. if none, then this contains -1
    if (aadatHaz.isNotEmpty()) {
        val (haizStart, haizEnd) = parseRange(aadatHaz)
        if (haizStart != null && haizEnd != null) {
            for (i in haizStart..haizEnd) {
                aadatHaizList += i
            }
        }
    } else {
        aadatHaizList += -1
    }
    val aadatTuhrList = mutableListOf<Int>()
    if (aadatTuhr.isNotEmpty()) {
        val tuhrStart = parseRange(aadatTuhr)[0]
        val tuhrEnd = parseRange(aadatTuhr)[1]
        if (tuhrStart != null && tuhrEnd != null) {
            for (i in tuhrStart..tuhrEnd) {
                aadatTuhrList += i
            }
        }
    } else {
        aadatTuhrList += -1
    }

    val aadatNifasList = mutableListOf<Int>() //this is all the haiz aadat possibilities. if none, then this contains -1
    if (aadatNifas.isNotEmpty()) {//nifas aadat isn't blank
        val nifasStart =
            parseRange(aadatNifas)[0] //parse range function splits it and returns the 2 values on either end of the dash
        val nifasEnd = parseRange(aadatNifas)[1]
        if (nifasStart != null && nifasEnd != null) {
            for (i in nifasStart..nifasEnd) {
                aadatNifasList += i
            }
        }
    } else {
        aadatNifasList += -1
    }
    if (cycleLength == "") {//there is no cycle length
        if (!aadatTuhr.contains('-') && aadatTuhr != "") {//if tuhr aadat doesn't contain a -, then just put the on tuhr aadat in array
            aadatTuhrList += aadatTuhr.toInt()
        }
        if (!aadatHaz.contains(('-')) && aadatHaz != "") {//if haiz aadat doen't have -, then just enter that one habit
            aadatHaizList += aadatHaz.toInt()
        }
        for (tuhrAadat in aadatTuhrList) {
            for (aadatHaiz in aadatHaizList) {
                combosToTry += AadatsOfHaizAndTuhr(aadatHaiz.getMilliDays(), tuhrAadat.getMilliDays())
            }
        }


    } else {//there is cycle length, and only one of haiz or tuhr, which is ranged
        if (aadatTuhrList[0] == -1) {//there is no tuhr aadat  - haiz aadat is a range
            for (haizAadat in aadatHaizList) {
                val tuhrAadat = parseDays(cycleLength)?.minus(haizAadat.getMilliDays())
                if (tuhrAadat != null && tuhrAadat >= 15.getMilliDays()) {
                    combosToTry += AadatsOfHaizAndTuhr(haizAadat.getMilliDays(), tuhrAadat)
                }
            }

        } else if (aadatHaizList[0] == -1) {//there is no haiz aadat - tuhr aadat is a range
            for (tuhrAadat in aadatTuhrList) {//got through each tuhr aadat, figure out if it's composite haiz is a viablr haiz, if so, add it to the combos
                val haizAadat = parseDays(cycleLength)?.minus(tuhrAadat.getMilliDays())
                if (haizAadat != null && haizAadat >= 3.getMilliDays() && haizAadat <= 10.getMilliDays()) {
                    combosToTry += AadatsOfHaizAndTuhr(haizAadat, tuhrAadat.getMilliDays())
                }
            }
        }
    }
    if (!aadatNifas.contains(('-')) && aadatNifas != "") {//if nifas aadat doen't have -, then just enter that one habit
        aadatNifasList += aadatNifas.toInt()
    }
    if (aadatNifasList[0] != -1) {//we do have a nifas aadat
        val combosToTryWithNifas = mutableListOf<AadatsOfHaizAndTuhr>() //this is what we will output
        for (combo in combosToTry) {
            for (nifasAadat in aadatNifasList) {
                combosToTryWithNifas += AadatsOfHaizAndTuhr(combo.aadatHaiz, combo.aadatTuhr, nifasAadat.getMilliDays())
            }
        }
        return combosToTryWithNifas
    } else {//we don't have a nifas aadat
        return combosToTry
    }
}

var formattedAnswers = OutputStringsLanguages()

fun parseEntries(inputContainer: HTMLElement) {
    var entries = listOf<Entry>()

    with(inputContainer) {
//        NOTE: BECAUSE PREG ISALWAYS DATE NOT TIME, I AM NOT APPLYING TO LOCAL BUT TO INSTANT
        val timezone = if (isDateTime && !timezoneSelect.disabled) timezoneSelect.value else "UTC"

        val pregnancyStrt = pregStartTime.value.instant(timezone, isDateTime)
        val pregnancyEnd = pregEndTime.value.instant(timezone, isDateTime)

        val typeOfMasla: TypesOfMasla = if (isMubtadia) {
            TypesOfMasla.MUBTADIA
        } else if (isNifas) {
            TypesOfMasla.NIFAS
        } else {
            TypesOfMasla.MUTADAH
        }
        val typesOfInputs: TypesOfInputs = if (isDateOnly) {
            TypesOfInputs.DATE_ONLY
        } else if (isDuration) {
            TypesOfInputs.DURATION
        } else {
            TypesOfInputs.DATE_AND_TIME
        }

        val preMaslaValues = PreMaslaValues(
            parseDays(aadatHaz.value), parseDays(aadatTuhr.value), parseDays(mawjoodaTuhr.value), isMawjoodaFasid
        )

        val ikhtilaafaat = Ikhtilaafaat(
            ikhtilaf1, ikhtilaf2, ikhtilaf3, ikhtilaf4
        )

        val pregnancy = Pregnancy(
            pregnancyStrt, pregnancyEnd, parseDays(aadatNifas.value), isMustabeen
        )

        var allTheInputs: AllTheInputs

        if (typesOfInputs == TypesOfInputs.DURATION) {
            val durations = haizDurationInputDatesRows.map { row ->
                Duration(
                    type = when (row.damOrTuhr) {
                        Vls.Opts.DAM -> {
                            DurationType.DAM
                        }

                        Vls.Opts.TUHR -> {
                            DurationType.TUHR
                        }

                        Vls.Opts.HAML -> {
                            DurationType.HAML
                        }

                        Vls.Opts.WILADAT -> {
                            DurationType.WILADAT_ISQAT
                        }

                        else -> {
                            DurationType.NIFAS
                        }
                    }, timeInMilliseconds = parseDays(row.durationInput.value)!!
                )
            }
            allTheInputs = AllTheInputs(
                entries, preMaslaValues, typeOfMasla, pregnancy, typesOfInputs, languageSelected, ikhtilaafaat, timezone, addNow
            )
            allTheInputs = convertDurationsIntoEntries(durations, allTheInputs)
        } else {
            entries = haizInputDatesRows.map { row ->
                Entry(
                    startTime = row.startTimeInput.value.instant(timezone, isDateTime),
                    endTime = row.endTimeInput.value.instant(timezone, isDateTime)
                )
            }
            allTheInputs = AllTheInputs(
                entries, preMaslaValues, typeOfMasla, pregnancy, typesOfInputs, languageSelected, ikhtilaafaat, timezone, addNow
            )
        }

        if ((aadatHaz.value + aadatTuhr.value + aadatNifas.value).contains("-") && devmode) {
            contentContainer.visibility = false
            handleRangedInput(allTheInputs, aadatHaz.value, aadatTuhr.value, cycleLength.value, aadatNifas.value)
            return
        }

//        @Suppress("UnsafeCastFromDynamic")
        val output: OutputTexts = if (allTheInputs.entries != null) {
            handleEntries(allTheInputs)
        } else {
            NO_OUTPUT
        }

        contentContainer.visibility = true
        contentContainer.setAttribute("data-saved", "false")
        formattedAnswers = output.outputText.formatStrings() // TODO: send formatted strngs or raw-as-from-orig?
        contentEnglish.innerHTML = formattedAnswers.englishString
        contentMMEnglish.innerHTML = formattedAnswers.mmEnglishString
        contentUrdu.innerHTML = formattedAnswers.urduString
        haizDatesList = output.hazDatesList
        populateTitleFieldIfEmpty(inputContainer, aadatHaz.value, aadatTuhr.value, mawjoodaTuhr.value)
        contentContainer.scrollIntoView()
    }
}

private fun handleRangedInput(
    allTheInputs: AllTheInputs, aadatHaz: String, aadatTuhr: String, cycleLength: String, aadatNifas: String
) {
    val combosToTry = makeRangeArray(aadatHaz, aadatTuhr, cycleLength, aadatNifas)
    val listOfLists = mutableListOf<MutableList<Entry>>()
    val listOfDescriptions = mutableListOf<String>()
    for (aadatCombo in combosToTry) { //go through combos and input them into logic and get their output
        if (aadatCombo.aadatTuhr == -(1.getMilliDays())) {
            allTheInputs.preMaslaValues.inputtedAadatTuhr = null
        } else {
            allTheInputs.preMaslaValues.inputtedAadatTuhr = aadatCombo.aadatTuhr
        }
        if (aadatCombo.aadatHaiz == -(1.getMilliDays())) {
            allTheInputs.preMaslaValues.inputtedAadatHaiz = null
        } else {
            allTheInputs.preMaslaValues.inputtedAadatHaiz = aadatCombo.aadatHaiz
        }
        if (aadatCombo.aadatNifas == -(1.getMilliDays())) {
            allTheInputs.pregnancy!!.aadatNifas = null
        } else {
            allTheInputs.pregnancy!!.aadatNifas = aadatCombo.aadatNifas
        }
        val output = handleEntries(allTheInputs)

        // We gotta put this next line, so we don't keep on getting error messages every time it puts in a new value.
        // Break at first error
        if (output == NO_OUTPUT) return
        listOfLists += output.hazDatesList

        //create a description for each combo
        if (aadatCombo.aadatNifas != null && aadatCombo.aadatNifas != (-1).getMilliDays()) {//aadat nifas exists
            listOfDescriptions += if (aadatCombo.aadatHaiz == (-1).getMilliDays()) {
                "(${(aadatCombo.aadatNifas!!.getDays())})/${(aadatCombo.aadatTuhr.getDays())}"
            } else if (aadatCombo.aadatTuhr == (-1).getMilliDays()) {
                "(${(aadatCombo.aadatNifas!!.getDays())})/${(aadatCombo.aadatHaiz.getDays())}"
            } else {
                "(${(aadatCombo.aadatNifas!!.getDays())})/${(aadatCombo.aadatHaiz.getDays())}/${(aadatCombo.aadatTuhr.getDays())}"
            }
        } else {//no nifas
            listOfDescriptions += if (aadatCombo.aadatHaiz == (-1).getMilliDays()) {
                "${(aadatCombo.aadatTuhr.getDays())}"
            } else if (aadatCombo.aadatTuhr == (-1).getMilliDays()) {
                "${(aadatCombo.aadatHaiz.getDays())}"
            } else {
                "${(aadatCombo.aadatHaiz.getDays())}/${(aadatCombo.aadatTuhr.getDays())}"
            }
        }
    }
    val output = generatInfoForCompareTable(listOfLists.toMutableList())
    drawCompareTable(output.headerList, output.listOfColorsOfDaysList, output.resultColors, listOfDescriptions)
}

fun populateTitleFieldIfEmpty(inputContainer: HTMLElement, aadatHaz: String, aadatTuhr: String, mawjoodaTuhr: String) {
    with(inputContainer) {
        if (saailaDetails == "") {
            var text = "$aadatHaz/$aadatTuhr/$mawjoodaTuhr".trim()
            if (text.contains("//")) {
                text = text.replace("//", "/")
            }
            if (text.startsWith("/")) {
                text = text.drop(1)
            }
            if (text.endsWith("/")) {
                text = text.dropLast(1)
            }
            saailaDetailsInput.value = text
        }
    }
}

fun validateNifasDurations(durations: List<Duration>): Boolean {
    //this is ensuring that we have both pregnancy and birth, and only one of each.

    //I am wondering if, when preg, or birth, or both are missing, we can just arbitrarily add them to the start of the masla.
    //it seems possible, but IDK if that is what we want.

    var pregnancy = false
    var wiladatIsqat = false
    for (duration in durations) {
        if (duration.type == DurationType.HAML) {
            if (pregnancy) {
                window.alert("You can only solve one pregnancy per masla")
                return false
            } else {//is false
                pregnancy = true
            }
        } else if (duration.type == DurationType.WILADAT_ISQAT) {
            if (wiladatIsqat) {
                window.alert("You can only solve one birth at a time")
                return false
            } else if (!pregnancy) {
                window.alert("Please add pregnancy before birth")
                return false
            } else {//is false
                wiladatIsqat = true
            }
        }
    }
    if (!pregnancy || !wiladatIsqat) {
        window.alert("You need to add pregnancy and birth/miscarriage to solve a nifas question.")
        return false
    }
    return true
}

fun convertDurationsIntoEntries(
    durations: List<Duration>, allTheOriginalInputs: AllTheInputs = AllTheInputs(null)
): AllTheInputs {
    if (allTheOriginalInputs.typeOfMasla == TypesOfMasla.NIFAS) {
        if (!validateNifasDurations(durations)) {
            return AllTheInputs(null)
        }
    }
    for (index in durations.indices) {
        if (index > 0) {
            durations[index].startTime = durations[index - 1].endDate
        }
    }
    var mawjodahtuhreditable: Long? = allTheOriginalInputs.preMaslaValues.inputtedMawjoodahTuhr
    var isMawjoodaFasid = allTheOriginalInputs.preMaslaValues.isMawjoodaFasid
    val entries = mutableListOf<Entry>()
    var pregnancyEnd = ARBITRARY_DATE
    var pregnancyStrt: Instant = ARBITRARY_DATE

    //as there is no way that entries can begin with a tuhr, and we are translating durations to entries,
    // we will put a beginning tuhr in mawjoodah paki.
    //beginning tuhr cannot be fasid. if it is fasid, it is tuhr in haml
    //later, we will put these back in fixed durations
//    if(durations[0].type == DurationType.TUHR && durations[0].days>=15){
//        mawjodahtuhreditable += durations[0].timeInMilliseconds
//    }
//    else if(durations[0].type==DurationType.HAML &&
//        durations[1].days>=15&&
//        durations[1].type ==DurationType.TUHR){
//        mawjodahtuhreditable += durations[1].timeInMilliseconds
//        isMawjoodaFasid = true
//    }


    for (dur in durations) {
        when (dur.type) {
            DurationType.DAM -> {
                entries += Entry(dur.startTime, dur.endDate)
            }

            DurationType.HAML -> {
                pregnancyStrt = dur.startTime
                if (entries.size == 0) {
                    isMawjoodaFasid = true
                }

            }

            DurationType.WILADAT_ISQAT -> {
                pregnancyEnd = dur.startTime
            }

            DurationType.TUHR -> {
                if (entries.size == 0) {
                    if (mawjodahtuhreditable == null) {
                        mawjodahtuhreditable = dur.timeInMilliseconds
                    } else {
                        mawjodahtuhreditable += dur.timeInMilliseconds
                    }
                }
            }

            else -> error("Not Blood")
        }
    }
    if (mawjodahtuhreditable != null) {
        if (mawjodahtuhreditable < 15.getMilliDays() && mawjodahtuhreditable != -1L) {
            //give an error
            window.alert("Tuhr before first dam is less than 15 days, so we will need previous information to solve this masla")
            return AllTheInputs(null)
        }
    }
    val newPreMaslaValues = PreMaslaValues(
        allTheOriginalInputs.preMaslaValues.inputtedAadatHaiz,
        allTheOriginalInputs.preMaslaValues.inputtedAadatTuhr,
        mawjodahtuhreditable,
        isMawjoodaFasid
    )
    var newPregnancy: Pregnancy? = null
    if (allTheOriginalInputs.pregnancy != null) {
        newPregnancy = Pregnancy(
            pregnancyStrt,
            pregnancyEnd,
            allTheOriginalInputs.pregnancy.aadatNifas,
            allTheOriginalInputs.pregnancy.mustabeenUlKhilqat
        )
    }
    return AllTheInputs(
        entries,
        newPreMaslaValues,
        allTheOriginalInputs.typeOfMasla,
        newPregnancy,
        TypesOfInputs.DURATION,
        allTheOriginalInputs.language,
        allTheOriginalInputs.ikhtilaafaat
    )
}

fun compareResults() {
    val listOfLists = inputsContainers.map { it.haizDatesList!! }
    val listOfDescriptions = inputsContainers.map { it.saailaDetails }
    val output = generatInfoForCompareTable(listOfLists.toMutableList())
    drawCompareTable(output.headerList, output.listOfColorsOfDaysList, output.resultColors, listOfDescriptions)
}

fun drawCompareTable(
    headerList: List<Instant>,
    listOfColorsOfDaysList: List<List<Int>>,
    resultColors: List<Int>,
    listOfDescriptions: List<String>
) {
    val comparisonGrid = comparisonGridElement!!
    comparisonGrid.classList.replace(CssC.COLUMN, CssC.GRID)
    comparisonGrid.style.setProperty("--columns", "${headerList.size}")
    comparisonGrid.style.setProperty("--rows", "${listOfDescriptions.size - 1}")
    comparisonGrid.replaceChildren {
        val lang = languageSelected
        val dur = inputsContainers.first().isDuration
        val titleClasses = "${CssC.TITLE_CELL} ${lang}-align ${if (dur) CssC.HIDDEN else ""}"

        // Month Row
        oneRow(true, "", false) {
            for (header in headerList) {
                val headerDate = LocalDate.ofInstant(header)
                val date = headerDate.dayOfMonth()
                div(classes = "${CssC.MONTHS_ROW} ${CssC.TABLE_CELL} $titleClasses") {
                    if (date == 1) {
                        makeSpans(
                            englishMonthNames[headerDate.monthValue().toInt() - 1],
                            mmEnglishMonthNames[headerDate.monthValue().toInt() - 1],
                            urduMonthNames[headerDate.monthValue().toInt() - 1]
                        )
                    }
                }
            }
        }

        // Date Row
        oneRow(true, "", false) {
            for (i in headerList.indices) {
                val header = headerList[i]
                val date = LocalDate.ofInstant(header).dayOfMonth().toString()

                div(classes = "${CssC.DATES_ROW} ${CssC.TABLE_CELL} $titleClasses") {
                    +date
                }
            }
        }

        // Conclusion Empty Colored Row
        oneRow(true, "", true) {
            for (day in resultColors) {
                div {
                    classes = setOf(
                        CssC.HALF_TABLE_CELL, when (day) {
                            1 -> CssC.AYYAM_E_SHAKK
                            2 -> CssC.NA_PAAKI
                            else -> CssC.EMPTY_TABLE_CELL
                        }
                    )
                }
            }
        }

        // One Row Each For Each Clone
        for (j in listOfColorsOfDaysList.indices) {
            val colorsOfDaysList = listOfColorsOfDaysList[j]
            val titleDescriptionOfList = listOfDescriptions[j]

            oneRow(true, titleDescriptionOfList, true) {
                for (k in colorsOfDaysList.indices) {
                    val cellValue = colorsOfDaysList[k]
                    div {
                        classes = setOf(
                            CssC.TABLE_CELL, CssC.BORDERED, if (cellValue == 1) CssC.NA_PAAKI else CssC.EMPTY_TABLE_CELL
                        )
                        +"${k + 1}"
                    }
                }
            }
        }
    }

    GlobalScope.launch {
        val canvas = comparisonGrid.toCanvas()
        comparisonGrid.replaceChildren(canvas)
        comparisonGrid.classList.replace(CssC.GRID, CssC.COLUMN)
        comparisonGrid.appendChild {
            a(href = canvas.toDataURL()) { +"Download Table" }.asDynamic().download = "hazapp_comparison_table.png"
        }
    }
}

// VALS
const val IS_DEFAULT_INPUT_MODE_DATE_ONLY = true
const val IS_DEFAULT_INPUT_MODE_MUTADA = true

private val inputsContainersContainer get() = document.getElementById(Ids.InputContainers.INPUT_CONTAINERS_CONTAINER) as HTMLElement

@Suppress("UNCHECKED_CAST")
val inputsContainers get() = inputsContainersContainer.children.asList() as List<HTMLElement>

val rootHazapp = document.getElementById("root")
val devmode = window.location.href.contains("dev")
val languageSelector get() = document.getElementById(Ids.LANGUAGE) as HTMLSelectElement
val languageSelected get() = languageSelector.value
val logoutDiv get() = document.getElementById(Ids.LoginLogout.LOGOUT_DIV) as HTMLDivElement
private val comparisonGridElement get() = document.getElementById(Ids.Results.DATES_DIFFERENCE_TABLE) as HTMLElement?

val HTMLElement.typeSelect get() = getChildById(Ids.Inputs.INPUT_TYPE_SELECT) as HTMLSelectElement
val HTMLElement.isDateTime get() = typeSelect.value == Vls.Types.DATE_TIME
val HTMLElement.isDateOnly get() = typeSelect.value == Vls.Types.DATE_ONLY
val HTMLElement.isDuration get() = typeSelect.value == Vls.Types.DURATION

val HTMLElement.disableTimeZone get() = getChildById(Ids.Inputs.IS_DAYLIGHT_SAVINGS) as HTMLInputElement
val HTMLElement.timezoneSelect get() = getChildById(Ids.Inputs.SELECT_LOCALE) as HTMLSelectElement

val HTMLElement.maslaSelect get() = getChildById(Ids.Inputs.MASLA_TYPE_SELECT) as HTMLSelectElement
val HTMLElement.isMutada get() = maslaSelect.value == Vls.Maslas.MUTADA
val HTMLElement.isNifas get() = maslaSelect.value == Vls.Maslas.NIFAS
val HTMLElement.isMubtadia get() = maslaSelect.value == Vls.Maslas.MUBTADIA

val HTMLElement.pregStartTime get() = getChildById(Ids.Inputs.PREG_START_TIME_INPUT) as HTMLInputElement
val HTMLElement.pregEndTime get() = getChildById(Ids.Inputs.PREG_END_TIME_INPUT) as HTMLInputElement

val HTMLElement.aadatHaz get() = getChildById(Ids.Inputs.AADAT_HAIZ_INPUT) as HTMLInputElement
val HTMLElement.aadatTuhr get() = getChildById(Ids.Inputs.AADAT_TUHR_INPUT) as HTMLInputElement
val HTMLElement.mawjoodaTuhr get() = getChildById(Ids.Inputs.MAWJOODA_TUHR_INPUT) as HTMLInputElement
val HTMLElement.aadatNifas get() = getChildById(Ids.Inputs.AADAT_NIFAS_INPUT) as HTMLInputElement
val HTMLElement.cycleLength get() = getChildById(Ids.Inputs.ZAALLA_CYCLE_LENGTH) as HTMLInputElement

val HTMLElement.isZaalla get() = (getChildById(Ids.Inputs.ZAALLA_CHECKBOX) as HTMLInputElement).checked
val HTMLElement.isMustabeenInput get() = (getChildById(Ids.Inputs.MUSTABEEN_CHECKBOX) as HTMLInputElement)
val HTMLElement.isMustabeen get() = isMustabeenInput.checked
val HTMLElement.isMawjoodaFasidInput get() = (getChildById(Ids.Inputs.MAWJOODA_FAASID_CHECKBOX) as HTMLInputElement)
val HTMLElement.isMawjoodaFasid get() = isMawjoodaFasidInput.checked

val HTMLElement.contentContainer get() = (getChildById(Ids.Results.CONTENT_CONTAINER)!!) as HTMLDivElement
val HTMLElement.contentEnglish get() = getChildById(Ids.Results.CONTENT_ENGLISH) as HTMLParagraphElement
val HTMLElement.contentMMEnglish get() = getChildById(Ids.Results.CONTENT_MMENGLISH) as HTMLParagraphElement
val HTMLElement.contentUrdu get() = getChildById(Ids.Results.CONTENT_URDU) as HTMLParagraphElement
private val HTMLElement.contentDatesElement get() = getChildById(Ids.Results.CONTENT_DATES) as HTMLParagraphElement
val HTMLElement.tooltip get() = getChildByClass(Ids.Results.COPY_TOOLTIP) as HTMLElement

val HTMLElement.ikhtilaf1Input get() = (getChildById(Ids.Ikhtilafat.IKHTILAF1) as HTMLInputElement)
val HTMLElement.ikhtilaf1 get() = ikhtilaf1Input.checked
val HTMLElement.ikhtilaf2Input get() = (getChildById(Ids.Ikhtilafat.IKHTILAF2) as HTMLInputElement)
val HTMLElement.ikhtilaf2 get() = ikhtilaf2Input.checked
val HTMLElement.ikhtilaf3Input get() = (getChildById(Ids.Ikhtilafat.IKHTILAF3) as HTMLInputElement)
val HTMLElement.ikhtilaf3 get() = ikhtilaf3Input.checked
val HTMLElement.ikhtilaf4Input get() = (getChildById(Ids.Ikhtilafat.IKHTILAF4) as HTMLInputElement)
val HTMLElement.ikhtilaf4 get() = ikhtilaf4Input.checked
val HTMLElement.addNow get() = (getChildById(Ids.Inputs.NOW_CHECKBOX) as HTMLInputElement).checked


private var HTMLElement.haizDatesList: List<Entry>?
    get() = (contentDatesElement.asDynamic().haizDatesList as List<Entry>?)?.takeIf { it != undefined }
    set(value) {
        contentDatesElement.asDynamic().haizDatesList = value
    }

private val englishElements get() = document.getElementsByClassName(CssC.ENGLISH).asList()
private val mmenglishElements get() = document.getElementsByClassName(CssC.MMENGLISH).asList()
private val urduElements get() = document.getElementsByClassName(CssC.URDU).asList()
private val languageElements get() = listOf(englishElements, urduElements, mmenglishElements).flatten()
private val devElements get() = document.getElementsByClassName(CssC.DEV).asList()
val collapsingElements get() = document.getElementsByClassName(CssC.COLLAPSIBLE).asList()

val HTMLElement.hazInputTableBody get() = haizInputTable.tBodies[0] as HTMLTableSectionElement
val HTMLElement.hazDurationInputTableBody get() = haizDurationInputTable.tBodies[0] as HTMLTableSectionElement

@Suppress("UNCHECKED_CAST")
val HTMLElement.haizInputDatesRows get() = hazInputTableBody.rows.asList() as List<HTMLTableRowElement>

@Suppress("UNCHECKED_CAST")
val HTMLElement.haizDurationInputDatesRows get() = hazDurationInputTableBody.rows.asList() as List<HTMLTableRowElement>

val HTMLTableRowElement.startTimeInput get() = getChildById(Ids.Row.INPUT_START_TIME) as HTMLInputElement
val HTMLTableRowElement.endTimeInput get() = getChildById(Ids.Row.INPUT_END_TIME) as HTMLInputElement
val HTMLTableRowElement.durationInput get() = getChildById(Ids.Row.INPUT_DURATION) as HTMLInputElement
private val HTMLTableRowElement.durationTypeInput get() = getChildById(Ids.Row.INPUT_TYPE_OF_DURATION) as HTMLSelectElement
val HTMLTableRowElement.damOrTuhr get() = durationTypeInput.value

val HTMLElement.haizTimeInputs
    get() = haizInputDatesRows.flatMap { row ->
        listOf(row.startTimeInput, row.endTimeInput)
    }
private val HTMLElement.haizDurationInputs
    get() = haizDurationInputDatesRows.flatMap { row ->
        listOf(row.durationInput, row.durationTypeInput)
    }

val HTMLElement.timeInputsGroups get() = listOf(listOf(pregStartTime, pregEndTime), haizTimeInputs)
val HTMLElement.durationInputsGroups get() = listOf(haizDurationInputs)
