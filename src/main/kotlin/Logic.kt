import kotlinx.browser.window
import kotlin.js.Date

//output line 1 is indices of all the things durations that make up the sum of this thingy.
// we should only write it, if there is more than one index
//output line 2 is printing if an istihaza after was added too. it contains amount of istihazaAfter
//output line 3 is mp,gp, dm, hz, soorat, as well as istihazaBefore, Haiz, IstihazaAfter, and AadatHaiz/AaadatTuhr
// at the end of it.
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
    println("This is the raw fixedDurations after adding indices: ${fixedDurations}")

    if(istimrar==true){//make the last period an istimrar type
        fixedDurations[fixedDurations.size-1].type=DurationType.ISTIMRAR;
    }

    if(isPregnancy==true){
        addStartDateToFixedDurations(fixedDurations)

        markAllTuhrsInPregnancyAsHaml(fixedDurations, pregnancy)

        if(pregnancy.mustabeenUlKhilqat==false){
            //if it's not mustabeen ulkhilqat, deal with it like haiz
            removeTuhrLessThan15(fixedDurations)
            removeDamLessThan3(fixedDurations)
            addStartDateToFixedDurations(fixedDurations)
            dealWithBiggerThan10Dam(fixedDurations, durations, inputtedAadatHaz,inputtedAadatTuhr)
            return generateOutputStringPregnancy(fixedDurations, durations, isDateOnly, pregnancy)
        }else{         //it is mustabeen ul khilqat
            //mark all dam in pregnancy as isithaza.
            markAllDamsInPregnancyAsHaml(fixedDurations, pregnancy)
            removeTuhrLessThan15(fixedDurations)//do this before the next, cuz why not, mkes thigns simpler in joining dams
            addStartDateToFixedDurations(fixedDurations)//cuz the last shoulda messed it up
            makeAllDamInFortyAfterWiladatAsMuttasil(fixedDurations,pregnancy)//also, marking them as Dam in nifaas
            dealWithDamInMuddateNifas(fixedDurations,pregnancy)
            removeDamLessThan3(fixedDurations) //this won't effect dam in muddat e haml
            addStartDateToFixedDurations(fixedDurations)
            dealWithBiggerThan10Dam(fixedDurations, durations, inputtedAadatHaz,inputtedAadatTuhr)
            return generateOutputStringPregnancy(fixedDurations, durations, isDateOnly, pregnancy)
        }
    }else{//is not pregnancy
        removeTuhrLessThan15(fixedDurations)
        println("This is fixedDurations after removing less than 15T: ${fixedDurations}")
        removeDamLessThan3(fixedDurations)
        println("This is fixedDurations after removing less than 3D: ${fixedDurations}")
        addStartDateToFixedDurations(fixedDurations)
        println("Now we add start date: ${fixedDurations}")
        dealWithBiggerThan10Dam(fixedDurations, durations, inputtedAadatHaz,inputtedAadatTuhr)
        println("After dealing with bigger than 10D: ${fixedDurations}")
        return generateOutputString(fixedDurations, durations, isDateOnly)
    }

    return generateOutputString(fixedDurations, durations, isDateOnly)

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
                var istihazaAfter = (fixedDurations[i].timeInMilliseconds-pregnancy.aadatNifas!!*MILLISECONDS_IN_A_DAY).toLong()
                var nifasInfo = BiggerThanFortyNifas(pregnancy.aadatNifas!!.toLong()*MILLISECONDS_IN_A_DAY, istihazaAfter,null, null)
                fixedDurations[i].biggerThanForty=nifasInfo

//
//                var secondDuration = (fixedDurations[i].timeInMilliseconds-pregnancy.aadatNifas!!*MILLISECONDS_IN_A_DAY).toLong()
//                var newFixedDuration = FixedDuration(DurationType.DAM,secondDuration, startDate = addTimeToDate(fixedDurations[i].startDate, pregnancy.aadatNifas.toLong()))
//                fixedDurations[i].timeInMilliseconds=pregnancy.aadatNifas.toLong()*MILLISECONDS_IN_A_DAY
//                fixedDurations.add(i+1,newFixedDuration)

                //break it up into dam and tuhr?
                //maybe do that later in bigger than 10
                //as that is the only way to get aadat.
                break;
            }else{//it is 40 or less
                //do nothing to this. don't even bother to update aadat.
            }
        }

        i++
    }
}

fun makeAllDamInFortyAfterWiladatAsMuttasil(fixedDurations:MutableList<FixedDuration>,pregnancy:Pregnancy){
    var birthDate:Long = pregnancy.birthTime.getTime().toLong()
    var fortyPlusBD = birthDate+(40*MILLISECONDS_IN_A_DAY)
    var i = 0
    while (i<fixedDurations.size){
        var startTime = fixedDurations[i].startDate.getTime()
        if(fixedDurations[i].startDate.getTime()>=birthDate &&
                fixedDurations[i].startDate.getTime()<=fortyPlusBD &&
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
                var newDuration:Long = fixedDurations[i].startDate.getTime().toLong()-birthDate
                fixedDurations[i].startDate=pregnancy.birthTime
                fixedDurations[i].timeInMilliseconds += newDuration
                fixedDurations[i].type = DurationType.DAM_IN_NIFAAS_PERIOD
            }
        }
        if(fixedDurations[i].startDate.getTime()>fortyPlusBD){
            break;
        }
        i++
    }
}


fun markAllTuhrsInPregnancyAsHaml(fixedDurations: MutableList<FixedDuration>, pregnancy:Pregnancy){
    println("In marking Tuhrs as Tuhr in pregnancy")
    for (i in fixedDurations.indices){
  //      println("1")
        var endDateOfFixedDuration = fixedDurations[i].endDate
  //      println("2")
//        println("type is ${fixedDurations[i].type}")
//        println("startDate is ${fixedDurations[i].startDate}")
//        println("endDate is ${endDateOfFixedDuration}")
//        println("birthtime is ${pregnancy.birthTime}")

        if(fixedDurations[i].type == DurationType.TUHR &&
            fixedDurations[i].startDate.getTime() < pregnancy.birthTime.getTime() &&
            endDateOfFixedDuration.getTime() > pregnancy.pregStartTime.getTime()){
            println("3")

            fixedDurations[i].type = DurationType.TUHR_IN_HAML
            println("4")

        }
    }
    println("passed this")
}

fun markAllDamsInPregnancyAsHaml(fixedDurations: MutableList<FixedDuration>, pregnancy:Pregnancy){
    var i =0
    val startDateOfHaml = pregnancy.pregStartTime.getTime().toLong()
    val endDateOfHaml = pregnancy.birthTime.getTime().toLong()

    while(i<fixedDurations.size){
        var endDateOfFixedDuration = fixedDurations[i].endDate

        //this dam started before pregnancy, ends in the middle of pregnancy
        //  ---pregnancy---  birth
        if(fixedDurations[i].type == DurationType.DAM &&
                fixedDurations[i].startDate.getTime()<startDateOfHaml &&
                endDateOfFixedDuration.getTime()>startDateOfHaml &&
                endDateOfFixedDuration.getTime()<=endDateOfHaml){
            println("We got choice a")
            //mark the portion in pregnancy as dam in haml. we're gonna have to make more dam???
            //or, we could just shorten it to prepregnancy stae, and leave it as is. who cares about dam in haml?
            //we can even put it in istihazaAfter.
            //maybe that's a bad idea, as it could trigger daur...
            //for now, we are just shortening it.
            var newDuration = startDateOfHaml - fixedDurations[i].startDate.getTime().toLong()
            var timeInHaml = fixedDurations[i].timeInMilliseconds-newDuration
            fixedDurations[i].timeInMilliseconds = newDuration
            //maybe we really should leave an istihaz after here????
            //we gotta figure out what to do with indices here
            var newFixedDuration:FixedDuration = FixedDuration(DurationType.DAM_IN_HAML,timeInHaml, startDate = addTimeToDate(fixedDurations[i].startDate, newDuration))
            fixedDurations.add(i+1, newFixedDuration)
        }
        //this started in the middle, ended in the middle of it
        //  pregnancy ---- birth
        if(fixedDurations[i].type == DurationType.DAM &&
                    endDateOfFixedDuration.getTime() <= endDateOfHaml &&
                    fixedDurations[i].startDate.getTime() >= startDateOfHaml
            ){
            println("We got choice b")

            //mark it as dam in haml, aka, istihaza.
            fixedDurations[i].type = DurationType.DAM_IN_HAML
        }
        //this starts in the middle of pregnancy, ends after it.
        // pregnancy   ---birth---
        if(fixedDurations[i].type == DurationType.DAM &&
                    fixedDurations[i].startDate.getTime()<endDateOfHaml &&
                    fixedDurations[i].startDate.getTime()>=startDateOfHaml &&
                    endDateOfFixedDuration.getTime()>endDateOfHaml
                ){
            println("We got choice c")
            println("End date of haml is ${Date(endDateOfHaml)}")
            println("this fd start time is ${fixedDurations[i].startDate}")

            var firstDuration = pregnancy.birthTime.getTime().toLong()-fixedDurations[i].startDate.getTime().toLong()
            var secondDuration = fixedDurations[i].timeInMilliseconds-firstDuration
            var secondFixedDuration:FixedDuration = FixedDuration(DurationType.DAM,secondDuration, startDate = pregnancy.birthTime)
            fixedDurations.add(i+1,secondFixedDuration)
            fixedDurations[i].timeInMilliseconds = firstDuration
            fixedDurations[i].type=DurationType.DAM_IN_HAML

        }
        //this started before pregnancy began, ends after pregnancy ended
        //  ---pregnancy----birth---
        if(fixedDurations[i].type == DurationType.DAM &&
                fixedDurations[i].startDate.getTime()<startDateOfHaml &&
                endDateOfFixedDuration.getTime()>endDateOfHaml){
            //make 1 at the start, one at the end, and 1 in the middle
            var firstDuration = startDateOfHaml-fixedDurations[i].startDate.getTime().toLong()
            var secondDuration = endDateOfHaml-startDateOfHaml
            var thirdDuration = fixedDurations[i].timeInMilliseconds-firstDuration-secondDuration

            var secondFixedDuration:FixedDuration = FixedDuration(DurationType.DAM_IN_HAML,secondDuration, startDate = pregnancy.pregStartTime)
            var thirdFixedDuration:FixedDuration = FixedDuration(DurationType.DAM,thirdDuration, startDate = pregnancy.birthTime)
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

fun dealWithBiggerThan10Dam(fixedDurations: MutableList<FixedDuration>, durations: List<Duration>,inputtedAadatHaz: Double?,inputtedAadatTuhr: Double?){
    var hazDatesList = mutableListOf<Entry>()
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
            //put it in haz lis
            hazDatesList += Entry(fixedDurations[i].startDate, addTimeToDate(fixedDurations[i].startDate, fixedDurations[i].timeInMilliseconds))

        }else if(fixedDurations[i].type==DurationType.DAM_IN_NIFAAS_PERIOD && fixedDurations[i].days>40){
            var istihazaAfter = fixedDurations[i].biggerThanForty!!.istihazaAfter
            var aadatNifas = fixedDurations[i].biggerThanForty!!.nifas
            var sd = addTimeToDate(fixedDurations[i].startDate,aadatNifas)
            var nifasInfo = BiggerThanFortyNifas(aadatNifas, istihazaAfter,aadatHaz, aadatTuhr)
            fixedDurations[i].biggerThanForty=nifasInfo

            aadatHaz = dealWithIstihazaAfter(istihazaAfter,aadatHaz,aadatTuhr,hazDatesList,fixedDurations, i,sdOfIstihazaAfter = sd)

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
//            else if(i>0 && fixedDurations[i-1].type==DurationType.DAM_IN_NIFAAS_PERIOD){
//                //we gotta do this here, because this is the only way we have aadat.
//                //so we begin with aadat-e tuhr, then haiz...
//                aadatHaz = dealWithIstihazaAfter(fixedDurations[i].timeInMilliseconds,aadatHaz,aadatTuhr,hazDatesList,fixedDurations,i,fixedDurations[i].startDate!!)
//
//            }
            else{
                val mp:Long = fixedDurations[i-1].timeInMilliseconds + fixedDurations[i-1].istihazaAfter
                val gp:Long = aadatTuhr
                val dm:Long = fixedDurations[i].timeInMilliseconds
                val hz:Long = aadatHaz
                val output:FiveSoortainOutput = fiveSoortain(mp, gp, dm, hz)
                println("MP is ${mp}")
                println("GP is ${gp}")
                println("Dm is ${dm}")
                println("Hz is ${hz}")
                println("Soorat is ${output.soorat}")
                println("istihazaBefore is ${output.istihazaBefore}, haiz is ${output.haiz} and istihazaAfter is ${output.istihazaAfter}")

                //deal with output
                //update aadats
                aadatHaz = output.haiz.toLong()
                if(output.aadatTuhrChanges && fixedDurations[i-1].type==DurationType.TUHR){
                    //if mp is not tuhrefaasid or tuhr in haml
                    aadatTuhr = mp;
                }
                println("Aadat after this should be ${aadatTuhr}/${aadatHaz}")
                val hall =  BiggerThanTenDm(mp,gp,dm,hz, output.soorat, output.istihazaBefore,
                    output.haiz, output.istihazaAfter, aadatHaz,aadatTuhr)
                fixedDurations[i].biggerThanTen=hall

                //put it in haz list
                val sd = addTimeToDate(fixedDurations[i].startDate,(output.istihazaBefore*MILLISECONDS_IN_A_DAY))
                val ed = addTimeToDate(sd,(output.haiz*MILLISECONDS_IN_A_DAY).toLong())
                hazDatesList += Entry(sd, ed)


                aadatHaz = dealWithIstihazaAfter(output.istihazaAfter,aadatHaz,aadatTuhr,hazDatesList,fixedDurations, i, sd)



            }
        }
        if(fixedDurations[i].type==DurationType.ISTIMRAR) {//if the last period is an istimrar
            //if we hit a dam bigger than 10, check to see if we have aadat
            if (aadatHaz == (-1).toLong() || aadatTuhr == (-1).toLong()) {
                //give error message
                window.alert("We need both aadaat to be able to solve this")
                break
            } else {
                val veryBigArbitraryNumber = 1000;
                val dm:Long = (veryBigArbitraryNumber*MILLISECONDS_IN_A_DAY).toLong()
                val mp = fixedDurations[i - 1].timeInMilliseconds
                val gp = aadatTuhr
                val hz = aadatHaz
                val output: FiveSoortainOutput = threeSoortainIstimrar(mp, gp, hz)
                //update aadats
                aadatHaz = output.haiz.toLong()
                if(output.aadatTuhrChanges && fixedDurations[i-1].type!=DurationType.TUHREFAASID){
                    //if mp is not tuhrefaasid
                    aadatTuhr = mp;
                }
                val hall =  BiggerThanTenDm(mp,gp,dm,hz, output.soorat, output.istihazaBefore,output.haiz, output.istihazaAfter, aadatHaz,aadatTuhr)
                fixedDurations[i].biggerThanTen=hall
                //put it in haz list
                val sd = addTimeToDate(fixedDurations[i].startDate!!,(output.istihazaBefore*MILLISECONDS_IN_A_DAY))
                val ed = addTimeToDate(sd,(output.haiz*MILLISECONDS_IN_A_DAY))
                hazDatesList += Entry(sd, ed)

                //for the moment, let's place 3 cycles in haz list in isitmrar
                val quotient = 3

                //put the right number of hazes in haz list
                var aadatTuhrStartDate:Date = ed
                var aadatHaizStartDate:Date = sd
                var aadatHaizEndDate:Date = ed
                for (j in 1 .. quotient){
                    aadatHaizStartDate = addTimeToDate(aadatTuhrStartDate,(aadatTuhr*MILLISECONDS_IN_A_DAY))
                    aadatHaizEndDate = addTimeToDate(aadatHaizStartDate,(aadatHaz*MILLISECONDS_IN_A_DAY))
                    hazDatesList += Entry(aadatHaizStartDate,aadatHaizEndDate)

                    aadatTuhrStartDate=aadatHaizEndDate
                }

            }
        }
    }

}

fun dealWithIstihazaAfter(istihazaAfter: Long, aadatHaz: Long, aadatTuhr: Long, hazDatesList: MutableList<Entry>, fixedDurations: MutableList<FixedDuration>, i: Int, sdOfIstihazaAfter:Date):Long {
    //if istihazaAfter is bigger than addatTuhr +3, run daur
    var returnAadatHaiz = aadatHaz
    if (istihazaAfter>=aadatTuhr+3 && fixedDurations[i].type==DurationType.DAM){
        println("Istihaza After is bigger than aadatTuhr+3, running daur")
        //find quotient and remainder
        val remainder = istihazaAfter%(aadatHaz+aadatTuhr)
        val quotient = ((istihazaAfter-remainder)/(aadatHaz+aadatTuhr)).toInt()

        //put the right number of hazes in haz list
        var aadatTuhrStartDate:Date = sdOfIstihazaAfter
        var aadatHaizStartDate:Date = addTimeToDate(aadatTuhrStartDate,(aadatTuhr))
        var aadatHaizEndDate:Date = addTimeToDate(aadatHaizStartDate,(aadatHaz))
        for (j in 1 .. quotient){
            aadatHaizStartDate = addTimeToDate(aadatTuhrStartDate,(aadatTuhr))
            aadatHaizEndDate = addTimeToDate(aadatHaizStartDate,(aadatHaz))
            hazDatesList += Entry(aadatHaizStartDate,aadatHaizEndDate)

            aadatTuhrStartDate=aadatHaizEndDate
        }


        if (remainder<aadatTuhr + 3){//it ended in tuhr or right between haz and tuhr
            //add istihazaAfter to next Tuhur mark it as fasid
            //if it exists
            //if remainder is not equal to zero
            if(i<fixedDurations.size-1 && remainder>0){//there is a tuhur after this
                fixedDurations[i+1].type=DurationType.TUHREFAASID
//                            fixedDurations[i+1].timeInMilliseconds+=(remainder*MILLISECONDS_IN_A_DAY).toLong()
//                            fixedDurations[i].timeInMilliseconds-=(remainder*MILLISECONDS_IN_A_DAY).toLong()
                fixedDurations[i+1].istihazaAfter=remainder
            }

        }else{//it ended in less than haiz
            //change aadatHaiz
            returnAadatHaiz = remainder-aadatTuhr

            //add to haz list
            val lastHzEndTime = hazDatesList[hazDatesList.lastIndex].endTime
            val strTime = addTimeToDate(lastHzEndTime,(aadatTuhr*MILLISECONDS_IN_A_DAY).toLong())
            val endTime = addTimeToDate(strTime,(aadatHaz*MILLISECONDS_IN_A_DAY).toLong())
            hazDatesList += Entry(strTime,endTime)

        }

    }else{
        println("Istihaza After is smaller than aadatTuhr+3, not running daur")

        //else add istihazaAfter to next Tuhr, mark it as fasid
        //if it exists
        if(i<fixedDurations.size-1){
            println("marking next tuhr as fasid, because there is istihaza at the end of this dam")
            fixedDurations[i+1].type=DurationType.TUHREFAASID
//                        fixedDurations[i+1].timeInMilliseconds+=(output.istihazaAfter*MILLISECONDS_IN_A_DAY).toLong()
            //                       fixedDurations[i].timeInMilliseconds-=(output.istihazaAfter*MILLISECONDS_IN_A_DAY).toLong()
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
    val soorat: Soortain;
    val istihazaBefore:Long;
    val haiz:Long;
    val istihazaAfter:Long;
    val aadatTuhrChanges:Boolean; // 0 for gp, 1 for mp (change)
    val veryBigArbitraryNumber = 1000;

    if (mp <= gp) {    //Qism A (Always A-1 in istimrar)
        soorat = Soortain.A_1;
        istihazaBefore = gp-mp;
        haiz = hz;
        istihazaAfter = (veryBigArbitraryNumber*MILLISECONDS_IN_A_DAY).toLong();
        aadatTuhrChanges = false;
    }else {	// mp>gp qism B
        if (hz - (mp - gp) >= 3*MILLISECONDS_IN_A_DAY) {							// soorat B-2
            soorat = Soortain.B_2;
            istihazaBefore = 0;
            haiz = hz-(mp-gp);
            istihazaAfter = (veryBigArbitraryNumber*MILLISECONDS_IN_A_DAY).toLong();
            aadatTuhrChanges = true;
        }else{ //if (hz - (mp - gp) < 3) {						// soorat B-3
            soorat = Soortain.B_3;
            istihazaBefore = 0;
            haiz = hz;
            istihazaAfter = (veryBigArbitraryNumber*MILLISECONDS_IN_A_DAY).toLong();
            aadatTuhrChanges = true;
        }
    }
    return FiveSoortainOutput(soorat,istihazaBefore,haiz,istihazaAfter, aadatTuhrChanges)
}

fun fiveSoortain(mp: Long, gp: Long, dm: Long, hz:Long):FiveSoortainOutput{
    val soorat: Soortain;
    val istihazaBefore:Long;
    val haiz:Long;
    val istihazaAfter:Long;
    val aadatTuhrChanges:Boolean; // 0 for gp, 1 for mp (change)

    if (mp <= gp) {    //Qism A
        if (hz <= dm - (gp - mp)) {	                  // soorat A-1
            // if GP==MP, we would output 0 istihaza, not good!
            // 0 istihaza is ok, for now. just no negative numbers, please
            soorat = Soortain.A_1;
            istihazaBefore = gp-mp;
            haiz = hz;
            istihazaAfter = dm-(gp-mp)-hz;
            aadatTuhrChanges = false;
        }
        else if (3*MILLISECONDS_IN_A_DAY <= dm-(gp-mp) && dm-(gp-mp) < hz) {  // soorat A-2
            soorat = Soortain.A_2;
            istihazaBefore = gp-mp;
            haiz = dm-(gp-mp);
            istihazaAfter = 0;
            aadatTuhrChanges = false;
        }
        else{ //if (dm - (gp - mp) < 3*MILLISECONDS_IN_A_DAY) {                  // soorat A-3
            soorat = Soortain.A_3;
            istihazaBefore = 0;
            haiz = hz;
            istihazaAfter = dm-hz;
            aadatTuhrChanges = true;
        }
    }else {	// mp>gp qism B
        if (hz - (mp - gp) >= 3*MILLISECONDS_IN_A_DAY) {							// soorat B-2
            soorat = Soortain.B_2;
            istihazaBefore = 0;
            haiz = hz-(mp-gp);
            istihazaAfter = dm-(hz-(mp-gp));
            aadatTuhrChanges = true;
        }else{ //if (hz - (mp - gp) < 3*MILLISECONDS_IN_A_DAY) {						// soorat B-3
            soorat = Soortain.B_3;
            istihazaBefore = 0;
            haiz = hz;
            istihazaAfter = dm-hz;
            aadatTuhrChanges = true;
        }
    }
  return FiveSoortainOutput(soorat,istihazaBefore,haiz,istihazaAfter, aadatTuhrChanges)
}