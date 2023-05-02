@file:Suppress("SpellCheckingInspection")

import kotlinx.datetime.internal.JSJoda.Instant
import kotlinx.datetime.internal.JSJoda.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

/* TODO: INSTANT TAKES MONTH PROPERLY, JAN = 1, NONE OF THIS JSDATE PROBLEM.
    AS SUCH, IF YOU FEEL LIKE IT, YOU CAN MOVE BACK ALL THE MONTH NUMBERS AND
    REMOVE THE SMALL MONTH+1 IN THE FUNCTION THAT I PUT IN. UP TO YOU.
 */

fun difference(str1: String?, str2: String?): String? {
    if (str1 == null) {
        return str2
    }
    if (str2 == null) {
        return str1
    }
    val at = indexOfDifference(str1, str2)
    return if (at == INDEX_NOT_FOUND) {
        EMPTY
    } else str1.substring(at - 10, at + 10) + "[ Compared to: ]" + str2.substring(at - 10, at + 10)
}

fun indexOfDifference(cs1: CharSequence?, cs2: CharSequence?): Int {
    if (cs1 === cs2) {
        return INDEX_NOT_FOUND
    }
    if (cs1 == null || cs2 == null) {
        return 0
    }
    var i = 0
    while (i < cs1.length && i < cs2.length) {
        if (cs1[i] != cs2[i]) {
            break
        }
        ++i
    }
    return if (i < cs2.length || i < cs1.length) {
        i
    } else INDEX_NOT_FOUND
}

var INDEX_NOT_FOUND = -1 //"Index Not Found"
var EMPTY = "Empty"

// 2023-04-02T00:22:00Z
// 2020-05-14T00:00:00:00Z

class LogicTest {
//    @Test
//    fun testHandleEntries() {
//        val istimrar = false
//        val entries = listOf(
//            Entry(instant(), instant()),
//            Entry(instant(), instant())
//        )
//        val result = handleEntries(entries, istimrar)
//        assertNotNull(result) // TODO: Replace this with actual test
//    }
    @Test
    fun testRemoveDamLessThan3(){
        val durations = mutableListOf(
            FixedDuration(DurationType.TUHR, timeInMilliseconds= (15.getMilliDays())),
            FixedDuration(DurationType.DAM, timeInMilliseconds= (2.getMilliDays())),
            FixedDuration(DurationType.TUHR, timeInMilliseconds= (15.getMilliDays()))
        )
        removeDamLessThan3(durations)
        //expected that the size will be 1
        assertEquals(1,durations.size)
        //expected that the duration will be 32 days.
        assertEquals(DurationType.TUHREFAASID,durations[0].type)
        assertEquals(32.getMilliDays(), durations[0].timeInMilliseconds)
    }

    @Test
    fun testRemoveTuhrLessThan15(){
        val fixedDurations = mutableListOf(
            FixedDuration(DurationType.DAM, timeInMilliseconds = (2.getMilliDays())),
            FixedDuration(DurationType.TUHR, timeInMilliseconds = (2.getMilliDays())),
            FixedDuration(DurationType.DAM, timeInMilliseconds = (2.getMilliDays()))
        )
        removeTuhrLessThan15(fixedDurations)
        assertEquals(1, fixedDurations.size)
        assertEquals(DurationType.DAM, fixedDurations[0].type)
        assertEquals(6.0,fixedDurations[0].days)
    }
    @Test
    fun testFiveSoortain(){
        val mp = 21.getMilliDays()
        val gp = 16.getMilliDays()
        val dm = 11.getMilliDays()
        val hz = 7.getMilliDays()
        val output:FiveSoortainOutput = fiveSoortain(mp,gp,dm,hz)
        assertEquals(Soortain.B_3, output.soorat)
        assertEquals(0L, output.istihazaBefore)
        assertEquals(7.getMilliDays(), output.haiz)
        assertEquals(4.getMilliDays(), output.istihazaAfter)
        assertEquals(true, output.aadatTuhrChanges)
    }
    @Test
    fun testAddIndicesToFixedDurations(){
        val fixedDurations = mutableListOf(
            FixedDuration(DurationType.DAM, timeInMilliseconds = (2.getMilliDays())),
            FixedDuration(DurationType.TUHR, timeInMilliseconds = (2.getMilliDays())),
            FixedDuration(DurationType.DAM, timeInMilliseconds = (2.getMilliDays()))
        )
        addIndicesToFixedDurations(fixedDurations)
        assertEquals(0,fixedDurations[0].indices[0])
        assertEquals(1,fixedDurations[1].indices[0])
        assertEquals(2,fixedDurations[2].indices[0])
        assertEquals(1,fixedDurations[0].indices.size)
        assertEquals(1,fixedDurations[1].indices.size)
        assertEquals(1,fixedDurations[2].indices.size)

    }
//    @Test
//    fun testAddStartDateToFixedDurations(){
//        firstStartTime = makeInstant(2020, 8, 31)
//        val fixedDurations = mutableListOf(
//            FixedDuration(DurationType.DAM, timeInMilliseconds = (2.millisFromDays()).toLong()),
//            FixedDuration(DurationType.TUHR, timeInMilliseconds = (2.millisFromDays()).toLong()),
//            FixedDuration(DurationType.DAM, timeInMilliseconds = (3.millisFromDays()).toLong())
//        )
//        assertEquals(makeInstant(2020, 8, 31),fixedDurations[0].startDate)
//        assertEquals(makeInstant(2020, 9, 2),fixedDurations[0].startDate)
//        assertEquals(makeInstant(2020, 9, 5),fixedDurations[0].startDate)
//
//    }

    @Test
    fun realWorldLogicTest(){
        val entries = mutableListOf<Entry>()
        entries+=//14 jun - 20 Jun
            Entry(makeInstant(2020, 6, 14), makeInstant(2020, 6, 20))
        entries+=//20 Jul - 27 Jul
            Entry(makeInstant(2020, 7, 20), makeInstant(2020, 7, 27))
        entries+=//30 Aug - 1 Oct
            Entry(makeInstant(2020, 8, 30), makeInstant(2020, 10, 1))

        val output = handleEntries(AllTheInputs(entries))
        val haizDateList = output.hazDatesList
        val expectedHaizDatesList = mutableListOf<Entry>()
        expectedHaizDatesList += Entry(makeInstant(2020, 6, 14), makeInstant(2020, 6, 20))
        expectedHaizDatesList += Entry(makeInstant(2020, 7, 20), makeInstant(2020, 7, 27))
        expectedHaizDatesList += Entry(makeInstant(2020, 8, 30), makeInstant(2020, 9, 2))

        for(i in haizDateList.indices){
            assertEquals(haizDateList[i].startTime.getMillisLong(), expectedHaizDatesList[i].startTime.getMillisLong())
            assertEquals(haizDateList[i].endTime.getMillisLong(), expectedHaizDatesList[i].endTime.getMillisLong())
        }

    }

    @Test
    fun realWorldLogicTest1(){
        val entries = listOf(
            Entry(makeInstant(2020, 4, 15), makeInstant(2020, 4, 21)),
            Entry(makeInstant(2020, 5, 7), makeInstant(2020, 5, 14)),
            Entry(makeInstant(2021, 6, 14), makeInstant(2021, 10, 6)))

        val output = handleEntries(AllTheInputs(
            entries, typeOfInput = TypesOfInputs.DATE_ONLY, typeOfMasla = TypesOfMasla.NIFAS,
            pregnancy = Pregnancy(
                makeInstant(2020, 10, 6),
                makeInstant(2021, 6, 15),
                25.getMilliDays(),
                true))
        )
        val haizDateList = output.hazDatesList

//        From 15 4 2020 to 21 4 2020
//        From 07 5 2020 to 14 5 2020
//        From 15 6 2021 to 10 7 2021
//        From 26 7 2021 to 02 8 2021
//        From 18 8 2021 to 25 8 2021
//        From 10 9 2021 to 17 9 2021
        val expectedHaizDatesList = mutableListOf<Entry>()
        expectedHaizDatesList += Entry(makeInstant(2020, 4, 15), makeInstant(2020, 4, 21))
        expectedHaizDatesList += Entry(makeInstant(2020, 5, 7), makeInstant(2020, 5, 14))
        expectedHaizDatesList += Entry(makeInstant(2021, 6, 15), makeInstant(2021, 7, 10))
        expectedHaizDatesList += Entry(makeInstant(2021, 7, 26), makeInstant(2021, 8, 2))
        expectedHaizDatesList += Entry(makeInstant(2021, 8, 18), makeInstant(2021, 8, 25))
        expectedHaizDatesList += Entry(makeInstant(2021, 9, 10), makeInstant(2021, 9, 17))
        expectedHaizDatesList += Entry(makeInstant(2021, 10, 3), makeInstant(2021, 10, 6))

        for(i in expectedHaizDatesList.indices){
            assertEquals(haizDateList[i].startTime.getMillisLong(), expectedHaizDatesList[i].startTime.getMillisLong())
            assertEquals(haizDateList[i].endTime.getMillisLong(), expectedHaizDatesList[i].endTime.getMillisLong())
        }

    }
    @Test
    fun realWorldLogicTest2(){
        //23 Apr - 28 APr
        //15 may - 21 May
        //pregnancy
        //isqat ghair mustabeen
        //25 Jul - 14 Sept
        //14 sept - 21 sept
        //6 oct - 6 Oct
        val entries = mutableListOf<Entry>()
        entries+=//each month has to be one minus the real
            Entry(makeInstant(2021, 4, 23), makeInstant(2021, 4, 28))
        entries+=
            Entry(makeInstant(2021, 5, 15), makeInstant(2021, 5, 21))
        entries+=//30 Aug - 1 Oct
            Entry(makeInstant(2021, 7, 25), makeInstant(2021, 9, 14))
        entries+=//30 Aug - 1 Oct
            Entry(makeInstant(2021, 9, 14), makeInstant(2021, 9, 21))
        entries+=//30 Aug - 1 Oct
            Entry(makeInstant(2021, 10, 6), makeInstant(2021, 10, 6))

        val output = handleEntries(
            AllTheInputs(
                entries,
                typeOfInput = TypesOfInputs.DATE_ONLY,
                typeOfMasla = TypesOfMasla.NIFAS,
                ikhtilaafaat = Ikhtilaafaat(ghairMustabeenIkhtilaaf = false),
                pregnancy = Pregnancy(
                    makeInstant(2021, 5, 21),
                    makeInstant(2021, 7, 25),
                    25.getMilliDays(),
                    mustabeenUlKhilqat = false
                )
            ),
        )
        val haizDateList = output.hazDatesList

        val expectedHaizDatesList = mutableListOf<Entry>()
        expectedHaizDatesList += Entry(makeInstant(2021, 4, 23), makeInstant(2021, 4, 28))
        expectedHaizDatesList += Entry(makeInstant(2021, 5, 15), makeInstant(2021, 5, 21))
        expectedHaizDatesList += Entry(makeInstant(2021, 7, 25), makeInstant(2021, 7, 31))
        expectedHaizDatesList += Entry(makeInstant(2021, 8, 17), makeInstant(2021, 8, 23))
        expectedHaizDatesList += Entry(makeInstant(2021, 9, 9), makeInstant(2021, 9, 15))
        expectedHaizDatesList += Entry(makeInstant(2021, 10, 6), makeInstant(2021, 10, 6))
        assertEquals(haizDateList.size, expectedHaizDatesList.size)

        for(i in expectedHaizDatesList.indices){
            assertEquals(haizDateList[i].startTime.getMillisLong(), expectedHaizDatesList[i].startTime.getMillisLong())
            assertEquals(haizDateList[i].endTime.getMillisLong(), expectedHaizDatesList[i].endTime.getMillisLong())
        }

    }
    @Test
    fun mashqiSawal1(){
        val entries = mutableListOf<Entry>()
        entries+=//each month has to be one minus the real
            Entry(makeInstant(2020, 12, 25), makeInstant(2020, 12, 30))
        entries+=
            Entry(makeInstant(2021, 1, 20), makeInstant(2021, 1, 22))
        entries+=
            Entry(makeInstant(2021, 1, 25), makeInstant(2021, 1, 26))
        entries+=
            Entry(makeInstant(2021, 2, 13), makeInstant(2021, 2, 20))
        entries+=
            Entry(makeInstant(2021, 3, 3), makeInstant(2021, 3, 3))
        entries+=
            Entry(makeInstant(2021, 3, 6), makeInstant(2021, 3, 9))

        val output = handleEntries(AllTheInputs(
            entries,typeOfInput = TypesOfInputs.DATE_ONLY))
        val haizDateList = output.hazDatesList

        val expectedHaizDatesList = mutableListOf<Entry>()
        expectedHaizDatesList += Entry(makeInstant(2020, 12, 25), makeInstant(2020, 12, 30))
        expectedHaizDatesList += Entry(makeInstant(2021, 1, 20), makeInstant(2021, 1, 26))
        expectedHaizDatesList += Entry(makeInstant(2021, 2, 16), makeInstant(2021, 2, 22))
        assertEquals(haizDateList.size, expectedHaizDatesList.size)

        for(i in expectedHaizDatesList.indices){
            assertEquals(haizDateList[i].startTime.getMillisLong(), expectedHaizDatesList[i].startTime.getMillisLong())
            assertEquals(haizDateList[i].endTime.getMillisLong(), expectedHaizDatesList[i].endTime.getMillisLong())
        }
        val expectedEndingOutputValues = EndingOutputValues(
            true,
            AadatsOfHaizAndTuhr(6.getMilliDays(), 21.getMilliDays()),
            mutableListOf())
        assertEquals(expectedEndingOutputValues.aadats, output.endingOutputValues.aadats)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
//        assertEquals(expectedEndingOutputValues.futureDateType!!.date.getTime(),output.endingOutputValues.futureDateType!!.date.getTime())
//        assertEquals(expectedEndingOutputValues.futureDateType!!.futureDates,output.endingOutputValues.futureDateType!!.futureDates)

    }
    @Test
    fun mashqiSawal2(){
        val entries = mutableListOf<Entry>()
        entries+=//each month has to be one minus the real
            Entry(makeInstant(2020, 12, 5), makeInstant(2020, 12, 14))
        entries+=
            Entry(makeInstant(2021, 1, 5), makeInstant(2021, 1, 14))
        entries+=
            Entry(makeInstant(2021, 2, 7), makeInstant(2021, 2, 13))
        entries+=
            Entry(makeInstant(2021, 2, 21), makeInstant(2021, 3, 11))


        val output = handleEntries(AllTheInputs(
            entries))
        val haizDateList = output.hazDatesList

        val expectedHaizDatesList = mutableListOf<Entry>()
        expectedHaizDatesList += Entry(makeInstant(2020, 12, 5), makeInstant(2020, 12, 14))
        expectedHaizDatesList += Entry(makeInstant(2021, 1, 5), makeInstant(2021, 1, 14))
        expectedHaizDatesList += Entry(makeInstant(2021, 2, 7), makeInstant(2021, 2, 14))
        expectedHaizDatesList += Entry(makeInstant(2021, 3, 10), makeInstant(2021, 3, 11))
        assertEquals(haizDateList.size, expectedHaizDatesList.size)

        for(i in expectedHaizDatesList.indices){
            assertEquals(haizDateList[i].startTime.getMillisLong(), expectedHaizDatesList[i].startTime.getMillisLong())
            assertEquals(haizDateList[i].endTime.getMillisLong(), expectedHaizDatesList[i].endTime.getMillisLong())
        }

        val expectedEndingOutputValues = EndingOutputValues(false, AadatsOfHaizAndTuhr(7.getMilliDays(), 24.getMilliDays()), mutableListOf())
        assertEquals(expectedEndingOutputValues.aadats, output.endingOutputValues.aadats)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
//        assertEquals(expectedEndingOutputValues.futureDateType!!.date.getTime(),output.endingOutputValues.futureDateType!!.date.getTime())
//        assertEquals(expectedEndingOutputValues.futureDateType!!.futureDates,output.endingOutputValues.futureDateType!!.futureDates)
    }
    @Test
    fun mashqiSawal3(){
        val entries = mutableListOf<Entry>()
        entries+=//each month has to be one minus the real
            Entry(makeInstant(2020, 4, 29), makeInstant(2020, 5, 6))
        entries+=
            Entry(makeInstant(2020, 5, 26), makeInstant(2020, 5, 30))
        entries+=
            Entry(makeInstant(2020, 8, 2), makeInstant(2020, 8, 16))


        val output = handleEntries(AllTheInputs(entries))
        val haizDateList = output.hazDatesList

        val expectedHaizDatesList = mutableListOf<Entry>()
        expectedHaizDatesList +=
            Entry(makeInstant(2020, 4, 29), makeInstant(2020, 5, 6))
        expectedHaizDatesList +=
            Entry(makeInstant(2020, 5, 26), makeInstant(2020, 5, 30))
        expectedHaizDatesList +=
            Entry(makeInstant(2020, 8, 2), makeInstant(2020, 8, 6))

        assertEquals(haizDateList.size, expectedHaizDatesList.size)

        for(i in expectedHaizDatesList.indices){
            assertEquals(haizDateList[i].startTime.getMillisLong(), expectedHaizDatesList[i].startTime.getMillisLong())
            assertEquals(haizDateList[i].endTime.getMillisLong(), expectedHaizDatesList[i].endTime.getMillisLong())
        }

        val expectedEndingOutputValues =
            EndingOutputValues(true, AadatsOfHaizAndTuhr(4.getMilliDays(), 64.getMilliDays()), mutableListOf())
        assertEquals(expectedEndingOutputValues.aadats, output.endingOutputValues.aadats)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        //since no future date was provided, it won't be part of the test
//        assertEquals(expectedEndingOutputValues.futureDateType!!.date.getTime(),output.endingOutputValues.futureDateType!!.date.getTime())
//        assertEquals(expectedEndingOutputValues.futureDateType!!.futureDates,output.endingOutputValues.futureDateType!!.futureDates)
    }
    @Test
    fun mashqiSawal4(){
        val entries = mutableListOf<Entry>()
        entries+=//each month has to be one minus the real
            Entry(makeInstant(2020, 4, 16), makeInstant(2020, 4, 24))
        entries+=
            Entry(makeInstant(2020, 5, 23), makeInstant(2020, 6, 1))
        entries+=
            Entry(makeInstant(2020, 8, 2), makeInstant(2020, 8, 17))
        entries+=
            Entry(makeInstant(2020, 9, 5), makeInstant(2020, 9, 28))


        val output = handleEntries(AllTheInputs(entries))
        val haizDateList = output.hazDatesList

        val expectedHaizDatesList = mutableListOf<Entry>()
        expectedHaizDatesList +=
            Entry(makeInstant(2020, 4, 16), makeInstant(2020, 4, 24))
        expectedHaizDatesList +=
            Entry(makeInstant(2020, 5, 23), makeInstant(2020, 6, 1))
        expectedHaizDatesList +=
            Entry(makeInstant(2020, 8, 2), makeInstant(2020, 8, 11))
        expectedHaizDatesList +=
            Entry(makeInstant(2020, 9, 5), makeInstant(2020, 9, 14))
        assertEquals(haizDateList.size, expectedHaizDatesList.size)

        for(i in expectedHaizDatesList.indices){
            assertEquals(haizDateList[i].startTime.getMillisLong(), expectedHaizDatesList[i].startTime.getMillisLong())
            assertEquals(haizDateList[i].endTime.getMillisLong(), expectedHaizDatesList[i].endTime.getMillisLong())
        }

        val expectedEndingOutputValues =
            EndingOutputValues(true, AadatsOfHaizAndTuhr(9.getMilliDays(), 62.getMilliDays()),
//                FutureDateType(makeInstant(2020, 10, 12), TypesOfFutureDates.A3_CHANGING_TO_A2)
                mutableListOf()
            )
        assertEquals(expectedEndingOutputValues.aadats, output.endingOutputValues.aadats)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
//        assertEquals(expectedEndingOutputValues.futureDateType!!.date.getTime(),output.endingOutputValues.futureDateType!!.date.getTime())
//        assertEquals(expectedEndingOutputValues.futureDateType!!.futureDates,output.endingOutputValues.futureDateType!!.futureDates)
    }
    @Test
    fun mashqiSawal5() {
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2020, 7, 2), makeInstant(2020, 7, 4))
        entries +=
            Entry(makeInstant(2020, 7, 8), makeInstant(2020, 7, 10))
        entries +=
            Entry(makeInstant(2020, 8, 1), makeInstant(2020, 8, 3))
        entries +=
            Entry(makeInstant(2020, 8, 7), makeInstant(2020, 8, 9))
        entries +=
            Entry(makeInstant(2020, 8, 31), makeInstant(2020, 9, 4))
        entries +=
            Entry(makeInstant(2020, 9, 7), makeInstant(2020, 9, 10))
        entries +=
            Entry(makeInstant(2020, 9, 29), makeInstant(2020, 10, 4))
        entries +=
            Entry(makeInstant(2020, 10, 7), makeInstant(2020, 10, 8))
        entries +=
            Entry(makeInstant(2020, 10, 21), makeInstant(2020, 11, 2))

        val output = handleEntries(AllTheInputs(entries))
        val haizDateList = output.hazDatesList

        val expectedHaizDatesList = mutableListOf<Entry>()
        expectedHaizDatesList +=
            Entry(makeInstant(2020, 7, 2), makeInstant(2020, 7, 10))
        expectedHaizDatesList +=
            Entry(makeInstant(2020, 8, 1), makeInstant(2020, 8, 9))
        expectedHaizDatesList +=
            Entry(makeInstant(2020, 8, 31), makeInstant(2020, 9, 10))
        expectedHaizDatesList +=
            Entry(makeInstant(2020, 10, 2), makeInstant(2020, 10, 12))
        assertEquals(haizDateList.size, expectedHaizDatesList.size)

        for (i in expectedHaizDatesList.indices) {
            assertEquals(haizDateList[i].startTime.getMillisLong(), expectedHaizDatesList[i].startTime.getMillisLong())
            assertEquals(haizDateList[i].endTime.getMillisLong(), expectedHaizDatesList[i].endTime.getMillisLong())
        }

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(10.getMilliDays(), 22.getMilliDays()),
//                FutureDateType(makeInstant(2020, 11, 3), TypesOfFutureDates.END_OF_AADAT_TUHR)
                mutableListOf()
            )
        assertEquals(expectedEndingOutputValues.aadats, output.endingOutputValues.aadats)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
//        assertEquals(
//            expectedEndingOutputValues.futureDateType!!.date.getTime(),
//            output.endingOutputValues.futureDateType!!.date.getTime()
//        )
//        assertEquals(
//            expectedEndingOutputValues.futureDateType!!.futureDates,
//            output.endingOutputValues.futureDateType!!.futureDates
//        )
    }
    @Test
    fun mashqiSawal6() {
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2020, 2, 27), makeInstant(2020, 3, 3))
        entries +=
            Entry(makeInstant(2020, 3, 25), makeInstant(2020, 3, 31))
        entries +=
            Entry(makeInstant(2020, 4, 21), makeInstant(2020, 4, 26))
        entries +=
            Entry(makeInstant(2021, 2, 14), makeInstant(2021, 4, 14))

        val output = handleEntries(AllTheInputs(
            entries,
            typeOfMasla = TypesOfMasla.NIFAS,
            pregnancy = Pregnancy(makeInstant(2020, 4, 26), makeInstant(2021, 2, 14), 40.getMilliDays(), mustabeenUlKhilqat = true))
        )
        val haizDateList = output.hazDatesList

        val expectedHaizDatesList = mutableListOf<Entry>()
        expectedHaizDatesList +=
            Entry(makeInstant(2020, 2, 27), makeInstant(2020, 3, 3))
        expectedHaizDatesList +=
            Entry(makeInstant(2020, 3, 25), makeInstant(2020, 3, 31))
        expectedHaizDatesList +=
            Entry(makeInstant(2020, 4, 21), makeInstant(2020, 4, 26))
        expectedHaizDatesList +=
            Entry(makeInstant(2021, 2, 14), makeInstant(2021, 3, 26))
        assertEquals(haizDateList.size, expectedHaizDatesList.size)

        for (i in expectedHaizDatesList.indices) {
            assertEquals(haizDateList[i].startTime.getMillisLong(), expectedHaizDatesList[i].startTime.getMillisLong())
            assertEquals(haizDateList[i].endTime.getMillisLong(), expectedHaizDatesList[i].endTime.getMillisLong())
        }

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(5.getMilliDays(), 21.getMilliDays(), parseDays("40")!!),
//                FutureDateType(makeInstant(2021, 4, 16), TypesOfFutureDates.END_OF_AADAT_TUHR)
                mutableListOf()
            )
        assertEquals(expectedEndingOutputValues.aadats, output.endingOutputValues.aadats)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
//        assertEquals(
//            expectedEndingOutputValues.futureDateType!!.date.getTime(),
//            output.endingOutputValues.futureDateType!!.date.getTime()
//        )
//        assertEquals(
//            expectedEndingOutputValues.futureDateType!!.futureDates,
//            output.endingOutputValues.futureDateType!!.futureDates
//        )
    }
    @Test
    fun mashqiSawal7() {
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2021, 1, 19), makeInstant(2021, 1, 26))
        entries +=
            Entry(makeInstant(2021, 2, 15), makeInstant(2021, 2, 20))
        entries +=
            Entry(makeInstant(2021, 3, 27), makeInstant(2021, 4, 3))
        entries +=
            Entry(makeInstant(2021, 4, 12), makeInstant(2021, 4, 12))


        val output = handleEntries(AllTheInputs(entries))
        val haizDateList = output.hazDatesList

        val expectedHaizDatesList = mutableListOf<Entry>()
        expectedHaizDatesList +=
            Entry(makeInstant(2021, 1, 19), makeInstant(2021, 1, 26))
        expectedHaizDatesList +=
            Entry(makeInstant(2021, 2, 15), makeInstant(2021, 2, 20))
        expectedHaizDatesList +=
            Entry(makeInstant(2021, 3, 27), makeInstant(2021, 4, 1))
        assertEquals(haizDateList.size, expectedHaizDatesList.size)

        for (i in expectedHaizDatesList.indices) {
            assertEquals(haizDateList[i].startTime.getMillisLong(), expectedHaizDatesList[i].startTime.getMillisLong())
            assertEquals(haizDateList[i].endTime.getMillisLong(), expectedHaizDatesList[i].endTime.getMillisLong())
        }

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(5.getMilliDays(), 35.getMilliDays()),
//                FutureDateType(makeInstant(2021, 5, 6), TypesOfFutureDates.END_OF_AADAT_TUHR)
                mutableListOf()
            )
        assertEquals(expectedEndingOutputValues.aadats, output.endingOutputValues.aadats)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
//        assertEquals(
//            expectedEndingOutputValues.futureDateType!!.date.getTime(),
//            output.endingOutputValues.futureDateType!!.date.getTime()
//        )
//        assertEquals(
//            expectedEndingOutputValues.futureDateType!!.futureDates,
//            output.endingOutputValues.futureDateType!!.futureDates
//        )
    }
    @Test
    fun mashqiSawal8() {
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2020, 11, 24), makeInstant(2020, 11, 30))
        entries +=
            Entry(makeInstant(2020, 12, 16), makeInstant(2020, 12, 22))
        entries +=
            Entry(makeInstant(2021, 1, 10), makeInstant(2021, 1, 18))
        entries +=
            Entry(makeInstant(2021, 2, 1), makeInstant(2021, 2, 10))
        entries +=
            Entry(makeInstant(2021, 2, 23), makeInstant(2021, 3, 3))
        entries +=
            Entry(makeInstant(2021, 3, 22), makeInstant(2021, 3, 28))
        entries +=
            Entry(makeInstant(2021, 4, 10), makeInstant(2021, 4, 23))

        val output = handleEntries(AllTheInputs(entries))
        val haizDateList = output.hazDatesList

        val expectedHaizDatesList = mutableListOf<Entry>()
        expectedHaizDatesList +=
            Entry(makeInstant(2020, 11, 24), makeInstant(2020, 11, 30))
        expectedHaizDatesList +=
            Entry(makeInstant(2020, 12, 16), makeInstant(2020, 12, 22))
        expectedHaizDatesList +=
            Entry(makeInstant(2021, 1, 10), makeInstant(2021, 1, 13))
        expectedHaizDatesList +=
            Entry(makeInstant(2021, 2, 1), makeInstant(2021, 2, 4))
        expectedHaizDatesList +=
            Entry(makeInstant(2021, 2, 23), makeInstant(2021, 2, 26))
        expectedHaizDatesList +=
            Entry(makeInstant(2021, 3, 22), makeInstant(2021, 3, 25))
        expectedHaizDatesList +=
            Entry(makeInstant(2021, 4, 13), makeInstant(2021, 4, 16))

        assertEquals(haizDateList.size, expectedHaizDatesList.size)

        for (i in expectedHaizDatesList.indices) {
            assertEquals(haizDateList[i].startTime.getMillisLong(), expectedHaizDatesList[i].startTime.getMillisLong())
            assertEquals(haizDateList[i].endTime.getMillisLong(), expectedHaizDatesList[i].endTime.getMillisLong())
        }

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(3.getMilliDays(), 19.getMilliDays()),
//                FutureDateType(makeInstant(2021, 5, 5), TypesOfFutureDates.END_OF_AADAT_TUHR)
                mutableListOf()
            )
        assertEquals(expectedEndingOutputValues.aadats, output.endingOutputValues.aadats)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
//        assertEquals(
//            expectedEndingOutputValues.futureDateType!!.date.getTime(),
//            output.endingOutputValues.futureDateType!!.date.getTime()
//        )
//        assertEquals(
//            expectedEndingOutputValues.futureDateType!!.futureDates,
//            output.endingOutputValues.futureDateType!!.futureDates
//        )
    }
    @Test
    fun mashqiSawal9() {
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real, so does day
            Entry(makeInstant(2020, 5, 4), makeInstant(2020, 5, 12))
        entries +=
            Entry(makeInstant(2020, 6, 2), makeInstant(2020, 6, 10))
        entries +=
            Entry(makeInstant(2021, 3, 5), makeInstant(2021, 4, 4))
        entries +=
            Entry(makeInstant(2021, 4, 14), makeInstant(2021, 4, 18))
        entries +=
            Entry(makeInstant(2021, 4, 23), makeInstant(2021, 4, 23))

        val output = handleEntries(AllTheInputs(entries,
            typeOfMasla = TypesOfMasla.NIFAS,
            pregnancy = Pregnancy(makeInstant(2020, 6, 10), makeInstant(2021, 3, 5), 40.getMilliDays(), mustabeenUlKhilqat = true))
        )
        val haizDateList = output.hazDatesList

        val expectedHaizDatesList = mutableListOf<Entry>()
        expectedHaizDatesList +=
            Entry(makeInstant(2020, 5, 4), makeInstant(2020, 5, 12))
        expectedHaizDatesList +=
            Entry(makeInstant(2020, 6, 2), makeInstant(2020, 6, 10))
        expectedHaizDatesList +=
            Entry(makeInstant(2021, 3, 5), makeInstant(2021, 4, 14))

        assertEquals(haizDateList.size, expectedHaizDatesList.size)

        for (i in expectedHaizDatesList.indices) {
            assertEquals(haizDateList[i].startTime.getMillisLong(), expectedHaizDatesList[i].startTime.getMillisLong())
            assertEquals(haizDateList[i].endTime.getMillisLong(), expectedHaizDatesList[i].endTime.getMillisLong())
        }

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(8.getMilliDays(), 21.getMilliDays()),
                mutableListOf()

            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        //this answer doesn't provide aadat
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
//        assertEquals(
//            expectedEndingOutputValues.futureDateType!!.date.getTime(),
//            output.endingOutputValues.futureDateType!!.date.getTime()
//        )
//        assertEquals(
//            expectedEndingOutputValues.futureDateType!!.futureDates,
//            output.endingOutputValues.futureDateType!!.futureDates
//        )
    }
    @Test
    fun bugMasla1() {
        val entries = listOf(
            Entry(makeInstant(2020, 11, 27), makeInstant(2020, 12, 7)),
            Entry(makeInstant(2020, 12, 31), makeInstant(2021, 1, 7)),
            Entry(makeInstant(2021, 1, 26), makeInstant(2021, 2, 1)),
            Entry(makeInstant(2021, 2, 11), makeInstant(2021, 2, 23)),
            Entry(makeInstant(2021, 2, 28), makeInstant(2021, 3, 2)),
            Entry(makeInstant(2021, 11, 12), makeInstant(2021, 12, 26)),
            Entry(makeInstant(2021, 12, 30), makeInstant(2022, 1, 8))
        )

        val output = handleEntries(AllTheInputs(entries, ikhtilaafaat = Ikhtilaafaat(daurHaizIkhtilaf = true)))
        val haizDateList = output.hazDatesList

        val expectedHaizDatesList = mutableListOf<Entry>()
        expectedHaizDatesList +=
            Entry(makeInstant(2020, 11, 27), makeInstant(2020, 12, 7))
        expectedHaizDatesList +=
            Entry(makeInstant(2020, 12, 31), makeInstant(2021, 1, 7))
        expectedHaizDatesList +=
            Entry(makeInstant(2021, 1, 31), makeInstant(2021, 2, 7))
        expectedHaizDatesList +=
            Entry(makeInstant(2021, 11, 12), makeInstant(2021, 11, 19))
        expectedHaizDatesList +=
            Entry(makeInstant(2021, 12, 13), makeInstant(2021, 12, 20))

        assertEquals(expectedHaizDatesList.size, haizDateList.size)

        for (i in expectedHaizDatesList.indices) {
            assertEquals(haizDateList[i].startTime.getMillisLong(), expectedHaizDatesList[i].startTime.getMillisLong())
            assertEquals(haizDateList[i].endTime.getMillisLong(), expectedHaizDatesList[i].endTime.getMillisLong())
        }

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(7.getMilliDays(), 24.getMilliDays()),
                mutableListOf(

                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
    }
    @Test
    fun testingAadatCase1() {
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 3, 3))

        val output = handleEntries(AllTheInputs(entries))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(-1, -1),
                mutableListOf()
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
    }
    @Test
    fun testingAadatCase1part2() {
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 3, 3))

        val output = handleEntries(AllTheInputs(
            entries,
            PreMaslaValues(
                parseDays("7"),
                parseDays("15")
            ))
        )

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(7.getMilliDays(), 15.getMilliDays()),
                mutableListOf()
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
    }
    @Test
    fun testingAadatCase2() {
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 2, 1), makeInstant(2022, 2, 6))
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 3, 3))

        val output = handleEntries(AllTheInputs(entries))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(5.getMilliDays(), -1),
                mutableListOf()
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
    }
// TODO: THE FIRST ENTRY WAS DAY 0
//    @Test
//    fun testingAadatCase2part2() {
//        val entries = mutableListOf<Entry>()
//        entries +=//each month has to be one minus the real
//            Entry(makeInstant(2022, 2, 1), makeInstant(2022, 1, 6))
//        entries +=//each month has to be one minus the real
//            Entry(makeInstant(2022, 2, 1), makeInstant(2022, 2, 6))
//        entries +=//each month has to be one minus the real
//            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 3, 3))
//
//        val output = handleEntries(AllTheInputs(entries))
//
//        val expectedEndingOutputValues =
//            EndingOutputValues(
//                false,
//                AadatsOfHaizAndTuhr(5.millisFromDays(), 27.millisFromDays()),
//                mutableListOf()
//            )
//        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
//        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
//        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
//    }
    @Test
    fun testingAadatCase3() {
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 1, 1), makeInstant(2022, 1, 5))

        val output = handleEntries(AllTheInputs(entries))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(4.getMilliDays(), -1),
                mutableListOf()
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
    }
    @Test
    fun testingAadatCase3Part2() {
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 1, 1), makeInstant(2022, 1, 5))

        val output = handleEntries(
            AllTheInputs(entries,
                PreMaslaValues(
            8.getMilliDays(),
            30.getMilliDays())))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(4.getMilliDays(), 30.getMilliDays()),
                mutableListOf()
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
    }
    @Test
    fun testingAadatCase5() {
        //A-1
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 1, 1), makeInstant(2022, 1, 9))
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 2, 1), makeInstant(2022, 2, 6))
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 2, 27), makeInstant(2022, 3, 10))

        val output = handleEntries(
            AllTheInputs(entries,
                PreMaslaValues(
            8.getMilliDays(),
            30.getMilliDays())))
        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(5.getMilliDays(), 23.getMilliDays()),
                mutableListOf()
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
    }
    @Test
    fun testingAadatCase6() {
        //B-2
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 1, 1), makeInstant(2022, 1, 9))
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 2, 1), makeInstant(2022, 2, 6))
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 2), makeInstant(2022, 3, 16))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(8.getMilliDays(),
            30.getMilliDays())))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(4.getMilliDays(), 24.getMilliDays()),
                mutableListOf()
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
    }
    @Test
    fun testingAadatCase7() {
        //A-3
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 2, 21), makeInstant(2022, 3, 4))

        val output = handleEntries(
            AllTheInputs(entries,
                PreMaslaValues(
            5.getMilliDays(),
            60.getMilliDays(), 30.getMilliDays(),false)))
        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(5.getMilliDays(), 60.getMilliDays()),
                mutableListOf()
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
    }
    @Test
    fun testingAadatCase8() {
        //A-2
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 2, 21), makeInstant(2022, 3, 26))

        val output = handleEntries(
            AllTheInputs(entries, PreMaslaValues(
            5.getMilliDays(),
            60.getMilliDays(), 30.getMilliDays(),false)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(3.getMilliDays(), 60.getMilliDays()),
                mutableListOf()
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
    }
    @Test
    fun testingAadatCase9() {
        //B-3
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 2, 21), makeInstant(2022, 3, 26))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            5.getMilliDays(),
            30.getMilliDays(), 60.getMilliDays(),false)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(5.getMilliDays(), 60.getMilliDays()),
                mutableListOf()
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
    }
    @Test
    fun testingAadatCase9part2() {
        //B-3
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 2, 21), makeInstant(2022, 3, 26))

        val output = handleEntries(AllTheInputs(entries,
            PreMaslaValues(
            5.getMilliDays(),
            30.getMilliDays(), 60.getMilliDays(),true)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(5.getMilliDays(), 30.getMilliDays()),
                mutableListOf()
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
    }
    @Test
    fun testingAadatCase10() {
        //A-3 becoming A-2
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 2, 1), makeInstant(2022, 3, 1))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            5.getMilliDays(),
            60.getMilliDays(), 30.getMilliDays(),true)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(5.getMilliDays(), 60.getMilliDays()),
                mutableListOf()
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
    }
    @Test
    fun testingAadatCase11() {
        //daur ending in istehaza
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 3, 31))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            6.getMilliDays(),
            15.getMilliDays(), 17.getMilliDays(),false)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(4.getMilliDays(), 17.getMilliDays()),
                mutableListOf()
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
    }
    @Test
    fun testingAadatCase12() {
        //daur ending in haiz, less than 3
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 4, 12))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            6.getMilliDays(),
            15.getMilliDays(), 17.getMilliDays(),false)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(4.getMilliDays(), 17.getMilliDays()),
                mutableListOf()
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
    }
    @Test
    fun testingAadatCase12part3() {
        //daur ending in istehaza, more than aadat, less than 10
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 4, 19))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            6.getMilliDays(),
            15.getMilliDays(), 17.getMilliDays(),false),
        ))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(4.getMilliDays(), 17.getMilliDays()),
                mutableListOf()
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
    }

    @Test
    fun testingAadatCase12part2() {
        //daur ending in haiz, 3, less than aadat
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 4, 15))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            6.getMilliDays(),
            15.getMilliDays(), 17.getMilliDays(),false),
        ))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(4.getMilliDays(), 17.getMilliDays()),
                mutableListOf()
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
    }
    @Test
    fun testingFinalDatesCase1() {
        //daur ending in haiz, 3, less than aadat
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 3, 3))

        val output = handleEntries(AllTheInputs(entries))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(-1L, -1L),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 3, 11), TypesOfFutureDates.AFTER_TEN_DAYS)
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
    }
    @Test
    fun testingFinalDatesCase2() {
        //daur ending in haiz, 3, less than aadat
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 3, 3))

        val output = handleEntries(AllTheInputs(
            entries,
            PreMaslaValues(
            5.getMilliDays(),
            15.getMilliDays()),
        ))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(5.getMilliDays(), 15.getMilliDays()),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 3, 4), TypesOfFutureDates.BEFORE_THREE_DAYS),
                    FutureDateType(makeInstant(2022, 3, 6), TypesOfFutureDates.IC_FORBIDDEN_DATE),
                    FutureDateType(makeInstant(2022, 3, 11), TypesOfFutureDates.AFTER_TEN_DAYS)
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingFinalDatesCase3() {
        //daur ending in haiz, 3, less than aadat
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 3, 4))

        val output = handleEntries(
            AllTheInputs(
            entries,PreMaslaValues(
            5.getMilliDays(),
            15.getMilliDays())))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(3.getMilliDays(), 15.getMilliDays()),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 3, 6), TypesOfFutureDates.IC_FORBIDDEN_DATE),
                    FutureDateType(makeInstant(2022, 3, 11), TypesOfFutureDates.AFTER_TEN_DAYS)
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingFinalDatesCase4() {
        //A-1
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 3, 21))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            7.getMilliDays(),
            25.getMilliDays(),
                20.getMilliDays(),
                false)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(7.getMilliDays(), 25.getMilliDays()),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 4, 7), TypesOfFutureDates.END_OF_AADAT_TUHR)
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingFinalDatesCase4part2() {
        //A-1 about to end
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 4, 7))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            7.getMilliDays(),
            25.getMilliDays(), 20.getMilliDays(),false)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(7.getMilliDays(), 25.getMilliDays()),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 4, 10), TypesOfFutureDates.BEFORE_THREE_DAYS),
                    FutureDateType(makeInstant(2022, 4, 14), TypesOfFutureDates.END_OF_AADAT_HAIZ),
                    FutureDateType(makeInstant(2022, 4, 14), TypesOfFutureDates.IHTIYATI_GHUSL),
                    FutureDateType(makeInstant(2022, 4, 14), TypesOfFutureDates.IC_FORBIDDEN_DATE)
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingFinalDatesCase5() {
        //A-3
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 3, 12))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            5.getMilliDays(),
            60.getMilliDays(), 30.getMilliDays(),false)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(5.getMilliDays(), 60.getMilliDays()),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 3, 31), TypesOfFutureDates.A3_CHANGING_TO_A2)
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingFinalDatesCase5part2() {
        //A-3 - but daur ending in tuhr
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 3, 12))

        val output = handleEntries(AllTheInputs(entries, PreMaslaValues(
            5.getMilliDays(),
            60.getMilliDays(), 15.getMilliDays(),false)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(5.getMilliDays(), 60.getMilliDays()),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 4, 15), TypesOfFutureDates.A3_CHANGING_TO_A2),
//                    FutureDateType(makeInstant(2022, 3, 21), TypesOfFutureDates.END_OF_AADAT_TUHR)

                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingFinalDatesCase5part3() {
        //A-3 - but daur ending in haiz
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 3, 21))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            5.getMilliDays(),
            60.getMilliDays(), 15.getMilliDays(),false)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(5.getMilliDays(), 60.getMilliDays()),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 4, 15), TypesOfFutureDates.A3_CHANGING_TO_A2),
//                    FutureDateType(makeInstant(2022, 3, 24), TypesOfFutureDates.BEFORE_THREE_DAYS),
//                    FutureDateType(makeInstant(2022, 3, 26), TypesOfFutureDates.END_OF_AADAT_HAIZ),
//                    FutureDateType(makeInstant(2022, 3, 26), TypesOfFutureDates.IC_FORBIDDEN_DATE),
//                    FutureDateType(makeInstant(2022, 3, 26), TypesOfFutureDates.IHTIYATI_GHUSL)

                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingFinalDatesCase6() {
        //B-3
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 3, 21))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            5.getMilliDays(),
            30.getMilliDays(), 60.getMilliDays(),false)
        ))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(5.getMilliDays(), 60.getMilliDays()),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 5, 5), TypesOfFutureDates.END_OF_AADAT_TUHR),

                    )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingFinalDatesCase7() {
        //B-2
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 3, 21))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            5.getMilliDays(),
            59.getMilliDays(), 60.getMilliDays(),false)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(4.getMilliDays(), 60.getMilliDays()),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 5, 4), TypesOfFutureDates.END_OF_AADAT_TUHR),

                    )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingFinalDatesCase8() {
        //A-3 shifting to A-2
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 4, 15))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            5.getMilliDays(),
            60.getMilliDays(), 15.getMilliDays(),false)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(5.getMilliDays(), 60.getMilliDays()),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 4, 18), TypesOfFutureDates.BEFORE_THREE_DAYS_MASLA_WILL_CHANGE),
                    FutureDateType(makeInstant(2022, 4, 20), TypesOfFutureDates.END_OF_AADAT_HAIZ),
                    FutureDateType(makeInstant(2022, 4, 20), TypesOfFutureDates.IC_FORBIDDEN_DATE),
                    FutureDateType(makeInstant(2022, 4, 20), TypesOfFutureDates.IHTIYATI_GHUSL)
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingFinalDatesCase9() {
        //ihtiyati ghusl dam less than 3
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 3, 3))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            5.getMilliDays(),
            18.getMilliDays(), 18.getMilliDays(),false)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(5.getMilliDays(), 18.getMilliDays()),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 3, 4), TypesOfFutureDates.BEFORE_THREE_DAYS),
                    FutureDateType(makeInstant(2022, 3, 6), TypesOfFutureDates.IC_FORBIDDEN_DATE),
                    FutureDateType(makeInstant(2022, 3, 11), TypesOfFutureDates.AFTER_TEN_DAYS),
                    FutureDateType(makeInstant(2022, 3, 6), TypesOfFutureDates.IHTIYATI_GHUSL),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingFinalDatesCase9part2a() {
        //ayyame qabliyya
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 3, 3))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            5.getMilliDays(),
            28.getMilliDays(), 18.getMilliDays(),false)
        ))
        val expectedEndingOutputValues =
            EndingOutputValues(
                null,
                AadatsOfHaizAndTuhr(5.getMilliDays(), 28.getMilliDays()),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 3, 11), TypesOfFutureDates.START_OF_AADAT_AYYAMEQABLIYYA),
                    FutureDateType(makeInstant(2022, 3, 11), TypesOfFutureDates.BEFORE_TEN_DAYS_AYYAMEQABLIYYAH),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingFinalDatesCase9part2b() {
        //ayyame qabliyya switch on
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 3, 3))

        val output = handleEntries(AllTheInputs(entries,PreMaslaValues(
            5.getMilliDays(),
            28.getMilliDays(), 18.getMilliDays(),false),
            ikhtilaafaat = Ikhtilaafaat(ayyameQabliyyaIkhtilaf = true)
        ))
        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(5.getMilliDays(), 28.getMilliDays()),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 3, 4), TypesOfFutureDates.BEFORE_THREE_DAYS),
                    FutureDateType(makeInstant(2022, 3, 6), TypesOfFutureDates.IC_FORBIDDEN_DATE),
                    FutureDateType(makeInstant(2022, 3, 11), TypesOfFutureDates.AFTER_TEN_DAYS),
                    FutureDateType(makeInstant(2022, 3, 16), TypesOfFutureDates.IHTIYATI_GHUSL),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }

    @Test
    fun testingFinalDatesCase9part3a() {
        //ihtiyati ghusl dam less than 3 - A-3
        //another ayyame qabliyyah
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 3, 3))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            5.getMilliDays(),
            28.getMilliDays(), 17.getMilliDays(),false)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                null,
                AadatsOfHaizAndTuhr(5.getMilliDays(), 28.getMilliDays()),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 3, 12), TypesOfFutureDates.START_OF_AADAT_AYYAMEQABLIYYA),
                    FutureDateType(makeInstant(2022, 3, 11), TypesOfFutureDates.BEFORE_TEN_DAYS_AYYAMEQABLIYYAH),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingFinalDatesCase9part3b() {
        //ihtiyati ghusl dam less than 3 - A-3
        //another ayyame qabliyyah with ayyame qabliyya off
        val entries = listOf(
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 3, 3))
        )

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            5.getMilliDays(),
            28.getMilliDays(), 17.getMilliDays(),false),
            ikhtilaafaat = Ikhtilaafaat(ayyameQabliyyaIkhtilaf = true)
        ))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(5.getMilliDays(), 28.getMilliDays()),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 3, 4), TypesOfFutureDates.BEFORE_THREE_DAYS),
                    FutureDateType(makeInstant(2022, 3, 6), TypesOfFutureDates.IC_FORBIDDEN_DATE),
                    FutureDateType(makeInstant(2022, 3, 11), TypesOfFutureDates.AFTER_TEN_DAYS),
                    FutureDateType(makeInstant(2022, 3, 6), TypesOfFutureDates.IHTIYATI_GHUSL),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingFinalDatesCase9part4() {
        //ihtiyati ghusl dam less than 3 - B-2
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 3, 3))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            5.getMilliDays(),
            17.getMilliDays(), 18.getMilliDays(),false)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(5.getMilliDays(), 17.getMilliDays()),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 3, 4), TypesOfFutureDates.BEFORE_THREE_DAYS),
                    FutureDateType(makeInstant(2022, 3, 6), TypesOfFutureDates.IC_FORBIDDEN_DATE),
                    FutureDateType(makeInstant(2022, 3, 11), TypesOfFutureDates.AFTER_TEN_DAYS),
                    FutureDateType(makeInstant(2022, 3, 5), TypesOfFutureDates.IHTIYATI_GHUSL),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingFinalDatesCase9part5() {
        //ihtiyati ghusl dam less than 3 - B-3
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 3, 3))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            5.getMilliDays(),
            17.getMilliDays(), 30.getMilliDays(),false),
        ))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(5.getMilliDays(), 17.getMilliDays()),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 3, 4), TypesOfFutureDates.BEFORE_THREE_DAYS),
                    FutureDateType(makeInstant(2022, 3, 6), TypesOfFutureDates.IC_FORBIDDEN_DATE),
                    FutureDateType(makeInstant(2022, 3, 11), TypesOfFutureDates.AFTER_TEN_DAYS),
                    FutureDateType(makeInstant(2022, 3, 6), TypesOfFutureDates.IHTIYATI_GHUSL),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingFinalDatesCase10() {
        //ihtiyati ghusl dam less than 3 - B-3
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 3, 5))

        val output = handleEntries(AllTheInputs(
            entries, PreMaslaValues(
            5.getMilliDays(),
            17.getMilliDays(), 30.getMilliDays(),false),
        ))
        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(4.getMilliDays(), 30.getMilliDays()),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 3, 6), TypesOfFutureDates.IC_FORBIDDEN_DATE),
                    FutureDateType(makeInstant(2022, 3, 11), TypesOfFutureDates.AFTER_TEN_DAYS),
                    FutureDateType(makeInstant(2022, 3, 6), TypesOfFutureDates.IHTIYATI_GHUSL),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun bugMaslaDescribedInIssue67() {
        //A-3 changing to A-1
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 2, 23), makeInstant(2022, 2, 28))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            9.getMilliDays(),
            21.getMilliDays(), 23.getMilliDays(),true)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(5.getMilliDays(), 21.getMilliDays()),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 3, 4), TypesOfFutureDates.IC_FORBIDDEN_DATE),
                    FutureDateType(makeInstant(2022, 3, 5), TypesOfFutureDates.AFTER_TEN_DAYS),
                    FutureDateType(makeInstant(2022, 3, 2), TypesOfFutureDates.IHTIYATI_GHUSL),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun bugMaslaDescribedInIssue103() {
        //pregnancy
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2021, 5, 4), makeInstant(2021, 5, 12))
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2021, 6, 2), makeInstant(2021, 6, 10))
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 5), makeInstant(2022, 4, 23))

        val output = handleEntries(AllTheInputs(
            entries,
            typeOfMasla = TypesOfMasla.NIFAS,
            pregnancy = Pregnancy(makeInstant(2021, 6, 10), makeInstant(2022, 3, 5),
                40.getMilliDays(),
                mustabeenUlKhilqat = true)
        ))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(8.getMilliDays(), 21.getMilliDays()),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 5, 5), TypesOfFutureDates.END_OF_AADAT_TUHR),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun bugMaslaDescribedInIssue116() {
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2021, 12, 10), makeInstant(2021, 12, 16))
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 1, 9), makeInstant(2022, 1, 16))
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 2, 10), makeInstant(2022, 3, 15))

        val output = handleEntries(AllTheInputs(
            entries))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(6.getMilliDays(), 25.getMilliDays()),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 3, 16), TypesOfFutureDates.BEFORE_THREE_DAYS),
                    FutureDateType(makeInstant(2022, 3, 19), TypesOfFutureDates.END_OF_AADAT_HAIZ),
                    FutureDateType(makeInstant(2022, 3, 19), TypesOfFutureDates.IHTIYATI_GHUSL),
                    FutureDateType(makeInstant(2022, 3, 19), TypesOfFutureDates.IC_FORBIDDEN_DATE),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun bugMaslaOccured17March2022() {
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 3, 12))
        val output = handleEntries(AllTheInputs(entries,PreMaslaValues(
            6.getMilliDays(),
            20.getMilliDays(), 15.getMilliDays(),false)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(6.getMilliDays(), 20.getMilliDays()),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 4, 1), TypesOfFutureDates.END_OF_AADAT_TUHR),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun parseDaysTest(){
        val str = "17:17:30"
        val expectedOutput:Long = 1531800000
        assertEquals(expectedOutput, parseDays(str))
    }
    @Test
    fun parseDaysTest1(){
        val str = "17:17"
        val expectedOutput:Long = 1530000000
        assertEquals(expectedOutput, parseDays(str))
    }
    @Test
    fun parseDaysTest2(){
        val str = "17"
        val expectedOutput:Long = 1468800000
        assertEquals(expectedOutput, parseDays(str))
    }
    @Test
    fun parseDaysTest3(){
        val str = "17:17:30:9"
        val expectedOutput: Long = 1531800000
        assertEquals(expectedOutput, parseDays(str))
    }

    @Test
    fun bugMaslaDescribedInIssue130() {
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 4, 13, 0), makeInstant(2022, 3, 17, 6, 45))
        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            parseDays("7:20:30"),
            parseDays("17:17:30"), parseDays("37:5:30"),true)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(parseDays("7:20:30")!!, parseDays("17:17:30")!!),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 3, 30, 3, 0), TypesOfFutureDates.END_OF_AADAT_TUHR),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }


    @Test
    fun calculateEndTime(){
        val fixedDuration1=
            FixedDuration(type=DurationType.DAM,
                timeInMilliseconds=5251800000,
                indices= mutableListOf(10, 11, 12, 13, 14, 15, 16),
                istihazaAfter=0,
                biggerThanTen=BiggerThanTenDm(
                    mp=4243200000,
                    gp=1903140000,
                    dm=5251800000,
                    hz=433740000,
                    qism=Soortain.B_3,
                    istihazaBefore=0,
                    haiz=433740000,
                    istihazaAfter=4818060000,
                    aadatHaiz=433740000,
                    aadatTuhr=1903140000,
                    durationsList= mutableListOf(
                        Duration(type=DurationType.HAIZ,
                            timeInMilliseconds=433740000,
                            startTime=makeInstant(2021, 2, 27, 8, 10)),
                        Duration(type=DurationType.ISTIHAZA_AFTER,
                            timeInMilliseconds=1903140000,
                            startTime=makeInstant(2021, 3, 4, 8, 39)),
                        Duration(type=DurationType.HAIZ,
                            timeInMilliseconds=433740000,
                            startTime=makeInstant(2021, 3, 26, 9, 18)),
                        Duration(type=DurationType.ISTIHAZA_AFTER,
                            timeInMilliseconds=1903140000,
                            startTime=makeInstant(2021, 3, 31, 9, 47)),
                        Duration(type=DurationType.HAIZ,
                            timeInMilliseconds=433740000,
                            startTime= makeInstant(2021, 4, 22, 10, 26)),
                        Duration(type=DurationType.ISTIHAZA_AFTER,
                            timeInMilliseconds=144300000,
                            startTime= makeInstant(2021, 9, 27, 10, 55)))),
                biggerThanForty=null,
                startDate= makeInstant(2021, 2, 27, 8, 10))

        val endtime = fixedDuration1.endDate
        val expectedentime = makeInstant(2021, 4, 29, 3, 0)
        assertEquals(endtime.getMillisLong(), expectedentime.getMillisLong())
    }
    @Test
    fun testingMubtadiaFinalOutputsCase1() {
        //dam less than 3, no aadat
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 3, 2))

        val output = handleEntries(AllTheInputs(
            entries, typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(-1, -1),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 3, 11), TypesOfFutureDates.END_OF_AADAT_HAIZ)
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase2() {
        //dam more than 3, no aadat
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 3, 5))

        val output = handleEntries(AllTheInputs(
            entries, typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(4.getMilliDays(), -1),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 3, 11), TypesOfFutureDates.END_OF_AADAT_HAIZ)
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testBugMaslaIssue134() {
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2021, 10, 14, 15, 20), makeInstant(2021, 12, 15, 6, 0))
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2021, 12, 30, 15, 20), makeInstant(2022, 3, 28, 0, 27))

        val output = handleEntries(
            AllTheInputs(
            entries,PreMaslaValues(
            parseDays("6:16:40"),
            parseDays("27:6:20"),
            parseDays("27:6:20"),
            false),
            typeOfMasla = TypesOfMasla.NIFAS,
            pregnancy = Pregnancy(makeInstant(2021, 12, 12, 0, 0), makeInstant(2021, 12, 30, 0, 0),
                40.getMilliDays(), mustabeenUlKhilqat = false)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(parseDays("6:16:40")!!, parseDays("27:6:20")!!),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 4, 11, 12, 20), TypesOfFutureDates.END_OF_AADAT_TUHR)
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testBugMaslaIssue136() {
        //A-2 Masla
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2021, 9, 15), makeInstant(2021, 10, 24))
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2021, 11, 8), makeInstant(2021, 11, 24))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            parseDays("6"),
            parseDays("27")),
            TypesOfMasla.NIFAS,
            pregnancy = Pregnancy(
                makeInstant(2021, 4, 15), makeInstant(2021, 9, 15),
                null, mustabeenUlKhilqat = true
            ))
        )

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(parseDays("4")!!, parseDays("27")!!),
                mutableListOf(
                    FutureDateType(makeInstant(2021, 11, 26), TypesOfFutureDates.END_OF_AADAT_HAIZ),
                    FutureDateType(makeInstant(2021, 11, 26), TypesOfFutureDates.IC_FORBIDDEN_DATE),
                    FutureDateType(makeInstant(2021, 11, 26), TypesOfFutureDates.IHTIYATI_GHUSL)

                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)

        for (i in output.endingOutputValues.futureDateType.indices) {
            assertEquals(
                expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),
                output.endingOutputValues.futureDateType[i].date.getMillisLong()
            )
            assertEquals(
                expectedEndingOutputValues.futureDateType[i].futureDates,
                output.endingOutputValues.futureDateType[i].futureDates
            )
        }
    }
    @Test
    fun testBugMaslaIssue138a() {
        //AyyameQabliyya
        val entries = mutableListOf(
//each month has to be one minus the real
            Entry(makeInstant(2022, 1, 13), makeInstant(2022, 1, 19)),
            Entry(makeInstant(2022, 2, 22), makeInstant(2022, 2, 27)),
            Entry(makeInstant(2022, 3, 17), makeInstant(2022, 3, 31)),

            )

        val output = handleEntries(AllTheInputs(entries))

        val expectedEndingOutputValues =
            EndingOutputValues(
                null,
                AadatsOfHaizAndTuhr(parseDays("5")!!, parseDays("34")!!),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 4, 2), TypesOfFutureDates.START_OF_AADAT_AYYAMEQABLIYYA),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)

        for (i in output.endingOutputValues.futureDateType.indices) {
            assertEquals(
                expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),
                output.endingOutputValues.futureDateType[i].date.getMillisLong()
            )
            assertEquals(
                expectedEndingOutputValues.futureDateType[i].futureDates,
                output.endingOutputValues.futureDateType[i].futureDates
            )
        }
    }
    @Test
    fun testBugMaslaIssue138b() {
        //AyyameQabliyya turned
        val entries = mutableListOf(
//each month has to be one minus the real
            Entry(makeInstant(2022, 1, 13), makeInstant(2022, 1, 19)),
            Entry(makeInstant(2022, 2, 22), makeInstant(2022, 2, 27)),
            Entry(makeInstant(2022, 3, 17), makeInstant(2022, 3, 31)),

            )

        val output = handleEntries(AllTheInputs(
            entries, ikhtilaafaat = Ikhtilaafaat( ayyameQabliyyaIkhtilaf = true))
        )

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(parseDays("5")!!, parseDays("34")!!),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 4, 2), TypesOfFutureDates.A3_CHANGING_TO_A2),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)

        for (i in output.endingOutputValues.futureDateType.indices) {
            assertEquals(
                expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),
                output.endingOutputValues.futureDateType[i].date.getMillisLong()
            )
            assertEquals(
                expectedEndingOutputValues.futureDateType[i].futureDates,
                output.endingOutputValues.futureDateType[i].futureDates
            )
        }
    }
    @Test
    fun testBugMaslaIssue147() {
        //missing ihtiyati ghusl
        val entries = mutableListOf(
//each month has to be one minus the real
            Entry(makeInstant(2021, 11, 8), makeInstant(2021, 11, 13)),
            Entry(makeInstant(2021, 11, 30), makeInstant(2021, 12, 8)),
            Entry(makeInstant(2021, 12, 28), makeInstant(2022, 1, 2)),
            Entry(makeInstant(2022, 1, 16), makeInstant(2022, 1, 25)),
            Entry(makeInstant(2022, 2, 11), makeInstant(2022, 2, 21)),
            Entry(makeInstant(2022, 3, 10), makeInstant(2022, 3, 22)),
            Entry(makeInstant(2022, 4, 8), makeInstant(2022, 4, 8)),

            )

        val output = handleEntries(AllTheInputs(
            entries)
        )

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(parseDays("10")!!, parseDays("17")!!),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 4, 11), TypesOfFutureDates.BEFORE_THREE_DAYS),
                    FutureDateType(makeInstant(2022, 4, 18), TypesOfFutureDates.IC_FORBIDDEN_DATE),
                    FutureDateType(makeInstant(2022, 4, 18), TypesOfFutureDates.AFTER_TEN_DAYS),
                    FutureDateType(makeInstant(2022, 4, 16), TypesOfFutureDates.IHTIYATI_GHUSL),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)

        for (i in output.endingOutputValues.futureDateType.indices) {
            assertEquals(
                expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),
                output.endingOutputValues.futureDateType[i].date.getMillisLong()
            )
            assertEquals(
                expectedEndingOutputValues.futureDateType[i].futureDates,
                output.endingOutputValues.futureDateType[i].futureDates
            )
        }
    }

    @Test
    fun testingMubtadiaFinalOutputsCase3() {
        //dam 10, no aadat
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 3, 11))

        val output = handleEntries(AllTheInputs(
            entries,typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                null,
                AadatsOfHaizAndTuhr(10.getMilliDays(), -1),
                mutableListOf(
                    FutureDateType(Instant.EPOCH, TypesOfFutureDates.TEN_DAYS_EXACTLY)
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase4a() {
        //dam >10, no aadat
        val entries = listOf(
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 3, 12))
        )

        val output = handleEntries(AllTheInputs(
            entries, typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(-1L, -1L),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 3, 31), TypesOfFutureDates.END_OF_AADAT_TUHR)
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)

        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase4b() {//ikhtilaf
        //dam >10, no aadat
        val entries = listOf(
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 3, 12))
        )

        val output = handleEntries(AllTheInputs(
            entries,
            typeOfMasla = TypesOfMasla.MUBTADIA,
            ikhtilaafaat = Ikhtilaafaat(mubtadiaIkhitilaf = true))
        )

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(parseDays("10")!!, parseDays("20")!!),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 3, 31), TypesOfFutureDates.END_OF_AADAT_TUHR)
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase5a() {
        //dam >10, no aadat ends at end of istehaza, start of daur
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 3, 31))

        val output = handleEntries(AllTheInputs(
            entries, typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(-1L, -1L),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 4, 3), TypesOfFutureDates.BEFORE_THREE_DAYS),
                    FutureDateType(makeInstant(2022, 4, 10), TypesOfFutureDates.END_OF_AADAT_HAIZ),
                    FutureDateType(makeInstant(2022, 4, 10), TypesOfFutureDates.IHTIYATI_GHUSL),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase5b() {//ikhtilaf
        //dam >10, no aadat ends at end of istehaza, start of daur
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 3, 31))

        val output = handleEntries(AllTheInputs(
            entries, typeOfMasla = TypesOfMasla.MUBTADIA, ikhtilaafaat = Ikhtilaafaat(mubtadiaIkhitilaf = true)
        ))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(parseDays("10")!!, parseDays("20")!!),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 4, 3), TypesOfFutureDates.BEFORE_THREE_DAYS),
                    FutureDateType(makeInstant(2022, 4, 10), TypesOfFutureDates.END_OF_AADAT_HAIZ),
                    FutureDateType(makeInstant(2022, 4, 10), TypesOfFutureDates.IHTIYATI_GHUSL),
//                    FutureDateType(makeInstant(2022, 4, 10), TypesOfFutureDates.IC_FORBIDDEN_DATE),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase6a() {
        //dam >10, no aadat ends at start of haiz less than 3, start of daur
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 4, 1))

        val output = handleEntries(AllTheInputs(
            entries,typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(-1L, -1L),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 4, 3), TypesOfFutureDates.BEFORE_THREE_DAYS),
                    FutureDateType(makeInstant(2022, 4, 10), TypesOfFutureDates.END_OF_AADAT_HAIZ),
                    FutureDateType(makeInstant(2022, 4, 10), TypesOfFutureDates.IHTIYATI_GHUSL),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase6b() {//ikhtilaf
        //dam >10, no aadat ends at start of haiz less than 3, start of daur
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 4, 1))

        val output = handleEntries( AllTheInputs(
            entries, typeOfMasla = TypesOfMasla.MUBTADIA, ikhtilaafaat = Ikhtilaafaat(mubtadiaIkhitilaf = true)
        ))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(parseDays("10")!!,parseDays("20")!!),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 4, 3), TypesOfFutureDates.BEFORE_THREE_DAYS),
                    FutureDateType(makeInstant(2022, 4, 10), TypesOfFutureDates.END_OF_AADAT_HAIZ),
                    FutureDateType(makeInstant(2022, 4, 10), TypesOfFutureDates.IHTIYATI_GHUSL),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase7a() {
        //dam >10, no aadat ends at start of haiz bigger than 3, less than 10 start of daur
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 4, 5))

        val output = handleEntries(AllTheInputs(
            entries,typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(parseDays("5")!!, -1L),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 4, 10), TypesOfFutureDates.END_OF_AADAT_HAIZ),
                    FutureDateType(makeInstant(2022, 4, 10), TypesOfFutureDates.IHTIYATI_GHUSL),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase7b() {//ikhtilaf
        //gotta fix this

        //dam >10, no aadat ends at start of haiz bigger than 3, less than 10 start of daur
        val entries = listOf(
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 4, 5))
        )

        val output = handleEntries(AllTheInputs(
            entries, typeOfMasla = TypesOfMasla.MUBTADIA, ikhtilaafaat = Ikhtilaafaat(mubtadiaIkhitilaf = true)
        ))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(parseDays("10")!!, parseDays("20")!!),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 4, 10), TypesOfFutureDates.END_OF_AADAT_HAIZ),
                    FutureDateType(makeInstant(2022, 4, 10), TypesOfFutureDates.IHTIYATI_GHUSL),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase8a() {
        //dam >10, no aadat ends at end of haiz 10  daur
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 4, 10))

        val output = handleEntries(AllTheInputs(
            entries,typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(-1L, -1L),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 4, 30), TypesOfFutureDates.END_OF_AADAT_TUHR),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }

    @Test
    fun testingMubtadiaFinalOutputsCase8b() {//ikhtilaaf
        //dam >10, no aadat ends at end of haiz 10  daur
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 4, 10))

        val output = handleEntries(AllTheInputs(
            entries, typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(-1L, -1L),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 4, 30), TypesOfFutureDates.END_OF_AADAT_TUHR),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase9() {
        //dam <3, aadat
        val entries = mutableListOf(
            Entry(makeInstant(2022, 4, 1), makeInstant(2022, 4, 5)),
            Entry(makeInstant(2022, 4, 22), makeInstant(2022, 4, 22)),
            Entry(makeInstant(2022, 5, 7), makeInstant(2022, 5, 8)),

        )

        val output = handleEntries(AllTheInputs(entries,typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(parseDays("4")!!, -1L),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 5, 10), TypesOfFutureDates.BEFORE_THREE_DAYS),
                    FutureDateType(makeInstant(2022, 5, 17), TypesOfFutureDates.AFTER_TEN_DAYS),
                    FutureDateType(makeInstant(2022, 5, 11), TypesOfFutureDates.IHTIYATI_GHUSL),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase10() {
        //dam >3, <aadat
        val entries = mutableListOf(
            Entry(makeInstant(2022, 4, 1), makeInstant(2022, 4, 5)),
            Entry(makeInstant(2022, 4, 22), makeInstant(2022, 4, 22)),
            Entry(makeInstant(2022, 5, 7), makeInstant(2022, 5, 10)),

            )

        val output = handleEntries(AllTheInputs(entries,typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(parseDays("3")!!, -1L),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 5, 17), TypesOfFutureDates.AFTER_TEN_DAYS),
                    FutureDateType(makeInstant(2022, 5, 11), TypesOfFutureDates.IHTIYATI_GHUSL),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase11() {
        //dam >3, >aadat
        val entries = mutableListOf(
            Entry(makeInstant(2022, 4, 1), makeInstant(2022, 4, 5)),
            Entry(makeInstant(2022, 4, 22), makeInstant(2022, 4, 22)),
            Entry(makeInstant(2022, 5, 7), makeInstant(2022, 5, 11)),

            )

        val output = handleEntries(AllTheInputs(entries,typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(parseDays("4")!!, -1L),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 5, 17), TypesOfFutureDates.AFTER_TEN_DAYS),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase12() {
        //dam >3, >aadat
        val entries = mutableListOf(
            Entry(makeInstant(2022, 4, 1), makeInstant(2022, 4, 5)),
            Entry(makeInstant(2022, 4, 22), makeInstant(2022, 4, 22)),
            Entry(makeInstant(2022, 5, 7), makeInstant(2022, 5, 12)),

            )

        val output = handleEntries(AllTheInputs(entries,typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(parseDays("5")!!, -1L),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 5, 17), TypesOfFutureDates.AFTER_TEN_DAYS),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase13() {
        //dam 10 aadat
        val entries = mutableListOf(
            Entry(makeInstant(2022, 4, 1), makeInstant(2022, 4, 5)),
            Entry(makeInstant(2022, 4, 22), makeInstant(2022, 4, 22)),
            Entry(makeInstant(2022, 5, 7), makeInstant(2022, 5, 17)),

            )

        val output = handleEntries(AllTheInputs(entries,typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                null,
                AadatsOfHaizAndTuhr(parseDays("10")!!, -1L),
                mutableListOf(
                    FutureDateType(Instant.EPOCH, TypesOfFutureDates.TEN_DAYS_EXACTLY),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase14() {
        //dam bigger than 10  aadat
        val entries = mutableListOf(
            Entry(makeInstant(2022, 4, 1), makeInstant(2022, 4, 5)),
            Entry(makeInstant(2022, 4, 22), makeInstant(2022, 4, 22)),
            Entry(makeInstant(2022, 5, 7), makeInstant(2022, 5, 18)),

            )

        val output = handleEntries(AllTheInputs(entries,typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(parseDays("4")!!, -1L),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 5, 7).plusMillis(30.getMilliDays()), TypesOfFutureDates.END_OF_AADAT_TUHR),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase15() {
        //dam bigger than 10  aadat
        val entries = mutableListOf(
            Entry(makeInstant(2022, 4, 1), makeInstant(2022, 4, 5)),
            Entry(makeInstant(2022, 4, 22), makeInstant(2022, 4, 22)),
            Entry(makeInstant(2022, 5, 7), makeInstant(2022, 6, 7)),

            )

        val output = handleEntries(AllTheInputs(entries,typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(parseDays("4")!!, -1L),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 6, 9), TypesOfFutureDates.BEFORE_THREE_DAYS),
                    FutureDateType(makeInstant(2022, 6, 10), TypesOfFutureDates.END_OF_AADAT_HAIZ),
                    FutureDateType(makeInstant(2022, 6, 10), TypesOfFutureDates.IHTIYATI_GHUSL),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase16() {
        //dam bigger than 10  aadat
        val entries = mutableListOf(
            Entry(makeInstant(2022, 4, 1), makeInstant(2022, 4, 5)),
            Entry(makeInstant(2022, 4, 22), makeInstant(2022, 4, 22)),
            Entry(makeInstant(2022, 5, 7), makeInstant(2022, 6, 8)),

            )

        val output = handleEntries(AllTheInputs(entries,typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(parseDays("4")!!, -1L),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 6, 9), TypesOfFutureDates.BEFORE_THREE_DAYS),
                    FutureDateType(makeInstant(2022, 6, 10), TypesOfFutureDates.END_OF_AADAT_HAIZ),
                    FutureDateType(makeInstant(2022, 6, 10), TypesOfFutureDates.IHTIYATI_GHUSL),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase17() {
        //dam bigger than 10  aadat
        val entries = mutableListOf(
            Entry(makeInstant(2022, 4, 1), makeInstant(2022, 4, 5)),
            Entry(makeInstant(2022, 4, 22), makeInstant(2022, 4, 22)),
            Entry(makeInstant(2022, 5, 7), makeInstant(2022, 6, 9)),

            )

        val output = handleEntries(AllTheInputs(entries,typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(parseDays("3")!!, -1L),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 6, 10), TypesOfFutureDates.END_OF_AADAT_HAIZ),
                    FutureDateType(makeInstant(2022, 6, 10), TypesOfFutureDates.IHTIYATI_GHUSL),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase18() {
        //dam bigger than 10  aadat
        val entries = mutableListOf(
            Entry(makeInstant(2022, 4, 1), makeInstant(2022, 4, 5)),
            Entry(makeInstant(2022, 4, 22), makeInstant(2022, 4, 22)),
            Entry(makeInstant(2022, 5, 7), makeInstant(2022, 6, 10)),

            )

        val output = handleEntries(AllTheInputs(entries,typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(parseDays("4")!!, -1L),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 7, 6), TypesOfFutureDates.END_OF_AADAT_TUHR),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase19() {
        //dam bigger than 10  aadat
        val entries = mutableListOf(
            Entry(makeInstant(2022, 4, 1), makeInstant(2022, 4, 5)),
            Entry(makeInstant(2022, 4, 22), makeInstant(2022, 4, 22)),
            Entry(makeInstant(2022, 5, 7), makeInstant(2022, 6, 11)),

            )

        val output = handleEntries(AllTheInputs(entries,typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(parseDays("4")!!, -1L),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 7, 6), TypesOfFutureDates.END_OF_AADAT_TUHR),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaUsingInputtedAadat() {
        //dam bigger than 10  aadat
        val entries = mutableListOf(
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 3, 18)),
            )

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            parseDays("7"),
            null, parseDays("30"),false),
            typeOfMasla = TypesOfMasla.MUBTADIA))

        val hazDatesList = output.hazDatesList
        val expectedHazDatesList = mutableListOf(
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 3, 8))
        )

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(parseDays("7")!!, -1L),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 3, 31), TypesOfFutureDates.END_OF_AADAT_TUHR),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
        for(i in hazDatesList.indices){
            assertEquals(hazDatesList[i].startTime.getMillisLong(), expectedHazDatesList[i].startTime.getMillisLong())
            assertEquals(hazDatesList[i].endTime.getMillisLong(), expectedHazDatesList[i].endTime.getMillisLong())
        }
    }
    @Test
    fun testingMubtadiaUsingInputtedAadat2() {
        //dam bigger than 10  aadat
        val entries = mutableListOf(
            Entry(makeInstant(2022, 3, 1), makeInstant(2022, 3, 18)),
        )

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            parseDays("7"),
            null, parseDays("22"),false),
            typeOfMasla = TypesOfMasla.MUBTADIA))

        val hazDatesList = output.hazDatesList
        val expectedHazDatesList = mutableListOf(
            Entry(makeInstant(2022, 3, 2), makeInstant(2022, 3, 9))
        )

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(parseDays("7")!!, -1L),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 4, 1), TypesOfFutureDates.END_OF_AADAT_TUHR),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
        for(i in hazDatesList.indices){
            assertEquals(hazDatesList[i].startTime.getMillisLong(), expectedHazDatesList[i].startTime.getMillisLong())
            assertEquals(hazDatesList[i].endTime.getMillisLong(), expectedHazDatesList[i].endTime.getMillisLong())
        }
    }
    @Test
    fun testingMubtadiaDurationCase1a() {
        //5 د 13ط 18د 11ط 16د 15ط 11د 18ط استمرار
        val arbitraryTime = Instant.EPOCH
        val durations = listOf(
            Duration(DurationType.DAM, parseDays("5")!!, arbitraryTime),
            Duration(DurationType.TUHR, parseDays("13")!!, arbitraryTime),
            Duration(DurationType.DAM, parseDays("18")!!, arbitraryTime),
            Duration(DurationType.TUHR, parseDays("11")!!, arbitraryTime),
            Duration(DurationType.DAM, parseDays("16")!!, arbitraryTime),
            Duration(DurationType.TUHR, parseDays("15")!!, arbitraryTime),
            Duration(DurationType.DAM, parseDays("11")!!, arbitraryTime),
            Duration(DurationType.TUHR, parseDays("18")!!, arbitraryTime),
            Duration(DurationType.DAM, parseDays("100")!!, arbitraryTime),
        )

        val output = handleEntries( convertDurationsIntoEntries(
            durations,
            AllTheInputs(
                typeOfMasla = TypesOfMasla.MUBTADIA
            )
        ))

        val fixedDurations = output.fixedDurations

        val expectedFixedDurations = listOf(
            FixedDuration(
                DurationType.DAM_MUBTADIA,
                parseDays("63")!!,
                biggerThanTen = BiggerThanTenDm(0L, 0L, 0L, 0L, Soortain.A_1, 0L, 0L, 0L, 0L, 0L,
                    durationsList = mutableListOf(
                        Duration(DurationType.HAIZ,parseDays("10")!!, arbitraryTime),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("20")!!, arbitraryTime),
                        Duration(DurationType.HAIZ,parseDays("10")!!, arbitraryTime),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("20")!!, arbitraryTime),
                        Duration(DurationType.HAIZ,parseDays("3")!!, arbitraryTime),
                    )
                )
            ),
            FixedDuration(
                DurationType.TUHR_MUBTADIA_BECAME_A_MUTADA_NOW,
                parseDays("15")!!,
            ),
            FixedDuration(
                DurationType.DAM,
                parseDays("11")!!,
                biggerThanTen = BiggerThanTenDm(0L, 0L, 0L, 0L, Soortain.A_1, 0L, 0L, 0L, 0L, 0L,
                    durationsList = mutableListOf(
                        Duration(DurationType.HAIZ,parseDays("3")!!, arbitraryTime),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("8")!!, arbitraryTime),
                    )
                )
            ),
            FixedDuration(
                DurationType.TUHREFAASID_WITH_ISTEHAZA,
                parseDays("18")!!,
                istihazaAfter = parseDays("8")!!
            ),
            FixedDuration(
                DurationType.DAM,
                parseDays("100")!!,
                biggerThanTen = BiggerThanTenDm(0L, 0L, 0L, 0L, Soortain.A_1, 0L, 0L, 0L, 0L, 0L,
                    durationsList = mutableListOf(
                        Duration(DurationType.HAIZ,parseDays("3")!!, arbitraryTime),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("15")!!, arbitraryTime),
                        Duration(DurationType.HAIZ,parseDays("3")!!, arbitraryTime),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("15")!!, arbitraryTime),
                        Duration(DurationType.HAIZ,parseDays("3")!!, arbitraryTime),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("15")!!, arbitraryTime),
                        Duration(DurationType.HAIZ,parseDays("3")!!, arbitraryTime),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("15")!!, arbitraryTime),
                        Duration(DurationType.HAIZ,parseDays("3")!!, arbitraryTime),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("15")!!, arbitraryTime),
                        Duration(DurationType.HAIZ,parseDays("3")!!, arbitraryTime),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("7")!!, arbitraryTime),
                    )
                )
            ),

            )
        assertEquals(expectedFixedDurations.size, fixedDurations.size)
        for(i in fixedDurations.indices){
            assertEquals(fixedDurations[i].type, expectedFixedDurations[i].type)
            assertEquals(fixedDurations[i].timeInMilliseconds, expectedFixedDurations[i].timeInMilliseconds)
            assertEquals(fixedDurations[i].istihazaAfter, expectedFixedDurations[i].istihazaAfter)
            if(fixedDurations[i].biggerThanTen!=null){
                assertEquals(fixedDurations[i].biggerThanTen!!.durationsList.size,
                expectedFixedDurations[i].biggerThanTen!!.durationsList.size)
                for(j in fixedDurations[i].biggerThanTen!!.durationsList.indices){
                    assertEquals(fixedDurations[i].biggerThanTen!!.durationsList[j].timeInMilliseconds,
                    expectedFixedDurations[i].biggerThanTen!!.durationsList[j].timeInMilliseconds)

                    assertEquals(fixedDurations[i].biggerThanTen!!.durationsList[j].type,
                        expectedFixedDurations[i].biggerThanTen!!.durationsList[j].type)
                }
            }
        }


        val expectedAadats = AadatsOfHaizAndTuhr(parseDays("3")!!, parseDays("15")!!)
        assertEquals(expectedAadats.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedAadats.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
    }
    @Test
    fun testingMubtadiaDurationCase1b() {
        //5 د 13ط 18د 11ط 16د 15ط 11د 18ط استمرار
        val arbitraryTime = Instant.EPOCH
        val durations = listOf(
            Duration(DurationType.DAM, parseDays("5")!!, arbitraryTime),
            Duration(DurationType.TUHR, parseDays("13")!!, arbitraryTime),
            Duration(DurationType.DAM, parseDays("18")!!, arbitraryTime),
            Duration(DurationType.TUHR, parseDays("11")!!, arbitraryTime),
            Duration(DurationType.DAM, parseDays("16")!!, arbitraryTime),
            Duration(DurationType.TUHR, parseDays("15")!!, arbitraryTime),
            Duration(DurationType.DAM, parseDays("11")!!, arbitraryTime),
            Duration(DurationType.TUHR, parseDays("18")!!, arbitraryTime),
            Duration(DurationType.DAM, parseDays("100")!!, arbitraryTime),
        )

        val output = handleEntries( convertDurationsIntoEntries(
            durations,
            AllTheInputs(
                typeOfMasla = TypesOfMasla.MUBTADIA,
                ikhtilaafaat = Ikhtilaafaat(mubtadiaIkhitilaf = true)
            )
        ))

        val fixedDurations = output.fixedDurations

        val expectedFixedDurations = listOf(
            FixedDuration(
                DurationType.DAM,
                parseDays("63")!!,
                biggerThanTen = BiggerThanTenDm(0L, 0L, 0L, 0L, Soortain.A_1, 0L, 0L, 0L, 0L, 0L,
                    durationsList = mutableListOf(
                        Duration(DurationType.HAIZ,parseDays("10")!!, arbitraryTime),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("20")!!, arbitraryTime),
                        Duration(DurationType.HAIZ,parseDays("10")!!, arbitraryTime),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("20")!!, arbitraryTime),
                        Duration(DurationType.HAIZ,parseDays("3")!!, arbitraryTime),
                    )
                )
            ),
            FixedDuration(
                DurationType.TUHR,
                parseDays("15")!!,
            ),
            FixedDuration(
                DurationType.DAM,
                parseDays("11")!!,
                biggerThanTen = BiggerThanTenDm(0L, 0L, 0L, 0L, Soortain.A_1, 0L, 0L, 0L, 0L, 0L,
                    durationsList = mutableListOf(
                        Duration(DurationType.ISTIHAZA_BEFORE,parseDays("5")!!,arbitraryTime),
                        Duration(DurationType.HAIZ,parseDays("3")!!, arbitraryTime),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("3")!!, arbitraryTime),
                    )
                )
            ),
            FixedDuration(
                DurationType.TUHREFAASID_WITH_ISTEHAZA,
                parseDays("18")!!,
                istihazaAfter = parseDays("3")!!
            ),
            FixedDuration(
                DurationType.DAM,
                parseDays("100")!!,
                biggerThanTen = BiggerThanTenDm(0L, 0L, 0L, 0L, Soortain.A_1, 0L, 0L, 0L, 0L, 0L,
                    durationsList = mutableListOf(
                        Duration(DurationType.HAIZ,parseDays("3")!!, arbitraryTime),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("20")!!, arbitraryTime),
                        Duration(DurationType.HAIZ,parseDays("3")!!, arbitraryTime),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("20")!!, arbitraryTime),
                        Duration(DurationType.HAIZ,parseDays("3")!!, arbitraryTime),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("20")!!, arbitraryTime),
                        Duration(DurationType.HAIZ,parseDays("3")!!, arbitraryTime),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("20")!!, arbitraryTime),
                        Duration(DurationType.HAIZ,parseDays("3")!!, arbitraryTime),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("5")!!, arbitraryTime),
                    )
                )
            ),

            )
        val expectedAadats = AadatsOfHaizAndTuhr(parseDays("3")!!, parseDays("20")!!)

        assertEquals(expectedFixedDurations.size, fixedDurations.size)
        for(i in fixedDurations.indices){
            assertEquals(fixedDurations[i].type, expectedFixedDurations[i].type)
            assertEquals(fixedDurations[i].timeInMilliseconds, expectedFixedDurations[i].timeInMilliseconds)
            assertEquals(fixedDurations[i].istihazaAfter, expectedFixedDurations[i].istihazaAfter)
            if(fixedDurations[i].biggerThanTen!=null){
                assertEquals(fixedDurations[i].biggerThanTen!!.durationsList.size,
                    expectedFixedDurations[i].biggerThanTen!!.durationsList.size)
                for(j in fixedDurations[i].biggerThanTen!!.durationsList.indices){
                    assertEquals(fixedDurations[i].biggerThanTen!!.durationsList[j].timeInMilliseconds,
                        expectedFixedDurations[i].biggerThanTen!!.durationsList[j].timeInMilliseconds)

                    assertEquals(fixedDurations[i].biggerThanTen!!.durationsList[j].type,
                        expectedFixedDurations[i].biggerThanTen!!.durationsList[j].type)
                }
            }
        }


        assertEquals(expectedAadats.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedAadats.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
    }

    @Test
    fun testingMubtadiaDurationCase2a() {
        val arbitraryTime = Instant.EPOCH
        val durations = listOf(
            Duration(DurationType.DAM, parseDays("14")!!, arbitraryTime),
            Duration(DurationType.TUHR, parseDays("21")!!, arbitraryTime),
            Duration(DurationType.DAM, parseDays("100")!!, arbitraryTime),
        )

        val output = handleEntries( convertDurationsIntoEntries(
            durations,
            AllTheInputs(
                typeOfMasla = TypesOfMasla.MUBTADIA,
            )
        ))

        val fixedDurations = output.fixedDurations

        val expectedFixedDurations = listOf(
            FixedDuration(
                DurationType.DAM_MUBTADIA,
                parseDays("14")!!,
                biggerThanTen = BiggerThanTenDm(0L, 0L, 0L, 0L, Soortain.A_1, 0L, 0L, 0L, 0L, 0L,
                    durationsList = mutableListOf(
                        Duration(DurationType.HAIZ,parseDays("10")!!, arbitraryTime),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("4")!!, arbitraryTime),
                    )
                )
            ),
            FixedDuration(
                DurationType.TUHREFAASID_MUBTADIA_WITH_ISTEHAZA,
                parseDays("21")!!,
                istihazaAfter = parseDays("4")!!
            ),
            FixedDuration(
                DurationType.DAM_MUBTADIA,
                parseDays("100")!!,
                biggerThanTen = BiggerThanTenDm(0L, 0L, 0L, 0L, Soortain.A_1, 0L, 0L, 0L, 0L, 0L,
                    durationsList = mutableListOf(
                        Duration(DurationType.HAIZ,parseDays("10")!!, arbitraryTime),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("20")!!, arbitraryTime),
                        Duration(DurationType.HAIZ,parseDays("10")!!, arbitraryTime),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("20")!!, arbitraryTime),
                        Duration(DurationType.HAIZ,parseDays("10")!!, arbitraryTime),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("20")!!, arbitraryTime),
                        Duration(DurationType.HAIZ,parseDays("10")!!, arbitraryTime),
                    )
                )
            ),
            )
        val expectedAadats = AadatsOfHaizAndTuhr(-1,-1)

        assertEquals(expectedFixedDurations.size, fixedDurations.size)
        for(i in fixedDurations.indices){
            assertEquals(fixedDurations[i].type, expectedFixedDurations[i].type)
            assertEquals(fixedDurations[i].timeInMilliseconds, expectedFixedDurations[i].timeInMilliseconds)
            assertEquals(fixedDurations[i].istihazaAfter, expectedFixedDurations[i].istihazaAfter)
            if(fixedDurations[i].biggerThanTen!=null){
                assertEquals(fixedDurations[i].biggerThanTen!!.durationsList.size,
                    expectedFixedDurations[i].biggerThanTen!!.durationsList.size)
                for(j in fixedDurations[i].biggerThanTen!!.durationsList.indices){
                    assertEquals(fixedDurations[i].biggerThanTen!!.durationsList[j].timeInMilliseconds,
                        expectedFixedDurations[i].biggerThanTen!!.durationsList[j].timeInMilliseconds)

                    assertEquals(fixedDurations[i].biggerThanTen!!.durationsList[j].type,
                        expectedFixedDurations[i].biggerThanTen!!.durationsList[j].type)
                }
            }
        }


        assertEquals(expectedAadats.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedAadats.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
    }
    @Test
    fun testingMubtadiaDurationCase2b() {
        val arbitraryTime = Instant.EPOCH
        val durations = listOf(
            Duration(DurationType.DAM, parseDays("14")!!, arbitraryTime),
            Duration(DurationType.TUHR, parseDays("21")!!, arbitraryTime),
            Duration(DurationType.DAM, parseDays("100")!!, arbitraryTime),
        )

        val output = handleEntries( convertDurationsIntoEntries(
            durations,
            AllTheInputs(
                typeOfMasla = TypesOfMasla.MUBTADIA,
                ikhtilaafaat = Ikhtilaafaat(mubtadiaIkhitilaf = true)
            )
        ))

        val fixedDurations = output.fixedDurations

        val expectedFixedDurations = listOf(
            FixedDuration(
                DurationType.DAM,
                parseDays("14")!!,
                biggerThanTen = BiggerThanTenDm(0L, 0L, 0L, 0L, Soortain.A_1, 0L, 0L, 0L, 0L, 0L,
                    durationsList = mutableListOf(
                        Duration(DurationType.HAIZ,parseDays("10")!!, arbitraryTime),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("4")!!, arbitraryTime),
                    )
                )
            ),
            FixedDuration(
                DurationType.TUHREFAASID_WITH_ISTEHAZA,
                parseDays("21")!!,
                istihazaAfter = parseDays("4")!!
            ),
            FixedDuration(
                DurationType.DAM,
                parseDays("100")!!,
                biggerThanTen = BiggerThanTenDm(0L, 0L, 0L, 0L, Soortain.A_1, 0L, 0L, 0L, 0L, 0L,
                    durationsList = mutableListOf(
                        Duration(DurationType.HAIZ,parseDays("5")!!, arbitraryTime),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("20")!!, arbitraryTime),
                        Duration(DurationType.HAIZ,parseDays("5")!!, arbitraryTime),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("20")!!, arbitraryTime),
                        Duration(DurationType.HAIZ,parseDays("5")!!, arbitraryTime),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("20")!!, arbitraryTime),
                        Duration(DurationType.HAIZ,parseDays("5")!!, arbitraryTime),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("20")!!, arbitraryTime),
                    )
                )
            ),
        )
        val expectedAadats = AadatsOfHaizAndTuhr(parseDays("5")!!,parseDays("20")!!)

        assertEquals(expectedFixedDurations.size, fixedDurations.size)
        for(i in fixedDurations.indices){
            assertEquals(fixedDurations[i].type, expectedFixedDurations[i].type)
            assertEquals(fixedDurations[i].timeInMilliseconds, expectedFixedDurations[i].timeInMilliseconds)
            assertEquals(fixedDurations[i].istihazaAfter, expectedFixedDurations[i].istihazaAfter)
            if(fixedDurations[i].biggerThanTen!=null){
                assertEquals(fixedDurations[i].biggerThanTen!!.durationsList.size,
                    expectedFixedDurations[i].biggerThanTen!!.durationsList.size)
                for(j in fixedDurations[i].biggerThanTen!!.durationsList.indices){
                    assertEquals(fixedDurations[i].biggerThanTen!!.durationsList[j].timeInMilliseconds,
                        expectedFixedDurations[i].biggerThanTen!!.durationsList[j].timeInMilliseconds)

                    assertEquals(fixedDurations[i].biggerThanTen!!.durationsList[j].type,
                        expectedFixedDurations[i].biggerThanTen!!.durationsList[j].type)
                }
            }
        }


        assertEquals(expectedAadats.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedAadats.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
    }

    @Test
    fun testingMutadahDurationCase1a() {
        //mashq 11, sawal 10
        val durations = listOf(
            Duration(DurationType.TUHR, parseDays("26")!!),
            Duration(DurationType.DAM, parseDays("13")!!),
            Duration(DurationType.TUHR, parseDays("16")!!),
            Duration(DurationType.DAM, parseDays("7")!!),
            Duration(DurationType.TUHR, parseDays("18")!!),
            Duration(DurationType.DAM, parseDays("100")!!),
        )

        val output = handleEntries( convertDurationsIntoEntries(
            durations,
            AllTheInputs(
                typeOfMasla = TypesOfMasla.MUTADAH,
                preMaslaValues = PreMaslaValues(parseDays("5")!!,
                parseDays("27")!!)
            )
        ))

        val fixedDurations = output.fixedDurations

        val expectedFixedDurations = listOf(
            FixedDuration(
                DurationType.TUHR,
                parseDays("26")!!,
            ),
            FixedDuration(
                DurationType.DAM,
                parseDays("13")!!,
                biggerThanTen = BiggerThanTenDm(0L, 0L, 0L, 0L, Soortain.A_1, 0L, 0L, 0L, 0L, 0L,
                    durationsList = mutableListOf(
                        Duration(DurationType.ISTIHAZA_BEFORE, parseDays("1")!!),
                        Duration(DurationType.HAIZ,parseDays("5")!!),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("7")!!),
                    )
                )
            ),
            FixedDuration(
                DurationType.TUHREFAASID_WITH_ISTEHAZA,
                parseDays("16")!!,
                istihazaAfter = parseDays("7")!!
            ),
            FixedDuration(
                DurationType.DAM,
                parseDays("7")!!,
            ),
            FixedDuration(
                DurationType.TUHR,
                parseDays("18")!!,
            ),
            FixedDuration(
                DurationType.DAM,
                parseDays("100")!!,
                biggerThanTen = BiggerThanTenDm(0L, 0L, 0L, 0L, Soortain.A_1, 0L, 0L, 0L, 0L, 0L,
                    durationsList = mutableListOf(
                        Duration(DurationType.ISTIHAZA_BEFORE, parseDays("9")!!),
                        Duration(DurationType.HAIZ,parseDays("7")!!),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("27")!!),
                        Duration(DurationType.HAIZ,parseDays("7")!!),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("27")!!),
                        Duration(DurationType.HAIZ,parseDays("7")!!),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("16")!!),
                    )
                )
            ),
        )
        val expectedAadats = AadatsOfHaizAndTuhr(parseDays("7")!!,parseDays("27")!!)

        assertEquals(expectedFixedDurations.size, fixedDurations.size)
        for(i in fixedDurations.indices){
            assertEquals(fixedDurations[i].type, expectedFixedDurations[i].type)
            assertEquals(fixedDurations[i].timeInMilliseconds, expectedFixedDurations[i].timeInMilliseconds)
            assertEquals(fixedDurations[i].istihazaAfter, expectedFixedDurations[i].istihazaAfter)
            if(fixedDurations[i].biggerThanTen!=null){
                assertEquals(fixedDurations[i].biggerThanTen!!.durationsList.size,
                    expectedFixedDurations[i].biggerThanTen!!.durationsList.size)
                for(j in fixedDurations[i].biggerThanTen!!.durationsList.indices){
                    assertEquals(fixedDurations[i].biggerThanTen!!.durationsList[j].timeInMilliseconds,
                        expectedFixedDurations[i].biggerThanTen!!.durationsList[j].timeInMilliseconds)

                    assertEquals(fixedDurations[i].biggerThanTen!!.durationsList[j].type,
                        expectedFixedDurations[i].biggerThanTen!!.durationsList[j].type)
                }
            }
        }


        assertEquals(expectedAadats.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedAadats.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
    }

    @Test
    fun testingMutadahDurationCase1b() {
        //mashq 11, sawal 10//testing daur ikhtilaf
        val durations = listOf(
            Duration(DurationType.TUHR, parseDays("26")!!),
            Duration(DurationType.DAM, parseDays("13")!!),
            Duration(DurationType.TUHR, parseDays("16")!!),
            Duration(DurationType.DAM, parseDays("7")!!),
            Duration(DurationType.TUHR, parseDays("18")!!),
            Duration(DurationType.DAM, parseDays("86")!!),
        )

        val output = handleEntries( convertDurationsIntoEntries(
            durations,
            AllTheInputs(
                typeOfMasla = TypesOfMasla.MUTADAH,
                preMaslaValues = PreMaslaValues(parseDays("5")!!,
                    parseDays("27")!!),
                ikhtilaafaat = Ikhtilaafaat(daurHaizIkhtilaf = true)
            )
        ))

        val fixedDurations = output.fixedDurations

        val expectedFixedDurations = listOf(
            FixedDuration(
                DurationType.TUHR,
                parseDays("26")!!,
            ),
            FixedDuration(
                DurationType.DAM,
                parseDays("13")!!,
                biggerThanTen = BiggerThanTenDm(0L, 0L, 0L, 0L, Soortain.A_1, 0L, 0L, 0L, 0L, 0L,
                    durationsList = mutableListOf(
                        Duration(DurationType.ISTIHAZA_BEFORE, parseDays("1")!!),
                        Duration(DurationType.HAIZ,parseDays("5")!!),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("7")!!),
                    )
                )
            ),
            FixedDuration(
                DurationType.TUHREFAASID_WITH_ISTEHAZA,
                parseDays("16")!!,
                istihazaAfter = parseDays("7")!!
            ),
            FixedDuration(
                DurationType.DAM,
                parseDays("7")!!,
            ),
            FixedDuration(
                DurationType.TUHR,
                parseDays("18")!!,
            ),
            FixedDuration(
                DurationType.DAM,
                parseDays("86")!!,
                biggerThanTen = BiggerThanTenDm(0L, 0L, 0L, 0L, Soortain.A_1, 0L, 0L, 0L, 0L, 0L,
                    durationsList = mutableListOf(
                        Duration(DurationType.ISTIHAZA_BEFORE, parseDays("9")!!),
                        Duration(DurationType.HAIZ,parseDays("7")!!),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("27")!!),
                        Duration(DurationType.HAIZ,parseDays("7")!!),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("27")!!),
                        Duration(DurationType.HAIZ,parseDays("9")!!),
                    )
                )
            ),
        )
        val expectedAadats = AadatsOfHaizAndTuhr(parseDays("7")!!,parseDays("27")!!)
        //we put aadat as 7 and not 9, because we don't use last aadat of daur

        assertEquals(expectedFixedDurations.size, fixedDurations.size)
        for(i in fixedDurations.indices){
            assertEquals(fixedDurations[i].type, expectedFixedDurations[i].type)
            assertEquals(fixedDurations[i].timeInMilliseconds, expectedFixedDurations[i].timeInMilliseconds)
            assertEquals(fixedDurations[i].istihazaAfter, expectedFixedDurations[i].istihazaAfter)
            if(fixedDurations[i].biggerThanTen!=null){
                assertEquals(fixedDurations[i].biggerThanTen!!.durationsList.size,
                    expectedFixedDurations[i].biggerThanTen!!.durationsList.size)
                for(j in fixedDurations[i].biggerThanTen!!.durationsList.indices){
                    assertEquals(fixedDurations[i].biggerThanTen!!.durationsList[j].timeInMilliseconds,
                        expectedFixedDurations[i].biggerThanTen!!.durationsList[j].timeInMilliseconds)

                    assertEquals(fixedDurations[i].biggerThanTen!!.durationsList[j].type,
                        expectedFixedDurations[i].biggerThanTen!!.durationsList[j].type)
                }
            }
        }
        assertEquals(expectedAadats.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedAadats.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
    }
    @Test
    fun testingMutadahDurationCase1c() {
        //mashq 11, sawal 10//testing daur ikhtilaf
        val durations = listOf(
            Duration(DurationType.TUHR, parseDays("26")!!),
            Duration(DurationType.DAM, parseDays("13")!!),
            Duration(DurationType.TUHR, parseDays("16")!!),
            Duration(DurationType.DAM, parseDays("7")!!),
            Duration(DurationType.TUHR, parseDays("18")!!),
            Duration(DurationType.DAM, parseDays("86")!!),
            Duration(DurationType.TUHR, parseDays("28")!!),
            Duration(DurationType.DAM, parseDays("11")!!),
        )

        val output = handleEntries( convertDurationsIntoEntries(
            durations,
            AllTheInputs(
                typeOfMasla = TypesOfMasla.MUTADAH,
                preMaslaValues = PreMaslaValues(parseDays("5")!!,
                    parseDays("27")!!),
                ikhtilaafaat = Ikhtilaafaat(daurHaizIkhtilaf = true)
            )
        ))

        val fixedDurations = output.fixedDurations

        val expectedFixedDurations = listOf(
            FixedDuration(
                DurationType.TUHR,
                parseDays("26")!!,
            ),
            FixedDuration(
                DurationType.DAM,
                parseDays("13")!!,
                biggerThanTen = BiggerThanTenDm(0L, 0L, 0L, 0L, Soortain.A_1, 0L, 0L, 0L, 0L, 0L,
                    durationsList = mutableListOf(
                        Duration(DurationType.ISTIHAZA_BEFORE, parseDays("1")!!),
                        Duration(DurationType.HAIZ,parseDays("5")!!),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("7")!!),
                    )
                )
            ),
            FixedDuration(
                DurationType.TUHREFAASID_WITH_ISTEHAZA,
                parseDays("16")!!,
                istihazaAfter = parseDays("7")!!
            ),
            FixedDuration(
                DurationType.DAM,
                parseDays("7")!!,
            ),
            FixedDuration(
                DurationType.TUHR,
                parseDays("18")!!,
            ),
            FixedDuration(
                DurationType.DAM,
                parseDays("86")!!,
                biggerThanTen = BiggerThanTenDm(0L, 0L, 0L, 0L, Soortain.A_1, 0L, 0L, 0L, 0L, 0L,
                    durationsList = mutableListOf(
                        Duration(DurationType.ISTIHAZA_BEFORE, parseDays("9")!!),
                        Duration(DurationType.HAIZ,parseDays("7")!!),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("27")!!),
                        Duration(DurationType.HAIZ,parseDays("7")!!),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("27")!!),
                        Duration(DurationType.HAIZ,parseDays("9")!!),
                    )
                )
            ),
            FixedDuration(
                DurationType.TUHR,
                parseDays("28")!!,
            ),
            FixedDuration(
                DurationType.DAM,
                parseDays("11")!!,
                biggerThanTen = BiggerThanTenDm(0L, 0L, 0L, 0L, Soortain.A_1, 0L, 0L, 0L, 0L, 0L,
                    durationsList = mutableListOf(
                        Duration(DurationType.HAIZ,parseDays("8")!!),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("3")!!),
                    )
                )
            ),

            )
        val expectedAadats = AadatsOfHaizAndTuhr(parseDays("8")!!,parseDays("28")!!)
        //we put aadat as 7 and not 9, because we don't use last aadat of daur

        assertEquals(expectedFixedDurations.size, fixedDurations.size)
        for(i in fixedDurations.indices){
            assertEquals(fixedDurations[i].type, expectedFixedDurations[i].type)
            assertEquals(fixedDurations[i].timeInMilliseconds, expectedFixedDurations[i].timeInMilliseconds)
            assertEquals(fixedDurations[i].istihazaAfter, expectedFixedDurations[i].istihazaAfter)
            if(fixedDurations[i].biggerThanTen!=null){
                assertEquals(fixedDurations[i].biggerThanTen!!.durationsList.size,
                    expectedFixedDurations[i].biggerThanTen!!.durationsList.size)
                for(j in fixedDurations[i].biggerThanTen!!.durationsList.indices){
                    assertEquals(fixedDurations[i].biggerThanTen!!.durationsList[j].timeInMilliseconds,
                        expectedFixedDurations[i].biggerThanTen!!.durationsList[j].timeInMilliseconds)

                    assertEquals(fixedDurations[i].biggerThanTen!!.durationsList[j].type,
                        expectedFixedDurations[i].biggerThanTen!!.durationsList[j].type)
                }
            }
        }
        assertEquals(expectedAadats.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedAadats.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
    }
    @Test
    fun testingNifasDurationCase1() {
        //mashq 12, sawal 1//testing daur ikhtilaf
        val durations = listOf(
            Duration(DurationType.HAML, parseDays("0")!!),
            Duration(DurationType.WILADAT_ISQAT, parseDays("0")!!),
            Duration(DurationType.DAM, parseDays("2")!!),
            Duration(DurationType.TUHR, parseDays("30")!!),
            Duration(DurationType.DAM, parseDays("2")!!),
            Duration(DurationType.TUHR, parseDays("2")!!),
            Duration(DurationType.DAM, parseDays("1")!!),
        )

        val output = handleEntries( convertDurationsIntoEntries(
            durations,
            AllTheInputs(
                typeOfMasla = TypesOfMasla.NIFAS,
                pregnancy = Pregnancy(aadatNifas= 40.getMilliDays(),
                    mustabeenUlKhilqat = true)
            )
        ))

        val fixedDurations = output.fixedDurations

        val expectedFixedDurations = listOf(
            FixedDuration(
                DurationType.HAML,
                parseDays("0")!!,
            ),
            FixedDuration(
                DurationType.WILADAT_ISQAT,
                parseDays("0")!!,
            ),
            FixedDuration(
                DurationType.DAM_IN_NIFAS_PERIOD,
                parseDays("37")!!,
            ),

            )
        val expectedAadats = AadatsOfHaizAndTuhr(-1,-1, parseDays("37")!!)
        assertEquals(expectedFixedDurations.size, fixedDurations.size)
        for(i in fixedDurations.indices){
            assertEquals(fixedDurations[i].type, expectedFixedDurations[i].type)
            assertEquals(fixedDurations[i].timeInMilliseconds, expectedFixedDurations[i].timeInMilliseconds)
            assertEquals(fixedDurations[i].istihazaAfter, expectedFixedDurations[i].istihazaAfter)
            if(fixedDurations[i].biggerThanTen!=null){
                assertEquals(fixedDurations[i].biggerThanTen!!.durationsList.size,
                    expectedFixedDurations[i].biggerThanTen!!.durationsList.size)
                for(j in fixedDurations[i].biggerThanTen!!.durationsList.indices){
                    assertEquals(fixedDurations[i].biggerThanTen!!.durationsList[j].timeInMilliseconds,
                        expectedFixedDurations[i].biggerThanTen!!.durationsList[j].timeInMilliseconds)

                    assertEquals(fixedDurations[i].biggerThanTen!!.durationsList[j].type,
                        expectedFixedDurations[i].biggerThanTen!!.durationsList[j].type)
                }
            }
        }
        assertEquals(expectedAadats.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedAadats.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedAadats.aadatNifas, output.endingOutputValues.aadats!!.aadatNifas)
    }
    @Test
    fun testingNifasDurationCase2() {
        //mashq 13, sawal 1//testing daur ikhtilaf
        val durations = listOf(
            Duration(DurationType.HAML, parseDays("0")!!),
            Duration(DurationType.WILADAT_ISQAT, parseDays("0")!!),
            Duration(DurationType.DAM, parseDays("10")!!),
            Duration(DurationType.TUHR, parseDays("20")!!),
            Duration(DurationType.DAM, parseDays("8")!!),
            Duration(DurationType.TUHR, parseDays("14")!!),
            Duration(DurationType.DAM, parseDays("4")!!),
            Duration(DurationType.DAM, parseDays("100")!!),
        )

        val output = handleEntries( convertDurationsIntoEntries(
            durations,
            AllTheInputs(
                preMaslaValues = PreMaslaValues(parseDays("8")!!, parseDays("27")!!),
                typeOfMasla = TypesOfMasla.NIFAS,
                pregnancy = Pregnancy(aadatNifas =  35.getMilliDays(), mustabeenUlKhilqat = true)
            )
        ))

        val fixedDurations = output.fixedDurations

        val expectedFixedDurations = listOf(
            FixedDuration(
                DurationType.HAML,
                parseDays("0")!!,
            ),
            FixedDuration(
                DurationType.WILADAT_ISQAT,
                parseDays("0")!!,
            ),
            FixedDuration(
                DurationType.DAM_IN_NIFAS_PERIOD,
                parseDays("156")!!,
                biggerThanForty = BiggerThanFortyNifas(
                    35.getMilliDays(),
                    0, 0, 0, 0,
                    mutableListOf(
                        Duration(DurationType.NIFAS, parseDays("35")!!),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("27")!!),
                        Duration(DurationType.HAIZ, parseDays("8")!!),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("27")!!),
                        Duration(DurationType.HAIZ, parseDays("8")!!),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("27")!!),
                        Duration(DurationType.HAIZ, parseDays("8")!!),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("16")!!),

                        )

                )
            ),

            )
        val expectedAadats = AadatsOfHaizAndTuhr(parseDays("8")!!,parseDays("27")!!, parseDays("35")!!)
        assertEquals(expectedFixedDurations.size, fixedDurations.size)
        for(i in fixedDurations.indices){
            assertEquals(fixedDurations[i].type, expectedFixedDurations[i].type)
            assertEquals(fixedDurations[i].timeInMilliseconds, expectedFixedDurations[i].timeInMilliseconds)
            assertEquals(fixedDurations[i].istihazaAfter, expectedFixedDurations[i].istihazaAfter)
            if(fixedDurations[i].biggerThanTen!=null){
                assertEquals(fixedDurations[i].biggerThanTen!!.durationsList.size,
                    expectedFixedDurations[i].biggerThanTen!!.durationsList.size)
                for(j in fixedDurations[i].biggerThanTen!!.durationsList.indices){
                    assertEquals(fixedDurations[i].biggerThanTen!!.durationsList[j].timeInMilliseconds,
                        expectedFixedDurations[i].biggerThanTen!!.durationsList[j].timeInMilliseconds)

                    assertEquals(fixedDurations[i].biggerThanTen!!.durationsList[j].type,
                        expectedFixedDurations[i].biggerThanTen!!.durationsList[j].type)
                }
            }
        }
        assertEquals(expectedAadats.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedAadats.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedAadats.aadatNifas, output.endingOutputValues.aadats!!.aadatNifas)
    }
    @Test
    fun testingNifasDurationCase3() {
        //mashq 15, sawal 1
        val durations = listOf(
            Duration(DurationType.HAML, parseDays("0")!!),
            Duration(DurationType.DAM, parseDays("60")!!),
            Duration(DurationType.TUHR, parseDays("10")!!),
            Duration(DurationType.WILADAT_ISQAT, parseDays("0")!!),
            Duration(DurationType.DAM, parseDays("16")!!),
            Duration(DurationType.TUHR, parseDays("17")!!),
            Duration(DurationType.DAM, parseDays("8")!!),
            Duration(DurationType.TUHR, parseDays("27")!!),
            Duration(DurationType.DAM, parseDays("16")!!),
        )

        val output = handleEntries( convertDurationsIntoEntries(
            durations,
            AllTheInputs(
                preMaslaValues = PreMaslaValues(parseDays("8")!!, parseDays("24")!!),
                typeOfMasla = TypesOfMasla.NIFAS,
                pregnancy = Pregnancy(aadatNifas = 38.getMilliDays(),
                    mustabeenUlKhilqat = true)
            )
        ))

        val fixedDurations = output.fixedDurations

        val expectedFixedDurations = listOf(
            FixedDuration(
                DurationType.HAML,
                parseDays("0")!!,
            ),
            FixedDuration(
                DurationType.DAM_IN_HAML,
                parseDays("60")!!,
            ),
            FixedDuration(
                DurationType.TUHR_IN_HAML,
                parseDays("10")!!,
            ),

            FixedDuration(
                DurationType.WILADAT_ISQAT,
                parseDays("0")!!,
            ),
            FixedDuration(
                DurationType.DAM_IN_NIFAS_PERIOD,
                parseDays("41")!!,
                biggerThanForty = BiggerThanFortyNifas(
                    38.getMilliDays(),
                    0, 0, 0, 0,
                    mutableListOf(
                        Duration(DurationType.NIFAS, parseDays("38")!!),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("3")!!),
                        )
                )
            ),
            FixedDuration(
                DurationType.TUHREFAASID_WITH_ISTEHAZA,
                parseDays("27")!!,
                istihazaAfter = parseDays("3")!!
            ),
            FixedDuration(
                DurationType.DAM,
                parseDays("16")!!,
                biggerThanTen = BiggerThanTenDm(0, 0, 0, 0,Soortain.A_1, 0, 0, 0, 0, 0,
                durationsList = mutableListOf(
                    Duration(DurationType.HAIZ, parseDays("8")!!),
                    Duration(DurationType.ISTIHAZA_AFTER, parseDays("8")!!),
                ))
            ),

            )
        val expectedAadats = AadatsOfHaizAndTuhr(parseDays("8")!!,parseDays("24")!!, parseDays("38")!!)
        assertEquals(expectedFixedDurations.size, fixedDurations.size)
        for(i in fixedDurations.indices){
            assertEquals(fixedDurations[i].type, expectedFixedDurations[i].type)
            assertEquals(fixedDurations[i].timeInMilliseconds, expectedFixedDurations[i].timeInMilliseconds)
            assertEquals(fixedDurations[i].istihazaAfter, expectedFixedDurations[i].istihazaAfter)
            if(fixedDurations[i].biggerThanTen!=null){
                assertEquals(fixedDurations[i].biggerThanTen!!.durationsList.size,
                    expectedFixedDurations[i].biggerThanTen!!.durationsList.size)
                for(j in fixedDurations[i].biggerThanTen!!.durationsList.indices){
                    assertEquals(fixedDurations[i].biggerThanTen!!.durationsList[j].timeInMilliseconds,
                        expectedFixedDurations[i].biggerThanTen!!.durationsList[j].timeInMilliseconds)

                    assertEquals(fixedDurations[i].biggerThanTen!!.durationsList[j].type,
                        expectedFixedDurations[i].biggerThanTen!!.durationsList[j].type)
                }
            }
        }
        assertEquals(expectedAadats.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedAadats.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedAadats.aadatNifas, output.endingOutputValues.aadats!!.aadatNifas)
    }
    @Test
    fun testingIsqatDurationCase1() {
        //mithal 1 pg 53
        val durations = listOf(
            Duration(DurationType.HAML, parseDays("0")!!),
            Duration(DurationType.TUHR, parseDays("60")!!),
            Duration(DurationType.DAM, parseDays("10")!!),
            Duration(DurationType.TUHR, parseDays("15")!!),
            Duration(DurationType.DAM, parseDays("2")!!),
            Duration(DurationType.WILADAT_ISQAT, parseDays("0")!!),
            Duration(DurationType.DAM, parseDays("15")!!),
            Duration(DurationType.TUHR, parseDays("16")!!),
            Duration(DurationType.DAM, parseDays("20")!!),
        )

        val output = handleEntries( convertDurationsIntoEntries(
            durations,
            AllTheInputs(
                preMaslaValues = PreMaslaValues(parseDays("8")!!, parseDays("24")!!),
                typeOfMasla = TypesOfMasla.NIFAS,
                pregnancy = Pregnancy(
                    mustabeenUlKhilqat = false),
                ikhtilaafaat = Ikhtilaafaat(ghairMustabeenIkhtilaaf = false)
            )
        ))

        val fixedDurations = output.fixedDurations

        val expectedFixedDurations = listOf(
            FixedDuration(
                DurationType.HAML,
                parseDays("0")!!,
            ),
            FixedDuration(
                DurationType.TUHR_IN_HAML,
                parseDays("60")!!,
            ),
            FixedDuration(
                DurationType.DAM,
                parseDays("10")!!,
            ),
            FixedDuration(
                DurationType.TUHR_IN_HAML,
                parseDays("15")!!,
            ),
            FixedDuration(
                DurationType.WILADAT_ISQAT,
                parseDays("0")!!,
            ),
            FixedDuration(
                DurationType.DAM,
                parseDays("17")!!,
                biggerThanTen = BiggerThanTenDm(
                    -1,-1,-1,-1,Soortain.A_1,-1,-1,-1,-1,-1,
                    mutableListOf(
                        Duration(DurationType.ISTIHAZA_BEFORE, parseDays("9")!!),
                        Duration(DurationType.HAIZ, parseDays("8")!!)

                    )
                )
            ),
            FixedDuration(
                DurationType.TUHR,
                parseDays("16")!!,
            ),
            FixedDuration(
                DurationType.DAM,
                parseDays("20")!!,
                biggerThanTen = BiggerThanTenDm(
                    -1,-1,-1,-1,Soortain.A_1,-1,-1,-1,-1,-1,
                    mutableListOf(
                        Duration(DurationType.ISTIHAZA_BEFORE, parseDays("8")!!),
                        Duration(DurationType.HAIZ, parseDays("8")!!),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("4")!!),
                    )
                )
            ),

            )
        val expectedAadats = AadatsOfHaizAndTuhr(parseDays("8")!!,parseDays("24")!!, -1)

        assertEquals(expectedFixedDurations.size, fixedDurations.size)
        for(i in fixedDurations.indices){
            assertEquals(fixedDurations[i].type, expectedFixedDurations[i].type)
            assertEquals(fixedDurations[i].timeInMilliseconds, expectedFixedDurations[i].timeInMilliseconds)
            assertEquals(fixedDurations[i].istihazaAfter, expectedFixedDurations[i].istihazaAfter)
            if(fixedDurations[i].biggerThanTen!=null){
                assertEquals(fixedDurations[i].biggerThanTen!!.durationsList.size,
                    expectedFixedDurations[i].biggerThanTen!!.durationsList.size)
                for(j in fixedDurations[i].biggerThanTen!!.durationsList.indices){
                    assertEquals(fixedDurations[i].biggerThanTen!!.durationsList[j].timeInMilliseconds,
                        expectedFixedDurations[i].biggerThanTen!!.durationsList[j].timeInMilliseconds)

                    assertEquals(fixedDurations[i].biggerThanTen!!.durationsList[j].type,
                        expectedFixedDurations[i].biggerThanTen!!.durationsList[j].type)
                }
            }
        }
        assertEquals(expectedAadats.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedAadats.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedAadats.aadatNifas, output.endingOutputValues.aadats!!.aadatNifas)
    }
    @Test
    fun bugMaslaIssue168() {
        //writitng isqat line twice
        val entries = listOf(
            Entry(makeInstant(2022, 2, 13), makeInstant(2022, 2, 21)),
            Entry(makeInstant(2022, 3, 27), makeInstant(2022, 4, 19)),

        )

        val output = handleEntries(
            AllTheInputs(
                entries,
                preMaslaValues = PreMaslaValues(inputtedMawjoodahTuhr = parseDays("31")!!),
                typeOfMasla = TypesOfMasla.NIFAS,
                pregnancy = Pregnancy(makeInstant(2022, 3, 21), makeInstant(2022, 4, 15),
                    mustabeenUlKhilqat = false),
                ikhtilaafaat = Ikhtilaafaat(ghairMustabeenIkhtilaaf = false)
            )
        )

        val fixedDurations = output.fixedDurations

        val expectedFixedDurations = listOf(
            FixedDuration(
                DurationType.DAM,
                parseDays("8")!!,
            ),
            FixedDuration(
                DurationType.HAML,
                parseDays("0")!!,
            ),
            FixedDuration(
                DurationType.TUHR_IN_HAML,
                parseDays("34")!!,
            ),
            FixedDuration(
                DurationType.WILADAT_ISQAT,
                parseDays("0")!!,
            ),
            FixedDuration(
                DurationType.DAM,
                parseDays("23")!!,
                biggerThanTen = BiggerThanTenDm(
                    -1,-1,-1,-1,Soortain.A_1,-1,-1,-1,-1,-1,
                    mutableListOf(
                        Duration(DurationType.HAIZ, parseDays("5")!!),
                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("18")!!)

                    )
                )
            ),
            )
        val expectedAadats = AadatsOfHaizAndTuhr(parseDays("5")!!,parseDays("31")!!, -1)

        assertEquals(expectedFixedDurations.size, fixedDurations.size)
        for(i in fixedDurations.indices){
            assertEquals(fixedDurations[i].type, expectedFixedDurations[i].type)
            assertEquals(fixedDurations[i].timeInMilliseconds, expectedFixedDurations[i].timeInMilliseconds)
            assertEquals(fixedDurations[i].istihazaAfter, expectedFixedDurations[i].istihazaAfter)
            if(fixedDurations[i].biggerThanTen!=null){
                assertEquals(fixedDurations[i].biggerThanTen!!.durationsList.size,
                    expectedFixedDurations[i].biggerThanTen!!.durationsList.size)
                for(j in fixedDurations[i].biggerThanTen!!.durationsList.indices){
                    assertEquals(fixedDurations[i].biggerThanTen!!.durationsList[j].timeInMilliseconds,
                        expectedFixedDurations[i].biggerThanTen!!.durationsList[j].timeInMilliseconds)

                    assertEquals(fixedDurations[i].biggerThanTen!!.durationsList[j].type,
                        expectedFixedDurations[i].biggerThanTen!!.durationsList[j].type)
                }
            }
        }
        assertEquals(expectedAadats.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedAadats.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedAadats.aadatNifas, output.endingOutputValues.aadats!!.aadatNifas)
    }
    @Test
    fun issue207() {
        //making sure that 6 months greater tuhr doesn't cause ayyame qabliyya
        val entries = listOf(
            Entry(makeInstant(2022, 6, 14), makeInstant(2022, 6, 21)),
            Entry(makeInstant(2022, 7, 18), makeInstant(2022, 7, 24)),
            Entry(makeInstant(2023, 1, 29), makeInstant(2023, 2, 8)),
            Entry(makeInstant(2023, 3, 27), makeInstant(2023, 4, 6)),

            )

        val output = handleEntries(
            AllTheInputs(
                entries,
                typeOfMasla = TypesOfMasla.MUTADAH,
            )
        )

        val fixedDurations = output.fixedDurations

        val expectedFixedDurations = listOf(
            FixedDuration(
                DurationType.DAM,
                parseDays("10")!!,
                ayyameqabliyya = null
            ),
        )
        val expectedAadats = AadatsOfHaizAndTuhr(parseDays("10")!!,parseDays("47")!!)
//        println("Actual Tuhr Aadat is ${output.endingOutputValues.aadats!!.aadatTuhr.daysFromMillis()}")

        assertEquals(expectedFixedDurations.last().ayyameqabliyya, fixedDurations.last().ayyameqabliyya)
        assertEquals(expectedAadats.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedAadats.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
    }

    @Test
    fun bugMaslaIssue195() {
        //haidh less than 3 not being created printed because ayyame qabliyya, but after aadat
        val entries = listOf<Entry>(
            Entry(makeInstant(2022, 7, 13, 8, 30), makeInstant(2022, 7, 23, 11, 0)),
        )
        val output = handleEntries(
            AllTheInputs(
                entries,
                typeOfMasla = TypesOfMasla.MUTADAH,
                preMaslaValues = PreMaslaValues(
                    parseDays("8:19"),
                    parseDays("26:6"),
                    parseDays("18:11")
                )
            )
        )
        val expectedFixedDurations = listOf<FixedDuration>(
            FixedDuration(DurationType.DAM, parseDays("10:2:30")!!,
                biggerThanTen = BiggerThanTenDm(0, 0, 0, 0,Soortain.A_3, 0, 0, 0, 0,
                    durationsList= mutableListOf(
                        Duration(DurationType.ISTIHAZA_BEFORE,parseDays("7:19")!!),
                        Duration(DurationType.LESS_THAN_3_HAIZ, parseDays("2:7:30")!!)
                    ),
                    aadatTuhr = 0
                )
            )
        )
        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(parseDays("8:19")!!, parseDays("26:6")!!),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 4, 26), TypesOfFutureDates.IC_FORBIDDEN_DATE),
                    FutureDateType(makeInstant(2022, 4, 30), TypesOfFutureDates.AFTER_TEN_DAYS),
                    FutureDateType(makeInstant(2022, 4, 28), TypesOfFutureDates.IHTIYATI_GHUSL),
                    //it wants the last to be 26, even though this is A-2
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedFixedDurations.size, output.fixedDurations.size)
        assertEquals(expectedFixedDurations[0].biggerThanTen!!.durationsList.size, output.fixedDurations[0].biggerThanTen!!.durationsList.size)
        assertEquals(expectedFixedDurations[0].biggerThanTen!!.durationsList[0].type, output.fixedDurations[0].biggerThanTen!!.durationsList[0].type)
        assertEquals(expectedFixedDurations[0].biggerThanTen!!.durationsList[1].type, output.fixedDurations[0].biggerThanTen!!.durationsList[1].type)
        assertEquals(expectedFixedDurations[0].biggerThanTen!!.durationsList[0].timeInMilliseconds, output.fixedDurations[0].biggerThanTen!!.durationsList[0].timeInMilliseconds)
        assertEquals(expectedFixedDurations[0].biggerThanTen!!.durationsList[1].timeInMilliseconds, output.fixedDurations[0].biggerThanTen!!.durationsList[1].timeInMilliseconds)
    }
    @Test
    fun bugMaslaDurationsNifasIssue166() {
        //inputs : 8D 24T 8D Pregnancy 60D 10T Birth
        val durations = listOf<Duration>(
            Duration(DurationType.DAM,parseDays("8")!!),
            Duration(DurationType.TUHR,parseDays("24")!!),
            Duration(DurationType.DAM,parseDays("8")!!),
            Duration(DurationType.HAML,parseDays("0")!!),
            Duration(DurationType.DAM,parseDays("60")!!),
            Duration(DurationType.TUHR,parseDays("10")!!),
            Duration(DurationType.WILADAT_ISQAT,parseDays("0")!!),

            )
        val output = handleEntries(convertDurationsIntoEntries(durations, AllTheInputs(
            typeOfMasla = TypesOfMasla.NIFAS,
            pregnancy = Pregnancy(aadatNifas = parseDays("40")!!,
                mustabeenUlKhilqat = true)
        )
        ))
        assertEquals(parseDays("8"),output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(parseDays("24"),output.endingOutputValues.aadats!!.aadatTuhr)
    }
    @Test
    fun bugMaslaIssue170() {
        //NIFAS MASLA insufficient final values
        val entries = listOf<Entry>(
            Entry(makeInstant(2021, 5, 21), makeInstant(2021, 5, 27)),
            Entry(makeInstant(2021, 6, 21), makeInstant(2021, 6, 27)),
            //preg +Birth
            Entry(makeInstant(2022, 3, 4), makeInstant(2022, 3, 28)),
            Entry(makeInstant(2022, 4, 20), makeInstant(2022, 4, 25)),
        )
        val output = handleEntries(
            AllTheInputs(
                entries,
                typeOfMasla = TypesOfMasla.NIFAS,
                pregnancy = Pregnancy(makeInstant(2021, 6, 27), makeInstant(2022, 3, 4), aadatNifas = parseDays("40")!!,
                    mustabeenUlKhilqat = true)
            )
        )
        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(parseDays("5")!!, parseDays("23")!!, parseDays("24")!!),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 4, 26), TypesOfFutureDates.IC_FORBIDDEN_DATE),
                    FutureDateType(makeInstant(2022, 4, 30), TypesOfFutureDates.AFTER_TEN_DAYS),
                    FutureDateType(makeInstant(2022, 4, 28), TypesOfFutureDates.IHTIYATI_GHUSL),
                    //it wants the last to be 26, even though this is A-2
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }

    }
    @Test
    fun bugMaslaIssue203() {
        //Wrong tuhr habit
//        15 April - 20 April
//                7 May - 15 May
//                June, july, aug clean
//        2 Sep - 10 Sep
//                18 Oct - 20 Oct
//                9 Nov - 22 Nov
//                3 Dec - 1 day
//                7 Dec - 30 dec on and off.
        val entries = listOf<Entry>(
            Entry(makeInstant(2022, 4, 15), makeInstant(2022, 4, 20)),
            Entry(makeInstant(2022, 5, 7), makeInstant(2022, 5, 15)),
            Entry(makeInstant(2022, 9, 2), makeInstant(2022, 9, 10)),
            Entry(makeInstant(2022, 10, 18), makeInstant(2022, 10, 20)),
            Entry(makeInstant(2022, 11, 9), makeInstant(2022, 11, 22)),
            Entry(makeInstant(2022, 12, 3), makeInstant(2022, 12, 3)),
            Entry(makeInstant(2022, 12, 7), makeInstant(2022, 12, 30)),
        )
        val output = handleEntries(
            AllTheInputs(
                entries,
                typeOfMasla = TypesOfMasla.MUTADAH
            )
        )
        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(parseDays("8")!!, parseDays("110")!!),
                mutableListOf(
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
    }
    @Test
    fun bugMaslaIssue201() {
        //wrong future advice date given
        val entries = listOf<Entry>(
            Entry(makeInstant(2022, 10, 23), makeInstant(2022, 11, 26)),
        )
        val output = handleEntries(
            AllTheInputs(
                entries,
                PreMaslaValues(parseDays("9"),parseDays("54"),parseDays("22")),
                typeOfMasla = TypesOfMasla.MUTADAH,
            )
        )
        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(parseDays("9")!!, parseDays("54")!!),
                mutableListOf(
                    FutureDateType(makeInstant(2022, 11, 27), TypesOfFutureDates.BEFORE_THREE_DAYS_MASLA_WILL_CHANGE),
                    FutureDateType(makeInstant(2022, 12, 3), TypesOfFutureDates.END_OF_AADAT_HAIZ),
                    FutureDateType(makeInstant(2022, 12, 3), TypesOfFutureDates.IC_FORBIDDEN_DATE),
                    FutureDateType(makeInstant(2022, 12, 3), TypesOfFutureDates.IHTIYATI_GHUSL),
                    //it wants the last to be 26, even though this is A-2
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getMillisLong(),output.endingOutputValues.futureDateType[i].date.getMillisLong())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }

    }

    @Test
    fun issue206TestingIsqat() {
        val entries = listOf<Entry>(
            Entry(makeInstant(2022, 11, 28), makeInstant(2022, 12, 4)),
            Entry(makeInstant(2022, 12, 27), makeInstant(2023, 1, 1)),
            Entry(makeInstant(2023, 1, 20), makeInstant(2023, 1, 25)),
            Entry(makeInstant(2023, 3, 22), makeInstant(2023, 4, 9)),
        )
        val output = handleEntries(
            AllTheInputs(
                entries,
                typeOfMasla = TypesOfMasla.NIFAS,
                pregnancy = Pregnancy(makeInstant(2023, 1, 25),makeInstant(2023, 3, 22),mustabeenUlKhilqat = false)
            )
        )
        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(parseDays("5")!!, parseDays("56")!!),
                mutableListOf(
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)

    }
    @Test
    fun testMuftiAhmadMumtazMasla208() {
        val entries = listOf<Entry>(
            Entry(makeInstant(2023, 1, 1), makeInstant(2023, 1, 12)),
        )
        val output = handleEntries(
            AllTheInputs(
                entries,
                typeOfMasla = TypesOfMasla.MUTADAH,
                preMaslaValues = PreMaslaValues(
                    3.getMilliDays(),
                    179.getMilliDays(),
                    15.getMilliDays(),
                    false
                )
            )
        )
        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(parseDays("3")!!, parseDays("179")!!),
                mutableListOf(
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
    }

    @Test
    fun testDaysHoursMinutesDigitalIssue171() {
        //if value is 0
        val inputtedMilliseconds = 0L
        val outputEnglish = daysHoursMinutesDigital(inputtedMilliseconds, TypesOfInputs.DATE_ONLY, Vls.Langs.ENGLISH)
        val expectedOutputEnglish = "0 days"
        assertEquals(outputEnglish, expectedOutputEnglish)
        val outputUrdu = daysHoursMinutesDigital(inputtedMilliseconds, TypesOfInputs.DATE_ONLY, Vls.Langs.URDU)
        val expectedOutputUrdu = "0 دن"
        assertEquals(outputUrdu, expectedOutputUrdu)
    }
    @Test
    fun test1ForDaylightSaving() {
//        This is in America
//        Clocks went back one hour on November 6, 2022,
//        And one hour ahead on March 12, 2023,
//        Since last message, dates of bleeding have been:
//        ♦12 Oct 9:14pm - 21 Oct 1:05pm
//        ♦3 Nov 9am - 13 Nov 8:11pm
//        ♦30 Nov 9:57am - 8 Dec 2:30pm
//        ♦23 Dec 3:40pm - 3 Jan 1:40pm
//        ♦18 Jan 10:05am - 26 Jan 4:15pm
//        ♦9 Feb 2:03pm - 19 Feb 11:31am
//        ♦8 Mar 11:15am - 18 Mar 11:50pm
//        ♦2 Apr 10pm - current
//
//        Previous habit:
//        - 18T 14H 17M
//                - 6B 16H 11M
//                (T= tuhur B= blood H= hours M = minutes)
//        ♦12 Oct 9:14pm - 17 Oct 8:44am
//        (New bleeding habit: 4B 11H 30M)
//        ♦4 Nov 11:01pm - 9 Nov 9:31am (due to dst) clocks went back an hour on November 6
//        ♦30 Nov 9:57am - 8 Dec 2:30pm
//        (New bleeding habit: 8B 4H 33M)
//        ♦27 Dec 4:47am - 4 Jan 9:20am
//        ♦22 Jan 11:37pm - 31 Jan 4:10am
//        🔸Feb: no Haidh since less than 3 days bleeding in makan
//        ♦8 Mar 11:15am - 16 Mar 4:48pm (due to dst) clocks went forward an hour on March 12
//        ♦4 Apr 7:05am - 12 Apr 11:38am (if still bleeding)

        val timzn = "America/Los_Angeles"

        val entries = listOf<Entry>(
            Entry(makeInstant(2022, 10, 12, 21, 14,true, timzn),
                makeInstant(2022, 10, 21, 13, 5,true, timzn)),
            Entry(makeInstant(2022, 11, 3, 9, 0,true, timzn),
                makeInstant(2022, 11, 13, 20, 11,true, timzn)),
            Entry(makeInstant(2022, 11, 30, 9, 57,true, timzn),
                makeInstant(2022, 12, 8, 14, 30,true, timzn)),
            Entry(makeInstant(2022, 12, 23, 15, 40,true, timzn),
                makeInstant(2023, 1, 3, 13, 40,true, timzn)),
            Entry(makeInstant(2023, 1, 18, 10, 5,true, timzn),
                makeInstant(2023, 1, 26, 16, 15,true, timzn)),
            Entry(makeInstant(2023, 2, 9, 14, 3,true, timzn),
                makeInstant(2023, 2, 19, 11, 31,true, timzn)),
            Entry(makeInstant(2023, 3, 8, 11, 15,true, timzn),
                makeInstant(2023, 3, 18, 23, 50,true, timzn)),
            Entry(makeInstant(2023, 4, 2, 22, 0,true, timzn),
                makeInstant(2023, 4, 12, 11, 38,true, timzn)),
        )
        val output = handleEntries(
            AllTheInputs(
                entries,
                typeOfMasla = TypesOfMasla.MUTADAH,
                typeOfInput = TypesOfInputs.DATE_AND_TIME,
                preMaslaValues = PreMaslaValues(parseDays("4:11:30"),parseDays("18:14:17"), parseDays("18:14:17"),false),
                timeZone = timzn

            )
        )
        val localHazDates = localHazDatesList(output.hazDatesList,timzn)
//        println(localHazDates)

        val expectedlocalHazDates = listOf<LocalEntry>(
            LocalEntry(LocalDateTime.of(2022, 10, 12, 21, 14),
                LocalDateTime.of(2022, 10, 17, 8, 44)),
            LocalEntry(LocalDateTime.of(2022, 11, 4, 23, 1),
                LocalDateTime.of(2022, 11, 9, 9, 31)),
            LocalEntry(LocalDateTime.of(2022, 11, 30, 9, 57),
                LocalDateTime.of(2022, 12, 8, 14, 30)),
            LocalEntry(LocalDateTime.of(2022, 12, 27, 4, 47),
                LocalDateTime.of(2023, 1, 4, 9, 20)),
            LocalEntry(LocalDateTime.of(2023, 1, 22, 23, 37),
                LocalDateTime.of(2023, 1, 31, 4, 10)),
            LocalEntry(LocalDateTime.of(2023, 3, 8, 11, 15),
                LocalDateTime.of(2023, 3, 16, 16, 48)),
            LocalEntry(LocalDateTime.of(2023, 4, 4, 7, 5),
                LocalDateTime.of(2023, 4, 12, 11, 38)),
        )
        for(i in localHazDates.indices){
            assertEquals(localHazDates[i].startTime,expectedlocalHazDates[i].startTime)
            assertEquals(localHazDates[i].endTime,expectedlocalHazDates[i].endTime)
        }
        assertEquals(localHazDates.size,expectedlocalHazDates.size)
    }

    @Test
    fun testDurationsMawjoodahPaki() {
        val durations = listOf<Duration>(
            Duration(DurationType.DAM, 0L),
            Duration(DurationType.TUHR, 15.getMilliDays()),
            Duration(DurationType.DAM, 5.getMilliDays())
        )
        val output = handleEntries(convertDurationsIntoEntries(durations))
        val fixedDurations=output.fixedDurations
        val expectedFixedDurations = listOf<FixedDuration>(
            FixedDuration(DurationType.TUHREFAASID, 15.getMilliDays()),
            FixedDuration(DurationType.DAM, 5.getMilliDays())
        )
        assertEquals(expectedFixedDurations.size, fixedDurations.size)
        for(i in fixedDurations.indices){
            assertEquals(fixedDurations[i].type, expectedFixedDurations[i].type)
            assertEquals(fixedDurations[i].timeInMilliseconds, expectedFixedDurations[i].timeInMilliseconds)
            assertEquals(fixedDurations[i].istihazaAfter, expectedFixedDurations[i].istihazaAfter)
            if(fixedDurations[i].biggerThanTen!=null){
                assertEquals(fixedDurations[i].biggerThanTen!!.durationsList.size,
                    expectedFixedDurations[i].biggerThanTen!!.durationsList.size)
                for(j in fixedDurations[i].biggerThanTen!!.durationsList.indices){
                    assertEquals(fixedDurations[i].biggerThanTen!!.durationsList[j].timeInMilliseconds,
                        expectedFixedDurations[i].biggerThanTen!!.durationsList[j].timeInMilliseconds)

                    assertEquals(fixedDurations[i].biggerThanTen!!.durationsList[j].type,
                        expectedFixedDurations[i].biggerThanTen!!.durationsList[j].type)
                }
            }
        }

    }
    @Test
    fun testDurationsMawjoodahPakiCase2() {
        val durations = listOf<Duration>(
            Duration(DurationType.TUHR, 15.getMilliDays()),
            Duration(DurationType.DAM, 0.getMilliDays()),
            Duration(DurationType.TUHR, 15.getMilliDays()),
            Duration(DurationType.DAM, 5.getMilliDays()),
        )
        val output = handleEntries(convertDurationsIntoEntries(durations))
        val fixedDurations=output.fixedDurations
        val expectedFixedDurations = listOf<FixedDuration>(
            FixedDuration(DurationType.TUHREFAASID, 30.getMilliDays()),
            FixedDuration(DurationType.DAM, 5.getMilliDays())
        )
        assertEquals(expectedFixedDurations.size, fixedDurations.size)
        for(i in fixedDurations.indices){
            assertEquals(fixedDurations[i].type, expectedFixedDurations[i].type)
            assertEquals(fixedDurations[i].timeInMilliseconds, expectedFixedDurations[i].timeInMilliseconds)
            assertEquals(fixedDurations[i].istihazaAfter, expectedFixedDurations[i].istihazaAfter)
            if(fixedDurations[i].biggerThanTen!=null){
                assertEquals(fixedDurations[i].biggerThanTen!!.durationsList.size,
                    expectedFixedDurations[i].biggerThanTen!!.durationsList.size)
                for(j in fixedDurations[i].biggerThanTen!!.durationsList.indices){
                    assertEquals(fixedDurations[i].biggerThanTen!!.durationsList[j].timeInMilliseconds,
                        expectedFixedDurations[i].biggerThanTen!!.durationsList[j].timeInMilliseconds)

                    assertEquals(fixedDurations[i].biggerThanTen!!.durationsList[j].type,
                        expectedFixedDurations[i].biggerThanTen!!.durationsList[j].type)
                }
            }
        }

    }
    @Test
    fun testDurationsMawjoodahPakiCase3() {
        val durations = listOf<Duration>(
            Duration(DurationType.TUHR, 15.getMilliDays()),
            Duration(DurationType.DAM, 2.getMilliDays()),
            Duration(DurationType.TUHR, 15.getMilliDays()),
            Duration(DurationType.DAM, 5.getMilliDays()),
        )
        val output = handleEntries(convertDurationsIntoEntries(durations))
        val fixedDurations=output.fixedDurations
        val expectedFixedDurations = listOf<FixedDuration>(
            FixedDuration(DurationType.TUHREFAASID, 32.getMilliDays()),
            FixedDuration(DurationType.DAM, 5.getMilliDays())
        )
        assertEquals(expectedFixedDurations.size, fixedDurations.size)
        for(i in fixedDurations.indices){
            assertEquals(fixedDurations[i].type, expectedFixedDurations[i].type)
            assertEquals(fixedDurations[i].timeInMilliseconds, expectedFixedDurations[i].timeInMilliseconds)
            assertEquals(fixedDurations[i].istihazaAfter, expectedFixedDurations[i].istihazaAfter)
            if(fixedDurations[i].biggerThanTen!=null){
                assertEquals(fixedDurations[i].biggerThanTen!!.durationsList.size,
                    expectedFixedDurations[i].biggerThanTen!!.durationsList.size)
                for(j in fixedDurations[i].biggerThanTen!!.durationsList.indices){
                    assertEquals(fixedDurations[i].biggerThanTen!!.durationsList[j].timeInMilliseconds,
                        expectedFixedDurations[i].biggerThanTen!!.durationsList[j].timeInMilliseconds)

                    assertEquals(fixedDurations[i].biggerThanTen!!.durationsList[j].type,
                        expectedFixedDurations[i].biggerThanTen!!.durationsList[j].type)
                }
            }
        }

    }


//    @Test
//    fun testingBugMaslaIssue161() {
//        //durationgs, requesting habit when habit exists
//        val durations = listOf(
//            Duration(DurationType.DAM, parseDays("8")!!),
//            Duration(DurationType.TUHR, parseDays("24")!!),
//            Duration(DurationType.DAM, parseDays("8")!!),
//            Duration(DurationType.HAML, parseDays("0")!!),
//            Duration(DurationType.DAM, parseDays("60")!!),
//            Duration(DurationType.TUHR, parseDays("10")!!),
//            Duration(DurationType.WILADAT_ISQAT, parseDays("0")!!),
//            Duration(DurationType.DAM, parseDays("16")!!),
//            Duration(DurationType.TUHR, parseDays("17")!!),
//            Duration(DurationType.DAM, parseDays("8")!!),
//            Duration(DurationType.TUHR, parseDays("27")!!),
//            Duration(DurationType.DAM, parseDays("16")!!),
//        )
//
//        val output = handleEntries( convertDurationsIntoEntries(
//            durations,
//            AllTheInputs(
//                typeOfMasla = TypesOfMasla.NIFAS,
//                pregnancy = Pregnancy( aadatNifas = parseDays("40")!!,
//                    mustabeenUlKhilqat = true)
//            )
//        ))
//
//        val fixedDurations = output.fixedDurations
//        println(fixedDurations)
//
//        val expectedFixedDurations = listOf(
//            FixedDuration(
//                DurationType.HAML,
//                parseDays("0")!!,
//            ),
//            FixedDuration(
//                DurationType.TUHR_IN_HAML,
//                parseDays("60")!!,
//            ),
//            FixedDuration(
//                DurationType.DAM,
//                parseDays("10")!!,
//            ),
//            FixedDuration(
//                DurationType.TUHR_IN_HAML,
//                parseDays("15")!!,
//            ),
//            FixedDuration(
//                DurationType.WILADAT_ISQAT,
//                parseDays("0")!!,
//            ),
//            FixedDuration(
//                DurationType.DAM,
//                parseDays("17")!!,
//                biggerThanTen = BiggerThanTenDm(
//                    -1,-1,-1,-1,Soortain.A_1,-1,-1,-1,-1,-1,
//                    mutableListOf(
//                        Duration(DurationType.ISTIHAZA_BEFORE, parseDays("9")!!),
//                        Duration(DurationType.HAIZ, parseDays("8")!!)
//
//                    )
//                )
//            ),
//            FixedDuration(
//                DurationType.TUHR,
//                parseDays("16")!!,
//            ),
//            FixedDuration(
//                DurationType.DAM,
//                parseDays("20")!!,
//                biggerThanTen = BiggerThanTenDm(
//                    -1,-1,-1,-1,Soortain.A_1,-1,-1,-1,-1,-1,
//                    mutableListOf(
//                        Duration(DurationType.ISTIHAZA_BEFORE, parseDays("8")!!),
//                        Duration(DurationType.HAIZ, parseDays("8")!!),
//                        Duration(DurationType.ISTIHAZA_AFTER, parseDays("4")!!),
//                    )
//                )
//            ),
//
//            )
//        val expectedAadats = AadatsOfHaizAndTuhr(parseDays("8")!!,parseDays("24")!!, -1)
//
//        assertEquals(expectedFixedDurations.size, fixedDurations.size)
//        for(i in fixedDurations.indices){
//            assertEquals(fixedDurations[i].type, expectedFixedDurations[i].type)
//            assertEquals(fixedDurations[i].timeInMilliseconds, expectedFixedDurations[i].timeInMilliseconds)
//            assertEquals(fixedDurations[i].istihazaAfter, expectedFixedDurations[i].istihazaAfter)
//            if(fixedDurations[i].biggerThanTen!=null){
//                assertEquals(fixedDurations[i].biggerThanTen!!.durationsList.size,
//                    expectedFixedDurations[i].biggerThanTen!!.durationsList.size)
//                for(j in fixedDurations[i].biggerThanTen!!.durationsList.indices){
//                    assertEquals(fixedDurations[i].biggerThanTen!!.durationsList[j].timeInMilliseconds,
//                        expectedFixedDurations[i].biggerThanTen!!.durationsList[j].timeInMilliseconds)
//
//                    assertEquals(fixedDurations[i].biggerThanTen!!.durationsList[j].type,
//                        expectedFixedDurations[i].biggerThanTen!!.durationsList[j].type)
//                }
//            }
//        }
//        assertEquals(expectedAadats.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
//        assertEquals(expectedAadats.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
//        assertEquals(expectedAadats.aadatNifas, output.endingOutputValues.aadats!!.aadatNifas)
//    }

}