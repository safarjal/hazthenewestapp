import kotlinx.browser.window
import kotlin.js.Date

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

lateinit var firstStartTime:Date

fun handleEntries(entries: List<Entry>, istimrar:Boolean, inputtedAadatHaz:Double?, inputtedAadatTuhr:Double?, isDateOnly:Boolean, isPregnancy: Boolean, pregnancy: Pregnancy): OutputTexts {
    firstStartTime = entries[0].startTime
    val times = entries
        .flatMap { entry -> listOf(entry.startTime, entry.endTime) }
        .map { it.getTime().toLong() }
    require(times == times.sorted())
    //step 1 - create an array of dam and tuhur durations
    var isDam = true
    val durations = times.zipWithNext { firstTime, secondTime ->
        val type = if (isDam) DurationType.DAM else DurationType.TUHR
        isDam = !isDam
        Duration(type, secondTime - firstTime, Date(firstTime))
    }
    val fixedDurations = durations
        .map { duration ->
            FixedDuration(duration.type, duration.timeInMilliseconds,startDate = duration.startTime)
        }
        .toMutableList()
    addIndicesToFixedDurations(fixedDurations)

    if(istimrar){//make the last period an istimrar type
        fixedDurations[fixedDurations.size-1].type=DurationType.ISTIMRAR
    }
    if(isPregnancy){
        addStartDateToFixedDurations(fixedDurations)

        markAllTuhrsInPregnancyAsHaml(fixedDurations, pregnancy)
        //the above also added start of pregnancy

        if(!pregnancy.mustabeenUlKhilqat){
            //if it's not mustabeen ulkhilqat, deal with it like haiz
            removeTuhrLessThan15(fixedDurations)
            removeTuhrLessThan15InPregnancy(fixedDurations)
            removeDamLessThan3(fixedDurations)
            addStartDateToFixedDurations(fixedDurations)
            dealWithBiggerThan10Dam(fixedDurations, inputtedAadatHaz,inputtedAadatTuhr)
            addDurationsToDams(fixedDurations)
            addWiladat(fixedDurations, pregnancy)
            addStartOfPregnancy(fixedDurations, pregnancy)
            val endingOutputValues = calculateEndingOutputValues(fixedDurations)
            return generateOutputStringPregnancy(fixedDurations, durations, isDateOnly, pregnancy, endingOutputValues)
        }else{         //it is mustabeen ul khilqat
            //mark all dam in pregnancy as isithaza.
            markAllDamsInPregnancyAsHaml(fixedDurations, pregnancy)
            removeTuhrLessThan15(fixedDurations)//do this before the next, cuz why not, mkes thigns simpler in joining dams
            addStartDateToFixedDurations(fixedDurations)//cuz the last shoulda messed it up
            makeAllDamInFortyAfterWiladatAsMuttasil(fixedDurations,pregnancy) //also, marking them as Dam in
            dealWithDamInMuddateNifas(fixedDurations,pregnancy)
            removeDamLessThan3(fixedDurations) //this won't effect dam in muddat e haml
            addStartDateToFixedDurations(fixedDurations)
            dealWithBiggerThan10Dam(fixedDurations, inputtedAadatHaz,inputtedAadatTuhr)
            addDurationsToDams(fixedDurations)
            addWiladat(fixedDurations, pregnancy)
            addStartOfPregnancy(fixedDurations, pregnancy)
            val endingOutputValues = calculateEndingOutputValues(fixedDurations)
            return generateOutputStringPregnancy(fixedDurations, durations, isDateOnly, pregnancy, endingOutputValues)
        }
    }else{//is not pregnancy
        removeTuhrLessThan15(fixedDurations)
        removeDamLessThan3(fixedDurations)
        addStartDateToFixedDurations(fixedDurations)
        dealWithBiggerThan10Dam(fixedDurations, inputtedAadatHaz,inputtedAadatTuhr)
        addDurationsToDams(fixedDurations)
        val endingOutputValues = calculateEndingOutputValues(fixedDurations)
        return generateOutputString(fixedDurations, durations, isDateOnly, endingOutputValues)
    }
}
fun dealWithDamInMuddateNifas(fixedDurations:MutableList<FixedDuration>,pregnancy:Pregnancy){
    var i = 0
    while (i<fixedDurations.size){
        if(fixedDurations[i].type==DurationType.DAM_IN_NIFAAS_PERIOD){
            if(fixedDurations[i].timeInMilliseconds > 40*MILLISECONDS_IN_A_DAY){
                //if nifas exceeded 40
                if(pregnancy.aadatNifas==null){
                    pregnancy.aadatNifas=40.0
                }
                val istihazaAfter = (fixedDurations[i].timeInMilliseconds-pregnancy.aadatNifas!!*MILLISECONDS_IN_A_DAY).toLong()
                val nifasInfo = BiggerThanFortyNifas(pregnancy.aadatNifas!!.toLong()*MILLISECONDS_IN_A_DAY, istihazaAfter,-1, -1,
                    mutableListOf())
                fixedDurations[i].biggerThanForty=nifasInfo

//
//                var secondDuration = (fixedDurations[i].timeInMilliseconds-pregnancy.aadatNifas!!*MILLISECONDS_IN_A_DAY).toLong()
//                var newFixedDuration = FixedDuration(DurationType.DAM,secondDuration, startDate = addTimeToDate(fixedDurations[i].startDate, pregnancy.aadatNifas.toLong()))
//                fixedDurations[i].timeInMilliseconds=pregnancy.aadatNifas.toLong()*MILLISECONDS_IN_A_DAY
//                fixedDurations.add(i+1,newFixedDuration)

                //break it up into dam and tuhr?
                //maybe do that later in bigger than 10
                //as that is the only way to get aadat.
                break
            }else{//it is 40 or less
                //do nothing to this. don't even bother to update aadat.
                //maybe update aadat? if it's working, why fix?
            }
        }

        i++
    }
}

fun makeAllDamInFortyAfterWiladatAsMuttasil(fixedDurations:MutableList<FixedDuration>,pregnancy:Pregnancy){
    val birthDate:Long = pregnancy.birthTime.getTime().toLong()
    val fortyPlusBD = birthDate+(40*MILLISECONDS_IN_A_DAY)
    var i = 0
    while (i<fixedDurations.size){
        val startTime = fixedDurations[i].startDate.getTime()
        if(startTime>=birthDate &&
                startTime<=fortyPlusBD &&
                fixedDurations[i].type == DurationType.DAM){
            //if a dam starts after or at birth, and before or at 40
            //then check the dam before it if it exists, is it in nifaas period.
            if(i>1 && fixedDurations[i-2].type==DurationType.DAM_IN_NIFAAS_PERIOD){
                //if it is, then extend last Dam to cover this one
                fixedDurations[i-2].timeInMilliseconds += fixedDurations[i-1].timeInMilliseconds + fixedDurations[i].timeInMilliseconds
                fixedDurations[i-2].indices.addAll(fixedDurations[i-1].indices)
                fixedDurations[i-2].indices.addAll(fixedDurations[i].indices)
                fixedDurations.removeAt(i-1)
                fixedDurations.removeAt(i-1)
                i -= 2
            }else{//there is no dam before this in nifas period
                val newDuration:Long = startTime.toLong()-birthDate
                fixedDurations[i].startDate=pregnancy.birthTime
                fixedDurations[i].timeInMilliseconds += newDuration
                fixedDurations[i].type = DurationType.DAM_IN_NIFAAS_PERIOD
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


fun markAllTuhrsInPregnancyAsHaml(fixedDurations: MutableList<FixedDuration>, pregnancy:Pregnancy){
    for (i in fixedDurations.indices){
        val endDateOfFixedDuration = fixedDurations[i].endDate

        if(fixedDurations[i].type == DurationType.TUHR &&
            fixedDurations[i].startDate.getTime() < pregnancy.birthTime.getTime() &&
            endDateOfFixedDuration.getTime() > pregnancy.pregStartTime.getTime()){

            fixedDurations[i].type = DurationType.TUHR_IN_HAML

        }
    }
}

fun markAllDamsInPregnancyAsHaml(fixedDurations: MutableList<FixedDuration>, pregnancy:Pregnancy){
    var i =0
    val startDateOfHaml = pregnancy.pregStartTime.getTime().toLong()
    val endDateOfHaml = pregnancy.birthTime.getTime().toLong()

    while(i<fixedDurations.size){
        val endDateOfFixedDuration = fixedDurations[i].endDate

        //this dam started before pregnancy, ends in the middle of pregnancy
        //  ---(pregnancy---  birth)
        if(fixedDurations[i].type == DurationType.DAM &&
                fixedDurations[i].startDate.getTime()<startDateOfHaml &&
                endDateOfFixedDuration.getTime()>startDateOfHaml &&
                endDateOfFixedDuration.getTime()<=endDateOfHaml){
            //mark the portion in pregnancy as dam in haml. we're gonna have to make more dam???
            //or, we could just shorten it to prepregnancy stae, and leave it as is. who cares about dam in haml?
            //we can even put it in istihazaAfter.
            //maybe that's a bad idea, as it could trigger daur...
            //for now, we are just shortening it.
            val newDuration = startDateOfHaml - fixedDurations[i].startDate.getTime().toLong()
            val timeInHaml = fixedDurations[i].timeInMilliseconds-newDuration
            fixedDurations[i].timeInMilliseconds = newDuration
            //maybe we really should leave an istihaz after here????
            //we gotta figure out what to do with indices here
            val newFixedDuration = FixedDuration(DurationType.DAM_IN_HAML,timeInHaml, startDate = addTimeToDate(fixedDurations[i].startDate, newDuration))
            fixedDurations.add(i+1, newFixedDuration)
        }
        //this started in the middle, ended in the middle of it
        //  (pregnancy ---- birth)
        if(fixedDurations[i].type == DurationType.DAM &&
                    endDateOfFixedDuration.getTime() <= endDateOfHaml &&
                    fixedDurations[i].startDate.getTime() >= startDateOfHaml
            ){

            //mark it as dam in haml, aka, istihaza.
            fixedDurations[i].type = DurationType.DAM_IN_HAML
        }
        //this starts in the middle of pregnancy, ends after it.
        // (pregnancy   ---birth)---
        if(fixedDurations[i].type == DurationType.DAM &&
                    fixedDurations[i].startDate.getTime()<endDateOfHaml &&
                    fixedDurations[i].startDate.getTime()>=startDateOfHaml &&
                    endDateOfFixedDuration.getTime()>endDateOfHaml
                ){

            val firstDuration = pregnancy.birthTime.getTime().toLong()-fixedDurations[i].startDate.getTime().toLong()
            val secondDuration = fixedDurations[i].timeInMilliseconds-firstDuration
            val secondFixedDuration = FixedDuration(DurationType.DAM,secondDuration, startDate = pregnancy.birthTime)
            fixedDurations.add(i+1,secondFixedDuration)
            fixedDurations[i].timeInMilliseconds = firstDuration
            fixedDurations[i].type=DurationType.DAM_IN_HAML

        }
        //this started before pregnancy began, ends after pregnancy ended
        //  ---(pregnancy----birth)---
        if(fixedDurations[i].type == DurationType.DAM &&
                fixedDurations[i].startDate.getTime()<startDateOfHaml &&
                endDateOfFixedDuration.getTime()>endDateOfHaml){
            //make 1 at the start, one at the end, and 1 in the middle
            val firstDuration = startDateOfHaml-fixedDurations[i].startDate.getTime().toLong()
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
    var date:Date = firstStartTime
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
fun removeDamLessThan3 (fixedDurations: MutableList<FixedDuration>){
    var i=0
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
            }
        }
        i++
    }
}
//step 4 - Deal with bigger than 10 dam
//          iterate through array. getting aadat on the way. each time you encounter a dam
//          less than 10, update it into HazAadat. each time you encounter a tuhur
//          (not a tuhr-e-faasid), update it into aadat too.

fun dealWithBiggerThan10Dam(fixedDurations: MutableList<FixedDuration>, inputtedAadatHaz: Double?,inputtedAadatTuhr: Double?){
    var aadatHaz:Long = -1
    var aadatTuhr:Long = -1

    if (inputtedAadatHaz != null && inputtedAadatHaz>=3 && inputtedAadatHaz<=10){
        aadatHaz = (inputtedAadatHaz * MILLISECONDS_IN_A_DAY).toLong()
    }
    if (inputtedAadatTuhr!= null && inputtedAadatTuhr>=15){
        aadatTuhr = (inputtedAadatTuhr * MILLISECONDS_IN_A_DAY).toLong()
    }

    //now the aadaat are in milliseconds

    for (i in fixedDurations.indices){
        //iterate through fixedDurations

        //get aadat if dam is less than 10
        if(fixedDurations[i].type==DurationType.DAM && fixedDurations[i].days<=10){
            aadatHaz = fixedDurations[i].timeInMilliseconds
            if(i>0 && fixedDurations[i-1].type==DurationType.TUHR){
                aadatTuhr = fixedDurations[i-1].timeInMilliseconds
            }

        }else if(fixedDurations[i].type==DurationType.DAM_IN_NIFAAS_PERIOD && fixedDurations[i].days>40){
            //check if we have aadaat.
            //we don't need mawjoodah paki
            if(aadatHaz==(-1).toLong() ||aadatTuhr==(-1).toLong()){
                //give error message
                window.alert("We need both aadaat to be able to solve this")
                break
            }

            val istihazaAfter = fixedDurations[i].biggerThanForty!!.istihazaAfter
            val aadatNifas = fixedDurations[i].biggerThanForty!!.nifas
            val nifasInfo = BiggerThanFortyNifas(aadatNifas, istihazaAfter,aadatHaz, aadatTuhr, mutableListOf())
            fixedDurations[i].biggerThanForty=nifasInfo

            aadatHaz = dealWithIstihazaAfter(istihazaAfter,aadatHaz,aadatTuhr,fixedDurations, i)

        }else if(fixedDurations[i].type==DurationType.DAM && fixedDurations[i].days>10){
            //if we hit a dam bigger than 10, check to see if we have aadat
            if(aadatHaz==(-1).toLong() ||aadatTuhr==(-1).toLong()){
                //give error message
                window.alert("We need both aadaat to be able to solve this")
                break
            }else if(i<1){
                //give error message
                window.alert("We need at least one more period before this to be able to solve this")
                break
            }
            else{
                val mp:Long = fixedDurations[i-1].timeInMilliseconds + fixedDurations[i-1].istihazaAfter
                val gp:Long = aadatTuhr
                val dm:Long = fixedDurations[i].timeInMilliseconds
                val hz:Long = aadatHaz
                val output:FiveSoortainOutput = fiveSoortain(mp, gp, dm, hz)

                //deal with output
                //update aadats
                aadatHaz = output.haiz
                if(output.aadatTuhrChanges && fixedDurations[i-1].type==DurationType.TUHR){
                    //if mp is not tuhrefaasid or tuhr in haml
                    aadatTuhr = mp
                }
                val hall =  BiggerThanTenDm(mp,gp,dm,hz, output.soorat, output.istihazaBefore,
                    output.haiz, output.istihazaAfter, aadatHaz,aadatTuhr, mutableListOf())
                fixedDurations[i].biggerThanTen=hall

                aadatHaz = dealWithIstihazaAfter(output.istihazaAfter,aadatHaz,aadatTuhr,fixedDurations, i)
            }
        }
        if(fixedDurations[i].type==DurationType.ISTIMRAR) {//if the last period is an istimrar
            //if we hit a dam bigger than 10, check to see if we have aadat
            if (aadatHaz == (-1).toLong() || aadatTuhr == (-1).toLong()) {
                //give error message
                window.alert("We need both aadaat to be able to solve this")
                break
            } else {
                val veryBigArbitraryNumber = 1000
                val dm:Long = (veryBigArbitraryNumber*MILLISECONDS_IN_A_DAY)
                val mp = fixedDurations[i - 1].timeInMilliseconds
                val gp = aadatTuhr
                val hz = aadatHaz
                val output: FiveSoortainOutput = threeSoortainIstimrar(mp, gp, hz)
                //update aadats
                aadatHaz = output.haiz
                if(output.aadatTuhrChanges && fixedDurations[i-1].type!=DurationType.TUHREFAASID){
                    //if mp is not tuhrefaasid
                    aadatTuhr = mp
                }
                val hall =  BiggerThanTenDm(mp,gp,dm,hz, output.soorat, output.istihazaBefore,output.haiz, output.istihazaAfter, aadatHaz,aadatTuhr, mutableListOf())
                fixedDurations[i].biggerThanTen=hall
            }
        }
    }
}

fun dealWithIstihazaAfter(istihazaAfter: Long, aadatHaz: Long, aadatTuhr: Long, fixedDurations: MutableList<FixedDuration>, i: Int):Long {
    //if istihazaAfter is bigger than addatTuhr +3, run daur
    var returnAadatHaiz = aadatHaz
    if (istihazaAfter>=aadatTuhr+(3*MILLISECONDS_IN_A_DAY)){
        //find  remainder

        val remainder = istihazaAfter%(aadatHaz+aadatTuhr)

        if (remainder<aadatTuhr + (3*MILLISECONDS_IN_A_DAY)){//it ended in tuhr or right between haz and tuhr
            //add istihazaAfter to next Tuhur mark it as fasid
            //if it exists
            //if remainder is not equal to zero
            if(i<fixedDurations.lastIndex && remainder>0){//there is a tuhur after this
                fixedDurations[i+1].type=DurationType.TUHREFAASID
                fixedDurations[i+1].istihazaAfter=remainder
            }

        }else{//it ended in less than haiz
            //change aadatHaiz
            returnAadatHaiz = remainder-aadatTuhr

        }

    }else{

        //else add istihazaAfter to next Tuhr, mark it as fasid
        //if it exists
        if(i<fixedDurations.size-1){
            fixedDurations[i+1].type=DurationType.TUHREFAASID
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

fun threeSoortainIstimrar(mp:Long, gp:Long, hz: Long):FiveSoortainOutput{
    val soorat: Soortain
    val istihazaBefore:Long
    val haiz:Long
    val istihazaAfter:Long
    val aadatTuhrChanges:Boolean // 0 for gp, 1 for mp (change)
    val veryBigArbitraryNumber = 1000

    if (mp <= gp) {    //Qism A (Always A-1 in istimrar)
        soorat = Soortain.A_1
        istihazaBefore = gp-mp
        haiz = hz
        istihazaAfter = (veryBigArbitraryNumber*MILLISECONDS_IN_A_DAY)
        aadatTuhrChanges = false
    }else {	// mp>gp qism B
        if (hz - (mp - gp) >= 3*MILLISECONDS_IN_A_DAY) {							// soorat B-2
            soorat = Soortain.B_2
            istihazaBefore = 0
            haiz = hz-(mp-gp)
            istihazaAfter = (veryBigArbitraryNumber*MILLISECONDS_IN_A_DAY)
            aadatTuhrChanges = true
        }else{ //if (hz - (mp - gp) < 3) {						// soorat B-3
            soorat = Soortain.B_3
            istihazaBefore = 0
            haiz = hz
            istihazaAfter = (veryBigArbitraryNumber*MILLISECONDS_IN_A_DAY)
            aadatTuhrChanges = true
        }
    }
    return FiveSoortainOutput(soorat,istihazaBefore,haiz,istihazaAfter, aadatTuhrChanges)
}

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
        else if (3*MILLISECONDS_IN_A_DAY <= dm-(gp-mp) && dm-(gp-mp) < hz) {  // soorat A-2
            soorat = Soortain.A_2
            istihazaBefore = gp-mp
            haiz = dm-(gp-mp)
            istihazaAfter = 0
            aadatTuhrChanges = false
        }
        else{ //if (dm - (gp - mp) < 3*MILLISECONDS_IN_A_DAY) {                  // soorat A-3
            soorat = Soortain.A_3
            istihazaBefore = 0
            haiz = hz
            istihazaAfter = dm-hz
            aadatTuhrChanges = true
        }
    }else {	// mp>gp qism B
        if (hz - (mp - gp) >= 3*MILLISECONDS_IN_A_DAY) {							// soorat B-2
            soorat = Soortain.B_2
            istihazaBefore = 0
            haiz = hz-(mp-gp)
            istihazaAfter = dm-(hz-(mp-gp))
            aadatTuhrChanges = true
        }else{ //if (hz - (mp - gp) < 3*MILLISECONDS_IN_A_DAY) {						// soorat B-3
            soorat = Soortain.B_3
            istihazaBefore = 0
            haiz = hz
            istihazaAfter = dm-hz
            aadatTuhrChanges = true
        }
    }
  return FiveSoortainOutput(soorat,istihazaBefore,haiz,istihazaAfter, aadatTuhrChanges)
}
fun addDurationsToDams(fixedDurations: MutableList<FixedDuration>){
    //currently, this doesn't handle istimrar

    for (i in fixedDurations.indices){
        if(fixedDurations[i].type==DurationType.DAM && fixedDurations[i].days>10){
            //bigger than 10
            val istihazaBefore = fixedDurations[i].biggerThanTen!!.istihazaBefore
            val istihazaBeforeStartDate:Date = fixedDurations[i].startDate

            if (istihazaBefore>0){
                fixedDurations[i].biggerThanTen!!.durationsList += Duration(DurationType.ISTIHAZA_BEFORE,istihazaBefore,istihazaBeforeStartDate)
            }
            val haizStartDate = addTimeToDate(istihazaBeforeStartDate, (istihazaBefore))
            val haiz = fixedDurations[i].biggerThanTen!!.haiz
            fixedDurations[i].biggerThanTen!!.durationsList += Duration(DurationType.HAIZ,haiz,haizStartDate)

            val istihazaAfterStartDate = addTimeToDate(haizStartDate, (haiz))
            val istihazaAfter = fixedDurations[i].biggerThanTen!!.istihazaAfter
            val aadatTuhr = fixedDurations[i].biggerThanTen!!.aadatTuhr
            val aadatHaz = fixedDurations[i].biggerThanTen!!.aadatHaiz

            if(istihazaAfter>0){
                if(istihazaAfter>=aadatTuhr+3*MILLISECONDS_IN_A_DAY||
                    (istihazaAfter>aadatTuhr &&
                            istihazaAfter<aadatTuhr+3*MILLISECONDS_IN_A_DAY &&
                            i == fixedDurations.size-1)){
                    //daur
                    //find quotient and remainder
                    val remainder = istihazaAfter%(aadatHaz+aadatTuhr)
                    val quotient = ((istihazaAfter-remainder)/(aadatHaz+aadatTuhr))
                    var aadatTuhrStartDate:Date = istihazaAfterStartDate
                    var aadatTuhrEndDate:Date
                    var aadatHaizEndDate:Date

                    for(j in 1 .. quotient){
                        //add a quotient number of tuhr/hazes
                        aadatTuhrEndDate = addTimeToDate(aadatTuhrStartDate,(aadatTuhr))
                        aadatHaizEndDate = addTimeToDate(aadatTuhrEndDate,(aadatHaz))

                        fixedDurations[i].biggerThanTen!!.durationsList+=Duration(DurationType.ISTIHAZA_AFTER,aadatTuhr,aadatTuhrStartDate)
                        fixedDurations[i].biggerThanTen!!.durationsList+=Duration(DurationType.HAIZ,aadatHaz,aadatTuhrEndDate)
                        aadatTuhrStartDate=aadatHaizEndDate
                    }
                    //now deal with remiander


                    if(remainder==0L){
                        //there is nothing more to be added
                    }else if(remainder>aadatTuhr
                        && remainder<aadatTuhr+3*MILLISECONDS_IN_A_DAY
                        && i==fixedDurations.size-1){//it is the last period, and ends in less than 3 haiz
                        fixedDurations[i].biggerThanTen!!.durationsList+=Duration(DurationType.ISTIHAZA_AFTER,aadatTuhr,aadatTuhrStartDate)
                        aadatTuhrEndDate = addTimeToDate(aadatTuhrStartDate,(aadatTuhr))
                        val lastHaiz = remainder-aadatTuhr
                        fixedDurations[i].biggerThanTen!!.durationsList+=Duration(DurationType.LESS_THAN_3_HAIZ,lastHaiz,aadatTuhrEndDate)
                    }else if(remainder<aadatTuhr+3*MILLISECONDS_IN_A_DAY){
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

        }else if(fixedDurations[i].type==DurationType.DAM_IN_NIFAAS_PERIOD&&fixedDurations[i].days>40){
            //bigger than 40 nifas
            val aadatNifas = fixedDurations[i].biggerThanForty!!.nifas
            //make nifas period
            fixedDurations[i].biggerThanForty!!.durationsList+=Duration(DurationType.NIFAAS,aadatNifas,fixedDurations[i].startDate)
            //now deal with istihaza after
            //I'm copy/pasting from above, with minor changes

            val istihazaAfterStartDate = addTimeToDate(fixedDurations[i].startDate, (aadatNifas))
            val istihazaAfter = fixedDurations[i].biggerThanForty!!.istihazaAfter
            val aadatTuhr = fixedDurations[i].biggerThanForty!!.aadatTuhr
            val aadatHaz = fixedDurations[i].biggerThanForty!!.aadatHaiz

            if(istihazaAfter>0){
                if(istihazaAfter>=aadatTuhr+3*MILLISECONDS_IN_A_DAY||
                    (istihazaAfter>aadatTuhr &&
                            istihazaAfter<aadatTuhr+3*MILLISECONDS_IN_A_DAY &&
                            i == fixedDurations.size-1)){
                    //daur
                    //find quotient and remainder
                    val remainder = istihazaAfter%(aadatHaz+aadatTuhr)
                    val quotient = ((istihazaAfter-remainder)/(aadatHaz+aadatTuhr))
                    var aadatTuhrStartDate:Date = istihazaAfterStartDate
                    var aadatTuhrEndDate:Date
                    var aadatHaizEndDate:Date

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
                        && remainder<aadatTuhr+3*MILLISECONDS_IN_A_DAY
                        && i==fixedDurations.lastIndex){//it is the last period, and ends in less than 3 haiz
                        fixedDurations[i].biggerThanForty!!.durationsList+=Duration(DurationType.ISTIHAZA_AFTER,aadatTuhr,aadatTuhrStartDate)
                        aadatTuhrEndDate = addTimeToDate(aadatTuhrStartDate,(aadatTuhr))
                        val lastHaiz = remainder-aadatTuhr
                        fixedDurations[i].biggerThanForty!!.durationsList+=Duration(DurationType.LESS_THAN_3_HAIZ,lastHaiz,aadatTuhrEndDate)
                    }else if(remainder<aadatTuhr+3*MILLISECONDS_IN_A_DAY){
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
        }else if(fixedDuration.type==DurationType.DAM_IN_NIFAAS_PERIOD && fixedDuration.days<=40){
            hazDatesList+=Entry(fixedDuration.startDate,fixedDuration.endDate)
        }else if(fixedDuration.type==DurationType.DAM_IN_NIFAAS_PERIOD && fixedDuration.days>40){
            for(duration in fixedDuration.biggerThanForty!!.durationsList){
                if(duration.type==DurationType.HAIZ
                    ||duration.type == DurationType.NIFAAS
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
        if(fixedDurations[i].endDate.getTime()>pregnancy.birthTime.getTime()||
                fixedDurations[i].type==DurationType.DAM_IN_NIFAAS_PERIOD){
            val newFixedDuration = FixedDuration(DurationType.WILADAT_ISQAT, 0L, mutableListOf(),startDate = pregnancy.birthTime)
            fixedDurations.add(i,newFixedDuration)
            break
        }
        if(i==fixedDurations.size-1){
            //if we got to the last one without anything happening, just add it anyway
            val newFixedDuration = FixedDuration(DurationType.WILADAT_ISQAT, 0L, mutableListOf(),startDate = pregnancy.birthTime)
            fixedDurations.add(i,newFixedDuration)
        }
    }
}
fun addStartOfPregnancy(fixedDurations: MutableList<FixedDuration>,pregnancy: Pregnancy){
    //add start of pregnancy in fixed periods
    for(i in fixedDurations.indices){
        if(fixedDurations[i].endDate.getTime()>pregnancy.pregStartTime.getTime()){
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
        if (list[0].startTime.getTime() <earliestStartTime.getTime())
            earliestStartTime = list[0].startTime
        if (list[list.lastIndex].endTime.getTime() > latestEndTime.getTime())
            latestEndTime = list.last().endTime
    }
    val firstLast = Entry(earliestStartTime, latestEndTime)

    val ndays = ((latestEndTime.getTime()-earliestStartTime.getTime())/MILLISECONDS_IN_A_DAY).toInt()

    val headerList = mutableListOf<Date>()
    for(day in 0..(ndays)){//header list is one longer than ndays
        val dateOfDay = addTimeToDate(firstLast.startTime, (day)*MILLISECONDS_IN_A_DAY)
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
                if (header.getTime() >= entry.startTime.getTime() && header.getTime() < entry.endTime.getTime()) {
                    //that date is a haiz
                    colorsOfDaysList +=1
                    break
                }else if (header.getTime() < entry.startTime.getTime()) {
                    //that date is a tuhur
                    colorsOfDaysList +=0
                    break
                }else if(header.getTime()>=list.last().endTime.getTime()){
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
        resultColors += if(compare == minColor){
            0 //yaqeeni paki
        }else if(compare == maxColor){
            2 //yaqeeni napaki
        }else{
            1//ayyam-e-shakk
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

fun getDifferenceFromMultiple (listOfLists:List<List<Entry>>):String{
    //find out number of lists
    val numberOfLists = listOfLists.size

    //step 1: merge them into one list
    val dateTypeList = mutableListOf<DateTypeList>()

    for (list in listOfLists){
        for(date in list){
            dateTypeList += DateTypeList(date.startTime,DateTypes.START)
            dateTypeList += DateTypeList(date.endTime,DateTypes.END)
        }
    }

    //step 2: order list by date
    //since we want to prioritize khurooj, we should reverse order
    dateTypeList.sortBy { it.date.getTime() }

    //step 3: create a counter
    var counter = 0

    //step 4: step through the list, create an output list
    val counterMin = 0 //at counter min, it is yaqeeni paki
    val counterMax = numberOfLists //at counter max, it is yaqeeni na-paki
    //all other counter values are ayyam-e-shakk

    val outputList = mutableListOf<DateTypeList>()
    for(dateType in dateTypeList){
        //plus 1 for every start time, -1 for every end time
        if(dateType.type==DateTypes.START){
            counter++
            //this is definitely a dukhool, and can lead to yaqeeni napaki
            //this cannot be yaqeeni paki, or khurooj
            //though it could simultaneously be a khurooj some other way
            if(counter==counterMax){
                outputList+=DateTypeList(dateType.date, DateTypes.YAQEENI_NA_PAKI)
            }else{//it is dukhool shakk
                outputList+=DateTypeList(dateType.date, DateTypes.AYYAAM_E_SHAKK_DUKHOOL)
            }
        }else{//the type is end
            counter--
            //this is a khurooj or yaqeeni paki
            if(counter==counterMin){
                outputList+=DateTypeList(dateType.date, DateTypes.YAQEENI_PAKI)
            }else{
                outputList+=DateTypeList(dateType.date, DateTypes.AYYAAM_E_SHAKK_KHUROOJ)
            }
        }
    }

    //create a people-friendly version of output list
    var str = ""
    val durationTypes = mutableListOf<DurationTypes>()
    var i=0

    while (i<outputList.size-1){
        val startTime = outputList[i].date
        val endTime = outputList[i+1].date
        durationTypes += DurationTypes(startTime,endTime,outputList[i].type)
        i++
    }
    //clean up the junk in durationTypes list now
//    var j = 0
//    while (j <= durationTypes.lastIndex){
//        var duration=durationTypes[j]
//        //remove things with duration 0
//        if(duration.endTime.getTime()==duration.startTime.getTime()){
//            durationTypes.removeAt(j)
//            j--
//        }
//        j++
//    }
//    //remove things wherebthe next one is the same type as this one.
//    //if there is a next one
//    else if(j+1<durationTypes.size && durationTypes[j+1].type==duration.type){
//        durationTypes.add(j, DurationTypes(duration.startTime, durationTypes[i+1].endTime, duration.type))
//        durationTypes.removeAt(j+1)
//        durationTypes.removeAt(j+1)
//    }


    str += generateGetDifferenceString(durationTypes)

    return str
}

fun calculateEndingOutputValues(fixedDurations: MutableList<FixedDuration>):EndingOutputValues{
    val filHaalPaki = calculateFilHaal(fixedDurations)
    val aadaat = finalAadats(fixedDurations)
    val futureDates = futureDatesOfInterest(fixedDurations)
    return EndingOutputValues(filHaalPaki,aadaat,futureDates)
}

fun futureDatesOfInterest(fixedDurations: MutableList<FixedDuration>):FutureDateType?{
    val i = fixedDurations.size-1 //last period

    //A-3 changing to A-2 or A-1
    if(fixedDurations.last().days>10&&fixedDurations.last().type==DurationType.DAM) {
        if(fixedDurations.last().biggerThanTen!!.qism==Soortain.A_3){
            //A-3 is when bleeding begins well before aadat.
            //A-2 is when bleeding is 3 days or more into aadat.
            //But we must ask again at start of aadat, not A-2.
            val mp = fixedDurations[i].biggerThanTen!!.mp
            val gp = fixedDurations[i].biggerThanTen!!.gp
            //when dm becomes equal to or more than gp-mp
            //dm is duration
            val date = addTimeToDate(fixedDurations[i].startDate, gp-mp)
            return FutureDateType(date,TypesOfFutureDates.A3_CHANGING_TO_A2)

        }
    }

    //last date of aadat of haiz.
    //with insructions for ihtiyati ghusl
    if(fixedDurations.last().days>10&&fixedDurations.last().type==DurationType.DAM) {
        if(fixedDurations.last().biggerThanTen!!.durationsList.last().type==DurationType.HAIZ||
            fixedDurations.last().biggerThanTen!!.durationsList.last().type==DurationType.LESS_THAN_3_HAIZ){
            val date = addTimeToDate(fixedDurations[i].biggerThanTen!!.durationsList.last().startTime,fixedDurations.last().biggerThanTen!!.aadatHaiz)
            return FutureDateType(date,TypesOfFutureDates.END_OF_AADAT_HAIZ)

        }
    }else if(fixedDurations.last().days>40 && fixedDurations[i].type==DurationType.DAM_IN_NIFAAS_PERIOD){
        if(fixedDurations.last().biggerThanForty!!.durationsList.last().type==DurationType.HAIZ||
            fixedDurations.last().biggerThanForty!!.durationsList.last().type==DurationType.LESS_THAN_3_HAIZ){
            val date = addTimeToDate(fixedDurations[i].biggerThanForty!!.durationsList.last().startTime,fixedDurations.last().biggerThanForty!!.aadatHaiz)
            return FutureDateType(date,TypesOfFutureDates.END_OF_AADAT_HAIZ)
        }
    }


    //if there is a daur situation, and person is currently in a state of paki

    if(fixedDurations.last().days>10&&fixedDurations.last().type==DurationType.DAM) {
        if(fixedDurations.last().biggerThanTen!!.durationsList.last().type==DurationType.ISTIHAZA_AFTER){
            val date = addTimeToDate(fixedDurations[i].biggerThanTen!!.durationsList.last().startTime, fixedDurations.last().biggerThanTen!!.aadatTuhr)
            //if bigger than 10, ended in istihaza
            return FutureDateType(date,TypesOfFutureDates.END_OF_AADAT_TUHR)
        }

    }else if(fixedDurations.last().days>40 && fixedDurations.last().type==DurationType.DAM_IN_NIFAAS_PERIOD){
        if(fixedDurations.last().biggerThanForty!!.durationsList.last().type==DurationType.ISTIHAZA_AFTER){
            val date = addTimeToDate(fixedDurations[i].biggerThanForty!!.durationsList.last().startTime, fixedDurations[i].biggerThanForty!!.aadatTuhr)
            //if bigger than 40, ended in istihaza
            return FutureDateType(date,TypesOfFutureDates.END_OF_AADAT_TUHR)
        }

    }

    return null
}
fun finalAadats(fixedDurations: MutableList<FixedDuration>):AadatsOfHaizAndTuhr?{
    //we only provide an aadat in a bigger than 10 situation
    val i = fixedDurations.lastIndex
    if(fixedDurations[i].days>10&&fixedDurations[i].type==DurationType.DAM) {
        val j = fixedDurations[i].biggerThanTen!!.durationsList.size-1
        return if(fixedDurations[i].biggerThanTen!!.durationsList[j].type==DurationType.ISTIHAZA_AFTER){
            //if it ended in paki, no tension
            AadatsOfHaizAndTuhr(fixedDurations[i].biggerThanTen!!.aadatHaiz,fixedDurations[i].biggerThanTen!!.aadatTuhr)
        }else if(fixedDurations[i].biggerThanTen!!.durationsList[j].type==DurationType.LESS_THAN_3_HAIZ){
            //it ended in a haiz less than 3, no tension
            AadatsOfHaizAndTuhr(fixedDurations[i].biggerThanTen!!.aadatHaiz,fixedDurations[i].biggerThanTen!!.aadatTuhr)
        }else{
            //it ended in a hiaz more than 3. We are going to give that haiz as aadat
            AadatsOfHaizAndTuhr(fixedDurations[i].biggerThanTen!!.durationsList[j].timeInMilliseconds,fixedDurations[i].biggerThanTen!!.aadatTuhr)
        }

    }else if(fixedDurations[i].days>40 && fixedDurations[i].type==DurationType.DAM_IN_NIFAAS_PERIOD){
        val j = fixedDurations[i].biggerThanForty!!.durationsList.size-1
        return if(fixedDurations[i].biggerThanForty!!.durationsList[j].type==DurationType.ISTIHAZA_AFTER){
            //if it ended in paki, no tension
            AadatsOfHaizAndTuhr(fixedDurations[i].biggerThanForty!!.aadatHaiz,fixedDurations[i].biggerThanForty!!.aadatTuhr)
        }else if(fixedDurations[i].biggerThanForty!!.durationsList[j].type==DurationType.LESS_THAN_3_HAIZ){
            //it ended in a haiz less than 3, no tension
            AadatsOfHaizAndTuhr(fixedDurations[i].biggerThanForty!!.aadatHaiz,fixedDurations[i].biggerThanForty!!.aadatTuhr)
        }else{
            //it ended in a hiaz more than 3. We are going to give that haiz as aadat
            AadatsOfHaizAndTuhr(fixedDurations[i].biggerThanForty!!.durationsList[j].timeInMilliseconds,fixedDurations[i].biggerThanForty!!.aadatTuhr)
        }
    }
    return null
}

fun calculateFilHaal(fixedDurations: MutableList<FixedDuration>):Boolean{
    //calculate filHaal status
    val filHaalPaki:Boolean
    val i = fixedDurations.lastIndex

    filHaalPaki = if(fixedDurations[i].days>10&&fixedDurations[i].type==DurationType.DAM){
        fixedDurations.last().biggerThanTen!!.durationsList.last().type==DurationType.ISTIHAZA_AFTER

    }else if(fixedDurations[i].days>40 && fixedDurations[i].type==DurationType.DAM_IN_NIFAAS_PERIOD){
        fixedDurations.last().biggerThanForty!!.durationsList.last().type==DurationType.ISTIHAZA_AFTER
    }else{
        false
    }
    return filHaalPaki
}