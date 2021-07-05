import kotlinx.browser.document
import org.w3c.dom.Element
import org.w3c.dom.get
import kotlin.test.*

class TestClient {
    @Test
    fun testAddInputLayout_laysOutAllElements() {
        val container = document.createElement("div")
        container.addInputLayout()
        val inputTable = assertNotNull(container.getChildById(Ids.INPUT_TABLE))
        val rows = inputTable.childNodes
        assertEquals(1, rows.length)
        val row = assertIs<Element>(rows[0])
        assertNotNull(row.getChildById(Ids.Row.INPUT_START_TIME))
        assertNotNull(row.getChildById(Ids.Row.INPUT_END_TIME))
        assertNotNull(row.getChildById(Ids.Row.BUTTON_ADD))
        assertNotNull(row.getChildById(Ids.Row.BUTTON_REMOVE))
        assertNotNull(container.getChildById(Ids.BUTTON_CALCULATE))
    }
}
