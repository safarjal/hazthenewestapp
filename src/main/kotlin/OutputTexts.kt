import kotlin.js.Date

fun outputStringHeaderLine(fixedDurations: MutableList<FixedDuration>, index:Int):String{
    return "<b>${daysHoursMinutesDigital(fixedDurations[index].days)} ${fixedDurations[index].type}</b>\n"
}

fun outputStringSumOfIndicesLine(fixedDurations: MutableList<FixedDuration>, durations:List<Duration>, index:Int):String{
    if(fixedDurations[index].indices.size>1){
        var sum:Double = 0.0
        var str = ""
        for (index in fixedDurations[index].indices){
            sum+=durations[index].days
            str += " + ${daysHoursMinutesDigital(durations[index].days)}"
        }
        str=str.removePrefix(" + ")
        return "\t${str} = ${daysHoursMinutesDigital(sum)}\n"
    }else{
        return ""
    }
}

fun outputStringIstihazaAfterLine(fixedDurations: MutableList<FixedDuration>,index: Int):String{
    val istihazaAfter = fixedDurations[index].istihazaAfter
    var str = ""
    if(istihazaAfter!=0.0){
        str +="\t${daysHoursMinutesDigital(fixedDurations[index].days-istihazaAfter)} " +
                "tuhr + ${daysHoursMinutesDigital(istihazaAfter)} istihaza " +
                "= ${daysHoursMinutesDigital(fixedDurations[index].days)} tuhr-e-faasid\n"
    }

    return str
}

fun outputStringBiggerThan10Hall(fixedDurations: MutableList<FixedDuration>,index:Int):String{
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
    str += "\t${daysHoursMinutesDigital(mp)}\t${daysHoursMinutesDigital(gp)}\t${daysHoursMinutesDigital(dm)}\t${daysHoursMinutesDigital(hz)}\t${qism}\n"
    str +="\tAadat: ${aadatHaz}/${aadatTuhr}\n"
    str += "\tOut of ${daysHoursMinutesDigital(dm)}, the first "
    if (istihazaBefore>0){
        str += "${daysHoursMinutesDigital(istihazaBefore)} are istihaza, then the next "
    }
    str += "${daysHoursMinutesDigital(haiz)} are haiz, "

    //if istihazaAfter is bigger than addatTuhr +3, run daur
    if (istihazaAfter>=aadatTuhr+3){
        //find quotient and remainder
        val remainder = istihazaAfter%(aadatHaz+aadatTuhr)
        val quotient = ((istihazaAfter-remainder)/(aadatHaz+aadatTuhr)).toInt()

        if(remainder == 0.0){
            for (j in 1 until quotient){
                str+="then the next ${daysHoursMinutesDigital(aadatTuhr)} are istihaza, " +
                        "then the next ${daysHoursMinutesDigital(aadatHaz)} are haiz, "
            }
            str+="then the next ${daysHoursMinutesDigital(aadatTuhr)} are istihaza, " +
                    "then the last ${daysHoursMinutesDigital(aadatHaz)} are haiz. "

        }else{//remainder exists
            for (j in 1 .. quotient){
                str+="then the next ${daysHoursMinutesDigital(aadatTuhr)} are istihaza, " +
                        "then the next ${daysHoursMinutesDigital(aadatHaz)} are haiz, "
            }
            if (remainder<aadatTuhr + 3){//it ended in tuhr
                str+="then the last ${daysHoursMinutesDigital(remainder)} are istihaza.\n"

            }else{//it ended in haiz
                str+="then the next ${aadatTuhr} days are tuhr, then the last ${remainder-aadatTuhr} days are haiz\n"
                //change aadatHaiz
                val newAadatHaz = remainder-aadatTuhr
                //add aadat line
                str+="\tAadat: ${daysHoursMinutesDigital(newAadatHaz)}/${daysHoursMinutesDigital(aadatTuhr)}\n"

            }
        }
    }else{
        str += "and the last ${daysHoursMinutesDigital(istihazaAfter)} are istihaza.\n"

    }
    str+="\t\n"

    //output hukm in dates
    val istihazaBeforeStartDate:Date = fixedDurations[index].startDate!!
    val haizStartDate = addTimeToDate(istihazaBeforeStartDate, (istihazaBefore*MILLISECONDS_IN_A_DAY).toLong())
    val istihazaAfterStartDate = addTimeToDate(haizStartDate, (haiz*MILLISECONDS_IN_A_DAY).toLong())
    val istihazaAfterEndDate = addTimeToDate(istihazaAfterStartDate, (istihazaAfter*MILLISECONDS_IN_A_DAY).toLong())

    if(istihazaBefore!=0.0){
        str+="\tFrom ${istihazaBeforeStartDate} to ${haizStartDate} is istihaza, yaqeeni paki\n"
    }
    str+="\tFrom ${haizStartDate} to ${istihazaAfterStartDate} is haiz\n"
    if(istihazaAfter!=0.0){
        if (istihazaAfter>=aadatTuhr+3){
            //find quotient and remainder
            val remainder = istihazaAfter%(aadatHaz+aadatTuhr)
            val quotient = ((istihazaAfter-remainder)/(aadatHaz+aadatTuhr)).toInt()

            var aadatTuhrStartDate:Date = istihazaAfterStartDate
            var aadatTuhrEndDate:Date
            var aadatHaizEndDate:Date
            for (j in 1 .. quotient){
                aadatTuhrEndDate = addTimeToDate(istihazaAfterStartDate,(aadatTuhr*MILLISECONDS_IN_A_DAY).toLong())
                aadatHaizEndDate = addTimeToDate(aadatTuhrEndDate,(aadatHaz*MILLISECONDS_IN_A_DAY).toLong())
                str+= "\tFrom ${aadatTuhrStartDate} to ${aadatTuhrEndDate} is istihaza, yaqeeni paki\n"
                str+= "\tFrom ${aadatTuhrEndDate} to ${aadatHaizEndDate} is haiz\n"
                aadatTuhrStartDate=aadatHaizEndDate
            }
            if (remainder<aadatTuhr + 3 && remainder!=0.0){//it ended in tuhr
                str+= "\tFrom ${aadatTuhrStartDate} to ${istihazaAfterEndDate} is istihaza, yaqeeni paki\n"

            }else{//it ended in haiz
                aadatTuhrEndDate = addTimeToDate(istihazaAfterStartDate,(aadatTuhr*MILLISECONDS_IN_A_DAY).toLong())
                str+= "\tFrom ${aadatTuhrStartDate} to ${aadatTuhrEndDate} is istihaza, yaqeeni paki\n"
                str+= "\tFrom ${aadatTuhrEndDate} to ${istihazaAfterEndDate} is haiz\n"

                //change aadatHaiz
                val newAadatHaz = remainder-aadatTuhr
                //add aadat line
                str+="\tAadat: ${newAadatHaz}/${aadatTuhr}\n"

            }

        }else{//no duar
            str+="\tFrom ${istihazaAfterStartDate} to ${istihazaAfterEndDate} is istihaza, yaqeeni paki\n"

        }
    }



    println(str)
    return str
}