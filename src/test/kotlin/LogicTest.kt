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
            FixedDuration(DurationType.TUHR, timeInMilliseconds=(86400000*15).toLong()),
            FixedDuration(DurationType.DAM, timeInMilliseconds=(86400000*2).toLong()),
            FixedDuration(DurationType.TUHR, timeInMilliseconds=(86400000*15).toLong())
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

        val output = handleEntries(entries,null,null, null, false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(1,1,1),Date(1,1,1),null,false),
            isMubtadia = false,
            language = "urdu", false
        )
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
        val entries = mutableListOf<Entry>()
        entries+=//each month has to be one minus the real
            Entry(Date(2020,3,15), Date(2020,3,21))
        entries+=
            Entry(Date(2020,4,7), Date(2020,4,14))
        entries+=//30 Aug - 1 Oct
            Entry(Date(2021,5,14), Date(2021,9,6))

        val output = handleEntries(entries, null,null, null,false,
            isDateOnly = true,
            isPregnancy = true,
            pregnancy = Pregnancy(Date(2020,9,6),Date(2021,5,15),25*MILLISECONDS_IN_A_DAY,true), isMubtadia = false,
            language = "urdu", false
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

        val output = handleEntries(entries,null, null, null,false,
            isDateOnly = true,
            isPregnancy = true,
            pregnancy = Pregnancy(Date(2021,4,21),Date(2021,6,25),25*MILLISECONDS_IN_A_DAY,mustabeenUlKhilqat = false)
        , isMubtadia = false,
            language = "urdu", false)
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

        val output = handleEntries(entries,null,null, null,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(1,1,1),Date(1,1,1),null,mustabeenUlKhilqat = false)
        , isMubtadia = false,
            language = "urdu")
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
        val expectedEndingOutputValues = EndingOutputValues(true, AadatsOfHaizAndTuhr(6*MILLISECONDS_IN_A_DAY,21*MILLISECONDS_IN_A_DAY), mutableListOf())
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


        val output = handleEntries(entries,null,null, null,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(1,1,1),Date(1,1,1),null,mustabeenUlKhilqat = false)
        , isMubtadia = false,
            language = "urdu")
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


        val output = handleEntries(entries,null,null, null,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(1,1,1),Date(1,1,1),null,mustabeenUlKhilqat = false)
        , isMubtadia = false,
            language = "urdu")
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


        val output = handleEntries(entries,null,null, null,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(1,1,1),Date(1,1,1),null,mustabeenUlKhilqat = false)
        , isMubtadia = false,
            language = "urdu")
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

        val output = handleEntries(
            entries,
            null,
            null, null,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(1, 1, 1), Date(1, 1, 1), null, mustabeenUlKhilqat = false)
        , isMubtadia = false,
            language = "urdu")
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

        val output = handleEntries(
            entries,
            null,
            null, null,false,
            isDateOnly = true,
            isPregnancy = true,
            pregnancy = Pregnancy(Date(2020, 3, 26), Date(2021, 1, 14), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
        , isMubtadia = false,
            language = "urdu")
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
                AadatsOfHaizAndTuhr(5 * MILLISECONDS_IN_A_DAY, 21 * MILLISECONDS_IN_A_DAY),
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


        val output = handleEntries(
            entries,
            null,
            null, null,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2020, 3, 26), Date(2021, 1, 14), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
        , isMubtadia = false,
            language = "urdu")
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

        val output = handleEntries(
            entries,
            null,
            null, null,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2020, 3, 26), Date(2021, 1, 14), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
        , isMubtadia = false,
            language = "urdu")
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

        val output = handleEntries(
            entries,
            null,
            null, null,false,
            isDateOnly = true,
            isPregnancy = true,
            pregnancy = Pregnancy(Date(2020, 5, 10), Date(2021, 2, 5), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
        , isMubtadia = false,
            language = "urdu")
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
        println(output.urduText)

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(8 * MILLISECONDS_IN_A_DAY, 21 * MILLISECONDS_IN_A_DAY),
//                FutureDateType(Date(2021, 4, 5), TypesOfFutureDates.END_OF_AADAT_TUHR)
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
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2020, 10, 27), Date(2020, 11, 7))
        entries +=
            Entry(Date(2020, 11, 31), Date(2021, 0, 7))
        entries +=
            Entry(Date(2021, 0, 26), Date(2021, 1, 1))
        entries +=
            Entry(Date(2021, 1, 11), Date(2021, 1, 23))
        entries +=
            Entry(Date(2021, 1, 28), Date(2021, 2, 2))
        entries +=
            Entry(Date(2021, 10, 12), Date(2021, 11, 26))
        entries +=
            Entry(Date(2021, 11, 30), Date(2022, 0, 8))

        val output = handleEntries(
            entries,
            null,
            null, null,false,
            isDateOnly = true,
            isPregnancy = true,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList

        val expectedHaizDatesList = mutableListOf<Entry>()
        expectedHaizDatesList +=
            Entry(Date(2020, 10, 27), Date(2020, 11, 7))
        expectedHaizDatesList +=
            Entry(Date(2020, 11, 31), Date(2021, 0, 7))
        expectedHaizDatesList +=
            Entry(Date(2021, 0, 31), Date(2021, 1, 7))
        expectedHaizDatesList +=
            Entry(Date(2021, 10, 12), Date(2021, 11, 22))

        assertEquals(haizDateList.size, expectedHaizDatesList.size)

        for (i in expectedHaizDatesList.indices) {
            assertEquals(haizDateList[i].startTime.getTime(), expectedHaizDatesList[i].startTime.getTime())
            assertEquals(haizDateList[i].endTime.getTime(), expectedHaizDatesList[i].endTime.getTime())
        }

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(7 * MILLISECONDS_IN_A_DAY, 24 * MILLISECONDS_IN_A_DAY),
                mutableListOf()
            )
        println(output.fixedDurations)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
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
    fun testingAadatCase1() {
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 0), Date(2022, 2, 2))

        val output = handleEntries(
            entries,
            null,
            null, null,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList

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

        val output = handleEntries(
            entries,
            7*MILLISECONDS_IN_A_DAY,
            15*MILLISECONDS_IN_A_DAY, null,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList

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

        val output = handleEntries(
            entries,
            -1,
            -1, null,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList

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
        println(entries)

        val output = handleEntries(
            entries,
            -1,
            -1, null,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList
        println(output.fixedDurations)

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(5*MILLISECONDS_IN_A_DAY, 27*MILLISECONDS_IN_A_DAY),
                mutableListOf()
            )
        println("Aadat of tuhr is ${ output.endingOutputValues.aadats!!.aadatTuhr / MILLISECONDS_IN_A_DAY }")
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
    }
    @Test
    fun testingAadatCase3() {
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 0, 1), Date(2022, 0, 5))
        println(entries)

        val output = handleEntries(
            entries,
            -1,
            -1, null,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList
        println(output.fixedDurations)

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(4*MILLISECONDS_IN_A_DAY, -1),
                mutableListOf()
            )
        println("Aadat of tuhr is ${ output.endingOutputValues.aadats!!.aadatTuhr / MILLISECONDS_IN_A_DAY }")
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
    }
    @Test
    fun testingAadatCase3part2() {
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 0, 1), Date(2022, 0, 5))
        println(entries)

        val output = handleEntries(
            entries,
            8*MILLISECONDS_IN_A_DAY,
            30*MILLISECONDS_IN_A_DAY, null,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList
        println(output.fixedDurations)

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(4*MILLISECONDS_IN_A_DAY, 30*MILLISECONDS_IN_A_DAY),
                mutableListOf()
            )
        println("Aadat of tuhr is ${ output.endingOutputValues.aadats!!.aadatTuhr / MILLISECONDS_IN_A_DAY }")
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
        println(entries)

        val output = handleEntries(
            entries,
            8*MILLISECONDS_IN_A_DAY,
            30*MILLISECONDS_IN_A_DAY, null,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList
        println(output.fixedDurations)

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
        println(entries)

        val output = handleEntries(
            entries,
            8*MILLISECONDS_IN_A_DAY,
            30*MILLISECONDS_IN_A_DAY, null,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList
        println(output.fixedDurations)

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
        println(entries)

        val output = handleEntries(
            entries,
            5*MILLISECONDS_IN_A_DAY,
            60*MILLISECONDS_IN_A_DAY, 30*MILLISECONDS_IN_A_DAY,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList
        println(output.fixedDurations)

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
        println(entries)

        val output = handleEntries(
            entries,
            5*MILLISECONDS_IN_A_DAY,
            60*MILLISECONDS_IN_A_DAY, 30*MILLISECONDS_IN_A_DAY,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList
        println(output.fixedDurations)

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
        println(entries)

        val output = handleEntries(
            entries,
            5*MILLISECONDS_IN_A_DAY,
            30*MILLISECONDS_IN_A_DAY, 60*MILLISECONDS_IN_A_DAY,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList
        println(output.fixedDurations)

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
        println(entries)

        val output = handleEntries(
            entries,
            5*MILLISECONDS_IN_A_DAY,
            30*MILLISECONDS_IN_A_DAY, 60*MILLISECONDS_IN_A_DAY,true,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList
        println(output.fixedDurations)

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

        val output = handleEntries(
            entries,
            5*MILLISECONDS_IN_A_DAY,
            60*MILLISECONDS_IN_A_DAY, 30*MILLISECONDS_IN_A_DAY,true,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(5*MILLISECONDS_IN_A_DAY, 60*MILLISECONDS_IN_A_DAY),
                mutableListOf()
            )
        println("aadat haiz is ${output.endingOutputValues.aadats!!.aadatHaiz/MILLISECONDS_IN_A_DAY}")
        println("aadat tuhr is ${output.endingOutputValues.aadats!!.aadatTuhr/MILLISECONDS_IN_A_DAY}")
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
    }
    @Test
    fun testingAadatCase11() {
        //daur ending in istehaza
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 1), Date(2022, 2, 31))

        val output = handleEntries(
            entries,
            6*MILLISECONDS_IN_A_DAY,
            15*MILLISECONDS_IN_A_DAY, 17*MILLISECONDS_IN_A_DAY,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(4*MILLISECONDS_IN_A_DAY, 17*MILLISECONDS_IN_A_DAY),
                mutableListOf()
            )
        println("aadat haiz is ${output.endingOutputValues.aadats!!.aadatHaiz/MILLISECONDS_IN_A_DAY}")
        println("aadat tuhr is ${output.endingOutputValues.aadats!!.aadatTuhr/MILLISECONDS_IN_A_DAY}")
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
    }
    @Test
    fun testingAadatCase12() {
        //daur ending in haiz, less than 3
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 1), Date(2022, 3, 12))

        val output = handleEntries(
            entries,
            6*MILLISECONDS_IN_A_DAY,
            15*MILLISECONDS_IN_A_DAY, 17*MILLISECONDS_IN_A_DAY,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(4*MILLISECONDS_IN_A_DAY, 17*MILLISECONDS_IN_A_DAY),
                mutableListOf()
            )
        println("aadat haiz is ${output.endingOutputValues.aadats!!.aadatHaiz/MILLISECONDS_IN_A_DAY}")
        println("aadat tuhr is ${output.endingOutputValues.aadats!!.aadatTuhr/MILLISECONDS_IN_A_DAY}")
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
    }
    @Test
    fun testingAadatCase12part3() {
        //daur ending in istehaza, more than aadat, less than 10
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 1), Date(2022, 3, 19))

        val output = handleEntries(
            entries,
            6*MILLISECONDS_IN_A_DAY,
            15*MILLISECONDS_IN_A_DAY, 17*MILLISECONDS_IN_A_DAY,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(4*MILLISECONDS_IN_A_DAY, 17*MILLISECONDS_IN_A_DAY),
                mutableListOf()
            )
        println("aadat haiz is ${output.endingOutputValues.aadats!!.aadatHaiz/MILLISECONDS_IN_A_DAY}")
        println("aadat tuhr is ${output.endingOutputValues.aadats!!.aadatTuhr/MILLISECONDS_IN_A_DAY}")
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
    }

    @Test
    fun testingAadatCase12part2() {
        //daur ending in haiz, 3, less than aadat
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 1), Date(2022, 3, 15))

        val output = handleEntries(
            entries,
            6*MILLISECONDS_IN_A_DAY,
            15*MILLISECONDS_IN_A_DAY, 17*MILLISECONDS_IN_A_DAY,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList

        val expectedEndingOutputValues =
            EndingOutputValues(
                true,
                AadatsOfHaizAndTuhr(4*MILLISECONDS_IN_A_DAY, 17*MILLISECONDS_IN_A_DAY),
                mutableListOf()
            )
        println("aadat haiz is ${output.endingOutputValues.aadats!!.aadatHaiz/MILLISECONDS_IN_A_DAY}")
        println("aadat tuhr is ${output.endingOutputValues.aadats!!.aadatTuhr/MILLISECONDS_IN_A_DAY}")
        assertEquals(expectedEndingOutputValues.aadats!!.aadatHaiz, output.endingOutputValues.aadats!!.aadatHaiz)
        assertEquals(expectedEndingOutputValues.aadats!!.aadatTuhr, output.endingOutputValues.aadats!!.aadatTuhr)
    }
    @Test
    fun testingFinalDatesCase1() {
        //daur ending in haiz, 3, less than aadat
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 1), Date(2022, 2, 3))

        val output = handleEntries(
            entries,
            null,
            null, null,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(-1L, -1L),
                mutableListOf()
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

        val output = handleEntries(
            entries,
            5*MILLISECONDS_IN_A_DAY,
            15*MILLISECONDS_IN_A_DAY, null,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList

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
            entries,
            5*MILLISECONDS_IN_A_DAY,
            15*MILLISECONDS_IN_A_DAY, null,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList

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

        val output = handleEntries(
            entries,
            7*MILLISECONDS_IN_A_DAY,
            25*MILLISECONDS_IN_A_DAY, 20*MILLISECONDS_IN_A_DAY,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList

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

        val output = handleEntries(
            entries,
            7*MILLISECONDS_IN_A_DAY,
            25*MILLISECONDS_IN_A_DAY, 20*MILLISECONDS_IN_A_DAY,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(7*MILLISECONDS_IN_A_DAY, 25*MILLISECONDS_IN_A_DAY),
                mutableListOf(
                    FutureDateType(Date(2022,3, 14), TypesOfFutureDates.END_OF_AADAT_HAIZ)
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

        val output = handleEntries(
            entries,
            5*MILLISECONDS_IN_A_DAY,
            60*MILLISECONDS_IN_A_DAY, 30*MILLISECONDS_IN_A_DAY,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList

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

        val output = handleEntries(
            entries,
            5*MILLISECONDS_IN_A_DAY,
            60*MILLISECONDS_IN_A_DAY, 15*MILLISECONDS_IN_A_DAY,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList

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

        val output = handleEntries(
            entries,
            5*MILLISECONDS_IN_A_DAY,
            60*MILLISECONDS_IN_A_DAY, 15*MILLISECONDS_IN_A_DAY,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(5*MILLISECONDS_IN_A_DAY, 15*MILLISECONDS_IN_A_DAY),
                mutableListOf(
                    FutureDateType(Date(2022,3, 15), TypesOfFutureDates.A3_CHANGING_TO_A2),
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

        val output = handleEntries(
            entries,
            5*MILLISECONDS_IN_A_DAY,
            30*MILLISECONDS_IN_A_DAY, 60*MILLISECONDS_IN_A_DAY,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList

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

        val output = handleEntries(
            entries,
            5*MILLISECONDS_IN_A_DAY,
            59*MILLISECONDS_IN_A_DAY, 60*MILLISECONDS_IN_A_DAY,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList

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

        val output = handleEntries(
            entries,
            5*MILLISECONDS_IN_A_DAY,
            60*MILLISECONDS_IN_A_DAY, 15*MILLISECONDS_IN_A_DAY,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList

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

        val output = handleEntries(
            entries,
            5*MILLISECONDS_IN_A_DAY,
            18*MILLISECONDS_IN_A_DAY, 18*MILLISECONDS_IN_A_DAY,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList

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
    fun testingFinalDatesCase9part2() {
        //ihtiyati ghusl dam less than 3 - A-2 or A-1
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 1), Date(2022, 2, 3))

        val output = handleEntries(
            entries,
            5*MILLISECONDS_IN_A_DAY,
            28*MILLISECONDS_IN_A_DAY, 18*MILLISECONDS_IN_A_DAY,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList

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
    fun testingFinalDatesCase9part3() {
        //ihtiyati ghusl dam less than 3 - A-3
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 2, 1), Date(2022, 2, 3))

        val output = handleEntries(
            entries,
            5*MILLISECONDS_IN_A_DAY,
            28*MILLISECONDS_IN_A_DAY, 17*MILLISECONDS_IN_A_DAY,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList

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

        val output = handleEntries(
            entries,
            5*MILLISECONDS_IN_A_DAY,
            17*MILLISECONDS_IN_A_DAY, 18*MILLISECONDS_IN_A_DAY,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList

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

        val output = handleEntries(
            entries,
            5*MILLISECONDS_IN_A_DAY,
            17*MILLISECONDS_IN_A_DAY, 30*MILLISECONDS_IN_A_DAY,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList

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

        val output = handleEntries(
            entries,
            5*MILLISECONDS_IN_A_DAY,
            17*MILLISECONDS_IN_A_DAY, 30*MILLISECONDS_IN_A_DAY,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList

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
    fun BugMaslaDescribedInIssue67() {
        //A-3 changing to A-1
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 1, 23), Date(2022, 1, 28))

        val output = handleEntries(
            entries,
            9*MILLISECONDS_IN_A_DAY,
            21*MILLISECONDS_IN_A_DAY, 23*MILLISECONDS_IN_A_DAY,true,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList

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
    fun BugMaslaDescribedInIssue58() {
        //A-3 changing to A-1
        val entries = mutableListOf<Entry>()
        entries +=//each month has to be one minus the real
            Entry(Date(2021, 8, 24), Date(2021, 8, 30))
        entries +=//each month has to be one minus the real
            Entry(Date(2021, 9, 18), Date(2021, 9, 24))
        entries +=//each month has to be one minus the real
            Entry(Date(2021, 11, 17), Date(2021, 11, 22))
        entries +=//each month has to be one minus the real
            Entry(Date(2022, 0, 21), Date(2022, 1, 14))

        val output = handleEntries(
            entries,
            -1L,
            -1L, -1L,false,
            isDateOnly = true,
            isPregnancy = false,
            pregnancy = Pregnancy(Date(2021, 2, 2), Date(2021, 10, 12), 40*MILLISECONDS_IN_A_DAY, mustabeenUlKhilqat = true)
            , isMubtadia = false,
            language = "urdu")
        val haizDateList = output.hazDatesList

        val expectedEndingOutputValues =
            EndingOutputValues(
                false,
                AadatsOfHaizAndTuhr(5*MILLISECONDS_IN_A_DAY, 54*MILLISECONDS_IN_A_DAY),
                mutableListOf(
                    FutureDateType(Date(2022,1, 17), TypesOfFutureDates.BEFORE_THREE_DAYS_MASLA_WILL_CHANGE),
                    FutureDateType(Date(2022,1, 19), TypesOfFutureDates.END_OF_AADAT_HAIZ),
                    FutureDateType(Date(2022,1, 19), TypesOfFutureDates.IC_FORBIDDEN_DATE),
                    FutureDateType(Date(2022,1, 19), TypesOfFutureDates.IHTIYATI_GHUSL),
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

}



