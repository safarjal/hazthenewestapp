@file:Suppress("SpellCheckingInspection")

import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.*
import kotlinx.html.js.*
import org.w3c.dom.*
import kotlin.js.Date

// START PROGRAM
fun main() {
    window.onload = {
        if (root_hazapp.isNotEmpty() && askPassword()) {    // Hazapp Page
            document.body!!.addInputLayout()
            setupRows(inputsContainers.first())
            document.addEventListener(Events.VISIBILITY_CHANGE, {
                if (!document.isHidden) {
                    setMaxToCurrentTimeForTimeInputs(inputsContainers.first())
                }
            })
        } else mainOtherCalcs()                             // Other Calcs Page

        parseHREF()
    }
}

private fun askPassword(): Boolean {
    val pass1 = "786"
    val password = window.prompt("${StringsOfLanguages.ENGLISH.warningOnlyAuthorizedPersonnel}\n\n" +
            "${StringsOfLanguages.URDU.warningOnlyAuthorizedPersonnel}\n\n" +
            "${StringsOfLanguages.URDU.passwordRequired}\n\n", "")
    return pass1 == password || askPassword()
}

private fun parseHREF() {
    // DEVMODE
    for (element in devElements) element.visibility = window.location.href.contains("dev")

    // DEFAULT LANGUAGE
    languageSelector.onchange = { languageChange() }
    if (window.location.href.contains("lang=en")) languageSelector.value = Vls.Langs.ENGLISH
    languageChange()
}

fun languageChange() {
    val lang = languageSelector.value
    // Invis every language dependent element based on if it DOESN'T have the selected language: // TODO: Make better.
    for (element in languageElements) element.classList.toggle(CssC.LANG_INVIS, !element.classList.contains(lang))

    document.body!!.classList.toggle(CssC.RTL, lang == Vls.Langs.URDU) // RTL on body if Urdu
    document.querySelectorAll("select") // Selected options don't invis so switch them.
        .asList()
        .map { it as HTMLSelectElement }
        .forEach { select ->
            select.children
                .asList()
                .map { it as HTMLOptionElement }
                // Same val as selected option, different lang:
                .firstOrNull { option -> option.value == select.value && option.classList.contains(lang) }
                ?.selected = true
        }
    if (datesDifferenceGridElement?.children?.asList()?.isNotEmpty() == true) {
        datesDifferenceGridElement?.getElementsByClassName(CssC.TITLE_CELL)?.asList()?.forEach {
            it.classList.toggle("${Vls.Langs.ENGLISH}-align", lang == Vls.Langs.ENGLISH)
            it.classList.toggle("${Vls.Langs.URDU}-align", lang == Vls.Langs.URDU)
        }
    }
}

fun makeRangeArray(aadatHaz:String,aadatTuhr:String):MutableList<AadatsOfHaizAndTuhr>{
    val aadatHaizList = mutableListOf<Int>()
    val haizStart = parseRange(aadatHaz)[0]
    val haizEnd = parseRange(aadatHaz)[1]
    if(haizStart!=null && haizEnd!=null){
        for (i in haizStart .. haizEnd){
            aadatHaizList += i
        }
    }
    val aadatTuhrList = mutableListOf<Int>()
    val tuhrStart = parseRange(aadatTuhr)[0]
    val tuhrEnd = parseRange(aadatTuhr)[1]
    if(tuhrStart!=null && tuhrEnd!=null){
        for (i in tuhrStart .. tuhrEnd){
            aadatTuhrList += i
        }
    }
    if(!aadatTuhr.contains('-')){
        aadatTuhrList+= aadatTuhr.toInt()
    }
    if(!aadatHaz.contains(('-'))){
        aadatHaizList+= aadatHaz.toInt()
    }

    val combosToTry = mutableListOf<AadatsOfHaizAndTuhr>()
    for(tuhrAadat in aadatTuhrList){
        for (aadatHaiz in aadatHaizList){
            combosToTry+=AadatsOfHaizAndTuhr(aadatHaiz*MILLISECONDS_IN_A_DAY,tuhrAadat*MILLISECONDS_IN_A_DAY)
        }
    }
    return combosToTry
}

fun parseEntries(inputContainer: HTMLElement) {
    var entries = listOf<Entry>()

    with(inputContainer) {
        val pregnancyStrt = Date(pregStartTime.valueAsNumber)
        val pregnancyEnd = Date(pregEndTime.valueAsNumber)

        val typeOfMasla:TypesOfMasla = if(isMubtadia){
            TypesOfMasla.MUBTADIA
        } else if(isNifas){
            TypesOfMasla.NIFAS
        } else{
            TypesOfMasla.MUTADAH
        }
        val typesOfInputs:TypesOfInputs = if(isDateOnly){
            TypesOfInputs.DATE_ONLY
        } else if(isDuration){
            TypesOfInputs.DURATION
        }else{TypesOfInputs.DATE_AND_TIME}

        val preMaslaValues = PreMaslaValues(
            parseDays(aadatHaz.value),
            parseDays(aadatTuhr.value),
            parseDays(mawjoodaTuhr.value),
            isMawjoodaFasid
        )

        val ikhtilaafaat = Ikhtilaafaat(
            ikhtilaf1,
            ikhtilaf2,
            ikhtilaf3,
            ikhtilaf4)

        val pregnancy = Pregnancy(
            pregnancyStrt,
            pregnancyEnd,
            parseDays(aadatNifas.value),
            isMustabeen
        )

        var allTheInputs=AllTheInputs()

        if(typesOfInputs==TypesOfInputs.DURATION){
            val durations = haizDurationInputDatesRows.map { row ->
                Duration(
                    type = when (row.damOrTuhr) {
                        Vls.Opts.DAM -> {DurationType.DAM}
                        Vls.Opts.TUHR -> {DurationType.TUHR}
                        Vls.Opts.HAML -> {DurationType.HAML}
                        Vls.Opts.WILADAT -> {DurationType.WILADAT_ISQAT}
                        else -> {DurationType.NIFAS}
                    },
                    timeInMilliseconds = parseDays(row.durationInput.value)!!
                ) }
            allTheInputs = AllTheInputs(
                entries,
                preMaslaValues,
                typeOfMasla,
                pregnancy,
                typesOfInputs,
                languageSelector.value,
                ikhtilaafaat)
            allTheInputs = convertDurationsIntoEntries(durations, allTheInputs)
        }else{
            entries = haizInputDatesRows.map { row ->
                Entry(
                    startTime = Date(row.startTimeInput.valueAsNumber),
                    endTime = Date(row.endTimeInput.valueAsNumber)
                )
            }
            allTheInputs = AllTheInputs(
                entries,
                preMaslaValues,
                typeOfMasla,
                pregnancy,
                typesOfInputs,
                languageSelector.value,
                ikhtilaafaat)
        }

        if(aadatHaz.value.contains("-")||
            aadatTuhr.value.contains("-")||
            aadatNifas.value.contains("-")){
            contentContainer.visibility = false
            handleRangedInput(allTheInputs, aadatHaz.value, aadatTuhr.value)
            return
        }

        @Suppress("UnsafeCastFromDynamic")
        var output:OutputTexts
        if(allTheInputs.entries!=null){
            output = handleEntries(allTheInputs)
        }else{
            output = NO_OUTPUT
        }
        contentContainer.visibility = true
        contentEnglish.innerHTML = replaceBoldTagWithBoldAndStar(output.englishText)
        contentUrdu.innerHTML = replaceBoldTagWithBoldAndStar(output.urduText)
        haizDatesList = output.hazDatesList
        populateTitleFieldIfEmpty(inputContainer, aadatHaz.value, aadatTuhr.value, mawjoodaTuhr.value)
    }
}
private fun handleRangedInput(allTheInputs: AllTheInputs, aadatHaz: String, aadatTuhr: String) {
    val combosToTry = makeRangeArray(aadatHaz, aadatTuhr)
    val listOfLists = mutableListOf<MutableList<Entry>>()
    val listOfDescriptions = mutableListOf<String>()
    for (aadatCombo in combosToTry){
        allTheInputs.preMaslaValues.inputtedAadatTuhr=aadatCombo.aadatTuhr
        allTheInputs.preMaslaValues.inputtedAadatHaiz=aadatCombo.aadatHaiz
        val output = handleEntries(allTheInputs)
        if (output == NO_OUTPUT) return
        listOfLists+=output.hazDatesList
        listOfDescriptions += "${(aadatCombo.aadatHaiz/MILLISECONDS_IN_A_DAY)}/${(aadatCombo.aadatTuhr/MILLISECONDS_IN_A_DAY)}"
    }
    val output = generatInfoForCompareTable(listOfLists.toMutableList())
    drawCompareTable(output.headerList,output.listOfColorsOfDaysList, output.resultColors, listOfDescriptions)

}
fun populateTitleFieldIfEmpty(inputContainer: HTMLElement, aadatHaz:String, aadatTuhr:String, mawjoodaTuhr:String) {
    with(inputContainer) {
        if(descriptionText.value==""){
            var text = "$aadatHaz/$aadatTuhr/$mawjoodaTuhr".trim()
            if(text.contains("//")){text=text.replace("//","/")}
            if(text.startsWith("/")){text=text.drop(1)}
            if(text.endsWith("/")){ text=text.dropLast(1) }
            descriptionText.value = text
        }
    }
}

fun validateNifasDurations(durations:List<Duration>):Boolean{
    //this is ensuring that we have both pregnancy and birth, and only one of each.

    //I am wondering if, if preg or birth, or both are missing, we can just arbitrarily add them to the start of the masla.
    //it seems possible, but idk if that is what we want.

    var pregnancy=false
    var wiladatIsqat=false
    for(duration in durations){
        if(duration.type==DurationType.HAML){
            if(pregnancy){
                window.alert("You can only solve one pregnancy per masla")
                return false
            }else{//is false
                pregnancy=true
            }
        }else if(duration.type==DurationType.WILADAT_ISQAT){
            if(wiladatIsqat){
                window.alert("You can only solve one birth at a time")
                return false
            }else if(!pregnancy){
                window.alert("Please add pregnancy before birth")
                return false
            }else{//is false
                wiladatIsqat=true
            }
        }
    }
    if(!pregnancy||!wiladatIsqat){
        window.alert("You need to add pregnancy and birth/miscarriage to solve a nifas question.")
        return false
    }
    return true
}

fun convertDurationsIntoEntries(durations:List<Duration>, allTheOriginalInputs: AllTheInputs = AllTheInputs(null)):AllTheInputs{
    if(allTheOriginalInputs.typeOfMasla==TypesOfMasla.NIFAS){
        if(!validateNifasDurations(durations)){
            return AllTheInputs(null)
        }
    }
    for (index in durations.indices){
        if(index > 0){
            durations[index].startTime = durations[index-1].endDate
        }
    }
    var mawjodahtuhreditable:Long?= allTheOriginalInputs.preMaslaValues.inputtedMawjoodahTuhr
    var isMawjoodaFasid = allTheOriginalInputs.preMaslaValues.isMawjoodaFasid
    val entries= mutableListOf<Entry>()
    var pregnancyEnd = ARBITRARY_DATE
    var pregnancyStrt:Date = ARBITRARY_DATE

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



    for(dur in durations){
        when (dur.type) {
            DurationType.DAM -> {
                entries += Entry(dur.startTime, dur.endDate)
            }
            DurationType.HAML -> {
                pregnancyStrt=dur.startTime
                if(entries.size==0){
                    isMawjoodaFasid=true
                }

            }
            DurationType.WILADAT_ISQAT -> {
                pregnancyEnd=dur.startTime
            }
            DurationType.TUHR -> {
                if(entries.size==0){
                    if(mawjodahtuhreditable==null){
                        mawjodahtuhreditable = dur.timeInMilliseconds
                    }else{
                        mawjodahtuhreditable += dur.timeInMilliseconds
                    }
                }
            }
        }
    }
    if (mawjodahtuhreditable != null) {
        if(mawjodahtuhreditable<15*MILLISECONDS_IN_A_DAY && mawjodahtuhreditable!=-1L){
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
    var newPregnancy:Pregnancy? = null
    if(allTheOriginalInputs.pregnancy!=null){
        newPregnancy= Pregnancy(
            pregnancyStrt,
            pregnancyEnd,
            allTheOriginalInputs.pregnancy.aadatNifas,
            allTheOriginalInputs.pregnancy.mustabeenUlKhilqat
        )
    }
    return AllTheInputs(entries,
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
    val listOfDescriptions = inputsContainers.map { it.descriptionText.value }
    val output = generatInfoForCompareTable(listOfLists.toMutableList())
    drawCompareTable(output.headerList,output.listOfColorsOfDaysList, output.resultColors, listOfDescriptions)
}

fun drawCompareTable(
    headerList:List<Date>,
    listOfColorsOfDaysList: List<List<Int>>,
    resultColors: List<Int>,
    listOfDescriptions: List<String>
){
    val datesDifferenceTableElement = datesDifferenceGridElement!!
    datesDifferenceTableElement.style.setProperty("--columns",  "${headerList.size}")
    datesDifferenceTableElement.style.setProperty("--rows",  "${listOfDescriptions.size - 1}")
    datesDifferenceTableElement.replaceChildren {
        val lang = languageSelector.value
        val dur = inputsContainers.first().isDuration
        val titleClasses = "${CssC.TITLE_CELL} ${lang}-align ${if (dur) CssC.HIDDEN else ""}"

        // Month Row
        oneRow(true, "", false) {
            for (header in headerList) {
                val date = header.getDate()
                div(classes = "${CssC.MONTHS_ROW} ${CssC.TABLE_CELL} $titleClasses") {
                    if (date == 1) {
                        makeSpans(englishMonthNames[header.getMonth()], urduMonthNames[header.getMonth()])
                    }
                }
            }
        }

        // Date Row
        oneRow(true, "", false) {
            for (i in headerList.indices) {
                val header = headerList[i]
                val date = header.getDate().toString()

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
                        CssC.ENPTY_TABLE_CELL,
                        when (day) {
                            1 -> CssC.AYYAM_E_SHAKK
                            2 -> CssC.NA_PAAKI
                            else -> ""
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
                            CssC.TABLE_CELL,
                            CssC.BORDERED,
                            if (cellValue == 1) CssC.NA_PAAKI else ""
                        )
                        +"${k + 1}"
                    }
                }
            }
        }
    }

    html2canvas(datesDifferenceGridElement!!).then { canvas ->
        datesDifferenceGridElement!!.after(canvas)
    }
}

// VALS
const val IS_DEFAULT_INPUT_MODE_DATE_ONLY = true
const val IS_DEFAULT_INPUT_MODE_MUTADA = true

private val inputsContainersContainer get() = document.getElementById(Ids.INPUT_CONTAINERS_CONTAINER) as HTMLElement
@Suppress("UNCHECKED_CAST")
val inputsContainers get() = inputsContainersContainer.children.asList() as List<HTMLElement>

val languageSelector get() = document.getElementById(Ids.LANGUAGE) as HTMLSelectElement

private val datesDifferenceGridElement get() = document.getElementById(Ids.DATES_DIFFERENCE_TABLE) as HTMLElement?
private val root_hazapp = document.getElementsByClassName("root").asList()

val HTMLElement.typeSelect get() = getChildById(Ids.Inputs.INPUT_TYPE_SELECT) as HTMLSelectElement
val HTMLElement.isDateTime get() = typeSelect.value == Vls.Types.DATE_TIME
val HTMLElement.isDateOnly get() = typeSelect.value == Vls.Types.DATE_ONLY
val HTMLElement.isDuration get() = typeSelect.value == Vls.Types.DURATION

val HTMLElement.isMutada get() = (getChildById(Ids.Inputs.MASLA_TYPE_SELECT) as HTMLSelectElement).value == Vls.Maslas.MUTADA
val HTMLElement.isNifas get() = (getChildById(Ids.Inputs.MASLA_TYPE_SELECT) as HTMLSelectElement).value == Vls.Maslas.NIFAS
val HTMLElement.isMubtadia get() = (getChildById(Ids.Inputs.MASLA_TYPE_SELECT) as HTMLSelectElement).value == Vls.Maslas.MUBTADIA

val HTMLElement.pregStartTime get() = getChildById(Ids.Inputs.PREG_START_TIME_INPUT) as HTMLInputElement
val HTMLElement.pregEndTime get() = getChildById(Ids.Inputs.PREG_END_TIME_INPUT) as HTMLInputElement

val HTMLElement.aadatHaz get() = getChildById(Ids.Inputs.AADAT_HAIZ_INPUT) as HTMLInputElement
val HTMLElement.aadatTuhr get() = getChildById(Ids.Inputs.AADAT_TUHR_INPUT) as HTMLInputElement
val HTMLElement.mawjoodaTuhr get() = getChildById(Ids.Inputs.MAWJOODA_TUHR_INPUT) as HTMLInputElement
val HTMLElement.aadatNifas get() = getChildById(Ids.Inputs.AADAT_NIFAS_INPUT) as HTMLInputElement
val HTMLElement.cycleLength get() = getChildById(Ids.Inputs.ZAALLA_CYCLE_LENGTH) as HTMLInputElement

val HTMLElement.isZaalla get() = (getChildById(Ids.Inputs.ZAALLA_CHECKBOX) as HTMLInputElement).checked
val HTMLElement.isMustabeen get() = (getChildById(Ids.Inputs.MUSTABEEN_CHECKBOX) as HTMLInputElement).checked
val HTMLElement.isMawjoodaFasid get() = (getChildById(Ids.Inputs.MAWJOODA_FASID_CHECKBOX) as HTMLInputElement).checked

val HTMLElement.contentContainer get() = (getChildById(Ids.Results.CONTENT_CONTAINER)!!) as HTMLDivElement
private val HTMLElement.contentEnglish get() = getChildById(Ids.Results.CONTENT_ENGLISH) as HTMLParagraphElement
private val HTMLElement.contentUrdu get() = getChildById(Ids.Results.CONTENT_URDU) as HTMLParagraphElement
private val HTMLElement.contentDatesElement get() = getChildById(Ids.Results.CONTENT_DATES) as HTMLParagraphElement

private val HTMLElement.descriptionText get() = (getChildById(Ids.Inputs.INPUT_DESCRIPTION) as HTMLTextAreaElement)

private val HTMLElement.ikhtilaf1 get() = (getChildById(Ids.Ikhtilafat.IKHTILAF1) as HTMLInputElement).checked
private val HTMLElement.ikhtilaf2 get() = (getChildById(Ids.Ikhtilafat.IKHTILAF2) as HTMLInputElement).checked
private val HTMLElement.ikhtilaf3 get() = (getChildById(Ids.Ikhtilafat.IKHTILAF3) as HTMLInputElement).checked
private val HTMLElement.ikhtilaf4 get() = (getChildById(Ids.Ikhtilafat.IKHTILAF4) as HTMLInputElement).checked

private var HTMLElement.haizDatesList: List<Entry>?
    get() = (contentDatesElement.asDynamic().haizDatesList as List<Entry>?)?.takeIf { it != undefined }
    set(value) { contentDatesElement.asDynamic().haizDatesList = value }

private val englishElements get() = document.getElementsByClassName(CssC.ENGLISH).asList()
private val urduElements get() = document.getElementsByClassName(CssC.URDU).asList()
private val languageElements get() = listOf(englishElements, urduElements).flatten()
private val devElements get() = document.getElementsByClassName(CssC.DEV).asList()

val HTMLElement.hazInputTableBody: HTMLTableSectionElement get() {
    val inputDatesTable = getChildById(Ids.HAIZ_INPUT_TABLE) as HTMLTableElement
    return inputDatesTable.tBodies[0] as HTMLTableSectionElement
}
val HTMLElement.hazDurationInputTableBody: HTMLTableSectionElement get() {
    val inputDatesTable = getChildById(Ids.HAIZ_DURATION_INPUT_TABLE) as HTMLTableElement
    return inputDatesTable.tBodies[0] as HTMLTableSectionElement
}

val HTMLElement.haizInputDatesRows: List<HTMLTableRowElement> get() {
    @Suppress("UNCHECKED_CAST")
    return hazInputTableBody.rows.asList() as List<HTMLTableRowElement>
}
val HTMLElement.haizDurationInputDatesRows: List<HTMLTableRowElement> get() {
    @Suppress("UNCHECKED_CAST")
    return hazDurationInputTableBody.rows.asList() as List<HTMLTableRowElement>
}

val HTMLTableRowElement.startTimeInput get() = getChildById(Ids.Row.INPUT_START_TIME) as HTMLInputElement
val HTMLTableRowElement.endTimeInput get() = getChildById(Ids.Row.INPUT_END_TIME) as HTMLInputElement
val HTMLTableRowElement.durationInput get() = getChildById(Ids.Row.INPUT_DURATION) as HTMLInputElement
private val HTMLTableRowElement.durationTypeInput get() = getChildById(Ids.Row.INPUT_TYPE_OF_DURATION) as HTMLSelectElement
val HTMLTableRowElement.damOrTuhr get() = durationTypeInput.value

val HTMLElement.haizTimeInputs get() = haizInputDatesRows.flatMap { row ->
    listOf(row.startTimeInput, row.endTimeInput)
}
private val HTMLElement.haizDurationInputs get() = haizDurationInputDatesRows.flatMap { row ->
    listOf(row.durationInput, row.durationTypeInput)
}

val HTMLElement.timeInputsGroups get() = listOf(listOf(pregStartTime, pregEndTime), haizTimeInputs)
val HTMLElement.durationInputsGroups get() = listOf(haizDurationInputs)
