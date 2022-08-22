@file:Suppress("SpellCheckingInspection")
@file:OptIn(DelicateCoroutinesApi::class)

import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
    for (element in devElements) element.visibility = devmode

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
        .forEach { select -> setOptionInSelect(select) }
}

fun makeRangeArray(aadatHaz:String,aadatTuhr:String, cycleLength: String, aadatNifas: String):MutableList<AadatsOfHaizAndTuhr>{
    //this returns an array conating all the possibilities we want to plug in and try
    val combosToTry = mutableListOf<AadatsOfHaizAndTuhr>() //this is what we will output
    val aadatHaizList = mutableListOf<Int>() //this is all the haiz aadat possibilities. if none, then this contains -1
    if(aadatHaz!=""){
        val haizStart = parseRange(aadatHaz)[0]
        val haizEnd = parseRange(aadatHaz)[1]
        if(haizStart!=null && haizEnd!=null){
            for (i in haizStart .. haizEnd){
                aadatHaizList += i
            }
        }
    }else{
        aadatHaizList += -1
    }
    val aadatTuhrList = mutableListOf<Int>()
    if(aadatTuhr!=""){
        val tuhrStart = parseRange(aadatTuhr)[0]
        val tuhrEnd = parseRange(aadatTuhr)[1]
        if(tuhrStart!=null && tuhrEnd!=null){
            for (i in tuhrStart .. tuhrEnd){
                aadatTuhrList += i
            }
        }
    }else{
        aadatTuhrList += -1
    }

    val aadatNifasList = mutableListOf<Int>() //this is all the haiz aadat possibilities. if none, then this contains -1
    if(aadatNifas!=""){//nifas aadat isn't blank
        val nifasStart = parseRange(aadatNifas)[0] //parse range function splits it and returns the 2 values on either end of the dash
        val nifasEnd = parseRange(aadatNifas)[1]
        if(nifasStart!=null && nifasEnd!=null){
            for (i in nifasStart .. nifasEnd){
                aadatNifasList += i
            }
        }
    }else{
        aadatNifasList += -1
    }
    if(cycleLength==""){//there is no cycle length
        if(!aadatTuhr.contains('-') && aadatTuhr !=""){//if tuhr aadat doesn't contain a -, then just put the on tuhr aadat in array
            aadatTuhrList+= aadatTuhr.toInt()
        }
        if(!aadatHaz.contains(('-')) && aadatHaz != ""){//if haiz aadat doen't have -, then just enter that one habit
            aadatHaizList+= aadatHaz.toInt()
        }
        for(tuhrAadat in aadatTuhrList){
            for (aadatHaiz in aadatHaizList){
                combosToTry+=AadatsOfHaizAndTuhr(aadatHaiz*MILLISECONDS_IN_A_DAY,tuhrAadat*MILLISECONDS_IN_A_DAY)
            }
        }


    }else{//there is cycle length, and only one of haiz or tuhr, which is ranged
        if(aadatTuhrList[0]==-1){//there is no tuhr aadat  - haiz aadat is a range
            for(haizAadat in aadatHaizList){
                var tuhrAadat = parseDays(cycleLength)?.minus(haizAadat*MILLISECONDS_IN_A_DAY)
                if(tuhrAadat!=null && tuhrAadat>=15*MILLISECONDS_IN_A_DAY){
                    combosToTry+=AadatsOfHaizAndTuhr(haizAadat*MILLISECONDS_IN_A_DAY, tuhrAadat)
                }
            }

        }else if(aadatHaizList[0]==-1){//there is no haiz aadat - tuhr aadat is a range
            for(tuhrAadat in aadatTuhrList){//got through each tuhr aadat, figure out if it's composite haiz is a viablr haiz, if so, add it to the combos
                var haizAadat = parseDays(cycleLength)?.minus(tuhrAadat*MILLISECONDS_IN_A_DAY)
                if(haizAadat!=null && haizAadat>=3*MILLISECONDS_IN_A_DAY && haizAadat<=10*MILLISECONDS_IN_A_DAY){
                    combosToTry+=AadatsOfHaizAndTuhr(haizAadat, tuhrAadat*MILLISECONDS_IN_A_DAY)
                }
            }
        }


    }
    if(!aadatNifas.contains(('-')) && aadatNifas != ""){//if nifas aadat doen't have -, then just enter that one habit
        aadatNifasList+= aadatNifas.toInt()
    }
    if(aadatNifasList[0]!=-1){//we do have a nifas aadat
        val combosToTryWithNifas = mutableListOf<AadatsOfHaizAndTuhr>() //this is what we will output
        for (combo in combosToTry){
            for (nifasAadat in aadatNifasList){
                combosToTryWithNifas+=AadatsOfHaizAndTuhr(combo.aadatHaiz, combo.aadatTuhr, nifasAadat*MILLISECONDS_IN_A_DAY)
            }
        }
        return combosToTryWithNifas
    }else{//we don't have a nifas aadat
        return combosToTry
    }
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

        if((aadatHaz.value + aadatTuhr.value + aadatNifas.value).contains("-") && devmode){
            contentContainer.visibility = false
            handleRangedInput(allTheInputs, aadatHaz.value, aadatTuhr.value, cycleLength.value, aadatNifas.value)
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
private fun handleRangedInput(allTheInputs: AllTheInputs, aadatHaz: String, aadatTuhr: String, cycleLength:String, aadatNifas:String) {
    val combosToTry = makeRangeArray(aadatHaz, aadatTuhr, cycleLength, aadatNifas)
    val listOfLists = mutableListOf<MutableList<Entry>>()
    val listOfDescriptions = mutableListOf<String>()
    for (aadatCombo in combosToTry){ //go through combos and input them into logic and get their output
        if(aadatCombo.aadatTuhr==-1*MILLISECONDS_IN_A_DAY){
            allTheInputs.preMaslaValues.inputtedAadatTuhr=null
        }else{
            allTheInputs.preMaslaValues.inputtedAadatTuhr=aadatCombo.aadatTuhr
        }
        if(aadatCombo.aadatHaiz==-1*MILLISECONDS_IN_A_DAY){
            allTheInputs.preMaslaValues.inputtedAadatHaiz=null
        }else{
            allTheInputs.preMaslaValues.inputtedAadatHaiz=aadatCombo.aadatHaiz
        }
        if(aadatCombo.aadatNifas==-1*MILLISECONDS_IN_A_DAY){
            allTheInputs.pregnancy!!.aadatNifas=null
        }else{
            allTheInputs.pregnancy!!.aadatNifas=aadatCombo.aadatNifas
        }
        val output = handleEntries(allTheInputs)
        if (output == NO_OUTPUT) return //we gotta put this line so we don't keep on getting error messages every time it puts in a new value. gotta break at first error
        listOfLists+=output.hazDatesList

        //create a description for each combo
        if(aadatCombo.aadatNifas!=null && aadatCombo.aadatNifas!=-1*MILLISECONDS_IN_A_DAY){//aadat nifas exists
            if(aadatCombo.aadatHaiz==-1*MILLISECONDS_IN_A_DAY){
                listOfDescriptions += "(${(aadatCombo.aadatNifas!! /MILLISECONDS_IN_A_DAY)})/${(aadatCombo.aadatTuhr/MILLISECONDS_IN_A_DAY)}"
            }else if(aadatCombo.aadatTuhr==-1*MILLISECONDS_IN_A_DAY){
                listOfDescriptions += "(${(aadatCombo.aadatNifas!! /MILLISECONDS_IN_A_DAY)})/${(aadatCombo.aadatHaiz/MILLISECONDS_IN_A_DAY)}"
            }else{
                listOfDescriptions += "(${(aadatCombo.aadatNifas!! /MILLISECONDS_IN_A_DAY)})/${(aadatCombo.aadatHaiz/MILLISECONDS_IN_A_DAY)}/${(aadatCombo.aadatTuhr/MILLISECONDS_IN_A_DAY)}"
            }
        }else{//no nifas
            if(aadatCombo.aadatHaiz==-1*MILLISECONDS_IN_A_DAY){
                listOfDescriptions += "${(aadatCombo.aadatTuhr/MILLISECONDS_IN_A_DAY)}"
            }else if(aadatCombo.aadatTuhr==-1*MILLISECONDS_IN_A_DAY){
                listOfDescriptions += "${(aadatCombo.aadatHaiz/MILLISECONDS_IN_A_DAY)}"
            }else{
                listOfDescriptions += "${(aadatCombo.aadatHaiz/MILLISECONDS_IN_A_DAY)}/${(aadatCombo.aadatTuhr/MILLISECONDS_IN_A_DAY)}"
            }
        }
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
    val comparisonGrid = comparisonGridElement!!
    comparisonGrid.classList.replace(CssC.COLUMN, CssC.GRID)
    comparisonGrid.style.setProperty("--columns",  "${headerList.size}")
    comparisonGrid.style.setProperty("--rows",  "${listOfDescriptions.size - 1}")
    comparisonGrid.replaceChildren {
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
                        CssC.HALF_TABLE_CELL,
                        when (day) {
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
                            CssC.TABLE_CELL,
                            CssC.BORDERED,
                            if (cellValue == 1) CssC.NA_PAAKI else CssC.EMPTY_TABLE_CELL
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
            a(href = canvas.toDataURL()) { +"Download Table" }
                .asDynamic().download = "hazapp_comparison_table.png"
        }
    }
}

// VALS
const val IS_DEFAULT_INPUT_MODE_DATE_ONLY = true
const val IS_DEFAULT_INPUT_MODE_MUTADA = true

private val inputsContainersContainer get() = document.getElementById(Ids.InputContainers.INPUT_CONTAINERS_CONTAINER) as HTMLElement
@Suppress("UNCHECKED_CAST")
val inputsContainers get() = inputsContainersContainer.children.asList() as List<HTMLElement>

val languageSelector get() = document.getElementById(Ids.LANGUAGE) as HTMLSelectElement
private val root_hazapp = document.getElementsByClassName("root").asList()
val devmode = window.location.href.contains("dev")
private val comparisonGridElement get() = document.getElementById(Ids.Results.DATES_DIFFERENCE_TABLE) as HTMLElement?

val HTMLElement.typeSelect get() = getChildById(Ids.Inputs.INPUT_TYPE_SELECT) as HTMLSelectElement
val HTMLElement.isDateTime get() = typeSelect.value == Vls.Types.DATE_TIME
val HTMLElement.isDateOnly get() = typeSelect.value == Vls.Types.DATE_ONLY
val HTMLElement.isDuration get() = typeSelect.value == Vls.Types.DURATION

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
    val inputDatesTable = getChildById(Ids.InputTables.HAIZ_INPUT_TABLE) as HTMLTableElement
    return inputDatesTable.tBodies[0] as HTMLTableSectionElement
}
val HTMLElement.hazDurationInputTableBody: HTMLTableSectionElement get() {
    val inputDatesTable = getChildById(Ids.InputTables.HAIZ_DURATION_INPUT_TABLE) as HTMLTableElement
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
