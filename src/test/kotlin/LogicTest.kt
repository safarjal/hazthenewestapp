import kotlin.js.Date
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

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
        var entries = listOf<Entry>()
        entries+=//14 jun - 20 Jun
            Entry(Date(2020,5,14), Date(2020,5,20))
        entries+=//20 Jul - 27 Jul
            Entry(Date(2020,6,20), Date(2020,6,27))
        entries+=//30 Aug - 1 Oct
            Entry(Date(2020,7,30), Date(2020,9,1))
        println(entries)

        val output = handleEntries(entries,false,null,null,true,false,Pregnancy(Date(1,1,1),Date(1,1,1),null,false))
        val haizDateList = output.hazDatesList
        var expectedHaizDatesList = listOf<Entry>()
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
        var entries = listOf<Entry>()
        entries+=//each month has to be one minus the real
            Entry(Date(2020,3,15), Date(2020,3,21))
        entries+=
            Entry(Date(2020,4,7), Date(2020,4,14))
        entries+=//30 Aug - 1 Oct
            Entry(Date(2021,5,14), Date(2021,9,6))
        println(entries)

        val output = handleEntries(entries,false,null,null,true,true,Pregnancy(Date(2020,9,6),Date(2021,5,15),25.0,true))
        val haizDateList = output.hazDatesList

//        From 15 4 2020 to 21 4 2020
//        From 07 5 2020 to 14 5 2020
//        From 15 6 2021 to 10 7 2021
//        From 26 7 2021 to 02 8 2021
//        From 18 8 2021 to 25 8 2021
//        From 10 9 2021 to 17 9 2021
        var expectedHaizDatesList = listOf<Entry>()
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
        var entries = listOf<Entry>()
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
        println(entries)

        val output = handleEntries(entries,false,null,null,true,true,Pregnancy(Date(2021,4,21),Date(2021,6,25),25.0,mustabeenUlKhilqat = false))
        val haizDateList = output.hazDatesList

        var expectedHaizDatesList = listOf<Entry>()
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
        var entries = listOf<Entry>()
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

        println(entries)

        val output = handleEntries(entries,false,null,null,true,false,Pregnancy(Date(1,1,1),Date(1,1,1),null,mustabeenUlKhilqat = false))
        val haizDateList = output.hazDatesList

        var expectedHaizDatesList = listOf<Entry>()
        expectedHaizDatesList += Entry(Date(2020,11,25), Date(2020,11,30))
        expectedHaizDatesList += Entry(Date(2021,0,20), Date(2021,0,26))
        expectedHaizDatesList += Entry(Date(2021,1,16), Date(2021,1,22))
        assertEquals(haizDateList.size, expectedHaizDatesList.size)

        for(i in expectedHaizDatesList.indices){
            assertEquals(haizDateList[i].startTime.getTime(), expectedHaizDatesList[i].startTime.getTime())
            assertEquals(haizDateList[i].endTime.getTime(), expectedHaizDatesList[i].endTime.getTime())
        }
        var expectedEndingOutputValues = EndingOutputValues(true, AadatsOfHaizAndTuhr(6*MILLISECONDS_IN_A_DAY,21*MILLISECONDS_IN_A_DAY), FutureDateType(Date(2021,2,15), TypesOfFutureDates.END_OF_AADAT_TUHR))
        assertEquals(expectedEndingOutputValues.aadats, output.endingOutputValues.aadats)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType!!.date.getTime(),output.endingOutputValues.futureDateType!!.date.getTime())
        assertEquals(expectedEndingOutputValues.futureDateType!!.futureDates,output.endingOutputValues.futureDateType!!.futureDates)

    }
    @Test
    fun mashqiSawal2(){
        var entries = listOf<Entry>()
        entries+=//each month has to be one minus the real
            Entry(Date(2020,11,5), Date(2020,11,14))
        entries+=
            Entry(Date(2021,0,5), Date(2021,0,14))
        entries+=
            Entry(Date(2021,1,7), Date(2021,1,13))
        entries+=
            Entry(Date(2021,1,21), Date(2021,2,11))

        println(entries)

        val output = handleEntries(entries,false,null,null,true,false,Pregnancy(Date(1,1,1),Date(1,1,1),null,mustabeenUlKhilqat = false))
        val haizDateList = output.hazDatesList

        var expectedHaizDatesList = listOf<Entry>()
        expectedHaizDatesList += Entry(Date(2020,11,5), Date(2020,11,14))
        expectedHaizDatesList += Entry(Date(2021,0,5), Date(2021,0,14))
        expectedHaizDatesList += Entry(Date(2021,1,7), Date(2021,1,14))
        expectedHaizDatesList += Entry(Date(2021,2,10), Date(2021,2,11))
        assertEquals(haizDateList.size, expectedHaizDatesList.size)

        for(i in expectedHaizDatesList.indices){
            assertEquals(haizDateList[i].startTime.getTime(), expectedHaizDatesList[i].startTime.getTime())
            assertEquals(haizDateList[i].endTime.getTime(), expectedHaizDatesList[i].endTime.getTime())
        }

        var expectedEndingOutputValues = EndingOutputValues(false, AadatsOfHaizAndTuhr(7*MILLISECONDS_IN_A_DAY,24*MILLISECONDS_IN_A_DAY), FutureDateType(Date(2021,2,17), TypesOfFutureDates.END_OF_AADAT_HAIZ))
        assertEquals(expectedEndingOutputValues.aadats, output.endingOutputValues.aadats)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        assertEquals(expectedEndingOutputValues.futureDateType!!.date.getTime(),output.endingOutputValues.futureDateType!!.date.getTime())
        assertEquals(expectedEndingOutputValues.futureDateType!!.futureDates,output.endingOutputValues.futureDateType!!.futureDates)
    }
    @Test
    fun mashqiSawal3(){
        var entries = listOf<Entry>()
        entries+=//each month has to be one minus the real
            Entry(Date(2020,3,29), Date(2020,4,6))
        entries+=
            Entry(Date(2020,4,26), Date(2020,4,30))
        entries+=
            Entry(Date(2020,7,2), Date(2020,7,16))

        println(entries)

        val output = handleEntries(entries,false,null,null,true,false,Pregnancy(Date(1,1,1),Date(1,1,1),null,mustabeenUlKhilqat = false))
        val haizDateList = output.hazDatesList

        var expectedHaizDatesList = listOf<Entry>()
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

        var expectedEndingOutputValues =
            EndingOutputValues(true, AadatsOfHaizAndTuhr(4*MILLISECONDS_IN_A_DAY,64*MILLISECONDS_IN_A_DAY), FutureDateType(Date(2021,2,17), TypesOfFutureDates.END_OF_AADAT_HAIZ))
        assertEquals(expectedEndingOutputValues.aadats, output.endingOutputValues.aadats)
        assertEquals(expectedEndingOutputValues.filHaalPaki, output.endingOutputValues.filHaalPaki)
        //since no future date was provided, it won't be part of the test
//        assertEquals(expectedEndingOutputValues.futureDateType!!.date.getTime(),output.endingOutputValues.futureDateType!!.date.getTime())
//        assertEquals(expectedEndingOutputValues.futureDateType!!.futureDates,output.endingOutputValues.futureDateType!!.futureDates)
    }


}



