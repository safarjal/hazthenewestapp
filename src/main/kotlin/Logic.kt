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

fun handleEntries(entries: List<Entry>, istimrar:Boolean): String {
    firstStartTime = entries[0].startTime
    val times = entries
        .flatMap { entry -> listOf(entry.startTime, entry.endTime) }
        .map { it.getTime().toLong() }
    require(times == times.sorted())
    //step 1 - create an array of dam and tuhur durations in days
    var isDam = true
    val durations = times.zipWithNext { firstTime, secondTime ->
        val type = if (isDam) DurationType.DAM else DurationType.TUHR
        isDam = !isDam
        Duration(type, secondTime - firstTime)
    }
    val fixedDurations = durations
        .map { duration ->
            FixedDuration(duration.type, duration.timeInMilliseconds)
        }
        .toMutableList()
    addIndicesToFixedDurations(fixedDurations)
    removeTuhrLessThan15(fixedDurations)
    removeDamLessThan3(fixedDurations)
    addStartDateToFixedDurations(fixedDurations)

    if(istimrar==true){//make the last period an istimrar type
        fixedDurations[fixedDurations.size-1].type=DurationType.ISTIMRAR;
    }

    return dealWithBiggerThan10Dam(fixedDurations, durations)
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
//        println(fixedDurations[i].indices)
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
                fixedDurations[i-1].type = DurationType.TUHREFAASID
                fixedDurations[i-1].timeInMilliseconds += fixedDurations[i].timeInMilliseconds + fixedDurations[i+1].timeInMilliseconds
                fixedDurations[i-1].indices.addAll(fixedDurations[i].indices)
                fixedDurations.removeAt(i)
                fixedDurations[i-1].indices.addAll(fixedDurations[i].indices)
                fixedDurations.removeAt(i)
                i--
            }
        }
        i++
    }
}
//step 4 - Deal with bigger than 10 dam
//          iterate through array. getting aadat on the way. each time you encounter a dam
//          less than 10, update it into HazAadat. each time you encounter a tuhur
//          (not a tuhr-e-faasid), update it into aadat too.

fun dealWithBiggerThan10Dam(fixedDurations: MutableList<FixedDuration>, durations: List<Duration>): String {
    var hazDatesList = mutableListOf<Entry>()
    var outputStr = ""
    var aadatHaz:Double = -1.0
    var aadatTuhr:Double = -1.0
    for (i in fixedDurations.indices){
        //iterate through fixedDurations

        //also, start writing output
            // line 1
        outputStr += outputStringHeaderLine(fixedDurations, i)
        //line 2 get the sum of everything that made this up
        outputStr+= outputStringSumOfIndicesLine(fixedDurations,durations,i)

        //if there is an added istihaza after, get that
        outputStr+=outputStringIstihazaAfterLine(fixedDurations, i)


        //get aadat if dam is less than 10
        if(fixedDurations[i].type==DurationType.DAM && fixedDurations[i].days<=10){
            aadatHaz = fixedDurations[i].days
            if(i>0 && fixedDurations[i-1].type==DurationType.TUHR){
                aadatTuhr = fixedDurations[i-1].days
            }
            //put it in haz lis
            hazDatesList += Entry(fixedDurations[i].startDate!!, addTimeToDate(fixedDurations[i].startDate!!, fixedDurations[i].timeInMilliseconds))

        }else if(fixedDurations[i].type==DurationType.DAM && fixedDurations[i].days>10){
            //if we hit a dam bigger than 10, check to see if we have aadat
            if(aadatHaz==-1.0 ||aadatTuhr==-1.0){
                //give error message
                window.alert("We need both aadaat to be able to solve this")
                break
            }else{
                val mp = fixedDurations[i-1].days
                val gp = aadatTuhr
                val dm = fixedDurations[i].days
                val hz = aadatHaz
                val output:FiveSoortainOutput = fiveSoortain(mp, gp, dm, hz)
                //deal with output
                //update aadats
                aadatHaz = output.haiz.toDouble()
                if(output.aadatTuhrChanges && fixedDurations[i-1].type!=DurationType.TUHREFAASID){
                    //if mp is not tuhrefaasid
                    aadatTuhr = mp;
                }
                val hall =  BiggerThanTenDm(mp,gp,dm,hz, output.soorat, output.istihazaBefore,output.haiz, output.istihazaAfter, aadatHaz,aadatTuhr)
                fixedDurations[i].biggerThanTen=hall

                //put it in haz list
                val sd = addTimeToDate(fixedDurations[i].startDate!!,(output.istihazaBefore*MILLISECONDS_IN_A_DAY).toLong())
                val ed = addTimeToDate(sd,(output.haiz*MILLISECONDS_IN_A_DAY).toLong())
                hazDatesList += Entry(sd, ed)


                outputStr+=outputStringBiggerThan10Hall(fixedDurations,i)

                //if istihazaAfter is bigger than addatTuhr +3, run daur
                if (output.istihazaAfter>=aadatTuhr+3){
                    //find quotient and remainder
                    val remainder = output.istihazaAfter%(aadatHaz+aadatTuhr)
                    val quotient = ((output.istihazaAfter-remainder)/(aadatHaz+aadatTuhr)).toInt()

                    //put the right number of hazes in haz list
                    var aadatTuhrStartDate:Date = ed
                    var aadatHaizStartDate:Date = sd
                    var aadatHaizEndDate:Date = ed
                    for (j in 1 .. quotient){
                        aadatHaizStartDate = addTimeToDate(aadatTuhrStartDate,(aadatTuhr*MILLISECONDS_IN_A_DAY).toLong())
                        aadatHaizEndDate = addTimeToDate(aadatHaizStartDate,(aadatHaz*MILLISECONDS_IN_A_DAY).toLong())
                        hazDatesList += Entry(aadatHaizStartDate,aadatHaizEndDate)

                        aadatTuhrStartDate=aadatHaizEndDate
                    }


                    if (remainder<aadatTuhr + 3){//it ended in tuhr
                        //add istihazaAfter to next Tuhur mark it as fasid
                        //if it exists
                        if(i<fixedDurations.size-1){//there is a tuhur after this
                            fixedDurations[i+1].type=DurationType.TUHREFAASID
                            fixedDurations[i+1].timeInMilliseconds+=(remainder*MILLISECONDS_IN_A_DAY).toLong()
                            fixedDurations[i].timeInMilliseconds-=(remainder*MILLISECONDS_IN_A_DAY).toLong()
                            fixedDurations[i+i].istihazaAfter=remainder
                        }

                    }else{//it ended in haiz
                        //change aadatHaiz
                        aadatHaz = remainder-aadatTuhr

                        //add to haz list
                        val lastHzEndTime = hazDatesList[hazDatesList.lastIndex].endTime
                        val strTime = addTimeToDate(lastHzEndTime,(aadatTuhr*MILLISECONDS_IN_A_DAY).toLong())
                        val endTime = addTimeToDate(strTime,(aadatHaz*MILLISECONDS_IN_A_DAY).toLong())
                        hazDatesList += Entry(strTime,endTime)

                    }

                }else{
                    //else add istihazaAfter to next Tuhr, mark it as fasid
                        //if it exists
                    if(i<fixedDurations.size-1){
                        fixedDurations[i+1].type=DurationType.TUHREFAASID
                        fixedDurations[i+1].timeInMilliseconds+=(output.istihazaAfter*MILLISECONDS_IN_A_DAY).toLong()
                        fixedDurations[i].timeInMilliseconds-=(output.istihazaAfter*MILLISECONDS_IN_A_DAY).toLong()
                        fixedDurations[i+1].istihazaAfter = output.istihazaAfter
                    }

                }
            }
        }
        if(fixedDurations[i].type==DurationType.ISTIMRAR) {//if the last period is an istimrar
            //if we hit a dam bigger than 10, check to see if we have aadat
            if (aadatHaz == -1.0 || aadatTuhr == -1.0) {
                //give error message
                window.alert("We need both aadaat to be able to solve this")
                break
            } else {
                val dm = 1000.0
                val mp = fixedDurations[i - 1].days
                val gp = aadatTuhr
                val hz = aadatHaz
                val output: FiveSoortainOutput = threeSoortainIstimrar(mp, gp, hz)
                //update aadats
                aadatHaz = output.haiz.toDouble()
                if(output.aadatTuhrChanges && fixedDurations[i-1].type!=DurationType.TUHREFAASID){
                    //if mp is not tuhrefaasid
                    aadatTuhr = mp;
                }
                val hall =  BiggerThanTenDm(mp,gp,dm,hz, output.soorat, output.istihazaBefore,output.haiz, output.istihazaAfter, aadatHaz,aadatTuhr)
                fixedDurations[i].biggerThanTen=hall
                //put it in haz list
                val sd = addTimeToDate(fixedDurations[i].startDate!!,(output.istihazaBefore*MILLISECONDS_IN_A_DAY).toLong())
                val ed = addTimeToDate(sd,(output.haiz*MILLISECONDS_IN_A_DAY).toLong())
                hazDatesList += Entry(sd, ed)


                outputStr+=outputStringBiggerThan10Hall(fixedDurations,i)


            }
        }
    }
    println(hazDatesList)
    return outputStr
}

class FiveSoortainOutput (
    val soorat: Soortain,
    val istihazaBefore: Double,
    val haiz:Double,
    val istihazaAfter: Double,
    val aadatTuhrChanges:Boolean
)

fun threeSoortainIstimrar(mp:Double, gp:Double, hz: Double):FiveSoortainOutput{
    val soorat: Soortain;
    val istihazaBefore:Double;
    val haiz:Double;
    val istihazaAfter:Double;
    val aadatTuhrChanges:Boolean; // 0 for gp, 1 for mp (change)

    if (mp <= gp) {    //Qism A (Always A-1 in istimrar)
        soorat = Soortain.A_1;
        istihazaBefore = gp-mp;
        haiz = hz;
        istihazaAfter = 1000.0;
        aadatTuhrChanges = false;
    }else {	// mp>gp qism B
        if (hz - (mp - gp) >= 3) {							// soorat B-2
            soorat = Soortain.B_2;
            istihazaBefore = 0.0;
            haiz = hz-(mp-gp);
            istihazaAfter = 1000.0;
            aadatTuhrChanges = true;
        }else{ //if (hz - (mp - gp) < 3) {						// soorat B-3
            soorat = Soortain.B_3;
            istihazaBefore = 0.0;
            haiz = hz;
            istihazaAfter = 1000.0;
            aadatTuhrChanges = true;
        }
    }
    return FiveSoortainOutput(soorat,istihazaBefore,haiz,istihazaAfter, aadatTuhrChanges)
}

fun fiveSoortain(mp: Double, gp: Double, dm: Double, hz:Double):FiveSoortainOutput{
    val soorat: Soortain;
    val istihazaBefore:Double;
    val haiz:Double;
    val istihazaAfter:Double;
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
        else if (3 <= dm-(gp-mp) && dm-(gp-mp) < hz) {  // soorat A-2
            soorat = Soortain.A_2;
            istihazaBefore = gp-mp;
            haiz = dm-(gp-mp);
            istihazaAfter = 0.0;
            aadatTuhrChanges = false;
        }
        else{ //if (dm - (gp - mp) < 3) {                  // soorat A-3
            soorat = Soortain.A_3;
            istihazaBefore = 0.0;
            haiz = hz;
            istihazaAfter = dm-hz;
            aadatTuhrChanges = true;
        }
    }else {	// mp>gp qism B
        if (hz - (mp - gp) >= 3) {							// soorat B-2
            soorat = Soortain.B_2;
            istihazaBefore = 0.0;
            haiz = hz-(mp-gp);
            istihazaAfter = dm-(hz-(mp-gp));
            aadatTuhrChanges = true;
        }else{ //if (hz - (mp - gp) < 3) {						// soorat B-3
            soorat = Soortain.B_3;
            istihazaBefore = 0.0;
            haiz = hz;
            istihazaAfter = dm-hz;
            aadatTuhrChanges = true;
        }
    }
  return FiveSoortainOutput(soorat,istihazaBefore,haiz,istihazaAfter, aadatTuhrChanges)
}