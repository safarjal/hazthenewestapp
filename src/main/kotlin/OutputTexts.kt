@file:Suppress("SpellCheckingInspection")
import kotlinx.datetime.internal.JSJoda.Instant

fun addPreMaslaValuesText(preMaslaValues: PreMaslaValues):OutputStringsLanguages{
    var englishStr = ""
    var urduStr = ""
    if(preMaslaValues.inputtedAadatHaiz!=null &&
        preMaslaValues.inputtedAadatTuhr!=null &&
        preMaslaValues.inputtedMawjoodahTuhr!=null){//we have all 3 pre masla values

        //add line about previous habits
        val (ur1, en1) = replacement(Strings::preMaslaHabitOfHaizAndTuhr, PlcHolder.DUR1, preMaslaValues.inputtedAadatHaiz!!, TypesOfInputs.DURATION)
            .replacement(PlcHolder.DUR2, preMaslaValues.inputtedAadatTuhr!!, TypesOfInputs.DURATION)
        urduStr += ur1
        englishStr += en1
        //add line about mawjooda paki
        val (ur2, en2) = replacement(Strings::preMaslaValueOfMawjoodaPaki, PlcHolder.DUR1, preMaslaValues.inputtedMawjoodahTuhr!!, TypesOfInputs.DURATION)
        urduStr += ur2
        englishStr += en2
        //remove the word fasid or invalid, if tuhr was saheeh
        if(!preMaslaValues.isMawjoodaFasid){//if tuhr is not fasid
            urduStr=urduStr.replace(PlcHolder.FASID, "")
            englishStr=englishStr.replace(PlcHolder.INVALID, "")
        }
    }
    return OutputStringsLanguages(urduStr,englishStr)
}

fun generateOutputStringPregnancy(fixedDurations: MutableList<FixedDuration>,
                                  pregnancy: Pregnancy,
                                  endingOutputValues: EndingOutputValues,
                                  typeOfInput: TypesOfInputs,
                                  timeZone: String):OutputTexts{
    var englishStr = ""
    var urduStr = ""
    val hazDatesList = getHaizDatesList(fixedDurations)
    urduStr+= generateLanguagedOutputStringPregnancy(fixedDurations,pregnancy, endingOutputValues, typeOfInput, timeZone).urduString
    englishStr+= generateLanguagedOutputStringPregnancy(fixedDurations,pregnancy, endingOutputValues, typeOfInput, timeZone).englishString


    return OutputTexts(englishStr, urduStr, "", hazDatesList, endingOutputValues, fixedDurations)
}
fun generateOutputStringMubtadia(fixedDurations: MutableList<FixedDuration>,
                                 endingOutputValues: EndingOutputValues,
                                 typeOfInput: TypesOfInputs,
                                 preMaslaValues: PreMaslaValues,
                                 timeZone: String):OutputTexts{
    val hazDatesList = getHaizDatesList(fixedDurations)
    val urduStr = generateOutputString(fixedDurations, endingOutputValues, typeOfInput, TypesOfMasla.MUBTADIA, preMaslaValues, timeZone).urduString
    val englishStr = generateOutputString(fixedDurations, endingOutputValues, typeOfInput, TypesOfMasla.MUBTADIA, preMaslaValues, timeZone).englishString

    val hazDatesStr = generateHazDatesStr(hazDatesList,typeOfInput,timeZone)

    return OutputTexts(englishStr, urduStr, hazDatesStr, hazDatesList,endingOutputValues, fixedDurations)
}
fun generateOutputStringMutadah(fixedDurations: MutableList<FixedDuration>,
                                endingOutputValues: EndingOutputValues,
                                typeOfInput: TypesOfInputs,
                                preMaslaValues: PreMaslaValues,
                                timeZone: String):OutputTexts{
    val hazDatesList = getHaizDatesList(fixedDurations)

    val urduStr = generateOutputString(fixedDurations, endingOutputValues, typeOfInput, TypesOfMasla.MUTADAH, preMaslaValues, timeZone).urduString
    val englishStr = generateOutputString(fixedDurations, endingOutputValues, typeOfInput, TypesOfMasla.MUTADAH, preMaslaValues, timeZone).englishString

    val hazDatesStr = generateHazDatesStr(hazDatesList,typeOfInput,timeZone)

    return OutputTexts(englishStr,urduStr, hazDatesStr, hazDatesList,endingOutputValues, fixedDurations)
}

fun generateHazDatesStr(hazDatesList: MutableList<Entry>,typeOfInput: TypesOfInputs, timeZone: String):String{
    val lang = Vls.Langs.ENGLISH
    var str = ""
    for(entry in hazDatesList){
        str+="From ${languagedDateFormat(entry.startTime,typeOfInput,lang,timeZone)} to ${languagedDateFormat(entry.endTime,typeOfInput,lang,timeZone)}<br>"
    }
    return str
}

fun generateLanguagedOutputStringPregnancy(fixedDurations: MutableList<FixedDuration>,
                                           pregnancy: Pregnancy, 
                                           endingOutputValues: EndingOutputValues, 
                                           typeOfInput: TypesOfInputs,
                                           timeZone: String):OutputStringsLanguages{
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
                strUrdu += outputStringBiggerThan10HallDurations(fixedDurations, index).urduString
                strEnglish += outputStringBiggerThan10HallDurations(fixedDurations, index).englishString
                strUrdu += outputStringBiggerThan40HallDuration(fixedDurations, index).urduString
                strEnglish += outputStringBiggerThan40HallDuration(fixedDurations, index).englishString
                if(fixedDurations[index].type==DurationType.HAML){
                    strUrdu += StringsOfLanguages.URDU.pregduration
                    strEnglish += StringsOfLanguages.ENGLISH.pregduration
                }
                if(fixedDurations[index].type==DurationType.WILADAT_ISQAT){
                    strUrdu += StringsOfLanguages.URDU.birthduration
                    strEnglish += StringsOfLanguages.ENGLISH.birthduration
                }

            }else{
                strUrdu += outputStringHeaderLine(fixedDurations,index, typeOfInput, timeZone).urduString
                strEnglish += outputStringHeaderLine(fixedDurations,index, typeOfInput, timeZone).englishString
                strUrdu += outputStringBiggerThan10Hall(fixedDurations,index, typeOfInput, timeZone).urduString
                strEnglish += outputStringBiggerThan10Hall(fixedDurations,index, typeOfInput, timeZone).englishString
                strUrdu += outputStringBiggerThan40Hall(fixedDurations,index, typeOfInput, timeZone).urduString
                strEnglish += outputStringBiggerThan40Hall(fixedDurations,index, typeOfInput, timeZone).englishString
                if(fixedDurations[index].type==DurationType.HAML){
                    strUrdu += StringsOfLanguages.URDU.preg
                    strEnglish += StringsOfLanguages.ENGLISH.preg
                }
                if(fixedDurations[index].type==DurationType.WILADAT_ISQAT){
                    val (ur, en) = replacement(Strings::birth, PlcHolder.DT1, birthTime, typeOfInput, timeZone)
                    strUrdu += ur
                    strEnglish += en

                    if(index<fixedDurations.size-2){//if there is something after wiladat
                        strUrdu += StringsOfLanguages.URDU.afterpregheader
                        strEnglish += StringsOfLanguages.ENGLISH.afterpregheader
                    }
                }

            }

        }
        strUrdu += outputStringFinalLines(endingOutputValues, typeOfInput, timeZone).urduString
        strEnglish += outputStringFinalLines(endingOutputValues, typeOfInput, timeZone).englishString



    }else{//if it is ghair mustabeenulkhilqat
        strUrdu += StringsOfLanguages.URDU.headerline
        strEnglish += StringsOfLanguages.ENGLISH.headerline

        for(index in fixedDurations.indices){
            if(typeOfInput==TypesOfInputs.DURATION){
                strUrdu += outputStringHeaderLineDuration(fixedDurations,index, mustabeen).urduString
                strEnglish += outputStringHeaderLineDuration(fixedDurations,index,mustabeen).englishString
                strUrdu += outputStringBiggerThan10HallDurations(fixedDurations, index).urduString
                strEnglish += outputStringBiggerThan10HallDurations(fixedDurations, index).englishString
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
                strUrdu += outputStringHeaderLine(fixedDurations,index, typeOfInput, timeZone, mustabeen).urduString
                strEnglish += outputStringHeaderLine(fixedDurations,index, typeOfInput, timeZone, mustabeen).englishString
                strUrdu += outputStringBiggerThan10Hall(fixedDurations,index, typeOfInput, timeZone).urduString
                strEnglish += outputStringBiggerThan10Hall(fixedDurations,index, typeOfInput, timeZone).englishString
                if(fixedDurations[index].type==DurationType.HAML){
                    strUrdu += StringsOfLanguages.URDU.preg
                    strEnglish += StringsOfLanguages.ENGLISH.preg
                }
                if(fixedDurations[index].type==DurationType.WILADAT_ISQAT){
                    val (ur, en) = replacement(Strings::earlymiscarriage, PlcHolder.DT1, birthTime, typeOfInput, timeZone)
                    strUrdu += ur
                    strEnglish += en
                    if(index<fixedDurations.size-2){//if there is something after wiladat
                        strUrdu += StringsOfLanguages.URDU.afterpregheader
                        strEnglish += StringsOfLanguages.ENGLISH.afterpregheader
                    }
                }
            }

        }
        strUrdu += outputStringFinalLines (endingOutputValues, typeOfInput, timeZone).urduString
        strEnglish += outputStringFinalLines (endingOutputValues, typeOfInput, timeZone).englishString

    }


    return OutputStringsLanguages(strUrdu,strEnglish)
}

fun generateOutputString(fixedDurations: MutableList<FixedDuration>,
                         endingOutputValues: EndingOutputValues,
                         typeOfInput: TypesOfInputs,
                         typesOfMasla: TypesOfMasla,
                         preMaslaValues: PreMaslaValues,
                         timeZone: String):OutputStringsLanguages{
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
            strUrdu += outputStringBiggerThan10HallDurations(fixedDurations, index).urduString
            strEnglish += outputStringBiggerThan10HallDurations(fixedDurations, index).englishString
            if(index==fixedDurations.size-1){//if this os the last index
                strUrdu += outputStringFinalLines(endingOutputValues, typeOfInput, timeZone).urduString
                strEnglish += outputStringFinalLines(endingOutputValues, typeOfInput, timeZone).englishString
            }
        }else{//not durations
            strUrdu += outputStringHeaderLine(fixedDurations,index, typeOfInput, timeZone).urduString //اس تاریخ سے اس تاریخ تک اتنے دن حیض
            strEnglish += outputStringHeaderLine(fixedDurations,index, typeOfInput, timeZone).englishString //اس تاریخ سے اس تاریخ تک اتنے دن حیض
            strUrdu += outputStringBiggerThan10Hall(fixedDurations,index, typeOfInput, timeZone).urduString
            strEnglish += outputStringBiggerThan10Hall(fixedDurations,index, typeOfInput, timeZone).englishString
            if(index==fixedDurations.size-1){//if this os the last index
                strUrdu += outputStringFinalLines(endingOutputValues, typeOfInput, timeZone).urduString
                strEnglish += outputStringFinalLines(endingOutputValues, typeOfInput, timeZone).englishString
            }
        }
        index++
    }
    return OutputStringsLanguages(strUrdu,strEnglish)
}

fun outputStringFinalLines(endingOutputValues: EndingOutputValues,
                           typeOfInput: TypesOfInputs,
                           timeZone: String):OutputStringsLanguages{

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
    strUrdu+=outputStringAskAgainLine(typeOfInput, futureDates, timeZone).urduString
    strEnglish+=outputStringAskAgainLine(typeOfInput, futureDates, timeZone).englishString

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

fun outputStringAskAgainLine(typeOfInput: TypesOfInputs,
                             futureDates: MutableList<FutureDateType>,
                             timeZone: String):OutputStringsLanguages{
    var strUrdu = ""
    var strEnglish = ""

    for(futureDate in futureDates){
        val date = futureDate.date
        val type = futureDate.futureDates
        when (type) {
            TypesOfFutureDates.END_OF_AADAT_HAIZ -> {
                arrayOf(strUrdu, strEnglish).replacement(Strings::haizend, PlcHolder.DT1, date, typeOfInput, timeZone)
            }
            TypesOfFutureDates.END_OF_AADAT_TUHR -> {
                arrayOf(strUrdu, strEnglish).replacement(Strings::endofpaki, PlcHolder.DT1, date, typeOfInput, timeZone)
            }
            TypesOfFutureDates.IC_FORBIDDEN_DATE -> {
                arrayOf(strUrdu, strEnglish).replacement(Strings::sexnotallowed, PlcHolder.DT1, date, typeOfInput, timeZone)
            }
            TypesOfFutureDates.AFTER_TEN_DAYS -> {
                arrayOf(strUrdu, strEnglish).replacement(Strings::aftertendays, PlcHolder.DT1, date, typeOfInput, timeZone)
            }
            TypesOfFutureDates.FORTY_DAYS -> {
                arrayOf(strUrdu, strEnglish).replacement(Strings::afterfortydays, PlcHolder.DT1, date, typeOfInput, timeZone)
            }
            TypesOfFutureDates.BEFORE_THREE_DAYS_MASLA_WILL_CHANGE -> {
                arrayOf(strUrdu, strEnglish).replacement(Strings::bleedingstopsbeforethreemaslachanges, PlcHolder.DT1, date, typeOfInput, timeZone)
            }
            TypesOfFutureDates.BEFORE_THREE_DAYS -> {
                arrayOf(strUrdu, strEnglish).replacement(Strings::bleedingstopsbeforethree, PlcHolder.DT1, date, typeOfInput, timeZone)
            }
            TypesOfFutureDates.IHTIYATI_GHUSL -> {
                arrayOf(strUrdu, strEnglish).replacement(Strings::ihtiyatighusl, PlcHolder.DT1, date, typeOfInput, timeZone)
            }
            TypesOfFutureDates.A3_CHANGING_TO_A2 -> {
                arrayOf(strUrdu, strEnglish).replacement(Strings::situationmaychange, PlcHolder.DT1, date, typeOfInput, timeZone)
            }
            TypesOfFutureDates.BEFORE_TEN_DAYS_AYYAMEQABLIYYAH -> {
                arrayOf(strUrdu, strEnglish).replacement(Strings::beforetendaysayyameqabliyyaallconsideredhaiz, PlcHolder.DT1, date, typeOfInput, timeZone)
            }
            TypesOfFutureDates.START_OF_AADAT_AYYAMEQABLIYYA -> {
                arrayOf(strUrdu, strEnglish).replacement(Strings::endofistehazaayyameqabliyya, PlcHolder.DT1, date, typeOfInput, timeZone)
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
fun outputStringAadatLine(
    typeOfInput: TypesOfInputs,
    aadats: AadatsOfHaizAndTuhr?
):OutputStringsLanguages{
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
                val (ur, en) = replacement(Strings::onlynifashabit, PlcHolder.DUR1, aadats.aadatNifas!!, typeOfInput)
                strUrdu += ur
                strEnglish += en

            }else {//adat nifas doesn't exists
                strUrdu += StringsOfLanguages.URDU.thereisnoaadat
                strEnglish += StringsOfLanguages.ENGLISH.thereisnoaadat
            }
        }else if(aadatHaiz!=-1L && aadatTuhr==-1L) {//aadat of haiz exists, but not aadat of tuhr
            if (aadats.aadatNifas != null && aadats.aadatNifas != -1L) {//adat nifas exists
                val (ur, en) = replacement(Strings::nifasAndHaizHabit, PlcHolder.DUR1, aadatHaiz, typeOfInput)
                    .replacement(PlcHolder.DUR2, aadats.aadatNifas!!, typeOfInput)
                strUrdu += ur
                strEnglish += en

            } else {//adat nifas doesn't exists
                val (ur, en) = replacement(Strings::aadatofhaizonly, PlcHolder.DUR1, aadatHaiz, typeOfInput)
                strUrdu += ur
                strEnglish += en
            }
        }else if(aadatHaiz==-1L && aadatTuhr!=-1L){//aadat tuhr exist and aadat haiz doesn;t exist
            if (aadats.aadatNifas != null && aadats.aadatNifas != -1L) {//adat nifas exists
                val (ur, en) = replacement(Strings::nifasAndTuhrHabit, PlcHolder.DUR1, aadatTuhr, typeOfInput)
                    .replacement(PlcHolder.DUR2, aadats.aadatNifas!!, typeOfInput)
                strUrdu += ur
                strEnglish += en
            }else{
                val (ur, en) = replacement(Strings::onlyTuhrHabit, PlcHolder.DUR1, aadatTuhr, typeOfInput)
                    .replacement(PlcHolder.DUR2, aadatTuhr, typeOfInput)
                strUrdu += ur
                strEnglish += en
            }
        }else{//adats of haiz and tuhr exist
            if(aadats.aadatNifas != null && aadats.aadatNifas!=-1L){//adat nifas exists
                val (ur, en) = replacement(Strings::habitwithnifas, PlcHolder.DUR1, aadatHaiz, typeOfInput)
                    .replacement(PlcHolder.DUR2, aadatTuhr, typeOfInput)
                    .replacement(PlcHolder.DUR2, aadats.aadatNifas!!, typeOfInput)
                strUrdu += ur
                strEnglish += en
            }else{//adat nifas doesn't exists
                val (ur, en) = replacement(Strings::habitwithnifas, PlcHolder.DUR1, aadatHaiz, typeOfInput)
                    .replacement(PlcHolder.DUR2, aadatTuhr, typeOfInput)
                strUrdu += ur
                strEnglish += en
            }
        }
        OutputStringsLanguages(strUrdu, strEnglish)
    }
}
fun outputStringBiggerThan10HallDurations(
    fixedDurations: MutableList<FixedDuration>,
    index: Int
):OutputStringsLanguages{
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
                val (ur, en) = replacement(Strings::startingFromIstehaza, PlcHolder.DUR1, duration.timeInMilliseconds, typeOfInput)
                strUrdu += ur
                strEnglish += en
            }else if(duration.type == DurationType.HAIZ){
                if(duration==fixedDurations[index].biggerThanTen!!.durationsList[0]){
                    val (ur, en) = replacement(Strings::startingFromHaiz, PlcHolder.DUR1, duration.timeInMilliseconds, typeOfInput)
                    strUrdu += ur
                    strEnglish += en
                }else{
                    val (ur, en) = replacement(Strings::followedByHaizAfter, PlcHolder.DUR1, duration.timeInMilliseconds, typeOfInput)
                    strUrdu += ur
                    strEnglish += en
                }
            }else if(duration.type == DurationType.ISTIHAZA_AFTER){
                val (ur, en) = replacement(Strings::followedByistehazaAfter, PlcHolder.DUR1, duration.timeInMilliseconds, typeOfInput)
                strUrdu += ur
                strEnglish += en
            }else if(duration.type == DurationType.LESS_THAN_3_HAIZ){
                val (ur, en) = replacement(Strings::followedByHaizAfter, PlcHolder.DUR1, duration.timeInMilliseconds, typeOfInput)
                strUrdu += ur
                strEnglish += en
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

fun outputStringBiggerThan10Hall(fixedDurations: MutableList<FixedDuration>,
                                 index: Int,
                                 typeOfInput: TypesOfInputs,
                                 timeZone: String):OutputStringsLanguages{
    //legacy code this, I think it's unused
    var strUrdu = ""
    var strEnglish = ""

    fun haizLine(sd:Instant, ed:Instant, typeOfInput: TypesOfInputs):OutputStringsLanguages{
        val (ur, en) = replacement(Strings::haizdaysinsolution, PlcHolder.DT1, sd, typeOfInput, timeZone)
            .replacement(PlcHolder.DT2, ed, typeOfInput, timeZone)
            .replacement(PlcHolder.DUR1, (difference(sd,ed)), typeOfInput)

        return OutputStringsLanguages(ur, en)
    }
    fun istihazaLine(sd:Instant, ed:Instant, typeOfInput: TypesOfInputs):OutputStringsLanguages{
        val (ur, en) = replacement(Strings::istihazadays, PlcHolder.DT1, sd, typeOfInput, timeZone)
            .replacement(PlcHolder.DT2, ed, typeOfInput, timeZone)
            .replacement(PlcHolder.DUR1, (difference(sd,ed)), typeOfInput)
        return OutputStringsLanguages(ur, en)
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
                    strUrdu += istihazaLine(duration.startTime,duration.endDate,typeOfInput).urduString
                    strEnglish += istihazaLine(duration.startTime,duration.endDate,typeOfInput).englishString
                    val (ur, en) = replacement(Strings::istihazadetailslinetwo, PlcHolder.DT1, duration.startTime, typeOfInput, timeZone)
                    strUrdu += ur
                    strEnglish += en
                    strUrdu += StringsOfLanguages.URDU.istihazadetailslineone
                    strEnglish += StringsOfLanguages.ENGLISH.istihazadetailslineone

                }
                DurationType.LESS_THAN_3_HAIZ -> {
                    strUrdu+= haizLine(duration.startTime,duration.endDate,typeOfInput).urduString
                    strEnglish+= haizLine(duration.startTime,duration.endDate,typeOfInput).englishString
                    //maybe we'll wanna add something about itibaar bil khawateem
                }
                else -> error(PlcHolder.Msg.NOT_BLOOD)
            }
        }

        strUrdu += StringsOfLanguages.URDU.dashesline
        strEnglish += StringsOfLanguages.ENGLISH.dashesline
    }

    return OutputStringsLanguages(strUrdu,strEnglish)
}
fun outputStringBiggerThan40HallDuration(
    fixedDurations: MutableList<FixedDuration>,
    index: Int
):OutputStringsLanguages{
    val typeOfInput=TypesOfInputs.DURATION
    var strUrdu = ""
    var strEnglish = ""
    if(fixedDurations[index].days>40&&fixedDurations[index].type==DurationType.DAM_IN_NIFAS_PERIOD){
        strUrdu+=UnicodeChars.ABACUS
        strEnglish+=UnicodeChars.ABACUS
        for(duration in fixedDurations[index].biggerThanForty!!.durationsList){
            when (duration.type) {
                DurationType.NIFAS -> {
                    val (ur, en) = replacement(Strings::startingFromNifas, PlcHolder.DUR1, duration.timeInMilliseconds, typeOfInput)
                    strUrdu += ur
                    strEnglish += en
                }
                DurationType.ISTIHAZA_AFTER -> {
                    val (ur, en) = replacement(Strings::followedByistehazaAfter, PlcHolder.DUR1, duration.timeInMilliseconds, typeOfInput)
                    strUrdu += ur
                    strEnglish += en
                }
                DurationType.HAIZ -> {
                    val (ur, en) = replacement(Strings::followedByHaizAfter, PlcHolder.DUR1, duration.timeInMilliseconds, typeOfInput)
                    strUrdu += ur
                    strEnglish += en
                }
                DurationType.LESS_THAN_3_HAIZ -> {
                    val (ur, en) = replacement(Strings::followedByHaizAfter, PlcHolder.DUR1, duration.timeInMilliseconds, typeOfInput)
                    strUrdu += ur
                    strEnglish += en
                    //maybe we'll wanna add something about itibaar bil khawateem
                }
                else -> error(PlcHolder.Msg.NOT_BLOOD)
            }
        }
         }
    if(strUrdu!=""&&strEnglish!=""){
        strUrdu+=StringsOfLanguages.URDU.khatimaplusnewline
        strEnglish+=StringsOfLanguages.ENGLISH.khatimaplusnewline
    }
    return OutputStringsLanguages(strUrdu, strEnglish)
}
fun outputStringBiggerThan40Hall(fixedDurations: MutableList<FixedDuration>,
                                 index: Int,
                                 typeOfInput: TypesOfInputs,
                                 timeZone: String):OutputStringsLanguages{

    var strUrdu = ""
    var strEnglish = ""

    fun nifasLine(sd:Instant, ed:Instant, typeOfInput: TypesOfInputs):OutputStringsLanguages{
        val (ur, en) = replacement(Strings::nifasdaysinsolution, PlcHolder.DT1, sd, typeOfInput, timeZone)
            .replacement(PlcHolder.DT2, ed, typeOfInput, timeZone)
            .replacement(PlcHolder.DUR1, (difference(sd,ed)), typeOfInput)
        return OutputStringsLanguages(ur, en)
    }
    fun haizLine(sd:Instant, ed:Instant, typeOfInput: TypesOfInputs):OutputStringsLanguages{
        val (ur, en) = replacement(Strings::haizdaysinsolution, PlcHolder.DT1, sd, typeOfInput, timeZone)
            .replacement(PlcHolder.DT2, ed, typeOfInput, timeZone)
            .replacement(PlcHolder.DUR1, (difference(sd,ed)), typeOfInput)
        return OutputStringsLanguages(ur, en)

    }
    fun istihazaLine(sd:Instant, ed:Instant, typeOfInput: TypesOfInputs):OutputStringsLanguages{
        val (ur, en) = replacement(Strings::istihazadays, PlcHolder.DT1, sd, typeOfInput, timeZone)
            .replacement(PlcHolder.DT2, ed, typeOfInput, timeZone)
            .replacement(PlcHolder.DUR1, (difference(sd,ed)), typeOfInput)
        return OutputStringsLanguages(ur, en)
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

                    val (ur, en) = replacement(Strings::istihazadetailslinetwo, PlcHolder.DT1, duration.startTime, typeOfInput, timeZone)
                    strUrdu += ur
                    strEnglish += en

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
                else -> error(PlcHolder.Msg.NOT_BLOOD)
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
        val sd:Instant = fixedDurations[index].startDate
        val et = fixedDurations[index].endDate
        if(fixedDurations[index].days in 3.0..10.0){//if it's between 3 and 10, write haiz
            val (ur, en) = replacement(Strings::durationHaiz, PlcHolder.DUR1, difference(sd,et), typeOfInput)
            outputStringUrdu = ur
            outputStringEnglish = en
        }else{//bigger than 10
            val (ur, en) = replacement(Strings::durationDam, PlcHolder.DUR1, difference(sd,et), typeOfInput)
            outputStringUrdu = ur
            outputStringEnglish = en
        }

    }else if (fixedDurations[index].type == DurationType.TUHR||
        fixedDurations[index].type == DurationType.TUHR_MUBTADIA||
        fixedDurations[index].type==DurationType.TUHR_MUBTADIA_BECAME_A_MUTADA_NOW){
        val time = fixedDurations[index].timeInMilliseconds
        val (ur, en) = replacement(Strings::durationPaki, PlcHolder.DUR1, time, typeOfInput)
        outputStringUrdu = ur
        outputStringEnglish = en
        if(fixedDurations[index].type == DurationType.TUHR_MUBTADIA_BECAME_A_MUTADA_NOW){
            outputStringUrdu += StringsOfLanguages.URDU.becamemutadah
            outputStringEnglish += StringsOfLanguages.ENGLISH.becamemutadah
        }

    }else if (fixedDurations[index].type == DurationType.TUHREFAASID||fixedDurations[index].type==DurationType.TUHREFAASID_MUBTADIA){
        val (ur, en) = replacement(Strings::durationTuhrefasid, PlcHolder.DUR1, fixedDurations[index].timeInMilliseconds, typeOfInput)
        outputStringUrdu = ur
        outputStringEnglish = en
    }else if (fixedDurations[index].type==DurationType.TUHREFAASID_WITH_ISTEHAZA||
        fixedDurations[index].type==DurationType.TUHREFAASID_MUBTADIA_WITH_ISTEHAZA){
        val (ur, en) = replacement(Strings::durationTuhreFasidWithAddition, PlcHolder.DUR1, fixedDurations[index].istihazaAfter, typeOfInput)
            .replacement(PlcHolder.DUR2, (fixedDurations[index].istihazaAfter+fixedDurations[index].timeInMilliseconds), typeOfInput)
        outputStringUrdu = ur
        outputStringEnglish = en
    }else if (fixedDurations[index].type == DurationType.DAM_IN_NIFAS_PERIOD){
        val sd = fixedDurations[index].startDate
        val et = fixedDurations[index].endDate
        if(fixedDurations[index].days<=40){
            val (ur, en) = replacement(Strings::durationNifas, PlcHolder.DUR1, difference(sd,et), typeOfInput)
            outputStringUrdu = ur
            outputStringEnglish = en
        }else{//more than 40
            val (ur, en) = replacement(Strings::durationDam, PlcHolder.DUR1, fixedDurations[index].timeInMilliseconds, typeOfInput)
            outputStringUrdu = ur
            outputStringEnglish = en
        }
    }else if (fixedDurations[index].type == DurationType.TUHR_IN_HAML){
        if(!isMustabeen){
            val time = fixedDurations[index].timeInMilliseconds
            val (ur, en) = replacement(Strings::durationPaki, PlcHolder.DUR1, time, typeOfInput)
            outputStringUrdu = ur
            outputStringEnglish = en
        }
    }else if (fixedDurations[index].type == DurationType.TUHREFAASID_IN_HAML){
        if(!isMustabeen){
            val (ur, en) = replacement(Strings::durationTuhreFasidWithAddition, PlcHolder.DUR1, fixedDurations[index].istihazaAfter, typeOfInput)
                .replacement(PlcHolder.DUR2, fixedDurations[index].timeInMilliseconds, typeOfInput)
                .replacement(PlcHolder.DUR2, (fixedDurations[index].istihazaAfter+fixedDurations[index].timeInMilliseconds), typeOfInput)
            outputStringUrdu = ur
            outputStringEnglish = en
        }
    }else if (fixedDurations[index].type == DurationType.DAM_IN_HAML){
        println(PlcHolder.Msg.PLACEHOLDER)
    }else if (fixedDurations[index].type == DurationType.TUHR_BIGGER_THAN_6_MONTHS){
        val (ur, en) = replacement(Strings::twomonthstuhr, PlcHolder.DUR1, fixedDurations[index].timeInMilliseconds, typeOfInput)
        outputStringUrdu = ur
        outputStringEnglish = en
    }else if(fixedDurations[index].type == DurationType.ISTEHAZA_AYYAMEQABLIYYA){
        println(PlcHolder.Msg.PLACEHOLDER)
    }
    return OutputStringsLanguages(outputStringUrdu, outputStringEnglish)
}
fun outputStringHeaderLine(fixedDurations: MutableList<FixedDuration>,
                           index: Int,
                           typeOfInput: TypesOfInputs,
                           timeZone: String,
                           mustabeen:Boolean = true):OutputStringsLanguages{
    var outputStringUrdu = ""
    var outputStringEnglish = ""
    if (fixedDurations[index].type==DurationType.DAM||
        fixedDurations[index].type==DurationType.DAM_MUBTADIA){
        val sd:Instant = fixedDurations[index].startDate
        val et = fixedDurations[index].endDate
        if(fixedDurations[index].days in 3.0..10.0){//if it's between 3 and 10, write haiz
            val (ur, en) = replacement(Strings::haizdays, PlcHolder.DT1, sd, typeOfInput, timeZone)
                .replacement(PlcHolder.DT2, et, typeOfInput, timeZone)
                .replacement(PlcHolder.DUR1, (difference(sd,et)), typeOfInput)
            outputStringUrdu = ur
            outputStringEnglish = en
        }else{//bigger than 10
            if (fixedDurations[index].indices.size>1){//this dam is made up of more than 1
                val (ur, en) = replacement(Strings::continuosbleeding, PlcHolder.DT1, sd, typeOfInput, timeZone)
                    .replacement(PlcHolder.DT2, et, typeOfInput, timeZone)
                    .replacement(PlcHolder.DUR1, fixedDurations[index].timeInMilliseconds, typeOfInput)
                outputStringUrdu = ur
                outputStringEnglish = en
            }else{
                val (ur, en) = replacement(Strings::blooddays, PlcHolder.DT1, sd, typeOfInput, timeZone)
                    .replacement(PlcHolder.DT2, et, typeOfInput, timeZone)
                    .replacement(PlcHolder.DUR1, fixedDurations[index].timeInMilliseconds, typeOfInput)
                outputStringUrdu = ur
                outputStringEnglish = en
            }
        }

    }else if (fixedDurations[index].type == DurationType.TUHR||
        fixedDurations[index].type == DurationType.TUHR_MUBTADIA||
        fixedDurations[index].type==DurationType.TUHR_MUBTADIA_BECAME_A_MUTADA_NOW){
        val time = fixedDurations[index].timeInMilliseconds
        val (ur, en) = replacement(Strings::pakidays, PlcHolder.DUR1, time, typeOfInput)
        outputStringUrdu = ur
        outputStringEnglish = en
        if(fixedDurations[index].type == DurationType.TUHR_MUBTADIA_BECAME_A_MUTADA_NOW){
            outputStringUrdu += StringsOfLanguages.URDU.becamemutadah
            outputStringEnglish += StringsOfLanguages.ENGLISH.becamemutadah
        }

    }else if (fixedDurations[index].type == DurationType.TUHREFAASID||fixedDurations[index].type==DurationType.TUHREFAASID_MUBTADIA){
        val (ur, en) = replacement(Strings::tuhrfasid, PlcHolder.DUR1, fixedDurations[index].timeInMilliseconds, typeOfInput)
        outputStringUrdu = ur
        outputStringEnglish = en
    }else if (fixedDurations[index].type==DurationType.TUHREFAASID_WITH_ISTEHAZA||
        fixedDurations[index].type==DurationType.TUHREFAASID_MUBTADIA_WITH_ISTEHAZA){
        val (ur, en) = replacement(Strings::tuhrfasidwithaddition, PlcHolder.DUR1, fixedDurations[index].istihazaAfter, typeOfInput)
            .replacement(PlcHolder.DUR2, fixedDurations[index].timeInMilliseconds, typeOfInput)
            .replacement(PlcHolder.DUR2, (fixedDurations[index].istihazaAfter+fixedDurations[index].timeInMilliseconds), typeOfInput)
        outputStringUrdu = ur
        outputStringEnglish = en
    }else if (fixedDurations[index].type == DurationType.DAM_IN_NIFAS_PERIOD){
        val sd = fixedDurations[index].startDate
        val et = fixedDurations[index].endDate
        if(fixedDurations[index].days<=40){
            val (ur, en) = replacement(Strings::nifasdays, PlcHolder.DT1, sd, typeOfInput, timeZone)
                .replacement(PlcHolder.DT2, et, typeOfInput, timeZone)
                .replacement(PlcHolder.DUR1, difference(sd,et), typeOfInput)
            outputStringUrdu = ur
            outputStringEnglish = en
        }else{//more than 40
            val (ur, en) = replacement(Strings::blooddays, PlcHolder.DT1, sd, typeOfInput, timeZone)
                .replacement(PlcHolder.DT2, et, typeOfInput, timeZone)
                .replacement(PlcHolder.DUR1, fixedDurations[index].timeInMilliseconds, typeOfInput)
            outputStringUrdu = ur
            outputStringEnglish = en
        }
    }else if (fixedDurations[index].type == DurationType.TUHR_IN_HAML){
        if(!mustabeen){
            val time = fixedDurations[index].timeInMilliseconds
            val (ur, en) = replacement(Strings::pakidays, PlcHolder.DUR1, time, typeOfInput)
            outputStringUrdu = ur
            outputStringEnglish = en
        }
    }else if (fixedDurations[index].type == DurationType.TUHREFAASID_IN_HAML){
        if(!mustabeen){
            val time = fixedDurations[index].timeInMilliseconds
            val (ur, en) = replacement(Strings::tuhrfasidwithaddition, PlcHolder.DUR1, fixedDurations[index].istihazaAfter, typeOfInput)
                .replacement(PlcHolder.DUR2, fixedDurations[index].timeInMilliseconds, typeOfInput)
                .replacement(PlcHolder.DUR2, (fixedDurations[index].istihazaAfter+fixedDurations[index].timeInMilliseconds), typeOfInput)
            outputStringUrdu = ur
            outputStringEnglish = en
        }
    }else if (fixedDurations[index].type == DurationType.DAM_IN_HAML){
        println(PlcHolder.Msg.PLACEHOLDER)
    }else if (fixedDurations[index].type == DurationType.TUHR_BIGGER_THAN_6_MONTHS){
        val (ur, en) = replacement(Strings::twomonthstuhr, PlcHolder.DUR1, fixedDurations[index].timeInMilliseconds, typeOfInput)
        outputStringUrdu = ur
        outputStringEnglish = en
    }else if (fixedDurations[index].type == DurationType.ISTEHAZA_AYYAMEQABLIYYA){
        val sd = fixedDurations[index].startDate
        val et = fixedDurations[index].endDate
        val (ur, en) = replacement(Strings::daysayyameqabliyya, PlcHolder.DT1, sd, typeOfInput, timeZone)
            .replacement(PlcHolder.DT2, et, typeOfInput, timeZone)
            .replacement(PlcHolder.DUR1, (difference(sd,et)), typeOfInput)
        outputStringUrdu = ur
        outputStringEnglish = en
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
//            str += "${UnicodeChars.ORANGE_DIAMOND} <b><em>From ${languagedDateFormat(startTime,TypesOfInputs.DATE_ONLY, Vls.Langs.ENGLISH, timeZone)} to ${languagedDateFormat(endTime,TypesOfInputs.DATE_ONLY, Vls.Langs.ENGLISH, timeZone)} is ${type}</em></b>\n\n"
//        }else if (durationType.type==DateTypes.YAQEENI_PAKI){
//            str += "${UnicodeChars.WHITE_DIAMOND} From ${languagedDateFormat(startTime,TypesOfInputs.DATE_ONLY, Vls.Langs.ENGLISH, timeZone)} to ${languagedDateFormat(endTime,TypesOfInputs.DATE_ONLY, Vls.Langs.ENGLISH, timeZone)} is ${type}\n\n"
//        }else if (durationType.type==DateTypes.YAQEENI_NA_PAKI){
//            str += "${UnicodeChars.RED_DIAMOND} From ${languagedDateFormat(startTime,TypesOfInputs.DATE_ONLY, Vls.Langs.ENGLISH, timeZone)} to ${languagedDateFormat(endTime,TypesOfInputs.DATE_ONLY, Vls.Langs.ENGLISH, timeZone)} is ${type}\n\n"
//        }else if (durationType.type==DateTypes.AYYAAM_E_SHAKK_KHUROOJ){
//            str += "${UnicodeChars.SNOWFLAKE} <b><em>From ${languagedDateFormat(startTime,TypesOfInputs.DATE_ONLY, Vls.Langs.ENGLISH, timeZone)} to ${languagedDateFormat(endTime,TypesOfInputs.DATE_ONLY, Vls.Langs.ENGLISH, timeZone)} is ${type}</em></b>\n\n"
//        }
//
//    }
//    return str
//}




