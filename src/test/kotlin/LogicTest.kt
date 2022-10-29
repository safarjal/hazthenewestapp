@file:Suppress("SpellCheckingInspection")

import kotlin.js.Date
import kotlin.test.Test
import kotlin.test.assertEquals

class LogicTest {
//    @Test
//    fun testHandleEntries() {
//        val istimrar = false
//        val entries = listOf(
//            Entry(Date(), Date()),
//            Entry(Date(), Date())
//        )
//        val result = handleEntries(entries, istimrar)
//        assertNotNull(result) // TODO: Replace this with actual test
//    }

    @Test
    fun testRemoveDamLessThan3(){
        val durations = mutableListOf(
            FixedDuration(DurationType.TUHR, timeInMilliseconds= (MILLISECONDS_IN_A_DAY*15)),
            FixedDuration(DurationType.DAM, timeInMilliseconds= (MILLISECONDS_IN_A_DAY*2)),
            FixedDuration(DurationType.TUHR, timeInMilliseconds= (MILLISECONDS_IN_A_DAY*15))
        )
        removeDamLessThan3(durations)
        //expected that the size will be 1
        assertEquals(1,durations.size)
        //expected that the duration will be 32 days.
        assertEquals(DurationType.TUHREFAASID,durations[0].type)
        assertEquals(32*MILLISECONDS_IN_A_DAY, durations[0].timeInMilliseconds)
    }

    @Test
    fun testRemoveTuhrLessThan15(){
        val fixedDurations = mutableListOf(
            FixedDuration(DurationType.DAM, timeInMilliseconds = (MILLISECONDS_IN_A_DAY*2)),
            FixedDuration(DurationType.TUHR, timeInMilliseconds = (MILLISECONDS_IN_A_DAY*2)),
            FixedDuration(DurationType.DAM, timeInMilliseconds = (MILLISECONDS_IN_A_DAY*2))
        )
        removeTuhrLessThan15(fixedDurations)
        assertEquals(1, fixedDurations.size)
        assertEquals(DurationType.DAM, fixedDurations[0].type)
        assertEquals(6.0,fixedDurations[0].days)
    }
    @Test
    fun testFiveSoortain(){
        val mp:Long = 21L*MILLISECONDS_IN_A_DAY
        val gp = 16L*MILLISECONDS_IN_A_DAY
        val dm = 11L*MILLISECONDS_IN_A_DAY
        val hz = 7L*MILLISECONDS_IN_A_DAY
        val output:FiveSoortainOutput = fiveSoortain(mp,gp,dm,hz)
        assertEquals(Soortain.B_3, output.soorat)
        assertEquals(0L, output.istihazaBefore)
        assertEquals(7*MILLISECONDS_IN_A_DAY, output.haiz)
        assertEquals(4*MILLISECONDS_IN_A_DAY, output.istihazaAfter)
        assertEquals(true, output.aadatTuhrChanges)
    }
    @Test
    fun testAddIndicesToFixedDurations(){
        val fixedDurations = mutableListOf(
            FixedDuration(DurationType.DAM, timeInMilliseconds = (MILLISECONDS_IN_A_DAY*2)),
            FixedDuration(DurationType.TUHR, timeInMilliseconds = (MILLISECONDS_IN_A_DAY*2)),
            FixedDuration(DurationType.DAM, timeInMilliseconds = (MILLISECONDS_IN_A_DAY*2))
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
//        firstStartTime = Date(2020,7,31)
//        val fixedDurations = mutableListOf(
//            FixedDuration(DurationType.DAM, timeInMilliseconds = (MILLISECONDS_IN_A_DAY*2).toLong()),
//            FixedDuration(DurationType.TUHR, timeInMilliseconds = (MILLISECONDS_IN_A_DAY*2).toLong()),
//            FixedDuration(DurationType.DAM, timeInMilliseconds = (MILLISECONDS_IN_A_DAY*3).toLong())
//        )
//        assertEquals(Date(2020,7,31),fixedDurations[0].startDate)
//        assertEquals(Date(2020,8,2),fixedDurations[0].startDate)
//        assertEquals(Date(2020,8,5),fixedDurations[0].startDate)
//
//    }

    @Test
    fun realWorldLogicTest(){
        val entries = mutableListOf<Entry>()
        entries+=//14 jun - 20 Jun
            Entry(Date(2020,5,14), Date(2020,5,20))
        entries+=//20 Jul - 27 Jul
            Entry(Date(2020,6,20), Date(2020,6,27))
        entries+=//30 Aug - 1 Oct
            Entry(Date(2020,7,30), Date(2020,9,1))

        val output = handleEntries(AllTheInputs(entries))
        val haizDateList = output.hazDatesList
        val expectedHaizDatesList = mutableListOf<Entry>()
        expectedHaizDatesList += Entry(Date(2020,5,14), Date(2020,5,20))
        expectedHaizDatesList += Entry(Date(2020,6,20), Date(2020,6,27))
        expectedHaizDatesList += Entry(Date(2020,7,30), Date(2020,8,2))

        for(i in haizDateList.indices){
            assertEquals(haizDateList[i].startTime.getTime(), expectedHaizDatesList[i].startTime.getTime())
            assertEquals(haizDateList[i].endTime.getTime(), expectedHaizDatesList[i].endTime.getTime())
        }

    }

    @Test
    fun realWorldLogicTest1(){
        val entries = listOf(
            Entry(Date(2020,3,15), Date(2020,3,21)),
            Entry(Date(2020,4,7), Date(2020,4,14)),
            Entry(Date(2021,5,14), Date(2021,9,6)))

        val output = handleEntries(AllTheInputs(
            entries, typeOfInput = TypesOfInputs.DATE_ONLY, typeOfMasla = TypesOfMasla.NIFAS,
            pregnancy = Pregnancy(
                Date(2020,9,6),
                Date(2021,5,15),
                25*MILLISECONDS_IN_A_DAY,
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
        expectedHaizDatesList += Entry(Date(2020,3,15), Date(2020,3,21))
        expectedHaizDatesList += Entry(Date(2020,4,7), Date(2020,4,14))
        expectedHaizDatesList += Entry(Date(2021,5,15), Date(2021,6,10))
        expectedHaizDatesList += Entry(Date(2021,6,26), Date(2021,7,2))
        expectedHaizDatesList += Entry(Date(2021,7,18), Date(2021,7,25))
        expectedHaizDatesList += Entry(Date(2021,8,10), Date(2021,8,17))
        expectedHaizDatesList += Entry(Date(2021,9,3), Date(2021,9,6))

        for(i in expectedHaizDatesList.indices){
            assertEquals(haizDateList[i].startTime.getTime(), expectedHaizDatesList[i].startTime.getTime())
            assertEquals(haizDateList[i].endTime.getTime(), expectedHaizDatesList[i].endTime.getTime())
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
            Entry(Date(2021,3,23), Date(2021,3,28))
        entries+=
            Entry(Date(2021,4,15), Date(2021,4,21))
        entries+=//30 Aug - 1 Oct
            Entry(Date(2021,6,25), Date(2021,8,14))
        entries+=//30 Aug - 1 Oct
            Entry(Date(2021,8,14), Date(2021,8,21))
        entries+=//30 Aug - 1 Oct
            Entry(Date(2021,9,6), Date(2021,9,6))

        val output = handleEntries(AllTheInputs(
            entries,
            typeOfInput = TypesOfInputs.DATE_ONLY,
            typeOfMasla = TypesOfMasla.NIFAS,
            pregnancy = Pregnancy(Date(2021,4,21),Date(2021,6,25),25*MILLISECONDS_IN_A_DAY,mustabeenUlKhilqat = false)),
        )
        val haizDateList = output.hazDatesList

        val expectedHaizDatesList = mutableListOf<Entry>()
        expectedHaizDatesList += Entry(Date(2021,3,23), Date(2021,3,28))
        expectedHaizDatesList += Entry(Date(2021,4,15), Date(2021,4,21))
        expectedHaizDatesList += Entry(Date(2021,6,25), Date(2021,6,31))
        expectedHaizDatesList += Entry(Date(2021,7,17), Date(2021,7,23))
        expectedHaizDatesList += Entry(Date(2021,8,9), Date(2021,8,15))
        expectedHaizDatesList += Entry(Date(2021,9,6), Date(2021,9,6))
        assertEquals(haizDateList.size, expectedHaizDatesList.size)

        for(i in expectedHaizDatesList.indices){
            assertEquals(haizDateList[i].startTime.getTime(), expectedHaizDatesList[i].startTime.getTime())
            assertEquals(haizDateList[i].endTime.getTime(), expectedHaizDatesList[i].endTime.getTime())
        }

    }
    @Test
    fun mashqiSawal1(){
        val entries = mutableListOf<Entry>()
        entries+=//each month has to be one minus the real
            Entry(Date(2020,11,25), Date(2020,11,30))
        entries+=
            Entry(Date(2021,0,20), Date(2021,0,22))
        entries+=
            Entry(Date(2021,0,25), Date(2021,0,26))
        entries+=
            Entry(Date(2021,1,13), Date(2021,1,20))
        entries+=
            Entry(Date(2021,2,3), Date(2021,2,3))
        entries+=
            Entry(Date(2021,2,6), Date(2021,2,9))

        val output = handleEntries(AllTheInputs(
            entries,typeOfInput = TypesOfInputs.DATE_ONLY))
        val haizDateList = output.hazDatesList

        val expectedHaizDatesList = mutableListOf<Entry>()
        expectedHaizDatesList += Entry(Date(2020,11,25), Date(2020,11,30))
        expectedHaizDatesList += Entry(Date(2021,0,20), Date(2021,0,26))
        expectedHaizDatesList += Entry(Date(2021,1,16), Date(2021,1,22))
        assertEquals(haizDateList.size, expectedHaizDatesList.size)

        for(i in expectedHaizDatesList.indices){
            assertEquals(haizDateList[i].startTime.getTime(), expectedHaizDatesList[i].startTime.getTime())
            assertEquals(haizDateList[i].endTime.getTime(), expectedHaizDatesList[i].endTime.getTime())
        }
        val expectedEndingOutputValues = EndingOutputValues(
            true,
            AadatsOfHaizAndTuhr(6*MILLISECONDS_IN_A_DAY,21*MILLISECONDS_IN_A_DAY),
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
            Entry(Date(2020,11,5), Date(2020,11,14))
        entries+=
            Entry(Date(2021,0,5), Date(2021,0,14))
        entries+=
            Entry(Date(2021,1,7), Date(2021,1,13))
        entries+=
            Entry(Date(2021,1,21), Date(2021,2,11))


        val output = handleEntries(AllTheInputs(
            entries))
        val haizDateList = output.hazDatesList

        val expectedHaizDatesList = mutableListOf<Entry>()
        expectedHaizDatesList += Entry(Date(2020,11,5), Date(2020,11,14))
        expectedHaizDatesList += Entry(Date(2021,0,5), Date(2021,0,14))
        expectedHaizDatesList += Entry(Date(2021,1,7), Date(2021,1,14))
        expectedHaizDatesList += Entry(Date(2021,2,10), Date(2021,2,11))
        assertEquals(haizDateList.size, expectedHaizDatesList.size)

        for(i in expectedHaizDatesList.indices){
            assertEquals(haizDateList[i].startTime.getTime(), expectedHaizDatesList[i].startTime.getTime())
            assertEquals(haizDateList[i].endTime.getTime(), expectedHaizDatesList[i].endTime.getTime())
        }

        val expectedEndingOutputValues = EndingOutputValues(false, AadatsOfHaizAndTuhr(7*MILLISECONDS_IN_A_DAY,24*MILLISECONDS_IN_A_DAY), mutableListOf())
        assertEquals(expectedEndingOutputValues.aadats, output.endingOutputValues.aadats)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
//        assertEquals(expectedEndingOutputValues.futureDateType!!.date.getTime(),output.endingOutputValues.futureDateType!!.date.getTime())
//        assertEquals(expectedEndingOutputValues.futureDateType!!.futureDates,output.endingOutputValues.futureDateType!!.futureDates)
    }
    @Test
    fun mashqiSawal3(){
        val entries = mutableListOf<Entry>()
        entries+=//each month has to be one minus the real
            Entry(Date(2020,3,29), Date(2020,4,6))
        entries+=
            Entry(Date(2020,4,26), Date(2020,4,30))
        entries+=
            Entry(Date(2020,7,2), Date(2020,7,16))


        val output = handleEntries(AllTheInputs(entries))
        val haizDateList = output.hazDatesList

        val expectedHaizDatesList = mutableListOf<Entry>()
        expectedHaizDatesList +=
            Entry(Date(2020,3,29), Date(2020,4,6))
        expectedHaizDatesList +=
            Entry(Date(2020,4,26), Date(2020,4,30))
        expectedHaizDatesList +=
            Entry(Date(2020,7,2), Date(2020,7,6))

        assertEquals(haizDateList.size, expectedHaizDatesList.size)

        for(i in expectedHaizDatesList.indices){
            assertEquals(haizDateList[i].startTime.getTime(), expectedHaizDatesList[i].startTime.getTime())
            assertEquals(haizDateList[i].endTime.getTime(), expectedHaizDatesList[i].endTime.getTime())
        }

        val expectedEndingOutputValues =
            EndingOutputValues(true, AadatsOfHaizAndTuhr(4*MILLISECONDS_IN_A_DAY,64*MILLISECONDS_IN_A_DAY), mutableListOf())
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
            Entry(Date(2020,3,16), Date(2020,3,24))
        entries+=
            Entry(Date(2020,4,23), Date(2020,5,1))
        entries+=
            Entry(Date(2020,7,2), Date(2020,7,17))
        entries+=
            Entry(Date(2020,8,5), Date(2020,8,28))


        val output = handleEntries(AllTheInputs(entries))
        val haizDateList = output.hazDatesList

        val expectedHaizDatesList = mutableListOf<Entry>()
        expectedHaizDatesList +=
            Entry(Date(2020,3,16), Date(2020,3,24))
        expectedHaizDatesList +=
            Entry(Date(2020,4,23), Date(2020,5,1))
        expectedHaizDatesList +=
            Entry(Date(2020,7,2), Date(2020,7,11))
        expectedHaizDatesList +=
            Entry(Date(2020,8,5), Date(2020,8,14))
        assertEquals(haizDateList.size, expectedHaizDatesList.size)

        for(i in expectedHaizDatesList.indices){
            assertEquals(haizDateList[i].startTime.getTime(), expectedHaizDatesList[i].startTime.getTime())
            assertEquals(haizDateList[i].endTime.getTime(), expectedHaizDatesList[i].endTime.getTime())
        }

        val expectedEndingOutputValues =
            EndingOutputValues(true, AadatsOfHaizAndTuhr(9*MILLISECONDS_IN_A_DAY,62*MILLISECONDS_IN_A_DAY),
//                FutureDateType(Date(2020,9,12), TypesOfFutureDates.A3_CHANGING_TO_A2)
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
            Entry(Date(2020, 6, 2), Date(2020, 6, 4))
        entries +=
            Entry(Date(2020, 6, 8), Date(2020, 6, 10))
        entries +=
            Entry(Date(2020, 7, 1), Date(2020, 7, 3))
        entries +=
            Entry(Date(2020, 7, 7), Date(2020, 7, 9))
        entries +=
            Entry(Date(2020, 7, 31), Date(2020, 8, 4))
        entries +=
            Entry(Date(2020, 8, 7), Date(2020, 8, 10))
        entries +=
            Entry(Date(2020, 8, 29), Date(2020, 9, 4))
        entries +=
            Entry(Date(2020, 9, 7), Date(2020, 9, 8))
        entries +=
            Entry(Date(2020, 9, 21), Date(2020, 10, 2))

        val output = handleEntries(AllTheInputs(entries))
        val haizDateList = output.hazDatesList

        val expectedHaizDatesList = mutableListOf<Entry>()
        expectedHaizDatesList +=
            Entry(Date(2020, 6, 2), Date(2020, 6, 10))
        expectedHaizDatesList +=
            Entry(Date(2020, 7, 1), Date(2020, 7, 9))
        expectedHaizDatesList +=
            Entry(Date(2020, 7, 31), Date(2020, 8, 10))
        expectedHaizDatesList +=
            Entry(Date(2020, 9, 2), Date(2020, 9, 12))
        assertEquals(haizDateList.size, expectedHaizDatesList.size)

        for (i in expectedHaizDatesList.indices) {
            assertEquals(haizDateList[i].startTime.getTime(), expectedHaizDatesList[i].startTime.getTime())
            assertEquals(haizDateList[i].endTime.getTime(), expectedHaizDatesList[i].endTime.getTime())
        }

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(10 * MILLISECONDS_IN_A_DAY, 22 * MILLISECONDS_IN_A_DAY),
//                FutureDateType(Date(2020, 10, 3), TypesOfFutureDates.END_OF_AADAT_TUHR)
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
            Entry(Date(2020, 1, 27), Date(2020, 2, 3))
        entries +=
            Entry(Date(2020, 2, 25), Date(2020, 2, 31))
        entries +=
            Entry(Date(2020, 3, 21), Date(2020, 3, 26))
        entries +=
            Entry(Date(2021, 1, 14), Date(2021, 3, 14))

        val output = handleEntries(AllTheInputs(
            entries,
            typeOfMasla = TypesOfMasla.NIFAS,
            pregnancy = Pregnancy(Date(2020, 3, 26), Date(2021, 1, 14), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true))
        )
        val haizDateList = output.hazDatesList

        val expectedHaizDatesList = mutableListOf<Entry>()
        expectedHaizDatesList +=
            Entry(Date(2020, 1, 27), Date(2020, 2, 3))
        expectedHaizDatesList +=
            Entry(Date(2020, 2, 25), Date(2020, 2, 31))
        expectedHaizDatesList +=
            Entry(Date(2020, 3, 21), Date(2020, 3, 26))
        expectedHaizDatesList +=
            Entry(Date(2021, 1, 14), Date(2021, 2, 26))
        assertEquals(haizDateList.size, expectedHaizDatesList.size)

        for (i in expectedHaizDatesList.indices) {
            assertEquals(haizDateList[i].startTime.getTime(), expectedHaizDatesList[i].startTime.getTime())
            assertEquals(haizDateList[i].endTime.getTime(), expectedHaizDatesList[i].endTime.getTime())
        }

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(5 * MILLISECONDS_IN_A_DAY, 21 * MILLISECONDS_IN_A_DAY, parseDays("40")!!),
//                FutureDateType(Date(2021, 3, 16), TypesOfFutureDates.END_OF_AADAT_TUHR)
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
            Entry(Date(2021, 0, 19), Date(2021, 0, 26))
        entries +=
            Entry(Date(2021, 1, 15), Date(2021, 1, 20))
        entries +=
            Entry(Date(2021, 2, 27), Date(2021, 3, 3))
        entries +=
            Entry(Date(2021, 3, 12), Date(2021, 3, 12))


        val output = handleEntries(AllTheInputs(entries))
        val haizDateList = output.hazDatesList

        val expectedHaizDatesList = mutableListOf<Entry>()
        expectedHaizDatesList +=
            Entry(Date(2021, 0, 19), Date(2021, 0, 26))
        expectedHaizDatesList +=
            Entry(Date(2021, 1, 15), Date(2021, 1, 20))
        expectedHaizDatesList +=
            Entry(Date(2021, 2, 27), Date(2021, 3, 1))
        assertEquals(haizDateList.size, expectedHaizDatesList.size)

        for (i in expectedHaizDatesList.indices) {
            assertEquals(haizDateList[i].startTime.getTime(), expectedHaizDatesList[i].startTime.getTime())
            assertEquals(haizDateList[i].endTime.getTime(), expectedHaizDatesList[i].endTime.getTime())
        }

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(5 * MILLISECONDS_IN_A_DAY, 35 * MILLISECONDS_IN_A_DAY),
//                FutureDateType(Date(2021, 4, 6), TypesOfFutureDates.END_OF_AADAT_TUHR)
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
            Entry(Date(2020, 10, 24), Date(2020, 10, 30))
        entries +=
            Entry(Date(2020, 11, 16), Date(2020, 11, 22))
        entries +=
            Entry(Date(2021, 0, 10), Date(2021, 0, 18))
        entries +=
            Entry(Date(2021, 1, 1), Date(2021, 1, 10))
        entries +=
            Entry(Date(2021, 1, 23), Date(2021, 2, 3))
        entries +=
            Entry(Date(2021, 2, 22), Date(2021, 2, 28))
        entries +=
            Entry(Date(2021, 3, 10), Date(2021, 3, 23))

        val output = handleEntries(AllTheInputs(entries))
        val haizDateList = output.hazDatesList

        val expectedHaizDatesList = mutableListOf<Entry>()
        expectedHaizDatesList +=
            Entry(Date(2020, 10, 24), Date(2020, 10, 30))
        expectedHaizDatesList +=
            Entry(Date(2020, 11, 16), Date(2020, 11, 22))
        expectedHaizDatesList +=
            Entry(Date(2021, 0, 10), Date(2021, 0, 13))
        expectedHaizDatesList +=
            Entry(Date(2021, 1, 1), Date(2021, 1, 4))
        expectedHaizDatesList +=
            Entry(Date(2021, 1, 23), Date(2021, 1, 26))
        expectedHaizDatesList +=
            Entry(Date(2021, 2, 22), Date(2021, 2, 25))
        expectedHaizDatesList +=
            Entry(Date(2021, 3, 13), Date(2021, 3, 16))

        assertEquals(haizDateList.size, expectedHaizDatesList.size)

        for (i in expectedHaizDatesList.indices) {
            assertEquals(haizDateList[i].startTime.getTime(), expectedHaizDatesList[i].startTime.getTime())
            assertEquals(haizDateList[i].endTime.getTime(), expectedHaizDatesList[i].endTime.getTime())
        }

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(3 * MILLISECONDS_IN_A_DAY, 19 * MILLISECONDS_IN_A_DAY),
//                FutureDateType(Date(2021, 4, 5), TypesOfFutureDates.END_OF_AADAT_TUHR)
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
            Entry(Date(2020, 4, 4), Date(2020, 4, 12))
        entries +=
            Entry(Date(2020, 5, 2), Date(2020, 5, 10))
        entries +=
            Entry(Date(2021, 2, 5), Date(2021, 3, 4))
        entries +=
            Entry(Date(2021, 3, 14), Date(2021, 3, 18))
        entries +=
            Entry(Date(2021, 3, 23), Date(2021, 3, 23))

        val output = handleEntries(AllTheInputs(entries,
            typeOfMasla = TypesOfMasla.NIFAS,
            pregnancy = Pregnancy(Date(2020, 5, 10), Date(2021, 2, 5), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true))
        )
        val haizDateList = output.hazDatesList

        val expectedHaizDatesList = mutableListOf<Entry>()
        expectedHaizDatesList +=
            Entry(Date(2020, 4, 4), Date(2020, 4, 12))
        expectedHaizDatesList +=
            Entry(Date(2020, 5, 2), Date(2020, 5, 10))
        expectedHaizDatesList +=
            Entry(Date(2021, 2, 5), Date(2021, 3, 14))

        assertEquals(haizDateList.size, expectedHaizDatesList.size)

        for (i in expectedHaizDatesList.indices) {
            assertEquals(haizDateList[i].startTime.getTime(), expectedHaizDatesList[i].startTime.getTime())
            assertEquals(haizDateList[i].endTime.getTime(), expectedHaizDatesList[i].endTime.getTime())
        }

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(8 * MILLISECONDS_IN_A_DAY, 21 * MILLISECONDS_IN_A_DAY),
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
            Entry(Date(2020, 10, 27), Date(2020, 11, 7)),
            Entry(Date(2020, 11, 31), Date(2021, 0, 7)),
            Entry(Date(2021, 0, 26), Date(2021, 1, 1)),
            Entry(Date(2021, 1, 11), Date(2021, 1, 23)),
            Entry(Date(2021, 1, 28), Date(2021, 2, 2)),
            Entry(Date(2021, 10, 12), Date(2021, 11, 26)),
            Entry(Date(2021, 11, 30), Date(2022, 0, 8))
        )

        val output = handleEntries(AllTheInputs(entries, ikhtilaafaat = Ikhtilaafaat(daurHaizIkhtilaf = true)))
        val haizDateList = output.hazDatesList

        val expectedHaizDatesList = mutableListOf<Entry>()
        expectedHaizDatesList +=
            Entry(Date(2020, 10, 27), Date(2020, 11, 7))
        expectedHaizDatesList +=
            Entry(Date(2020, 11, 31), Date(2021, 0, 7))
        expectedHaizDatesList +=
            Entry(Date(2021, 0, 31), Date(2021, 1, 7))
        expectedHaizDatesList +=
            Entry(Date(2021, 10, 12), Date(2021, 10, 19))
        expectedHaizDatesList +=
            Entry(Date(2021, 11, 13), Date(2021, 11, 20))

        assertEquals(expectedHaizDatesList.size, haizDateList.size)

        for (i in expectedHaizDatesList.indices) {
            assertEquals(haizDateList[i].startTime.getTime(), expectedHaizDatesList[i].startTime.getTime())
            assertEquals(haizDateList[i].endTime.getTime(), expectedHaizDatesList[i].endTime.getTime())
        }

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(7 * MILLISECONDS_IN_A_DAY, 24 * MILLISECONDS_IN_A_DAY),
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
            Entry(Date(2022, 2, 0), Date(2022, 2, 2))

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
            Entry(Date(2022, 2, 0), Date(2022, 2, 2))

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
                AadatsOfHaizAndTuhr(7*MILLISECONDS_IN_A_DAY, 15*MILLISECONDS_IN_A_DAY),
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
            Entry(Date(2022, 1, 0), Date(2022, 1, 5))
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 0), Date(2022, 2, 2))

        val output = handleEntries(AllTheInputs(entries))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(5*MILLISECONDS_IN_A_DAY, -1),
                mutableListOf()
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
    }
    @Test
    fun testingAadatCase2part2() {
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 0, 1), Date(2022, 0, 5))
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 1, 1), Date(2022, 1, 6))
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 1), Date(2022, 2, 3))

        val output = handleEntries(AllTheInputs(entries))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(5*MILLISECONDS_IN_A_DAY, 27*MILLISECONDS_IN_A_DAY),
                mutableListOf()
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
    }
    @Test
    fun testingAadatCase3() {
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 0, 1), Date(2022, 0, 5))

        val output = handleEntries(AllTheInputs(entries))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(4*MILLISECONDS_IN_A_DAY, -1),
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
            Entry(Date(2022, 0, 1), Date(2022, 0, 5))

        val output = handleEntries(
            AllTheInputs(entries,
                PreMaslaValues(
            8*MILLISECONDS_IN_A_DAY,
            30*MILLISECONDS_IN_A_DAY)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(4*MILLISECONDS_IN_A_DAY, 30*MILLISECONDS_IN_A_DAY),
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
            Entry(Date(2022, 0, 1), Date(2022, 0, 9))
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 1, 1), Date(2022, 1, 6))
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 1, 27), Date(2022, 2, 10))

        val output = handleEntries(
            AllTheInputs(entries,
                PreMaslaValues(
            8*MILLISECONDS_IN_A_DAY,
            30*MILLISECONDS_IN_A_DAY)))
        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(5*MILLISECONDS_IN_A_DAY, 23*MILLISECONDS_IN_A_DAY),
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
            Entry(Date(2022, 0, 1), Date(2022, 0, 9))
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 1, 1), Date(2022, 1, 6))
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 2), Date(2022, 2, 16))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(8*MILLISECONDS_IN_A_DAY,
            30*MILLISECONDS_IN_A_DAY)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(4*MILLISECONDS_IN_A_DAY, 24*MILLISECONDS_IN_A_DAY),
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
            Entry(Date(2022, 1, 21), Date(2022, 2, 4))

        val output = handleEntries(
            AllTheInputs(entries,
                PreMaslaValues(
            5*MILLISECONDS_IN_A_DAY,
            60*MILLISECONDS_IN_A_DAY, 30*MILLISECONDS_IN_A_DAY,false)))
        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(5*MILLISECONDS_IN_A_DAY, 30*MILLISECONDS_IN_A_DAY),
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
            Entry(Date(2022, 1, 21), Date(2022, 2, 26))

        val output = handleEntries(
            AllTheInputs(entries, PreMaslaValues(
            5*MILLISECONDS_IN_A_DAY,
            60*MILLISECONDS_IN_A_DAY, 30*MILLISECONDS_IN_A_DAY,false)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(3*MILLISECONDS_IN_A_DAY, 60*MILLISECONDS_IN_A_DAY),
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
            Entry(Date(2022, 1, 21), Date(2022, 2, 26))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            5*MILLISECONDS_IN_A_DAY,
            30*MILLISECONDS_IN_A_DAY, 60*MILLISECONDS_IN_A_DAY,false)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(5*MILLISECONDS_IN_A_DAY, 60*MILLISECONDS_IN_A_DAY),
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
            Entry(Date(2022, 1, 21), Date(2022, 2, 26))

        val output = handleEntries(AllTheInputs(entries,
            PreMaslaValues(
            5*MILLISECONDS_IN_A_DAY,
            30*MILLISECONDS_IN_A_DAY, 60*MILLISECONDS_IN_A_DAY,true)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(5*MILLISECONDS_IN_A_DAY, 30*MILLISECONDS_IN_A_DAY),
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
            Entry(Date(2022, 1, 1), Date(2022, 2, 1))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            5*MILLISECONDS_IN_A_DAY,
            60*MILLISECONDS_IN_A_DAY, 30*MILLISECONDS_IN_A_DAY,true)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(5*MILLISECONDS_IN_A_DAY, 60*MILLISECONDS_IN_A_DAY),
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
            Entry(Date(2022, 2, 1), Date(2022, 2, 31))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            6*MILLISECONDS_IN_A_DAY,
            15*MILLISECONDS_IN_A_DAY, 17*MILLISECONDS_IN_A_DAY,false)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(4*MILLISECONDS_IN_A_DAY, 17*MILLISECONDS_IN_A_DAY),
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
            Entry(Date(2022, 2, 1), Date(2022, 3, 12))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            6*MILLISECONDS_IN_A_DAY,
            15*MILLISECONDS_IN_A_DAY, 17*MILLISECONDS_IN_A_DAY,false)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(4*MILLISECONDS_IN_A_DAY, 17*MILLISECONDS_IN_A_DAY),
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
            Entry(Date(2022, 2, 1), Date(2022, 3, 19))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            6*MILLISECONDS_IN_A_DAY,
            15*MILLISECONDS_IN_A_DAY, 17*MILLISECONDS_IN_A_DAY,false),
        ))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(4*MILLISECONDS_IN_A_DAY, 17*MILLISECONDS_IN_A_DAY),
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
            Entry(Date(2022, 2, 1), Date(2022, 3, 15))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            6*MILLISECONDS_IN_A_DAY,
            15*MILLISECONDS_IN_A_DAY, 17*MILLISECONDS_IN_A_DAY,false),
        ))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(4*MILLISECONDS_IN_A_DAY, 17*MILLISECONDS_IN_A_DAY),
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
            Entry(Date(2022, 2, 1), Date(2022, 2, 3))

        val output = handleEntries(AllTheInputs(entries))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(-1L, -1L),
                mutableListOf(
                    FutureDateType(Date(2022,2,11), TypesOfFutureDates.AFTER_TEN_DAYS)
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
            Entry(Date(2022, 2, 1), Date(2022, 2, 3))

        val output = handleEntries(AllTheInputs(
            entries,
            PreMaslaValues(
            5*MILLISECONDS_IN_A_DAY,
            15*MILLISECONDS_IN_A_DAY),
        ))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(5*MILLISECONDS_IN_A_DAY, 15*MILLISECONDS_IN_A_DAY),
                mutableListOf(
                    FutureDateType(Date(2022,2,4), TypesOfFutureDates.BEFORE_THREE_DAYS),
                    FutureDateType(Date(2022,2, 6), TypesOfFutureDates.IC_FORBIDDEN_DATE),
                    FutureDateType(Date(2022, 2,11), TypesOfFutureDates.AFTER_TEN_DAYS)
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingFinalDatesCase3() {
        //daur ending in haiz, 3, less than aadat
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 1), Date(2022, 2, 4))

        val output = handleEntries(
            AllTheInputs(
            entries,PreMaslaValues(
            5*MILLISECONDS_IN_A_DAY,
            15*MILLISECONDS_IN_A_DAY)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(3*MILLISECONDS_IN_A_DAY, 15*MILLISECONDS_IN_A_DAY),
                mutableListOf(
                    FutureDateType(Date(2022,2, 6), TypesOfFutureDates.IC_FORBIDDEN_DATE),
                    FutureDateType(Date(2022, 2,11), TypesOfFutureDates.AFTER_TEN_DAYS)
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingFinalDatesCase4() {
        //A-1
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 1), Date(2022, 2, 21))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            7*MILLISECONDS_IN_A_DAY,
            25*MILLISECONDS_IN_A_DAY,
                20*MILLISECONDS_IN_A_DAY,
                false)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(7*MILLISECONDS_IN_A_DAY, 25*MILLISECONDS_IN_A_DAY),
                mutableListOf(
                    FutureDateType(Date(2022,3, 7), TypesOfFutureDates.END_OF_AADAT_TUHR)
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingFinalDatesCase4part2() {
        //A-1 about to end
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 1), Date(2022, 3, 7))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            7*MILLISECONDS_IN_A_DAY,
            25*MILLISECONDS_IN_A_DAY, 20*MILLISECONDS_IN_A_DAY,false)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(7*MILLISECONDS_IN_A_DAY, 25*MILLISECONDS_IN_A_DAY),
                mutableListOf(
                    FutureDateType(Date(2022,3, 10), TypesOfFutureDates.BEFORE_THREE_DAYS),
                    FutureDateType(Date(2022,3, 14), TypesOfFutureDates.END_OF_AADAT_HAIZ),
                    FutureDateType(Date(2022,3, 14), TypesOfFutureDates.IHTIYATI_GHUSL),
                    FutureDateType(Date(2022,3, 14), TypesOfFutureDates.IC_FORBIDDEN_DATE)
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingFinalDatesCase5() {
        //A-3
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 1), Date(2022, 2, 12))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            5*MILLISECONDS_IN_A_DAY,
            60*MILLISECONDS_IN_A_DAY, 30*MILLISECONDS_IN_A_DAY,false)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(5*MILLISECONDS_IN_A_DAY, 30*MILLISECONDS_IN_A_DAY),
                mutableListOf(
                    FutureDateType(Date(2022,2, 31), TypesOfFutureDates.A3_CHANGING_TO_A2)
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingFinalDatesCase5part2() {
        //A-3 - but daur ending in tuhr
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 1), Date(2022, 2, 12))

        val output = handleEntries(AllTheInputs(entries, PreMaslaValues(
            5*MILLISECONDS_IN_A_DAY,
            60*MILLISECONDS_IN_A_DAY, 15*MILLISECONDS_IN_A_DAY,false)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(5*MILLISECONDS_IN_A_DAY, 15*MILLISECONDS_IN_A_DAY),
                mutableListOf(
                    FutureDateType(Date(2022,3, 15), TypesOfFutureDates.A3_CHANGING_TO_A2),
                    FutureDateType(Date(2022,2, 21), TypesOfFutureDates.END_OF_AADAT_TUHR)

                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingFinalDatesCase5part3() {
        //A-3 - but daur ending in haiz
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 1), Date(2022, 2, 21))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            5*MILLISECONDS_IN_A_DAY,
            60*MILLISECONDS_IN_A_DAY, 15*MILLISECONDS_IN_A_DAY,false)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(5*MILLISECONDS_IN_A_DAY, 15*MILLISECONDS_IN_A_DAY),
                mutableListOf(
                    FutureDateType(Date(2022,3, 15), TypesOfFutureDates.A3_CHANGING_TO_A2),
                    FutureDateType(Date(2022,2, 24), TypesOfFutureDates.BEFORE_THREE_DAYS),
                    FutureDateType(Date(2022,2, 26), TypesOfFutureDates.END_OF_AADAT_HAIZ),
                    FutureDateType(Date(2022,2, 26), TypesOfFutureDates.IC_FORBIDDEN_DATE),
                    FutureDateType(Date(2022,2, 26), TypesOfFutureDates.IHTIYATI_GHUSL)

                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingFinalDatesCase6() {
        //B-3
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 1), Date(2022, 2, 21))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            5*MILLISECONDS_IN_A_DAY,
            30*MILLISECONDS_IN_A_DAY, 60*MILLISECONDS_IN_A_DAY,false)
        ))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(5*MILLISECONDS_IN_A_DAY, 60*MILLISECONDS_IN_A_DAY),
                mutableListOf(
                    FutureDateType(Date(2022,4, 5), TypesOfFutureDates.END_OF_AADAT_TUHR),

                    )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingFinalDatesCase7() {
        //B-2
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 1), Date(2022, 2, 21))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            5*MILLISECONDS_IN_A_DAY,
            59*MILLISECONDS_IN_A_DAY, 60*MILLISECONDS_IN_A_DAY,false)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(4*MILLISECONDS_IN_A_DAY, 60*MILLISECONDS_IN_A_DAY),
                mutableListOf(
                    FutureDateType(Date(2022,4, 4), TypesOfFutureDates.END_OF_AADAT_TUHR),

                    )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingFinalDatesCase8() {
        //A-3 shifting to A-2
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 1), Date(2022, 3, 15))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            5*MILLISECONDS_IN_A_DAY,
            60*MILLISECONDS_IN_A_DAY, 15*MILLISECONDS_IN_A_DAY,false)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(5*MILLISECONDS_IN_A_DAY, 60*MILLISECONDS_IN_A_DAY),
                mutableListOf(
                    FutureDateType(Date(2022,3, 18), TypesOfFutureDates.BEFORE_THREE_DAYS_MASLA_WILL_CHANGE),
                    FutureDateType(Date(2022,3, 20), TypesOfFutureDates.END_OF_AADAT_HAIZ),
                    FutureDateType(Date(2022,3, 20), TypesOfFutureDates.IC_FORBIDDEN_DATE),
                    FutureDateType(Date(2022,3, 20), TypesOfFutureDates.IHTIYATI_GHUSL)
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingFinalDatesCase9() {
        //ihtiyati ghusl dam less than 3
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 1), Date(2022, 2, 3))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            5*MILLISECONDS_IN_A_DAY,
            18*MILLISECONDS_IN_A_DAY, 18*MILLISECONDS_IN_A_DAY,false)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(5*MILLISECONDS_IN_A_DAY, 18*MILLISECONDS_IN_A_DAY),
                mutableListOf(
                    FutureDateType(Date(2022,2, 4), TypesOfFutureDates.BEFORE_THREE_DAYS),
                    FutureDateType(Date(2022,2, 6), TypesOfFutureDates.IC_FORBIDDEN_DATE),
                    FutureDateType(Date(2022,2, 11), TypesOfFutureDates.AFTER_TEN_DAYS),
                    FutureDateType(Date(2022,2, 6), TypesOfFutureDates.IHTIYATI_GHUSL),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingFinalDatesCase9part2a() {
        //ayyame qabliyya
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 1), Date(2022, 2, 3))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            5*MILLISECONDS_IN_A_DAY,
            28*MILLISECONDS_IN_A_DAY, 18*MILLISECONDS_IN_A_DAY,false)
        ))
        val expectedEndingOutputValues =
            EndingOutputValues(
                null,
                AadatsOfHaizAndTuhr(5*MILLISECONDS_IN_A_DAY, 28*MILLISECONDS_IN_A_DAY),
                mutableListOf(
                    FutureDateType(Date(2022,2, 11), TypesOfFutureDates.START_OF_AADAT_AYYAMEQABLIYYA),
                    FutureDateType(Date(2022,2, 11), TypesOfFutureDates.BEFORE_TEN_DAYS_AYYAMEQABLIYYAH),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingFinalDatesCase9part2b() {
        //ayyame qabliyya switch on
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 1), Date(2022, 2, 3))

        val output = handleEntries(AllTheInputs(entries,PreMaslaValues(
            5*MILLISECONDS_IN_A_DAY,
            28*MILLISECONDS_IN_A_DAY, 18*MILLISECONDS_IN_A_DAY,false),
            ikhtilaafaat = Ikhtilaafaat(ayyameQabliyyaIkhtilaf = true)
        ))
        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(5*MILLISECONDS_IN_A_DAY, 28*MILLISECONDS_IN_A_DAY),
                mutableListOf(
                    FutureDateType(Date(2022,2, 4), TypesOfFutureDates.BEFORE_THREE_DAYS),
                    FutureDateType(Date(2022,2, 6), TypesOfFutureDates.IC_FORBIDDEN_DATE),
                    FutureDateType(Date(2022,2, 11), TypesOfFutureDates.AFTER_TEN_DAYS),
                    FutureDateType(Date(2022,2, 16), TypesOfFutureDates.IHTIYATI_GHUSL),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }

    @Test
    fun testingFinalDatesCase9part3a() {
        //ihtiyati ghusl dam less than 3 - A-3
        //another ayyame qabliyyah
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 1), Date(2022, 2, 3))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            5*MILLISECONDS_IN_A_DAY,
            28*MILLISECONDS_IN_A_DAY, 17*MILLISECONDS_IN_A_DAY,false)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                null,
                AadatsOfHaizAndTuhr(5*MILLISECONDS_IN_A_DAY, 28*MILLISECONDS_IN_A_DAY),
                mutableListOf(
                    FutureDateType(Date(2022,2, 12), TypesOfFutureDates.START_OF_AADAT_AYYAMEQABLIYYA),
                    FutureDateType(Date(2022,2, 11), TypesOfFutureDates.BEFORE_TEN_DAYS_AYYAMEQABLIYYAH),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingFinalDatesCase9part3b() {
        //ihtiyati ghusl dam less than 3 - A-3
        //another ayyame qabliyyah with ayyame qabliyya off
        val entries = listOf(
            Entry(Date(2022, 2, 1), Date(2022, 2, 3))
        )

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            5*MILLISECONDS_IN_A_DAY,
            28*MILLISECONDS_IN_A_DAY, 17*MILLISECONDS_IN_A_DAY,false),
            ikhtilaafaat = Ikhtilaafaat(ayyameQabliyyaIkhtilaf = true)
        ))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(5*MILLISECONDS_IN_A_DAY, 28*MILLISECONDS_IN_A_DAY),
                mutableListOf(
                    FutureDateType(Date(2022,2, 4), TypesOfFutureDates.BEFORE_THREE_DAYS),
                    FutureDateType(Date(2022,2, 6), TypesOfFutureDates.IC_FORBIDDEN_DATE),
                    FutureDateType(Date(2022,2, 11), TypesOfFutureDates.AFTER_TEN_DAYS),
                    FutureDateType(Date(2022,2, 6), TypesOfFutureDates.IHTIYATI_GHUSL),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingFinalDatesCase9part4() {
        //ihtiyati ghusl dam less than 3 - B-2
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 1), Date(2022, 2, 3))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            5*MILLISECONDS_IN_A_DAY,
            17*MILLISECONDS_IN_A_DAY, 18*MILLISECONDS_IN_A_DAY,false)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(5*MILLISECONDS_IN_A_DAY, 17*MILLISECONDS_IN_A_DAY),
                mutableListOf(
                    FutureDateType(Date(2022,2, 4), TypesOfFutureDates.BEFORE_THREE_DAYS),
                    FutureDateType(Date(2022,2, 6), TypesOfFutureDates.IC_FORBIDDEN_DATE),
                    FutureDateType(Date(2022,2, 11), TypesOfFutureDates.AFTER_TEN_DAYS),
                    FutureDateType(Date(2022,2, 5), TypesOfFutureDates.IHTIYATI_GHUSL),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingFinalDatesCase9part5() {
        //ihtiyati ghusl dam less than 3 - B-3
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 1), Date(2022, 2, 3))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            5*MILLISECONDS_IN_A_DAY,
            17*MILLISECONDS_IN_A_DAY, 30*MILLISECONDS_IN_A_DAY,false),
        ))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(5*MILLISECONDS_IN_A_DAY, 17*MILLISECONDS_IN_A_DAY),
                mutableListOf(
                    FutureDateType(Date(2022,2, 4), TypesOfFutureDates.BEFORE_THREE_DAYS),
                    FutureDateType(Date(2022,2, 6), TypesOfFutureDates.IC_FORBIDDEN_DATE),
                    FutureDateType(Date(2022,2, 11), TypesOfFutureDates.AFTER_TEN_DAYS),
                    FutureDateType(Date(2022,2, 6), TypesOfFutureDates.IHTIYATI_GHUSL),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingFinalDatesCase10() {
        //ihtiyati ghusl dam less than 3 - B-3
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 1), Date(2022, 2, 5))

        val output = handleEntries(AllTheInputs(
            entries, PreMaslaValues(
            5*MILLISECONDS_IN_A_DAY,
            17*MILLISECONDS_IN_A_DAY, 30*MILLISECONDS_IN_A_DAY,false),
        ))
        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(4*MILLISECONDS_IN_A_DAY, 30*MILLISECONDS_IN_A_DAY),
                mutableListOf(
                    FutureDateType(Date(2022,2, 6), TypesOfFutureDates.IC_FORBIDDEN_DATE),
                    FutureDateType(Date(2022,2, 11), TypesOfFutureDates.AFTER_TEN_DAYS),
                    FutureDateType(Date(2022,2, 6), TypesOfFutureDates.IHTIYATI_GHUSL),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun bugMaslaDescribedInIssue67() {
        //A-3 changing to A-1
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 1, 23), Date(2022, 1, 28))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            9*MILLISECONDS_IN_A_DAY,
            21*MILLISECONDS_IN_A_DAY, 23*MILLISECONDS_IN_A_DAY,true)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(5*MILLISECONDS_IN_A_DAY, 21*MILLISECONDS_IN_A_DAY),
                mutableListOf(
                    FutureDateType(Date(2022,2, 4), TypesOfFutureDates.IC_FORBIDDEN_DATE),
                    FutureDateType(Date(2022,2, 5), TypesOfFutureDates.AFTER_TEN_DAYS),
                    FutureDateType(Date(2022,2, 2), TypesOfFutureDates.IHTIYATI_GHUSL),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun bugMaslaDescribedInIssue103() {
        //pregnancy
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2021, 4, 4), Date(2021, 4, 12))
        entries +=//each month has to be one minus the real
            Entry(Date(2021, 5, 2), Date(2021, 5, 10))
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 5), Date(2022, 3, 23))

        val output = handleEntries(AllTheInputs(
            entries,
            typeOfMasla = TypesOfMasla.NIFAS,
            pregnancy = Pregnancy(Date(2021, 5, 10), Date(2022, 2, 5),
                40*MILLISECONDS_IN_A_DAY,
                mustabeenUlKhilqat = true)
        ))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(8*MILLISECONDS_IN_A_DAY, 21*MILLISECONDS_IN_A_DAY),
                mutableListOf(
                    FutureDateType(Date(2022,4, 5), TypesOfFutureDates.END_OF_AADAT_TUHR),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun bugMaslaDescribedInIssue116() {
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2021, 11, 10), Date(2021, 11, 16))
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 0, 9), Date(2022, 0, 16))
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 1, 10), Date(2022, 2, 15))

        val output = handleEntries(AllTheInputs(
            entries))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(6*MILLISECONDS_IN_A_DAY, 25*MILLISECONDS_IN_A_DAY),
                mutableListOf(
                    FutureDateType(Date(2022,2, 16), TypesOfFutureDates.BEFORE_THREE_DAYS),
                    FutureDateType(Date(2022,2, 19), TypesOfFutureDates.END_OF_AADAT_HAIZ),
                    FutureDateType(Date(2022,2, 19), TypesOfFutureDates.IHTIYATI_GHUSL),
                    FutureDateType(Date(2022,2, 19), TypesOfFutureDates.IC_FORBIDDEN_DATE),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun bugMaslaOccured17March2022() {
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 1), Date(2022, 2, 12))
        val output = handleEntries(AllTheInputs(entries,PreMaslaValues(
            6*MILLISECONDS_IN_A_DAY,
            20*MILLISECONDS_IN_A_DAY, 15*MILLISECONDS_IN_A_DAY,false)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(6*MILLISECONDS_IN_A_DAY, 20*MILLISECONDS_IN_A_DAY),
                mutableListOf(
                    FutureDateType(Date(2022,3, 1), TypesOfFutureDates.END_OF_AADAT_TUHR),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
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
            Entry(Date(2022, 2, 4, 13,0), Date(2022, 2, 17, 6,45))
        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            parseDays("7:20:30"),
            parseDays("17:17:30"), parseDays("37:5:30"),true)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(parseDays("7:20:30")!!, parseDays("17:17:30")!!),
                mutableListOf(
                    FutureDateType(Date(2022,2, 30, 3,0), TypesOfFutureDates.END_OF_AADAT_TUHR),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
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
                            startTime=Date(2021,1,27,8,10)),
                        Duration(type=DurationType.ISTIHAZA_AFTER,
                            timeInMilliseconds=1903140000,
                            startTime=Date(2021,2,4,8,39)),
                        Duration(type=DurationType.HAIZ,
                            timeInMilliseconds=433740000,
                            startTime=Date(2021,2,26,9,18)),
                        Duration(type=DurationType.ISTIHAZA_AFTER,
                            timeInMilliseconds=1903140000,
                            startTime=Date(2021,2,31,9,47)),
                        Duration(type=DurationType.HAIZ,
                            timeInMilliseconds=433740000,
                            startTime= Date(2021,3,22,10,26)),
                        Duration(type=DurationType.ISTIHAZA_AFTER,
                            timeInMilliseconds=144300000,
                            startTime= Date(2021, 8,27,10,55)))),
                biggerThanForty=null,
                startDate= Date(2021,1,27,8,10))

        val endtime = fixedDuration1.endDate
        val expectedentime = Date(2021,3,29, 3,0)
        assertEquals(endtime.getTime(), expectedentime.getTime())
    }
    @Test
    fun testingMubtadiaFinalOutputsCase1() {
        //dam less than 3, no aadat
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 1), Date(2022, 2, 2))

        val output = handleEntries(AllTheInputs(
            entries, typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(-1, -1),
                mutableListOf(
                    FutureDateType(Date(2022,2, 11), TypesOfFutureDates.END_OF_AADAT_HAIZ)
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase2() {
        //dam more than 3, no aadat
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 1), Date(2022, 2, 5))

        val output = handleEntries(AllTheInputs(
            entries, typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(4*MILLISECONDS_IN_A_DAY, -1),
                mutableListOf(
                    FutureDateType(Date(2022,2, 11), TypesOfFutureDates.END_OF_AADAT_HAIZ)
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testBugMaslaIssue134() {
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2021, 9, 14,15,20), Date(2021, 11, 15,6,0))
        entries +=//each month has to be one minus the real
            Entry(Date(2021, 11, 30,15,20), Date(2022, 2, 28,0,27))

        val output = handleEntries(
            AllTheInputs(
            entries,PreMaslaValues(
            parseDays("6:16:40"),
            parseDays("27:6:20"),
            parseDays("27:6:20"),
            false),
            typeOfMasla = TypesOfMasla.NIFAS,
            pregnancy = Pregnancy(Date(2021, 11, 12,0,0), Date(2021, 11, 30, 0,0),
                40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = false)))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(parseDays("6:16:40")!!, parseDays("27:6:20")!!),
                mutableListOf(
                    FutureDateType(Date(2022,3, 11,12,20), TypesOfFutureDates.END_OF_AADAT_TUHR)
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testBugMaslaIssue136() {
        //A-2 Masla
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2021, 8, 15), Date(2021, 9, 24))
        entries +=//each month has to be one minus the real
            Entry(Date(2021, 10, 8), Date(2021, 10, 24))

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            parseDays("6"),
            parseDays("27")),
            TypesOfMasla.NIFAS,
            pregnancy = Pregnancy(
                Date(2021, 3, 15), Date(2021, 8, 15),
                null, mustabeenUlKhilqat = true
            ))
        )

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(parseDays("4")!!, parseDays("27")!!),
                mutableListOf(
                    FutureDateType(Date(2021, 10, 26), TypesOfFutureDates.END_OF_AADAT_HAIZ),
                    FutureDateType(Date(2021, 10, 26), TypesOfFutureDates.IC_FORBIDDEN_DATE),
                    FutureDateType(Date(2021, 10, 26), TypesOfFutureDates.IHTIYATI_GHUSL)

                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)

        for (i in output.endingOutputValues.futureDateType.indices) {
            assertEquals(
                expectedEndingOutputValues.futureDateType[i].date.getTime(),
                output.endingOutputValues.futureDateType[i].date.getTime()
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
        val entries = mutableListOf(//each month has to be one minus the real
            Entry(Date(2022, 0, 13), Date(2022, 0, 19)),
            Entry(Date(2022, 1, 22), Date(2022, 1, 27)),
            Entry(Date(2022, 2, 17), Date(2022, 2, 31)),

        )

        val output = handleEntries(AllTheInputs(entries))

        val expectedEndingOutputValues =
            EndingOutputValues(
                null,
                AadatsOfHaizAndTuhr(parseDays("5")!!, parseDays("34")!!),
                mutableListOf(
                    FutureDateType(Date(2022, 3, 2), TypesOfFutureDates.START_OF_AADAT_AYYAMEQABLIYYA),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)

        for (i in output.endingOutputValues.futureDateType.indices) {
            assertEquals(
                expectedEndingOutputValues.futureDateType[i].date.getTime(),
                output.endingOutputValues.futureDateType[i].date.getTime()
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
        val entries = mutableListOf(//each month has to be one minus the real
            Entry(Date(2022, 0, 13), Date(2022, 0, 19)),
            Entry(Date(2022, 1, 22), Date(2022, 1, 27)),
            Entry(Date(2022, 2, 17), Date(2022, 2, 31)),

            )

        val output = handleEntries(AllTheInputs(
            entries, ikhtilaafaat = Ikhtilaafaat( ayyameQabliyyaIkhtilaf = true))
        )

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(parseDays("5")!!, parseDays("18")!!),
                mutableListOf(
                    FutureDateType(Date(2022, 3, 2), TypesOfFutureDates.A3_CHANGING_TO_A2),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)

        for (i in output.endingOutputValues.futureDateType.indices) {
            assertEquals(
                expectedEndingOutputValues.futureDateType[i].date.getTime(),
                output.endingOutputValues.futureDateType[i].date.getTime()
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
        val entries = mutableListOf(//each month has to be one minus the real
            Entry(Date(2021, 10, 8), Date(2021, 10, 13)),
            Entry(Date(2021, 10, 30), Date(2021, 11, 8)),
            Entry(Date(2021, 11, 28), Date(2022, 0, 2)),
            Entry(Date(2022, 0, 16), Date(2022, 0, 25)),
            Entry(Date(2022, 1, 11), Date(2022, 1, 21)),
            Entry(Date(2022, 2, 10), Date(2022, 2, 22)),
            Entry(Date(2022, 3, 8), Date(2022, 3, 8)),

            )

        val output = handleEntries(AllTheInputs(
            entries)
        )

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(parseDays("10")!!, parseDays("17")!!),
                mutableListOf(
                    FutureDateType(Date(2022, 3, 11), TypesOfFutureDates.BEFORE_THREE_DAYS),
                    FutureDateType(Date(2022, 3, 18), TypesOfFutureDates.IC_FORBIDDEN_DATE),
                    FutureDateType(Date(2022, 3, 18), TypesOfFutureDates.AFTER_TEN_DAYS),
                    FutureDateType(Date(2022, 3, 16), TypesOfFutureDates.IHTIYATI_GHUSL),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)

        for (i in output.endingOutputValues.futureDateType.indices) {
            assertEquals(
                expectedEndingOutputValues.futureDateType[i].date.getTime(),
                output.endingOutputValues.futureDateType[i].date.getTime()
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
            Entry(Date(2022, 2, 1), Date(2022, 2, 11))

        val output = handleEntries(AllTheInputs(
            entries,typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                null,
                AadatsOfHaizAndTuhr(10*MILLISECONDS_IN_A_DAY, -1),
                mutableListOf(
                    FutureDateType(Date(0,0, 0), TypesOfFutureDates.TEN_DAYS_EXACTLY)
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase4a() {
        //dam >10, no aadat
        val entries = listOf(
            Entry(Date(2022, 2, 1), Date(2022, 2, 12))
        )

        val output = handleEntries(AllTheInputs(
            entries, typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(-1L, -1L),
                mutableListOf(
                    FutureDateType(Date(2022,2, 31), TypesOfFutureDates.END_OF_AADAT_TUHR)
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)

        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase4b() {//ikhtilaf
        //dam >10, no aadat
        val entries = listOf(
            Entry(Date(2022, 2, 1), Date(2022, 2, 12))
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
                    FutureDateType(Date(2022,2, 31), TypesOfFutureDates.END_OF_AADAT_TUHR)
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase5a() {
        //dam >10, no aadat ends at end of istehaza, start of daur
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 1), Date(2022, 2, 31))

        val output = handleEntries(AllTheInputs(
            entries, typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(-1L, -1L),
                mutableListOf(
                    FutureDateType(Date(2022,3, 3), TypesOfFutureDates.BEFORE_THREE_DAYS),
                    FutureDateType(Date(2022,3, 10), TypesOfFutureDates.END_OF_AADAT_HAIZ),
                    FutureDateType(Date(2022,3, 10), TypesOfFutureDates.IHTIYATI_GHUSL),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase5b() {//ikhtilaf
        //dam >10, no aadat ends at end of istehaza, start of daur
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 1), Date(2022, 2, 31))

        val output = handleEntries(AllTheInputs(
            entries, typeOfMasla = TypesOfMasla.MUBTADIA, ikhtilaafaat = Ikhtilaafaat(mubtadiaIkhitilaf = true)
        ))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(parseDays("10")!!, parseDays("20")!!),
                mutableListOf(
                    FutureDateType(Date(2022,3, 3), TypesOfFutureDates.BEFORE_THREE_DAYS),
                    FutureDateType(Date(2022,3, 10), TypesOfFutureDates.END_OF_AADAT_HAIZ),
                    FutureDateType(Date(2022,3, 10), TypesOfFutureDates.IHTIYATI_GHUSL),
//                    FutureDateType(Date(2022,3, 10), TypesOfFutureDates.IC_FORBIDDEN_DATE),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase6a() {
        //dam >10, no aadat ends at start of haiz less than 3, start of daur
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 1), Date(2022, 3, 1))

        val output = handleEntries(AllTheInputs(
            entries,typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(-1L, -1L),
                mutableListOf(
                    FutureDateType(Date(2022,3, 3), TypesOfFutureDates.BEFORE_THREE_DAYS),
                    FutureDateType(Date(2022,3, 10), TypesOfFutureDates.END_OF_AADAT_HAIZ),
                    FutureDateType(Date(2022,3, 10), TypesOfFutureDates.IHTIYATI_GHUSL),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase6b() {//ikhtilaf
        //dam >10, no aadat ends at start of haiz less than 3, start of daur
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 1), Date(2022, 3, 1))

        val output = handleEntries( AllTheInputs(
            entries, typeOfMasla = TypesOfMasla.MUBTADIA, ikhtilaafaat = Ikhtilaafaat(mubtadiaIkhitilaf = true)
        ))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(parseDays("10")!!,parseDays("20")!!),
                mutableListOf(
                    FutureDateType(Date(2022,3, 3), TypesOfFutureDates.BEFORE_THREE_DAYS),
                    FutureDateType(Date(2022,3, 10), TypesOfFutureDates.END_OF_AADAT_HAIZ),
                    FutureDateType(Date(2022,3, 10), TypesOfFutureDates.IHTIYATI_GHUSL),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase7a() {
        //dam >10, no aadat ends at start of haiz bigger than 3, less than 10 start of daur
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 1), Date(2022, 3, 5))

        val output = handleEntries(AllTheInputs(
            entries,typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(parseDays("5")!!, -1L),
                mutableListOf(
                    FutureDateType(Date(2022,3, 10), TypesOfFutureDates.END_OF_AADAT_HAIZ),
                    FutureDateType(Date(2022,3, 10), TypesOfFutureDates.IHTIYATI_GHUSL),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase7b() {//ikhtilaf
        //gotta fix this

        //dam >10, no aadat ends at start of haiz bigger than 3, less than 10 start of daur
        val entries = listOf(
            Entry(Date(2022, 2, 1), Date(2022, 3, 5))
        )

        val output = handleEntries(AllTheInputs(
            entries, typeOfMasla = TypesOfMasla.MUBTADIA, ikhtilaafaat = Ikhtilaafaat(mubtadiaIkhitilaf = true)
        ))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(parseDays("10")!!, parseDays("20")!!),
                mutableListOf(
                    FutureDateType(Date(2022,3, 10), TypesOfFutureDates.END_OF_AADAT_HAIZ),
                    FutureDateType(Date(2022,3, 10), TypesOfFutureDates.IHTIYATI_GHUSL),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase8a() {
        //dam >10, no aadat ends at end of haiz 10  daur
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 1), Date(2022, 3, 10))

        val output = handleEntries(AllTheInputs(
            entries,typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(-1L, -1L),
                mutableListOf(
                    FutureDateType(Date(2022,3, 30), TypesOfFutureDates.END_OF_AADAT_TUHR),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }

    @Test
    fun testingMubtadiaFinalOutputsCase8b() {//ikhtilaaf
        //dam >10, no aadat ends at end of haiz 10  daur
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 1), Date(2022, 3, 10))

        val output = handleEntries(AllTheInputs(
            entries, typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(-1L, -1L),
                mutableListOf(
                    FutureDateType(Date(2022,3, 30), TypesOfFutureDates.END_OF_AADAT_TUHR),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase9() {
        //dam <3,  aadat
        val entries = mutableListOf(
            Entry(Date(2022, 3, 1), Date(2022, 3, 5)),
            Entry(Date(2022, 3, 22), Date(2022, 3, 22)),
            Entry(Date(2022, 4, 7), Date(2022, 4, 8)),

        )

        val output = handleEntries(AllTheInputs(entries,typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(parseDays("4")!!, -1L),
                mutableListOf(
                    FutureDateType(Date(2022,4, 10), TypesOfFutureDates.BEFORE_THREE_DAYS),
                    FutureDateType(Date(2022,4, 17), TypesOfFutureDates.AFTER_TEN_DAYS),
                    FutureDateType(Date(2022,4, 11), TypesOfFutureDates.IHTIYATI_GHUSL),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase10() {
        //dam >3,  <aadat
        val entries = mutableListOf(
            Entry(Date(2022, 3, 1), Date(2022, 3, 5)),
            Entry(Date(2022, 3, 22), Date(2022, 3, 22)),
            Entry(Date(2022, 4, 7), Date(2022, 4, 10)),

            )

        val output = handleEntries(AllTheInputs(entries,typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(parseDays("3")!!, -1L),
                mutableListOf(
                    FutureDateType(Date(2022,4, 17), TypesOfFutureDates.AFTER_TEN_DAYS),
                    FutureDateType(Date(2022,4, 11), TypesOfFutureDates.IHTIYATI_GHUSL),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase11() {
        //dam >3,  >aadat
        val entries = mutableListOf(
            Entry(Date(2022, 3, 1), Date(2022, 3, 5)),
            Entry(Date(2022, 3, 22), Date(2022, 3, 22)),
            Entry(Date(2022, 4, 7), Date(2022, 4, 11)),

            )

        val output = handleEntries(AllTheInputs(entries,typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(parseDays("4")!!, -1L),
                mutableListOf(
                    FutureDateType(Date(2022,4, 17), TypesOfFutureDates.AFTER_TEN_DAYS),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase12() {
        //dam >3,  >aadat
        val entries = mutableListOf(
            Entry(Date(2022, 3, 1), Date(2022, 3, 5)),
            Entry(Date(2022, 3, 22), Date(2022, 3, 22)),
            Entry(Date(2022, 4, 7), Date(2022, 4, 12)),

            )

        val output = handleEntries(AllTheInputs(entries,typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(parseDays("5")!!, -1L),
                mutableListOf(
                    FutureDateType(Date(2022,4, 17), TypesOfFutureDates.AFTER_TEN_DAYS),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase13() {
        //dam 10 aadat
        val entries = mutableListOf(
            Entry(Date(2022, 3, 1), Date(2022, 3, 5)),
            Entry(Date(2022, 3, 22), Date(2022, 3, 22)),
            Entry(Date(2022, 4, 7), Date(2022, 4, 17)),

            )

        val output = handleEntries(AllTheInputs(entries,typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                null,
                AadatsOfHaizAndTuhr(parseDays("10")!!, -1L),
                mutableListOf(
                    FutureDateType(Date(0,0, 0), TypesOfFutureDates.TEN_DAYS_EXACTLY),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase14() {
        //dam bigger than 10  aadat
        val entries = mutableListOf(
            Entry(Date(2022, 3, 1), Date(2022, 3, 5)),
            Entry(Date(2022, 3, 22), Date(2022, 3, 22)),
            Entry(Date(2022, 4, 7), Date(2022, 4, 18)),

            )

        val output = handleEntries(AllTheInputs(entries,typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(parseDays("4")!!, -1L),
                mutableListOf(
                    FutureDateType(addTimeToDate(Date(2022,4,7),30*MILLISECONDS_IN_A_DAY), TypesOfFutureDates.END_OF_AADAT_TUHR),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase15() {
        //dam bigger than 10  aadat
        val entries = mutableListOf(
            Entry(Date(2022, 3, 1), Date(2022, 3, 5)),
            Entry(Date(2022, 3, 22), Date(2022, 3, 22)),
            Entry(Date(2022, 4, 7), Date(2022, 5, 7)),

            )

        val output = handleEntries(AllTheInputs(entries,typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(parseDays("4")!!, -1L),
                mutableListOf(
                    FutureDateType(Date(2022,5,9), TypesOfFutureDates.BEFORE_THREE_DAYS),
                    FutureDateType(Date(2022,5,10), TypesOfFutureDates.END_OF_AADAT_HAIZ),
                    FutureDateType(Date(2022,5,10), TypesOfFutureDates.IHTIYATI_GHUSL),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase16() {
        //dam bigger than 10  aadat
        val entries = mutableListOf(
            Entry(Date(2022, 3, 1), Date(2022, 3, 5)),
            Entry(Date(2022, 3, 22), Date(2022, 3, 22)),
            Entry(Date(2022, 4, 7), Date(2022, 5, 8)),

            )

        val output = handleEntries(AllTheInputs(entries,typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(parseDays("4")!!, -1L),
                mutableListOf(
                    FutureDateType(Date(2022,5,9), TypesOfFutureDates.BEFORE_THREE_DAYS),
                    FutureDateType(Date(2022,5,10), TypesOfFutureDates.END_OF_AADAT_HAIZ),
                    FutureDateType(Date(2022,5,10), TypesOfFutureDates.IHTIYATI_GHUSL),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase17() {
        //dam bigger than 10  aadat
        val entries = mutableListOf(
            Entry(Date(2022, 3, 1), Date(2022, 3, 5)),
            Entry(Date(2022, 3, 22), Date(2022, 3, 22)),
            Entry(Date(2022, 4, 7), Date(2022, 5, 9)),

            )

        val output = handleEntries(AllTheInputs(entries,typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(parseDays("3")!!, -1L),
                mutableListOf(
                    FutureDateType(Date(2022,5,10), TypesOfFutureDates.END_OF_AADAT_HAIZ),
                    FutureDateType(Date(2022,5,10), TypesOfFutureDates.IHTIYATI_GHUSL),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase18() {
        //dam bigger than 10  aadat
        val entries = mutableListOf(
            Entry(Date(2022, 3, 1), Date(2022, 3, 5)),
            Entry(Date(2022, 3, 22), Date(2022, 3, 22)),
            Entry(Date(2022, 4, 7), Date(2022, 5, 10)),

            )

        val output = handleEntries(AllTheInputs(entries,typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(parseDays("4")!!, -1L),
                mutableListOf(
                    FutureDateType(Date(2022,6,6), TypesOfFutureDates.END_OF_AADAT_TUHR),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaFinalOutputsCase19() {
        //dam bigger than 10  aadat
        val entries = mutableListOf(
            Entry(Date(2022, 3, 1), Date(2022, 3, 5)),
            Entry(Date(2022, 3, 22), Date(2022, 3, 22)),
            Entry(Date(2022, 4, 7), Date(2022, 5, 11)),

            )

        val output = handleEntries(AllTheInputs(entries,typeOfMasla = TypesOfMasla.MUBTADIA))

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(parseDays("4")!!, -1L),
                mutableListOf(
                    FutureDateType(Date(2022,6,6), TypesOfFutureDates.END_OF_AADAT_TUHR),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
    }
    @Test
    fun testingMubtadiaUsingInputtedAadat() {
        //dam bigger than 10  aadat
        val entries = mutableListOf(
            Entry(Date(2022, 2, 1), Date(2022, 2, 18)),
            )

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            parseDays("7"),
            null, parseDays("30"),false),
            typeOfMasla = TypesOfMasla.MUBTADIA))

        val hazDatesList = output.hazDatesList
        val expectedHazDatesList = mutableListOf(
            Entry(Date(2022, 2, 1), Date(2022,2,8))
        )

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(parseDays("7")!!, -1L),
                mutableListOf(
                    FutureDateType(Date(2022,2,31), TypesOfFutureDates.END_OF_AADAT_TUHR),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
        for(i in hazDatesList.indices){
            assertEquals(hazDatesList[i].startTime.getTime(), expectedHazDatesList[i].startTime.getTime())
            assertEquals(hazDatesList[i].endTime.getTime(), expectedHazDatesList[i].endTime.getTime())
        }
    }
    @Test
    fun testingMubtadiaUsingInputtedAadat2() {
        //dam bigger than 10  aadat
        val entries = mutableListOf(
            Entry(Date(2022, 2, 1), Date(2022, 2, 18)),
        )

        val output = handleEntries(AllTheInputs(
            entries,PreMaslaValues(
            parseDays("7"),
            null, parseDays("22"),false),
            typeOfMasla = TypesOfMasla.MUBTADIA))

        val hazDatesList = output.hazDatesList
        val expectedHazDatesList = mutableListOf(
            Entry(Date(2022, 2, 2), Date(2022,2,9))
        )

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(parseDays("7")!!, -1L),
                mutableListOf(
                    FutureDateType(Date(2022,3,1), TypesOfFutureDates.END_OF_AADAT_TUHR),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }
        for(i in hazDatesList.indices){
            assertEquals(hazDatesList[i].startTime.getTime(), expectedHazDatesList[i].startTime.getTime())
            assertEquals(hazDatesList[i].endTime.getTime(), expectedHazDatesList[i].endTime.getTime())
        }
    }
    @Test
    fun testingMubtadiaDurationCase1a() {
        //5 د 13ط 18د 11ط 16د 15ط 11د 18ط استمرار
        val arbitraryTime = Date(0,0,0)
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
        val arbitraryTime = Date(0,0,0)
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
        val arbitraryTime = Date(0,0,0)
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
        val arbitraryTime = Date(0,0,0)
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
                pregnancy = Pregnancy(aadatNifas= 40*MILLISECONDS_IN_A_DAY,
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
                pregnancy = Pregnancy(aadatNifas =  35*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
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
                    35*MILLISECONDS_IN_A_DAY,
                    0,0,0,0,
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
                pregnancy = Pregnancy(aadatNifas = 38*MILLISECONDS_IN_A_DAY,
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
                    38*MILLISECONDS_IN_A_DAY,
                    0,0,0,0,
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
                biggerThanTen = BiggerThanTenDm(0,0,0,0,Soortain.A_1,0,0,0,0,0,
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
                    mustabeenUlKhilqat = false)
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
            Entry(Date(2022,1,13), Date(2022, 1, 21)),
            Entry(Date(2022,2,27), Date(2022, 3, 19)),

        )

        val output = handleEntries(
            AllTheInputs(
                entries,
                preMaslaValues = PreMaslaValues(inputtedMawjoodahTuhr = parseDays("31")!!),
                typeOfMasla = TypesOfMasla.NIFAS,
                pregnancy = Pregnancy(Date(2022,2,21), Date(2022, 3,15),
                    mustabeenUlKhilqat = false)
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
    fun bugMaslaIssue195() {
        //haidh less than 3 not being created printed because ayyame qabliyya, but after aadat
        val entries = listOf<Entry>(
            Entry(Date(2022,6,13,8,30), Date(2022, 6, 23, 11,0)),
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
                biggerThanTen = BiggerThanTenDm(0,0,0,0,Soortain.A_3,0, 0, 0, 0,
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
                    FutureDateType(Date(2022,3,26), TypesOfFutureDates.IC_FORBIDDEN_DATE),
                    FutureDateType(Date(2022,3,30), TypesOfFutureDates.AFTER_TEN_DAYS),
                    FutureDateType(Date(2022,3,28), TypesOfFutureDates.IHTIYATI_GHUSL),
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
            Entry(Date(2021,4,21), Date(2021, 4, 27)),
            Entry(Date(2021,5,21), Date(2021, 5, 27)),
            //preg +Birth
            Entry(Date(2022,2,4), Date(2022, 2, 28)),
            Entry(Date(2022,3,20), Date(2022, 3, 25)),
        )
        val output = handleEntries(
            AllTheInputs(
                entries,
                typeOfMasla = TypesOfMasla.NIFAS,
                pregnancy = Pregnancy(Date(2021,5,27), Date(2022, 2,4), aadatNifas = parseDays("40")!!,
                    mustabeenUlKhilqat = true)
            )
        )
        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(parseDays("5")!!, parseDays("23")!!, parseDays("24")!!),
                mutableListOf(
                    FutureDateType(Date(2022,3,26), TypesOfFutureDates.IC_FORBIDDEN_DATE),
                    FutureDateType(Date(2022,3,30), TypesOfFutureDates.AFTER_TEN_DAYS),
                    FutureDateType(Date(2022,3,28), TypesOfFutureDates.IHTIYATI_GHUSL),
                    //it wants the last to be 26, even though this is A-2
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType.size, output.endingOutputValues.futureDateType.size)
        for(i in output.endingOutputValues.futureDateType.indices){
            assertEquals(expectedEndingOutputValues.futureDateType[i].date.getTime(),output.endingOutputValues.futureDateType[i].date.getTime())
            assertEquals(expectedEndingOutputValues.futureDateType[i].futureDates,output.endingOutputValues.futureDateType[i].futureDates)
        }

    }
    @Test
    fun testAadatFromMiddle() {
        //issue #200
        val entries = listOf<Entry>(
            Entry(Date(2022,5,1), Date(2022, 5, 30)),
            Entry(Date(2022,7,1), Date(2022, 7, 7)),
            Entry(Date(2022,8,1), Date(2022, 8, 7)),
            Entry(Date(2022,9,1), Date(2022, 9, 13)),
        )
        val output = handleEntries(
            AllTheInputs(
                entries,
                typeOfMasla = TypesOfMasla.MUTADAH,
                typeOfInput = TypesOfInputs.DATE_ONLY
            )
        )
        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(parseDays("6")!!, parseDays("25")!!),
                mutableListOf(
                    FutureDateType(Date(2022,10,2), TypesOfFutureDates.END_OF_AADAT_TUHR),
                )
            )
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)

        val expectedFixedDurations = listOf<FixedDuration>(
            FixedDuration(DurationType.UNSOLVED_DAM, parseDays("29")!!),
            FixedDuration(DurationType.UNSOLVED_TUHR_PRESUMED_FASID, parseDays("32")!!),
        )
        assertEquals(expectedFixedDurations[0].type, output.fixedDurations[0].type)
        assertEquals(expectedFixedDurations[1].type, output.fixedDurations[1].type)
        assertEquals(expectedFixedDurations[0].days, output.fixedDurations[0].days)
        assertEquals(expectedFixedDurations[1].days, output.fixedDurations[1].days)

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
    fun testDurationsMawjoodahPaki() {
        var durations = listOf<Duration>(
            Duration(DurationType.DAM,0L),
            Duration(DurationType.TUHR, 15*MILLISECONDS_IN_A_DAY),
            Duration(DurationType.DAM, 5*MILLISECONDS_IN_A_DAY)
        )
        var output = handleEntries(convertDurationsIntoEntries(durations))
        val fixedDurations=output.fixedDurations
        var expectedFixedDurations = listOf<FixedDuration>(
            FixedDuration(DurationType.TUHREFAASID, 15*MILLISECONDS_IN_A_DAY),
            FixedDuration(DurationType.DAM, 5*MILLISECONDS_IN_A_DAY)
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
        var durations = listOf<Duration>(
            Duration(DurationType.TUHR, 15*MILLISECONDS_IN_A_DAY),
            Duration(DurationType.DAM, 0*MILLISECONDS_IN_A_DAY),
            Duration(DurationType.TUHR, 15*MILLISECONDS_IN_A_DAY),
            Duration(DurationType.DAM, 5*MILLISECONDS_IN_A_DAY),
        )
        var output = handleEntries(convertDurationsIntoEntries(durations))
        val fixedDurations=output.fixedDurations
        var expectedFixedDurations = listOf<FixedDuration>(
            FixedDuration(DurationType.TUHREFAASID, 30*MILLISECONDS_IN_A_DAY),
            FixedDuration(DurationType.DAM, 5*MILLISECONDS_IN_A_DAY)
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
        var durations = listOf<Duration>(
            Duration(DurationType.TUHR, 15*MILLISECONDS_IN_A_DAY),
            Duration(DurationType.DAM, 2*MILLISECONDS_IN_A_DAY),
            Duration(DurationType.TUHR, 15*MILLISECONDS_IN_A_DAY),
            Duration(DurationType.DAM, 5*MILLISECONDS_IN_A_DAY),
        )
        var output = handleEntries(convertDurationsIntoEntries(durations))
        val fixedDurations=output.fixedDurations
        var expectedFixedDurations = listOf<FixedDuration>(
            FixedDuration(DurationType.TUHREFAASID, 32*MILLISECONDS_IN_A_DAY),
            FixedDuration(DurationType.DAM, 5*MILLISECONDS_IN_A_DAY)
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