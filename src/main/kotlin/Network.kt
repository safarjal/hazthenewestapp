import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

suspend fun saveCustomer(): String {
    val client = HttpClient(Js) {
        install(ContentNegotiation) { json(Json) }
    }
    val response: HttpResponse = client.post("https://github.com/ktorio") {
        contentType(ContentType.Application.Json)
        setBody("hello")
    }
    return response.bodyAsText()
}

@Serializable
data class Customer(val id: Int, val firstName: String, val lastName: String)