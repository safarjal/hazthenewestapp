import kotlinx.browser.document
import kotlin.test.Test
import kotlin.test.assertEquals

class TestClient {
    @Test
    fun testSayHello() {
        val container = document.createElement("div")
        container.addInputLayout()
        //assertEquals("Hello from JS", container.textContent)
    }
} 