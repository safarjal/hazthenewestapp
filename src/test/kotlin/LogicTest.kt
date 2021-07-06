import kotlin.js.Date
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class LogicTest {
    @Test
    fun testHandleEntries() {
        val entries = listOf(
            Entry(Date(), Date()),
            Entry(Date(), Date())
        )
        val result = handleEntries(entries)
        assertNotNull(result)
    }
    @Test
    fun testRemoveDamLessThan3(){
        val durations = mutableListOf<Duration>(
            Duration(DurationType.TUHR, timeInMilliseconds=(86400000*15).toDouble()),
            Duration(DurationType.DAM, timeInMilliseconds=(86400000*2).toDouble()),
            Duration(DurationType.TUHR, timeInMilliseconds=(86400000*15).toDouble())

            )
        removeDamLessThan3(durations)
        assertEquals(1,durations.size)
        assertEquals(DurationType.TUHREFAASID,durations[0].type)
//        assertEquals((86400000L*32).toDouble(),durations[0].timeInMilliseconds)
        assertEquals(32.0,durations[0].days)
        val expectedDuration = Duration(
            DurationType.TUHREFAASID,
            32.0
        )
        assertEquals(expectedDuration, durations[0])

    }
}
