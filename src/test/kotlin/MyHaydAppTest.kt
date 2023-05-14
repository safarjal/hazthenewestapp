import kotlinx.datetime.internal.JSJoda.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

fun convertHazDatesListToDurations(hazDatesList: List<Entry>):List<Duration>{
    val durAnswers = mutableListOf<Duration>()
    var time = Instant.EPOCH.getMillisLong()
    for(hazDates in hazDatesList){
        val tuhrTime = hazDates.startTime.getMillisLong()-time
        if(tuhrTime!=0L){
            durAnswers+=Duration(DurationType.TUHR,tuhrTime)
        }
        val hazTime = hazDates.endTime.getMillisLong()-hazDates.startTime.getMillisLong()
        durAnswers+=Duration(DurationType.HAIZ,hazTime)
        time = hazDates.endTime.getMillisLong()
    }
    return durAnswers
}
fun getAnswerListFromMyHaydAppString(string: String):List<Duration>{
//   "H(31, 4, 38) 10B 15T: 10H 15T"
//    10H 15T
    val answerStr = string.substringAfter(": ")

    val listOfAnswerStrings = answerStr.split(" ")
    val durationsList = mutableListOf<Duration>()
    for(dur in listOfAnswerStrings){
        val durLength = dur.removeSuffix("H").removeSuffix("T").toInt()
        val durType = if(dur.endsWith("H")) {DurationType.HAIZ} else { DurationType.TUHR}
        durationsList += Duration(durType, durLength.getMilliDays())
    }
    if(durationsList.last().type==DurationType.TUHR){
        durationsList.removeLast()  //.removeAt(durationsList.lastIndex)
    }
    if(durationsList.last().type==DurationType.TUHR){
        durationsList.removeLast() //.removeAt(durationsList.lastIndex)
    }
    return durationsList

}
fun getHabitsListFromMyHaydAppString(string: String):PreMaslaValues{
//   "H(31, 4, 38) 10B 15T: 10H 15T"
//    habit of tuhr/haidh/nifas
    val habitsStr = string.substringBefore(")").removePrefix("H(")
    val habitOfTuhr = habitsStr.split(", ")[0].toInt()
    val habitOfHaz = habitsStr.split(", ")[1].toInt()
    val habitOfNifas = habitsStr.split(", ")[2].toInt()
    return PreMaslaValues(habitOfHaz.getMilliDays(), habitOfTuhr.getMilliDays())
}
fun getDurationsListFromMyHaydAppString(string: String):List<Duration>{
//   "H(31, 4, 38) 10B 15T: 10H 15T"
//   "10B 15T"
    val durationSequenceStr = string.substringAfter(") ").substringBefore(":")

    val listOfDurationStrings = durationSequenceStr.split(" ")
    val durationsList = mutableListOf<Duration>()
    for(dur in listOfDurationStrings){
        val durLength = dur.removeSuffix("B").removeSuffix("T").toInt()
        val durType = if(dur.endsWith("B")) {DurationType.DAM} else { DurationType.TUHR}
        durationsList += Duration(durType, durLength.getMilliDays())
    }
    return durationsList
}

//fun convertHazDatesListToDurations(hazDatesList: List<Entry>):List<Duration>{
//    val durAnswers = mutableListOf<Duration>()
//    var time = Instant.EPOCH.getMillisLong()
//
//    for(hazDates in hazDatesList){
//        val tuhrTime = hazDates.startTime.getMillisLong() - time
//        if(tuhrTime!=0L){
//            durAnswers+=Duration(DurationType.TUHR, tuhrTime)
//        }
//        val hazTime = hazDates.endTime.getMillisLong()-hazDates.startTime.getMillisLong()
//        durAnswers += Duration(DurationType.HAIZ, hazTime)
//        time = hazDates.endTime.getMillisLong()
//    }
//    return durAnswers
//}
//fun getAnswerListFromMyHaydAppString(string: String):List<Duration>{
////   "H(31, 4, 38) 10B 15T: 10H 15T"
////    10H 15T
//    val answerStr = string.substringAfter(": ")
//
//    val listOfAnswerStrings = answerStr.split(" ")
//    val durationsList = mutableListOf<Duration>()
//    for(dur in listOfAnswerStrings){
//        val durLength = dur.removeSuffix("H").removeSuffix("T").toInt()
//        val durType = if(dur.endsWith("H")) {DurationType.HAIZ} else { DurationType.TUHR}
//        durationsList += Duration(durType, durLength.getMilliDays())
//    }
//    if(durationsList.last().type==DurationType.TUHR){
//        durationsList.removeLast()  //.removeAt(durationsList.lastIndex)
//    }
//    if(durationsList.last().type==DurationType.TUHR){
//        durationsList.removeLast() //.removeAt(durationsList.lastIndex)
//    }
//    return durationsList
//
//}
//fun getHabitsListFromMyHaydAppString(string: String):PreMaslaValues{
////   "H(31, 4, 38) 10B 15T: 10H 15T"
////    habit of tuhr/haidh/nifas
//    val habitsStr = string.substringBefore(")").removePrefix("H(").split(", ")
//    val habitOfTuhr = habitsStr[0].toInt()
//    val habitOfHaz = habitsStr[1].toInt()
//    val habitOfNifas = habitsStr[2].toInt()
//    return PreMaslaValues(habitOfHaz.getMilliDays(), habitOfTuhr.getMilliDays())
//}
//fun getDurationsListFromMyHaydAppString(string: String):List<Duration>{
////   "H(31, 4, 38) 10B 15T: 10H 15T"
////   "10B 15T"
//    val durationSequenceStr = string.substringAfter(") ").substringBefore(":")
//
//    val listOfDurationStrings = durationSequenceStr.split(" ")
//    val durationsList = mutableListOf<Duration>()
//    for(dur in listOfDurationStrings){
//        val durLength = dur.removeSuffix("B").removeSuffix("T").toInt()
//        val durType = if(dur.endsWith("B")) {DurationType.DAM} else { DurationType.TUHR}
//        durationsList += Duration(durType, durLength.getMilliDays())
//    }
//    return durationsList
//}

fun compareStrings(string: String): Array<List<Duration>> {
    val durations = getDurationsListFromMyHaydAppString(string)
    val preMaslaValues = getHabitsListFromMyHaydAppString(string)
    val output = handleEntries(convertDurationsIntoEntries(durations,AllTheInputs(null, preMaslaValues)))
    val theirAnswer = getAnswerListFromMyHaydAppString(string)
    val ourAnswer = convertHazDatesListToDurations(output.hazDatesList)
    return arrayOf(theirAnswer, ourAnswer)
}
class MyHadhAppTest {
    @Test
    fun issue214testMyHaydAppMaslas() {
        val stringOfStrings =
            "H(31, 4, 38) 10B 15T: 10H 15T\n" +
            "H(17, 6, 3) 7B 15T: 7H 15T\n" +
            "H(25, 3, 5) 6B 15T: 6H 15T\n" +
//          "H(34, 7, 36) 18B 15T: 7H 11T 15T\n" +
            "H(24, 3, 10) 5B 15T: 5H 15T\n" +
            "H(17, 9, 24) 7B 35T 20B 23T 9B 15T: 7H 35T 7H 36T 9H 15T\n" +
            "H(30, 10, 23) 6B 22T 3B 7T 3B 24T 4B 15T: 6H 30T 5H 24T 4H 15T\n" +
            "H(30, 6, 39) 4B 15T 6B 17T 9B 21T 6B 15T: 4H 15T 6H 17T 9H 21T 6H 15T\n" +
            "H(21, 4, 0) 5B 21T 10B 15T 14B 22T 5B 15T: 5H 21T 10H 21T 8H 22T 5H 15T\n" +
//          "H(23, 3, 19) 16B 19T 8B 34T 3B 15T: 3H 32T 8H 34T 3H 15T\n" +
//          "H(21, 6, 37) 4B 3T 7B 25T 8B 22T 6B 21T 3B 15T: 6H 33T 8H 22T 6H 21T 3H 15T\n" +
            "H(17, 5, 39) 8B 25T 18B 19T 6B 17T 5B 15T: 8H 25T 8H 29T 6H 17T 5H 15T\n" +
            "H(28, 4, 10) 9B 20T 8B 24T 10B 15T: 9H 20T 8H 24T 10H 15T\n" +
            "H(27, 10, 25) 7B 20T 2B 21T 5B 15T: 7H 43T 5H 15T\n" +
            "H(34, 7, 4) 3B 24T 14B 14T 17B 15T: 3H 34T 3H 32T 15T\n" +
            "H(24, 5, 18) 8B 15T: 8H 15T\n" +
//          "H(26, 8, 28) 12B 22T 15B 20T 20B 19T 10B 15T: 8H 26T 8H 27T 7H 32T 10H 15T\n" +
            "H(32, 3, 30) 6B 25T 6B 15T: 6H 25T 6H 15T\n" +
            "H(33, 3, 28) 5B 33T 8B 24T 6B 13T 6B 20T 4B 15T: 5H 33T 8H 33T 8H 28T 4H 15T\n" +
//          "H(24, 5, 33) 2B 8T 6B 15T: 5H 11T 15T\n" +
//          "H(29, 3, 13) 9B 5T 7B 20T 20B 21T 3B 15T: 3H 38T 3H 38T 3H 15T\n" +
            "H(35, 5, 36) 9B 22T 5B 35T 4B 28T 7B 28T 6B 15T: 9H 22T 5H 35T 4H 28T 7H 28T 6H 15T\n" +
            "H(17, 5, 35) 4B 19T 5B 15T: 4H 19T 5H 15T\n" +
//          "H(29, 3, 18) 14B 20T 3B 7T 20B 30T 19B 8T 9B 15T: 3H 31T 3H 57T 3H 29T 3H 1T 15T\n" +
//          "H(28, 3, 28) 17B 14T 4B 15T: 3H 28T 3H 1T 15T\n" +
//          "H(25, 10, 4) 17B 15T 19B 15T: 10H 25T 10H 6T 15T\n" +
            "H(27, 6, 26) 6B 22T 2B 15T 9B 24T 1B 23T 10B 15T: 6H 39T 9H 48T 10H 15T"
        val stringsArray = stringOfStrings.split("\n")
        var i = 0
        for(string in stringsArray){
            i++
            val (theirAnswer, ourAnswer) = compareStrings(string)
            assertEquals(theirAnswer.size, ourAnswer.size, message = "Size Different: $i: $string")
            for(i in theirAnswer.indices){
                assertEquals(theirAnswer[i].type, ourAnswer[i].type, message = "Type Different: $i: $string")
                assertEquals(theirAnswer[i].timeInMilliseconds, ourAnswer[i].timeInMilliseconds, message = "Time Different: $i: $string")
            }
        }
    }
}