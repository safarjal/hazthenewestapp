import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSelectElement
import kotlin.js.Date

private val languageSelecter get() = document.getElementById("language") as HTMLSelectElement

private val addCalcsDateToAddTo get() = (document.getElementById(Ids.AddTimeToDate.DATE_TO_ADD_TO) as HTMLInputElement)
private val addCalcsDurationToAdd get() = (document.getElementById(Ids.AddTimeToDate.TIME_TO_ADD) as HTMLInputElement)
private val addCalcsOutputDate get() = document.getElementById(Ids.AddTimeToDate.OUTOUT_FIELD) as HTMLDivElement
private val addCalcsIsDateOnlyAddTimeToDate get() = (document.getElementById("add_time_to_date_only_date") as HTMLInputElement)
private val addCalcsIsDateTimeAddTimeToDate get() = (document.getElementById("add_time_to_date_time_date") as HTMLInputElement)

private val addCalcsStrtDate get() = (document.getElementById(Ids.CalcDuration.STRT_DATE) as HTMLInputElement)
private val addCalcsEndDate get() = (document.getElementById(Ids.CalcDuration.END_DATE) as HTMLInputElement)
private val addCalcsOutputDuration get() = document.getElementById(Ids.CalcDuration.OUTPUT_FIELD) as HTMLDivElement
private val addCalcsIsDateOnlyGetDuration get() = (document.getElementById("get_duration_only_date") as HTMLInputElement)
private val addCalcsIsDateTimeGetDuration get() = (document.getElementById("get_duration_time_date") as HTMLInputElement)

fun addCalcsGetDuration(){
    var isDateOnly:Boolean = addCalcsIsDateOnlyGetDuration.checked
    var startDate = Date(addCalcsStrtDate.valueAsNumber)
    var endDate = Date(addCalcsEndDate.valueAsNumber)
    var strUrdu = ""
    var strEnglish = ""
    if(startDate!=null && endDate!=null && startDate.getTime()<endDate.getTime()){
        var result = (endDate.getTime()-startDate.getTime()).toLong()
        strUrdu = daysHoursMinutesDigitalUrdu(result,isDateOnly)
        strEnglish = daysHoursMinutesDigitalEnglish(result, isDateOnly)
    }
    var resultStrings =  OutputStringsLanguages(strUrdu,strEnglish)
    if(languageSelecter.value=="urdu"){
        addCalcsOutputDuration.innerHTML = resultStrings.urduString
    }else if(languageSelecter.value=="english"){
        addCalcsOutputDuration.innerHTML = resultStrings.englishString
    }

}

fun addOnChangeListners(){
    println("adding on change")
    addCalcsDateToAddTo.onchange = { addCalcsAddTimeToDate()}
    addCalcsDurationToAdd.onchange = { addCalcsAddTimeToDate()}
    addCalcsStrtDate.onchange = {addCalcsGetDuration()}
    addCalcsEndDate.onchange = {addCalcsGetDuration()}
    addCalcsIsDateOnlyAddTimeToDate.onchange = {switchDateTime("add-time-to-date")}
    addCalcsIsDateTimeAddTimeToDate.onchange = {switchDateTime("add-time-to-date")}
    addCalcsIsDateOnlyGetDuration.onchange = {switchDateTime("calculate-duration")}
    addCalcsIsDateTimeGetDuration.onchange = {switchDateTime("calculate-duration")}
}
fun addCalcsAddTimeToDate(){
    println("started here")
    var isDateOnly:Boolean = addCalcsIsDateOnlyAddTimeToDate.checked
    var date = Date(addCalcsDateToAddTo.valueAsNumber)
    var duration = parseDays(addCalcsDurationToAdd.value)
    var strResultUrdu = ""
    var strResultEnglish = ""
    if(duration!=null&&date!=null){
        var result = addTimeToDate(date,duration)
        strResultEnglish = englishDateFormat(result,isDateOnly)
        strResultUrdu = urduDateFormat(result,isDateOnly)
    }
    var resultStrings = OutputStringsLanguages(strResultUrdu,strResultEnglish)
    if(languageSelecter.value=="urdu"){
        addCalcsOutputDate.innerHTML = resultStrings.urduString
    }else if(languageSelecter.value=="english"){
        addCalcsOutputDate.innerHTML = resultStrings.englishString
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