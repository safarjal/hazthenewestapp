import kotlin.js.Date

fun generateOutputString(fixedDurations: MutableList<FixedDuration>,durations: List<Duration>, isDateOnly:Boolean):String{
    var index = 0
    var str = ""
    while (index<fixedDurations.size){
        str += outputStringHeaderLine(fixedDurations,index, isDateOnly)
        str += outputStringSumOfIndicesLine(fixedDurations,durations, index, isDateOnly)
        str += outputStringIstihazaAfterLine(fixedDurations, index, isDateOnly)
        str += outputStringBiggerThan10Hall(fixedDurations, index, isDateOnly)

        index++
    }
    return str
}

fun outputStringHeaderLine(fixedDurations: MutableList<FixedDuration>, index:Int, isDateOnly: Boolean):String{
    if(fixedDurations[index].type==DurationType.ISTIMRAR){
        return "<b>${fixedDurations[index].type}</b>\n"
    }else if((index +1)< fixedDurations.size && fixedDurations[index+1].istihazaAfter>0){
        return "<b>${daysHoursMinutesDigital(fixedDurations[index].days + fixedDurations[index+1].istihazaAfter,isDateOnly)} ${fixedDurations[index].type}</b>\n"
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
        str +="\t${daysHoursMinutesDigital(fixedDurations[index].days-istihazaAfter,isDateOnly)} " +
                "tuhr + ${daysHoursMinutesDigital(istihazaAfter,isDateOnly)} istihaza " +
                "= ${daysHoursMinutesDigital(fixedDurations[index].days,isDateOnly)} tuhr-e-faasid\n"
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
                    println("$remainder remiander")
                    println("$aadatTuhr aadatTuhr")
                    str+="\tAadat: ${daysHoursMinutesDigital(newAadatHaz1,isDateOnly)}/${daysHoursMinutesDigital(aadatTuhr,isDateOnly)}\n"
                }
           }

        }else{//no duar
            str+="\tFrom ${parseDate(istihazaAfterStartDate, isDateOnly)} to ${parseDate(istihazaAfterEndDate, isDateOnly)} is istihaza, yaqeeni paki\n"

        }
    }
   return str
}