import kotlin.js.Date
fun generateOutputStringPregnancy(fixedDurations: MutableList<FixedDuration>,
                         isDateOnly:Boolean, pregnancy: Pregnancy, endingOutputValues: EndingOutputValues):OutputTexts{
    val englishStr = ""
    var urduStr = ""
    val hazDatesList = getHaizDatesList(fixedDurations)
    urduStr+= generateUrduOutputStringPregnancy(fixedDurations,isDateOnly,pregnancy, endingOutputValues)

    val hazDatesStr = generateHazDatesStr(hazDatesList,isDateOnly)

    return OutputTexts(englishStr,urduStr, hazDatesStr,hazDatesList,endingOutputValues, fixedDurations)
}

fun generateOutputString(fixedDurations: MutableList<FixedDuration>,durations: List<Duration>,
                         isDateOnly:Boolean, endingOutputValues: EndingOutputValues):OutputTexts{
    var index = 0
    var englishStr = ""
    val hazDatesList = getHaizDatesList(fixedDurations)

    while (index<fixedDurations.size){
        englishStr += outputStringHeaderLine(fixedDurations,index, isDateOnly)
        englishStr += outputStringSumOfIndicesLine(fixedDurations,durations, index, isDateOnly)
        englishStr += outputStringIstihazaAfterLine(fixedDurations, index, isDateOnly)
        englishStr += outputStringBiggerThan10Hall(fixedDurations, index, isDateOnly)


        index++
    }
    val urduStr = generateUrduOutputString(fixedDurations, isDateOnly, endingOutputValues)
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

fun generateUrduOutputStringPregnancy(fixedDurations: MutableList<FixedDuration>, isDateOnly: Boolean, pregnancy: Pregnancy, endingOutputValues: EndingOutputValues):String{
    val mustabeen = pregnancy.mustabeenUlKhilqat
//    var startTimeOfPregnancy = pregnancy.pregStartTime.getTime()
    val birthTime = pregnancy.birthTime
    var str = "${UnicodeChars.ROSE}<b>جواب ::</b>\n\n"

    if(mustabeen){
        if(fixedDurations[0].type!=DurationType.HAML){
            str += "حمل سے پہلے اس ترتیب سے خون آیا اور پاکی ملی:\n\n"
        }
        for(index in fixedDurations.indices){
            str += outputStringUrduHeaderLine(fixedDurations,index, isDateOnly)
            str += outputStringUrduBiggerThan10Hall(fixedDurations,index, isDateOnly)
            str += outputStringUrduBiggerThan40Hall(fixedDurations,index, isDateOnly)
            if(fixedDurations[index].type==DurationType.HAML){
                str += "\n<b>حمل</b>\n"
            }
            if(fixedDurations[index].type==DurationType.WILADAT_ISQAT){
                str += "\n<b>ولادت ${urduDateFormat(birthTime, isDateOnly)}</b>\n"
                if(index<fixedDurations.size-2){//if there is something after wiladat
                    str += "\n<b>ولادت کے بعد اس ترتیب سے خون آیااور پاکی ملی:</b>\n"
                }
            }

        }
        str += outputStringUrduFinalLines(isDateOnly, endingOutputValues)



    }else{//if it is ghair mustabeenulkhilqat
        str += "اس ترتیب سے خون آیا اور پاکی ملی:\n\n"

        for(index in fixedDurations.indices){
            str += outputStringUrduHeaderLine(fixedDurations,index, isDateOnly)
            str += outputStringUrduBiggerThan10Hall(fixedDurations,index, isDateOnly)
            if(fixedDurations[index].type==DurationType.HAML){
                str += "\n<b>حمل</b>\n"
            }
            if(fixedDurations[index].type==DurationType.WILADAT_ISQAT){
                str += "\n<b>${urduDateFormat(birthTime, isDateOnly)} کو اسقاط ہوا (غیر مستبین الخلقہ)</b>\n"
                if(index<fixedDurations.size-2){//if there is something after wiladat
                    str += "\n<b>ولادت کے بعد اس ترتیب سے خون آیااور پاکی ملی:</b>\n"
                }
            }

        }
        str += outputStringUrduFinalLines (isDateOnly, endingOutputValues)

    }


    return str
}

fun generateUrduOutputString(fixedDurations: MutableList<FixedDuration>, isDateOnly: Boolean, endingOutputValues: EndingOutputValues):String{
    var str = "${UnicodeChars.ROSE}<b>جواب ::</b>\n\n"
    str += "مندرجہ ذیل ترتیب سے دم و طہر آیا:\n\n"
    var index = 0
    while (index<fixedDurations.size){
        str += outputStringUrduHeaderLine(fixedDurations,index, isDateOnly)
        str += outputStringUrduBiggerThan10Hall(fixedDurations,index, isDateOnly)
        if(index==fixedDurations.size-1){//if this os the last index
            str += outputStringUrduFinalLines(isDateOnly, endingOutputValues)
        }
        index++
    }
    return str
}

fun outputStringUrduFinalLines(isDateOnly: Boolean, endingOutputValues: EndingOutputValues):String{

    var strUrdu = ""
    val aadats = endingOutputValues.aadats
    strUrdu+=outputStringUrduAadatLine(isDateOnly, aadats)
    val filHaal = endingOutputValues.filHaalPaki
    strUrdu+=outputStringUrduFilHaalLine(filHaal)
    val futureDates = endingOutputValues.futureDateType
    strUrdu+=outputStringUrduAskAgainLine(isDateOnly, futureDates)

    //plis note down line
    strUrdu+="جب بھی خون یا دھبے آئیں تو وقت تاریخ مہینہ نوٹ فرمالیجئے۔\n\n"

    //Allahu Aaalam line
    strUrdu+="<b>واللہ تعالی اعلم بالصواب</b>\n\n"

    return strUrdu
}
fun outputStringUrduFilHaalLine(filHaalPaki:Boolean):String{
    val filHaalPakiStr = "فی الحال آپ کے پاکی کے دن ہیں اپنی عبادات جاری رکھیں ۔\n\n"
    val filHaalHaizStr = "فی الحال آپ کے حیض کے دن ہیں نمازیں نہ پڑہیں ۔\n\n"
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
        strUrdu += "اگر خون اسی طرح جاری رہے یا فی الحال بند ہوجائے لیکن پندرہ دن کی کامل پاکی نہیں ملی کہ دوبارہ خون یا دھبہ آگیا تب پھر<b> ${urduDateFormat(futureDate, isDateOnly)} کو ضرور دوبارہ پوچھ لیں، اس لیے کہ مسئلہ کی صورت بدل جائے گی۔</b>\n\n"
    }else if(futureDatesType==TypesOfFutureDates.END_OF_AADAT_HAIZ){
        strUrdu += "اگر خون اسی طرح جاری رہے تب پھر<b> ${urduDateFormat(futureDate, isDateOnly)} تک آپ کے حیض کے دن ہونگے۔</b>\n\n"
        strUrdu += "اگر خون ${urduDateFormat(futureDate, isDateOnly)} سے پہلے بند ہو جاۓ تو غسل کر کے نمازیں شروع کر لیں، لیکن احتیاطا ${urduDateFormat(futureDate, isDateOnly)} کو بہی غسل کر لیجیے۔/n/n"
        strUrdu += "اگر خون رک بھي جاۓ اور غسل کر کے نمازيں بھي شروع کر لي هوں، تب بھي ${urduDateFormat(futureDate, isDateOnly)} سے پهلے ازدواجي تعلق کي اجازت نهيں\n\n"
    }else if(futureDatesType==TypesOfFutureDates.END_OF_AADAT_TUHR){
        strUrdu += "اگر خون اسی طرح جاری رہے یا فی الحال بند ہوجائے لیکن پندرہ دن کی کامل پاکی نہیں ملی کہ دوبارہ خون یا دھبہ آگیا تب پھر<b> ${urduDateFormat(futureDate, isDateOnly)} تک آپ کے یقینی پاکی کے دن ہونگے۔</b>\n\n"

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
        strUrdu+="${UnicodeChars.GREEN_CIRCLE} <b>عادت:: حیض: ${daysHoursMinutesDigitalUrdu(aadatHaiz, isDateOnly)}، طہر: ${daysHoursMinutesDigitalUrdu(aadatTuhr, isDateOnly)}</b>\n\n"
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

fun outputStringUrduBiggerThan10Hall(fixedDurations: MutableList<FixedDuration>,index: Int, isDateOnly: Boolean):String{
    var strUrdu = ""

    fun haizLineUrdu(sd:Date,ed:Date, isDateOnly: Boolean):String{
        return "${UnicodeChars.RED_CIRCLE} ${urduDateFormat(sd, isDateOnly)} تا ${urduDateFormat(ed,isDateOnly)} کل ${daysHoursMinutesDigitalUrdu((difference(sd,ed)), isDateOnly)} حیض کے ہیں۔\n\n"
    }
    fun istihazaLineUrdu(sd:Date,ed:Date, isDateOnly: Boolean):String{
        return "${UnicodeChars.YELLOW_CIRCLE} ${urduDateFormat(sd, isDateOnly)} تا ${urduDateFormat(ed,isDateOnly)} کل ${daysHoursMinutesDigitalUrdu(difference(sd,ed), isDateOnly)} یقینی پاکی (استحاضہ) کے ہیں۔\n\n"
    }

    if((fixedDurations[index].days>10&&fixedDurations[index].type==DurationType.DAM)){
        strUrdu += "${UnicodeChars.FAT_DASH}${UnicodeChars.FAT_DASH}${UnicodeChars.FAT_DASH}${UnicodeChars.FAT_DASH}\n\n"
        strUrdu += "${UnicodeChars.RAINBOW} <b>مسئلہ کا حل ::</b>\n\n"

        for(duration in fixedDurations[index].biggerThanTen!!.durationsList){
            if(duration.type == DurationType.ISTIHAZA_BEFORE){
                strUrdu+= istihazaLineUrdu(duration.startTime,duration.endDate,isDateOnly)
                strUrdu+= "${UnicodeChars.BLACK_SQUARE} اس دوران میں جو نمازیں حیض سمجھ کر چھوڑیں،  ان کی قضاء ضروری ہے۔\n\n"

            }else if(duration.type == DurationType.HAIZ){
                strUrdu+= haizLineUrdu(duration.startTime,duration.endDate,isDateOnly)

            }else if(duration.type == DurationType.ISTIHAZA_AFTER){
                strUrdu+= istihazaLineUrdu(duration.startTime,duration.endDate,isDateOnly)
                strUrdu+= "${UnicodeChars.BLACK_SQUARE} ${urduDateFormat(duration.startTime,isDateOnly)} کو اگر غسل کر لیا تھا، تو غسل کے بعد والی نمازیں درست ہیں۔ اگر غسل نہیں کیا تھا، تو جب تک غسل نہیں کیا، اس کی نمازیں قضاء کریں۔\n\n"
                strUrdu+= "${UnicodeChars.BLACK_SQUARE} اگر اس دوران میں کوئی نمازیں حیض سمجھ کر چھوڑیں تھیں، ان کو بھی قضاء کریں۔\n\n"

            }else if(duration.type == DurationType.LESS_THAN_3_HAIZ){
                strUrdu+= haizLineUrdu(duration.startTime,duration.endDate,isDateOnly)
                //maybe we'll wanna add something about itibaar bil khawateem
            }
        }

        strUrdu += "${UnicodeChars.FAT_DASH}${UnicodeChars.FAT_DASH}${UnicodeChars.FAT_DASH}${UnicodeChars.FAT_DASH}\n\n"
    }

    return strUrdu
}
fun outputStringUrduBiggerThan40Hall(fixedDurations: MutableList<FixedDuration>,index: Int, isDateOnly: Boolean):String{

    var strUrdu = ""

    fun nifasLineUrdu(sd:Date,ed:Date, isDateOnly: Boolean):String{
        return "${UnicodeChars.RED_CIRCLE} ${urduDateFormat(sd, isDateOnly)} تا ${urduDateFormat(ed,isDateOnly)} کل ${daysHoursMinutesDigitalUrdu((difference(sd,ed)), isDateOnly)} نفاس کے ہیں۔\n\n"
    }
    fun haizLineUrdu(sd:Date,ed:Date, isDateOnly: Boolean):String{
        return "${UnicodeChars.RED_CIRCLE} ${urduDateFormat(sd, isDateOnly)} تا ${urduDateFormat(ed,isDateOnly)} کل ${daysHoursMinutesDigitalUrdu((difference(sd,ed)), isDateOnly)} حیض کے ہیں۔\n\n"
    }
    fun istihazaLineUrdu(sd:Date,ed:Date, isDateOnly: Boolean):String{
        return "${UnicodeChars.YELLOW_CIRCLE} ${urduDateFormat(sd, isDateOnly)} تا ${urduDateFormat(ed,isDateOnly)} کل ${daysHoursMinutesDigitalUrdu(difference(sd,ed), isDateOnly)} یقینی پاکی (استحاضہ) کے ہیں۔\n\n"
    }

    if(fixedDurations[index].days>40&&fixedDurations[index].type==DurationType.DAM_IN_NIFAAS_PERIOD){
        strUrdu += "${UnicodeChars.FAT_DASH}${UnicodeChars.FAT_DASH}${UnicodeChars.FAT_DASH}${UnicodeChars.FAT_DASH}\n\n"
        strUrdu += "${UnicodeChars.RAINBOW} <b>مسئلہ کا حل ::</b>\n\n"

        for(duration in fixedDurations[index].biggerThanForty!!.durationsList){
            if(duration.type==DurationType.NIFAAS){
                strUrdu+= nifasLineUrdu(duration.startTime,duration.endDate, isDateOnly)
            }else if(duration.type==DurationType.ISTIHAZA_AFTER){
                strUrdu+= istihazaLineUrdu(duration.startTime,duration.endDate, isDateOnly)
                strUrdu+= "${UnicodeChars.BLACK_SQUARE} ${urduDateFormat(duration.startTime,isDateOnly)} کو اگر غسل کر لیا تھا، تو غسل کے بعد والی نمازیں درست ہیں۔ اگر غسل نہیں کیا تھا، تو جب تک غسل نہیں کیا، اس کی نمازیں قضاء کریں۔\n\n"
                strUrdu+= "${UnicodeChars.BLACK_SQUARE} اگر اس دوران میں کوئی نمازیں حیض سمجھ کر چھوڑیں تھیں، ان کو بھی قضاء کریں۔\n\n"

            }else if(duration.type==DurationType.HAIZ){
                strUrdu+= haizLineUrdu(duration.startTime,duration.endDate, isDateOnly)

            }else if(duration.type == DurationType.LESS_THAN_3_HAIZ){
                strUrdu+= haizLineUrdu(duration.startTime,duration.endDate,isDateOnly)
                //maybe we'll wanna add something about itibaar bil khawateem
            }
        }
        strUrdu += "${UnicodeChars.FAT_DASH}${UnicodeChars.FAT_DASH}${UnicodeChars.FAT_DASH}${UnicodeChars.FAT_DASH}\n\n"
    }

    return strUrdu
}

fun outputStringUrduHeaderLine(fixedDurations: MutableList<FixedDuration>,index: Int, isDateOnly: Boolean):String{
    var outputString = ""
    if (fixedDurations[index].type==DurationType.DAM){
        val sd:Date = fixedDurations[index].startDate
        val et = fixedDurations[index].endDate
        if(fixedDurations[index].days in 3.0..10.0){//if it's between 3 and 10, write haiz
            outputString = "${urduDateFormat(sd, isDateOnly)} سے ${urduDateFormat(et, isDateOnly)}" +
                    " تک کل ${daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds,isDateOnly)} حیض۔\n\n"
        }else{//bigger than 10
            if (fixedDurations[index].indices.size>1){//this dam is made up of more than 1
                outputString = "\n\n${urduDateFormat(sd, isDateOnly)} سے ${urduDateFormat(et, isDateOnly)}" +
                        " تک کل ${daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds,isDateOnly)} خون جاری رھا (چونکہ آپ کو دو خون کے درمیان میں 15 دن کی کامل پاکی نہیں ملی ہے اسلیئے یوں سمجھا جائے گا کہ آپ کو مسلسل خون جاری ہی رہا ہے۔)\n\n"

            }else{
                outputString = "\n\n${urduDateFormat(sd, isDateOnly)} سے ${urduDateFormat(et, isDateOnly)}" +
                        " تک کل ${daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds,isDateOnly)} خون۔\n\n"
            }
        }

    }else if (fixedDurations[index].type == DurationType.TUHR){
        val time = fixedDurations[index].timeInMilliseconds
        outputString =  "${daysHoursMinutesDigitalUrdu(time, isDateOnly)} پاکی۔\n\n"

    }else if (fixedDurations[index].type == DurationType.TUHREFAASID){
        outputString =  daysHoursMinutesDigitalUrdu(fixedDurations[index].istihazaAfter, isDateOnly) +
                " استحاضہ + ${daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds, isDateOnly)} پاکی =" +
                " ${daysHoursMinutesDigitalUrdu((fixedDurations[index].istihazaAfter+fixedDurations[index].timeInMilliseconds), isDateOnly)} طہر فاسد۔\n\n"
    }else if (fixedDurations[index].type == DurationType.DAM_IN_NIFAAS_PERIOD){
        val sd = fixedDurations[index].startDate
        val et = fixedDurations[index].endDate
        if(fixedDurations[index].days<=40){
            outputString = "${urduDateFormat(sd, isDateOnly)} سے ${urduDateFormat(et, isDateOnly)}" +
                    " تک کل ${daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds,isDateOnly)} نفاس۔\n\n"
        }else{//more than 40
            outputString = "\n\n${urduDateFormat(sd, isDateOnly)} سے ${urduDateFormat(et, isDateOnly)}" +
                    " تک کل ${daysHoursMinutesDigitalUrdu(fixedDurations[index].timeInMilliseconds,isDateOnly)} خون۔\n\n"
        }
    }else if (fixedDurations[index].type == DurationType.TUHR_IN_HAML){

    }else if (fixedDurations[index].type == DurationType.DAM_IN_HAML){

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