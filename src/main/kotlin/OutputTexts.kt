@file:Suppress("SpellCheckingInspection")
import kotlin.js.Date

fun generateOutputStringPregnancy(fixedDurations: MutableList<FixedDuration>,
                                  pregnancy: Pregnancy,
                                  endingOutputValues: EndingOutputValues,
                                  typeOfInput: TypesOfInputs):OutputTexts{
    var englishStr = ""
    var urduStr = ""
    val hazDatesList = getHaizDatesList(fixedDurations)
    urduStr+= generateLanguagedOutputStringPregnancy(fixedDurations,pregnancy, endingOutputValues, typeOfInput).urduString
    englishStr+= generateLanguagedOutputStringPregnancy(fixedDurations,pregnancy, endingOutputValues, typeOfInput).englishString


    return OutputTexts(englishStr,urduStr, "",hazDatesList,endingOutputValues, fixedDurations)
}
fun generateOutputStringMubtadia(fixedDurations: MutableList<FixedDuration>,
                                 endingOutputValues: EndingOutputValues,
                                 typeOfInput: TypesOfInputs):OutputTexts{
    val hazDatesList = getHaizDatesList(fixedDurations)
    val urduStr = generateOutputString(fixedDurations, endingOutputValues, typeOfInput).urduString
    val englishStr = generateOutputString(fixedDurations, endingOutputValues, typeOfInput).englishString

    val hazDatesStr = generateHazDatesStr(hazDatesList,typeOfInput)

    return OutputTexts(englishStr,urduStr, hazDatesStr, hazDatesList,endingOutputValues, fixedDurations)
}
fun generateOutputStringMutadah(fixedDurations: MutableList<FixedDuration>,
                                endingOutputValues: EndingOutputValues,
                                typeOfInput: TypesOfInputs):OutputTexts{
    val hazDatesList = getHaizDatesList(fixedDurations)

//    while (index<fixedDurations.size){
//        englishStr += outputStringHeaderLine(fixedDurations,index, isDateOnly)
//        englishStr += outputStringSumOfIndicesLine(fixedDurations,durations, index, isDateOnly)
//        englishStr += outputStringIstihazaAfterLine(fixedDurations, index, isDateOnly)
//        englishStr += outputStringBiggerThan10Hall(fixedDurations, index, isDateOnly)
//
//
//        index++
//    }

    val urduStr = generateOutputString(fixedDurations, endingOutputValues, typeOfInput).urduString
    val englishStr = generateOutputString(fixedDurations, endingOutputValues, typeOfInput).englishString

    val hazDatesStr = generateHazDatesStr(hazDatesList,typeOfInput)

    return OutputTexts(englishStr,urduStr, hazDatesStr, hazDatesList,endingOutputValues, fixedDurations)
}

fun generateHazDatesStr(hazDatesList: MutableList<Entry>,typeOfInput: TypesOfInputs):String{
    var str = ""
    for(entry in hazDatesList){
        str+="From ${englishDateFormat(entry.startTime,typeOfInput)} to ${englishDateFormat(entry.endTime,typeOfInput)}<br>"
    }
    return str
}

fun generateLanguagedOutputStringPregnancy(fixedDurations: MutableList<FixedDuration>, pregnancy: Pregnancy, endingOutputValues: EndingOutputValues, typeOfInput: TypesOfInputs):OutputStringsLanguages{
    val mustabeen = pregnancy.mustabeenUlKhilqat
//    var startTimeOfPregnancy = pregnancy.pregStartTime.getTime()
    val birthTime = pregnancy.birthTime
    var strUrdu = StringsOfLanguages.URDU.answer
    var strEnglish = StringsOfLanguages.ENGLISH.answer

    if(mustabeen){
        if(fixedDurations[0].type!=DurationType.HAML){
            strUrdu += StringsOfLanguages.URDU.beforepregheader
            strEnglish += StringsOfLanguages.ENGLISH.beforepregheader
        }
        for(index in fixedDurations.indices){
            if(typeOfInput==TypesOfInputs.DURATION) {
                strUrdu += outputStringHeaderLineDuration(fixedDurations,index).urduString
                strEnglish += outputStringHeaderLineDuration(fixedDurations,index).englishString
                strUrdu += outputStringBiggerThan10HallDurations(fixedDurations,index).urduString
                strEnglish += outputStringBiggerThan10HallDurations(fixedDurations,index).englishString
                strUrdu += outputStringBiggerThan40HallDuration(fixedDurations,index).urduString
                strEnglish += outputStringBiggerThan40HallDuration(fixedDurations,index).englishString
                if(fixedDurations[index].type==DurationType.HAML){
                    strUrdu += StringsOfLanguages.URDU.pregduration
                    strEnglish += StringsOfLanguages.ENGLISH.pregduration
                }
                if(fixedDurations[index].type==DurationType.WILADAT_ISQAT){
                    strUrdu += StringsOfLanguages.URDU.birthduration
                    strEnglish += StringsOfLanguages.ENGLISH.birthduration
                }

            }else{
                strUrdu += outputStringHeaderLine(fixedDurations,index, typeOfInput).urduString
                strEnglish += outputStringHeaderLine(fixedDurations,index, typeOfInput).englishString
                strUrdu += outputStringBiggerThan10Hall(fixedDurations,index, typeOfInput).urduString
                strEnglish += outputStringBiggerThan10Hall(fixedDurations,index, typeOfInput).englishString
                strUrdu += outputStringBiggerThan40Hall(fixedDurations,index, typeOfInput).urduString
                strEnglish += outputStringBiggerThan40Hall(fixedDurations,index, typeOfInput).englishString
                if(fixedDurations[index].type==DurationType.HAML){
                    strUrdu += StringsOfLanguages.URDU.preg
                    strEnglish += StringsOfLanguages.ENGLISH.preg
                }
                if(fixedDurations[index].type==DurationType.WILADAT_ISQAT){
                    strUrdu += StringsOfLanguages.URDU.birth.replace("date1", urduDateFormat(birthTime, typeOfInput))
                    strEnglish += StringsOfLanguages.ENGLISH.birth.replace("date1",
                        englishDateFormat(birthTime, typeOfInput)
                    )
                    if(index<fixedDurations.size-2){//if there is something after wiladat
                        strUrdu += StringsOfLanguages.URDU.afterpregheader
                        strEnglish += StringsOfLanguages.ENGLISH.afterpregheader
                    }
                }

            }

        }
        strUrdu += outputStringFinalLines(endingOutputValues, typeOfInput, pregnancy).urduString
        strEnglish += outputStringFinalLines(endingOutputValues, typeOfInput, pregnancy).englishString



    }else{//if it is ghair mustabeenulkhilqat
        strUrdu += StringsOfLanguages.URDU.headerline
        strEnglish += StringsOfLanguages.ENGLISH.headerline

        for(index in fixedDurations.indices){
            if(typeOfInput==TypesOfInputs.DURATION){
                strUrdu += outputStringHeaderLineDuration(fixedDurations,index, mustabeen).urduString
                strEnglish += outputStringHeaderLineDuration(fixedDurations,index,mustabeen).englishString
                strUrdu += outputStringBiggerThan10HallDurations(fixedDurations,index).urduString
                strEnglish += outputStringBiggerThan10HallDurations(fixedDurations,index).englishString
                if(fixedDurations[index].type==DurationType.HAML){
                    strUrdu += StringsOfLanguages.URDU.preg
                    strEnglish += StringsOfLanguages.ENGLISH.preg
                }
                if(fixedDurations[index].type==DurationType.WILADAT_ISQAT){
                    strUrdu += StringsOfLanguages.URDU.earlymiscarriageduration
                    strEnglish += StringsOfLanguages.ENGLISH.earlymiscarriageduration
                    if(index<fixedDurations.size-2){//if there is something after wiladat
                        strUrdu += StringsOfLanguages.URDU.afterpregheader
                        strEnglish += StringsOfLanguages.ENGLISH.afterpregheader
                    }
                }

            }else{
                strUrdu += outputStringHeaderLine(fixedDurations,index, typeOfInput, mustabeen).urduString
                strEnglish += outputStringHeaderLine(fixedDurations,index, typeOfInput, mustabeen).englishString
                strUrdu += outputStringBiggerThan10Hall(fixedDurations,index, typeOfInput).urduString
                strEnglish += outputStringBiggerThan10Hall(fixedDurations,index, typeOfInput).englishString
                if(fixedDurations[index].type==DurationType.HAML){
                    strUrdu += StringsOfLanguages.URDU.preg
                    strEnglish += StringsOfLanguages.ENGLISH.preg
                }
                if(fixedDurations[index].type==DurationType.WILADAT_ISQAT){
                    strUrdu += StringsOfLanguages.URDU.earlymiscarriage.replace("date1",
                        urduDateFormat(birthTime, typeOfInput)
                    )
                    strEnglish += StringsOfLanguages.ENGLISH.earlymiscarriage.replace("date1",
                        englishDateFormat(birthTime, typeOfInput)
                    )
                    if(index<fixedDurations.size-2){//if there is something after wiladat
                        strUrdu += StringsOfLanguages.URDU.afterpregheader
                        strEnglish += StringsOfLanguages.ENGLISH.afterpregheader
                    }
                }
            }

        }
        strUrdu += outputStringFinalLines (endingOutputValues, typeOfInput).urduString
        strEnglish += outputStringFinalLines (endingOutputValues, typeOfInput).englishString

    }


    return OutputStringsLanguages(strUrdu,strEnglish)
}

fun generateOutputString(fixedDurations: MutableList<FixedDuration>, endingOutputValues: EndingOutputValues, typeOfInput: TypesOfInputs):OutputStringsLanguages{
    //ToDo: figure out how to do languages for real
    var strUrdu = StringsOfLanguages.URDU.answer //جواب:
    var strEnglish = StringsOfLanguages.ENGLISH.answer //جواب:

    strUrdu += StringsOfLanguages.URDU.headerline //اس ترتیب سے خون آیا اور پاکی ملی
    strEnglish += StringsOfLanguages.ENGLISH.headerline //اس ترتیب سے خون آیا اور پاکی ملی
    var index = 0
    while (index<fixedDurations.size){
        if(typeOfInput==TypesOfInputs.DURATION){
            strUrdu += outputStringHeaderLineDuration(fixedDurations,index).urduString //اس تاریخ سے اس تاریخ تک اتنے دن حیض
            strEnglish += outputStringHeaderLineDuration(fixedDurations,index).englishString //اس تاریخ سے اس تاریخ تک اتنے دن حیض
            strUrdu += outputStringBiggerThan10HallDurations(fixedDurations,index).urduString
            strEnglish += outputStringBiggerThan10HallDurations(fixedDurations,index).englishString
            if(index==fixedDurations.size-1){//if this os the last index
                strUrdu += outputStringFinalLines(endingOutputValues, typeOfInput).urduString
                strEnglish += outputStringFinalLines(endingOutputValues, typeOfInput).englishString
            }
        }else{
            strUrdu += outputStringHeaderLine(fixedDurations,index, typeOfInput).urduString //اس تاریخ سے اس تاریخ تک اتنے دن حیض
            strEnglish += outputStringHeaderLine(fixedDurations,index, typeOfInput).englishString //اس تاریخ سے اس تاریخ تک اتنے دن حیض
            strUrdu += outputStringBiggerThan10Hall(fixedDurations,index, typeOfInput).urduString
            strEnglish += outputStringBiggerThan10Hall(fixedDurations,index, typeOfInput).englishString
            if(index==fixedDurations.size-1){//if this os the last index
                strUrdu += outputStringFinalLines(endingOutputValues, typeOfInput).urduString
                strEnglish += outputStringFinalLines(endingOutputValues, typeOfInput).englishString
            }
        }
        index++
    }
    return OutputStringsLanguages(strUrdu,strEnglish)
}
fun generateUrduOutputStringMubtadia(fixedDurations: MutableList<FixedDuration>, endingOutputValues: EndingOutputValues, typeOfInput: TypesOfInputs):String{
    //ToDo: figure out how to do languages for real
    var str = StringsOfLanguages.URDU.answer //جواب:
    str += StringsOfLanguages.URDU.headerlinemubtadia //اس ترتیب سے خون آیا اور پاکی ملی
    var index = 0
    while (index<fixedDurations.size){
        str += outputStringHeaderLine(fixedDurations,index, typeOfInput)
        str += outputStringBiggerThan10Hall(fixedDurations,index, typeOfInput)
        if(index==fixedDurations.size-1){//if this os the last index
            str += outputStringFinalLines(endingOutputValues, typeOfInput)
        }
        index++
    }
    return str
}

fun outputStringFinalLines(endingOutputValues: EndingOutputValues, typeOfInput: TypesOfInputs, pregnancy: Pregnancy? = null):OutputStringsLanguages{

    var strUrdu = ""
    var strEnglish = ""
    val aadats = endingOutputValues.aadats
    strUrdu+=outputStringAadatLine(typeOfInput, aadats).urduString
    strEnglish+=outputStringAadatLine(typeOfInput, aadats).englishString
    if(typeOfInput==TypesOfInputs.DURATION){return OutputStringsLanguages(strUrdu,strEnglish)}
    val filHaal = endingOutputValues.filHaalPaki
    strUrdu+=outputStringFilHaalLine(filHaal).urduString
    strEnglish+=outputStringFilHaalLine(filHaal).englishString
    val futureDates = endingOutputValues.futureDateType
    strUrdu+=outputStringAskAgainLine(typeOfInput,futureDates).urduString
    strEnglish+=outputStringAskAgainLine(typeOfInput, futureDates).englishString

    //plis note down line
    strUrdu+=StringsOfLanguages.URDU.writedown
    strEnglish+=StringsOfLanguages.ENGLISH.writedown

    //Allahu Aaalam line
    strUrdu+=StringsOfLanguages.URDU.allahknows
    strEnglish+=StringsOfLanguages.ENGLISH.allahknows

    return OutputStringsLanguages(strUrdu,strEnglish)
}
fun outputStringFilHaalLine(filHaalPaki:Boolean?):OutputStringsLanguages{
    val filHaalPakiStrUrdu = StringsOfLanguages.URDU.currentpaki
    val filHaalPakiStrEnglish = StringsOfLanguages.ENGLISH.currentpaki
    val filHaalHaizStrUrdu = StringsOfLanguages.URDU.currenthaiz
    val filHaalHaizStrEnglish = StringsOfLanguages.ENGLISH.currenthaiz
    when (filHaalPaki) {
        true -> return OutputStringsLanguages(filHaalPakiStrUrdu, filHaalPakiStrEnglish)
        false -> return OutputStringsLanguages(filHaalHaizStrUrdu, filHaalHaizStrEnglish)
        null -> return OutputStringsLanguages("", "")

//    //right now, we are just going to check to see what last halat is
//    var istihazaAfter = fixedDurations[index].biggerThanTen?.istihazaAfter ?: return ""
//    var aadatHaiz = fixedDurations[index].biggerThanTen?.aadatHaiz ?: return ""
//    var aadatTuhr = fixedDurations[index].biggerThanTen?.aadatTuhr ?: return ""
//    if(istihazaAfter==0L){//last halat is haiz
//        strUrdu+=filHaalHaizStr
//    }else if(istihazaAfter>=aadatTuhr+3){//last period is long istihaza, lets's figure out more
//        //find remainder
//        var remainder = istihazaAfter%(aadatHaiz+aadatTuhr)
//
//        if (remainder<aadatTuhr + 3 && remainder!=0L){//it ended in tuhr
//            strUrdu+=filHaalPakiStr
//        }else{//it ended in haiz or remainder is 0
//             if (remainder!=0L){//it ended in haiz
//                 strUrdu+=filHaalHaizStr
//            }else{//it ended in tuhr
//                strUrdu+=filHaalPakiStr
//            }
//        }
//    }else{//last halat is short istihaza
//        strUrdu+=filHaalPakiStr
//    }

//    return strUrdu
    }
}

fun outputStringAskAgainLine(typeOfInput: TypesOfInputs, futureDates: MutableList<FutureDateType>):OutputStringsLanguages{
    var strUrdu = ""
    var strEnglish = ""

    for(futureDate in futureDates){
        val date = futureDate.date
        val type= futureDate.futureDates
        when (type) {
            TypesOfFutureDates.END_OF_AADAT_HAIZ -> {
                strUrdu += StringsOfLanguages.URDU.haizend.replace("date1", urduDateFormat(date, typeOfInput))
                strEnglish += StringsOfLanguages.ENGLISH.haizend.replace("date1", englishDateFormat(date, typeOfInput))
            }
            TypesOfFutureDates.END_OF_AADAT_TUHR -> {
                strUrdu += StringsOfLanguages.URDU.endofpaki.replace("date1", urduDateFormat(date, typeOfInput))
                strEnglish += StringsOfLanguages.ENGLISH.endofpaki.replace("date1", englishDateFormat(date, typeOfInput))
            }
            TypesOfFutureDates.IC_FORBIDDEN_DATE -> {
                strUrdu += StringsOfLanguages.URDU.sexnotallowed.replace("date1", urduDateFormat(date, typeOfInput))
                strEnglish += StringsOfLanguages.ENGLISH.sexnotallowed.replace("date1",
                    englishDateFormat(date, typeOfInput)
                )
            }
            TypesOfFutureDates.AFTER_TEN_DAYS -> {
                strUrdu += StringsOfLanguages.URDU.aftertendays.replace("date1", urduDateFormat(date, typeOfInput))
                strEnglish += StringsOfLanguages.ENGLISH.aftertendays.replace("date1", englishDateFormat(date, typeOfInput))
            }
            TypesOfFutureDates.FORTY_DAYS -> {
                strUrdu += StringsOfLanguages.URDU.afterfortydays.replace("date1", urduDateFormat(date, typeOfInput))
                strEnglish += StringsOfLanguages.ENGLISH.afterfortydays.replace("date1",
                    englishDateFormat(date, typeOfInput)
                )
            }
            TypesOfFutureDates.BEFORE_THREE_DAYS_MASLA_WILL_CHANGE -> {
                strUrdu += StringsOfLanguages.URDU.bleedingstopsbeforethreemaslachanges.replace("date1",
                    urduDateFormat(date, typeOfInput)
                )
                strEnglish += StringsOfLanguages.ENGLISH.bleedingstopsbeforethreemaslachanges.replace("date1",
                    englishDateFormat(date, typeOfInput)
                )
            }
            TypesOfFutureDates.BEFORE_THREE_DAYS -> {
                strUrdu += StringsOfLanguages.URDU.bleedingstopsbeforethree.replace("date1",
                    urduDateFormat(date, typeOfInput)
                )
                strEnglish += StringsOfLanguages.ENGLISH.bleedingstopsbeforethree.replace("date1",
                    englishDateFormat(date, typeOfInput)
                )
            }
            TypesOfFutureDates.IHTIYATI_GHUSL -> {
                strUrdu += StringsOfLanguages.URDU.ihtiyatighusl.replace("date1", urduDateFormat(date, typeOfInput))
                strEnglish += StringsOfLanguages.ENGLISH.ihtiyatighusl.replace("date1",
                    englishDateFormat(date, typeOfInput)
                )
            }
            TypesOfFutureDates.A3_CHANGING_TO_A2 -> {
                strUrdu += StringsOfLanguages.URDU.situationmaychange.replace("date1", urduDateFormat(date, typeOfInput))
                strEnglish += StringsOfLanguages.ENGLISH.situationmaychange.replace("date1",
                    englishDateFormat(date, typeOfInput)
                )
            }
            TypesOfFutureDates.BEFORE_TEN_DAYS_AYYAMEQABLIYYAH -> {
                strUrdu += StringsOfLanguages.URDU.beforetendaysayyameqabliyyaallconsideredhaiz.replace("date1",
                    urduDateFormat(date, typeOfInput)
                )
                strEnglish += StringsOfLanguages.ENGLISH.beforetendaysayyameqabliyyaallconsideredhaiz.replace("date1",
                    englishDateFormat(date, typeOfInput)
                )
            }
            TypesOfFutureDates.START_OF_AADAT_AYYAMEQABLIYYA -> {
                strUrdu += StringsOfLanguages.URDU.endofistehazaayyameqabliyya.replace("date1",
                    urduDateFormat(date, typeOfInput)
                )
                strEnglish += StringsOfLanguages.ENGLISH.endofistehazaayyameqabliyya.replace("date1",
                    englishDateFormat(date, typeOfInput)
                )
            }
            TypesOfFutureDates.TEN_DAYS_EXACTLY -> {
                strUrdu+= StringsOfLanguages.URDU.tendaysdoghusl
                strUrdu+= StringsOfLanguages.URDU.askagainnodate
                strEnglish+= StringsOfLanguages.ENGLISH.tendaysdoghusl
                strEnglish+=StringsOfLanguages.ENGLISH.askagainnodate
            }
        }
    }
    return OutputStringsLanguages(strUrdu,strEnglish)
}
fun outputStringAadatLine(typeOfInput: TypesOfInputs, aadats:AadatsOfHaizAndTuhr?):OutputStringsLanguages{
    //we should probably put in the possibility of tuhr aadat only...
    //and tuhr aadat with nifas
    var strUrdu = ""
    var strEnglish = ""

    return if (aadats==null) OutputStringsLanguages("","")
    else {
        val aadatTuhr = aadats.aadatTuhr
        val aadatHaiz = aadats.aadatHaiz
        if(aadatHaiz==-1L && aadatTuhr==-1L){//neither haiz nor tuhr aadat exists
            if(aadats.aadatNifas != null && aadats.aadatNifas!=-1L){//adat nifas exists
                strUrdu+= StringsOfLanguages.URDU.onlynifashabit
                    .replace("duration1", daysHoursMinutesDigitalUrdu(aadats.aadatNifas!!, typeOfInput))
                strEnglish+= StringsOfLanguages.ENGLISH.onlynifashabit
                    .replace("duration1", daysHoursMinutesDigitalEnglish(aadats.aadatNifas!!, typeOfInput))

            }else {//adat nifas doesn't exists
                strUrdu += StringsOfLanguages.URDU.thereisnoaadat
                strEnglish += StringsOfLanguages.ENGLISH.thereisnoaadat
            }
        }else if(aadatHaiz!=-1L && aadatTuhr==-1L) {//aadat of haiz exists, but not aadat of tuhr
            if (aadats.aadatNifas != null && aadats.aadatNifas != -1L) {//adat nifas exists
                strUrdu += StringsOfLanguages.URDU.nifasAndHaizHabit
                    .replace("duration1", daysHoursMinutesDigitalUrdu(aadatHaiz, typeOfInput))
                    .replace("duration2", daysHoursMinutesDigitalUrdu(aadats.aadatNifas!!, typeOfInput))
                strEnglish += StringsOfLanguages.ENGLISH.nifasAndHaizHabit
                    .replace("duration1", daysHoursMinutesDigitalEnglish(aadatHaiz, typeOfInput))
                    .replace("duration2", daysHoursMinutesDigitalEnglish(aadats.aadatNifas!!, typeOfInput))

            } else {//adat nifas doesn't exists
                strUrdu += StringsOfLanguages.URDU.aadatofhaizonly
                    .replace("duration1", daysHoursMinutesDigitalUrdu(aadatHaiz, typeOfInput))
                strEnglish += StringsOfLanguages.ENGLISH.aadatofhaizonly
                    .replace("duration1", daysHoursMinutesDigitalEnglish(aadatHaiz, typeOfInput))
            }
        }else if(aadatHaiz==-1L && aadatTuhr!=-1L){//aadat tuhr exist and aadat haiz doesn;t exist
            if (aadats.aadatNifas != null && aadats.aadatNifas != -1L) {//adat nifas exists
                strUrdu += StringsOfLanguages.URDU.nifasAndTuhrHabit
                    .replace("duration1", daysHoursMinutesDigitalUrdu(aadatTuhr, typeOfInput))
                    .replace("duration2", daysHoursMinutesDigitalUrdu(aadats.aadatNifas!!, typeOfInput))
                strEnglish += StringsOfLanguages.ENGLISH.nifasAndTuhrHabit
                    .replace("duration1", daysHoursMinutesDigitalEnglish(aadatTuhr, typeOfInput))
                    .replace("duration2", daysHoursMinutesDigitalEnglish(aadats.aadatNifas!!, typeOfInput))

            }else{
                strUrdu += StringsOfLanguages.URDU.onlyTuhrHabit
                    .replace("duration1", daysHoursMinutesDigitalUrdu(aadatTuhr, typeOfInput))
                strEnglish += StringsOfLanguages.ENGLISH.onlyTuhrHabit
                    .replace("duration1", daysHoursMinutesDigitalEnglish(aadatTuhr, typeOfInput))

            }
        }else{//adats of haiz and tuhr exist
            if(aadats.aadatNifas != null && aadats.aadatNifas!=-1L){//adat nifas exists
                strUrdu+= StringsOfLanguages.URDU.habitwithnifas
                    .replace("duration1", daysHoursMinutesDigitalUrdu(aadatHaiz, typeOfInput))
                    .replace("duration2", daysHoursMinutesDigitalUrdu(aadatTuhr, typeOfInput))
                    .replace("duration3", daysHoursMinutesDigitalUrdu(aadats.aadatNifas!!, typeOfInput))
                strEnglish+= StringsOfLanguages.ENGLISH.habitwithnifas
                    .replace("duration1", daysHoursMinutesDigitalEnglish(aadatHaiz, typeOfInput))
                    .replace("duration2", daysHoursMinutesDigitalEnglish(aadatTuhr, typeOfInput))
                    .replace("duration3", daysHoursMinutesDigitalEnglish(aadats.aadatNifas!!, typeOfInput))
            }else{//adat nifas doesn't exists
                strUrdu+= StringsOfLanguages.URDU.habit
                    .replace("duration1", daysHoursMinutesDigitalUrdu(aadatHaiz, typeOfInput))
                    .replace("duration2", daysHoursMinutesDigitalUrdu(aadatTuhr, typeOfInput))
                strEnglish+= StringsOfLanguages.ENGLISH.habit
                    .replace("duration1", daysHoursMinutesDigitalEnglish(aadatHaiz, typeOfInput))
                    .replace("duration2", daysHoursMinutesDigitalEnglish(aadatTuhr, typeOfInput))
            }
        }
        OutputStringsLanguages(strUrdu, strEnglish)
    }

//    var aadatHaiz = fixedDurations[index].biggerThanTen?.aadatHaiz ?: return ""
//    var aadatTuhr = fixedDurations[index].biggerThanTen?.aadatTuhr ?: return ""
//    var istihazaAfter = fixedDurations[index].biggerThanTen?.istihazaAfter ?: return ""
//
//    if (istihazaAfter>=aadatTuhr+3) {//if we have a long istihaza after, there is a possibility that aadat changed
//        //find remainder
//        var remainder = istihazaAfter % (aadatHaiz + aadatTuhr)
//        if (remainder<aadatTuhr + 3 && remainder!=0L){//it ended in tuhr, so aadat doesn't change
//
//        }else{//it ended in haiz or remainder is 0 (which means ending in tuhr)
//            //change aadatHaiz if remainder is not zero (if it is zero, aadat doesn't change, so shouldn't be printed
//            if (remainder!=0L){
//                val aadatHaiz = (remainder-aadatTuhr).toString()
//            }
//        }
//    }
//
//    strUrdu+="${UnicodeChars.GREEN_CIRCLE} <b>عادت:: حیض: ${daysHoursMinutesDigitalUrdu(aadatHaiz, isDateOnly)}، طہر: ${daysHoursMinutesDigitalUrdu(aadatTuhr, isDateOnly)}</b>\n\n"
//
//    return strUrdu
}
fun outputStringBiggerThan10HallDurations(fixedDurations: MutableList<FixedDuration>, index: Int):OutputStringsLanguages{
    var strUrdu = ""
    var strEnglish = ""
    val typeOfInput = TypesOfInputs.DURATION

    if((fixedDurations[index].days>10 &&
                (fixedDurations[index].type==DurationType.DAM||
                        fixedDurations[index].type==DurationType.DAM_MUBTADIA))){
        strUrdu += UnicodeChars.ABACUS
        strEnglish += UnicodeChars.ABACUS

        for(duration in fixedDurations[index].biggerThanTen!!.durationsList){
            if(duration.type == DurationType.ISTIHAZA_BEFORE){
                strUrdu+= StringsOfLanguages.URDU.startingFromIstehaza.replace("duration1",
                    daysHoursMinutesDigitalUrdu(duration.timeInMilliseconds, typeOfInput)
                )
                strEnglish+= StringsOfLanguages.ENGLISH.startingFromIstehaza.replace("duration1",
                    daysHoursMinutesDigitalEnglish(duration.timeInMilliseconds, typeOfInput)
                )

            }else if(duration.type == DurationType.HAIZ){
                if(duration==fixedDurations[index].biggerThanTen!!.durationsList[0]){
                    strUrdu+= StringsOfLanguages.URDU.startingFromHaiz.replace("duration1",
                        daysHoursMinutesDigitalUrdu(duration.timeInMilliseconds, typeOfInput)
                    )
                    strEnglish+= StringsOfLanguages.ENGLISH.startingFromHaiz.replace("duration1",
                        daysHoursMinutesDigitalEnglish(duration.timeInMilliseconds, typeOfInput)
                    )
                }else{
                    strUrdu+= StringsOfLanguages.URDU.followedByHaizAfter.replace("duration1",
                        daysHoursMinutesDigitalUrdu(duration.timeInMilliseconds, typeOfInput)
                    )
                    strEnglish+= StringsOfLanguages.ENGLISH.followedByHaizAfter.replace("duration1",
                        daysHoursMinutesDigitalEnglish(duration.timeInMilliseconds, typeOfInput)
                    )
                }
            }else if(duration.type == DurationType.ISTIHAZA_AFTER){
                strUrdu+= StringsOfLanguages.URDU.followedByistehazaAfter.replace("duration1",
                    daysHoursMinutesDigitalUrdu(duration.timeInMilliseconds, typeOfInput)
                )
                strEnglish+= StringsOfLanguages.ENGLISH.followedByistehazaAfter.replace("duration1",
                    daysHoursMinutesDigitalEnglish(duration.timeInMilliseconds, typeOfInput)
                )

            }else if(duration.type == DurationType.LESS_THAN_3_HAIZ){
                strUrdu+= StringsOfLanguages.URDU.followedByHaizAfter.replace("duration1",
                    daysHoursMinutesDigitalUrdu(duration.timeInMilliseconds, typeOfInput)
                )
                strEnglish+= StringsOfLanguages.ENGLISH.followedByHaizAfter.replace("duration1",
                    daysHoursMinutesDigitalEnglish(duration.timeInMilliseconds, typeOfInput)
                )
                //maybe we'll wanna add something about itibaar bil khawateem
            }else if(duration.type == DurationType.NIFAS){
                strUrdu+= StringsOfLanguages.URDU.startingFromNifas
                strEnglish+= StringsOfLanguages.ENGLISH.startingFromNifas
            }
        }
    }
    if(strUrdu!=""&& strEnglish!=""){
        strUrdu+=StringsOfLanguages.URDU.khatimaplusnewline
        strEnglish+=StringsOfLanguages.ENGLISH.khatimaplusnewline
    }
    return OutputStringsLanguages(strUrdu,strEnglish)
}

fun outputStringBiggerThan10Hall(fixedDurations: MutableList<FixedDuration>, index: Int, typeOfInput: TypesOfInputs):OutputStringsLanguages{
    //legacy code this, I think it's unused
    var strUrdu = ""
    var strEnglish = ""

    fun haizLine(sd:Date, ed:Date, typeOfInput: TypesOfInputs):OutputStringsLanguages{
        return OutputStringsLanguages(
            StringsOfLanguages.URDU.haizdaysinsolution.replace("date1", urduDateFormat(sd, typeOfInput)).replace("date2",
                urduDateFormat(ed,typeOfInput)
            ).replace("duration1", daysHoursMinutesDigitalUrdu((difference(sd,ed)), typeOfInput)),
            StringsOfLanguages.ENGLISH.haizdaysinsolution.replace("date1", englishDateFormat(sd, typeOfInput)).replace("date2",
                englishDateFormat(ed,typeOfInput)
            ).replace("duration1", daysHoursMinutesDigitalEnglish((difference(sd,ed)), typeOfInput))
        )
    }
    fun istihazaLine(sd:Date, ed:Date, typeOfInput: TypesOfInputs):OutputStringsLanguages{
        return OutputStringsLanguages(
            StringsOfLanguages.URDU.istihazadays.replace("date1", urduDateFormat(sd, typeOfInput)).replace("date2",
                urduDateFormat(ed,typeOfInput)
            ).replace("duration1", daysHoursMinutesDigitalUrdu(difference(sd,ed), typeOfInput)),
            StringsOfLanguages.ENGLISH.istihazadays.replace("date1", englishDateFormat(sd, typeOfInput)).replace("date2",
                englishDateFormat(ed,typeOfInput)
            ).replace("duration1", daysHoursMinutesDigitalEnglish(difference(sd,ed), typeOfInput))

        )
    }

    if((fixedDurations[index].days>10 &&
        (fixedDurations[index].type==DurationType.DAM||
        fixedDurations[index].type==DurationType.DAM_MUBTADIA))){

        strUrdu += StringsOfLanguages.URDU.dashesline
        strEnglish += StringsOfLanguages.ENGLISH.dashesline
        strUrdu += StringsOfLanguages.URDU.solution
        strEnglish += StringsOfLanguages.ENGLISH.solution

        for(duration in fixedDurations[index].biggerThanTen!!.durationsList){
            when (duration.type) {
                DurationType.ISTIHAZA_BEFORE -> {
                    strUrdu+= istihazaLine(duration.startTime,duration.endDate,typeOfInput).urduString
                    strEnglish+= istihazaLine(duration.startTime,duration.endDate,typeOfInput).englishString
                    strUrdu+= StringsOfLanguages.URDU.istihazadetailslineone
                    strEnglish+= StringsOfLanguages.ENGLISH.istihazadetailslineone

                }
                DurationType.HAIZ -> {
                    strUrdu+= haizLine(duration.startTime,duration.endDate,typeOfInput).urduString
                    strEnglish+= haizLine(duration.startTime,duration.endDate,typeOfInput).englishString

                }
                DurationType.ISTIHAZA_AFTER -> {
                    strUrdu+= istihazaLine(duration.startTime,duration.endDate,typeOfInput).urduString
                    strEnglish+= istihazaLine(duration.startTime,duration.endDate,typeOfInput).englishString
                    strUrdu+= StringsOfLanguages.URDU.istihazadetailslinetwo.replace("date1",
                        urduDateFormat(duration.startTime,typeOfInput)
                    )
                    strEnglish+= StringsOfLanguages.ENGLISH.istihazadetailslinetwo.replace("date1",
                        englishDateFormat(duration.startTime,typeOfInput)
                    )
                    strUrdu+= StringsOfLanguages.URDU.istihazadetailslineone
                    strEnglish+= StringsOfLanguages.ENGLISH.istihazadetailslineone

                }
                DurationType.LESS_THAN_3_HAIZ -> {
                    strUrdu+= haizLine(duration.startTime,duration.endDate,typeOfInput).urduString
                    strEnglish+= haizLine(duration.startTime,duration.endDate,typeOfInput).englishString
                    //maybe we'll wanna add something about itibaar bil khawateem
                }
            }
        }

        strUrdu += StringsOfLanguages.URDU.dashesline
        strEnglish += StringsOfLanguages.ENGLISH.dashesline
    }

    return OutputStringsLanguages(strUrdu,strEnglish)
}
fun outputStringBiggerThan40HallDuration(fixedDurations: MutableList<FixedDuration>, index: Int):OutputStringsLanguages{
    val typeOfInput=TypesOfInputs.DURATION
    var strUrdu = ""
    var strEnglish = ""
    if(fixedDurations[index].days>40&&fixedDurations[index].type==DurationType.DAM_IN_NIFAS_PERIOD){
        strUrdu+=UnicodeChars.ABACUS
        strEnglish+=UnicodeChars.ABACUS
        for(duration in fixedDurations[index].biggerThanForty!!.durationsList){
            when (duration.type) {
                DurationType.NIFAS -> {
                    strUrdu+= StringsOfLanguages.URDU.startingFromNifas.replace("duration1",
                        daysHoursMinutesDigitalUrdu(duration.timeInMilliseconds, typeOfInput)
                    )
                    strEnglish+= StringsOfLanguages.ENGLISH.startingFromNifas.replace("duration1",
                        daysHoursMinutesDigitalEnglish(duration.timeInMilliseconds, typeOfInput)
                    )
                }
                DurationType.ISTIHAZA_AFTER -> {
                    strUrdu+= StringsOfLanguages.URDU.followedByistehazaAfter.replace("duration1",
                        daysHoursMinutesDigitalUrdu(duration.timeInMilliseconds, typeOfInput)
                    )
                    strEnglish+= StringsOfLanguages.ENGLISH.followedByistehazaAfter.replace("duration1",
                        daysHoursMinutesDigitalEnglish(duration.timeInMilliseconds, typeOfInput)
                    )
                }
                DurationType.HAIZ -> {
                    strUrdu+= StringsOfLanguages.URDU.followedByHaizAfter.replace("duration1",
                        daysHoursMinutesDigitalUrdu(duration.timeInMilliseconds, typeOfInput)
                    )
                    strEnglish+= StringsOfLanguages.ENGLISH.followedByHaizAfter.replace("duration1",
                        daysHoursMinutesDigitalEnglish(duration.timeInMilliseconds, typeOfInput)
                    )
                }
                DurationType.LESS_THAN_3_HAIZ -> {
                    strUrdu+= StringsOfLanguages.URDU.followedByHaizAfter.replace("duration1",
                        daysHoursMinutesDigitalUrdu(duration.timeInMilliseconds, typeOfInput)
                    )
                    strUrdu+= StringsOfLanguages.ENGLISH.followedByHaizAfter.replace("duration1",
                        daysHoursMinutesDigitalEnglish(duration.timeInMilliseconds, typeOfInput)
                    )
                    //maybe we'll wanna add something about itibaar bil khawateem
                }
            }
        }
         }
    if(strUrdu!=""&&strEnglish!=""){
        strUrdu+=StringsOfLanguages.URDU.khatimaplusnewline
        strEnglish+=StringsOfLanguages.ENGLISH.khatimaplusnewline
    }
    return OutputStringsLanguages(strUrdu, strEnglish)
}
fun outputStringBiggerThan40Hall(fixedDurations: MutableList<FixedDuration>, index: Int, typeOfInput: TypesOfInputs):OutputStringsLanguages{

    var strUrdu = ""
    var strEnglish = ""

    fun nifasLine(sd:Date, ed:Date, typeOfInput: TypesOfInputs):OutputStringsLanguages{
        return OutputStringsLanguages(
            StringsOfLanguages.URDU.nifasdaysinsolution.replace("date1", urduDateFormat(sd, typeOfInput)).replace("date2",
                urduDateFormat(ed,typeOfInput)
            ).replace("duration1", daysHoursMinutesDigitalUrdu((difference(sd,ed)), typeOfInput)),
            StringsOfLanguages.ENGLISH.nifasdaysinsolution.replace("date1", englishDateFormat(sd, typeOfInput)).replace("date2",
                englishDateFormat(ed,typeOfInput)
            ).replace("duration1", daysHoursMinutesDigitalEnglish((difference(sd,ed)), typeOfInput))
        )
    }
    fun haizLine(sd:Date, ed:Date, typeOfInput: TypesOfInputs):OutputStringsLanguages{
        return OutputStringsLanguages(
            StringsOfLanguages.URDU.haizdaysinsolution.replace("date1", urduDateFormat(sd, typeOfInput)).replace("date2",
                urduDateFormat(ed, typeOfInput)
            ).replace("duration1", daysHoursMinutesDigitalUrdu((difference(sd,ed)), typeOfInput)),
            StringsOfLanguages.ENGLISH.haizdaysinsolution.replace("date1", englishDateFormat(sd, typeOfInput)).replace("date2",
                englishDateFormat(ed, typeOfInput)
            ).replace("duration1", daysHoursMinutesDigitalEnglish((difference(sd,ed)), typeOfInput))
        )
    }
    fun istihazaLine(sd:Date, ed:Date, typeOfInput: TypesOfInputs):OutputStringsLanguages{
        return OutputStringsLanguages(
            StringsOfLanguages.URDU.istihazadays.replace("date1", urduDateFormat(sd, typeOfInput)).replace("date2",
                urduDateFormat(ed,typeOfInput)
            ).replace("duration1", daysHoursMinutesDigitalUrdu(difference(sd,ed), typeOfInput)),
            StringsOfLanguages.ENGLISH.istihazadays.replace("date1", englishDateFormat(sd, typeOfInput)).replace("date2",
                englishDateFormat(ed,typeOfInput)
            ).replace("duration1", daysHoursMinutesDigitalEnglish(difference(sd,ed), typeOfInput))
        )
    }

    if(fixedDurations[index].days>40&&fixedDurations[index].type==DurationType.DAM_IN_NIFAS_PERIOD){
        strUrdu += StringsOfLanguages.URDU.dashesline
        strEnglish += StringsOfLanguages.ENGLISH.dashesline
        strUrdu += StringsOfLanguages.URDU.solution
        strEnglish += StringsOfLanguages.ENGLISH.solution

        for(duration in fixedDurations[index].biggerThanForty!!.durationsList){
            when (duration.type) {
                DurationType.NIFAS -> {
                    strUrdu+= nifasLine(duration.startTime,duration.endDate, typeOfInput).urduString
                    strEnglish+= nifasLine(duration.startTime,duration.endDate, typeOfInput).englishString
                }
                DurationType.ISTIHAZA_AFTER -> {
                    strUrdu+= istihazaLine(duration.startTime,duration.endDate, typeOfInput).urduString
                    strEnglish+= istihazaLine(duration.startTime,duration.endDate, typeOfInput).englishString
                    strUrdu+= StringsOfLanguages.URDU.istihazadetailslinetwo.replace("date1",
                        urduDateFormat(duration.startTime,typeOfInput)
                    )
                    strEnglish+= StringsOfLanguages.ENGLISH.istihazadetailslinetwo.replace("date1",
                        englishDateFormat(duration.startTime,typeOfInput)
                    )
                    strUrdu+= StringsOfLanguages.URDU.istihazadetailslineone
                    strEnglish+= StringsOfLanguages.ENGLISH.istihazadetailslineone

                }
                DurationType.HAIZ -> {
                    strUrdu+= haizLine(duration.startTime,duration.endDate, typeOfInput).urduString
                    strEnglish+= haizLine(duration.startTime,duration.endDate, typeOfInput).englishString

                }
                DurationType.LESS_THAN_3_HAIZ -> {
                    strUrdu+= haizLine(duration.startTime,duration.endDate,typeOfInput).urduString
                    strEnglish+= haizLine(duration.startTime,duration.endDate,typeOfInput).englishString
                    //maybe we'll wanna add something about itibaar bil khawateem
                }
            }
        }
        strUrdu += StringsOfLanguages.URDU.dashesline
        strEnglish += StringsOfLanguages.ENGLISH.dashesline
    }

    return OutputStringsLanguages(strUrdu,strEnglish)
}
fun outputStringHeaderLineDuration(fixedDurations: MutableList<FixedDuration>, index: Int, isMustabeen:Boolean = true):OutputStringsLanguages{
    val typeOfInput=TypesOfInputs.DURATION
    //in duration we just give the fixed duration
    var outputStringUrdu = ""
    var outputStringEnglish = ""
    if (fixedDurations[index].type==DurationType.DAM||
        fixedDurations[index].type==DurationType.DAM_MUBTADIA){
        val sd:Date = fixedDurations[index].startDate
        val et = fixedDurations[index].endDate
        if(fixedDurations[index].days in 3.0..10.0){//if it's between 3 and 10, write haiz
            outputStringUrdu = StringsOfLanguages.URDU.durationHaiz.replace("duration1",
                daysHoursMinutesDigitalUrdu((difference(sd,et)), typeOfInput)
            )
            outputStringEnglish = StringsOfLanguages.ENGLISH.durationHaiz.replace("duration1",
                daysHoursMinutesDigitalEnglish((difference(sd,et)), typeOfInput)
            )
        }else{//bigger than 10
            outputStringUrdu = StringsOfLanguages.URDU.durationDam.replace("duration1",
                daysHoursMinutesDigitalUrdu((difference(sd,et)), typeOfInput)
            )
            outputStringEnglish = StringsOfLanguages.ENGLISH.durationDam.replace("duration1",
                daysHoursMinutesDigitalEnglish((difference(sd,et)), typeOfInput)
            )
        }

    }else if (fixedDurations[index].type == DurationType.TUHR||
        fixedDurations[index].type == DurationType.TUHR_MUBTADIA||
        fixedDurations[index].type==DurationType.TUHR_MUBTADIA_BECAME_A_MUTADA_NOW){
        val time = fixedDurations[index].timeInMilliseconds
        outputStringUrdu =  StringsOfLanguages.URDU.durationPaki.replace("duration1",
            daysHoursMinutesDigitalUrdu(time, typeOfInput)
        )
        outputStringEnglish =  StringsOfLanguages.ENGLISH.durationPaki.replace("duration1",
            daysHoursMinutesDigitalEnglish(time, typeOfInput)
        )
        if(fixedDurations[index].type == DurationType.TUHR_MUBTADIA_BECAME_A_MUTADA_NOW){
            outputStringUrdu+=StringsOfLanguages.URDU.becamemutadah
            outputStringEnglish+=StringsOfLanguages.ENGLISH.becamemutadah
        }

    }else if (fixedDurations[index].type == DurationType.TUHREFAASID||fixedDurations[index].type==DurationType.TUHREFAASID_MUBTADIA){
        outputStringUrdu =  StringsOfLanguages.URDU.durationTuhrefasid.replace("duration1",
            daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds, typeOfInput)
        )
        outputStringEnglish =  StringsOfLanguages.ENGLISH.durationTuhrefasid.replace("duration1",
            daysHoursMinutesDigitalEnglish(fixedDurations[index].timeInMilliseconds, typeOfInput)
        )
    }else if (fixedDurations[index].type==DurationType.TUHREFAASID_WITH_ISTEHAZA||
        fixedDurations[index].type==DurationType.TUHREFAASID_MUBTADIA_WITH_ISTEHAZA){
        outputStringUrdu =  StringsOfLanguages.URDU.durationTuhreFasidWithAddition.replace("duration1",
            daysHoursMinutesDigitalUrdu(fixedDurations[index].istihazaAfter, typeOfInput)
        ).replace("duration2", daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds, typeOfInput)).replace("duration3",
            daysHoursMinutesDigitalUrdu((fixedDurations[index].istihazaAfter+fixedDurations[index].timeInMilliseconds), typeOfInput)
        )
        outputStringEnglish =  StringsOfLanguages.ENGLISH.durationTuhreFasidWithAddition.replace("duration1",
            daysHoursMinutesDigitalEnglish(fixedDurations[index].istihazaAfter, typeOfInput)
        ).replace("duration2", daysHoursMinutesDigitalEnglish(fixedDurations[index].timeInMilliseconds, typeOfInput)).replace("duration3",
            daysHoursMinutesDigitalEnglish((fixedDurations[index].istihazaAfter+fixedDurations[index].timeInMilliseconds), typeOfInput)
        )
    }else if (fixedDurations[index].type == DurationType.DAM_IN_NIFAS_PERIOD){
        val sd = fixedDurations[index].startDate
        val et = fixedDurations[index].endDate
        if(fixedDurations[index].days<=40){
            outputStringUrdu = StringsOfLanguages.URDU.durationNifas.replace("duration1",
                daysHoursMinutesDigitalUrdu((difference(sd,et)), typeOfInput)
            )
            outputStringEnglish = StringsOfLanguages.ENGLISH.durationNifas.replace("duration1",
                daysHoursMinutesDigitalEnglish((difference(sd,et)), typeOfInput)
            )
        }else{//more than 40
            outputStringUrdu = StringsOfLanguages.URDU.durationDam.replace("duration1",
                daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds,typeOfInput)
            )
            outputStringEnglish = StringsOfLanguages.ENGLISH.durationDam.replace("duration1",
                daysHoursMinutesDigitalEnglish(fixedDurations[index].timeInMilliseconds,typeOfInput)
            )
        }
    }else if (fixedDurations[index].type == DurationType.TUHR_IN_HAML){
        if(!isMustabeen){
            val time = fixedDurations[index].timeInMilliseconds
            outputStringUrdu =  StringsOfLanguages.URDU.durationPaki.replace("duration1",
                daysHoursMinutesDigitalUrdu(time, typeOfInput)
            )
            outputStringEnglish =  StringsOfLanguages.ENGLISH.durationPaki.replace("duration1",
                daysHoursMinutesDigitalEnglish(time, typeOfInput)
            )
        }
    }else if (fixedDurations[index].type == DurationType.TUHREFAASID_IN_HAML){
        if(!isMustabeen){
                outputStringUrdu =  StringsOfLanguages.URDU.durationTuhreFasidWithAddition.replace("duration1",
                    daysHoursMinutesDigitalUrdu(fixedDurations[index].istihazaAfter, typeOfInput)
                ).replace("duration2",
                    daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds, typeOfInput)
                ).replace("duration3",
                    daysHoursMinutesDigitalUrdu((fixedDurations[index].istihazaAfter+fixedDurations[index].timeInMilliseconds), typeOfInput)
                )
                outputStringEnglish =  StringsOfLanguages.ENGLISH.durationTuhreFasidWithAddition.replace("duration1",
                    daysHoursMinutesDigitalEnglish(fixedDurations[index].istihazaAfter, typeOfInput)
                ).replace("duration2",
                    daysHoursMinutesDigitalEnglish(fixedDurations[index].timeInMilliseconds, typeOfInput)
                ).replace("duration3",
                    daysHoursMinutesDigitalEnglish((fixedDurations[index].istihazaAfter+fixedDurations[index].timeInMilliseconds), typeOfInput)
                )
        }
    }else if (fixedDurations[index].type == DurationType.DAM_IN_HAML){

    }else if (fixedDurations[index].type == DurationType.TUHR_BIGGER_THAN_6_MONTHS){
        outputStringUrdu = StringsOfLanguages.URDU.twomonthstuhr.replace("duration1",
            daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds,typeOfInput)
        )
        outputStringEnglish = StringsOfLanguages.ENGLISH.twomonthstuhr.replace("duration1",
            daysHoursMinutesDigitalEnglish(fixedDurations[index].timeInMilliseconds,typeOfInput)
        )
    }else if(fixedDurations[index].type == DurationType.ISTEHAZA_AYYAMEQABLIYYA){

    }
    return OutputStringsLanguages(outputStringUrdu, outputStringEnglish)
}
fun outputStringHeaderLine(fixedDurations: MutableList<FixedDuration>, index: Int, typeOfInput: TypesOfInputs, mustabeen:Boolean = true):OutputStringsLanguages{
    var outputStringUrdu = ""
    var outputStringEnglish = ""
    if (fixedDurations[index].type==DurationType.DAM||
        fixedDurations[index].type==DurationType.DAM_MUBTADIA){
        val sd:Date = fixedDurations[index].startDate
        val et = fixedDurations[index].endDate
        if(fixedDurations[index].days in 3.0..10.0){//if it's between 3 and 10, write haiz
            outputStringUrdu = StringsOfLanguages.URDU.haizdays.replace("date1", urduDateFormat(sd, typeOfInput)).replace("date2",
                urduDateFormat(et,typeOfInput)
            ).replace("duration1", daysHoursMinutesDigitalUrdu((difference(sd,et)), typeOfInput))
            outputStringEnglish = StringsOfLanguages.ENGLISH.haizdays.replace("date1",
                englishDateFormat(sd, typeOfInput)
            ).replace("date2", englishDateFormat(et,typeOfInput)).replace("duration1",
                daysHoursMinutesDigitalEnglish((difference(sd,et)), typeOfInput)
            )
        }else{//bigger than 10
            if (fixedDurations[index].indices.size>1){//this dam is made up of more than 1
                outputStringUrdu = StringsOfLanguages.URDU.continuosbleeding.replace("date1",
                    urduDateFormat(sd, typeOfInput)
                ).replace("date2", urduDateFormat(et, typeOfInput)).replace("duration1",
                    daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds,typeOfInput)
                )
                outputStringEnglish = StringsOfLanguages.ENGLISH.continuosbleeding.replace("date1",
                    englishDateFormat(sd, typeOfInput)
                ).replace("date2", englishDateFormat(et, typeOfInput)).replace("duration1",
                    daysHoursMinutesDigitalEnglish(fixedDurations[index].timeInMilliseconds,typeOfInput)
                )
            }else{
                outputStringUrdu = StringsOfLanguages.URDU.blooddays.replace("date1", urduDateFormat(sd, typeOfInput)).replace("date2",
                    urduDateFormat(et, typeOfInput)
                ).replace("duration1",
                    daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds,typeOfInput)
                )
                outputStringEnglish = StringsOfLanguages.ENGLISH.blooddays.replace("date1",
                    englishDateFormat(sd, typeOfInput)
                ).replace("date2", englishDateFormat(et, typeOfInput)).replace("duration1",
                    daysHoursMinutesDigitalEnglish(fixedDurations[index].timeInMilliseconds,typeOfInput)
                )
            }
        }

    }else if (fixedDurations[index].type == DurationType.TUHR||
        fixedDurations[index].type == DurationType.TUHR_MUBTADIA||
        fixedDurations[index].type==DurationType.TUHR_MUBTADIA_BECAME_A_MUTADA_NOW){
        val time = fixedDurations[index].timeInMilliseconds
        outputStringUrdu =  StringsOfLanguages.URDU.pakidays.replace("duration1",
            daysHoursMinutesDigitalUrdu(time, typeOfInput)
        )
        outputStringEnglish =  StringsOfLanguages.ENGLISH.pakidays.replace("duration1",
            daysHoursMinutesDigitalEnglish(time, typeOfInput)
        )
        if(fixedDurations[index].type == DurationType.TUHR_MUBTADIA_BECAME_A_MUTADA_NOW){
            outputStringUrdu+=StringsOfLanguages.URDU.becamemutadah
            outputStringEnglish+=StringsOfLanguages.ENGLISH.becamemutadah
        }

    }else if (fixedDurations[index].type == DurationType.TUHREFAASID||fixedDurations[index].type==DurationType.TUHREFAASID_MUBTADIA){
        outputStringUrdu =  StringsOfLanguages.URDU.tuhrfasid.replace("duration1",
            daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds, typeOfInput)
        )
        outputStringEnglish =  StringsOfLanguages.ENGLISH.tuhrfasid.replace("duration1",
            daysHoursMinutesDigitalEnglish(fixedDurations[index].timeInMilliseconds, typeOfInput)
        )
    }else if (fixedDurations[index].type==DurationType.TUHREFAASID_WITH_ISTEHAZA||
        fixedDurations[index].type==DurationType.TUHREFAASID_MUBTADIA_WITH_ISTEHAZA){
        outputStringUrdu =  StringsOfLanguages.URDU.tuhrfasidwithaddition.replace("duration1",
            daysHoursMinutesDigitalUrdu(fixedDurations[index].istihazaAfter, typeOfInput)
        ).replace("duration2", daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds, typeOfInput)).replace("duration3",
            daysHoursMinutesDigitalUrdu((fixedDurations[index].istihazaAfter+fixedDurations[index].timeInMilliseconds), typeOfInput)
        )
        outputStringEnglish =  StringsOfLanguages.ENGLISH.tuhrfasidwithaddition.replace("duration1",
            daysHoursMinutesDigitalEnglish(fixedDurations[index].istihazaAfter, typeOfInput)
        ).replace("duration2", daysHoursMinutesDigitalEnglish(fixedDurations[index].timeInMilliseconds, typeOfInput)).replace("duration3",
            daysHoursMinutesDigitalEnglish((fixedDurations[index].istihazaAfter+fixedDurations[index].timeInMilliseconds), typeOfInput)
        )
    }else if (fixedDurations[index].type == DurationType.DAM_IN_NIFAS_PERIOD){
        val sd = fixedDurations[index].startDate
        val et = fixedDurations[index].endDate
        if(fixedDurations[index].days<=40){
            outputStringUrdu = StringsOfLanguages.URDU.nifasdays.replace("date1", urduDateFormat(sd, typeOfInput)).replace("date2",
                urduDateFormat(et,typeOfInput)
            ).replace("duration1", daysHoursMinutesDigitalUrdu((difference(sd,et)), typeOfInput))
            outputStringEnglish = StringsOfLanguages.ENGLISH.nifasdays.replace("date1",
                englishDateFormat(sd, typeOfInput)
            ).replace("date2", englishDateFormat(et,typeOfInput)).replace("duration1",
                daysHoursMinutesDigitalEnglish((difference(sd,et)), typeOfInput)
            )
        }else{//more than 40
            outputStringUrdu = StringsOfLanguages.URDU.blooddays.replace("date1", urduDateFormat(sd, typeOfInput)).replace("date2",
                urduDateFormat(et, typeOfInput)
            ).replace("duration1", daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds,typeOfInput))
            outputStringEnglish = StringsOfLanguages.ENGLISH.blooddays.replace("date1",
                englishDateFormat(sd, typeOfInput)
            ).replace("date2", englishDateFormat(et, typeOfInput)).replace("duration1",
                daysHoursMinutesDigitalEnglish(fixedDurations[index].timeInMilliseconds,typeOfInput)
            )
        }
    }else if (fixedDurations[index].type == DurationType.TUHR_IN_HAML){
        if(!mustabeen){
            val time = fixedDurations[index].timeInMilliseconds
            outputStringUrdu =  StringsOfLanguages.URDU.pakidays.replace("duration1",
                daysHoursMinutesDigitalUrdu(time, typeOfInput)
            )
            outputStringEnglish =  StringsOfLanguages.ENGLISH.pakidays.replace("duration1",
                daysHoursMinutesDigitalEnglish(time, typeOfInput)
            )
        }
    }else if (fixedDurations[index].type == DurationType.TUHREFAASID_IN_HAML){
        if(!mustabeen){
            outputStringUrdu =  StringsOfLanguages.URDU.tuhrfasidwithaddition.replace("duration1",
                daysHoursMinutesDigitalUrdu(fixedDurations[index].istihazaAfter, typeOfInput)
            ).replace("duration2", daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds, typeOfInput)).replace("duration3",
                daysHoursMinutesDigitalUrdu((fixedDurations[index].istihazaAfter+fixedDurations[index].timeInMilliseconds), typeOfInput)
            )
            outputStringEnglish =  StringsOfLanguages.ENGLISH.tuhrfasidwithaddition.replace("duration1",
                daysHoursMinutesDigitalEnglish(fixedDurations[index].istihazaAfter, typeOfInput)
            ).replace("duration2",
                daysHoursMinutesDigitalEnglish(fixedDurations[index].timeInMilliseconds, typeOfInput)
            ).replace("duration3",
                daysHoursMinutesDigitalEnglish((fixedDurations[index].istihazaAfter+fixedDurations[index].timeInMilliseconds), typeOfInput)
            )
        }
    }else if (fixedDurations[index].type == DurationType.DAM_IN_HAML){

    }else if (fixedDurations[index].type == DurationType.TUHR_BIGGER_THAN_6_MONTHS){
        outputStringUrdu = StringsOfLanguages.URDU.twomonthstuhr.replace("duration1",
            daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds,typeOfInput)
        )
        outputStringEnglish = StringsOfLanguages.ENGLISH.twomonthstuhr.replace("duration1",
            daysHoursMinutesDigitalEnglish(fixedDurations[index].timeInMilliseconds,typeOfInput)
        )
    }else if (fixedDurations[index].type == DurationType.ISTEHAZA_AYYAMEQABLIYYA){
        val sd = fixedDurations[index].startDate
        val et = fixedDurations[index].endDate
        outputStringUrdu = StringsOfLanguages.URDU.daysayyameqabliyya.replace("date1", urduDateFormat(sd, typeOfInput)).replace("date2",
            urduDateFormat(et,typeOfInput)
        ).replace("duration1", daysHoursMinutesDigitalUrdu((difference(sd,et)), typeOfInput))
        outputStringEnglish = StringsOfLanguages.ENGLISH.daysayyameqabliyya.replace("date1",
            englishDateFormat(sd, typeOfInput)
        ).replace("date2", englishDateFormat(et,typeOfInput)).replace("duration1",
            daysHoursMinutesDigitalEnglish((difference(sd,et)), typeOfInput)
        )
    }
    return OutputStringsLanguages(outputStringUrdu,outputStringEnglish)
}

//fun outputStringHeaderLine(fixedDurations: MutableList<FixedDuration>, index:Int, isDateOnly: Boolean):String{
//    if((index +1)< fixedDurations.size && fixedDurations[index+1].istihazaAfter>0){
//        return "<b>${daysHoursMinutesDigitalEnglish(fixedDurations[index].timeInMilliseconds,isDateOnly)} ${fixedDurations[index].type}</b>\n"
//    }else{
//        return "<b>${daysHoursMinutesDigitalEnglish(fixedDurations[index].timeInMilliseconds,isDateOnly)} ${fixedDurations[index].type}</b>\n"
//
//    }
//}
//
//fun outputStringSumOfIndicesLine(fixedDurations: MutableList<FixedDuration>, durations:List<Duration>, index:Int, isDateOnly: Boolean):String{
//    if(fixedDurations[index].indices.size>1){
//        var sum = 0L
//        var str = ""
//        for (index in fixedDurations[index].indices){
//            sum+=durations[index].timeInMilliseconds
//            str += " + ${daysHoursMinutesDigitalEnglish(durations[index].timeInMilliseconds,isDateOnly)}"
//        }
//        str=str.removePrefix(" + ")
//        return "\t${str} = ${daysHoursMinutesDigitalEnglish(sum,isDateOnly)}\n"
//    }else{
//        return ""
//    }
//}
//
//fun outputStringIstihazaAfterLine(fixedDurations: MutableList<FixedDuration>,index: Int, isDateOnly: Boolean):String{
//    val istihazaAfter = fixedDurations[index].istihazaAfter
//    var str = ""
//    if(istihazaAfter!=0L){
//        str +="\t${daysHoursMinutesDigitalEnglish(fixedDurations[index].timeInMilliseconds,isDateOnly)} " +
//                "tuhr + ${daysHoursMinutesDigitalEnglish(istihazaAfter,isDateOnly)} istihaza " +
//                "= ${daysHoursMinutesDigitalEnglish((fixedDurations[index].timeInMilliseconds +
//                        fixedDurations[index].istihazaAfter),isDateOnly)} tuhr-e-faasid\n"
//    }
//
//    return str
//}
//
//fun outputStringBiggerThan10Hall(fixedDurations: MutableList<FixedDuration>,index:Int, isDateOnly: Boolean):String{
//    val mp = fixedDurations[index].biggerThanTen?.mp ?: return ""
//    val gp = fixedDurations[index].biggerThanTen?.gp ?: return ""
//    val dm = fixedDurations[index].biggerThanTen?.dm ?: return ""
//    val hz = fixedDurations[index].biggerThanTen?.hz ?: return ""
//    val qism = fixedDurations[index].biggerThanTen?.qism ?: return ""
//    val istihazaBefore = fixedDurations[index].biggerThanTen?.istihazaBefore ?: return ""
//    val haiz = fixedDurations[index].biggerThanTen?.haiz ?: return ""
//    val istihazaAfter = fixedDurations[index].biggerThanTen?.istihazaAfter ?: return ""
//    val aadatTuhr = fixedDurations[index].biggerThanTen?.aadatTuhr ?: return ""
//    val aadatHaz = fixedDurations[index].biggerThanTen?.aadatHaiz ?: return ""
//
//
//    //output hukm:
//    var str =""
////    var str = "Rough work \n"
////    str += "MP\tGP\tDm\tHz\tQism\n"
//
//    str += "\t${daysHoursMinutesDigitalEnglish(mp,isDateOnly)}\t${daysHoursMinutesDigitalEnglish(gp,isDateOnly)}\t" +
//            "${daysHoursMinutesDigitalEnglish(dm,isDateOnly)}\t${daysHoursMinutesDigitalEnglish(hz,isDateOnly)}\t${qism}\n"
//
//    str +="\tAadat: ${daysHoursMinutesDigitalEnglish(aadatHaz,isDateOnly)}/${daysHoursMinutesDigitalEnglish(aadatTuhr,isDateOnly)}\n"
//
//    str += "\tOut of ${daysHoursMinutesDigitalEnglish(dm,isDateOnly)}, the first "
//
//    if (istihazaBefore>0){
//        str += "${daysHoursMinutesDigitalEnglish(istihazaBefore,isDateOnly)} are istihaza, then the next "
//    }
//    str += "${daysHoursMinutesDigitalEnglish(haiz,isDateOnly)} are haiz, "
//
//    //if istihazaAfter is bigger than addatTuhr +3, run daur
//    if (istihazaAfter>=aadatTuhr+3){
//        //find quotient and remainder
//        val remainder = istihazaAfter%(aadatHaz+aadatTuhr)
//        val quotient = ((istihazaAfter-remainder)/(aadatHaz+aadatTuhr))
//
//        if(remainder == 0L){
//            for (j in 1 until quotient){
//                str+="then the next ${daysHoursMinutesDigitalEnglish(aadatTuhr,isDateOnly)} are istihaza, " +
//                        "then the next ${daysHoursMinutesDigitalEnglish(aadatHaz,isDateOnly)} are haiz, "
//            }
//            str+="then the next ${daysHoursMinutesDigitalEnglish(aadatTuhr,isDateOnly)} are istihaza, " +
//                    "then the last ${daysHoursMinutesDigitalEnglish(aadatHaz,isDateOnly)} are haiz. "
//
//        }else{//remainder exists
//            for (j in 1 .. quotient){
//                str+="then the next ${daysHoursMinutesDigitalEnglish(aadatTuhr,isDateOnly)} are istihaza, " +
//                        "then the next ${daysHoursMinutesDigitalEnglish(aadatHaz,isDateOnly)} are haiz, "
//            }
//            if (remainder<aadatTuhr + 3){//it ended in tuhr
//                str+="then the last ${daysHoursMinutesDigitalEnglish(remainder,isDateOnly)} are istihaza.\n"
//
//            }else{//it ended in haiz
//                str+="then the next ${daysHoursMinutesDigitalEnglish(aadatTuhr,isDateOnly)} are tuhr, " +
//                        "then the last ${daysHoursMinutesDigitalEnglish(remainder-aadatTuhr,isDateOnly)} are haiz\n"
//                //change aadatHaiz
//                val newAadatHaz = remainder-aadatTuhr
//                //add aadat line
//                str+="\tAadat: ${daysHoursMinutesDigitalEnglish(newAadatHaz,isDateOnly)}/${daysHoursMinutesDigitalEnglish(aadatTuhr,isDateOnly)}\n"
//
//            }
//        }
//    }else{
//        str += "and the last ${daysHoursMinutesDigitalEnglish(istihazaAfter,isDateOnly)} are istihaza.\n"
//
//    }
//
//    str+="\t\n"
//
//    //output hukm in dates
//    val istihazaBeforeStartDate:Date = fixedDurations[index].startDate
//    val haizStartDate = addTimeToDate(istihazaBeforeStartDate, istihazaBefore)
//    val istihazaAfterStartDate = addTimeToDate(haizStartDate, haiz)
//    val istihazaAfterEndDate = addTimeToDate(istihazaAfterStartDate, istihazaAfter)
//
//    if(istihazaBefore!=0L){
//        str+="\tFrom ${englishDateFormat(istihazaBeforeStartDate, isDateOnly)} to ${englishDateFormat(haizStartDate, isDateOnly)} is istihaza, yaqeeni paki\n"
//    }
//    str+="\tFrom ${englishDateFormat(haizStartDate, isDateOnly)} to ${englishDateFormat(istihazaAfterStartDate, isDateOnly)} is haiz\n"
//    if(istihazaAfter!=0L){
//        if (istihazaAfter>=aadatTuhr+3){
//            //find quotient and remainder
//            val remainder = istihazaAfter%(aadatHaz+aadatTuhr)
//            val quotient = ((istihazaAfter-remainder)/(aadatHaz+aadatTuhr))
//
//
//            var aadatTuhrStartDate:Date = istihazaAfterStartDate
//            var aadatTuhrEndDate:Date
//            var aadatHaizEndDate:Date
//            for (j in 1 .. quotient){
//                aadatTuhrEndDate = addTimeToDate(aadatTuhrStartDate,aadatTuhr)
//                aadatHaizEndDate = addTimeToDate(aadatTuhrEndDate,aadatHaz)
//                str+= "\tFrom ${englishDateFormat(aadatTuhrStartDate, isDateOnly)} to ${englishDateFormat(aadatTuhrEndDate, isDateOnly)} is istihaza, yaqeeni paki\n"
//                str+= "\tFrom ${englishDateFormat(aadatTuhrEndDate, isDateOnly)} to ${englishDateFormat(aadatHaizEndDate, isDateOnly)} is haiz\n"
//                aadatTuhrStartDate=aadatHaizEndDate
//            }
//            if (remainder<aadatTuhr + 3 && remainder!=0L){//it ended in tuhr
//                str+= "\tFrom ${englishDateFormat(aadatTuhrStartDate, isDateOnly)} to ${englishDateFormat(istihazaAfterEndDate, isDateOnly)} is istihaza, yaqeeni paki\n"
//
//            }else{//it ended in haiz or remainder is 0
//                aadatTuhrEndDate = addTimeToDate(aadatTuhrStartDate,aadatTuhr)
//                str+= "\tFrom ${englishDateFormat(aadatTuhrStartDate, isDateOnly)} to ${englishDateFormat(aadatTuhrEndDate, isDateOnly)} is istihaza, yaqeeni paki\n"
//                str+= "\tFrom ${englishDateFormat(aadatTuhrEndDate, isDateOnly)} to ${englishDateFormat(istihazaAfterEndDate, isDateOnly)} is haiz\n"
//
//                //change aadatHaiz if remainder is not zero (if it is zero, aadat doesn't change, so shouldn't be printed
//                if (remainder!=0L){
//                    val newAadatHaz1 = remainder-aadatTuhr
//                    //add aadat line
//                    str+="\tAadat: ${daysHoursMinutesDigitalEnglish(newAadatHaz1,isDateOnly)}/${daysHoursMinutesDigitalEnglish(aadatTuhr,isDateOnly)}\n"
//                }
//           }
//
//        }else{//no duar
//            str+="\tFrom ${englishDateFormat(istihazaAfterStartDate, isDateOnly)} to ${englishDateFormat(istihazaAfterEndDate, isDateOnly)} is istihaza, yaqeeni paki\n"
//
//        }
//    }
//   return str
//}

fun generateGetDifferenceString(durationTypes:MutableList<DurationTypes>):String {
    var str = ""
    for( durationType in durationTypes){
        var type = ""
        if (durationType.type==DateTypes.YAQEENI_PAKI){type="yaqeeni paki"}
        else if (durationType.type==DateTypes.YAQEENI_NA_PAKI){type="yaqeeni na paki"}
        else if (durationType.type==DateTypes.AYYAAM_E_SHAKK_KHUROOJ){type="shakk fil khurooj"}
        else if (durationType.type==DateTypes.AYYAAM_E_SHAKK_DUKHOOL){type="shakk fil dukhool"}
        val startTime=durationType.startTime
        val endTime = durationType.endTime

        if(durationType.type==DateTypes.AYYAAM_E_SHAKK_DUKHOOL){
            str += "${UnicodeChars.YELLOW_CIRCLE} <b><em>From ${englishDateFormat(startTime,TypesOfInputs.DATE_ONLY)} to ${englishDateFormat(endTime,TypesOfInputs.DATE_ONLY)} is ${type}</em></b>\n\n"
        }else if (durationType.type==DateTypes.YAQEENI_PAKI){
            str += "${UnicodeChars.WHITE_CIRCLE} From ${englishDateFormat(startTime,TypesOfInputs.DATE_ONLY)} to ${englishDateFormat(endTime,TypesOfInputs.DATE_ONLY)} is ${type}\n\n"
        }else if (durationType.type==DateTypes.YAQEENI_NA_PAKI){
            str += "${UnicodeChars.RED_CIRCLE} From ${englishDateFormat(startTime,TypesOfInputs.DATE_ONLY)} to ${englishDateFormat(endTime,TypesOfInputs.DATE_ONLY)} is ${type}\n\n"
        }else if (durationType.type==DateTypes.AYYAAM_E_SHAKK_KHUROOJ){
            str += "${UnicodeChars.GREEN_CIRCLE} <b><em>From ${englishDateFormat(startTime,TypesOfInputs.DATE_ONLY)} to ${englishDateFormat(endTime,TypesOfInputs.DATE_ONLY)} is ${type}</em></b>\n\n"
        }

    }
    return str
}




