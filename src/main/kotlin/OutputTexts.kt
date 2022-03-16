import kotlin.js.Date
fun generateOutputStringPregnancy(fixedDurations: MutableList<FixedDuration>,
                         isDateOnly:Boolean, pregnancy: Pregnancy, endingOutputValues: EndingOutputValues, isDuration: Boolean):OutputTexts{
    var englishStr = ""
    var urduStr = ""
    val hazDatesList = getHaizDatesList(fixedDurations)
    urduStr+= generateLanguagedOutputStringPregnancy(fixedDurations,isDateOnly,pregnancy, endingOutputValues, isDuration).urduString
    englishStr+= generateLanguagedOutputStringPregnancy(fixedDurations,isDateOnly,pregnancy, endingOutputValues, isDuration).englishString

    val hazDatesStr = generateHazDatesStr(hazDatesList,isDateOnly)

    return OutputTexts(englishStr,urduStr, "",hazDatesList,endingOutputValues, fixedDurations)
}
fun generateOutputStringMubtadia(fixedDurations: MutableList<FixedDuration>,durations: List<Duration>,
                         isDateOnly:Boolean, endingOutputValues: EndingOutputValues, isDuration: Boolean):OutputTexts{
    var index = 0
    val hazDatesList = getHaizDatesList(fixedDurations)
    val urduStr = generateOutputString(fixedDurations, isDateOnly, endingOutputValues, isDuration).urduString
    val englishStr = generateOutputString(fixedDurations, isDateOnly, endingOutputValues, isDuration).englishString

    val hazDatesStr = generateHazDatesStr(hazDatesList,isDateOnly)

    return OutputTexts(englishStr,urduStr, hazDatesStr, hazDatesList,endingOutputValues, fixedDurations)
}
fun generateOutputStringMutadah(fixedDurations: MutableList<FixedDuration>,durations: List<Duration>,
                         isDateOnly:Boolean, endingOutputValues: EndingOutputValues, isDuration: Boolean):OutputTexts{
    var index = 0
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

    val urduStr = generateOutputString(fixedDurations, isDateOnly, endingOutputValues, isDuration).urduString
    val englishStr = generateOutputString(fixedDurations, isDateOnly, endingOutputValues, isDuration).englishString

    val hazDatesStr = generateHazDatesStr(hazDatesList,isDateOnly)

    return OutputTexts(englishStr,urduStr, hazDatesStr, hazDatesList,endingOutputValues, fixedDurations)
}

fun generateHazDatesStr(hazDatesList: MutableList<Entry>,isDateOnly: Boolean):String{
    var str = ""
    for(entry in hazDatesList){
        str+="From ${englishDateFormat(entry.startTime,isDateOnly)} to ${englishDateFormat(entry.endTime,isDateOnly)}<br>"
    }
    return str
}

fun generateLanguagedOutputStringPregnancy(fixedDurations: MutableList<FixedDuration>, isDateOnly: Boolean, pregnancy: Pregnancy, endingOutputValues: EndingOutputValues, isDuration: Boolean):OutputStringsLanguages{
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
            if(isDuration){
                strUrdu += outputStringHeaderLineDuration(fixedDurations,index, isDateOnly).urduString
                strEnglish += outputStringHeaderLineDuration(fixedDurations,index, isDateOnly).englishString
                strUrdu += outputStringBiggerThan10HallDurations(fixedDurations,index, isDateOnly).urduString
                strEnglish += outputStringBiggerThan10HallDurations(fixedDurations,index, isDateOnly).englishString
                strUrdu += outputStringBiggerThan40HallDuration(fixedDurations,index, isDateOnly).urduString
                strEnglish += outputStringBiggerThan40HallDuration(fixedDurations,index, isDateOnly).englishString
                if(fixedDurations[index].type==DurationType.HAML){
                    strUrdu += StringsOfLanguages.URDU.pregduration
                    strEnglish += StringsOfLanguages.ENGLISH.pregduration
                }
                if(fixedDurations[index].type==DurationType.WILADAT_ISQAT){
                    strUrdu += StringsOfLanguages.URDU.birthduration
                    strEnglish += StringsOfLanguages.ENGLISH.birthduration
                }

            }else{
                strUrdu += outputStringHeaderLine(fixedDurations,index, isDateOnly).urduString
                strEnglish += outputStringHeaderLine(fixedDurations,index, isDateOnly).englishString
                strUrdu += outputStringBiggerThan10Hall(fixedDurations,index, isDateOnly).urduString
                strEnglish += outputStringBiggerThan10Hall(fixedDurations,index, isDateOnly).englishString
                strUrdu += outputStringBiggerThan40Hall(fixedDurations,index, isDateOnly).urduString
                strEnglish += outputStringBiggerThan40Hall(fixedDurations,index, isDateOnly).englishString
                if(fixedDurations[index].type==DurationType.HAML){
                    strUrdu += StringsOfLanguages.URDU.preg
                    strEnglish += StringsOfLanguages.ENGLISH.preg
                }
                if(fixedDurations[index].type==DurationType.WILADAT_ISQAT){
                    strUrdu += StringsOfLanguages.URDU.birth.replace("date1", "${urduDateFormat(birthTime, isDateOnly)}")
                    strEnglish += StringsOfLanguages.ENGLISH.birth.replace("date1", "${englishDateFormat(birthTime, isDateOnly)}")
                    if(index<fixedDurations.size-2){//if there is something after wiladat
                        strUrdu += StringsOfLanguages.URDU.afterpregheader
                        strEnglish += StringsOfLanguages.ENGLISH.afterpregheader
                    }
                }

            }

        }
        strUrdu += outputStringFinalLines(isDateOnly, endingOutputValues, isDuration).urduString
        strEnglish += outputStringFinalLines(isDateOnly, endingOutputValues, isDuration).englishString



    }else{//if it is ghair mustabeenulkhilqat
        strUrdu += StringsOfLanguages.URDU.headerline
        strEnglish += StringsOfLanguages.ENGLISH.headerline

        for(index in fixedDurations.indices){
            strUrdu += outputStringHeaderLine(fixedDurations,index, isDateOnly).urduString
            strEnglish += outputStringHeaderLine(fixedDurations,index, isDateOnly).englishString
            strUrdu += outputStringBiggerThan10Hall(fixedDurations,index, isDateOnly).urduString
            strEnglish += outputStringBiggerThan10Hall(fixedDurations,index, isDateOnly).englishString
            if(fixedDurations[index].type==DurationType.HAML){
                strUrdu += StringsOfLanguages.URDU.preg
                strEnglish += StringsOfLanguages.ENGLISH.preg
            }
            if(fixedDurations[index].type==DurationType.WILADAT_ISQAT){
                strUrdu += StringsOfLanguages.URDU.earlymiscarriage.replace("date1", "${urduDateFormat(birthTime, isDateOnly)}")
                strEnglish += StringsOfLanguages.ENGLISH.earlymiscarriage.replace("date1", "${englishDateFormat(birthTime, isDateOnly)}")
                if(index<fixedDurations.size-2){//if there is something after wiladat
                    strUrdu += StringsOfLanguages.URDU.afterpregheader
                    strEnglish += StringsOfLanguages.ENGLISH.afterpregheader
                }
            }

        }
        strUrdu += outputStringFinalLines (isDateOnly, endingOutputValues, isDuration).urduString
        strEnglish += outputStringFinalLines (isDateOnly, endingOutputValues, isDuration).englishString

    }


    return OutputStringsLanguages(strUrdu,strEnglish)
}

fun generateOutputString(fixedDurations: MutableList<FixedDuration>, isDateOnly: Boolean, endingOutputValues: EndingOutputValues, isDuration:Boolean):OutputStringsLanguages{
    //ToDo: figure out how to do languages for real
    var strUrdu = StringsOfLanguages.URDU.answer //جواب:
    var strEnglish = StringsOfLanguages.ENGLISH.answer //جواب:

    strUrdu += StringsOfLanguages.URDU.headerline //اس ترتیب سے خون آیا اور پاکی ملی
    strEnglish += StringsOfLanguages.ENGLISH.headerline //اس ترتیب سے خون آیا اور پاکی ملی
    var index = 0
    while (index<fixedDurations.size){
        if(isDuration){
            strUrdu += outputStringHeaderLineDuration(fixedDurations,index, isDateOnly).urduString //اس تاریخ سے اس تاریخ تک اتنے دن حیض
            strEnglish += outputStringHeaderLineDuration(fixedDurations,index, isDateOnly).englishString //اس تاریخ سے اس تاریخ تک اتنے دن حیض
            strUrdu += outputStringBiggerThan10HallDurations(fixedDurations,index, isDateOnly).urduString
            strEnglish += outputStringBiggerThan10HallDurations(fixedDurations,index, isDateOnly).englishString
            if(index==fixedDurations.size-1){//if this os the last index
                strUrdu += outputStringFinalLines(isDateOnly, endingOutputValues, isDuration).urduString
                strEnglish += outputStringFinalLines(isDateOnly, endingOutputValues, isDuration).englishString
            }
        }else{
            strUrdu += outputStringHeaderLine(fixedDurations,index, isDateOnly).urduString //اس تاریخ سے اس تاریخ تک اتنے دن حیض
            strEnglish += outputStringHeaderLine(fixedDurations,index, isDateOnly).englishString //اس تاریخ سے اس تاریخ تک اتنے دن حیض
            strUrdu += outputStringBiggerThan10Hall(fixedDurations,index, isDateOnly).urduString
            strEnglish += outputStringBiggerThan10Hall(fixedDurations,index, isDateOnly).englishString
            if(index==fixedDurations.size-1){//if this os the last index
                strUrdu += outputStringFinalLines(isDateOnly, endingOutputValues, isDuration).urduString
                strEnglish += outputStringFinalLines(isDateOnly, endingOutputValues, isDuration).englishString
            }
        }
        index++
    }
    return OutputStringsLanguages(strUrdu,strEnglish)
}
fun generateUrduOutputStringMubtadia(fixedDurations: MutableList<FixedDuration>, isDateOnly: Boolean, endingOutputValues: EndingOutputValues, isDuration: Boolean):String{
    //ToDo: figure out how to do languages for real
    var str = StringsOfLanguages.URDU.answer //جواب:
    str += StringsOfLanguages.URDU.headerlinemubtadia //اس ترتیب سے خون آیا اور پاکی ملی
    var index = 0
    while (index<fixedDurations.size){
        str += outputStringHeaderLine(fixedDurations,index, isDateOnly)
        str += outputStringBiggerThan10Hall(fixedDurations,index, isDateOnly)
        if(index==fixedDurations.size-1){//if this os the last index
            str += outputStringFinalLines(isDateOnly, endingOutputValues, isDuration)
        }
        index++
    }
    return str
}

fun outputStringFinalLines(isDateOnly: Boolean, endingOutputValues: EndingOutputValues, isDuration: Boolean):OutputStringsLanguages{

    var strUrdu = ""
    var strEnglish = ""
    val aadats = endingOutputValues.aadats
    strUrdu+=outputStringAadatLine(isDateOnly, aadats).urduString
    strEnglish+=outputStringAadatLine(isDateOnly, aadats).englishString
    if(isDuration){return OutputStringsLanguages(strUrdu,strEnglish)}
    val filHaal = endingOutputValues.filHaalPaki
    strUrdu+=outputStringFilHaalLine(filHaal).urduString
    strEnglish+=outputStringFilHaalLine(filHaal).englishString
    val futureDates = endingOutputValues.futureDateType
    strUrdu+=outputStringAskAgainLine(isDateOnly, futureDates).urduString
    strEnglish+=outputStringAskAgainLine(isDateOnly, futureDates).englishString

    //plis note down line
    strUrdu+=StringsOfLanguages.URDU.writedown
    strEnglish+=StringsOfLanguages.ENGLISH.writedown

    //Allahu Aaalam line
    strUrdu+=StringsOfLanguages.URDU.allahknows
    strEnglish+=StringsOfLanguages.ENGLISH.allahknows

    return OutputStringsLanguages(strUrdu,strEnglish)
}
fun outputStringFilHaalLine(filHaalPaki:Boolean):OutputStringsLanguages{
    val filHaalPakiStrUrdu = StringsOfLanguages.URDU.currentpaki
    val filHaalPakiStrEnglish = StringsOfLanguages.ENGLISH.currentpaki
    val filHaalHaizStrUrdu = StringsOfLanguages.URDU.currenthaiz
    val filHaalHaizStrEnglish = StringsOfLanguages.URDU.currenthaiz
    return if(filHaalPaki){
        OutputStringsLanguages(filHaalPakiStrUrdu,filHaalPakiStrEnglish)
    }else{
        OutputStringsLanguages(filHaalHaizStrUrdu, filHaalHaizStrEnglish)
    }
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

fun outputStringAskAgainLine(isDateOnly: Boolean, futureDates: MutableList<FutureDateType>):OutputStringsLanguages{
    var strUrdu = ""
    var strEnglish = ""
    println(futureDates.size)
    println("started ask again lines")
    for(futureDate in futureDates){
        val date = futureDate.date
        val type= futureDate.futureDates
        println(date)
        println(type)
        if(type==TypesOfFutureDates.END_OF_AADAT_HAIZ){
            strUrdu += StringsOfLanguages.URDU.haizend.replace("date1", "${urduDateFormat(date, isDateOnly)}")
            strEnglish += StringsOfLanguages.ENGLISH.haizend.replace("date1", "${englishDateFormat(date, isDateOnly)}")
        }else if(type==TypesOfFutureDates.END_OF_AADAT_TUHR){
            strUrdu += StringsOfLanguages.URDU.endofpaki.replace("date1", "${urduDateFormat(date, isDateOnly)}")
            strEnglish += StringsOfLanguages.ENGLISH.endofpaki.replace("date1", "${englishDateFormat(date, isDateOnly)}")
        }else if(type==TypesOfFutureDates.IC_FORBIDDEN_DATE){
            strUrdu += StringsOfLanguages.URDU.sexnotallowed.replace("date1", "${urduDateFormat(date, isDateOnly)}")
            strEnglish += StringsOfLanguages.ENGLISH.sexnotallowed.replace("date1", "${englishDateFormat(date, isDateOnly)}")
        }else if(type==TypesOfFutureDates.AFTER_TEN_DAYS){
            strUrdu += StringsOfLanguages.URDU.aftertendays.replace("date1", "${urduDateFormat(date, isDateOnly)}")
            strEnglish += StringsOfLanguages.ENGLISH.aftertendays.replace("date1", "${englishDateFormat(date, isDateOnly)}")
        }else if(type==TypesOfFutureDates.FORTY_DAYS){
            strUrdu += StringsOfLanguages.URDU.afterfortydays.replace("date1", "${urduDateFormat(date, isDateOnly)}")
            strEnglish += StringsOfLanguages.ENGLISH.afterfortydays.replace("date1", "${englishDateFormat(date, isDateOnly)}")
        }else if(type==TypesOfFutureDates.BEFORE_THREE_DAYS_MASLA_WILL_CHANGE){
            strUrdu += StringsOfLanguages.URDU.bleedingstopsbeforethreemaslachanges.replace("date1", "${urduDateFormat(date, isDateOnly)}")
            strEnglish += StringsOfLanguages.ENGLISH.bleedingstopsbeforethreemaslachanges.replace("date1", "${englishDateFormat(date, isDateOnly)}")
        }else if(type==TypesOfFutureDates.BEFORE_THREE_DAYS){
            strUrdu += StringsOfLanguages.URDU.bleedingstopsbeforethree.replace("date1", "${urduDateFormat(date, isDateOnly)}")
            strEnglish += StringsOfLanguages.ENGLISH.bleedingstopsbeforethree.replace("date1", "${englishDateFormat(date, isDateOnly)}")
        }else if(type==TypesOfFutureDates.IHTIYATI_GHUSL){
            strUrdu += StringsOfLanguages.URDU.ihtiyatighusl.replace("date1", "${urduDateFormat(date, isDateOnly)}")
            strEnglish += StringsOfLanguages.ENGLISH.ihtiyatighusl.replace("date1", "${englishDateFormat(date, isDateOnly)}")
        }else if(type==TypesOfFutureDates.A3_CHANGING_TO_A2){
            strUrdu += StringsOfLanguages.URDU.situationmaychange.replace("date1", "${urduDateFormat(date, isDateOnly)}")
            strEnglish += StringsOfLanguages.ENGLISH.situationmaychange.replace("date1", "${englishDateFormat(date, isDateOnly)}")
        }else if(type==TypesOfFutureDates.BEFORE_TEN_DAYS_AYYAMEQABLIYYAH){
            strUrdu += StringsOfLanguages.URDU.beforetendaysayyameqabliyyaallconsideredhaiz.replace("date1", "${urduDateFormat(date, isDateOnly)}")
            strEnglish += StringsOfLanguages.ENGLISH.beforetendaysayyameqabliyyaallconsideredhaiz.replace("date1", "${englishDateFormat(date, isDateOnly)}")
        }else if(type==TypesOfFutureDates.START_OF_AADAT_AYYAMEQABLIYYA){
            strUrdu += StringsOfLanguages.URDU.endofistehazaayyameqabliyya.replace("date1", "${urduDateFormat(date, isDateOnly)}")
            strEnglish += StringsOfLanguages.ENGLISH.endofistehazaayyameqabliyya.replace("date1", "${englishDateFormat(date, isDateOnly)}")
        }
    }

    println(strUrdu)
    return OutputStringsLanguages(strUrdu,strEnglish)
}
fun outputStringAadatLine(isDateOnly: Boolean, aadats:AadatsOfHaizAndTuhr?):OutputStringsLanguages{
    var strUrdu = ""
    var strEnglish = ""

    return if(aadats==null){
        println("aadats were null")
        OutputStringsLanguages("","")
    }else{
        val aadatTuhr = aadats.aadatTuhr
        val aadatHaiz = aadats.aadatHaiz
        if(aadatHaiz==-1L && aadatTuhr==-1L){
            strUrdu+= StringsOfLanguages.URDU.thereisnoaadat
            strEnglish+= StringsOfLanguages.ENGLISH.thereisnoaadat
        }else if(aadatHaiz!=-1L && aadatTuhr==-1L){
            println("aadat of haiz is ${daysHoursMinutesDigitalEnglish(aadatHaiz,isDateOnly)}")
            strUrdu+= StringsOfLanguages.URDU.aadatofhaizonly
                .replace("duration1", "${daysHoursMinutesDigitalUrdu(aadatHaiz, isDateOnly)}")
            strEnglish+= StringsOfLanguages.ENGLISH.aadatofhaizonly
                .replace("duration1", "${daysHoursMinutesDigitalEnglish(aadatHaiz, isDateOnly)}")
        }else{
            strUrdu+= StringsOfLanguages.URDU.habit
                .replace("duration1", "${daysHoursMinutesDigitalUrdu(aadatHaiz, isDateOnly)}")
                .replace("duration2", "${daysHoursMinutesDigitalUrdu(aadatTuhr, isDateOnly)}")
            strEnglish+= StringsOfLanguages.ENGLISH.habit
                .replace("duration1", "${daysHoursMinutesDigitalEnglish(aadatHaiz, isDateOnly)}")
                .replace("duration2", "${daysHoursMinutesDigitalEnglish(aadatTuhr, isDateOnly)}")
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
fun outputStringBiggerThan10HallDurations(fixedDurations: MutableList<FixedDuration>, index: Int, isDateOnly: Boolean):OutputStringsLanguages{
    var strUrdu = ""
    var strEnglish = ""

    if((fixedDurations[index].days>10 &&
                (fixedDurations[index].type==DurationType.DAM||
                        fixedDurations[index].type==DurationType.DAM_MUBTADIA))){

        strUrdu += TAB
        strEnglish += TAB

        for(duration in fixedDurations[index].biggerThanTen!!.durationsList){
            if(duration.type == DurationType.ISTIHAZA_BEFORE){
                strUrdu+= StringsOfLanguages.URDU.startingFromIstehaza.replace("duration1", "${daysHoursMinutesDigitalUrdu(duration.timeInMilliseconds, isDateOnly)}")
                strEnglish+= StringsOfLanguages.ENGLISH.startingFromIstehaza.replace("duration1", "${daysHoursMinutesDigitalEnglish(duration.timeInMilliseconds, isDateOnly)}")

            }else if(duration.type == DurationType.HAIZ){
                if(duration==fixedDurations[index].biggerThanTen!!.durationsList[0]){
                    strUrdu+= StringsOfLanguages.URDU.startingFromHaiz.replace("duration1", "${daysHoursMinutesDigitalUrdu(duration.timeInMilliseconds, isDateOnly)}")
                    strEnglish+= StringsOfLanguages.ENGLISH.startingFromHaiz.replace("duration1", "${daysHoursMinutesDigitalEnglish(duration.timeInMilliseconds, isDateOnly)}")
                }else{
                    strUrdu+= StringsOfLanguages.URDU.followedByHaizAfter.replace("duration1", "${daysHoursMinutesDigitalUrdu(duration.timeInMilliseconds, isDateOnly)}")
                    strUrdu+= StringsOfLanguages.ENGLISH.followedByHaizAfter.replace("duration1", "${daysHoursMinutesDigitalEnglish(duration.timeInMilliseconds, isDateOnly)}")
                }
            }else if(duration.type == DurationType.ISTIHAZA_AFTER){
                strUrdu+= StringsOfLanguages.URDU.followedByistehazaAfter.replace("duration1", "${daysHoursMinutesDigitalUrdu(duration.timeInMilliseconds, isDateOnly)}")
                strEnglish+= StringsOfLanguages.ENGLISH.followedByistehazaAfter.replace("duration1", "${daysHoursMinutesDigitalEnglish(duration.timeInMilliseconds, isDateOnly)}")

            }else if(duration.type == DurationType.LESS_THAN_3_HAIZ){
                strUrdu+= StringsOfLanguages.URDU.followedByHaizAfter.replace("duration1", "${daysHoursMinutesDigitalUrdu(duration.timeInMilliseconds, isDateOnly)}")
                strEnglish+= StringsOfLanguages.ENGLISH.followedByHaizAfter.replace("duration1", "${daysHoursMinutesDigitalEnglish(duration.timeInMilliseconds, isDateOnly)}")
                //maybe we'll wanna add something about itibaar bil khawateem
            }else if(duration.type == DurationType.NIFAAS){
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

fun outputStringBiggerThan10Hall(fixedDurations: MutableList<FixedDuration>, index: Int, isDateOnly: Boolean):OutputStringsLanguages{
    var strUrdu = ""
    var strEnglish = ""

    fun haizLine(sd:Date, ed:Date, isDateOnly: Boolean):OutputStringsLanguages{
        return OutputStringsLanguages(
            StringsOfLanguages.URDU.haizdaysinsolution.replace("date1", "${urduDateFormat(sd, isDateOnly)}").replace("date2", "${urduDateFormat(ed,isDateOnly)}").replace("duration1", "${daysHoursMinutesDigitalUrdu((difference(sd,ed)), isDateOnly)}"),
            StringsOfLanguages.ENGLISH.haizdaysinsolution.replace("date1", "${englishDateFormat(sd, isDateOnly)}").replace("date2", "${englishDateFormat(ed,isDateOnly)}").replace("duration1", "${daysHoursMinutesDigitalEnglish((difference(sd,ed)), isDateOnly)}")
        )
    }
    fun istihazaLine(sd:Date, ed:Date, isDateOnly: Boolean):OutputStringsLanguages{
        return OutputStringsLanguages(
            StringsOfLanguages.URDU.istihazadays.replace("date1", "${urduDateFormat(sd, isDateOnly)}").replace("date2", "${urduDateFormat(ed,isDateOnly)}").replace("duration1", "${daysHoursMinutesDigitalUrdu(difference(sd,ed), isDateOnly)}"),
            StringsOfLanguages.ENGLISH.istihazadays.replace("date1", "${englishDateFormat(sd, isDateOnly)}").replace("date2", "${englishDateFormat(ed,isDateOnly)}").replace("duration1", "${daysHoursMinutesDigitalEnglish(difference(sd,ed), isDateOnly)}")

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
            if(duration.type == DurationType.ISTIHAZA_BEFORE){
                strUrdu+= istihazaLine(duration.startTime,duration.endDate,isDateOnly).urduString
                strEnglish+= istihazaLine(duration.startTime,duration.endDate,isDateOnly).englishString
                strUrdu+= StringsOfLanguages.URDU.istihazadetailslineone
                strEnglish+= StringsOfLanguages.ENGLISH.istihazadetailslineone

            }else if(duration.type == DurationType.HAIZ){
                strUrdu+= haizLine(duration.startTime,duration.endDate,isDateOnly).urduString
                strEnglish+= haizLine(duration.startTime,duration.endDate,isDateOnly).englishString

            }else if(duration.type == DurationType.ISTIHAZA_AFTER){
                strUrdu+= istihazaLine(duration.startTime,duration.endDate,isDateOnly).urduString
                strEnglish+= istihazaLine(duration.startTime,duration.endDate,isDateOnly).englishString
                strUrdu+= StringsOfLanguages.URDU.istihazadetailslinetwo.replace("date1", "${urduDateFormat(duration.startTime,isDateOnly)}")
                strEnglish+= StringsOfLanguages.ENGLISH.istihazadetailslinetwo.replace("date1", "${englishDateFormat(duration.startTime,isDateOnly)}")
                strUrdu+= StringsOfLanguages.URDU.istihazadetailslineone
                strEnglish+= StringsOfLanguages.ENGLISH.istihazadetailslineone

            }else if(duration.type == DurationType.LESS_THAN_3_HAIZ){
                strUrdu+= haizLine(duration.startTime,duration.endDate,isDateOnly).urduString
                strEnglish+= haizLine(duration.startTime,duration.endDate,isDateOnly).englishString
                //maybe we'll wanna add something about itibaar bil khawateem
            }
        }

        strUrdu += StringsOfLanguages.URDU.dashesline
        strEnglish += StringsOfLanguages.ENGLISH.dashesline
    }

    return OutputStringsLanguages(strUrdu,strEnglish)
}
fun outputStringBiggerThan40HallDuration(fixedDurations: MutableList<FixedDuration>, index: Int, isDateOnly: Boolean):OutputStringsLanguages{
    var strUrdu = ""
    var strEnglish = ""
    if(fixedDurations[index].days>40&&fixedDurations[index].type==DurationType.DAM_IN_NIFAAS_PERIOD){
        strUrdu+=TAB
        strEnglish+=TAB
        for(duration in fixedDurations[index].biggerThanForty!!.durationsList){
            if(duration.type==DurationType.NIFAAS){
                strUrdu+= StringsOfLanguages.URDU.startingFromNifas.replace("duration1", "${daysHoursMinutesDigitalUrdu(duration.timeInMilliseconds, isDateOnly)}")
                strEnglish+= StringsOfLanguages.ENGLISH.startingFromNifas.replace("duration1", "${daysHoursMinutesDigitalEnglish(duration.timeInMilliseconds, isDateOnly)}")
            }else if(duration.type==DurationType.ISTIHAZA_AFTER){
                strUrdu+= StringsOfLanguages.URDU.followedByistehazaAfter.replace("duration1", "${daysHoursMinutesDigitalUrdu(duration.timeInMilliseconds, isDateOnly)}")
                strEnglish+= StringsOfLanguages.ENGLISH.followedByistehazaAfter.replace("duration1", "${daysHoursMinutesDigitalEnglish(duration.timeInMilliseconds, isDateOnly)}")
            }else if(duration.type==DurationType.HAIZ){
                strUrdu+= StringsOfLanguages.URDU.followedByHaizAfter.replace("duration1", "${daysHoursMinutesDigitalUrdu(duration.timeInMilliseconds, isDateOnly)}")
                strEnglish+= StringsOfLanguages.ENGLISH.followedByHaizAfter.replace("duration1", "${daysHoursMinutesDigitalEnglish(duration.timeInMilliseconds, isDateOnly)}")
            }else if(duration.type == DurationType.LESS_THAN_3_HAIZ){
                strUrdu+= StringsOfLanguages.URDU.followedByHaizAfter.replace("duration1", "${daysHoursMinutesDigitalUrdu(duration.timeInMilliseconds, isDateOnly)}")
                strUrdu+= StringsOfLanguages.ENGLISH.followedByHaizAfter.replace("duration1", "${daysHoursMinutesDigitalEnglish(duration.timeInMilliseconds, isDateOnly)}")
                //maybe we'll wanna add something about itibaar bil khawateem
            }
        }
         }
    if(strUrdu!=""&&strEnglish!=""){
        strUrdu+=StringsOfLanguages.URDU.khatimaplusnewline
        strEnglish+=StringsOfLanguages.ENGLISH.khatimaplusnewline
    }
    return OutputStringsLanguages(strUrdu, strEnglish)
}
fun outputStringBiggerThan40Hall(fixedDurations: MutableList<FixedDuration>, index: Int, isDateOnly: Boolean):OutputStringsLanguages{

    var strUrdu = ""
    var strEnglish = ""

    fun nifasLine(sd:Date, ed:Date, isDateOnly: Boolean):OutputStringsLanguages{
        return OutputStringsLanguages(
            StringsOfLanguages.URDU.nifasdaysinsolution.replace("date1", "${urduDateFormat(sd, isDateOnly)}").replace("date2", "${urduDateFormat(ed,isDateOnly)}").replace("duration1", "${daysHoursMinutesDigitalUrdu((difference(sd,ed)), isDateOnly)}"),
            StringsOfLanguages.ENGLISH.nifasdaysinsolution.replace("date1", "${englishDateFormat(sd, isDateOnly)}").replace("date2", "${englishDateFormat(ed,isDateOnly)}").replace("duration1", "${daysHoursMinutesDigitalEnglish((difference(sd,ed)), isDateOnly)}")
        )
    }
    fun haizLine(sd:Date, ed:Date, isDateOnly: Boolean):OutputStringsLanguages{
        return OutputStringsLanguages(
            StringsOfLanguages.URDU.haizdaysinsolution.replace("date1","${urduDateFormat(sd, isDateOnly)}").replace("date2", "${urduDateFormat(ed, isDateOnly)}").replace("duration1", "${daysHoursMinutesDigitalUrdu((difference(sd,ed)), isDateOnly)}"),
            StringsOfLanguages.ENGLISH.haizdaysinsolution.replace("date1","${englishDateFormat(sd, isDateOnly)}").replace("date2", "${englishDateFormat(ed, isDateOnly)}").replace("duration1", "${daysHoursMinutesDigitalEnglish((difference(sd,ed)), isDateOnly)}")
        )
    }
    fun istihazaLine(sd:Date, ed:Date, isDateOnly: Boolean):OutputStringsLanguages{
        return OutputStringsLanguages(
            StringsOfLanguages.URDU.istihazadays.replace("date1", "${urduDateFormat(sd, isDateOnly)}").replace("date2", "${urduDateFormat(ed,isDateOnly)}").replace("duration1", "${daysHoursMinutesDigitalUrdu(difference(sd,ed), isDateOnly)}"),
            StringsOfLanguages.ENGLISH.istihazadays.replace("date1", "${englishDateFormat(sd, isDateOnly)}").replace("date2", "${englishDateFormat(ed,isDateOnly)}").replace("duration1", "${daysHoursMinutesDigitalEnglish(difference(sd,ed), isDateOnly)}")
        )
    }

    if(fixedDurations[index].days>40&&fixedDurations[index].type==DurationType.DAM_IN_NIFAAS_PERIOD){
        strUrdu += StringsOfLanguages.URDU.dashesline
        strEnglish += StringsOfLanguages.ENGLISH.dashesline
        strUrdu += StringsOfLanguages.URDU.solution
        strEnglish += StringsOfLanguages.ENGLISH.solution

        for(duration in fixedDurations[index].biggerThanForty!!.durationsList){
            if(duration.type==DurationType.NIFAAS){
                strUrdu+= nifasLine(duration.startTime,duration.endDate, isDateOnly).urduString
                strEnglish+= nifasLine(duration.startTime,duration.endDate, isDateOnly).englishString
            }else if(duration.type==DurationType.ISTIHAZA_AFTER){
                strUrdu+= istihazaLine(duration.startTime,duration.endDate, isDateOnly).urduString
                strEnglish+= istihazaLine(duration.startTime,duration.endDate, isDateOnly).englishString
                strUrdu+= StringsOfLanguages.URDU.istihazadetailslinetwo.replace("date1", "${urduDateFormat(duration.startTime,isDateOnly)}")
                strEnglish+= StringsOfLanguages.ENGLISH.istihazadetailslinetwo.replace("date1", "${englishDateFormat(duration.startTime,isDateOnly)}")
                strUrdu+= StringsOfLanguages.URDU.istihazadetailslineone
                strEnglish+= StringsOfLanguages.ENGLISH.istihazadetailslineone

            }else if(duration.type==DurationType.HAIZ){
                strUrdu+= haizLine(duration.startTime,duration.endDate, isDateOnly).urduString
                strEnglish+= haizLine(duration.startTime,duration.endDate, isDateOnly).englishString

            }else if(duration.type == DurationType.LESS_THAN_3_HAIZ){
                strUrdu+= haizLine(duration.startTime,duration.endDate,isDateOnly).urduString
                strEnglish+= haizLine(duration.startTime,duration.endDate,isDateOnly).englishString
                //maybe we'll wanna add something about itibaar bil khawateem
            }
        }
        strUrdu += StringsOfLanguages.URDU.dashesline
        strEnglish += StringsOfLanguages.ENGLISH.dashesline
    }

    return OutputStringsLanguages(strUrdu,strEnglish)
}
fun outputStringHeaderLineDuration(fixedDurations: MutableList<FixedDuration>, index: Int, isDateOnly: Boolean):OutputStringsLanguages{
    //in duration we just give the fixed duration
    var outputStringUrdu = ""
    var outputStringEnglish = ""
    if (fixedDurations[index].type==DurationType.DAM||
        fixedDurations[index].type==DurationType.DAM_MUBTADIA){
        val sd:Date = fixedDurations[index].startDate
        val et = fixedDurations[index].endDate
        if(fixedDurations[index].days in 3.0..10.0){//if it's between 3 and 10, write haiz
            outputStringUrdu = StringsOfLanguages.URDU.durationHaiz.replace("duration1", "${daysHoursMinutesDigitalUrdu((difference(sd,et)), isDateOnly)}")
            outputStringEnglish = StringsOfLanguages.ENGLISH.durationHaiz.replace("duration1", "${daysHoursMinutesDigitalEnglish((difference(sd,et)), isDateOnly)}")
        }else{//bigger than 10
            outputStringUrdu = StringsOfLanguages.URDU.durationDam.replace("duration1", "${daysHoursMinutesDigitalUrdu((difference(sd,et)), isDateOnly)}")
            outputStringEnglish = StringsOfLanguages.ENGLISH.durationDam.replace("duration1", "${daysHoursMinutesDigitalEnglish((difference(sd,et)), isDateOnly)}")
        }

    }else if (fixedDurations[index].type == DurationType.TUHR||
        fixedDurations[index].type == DurationType.TUHR_MUBTADIA||
        fixedDurations[index].type==DurationType.TUHR_MUBTADIA_BECAME_A_MUTADA_NOW){
        val time = fixedDurations[index].timeInMilliseconds
        outputStringUrdu =  StringsOfLanguages.URDU.durationPaki.replace("duration1", "${daysHoursMinutesDigitalUrdu(time, isDateOnly)}")
        outputStringEnglish =  StringsOfLanguages.ENGLISH.durationPaki.replace("duration1", "${daysHoursMinutesDigitalEnglish(time, isDateOnly)}")
        if(fixedDurations[index].type == DurationType.TUHR_MUBTADIA_BECAME_A_MUTADA_NOW){
            outputStringUrdu+=StringsOfLanguages.URDU.becamemutadah
            outputStringEnglish+=StringsOfLanguages.ENGLISH.becamemutadah
        }

    }else if (fixedDurations[index].type == DurationType.TUHREFAASID||fixedDurations[index].type==DurationType.TUHREFAASID_MUBTADIA){
        outputStringUrdu =  StringsOfLanguages.URDU.durationTuhrefasid.replace("duration1", "${daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds, isDateOnly)}")
        outputStringEnglish =  StringsOfLanguages.ENGLISH.durationTuhrefasid.replace("duration1", "${daysHoursMinutesDigitalEnglish(fixedDurations[index].timeInMilliseconds, isDateOnly)}")
    }else if (fixedDurations[index].type==DurationType.TUHREFAASID_WITH_ISTEHAZA||
        fixedDurations[index].type==DurationType.TUHREFAASID_MUBTADIA_WITH_ISTEHAZA){
        outputStringUrdu =  StringsOfLanguages.URDU.durationTuhreFasidWithAddition.replace("duration1", "${daysHoursMinutesDigitalUrdu(fixedDurations[index].istihazaAfter, isDateOnly)}").replace("duration2", "${daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds, isDateOnly)}").replace("duration3", "${daysHoursMinutesDigitalUrdu((fixedDurations[index].istihazaAfter+fixedDurations[index].timeInMilliseconds), isDateOnly)}")
        outputStringEnglish =  StringsOfLanguages.ENGLISH.durationTuhreFasidWithAddition.replace("duration1", "${daysHoursMinutesDigitalEnglish(fixedDurations[index].istihazaAfter, isDateOnly)}").replace("duration2", "${daysHoursMinutesDigitalEnglish(fixedDurations[index].timeInMilliseconds, isDateOnly)}").replace("duration3", "${daysHoursMinutesDigitalEnglish((fixedDurations[index].istihazaAfter+fixedDurations[index].timeInMilliseconds), isDateOnly)}")
    }else if (fixedDurations[index].type == DurationType.DAM_IN_NIFAAS_PERIOD){
        val sd = fixedDurations[index].startDate
        val et = fixedDurations[index].endDate
        if(fixedDurations[index].days<=40){
            outputStringUrdu = StringsOfLanguages.URDU.durationNifas.replace("duration1", "${daysHoursMinutesDigitalUrdu((difference(sd,et)), isDateOnly)}")
            outputStringEnglish = StringsOfLanguages.ENGLISH.durationNifas.replace("duration1", "${daysHoursMinutesDigitalEnglish((difference(sd,et)), isDateOnly)}")
        }else{//more than 40
            outputStringUrdu = StringsOfLanguages.URDU.durationDam.replace("duration1", "${daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds,isDateOnly)}")
            outputStringEnglish = StringsOfLanguages.ENGLISH.durationDam.replace("duration1", "${daysHoursMinutesDigitalEnglish(fixedDurations[index].timeInMilliseconds,isDateOnly)}")
        }
    }else if (fixedDurations[index].type == DurationType.TUHR_IN_HAML){

    }else if (fixedDurations[index].type == DurationType.DAM_IN_HAML){

    }else if (fixedDurations[index].type == DurationType.TUHR_BIGGER_THAN_6_MONTHS){
        outputStringUrdu = StringsOfLanguages.URDU.twomonthstuhr.replace("duration1", "${daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds,isDateOnly)}")
        outputStringEnglish = StringsOfLanguages.ENGLISH.twomonthstuhr.replace("duration1", "${daysHoursMinutesDigitalEnglish(fixedDurations[index].timeInMilliseconds,isDateOnly)}")
    }else if(fixedDurations[index].type == DurationType.ISTEHAZA_AYYAMEQABLIYYA){

    }
    return OutputStringsLanguages(outputStringUrdu, outputStringEnglish)
}
fun outputStringHeaderLine(fixedDurations: MutableList<FixedDuration>, index: Int, isDateOnly: Boolean):OutputStringsLanguages{
    var outputStringUrdu = ""
    var outputStringEnglish = ""
    if (fixedDurations[index].type==DurationType.DAM||
        fixedDurations[index].type==DurationType.DAM_MUBTADIA){
        val sd:Date = fixedDurations[index].startDate
        val et = fixedDurations[index].endDate
        if(fixedDurations[index].days in 3.0..10.0){//if it's between 3 and 10, write haiz
            outputStringUrdu = StringsOfLanguages.URDU.haizdays.replace("date1", "${urduDateFormat(sd, isDateOnly)}").replace("date2", "${urduDateFormat(et,isDateOnly)}").replace("duration1", "${daysHoursMinutesDigitalUrdu((difference(sd,et)), isDateOnly)}")
            outputStringEnglish = StringsOfLanguages.ENGLISH.haizdays.replace("date1", "${englishDateFormat(sd, isDateOnly)}").replace("date2", "${englishDateFormat(et,isDateOnly)}").replace("duration1", "${daysHoursMinutesDigitalEnglish((difference(sd,et)), isDateOnly)}")
        }else{//bigger than 10
            if (fixedDurations[index].indices.size>1){//this dam is made up of more than 1
                outputStringUrdu = StringsOfLanguages.URDU.continuosbleeding.replace("date1", "${urduDateFormat(sd, isDateOnly)}").replace("date2", "${urduDateFormat(et, isDateOnly)}").replace("duration1", "${daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds,isDateOnly)}")
                outputStringEnglish = StringsOfLanguages.ENGLISH.continuosbleeding.replace("date1", "${englishDateFormat(sd, isDateOnly)}").replace("date2", "${englishDateFormat(et, isDateOnly)}").replace("duration1", "${daysHoursMinutesDigitalEnglish(fixedDurations[index].timeInMilliseconds,isDateOnly)}")
            }else{
                outputStringUrdu = StringsOfLanguages.URDU.blooddays.replace("date1", "${urduDateFormat(sd, isDateOnly)}").replace("date2", "${urduDateFormat(et, isDateOnly)}").replace("duration1", "${daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds,isDateOnly)}")
                outputStringEnglish = StringsOfLanguages.ENGLISH.blooddays.replace("date1", "${englishDateFormat(sd, isDateOnly)}").replace("date2", "${englishDateFormat(et, isDateOnly)}").replace("duration1", "${daysHoursMinutesDigitalEnglish(fixedDurations[index].timeInMilliseconds,isDateOnly)}")
            }
        }

    }else if (fixedDurations[index].type == DurationType.TUHR||
        fixedDurations[index].type == DurationType.TUHR_MUBTADIA||
        fixedDurations[index].type==DurationType.TUHR_MUBTADIA_BECAME_A_MUTADA_NOW){
        val time = fixedDurations[index].timeInMilliseconds
        outputStringUrdu =  StringsOfLanguages.URDU.pakidays.replace("duration1", "${daysHoursMinutesDigitalUrdu(time, isDateOnly)}")
        outputStringEnglish =  StringsOfLanguages.ENGLISH.pakidays.replace("duration1", "${daysHoursMinutesDigitalEnglish(time, isDateOnly)}")
        if(fixedDurations[index].type == DurationType.TUHR_MUBTADIA_BECAME_A_MUTADA_NOW){
            outputStringUrdu+=StringsOfLanguages.URDU.becamemutadah
            outputStringEnglish+=StringsOfLanguages.ENGLISH.becamemutadah
        }

    }else if (fixedDurations[index].type == DurationType.TUHREFAASID||fixedDurations[index].type==DurationType.TUHREFAASID_MUBTADIA){
        outputStringUrdu =  StringsOfLanguages.URDU.tuhrfasid.replace("duration1", "${daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds, isDateOnly)}")
        outputStringEnglish =  StringsOfLanguages.ENGLISH.tuhrfasid.replace("duration1", "${daysHoursMinutesDigitalEnglish(fixedDurations[index].timeInMilliseconds, isDateOnly)}")
    }else if (fixedDurations[index].type==DurationType.TUHREFAASID_WITH_ISTEHAZA||
        fixedDurations[index].type==DurationType.TUHREFAASID_MUBTADIA_WITH_ISTEHAZA){
        outputStringUrdu =  StringsOfLanguages.URDU.tuhrfasidwithaddition.replace("duration1", "${daysHoursMinutesDigitalUrdu(fixedDurations[index].istihazaAfter, isDateOnly)}").replace("duration2", "${daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds, isDateOnly)}").replace("duration3", "${daysHoursMinutesDigitalUrdu((fixedDurations[index].istihazaAfter+fixedDurations[index].timeInMilliseconds), isDateOnly)}")
        outputStringEnglish =  StringsOfLanguages.ENGLISH.tuhrfasidwithaddition.replace("duration1", "${daysHoursMinutesDigitalEnglish(fixedDurations[index].istihazaAfter, isDateOnly)}").replace("duration2", "${daysHoursMinutesDigitalEnglish(fixedDurations[index].timeInMilliseconds, isDateOnly)}").replace("duration3", "${daysHoursMinutesDigitalEnglish((fixedDurations[index].istihazaAfter+fixedDurations[index].timeInMilliseconds), isDateOnly)}")
    }else if (fixedDurations[index].type == DurationType.DAM_IN_NIFAAS_PERIOD){
        val sd = fixedDurations[index].startDate
        val et = fixedDurations[index].endDate
        if(fixedDurations[index].days<=40){
            outputStringUrdu = StringsOfLanguages.URDU.nifasdays.replace("date1", "${urduDateFormat(sd, isDateOnly)}").replace("date2", "${urduDateFormat(et,isDateOnly)}").replace("duration1", "${daysHoursMinutesDigitalUrdu((difference(sd,et)), isDateOnly)}")
            outputStringEnglish = StringsOfLanguages.ENGLISH.nifasdays.replace("date1", "${englishDateFormat(sd, isDateOnly)}").replace("date2", "${englishDateFormat(et,isDateOnly)}").replace("duration1", "${daysHoursMinutesDigitalEnglish((difference(sd,et)), isDateOnly)}")
        }else{//more than 40
            outputStringUrdu = StringsOfLanguages.URDU.blooddays.replace("date1", "${urduDateFormat(sd, isDateOnly)}").replace("date2", "${urduDateFormat(et, isDateOnly)}").replace("duration1", "${daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds,isDateOnly)}")
            outputStringEnglish = StringsOfLanguages.ENGLISH.blooddays.replace("date1", "${englishDateFormat(sd, isDateOnly)}").replace("date2", "${englishDateFormat(et, isDateOnly)}").replace("duration1", "${daysHoursMinutesDigitalEnglish(fixedDurations[index].timeInMilliseconds,isDateOnly)}")
        }
    }else if (fixedDurations[index].type == DurationType.TUHR_IN_HAML){

    }else if (fixedDurations[index].type == DurationType.DAM_IN_HAML){

    }else if (fixedDurations[index].type == DurationType.TUHR_BIGGER_THAN_6_MONTHS){
        outputStringUrdu = StringsOfLanguages.URDU.twomonthstuhr.replace("duration1", "${daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds,isDateOnly)}")
        outputStringEnglish = StringsOfLanguages.ENGLISH.twomonthstuhr.replace("duration1", "${daysHoursMinutesDigitalEnglish(fixedDurations[index].timeInMilliseconds,isDateOnly)}")
    }else if (fixedDurations[index].type == DurationType.ISTEHAZA_AYYAMEQABLIYYA){
        val sd = fixedDurations[index].startDate
        val et = fixedDurations[index].endDate
        outputStringUrdu = StringsOfLanguages.URDU.daysayyameqabliyya.replace("date1", "${urduDateFormat(sd, isDateOnly)}").replace("date2", "${urduDateFormat(et,isDateOnly)}").replace("duration1", "${daysHoursMinutesDigitalUrdu((difference(sd,et)), isDateOnly)}")
        outputStringEnglish = StringsOfLanguages.ENGLISH.daysayyameqabliyya.replace("date1", "${englishDateFormat(sd, isDateOnly)}").replace("date2", "${englishDateFormat(et,isDateOnly)}").replace("duration1", "${daysHoursMinutesDigitalEnglish((difference(sd,et)), isDateOnly)}")
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
fun generateGetDifferenceString(durationTypes:MutableList<DurationTypes>):String{
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
            str += "${UnicodeChars.YELLOW_CIRCLE} <b><em>From ${englishDateFormat(startTime,true)} to ${englishDateFormat(endTime,true)} is ${type}</em></b>\n\n"
        }else if (durationType.type==DateTypes.YAQEENI_PAKI){
            str += "${UnicodeChars.WHITE_CIRCLE} From ${englishDateFormat(startTime,true)} to ${englishDateFormat(endTime,true)} is ${type}\n\n"
        }else if (durationType.type==DateTypes.YAQEENI_NA_PAKI){
            str += "${UnicodeChars.RED_CIRCLE} From ${englishDateFormat(startTime,true)} to ${englishDateFormat(endTime,true)} is ${type}\n\n"
        }else if (durationType.type==DateTypes.AYYAAM_E_SHAKK_KHUROOJ){
            str += "${UnicodeChars.GREEN_CIRCLE} <b><em>From ${englishDateFormat(startTime,true)} to ${englishDateFormat(endTime,true)} is ${type}</em></b>\n\n"
        }

    }
    return str
}




