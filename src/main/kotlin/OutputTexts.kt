@file:Suppress("SpellCheckingInspection")
import kotlinx.datetime.internal.JSJoda.Instant

fun addPreMaslaValuesText(preMaslaValues: PreMaslaValues):OutputStringsLanguages{
    val newStr = OutputStringsLanguages()
    if(preMaslaValues.inputtedAadatHaiz!=null &&
        preMaslaValues.inputtedAadatTuhr!=null &&
        preMaslaValues.inputtedMawjoodahTuhr!=null){//we have all 3 pre masla values

        //add line about previous habits
        newStr.add(
            baseStr(Strings::preMaslaHabitOfHaizAndTuhr)
            .replaceDur(Rplc.DUR1, preMaslaValues.inputtedAadatHaiz!!, TypesOfInputs.DURATION)
            .replaceDur(Rplc.DUR2, preMaslaValues.inputtedAadatTuhr!!, TypesOfInputs.DURATION)
        )

        newStr.add(
            baseStr(Strings::preMaslaValueOfMawjoodaPaki)
            .replaceDur(Rplc.DUR1, preMaslaValues.inputtedMawjoodahTuhr!!, TypesOfInputs.DURATION)
        )
        //remove the word fasid or invalid, if tuhr was saheeh
        if(!preMaslaValues.isMawjoodaFasid){//if tuhr is not fasid
            newStr.replace(Rplc.FASID, "", Rplc.INVALID, "")
        }
    }
    return newStr
}

fun generateOutputStringPregnancy(fixedDurations: MutableList<FixedDuration>,
                                  pregnancy: Pregnancy,
                                  endingOutputValues: EndingOutputValues,
                                  typeOfInput: TypesOfInputs,
                                  timeZone: String):OutputTexts{
    val hazDatesList = getHaizDatesList(fixedDurations)
    val newStr = generateLanguagedOutputStringPregnancy(fixedDurations,pregnancy, endingOutputValues, typeOfInput, timeZone)

    return OutputTexts(newStr, "", hazDatesList, endingOutputValues, fixedDurations)
}
fun generateOutputStringMubtadia(fixedDurations: MutableList<FixedDuration>,
                                 endingOutputValues: EndingOutputValues,
                                 typeOfInput: TypesOfInputs,
                                 preMaslaValues: PreMaslaValues,
                                 timeZone: String):OutputTexts{
    val hazDatesList = getHaizDatesList(fixedDurations)
    val newStr = generateOutputString(fixedDurations, endingOutputValues, typeOfInput, TypesOfMasla.MUBTADIA, preMaslaValues, timeZone)

    val hazDatesStr = generateHazDatesStr(hazDatesList,typeOfInput,timeZone)

    return OutputTexts(newStr, hazDatesStr, hazDatesList,endingOutputValues, fixedDurations)
}
fun generateOutputStringMutadah(fixedDurations: MutableList<FixedDuration>,
                                endingOutputValues: EndingOutputValues,
                                typeOfInput: TypesOfInputs,
                                preMaslaValues: PreMaslaValues,
                                timeZone: String):OutputTexts{
    val hazDatesList = getHaizDatesList(fixedDurations)

    val newStr = generateOutputString(fixedDurations, endingOutputValues, typeOfInput, TypesOfMasla.MUTADAH, preMaslaValues, timeZone)

    val hazDatesStr = generateHazDatesStr(hazDatesList,typeOfInput,timeZone)

    return OutputTexts(newStr, hazDatesStr, hazDatesList,endingOutputValues, fixedDurations)
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
                                           timeZone: String): OutputStringsLanguages{
    val mustabeen = pregnancy.mustabeenUlKhilqat
    val birthTime = pregnancy.birthTime
    val newStr = baseStr(Strings::answer)

    if(mustabeen){
        if(fixedDurations[0].type!=DurationType.HAML){
            newStr.addStrings(Strings::beforepregheader)
        }
        for(index in fixedDurations.indices){
            if(typeOfInput==TypesOfInputs.DURATION) {
                newStr.add(outputStringHeaderLineDuration(fixedDurations,index))
                    .add(outputStringBiggerThan10HallDurations(fixedDurations, index))
                    .add(outputStringBiggerThan40HallDuration(fixedDurations, index))
                if(fixedDurations[index].type==DurationType.HAML){
                    newStr.addStrings(Strings::pregduration)
                }
                if(fixedDurations[index].type==DurationType.WILADAT_ISQAT){
                    newStr.addStrings(Strings::birthduration)
                }

            }else{
                newStr.add(outputStringHeaderLine(fixedDurations,index, typeOfInput, timeZone))
                    .add(outputStringBiggerThan10Hall(fixedDurations,index, typeOfInput, timeZone))
                    .add(outputStringBiggerThan40Hall(fixedDurations,index, typeOfInput, timeZone))
                if(fixedDurations[index].type==DurationType.HAML){
                    newStr.addStrings(Strings::preg)
                }
                if(fixedDurations[index].type==DurationType.WILADAT_ISQAT){
                    newStr.add(
                        baseStr(Strings::birth)
                        .replaceDT(Rplc.DT1, birthTime, typeOfInput, timeZone)
                    )

                    if(index<fixedDurations.size-2){//if there is something after wiladat
                        newStr.addStrings(Strings::afterpregheader)
                    }
                }
            }
        }
        newStr.add(outputStringFinalLines(endingOutputValues, typeOfInput, timeZone))
    }else{//if it is ghair mustabeenulkhilqat
        newStr.addStrings(Strings::headerline)
        for(index in fixedDurations.indices){
            if(typeOfInput==TypesOfInputs.DURATION){
                newStr.add(outputStringHeaderLineDuration(fixedDurations,index, mustabeen))
                    .add(outputStringBiggerThan10HallDurations(fixedDurations, index))
                if(fixedDurations[index].type==DurationType.HAML){
                    newStr.addStrings(Strings::preg)
                }
                if(fixedDurations[index].type==DurationType.WILADAT_ISQAT){
                    newStr.addStrings(Strings::earlymiscarriageduration)
                    if(index<fixedDurations.size-2){//if there is something after wiladat
                        newStr.addStrings(Strings::afterpregheader)
                    }
                }
            }else{
                newStr.add(outputStringHeaderLine(fixedDurations,index, typeOfInput, timeZone, mustabeen))
                    .add(outputStringBiggerThan10Hall(fixedDurations,index, typeOfInput, timeZone))
                if(fixedDurations[index].type==DurationType.HAML){
                    newStr.addStrings(Strings::preg)
                }
                if(fixedDurations[index].type==DurationType.WILADAT_ISQAT){
                    newStr.add(
                        baseStr(Strings::earlymiscarriage)
                        .replaceDT(Rplc.DT1, birthTime, typeOfInput, timeZone)
                    )
                    if(index<fixedDurations.size-2){//if there is something after wiladat
                        newStr.addStrings(Strings::afterpregheader)
                    }
                }
            }
        }
        newStr.add(outputStringFinalLines (endingOutputValues, typeOfInput, timeZone))
    }
    return newStr
}

fun generateOutputString(fixedDurations: MutableList<FixedDuration>,
                         endingOutputValues: EndingOutputValues,
                         typeOfInput: TypesOfInputs,
                         typesOfMasla: TypesOfMasla,
                         preMaslaValues: PreMaslaValues,
                         timeZone: String): OutputStringsLanguages{
    val newStr = baseStr(Strings::answer) //جواب:
        .add(addPreMaslaValuesText(preMaslaValues))

    if(typesOfMasla==TypesOfMasla.MUTADAH){
        newStr.addStrings(Strings::headerline) //اس ترتیب سے خون آیا اور پاکی ملی
    }else if(typesOfMasla==TypesOfMasla.MUBTADIA){
        newStr.addStrings(Strings::headerlinemubtadia) //اس ترتیب سے خون آیا اور پاکی ملی
    }

    var index = 0
    while (index<fixedDurations.size){
        if(typeOfInput==TypesOfInputs.DURATION){
            newStr.add(outputStringHeaderLineDuration(fixedDurations,index)) //اس تاریخ سے اس تاریخ تک اتنے دن حیض
                .add(outputStringBiggerThan10HallDurations(fixedDurations, index))
            if(index==fixedDurations.size-1){//if this os the last index
                newStr.add(outputStringFinalLines(endingOutputValues, typeOfInput, timeZone))
            }
        }else{//not durations
            newStr.add(outputStringHeaderLine(fixedDurations,index, typeOfInput, timeZone)) //اس تاریخ سے اس تاریخ تک اتنے دن حیض
                .add(outputStringBiggerThan10Hall(fixedDurations,index, typeOfInput, timeZone))
            if(index==fixedDurations.size-1){//if this os the last index
                newStr.add(outputStringFinalLines(endingOutputValues, typeOfInput, timeZone))
            }
        }
        index++
    }
    return newStr
}

fun outputStringFinalLines(endingOutputValues: EndingOutputValues,
                           typeOfInput: TypesOfInputs,
                           timeZone: String):OutputStringsLanguages{
    val aadats = endingOutputValues.aadats
    val newStr = outputStringAadatLine(typeOfInput, aadats)
    if (typeOfInput==TypesOfInputs.DURATION) { return newStr }

    val filHaal = endingOutputValues.filHaalPaki
    val futureDates = endingOutputValues.futureDateType

    newStr.add(outputStringFilHaalLine(filHaal))
        .add(outputStringAskAgainLine(typeOfInput, futureDates, timeZone))
        .addStrings(Strings::writedown) //plis note down line
        .addStrings(Strings::allahknows) //Allahu Aaalam line

    return newStr
}
fun outputStringFilHaalLine(filHaalPaki:Boolean?):OutputStringsLanguages{
    when (filHaalPaki) {
        true -> return baseStr(Strings::currentpaki)
        false -> return baseStr(Strings::currenthaiz)
        null -> return OutputStringsLanguages()

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
    val newStr = OutputStringsLanguages()

    for(futureDate in futureDates){
        val date = futureDate.date
        val type = futureDate.futureDates
        when (type) {
            TypesOfFutureDates.END_OF_AADAT_HAIZ -> {
                newStr.add(
                    baseStr(Strings::haizend)
                    .replaceDT(Rplc.DT1, date, typeOfInput, timeZone)
                )
            }
            TypesOfFutureDates.END_OF_AADAT_TUHR -> {
                newStr.add(
                    baseStr(Strings::endofpaki)
                    .replaceDT(Rplc.DT1, date, typeOfInput, timeZone)
                )
            }
            TypesOfFutureDates.IC_FORBIDDEN_DATE -> {
                newStr.add(
                    baseStr(Strings::sexnotallowed)
                    .replaceDT(Rplc.DT1, date, typeOfInput, timeZone)
                )
            }
            TypesOfFutureDates.AFTER_TEN_DAYS -> {
                newStr.add(
                    baseStr(Strings::aftertendays)
                    .replaceDT(Rplc.DT1, date, typeOfInput, timeZone)
                )
            }
            TypesOfFutureDates.FORTY_DAYS -> {
                newStr.add(
                    baseStr(Strings::afterfortydays)
                    .replaceDT(Rplc.DT1, date, typeOfInput, timeZone)
                )
            }
            TypesOfFutureDates.BEFORE_THREE_DAYS_MASLA_WILL_CHANGE -> {
                newStr.add(
                    baseStr(Strings::bleedingstopsbeforethreemaslachanges)
                    .replaceDT(Rplc.DT1, date, typeOfInput, timeZone)
                )
            }
            TypesOfFutureDates.BEFORE_THREE_DAYS -> {
                newStr.add(
                    baseStr(Strings::bleedingstopsbeforethree)
                    .replaceDT(Rplc.DT1, date, typeOfInput, timeZone)
                )
            }
            TypesOfFutureDates.IHTIYATI_GHUSL -> {
                newStr.add(
                    baseStr(Strings::ihtiyatighusl)
                    .replaceDT(Rplc.DT1, date, typeOfInput, timeZone)
                )
            }
            TypesOfFutureDates.A3_CHANGING_TO_A2 -> {
                newStr.add(
                    baseStr(Strings::situationmaychange)
                    .replaceDT(Rplc.DT1, date, typeOfInput, timeZone)
                )
            }
            TypesOfFutureDates.BEFORE_TEN_DAYS_AYYAMEQABLIYYAH -> {
                newStr.add(
                    baseStr(Strings::beforetendaysayyameqabliyyaallconsideredhaiz)
                    .replaceDT(Rplc.DT1, date, typeOfInput, timeZone)
                )
            }
            TypesOfFutureDates.START_OF_AADAT_AYYAMEQABLIYYA -> {
                newStr.add(
                    baseStr(Strings::endofistehazaayyameqabliyya)
                    .replaceDT(Rplc.DT1, date, typeOfInput, timeZone)
                )
            }
            TypesOfFutureDates.TEN_DAYS_EXACTLY -> {
                newStr.addStrings(Strings::tendaysdoghusl)
                    .addStrings(Strings::askagainnodate)
            }
        }
    }
    return newStr
}
fun outputStringAadatLine(
    typeOfInput: TypesOfInputs,
    aadats: AadatsOfHaizAndTuhr?
):OutputStringsLanguages{
    //we should probably put in the possibility of tuhr aadat only...
    //and tuhr aadat with nifas
    val newStr = OutputStringsLanguages()

    return if (aadats==null) newStr
    else {
        val aadatTuhr = aadats.aadatTuhr
        val aadatHaiz = aadats.aadatHaiz
        if(aadatHaiz==-1L && aadatTuhr==-1L){//neither haiz nor tuhr aadat exists
            if(aadats.aadatNifas != null && aadats.aadatNifas!=-1L){//adat nifas exists
                newStr.add(
                    baseStr(Strings::onlynifashabit)
                    .replaceDur(Rplc.DUR1, aadats.aadatNifas!!, typeOfInput)
                )

            }else {//adat nifas doesn't exists
                newStr.addStrings(Strings::thereisnoaadat)
            }
        }else if(aadatHaiz!=-1L && aadatTuhr==-1L) {//aadat of haiz exists, but not aadat of tuhr
            if (aadats.aadatNifas != null && aadats.aadatNifas != -1L) {//adat nifas exists
                newStr.add(
                    baseStr(Strings::nifasAndHaizHabit)
                    .replaceDur(Rplc.DUR1, aadatHaiz, typeOfInput)
                    .replaceDur(Rplc.DUR2, aadats.aadatNifas!!, typeOfInput)
                )
            } else {//adat nifas doesn't exists
                newStr.add(
                    baseStr(Strings::aadatofhaizonly)
                    .replaceDur(Rplc.DUR1, aadatHaiz, typeOfInput)
                )
            }
        }else if(aadatHaiz==-1L && aadatTuhr!=-1L){//aadat tuhr exist and aadat haiz doesn;t exist
            if (aadats.aadatNifas != null && aadats.aadatNifas != -1L) {//adat nifas exists
                newStr.add(
                    baseStr(Strings::nifasAndTuhrHabit)
                    .replaceDur(Rplc.DUR1, aadatHaiz, typeOfInput)
                    .replaceDur(Rplc.DUR2, aadats.aadatNifas!!, typeOfInput)
                )
            }else{
                newStr.add(
                    baseStr(Strings::onlyTuhrHabit)
                    .replaceDur(Rplc.DUR1, aadatTuhr, typeOfInput)
                )
            }
        }else{//adats of haiz and tuhr exist
            if(aadats.aadatNifas != null && aadats.aadatNifas!=-1L){//adat nifas exists
                newStr.add(
                    baseStr(Strings::habitwithnifas)
                    .replaceDur(Rplc.DUR1, aadatHaiz, typeOfInput)
                    .replaceDur(Rplc.DUR2, aadatTuhr, typeOfInput)
                    .replaceDur(Rplc.DUR3, aadats.aadatNifas!!, typeOfInput)
                )
            }else{//adat nifas doesn't exists
                newStr.add(
                    baseStr(Strings::habit)
                    .replaceDur(Rplc.DUR1, aadatHaiz, typeOfInput)
                    .replaceDur(Rplc.DUR2, aadatTuhr, typeOfInput)
                )
            }
        }
        newStr
    }
}
fun outputStringBiggerThan10HallDurations(
    fixedDurations: MutableList<FixedDuration>,
    index: Int
):OutputStringsLanguages{
    val newStr = OutputStringsLanguages()
    val typeOfInput = TypesOfInputs.DURATION

    if((fixedDurations[index].days>10 &&
                (fixedDurations[index].type==DurationType.DAM||
                        fixedDurations[index].type==DurationType.DAM_MUBTADIA))){
        newStr.addStr(UnicodeChars.ABACUS)

        for(duration in fixedDurations[index].biggerThanTen!!.durationsList){
            if(duration.type == DurationType.ISTIHAZA_BEFORE){
                newStr.add(
                    baseStr(Strings::startingFromIstehaza)
                        .replaceDur(Rplc.DUR1, duration.timeInMilliseconds, typeOfInput)
                )
            }else if(duration.type == DurationType.HAIZ){
                if(duration==fixedDurations[index].biggerThanTen!!.durationsList[0]){
                    newStr.add(
                        baseStr(Strings::startingFromHaiz)
                            .replaceDur(Rplc.DUR1, duration.timeInMilliseconds, typeOfInput)
                    )
                }else{
                    newStr.add(
                        baseStr(Strings::followedByHaizAfter)
                            .replaceDur(Rplc.DUR1, duration.timeInMilliseconds, typeOfInput)
                    )
                }
            }else if(duration.type == DurationType.ISTIHAZA_AFTER){
                newStr.add(
                    baseStr(Strings::followedByistehazaAfter)
                        .replaceDur(Rplc.DUR1, duration.timeInMilliseconds, typeOfInput)
                )
            }else if(duration.type == DurationType.LESS_THAN_3_HAIZ){
                newStr.add(
                    baseStr(Strings::followedByHaizAfter)
                        .replaceDur(Rplc.DUR1, duration.timeInMilliseconds, typeOfInput)
                )
                //maybe we'll wanna add something about itibaar bil khawateem
            }else if(duration.type == DurationType.NIFAS){
                newStr.addStrings(Strings::startingFromNifas)
            }
        }
    }
    if(newStr.englishString != ""){
        newStr.addStrings(Strings::khatimaplusnewline)
    }
    return newStr
}

fun outputStringBiggerThan10Hall(fixedDurations: MutableList<FixedDuration>,
                                 index: Int,
                                 typeOfInput: TypesOfInputs,
                                 timeZone: String):OutputStringsLanguages{
    //legacy code this, I think it's unused
    val newStr = OutputStringsLanguages()

    fun haizLine(sd:Instant, ed:Instant, typeOfInput: TypesOfInputs): OutputStringsLanguages {
        return baseStr(Strings::haizdaysinsolution)
            .replaceDT(Rplc.DT1, sd, typeOfInput, timeZone)
            .replaceDT(Rplc.DT2, ed, typeOfInput, timeZone)
            .replaceDur(Rplc.DUR1, (difference(sd,ed)), typeOfInput)
    }
    fun istihazaLine(sd:Instant, ed:Instant, typeOfInput: TypesOfInputs): OutputStringsLanguages {
        return baseStr(Strings::istihazadays)
            .replaceDT(Rplc.DT1, sd, typeOfInput, timeZone)
            .replaceDT(Rplc.DT2, ed, typeOfInput, timeZone)
            .replaceDur(Rplc.DUR1, (difference(sd,ed)), typeOfInput)
    }

    if((fixedDurations[index].days>10 &&
        (fixedDurations[index].type==DurationType.DAM||
        fixedDurations[index].type==DurationType.DAM_MUBTADIA))){
        newStr.addStrings(Strings::dashesline)
            .addStrings(Strings::solution)
        for(duration in fixedDurations[index].biggerThanTen!!.durationsList){
            when (duration.type) {
                DurationType.ISTIHAZA_BEFORE -> {
                    newStr.add(istihazaLine(duration.startTime,duration.endDate,typeOfInput))
                        .addStrings(Strings::istihazadetailslineone)
                }
                DurationType.HAIZ -> {
                    newStr.add(haizLine(duration.startTime,duration.endDate,typeOfInput))
                }
                DurationType.ISTIHAZA_AFTER -> {
                    newStr.add(istihazaLine(duration.startTime,duration.endDate,typeOfInput))
                        .add(baseStr(Strings::istihazadetailslinetwo)
                            .replaceDT(Rplc.DT1, duration.startTime, typeOfInput, timeZone)
                        )
                        .addStrings(Strings::istihazadetailslineone)
                }
                DurationType.LESS_THAN_3_HAIZ -> {
                    newStr.add(haizLine(duration.startTime,duration.endDate,typeOfInput))
                    //maybe we'll wanna add something about itibaar bil khawateem
                }
                else -> error(Rplc.Msg.NOT_BLOOD)
            }
        }
        newStr.addStrings(Strings::dashesline)
    }

    return newStr
}
fun outputStringBiggerThan40HallDuration(
    fixedDurations: MutableList<FixedDuration>,
    index: Int
):OutputStringsLanguages{
    val typeOfInput=TypesOfInputs.DURATION
    val newStr = OutputStringsLanguages()
    if(fixedDurations[index].days>40&&fixedDurations[index].type==DurationType.DAM_IN_NIFAS_PERIOD) {
        newStr.addStr(UnicodeChars.ABACUS)
        for (duration in fixedDurations[index].biggerThanForty!!.durationsList) {
            when (duration.type) {
                DurationType.NIFAS -> {
                    newStr.add(
                        baseStr(Strings::startingFromNifas)
                            .replaceDur(Rplc.DUR1, duration.timeInMilliseconds, typeOfInput)
                    )
                }

                DurationType.ISTIHAZA_AFTER -> {
                    newStr.add(
                        baseStr(Strings::followedByistehazaAfter)
                            .replaceDur(Rplc.DUR1, duration.timeInMilliseconds, typeOfInput)
                    )
                }

                DurationType.HAIZ -> {
                    newStr.add(
                        baseStr(Strings::followedByHaizAfter)
                            .replaceDur(Rplc.DUR1, duration.timeInMilliseconds, typeOfInput)
                    )
                }

                DurationType.LESS_THAN_3_HAIZ -> {
//                    TODO: same as above?
                    newStr.add(
                        baseStr(Strings::followedByHaizAfter)
                            .replaceDur(Rplc.DUR1, duration.timeInMilliseconds, typeOfInput)
                    )
                    //maybe we'll wanna add something about itibaar bil khawateem
                }
                else -> error(Rplc.Msg.NOT_BLOOD)
            }
        }
    }
    if(newStr.englishString != ""){
        newStr.addStrings(Strings::khatimaplusnewline)
    }
    return newStr
}
fun outputStringBiggerThan40Hall(fixedDurations: MutableList<FixedDuration>,
                                 index: Int,
                                 typeOfInput: TypesOfInputs,
                                 timeZone: String):OutputStringsLanguages{

    val newStr = OutputStringsLanguages()

    fun nifasLine(sd:Instant, ed:Instant, typeOfInput: TypesOfInputs):OutputStringsLanguages{
        return baseStr(Strings::nifasdaysinsolution)
            .replaceDT(Rplc.DT1, sd, typeOfInput, timeZone)
            .replaceDT(Rplc.DT2, ed, typeOfInput, timeZone)
            .replaceDur(Rplc.DUR1, (difference(sd,ed)), typeOfInput)
    }
    fun haizLine(sd:Instant, ed:Instant, typeOfInput: TypesOfInputs):OutputStringsLanguages{
        return baseStr(Strings::haizdaysinsolution)
            .replaceDT(Rplc.DT1, sd, typeOfInput, timeZone)
            .replaceDT(Rplc.DT2, ed, typeOfInput, timeZone)
            .replaceDur(Rplc.DUR1, (difference(sd,ed)), typeOfInput)
    }
    fun istihazaLine(sd:Instant, ed:Instant, typeOfInput: TypesOfInputs):OutputStringsLanguages {
        return baseStr(Strings::istihazadays)
            .replaceDT(Rplc.DT1, sd, typeOfInput, timeZone)
            .replaceDT(Rplc.DT2, ed, typeOfInput, timeZone)
            .replaceDur(Rplc.DUR1, (difference(sd, ed)), typeOfInput)
    }

    if(fixedDurations[index].days>40&&fixedDurations[index].type==DurationType.DAM_IN_NIFAS_PERIOD){
        newStr.addStrings(Strings::dashesline)
            .addStrings(Strings::solution)

        for(duration in fixedDurations[index].biggerThanForty!!.durationsList){
            when (duration.type) {
                DurationType.NIFAS -> {
                    newStr.add(nifasLine(duration.startTime,duration.endDate, typeOfInput))
                }
                DurationType.ISTIHAZA_AFTER -> {
                    newStr.add(istihazaLine(duration.startTime,duration.endDate, typeOfInput))
                        .add(
                            baseStr(Strings::istihazadetailslinetwo)
                            .replaceDT(Rplc.DT1, duration.startTime, typeOfInput, timeZone)
                        )
                        .addStrings(Strings::istihazadetailslineone)
                }
                DurationType.HAIZ -> {
                    newStr.add(haizLine(duration.startTime,duration.endDate, typeOfInput))

                }
                DurationType.LESS_THAN_3_HAIZ -> {
                    newStr.add(haizLine(duration.startTime,duration.endDate,typeOfInput))
                    //maybe we'll wanna add something about itibaar bil khawateem
                }
                else -> error(Rplc.Msg.NOT_BLOOD)
            }
        }
        newStr.addStrings(Strings::dashesline)
    }
    return newStr
}
fun outputStringHeaderLineDuration(fixedDurations: MutableList<FixedDuration>, index: Int, isMustabeen:Boolean = true):OutputStringsLanguages{
    val typeOfInput=TypesOfInputs.DURATION
    //in duration, we just give the fixed duration
    val newStr = OutputStringsLanguages()
    if (fixedDurations[index].type==DurationType.DAM||
        fixedDurations[index].type==DurationType.DAM_MUBTADIA){
        val sd:Instant = fixedDurations[index].startDate
        val et = fixedDurations[index].endDate
        if(fixedDurations[index].days in 3.0..10.0){//if it's between 3 and 10, write haiz
            newStr.add(
                baseStr(Strings::durationHaiz)
                    .replaceDur(Rplc.DUR1, difference(sd,et), typeOfInput)
            )
        }else{//bigger than 10
            newStr.add(
                baseStr(Strings::durationDam)
                    .replaceDur(Rplc.DUR1, difference(sd,et), typeOfInput)
            )
        }
    }else if (fixedDurations[index].type == DurationType.TUHR||
        fixedDurations[index].type == DurationType.TUHR_MUBTADIA||
        fixedDurations[index].type==DurationType.TUHR_MUBTADIA_BECAME_A_MUTADA_NOW){
        val time = fixedDurations[index].timeInMilliseconds
        newStr.add(
            baseStr(Strings::durationPaki)
                .replaceDur(Rplc.DUR1, time, typeOfInput)
        )
        if(fixedDurations[index].type == DurationType.TUHR_MUBTADIA_BECAME_A_MUTADA_NOW){
            newStr.addStrings(Strings::becamemutadah)
        }
    }else if (fixedDurations[index].type == DurationType.TUHREFAASID||fixedDurations[index].type==DurationType.TUHREFAASID_MUBTADIA){
        newStr.add(
            baseStr(Strings::durationTuhrefasid)
            .replaceDur(Rplc.DUR1, fixedDurations[index].timeInMilliseconds, typeOfInput)
        )
    }else if (fixedDurations[index].type==DurationType.TUHREFAASID_WITH_ISTEHAZA||
        fixedDurations[index].type==DurationType.TUHREFAASID_MUBTADIA_WITH_ISTEHAZA){
        newStr.add(
            baseStr(Strings::durationTuhreFasidWithAddition)
            .replaceDur(Rplc.DUR1, fixedDurations[index].istihazaAfter, typeOfInput)
            .replaceDur(Rplc.DUR2, (fixedDurations[index].istihazaAfter+fixedDurations[index].timeInMilliseconds), typeOfInput)
        )
    }else if (fixedDurations[index].type == DurationType.DAM_IN_NIFAS_PERIOD){
        val sd = fixedDurations[index].startDate
        val et = fixedDurations[index].endDate
        if(fixedDurations[index].days<=40){
            newStr.add(
                baseStr(Strings::durationNifas)
                    .replaceDur(Rplc.DUR1, difference(sd,et), typeOfInput)
            )
        }else{//more than 40
            newStr.add(
                baseStr(Strings::durationDam)
                    .replaceDur(Rplc.DUR1, fixedDurations[index].timeInMilliseconds, typeOfInput)
            )
        }
    }else if (fixedDurations[index].type == DurationType.TUHR_IN_HAML){
        if(!isMustabeen){
            val time = fixedDurations[index].timeInMilliseconds
            newStr.add(
                baseStr(Strings::durationPaki)
                    .replaceDur(Rplc.DUR1, time, typeOfInput)
            )
        }
    }else if (fixedDurations[index].type == DurationType.TUHREFAASID_IN_HAML){
        if(!isMustabeen){
            newStr.add(
                baseStr(Strings::durationTuhreFasidWithAddition)
                    .replaceDur(Rplc.DUR1, fixedDurations[index].istihazaAfter, typeOfInput)
                .replaceDur(Rplc.DUR2, fixedDurations[index].timeInMilliseconds, typeOfInput)
                .replaceDur(Rplc.DUR2, (fixedDurations[index].istihazaAfter+fixedDurations[index].timeInMilliseconds), typeOfInput)
            )
        }
    }else if (fixedDurations[index].type == DurationType.DAM_IN_HAML){
        println(Rplc.Msg.PLACEHOLDER)
    }else if (fixedDurations[index].type == DurationType.TUHR_BIGGER_THAN_6_MONTHS){
        newStr.add(
            baseStr(Strings::twomonthstuhr)
                .replaceDur(Rplc.DUR1, fixedDurations[index].timeInMilliseconds, typeOfInput)
        )
    }else if(fixedDurations[index].type == DurationType.ISTEHAZA_AYYAMEQABLIYYA){
        println(Rplc.Msg.PLACEHOLDER)
    }
    return newStr
}
fun outputStringHeaderLine(fixedDurations: MutableList<FixedDuration>,
                           index: Int,
                           typeOfInput: TypesOfInputs,
                           timeZone: String,
                           mustabeen:Boolean = true):OutputStringsLanguages{
    val newStr = OutputStringsLanguages()
    if (fixedDurations[index].type==DurationType.DAM||
        fixedDurations[index].type==DurationType.DAM_MUBTADIA){
        val sd:Instant = fixedDurations[index].startDate
        val et = fixedDurations[index].endDate
        if(fixedDurations[index].days in 3.0..10.0){//if it's between 3 and 10, write haiz
            newStr.add(
                baseStr(Strings::haizdays)
                    .replaceDT(Rplc.DT1, sd, typeOfInput, timeZone)
                .replaceDT(Rplc.DT2, et, typeOfInput, timeZone)
                .replaceDur(Rplc.DUR1, (difference(sd,et)), typeOfInput)
            )
        }else{//bigger than 10
            if (fixedDurations[index].indices.size>1){//this dam is made up of more than 1
                newStr.add(
                    baseStr(Strings::continuosbleeding)
                        .replaceDT(Rplc.DT1, sd, typeOfInput, timeZone)
                    .replaceDT(Rplc.DT2, et, typeOfInput, timeZone)
                    .replaceDur(Rplc.DUR1, fixedDurations[index].timeInMilliseconds, typeOfInput)
                )
            }else{
                newStr.add(
                    baseStr(Strings::blooddays)
                        .replaceDT(Rplc.DT1, sd, typeOfInput, timeZone)
                    .replaceDT(Rplc.DT2, et, typeOfInput, timeZone)
                    .replaceDur(Rplc.DUR1, fixedDurations[index].timeInMilliseconds, typeOfInput)
                )
            }
        }
    }else if (fixedDurations[index].type == DurationType.TUHR||
        fixedDurations[index].type == DurationType.TUHR_MUBTADIA||
        fixedDurations[index].type==DurationType.TUHR_MUBTADIA_BECAME_A_MUTADA_NOW){
        val time = fixedDurations[index].timeInMilliseconds
        newStr.add(
            baseStr(Strings::pakidays)
                .replaceDur(Rplc.DUR1, time, typeOfInput)
        )
        if(fixedDurations[index].type == DurationType.TUHR_MUBTADIA_BECAME_A_MUTADA_NOW){
            newStr.addStrings(Strings::becamemutadah)
        }
    }else if (fixedDurations[index].type == DurationType.TUHREFAASID||fixedDurations[index].type==DurationType.TUHREFAASID_MUBTADIA){
        newStr.add(
            baseStr(Strings::tuhrfasid)
                .replaceDur(Rplc.DUR1, fixedDurations[index].timeInMilliseconds, typeOfInput)
        )
    }else if (fixedDurations[index].type==DurationType.TUHREFAASID_WITH_ISTEHAZA||
        fixedDurations[index].type==DurationType.TUHREFAASID_MUBTADIA_WITH_ISTEHAZA){
        newStr.add(
            baseStr(Strings::tuhrfasidwithaddition)
                .replaceDur(Rplc.DUR1, fixedDurations[index].istihazaAfter, typeOfInput)
            .replaceDur(Rplc.DUR2, fixedDurations[index].timeInMilliseconds, typeOfInput)
            .replaceDur(Rplc.DUR3, (fixedDurations[index].istihazaAfter+fixedDurations[index].timeInMilliseconds), typeOfInput)
        )
    }else if (fixedDurations[index].type == DurationType.DAM_IN_NIFAS_PERIOD){
        val sd = fixedDurations[index].startDate
        val et = fixedDurations[index].endDate
        if(fixedDurations[index].days<=40){
            newStr.add(
                baseStr(Strings::nifasdays)
                    .replaceDT(Rplc.DT1, sd, typeOfInput, timeZone)
                .replaceDT(Rplc.DT2, et, typeOfInput, timeZone)
                .replaceDur(Rplc.DUR1, difference(sd,et), typeOfInput)
            )
        }else{//more than 40
            newStr.add(
                baseStr(Strings::blooddays)
                    .replaceDT(Rplc.DT1, sd, typeOfInput, timeZone)
                .replaceDT(Rplc.DT2, et, typeOfInput, timeZone)
                .replaceDur(Rplc.DUR1, fixedDurations[index].timeInMilliseconds, typeOfInput)
            )
        }
    }else if (fixedDurations[index].type == DurationType.TUHR_IN_HAML){
        if(!mustabeen){
            val time = fixedDurations[index].timeInMilliseconds
            newStr.add(
                baseStr(Strings::pakidays)
                    .replaceDur(Rplc.DUR1, time, typeOfInput)
            )
        }
    }else if (fixedDurations[index].type == DurationType.TUHREFAASID_IN_HAML){
        if(!mustabeen){
            newStr.add(
                baseStr(Strings::tuhrfasidwithaddition)
                .replaceDur(Rplc.DUR1, fixedDurations[index].istihazaAfter, typeOfInput)
                .replaceDur(Rplc.DUR2, fixedDurations[index].timeInMilliseconds, typeOfInput)
                .replaceDur(Rplc.DUR2, (fixedDurations[index].istihazaAfter+fixedDurations[index].timeInMilliseconds), typeOfInput)
            )
        }
    }else if (fixedDurations[index].type == DurationType.DAM_IN_HAML){
        println(Rplc.Msg.PLACEHOLDER)
    }else if (fixedDurations[index].type == DurationType.TUHR_BIGGER_THAN_6_MONTHS){
        newStr.add(
            baseStr(Strings::twomonthstuhr)
                .replaceDur(Rplc.DUR1, fixedDurations[index].timeInMilliseconds, typeOfInput)
        )
    }else if (fixedDurations[index].type == DurationType.ISTEHAZA_AYYAMEQABLIYYA){
        val sd = fixedDurations[index].startDate
        val et = fixedDurations[index].endDate
        newStr.add(
            baseStr(Strings::daysayyameqabliyya)
                .replaceDT(Rplc.DT1, sd, typeOfInput, timeZone)
            .replaceDT(Rplc.DT2, et, typeOfInput, timeZone)
            .replaceDur(Rplc.DUR1, (difference(sd,et)), typeOfInput)
        )
    }
    return newStr
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




