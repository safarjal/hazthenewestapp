import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.*
import org.w3c.dom.HTMLElement
import kotlin.js.Json

const val hazappBackend = "http://localhost:3000"
//const val hazappBackend = "http://170.64.146.104/maslas"

val bearerTokenStorage = mutableListOf<BearerTokens>()
suspend fun login(username: String, password: String): Pair<HttpStatusCode, Json> {
    val client = HttpClient(Js) {
        install(Logging)
        install(ContentNegotiation) { json(Json) }
        install(HttpCookies)
        install(Auth) {
//            bearer {
//                loadTokens {
//                    bearerTokenStorage.last()
//                }
//            }
        }
    }
    val userData = User(user = UserData(username = username, password = password))

    val response: HttpResponse = client.post("$hazappBackend/users/sign_in") {
        headers {
            append(HttpHeaders.AccessControlAllowOrigin, "*")
//            append(HttpHeaders.UnsafeHeadersList, listOf<>(""))
//            append("Credentials", "include")
//            append("response_type", "code")
//            append("code", authorizationCode)
        }
        contentType(ContentType.Application.Json)
        setBody(userData)
    }

    console.log("Cookies: ", response.setCookie().toString(), client.cookies("http://0.0.0.0:3000/"), client.cookies("http://0.0.0.0:8080/"))

    if (response.status == HttpStatusCode.OK) {
        val token = response.headers[HttpHeaders.Authorization]
        console.log("Authorization Header: $token, ${bearerTokenStorage.firstOrNull()}")
//        console.log("Body: $token, ${body}")
        // Handle successful authentication
    } else {
        console.log("auth failed")
        // Handle authentication failure
    }
//    val jwtToken = client.readToken(response)?.getClaim("yourClaimName")?.asString()
    println(response.bodyAsText())
    client.close()

    return Pair(response.status, JSON.parse(response.body()))
}

suspend fun getDataFromInputsAndSend(inputsContainer: HTMLElement): Json {
    with(inputsContainer) {
        val entries = haizInputDatesRows.map { row ->
            SaveEntries(
                startTime = row.startTimeInput.value,
                endTime = row.endTimeInput.value
            )
        }

        val toSend = SaveData(
            typeOfMasla = maslaSelect.value,
            typeOfInput = typeSelect.value,
            entries = entries,
            answerEnglish = contentEnglish.textContent,
            answerUrdu = contentUrdu.textContent,
            others = OtherValues(
                title = titleText,
                question = questionText,
                aadatHaiz = aadatHaz.value,
                aadatTuhr = aadatTuhr.value,
                mawjoodahTuhr = mawjoodaTuhr.value,
                isMawjoodaFasid = isMawjoodaFasid,
                pregStartTime = if (isNifas) pregStartTime.value else null,
                birthTime = if (isNifas) pregEndTime.value else null,
                aadatNifas = if (isNifas) aadatNifas.value else null,
                mustabeenUlKhilqat = if (isNifas) isMustabeen else null,
                ghairMustabeenIkhtilaaf = ikhtilaf1,
                daurHaizIkhtilaf = ikhtilaf2,
                ayyameQabliyyaIkhtilaf = ikhtilaf3,
                mubtadiaIkhitilaf = ikhtilaf4,
                timeZone = if (isDateTime && !timezoneSelect.disabled) timezoneSelect.value else null,
            )
        )
        println(toSend)
        return sendData(toSend)
    }
}

suspend fun sendData(toSend: SaveData): Json {
    val client = HttpClient(Js) {
        install(ContentNegotiation) { json(Json) }
        install(HttpCookies)
//        install(Auth) {
//            bearer {
//                loadTokens {
//                    bearerTokenStorage.last()
//                }
//            }
//        }
    }
    val response: HttpResponse = client.post("$hazappBackend/maslas") {
        headers {
            append(HttpHeaders.AccessControlAllowOrigin, "*")
            append(HttpHeaders.AccessControlAllowCredentials, "true")
            append(HttpHeaders.Authorization, "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyIiwic2NwIjoidXNlciIsImF1ZCI6bnVsbCwiaWF0IjoxNzAzODAxMzk0LCJleHAiOjE3MDUwOTczOTQsImp0aSI6IjcxM2JjZTYxLTQxM2YtNDBiNS1iN2ZhLWQ4MDg0NTI1YTMzYSJ9.ciIs1UL_dSi5uwgQGfe3EKnZUmZdDyLRPZOWQjIFb5Q")
        }
        contentType(ContentType.Application.Json)
        setBody(toSend)
    }

    return JSON.parse(response.body())
}

suspend fun loadData(id: String): Json {
    val client = HttpClient(Js) {
        install(ContentNegotiation) { json(Json) }
    }
    val response: HttpResponse = client.get("$hazappBackend/maslas/$id") {
        headers {
            append(HttpHeaders.AccessControlAllowOrigin, "*")
        }
        contentType(ContentType.Application.Json)
    }

    return JSON.parse(response.body())
}

fun reInputData(data: Json, inputsContainer: HTMLElement) {
    with(inputsContainer) {
        maslaSelect.value = data["typeOfMasla"].toString()
        typeSelect.value = data["typeOfInput"].toString()
//        entries = entries,
//        answerEnglish = contentEnglish.textContent,
//        answerUrdu = contentUrdu.textContent,
    }
//            title = titleText,
//            question = questionText,
//            aadatHaiz = aadatHaz.value,
//            aadatTuhr = aadatTuhr.value,
//            mawjoodahTuhr = mawjoodaTuhr.value,
//            isMawjoodaFasid = isMawjoodaFasid,
//            pregStartTime = if (isNifas) pregStartTime.value else null,
//            birthTime = if (isNifas) pregEndTime.value else null,
//            aadatNifas = if (isNifas) aadatNifas.value else null,
//            mustabeenUlKhilqat = if (isNifas) isMustabeen else null,
//            ghairMustabeenIkhtilaaf = ikhtilaf1,
//            daurHaizIkhtilaf = ikhtilaf2,
//            ayyameQabliyyaIkhtilaf = ikhtilaf3,
//            mubtadiaIkhitilaf = ikhtilaf4,
//            timeZone = if (isDateTime && !timezoneSelect.disabled) timezoneSelect.value else null,
}
