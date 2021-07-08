import kotlin.js.Date
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class LogicTest {
    @Test
    fun testHandleEntries() {
        val istimrar = false
        val entries = listOf(
            Entry(Date(), Date()),
            Entry(Date(), Date())
        )
        val result = handleEntries(entries, istimrar)
        assertNotNull(result) // TODO: Replace this with actual test
    }

    @Test
    fun testRemoveDamLessThan3(){
        val durations = mutableListOf(
            FixedDuration(DurationType.TUHR, timeInMilliseconds=(86400000*15).toLong()),
            FixedDuration(DurationType.DAM, timeInMilliseconds=(86400000*2).toLong()),
            FixedDuration(DurationType.TUHR, timeInMilliseconds=(86400000*15).toLong())
        )
        removeDamLessThan3(durations)
        assertEquals(1,durations.size)
        assertEquals(DurationType.TUHREFAASID,durations[0].type)
        assertEquals(32.0, durations[0].days)
    }

    @Test
    fun testRemoveTuhrLessThan15(){
        val fixedDurations = mutableListOf(
            FixedDuration(DurationType.DAM, timeInMilliseconds = (MILLISECONDS_IN_A_DAY*2).toLong()),
            FixedDuration(DurationType.TUHR, timeInMilliseconds = (MILLISECONDS_IN_A_DAY*2).toLong()),
            FixedDuration(DurationType.DAM, timeInMilliseconds = (MILLISECONDS_IN_A_DAY*2).toLong())
        )
        removeTuhrLessThan15(fixedDurations)
        assertEquals(1, fixedDurations.size)
        assertEquals(DurationType.DAM, fixedDurations[0].type)
        assertEquals(6.0,fixedDurations[0].days)
    }
    @Test
    fun testFiveSoortain(){
        val mp = 21.0
        val gp = 16.0
        val dm = 11.0
        val hz = 7.0
        val output:FiveSoortainOutput = fiveSoortain(mp,gp,dm,hz)
        assertEquals(Soortain.B_3, output.soorat)
        assertEquals(0.0, output.istihazaBefore)
        assertEquals(7.0, output.haiz)
        assertEquals(4.0, output.istihazaAfter)
        assertEquals(true, output.aadatTuhrChanges)
    }
    @Test
    fun testAddIndicesToFixedDurations(){
        val fixedDurations = mutableListOf(
            FixedDuration(DurationType.DAM, timeInMilliseconds = (MILLISECONDS_IN_A_DAY*2).toLong()),
            FixedDuration(DurationType.TUHR, timeInMilliseconds = (MILLISECONDS_IN_A_DAY*2).toLong()),
            FixedDuration(DurationType.DAM, timeInMilliseconds = (MILLISECONDS_IN_A_DAY*2).toLong())
        )
        addIndicesToFixedDurations(fixedDurations)
        assertEquals(0,fixedDurations[0].indices[0])
        assertEquals(1,fixedDurations[1].indices[0])
        assertEquals(2,fixedDurations[2].indices[0])
        assertEquals(1,fixedDurations[0].indices.size)
        assertEquals(1,fixedDurations[1].indices.size)
        assertEquals(1,fixedDurations[2].indices.size)

    }
}
