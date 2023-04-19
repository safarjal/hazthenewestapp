import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSelectElement

private val languageSelecter get() = document.getElementById("language") as HTMLSelectElement

private val addCalcsDateToAddTo get() = (document.getElementById(Ids.AddTimeToDate.DATE_TO_ADD_TO) as HTMLInputElement)
private val addCalcsDurationToAdd get() = (document.getElementById(Ids.AddTimeToDate.TIME_TO_ADD) as HTMLInputElement)
private val addCalcsOutputDate get() = document.getElementById(Ids.AddTimeToDate.OUTOUT_FIELD) as HTMLDivElement
private val addCalcsIsDateOnlyAddTimeToDate get() = (document.getElementById("add_time_to_date_only_date") as HTMLInputElement)
private val addCalcsIsDateTimeAddTimeToDate get() = (document.getElementById("add_time_to_date_time_date") as HTMLInputElement)
private val addCalcsButtonAddTimeToDate get() = (document.getElementById("add_time_to_date_button") as HTMLInputElement)

private val addCalcsStrtDate get() = (document.getElementById(Ids.CalcDuration.STRT_DATE) as HTMLInputElement)
private val addCalcsEndDate get() = (document.getElementById(Ids.CalcDuration.END_DATE) as HTMLInputElement)
private val addCalcsOutputDuration get() = document.getElementById(Ids.CalcDuration.OUTPUT_FIELD) as HTMLDivElement
private val addCalcsIsDateOnlyGetDuration get() = (document.getElementById("get_duration_only_date") as HTMLInputElement)
private val addCalcsIsDateTimeGetDuration get() = (document.getElementById("get_duration_time_date") as HTMLInputElement)
private val addCalcsButtonGetDuration get() = (document.getElementById("get_duration_button") as HTMLInputElement)

fun mainOtherCalcs(){
    addListeners()
}

// TODO: TEST THIS!!!
fun addCalcsGetDuration(){
    val isDateOnly:Boolean = addCalcsIsDateOnlyGetDuration.checked
    val startDate = addCalcsStrtDate.value.instant()
    val endDate = addCalcsEndDate.value.instant()
    var strUrdu = ""
    var strEnglish = ""
    val typesOfInputs:TypesOfInputs
    if(isDateOnly){typesOfInputs=TypesOfInputs.DATE_ONLY}
    else{typesOfInputs=TypesOfInputs.DATE_AND_TIME}

    if(startDate != null && endDate != null && startDate.isBefore(endDate)){
        val result = (endDate.getMillisLong()-startDate.getMillisLong())
        strUrdu = daysHoursMinutesDigital(result, typesOfInputs, Vls.Langs.URDU)
        strEnglish = daysHoursMinutesDigital(result, typesOfInputs, Vls.Langs.ENGLISH)
    }
    val resultStrings =  OutputStringsLanguages(strUrdu,strEnglish)
    if(languageSelecter.value==Vls.Langs.URDU){
        addCalcsOutputDuration.innerHTML = resultStrings.urduString
    }else if(languageSelecter.value==Vls.Langs.ENGLISH){
        addCalcsOutputDuration.innerHTML = resultStrings.englishString
    }
}


fun addListeners(){
    addCalcsDateToAddTo.onchange = { addCalcsAddTimeToDate()}
//    ToDO: Fix this commented porion
//    addCalcsDurationToAdd.oninput = { event -> (event.currentTarget as HTMLInputElement).validateAadat(0..10000) }

//        addCalcsAddTimeToDate()
//    }
    addCalcsDurationToAdd.onchange = { addCalcsAddTimeToDate()}
    addCalcsStrtDate.onchange = {addCalcsGetDuration()}
    addCalcsEndDate.onchange = {addCalcsGetDuration()}
    addCalcsIsDateOnlyAddTimeToDate.onchange = {switchDateTime("add-time-to-date")}
    addCalcsIsDateTimeAddTimeToDate.onchange = {switchDateTime("add-time-to-date")}
    addCalcsIsDateOnlyGetDuration.onchange = {switchDateTime("calculate-duration")}
    addCalcsIsDateTimeGetDuration.onchange = {switchDateTime("calculate-duration")}
    addCalcsButtonAddTimeToDate.onclick = {addTimeToDateButtonClick()}
    addCalcsButtonGetDuration.onclick = {getDurationButtonClick()}
}

fun addCalcsAddTimeToDate() {
    val isDateOnly: Boolean = addCalcsIsDateOnlyAddTimeToDate.checked
    val typesOfInputs: TypesOfInputs
    if (isDateOnly) {
        typesOfInputs = TypesOfInputs.DATE_ONLY
    } else {
        typesOfInputs = TypesOfInputs.DATE_AND_TIME
        val date = addCalcsDateToAddTo.value.instant()
        val duration = parseDays(addCalcsDurationToAdd.value)
        var strResultUrdu = ""
        var strResultEnglish = ""
        if (duration != null && date != null) {
            val result = addTimeToDate(date, duration)
            strResultEnglish = languagedDateFormat(result, typesOfInputs, Vls.Langs.ENGLISH)
            strResultUrdu = languagedDateFormat(result, typesOfInputs, Vls.Langs.URDU)
        }
        val resultStrings = OutputStringsLanguages(strResultUrdu, strResultEnglish)
        if (languageSelecter.value == Vls.Langs.URDU) {
            addCalcsOutputDate.innerHTML = resultStrings.urduString
        } else if (languageSelecter.value == Vls.Langs.ENGLISH) {
            addCalcsOutputDate.innerHTML = resultStrings.englishString
        }
    }
}
fun switchDateTime(calcType:String){
    println("got here")
    if(calcType=="calculate-duration"){
        if(addCalcsIsDateOnlyGetDuration.checked){
            addCalcsStrtDate.type = "date"
            addCalcsEndDate.type = "date"
        }else{
            addCalcsStrtDate.type = "datetime-local"
            addCalcsEndDate.type = "datetime-local"
        }
    }else if(calcType=="add-time-to-date"){
        if(addCalcsIsDateOnlyAddTimeToDate.checked){
            addCalcsDateToAddTo.type = "date"
        }else{
            addCalcsDateToAddTo.type = "datetime-local"
        }
    }
}

fun getDurationButtonClick(){
    addCalcsStrtDate.value = addCalcsEndDate.value
    addCalcsGetDuration()
}

fun addTimeToDateButtonClick(){
    if(addCalcsDurationToAdd != null && addCalcsDateToAddTo.valueAsNumber != null){
        val result = addTimeToDate(addCalcsDateToAddTo.value.instant(), parseDays(addCalcsDurationToAdd.value)!!)
        addCalcsDateToAddTo.value=result.toDateInputString(addCalcsIsDateOnlyAddTimeToDate.checked)
        addCalcsDurationToAdd.value=""
        addCalcsAddTimeToDate()
    }
}