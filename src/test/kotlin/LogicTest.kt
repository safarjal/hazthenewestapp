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
        assertNotNull(result) // TODO: Replace this with actual test
    }

    @Test
    fun testRemoveDamLessThan3(){
        val durations = mutableListOf<Duration>(
            Duration(DurationType.TUHR, 15.0),
            Duration(DurationType.DAM, 2.0),
            Duration(DurationType.TUHR, 15.0)
        )
        removeDamLessThan3(durations)
        assertEquals(1, durations.size)
        assertEquals(DurationType.TUHREFAASID, durations[0].type)
        assertEquals(32.0, durations[0].days)
    }
}
