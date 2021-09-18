import kotlin.js.Date

fun generateOutputString(fixedDurations: MutableList<FixedDuration>,durations: List<Duration>,
                         isDateOnly:Boolean):OutputTexts{
    var index = 0
    var englishStr = ""
    while (index<fixedDurations.size){
        englishStr += outputStringHeaderLine(fixedDurations,index, isDateOnly)
        englishStr += outputStringSumOfIndicesLine(fixedDurations,durations, index, isDateOnly)
        englishStr += outputStringIstihazaAfterLine(fixedDurations, index, isDateOnly)
        englishStr += outputStringBiggerThan10Hall(fixedDurations, index, isDateOnly)

        index++
    }
    var urduStr = generateUrduOutputString(fixedDurations, isDateOnly)
    return OutputTexts(englishStr,urduStr)
}

class OutputTexts (
    var englishText:String,
    var urduText: String
)

fun generateUrduOutputString(fixedDurations: MutableList<FixedDuration>, isDateOnly: Boolean):String{
    var str = "${UnicodeChars.ROSE}<b>جواب ::</b>\n\n"
    str += "مندرجہ ذیل ترتیب سے دم و طہر آیا:\n\n"
    var index = 0
    while (index<fixedDurations.size){
        str += outputStringUrduHeaderLine(fixedDurations,index, isDateOnly)
        str += outputStringUrduBiggerThan10Hall(fixedDurations,index, isDateOnly)
        if(index==fixedDurations.size-1){//if this os the last index
            str += outputStringUrduFinalLines(fixedDurations,index, isDateOnly)
        }
        index++
    }
    return str
}
fun outputStringUrduFinalLines(fixedDurations: MutableList<FixedDuration>, index: Int, isDateOnly: Boolean):String{
    var strUrdu = ""
    strUrdu+=outputStringUrduAadatLine(fixedDurations, index, isDateOnly)
    strUrdu+=outputStringUrduFilHaalLine(fixedDurations, index)
    strUrdu+=outputStringUrduAskAgainLine(fixedDurations,index, isDateOnly)

    //plis note down line
    strUrdu+="جب بھی خون یا دھبے آئیں تو وقت تاریخ مہینہ نوٹ فرمالیجئے۔\n\n"

    //Allahu Aaalam line
    strUrdu+="<b>واللہ تعالی اعلم بالصواب</b>\n\n"

    return strUrdu
}
fun outputStringUrduFilHaalLine(fixedDurations: MutableList<FixedDuration>, index: Int):String{
    var strUrdu = ""
    var filHaalPakiStr = "فی الحال آپ کے پاکی کے دن ہیں اپنی عبادات جاری رکھیں ۔\n\n"
    var filHaalHaizStr = "فی الحال آپ کے حیض کے دن ہیں نمازیں نہ پڑہیں ۔\n\n"
    //right now, we are just going to check to see what last halat is
    var istihazaAfter = fixedDurations[index].biggerThanTen?.istihazaAfter ?: return ""
    var aadatHaiz = fixedDurations[index].biggerThanTen?.aadatHaiz ?: return ""
    var aadatTuhr = fixedDurations[index].biggerThanTen?.aadatTuhr ?: return ""
    if(istihazaAfter==0.0){//last halat is haiz
        strUrdu+=filHaalHaizStr
    }else if(istihazaAfter>=aadatTuhr+3){//last period is long istihaza, lets's figure out more
        //find remainder
        var remainder = istihazaAfter%(aadatHaiz+aadatTuhr)

        if (remainder<aadatTuhr + 3 && remainder!=0.0){//it ended in tuhr
            strUrdu+=filHaalPakiStr
        }else{//it ended in haiz or remainder is 0
             if (remainder!=0.0){//it ended in haiz
                 strUrdu+=filHaalHaizStr
            }else{//it ended in tuhr
                strUrdu+=filHaalPakiStr
            }
        }
    }else{//last halat is short istihaza
        strUrdu+=filHaalPakiStr
    }

    return strUrdu
}

fun outputStringUrduAskAgainLine(fixedDurations: MutableList<FixedDuration>,index: Int, isDateOnly: Boolean):String{
    var strUrdu = ""
    //my understanding is, that ask again line only gets generated if the fil haal is istihazaAfter
    var istihazaAfter = fixedDurations[index].biggerThanTen?.istihazaAfter ?: return ""
    var aadatHaiz = fixedDurations[index].biggerThanTen?.aadatHaiz ?: return ""
    var aadatTuhr = fixedDurations[index].biggerThanTen?.aadatTuhr ?: return ""


    if(istihazaAfter!=0.0){//if there is an istihaza after
        var endDateOfBleeding = fixedDurations[index].startDate?.let { addTimeToDate(it, fixedDurations[index].timeInMilliseconds) }
        var askAgainDate:Date? = null
        if(istihazaAfter>=aadatTuhr+3){//if istihazaAfter is long
            //find remainder
            var remainder = istihazaAfter%(aadatHaiz+aadatTuhr)
            if (remainder < aadatTuhr+3){//it ended in istihaza
                var startTimeOfIstihaza = endDateOfBleeding?.let { addTimeToDate(it, -(remainder*MILLISECONDS_IN_A_DAY).toLong()) }
                askAgainDate = startTimeOfIstihaza?.let { addTimeToDate(it, (aadatTuhr*MILLISECONDS_IN_A_DAY).toLong()) }!!
            }else{//it ended in haiz

            }

        }else{//short istihazaAfter
            if(fixedDurations[index].biggerThanTen?.qism==Soortain.A_3){
                //this can change to A2. gotta figure out when. set ask again to then.
            }else{
                var endDateOfHaiz = endDateOfBleeding?.let { addTimeToDate(it, -(istihazaAfter*MILLISECONDS_IN_A_DAY).toLong()) }
                askAgainDate = endDateOfHaiz?.let { addTimeToDate(it, (aadatTuhr*MILLISECONDS_IN_A_DAY).toLong()) }!!
            }
        }
        if(askAgainDate!=null){
            strUrdu = "اگر خون اسی طرح جاری رہے یا فی الحال بند ہوجائے لیکن پندرہ دن کی کامل پاکی نہیں ملی کہ دوبارہ خون یا دھبہ آگیا تب پھر<b> ${urduDateFormat(askAgainDate, isDateOnly)} تک آپ کے یقینی پاکی کے دن ہونگے۔</b>\n\n"
        }
    }

    return strUrdu
}
fun outputStringUrduAadatLine(fixedDurations: MutableList<FixedDuration>, index: Int, isDateOnly: Boolean):String{
    var strUrdu = ""
    var aadatHaiz = fixedDurations[index].biggerThanTen?.aadatHaiz ?: return ""
    var aadatTuhr = fixedDurations[index].biggerThanTen?.aadatTuhr ?: return ""
    var istihazaAfter = fixedDurations[index].biggerThanTen?.istihazaAfter ?: return ""

    if (istihazaAfter>=aadatTuhr+3) {//if we have a long istihaza after, there is a possibility that aadat changed
        //find remainder
        var remainder = istihazaAfter % (aadatHaiz + aadatTuhr)
        if (remainder<aadatTuhr + 3 && remainder!=0.0){//it ended in tuhr, so aadat doesn't change

        }else{//it ended in haiz or remainder is 0 (which means ending in tuhr)
            //change aadatHaiz if remainder is not zero (if it is zero, aadat doesn't change, so shouldn't be printed
            if (remainder!=0.0){
                val aadatHaiz = (remainder-aadatTuhr).toString()
            }
        }
    }

    strUrdu+="${UnicodeChars.GREEN_CIRCLE} <b>عادت:: حیض: ${daysHoursMinutesDigitalUrdu(aadatHaiz, isDateOnly)}، طہر: ${daysHoursMinutesDigitalUrdu(aadatTuhr, isDateOnly)}</b>\n\n"

    return strUrdu
}

fun outputStringUrduBiggerThan10Hall(fixedDurations: MutableList<FixedDuration>,index: Int, isDateOnly: Boolean):String{
    var strUrdu = ""

//    val mp = fixedDurations[index].biggerThanTen?.mp ?: return ""
//    val gp = fixedDurations[index].biggerThanTen?.gp ?: return ""
//    val dm = fixedDurations[index].biggerThanTen?.dm ?: return ""
//    val hz = fixedDurations[index].biggerThanTen?.hz ?: return ""
//    val qism = fixedDurations[index].biggerThanTen?.qism ?: return ""
    val istihazaBefore = fixedDurations[index].biggerThanTen?.istihazaBefore ?: return ""
    val haiz = fixedDurations[index].biggerThanTen?.haiz ?: return ""
    val istihazaAfter = fixedDurations[index].biggerThanTen?.istihazaAfter ?: return ""
    val aadatTuhr = fixedDurations[index].biggerThanTen?.aadatTuhr ?: return ""
    val aadatHaz = fixedDurations[index].biggerThanTen?.aadatHaiz ?: return ""
    var istimrar = false
    if (fixedDurations[index].type==DurationType.ISTIMRAR){
        istimrar = true
    }

    fun haizLineUrdu(sd:Date,ed:Date, isDateOnly: Boolean):String{
        return "${UnicodeChars.RED_CIRCLE} ${urduDateFormat(sd, isDateOnly)} تا ${urduDateFormat(ed,isDateOnly)} کل ${daysHoursMinutesDigitalUrdu((difference(sd,ed)), isDateOnly)} حیض کے ہیں۔\n\n"
    }
    fun istihazaLineUrdu(sd:Date,ed:Date):String{
        return "${UnicodeChars.YELLOW_CIRCLE} ${urduDateFormat(sd, isDateOnly)} تا ${urduDateFormat(ed,isDateOnly)} کل ${daysHoursMinutesDigitalUrdu(difference(sd,ed), isDateOnly)} یقینی پاکی (استحاضہ) کے ہیں۔\n\n"
    }

    if((fixedDurations[index].days>10&&fixedDurations[index].type==DurationType.DAM)
        ||istimrar == true){
        strUrdu += "${UnicodeChars.FAT_DASH}${UnicodeChars.FAT_DASH}${UnicodeChars.FAT_DASH}${UnicodeChars.FAT_DASH}\n\n"
        strUrdu += "${UnicodeChars.RAINBOW} <b>مسئلہ کا حل ::</b>\n\n"


        val istihazaBeforeStartDate:Date = fixedDurations[index].startDate!!
        val haizStartDate = addTimeToDate(istihazaBeforeStartDate, (istihazaBefore*MILLISECONDS_IN_A_DAY).toLong())
        val istihazaAfterStartDate = addTimeToDate(haizStartDate, (haiz*MILLISECONDS_IN_A_DAY).toLong())
        val istihazaAfterEndDate = addTimeToDate(istihazaAfterStartDate, (istihazaAfter*MILLISECONDS_IN_A_DAY).toLong())

        if(istihazaBefore!=0.0){
            strUrdu+= istihazaLineUrdu(istihazaBeforeStartDate,haizStartDate)
            strUrdu+= "${UnicodeChars.BLACK_SQUARE} اس دوران میں جو نمازیں حیض سمجھ کر چھوڑیں،  ان کی قضاء ضروری ہے۔\n\n"
        }
        strUrdu+= haizLineUrdu(haizStartDate, istihazaAfterStartDate, isDateOnly)
        if(istihazaAfter!=0.0){
            if (istihazaAfter>=aadatTuhr+3){
                //find quotient and remainder
                var remainder = istihazaAfter%(aadatHaz+aadatTuhr)
                var quotient = ((istihazaAfter-remainder)/(aadatHaz+aadatTuhr)).toInt()

                if(istimrar == true){
                    strUrdu+= "\t\n"
                    strUrdu+= "\tThe first 3 cycles of daur are as follows:\n"
                    remainder = 0.0
                    quotient = 3
                }

                var aadatTuhrStartDate:Date = istihazaAfterStartDate
                var aadatTuhrEndDate:Date
                var aadatHaizEndDate:Date
                for (j in 1 .. quotient){
                    aadatTuhrEndDate = addTimeToDate(aadatTuhrStartDate,(aadatTuhr*MILLISECONDS_IN_A_DAY).toLong())
                    aadatHaizEndDate = addTimeToDate(aadatTuhrEndDate,(aadatHaz*MILLISECONDS_IN_A_DAY).toLong())
                    strUrdu+= istihazaLineUrdu(aadatTuhrStartDate,aadatTuhrEndDate)
                    strUrdu+= haizLineUrdu(aadatTuhrEndDate,aadatHaizEndDate, isDateOnly)
                    aadatTuhrStartDate=aadatHaizEndDate
                }
                if (remainder<aadatTuhr + 3 && remainder!=0.0){//it ended in tuhr
                    strUrdu+= istihazaLineUrdu(aadatTuhrStartDate,istihazaAfterEndDate)

                }else{//it ended in haiz or remainder is 0
                    aadatTuhrEndDate = addTimeToDate(aadatTuhrStartDate,(aadatTuhr*MILLISECONDS_IN_A_DAY).toLong())
                    strUrdu+= istihazaLineUrdu(aadatTuhrStartDate,aadatTuhrEndDate)
                    strUrdu+= haizLineUrdu(aadatTuhrEndDate,istihazaAfterEndDate, isDateOnly)

                    //change aadatHaiz if remainder is not zero (if it is zero, aadat doesn't change, so shouldn't be printed
                    if (remainder!=0.0){
                        val newAadatHaz1 = remainder-aadatTuhr
                        //add aadat line
//                        strUrdu+="\tAadat: ${(daysHoursMinutesDigitalUrdu(newAadatHaz1,isDateOnly))}/${daysHoursMinutesDigitalUrdu(aadatTuhr,isDateOnly)}\n"
                    }
                }

            }else{//no duar
                strUrdu+= istihazaLineUrdu(istihazaAfterStartDate,istihazaAfterEndDate)
                strUrdu+= "${UnicodeChars.BLACK_SQUARE} ${urduDateFormat(istihazaAfterStartDate,isDateOnly)} کو اگر غسل کر لیا تھا، تو غسل کے بعد والی نمازیں درست ہیں۔ اگر غسل نہیں کیا تھا، تو جب تک غسل نہیں کیا، اس کی نمازیں قضاء کریں۔\n\n"
                strUrdu+= "${UnicodeChars.BLACK_SQUARE} اگر اس دوران میں کوئی نمازیں حیض سمجھ کر چھوڑیں تھیں، ان کو بھی قضاء کریں۔\n\n"

            }
        }

        strUrdu += "${UnicodeChars.FAT_DASH}${UnicodeChars.FAT_DASH}${UnicodeChars.FAT_DASH}${UnicodeChars.FAT_DASH}\n\n"
    }

    return strUrdu
}

fun outputStringUrduHeaderLine(fixedDurations: MutableList<FixedDuration>,index: Int, isDateOnly: Boolean):String{
    var outputString = ""
    if (fixedDurations[index].type==DurationType.DAM){
        var sd:Date = fixedDurations[index].startDate!!
        var et = addTimeToDate(fixedDurations[index].startDate!!,fixedDurations[index].timeInMilliseconds)
        if (index +1<fixedDurations.size && fixedDurations[index+1].istihazaAfter>0){
            et = addTimeToDate(et, fixedDurations[index +1].istihazaAfter.toLong())
        }
        if(fixedDurations[index].days in 3.0..10.0){//if it's between 3 and 10, write haiz
            outputString = "${urduDateFormat(sd, isDateOnly)} سے ${urduDateFormat(et, isDateOnly)}" +
                    " تک کل ${daysHoursMinutesDigitalUrdu(fixedDurations[index].days,isDateOnly)} حیض۔\n\n"
        }else{
            if (fixedDurations[index].indices.size>1){//this dam is made up of more than 1
                outputString = "\n\n${urduDateFormat(sd, isDateOnly)} سے ${urduDateFormat(et, isDateOnly)}" +
                        " تک کل ${daysHoursMinutesDigitalUrdu(fixedDurations[index].days,isDateOnly)} خون جاری رھا (چونکہ آپ کو دو خون کے درمیان میں 15 دن کی کامل پاکی نہیں ملی ہے اسلیئے یوں سمجھا جائے گا کہ آپ کو مسلسل خون جاری ہی رہا ہے۔)\n\n"

            }else{
                outputString = "\n\n${urduDateFormat(sd, isDateOnly)} سے ${urduDateFormat(et, isDateOnly)}" +
                        " تک کل ${daysHoursMinutesDigitalUrdu(fixedDurations[index].days,isDateOnly)} خون۔\n\n"
            }
        }

    }else if (fixedDurations[index].type == DurationType.TUHR){
        var days = fixedDurations[index].days
        outputString =  "${daysHoursMinutesDigitalUrdu(days, isDateOnly)} پاکی۔\n\n"

    }else if (fixedDurations[index].type == DurationType.TUHREFAASID){
        outputString =  "${daysHoursMinutesDigitalUrdu(fixedDurations[index].istihazaAfter, isDateOnly)}" +
                " استحاضہ + ${daysHoursMinutesDigitalUrdu(fixedDurations[index].days, isDateOnly)} پاکی =" +
                " ${daysHoursMinutesDigitalUrdu((fixedDurations[index].istihazaAfter+fixedDurations[index].days), isDateOnly)} طہر فاسد۔\n\n"
    }else{//istimrar
        var sd:Date = fixedDurations[index].startDate!!
        var et = addTimeToDate(fixedDurations[index].startDate!!,fixedDurations[index].timeInMilliseconds)
        outputString =  "${urduDateFormat(sd, isDateOnly)} سے ${urduDateFormat(et, isDateOnly)} تا حال خون جاری ہے۔\n\n"
    }
    return outputString
}

fun outputStringHeaderLine(fixedDurations: MutableList<FixedDuration>, index:Int, isDateOnly: Boolean):String{
    if(fixedDurations[index].type==DurationType.ISTIMRAR){
        return "<b>${fixedDurations[index].type}</b>\n"
    }else if((index +1)< fixedDurations.size && fixedDurations[index+1].istihazaAfter>0){
        return "<b>${daysHoursMinutesDigital(fixedDurations[index].days,isDateOnly)} ${fixedDurations[index].type}</b>\n"
    }else{
        return "<b>${daysHoursMinutesDigital(fixedDurations[index].days,isDateOnly)} ${fixedDurations[index].type}</b>\n"

    }
}

fun outputStringSumOfIndicesLine(fixedDurations: MutableList<FixedDuration>, durations:List<Duration>, index:Int, isDateOnly: Boolean):String{
    if(fixedDurations[index].indices.size>1){
        var sum:Double = 0.0
        var str = ""
        for (index in fixedDurations[index].indices){
            sum+=durations[index].days
            str += " + ${daysHoursMinutesDigital(durations[index].days,isDateOnly)}"
        }
        str=str.removePrefix(" + ")
        if(fixedDurations[index].type==DurationType.ISTIMRAR){
            str+=" + istimrar"
            return "\t${str} = istimrar\n"
        }else{
            return "\t${str} = ${daysHoursMinutesDigital(sum,isDateOnly)}\n"
        }
    }else{
        return ""
    }
}

fun outputStringIstihazaAfterLine(fixedDurations: MutableList<FixedDuration>,index: Int, isDateOnly: Boolean):String{
    val istihazaAfter = fixedDurations[index].istihazaAfter
    var str = ""
    if(istihazaAfter!=0.0){
        str +="\t${daysHoursMinutesDigital(fixedDurations[index].days,isDateOnly)} " +
                "tuhr + ${daysHoursMinutesDigital(istihazaAfter,isDateOnly)} istihaza " +
                "= ${daysHoursMinutesDigital((fixedDurations[index].days +
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
    var istimrar = false
    if (fixedDurations[index].type==DurationType.ISTIMRAR){
        istimrar = true
    }


    //output hukm:
    var str =""
//    var str = "Rough work \n"
//    str += "MP\tGP\tDm\tHz\tQism\n"

    if (istimrar == true){
        str += "\t${daysHoursMinutesDigital(mp,isDateOnly)}\t${daysHoursMinutesDigital(gp,isDateOnly)}\tIstimrar\t" +
                "${daysHoursMinutesDigital(hz,isDateOnly)}\t${qism}\n"
    }else{
        str += "\t${daysHoursMinutesDigital(mp,isDateOnly)}\t${daysHoursMinutesDigital(gp,isDateOnly)}\t" +
                "${daysHoursMinutesDigital(dm,isDateOnly)}\t${daysHoursMinutesDigital(hz,isDateOnly)}\t${qism}\n"
    }

    str +="\tAadat: ${daysHoursMinutesDigital(aadatHaz,isDateOnly)}/${daysHoursMinutesDigital(aadatTuhr,isDateOnly)}\n"

    if(istimrar == true){
        str += "\tFrom the start of istimrar, the first "
    }else{
        str += "\tOut of ${daysHoursMinutesDigital(dm,isDateOnly)}, the first "
    }

    if (istihazaBefore>0){
        str += "${daysHoursMinutesDigital(istihazaBefore,isDateOnly)} are istihaza, then the next "
    }
    str += "${daysHoursMinutesDigital(haiz,isDateOnly)} are haiz, "

    if (istimrar == true){
        str += "then there will be a daur of ${daysHoursMinutesDigital(aadatTuhr,isDateOnly)} tuhr, " +
                "${daysHoursMinutesDigital(aadatHaz,isDateOnly)} haiz"
    }else{
        //if istihazaAfter is bigger than addatTuhr +3, run daur
        if (istihazaAfter>=aadatTuhr+3){
            //find quotient and remainder
            val remainder = istihazaAfter%(aadatHaz+aadatTuhr)
            val quotient = ((istihazaAfter-remainder)/(aadatHaz+aadatTuhr)).toInt()

            if(remainder == 0.0){
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
    }

    str+="\t\n"

    //output hukm in dates
    val istihazaBeforeStartDate:Date = fixedDurations[index].startDate!!
    val haizStartDate = addTimeToDate(istihazaBeforeStartDate, (istihazaBefore*MILLISECONDS_IN_A_DAY).toLong())
    val istihazaAfterStartDate = addTimeToDate(haizStartDate, (haiz*MILLISECONDS_IN_A_DAY).toLong())
    val istihazaAfterEndDate = addTimeToDate(istihazaAfterStartDate, (istihazaAfter*MILLISECONDS_IN_A_DAY).toLong())

    if(istihazaBefore!=0.0){
        str+="\tFrom ${parseDate(istihazaBeforeStartDate, isDateOnly)} to ${parseDate(haizStartDate, isDateOnly)} is istihaza, yaqeeni paki\n"
    }
    str+="\tFrom ${parseDate(haizStartDate, isDateOnly)} to ${parseDate(istihazaAfterStartDate, isDateOnly)} is haiz\n"
    if(istihazaAfter!=0.0){
        if (istihazaAfter>=aadatTuhr+3){
            //find quotient and remainder
            var remainder = istihazaAfter%(aadatHaz+aadatTuhr)
            var quotient = ((istihazaAfter-remainder)/(aadatHaz+aadatTuhr)).toInt()

            if(istimrar == true){
                str+= "\t\n"
                str+= "\tThe first 3 cycles of daur are as follows:\n"
                remainder = 0.0
                quotient = 3
            }

            var aadatTuhrStartDate:Date = istihazaAfterStartDate
            var aadatTuhrEndDate:Date
            var aadatHaizEndDate:Date
            for (j in 1 .. quotient){
                aadatTuhrEndDate = addTimeToDate(aadatTuhrStartDate,(aadatTuhr*MILLISECONDS_IN_A_DAY).toLong())
                aadatHaizEndDate = addTimeToDate(aadatTuhrEndDate,(aadatHaz*MILLISECONDS_IN_A_DAY).toLong())
                str+= "\tFrom ${parseDate(aadatTuhrStartDate, isDateOnly)} to ${parseDate(aadatTuhrEndDate, isDateOnly)} is istihaza, yaqeeni paki\n"
                str+= "\tFrom ${parseDate(aadatTuhrEndDate, isDateOnly)} to ${parseDate(aadatHaizEndDate, isDateOnly)} is haiz\n"
                aadatTuhrStartDate=aadatHaizEndDate
            }
            if (remainder<aadatTuhr + 3 && remainder!=0.0){//it ended in tuhr
                str+= "\tFrom ${parseDate(aadatTuhrStartDate, isDateOnly)} to ${parseDate(istihazaAfterEndDate, isDateOnly)} is istihaza, yaqeeni paki\n"

            }else{//it ended in haiz or remainder is 0
                aadatTuhrEndDate = addTimeToDate(aadatTuhrStartDate,(aadatTuhr*MILLISECONDS_IN_A_DAY).toLong())
                str+= "\tFrom ${parseDate(aadatTuhrStartDate, isDateOnly)} to ${parseDate(aadatTuhrEndDate, isDateOnly)} is istihaza, yaqeeni paki\n"
                str+= "\tFrom ${parseDate(aadatTuhrEndDate, isDateOnly)} to ${parseDate(istihazaAfterEndDate, isDateOnly)} is haiz\n"

                //change aadatHaiz if remainder is not zero (if it is zero, aadat doesn't change, so shouldn't be printed
                if (remainder!=0.0){
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