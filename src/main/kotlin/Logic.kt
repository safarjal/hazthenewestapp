import kotlinx.browser.window
import kotlin.js.Date

var outputStr = ""
lateinit var firstStartTime:Date

fun handleEntries(entries: List<Entry>) {
    firstStartTime = entries[0].startTime
    val times = entries
        .flatMap { entry -> listOf(entry.startTime, entry.endTime) }
        .map { it.getTime() }
    require(times == times.sorted())
    //step 1 - create an array of dam and tuhur durations in days
    var isDam = true
    val durations = times.zipWithNext { firstTime, secondTime ->
        val type = if (isDam) DurationType.DAM else DurationType.TUHR
        isDam = !isDam
        Duration(type, (secondTime - firstTime) / 86400000, mutableListOf<Int>())
    }
    for (duration in durations) {
        println("duration type = ${duration.type}, duration days = ${duration.days}")
    }
    val fixedDurations = durations.map { it.copy() }.toMutableList()
    outputStr=""
    addIndicesToFixedDurations(fixedDurations)
    removeTuhrLessThan15(fixedDurations)
    removeDamLessThan3(fixedDurations)
    dealWithBiggerThan10Dam(fixedDurations, durations)
    println(outputStr)
}
fun addIndicesToFixedDurations(fixedDurations: MutableList<Duration>){
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

fun removeTuhrLessThan15 (fixedDurations: MutableList<Duration>){
    var i=0
    while(i < fixedDurations.size){//iterate through durations
        //if there is a tuhr less than 15
        if(fixedDurations[i].days<15 && fixedDurations[i].type== DurationType.TUHR){
            //it must be surrounded by dams on either side. increase size of damBefore. delete tuhr and dam after
            fixedDurations[i-1].days += fixedDurations[i].days + fixedDurations[i+1].days
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
fun removeDamLessThan3 (fixedDurations: MutableList<Duration>){
    var i=0
    while (i<fixedDurations.size-1){
        if(fixedDurations[i].type==DurationType.DAM && fixedDurations[i].days<3){
            if(i>0){//there is tuhur behind this and in front of it
                fixedDurations[i-1].type = DurationType.TUHREFAASID
                fixedDurations[i-1].days += fixedDurations[i].days + fixedDurations[i+1].days
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

fun dealWithBiggerThan10Dam(fixedDurations: MutableList<Duration>, durations: List<Duration>){
    var istihazaAfter:Double = 0.0
    var aadatHaz:Double = -1.0
    var aadatTuhr:Double = -1.0
    var i=0
    while (i<fixedDurations.size){
        //iterate through fixedDurations

        //also, start writing output
            // line 1
        outputStr += "${fixedDurations[i].days} days ${fixedDurations[i].type}\n"
        //line 2 get the sum of everything that made this up
        if(fixedDurations[i].indices.size>1){
            var sum:Double = 0.0
            var str = ""
            for (index in fixedDurations[i].indices){
                sum+=durations[index].days
                str += " + ${durations[index].days}"
            }
            str=str.removePrefix(" + ")
            outputStr += "${str} = ${sum}\n"
        }
        //if there is an added istihaza after, get that
        if(istihazaAfter!=0.0){
            outputStr+="${fixedDurations[i].days-istihazaAfter} days tuhr + ${istihazaAfter} days istihaza = ${fixedDurations[i].days} days tuhr-e-faasid\n"
            istihazaAfter=0.0
        }


        //get aadat
        if(fixedDurations[i].type==DurationType.DAM && fixedDurations[i].days<=10){
            aadatHaz = fixedDurations[i].days
            if(i>0 && fixedDurations[i-1].type==DurationType.TUHR){
                aadatTuhr = fixedDurations[i-1].days
            }
        }else if(fixedDurations[i].type==DurationType.DAM && fixedDurations[i].days>10){
            //if we hit a dam bigger than 10, check to see if we have aadat
            if(aadatHaz==-1.0 ||aadatTuhr==-1.0){
                //give error message
                window.alert("We need both aadaat to be able to solve this")//???
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

                //output hukm:
                outputStr += "Rough work \n"
                outputStr += "MP\tGP\tDm\tHz\tQism\n"
                outputStr += "${mp}\t${gp}\t${dm}\t${hz}\t${output.soorat}\n"
                outputStr += "Out of ${dm} days, the first ${output.istihazaBefore} days are istihaza, "
                outputStr += "then the next ${output.haiz} days are haiz, "

                //if istihazaAfter is bigger than addatTuhr +3, run daur
                if (output.istihazaAfter>=aadatTuhr+3){
                    //find quotient and remainder
                    val remainder = output.istihazaAfter%(aadatHaz+aadatTuhr)
                    val quotient = ((output.istihazaAfter-remainder)/(aadatHaz+aadatTuhr)).toInt()

                    for (j in 1 .. quotient){
                        outputStr+="then the next ${aadatTuhr} days are istihaza, " +
                                "then the next ${aadatHaz} days are haiz, "
                    }
                    if (remainder<aadatTuhr + 3){//it ended in tuhr
                        outputStr+="then the last ${remainder} days are istihaza.\n"
                        //add istihazaAfter to next Tuhur mark it as fasid
                        //if it exists
                        if(i<fixedDurations.size-1){//there is a tuhur after this
                            fixedDurations[i+1].type=DurationType.TUHREFAASID
                            fixedDurations[i+1].days+=remainder
                            fixedDurations[i].days-=remainder
                            istihazaAfter = remainder
                        }

                    }else{//it ended in haiz
                        outputStr+="then the next ${aadatTuhr} days are tuhr, then the last ${remainder-aadatTuhr} days are haiz\n"
                        //change aadatHaiz
                        aadatHaz = remainder-aadatTuhr

                    }

                }else{
                    outputStr += "and the last ${output.istihazaAfter} days are istihaza.\n"
                    //else add istihazaAfter to next Tuhr, mark it as fasid
                        //if it exists
                    if(i<fixedDurations.size-1){
                        fixedDurations[i+1].type=DurationType.TUHREFAASID
                        fixedDurations[i+1].days+=output.istihazaAfter
                        fixedDurations[i].days-=output.istihazaAfter
                        istihazaAfter = output.istihazaAfter
                    }

                }
                //add aadat line
                outputStr+="Aadat: ${aadatHaz}/${aadatTuhr}\n"
                //output hukm in dates


            }

        }
        i++
    }
}

class FiveSoortainOutput (
    val soorat: Soortain,
    val istihazaBefore: Double,
    val haiz:Double,
    val istihazaAfter: Double,
    val aadatTuhrChanges:Boolean

        )
fun fiveSoortain(mp: Double, gp: Double, dm: Double, hz:Double):FiveSoortainOutput{
    //This function will return an array called answer
    //answer[0] will be istihaza before, if any, answer[1] will be haiz, answer [2] will be istihaza after, if any. answer [3] can be soorat.
    //I'm putting soorat A-1 as 1, A-2 as 2, A-3 as 3, B-2 as 5 and B-3 as 6. Yes, we are skipping numbers here, but I can remember this.
    //I'm not adding aadat of haiz and tuhur in answer. aadat of haiz will always be answer[1]. aadat of tuhur depends, so will calculate aadat elsewhere.
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