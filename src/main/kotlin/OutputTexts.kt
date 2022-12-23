@file:Suppress("SpellCheckingInspection")
import kotlin.js.Date

fun addPreMaslaValuesText(preMaslaValues: PreMaslaValues):OutputStringsLanguages{
    var englishStr = ""
    var urduStr = ""
    if(preMaslaValues.inputtedAadatHaiz!=null &&
        preMaslaValues.inputtedAadatTuhr!=null &&
        preMaslaValues.inputtedMawjoodahTuhr!=null){//we have all 3 pre masla values

        //add line about previous habits
        urduStr += StringsOfLanguages.URDU.preMaslaHabitOfHaizAndTuhr
            .replace("duration1", daysHoursMinutesDigital(preMaslaValues.inputtedAadatHaiz!!,TypesOfInputs.DURATION,Vls.Langs.URDU))
            .replace("duration2", daysHoursMinutesDigital(preMaslaValues.inputtedAadatTuhr!!,TypesOfInputs.DURATION,Vls.Langs.URDU))

        englishStr += StringsOfLanguages.ENGLISH.preMaslaHabitOfHaizAndTuhr
            .replace("duration1", daysHoursMinutesDigital(preMaslaValues.inputtedAadatHaiz!!,TypesOfInputs.DURATION,Vls.Langs.ENGLISH))
            .replace("duration2", daysHoursMinutesDigital(preMaslaValues.inputtedAadatTuhr!!,TypesOfInputs.DURATION,Vls.Langs.ENGLISH))

        //add line about mawjooda paki
        urduStr += StringsOfLanguages.URDU.preMaslaValueOfMawjoodaPaki
            .replace("duration1", daysHoursMinutesDigital(preMaslaValues.inputtedMawjoodahTuhr!!,TypesOfInputs.DURATION,Vls.Langs.URDU))

        englishStr += StringsOfLanguages.ENGLISH.preMaslaValueOfMawjoodaPaki
            .replace("duration1", daysHoursMinutesDigital(preMaslaValues.inputtedMawjoodahTuhr!!,TypesOfInputs.DURATION,Vls.Langs.ENGLISH))
        //remove the word fasid or invalid, if tuhr was saheeh
        if(!preMaslaValues.isMawjoodaFasid){//if tuhr is not fasid
            urduStr=urduStr.replace("فاسد ", "")
            englishStr=englishStr.replace("invalid ", "")
        }
    }
    return OutputStringsLanguages(urduStr,englishStr)
}

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
                                 typeOfInput: TypesOfInputs, preMaslaValues: PreMaslaValues):OutputTexts{
    val hazDatesList = getHaizDatesList(fixedDurations)
    val urduStr = generateOutputString(fixedDurations, endingOutputValues, typeOfInput, TypesOfMasla.MUBTADIA, preMaslaValues).urduString
    val englishStr = generateOutputString(fixedDurations, endingOutputValues, typeOfInput, TypesOfMasla.MUBTADIA, preMaslaValues).englishString

    val hazDatesStr = generateHazDatesStr(hazDatesList,typeOfInput)

    return OutputTexts(englishStr,urduStr, hazDatesStr, hazDatesList,endingOutputValues, fixedDurations)
}
fun generateOutputStringMutadah(fixedDurations: MutableList<FixedDuration>,
                                endingOutputValues: EndingOutputValues,
                                typeOfInput: TypesOfInputs,
                                preMaslaValues: PreMaslaValues):OutputTexts{
    val hazDatesList = getHaizDatesList(fixedDurations)

    val urduStr = generateOutputString(fixedDurations, endingOutputValues, typeOfInput, TypesOfMasla.MUTADAH, preMaslaValues).urduString
    val englishStr = generateOutputString(fixedDurations, endingOutputValues, typeOfInput, TypesOfMasla.MUTADAH, preMaslaValues).englishString

    val hazDatesStr = generateHazDatesStr(hazDatesList,typeOfInput)

    return OutputTexts(englishStr,urduStr, hazDatesStr, hazDatesList,endingOutputValues, fixedDurations)
}

fun generateHazDatesStr(hazDatesList: MutableList<Entry>,typeOfInput: TypesOfInputs):String{
    val lang = Vls.Langs.ENGLISH
    var str = ""
    for(entry in hazDatesList){
        str+="From ${languagedDateFormat(entry.startTime,typeOfInput,lang)} to ${languagedDateFormat(entry.endTime,typeOfInput, lang)}<br>"
    }
    return str
}

fun generateLanguagedOutputStringPregnancy(fixedDurations: MutableList<FixedDuration>, pregnancy: Pregnancy, endingOutputValues: EndingOutputValues, typeOfInput: TypesOfInputs):OutputStringsLanguages{
    val mustabeen = pregnancy.mustabeenUlKhilqat
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
                    strUrdu += StringsOfLanguages.URDU.birth.replace("date1", languagedDateFormat(birthTime, typeOfInput, Vls.Langs.URDU))
                    strEnglish += StringsOfLanguages.ENGLISH.birth.replace("date1",
                        languagedDateFormat(birthTime, typeOfInput, Vls.Langs.ENGLISH)
                    )
                    if(index<fixedDurations.size-2){//if there is something after wiladat
                        strUrdu += StringsOfLanguages.URDU.afterpregheader
                        strEnglish += StringsOfLanguages.ENGLISH.afterpregheader
                    }
                }

            }

        }
        strUrdu += outputStringFinalLines(endingOutputValues, typeOfInput).urduString
        strEnglish += outputStringFinalLines(endingOutputValues, typeOfInput).englishString



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
                        languagedDateFormat(birthTime, typeOfInput, Vls.Langs.URDU)
                    )
                    strEnglish += StringsOfLanguages.ENGLISH.earlymiscarriage.replace("date1",
                        languagedDateFormat(birthTime, typeOfInput, Vls.Langs.ENGLISH)
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

fun generateOutputString(fixedDurations: MutableList<FixedDuration>, endingOutputValues: EndingOutputValues, typeOfInput: TypesOfInputs, typesOfMasla: TypesOfMasla, preMaslaValues: PreMaslaValues):OutputStringsLanguages{
    //ToDo: figure out how to do languages for real
    var strUrdu = StringsOfLanguages.URDU.answer //جواب:
    var strEnglish = StringsOfLanguages.ENGLISH.answer //جواب:

    strUrdu+= addPreMaslaValuesText(preMaslaValues).urduString
    strEnglish += addPreMaslaValuesText(preMaslaValues).englishString

    if(typesOfMasla==TypesOfMasla.MUTADAH){
        strUrdu += StringsOfLanguages.URDU.headerline //اس ترتیب سے خون آیا اور پاکی ملی
        strEnglish += StringsOfLanguages.ENGLISH.headerline //اس ترتیب سے خون آیا اور پاکی ملی
    }else if(typesOfMasla==TypesOfMasla.MUBTADIA){
        strUrdu += StringsOfLanguages.URDU.headerlinemubtadia //اس ترتیب سے خون آیا اور پاکی ملی
        strEnglish += StringsOfLanguages.ENGLISH.headerlinemubtadia //اس ترتیب سے خون آیا اور پاکی ملی
    }

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
        }else{//not durations
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

fun outputStringFinalLines(endingOutputValues: EndingOutputValues, typeOfInput: TypesOfInputs):OutputStringsLanguages{

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
                strUrdu += StringsOfLanguages.URDU.haizend.replace("date1", languagedDateFormat(date, typeOfInput, Vls.Langs.URDU))
                strEnglish += StringsOfLanguages.ENGLISH.haizend.replace("date1", languagedDateFormat(date, typeOfInput, Vls.Langs.ENGLISH))
            }
            TypesOfFutureDates.END_OF_AADAT_TUHR -> {
                strUrdu += StringsOfLanguages.URDU.endofpaki.replace("date1", languagedDateFormat(date, typeOfInput, Vls.Langs.URDU))
                strEnglish += StringsOfLanguages.ENGLISH.endofpaki.replace("date1", languagedDateFormat(date, typeOfInput, Vls.Langs.ENGLISH))
            }
            TypesOfFutureDates.IC_FORBIDDEN_DATE -> {
                strUrdu += StringsOfLanguages.URDU.sexnotallowed.replace("date1", languagedDateFormat(date, typeOfInput, Vls.Langs.URDU))
                strEnglish += StringsOfLanguages.ENGLISH.sexnotallowed.replace("date1",
                    languagedDateFormat(date, typeOfInput, Vls.Langs.ENGLISH)
                )
            }
            TypesOfFutureDates.AFTER_TEN_DAYS -> {
                strUrdu += StringsOfLanguages.URDU.aftertendays.replace("date1", languagedDateFormat(date, typeOfInput, Vls.Langs.URDU))
                strEnglish += StringsOfLanguages.ENGLISH.aftertendays.replace("date1", languagedDateFormat(date, typeOfInput, Vls.Langs.ENGLISH))
            }
            TypesOfFutureDates.FORTY_DAYS -> {
                strUrdu += StringsOfLanguages.URDU.afterfortydays.replace("date1", languagedDateFormat(date, typeOfInput, Vls.Langs.URDU))
                strEnglish += StringsOfLanguages.ENGLISH.afterfortydays.replace("date1",
                    languagedDateFormat(date, typeOfInput, Vls.Langs.ENGLISH)
                )
            }
            TypesOfFutureDates.BEFORE_THREE_DAYS_MASLA_WILL_CHANGE -> {
                strUrdu += StringsOfLanguages.URDU.bleedingstopsbeforethreemaslachanges.replace("date1",
                    languagedDateFormat(date, typeOfInput, Vls.Langs.URDU)
                )
                strEnglish += StringsOfLanguages.ENGLISH.bleedingstopsbeforethreemaslachanges.replace("date1",
                    languagedDateFormat(date, typeOfInput, Vls.Langs.ENGLISH)
                )
            }
            TypesOfFutureDates.BEFORE_THREE_DAYS -> {
                strUrdu += StringsOfLanguages.URDU.bleedingstopsbeforethree.replace("date1",
                    languagedDateFormat(date, typeOfInput, Vls.Langs.URDU)
                )
                strEnglish += StringsOfLanguages.ENGLISH.bleedingstopsbeforethree.replace("date1",
                    languagedDateFormat(date, typeOfInput, Vls.Langs.ENGLISH)
                )
            }
            TypesOfFutureDates.IHTIYATI_GHUSL -> {
                strUrdu += StringsOfLanguages.URDU.ihtiyatighusl.replace("date1", languagedDateFormat(date, typeOfInput, Vls.Langs.URDU))
                strEnglish += StringsOfLanguages.ENGLISH.ihtiyatighusl.replace("date1",
                    languagedDateFormat(date, typeOfInput, Vls.Langs.ENGLISH)
                )
            }
            TypesOfFutureDates.A3_CHANGING_TO_A2 -> {
                strUrdu += StringsOfLanguages.URDU.situationmaychange.replace("date1", languagedDateFormat(date, typeOfInput, Vls.Langs.URDU))
                strEnglish += StringsOfLanguages.ENGLISH.situationmaychange.replace("date1",
                    languagedDateFormat(date, typeOfInput, Vls.Langs.ENGLISH)
                )
            }
            TypesOfFutureDates.BEFORE_TEN_DAYS_AYYAMEQABLIYYAH -> {
                strUrdu += StringsOfLanguages.URDU.beforetendaysayyameqabliyyaallconsideredhaiz.replace("date1",
                    languagedDateFormat(date, typeOfInput, Vls.Langs.URDU)
                )
                strEnglish += StringsOfLanguages.ENGLISH.beforetendaysayyameqabliyyaallconsideredhaiz.replace("date1",
                    languagedDateFormat(date, typeOfInput, Vls.Langs.ENGLISH)
                )
            }
            TypesOfFutureDates.START_OF_AADAT_AYYAMEQABLIYYA -> {
                strUrdu += StringsOfLanguages.URDU.endofistehazaayyameqabliyya.replace("date1",
                    languagedDateFormat(date, typeOfInput, Vls.Langs.URDU)
                )
                strEnglish += StringsOfLanguages.ENGLISH.endofistehazaayyameqabliyya.replace("date1",
                    languagedDateFormat(date, typeOfInput, Vls.Langs.ENGLISH)
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
                    .replace("duration1", daysHoursMinutesDigital(aadats.aadatNifas!!, typeOfInput, Vls.Langs.URDU))
                strEnglish+= StringsOfLanguages.ENGLISH.onlynifashabit
                    .replace("duration1", daysHoursMinutesDigital(aadats.aadatNifas!!, typeOfInput, Vls.Langs.ENGLISH))

            }else {//adat nifas doesn't exists
                strUrdu += StringsOfLanguages.URDU.thereisnoaadat
                strEnglish += StringsOfLanguages.ENGLISH.thereisnoaadat
            }
        }else if(aadatHaiz!=-1L && aadatTuhr==-1L) {//aadat of haiz exists, but not aadat of tuhr
            if (aadats.aadatNifas != null && aadats.aadatNifas != -1L) {//adat nifas exists
                strUrdu += StringsOfLanguages.URDU.nifasAndHaizHabit
                    .replace("duration1", daysHoursMinutesDigital(aadatHaiz, typeOfInput, Vls.Langs.URDU))
                    .replace("duration2", daysHoursMinutesDigital(aadats.aadatNifas!!, typeOfInput, Vls.Langs.URDU))
                strEnglish += StringsOfLanguages.ENGLISH.nifasAndHaizHabit
                    .replace("duration1", daysHoursMinutesDigital(aadatHaiz, typeOfInput, Vls.Langs.ENGLISH))
                    .replace("duration2", daysHoursMinutesDigital(aadats.aadatNifas!!, typeOfInput, Vls.Langs.ENGLISH))

            } else {//adat nifas doesn't exists
                strUrdu += StringsOfLanguages.URDU.aadatofhaizonly
                    .replace("duration1", daysHoursMinutesDigital(aadatHaiz, typeOfInput, Vls.Langs.URDU))
                strEnglish += StringsOfLanguages.ENGLISH.aadatofhaizonly
                    .replace("duration1", daysHoursMinutesDigital(aadatHaiz, typeOfInput, Vls.Langs.ENGLISH))
            }
        }else if(aadatHaiz==-1L && aadatTuhr!=-1L){//aadat tuhr exist and aadat haiz doesn;t exist
            if (aadats.aadatNifas != null && aadats.aadatNifas != -1L) {//adat nifas exists
                strUrdu += StringsOfLanguages.URDU.nifasAndTuhrHabit
                    .replace("duration1", daysHoursMinutesDigital(aadatTuhr, typeOfInput, Vls.Langs.URDU))
                    .replace("duration2", daysHoursMinutesDigital(aadats.aadatNifas!!, typeOfInput, Vls.Langs.URDU))
                strEnglish += StringsOfLanguages.ENGLISH.nifasAndTuhrHabit
                    .replace("duration1", daysHoursMinutesDigital(aadatTuhr, typeOfInput, Vls.Langs.ENGLISH))
                    .replace("duration2", daysHoursMinutesDigital(aadats.aadatNifas!!, typeOfInput, Vls.Langs.ENGLISH))

            }else{
                strUrdu += StringsOfLanguages.URDU.onlyTuhrHabit
                    .replace("duration1", daysHoursMinutesDigital(aadatTuhr, typeOfInput, Vls.Langs.URDU))
                strEnglish += StringsOfLanguages.ENGLISH.onlyTuhrHabit
                    .replace("duration1", daysHoursMinutesDigital(aadatTuhr, typeOfInput, Vls.Langs.ENGLISH))

            }
        }else{//adats of haiz and tuhr exist
            if(aadats.aadatNifas != null && aadats.aadatNifas!=-1L){//adat nifas exists
                strUrdu+= StringsOfLanguages.URDU.habitwithnifas
                    .replace("duration1", daysHoursMinutesDigital(aadatHaiz, typeOfInput, Vls.Langs.URDU))
                    .replace("duration2", daysHoursMinutesDigital(aadatTuhr, typeOfInput, Vls.Langs.URDU))
                    .replace("duration3", daysHoursMinutesDigital(aadats.aadatNifas!!, typeOfInput, Vls.Langs.URDU))
                strEnglish+= StringsOfLanguages.ENGLISH.habitwithnifas
                    .replace("duration1", daysHoursMinutesDigital(aadatHaiz, typeOfInput, Vls.Langs.ENGLISH))
                    .replace("duration2", daysHoursMinutesDigital(aadatTuhr, typeOfInput, Vls.Langs.ENGLISH))
                    .replace("duration3", daysHoursMinutesDigital(aadats.aadatNifas!!, typeOfInput, Vls.Langs.ENGLISH))
            }else{//adat nifas doesn't exists
                strUrdu+= StringsOfLanguages.URDU.habit
                    .replace("duration1", daysHoursMinutesDigital(aadatHaiz, typeOfInput, Vls.Langs.URDU))
                    .replace("duration2", daysHoursMinutesDigital(aadatTuhr, typeOfInput, Vls.Langs.URDU))
                strEnglish+= StringsOfLanguages.ENGLISH.habit
                    .replace("duration1", daysHoursMinutesDigital(aadatHaiz, typeOfInput, Vls.Langs.ENGLISH))
                    .replace("duration2", daysHoursMinutesDigital(aadatTuhr, typeOfInput, Vls.Langs.ENGLISH))
            }
        }
        OutputStringsLanguages(strUrdu, strEnglish)
    }
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
                    daysHoursMinutesDigital(duration.timeInMilliseconds, typeOfInput, Vls.Langs.URDU)
                )
                strEnglish+= StringsOfLanguages.ENGLISH.startingFromIstehaza.replace("duration1",
                    daysHoursMinutesDigital(duration.timeInMilliseconds, typeOfInput, Vls.Langs.ENGLISH)
                )

            }else if(duration.type == DurationType.HAIZ){
                if(duration==fixedDurations[index].biggerThanTen!!.durationsList[0]){
                    strUrdu+= StringsOfLanguages.URDU.startingFromHaiz.replace("duration1",
                        daysHoursMinutesDigital(duration.timeInMilliseconds, typeOfInput, Vls.Langs.URDU)
                    )
                    strEnglish+= StringsOfLanguages.ENGLISH.startingFromHaiz.replace("duration1",
                        daysHoursMinutesDigital(duration.timeInMilliseconds, typeOfInput, Vls.Langs.ENGLISH)
                    )
                }else{
                    strUrdu+= StringsOfLanguages.URDU.followedByHaizAfter.replace("duration1",
                        daysHoursMinutesDigital(duration.timeInMilliseconds, typeOfInput, Vls.Langs.URDU)
                    )
                    strEnglish+= StringsOfLanguages.ENGLISH.followedByHaizAfter.replace("duration1",
                        daysHoursMinutesDigital(duration.timeInMilliseconds, typeOfInput, Vls.Langs.ENGLISH)
                    )
                }
            }else if(duration.type == DurationType.ISTIHAZA_AFTER){
                strUrdu+= StringsOfLanguages.URDU.followedByistehazaAfter.replace("duration1",
                    daysHoursMinutesDigital(duration.timeInMilliseconds, typeOfInput, Vls.Langs.URDU)
                )
                strEnglish+= StringsOfLanguages.ENGLISH.followedByistehazaAfter.replace("duration1",
                    daysHoursMinutesDigital(duration.timeInMilliseconds, typeOfInput, Vls.Langs.ENGLISH)
                )

            }else if(duration.type == DurationType.LESS_THAN_3_HAIZ){
                strUrdu+= StringsOfLanguages.URDU.followedByHaizAfter.replace("duration1",
                    daysHoursMinutesDigital(duration.timeInMilliseconds, typeOfInput, Vls.Langs.URDU)
                )
                strEnglish+= StringsOfLanguages.ENGLISH.followedByHaizAfter.replace("duration1",
                    daysHoursMinutesDigital(duration.timeInMilliseconds, typeOfInput, Vls.Langs.ENGLISH)
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
            StringsOfLanguages.URDU.haizdaysinsolution.replace("date1", languagedDateFormat(sd, typeOfInput, Vls.Langs.URDU)).replace("date2",
                languagedDateFormat(ed,typeOfInput, Vls.Langs.URDU)
            ).replace("duration1", daysHoursMinutesDigital((difference(sd,ed)), typeOfInput, Vls.Langs.URDU)),
            StringsOfLanguages.ENGLISH.haizdaysinsolution.replace("date1", languagedDateFormat(sd, typeOfInput, Vls.Langs.ENGLISH)).replace("date2",
                languagedDateFormat(ed,typeOfInput, Vls.Langs.ENGLISH)
            ).replace("duration1", daysHoursMinutesDigital((difference(sd,ed)), typeOfInput, Vls.Langs.ENGLISH))
        )
    }
    fun istihazaLine(sd:Date, ed:Date, typeOfInput: TypesOfInputs):OutputStringsLanguages{
        return OutputStringsLanguages(
            StringsOfLanguages.URDU.istihazadays.replace("date1", languagedDateFormat(sd, typeOfInput, Vls.Langs.URDU)).replace("date2",
                languagedDateFormat(ed,typeOfInput, Vls.Langs.URDU)
            ).replace("duration1", daysHoursMinutesDigital(difference(sd,ed), typeOfInput, Vls.Langs.URDU)),
            StringsOfLanguages.ENGLISH.istihazadays.replace("date1", languagedDateFormat(sd, typeOfInput, Vls.Langs.ENGLISH)).replace("date2",
                languagedDateFormat(ed,typeOfInput, Vls.Langs.ENGLISH)
            ).replace("duration1", daysHoursMinutesDigital(difference(sd,ed), typeOfInput, Vls.Langs.ENGLISH))

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
                        languagedDateFormat(duration.startTime,typeOfInput, Vls.Langs.URDU)
                    )
                    strEnglish+= StringsOfLanguages.ENGLISH.istihazadetailslinetwo.replace("date1",
                        languagedDateFormat(duration.startTime,typeOfInput, Vls.Langs.ENGLISH)
                    )
                    strUrdu+= StringsOfLanguages.URDU.istihazadetailslineone
                    strEnglish+= StringsOfLanguages.ENGLISH.istihazadetailslineone

                }
                DurationType.LESS_THAN_3_HAIZ -> {
                    strUrdu+= haizLine(duration.startTime,duration.endDate,typeOfInput).urduString
                    strEnglish+= haizLine(duration.startTime,duration.endDate,typeOfInput).englishString
                    //maybe we'll wanna add something about itibaar bil khawateem
                }
                else -> error("Not Blood")
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
                        daysHoursMinutesDigital(duration.timeInMilliseconds, typeOfInput, Vls.Langs.URDU)
                    )
                    strEnglish+= StringsOfLanguages.ENGLISH.startingFromNifas.replace("duration1",
                        daysHoursMinutesDigital(duration.timeInMilliseconds, typeOfInput, Vls.Langs.ENGLISH)
                    )
                }
                DurationType.ISTIHAZA_AFTER -> {
                    strUrdu+= StringsOfLanguages.URDU.followedByistehazaAfter.replace("duration1",
                        daysHoursMinutesDigital(duration.timeInMilliseconds, typeOfInput, Vls.Langs.URDU)
                    )
                    strEnglish+= StringsOfLanguages.ENGLISH.followedByistehazaAfter.replace("duration1",
                        daysHoursMinutesDigital(duration.timeInMilliseconds, typeOfInput, Vls.Langs.ENGLISH)
                    )
                }
                DurationType.HAIZ -> {
                    strUrdu+= StringsOfLanguages.URDU.followedByHaizAfter.replace("duration1",
                        daysHoursMinutesDigital(duration.timeInMilliseconds, typeOfInput, Vls.Langs.URDU)
                    )
                    strEnglish+= StringsOfLanguages.ENGLISH.followedByHaizAfter.replace("duration1",
                        daysHoursMinutesDigital(duration.timeInMilliseconds, typeOfInput, Vls.Langs.ENGLISH)
                    )
                }
                DurationType.LESS_THAN_3_HAIZ -> {
                    strUrdu+= StringsOfLanguages.URDU.followedByHaizAfter.replace("duration1",
                        daysHoursMinutesDigital(duration.timeInMilliseconds, typeOfInput, Vls.Langs.URDU)
                    )
                    strUrdu+= StringsOfLanguages.ENGLISH.followedByHaizAfter.replace("duration1",
                        daysHoursMinutesDigital(duration.timeInMilliseconds, typeOfInput, Vls.Langs.ENGLISH)
                    )
                    //maybe we'll wanna add something about itibaar bil khawateem
                }
                else -> error("Not Blood")
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
            StringsOfLanguages.URDU.nifasdaysinsolution.replace("date1", languagedDateFormat(sd, typeOfInput, Vls.Langs.URDU)).replace("date2",
                languagedDateFormat(ed,typeOfInput, Vls.Langs.URDU)
            ).replace("duration1", daysHoursMinutesDigital((difference(sd,ed)), typeOfInput, Vls.Langs.URDU)),
            StringsOfLanguages.ENGLISH.nifasdaysinsolution.replace("date1", languagedDateFormat(sd, typeOfInput, Vls.Langs.ENGLISH)).replace("date2",
                languagedDateFormat(ed,typeOfInput, Vls.Langs.ENGLISH)
            ).replace("duration1", daysHoursMinutesDigital((difference(sd,ed)), typeOfInput, Vls.Langs.ENGLISH))
        )
    }
    fun haizLine(sd:Date, ed:Date, typeOfInput: TypesOfInputs):OutputStringsLanguages{
        return OutputStringsLanguages(
            StringsOfLanguages.URDU.haizdaysinsolution.replace("date1", languagedDateFormat(sd, typeOfInput, Vls.Langs.URDU)).replace("date2",
                languagedDateFormat(ed, typeOfInput, Vls.Langs.URDU)
            ).replace("duration1", daysHoursMinutesDigital((difference(sd,ed)), typeOfInput, Vls.Langs.URDU)),
            StringsOfLanguages.ENGLISH.haizdaysinsolution.replace("date1", languagedDateFormat(sd, typeOfInput, Vls.Langs.ENGLISH)).replace("date2",
                languagedDateFormat(ed, typeOfInput, Vls.Langs.ENGLISH)
            ).replace("duration1", daysHoursMinutesDigital((difference(sd,ed)), typeOfInput, Vls.Langs.ENGLISH))
        )
    }
    fun istihazaLine(sd:Date, ed:Date, typeOfInput: TypesOfInputs):OutputStringsLanguages{
        return OutputStringsLanguages(
            StringsOfLanguages.URDU.istihazadays.replace("date1", languagedDateFormat(sd, typeOfInput, Vls.Langs.URDU)).replace("date2",
                languagedDateFormat(ed,typeOfInput, Vls.Langs.URDU)
            ).replace("duration1", daysHoursMinutesDigital(difference(sd,ed), typeOfInput, Vls.Langs.URDU)),
            StringsOfLanguages.ENGLISH.istihazadays.replace("date1", languagedDateFormat(sd, typeOfInput, Vls.Langs.ENGLISH)).replace("date2",
                languagedDateFormat(ed,typeOfInput, Vls.Langs.ENGLISH)
            ).replace("duration1", daysHoursMinutesDigital(difference(sd,ed), typeOfInput, Vls.Langs.ENGLISH))
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
                        languagedDateFormat(duration.startTime,typeOfInput, Vls.Langs.URDU)
                    )
                    strEnglish+= StringsOfLanguages.ENGLISH.istihazadetailslinetwo.replace("date1",
                        languagedDateFormat(duration.startTime,typeOfInput, Vls.Langs.ENGLISH)
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
                else -> error("Not Blood")
            }
        }
        strUrdu += StringsOfLanguages.URDU.dashesline
        strEnglish += StringsOfLanguages.ENGLISH.dashesline
    }

    return OutputStringsLanguages(strUrdu,strEnglish)
}
fun outputStringHeaderLineDuration(fixedDurations: MutableList<FixedDuration>, index: Int, isMustabeen:Boolean = true):OutputStringsLanguages{
    val typeOfInput=TypesOfInputs.DURATION
    //in duration, we just give the fixed duration
    var outputStringUrdu = ""
    var outputStringEnglish = ""
    if (fixedDurations[index].type==DurationType.DAM||
        fixedDurations[index].type==DurationType.DAM_MUBTADIA){
        val sd:Date = fixedDurations[index].startDate
        val et = fixedDurations[index].endDate
        if(fixedDurations[index].days in 3.0..10.0){//if it's between 3 and 10, write haiz
            outputStringUrdu = StringsOfLanguages.URDU.durationHaiz.replace("duration1",
                daysHoursMinutesDigital((difference(sd,et)), typeOfInput, Vls.Langs.URDU)
            )
            outputStringEnglish = StringsOfLanguages.ENGLISH.durationHaiz.replace("duration1",
                daysHoursMinutesDigital((difference(sd,et)), typeOfInput, Vls.Langs.ENGLISH)
            )
        }else{//bigger than 10
            outputStringUrdu = StringsOfLanguages.URDU.durationDam.replace("duration1",
                daysHoursMinutesDigital((difference(sd,et)), typeOfInput, Vls.Langs.URDU)
            )
            outputStringEnglish = StringsOfLanguages.ENGLISH.durationDam.replace("duration1",
                daysHoursMinutesDigital((difference(sd,et)), typeOfInput, Vls.Langs.ENGLISH)
            )
        }

    }else if (fixedDurations[index].type == DurationType.TUHR||
        fixedDurations[index].type == DurationType.TUHR_MUBTADIA||
        fixedDurations[index].type==DurationType.TUHR_MUBTADIA_BECAME_A_MUTADA_NOW){
        val time = fixedDurations[index].timeInMilliseconds
        outputStringUrdu =  StringsOfLanguages.URDU.durationPaki.replace("duration1",
            daysHoursMinutesDigital(time, typeOfInput, Vls.Langs.URDU)
        )
        outputStringEnglish =  StringsOfLanguages.ENGLISH.durationPaki.replace("duration1",
            daysHoursMinutesDigital(time, typeOfInput, Vls.Langs.ENGLISH)
        )
        if(fixedDurations[index].type == DurationType.TUHR_MUBTADIA_BECAME_A_MUTADA_NOW){
            outputStringUrdu+=StringsOfLanguages.URDU.becamemutadah
            outputStringEnglish+=StringsOfLanguages.ENGLISH.becamemutadah
        }

    }else if (fixedDurations[index].type == DurationType.TUHREFAASID||fixedDurations[index].type==DurationType.TUHREFAASID_MUBTADIA){
        outputStringUrdu =  StringsOfLanguages.URDU.durationTuhrefasid.replace("duration1",
            daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds, typeOfInput, Vls.Langs.URDU)
        )
        outputStringEnglish =  StringsOfLanguages.ENGLISH.durationTuhrefasid.replace("duration1",
            daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds, typeOfInput, Vls.Langs.ENGLISH)
        )
    }else if (fixedDurations[index].type==DurationType.TUHREFAASID_WITH_ISTEHAZA||
        fixedDurations[index].type==DurationType.TUHREFAASID_MUBTADIA_WITH_ISTEHAZA){
        outputStringUrdu =  StringsOfLanguages.URDU.durationTuhreFasidWithAddition.replace("duration1",
            daysHoursMinutesDigital(fixedDurations[index].istihazaAfter, typeOfInput, Vls.Langs.URDU)
        ).replace("duration2", daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds, typeOfInput, Vls.Langs.URDU)).replace("duration3",
            daysHoursMinutesDigital((fixedDurations[index].istihazaAfter+fixedDurations[index].timeInMilliseconds), typeOfInput, Vls.Langs.URDU)
        )
        outputStringEnglish =  StringsOfLanguages.ENGLISH.durationTuhreFasidWithAddition.replace("duration1",
            daysHoursMinutesDigital(fixedDurations[index].istihazaAfter, typeOfInput, Vls.Langs.ENGLISH)
        ).replace("duration2", daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds, typeOfInput, Vls.Langs.ENGLISH)).replace("duration3",
            daysHoursMinutesDigital((fixedDurations[index].istihazaAfter+fixedDurations[index].timeInMilliseconds), typeOfInput, Vls.Langs.ENGLISH)
        )
    }else if (fixedDurations[index].type == DurationType.DAM_IN_NIFAS_PERIOD){
        val sd = fixedDurations[index].startDate
        val et = fixedDurations[index].endDate
        if(fixedDurations[index].days<=40){
            outputStringUrdu = StringsOfLanguages.URDU.durationNifas.replace("duration1",
                daysHoursMinutesDigital((difference(sd,et)), typeOfInput, Vls.Langs.URDU)
            )
            outputStringEnglish = StringsOfLanguages.ENGLISH.durationNifas.replace("duration1",
                daysHoursMinutesDigital((difference(sd,et)), typeOfInput, Vls.Langs.ENGLISH)
            )
        }else{//more than 40
            outputStringUrdu = StringsOfLanguages.URDU.durationDam.replace("duration1",
                daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds,typeOfInput, Vls.Langs.URDU)
            )
            outputStringEnglish = StringsOfLanguages.ENGLISH.durationDam.replace("duration1",
                daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds,typeOfInput, Vls.Langs.ENGLISH)
            )
        }
    }else if (fixedDurations[index].type == DurationType.TUHR_IN_HAML){
        if(!isMustabeen){
            val time = fixedDurations[index].timeInMilliseconds
            outputStringUrdu =  StringsOfLanguages.URDU.durationPaki.replace("duration1",
                daysHoursMinutesDigital(time, typeOfInput, Vls.Langs.URDU)
            )
            outputStringEnglish =  StringsOfLanguages.ENGLISH.durationPaki.replace("duration1",
                daysHoursMinutesDigital(time, typeOfInput, Vls.Langs.ENGLISH)
            )
        }
    }else if (fixedDurations[index].type == DurationType.TUHREFAASID_IN_HAML){
        if(!isMustabeen){
                outputStringUrdu =  StringsOfLanguages.URDU.durationTuhreFasidWithAddition.replace("duration1",
                    daysHoursMinutesDigital(fixedDurations[index].istihazaAfter, typeOfInput, Vls.Langs.URDU)
                ).replace("duration2",
                    daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds, typeOfInput, Vls.Langs.URDU)
                ).replace("duration3",
                    daysHoursMinutesDigital((fixedDurations[index].istihazaAfter+fixedDurations[index].timeInMilliseconds), typeOfInput, Vls.Langs.URDU)
                )
                outputStringEnglish =  StringsOfLanguages.ENGLISH.durationTuhreFasidWithAddition.replace("duration1",
                    daysHoursMinutesDigital(fixedDurations[index].istihazaAfter, typeOfInput, Vls.Langs.ENGLISH)
                ).replace("duration2",
                    daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds, typeOfInput, Vls.Langs.ENGLISH)
                ).replace("duration3",
                    daysHoursMinutesDigital((fixedDurations[index].istihazaAfter+fixedDurations[index].timeInMilliseconds), typeOfInput, Vls.Langs.ENGLISH)
                )
        }
    }else if (fixedDurations[index].type == DurationType.DAM_IN_HAML){

    }else if (fixedDurations[index].type == DurationType.TUHR_BIGGER_THAN_6_MONTHS){
        outputStringUrdu = StringsOfLanguages.URDU.twomonthstuhr.replace("duration1",
            daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds,typeOfInput, Vls.Langs.URDU)
        )
        outputStringEnglish = StringsOfLanguages.ENGLISH.twomonthstuhr.replace("duration1",
            daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds,typeOfInput, Vls.Langs.ENGLISH)
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
            outputStringUrdu = StringsOfLanguages.URDU.haizdays.replace("date1", languagedDateFormat(sd, typeOfInput, Vls.Langs.URDU)).replace("date2",
                languagedDateFormat(et,typeOfInput, Vls.Langs.URDU)
            ).replace("duration1", daysHoursMinutesDigital((difference(sd,et)), typeOfInput, Vls.Langs.URDU))
            outputStringEnglish = StringsOfLanguages.ENGLISH.haizdays.replace("date1",
                languagedDateFormat(sd, typeOfInput, Vls.Langs.ENGLISH)
            ).replace("date2", languagedDateFormat(et,typeOfInput, Vls.Langs.ENGLISH)).replace("duration1",
                daysHoursMinutesDigital((difference(sd,et)), typeOfInput, Vls.Langs.ENGLISH)
            )
        }else{//bigger than 10
            if (fixedDurations[index].indices.size>1){//this dam is made up of more than 1
                outputStringUrdu = StringsOfLanguages.URDU.continuosbleeding.replace("date1",
                    languagedDateFormat(sd, typeOfInput, Vls.Langs.URDU)
                ).replace("date2", languagedDateFormat(et, typeOfInput, Vls.Langs.URDU)).replace("duration1",
                    daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds,typeOfInput, Vls.Langs.URDU)
                )
                outputStringEnglish = StringsOfLanguages.ENGLISH.continuosbleeding.replace("date1",
                    languagedDateFormat(sd, typeOfInput, Vls.Langs.ENGLISH)
                ).replace("date2", languagedDateFormat(et, typeOfInput, Vls.Langs.ENGLISH)).replace("duration1",
                    daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds,typeOfInput, Vls.Langs.ENGLISH)
                )
            }else{
                outputStringUrdu = StringsOfLanguages.URDU.blooddays.replace("date1", languagedDateFormat(sd, typeOfInput, Vls.Langs.URDU)).replace("date2",
                    languagedDateFormat(et, typeOfInput, Vls.Langs.URDU)
                ).replace("duration1",
                    daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds,typeOfInput, Vls.Langs.URDU)
                )
                outputStringEnglish = StringsOfLanguages.ENGLISH.blooddays.replace("date1",
                    languagedDateFormat(sd, typeOfInput, Vls.Langs.ENGLISH)
                ).replace("date2", languagedDateFormat(et, typeOfInput, Vls.Langs.ENGLISH)).replace("duration1",
                    daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds,typeOfInput, Vls.Langs.ENGLISH)
                )
            }
        }

    }else if (fixedDurations[index].type == DurationType.TUHR||
        fixedDurations[index].type == DurationType.TUHR_MUBTADIA||
        fixedDurations[index].type==DurationType.TUHR_MUBTADIA_BECAME_A_MUTADA_NOW){
        val time = fixedDurations[index].timeInMilliseconds
        outputStringUrdu =  StringsOfLanguages.URDU.pakidays.replace("duration1",
            daysHoursMinutesDigital(time, typeOfInput, Vls.Langs.URDU)
        )
        outputStringEnglish =  StringsOfLanguages.ENGLISH.pakidays.replace("duration1",
            daysHoursMinutesDigital(time, typeOfInput, Vls.Langs.ENGLISH)
        )
        if(fixedDurations[index].type == DurationType.TUHR_MUBTADIA_BECAME_A_MUTADA_NOW){
            outputStringUrdu+=StringsOfLanguages.URDU.becamemutadah
            outputStringEnglish+=StringsOfLanguages.ENGLISH.becamemutadah
        }

    }else if (fixedDurations[index].type == DurationType.TUHREFAASID||fixedDurations[index].type==DurationType.TUHREFAASID_MUBTADIA){
        outputStringUrdu =  StringsOfLanguages.URDU.tuhrfasid.replace("duration1",
            daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds, typeOfInput, Vls.Langs.URDU)
        )
        outputStringEnglish =  StringsOfLanguages.ENGLISH.tuhrfasid.replace("duration1",
            daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds, typeOfInput, Vls.Langs.ENGLISH)
        )
    }else if (fixedDurations[index].type==DurationType.TUHREFAASID_WITH_ISTEHAZA||
        fixedDurations[index].type==DurationType.TUHREFAASID_MUBTADIA_WITH_ISTEHAZA){
        outputStringUrdu =  StringsOfLanguages.URDU.tuhrfasidwithaddition.replace("duration1",
            daysHoursMinutesDigital(fixedDurations[index].istihazaAfter, typeOfInput, Vls.Langs.URDU)
        ).replace("duration2", daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds, typeOfInput, Vls.Langs.URDU)).replace("duration3",
            daysHoursMinutesDigital((fixedDurations[index].istihazaAfter+fixedDurations[index].timeInMilliseconds), typeOfInput, Vls.Langs.URDU)
        )
        outputStringEnglish =  StringsOfLanguages.ENGLISH.tuhrfasidwithaddition.replace("duration1",
            daysHoursMinutesDigital(fixedDurations[index].istihazaAfter, typeOfInput, Vls.Langs.ENGLISH)
        ).replace("duration2", daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds, typeOfInput, Vls.Langs.ENGLISH)).replace("duration3",
            daysHoursMinutesDigital((fixedDurations[index].istihazaAfter+fixedDurations[index].timeInMilliseconds), typeOfInput, Vls.Langs.ENGLISH)
        )
    }else if (fixedDurations[index].type == DurationType.DAM_IN_NIFAS_PERIOD){
        val sd = fixedDurations[index].startDate
        val et = fixedDurations[index].endDate
        if(fixedDurations[index].days<=40){
            outputStringUrdu = StringsOfLanguages.URDU.nifasdays.replace("date1", languagedDateFormat(sd, typeOfInput, Vls.Langs.URDU)).replace("date2",
                languagedDateFormat(et,typeOfInput, Vls.Langs.URDU)
            ).replace("duration1", daysHoursMinutesDigital((difference(sd,et)), typeOfInput, Vls.Langs.URDU))
            outputStringEnglish = StringsOfLanguages.ENGLISH.nifasdays.replace("date1",
                languagedDateFormat(sd, typeOfInput, Vls.Langs.ENGLISH)
            ).replace("date2", languagedDateFormat(et,typeOfInput, Vls.Langs.ENGLISH)).replace("duration1",
                daysHoursMinutesDigital((difference(sd,et)), typeOfInput, Vls.Langs.ENGLISH)
            )
        }else{//more than 40
            outputStringUrdu = StringsOfLanguages.URDU.blooddays.replace("date1", languagedDateFormat(sd, typeOfInput, Vls.Langs.URDU)).replace("date2",
                languagedDateFormat(et, typeOfInput, Vls.Langs.URDU)
            ).replace("duration1", daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds,typeOfInput, Vls.Langs.URDU))
            outputStringEnglish = StringsOfLanguages.ENGLISH.blooddays.replace("date1",
                languagedDateFormat(sd, typeOfInput, Vls.Langs.ENGLISH)
            ).replace("date2", languagedDateFormat(et, typeOfInput, Vls.Langs.ENGLISH)).replace("duration1",
                daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds,typeOfInput, Vls.Langs.ENGLISH)
            )
        }
    }else if (fixedDurations[index].type == DurationType.TUHR_IN_HAML){
        if(!mustabeen){
            val time = fixedDurations[index].timeInMilliseconds
            outputStringUrdu =  StringsOfLanguages.URDU.pakidays.replace("duration1",
                daysHoursMinutesDigital(time, typeOfInput, Vls.Langs.URDU)
            )
            outputStringEnglish =  StringsOfLanguages.ENGLISH.pakidays.replace("duration1",
                daysHoursMinutesDigital(time, typeOfInput, Vls.Langs.ENGLISH)
            )
        }
    }else if (fixedDurations[index].type == DurationType.TUHREFAASID_IN_HAML){
        if(!mustabeen){
            outputStringUrdu =  StringsOfLanguages.URDU.tuhrfasidwithaddition.replace("duration1",
                daysHoursMinutesDigital(fixedDurations[index].istihazaAfter, typeOfInput, Vls.Langs.URDU)
            ).replace("duration2", daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds, typeOfInput, Vls.Langs.URDU)).replace("duration3",
                daysHoursMinutesDigital((fixedDurations[index].istihazaAfter+fixedDurations[index].timeInMilliseconds), typeOfInput, Vls.Langs.URDU)
            )
            outputStringEnglish =  StringsOfLanguages.ENGLISH.tuhrfasidwithaddition.replace("duration1",
                daysHoursMinutesDigital(fixedDurations[index].istihazaAfter, typeOfInput, Vls.Langs.ENGLISH)
            ).replace("duration2",
                daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds, typeOfInput, Vls.Langs.ENGLISH)
            ).replace("duration3",
                daysHoursMinutesDigital((fixedDurations[index].istihazaAfter+fixedDurations[index].timeInMilliseconds), typeOfInput, Vls.Langs.ENGLISH)
            )
        }
    }else if (fixedDurations[index].type == DurationType.DAM_IN_HAML){

    }else if (fixedDurations[index].type == DurationType.TUHR_BIGGER_THAN_6_MONTHS){
        outputStringUrdu = StringsOfLanguages.URDU.twomonthstuhr.replace("duration1",
            daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds,typeOfInput, Vls.Langs.URDU)
        )
        outputStringEnglish = StringsOfLanguages.ENGLISH.twomonthstuhr.replace("duration1",
            daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds,typeOfInput, Vls.Langs.ENGLISH)
        )
    }else if (fixedDurations[index].type == DurationType.ISTEHAZA_AYYAMEQABLIYYA){
        val sd = fixedDurations[index].startDate
        val et = fixedDurations[index].endDate
        outputStringUrdu = StringsOfLanguages.URDU.daysayyameqabliyya.replace("date1", languagedDateFormat(sd, typeOfInput, Vls.Langs.URDU)).replace("date2",
            languagedDateFormat(et,typeOfInput, Vls.Langs.URDU)
        ).replace("duration1", daysHoursMinutesDigital((difference(sd,et)), typeOfInput, Vls.Langs.URDU))
        outputStringEnglish = StringsOfLanguages.ENGLISH.daysayyameqabliyya.replace("date1",
            languagedDateFormat(sd, typeOfInput, Vls.Langs.ENGLISH)
        ).replace("date2", languagedDateFormat(et,typeOfInput, Vls.Langs.ENGLISH)).replace("duration1",
            daysHoursMinutesDigital((difference(sd,et)), typeOfInput, Vls.Langs.ENGLISH)
        )
    }
    return OutputStringsLanguages(outputStringUrdu,outputStringEnglish)
}

//fun generateGetDifferenceString(durationTypes:MutableList<DurationTypes>):String {
//    var str = ""
//    for( durationType in durationTypes){
//        var type = ""
//        if (durationType.type==DateTypes.YAQEENI_PAKI){type="yaqeeni paki"}
//        else if (durationType.type==DateTypes.YAQEENI_NA_PAKI){type="yaqeeni na paki"}
//        else if (durationType.type==DateTypes.AYYAAM_E_SHAKK_KHUROOJ){type="shakk fil khurooj"}
//        else if (durationType.type==DateTypes.AYYAAM_E_SHAKK_DUKHOOL){type="shakk fil dukhool"}
//        val startTime=durationType.startTime
//        val endTime = durationType.endTime
//
//        if(durationType.type==DateTypes.AYYAAM_E_SHAKK_DUKHOOL){
//            str += "${UnicodeChars.ORANGE_DIAMOND} <b><em>From ${languagedDateFormat(startTime,TypesOfInputs.DATE_ONLY, Vls.Langs.ENGLISH)} to ${languagedDateFormat(endTime,TypesOfInputs.DATE_ONLY, Vls.Langs.ENGLISH)} is ${type}</em></b>\n\n"
//        }else if (durationType.type==DateTypes.YAQEENI_PAKI){
//            str += "${UnicodeChars.WHITE_DIAMOND} From ${languagedDateFormat(startTime,TypesOfInputs.DATE_ONLY, Vls.Langs.ENGLISH)} to ${languagedDateFormat(endTime,TypesOfInputs.DATE_ONLY, Vls.Langs.ENGLISH)} is ${type}\n\n"
//        }else if (durationType.type==DateTypes.YAQEENI_NA_PAKI){
//            str += "${UnicodeChars.RED_DIAMOND} From ${languagedDateFormat(startTime,TypesOfInputs.DATE_ONLY, Vls.Langs.ENGLISH)} to ${languagedDateFormat(endTime,TypesOfInputs.DATE_ONLY, Vls.Langs.ENGLISH)} is ${type}\n\n"
//        }else if (durationType.type==DateTypes.AYYAAM_E_SHAKK_KHUROOJ){
//            str += "${UnicodeChars.SNOWFLAKE} <b><em>From ${languagedDateFormat(startTime,TypesOfInputs.DATE_ONLY, Vls.Langs.ENGLISH)} to ${languagedDateFormat(endTime,TypesOfInputs.DATE_ONLY, Vls.Langs.ENGLISH)} is ${type}</em></b>\n\n"
//        }
//
//    }
//    return str
//}




