@file:Suppress("SpellCheckingInspection")

import kotlinx.browser.window
import kotlinx.datetime.internal.JSJoda.Instant

//output line 1 is indices of all the things durations that make up the sum of this thingy.
// we should only write it, if there is more than one index
//output line 2 is printing if an istihaza after was added too. it contains amount of istihazaAfter
//output line 3 is mp,gp, dm, hz, soorat, as well as istihazaBefore, Haiz, IstihazaAfter, and AadatHaiz/AaadatTuhr
// at the end of it.b
//output line 3, can be used to generate the daur lines.
//After this should come output in dates:
//if we passed dateTime at the start of this thingy, we could use
// istihazaBefore, Haiz, and IstihazaAfter to generate them
// and generate daur too

lateinit var firstStartTime:Instant

fun handleEntries(allTheInputs: AllTheInputs): OutputTexts {
    firstStartTime = allTheInputs.entries!![0].startTime
    val times = allTheInputs.entries
        .flatMap { entry -> listOf(entry.startTime, entry.endTime) }
        .map { it.getMillisLong() }
    require(times == times.sorted())
    //step 1 - create an array of dam and tuhur durations
    var isDam = true
    val durations = times.zipWithNext { firstTime, secondTime ->
        val type = if (isDam) DurationType.DAM else DurationType.TUHR
        isDam = !isDam
        Duration(type, secondTime - firstTime, Instant.ofEpochMilli(firstTime))
    }
    val fixedDurations = durations
        .map { duration ->
            FixedDuration(duration.type, duration.timeInMilliseconds,startDate = duration.startTime)
        }
        .toMutableList()

    addIndicesToFixedDurations(fixedDurations)

    val adatsOfHaizList = mutableListOf<AadatAfterIndexOfFixedDuration>()
    val adatsOfTuhrList = mutableListOf<AadatAfterIndexOfFixedDuration>()

    adatsOfHaizList +=AadatAfterIndexOfFixedDuration(-1,-1)
    adatsOfTuhrList +=AadatAfterIndexOfFixedDuration(-1,-1)

    if(allTheInputs.typeOfInput==TypesOfInputs.DURATION){
        // this is because ayyam-e-qabliyya cannot apply to a past masla, and durations is all past maslas
        allTheInputs.ikhtilaafaat.ayyameQabliyyaIkhtilaf = true //so we will turn off ayyam-e-qabliyya
    }

    return when (allTheInputs.typeOfMasla) {
        TypesOfMasla.NIFAS -> {
            handleNifas(allTheInputs, fixedDurations, adatsOfHaizList, adatsOfTuhrList)
        }
        TypesOfMasla.MUBTADIA -> {
            handleMubtadia(allTheInputs,fixedDurations,adatsOfHaizList,adatsOfTuhrList)
        }
        else -> {//is mutadah
            handleMutadah(allTheInputs, fixedDurations,adatsOfHaizList,adatsOfTuhrList)
        }
    }
}
fun handleMubtadia(allTheInputs: AllTheInputs,
                   fixedDurations: MutableList<FixedDuration>,
                   adatsOfHaizList: MutableList<AadatAfterIndexOfFixedDuration>,
                   adatsOfTuhrList: MutableList<AadatAfterIndexOfFixedDuration>): OutputTexts {
    removeTuhrLessThan15(fixedDurations)
    val newMawjoodaPakiValues = removeDamLessThan3(fixedDurations, allTheInputs.preMaslaValues.inputtedMawjoodahTuhr?:0L, allTheInputs.preMaslaValues.isMawjoodaFasid)
    allTheInputs.preMaslaValues.inputtedMawjoodahTuhr=newMawjoodaPakiValues.inputtedMawjoodahTuhr
    allTheInputs.preMaslaValues.isMawjoodaFasid=newMawjoodaPakiValues.isMawjoodaFasid
    addStartDateToFixedDurations(fixedDurations)
    if(!allTheInputs.ikhtilaafaat.mubtadiaIkhitilaf){
        val aadats = dealWithMubtadiaDam(
            fixedDurations,
            adatsOfHaizList,
            adatsOfTuhrList,
            allTheInputs.ikhtilaafaat,
            allTheInputs.preMaslaValues,
            allTheInputs.language
        ) ?: return NO_OUTPUT
        markAllMubtadiaDamsAndTuhrsAsMubtadia(fixedDurations, allTheInputs.ikhtilaafaat.mubtadiaIkhitilaf)
        //if we got aadats, then we run this portion
        if (aadats.aadatHaiz!=-1L && aadats.aadatTuhr!=-1L){
            dealWithBiggerThan10Dam(
                fixedDurations,
                PreMaslaValues(aadats.aadatHaiz,aadats.aadatTuhr, aadats.aadatTuhr),
                allTheInputs.language,
                adatsOfHaizList,
                adatsOfTuhrList,
                allTheInputs.ikhtilaafaat.daurHaizIkhtilaf
            )
        }
    }else{//mubtadia ikhtilaf is on, solve masla as a mutadah
        dealWithBiggerThan10Dam(
            fixedDurations,
            PreMaslaValues(10.getMilliDays(),
                20.getMilliDays(),
                20.getMilliDays()),
            allTheInputs.language,
            adatsOfHaizList,
            adatsOfTuhrList,
            allTheInputs.ikhtilaafaat.daurHaizIkhtilaf
        )
    }
    addDurationsToDams(fixedDurations, allTheInputs.ikhtilaafaat.daurHaizIkhtilaf, allTheInputs.typeOfInput)
    checkForAyyameQabliyya(fixedDurations,
        adatsOfHaizList,
        adatsOfTuhrList,
        allTheInputs.preMaslaValues.inputtedMawjoodahTuhr,
        allTheInputs.ikhtilaafaat.ayyameQabliyyaIkhtilaf)
    val endingOutputValues = calculateEndingOutputValues(fixedDurations,
        allTheInputs.preMaslaValues,
        adatsOfHaizList,
        adatsOfTuhrList, -1L, typesOfMasla = TypesOfMasla.MUBTADIA)
    putMawjoodahPakiInFixedDurations(fixedDurations, allTheInputs)
    return generateOutputStringMubtadia(fixedDurations,
        endingOutputValues,
        allTheInputs.typeOfInput,
        allTheInputs.preMaslaValues,
        allTheInputs.timeZone ?: "UTC")

}
fun handleNifas(allTheInputs: AllTheInputs,
                fixedDurations: MutableList<FixedDuration>,
                adatsOfHaizList: MutableList<AadatAfterIndexOfFixedDuration>,
                adatsOfTuhrList: MutableList<AadatAfterIndexOfFixedDuration>): OutputTexts {
    addStartDateToFixedDurations(fixedDurations)
    markAllTuhrsInPregnancyAsHaml(fixedDurations, allTheInputs.pregnancy!!, allTheInputs.ikhtilaafaat.ghairMustabeenIkhtilaaf)
    //the above also added start of pregnancy

    return if(allTheInputs.pregnancy.mustabeenUlKhilqat){
        handleMustabeenUlKhilqa(allTheInputs,fixedDurations,adatsOfHaizList,adatsOfTuhrList)
    }else{
        handleGhairMustabeenUlKhilqa(allTheInputs,fixedDurations,adatsOfHaizList,adatsOfTuhrList)
    }

}
fun handleGhairMustabeenUlKhilqa(allTheInputs: AllTheInputs, //isqaat
                                 fixedDurations: MutableList<FixedDuration>,
                                 adatsOfHaizList: MutableList<AadatAfterIndexOfFixedDuration>,
                                 adatsOfTuhrList: MutableList<AadatAfterIndexOfFixedDuration>): OutputTexts {
    //if it's not mustabeen ulkhilqat, deal with it like haiz
    removeTuhrLessThan15(fixedDurations)
    removeTuhrLessThan15InPregnancy(fixedDurations)
    val newMawjoodaPakiValues = removeDamLessThan3(
        fixedDurations,
        allTheInputs.preMaslaValues.inputtedMawjoodahTuhr?:0L,
        allTheInputs.preMaslaValues.isMawjoodaFasid)
    allTheInputs.preMaslaValues.inputtedMawjoodahTuhr=newMawjoodaPakiValues.inputtedMawjoodahTuhr
    allTheInputs.preMaslaValues.isMawjoodaFasid=newMawjoodaPakiValues.isMawjoodaFasid
    addStartDateToFixedDurations(fixedDurations)
    val mawjoodahIsNotAadat = checkIfMawjoodahPakiIsTuhrInHaml(
        fixedDurations,
        allTheInputs.pregnancy!!,
        allTheInputs.preMaslaValues.inputtedMawjoodahTuhr,
        allTheInputs.preMaslaValues.isMawjoodaFasid,
        allTheInputs.ikhtilaafaat.ghairMustabeenIkhtilaaf
    )
    if(!dealWithBiggerThan10Dam(
            fixedDurations,
            PreMaslaValues(
                allTheInputs.preMaslaValues.inputtedAadatHaiz,
                allTheInputs.preMaslaValues.inputtedAadatTuhr,
                allTheInputs.preMaslaValues.inputtedMawjoodahTuhr,
                mawjoodahIsNotAadat),
            allTheInputs.language,
            adatsOfHaizList,
            adatsOfTuhrList,
            allTheInputs.ikhtilaafaat.daurHaizIkhtilaf
        )){return NO_OUTPUT}
    addDurationsToDams(fixedDurations, allTheInputs.ikhtilaafaat.daurHaizIkhtilaf, allTheInputs.typeOfInput)
    checkForAyyameQabliyya(fixedDurations,
        adatsOfHaizList,
        adatsOfTuhrList,
        allTheInputs.preMaslaValues.inputtedMawjoodahTuhr,
        allTheInputs.ikhtilaafaat.ayyameQabliyyaIkhtilaf)
    addStartOfPregnancy(fixedDurations, allTheInputs.pregnancy)
    addWiladat(fixedDurations, allTheInputs.pregnancy)
    val endingOutputValues = calculateEndingOutputValues(fixedDurations,
        PreMaslaValues( null,
            allTheInputs.preMaslaValues.inputtedAadatTuhr,
            allTheInputs.preMaslaValues.inputtedMawjoodahTuhr,
            mawjoodahIsNotAadat),
        adatsOfHaizList,
        adatsOfTuhrList,
        -1L,
        typesOfMasla = TypesOfMasla.NIFAS)
    putMawjoodahPakiInFixedDurations(fixedDurations, allTheInputs)
    return generateOutputStringPregnancy(fixedDurations,
        allTheInputs.pregnancy,
        endingOutputValues,
        allTheInputs.typeOfInput,
        allTheInputs.timeZone ?: "UTC")

}
fun handleMustabeenUlKhilqa(allTheInputs: AllTheInputs, //wiladat
                            fixedDurations: MutableList<FixedDuration>,
                            adatsOfHaizList: MutableList<AadatAfterIndexOfFixedDuration>,
                            adatsOfTuhrList: MutableList<AadatAfterIndexOfFixedDuration>):OutputTexts{
    //mark all dam in pregnancy as isithaza.
    markAllDamsInPregnancyAsHaml(fixedDurations, allTheInputs.pregnancy!!)
    removeTuhrLessThan15(fixedDurations)//do this before the next, cuz why not, mkes thigns simpler in joining dams
    addStartDateToFixedDurations(fixedDurations)//cuz the last shoulda messed it up
    makeAllDamInFortyAfterWiladatAsMuttasil(fixedDurations,allTheInputs.pregnancy) //also, marking them as Dam in
    val newNifasAadat = dealWithDamInMuddateNifas(fixedDurations,allTheInputs.pregnancy, allTheInputs.language)
        ?: return NO_OUTPUT

    val newMawjoodaPakiValues = removeDamLessThan3(
        fixedDurations,
        allTheInputs.preMaslaValues.inputtedMawjoodahTuhr?:0L,
        allTheInputs.preMaslaValues.isMawjoodaFasid
    )//this won't affect dam in haml
    allTheInputs.preMaslaValues.inputtedMawjoodahTuhr=newMawjoodaPakiValues.inputtedMawjoodahTuhr
    allTheInputs.preMaslaValues.isMawjoodaFasid=newMawjoodaPakiValues.isMawjoodaFasid
    addStartDateToFixedDurations(fixedDurations)
    if(!dealWithBiggerThan10Dam(
            fixedDurations,
            allTheInputs.preMaslaValues,
            allTheInputs.language,
            adatsOfHaizList,
            adatsOfTuhrList,
            allTheInputs.ikhtilaafaat.daurHaizIkhtilaf
        )){return NO_OUTPUT}
    addDurationsToDams(fixedDurations, allTheInputs.ikhtilaafaat.daurHaizIkhtilaf, allTheInputs.typeOfInput)
    checkForAyyameQabliyya(fixedDurations,
        adatsOfHaizList,
        adatsOfTuhrList,allTheInputs.preMaslaValues.inputtedMawjoodahTuhr,
        allTheInputs.ikhtilaafaat.ayyameQabliyyaIkhtilaf)
    addStartOfPregnancy(fixedDurations, allTheInputs.pregnancy)
    addWiladat(fixedDurations, allTheInputs.pregnancy)
    val endingOutputValues = calculateEndingOutputValues(fixedDurations,
        allTheInputs.preMaslaValues,
        adatsOfHaizList,
        adatsOfTuhrList,
        allTheInputs.pregnancy.aadatNifas, newNifasAadat, TypesOfMasla.NIFAS)
    putMawjoodahPakiInFixedDurations(fixedDurations, allTheInputs)

    return generateOutputStringPregnancy(fixedDurations,
        allTheInputs.pregnancy,
        endingOutputValues,
        allTheInputs.typeOfInput,
        allTheInputs.timeZone ?: "UTC")

}
fun handleMutadah(allTheInputs: AllTheInputs,
                  fixedDurations: MutableList<FixedDuration>,
                  adatsOfHaizList: MutableList<AadatAfterIndexOfFixedDuration>,
                  adatsOfTuhrList: MutableList<AadatAfterIndexOfFixedDuration>):OutputTexts{
    removeTuhrLessThan15(fixedDurations)
    val newMawjoodaPakiValues = removeDamLessThan3(
        fixedDurations,
        allTheInputs.preMaslaValues.inputtedMawjoodahTuhr?:0L,
        allTheInputs.preMaslaValues.isMawjoodaFasid
    )
    allTheInputs.preMaslaValues.inputtedMawjoodahTuhr=newMawjoodaPakiValues.inputtedMawjoodahTuhr
    allTheInputs.preMaslaValues.isMawjoodaFasid=newMawjoodaPakiValues.isMawjoodaFasid
    addStartDateToFixedDurations(fixedDurations)
    if(!dealWithBiggerThan10Dam(
            fixedDurations,
            allTheInputs.preMaslaValues,
            allTheInputs.language,
            adatsOfHaizList,
            adatsOfTuhrList,
            allTheInputs.ikhtilaafaat.daurHaizIkhtilaf
        )){return NO_OUTPUT}
    addDurationsToDams(fixedDurations, allTheInputs.ikhtilaafaat.daurHaizIkhtilaf, allTheInputs.typeOfInput)
    checkForAyyameQabliyya(fixedDurations,
        adatsOfHaizList,
        adatsOfTuhrList,
        allTheInputs.preMaslaValues.inputtedMawjoodahTuhr,
        allTheInputs.ikhtilaafaat.ayyameQabliyyaIkhtilaf)

    val endingOutputValues = calculateEndingOutputValues(fixedDurations,
        allTheInputs.preMaslaValues,
        adatsOfHaizList,
        adatsOfTuhrList,
        -1L, typesOfMasla = TypesOfMasla.MUTADAH)
    putMawjoodahPakiInFixedDurations(fixedDurations, allTheInputs)
    return generateOutputStringMutadah(fixedDurations,
        endingOutputValues,
        allTheInputs.typeOfInput,
        allTheInputs.preMaslaValues,
        allTheInputs.timeZone ?: "UTC")

}

fun checkIfMawjoodahPakiIsTuhrInHaml(fixedDurations:MutableList<FixedDuration>,
                                     pregnancy:Pregnancy,
                                     inputtedMawjoodaTuhr:Long?,
                                     isMawjoodaFasid:Boolean,
                                     isTuhrInHamlAadatInGhairMustabeenIkhtilaf:Boolean):Boolean{
    if(isMawjoodaFasid){
        return true
    }
    else if(inputtedMawjoodaTuhr == null){
        return false
    }
    else if(isTuhrInHamlAadatInGhairMustabeenIkhtilaf){
        return false
    }
    else{
        val pregStartTime = pregnancy.pregStartTime
        val pregEndTime = pregnancy.birthTime
        val mawjoodahPakiEndDate = fixedDurations.first().startDate
        val mawjoodahPakiStartDate = addTimeToDate(mawjoodahPakiEndDate, -inputtedMawjoodaTuhr)
        if((mawjoodahPakiEndDate.getMillisLong()>pregStartTime.getMillisLong()&&
                    mawjoodahPakiEndDate.getMillisLong()<=pregEndTime.getMillisLong())||
           (mawjoodahPakiStartDate.getMillisLong()>=pregStartTime.getMillisLong()&&
                   mawjoodahPakiStartDate.getMillisLong()<pregEndTime.getMillisLong())||
           (mawjoodahPakiStartDate.getMillisLong()<=pregStartTime.getMillisLong()&&
                   mawjoodahPakiEndDate.getMillisLong()>=pregEndTime.getMillisLong())) {
            //either mawjoodah paki start or end date is during preg, or the 2 dates are on either side of preg
            return true
        }
    }
    return false
}

fun markAllMubtadiaDamsAndTuhrsAsMubtadia(fixedDurations:MutableList<FixedDuration>, mubtadiaIkhtilaf: Boolean){
    for(fixedDuration in fixedDurations) {
        if(mubtadiaIkhtilaf){//mubtadia is over after first bigger THAN 10
            if(fixedDuration.type == DurationType.DAM ) {
                fixedDuration.type = DurationType.DAM_MUBTADIA
                if(fixedDuration.days>10){
                    return
                }
            }else if(fixedDuration.type==DurationType.TUHR) {
                fixedDuration.type = DurationType.TUHR_MUBTADIA
            }else if(fixedDuration.type==DurationType.TUHREFAASID) {
                fixedDuration.type = DurationType.TUHREFAASID_MUBTADIA
            }else {
                return
            }
        }else{
            when (fixedDuration.type) {
                DurationType.DAM -> fixedDuration.type = DurationType.DAM_MUBTADIA
                DurationType.TUHR -> fixedDuration.type = DurationType.TUHR_MUBTADIA
                DurationType.TUHREFAASID -> fixedDuration.type = DurationType.TUHREFAASID_MUBTADIA
                DurationType.TUHR_MUBTADIA_BECAME_A_MUTADA_NOW -> return
                else -> return
            }
        }
    }
}
fun dealWithMubtadiaDam(fixedDurations:MutableList<FixedDuration>,
                        adatsOfHaizList: MutableList<AadatAfterIndexOfFixedDuration>,
                        adatsOfTuhrList: MutableList<AadatAfterIndexOfFixedDuration>,
                        ikhtilaafaat: Ikhtilaafaat,
                        preMaslaValues: PreMaslaValues,
                        language: String,
):AadatsOfHaizAndTuhr?{
    //this is not in case of pregnancy
    //the job of this function is to just tell how much of it from the start is istehaza,
    // how much is haiz, and what the aadat at the end of this is
    //if we get an aadat, we return true, otherwise return false
    val endOfDaurIkhtilaf=ikhtilaafaat.daurHaizIkhtilaf
    val mubtadiaIkhtilaf=ikhtilaafaat.mubtadiaIkhitilaf
    val inputtedAadatHaz = preMaslaValues.inputtedAadatHaiz
    val inputtedMawjoodaTuhr = preMaslaValues.inputtedMawjoodahTuhr



    var aadatHaz:Long = -1L
    var aadatTuhr:Long = -1L
    var iztirariAadatHaiz:Long = 10.getMilliDays()
    var iztirariAadatTuhr:Long = 20.getMilliDays()
    if(inputtedAadatHaz!=null){
        aadatHaz=inputtedAadatHaz
        adatsOfHaizList+=AadatAfterIndexOfFixedDuration(aadatHaz,-1)
        iztirariAadatHaiz = aadatHaz
        iztirariAadatTuhr = 30.getMilliDays() - iztirariAadatHaiz
    }



    var i = 0
    while (i<fixedDurations.size){
        //iterate through the dimaa
        if(fixedDurations[i].type==DurationType.DAM &&
            fixedDurations[i].days<=10 &&
            fixedDurations[i].days>=3){
            //we have a haiz aadat!
            aadatHaz = fixedDurations[i].timeInMilliseconds
            adatsOfHaizList+=AadatAfterIndexOfFixedDuration(aadatHaz,i)
            //change iztirari aadat of Tuhr
            iztirariAadatTuhr = 30.getMilliDays() - aadatHaz
            iztirariAadatHaiz = aadatHaz

            //check if next Tuhr is saheeh. if it is, declare it aadat
            if(i<fixedDurations.size-1 && fixedDurations[i+1].type==DurationType.TUHR){
                aadatTuhr=fixedDurations[i+1].timeInMilliseconds
                //once we have a tuhr habit too. so we have a mutadah, so we should stop now
                //println("Mubtadia Soorat 1: Haiz Sahih, Tuhr Saheeh")
                //return aadat
                adatsOfTuhrList+=AadatAfterIndexOfFixedDuration(aadatTuhr,i)
                fixedDurations[i+1].type = DurationType.TUHR_MUBTADIA_BECAME_A_MUTADA_NOW
                return AadatsOfHaizAndTuhr(aadatHaz,aadatTuhr)
            }
        }else if(fixedDurations[i].type==DurationType.DAM && fixedDurations[i].days>10){//this is where bigger than 10 starts
//            println("dam fasid, tuhr-e-fasid")
            //if there is tuhr behind it, check how long it is
            var istehazaBefore: Long = 0
            var haiz:Long
            var istehazaAfter:Long
            //if there is an addat of tuhr, we would not be in mubtadia anymore, so check iztirari
            var mawjoodahTuhr=-1L
            if(i>0 &&
                (fixedDurations[i-1].type==DurationType.TUHR||
                        fixedDurations[i-1].type==DurationType.TUHREFAASID)){
//                println("2")
//                println("dam-e-fasid tuhr-e-fasid type 1")
                //if there is a duration before this one, and it is either atuhr or a tuhr-e fasid
                //then mawjoodah tuhr will be that + any istihaza after associated with it
                mawjoodahTuhr = fixedDurations[i-1].timeInMilliseconds+fixedDurations[i-1].istihazaAfter
            }else if(inputtedMawjoodaTuhr!=null){
                mawjoodahTuhr=inputtedMawjoodaTuhr
            }
            if(aadatHaz!=-1L&&mawjoodahTuhr==-1L){
                //give error
                if(language==Vls.Langs.ENGLISH){
                    window.alert(StringsOfLanguages.ENGLISH.errorEnterMawjoodaPaki)
                }else if(language==Vls.Langs.URDU){
                    window.alert(StringsOfLanguages.URDU.errorEnterMawjoodaPaki)
                }
                return null
            }


            if(mawjoodahTuhr < iztirariAadatTuhr && mawjoodahTuhr !=-1L){
//                println("3")
//                println("dam-e-fasid tuhr-e-fasid type2")
                //if mawjoodah paki is less than iztirari aadat, then make the difference from the start istehaza
                istehazaBefore = iztirariAadatTuhr-mawjoodahTuhr
            }else{//if either mawjoodah tuhr was long, or it began with a long haiz
                //istehazabefore remains 0. we don't really need this else
//                println("4")

            }
            //follow this by haiz
            if(fixedDurations[i].timeInMilliseconds-istehazaBefore<= iztirariAadatHaiz){//no daur sitch
//                println("5")
                haiz = fixedDurations[i].timeInMilliseconds-istehazaBefore
                //we have an aadat of haiz!!
                //in this case, haiz and aadat of haiz is the same
                aadatHaz = haiz
                adatsOfHaizList+=AadatAfterIndexOfFixedDuration(aadatHaz,i)
                iztirariAadatHaiz = aadatHaz
                //check if the tuhr after this is saheeh
                if(i<fixedDurations.size - 1 && fixedDurations[i+1].type == DurationType.TUHR){
                    aadatTuhr = fixedDurations[i+1].timeInMilliseconds
                    adatsOfTuhrList+=AadatAfterIndexOfFixedDuration(aadatTuhr,i)
                    //we have a mutadah
                    val biggerThanTen = BiggerThanTenDm(0,0,0,0,Soortain.A_1,istehazaBefore, haiz, 0L, haiz, -1, mutableListOf())
                    fixedDurations[i].biggerThanTen = biggerThanTen
                    fixedDurations[i+1].type = DurationType.TUHR_MUBTADIA_BECAME_A_MUTADA_NOW
                    return AadatsOfHaizAndTuhr(aadatHaz,aadatTuhr)
                }else{//no tuhr aadat yet
                    val biggerThanTen = BiggerThanTenDm(0,0,0,0,Soortain.A_1,istehazaBefore, haiz, 0L, haiz, -1L, mutableListOf())
                    fixedDurations[i].biggerThanTen = biggerThanTen
                    iztirariAadatTuhr=30.getMilliDays()-haiz
                }
            }else{//we have an istehaza after sitch
//                println("6")

                haiz = iztirariAadatHaiz
                istehazaAfter = fixedDurations[i].timeInMilliseconds-istehazaBefore-iztirariAadatHaiz

                //now we gotta check if a daur happened
                iztirariAadatHaiz = dealWithIstihazaAfter(istehazaAfter, haiz, iztirariAadatTuhr, fixedDurations, i, endOfDaurIkhtilaf)
//                println("7")
//                println("istehaza after was ${istehazaAfter.daysFromMillis()}")
//                println("deal with istihaza after returned this aadat ${iztirariAadatHaiz.daysFromMillis()} ")

                //now we wanna check if aadat did change.
                //a bit hackish but..
                val remainder = istehazaAfter%(30.getMilliDays())
//                println("remainder was $remainder")
                if(iztirariAadatHaiz<10.getMilliDays() ||
                    (iztirariAadatHaiz==10.getMilliDays() && remainder==10.getMilliDays())){
//                    println("8")

                    //in this case we gotta check further
                    //if iztirari aadat of haiz is 10, the of tuhr is def 20.
                    //so istihazaAfter must be a number that gives a remainder of 10, when divided by 30

                    //adat did change, so do the aadat shtick
                    //we have an aadat of haiz!!
                    aadatHaz = iztirariAadatHaiz
                    adatsOfHaizList+=AadatAfterIndexOfFixedDuration(aadatHaz,i)
                    //check if the tuhr after this is saheeh
                    if(i<fixedDurations.size - 1 && fixedDurations[i+1].type == DurationType.TUHR){
                        aadatTuhr = fixedDurations[i+1].timeInMilliseconds
                        adatsOfTuhrList+=AadatAfterIndexOfFixedDuration(aadatTuhr,i)
                        //we have a mutadah
                        val biggerThanTen = BiggerThanTenDm(0,0,0,0,Soortain.A_1,istehazaBefore, haiz, istehazaAfter, aadatHaz, -1, mutableListOf())
                        fixedDurations[i].biggerThanTen = biggerThanTen
                        fixedDurations[i+1].type = DurationType.TUHR_MUBTADIA_BECAME_A_MUTADA_NOW
                        return AadatsOfHaizAndTuhr(aadatHaz,aadatTuhr)
                    }else{//no adat for tuhr yet
                        val biggerThanTen = BiggerThanTenDm(0,0,0,0,Soortain.A_1,istehazaBefore, haiz, istehazaAfter, aadatHaz, -1L, mutableListOf())
                        fixedDurations[i].biggerThanTen = biggerThanTen
                        iztirariAadatTuhr=30.getMilliDays()-aadatHaz
                    }
                }
                val biggerThanTen = BiggerThanTenDm(0,0,0,0,Soortain.A_1,istehazaBefore, haiz, istehazaAfter, aadatHaz, -1L, mutableListOf())
                fixedDurations[i].biggerThanTen = biggerThanTen
                if(mubtadiaIkhtilaf){
                    return AadatsOfHaizAndTuhr(iztirariAadatHaiz,iztirariAadatTuhr)
                }
//                println("9")

            }
        }
        i++
    }
    return AadatsOfHaizAndTuhr(aadatHaz,aadatTuhr)
}

fun dealWithDamInMuddateNifas(fixedDurations:MutableList<FixedDuration>,pregnancy:Pregnancy, language: String):Long?{
    //this function returns the aadat of nifas
    //if it retyrns null, that means there was an error
    var i = 0
    while (i<fixedDurations.size){
        if(fixedDurations[i].type==DurationType.DAM_IN_NIFAS_PERIOD){
            if(fixedDurations[i].timeInMilliseconds > 40.getMilliDays()){
                //if nifas exceeded 40
                if(pregnancy.aadatNifas==null){
                    //give error
                    if(language==Vls.Langs.ENGLISH){
                        window.alert(StringsOfLanguages.ENGLISH.errorEnterNifasAadat)
                    }else if(language==Vls.Langs.URDU){
                        window.alert(StringsOfLanguages.URDU.errorEnterNifasAadat)
                    }
                    pregnancy.aadatNifas=-1
                    return null
                }
                val istihazaAfter = fixedDurations[i].timeInMilliseconds-pregnancy.aadatNifas!!
                val nifasInfo = BiggerThanFortyNifas(
                    pregnancy.aadatNifas!!, istihazaAfter,-1, -1,
                    -1, mutableListOf())
                fixedDurations[i].biggerThanForty=nifasInfo
                //the rest of this is dealt with in bigger than 10
                //break it up into dam and tuhr?
                //maybe do that later in bigger than 10
                //as that is the only way to get aadat.
                return pregnancy.aadatNifas
            }else{//it is 40 or less
                //do update aadat
                return fixedDurations[i].timeInMilliseconds
            }
        }else if(i == fixedDurations.lastIndex){//there is no reported bleeding after wiladat
            //technically, she may have an aadat of 0? this is worth checking, but for us,
            // dealing with real world cases, we will assume that her bleeding after wiladat
            // just hasn't finished yet, so hasn't been entered. so her previous aadat still holds.
            return pregnancy.aadatNifas
        }
        i++
    }
    return null
}

fun makeAllDamInFortyAfterWiladatAsMuttasil(fixedDurations:MutableList<FixedDuration>,pregnancy:Pregnancy){
    val birthDate:Long = pregnancy.birthTime.getMillisLong()
    val fortyPlusBD = birthDate+(40.getMilliDays())
    var i = 0
    while (i<fixedDurations.size){
        val startTime = fixedDurations[i].startDate.getMillisLong()
        if(startTime in birthDate..fortyPlusBD &&
                fixedDurations[i].type == DurationType.DAM){
            //if a dam starts after or at birth, and before or at 40
            //then check the dam before it if it exists, is it in nifas period.
            if(i>1 && fixedDurations[i-2].type==DurationType.DAM_IN_NIFAS_PERIOD){
                //if it is, then extend last Dam to cover this one
                fixedDurations[i-2].timeInMilliseconds += fixedDurations[i-1].timeInMilliseconds + fixedDurations[i].timeInMilliseconds
                fixedDurations[i-2].indices.addAll(fixedDurations[i-1].indices)
                fixedDurations[i-2].indices.addAll(fixedDurations[i].indices)
                fixedDurations.removeAt(i-1)
                fixedDurations.removeAt(i-1)
                i -= 2
            }else{//there is no dam before this in nifas period
                val newDuration:Long = startTime - birthDate
                fixedDurations[i].startDate=pregnancy.birthTime
                fixedDurations[i].timeInMilliseconds += newDuration
                fixedDurations[i].type = DurationType.DAM_IN_NIFAS_PERIOD
                //since we added time to this one, we gotta subtract it from the one before.
                //if it exists
                if(i>0){
                    fixedDurations[i-1].timeInMilliseconds-=newDuration
                }
            }
        }
        if(startTime>fortyPlusBD){
            break
        }
        i++
    }
}


fun markAllTuhrsInPregnancyAsHaml(fixedDurations: MutableList<FixedDuration>,
                                  pregnancy:Pregnancy,
                                  isTuhrInHamlAadatInGhairMustabeenIkhtilaf:Boolean) {
    for (i in fixedDurations.indices){
        val endDateOfFixedDuration = fixedDurations[i].endDate
        if(fixedDurations[i].type == DurationType.TUHR &&
            fixedDurations[i].startDate.getMillisLong() <= pregnancy.birthTime.getMillisLong() &&
            endDateOfFixedDuration.getMillisLong() >= pregnancy.pregStartTime.getMillisLong()){
            if(isTuhrInHamlAadatInGhairMustabeenIkhtilaf && !pregnancy.mustabeenUlKhilqat){
                //in non mustabeen ulkhilqah, when the ikhtilafi masla is on, do not mark tuhr as tuhr in haml
            }else{
                fixedDurations[i].type = DurationType.TUHR_IN_HAML
            }
        }
    }
}

fun markAllDamsInPregnancyAsHaml(fixedDurations: MutableList<FixedDuration>, pregnancy:Pregnancy){
    var i =0
    val startDateOfHaml = pregnancy.pregStartTime.getMillisLong()
    val endDateOfHaml = pregnancy.birthTime.getMillisLong()

    while(i<fixedDurations.size){
        val endDateOfFixedDuration = fixedDurations[i].endDate

        //this dam started before pregnancy, ends in the middle of pregnancy
        //  ---(pregnancy---  birth)
        if(fixedDurations[i].type == DurationType.DAM &&
                fixedDurations[i].startDate.getMillisLong()<startDateOfHaml &&
                endDateOfFixedDuration.getMillisLong()>startDateOfHaml &&
                endDateOfFixedDuration.getMillisLong()<=endDateOfHaml){
            //mark the portion in pregnancy as dam in haml. we're gonna have to make more dam???
            //or, we could just shorten it to prepregnancy stae, and leave it as is. who cares about dam in haml?
            //we can even put it in istihazaAfter.
            //maybe that's a bad idea, as it could trigger daur...
            //for now, we are just shortening it.
            val newDuration = startDateOfHaml - fixedDurations[i].startDate.getMillisLong()
            val timeInHaml = fixedDurations[i].timeInMilliseconds-newDuration
            fixedDurations[i].timeInMilliseconds = newDuration
            //maybe we really should leave an istihaz after here????
            //we gotta figure out what to do with indices here
            val newFixedDuration = FixedDuration(DurationType.DAM_IN_HAML,timeInHaml, startDate=addTimeToDate(fixedDurations[i].startDate, newDuration))
            fixedDurations.add(i+1, newFixedDuration)
        }
        //this started in the middle, ended in the middle of it
        //  (pregnancy ---- birth)
        if(fixedDurations[i].type == DurationType.DAM &&
                    endDateOfFixedDuration.getMillisLong() <= endDateOfHaml &&
                    fixedDurations[i].startDate.getMillisLong() >= startDateOfHaml
            ){

            //mark it as dam in haml, aka, istihaza.
            fixedDurations[i].type = DurationType.DAM_IN_HAML
        }
        //this starts in the middle of pregnancy, ends after it.
        // (pregnancy   ---birth)---
        if(fixedDurations[i].type == DurationType.DAM &&
                    fixedDurations[i].startDate.getMillisLong()<endDateOfHaml &&
                    fixedDurations[i].startDate.getMillisLong()>=startDateOfHaml &&
                    endDateOfFixedDuration.getMillisLong()>endDateOfHaml
                ){

            val firstDuration = pregnancy.birthTime.getMillisLong() - fixedDurations[i].startDate.getMillisLong()
            val secondDuration = fixedDurations[i].timeInMilliseconds-firstDuration
            val secondFixedDuration = FixedDuration(DurationType.DAM,secondDuration, startDate = pregnancy.birthTime)
            fixedDurations.add(i+1,secondFixedDuration)
            fixedDurations[i].timeInMilliseconds = firstDuration
            fixedDurations[i].type=DurationType.DAM_IN_HAML

        }
        //this started before pregnancy began, ends after pregnancy ended
        //  ---(pregnancy----birth)---
        if(fixedDurations[i].type == DurationType.DAM &&
                fixedDurations[i].startDate.getMillisLong()<startDateOfHaml &&
                endDateOfFixedDuration.getMillisLong()>endDateOfHaml){
            //make 1 at the start, one at the end, and 1 in the middle
            val firstDuration = startDateOfHaml-fixedDurations[i].startDate.getMillisLong()
            val secondDuration = endDateOfHaml-startDateOfHaml
            val thirdDuration = fixedDurations[i].timeInMilliseconds-firstDuration-secondDuration

            val secondFixedDuration = FixedDuration(DurationType.DAM_IN_HAML,secondDuration, startDate = pregnancy.pregStartTime)
            val thirdFixedDuration = FixedDuration(DurationType.DAM,thirdDuration, startDate = pregnancy.birthTime)
            fixedDurations.add(i+1, secondFixedDuration)
            fixedDurations.add(i+2, thirdFixedDuration)
            fixedDurations[i].timeInMilliseconds=firstDuration


        }

        i++
    }
}


fun addStartDateToFixedDurations(fixedDurations: MutableList<FixedDuration>){
    var date: Instant = firstStartTime
    for (fixedDuration in fixedDurations){
        fixedDuration.startDate = date
        date = addTimeToDate(date,fixedDuration.timeInMilliseconds)
    }
}
fun addIndicesToFixedDurations(fixedDurations: MutableList<FixedDuration>){
    //this is so we can know the indics comparison between duration and fixed duration
    var i =0
    while(i<fixedDurations.size){
        fixedDurations[i].indices.add(i)
        i++
    }
}

fun removeTuhrLessThan15InPregnancy (fixedDurations: MutableList<FixedDuration>){
    var i=0
    while(i < fixedDurations.size){//iterate through durations
        //if there is a tuhr less than 15
        if(fixedDurations[i].days<15 && fixedDurations[i].type== DurationType.TUHR_IN_HAML){
            //it must be surrounded by dams on either side. increase size of damBefore. delete tuhr and dam after
            fixedDurations[i-1].timeInMilliseconds += fixedDurations[i].timeInMilliseconds + fixedDurations[i+1].timeInMilliseconds
            fixedDurations[i-1].indices.addAll(fixedDurations[i].indices)
            fixedDurations.removeAt(i)
            fixedDurations[i-1].indices.addAll(fixedDurations[i].indices)
            fixedDurations.removeAt(i)
        } else {
            i++
        }
    }
}


//step 2 - Remove tuhr-e-naaqis (less than 15 days):
//          iterate through array. if we find a tuhur less than 15, it must be surrounded
//          by dam. add all 3 duration values together, delete the originals, and set type as dam.
//          We want to keep the original list. Perumably the unflattened one will remain.


fun removeTuhrLessThan15 (fixedDurations: MutableList<FixedDuration>){
    var i=0
    while(i < fixedDurations.size){//iterate through durations
        //if there is a tuhr less than 15
        if(fixedDurations[i].days<15 && fixedDurations[i].type== DurationType.TUHR){
            //it must be surrounded by dams on either side. increase size of damBefore. delete tuhr and dam after
            fixedDurations[i-1].timeInMilliseconds += fixedDurations[i].timeInMilliseconds + fixedDurations[i+1].timeInMilliseconds
            fixedDurations[i-1].indices.addAll(fixedDurations[i].indices)
            fixedDurations.removeAt(i)
            fixedDurations[i-1].indices.addAll(fixedDurations[i].indices)
            fixedDurations.removeAt(i)
        } else {
            i++
        }
    }
}

//step 3 - Remove dam less than 3
//          iterate through array. when we find a dam less than 3, check if
//          there is a tuhur behind it. and in front of it. if there is then add all the 3 durations
//          together. set type as a new type tuhr-e-faasid. delete the originals.
fun removeDamLessThan3 (fixedDurations: MutableList<FixedDuration>,
                        inputtedMawjoodaTuhr: Long? = null,
                        isMawjoodaFasid: Boolean=false): PreMaslaValues {
    var i=0
    var isMawjoodaFasidEditable=isMawjoodaFasid
    var mawjoodahtuhreditable=inputtedMawjoodaTuhr
    while (i<fixedDurations.size-1){
        if(fixedDurations[i].type==DurationType.DAM && fixedDurations[i].days<3){
            if(i>0){//there is tuhur behind this and in front of it
                if(fixedDurations[i-1].type==DurationType.TUHR){//if there is one behind it
                    fixedDurations[i-1].type = DurationType.TUHREFAASID
                    fixedDurations[i-1].timeInMilliseconds += fixedDurations[i].timeInMilliseconds
                    fixedDurations[i-1].indices.addAll(fixedDurations[i].indices)

                    if(fixedDurations[i+1].type==DurationType.TUHR){
                        fixedDurations[i-1].timeInMilliseconds += fixedDurations[i+1].timeInMilliseconds
                        fixedDurations[i-1].indices.addAll(fixedDurations[i+1].indices)
                        fixedDurations.removeAt(i+1)

                    }
                    fixedDurations.removeAt(i)
                    i--
                }
            }else{//i is 0 (i should never be less than zero), ie, the first dam is less than 3
                var newStartDate = fixedDurations[0].startDate
                //mark the tuhr behind it as fasid
                isMawjoodaFasidEditable=true
                //adding the dam to the tuhr behind it
                if(mawjoodahtuhreditable==null){
                    mawjoodahtuhreditable=fixedDurations[0].timeInMilliseconds
                }else{
                    mawjoodahtuhreditable+=fixedDurations[0].timeInMilliseconds
                }
                newStartDate=addTimeToDate(newStartDate,fixedDurations[0].timeInMilliseconds)//but first move the start date onwards
                fixedDurations.removeAt(0)//delete that dam

                //if there is a tuhr in front of it, add it to the tuhr behind too
                if(fixedDurations[0].type==DurationType.TUHR){
                    mawjoodahtuhreditable+=fixedDurations[0].timeInMilliseconds//add the tuhr in front of it to mawjooda paki
                    newStartDate=addTimeToDate(newStartDate,fixedDurations[0].timeInMilliseconds)//but first move the start date onwards
                    //and delate that tuhr
                    fixedDurations.removeAt(0)
                }
                //assign new start date
                firstStartTime=newStartDate
            }
        }
        i++
    }
    if (mawjoodahtuhreditable != null) {
        if(mawjoodahtuhreditable<15.getMilliDays()){mawjoodahtuhreditable=null}
    }
    return PreMaslaValues(isMawjoodaFasid = isMawjoodaFasidEditable, inputtedMawjoodahTuhr = mawjoodahtuhreditable)
}
//step 4 - Deal with bigger than 10 dam
//          iterate through array. getting aadat on the way. each time you encounter a dam
//          less than 10, update it into HazAadat. each time you encounter a tuhur
//          (not a tuhr-e-faasid), update it into aadat too.

fun dealWithBiggerThan10Dam(fixedDurations: MutableList<FixedDuration>,
                            preMaslaValues: PreMaslaValues,
                            language: String,
                            adatsOfHaizList: MutableList<AadatAfterIndexOfFixedDuration>,
                            adatsOfTuhrList: MutableList<AadatAfterIndexOfFixedDuration>,
                            endOfDaurIkhtilaf: Boolean):Boolean{

    //This basically adds this info to each fixed duration of dam:
    // - istihaza before haiz duration
    // - haiz duration
    // - amount of dam left after haiz
    // - new aadats of haiz and tuhr
    // - we use a function dealWithIstihazaAfter, to figure out if aadat of haiz needs to be updated in case of daur
    val inputtedAadatHaz = preMaslaValues.inputtedAadatHaiz
    val inputtedAadatTuhr = preMaslaValues.inputtedAadatTuhr
    val inputtedMawjoodaTuhr = preMaslaValues.inputtedMawjoodahTuhr
    val isMawjoodaFasid  = preMaslaValues.isMawjoodaFasid


    var aadatHaz:Long = -1
    var aadatTuhr:Long = -1
    var mawjoodaTuhr:Long = -1

    if (inputtedAadatHaz != null && inputtedAadatHaz>=3.getMilliDays() && inputtedAadatHaz<=10.getMilliDays()){
        aadatHaz = inputtedAadatHaz
        adatsOfHaizList+=AadatAfterIndexOfFixedDuration(aadatHaz,-1)
    }
    if (inputtedAadatTuhr!= null && inputtedAadatTuhr>=15.getMilliDays()){
        aadatTuhr = inputtedAadatTuhr
        adatsOfTuhrList+=AadatAfterIndexOfFixedDuration(aadatTuhr,-1)
    }
    if (inputtedMawjoodaTuhr!= null && inputtedMawjoodaTuhr>=15.getMilliDays()){
        mawjoodaTuhr = inputtedMawjoodaTuhr
    }
    for (i in fixedDurations.indices){
        //iterate through fixedDurations

        //get aadat if dam is less than 10
        if(fixedDurations[i].type==DurationType.DAM && fixedDurations[i].days<=10&&fixedDurations[i].days>=3){
            aadatHaz = fixedDurations[i].timeInMilliseconds
            adatsOfHaizList+=AadatAfterIndexOfFixedDuration(aadatHaz,i)
            if(i>0 && fixedDurations[i-1].type==DurationType.TUHR){
                aadatTuhr = fixedDurations[i-1].timeInMilliseconds
                //if aadat is bigger than or equal to 6 months
                if(aadatTuhr>=30*6.getMilliDays()){
                    //make aadat 2 months
                    aadatTuhr = 30*2.getMilliDays()
                    //mark that tuhr as a super long tuhr
                    fixedDurations[i-1].type= DurationType.TUHR_BIGGER_THAN_6_MONTHS
                }
                adatsOfTuhrList+=AadatAfterIndexOfFixedDuration(aadatTuhr,i)
            }else if(i==0 && mawjoodaTuhr!=-1L && !isMawjoodaFasid){
                aadatTuhr = mawjoodaTuhr
                //if aadat is bigger than or equal to 6 months
                if(aadatTuhr>=30*6.getMilliDays()){
                    //make aadat 2 months
                    aadatTuhr = 30*2.getMilliDays()
                }
                adatsOfTuhrList+=AadatAfterIndexOfFixedDuration(aadatTuhr,i)
            }

        }else if(fixedDurations[i].type==DurationType.DAM_IN_NIFAS_PERIOD && fixedDurations[i].days>40){

            //check if we have aadaat.
            // first check for nifas aadat
            val aadatNifas = fixedDurations[i].biggerThanForty!!.nifas
            val istihazaAfter = fixedDurations[i].biggerThanForty!!.istihazaAfter

            //if istihaza after is less than 15, so ther is no possibilty of daur,
            // and it is followed by a Tuhr-e tamm, then we don't need aadats just yet
            if((istihazaAfter<18.getMilliDays() && i != fixedDurations.lastIndex)||
                    istihazaAfter<15.getMilliDays()){
                //we do not need aadaat yet.
                //I'm going to run this with a bogus aadat cuz we need it for other stuff
                dealWithIstihazaAfter(istihazaAfter,3.getMilliDays(),15.getMilliDays(),fixedDurations, i, endOfDaurIkhtilaf)
                val nifasInfo = BiggerThanFortyNifas(aadatNifas, istihazaAfter, aadatHaz,aadatHaz, aadatTuhr, mutableListOf())
                fixedDurations[i].biggerThanForty=nifasInfo
            }else{
                //we do need aadaat
                //we don't need mawjoodah paki
                if(aadatHaz==(-1).toLong() ||aadatTuhr==(-1).toLong()){
                    //give error message
                    if(language==Vls.Langs.ENGLISH){
                        window.alert(StringsOfLanguages.ENGLISH.errorEnterAadat)
                    }else if(language==Vls.Langs.URDU){
                        window.alert(StringsOfLanguages.URDU.errorEnterAadat)
                    }
                    return false
                }

                val haiz = aadatHaz
                aadatHaz = dealWithIstihazaAfter(istihazaAfter,aadatHaz,aadatTuhr,fixedDurations, i, endOfDaurIkhtilaf)
                adatsOfHaizList+=AadatAfterIndexOfFixedDuration(aadatHaz,i)
                val nifasInfo = BiggerThanFortyNifas(aadatNifas, istihazaAfter, haiz,aadatHaz, aadatTuhr, mutableListOf())
                fixedDurations[i].biggerThanForty=nifasInfo
            }

        }else if(fixedDurations[i].type==DurationType.DAM && fixedDurations[i].days>10){
            //if we hit a dam bigger than 10, check to see if we have aadat
            if(aadatHaz==-1L ||aadatTuhr==-1L){
                //give error message
                if(language==Vls.Langs.ENGLISH){
                    window.alert(StringsOfLanguages.ENGLISH.errorEnterAadat)
                }else if(language==Vls.Langs.URDU){
                    window.alert(StringsOfLanguages.URDU.errorEnterAadat)
                }
                return false
            }
            else{//we have aadat
                if(mawjoodaTuhr==-1L && i<1){//if mawjoodah tuhr doesn't exist and the first period is bigger than 10
                    //give error message
                    if(language==Vls.Langs.ENGLISH){
                        window.alert(StringsOfLanguages.ENGLISH.errorEnterMawjoodaPaki)
                    }else if(language==Vls.Langs.URDU){
                        window.alert(StringsOfLanguages.URDU.errorEnterMawjoodaPaki)
                    }
                    return false
                }else{

                    //set the mawjoodah tuhr to previous tuhr, if it exists
                    if(i>0){
                        mawjoodaTuhr = fixedDurations[i-1].timeInMilliseconds + fixedDurations[i-1].istihazaAfter
                    }
                }
                val mp:Long = mawjoodaTuhr
                val gp:Long = aadatTuhr
                val dm:Long = fixedDurations[i].timeInMilliseconds
                val hz:Long = aadatHaz
                val output:FiveSoortainOutput = fiveSoortain(mp, gp, dm, hz)

                //deal with output
                //update aadats
                aadatHaz = output.haiz
                adatsOfHaizList+=AadatAfterIndexOfFixedDuration(aadatHaz,i)

                if(output.aadatTuhrChanges && ((i<1 && !isMawjoodaFasid) ||
                            (i>0 && fixedDurations[i-1].type==DurationType.TUHR))){//and it exists
                    //if mp is not tuhrefaasid or tuhr in haml

                    //adding Mufti Mumtaz rule: If situation is A-3 then tuhr aadat doesn't change if
                    // it is the last dam
                    if(output.soorat==Soortain.A_3 && i==fixedDurations.lastIndex){
                        //aadat stays the same
                    }else{
                        aadatTuhr = mp
                    }
                    //if aadat is bigger than or equal to 6 months
                    if(aadatTuhr>=30*6.getMilliDays()){
                        //make aadat 2 months
                        aadatTuhr = 30*2.getMilliDays()
                        //mark that tuhr as a super long tuhr
                        //if it exists
                        if(i>0){
                            fixedDurations[i-1].type= DurationType.TUHR_BIGGER_THAN_6_MONTHS
                        }
                    }
                    adatsOfTuhrList+=AadatAfterIndexOfFixedDuration(aadatTuhr,i)

                }
                val hall =  BiggerThanTenDm(mp,gp,dm,hz, output.soorat, output.istihazaBefore,
                    output.haiz, output.istihazaAfter, aadatHaz,aadatTuhr, mutableListOf())
                fixedDurations[i].biggerThanTen=hall

                aadatHaz = dealWithIstihazaAfter(output.istihazaAfter,aadatHaz,aadatTuhr,fixedDurations, i, endOfDaurIkhtilaf)
                adatsOfHaizList+=AadatAfterIndexOfFixedDuration(aadatHaz,i) //this is the second aadat that goes to this i

            }
        }
    }
    return true
}

fun dealWithIstihazaAfter(istihazaAfter: Long,
                          aadatHaz: Long,
                          aadatTuhr: Long,
                          fixedDurations: MutableList<FixedDuration>,
                          i: Int, daurHaizIkhtilaf: Boolean): Long {
    //this basically does 2 things:
    // it returns the aadat of haiz at the end of istimrar, if it ended at haiz
    // it adds the right amount of istehaza to the next tuhr, and marks it as fasid, if it ended at istehaza.

    //if istihazaAfter is bigger than addatTuhr +3, run daur
    var returnAadatHaiz = aadatHaz
    if (istihazaAfter>=aadatTuhr+(3.getMilliDays())){
        //find  remainder

        val remainder = istihazaAfter%(aadatHaz+aadatTuhr)
        if(daurHaizIkhtilaf && remainder+aadatHaz<=10.getMilliDays()){//ikhtlafi masla
            returnAadatHaiz = remainder+aadatHaz
        }else if (remainder<aadatTuhr + (3.getMilliDays())){//it ended in tuhr or right between haz and tuhr
            //add istihazaAfter to next Tuhur mark it as fasid
            //if it exists
            //if remainder is not equal to zero
            if(i<fixedDurations.lastIndex && remainder>0 &&
                (fixedDurations[i+1].type==DurationType.TUHR||
                        fixedDurations[i+1].type==DurationType.TUHREFAASID)){//there is a tuhur after this
                fixedDurations[i+1].type=DurationType.TUHREFAASID
                fixedDurations[i+1].istihazaAfter=remainder
            }else if(i<fixedDurations.lastIndex && remainder>0 &&
                (fixedDurations[i+1].type==DurationType.TUHR_IN_HAML||
                        fixedDurations[i+1].type==DurationType.TUHREFAASID_IN_HAML)){
                //there is a tuhur after this
                fixedDurations[i+1].type=DurationType.TUHREFAASID_IN_HAML
                fixedDurations[i+1].istihazaAfter=remainder
            }


        }else{//it ended in less than haiz
            //change aadatHaiz
            returnAadatHaiz = remainder-aadatTuhr

        }

    }else if(istihazaAfter==0L){
        println("Shouldn't happen")
    }else{

        //else add istihazaAfter to next Tuhr, mark it as fasid
        //if it exists
        if(i<fixedDurations.size-1 &&
            (fixedDurations[i+1].type==DurationType.TUHR||
                    fixedDurations[i+1].type==DurationType.TUHREFAASID)){
            fixedDurations[i+1].type=DurationType.TUHREFAASID
            fixedDurations[i+1].istihazaAfter = istihazaAfter
        }else if(i<fixedDurations.size-1 &&
            (fixedDurations[i+1].type==DurationType.TUHR_IN_HAML||
                    fixedDurations[i+1].type==DurationType.TUHREFAASID_IN_HAML)){
            fixedDurations[i+1].type=DurationType.TUHREFAASID_IN_HAML
            fixedDurations[i+1].istihazaAfter = istihazaAfter
        }


    }
    return returnAadatHaiz
}

class FiveSoortainOutput (
    val soorat: Soortain,
    val istihazaBefore: Long,
    val haiz:Long,
    val istihazaAfter: Long,
    val aadatTuhrChanges:Boolean
)

fun fiveSoortain(mp: Long, gp: Long, dm: Long, hz:Long):FiveSoortainOutput{
    val soorat: Soortain
    val istihazaBefore:Long
    val haiz:Long
    val istihazaAfter:Long
    val aadatTuhrChanges:Boolean // 0 for gp, 1 for mp (change)

    if (mp <= gp) {    //Qism A
        if (hz <= dm - (gp - mp)) {	                  // soorat A-1
            // if GP==MP, we would output 0 istihaza, not good!
            // 0 istihaza is ok, for now. just no negative numbers, please
            soorat = Soortain.A_1
            istihazaBefore = gp-mp
            haiz = hz
            istihazaAfter = dm-(gp-mp)-hz
            aadatTuhrChanges = false
        }
        else if (3.getMilliDays() <= dm-(gp-mp) && dm-(gp-mp) < hz) {  // soorat A-2
            soorat = Soortain.A_2
            istihazaBefore = gp-mp
            haiz = dm-(gp-mp)
            istihazaAfter = 0
            aadatTuhrChanges = false
        }
        else{ //if (dm - (gp - mp) < 3.millisFromDays()) {                  // soorat A-3
            soorat = Soortain.A_3
            istihazaBefore = 0
            haiz = hz
            istihazaAfter = dm-hz
            aadatTuhrChanges = true
        }
    }else {	// mp>gp qism B
        if (hz - (mp - gp) >= 3.getMilliDays()) {							// soorat B-2
            soorat = Soortain.B_2
            istihazaBefore = 0
            haiz = hz-(mp-gp)
            istihazaAfter = dm-(hz-(mp-gp))
            aadatTuhrChanges = true
        }else{ //if (hz - (mp - gp) < 3.millisFromDays()) {						// soorat B-3
            soorat = Soortain.B_3
            istihazaBefore = 0
            haiz = hz
            istihazaAfter = dm-hz
            aadatTuhrChanges = true
        }
    }
  return FiveSoortainOutput(soorat,istihazaBefore,haiz,istihazaAfter, aadatTuhrChanges)
}
fun checkForAyyameQabliyya(fixedDurations: MutableList<FixedDuration>,
                           adatsOfHaizList: MutableList<AadatAfterIndexOfFixedDuration>,
                           adatsOfTuhrList: MutableList<AadatAfterIndexOfFixedDuration>,
                           inputtedMawjoodaTuhr: Long?, ayyameQabliyyaIkhtilaf: Boolean){
    if(!ayyameQabliyyaIkhtilaf){//the ikhtilaf button is not on
        //figure out aadat for the last fixed duration
        //for that, we need aadats befor it
        //we need to find out what aadats were, at this point.
        var hz = adatsOfHaizList[0].aadat
        var gp = adatsOfTuhrList[0].aadat
        for(adat in adatsOfHaizList){
            if(adat.index<fixedDurations.lastIndex){
                hz= adat.aadat
            }else{
                break
            }
        }
        for(adat in adatsOfTuhrList){
            if(adat.index<fixedDurations.lastIndex){
                //check if it is bigger than 6 motnhs
                if(adat.index>0 &&  fixedDurations[adat.index-1].type==DurationType.TUHR_BIGGER_THAN_6_MONTHS){
                    gp=fixedDurations[adat.index-1].timeInMilliseconds
                }else{
                    gp= adat.aadat
                }
            }else{
                break
            }
        }
        //now we have aadaat

        var mp = inputtedMawjoodaTuhr
        if(fixedDurations.size>1){
            mp = fixedDurations[fixedDurations.size-2].timeInMilliseconds+
                    fixedDurations[fixedDurations.size-2].istihazaAfter
        }

        if(mp!=null&&mp!=-1L&&hz!=-1L&&gp!=-1L){
            val ayyaameqabliyyah = gp-mp
            if(ayyaameqabliyyah+hz>10.getMilliDays() &&
                ayyaameqabliyyah<18.getMilliDays()&&
                fixedDurations.last().timeInMilliseconds<ayyaameqabliyyah){//hasn't entered into aadat yet
                fixedDurations.last().type = DurationType.ISTEHAZA_AYYAMEQABLIYYA
                fixedDurations.last().ayyameqabliyya=AyyameQabliyya(ayyaameqabliyyah, hz, gp)
            }

        }
    }else{
        //do nothing
    }


}
fun addDurationsToDams(fixedDurations: MutableList<FixedDuration>,
                       endOfDaurIkhtilaf:Boolean,
                       typesOfInputs: TypesOfInputs){
    //What this function does?
    //It creates a list of durations associated with each fixed duration
    //each duration contains its type, it's starting date, and it's duration
    //so, for example 15B could have a first 3 days of istihaza before, then
    // 7 days of haiz, then 3 days of istihaza after. this will make all those duration.

    //it should also make less than 3 haizes at the end of periods

    //in input:durations, we expect all info to be not current, and solve according to that, so lastIndex is not relevant

    for (i in fixedDurations.indices){
        if(fixedDurations[i].type==DurationType.DAM && //as this is the last dam, less than 3 can be made haiz
            fixedDurations[i].days>10 &&
            fixedDurations[i].biggerThanTen!!.qism==Soortain.A_3 &&
            i==fixedDurations.lastIndex &&
            typesOfInputs!=TypesOfInputs.DURATION){//A-3 switching to aadat
            val diffInPakis = fixedDurations[i].biggerThanTen!!.gp-fixedDurations[i].biggerThanTen!!.mp
            if(fixedDurations[i].timeInMilliseconds>=diffInPakis){
                fixedDurations[i].biggerThanTen!!.durationsList += Duration(DurationType.ISTIHAZA_BEFORE,diffInPakis,fixedDurations[i].startDate)
                if(fixedDurations[i].timeInMilliseconds > diffInPakis){//excluding the equal scenario
                    val haizStartDate = addTimeToDate(fixedDurations[i].startDate, diffInPakis)
                    val haizDuration = fixedDurations[i].timeInMilliseconds-diffInPakis
                    fixedDurations[i].biggerThanTen!!.durationsList += Duration(DurationType.LESS_THAN_3_HAIZ,haizDuration, haizStartDate)
                }
                return
            }
        }
        if((fixedDurations[i].type==DurationType.DAM||
                    fixedDurations[i].type==DurationType.DAM_MUBTADIA)
            && fixedDurations[i].days>10){

            //bigger than 10
            val istihazaBefore = fixedDurations[i].biggerThanTen!!.istihazaBefore
            val istihazaBeforeStartDate:Instant = fixedDurations[i].startDate

            if (istihazaBefore>0){
                fixedDurations[i].biggerThanTen!!.durationsList += Duration(DurationType.ISTIHAZA_BEFORE,istihazaBefore,istihazaBeforeStartDate)
            }
            val haizStartDate = addTimeToDate(istihazaBeforeStartDate, (istihazaBefore))
            val haiz = fixedDurations[i].biggerThanTen!!.haiz
            fixedDurations[i].biggerThanTen!!.durationsList += Duration(DurationType.HAIZ,haiz,haizStartDate)

            val istihazaAfterStartDate = addTimeToDate(haizStartDate, (haiz))
            val istihazaAfter = fixedDurations[i].biggerThanTen!!.istihazaAfter

            var aadatTuhr = fixedDurations[i].biggerThanTen!!.aadatTuhr
            var aadatHaz = fixedDurations[i].biggerThanTen!!.aadatHaiz

            //this is calculating iztirari aadaat in case we don't have aadat for mubtadia
            if(aadatTuhr==-1L){
                if(aadatHaz==-1L){
                    aadatHaz=10.getMilliDays()
                }
                aadatTuhr=30.getMilliDays()-haiz
            }

            if(istihazaAfter>0){
                if(istihazaAfter>=aadatTuhr+3.getMilliDays()||
                    (istihazaAfter>aadatTuhr &&
                            istihazaAfter<aadatTuhr+3.getMilliDays() &&
                            i==fixedDurations.lastIndex && typesOfInputs!=TypesOfInputs.DURATION)){

                    //daur
                    //find quotient and remainder
                    var remainder = istihazaAfter%(haiz+aadatTuhr)
                    var quotient = ((istihazaAfter-remainder)/(haiz+aadatTuhr))
                    if(endOfDaurIkhtilaf && remainder+aadatHaz<=10.getMilliDays()){
                        quotient--
                        remainder += aadatTuhr + aadatHaz
                    }
                    var aadatTuhrStartDate:Instant = istihazaAfterStartDate
                    var aadatTuhrEndDate:Instant
                    var aadatHaizEndDate:Instant

                    for(j in 1 .. quotient){
                        //add a quotient number of tuhr/hazes
                        aadatTuhrEndDate = addTimeToDate(aadatTuhrStartDate,(aadatTuhr))
                        aadatHaizEndDate = addTimeToDate(aadatTuhrEndDate,(haiz))

                        fixedDurations[i].biggerThanTen!!.durationsList+=Duration(DurationType.ISTIHAZA_AFTER,aadatTuhr,aadatTuhrStartDate)
                        fixedDurations[i].biggerThanTen!!.durationsList+=Duration(DurationType.HAIZ,haiz,aadatTuhrEndDate)
                        aadatTuhrStartDate=aadatHaizEndDate
                    }
                    //now deal with remiander


                    if(remainder==0L){
                        //there is nothing more to be added
                    }else if(remainder>aadatHaz+aadatTuhr){//this is in case of the ikhtlafi masla
                        fixedDurations[i].biggerThanTen!!.durationsList+=Duration(DurationType.ISTIHAZA_AFTER,aadatTuhr,aadatTuhrStartDate)
                        fixedDurations[i].biggerThanTen!!.durationsList+=Duration(DurationType.HAIZ,remainder-aadatTuhr,addTimeToDate(aadatTuhrStartDate,aadatTuhr))
                    }else if(remainder>aadatTuhr
                        && remainder<aadatTuhr+3.getMilliDays()
                        && i==fixedDurations.lastIndex && typesOfInputs!=TypesOfInputs.DURATION){//it is the last period, and ends in less than 3 haiz
                        fixedDurations[i].biggerThanTen!!.durationsList+=Duration(DurationType.ISTIHAZA_AFTER,aadatTuhr,aadatTuhrStartDate)
                        aadatTuhrEndDate = addTimeToDate(aadatTuhrStartDate,(aadatTuhr))
                        val lastHaiz = remainder-aadatTuhr
                        fixedDurations[i].biggerThanTen!!.durationsList+=Duration(DurationType.LESS_THAN_3_HAIZ,lastHaiz,aadatTuhrEndDate)
                    }else if(remainder<aadatTuhr+3.getMilliDays()){
                        //it ends in tuhr
                        fixedDurations[i].biggerThanTen!!.durationsList+=Duration(DurationType.ISTIHAZA_AFTER,remainder,aadatTuhrStartDate)
                    }else{
                        //there is full tuhur, followed by a partial haiz
                        fixedDurations[i].biggerThanTen!!.durationsList+=Duration(DurationType.ISTIHAZA_AFTER,aadatTuhr,aadatTuhrStartDate)
                        aadatTuhrEndDate = addTimeToDate(aadatTuhrStartDate,(aadatTuhr))
                        val lastHaiz = remainder-aadatTuhr
                        fixedDurations[i].biggerThanTen!!.durationsList+=Duration(DurationType.HAIZ,lastHaiz,aadatTuhrEndDate)
                    }

                }else{
                    //no daur
                    fixedDurations[i].biggerThanTen!!.durationsList+=Duration(DurationType.ISTIHAZA_AFTER,istihazaAfter,istihazaAfterStartDate)

                }
            }

        }else if(fixedDurations[i].type==DurationType.DAM_IN_NIFAS_PERIOD&&fixedDurations[i].days>40){
            //bigger than 40 nifas
            val aadatNifas = fixedDurations[i].biggerThanForty!!.nifas
            //make nifas period
            fixedDurations[i].biggerThanForty!!.durationsList+=Duration(DurationType.NIFAS,aadatNifas,fixedDurations[i].startDate)
            //now deal with istihaza after
            //I'm copy/pasting from above, with minor changes

            val istihazaAfterStartDate = addTimeToDate(fixedDurations[i].startDate, (aadatNifas))
            val istihazaAfter = fixedDurations[i].biggerThanForty!!.istihazaAfter
            var aadatTuhr = fixedDurations[i].biggerThanForty!!.aadatTuhr
            var aadatHaz = fixedDurations[i].biggerThanForty!!.haiz

            //this ought to fix if we got here without an aadat. persumably, we did it cuz there was no daur
            if(aadatTuhr==-1L){
                aadatTuhr=15.getMilliDays()
            }
            if(aadatHaz==-1L){
                aadatHaz=3.getMilliDays()
            }

            if(istihazaAfter>0){//if there is istehaza after
                if(istihazaAfter>=aadatTuhr+3.getMilliDays()||
                    (istihazaAfter>aadatTuhr &&
                            istihazaAfter<aadatTuhr+3.getMilliDays() &&
                            i == fixedDurations.lastIndex && typesOfInputs!=TypesOfInputs.DURATION)){

                    //daur
                    //find quotient and remainder
                    val remainder = istihazaAfter%(aadatHaz+aadatTuhr)
                    val quotient = ((istihazaAfter-remainder)/(aadatHaz+aadatTuhr))
                    var aadatTuhrStartDate:Instant = istihazaAfterStartDate
                    var aadatTuhrEndDate:Instant
                    var aadatHaizEndDate:Instant

                    for(j in 1 .. quotient){
                        //add a quotient number of tuhr/hazes
                        aadatTuhrEndDate = addTimeToDate(aadatTuhrStartDate,(aadatTuhr))
                        aadatHaizEndDate = addTimeToDate(aadatTuhrEndDate,(aadatHaz))

                        fixedDurations[i].biggerThanForty!!.durationsList+=Duration(DurationType.ISTIHAZA_AFTER,aadatTuhr,aadatTuhrStartDate)
                        fixedDurations[i].biggerThanForty!!.durationsList+=Duration(DurationType.HAIZ,aadatHaz,aadatTuhrEndDate)
                        aadatTuhrStartDate=aadatHaizEndDate
                    }
                    //now deal with remiander
                    if(remainder==0L){
                        //there is nothing more to be added
                    }else if(remainder>aadatTuhr
                        && remainder<aadatTuhr+3.getMilliDays()
                        && i==fixedDurations.lastIndex && typesOfInputs!=TypesOfInputs.DURATION){//it is the last period, and ends in less than 3 haiz
                        fixedDurations[i].biggerThanForty!!.durationsList+=Duration(DurationType.ISTIHAZA_AFTER,aadatTuhr,aadatTuhrStartDate)
                        aadatTuhrEndDate = addTimeToDate(aadatTuhrStartDate,(aadatTuhr))
                        val lastHaiz = remainder-aadatTuhr
                        fixedDurations[i].biggerThanForty!!.durationsList+=Duration(DurationType.LESS_THAN_3_HAIZ,lastHaiz,aadatTuhrEndDate)
                    }else if(remainder<aadatTuhr+3.getMilliDays()){
                        //it ends in tuhr
                        fixedDurations[i].biggerThanForty!!.durationsList+=Duration(DurationType.ISTIHAZA_AFTER,remainder,aadatTuhrStartDate)
                    }else{
                        //there is full tuhur, followed by a partial haiz
                        fixedDurations[i].biggerThanForty!!.durationsList+=Duration(DurationType.ISTIHAZA_AFTER,aadatTuhr,aadatTuhrStartDate)
                        aadatTuhrEndDate = addTimeToDate(aadatTuhrStartDate,(aadatTuhr))
                        val lastHaiz = remainder-aadatTuhr
                        fixedDurations[i].biggerThanForty!!.durationsList+=Duration(DurationType.HAIZ,lastHaiz,aadatTuhrEndDate)
                    }

                }else{
                    //no daur
                    fixedDurations[i].biggerThanForty!!.durationsList+=Duration(DurationType.ISTIHAZA_AFTER,istihazaAfter,istihazaAfterStartDate)

                }
            }

        }
        if(fixedDurations[i].type==DurationType.TUHREFAASID||
            fixedDurations[i].type==DurationType.TUHREFAASID_MUBTADIA){
            //check if it has istehaza attached

            if(fixedDurations[i].istihazaAfter>0){
                if(fixedDurations[i].type==DurationType.TUHREFAASID){
                    fixedDurations[i].type = DurationType.TUHREFAASID_WITH_ISTEHAZA
                }else if( fixedDurations[i].type==DurationType.TUHREFAASID_MUBTADIA){
                    fixedDurations[i].type=DurationType.TUHREFAASID_MUBTADIA_WITH_ISTEHAZA
                }
            }
        }
    }

}

fun getHaizDatesList(fixedDurations: MutableList<FixedDuration>):MutableList<Entry>{
    val hazDatesList = mutableListOf<Entry>()

    for(fixedDuration in fixedDurations){
        if(fixedDuration.type==DurationType.DAM && fixedDuration.days <=10){
            //less than 3 were eliminated earlier
            hazDatesList+=Entry(fixedDuration.startDate,fixedDuration.endDate)
        }else if(fixedDuration.type==DurationType.DAM && fixedDuration.days>10){
            for(duration in fixedDuration.biggerThanTen!!.durationsList){
                if(duration.type==DurationType.HAIZ
                    ||duration.type == DurationType.LESS_THAN_3_HAIZ){
                    hazDatesList+=Entry(duration.startTime,duration.endDate)
                }
            }
        }else if(fixedDuration.type==DurationType.DAM_IN_NIFAS_PERIOD && fixedDuration.days<=40){
            hazDatesList+=Entry(fixedDuration.startDate,fixedDuration.endDate)
        }else if(fixedDuration.type==DurationType.DAM_IN_NIFAS_PERIOD && fixedDuration.days>40){
            for(duration in fixedDuration.biggerThanForty!!.durationsList){
                if(duration.type==DurationType.HAIZ
                    ||duration.type == DurationType.NIFAS
                    ||duration.type == DurationType.LESS_THAN_3_HAIZ){
                    hazDatesList+=Entry(duration.startTime,duration.endDate)
                }
            }
        }
    }

    return hazDatesList

}
fun addWiladat(fixedDurations: MutableList<FixedDuration>, pregnancy: Pregnancy){
    for(i in fixedDurations.indices){
        if(fixedDurations[i].endDate.getMillisLong()>pregnancy.birthTime.getMillisLong()||
                fixedDurations[i].type==DurationType.DAM_IN_NIFAS_PERIOD){
            val newFixedDuration = FixedDuration(DurationType.WILADAT_ISQAT, 0L, mutableListOf(),startDate = pregnancy.birthTime)
            fixedDurations.add(i,newFixedDuration)
            break
        }
        if(i==fixedDurations.lastIndex){
            //if we got to the last one without anything happening, just add it anyway
            val newFixedDuration = FixedDuration(DurationType.WILADAT_ISQAT, 0L, mutableListOf(),startDate = pregnancy.birthTime)
            fixedDurations.add(i+1,newFixedDuration)
        }
    }
}
fun addStartOfPregnancy(fixedDurations: MutableList<FixedDuration>,pregnancy: Pregnancy){
    //add start of pregnancy in fixed periods
    for(i in fixedDurations.indices){
        if(fixedDurations[i].endDate.getMillisLong()>pregnancy.pregStartTime.getMillisLong()){
            val newFixedDuration= FixedDuration(DurationType.HAML,0L, mutableListOf(), startDate = pregnancy.pregStartTime)
            fixedDurations.add(i,newFixedDuration)
            break
        }
        if(i==fixedDurations.size-1){
            //if we got to the last one without anything happening, just add it anyway
            val newFixedDuration= FixedDuration(DurationType.HAML,0L, mutableListOf(), startDate = pregnancy.pregStartTime)
            fixedDurations.add(i,newFixedDuration)
        }
    }

}


fun generatInfoForCompareTable(listOfLists: MutableList<List<Entry>>):InfoForCompareTable {
    var earliestStartTime = listOfLists[0][0].startTime
    var latestEndTime=listOfLists[0].last().endTime
    for (list in listOfLists) {
        if (list[0].startTime.getMillisLong() <earliestStartTime.getMillisLong())
            earliestStartTime = list[0].startTime
        if (list[list.lastIndex].endTime.getMillisLong() > latestEndTime.getMillisLong())
            latestEndTime = list.last().endTime
    }
    val firstLast = Entry(earliestStartTime, latestEndTime)

    val ndays = (latestEndTime.getMillisLong() - earliestStartTime.getMillisLong()).getDays()

    val headerList = mutableListOf<Instant>()
    for(day in 0..(ndays)){//header list is one longer than ndays
        val dateOfDay = addTimeToDate(firstLast.startTime, (day).getMilliDays())
        if(headerList.size<ndays+1){
            headerList+=dateOfDay
        }
    }

    val listOfColorsOfDaysList = mutableListOf<MutableList<Int>>()
    for (list in listOfLists){//in the lists
        val colorsOfDaysList = mutableListOf<Int>()

        for(i in 0 until ndays){//go through each day
            val header = headerList[i]
            //check if this date is in between a startTime and an endtime
            for(entry in list) {//check the list to see if it is a haiz day
                if (header.getMillisLong() >= entry.startTime.getMillisLong() && header.getMillisLong() < entry.endTime.getMillisLong()) {
                    //that date is a haiz
                    colorsOfDaysList +=1
                    break
                }else if (header.getMillisLong() < entry.startTime.getMillisLong()) {
                    //that date is a tuhur
                    colorsOfDaysList +=0
                    break
                }else if(header.getMillisLong()>=list.last().endTime.getMillisLong()){
                    colorsOfDaysList +=0
                    break
                }
            }
        }
        listOfColorsOfDaysList +=colorsOfDaysList
    }

    //this bit describes which days are yaqeeni paki, yaqeeni napaki, or shakk
    val resultColors = mutableListOf<Int>()
    for(day in listOfColorsOfDaysList[0].indices){
        //for each day
        var compare = 0
        for(list in listOfColorsOfDaysList){
            val color = list[day]
            compare +=color
        }
        val maxColor = listOfColorsOfDaysList.size
        val minColor = 0
        resultColors += when (compare) {
            minColor -> 0 //yaqeeni paki
            maxColor ->  2 //yaqeeni napaki
            else -> 1//ayyam-e-shakk
        }
    }

    return InfoForCompareTable(headerList,listOfColorsOfDaysList,resultColors)

}

//fun getDifferenceFromMultiple (listOfLists:List<List<Entry>>):String{
//    //find out number of lists
//    var numberOfLists = listOfLists.size
//
//    //step 1: merge them into one list
//    var dateTypeList = mutableListOf<DateTypeList>()
//
//    for (list in listOfLists){
//        for(date in list){
//            dateTypeList += DateTypeList(date.startTime,DateTypes.START)
//            dateTypeList += DateTypeList(date.endTime,DateTypes.END)
//        }
//    }
//
//    //step 2: order list by date
//    dateTypeList.sortBy { it.date.getTime() }
//
//    //step 3: create a counter
//    var counter = 0
//
//    //step 4: step through the list, create an output list
//    var counterMin = 0 //at counter min, it is yaqeeni paki
//    var counterMax = numberOfLists //at counter max, it is yaqeeni na-paki
//    //all other counter values are ayyam-e-shakk
//
//    var outputList = mutableListOf<DateTypeList>()
//    for(dateType in dateTypeList){
//        //plus 1 for every start time, -1 for every end time
//        if(dateType.type==DateTypes.START){
//            counter++
//        }else{//the type is end
//            counter--
//        }
//
//        if(counter == counterMin){
//            outputList += DateTypeList(dateType.date, DateTypes.YAQEENI_PAKI)
//        }else if(counter == counterMax){//
//            outputList += DateTypeList(dateType.date, DateTypes.YAQEENI_NA_PAKI)
//        }else{
//            outputList += DateTypeList(dateType.date, DateTypes.AYYAAM_E_SHAKK)
//        }
//    }
//
//    //create a people-friendly version of output list
//    println("starting peoplefriendly")
//    var str = ""
//    var durationTypes = mutableListOf<DurationTypes>()
//    var i=0
//
//    while (i<outputList.size-1){
//        var startTime = outputList[i].date
//        var endTime = outputList[i+1].date
//        if(startTime.getTime()!=endTime.getTime()){//to prevent 0 duration
//            //in more than 1, there will be repeated ayyam-e-shakk. this is to prevent that
//            if(outputList[i].type!=DateTypes.AYYAAM_E_SHAKK){
//                durationTypes += DurationTypes(startTime,endTime,outputList[i].type)
//            }else{//It is ayyame shakk
//                //check all next ones to until there is a non aayam-e-shakk
//                var j = i
//                while(j<outputList.size-1){
//                    if(outputList[j+1].type!=DateTypes.AYYAAM_E_SHAKK){
//                        break
//                    }
//                    j++
//                }
//                endTime = outputList[j+1].date
//                i=j
//                durationTypes += DurationTypes(startTime,endTime,outputList[i].type)
//            }
//        }
//
//        i++
//    }
//    println("after a while")
//    println(durationTypes)
//
//    str += generateGetDifferenceString(durationTypes)
//    println(str)
//
//    return str
//}

// FORMERLY USED TO MAKE OUTDATED COMPARISONS IN ZALLA
//fun getDifferenceFromMultiple (listOfLists:List<List<Entry>>):String{
//    //find out number of lists
//    val numberOfLists = listOfLists.size
//
//    //step 1: merge them into one list
//    val dateTypeList = mutableListOf<DateTypeList>()
//
//    for (list in listOfLists){
//        for(date in list){
//            dateTypeList += DateTypeList(date.startTime,DateTypes.START)
//            dateTypeList += DateTypeList(date.endTime,DateTypes.END)
//        }
//    }
//
//    //step 2: order list by date
//    //since we want to prioritize khurooj, we should reverse order
//    dateTypeList.sortBy { it.date.getTime() }
//
//    //step 3: create a counter
//    var counter = 0
//
//    //step 4: step through the list, create an output list
//    val counterMin = 0 //at counter min, it is yaqeeni paki
//    val counterMax = numberOfLists //at counter max, it is yaqeeni na-paki
//    //all other counter values are ayyam-e-shakk
//
//    val outputList = mutableListOf<DateTypeList>()
//    for(dateType in dateTypeList){
//        //plus 1 for every start time, -1 for every end time
//        if(dateType.type==DateTypes.START){
//            counter++
//            //this is definitely a dukhool, and can lead to yaqeeni napaki
//            //this cannot be yaqeeni paki, or khurooj
//            //though it could simultaneously be a khurooj some other way
//            if(counter==counterMax){
//                outputList+=DateTypeList(dateType.date, DateTypes.YAQEENI_NA_PAKI)
//            }else{//it is dukhool shakk
//                outputList+=DateTypeList(dateType.date, DateTypes.AYYAAM_E_SHAKK_DUKHOOL)
//            }
//        }else{//the type is end
//            counter--
//            //this is a khurooj or yaqeeni paki
//            if(counter==counterMin){
//                outputList+=DateTypeList(dateType.date, DateTypes.YAQEENI_PAKI)
//            }else{
//                outputList+=DateTypeList(dateType.date, DateTypes.AYYAAM_E_SHAKK_KHUROOJ)
//            }
//        }
//    }
//
//    //create a people-friendly version of output list
//    var str = ""
//    val durationTypes = mutableListOf<DurationTypes>()
//    var i=0
//
//    while (i<outputList.size-1){
//        val startTime = outputList[i].date
//        val endTime = outputList[i+1].date
//        durationTypes += DurationTypes(startTime,endTime,outputList[i].type)
//        i++
//    }
//    //clean up the junk in durationTypes list now
////    var j = 0
////    while (j <= durationTypes.lastIndex){
////        var duration=durationTypes[j]
////        //remove things with duration 0
////        if(duration.endTime.getTime()==duration.startTime.getTime()){
////            durationTypes.removeAt(j)
////            j--
////        }
////        j++
////    }
////    //remove things wherebthe next one is the same type as this one.
////    //if there is a next one
////    else if(j+1<durationTypes.size && durationTypes[j+1].type==duration.type){
////        durationTypes.add(j, DurationTypes(duration.startTime, durationTypes[i+1].endTime, duration.type))
////        durationTypes.removeAt(j+1)
////        durationTypes.removeAt(j+1)
////    }
//
//
//    str += generateGetDifferenceString(durationTypes)
//
//    return str
//}

fun calculateEndingOutputValues(fixedDurations: MutableList<FixedDuration>,
                                preMaslaValues: PreMaslaValues,
                                adatsOfHaizList: MutableList<AadatAfterIndexOfFixedDuration>,
                                adatsOfTuhrList: MutableList<AadatAfterIndexOfFixedDuration>,
                                aadatNifas:Long?=-1L,
                                newAadatNifas:Long? = -1L,
                                typesOfMasla: TypesOfMasla): EndingOutputValues {
//    println("calc ending output")
    val inputtedAadatTuhr = preMaslaValues.inputtedAadatTuhr
    val inputtedMawjoodaTuhr = preMaslaValues.inputtedMawjoodahTuhr
    val isMawjoodaFasid = preMaslaValues.isMawjoodaFasid

    val filHaalPaki = calculateFilHaal(fixedDurations,adatsOfHaizList,adatsOfTuhrList,inputtedMawjoodaTuhr)
    val aadaat = finalAadatsOfHaizAndTuhr(
        fixedDurations,
        inputtedAadatTuhr,
        inputtedMawjoodaTuhr,
        isMawjoodaFasid,
        adatsOfHaizList,
        adatsOfTuhrList)
    if(typesOfMasla==TypesOfMasla.NIFAS){
        aadaat.aadatNifas = newAadatNifas
    }

    val futureDates = futureDatesOfInterest(
        fixedDurations,
        aadaat,
        aadatNifas,
        adatsOfHaizList,
        adatsOfTuhrList,
        inputtedMawjoodaTuhr,
        typesOfMasla)
    return EndingOutputValues(filHaalPaki,aadaat,futureDates)
}

fun futureDatesOfInterest(fixedDurations: MutableList<FixedDuration>,
                          aadats: AadatsOfHaizAndTuhr,
                          aadatNifas: Long?,
                          adatsOfHaizList: MutableList<AadatAfterIndexOfFixedDuration>,
                          adatsOfTuhrList: MutableList<AadatAfterIndexOfFixedDuration>,
                          inputtedMawjoodaTuhr: Long?, typesOfMasla: TypesOfMasla):MutableList<FutureDateType>{
//    println("got to future dates")
    val futureDatesList = mutableListOf<FutureDateType>()
//    println(fixedDurations.last())

    //bigger than 10
    if(fixedDurations.last().days>10&&fixedDurations.last().type==DurationType.DAM) {
//        println("at least here!")

        val lastDuration = fixedDurations.last().biggerThanTen!!.durationsList.last()
        val qism = fixedDurations.last().biggerThanTen!!.qism
        //A-3
        if(qism==Soortain.A_3){
            val mp = fixedDurations.last().biggerThanTen!!.mp
            val gp = fixedDurations.last().biggerThanTen!!.gp
            val startOfAadat = addTimeToDate(fixedDurations.last().startDate, gp-mp)//this is start of aadat
            if(startOfAadat.getMillisLong()>fixedDurations.last().endDate.getMillisLong()){//A-3 hasn't entered aadat yet , but could experience duar!
                futureDatesList+= FutureDateType(startOfAadat,TypesOfFutureDates.A3_CHANGING_TO_A2)
                if((lastDuration.type==DurationType.HAIZ &&
                            lastDuration.timeInMilliseconds<aadats.aadatHaiz)||
                    lastDuration.type==DurationType.LESS_THAN_3_HAIZ){
//                    println("5")
                    val endDateOfHaiz = addTimeToDate(lastDuration.startTime, aadats.aadatHaiz)
                    if (endDateOfHaiz.getMillisLong()<startOfAadat.getMillisLong()){
//                        println("6")
                        futureDatesList+=FutureDateType(endDateOfHaiz,TypesOfFutureDates.END_OF_AADAT_HAIZ)
                        if(typesOfMasla!=TypesOfMasla.MUBTADIA){
                            futureDatesList+=FutureDateType(endDateOfHaiz,TypesOfFutureDates.IC_FORBIDDEN_DATE)
                        }
                        futureDatesList+=FutureDateType(endDateOfHaiz,TypesOfFutureDates.IHTIYATI_GHUSL)
                    }
                    if(lastDuration.type==DurationType.LESS_THAN_3_HAIZ){
                        val threeDays=addTimeToDate(lastDuration.startTime, 3.getMilliDays())
//                        println("2")
                        futureDatesList+=FutureDateType(threeDays,TypesOfFutureDates.BEFORE_THREE_DAYS)
                    }
                }else if(lastDuration.type==DurationType.ISTIHAZA_AFTER&& lastDuration.timeInMilliseconds<aadats.aadatTuhr){
                    val endDateOfTuhr = addTimeToDate(fixedDurations.last().biggerThanTen!!.durationsList.last().startTime, aadats.aadatTuhr)
                    if(endDateOfTuhr.getMillisLong()<startOfAadat.getMillisLong()){
//                        println("3")
                        futureDatesList+=FutureDateType(endDateOfTuhr,TypesOfFutureDates.END_OF_AADAT_TUHR)
                    }
                }else if(lastDuration.type==DurationType.HAIZ&&lastDuration.timeInMilliseconds==aadats.aadatHaiz){
                    val endDateOfTuhr = addTimeToDate(fixedDurations.last().biggerThanTen!!.durationsList.last().endDate, aadats.aadatTuhr)
                    if(endDateOfTuhr.getMillisLong()<startOfAadat.getMillisLong()){
//                        println("4")
                        futureDatesList+=FutureDateType(endDateOfTuhr,TypesOfFutureDates.END_OF_AADAT_TUHR)
                    }
                }else if(lastDuration.type==DurationType.ISTIHAZA_AFTER&&lastDuration.timeInMilliseconds==aadats.aadatTuhr){
                    val endDateOfHaiz = addTimeToDate(fixedDurations.last().biggerThanTen!!.durationsList.last().endDate, aadats.aadatHaiz)
                    if(endDateOfHaiz.getMillisLong()<startOfAadat.getMillisLong()){
//                        println("7")
                        val threeDays = addTimeToDate(fixedDurations.last().biggerThanTen!!.durationsList.last().endDate, 3.getMilliDays())
                        futureDatesList+=FutureDateType(threeDays,TypesOfFutureDates.BEFORE_THREE_DAYS)
                        futureDatesList+=FutureDateType(endDateOfHaiz,TypesOfFutureDates.END_OF_AADAT_HAIZ)
                        if(typesOfMasla!=TypesOfMasla.MUBTADIA) {
                            futureDatesList += FutureDateType(endDateOfHaiz, TypesOfFutureDates.IC_FORBIDDEN_DATE)
                        }
                        futureDatesList+=FutureDateType(endDateOfHaiz,TypesOfFutureDates.IHTIYATI_GHUSL)
                    }
                }

            }else if(startOfAadat.getMillisLong()<=fixedDurations.last().endDate.getMillisLong()){//A-3 entered into aadat
                if(lastDuration.type==DurationType.LESS_THAN_3_HAIZ){
                    val lessThanThreeDate = addTimeToDate(lastDuration.startTime, 3.getMilliDays())
                    futureDatesList+= FutureDateType(lessThanThreeDate,TypesOfFutureDates.BEFORE_THREE_DAYS_MASLA_WILL_CHANGE)
                }else{
                    val lessThanThreeDate = addTimeToDate(lastDuration.endDate, 3.getMilliDays())
                    futureDatesList+= FutureDateType(lessThanThreeDate,TypesOfFutureDates.BEFORE_THREE_DAYS_MASLA_WILL_CHANGE)
                }
                val endofHaiz = addTimeToDate(startOfAadat, fixedDurations.last().biggerThanTen!!.haiz)
                futureDatesList+= FutureDateType(endofHaiz, TypesOfFutureDates.END_OF_AADAT_HAIZ)
                val icForbiddenDate = endofHaiz
                if(typesOfMasla!=TypesOfMasla.MUBTADIA) {
                    futureDatesList += FutureDateType(icForbiddenDate, TypesOfFutureDates.IC_FORBIDDEN_DATE)
                }
                val ihtiyatiGhuslDate = endofHaiz
                futureDatesList+=FutureDateType(ihtiyatiGhuslDate, TypesOfFutureDates.IHTIYATI_GHUSL)
            }
        }
        else if(fixedDurations.last().
            biggerThanTen!!.istihazaAfter>=aadats.aadatTuhr){//daur
//            println("daur")
            if(lastDuration.type==DurationType.ISTIHAZA_AFTER && lastDuration.timeInMilliseconds<aadats.aadatTuhr){
                val endOfTuhr = addTimeToDate(lastDuration.startTime, aadats.aadatTuhr)
                futureDatesList+= FutureDateType(endOfTuhr,TypesOfFutureDates.END_OF_AADAT_TUHR)
            }else if(lastDuration.type==DurationType.HAIZ && lastDuration.timeInMilliseconds<aadats.aadatHaiz){
                val endOfHaiz = addTimeToDate(lastDuration.startTime, aadats.aadatHaiz)
                futureDatesList+= FutureDateType(endOfHaiz, TypesOfFutureDates.END_OF_AADAT_HAIZ)
                futureDatesList+= FutureDateType(endOfHaiz, TypesOfFutureDates.IHTIYATI_GHUSL)
                if(typesOfMasla!=TypesOfMasla.MUBTADIA) {
                    futureDatesList += FutureDateType(endOfHaiz, TypesOfFutureDates.IC_FORBIDDEN_DATE)
                }
            }else if(lastDuration.type==DurationType.HAIZ){
                val endOfTuhr = addTimeToDate(lastDuration.endDate, aadats.aadatTuhr)
                futureDatesList+= FutureDateType(endOfTuhr,TypesOfFutureDates.END_OF_AADAT_TUHR)
            }else if(lastDuration.type==DurationType.ISTIHAZA_AFTER){
                val threeDays = addTimeToDate(lastDuration.endDate, 3.getMilliDays())
                futureDatesList+= FutureDateType(threeDays,TypesOfFutureDates.BEFORE_THREE_DAYS)
                val endOfHaiz = addTimeToDate(lastDuration.endDate, aadats.aadatHaiz)
                futureDatesList+= FutureDateType(endOfHaiz, TypesOfFutureDates.END_OF_AADAT_HAIZ)
                futureDatesList+= FutureDateType(endOfHaiz, TypesOfFutureDates.IHTIYATI_GHUSL)
                if(typesOfMasla!=TypesOfMasla.MUBTADIA) {
                    futureDatesList += FutureDateType(endOfHaiz, TypesOfFutureDates.IC_FORBIDDEN_DATE)
                }
            }else if(lastDuration.type==DurationType.LESS_THAN_3_HAIZ){
                val threeDays = addTimeToDate(lastDuration.startTime, 3.getMilliDays())
                futureDatesList+= FutureDateType(threeDays, TypesOfFutureDates.BEFORE_THREE_DAYS)
                val endOfHaiz = addTimeToDate(lastDuration.startTime, aadats.aadatHaiz)
                futureDatesList+= FutureDateType(endOfHaiz, TypesOfFutureDates.END_OF_AADAT_HAIZ)
                futureDatesList+= FutureDateType(endOfHaiz, TypesOfFutureDates.IHTIYATI_GHUSL)
                if(typesOfMasla!=TypesOfMasla.MUBTADIA) {
                    futureDatesList += FutureDateType(endOfHaiz, TypesOfFutureDates.IC_FORBIDDEN_DATE)
                }
            }

        }
        else{//not daur
            if((qism==Soortain.A_1&&fixedDurations.last().biggerThanTen!!.istihazaAfter>0L) ||
                qism==Soortain.B_2||
                qism==Soortain.B_3){
                //these all end on istihaza
                val endOfTuhr = addTimeToDate(lastDuration.startTime, aadats.aadatTuhr)
                if(endOfTuhr.getMillisLong()!=lastDuration.endDate.getMillisLong()){
                    futureDatesList+= FutureDateType(endOfTuhr,TypesOfFutureDates.END_OF_AADAT_TUHR)
                }else if(endOfTuhr.getMillisLong()==lastDuration.endDate.getMillisLong()){
                    val endOfHaz = addTimeToDate(lastDuration.endDate, aadats.aadatHaiz)
                    futureDatesList+= FutureDateType(endOfHaz, TypesOfFutureDates.END_OF_AADAT_HAIZ)
                    futureDatesList+= FutureDateType(endOfHaz, TypesOfFutureDates.IHTIYATI_GHUSL)
                    if(typesOfMasla!=TypesOfMasla.MUBTADIA) {
                        futureDatesList += FutureDateType(endOfHaz, TypesOfFutureDates.IC_FORBIDDEN_DATE)
                    }
                }
            }else if(qism==Soortain.A_2){
                val previousHaizAadat = fixedDurations.last().biggerThanTen!!.hz
                val endOfHaiz = addTimeToDate(lastDuration.startTime, previousHaizAadat)
                futureDatesList+= FutureDateType(endOfHaiz, TypesOfFutureDates.END_OF_AADAT_HAIZ)
                if(typesOfMasla!=TypesOfMasla.MUBTADIA) {
                    futureDatesList += FutureDateType(endOfHaiz, TypesOfFutureDates.IC_FORBIDDEN_DATE)
                }
                futureDatesList+= FutureDateType(endOfHaiz, TypesOfFutureDates.IHTIYATI_GHUSL)
            }else if(qism==Soortain.A_1&&fixedDurations.last().biggerThanTen!!.istihazaAfter==0L){
                val endOfTuhr = addTimeToDate(lastDuration.endDate, aadats.aadatTuhr)
                futureDatesList+= FutureDateType(endOfTuhr,TypesOfFutureDates.END_OF_AADAT_TUHR)

            }
        }
    }
    else if(fixedDurations.last().type==DurationType.ISTEHAZA_AYYAMEQABLIYYA){
        val endOfIstehazaDate = addTimeToDate(fixedDurations.last().startDate,fixedDurations.last().ayyameqabliyya!!.ayyameqabliyya)
        futureDatesList+= FutureDateType(endOfIstehazaDate, TypesOfFutureDates.START_OF_AADAT_AYYAMEQABLIYYA)
        val tenDays = addTimeToDate(fixedDurations.last().startDate, 10.getMilliDays())
        if(tenDays.getMillisLong()>fixedDurations.last().endDate.getMillisLong()){
            futureDatesList+= FutureDateType(tenDays, TypesOfFutureDates.BEFORE_TEN_DAYS_AYYAMEQABLIYYAH)
        }

    }
    else if(fixedDurations.last().days<10 &&
        (fixedDurations.last().type==DurationType.DAM||
                fixedDurations.last().type==DurationType.DAM_MUBTADIA)){
        //we should be using previous aadat in this case
        val previousAadat = getSecondLastAadatOfHaizAndTuhr(adatsOfHaizList,adatsOfTuhrList, fixedDurations, typesOfMasla).aadatHaiz
        if(previousAadat!=-1L){//if aadat of haiz exists
            var endOfAadat = addTimeToDate(fixedDurations.last().startDate, previousAadat)
            val tenDays = addTimeToDate(fixedDurations.last().startDate, 10.getMilliDays())
            if(fixedDurations.last().days<3){//this is less than 3 dam, so prior aadat
                val threeDays = addTimeToDate(fixedDurations.last().startDate, 3.getMilliDays())
                futureDatesList+=FutureDateType(threeDays, TypesOfFutureDates.BEFORE_THREE_DAYS)
                if(typesOfMasla!=TypesOfMasla.MUBTADIA){
                    futureDatesList+=FutureDateType(endOfAadat, TypesOfFutureDates.IC_FORBIDDEN_DATE)
                }
                futureDatesList+=FutureDateType(tenDays,TypesOfFutureDates.AFTER_TEN_DAYS)

                //ihtiyati ghusl calculation
                val ihtiyatiGhuslTime = ihtiyatiGhuslCalc(fixedDurations, adatsOfHaizList, adatsOfTuhrList, inputtedMawjoodaTuhr, typesOfMasla)
                if (ihtiyatiGhuslTime!=null){
                    futureDatesList+=FutureDateType(ihtiyatiGhuslTime,TypesOfFutureDates.IHTIYATI_GHUSL)
                }

            }else if(previousAadat!=-1L&&
                fixedDurations.last().timeInMilliseconds<previousAadat){
                //there is a prior aadat of haiz, and this is less than aadat, more than 3

                endOfAadat = addTimeToDate(fixedDurations.last().startDate, previousAadat)
                if(typesOfMasla!=TypesOfMasla.MUBTADIA){
                    futureDatesList += FutureDateType(endOfAadat, TypesOfFutureDates.IC_FORBIDDEN_DATE)
                }
                futureDatesList+=FutureDateType(tenDays,TypesOfFutureDates.AFTER_TEN_DAYS)

                //ihtiyati ghusl calculation
                val ihtiyatiGhuslTime = ihtiyatiGhuslCalc(fixedDurations, adatsOfHaizList, adatsOfTuhrList, inputtedMawjoodaTuhr, typesOfMasla)
                if (ihtiyatiGhuslTime!=null){
                    futureDatesList+=FutureDateType(ihtiyatiGhuslTime,TypesOfFutureDates.IHTIYATI_GHUSL)
                }
            }
            else if(previousAadat!=-1L&&
                fixedDurations.last().timeInMilliseconds>=previousAadat){
                futureDatesList+=FutureDateType(tenDays, TypesOfFutureDates.AFTER_TEN_DAYS)
                //ihtiyati ghusl calculation
                val ihtiyatiGhuslTime = ihtiyatiGhuslCalc(fixedDurations, adatsOfHaizList, adatsOfTuhrList, inputtedMawjoodaTuhr, typesOfMasla)
                if (ihtiyatiGhuslTime!=null){
                    futureDatesList+=FutureDateType(ihtiyatiGhuslTime,TypesOfFutureDates.IHTIYATI_GHUSL)
                }
            }
        }
        else{//blood is less than 10 and adat of haiz doesn't exist
            if(fixedDurations.last().type == DurationType.DAM_MUBTADIA){
                val after10Days = addTimeToDate(fixedDurations.last().startDate, 10.getMilliDays())
                futureDatesList+=FutureDateType(after10Days, TypesOfFutureDates.END_OF_AADAT_HAIZ)
            }
            else{//mutadah or nifas
                val after10Days = addTimeToDate(fixedDurations.last().startDate, 10.getMilliDays())
                futureDatesList+=FutureDateType(after10Days, TypesOfFutureDates.AFTER_TEN_DAYS)
            }
        }
    }
    else if(fixedDurations.last().timeInMilliseconds==10.getMilliDays()&&
        (fixedDurations.last().type==DurationType.DAM||
                fixedDurations.last().type==DurationType.DAM_MUBTADIA)){
        if(fixedDurations.last().type==DurationType.DAM_MUBTADIA){
            futureDatesList+=FutureDateType(Instant.EPOCH, TypesOfFutureDates.TEN_DAYS_EXACTLY)
//            var endOfTuhr = addTimeToDate(fixedDurations.last().startDate, 30.millisFromDays())
//            futureDatesList+=FutureDateType(endOfTuhr,TypesOfFutureDates.END_OF_AADAT_TUHR)

        }else{//regular dam mutadah, bleeding is 10
            futureDatesList+=FutureDateType(Instant.EPOCH, TypesOfFutureDates.TEN_DAYS_EXACTLY)
        }
    }
    else if(fixedDurations.last().days>40 && fixedDurations.last().type==DurationType.DAM_IN_NIFAS_PERIOD){
        val lastDuration=fixedDurations.last().biggerThanForty!!.durationsList.last()
        if(lastDuration.type==DurationType.ISTIHAZA_AFTER && lastDuration.timeInMilliseconds<aadats.aadatTuhr){
            val endOfTuhr = addTimeToDate(lastDuration.startTime, aadats.aadatTuhr)
            futureDatesList+= FutureDateType(endOfTuhr,TypesOfFutureDates.END_OF_AADAT_TUHR)
        }else if(lastDuration.type==DurationType.HAIZ && lastDuration.timeInMilliseconds<aadats.aadatHaiz){
            val endOfHaiz = addTimeToDate(lastDuration.startTime, aadats.aadatHaiz)
            futureDatesList+= FutureDateType(endOfHaiz, TypesOfFutureDates.END_OF_AADAT_HAIZ)
            futureDatesList+= FutureDateType(endOfHaiz, TypesOfFutureDates.IHTIYATI_GHUSL)
            futureDatesList+= FutureDateType(endOfHaiz, TypesOfFutureDates.IC_FORBIDDEN_DATE)
        }else if(lastDuration.type==DurationType.HAIZ){
            val endOfTuhr = addTimeToDate(lastDuration.startTime, aadats.aadatTuhr)
            futureDatesList+= FutureDateType(endOfTuhr,TypesOfFutureDates.END_OF_AADAT_TUHR)
        }else if(lastDuration.type==DurationType.ISTIHAZA_AFTER){
            val endOfHaiz = addTimeToDate(lastDuration.startTime, aadats.aadatHaiz)
            futureDatesList+= FutureDateType(endOfHaiz, TypesOfFutureDates.END_OF_AADAT_HAIZ)
            futureDatesList+= FutureDateType(endOfHaiz, TypesOfFutureDates.IHTIYATI_GHUSL)
            futureDatesList+= FutureDateType(endOfHaiz, TypesOfFutureDates.IC_FORBIDDEN_DATE)
        }
    }
    else if(fixedDurations.last().type==DurationType.DAM_IN_NIFAS_PERIOD&&
        fixedDurations.last().days<=40){
        var nifasAadat = 40.getMilliDays()
        if(aadatNifas!=null){nifasAadat=aadatNifas}
        val fortyDays = addTimeToDate(fixedDurations.last().startDate, 40.getMilliDays())
        futureDatesList+=FutureDateType(fortyDays, TypesOfFutureDates.FORTY_DAYS)
        if(fixedDurations.last().timeInMilliseconds<nifasAadat&&
            aadatNifas!=40.getMilliDays()){
            val aadatNifasEnd = addTimeToDate(fixedDurations.last().startDate, nifasAadat)
            futureDatesList+=FutureDateType(aadatNifasEnd,TypesOfFutureDates.IC_FORBIDDEN_DATE)
        }
    }
    else if(fixedDurations.last().type == DurationType.DAM_MUBTADIA&&
        fixedDurations.last().days>10){
        var izitrariAadatHaiz=-1L

        if(adatsOfHaizList.size>1){
            if(adatsOfHaizList.last().index!=fixedDurations.lastIndex){
                izitrariAadatHaiz=adatsOfHaizList.last().aadat
            }else{
                izitrariAadatHaiz=adatsOfHaizList[adatsOfHaizList.lastIndex-1].aadat
            }
        }
        if(izitrariAadatHaiz==-1L){izitrariAadatHaiz=10.getMilliDays()}
        val iztirariTuhrAadat = 30.getMilliDays()-izitrariAadatHaiz
        val lastDuration= fixedDurations.last().biggerThanTen!!.durationsList.last()
        if(lastDuration.type==DurationType.ISTIHAZA_AFTER && lastDuration.timeInMilliseconds<iztirariTuhrAadat){
            val endOfTuhr = addTimeToDate(lastDuration.startTime, iztirariTuhrAadat)
            futureDatesList+= FutureDateType(endOfTuhr,TypesOfFutureDates.END_OF_AADAT_TUHR)
        }else if(lastDuration.type==DurationType.HAIZ && lastDuration.timeInMilliseconds<izitrariAadatHaiz){
            val endOfHaiz = addTimeToDate(lastDuration.startTime, izitrariAadatHaiz)
            futureDatesList+= FutureDateType(endOfHaiz, TypesOfFutureDates.END_OF_AADAT_HAIZ)
            futureDatesList+= FutureDateType(endOfHaiz, TypesOfFutureDates.IHTIYATI_GHUSL)
        }else if(lastDuration.type==DurationType.HAIZ){
            val endOfTuhr = addTimeToDate(lastDuration.endDate, iztirariTuhrAadat)
            futureDatesList+= FutureDateType(endOfTuhr,TypesOfFutureDates.END_OF_AADAT_TUHR)
        }else if(lastDuration.type==DurationType.ISTIHAZA_AFTER){
            val endOfHaiz = addTimeToDate(lastDuration.endDate, izitrariAadatHaiz)
            val threeDays = addTimeToDate(lastDuration.endDate, 3.getMilliDays())
            futureDatesList+= FutureDateType(threeDays, TypesOfFutureDates.BEFORE_THREE_DAYS)
            futureDatesList+= FutureDateType(endOfHaiz, TypesOfFutureDates.END_OF_AADAT_HAIZ)
            futureDatesList+= FutureDateType(endOfHaiz, TypesOfFutureDates.IHTIYATI_GHUSL)
        }else if(lastDuration.type==DurationType.LESS_THAN_3_HAIZ){
            val endOfHaiz = addTimeToDate(lastDuration.startTime, izitrariAadatHaiz)
            val threeDays = addTimeToDate(lastDuration.startTime, 3.getMilliDays())
            futureDatesList+= FutureDateType(threeDays, TypesOfFutureDates.BEFORE_THREE_DAYS)
            futureDatesList+= FutureDateType(endOfHaiz, TypesOfFutureDates.END_OF_AADAT_HAIZ)
            futureDatesList+= FutureDateType(endOfHaiz, TypesOfFutureDates.IHTIYATI_GHUSL)
        }
    }
    return futureDatesList
}

fun finalAadatsOfHaizAndTuhr(fixedDurations: MutableList<FixedDuration>,
                             inputtedAadatTuhr: Long?,
                             inputtedMawjoodaTuhr: Long?,
                             isMawjoodaFasid: Boolean,
                             adatsOfHaizList: MutableList<AadatAfterIndexOfFixedDuration>,
                             adatsOfTuhrList: MutableList<AadatAfterIndexOfFixedDuration>): AadatsOfHaizAndTuhr{
    if(fixedDurations.last().type==DurationType.DAM &&
        fixedDurations.last().days>10 &&
        fixedDurations.last().biggerThanTen!!.qism==Soortain.A_3 &&
        fixedDurations.last().biggerThanTen!!.gp-fixedDurations.last().biggerThanTen!!.mp<=
        fixedDurations.last().timeInMilliseconds){
        //A-3 shifting to A-2, has entered aadat, but not 3 days into makan yet.
        //we will give it previous aadat of haiz and tuhr, according to A-1, though it could remain A-3
        //as this is an unusual and unique thing, we deal with it first

        //if the current tuhr was fasid, nd hence not aadat, then we give the last tuhr as habit,
        // otherwise the one before it
        if(adatsOfTuhrList.last().aadat==fixedDurations.last().biggerThanTen!!.mp &&
                adatsOfTuhrList.last().aadat!=fixedDurations.last().biggerThanTen!!.gp){
            //the last aadat we have is the mawjoodah paki,
            // so we return the tuhr before it as aadat
            return AadatsOfHaizAndTuhr(adatsOfHaizList[adatsOfHaizList.lastIndex-1].aadat,
                adatsOfTuhrList[adatsOfTuhrList.lastIndex-1].aadat)
        }else{
            //the last aadat is not mawjooda paki for whatever reason,
            //so we return the last tuhr as aadat
            return AadatsOfHaizAndTuhr(adatsOfHaizList[adatsOfHaizList.lastIndex-1].aadat,
                adatsOfTuhrList[adatsOfTuhrList.lastIndex].aadat)

        }
    }
    if(fixedDurations.last().type==DurationType.ISTEHAZA_AYYAMEQABLIYYA){
        return AadatsOfHaizAndTuhr(fixedDurations.last().ayyameqabliyya!!.aadatHaiz, fixedDurations.last().ayyameqabliyya!!.aadatTuhr)
    }
    if(adatsOfHaizList.last().index!=-1 &&
        adatsOfHaizList[adatsOfHaizList.lastIndex-1].index==fixedDurations.lastIndex){
        //Anam says, that in case of daur, if it ends at less than the aadat of haiz...
        //we give previous aadat of haiz, and not the current one
        //when there are 2 aadats at same index, that indicates daur, with
        // an aadat change at the end of it. So if the aadat changed at the last index, twice
        //there was daur.
        return AadatsOfHaizAndTuhr(adatsOfHaizList[adatsOfHaizList.lastIndex-1].aadat, adatsOfTuhrList.last().aadat)
    }

    return AadatsOfHaizAndTuhr(adatsOfHaizList.last().aadat,adatsOfTuhrList.last().aadat)


//    if(fixedDurations.last().type==DurationType.DAM&&
//        fixedDurations.last().days>10) {//ends at bigger than 10 mutadah
//        val lastDurationOfBiggerThanTen = fixedDurations.last().biggerThanTen!!.durationsList.last()
//        var haizAadat = -1L
//        var tuhrAadat = -1L
//
//        if(fixedDurations.last().biggerThanTen!!.qism==Soortain.A_3 &&
//                fixedDurations.last().biggerThanTen!!.gp-fixedDurations.last().biggerThanTen!!.mp<=
//            fixedDurations.last().timeInMilliseconds){
//            //A-3 shifting to A-2, has entered aadat, but not 3 days yet
//            //we will give it previous aadat of haiz and tuhr, according to A-1, though it could remain A-3
//            //as this is an unusual and unique thing, we deal with it first
//            haizAadat=adatsOfHaizList[adatsOfHaizList.lastIndex-1].aadat
//            tuhrAadat=adatsOfTuhrList[adatsOfTuhrList.lastIndex-1].aadat
//        }else{
//            if (lastDurationOfBiggerThanTen.type == DurationType.ISTIHAZA_AFTER||
//                lastDurationOfBiggerThanTen.type == DurationType.LESS_THAN_3_HAIZ) {
//                //if it ended in paki, last aadats are the right ones
//                haizAadat=adatsOfHaizList.last().aadat
//                tuhrAadat=adatsOfTuhrList.last().aadat
//
//            } else {
//                //it ended in a haiz, bigger than 3, less than aadat.
//                //Anam says we gotta give previous aadat here
//                haizAadat=adatsOfHaizList[adatsOfHaizList.lastIndex-1].aadat
//                tuhrAadat=adatsOfTuhrList.last().aadat
//            }
//        }
//        if(lastDurationOfBiggerThanTen.type==DurationType.HAIZ &&
//            lastDurationOfBiggerThanTen.timeInMilliseconds>fixedDurations.last().biggerThanTen!!.haiz){
//            //this is probably unnecessary
//            //TODO: we should write a test case for ikhtilafi masla
//            //for ikhtilafi masla
//            haizAadat=lastDurationOfBiggerThanTen.timeInMilliseconds
//        }
//
//        return AadatsOfHaizAndTuhr(haizAadat,tuhrAadat)
//    }
//    else if(fixedDurations.last().type==DurationType.DAM_MUBTADIA&&
//        fixedDurations.last().days>10){
//        //this is a bigger than 10 mubtadia dam and the last thing
//        return AadatsOfHaizAndTuhr(adatsOfHaizList.last().aadat, adatsOfTuhrList.last().aadat)
//    }
//    else if(fixedDurations.last().days>40 &&
//        fixedDurations.last().type==DurationType.DAM_IN_NIFAS_PERIOD){
//        val lastDurationBiggerThanForty = fixedDurations.last().biggerThanForty!!.durationsList.last()
//
//        return if(lastDurationBiggerThanForty.type==DurationType.ISTIHAZA_AFTER||
//            lastDurationBiggerThanForty.type==DurationType.LESS_THAN_3_HAIZ){
//            //if it ended in paki or in haiz less than 3, no tension
//            AadatsOfHaizAndTuhr(adatsOfHaizList.last().aadat, adatsOfTuhrList.last().aadat)
//        }else{//it ended in haiz, less than previous aadat
//            //it ended in a hiaz more than 3. We are not going to give that haiz as aadat
//            AadatsOfHaizAndTuhr(adatsOfHaizList[adatsOfHaizList.lastIndex-1].aadat, adatsOfTuhrList.last().aadat)
//        }
//    }
//    else if(fixedDurations.last().days<=10 &&
//        (fixedDurations.last().type==DurationType.DAM||
//                fixedDurations.last().type==DurationType.DAM_MUBTADIA)){
//        return AadatsOfHaizAndTuhr(adatsOfHaizList.last().aadat,adatsOfTuhrList.last().aadat)
//    }
//    else if(fixedDurations.last().days<=40 &&
//        fixedDurations.last().type==DurationType.DAM_IN_NIFAS_PERIOD){
//        return AadatsOfHaizAndTuhr(adatsOfHaizList.last().aadat, adatsOfTuhrList.last().aadat )
//    }
//    else if(fixedDurations.last().type==DurationType.ISTEHAZA_AYYAMEQABLIYYA){
//        return AadatsOfHaizAndTuhr(fixedDurations.last().ayyameqabliyya!!.aadatHaiz, fixedDurations.last().ayyameqabliyya!!.aadatTuhr)
//    }
//    return AadatsOfHaizAndTuhr(-1L,-1L)
}

fun getSecondLastAadatOfHaizAndTuhr(
    adatsOfHaizList: MutableList<AadatAfterIndexOfFixedDuration>, adatsOfTuhrList:MutableList<AadatAfterIndexOfFixedDuration>,
    fixedDurations: MutableList<FixedDuration>, typesOfMasla: TypesOfMasla):AadatsOfHaizAndTuhr{
    var previousAadatOfHaiz = -1L
    var previousAadatOfTuhr = -1L
    var lastFixedDurationsIndex = fixedDurations.lastIndex
    if(typesOfMasla==TypesOfMasla.NIFAS){//because of addition of haml and wiladat
        lastFixedDurationsIndex -= 2
    }
    if(adatsOfHaizList.last().index!=lastFixedDurationsIndex){
        previousAadatOfHaiz = adatsOfHaizList.last().aadat
    }else{
        if(adatsOfHaizList.size>1){
            previousAadatOfHaiz = adatsOfHaizList[adatsOfHaizList.lastIndex-1].aadat
        }
    }
    if(adatsOfTuhrList.last().index!=lastFixedDurationsIndex){
        previousAadatOfTuhr = adatsOfTuhrList.last().aadat
    }else{
        if(adatsOfTuhrList.size>1){
            previousAadatOfTuhr = adatsOfTuhrList[adatsOfTuhrList.lastIndex-1].aadat
        }
    }
    return AadatsOfHaizAndTuhr(previousAadatOfHaiz,previousAadatOfTuhr)
}

fun calculateFilHaal(fixedDurations: MutableList<FixedDuration>,
                     adatsOfHaizList: MutableList<AadatAfterIndexOfFixedDuration>,
                     adatsOfTuhrList: MutableList<AadatAfterIndexOfFixedDuration>,
                     inputtedMawjoodaTuhr: Long?): Boolean?{
    //calculate filHaal status
    var filHaalPaki:Boolean? = false

    if(fixedDurations.last().type==DurationType.DAM){

        if(fixedDurations.last().days>10){
            val aadatTuhr = fixedDurations.last().biggerThanTen!!.aadatTuhr
            val aadatHaz = fixedDurations.last().biggerThanTen!!.hz //previous aadat
            val lastDurationType = fixedDurations.last().biggerThanTen!!.durationsList.last().type
            val lastDurationTime = fixedDurations.last().biggerThanTen!!.durationsList.last().timeInMilliseconds
            if(lastDurationType==DurationType.LESS_THAN_3_HAIZ){
                filHaalPaki=false
            }else if(lastDurationType==DurationType.HAIZ){//A-2, A-1, daur
//                if(aadatHaz>lastDurationTime){//in A-2, and daur only
//                    filHaalPaki=false
//                }else {//A-1, //daur
//                    filHaalPaki = true
//                }
                filHaalPaki = aadatHaz <= lastDurationTime
            }else if(lastDurationType==DurationType.ISTIHAZA_AFTER){//daur, A-3, B-3, B-2
                if(aadatTuhr>lastDurationTime){
                    val qism = fixedDurations.last().biggerThanTen!!.qism
                    //this isn't actually about ayyame qabliya
                    val ayyameQabliyya = fixedDurations.last().biggerThanTen!!.gp-fixedDurations.last().biggerThanTen!!.mp
//                    if(qism==Soortain.A_3 && //A-3 entered into aadat
//                        ayyameQabliyya<=fixedDurations.last().timeInMilliseconds) {
//                        filHaalPaki=false
//                    }else{
//                        filHaalPaki=true
//                    }
                    filHaalPaki = !(qism==Soortain.A_3 && //A-3 entered into aadat
                            ayyameQabliyya<=fixedDurations.last().timeInMilliseconds)
                }else{
                    filHaalPaki=false
                }
            }
        }else if(fixedDurations.last().timeInMilliseconds==10.getMilliDays()){
            filHaalPaki=null

//            //if we had an aadat, figure out filhaal
//            var gp = adatsOfTuhrList.last().aadat
//            var hz = -1L
//            if(adatsOfHaizList.size>1){
//                hz = adatsOfHaizList[adatsOfHaizList.lastIndex-1].aadat
//            }
//            var mp = -1L
//            if(inputtedMawjoodaTuhr!=null){
//                mp=inputtedMawjoodaTuhr
//            }
//            if(fixedDurations.size>1&&
//                (fixedDurations[fixedDurations.lastIndex-1].type == DurationType.TUHR||
//                        fixedDurations[fixedDurations.lastIndex-1].type==DurationType.TUHREFAASID||
//                        fixedDurations[fixedDurations.lastIndex-1].type==DurationType.TUHR_MUBTADIA_BECAME_A_MUTADA_NOW)){
//                mp = fixedDurations[fixedDurations.lastIndex-1].timeInMilliseconds+fixedDurations[fixedDurations.lastIndex-1].istihazaAfter
//            }
//            if(mp!=-1L&&gp!=-1L&&hz!=-1L){
//                if(mp>gp){//B
//                    filHaalPaki=true
//                }else{//A
//                    if(gp-mp+hz<=10.millisFromDays()){//A-1
//                        filHaalPaki=true
//                    }else if(mp+10.millisFromDays()>=gp){//has entered or is about to enter aadat
//                        filHaalPaki=false
//                    }else{//A-3
//                        filHaalPaki=true
//                    }
//                }
//            }else{
//                filHaalPaki=null
//            }
        }else{
            filHaalPaki=false
        }
    }else if(fixedDurations.last().type==DurationType.DAM_MUBTADIA){
        if(fixedDurations.last().days>10){
            val aadatTuhr:Long
            var aadatHaz = -1L
            if(adatsOfHaizList.size>1){
                if(adatsOfHaizList.last().index!=fixedDurations.lastIndex){
                    aadatHaz=adatsOfHaizList.last().aadat
                }else{
                    aadatHaz=adatsOfHaizList[adatsOfHaizList.lastIndex-1].aadat
                }
            }

            val lastDurationType = fixedDurations.last().biggerThanTen!!.durationsList.last().type
            val lastDurationTime = fixedDurations.last().biggerThanTen!!.durationsList.last().timeInMilliseconds
            if(aadatHaz==-1L){
                aadatHaz=10.getMilliDays()
            }
            aadatTuhr=30.getMilliDays()-aadatHaz
            if(lastDurationType==DurationType.LESS_THAN_3_HAIZ){
                filHaalPaki=false
            }else if(lastDurationType==DurationType.HAIZ){
//                if(aadatHaz>lastDurationTime){
//                    filHaalPaki=false
//                }else{
//                    filHaalPaki=true
//                }
                filHaalPaki = aadatHaz <= lastDurationTime
            }else if(lastDurationType==DurationType.ISTIHAZA_AFTER){
//                if(aadatTuhr>lastDurationTime){
//                    filHaalPaki=true
//                }else{
//                    filHaalPaki=false
//                }
                filHaalPaki = aadatTuhr > lastDurationTime
            }
        }else if(fixedDurations.last().timeInMilliseconds==10.getMilliDays()){
            filHaalPaki=null
        }
    }else if(fixedDurations.last().type==DurationType.DAM_IN_NIFAS_PERIOD){
        if(fixedDurations.last().days>40){
            val aadatTuhr=fixedDurations.last().biggerThanForty!!.aadatTuhr
            val aadatHaz = fixedDurations.last().biggerThanForty!!.aadatHaiz
            val lastDurationType = fixedDurations.last().biggerThanForty!!.durationsList.last().type
            val lastDurationTime = fixedDurations.last().biggerThanForty!!.durationsList.last().timeInMilliseconds
            if(lastDurationType==DurationType.ISTIHAZA_AFTER){
//                if(aadatTuhr==-1L){
//                    filHaalPaki=true
//                }else if(aadatTuhr>lastDurationTime){
//                    filHaalPaki=true
//                }else{
//                    filHaalPaki=false
//                }
                filHaalPaki = if(aadatTuhr==-1L) true else aadatTuhr>lastDurationTime
            }else if(lastDurationType==DurationType.HAIZ){
//                if(aadatHaz==-1L){
//                    //this shouldn't happen
//                    filHaalPaki=false
//                }else if(aadatHaz>lastDurationTime){
//                    filHaalPaki=false
//                }else{
//                    filHaalPaki=true
//                }
                filHaalPaki = if(aadatHaz==-1L) false else aadatHaz <= lastDurationTime
            }else if(lastDurationType==DurationType.LESS_THAN_3_HAIZ){
                filHaalPaki = false
            }
        }
    }else if(fixedDurations.last().type == DurationType.ISTEHAZA_AYYAMEQABLIYYA) {
        if(fixedDurations.last().timeInMilliseconds>=fixedDurations.last().ayyameqabliyya!!.ayyameqabliyya) {
            filHaalPaki = false
        }else {
            filHaalPaki=null
        }
    }else {
        filHaalPaki = false
    }
    return filHaalPaki
}

fun ihtiyatiGhuslCalc(fixedDurations: MutableList<FixedDuration>,
                      adatsOfHaizList: MutableList<AadatAfterIndexOfFixedDuration>,
                      adatsOfTuhrList: MutableList<AadatAfterIndexOfFixedDuration>,
                      inputtedMawjoodaTuhr: Long?, typesOfMasla: TypesOfMasla):Instant?{
    //ihtiyati ghusl calculation
    var ihtiyatiGhuslTime:Instant? = null
    var mp = -1L
    var gp = getSecondLastAadatOfHaizAndTuhr(adatsOfHaizList,adatsOfTuhrList,fixedDurations,typesOfMasla).aadatTuhr
    val hz = getSecondLastAadatOfHaizAndTuhr(adatsOfHaizList, adatsOfTuhrList,fixedDurations, typesOfMasla).aadatHaiz

    if(fixedDurations.size>1 &&
        (fixedDurations[fixedDurations.size-2].type==DurationType.TUHR||
                fixedDurations[fixedDurations.size-2].type==DurationType.TUHREFAASID||
                fixedDurations[fixedDurations.size-2].type==DurationType.TUHREFAASID_WITH_ISTEHAZA||
                fixedDurations[fixedDurations.size-2].type==DurationType.TUHR_MUBTADIA||
                fixedDurations[fixedDurations.size-2].type==DurationType.TUHREFAASID_MUBTADIA||
                fixedDurations[fixedDurations.size-2].type==DurationType.TUHREFAASID_MUBTADIA_WITH_ISTEHAZA||
                fixedDurations[fixedDurations.size-2].type==DurationType.TUHR_MUBTADIA_BECAME_A_MUTADA_NOW)){
        mp = fixedDurations[fixedDurations.size-2].timeInMilliseconds+
                fixedDurations[fixedDurations.lastIndex-1].istihazaAfter

    }else if(inputtedMawjoodaTuhr!=null){
        mp=inputtedMawjoodaTuhr
    }
    if(mp!=-1L && gp!=-1L && hz!=-1L && fixedDurations.last().type==DurationType.DAM){
        if(mp>=gp){//aadat has already started, and may have ended
            if(hz-(mp-gp)>=3.getMilliDays()){
                ihtiyatiGhuslTime=addTimeToDate(fixedDurations.last().startDate, hz-(mp-gp))//B-2
            }else{
                ihtiyatiGhuslTime=addTimeToDate(fixedDurations.last().startDate, hz)
            }
        }else{//A
            val crossingTen = addTimeToDate(fixedDurations.last().startDate, 10.getMilliDays())
            if(crossingTen.getMillisLong()>=addTimeToDate(fixedDurations.last().startDate, gp-mp).getMillisLong()){
                //crossing 10 puts it in aadat or after it
                ihtiyatiGhuslTime=addTimeToDate(fixedDurations.last().startDate, (gp-mp+hz))
            }else{//A-3
                    ihtiyatiGhuslTime=addTimeToDate(fixedDurations.last().startDate, hz)
            }

        }
    }else if(fixedDurations.last().type==DurationType.DAM_MUBTADIA){
        gp = 30.getMilliDays() - hz //iztirari tuhr aadat
        if (mp>gp){
            ihtiyatiGhuslTime = addTimeToDate(fixedDurations.last().startDate, hz)
        }else{
            ihtiyatiGhuslTime = addTimeToDate(fixedDurations.last().startDate, mp-gp+hz)
        }
    }

    if (ihtiyatiGhuslTime != null) {
        if(ihtiyatiGhuslTime.getMillisLong()>fixedDurations.last().endDate.getMillisLong() &&
            ihtiyatiGhuslTime.getMillisLong()!=Instant.EPOCH.getMillisLong()){
            return ihtiyatiGhuslTime
        }
    }
    return null
}

fun putMawjoodahPakiInFixedDurations(fixedDurations: MutableList<FixedDuration>, allTheInputs: AllTheInputs){
    if(allTheInputs.typeOfInput==TypesOfInputs.DURATION &&
        allTheInputs.preMaslaValues.inputtedMawjoodahTuhr!=null &&
        allTheInputs.preMaslaValues.inputtedMawjoodahTuhr!=-1L){
        if(fixedDurations[0].type==DurationType.DAM){
            val startTime = addTimeToDate(fixedDurations[0].startDate, -allTheInputs.preMaslaValues.inputtedMawjoodahTuhr!!)
            if(allTheInputs.preMaslaValues.isMawjoodaFasid){
                fixedDurations.add(0, FixedDuration(DurationType.TUHREFAASID,
                    allTheInputs.preMaslaValues.inputtedMawjoodahTuhr!!, startDate = startTime))
            }else{
                fixedDurations.add(0, FixedDuration(DurationType.TUHR,
                    allTheInputs.preMaslaValues.inputtedMawjoodahTuhr!!, startDate = startTime))
            }
        }
        else if(fixedDurations[0].type==DurationType.HAML){
            if(allTheInputs.preMaslaValues.isMawjoodaFasid){//this is tuhr in haml
                fixedDurations.add(1, FixedDuration(DurationType.TUHR_IN_HAML,
                    allTheInputs.preMaslaValues.inputtedMawjoodahTuhr!!, startDate = fixedDurations[0].startDate))
                //start date is the same as last one, cuz haml has 0 length
            }else{//this is tuhr before haml
                val startTime = addTimeToDate(fixedDurations[0].startDate, -allTheInputs.preMaslaValues.inputtedMawjoodahTuhr!!)
                if(allTheInputs.preMaslaValues.isMawjoodaFasid){
                    fixedDurations.add(0, FixedDuration(DurationType.TUHREFAASID,
                        allTheInputs.preMaslaValues.inputtedMawjoodahTuhr!!, startDate = startTime))
                }else{
                    fixedDurations.add(0, FixedDuration(DurationType.TUHR,
                        allTheInputs.preMaslaValues.inputtedMawjoodahTuhr!!, startDate = startTime))
                }
            }
        }

    }
}
