import kotlin.js.Date
fun generateOutputStringPregnancy(fixedDurations: MutableList<FixedDuration>,
                         isDateOnly:Boolean, pregnancy: Pregnancy, endingOutputValues: EndingOutputValues, isDuration: Boolean):OutputTexts{
    var englishStr = ""
    var urduStr = ""
    val hazDatesList = getHaizDatesList(fixedDurations)
    urduStr+= generateUrduOutputStringPregnancy(fixedDurations,isDateOnly,pregnancy, endingOutputValues, isDuration)
    englishStr+= "\n\n${generateEnglishOutputStringPregnancy(fixedDurations,isDateOnly,pregnancy, endingOutputValues, isDuration)}"

    val hazDatesStr = generateHazDatesStr(hazDatesList,isDateOnly)

    return OutputTexts(englishStr,urduStr, "",hazDatesList,endingOutputValues, fixedDurations)
}
fun generateOutputStringMubtadia(fixedDurations: MutableList<FixedDuration>,durations: List<Duration>,
                         isDateOnly:Boolean, endingOutputValues: EndingOutputValues, isDuration: Boolean):OutputTexts{
    var index = 0
    var englishStr = ""
    val hazDatesList = getHaizDatesList(fixedDurations)
    val urduStr = generateUrduOutputString(fixedDurations, isDateOnly, endingOutputValues, isDuration)
    englishStr += "\n\n${generateEnglishOutputString(fixedDurations, isDateOnly, endingOutputValues, isDuration)}"

    val hazDatesStr = generateHazDatesStr(hazDatesList,isDateOnly)

    return OutputTexts(englishStr,urduStr, hazDatesStr, hazDatesList,endingOutputValues, fixedDurations)
}
fun generateOutputStringMutadah(fixedDurations: MutableList<FixedDuration>,durations: List<Duration>,
                         isDateOnly:Boolean, endingOutputValues: EndingOutputValues, isDuration: Boolean):OutputTexts{
    var index = 0
    var englishStr = ""
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

    val urduStr = generateUrduOutputString(fixedDurations, isDateOnly, endingOutputValues, isDuration)
    englishStr += "\n\n${generateEnglishOutputString(fixedDurations, isDateOnly, endingOutputValues, isDuration)}"

    val hazDatesStr = generateHazDatesStr(hazDatesList,isDateOnly)

    return OutputTexts(englishStr,urduStr, hazDatesStr, hazDatesList,endingOutputValues, fixedDurations)
}

fun generateHazDatesStr(hazDatesList: MutableList<Entry>,isDateOnly: Boolean):String{
    var str = ""
    for(entry in hazDatesList){
        str+="From ${parseDate(entry.startTime,isDateOnly)} to ${parseDate(entry.endTime,isDateOnly)}<br>"
    }
    return str
}

fun generateUrduOutputStringPregnancy(fixedDurations: MutableList<FixedDuration>, isDateOnly: Boolean, pregnancy: Pregnancy, endingOutputValues: EndingOutputValues, isDuration: Boolean):String{
    val mustabeen = pregnancy.mustabeenUlKhilqat
//    var startTimeOfPregnancy = pregnancy.pregStartTime.getTime()
    val birthTime = pregnancy.birthTime
    var str = StringsOfLanguages.URDU.answer

    if(mustabeen){
        if(fixedDurations[0].type!=DurationType.HAML){
            str += StringsOfLanguages.URDU.beforepregheader
        }
        for(index in fixedDurations.indices){
            if(isDuration){
                str += outputStringUrduHeaderLineDuration(fixedDurations,index, isDateOnly)
                str += outputStringUrduBiggerThan10HallDurations(fixedDurations,index, isDateOnly)
                str += outputStringUrduBiggerThan40HallDuration(fixedDurations,index, isDateOnly)
                if(fixedDurations[index].type==DurationType.HAML){
                    str += StringsOfLanguages.URDU.pregduration
                }
                if(fixedDurations[index].type==DurationType.WILADAT_ISQAT){
                    str += StringsOfLanguages.URDU.birthduration
                }

            }else{
                str += outputStringUrduHeaderLine(fixedDurations,index, isDateOnly)
                str += outputStringUrduBiggerThan10Hall(fixedDurations,index, isDateOnly)
                str += outputStringUrduBiggerThan40Hall(fixedDurations,index, isDateOnly)
                if(fixedDurations[index].type==DurationType.HAML){
                    str += StringsOfLanguages.URDU.preg
                }
                if(fixedDurations[index].type==DurationType.WILADAT_ISQAT){
                    str += StringsOfLanguages.URDU.birth.replace("date1", "${urduDateFormat(birthTime, isDateOnly)}")
                    if(index<fixedDurations.size-2){//if there is something after wiladat
                        str += StringsOfLanguages.URDU.afterpregheader
                    }
                }

            }

        }
        str += outputStringUrduFinalLines(isDateOnly, endingOutputValues, isDuration)



    }else{//if it is ghair mustabeenulkhilqat
        str += StringsOfLanguages.URDU.headerline

        for(index in fixedDurations.indices){
            str += outputStringUrduHeaderLine(fixedDurations,index, isDateOnly)
            str += outputStringUrduBiggerThan10Hall(fixedDurations,index, isDateOnly)
            if(fixedDurations[index].type==DurationType.HAML){
                str += StringsOfLanguages.URDU.preg
            }
            if(fixedDurations[index].type==DurationType.WILADAT_ISQAT){
                str += StringsOfLanguages.URDU.earlymiscarriage.replace("date1", "${urduDateFormat(birthTime, isDateOnly)}")
                if(index<fixedDurations.size-2){//if there is something after wiladat
                    str += StringsOfLanguages.URDU.afterpregheader
                }
            }

        }
        str += outputStringUrduFinalLines (isDateOnly, endingOutputValues, isDuration)

    }


    return str
}

fun generateUrduOutputString(fixedDurations: MutableList<FixedDuration>, isDateOnly: Boolean, endingOutputValues: EndingOutputValues, isDuration:Boolean):String{
    //ToDo: figure out how to do languages for real
    var str = StringsOfLanguages.URDU.answer //جواب:
    str += StringsOfLanguages.URDU.headerline //اس ترتیب سے خون آیا اور پاکی ملی
    var index = 0
    while (index<fixedDurations.size){
        if(isDuration){
            str += outputStringUrduHeaderLineDuration(fixedDurations,index, isDateOnly) //اس تاریخ سے اس تاریخ تک اتنے دن حیض
            str += outputStringUrduBiggerThan10HallDurations(fixedDurations,index, isDateOnly)
            if(index==fixedDurations.size-1){//if this os the last index
                str += outputStringUrduFinalLines(isDateOnly, endingOutputValues, isDuration)
            }
        }else{
            str += outputStringUrduHeaderLine(fixedDurations,index, isDateOnly) //اس تاریخ سے اس تاریخ تک اتنے دن حیض
            str += outputStringUrduBiggerThan10Hall(fixedDurations,index, isDateOnly)
            if(index==fixedDurations.size-1){//if this os the last index
                str += outputStringUrduFinalLines(isDateOnly, endingOutputValues, isDuration)
            }
        }
        index++
    }
    return str
}
fun generateUrduOutputStringMubtadia(fixedDurations: MutableList<FixedDuration>, isDateOnly: Boolean, endingOutputValues: EndingOutputValues, isDuration: Boolean):String{
    //ToDo: figure out how to do languages for real
    var str = StringsOfLanguages.URDU.answer //جواب:
    str += StringsOfLanguages.URDU.headerlinemubtadia //اس ترتیب سے خون آیا اور پاکی ملی
    var index = 0
    while (index<fixedDurations.size){
        str += outputStringUrduHeaderLine(fixedDurations,index, isDateOnly)
        str += outputStringUrduBiggerThan10Hall(fixedDurations,index, isDateOnly)
        if(index==fixedDurations.size-1){//if this os the last index
            str += outputStringUrduFinalLines(isDateOnly, endingOutputValues, isDuration)
        }
        index++
    }
    return str
}

fun outputStringUrduFinalLines(isDateOnly: Boolean, endingOutputValues: EndingOutputValues, isDuration: Boolean):String{

    var strUrdu = ""
    val aadats = endingOutputValues.aadats
    strUrdu+=outputStringUrduAadatLine(isDateOnly, aadats)
    if(isDuration){return strUrdu}
    val filHaal = endingOutputValues.filHaalPaki
    strUrdu+=outputStringUrduFilHaalLine(filHaal)
    val futureDates = endingOutputValues.futureDateType
    strUrdu+=outputStringUrduAskAgainLine(isDateOnly, futureDates)

    //plis note down line
    strUrdu+=StringsOfLanguages.URDU.writedown

    //Allahu Aaalam line
    strUrdu+=StringsOfLanguages.URDU.allahknows

    return strUrdu
}
fun outputStringUrduFilHaalLine(filHaalPaki:Boolean):String{
    val filHaalPakiStr = StringsOfLanguages.URDU.currentpaki
    val filHaalHaizStr = StringsOfLanguages.URDU.currenthaiz
    return if(filHaalPaki){
        filHaalPakiStr
    }else{
        filHaalHaizStr
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

fun outputStringUrduAskAgainLine(isDateOnly: Boolean, futureDateType: FutureDateType?):String{
    var strUrdu = ""
    if (futureDateType==null){
        return ""
    }
    val futureDate= futureDateType.date
    val futureDatesType = futureDateType.futureDates
    if(futureDatesType==TypesOfFutureDates.A3_CHANGING_TO_A2){
        strUrdu += StringsOfLanguages.URDU.situationmaychange.replace("date1", "${urduDateFormat(futureDate, isDateOnly)}")
    }else if(futureDatesType==TypesOfFutureDates.END_OF_AADAT_HAIZ){
        strUrdu += StringsOfLanguages.URDU.haizend.replace("date1", "${urduDateFormat(futureDate, isDateOnly)}")
        strUrdu += StringsOfLanguages.URDU.ihtiyatighusl.replace("date1", "${urduDateFormat(futureDate, isDateOnly)}")
        //sex line
        strUrdu += StringsOfLanguages.URDU.sexnotallowed.replace("date1", "${urduDateFormat(futureDate, isDateOnly)}")
    }else if(futureDatesType==TypesOfFutureDates.END_OF_AADAT_TUHR){
        strUrdu += StringsOfLanguages.URDU.endofpaki.replace("date1", "${urduDateFormat(futureDate, isDateOnly)}")

    }
    return strUrdu

//    //my understanding is, that ask again line only gets generated if the fil haal is istihazaAfter
//    var istihazaAfter = fixedDurations[index].biggerThanTen?.istihazaAfter ?: return ""
//    var aadatHaiz = fixedDurations[index].biggerThanTen?.aadatHaiz ?: return ""
//    var aadatTuhr = fixedDurations[index].biggerThanTen?.aadatTuhr ?: return ""
//
//
//    if(istihazaAfter!=0L){//if there is an istihaza after
//        var endDateOfBleeding = fixedDurations[index].startDate?.let { addTimeToDate(it, fixedDurations[index].timeInMilliseconds) }
//        var askAgainDate:Date? = null
//        if(istihazaAfter>=aadatTuhr+3){//if istihazaAfter is long
//            //find remainder
//            var remainder = istihazaAfter%(aadatHaiz+aadatTuhr)
//            if (remainder < aadatTuhr+3){//it ended in istihaza
//                var startTimeOfIstihaza = endDateOfBleeding?.let { addTimeToDate(it, -remainder) }
//                askAgainDate = startTimeOfIstihaza?.let { addTimeToDate(it, aadatTuhr) }!!
//            }else{//it ended in haiz
//
//            }
//
//        }else{//short istihazaAfter
//            if(fixedDurations[index].biggerThanTen?.qism==Soortain.A_3){
//                //this can change to A2. gotta figure out when. set ask again to then.
//            }else{
//                var endDateOfHaiz = endDateOfBleeding?.let { addTimeToDate(it, -(istihazaAfter)) }
//                askAgainDate = endDateOfHaiz?.let { addTimeToDate(it, (aadatTuhr)) }!!
//            }
//        }
//        if(askAgainDate!=null){
//            strUrdu = "اگر خون اسی طرح جاری رہے یا فی الحال بند ہوجائے لیکن پندرہ دن کی کامل پاکی نہیں ملی کہ دوبارہ خون یا دھبہ آگیا تب پھر<b> ${urduDateFormat(askAgainDate, isDateOnly)} تک آپ کے یقینی پاکی کے دن ہونگے۔</b>\n\n"
//        }
//    }
//
//    return strUrdu
}
fun outputStringUrduAadatLine(isDateOnly: Boolean, aadats:AadatsOfHaizAndTuhr?):String{
    var strUrdu = ""

    return if(aadats==null){
        ""
    }else{
        val aadatTuhr = aadats.aadatTuhr
        val aadatHaiz = aadats.aadatHaiz
        if(aadatHaiz==-1L && aadatTuhr==-1L){
            strUrdu+= StringsOfLanguages.URDU.thereisnoaadat
        }else if(aadatHaiz!=-1L && aadatTuhr==-1L){
            println("aadat of haiz is ${daysHoursMinutesDigital(aadatHaiz,isDateOnly)}")
            strUrdu+= StringsOfLanguages.URDU.aadatofhaizonly.replace("duration1", "${daysHoursMinutesDigitalUrdu(aadatHaiz, isDateOnly)}")
        }else{
            strUrdu+= StringsOfLanguages.URDU.habit.replace("duration1", "${daysHoursMinutesDigitalUrdu(aadatHaiz, isDateOnly)}").replace("duration2", "${daysHoursMinutesDigitalUrdu(aadatTuhr, isDateOnly)}")
        }
        strUrdu
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
fun outputStringUrduBiggerThan10HallDurations(fixedDurations: MutableList<FixedDuration>,index: Int, isDateOnly: Boolean):String{
    var strUrdu = ""

    if((fixedDurations[index].days>10 &&
                (fixedDurations[index].type==DurationType.DAM||
                        fixedDurations[index].type==DurationType.DAM_MUBTADIA))){

          strUrdu += TAB

        for(duration in fixedDurations[index].biggerThanTen!!.durationsList){
            if(duration.type == DurationType.ISTIHAZA_BEFORE){
                strUrdu+= StringsOfLanguages.URDU.startingFromIstehaza.replace("duration1", "${daysHoursMinutesDigitalUrdu(duration.timeInMilliseconds, isDateOnly)}")

            }else if(duration.type == DurationType.HAIZ){
                if(duration==fixedDurations[index].biggerThanTen!!.durationsList[0]){
                    strUrdu+= StringsOfLanguages.URDU.startingFromHaiz.replace("duration1", "${daysHoursMinutesDigitalUrdu(duration.timeInMilliseconds, isDateOnly)}")
                }else{
                    strUrdu+= StringsOfLanguages.URDU.followedByHaizAfter.replace("duration1", "${daysHoursMinutesDigitalUrdu(duration.timeInMilliseconds, isDateOnly)}")
                }
            }else if(duration.type == DurationType.ISTIHAZA_AFTER){
                strUrdu+= StringsOfLanguages.URDU.followedByistehazaAfter.replace("duration1", "${daysHoursMinutesDigitalUrdu(duration.timeInMilliseconds, isDateOnly)}")

            }else if(duration.type == DurationType.LESS_THAN_3_HAIZ){
                strUrdu+= StringsOfLanguages.URDU.followedByHaizAfter.replace("duration1", "${daysHoursMinutesDigitalUrdu(duration.timeInMilliseconds, isDateOnly)}")
                //maybe we'll wanna add something about itibaar bil khawateem
            }else if(duration.type == DurationType.NIFAAS){
                strUrdu+= StringsOfLanguages.URDU.startingFromNifas
            }
        }
    }
    if(strUrdu!=""){
        strUrdu+=StringsOfLanguages.URDU.khatimaplusnewline
    }

    return strUrdu
}
fun outputStringEnglishBiggerThan10HallDurations(fixedDurations: MutableList<FixedDuration>,index: Int, isDateOnly: Boolean):String{
    var strEnglish = ""

    if((fixedDurations[index].days>10 &&
                (fixedDurations[index].type==DurationType.DAM||
                        fixedDurations[index].type==DurationType.DAM_MUBTADIA))){

        strEnglish += TAB

        for(duration in fixedDurations[index].biggerThanTen!!.durationsList){
            if(duration.type == DurationType.ISTIHAZA_BEFORE){
                strEnglish+= StringsOfLanguages.ENGLISH.startingFromIstehaza.replace("duration1", "${daysHoursMinutesDigital(duration.timeInMilliseconds, isDateOnly)}")

            }else if(duration.type == DurationType.HAIZ){
                if(duration==fixedDurations[index].biggerThanTen!!.durationsList[0]){
                    strEnglish+= StringsOfLanguages.ENGLISH.startingFromHaiz.replace("duration1", "${daysHoursMinutesDigital(duration.timeInMilliseconds, isDateOnly)}")
                }else{
                    strEnglish+= StringsOfLanguages.ENGLISH.followedByHaizAfter.replace("duration1", "${daysHoursMinutesDigital(duration.timeInMilliseconds, isDateOnly)}")
                }
            }else if(duration.type == DurationType.ISTIHAZA_AFTER){
                strEnglish+= StringsOfLanguages.ENGLISH.followedByistehazaAfter.replace("duration1", "${daysHoursMinutesDigital(duration.timeInMilliseconds, isDateOnly)}")

            }else if(duration.type == DurationType.LESS_THAN_3_HAIZ){
                strEnglish+= StringsOfLanguages.ENGLISH.followedByHaizAfter.replace("duration1", "${daysHoursMinutesDigital(duration.timeInMilliseconds, isDateOnly)}")
                //maybe we'll wanna add something about itibaar bil khawateem
            }else if(duration.type == DurationType.NIFAAS){
                strEnglish+= StringsOfLanguages.ENGLISH.startingFromNifas
            }
        }
    }
    if(strEnglish!=""){
        strEnglish+=StringsOfLanguages.ENGLISH.khatimaplusnewline
    }

    return strEnglish
}

fun outputStringUrduBiggerThan10Hall(fixedDurations: MutableList<FixedDuration>,index: Int, isDateOnly: Boolean):String{
    var strUrdu = ""

    fun haizLineUrdu(sd:Date,ed:Date, isDateOnly: Boolean):String{
        return StringsOfLanguages.URDU.haizdaysinsolution.replace("date1", "${urduDateFormat(sd, isDateOnly)}").replace("date2", "${urduDateFormat(ed,isDateOnly)}").replace("duration1", "${daysHoursMinutesDigitalUrdu((difference(sd,ed)), isDateOnly)}")
    }
    fun istihazaLineUrdu(sd:Date,ed:Date, isDateOnly: Boolean):String{
        return StringsOfLanguages.URDU.istihazadays.replace("date1", "${urduDateFormat(sd, isDateOnly)}").replace("date2", "${urduDateFormat(ed,isDateOnly)}").replace("duration1", "${daysHoursMinutesDigitalUrdu(difference(sd,ed), isDateOnly)}")
    }

    if((fixedDurations[index].days>10 &&
        (fixedDurations[index].type==DurationType.DAM||
        fixedDurations[index].type==DurationType.DAM_MUBTADIA))){

        strUrdu += StringsOfLanguages.URDU.dashesline
        strUrdu += StringsOfLanguages.URDU.solution

        for(duration in fixedDurations[index].biggerThanTen!!.durationsList){
            if(duration.type == DurationType.ISTIHAZA_BEFORE){
                strUrdu+= istihazaLineUrdu(duration.startTime,duration.endDate,isDateOnly)
                strUrdu+= StringsOfLanguages.URDU.istihazadetailslineone

            }else if(duration.type == DurationType.HAIZ){
                strUrdu+= haizLineUrdu(duration.startTime,duration.endDate,isDateOnly)

            }else if(duration.type == DurationType.ISTIHAZA_AFTER){
                strUrdu+= istihazaLineUrdu(duration.startTime,duration.endDate,isDateOnly)
                strUrdu+= StringsOfLanguages.URDU.istihazadetailslinetwo.replace("date1", "${urduDateFormat(duration.startTime,isDateOnly)}")
                strUrdu+= StringsOfLanguages.URDU.istihazadetailslineone

            }else if(duration.type == DurationType.LESS_THAN_3_HAIZ){
                strUrdu+= haizLineUrdu(duration.startTime,duration.endDate,isDateOnly)
                //maybe we'll wanna add something about itibaar bil khawateem
            }
        }

        strUrdu += StringsOfLanguages.URDU.dashesline
    }

    return strUrdu
}
fun outputStringEnglishBiggerThan40HallDuration(fixedDurations: MutableList<FixedDuration>,index: Int, isDateOnly: Boolean):String{

    var strEnglish = ""
    if(fixedDurations[index].days>40&&fixedDurations[index].type==DurationType.DAM_IN_NIFAAS_PERIOD){
        strEnglish+=TAB
        for(duration in fixedDurations[index].biggerThanForty!!.durationsList){
            if(duration.type==DurationType.NIFAAS){
                strEnglish+= StringsOfLanguages.ENGLISH.startingFromNifas.replace("duration1", "${daysHoursMinutesDigital(duration.timeInMilliseconds, isDateOnly)}")
            }else if(duration.type==DurationType.ISTIHAZA_AFTER){
                strEnglish+= StringsOfLanguages.ENGLISH.followedByistehazaAfter.replace("duration1", "${daysHoursMinutesDigital(duration.timeInMilliseconds, isDateOnly)}")
            }else if(duration.type==DurationType.HAIZ){
                strEnglish+= StringsOfLanguages.ENGLISH.followedByHaizAfter.replace("duration1", "${daysHoursMinutesDigital(duration.timeInMilliseconds, isDateOnly)}")
            }else if(duration.type == DurationType.LESS_THAN_3_HAIZ){
                strEnglish+= StringsOfLanguages.ENGLISH.followedByHaizAfter.replace("duration1", "${daysHoursMinutesDigital(duration.timeInMilliseconds, isDateOnly)}")
                //maybe we'll wanna add something about itibaar bil khawateem
            }
        }
    }
    if(strEnglish!=""){
        strEnglish+=StringsOfLanguages.ENGLISH.khatimaplusnewline
    }
    return strEnglish
}
fun outputStringUrduBiggerThan40HallDuration(fixedDurations: MutableList<FixedDuration>,index: Int, isDateOnly: Boolean):String{

    var strUrdu = ""
    if(fixedDurations[index].days>40&&fixedDurations[index].type==DurationType.DAM_IN_NIFAAS_PERIOD){
        strUrdu+=TAB
        for(duration in fixedDurations[index].biggerThanForty!!.durationsList){
            if(duration.type==DurationType.NIFAAS){
                strUrdu+= StringsOfLanguages.URDU.startingFromNifas.replace("duration1", "${daysHoursMinutesDigitalUrdu(duration.timeInMilliseconds, isDateOnly)}")
            }else if(duration.type==DurationType.ISTIHAZA_AFTER){
                strUrdu+= StringsOfLanguages.URDU.followedByistehazaAfter.replace("duration1", "${daysHoursMinutesDigitalUrdu(duration.timeInMilliseconds, isDateOnly)}")
            }else if(duration.type==DurationType.HAIZ){
                strUrdu+= StringsOfLanguages.URDU.followedByHaizAfter.replace("duration1", "${daysHoursMinutesDigitalUrdu(duration.timeInMilliseconds, isDateOnly)}")
            }else if(duration.type == DurationType.LESS_THAN_3_HAIZ){
                strUrdu+= StringsOfLanguages.URDU.followedByHaizAfter.replace("duration1", "${daysHoursMinutesDigitalUrdu(duration.timeInMilliseconds, isDateOnly)}")
                //maybe we'll wanna add something about itibaar bil khawateem
            }
        }
         }
    if(strUrdu!=""){
        strUrdu+=StringsOfLanguages.URDU.khatimaplusnewline
    }
    return strUrdu
}
fun outputStringUrduBiggerThan40Hall(fixedDurations: MutableList<FixedDuration>,index: Int, isDateOnly: Boolean):String{

    var strUrdu = ""

    fun nifasLineUrdu(sd:Date,ed:Date, isDateOnly: Boolean):String{
        return StringsOfLanguages.URDU.nifasdaysinsolution.replace("date1", "${urduDateFormat(sd, isDateOnly)}").replace("date2", "${urduDateFormat(ed,isDateOnly)}").replace("duration1", "${daysHoursMinutesDigitalUrdu((difference(sd,ed)), isDateOnly)}")
    }
    fun haizLineUrdu(sd:Date,ed:Date, isDateOnly: Boolean):String{
        return StringsOfLanguages.URDU.haizdaysinsolution.replace("date1","${urduDateFormat(sd, isDateOnly)}").replace("date2", "${urduDateFormat(ed, isDateOnly)}").replace("duration1", "${daysHoursMinutesDigitalUrdu((difference(sd,ed)), isDateOnly)}")
    }
    fun istihazaLineUrdu(sd:Date,ed:Date, isDateOnly: Boolean):String{
        return StringsOfLanguages.URDU.istihazadays.replace("date1", "${urduDateFormat(sd, isDateOnly)}").replace("date2", "${urduDateFormat(ed,isDateOnly)}").replace("duration1", "${daysHoursMinutesDigitalUrdu(difference(sd,ed), isDateOnly)}")
    }

    if(fixedDurations[index].days>40&&fixedDurations[index].type==DurationType.DAM_IN_NIFAAS_PERIOD){
        strUrdu += StringsOfLanguages.URDU.dashesline
        strUrdu += StringsOfLanguages.URDU.solution

        for(duration in fixedDurations[index].biggerThanForty!!.durationsList){
            if(duration.type==DurationType.NIFAAS){
                strUrdu+= nifasLineUrdu(duration.startTime,duration.endDate, isDateOnly)
            }else if(duration.type==DurationType.ISTIHAZA_AFTER){
                strUrdu+= istihazaLineUrdu(duration.startTime,duration.endDate, isDateOnly)
                strUrdu+= StringsOfLanguages.URDU.istihazadetailslinetwo.replace("date1", "${urduDateFormat(duration.startTime,isDateOnly)}")
                strUrdu+= StringsOfLanguages.URDU.istihazadetailslineone

            }else if(duration.type==DurationType.HAIZ){
                strUrdu+= haizLineUrdu(duration.startTime,duration.endDate, isDateOnly)

            }else if(duration.type == DurationType.LESS_THAN_3_HAIZ){
                strUrdu+= haizLineUrdu(duration.startTime,duration.endDate,isDateOnly)
                //maybe we'll wanna add something about itibaar bil khawateem
            }
        }
        strUrdu += StringsOfLanguages.URDU.dashesline
    }

    return strUrdu
}
fun outputStringUrduHeaderLineDuration(fixedDurations: MutableList<FixedDuration>,index: Int, isDateOnly: Boolean):String{
    //in duration we just give the fixed duration
    var outputString = ""
    if (fixedDurations[index].type==DurationType.DAM||
        fixedDurations[index].type==DurationType.DAM_MUBTADIA){
        val sd:Date = fixedDurations[index].startDate
        val et = fixedDurations[index].endDate
        if(fixedDurations[index].days in 3.0..10.0){//if it's between 3 and 10, write haiz
            outputString = StringsOfLanguages.URDU.durationHaiz.replace("duration1", "${daysHoursMinutesDigitalUrdu((difference(sd,et)), isDateOnly)}")
        }else{//bigger than 10
            outputString = StringsOfLanguages.URDU.durationDam.replace("duration1", "${daysHoursMinutesDigitalUrdu((difference(sd,et)), isDateOnly)}")
        }

    }else if (fixedDurations[index].type == DurationType.TUHR||
        fixedDurations[index].type == DurationType.TUHR_MUBTADIA||
        fixedDurations[index].type==DurationType.TUHR_MUBTADIA_BECAME_A_MUTADA_NOW){
        val time = fixedDurations[index].timeInMilliseconds
        outputString =  StringsOfLanguages.URDU.durationPaki.replace("duration1", "${daysHoursMinutesDigitalUrdu(time, isDateOnly)}")
        if(fixedDurations[index].type == DurationType.TUHR_MUBTADIA_BECAME_A_MUTADA_NOW){
            outputString+=StringsOfLanguages.URDU.becamemutadah
        }

    }else if (fixedDurations[index].type == DurationType.TUHREFAASID||fixedDurations[index].type==DurationType.TUHREFAASID_MUBTADIA){
        outputString =  StringsOfLanguages.URDU.durationTuhrefasid.replace("duration1", "${daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds, isDateOnly)}")
    }else if (fixedDurations[index].type==DurationType.TUHREFAASID_WITH_ISTEHAZA||
        fixedDurations[index].type==DurationType.TUHREFAASID_MUBTADIA_WITH_ISTEHAZA){
        outputString =  StringsOfLanguages.URDU.durationTuhreFasidWithAddition.replace("duration1", "${daysHoursMinutesDigitalUrdu(fixedDurations[index].istihazaAfter, isDateOnly)}").replace("duration2", "${daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds, isDateOnly)}").replace("duration3", "${daysHoursMinutesDigitalUrdu((fixedDurations[index].istihazaAfter+fixedDurations[index].timeInMilliseconds), isDateOnly)}")
    }else if (fixedDurations[index].type == DurationType.DAM_IN_NIFAAS_PERIOD){
        val sd = fixedDurations[index].startDate
        val et = fixedDurations[index].endDate
        if(fixedDurations[index].days<=40){
            outputString = StringsOfLanguages.URDU.durationNifas.replace("duration1", "${daysHoursMinutesDigitalUrdu((difference(sd,et)), isDateOnly)}")
        }else{//more than 40
            outputString = StringsOfLanguages.URDU.durationDam.replace("duration1", "${daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds,isDateOnly)}")
        }
    }else if (fixedDurations[index].type == DurationType.TUHR_IN_HAML){

    }else if (fixedDurations[index].type == DurationType.DAM_IN_HAML){

    }else if (fixedDurations[index].type == DurationType.TUHR_BIGGER_THAN_6_MONTHS){
        outputString = StringsOfLanguages.URDU.twomonthstuhr.replace("duration1", "${daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds,isDateOnly)}")
    }
    return outputString
}
fun outputStringEnglishHeaderLineDuration(fixedDurations: MutableList<FixedDuration>,index: Int, isDateOnly: Boolean):String{
    //in duration we just give the fixed duration
    var outputString = ""
    if (fixedDurations[index].type==DurationType.DAM||
        fixedDurations[index].type==DurationType.DAM_MUBTADIA){
        val sd:Date = fixedDurations[index].startDate
        val et = fixedDurations[index].endDate
        if(fixedDurations[index].days in 3.0..10.0){//if it's between 3 and 10, write haiz
            outputString = StringsOfLanguages.ENGLISH.durationHaiz.replace("duration1", "${daysHoursMinutesDigital((difference(sd,et)), isDateOnly)}")
        }else{//bigger than 10
            outputString = StringsOfLanguages.ENGLISH.durationDam.replace("duration1", "${daysHoursMinutesDigital((difference(sd,et)), isDateOnly)}")
        }

    }else if (fixedDurations[index].type == DurationType.TUHR||
        fixedDurations[index].type == DurationType.TUHR_MUBTADIA||
        fixedDurations[index].type==DurationType.TUHR_MUBTADIA_BECAME_A_MUTADA_NOW){
        val time = fixedDurations[index].timeInMilliseconds
        outputString =  StringsOfLanguages.ENGLISH.durationPaki.replace("duration1", "${daysHoursMinutesDigital(time, isDateOnly)}")
        if(fixedDurations[index].type == DurationType.TUHR_MUBTADIA_BECAME_A_MUTADA_NOW){
            outputString+=StringsOfLanguages.ENGLISH.becamemutadah
        }

    }else if (fixedDurations[index].type == DurationType.TUHREFAASID||fixedDurations[index].type==DurationType.TUHREFAASID_MUBTADIA){
        outputString =  StringsOfLanguages.ENGLISH.durationTuhrefasid.replace("duration1", "${daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds, isDateOnly)}")
    }else if (fixedDurations[index].type==DurationType.TUHREFAASID_WITH_ISTEHAZA||
        fixedDurations[index].type==DurationType.TUHREFAASID_MUBTADIA_WITH_ISTEHAZA){
        outputString =  StringsOfLanguages.ENGLISH.durationTuhreFasidWithAddition.replace("duration1", "${daysHoursMinutesDigital(fixedDurations[index].istihazaAfter, isDateOnly)}").replace("duration2", "${daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds, isDateOnly)}").replace("duration3", "${daysHoursMinutesDigital((fixedDurations[index].istihazaAfter+fixedDurations[index].timeInMilliseconds), isDateOnly)}")
    }else if (fixedDurations[index].type == DurationType.DAM_IN_NIFAAS_PERIOD){
        val sd = fixedDurations[index].startDate
        val et = fixedDurations[index].endDate
        if(fixedDurations[index].days<=40){
            outputString = StringsOfLanguages.ENGLISH.durationNifas.replace("duration1", "${daysHoursMinutesDigital((difference(sd,et)), isDateOnly)}")
        }else{//more than 40
            outputString = StringsOfLanguages.ENGLISH.durationDam.replace("duration1", "${daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds,isDateOnly)}")
        }
    }else if (fixedDurations[index].type == DurationType.TUHR_IN_HAML){

    }else if (fixedDurations[index].type == DurationType.DAM_IN_HAML){

    }else if (fixedDurations[index].type == DurationType.TUHR_BIGGER_THAN_6_MONTHS){
        outputString = StringsOfLanguages.ENGLISH.twomonthstuhr.replace("duration1", "${daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds,isDateOnly)}")
    }
    return outputString
}
fun outputStringUrduHeaderLine(fixedDurations: MutableList<FixedDuration>,index: Int, isDateOnly: Boolean):String{
    var outputString = ""
    if (fixedDurations[index].type==DurationType.DAM||
        fixedDurations[index].type==DurationType.DAM_MUBTADIA){
        val sd:Date = fixedDurations[index].startDate
        val et = fixedDurations[index].endDate
        if(fixedDurations[index].days in 3.0..10.0){//if it's between 3 and 10, write haiz
            outputString = StringsOfLanguages.URDU.haizdays.replace("date1", "${urduDateFormat(sd, isDateOnly)}").replace("date2", "${urduDateFormat(et,isDateOnly)}").replace("duration1", "${daysHoursMinutesDigitalUrdu((difference(sd,et)), isDateOnly)}")
        }else{//bigger than 10
            if (fixedDurations[index].indices.size>1){//this dam is made up of more than 1
                outputString = StringsOfLanguages.URDU.continuosbleeding.replace("date1", "${urduDateFormat(sd, isDateOnly)}").replace("date2", "${urduDateFormat(et, isDateOnly)}").replace("duration1", "${daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds,isDateOnly)}")
            }else{
                outputString = StringsOfLanguages.URDU.blooddays.replace("date1", "${urduDateFormat(sd, isDateOnly)}").replace("date2", "${urduDateFormat(et, isDateOnly)}").replace("duration1", "${daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds,isDateOnly)}")
            }
        }

    }else if (fixedDurations[index].type == DurationType.TUHR||
        fixedDurations[index].type == DurationType.TUHR_MUBTADIA||
        fixedDurations[index].type==DurationType.TUHR_MUBTADIA_BECAME_A_MUTADA_NOW){
        val time = fixedDurations[index].timeInMilliseconds
        outputString =  StringsOfLanguages.URDU.pakidays.replace("duration1", "${daysHoursMinutesDigitalUrdu(time, isDateOnly)}")
        if(fixedDurations[index].type == DurationType.TUHR_MUBTADIA_BECAME_A_MUTADA_NOW){
            outputString+=StringsOfLanguages.URDU.becamemutadah
        }

    }else if (fixedDurations[index].type == DurationType.TUHREFAASID||fixedDurations[index].type==DurationType.TUHREFAASID_MUBTADIA){
        outputString =  StringsOfLanguages.URDU.tuhrfasid.replace("duration1", "${daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds, isDateOnly)}")
    }else if (fixedDurations[index].type==DurationType.TUHREFAASID_WITH_ISTEHAZA||
        fixedDurations[index].type==DurationType.TUHREFAASID_MUBTADIA_WITH_ISTEHAZA){
        outputString =  StringsOfLanguages.URDU.tuhrfasidwithaddition.replace("duration1", "${daysHoursMinutesDigitalUrdu(fixedDurations[index].istihazaAfter, isDateOnly)}").replace("duration2", "${daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds, isDateOnly)}").replace("duration3", "${daysHoursMinutesDigitalUrdu((fixedDurations[index].istihazaAfter+fixedDurations[index].timeInMilliseconds), isDateOnly)}")
    }else if (fixedDurations[index].type == DurationType.DAM_IN_NIFAAS_PERIOD){
        val sd = fixedDurations[index].startDate
        val et = fixedDurations[index].endDate
        if(fixedDurations[index].days<=40){
            outputString = StringsOfLanguages.URDU.nifasdays.replace("date1", "${urduDateFormat(sd, isDateOnly)}").replace("date2", "${urduDateFormat(et,isDateOnly)}").replace("duration1", "${daysHoursMinutesDigitalUrdu((difference(sd,et)), isDateOnly)}")
        }else{//more than 40
            outputString = StringsOfLanguages.URDU.blooddays.replace("date1", "${urduDateFormat(sd, isDateOnly)}").replace("date2", "${urduDateFormat(et, isDateOnly)}").replace("duration1", "${daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds,isDateOnly)}")
        }
    }else if (fixedDurations[index].type == DurationType.TUHR_IN_HAML){

    }else if (fixedDurations[index].type == DurationType.DAM_IN_HAML){

    }else if (fixedDurations[index].type == DurationType.TUHR_BIGGER_THAN_6_MONTHS){
        outputString = StringsOfLanguages.URDU.twomonthstuhr.replace("duration1", "${daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds,isDateOnly)}")
    }
    return outputString
}

fun outputStringHeaderLine(fixedDurations: MutableList<FixedDuration>, index:Int, isDateOnly: Boolean):String{
    if((index +1)< fixedDurations.size && fixedDurations[index+1].istihazaAfter>0){
        return "<b>${daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds,isDateOnly)} ${fixedDurations[index].type}</b>\n"
    }else{
        return "<b>${daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds,isDateOnly)} ${fixedDurations[index].type}</b>\n"

    }
}

fun outputStringSumOfIndicesLine(fixedDurations: MutableList<FixedDuration>, durations:List<Duration>, index:Int, isDateOnly: Boolean):String{
    if(fixedDurations[index].indices.size>1){
        var sum = 0L
        var str = ""
        for (index in fixedDurations[index].indices){
            sum+=durations[index].timeInMilliseconds
            str += " + ${daysHoursMinutesDigital(durations[index].timeInMilliseconds,isDateOnly)}"
        }
        str=str.removePrefix(" + ")
        return "\t${str} = ${daysHoursMinutesDigital(sum,isDateOnly)}\n"
    }else{
        return ""
    }
}

fun outputStringIstihazaAfterLine(fixedDurations: MutableList<FixedDuration>,index: Int, isDateOnly: Boolean):String{
    val istihazaAfter = fixedDurations[index].istihazaAfter
    var str = ""
    if(istihazaAfter!=0L){
        str +="\t${daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds,isDateOnly)} " +
                "tuhr + ${daysHoursMinutesDigital(istihazaAfter,isDateOnly)} istihaza " +
                "= ${daysHoursMinutesDigital((fixedDurations[index].timeInMilliseconds +
                        fixedDurations[index].istihazaAfter),isDateOnly)} tuhr-e-faasid\n"
    }

    return str
}

fun outputStringBiggerThan10Hall(fixedDurations: MutableList<FixedDuration>,index:Int, isDateOnly: Boolean):String{
    val mp = fixedDurations[index].biggerThanTen?.mp ?: return ""
    val gp = fixedDurations[index].biggerThanTen?.gp ?: return ""
    val dm = fixedDurations[index].biggerThanTen?.dm ?: return ""
    val hz = fixedDurations[index].biggerThanTen?.hz ?: return ""
    val qism = fixedDurations[index].biggerThanTen?.qism ?: return ""
    val istihazaBefore = fixedDurations[index].biggerThanTen?.istihazaBefore ?: return ""
    val haiz = fixedDurations[index].biggerThanTen?.haiz ?: return ""
    val istihazaAfter = fixedDurations[index].biggerThanTen?.istihazaAfter ?: return ""
    val aadatTuhr = fixedDurations[index].biggerThanTen?.aadatTuhr ?: return ""
    val aadatHaz = fixedDurations[index].biggerThanTen?.aadatHaiz ?: return ""


    //output hukm:
    var str =""
//    var str = "Rough work \n"
//    str += "MP\tGP\tDm\tHz\tQism\n"

    str += "\t${daysHoursMinutesDigital(mp,isDateOnly)}\t${daysHoursMinutesDigital(gp,isDateOnly)}\t" +
            "${daysHoursMinutesDigital(dm,isDateOnly)}\t${daysHoursMinutesDigital(hz,isDateOnly)}\t${qism}\n"

    str +="\tAadat: ${daysHoursMinutesDigital(aadatHaz,isDateOnly)}/${daysHoursMinutesDigital(aadatTuhr,isDateOnly)}\n"

    str += "\tOut of ${daysHoursMinutesDigital(dm,isDateOnly)}, the first "

    if (istihazaBefore>0){
        str += "${daysHoursMinutesDigital(istihazaBefore,isDateOnly)} are istihaza, then the next "
    }
    str += "${daysHoursMinutesDigital(haiz,isDateOnly)} are haiz, "

    //if istihazaAfter is bigger than addatTuhr +3, run daur
    if (istihazaAfter>=aadatTuhr+3){
        //find quotient and remainder
        val remainder = istihazaAfter%(aadatHaz+aadatTuhr)
        val quotient = ((istihazaAfter-remainder)/(aadatHaz+aadatTuhr))

        if(remainder == 0L){
            for (j in 1 until quotient){
                str+="then the next ${daysHoursMinutesDigital(aadatTuhr,isDateOnly)} are istihaza, " +
                        "then the next ${daysHoursMinutesDigital(aadatHaz,isDateOnly)} are haiz, "
            }
            str+="then the next ${daysHoursMinutesDigital(aadatTuhr,isDateOnly)} are istihaza, " +
                    "then the last ${daysHoursMinutesDigital(aadatHaz,isDateOnly)} are haiz. "

        }else{//remainder exists
            for (j in 1 .. quotient){
                str+="then the next ${daysHoursMinutesDigital(aadatTuhr,isDateOnly)} are istihaza, " +
                        "then the next ${daysHoursMinutesDigital(aadatHaz,isDateOnly)} are haiz, "
            }
            if (remainder<aadatTuhr + 3){//it ended in tuhr
                str+="then the last ${daysHoursMinutesDigital(remainder,isDateOnly)} are istihaza.\n"

            }else{//it ended in haiz
                str+="then the next ${daysHoursMinutesDigital(aadatTuhr,isDateOnly)} are tuhr, " +
                        "then the last ${daysHoursMinutesDigital(remainder-aadatTuhr,isDateOnly)} are haiz\n"
                //change aadatHaiz
                val newAadatHaz = remainder-aadatTuhr
                //add aadat line
                str+="\tAadat: ${daysHoursMinutesDigital(newAadatHaz,isDateOnly)}/${daysHoursMinutesDigital(aadatTuhr,isDateOnly)}\n"

            }
        }
    }else{
        str += "and the last ${daysHoursMinutesDigital(istihazaAfter,isDateOnly)} are istihaza.\n"

    }

    str+="\t\n"

    //output hukm in dates
    val istihazaBeforeStartDate:Date = fixedDurations[index].startDate
    val haizStartDate = addTimeToDate(istihazaBeforeStartDate, istihazaBefore)
    val istihazaAfterStartDate = addTimeToDate(haizStartDate, haiz)
    val istihazaAfterEndDate = addTimeToDate(istihazaAfterStartDate, istihazaAfter)

    if(istihazaBefore!=0L){
        str+="\tFrom ${parseDate(istihazaBeforeStartDate, isDateOnly)} to ${parseDate(haizStartDate, isDateOnly)} is istihaza, yaqeeni paki\n"
    }
    str+="\tFrom ${parseDate(haizStartDate, isDateOnly)} to ${parseDate(istihazaAfterStartDate, isDateOnly)} is haiz\n"
    if(istihazaAfter!=0L){
        if (istihazaAfter>=aadatTuhr+3){
            //find quotient and remainder
            val remainder = istihazaAfter%(aadatHaz+aadatTuhr)
            val quotient = ((istihazaAfter-remainder)/(aadatHaz+aadatTuhr))


            var aadatTuhrStartDate:Date = istihazaAfterStartDate
            var aadatTuhrEndDate:Date
            var aadatHaizEndDate:Date
            for (j in 1 .. quotient){
                aadatTuhrEndDate = addTimeToDate(aadatTuhrStartDate,aadatTuhr)
                aadatHaizEndDate = addTimeToDate(aadatTuhrEndDate,aadatHaz)
                str+= "\tFrom ${parseDate(aadatTuhrStartDate, isDateOnly)} to ${parseDate(aadatTuhrEndDate, isDateOnly)} is istihaza, yaqeeni paki\n"
                str+= "\tFrom ${parseDate(aadatTuhrEndDate, isDateOnly)} to ${parseDate(aadatHaizEndDate, isDateOnly)} is haiz\n"
                aadatTuhrStartDate=aadatHaizEndDate
            }
            if (remainder<aadatTuhr + 3 && remainder!=0L){//it ended in tuhr
                str+= "\tFrom ${parseDate(aadatTuhrStartDate, isDateOnly)} to ${parseDate(istihazaAfterEndDate, isDateOnly)} is istihaza, yaqeeni paki\n"

            }else{//it ended in haiz or remainder is 0
                aadatTuhrEndDate = addTimeToDate(aadatTuhrStartDate,aadatTuhr)
                str+= "\tFrom ${parseDate(aadatTuhrStartDate, isDateOnly)} to ${parseDate(aadatTuhrEndDate, isDateOnly)} is istihaza, yaqeeni paki\n"
                str+= "\tFrom ${parseDate(aadatTuhrEndDate, isDateOnly)} to ${parseDate(istihazaAfterEndDate, isDateOnly)} is haiz\n"

                //change aadatHaiz if remainder is not zero (if it is zero, aadat doesn't change, so shouldn't be printed
                if (remainder!=0L){
                    val newAadatHaz1 = remainder-aadatTuhr
                    //add aadat line
                    str+="\tAadat: ${daysHoursMinutesDigital(newAadatHaz1,isDateOnly)}/${daysHoursMinutesDigital(aadatTuhr,isDateOnly)}\n"
                }
           }

        }else{//no duar
            str+="\tFrom ${parseDate(istihazaAfterStartDate, isDateOnly)} to ${parseDate(istihazaAfterEndDate, isDateOnly)} is istihaza, yaqeeni paki\n"

        }
    }
   return str
}
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
            str += "${UnicodeChars.YELLOW_CIRCLE} <b><em>From ${parseDate(startTime,true)} to ${parseDate(endTime,true)} is ${type}</em></b>\n\n"
        }else if (durationType.type==DateTypes.YAQEENI_PAKI){
            str += "${UnicodeChars.WHITE_CIRCLE} From ${parseDate(startTime,true)} to ${parseDate(endTime,true)} is ${type}\n\n"
        }else if (durationType.type==DateTypes.YAQEENI_NA_PAKI){
            str += "${UnicodeChars.RED_CIRCLE} From ${parseDate(startTime,true)} to ${parseDate(endTime,true)} is ${type}\n\n"
        }else if (durationType.type==DateTypes.AYYAAM_E_SHAKK_KHUROOJ){
            str += "${UnicodeChars.GREEN_CIRCLE} <b><em>From ${parseDate(startTime,true)} to ${parseDate(endTime,true)} is ${type}</em></b>\n\n"
        }

    }
    return str
}




fun generateEnglishOutputString(fixedDurations: MutableList<FixedDuration>, isDateOnly: Boolean, endingOutputValues: EndingOutputValues, isDuration: Boolean):String{
    //ToDo: figure out how to do languages for real
    var str = StringsOfLanguages.ENGLISH.answer
    str += StringsOfLanguages.ENGLISH.headerline
    var index = 0
    while (index<fixedDurations.size){
        if(isDuration){
            str += outputStringEnglishHeaderLineDuration(fixedDurations,index, isDateOnly)
            str += outputStringEnglishBiggerThan10HallDurations(fixedDurations,index, isDateOnly)
            if(index==fixedDurations.size-1){//if this os the last index
                str += outputStringEnglishFinalLines(isDateOnly, endingOutputValues, isDuration)
            }
        }else{
            str += outputStringEnglishHeaderLine(fixedDurations,index, isDateOnly)
            str += outputStringEnglishBiggerThan10Hall(fixedDurations,index, isDateOnly)
            if(index==fixedDurations.size-1){//if this os the last index
                str += outputStringEnglishFinalLines(isDateOnly, endingOutputValues, isDuration)
            }
        }
        index++
    }
    return str
}

fun outputStringEnglishHeaderLine(fixedDurations: MutableList<FixedDuration>,index: Int, isDateOnly: Boolean):String{
    var outputString = ""
    if (fixedDurations[index].type==DurationType.DAM||
        fixedDurations[index].type==DurationType.DAM_MUBTADIA){
        val sd:Date = fixedDurations[index].startDate
        val et = fixedDurations[index].endDate
        if(fixedDurations[index].days in 3.0..10.0){//if it's between 3 and 10, write haiz
            outputString = StringsOfLanguages.ENGLISH.haizdays.replace("date1", "${parseDate(sd, isDateOnly)}").replace("date2", "${parseDate(et,isDateOnly)}").replace("duration1", "${daysHoursMinutesDigital((difference(sd,et)), isDateOnly)}")
        }else{//bigger than 10
            if (fixedDurations[index].indices.size>1){//this dam is made up of more than 1
                outputString = StringsOfLanguages.ENGLISH.continuosbleeding.replace("date1", "${parseDate(sd, isDateOnly)}").replace("date2", "${parseDate(et, isDateOnly)}").replace("duration1", "${daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds,isDateOnly)}")
            }else{
                outputString = StringsOfLanguages.ENGLISH.blooddays.replace("date1", "${parseDate(sd, isDateOnly)}").replace("date2", "${parseDate(et, isDateOnly)}").replace("duration1", "${daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds,isDateOnly)}")
            }
        }

    }else if (fixedDurations[index].type == DurationType.TUHR||
        fixedDurations[index].type == DurationType.TUHR_MUBTADIA||
        fixedDurations[index].type==DurationType.TUHR_MUBTADIA_BECAME_A_MUTADA_NOW){
        val time = fixedDurations[index].timeInMilliseconds
        outputString =  StringsOfLanguages.ENGLISH.pakidays.replace("duration1", "${daysHoursMinutesDigital(time, isDateOnly)}")
        if(fixedDurations[index].type==DurationType.TUHR_MUBTADIA_BECAME_A_MUTADA_NOW){
            outputString+= StringsOfLanguages.ENGLISH.becamemutadah
        }
    }else if (fixedDurations[index].type == DurationType.TUHREFAASID ||
        fixedDurations[index].type == DurationType.TUHREFAASID_MUBTADIA){
        outputString =  StringsOfLanguages.ENGLISH.tuhrfasid.replace("duration1", "${daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds, isDateOnly)}")
    }else if (fixedDurations[index].type==DurationType.TUHREFAASID_WITH_ISTEHAZA||
            fixedDurations[index].type==DurationType.TUHREFAASID_MUBTADIA_WITH_ISTEHAZA){
        outputString =  StringsOfLanguages.ENGLISH.tuhrfasidwithaddition.replace("duration1", "${daysHoursMinutesDigital(fixedDurations[index].istihazaAfter, isDateOnly)}").replace("duration2", "${daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds, isDateOnly)}").replace("duration3", "${daysHoursMinutesDigital((fixedDurations[index].istihazaAfter+fixedDurations[index].timeInMilliseconds), isDateOnly)}")
    }else if (fixedDurations[index].type == DurationType.DAM_IN_NIFAAS_PERIOD){
        val sd = fixedDurations[index].startDate
        val et = fixedDurations[index].endDate
        if(fixedDurations[index].days<=40){
            outputString = StringsOfLanguages.ENGLISH.nifasdays.replace("date1", "${parseDate(sd, isDateOnly)}").replace("date2", "${parseDate(et,isDateOnly)}").replace("duration1", "${daysHoursMinutesDigital((difference(sd,et)), isDateOnly)}")
        }else{//more than 40
            outputString = StringsOfLanguages.ENGLISH.blooddays.replace("date1", "${parseDate(sd, isDateOnly)}").replace("date2", "${parseDate(et, isDateOnly)}").replace("duration1", "${daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds,isDateOnly)}")
        }
    }else if (fixedDurations[index].type == DurationType.TUHR_IN_HAML){

    }else if (fixedDurations[index].type == DurationType.DAM_IN_HAML){

    }else if (fixedDurations[index].type == DurationType.TUHR_BIGGER_THAN_6_MONTHS){
        outputString = StringsOfLanguages.ENGLISH.twomonthstuhr.replace("duration1", "${daysHoursMinutesDigital(fixedDurations[index].timeInMilliseconds,isDateOnly)}")
    }
    return outputString
}

fun outputStringEnglishBiggerThan10Hall(fixedDurations: MutableList<FixedDuration>,index: Int, isDateOnly: Boolean):String{
    var strEnglish = ""

    fun haizLineEnglish(sd:Date,ed:Date, isDateOnly: Boolean):String{
        return StringsOfLanguages.ENGLISH.haizdaysinsolution.replace("date1", "${parseDate(sd, isDateOnly)}").replace("date2", "${parseDate(ed,isDateOnly)}").replace("duration1", "${daysHoursMinutesDigital((difference(sd,ed)), isDateOnly)}")
    }
    fun istihazaLineEnglish(sd:Date,ed:Date, isDateOnly: Boolean):String{
        return StringsOfLanguages.ENGLISH.istihazadays.replace("date1", "${parseDate(sd, isDateOnly)}").replace("date2", "${parseDate(ed,isDateOnly)}").replace("duration1", "${daysHoursMinutesDigital(difference(sd,ed), isDateOnly)}")
    }

    if((fixedDurations[index].days>10&&fixedDurations[index].type==DurationType.DAM)){
        strEnglish += StringsOfLanguages.ENGLISH.dashesline
        strEnglish += StringsOfLanguages.ENGLISH.solution

        for(duration in fixedDurations[index].biggerThanTen!!.durationsList){
            if(duration.type == DurationType.ISTIHAZA_BEFORE){
                strEnglish+= istihazaLineEnglish(duration.startTime,duration.endDate,isDateOnly)
                strEnglish+= StringsOfLanguages.ENGLISH.istihazadetailslineone

            }else if(duration.type == DurationType.HAIZ){
                strEnglish+= haizLineEnglish(duration.startTime,duration.endDate,isDateOnly)

            }else if(duration.type == DurationType.ISTIHAZA_AFTER){
                strEnglish+= istihazaLineEnglish(duration.startTime,duration.endDate,isDateOnly)
                strEnglish+= StringsOfLanguages.ENGLISH.istihazadetailslinetwo.replace("date1", "${parseDate(duration.startTime,isDateOnly)}")
                strEnglish+= StringsOfLanguages.ENGLISH.istihazadetailslineone

            }else if(duration.type == DurationType.LESS_THAN_3_HAIZ){
                strEnglish+= haizLineEnglish(duration.startTime,duration.endDate,isDateOnly)
                //maybe we'll wanna add something about itibaar bil khawateem
            }
        }

        strEnglish += StringsOfLanguages.ENGLISH.dashesline
    }

    return strEnglish
}
fun outputStringEnglishBiggerThan40Hall(fixedDurations: MutableList<FixedDuration>,index: Int, isDateOnly: Boolean):String{

    var strEnglish = ""

    fun nifasLineEnglish(sd:Date,ed:Date, isDateOnly: Boolean):String{
        return StringsOfLanguages.ENGLISH.nifasdaysinsolution.replace("date1", "${parseDate(sd, isDateOnly)}").replace("date2", "${parseDate(ed,isDateOnly)}").replace("duration1", "${daysHoursMinutesDigital((difference(sd,ed)), isDateOnly)}")
    }
    fun haizLineEnglish(sd:Date,ed:Date, isDateOnly: Boolean):String{
        return StringsOfLanguages.ENGLISH.haizdaysinsolution.replace("date1","${parseDate(sd, isDateOnly)}").replace("date2", "${parseDate(ed, isDateOnly)}").replace("duration1", "${daysHoursMinutesDigital((difference(sd,ed)), isDateOnly)}")
    }
    fun istihazaLineEnglish(sd:Date,ed:Date, isDateOnly: Boolean):String{
        return StringsOfLanguages.ENGLISH.istihazadays.replace("date1", "${parseDate(sd, isDateOnly)}").replace("date2", "${parseDate(ed,isDateOnly)}").replace("duration1", "${daysHoursMinutesDigital(difference(sd,ed), isDateOnly)}")
    }

    if(fixedDurations[index].days>40&&fixedDurations[index].type==DurationType.DAM_IN_NIFAAS_PERIOD){
        strEnglish += StringsOfLanguages.ENGLISH.dashesline
        strEnglish += StringsOfLanguages.ENGLISH.solution

        for(duration in fixedDurations[index].biggerThanForty!!.durationsList){
            if(duration.type==DurationType.NIFAAS){
                strEnglish+= nifasLineEnglish(duration.startTime,duration.endDate, isDateOnly)
            }else if(duration.type==DurationType.ISTIHAZA_AFTER){
                strEnglish+= istihazaLineEnglish(duration.startTime,duration.endDate, isDateOnly)
                strEnglish+= StringsOfLanguages.ENGLISH.istihazadetailslinetwo.replace("date1", "${parseDate(duration.startTime,isDateOnly)}")
                strEnglish+= StringsOfLanguages.ENGLISH.istihazadetailslineone

            }else if(duration.type==DurationType.HAIZ){
                strEnglish+= haizLineEnglish(duration.startTime,duration.endDate, isDateOnly)

            }else if(duration.type == DurationType.LESS_THAN_3_HAIZ){
                strEnglish+= haizLineEnglish(duration.startTime,duration.endDate,isDateOnly)
                //maybe we'll wanna add something about itibaar bil khawateem
            }
        }
        strEnglish += StringsOfLanguages.ENGLISH.dashesline
    }

    return strEnglish
}
fun generateEnglishOutputStringPregnancy(fixedDurations: MutableList<FixedDuration>, isDateOnly: Boolean, pregnancy: Pregnancy, endingOutputValues: EndingOutputValues, isDuration: Boolean):String{
    val mustabeen = pregnancy.mustabeenUlKhilqat
//    var startTimeOfPregnancy = pregnancy.pregStartTime.getTime()
    val birthTime = pregnancy.birthTime
    var str = StringsOfLanguages.ENGLISH.answer

    if(mustabeen){
        if(fixedDurations[0].type!=DurationType.HAML){
            str += StringsOfLanguages.ENGLISH.beforepregheader
        }
        for(index in fixedDurations.indices){
            if(isDuration){
                str += outputStringEnglishHeaderLineDuration(fixedDurations,index, isDateOnly)
                str += outputStringEnglishBiggerThan10HallDurations(fixedDurations,index, isDateOnly)
                str += outputStringEnglishBiggerThan40HallDuration(fixedDurations,index, isDateOnly)
                if(fixedDurations[index].type==DurationType.HAML){
                    str += StringsOfLanguages.ENGLISH.pregduration
                }
                if(fixedDurations[index].type==DurationType.WILADAT_ISQAT){
                    str += StringsOfLanguages.ENGLISH.birthduration
                }

            }else{
                str += outputStringEnglishHeaderLine(fixedDurations,index, isDateOnly)
                str += outputStringEnglishBiggerThan10Hall(fixedDurations,index, isDateOnly)
                str += outputStringEnglishBiggerThan40Hall(fixedDurations,index, isDateOnly)
                if(fixedDurations[index].type==DurationType.HAML){
                    str += StringsOfLanguages.ENGLISH.preg
                }
                if(fixedDurations[index].type==DurationType.WILADAT_ISQAT){
                    str += StringsOfLanguages.ENGLISH.birth.replace("date1", "${urduDateFormat(birthTime, isDateOnly)}")
                    if(index<fixedDurations.size-2){//if there is something after wiladat
                        str += StringsOfLanguages.ENGLISH.afterpregheader
                    }
                }

            }

        }
        str += outputStringEnglishFinalLines(isDateOnly, endingOutputValues, isDuration)



    }else{//if it is ghair mustabeenulkhilqat
        str += StringsOfLanguages.URDU.headerline

        for(index in fixedDurations.indices){
            str += outputStringUrduHeaderLine(fixedDurations,index, isDateOnly)
            str += outputStringUrduBiggerThan10Hall(fixedDurations,index, isDateOnly)
            if(fixedDurations[index].type==DurationType.HAML){
                str += StringsOfLanguages.URDU.preg
            }
            if(fixedDurations[index].type==DurationType.WILADAT_ISQAT){
                str += StringsOfLanguages.URDU.earlymiscarriage.replace("date1", "${urduDateFormat(birthTime, isDateOnly)}")
                if(index<fixedDurations.size-2){//if there is something after wiladat
                    str += StringsOfLanguages.URDU.afterpregheader
                }
            }

        }
        str += outputStringUrduFinalLines (isDateOnly, endingOutputValues, isDuration)

    }


    return str
}
fun outputStringEnglishFinalLines(isDateOnly: Boolean, endingOutputValues: EndingOutputValues, isDuration: Boolean):String{
    var strEnglish = ""
    val aadats = endingOutputValues.aadats
    if(isDuration){return strEnglish}
    strEnglish+=outputStringEnglishAadatLine(isDateOnly, aadats)
    val filHaal = endingOutputValues.filHaalPaki
    strEnglish+=outputStringEnglishFilHaalLine(filHaal)
    val futureDates = endingOutputValues.futureDateType
    strEnglish+=outputStringEnglishAskAgainLine(isDateOnly, futureDates)

    //plis note down line
    strEnglish+=StringsOfLanguages.ENGLISH.writedown

    //Allahu Aaalam line
    strEnglish+=StringsOfLanguages.ENGLISH.allahknows

    return strEnglish
}
fun outputStringEnglishFilHaalLine(filHaalPaki:Boolean):String{
    val filHaalPakiStr = StringsOfLanguages.ENGLISH.currentpaki
    val filHaalHaizStr = StringsOfLanguages.ENGLISH.currenthaiz
    return if(filHaalPaki){
        filHaalPakiStr
    }else{
        filHaalHaizStr
    }
}

fun outputStringEnglishAskAgainLine(isDateOnly: Boolean, futureDateType: FutureDateType?):String{
    var strEnglish = ""
    if (futureDateType==null){
        return ""
    }
    val futureDate= futureDateType.date
    val futureDatesType = futureDateType.futureDates
    if(futureDatesType==TypesOfFutureDates.A3_CHANGING_TO_A2){
        strEnglish += StringsOfLanguages.ENGLISH.situationmaychange.replace("date1", "${parseDate(futureDate, isDateOnly)}")
    }else if(futureDatesType==TypesOfFutureDates.END_OF_AADAT_HAIZ){
        strEnglish += StringsOfLanguages.ENGLISH.haizend.replace("date1", "${parseDate(futureDate, isDateOnly)}")
        strEnglish += StringsOfLanguages.ENGLISH.ihtiyatighusl.replace("date1", "${parseDate(futureDate, isDateOnly)}")
        //sex line
        strEnglish += StringsOfLanguages.ENGLISH.sexnotallowed.replace("date1", "${parseDate(futureDate, isDateOnly)}")
    }else if(futureDatesType==TypesOfFutureDates.END_OF_AADAT_TUHR){
        strEnglish += StringsOfLanguages.ENGLISH.endofpaki.replace("date1", "${parseDate(futureDate, isDateOnly)}")

    }
    return strEnglish

}
fun outputStringEnglishAadatLine(isDateOnly: Boolean, aadats:AadatsOfHaizAndTuhr?):String{
    var strEnglish = ""

    return if(aadats==null){
        ""
    }else{
        val aadatTuhr = aadats.aadatTuhr
        val aadatHaiz = aadats.aadatHaiz
        if(aadatTuhr==-1L&&aadatHaiz==-1L){
            strEnglish+= StringsOfLanguages.ENGLISH.thereisnoaadat
        }else if(aadatTuhr==-1L&&aadatHaiz!=-1L){
            strEnglish+= StringsOfLanguages.ENGLISH.aadatofhaizonly.replace("duration1", "${daysHoursMinutesDigital(aadatHaiz, isDateOnly)}")
        }else{
            strEnglish+= StringsOfLanguages.ENGLISH.habit.replace("duration1", "${daysHoursMinutesDigital(aadatHaiz, isDateOnly)}").replace("duration2", "${daysHoursMinutesDigital(aadatTuhr, isDateOnly)}")
        }
        strEnglish
    }


}