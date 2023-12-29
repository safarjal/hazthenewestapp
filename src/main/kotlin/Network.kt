import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.utils.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.await
import kotlinx.serialization.decodeFromString
import org.w3c.dom.HTMLElement
import org.w3c.fetch.INCLUDE
import org.w3c.fetch.RequestCredentials
import kotlin.js.Json

val HAZAPP_BACKEND = Url("http://localhost:3000/")
//val HAZAPP_BACKEND = Url("https://hazapp.ztree.pk/")

const val BEARER_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyIiwic2NwIjoidXNlciIsImF1ZCI6bnVsbCwiaWF0IjoxNzAzODgzNDg2LCJleHAiOjE3MDUxNzk0ODYsImp0aSI6IjZkMTM5MGQxLWMwZGItNDQ3MC05Y2JiLWI4ZDY2ZDdhM2JjMyJ9.85ZI2sCQz70v6M28uCQ9q4GZWEhtZvuQgX3It0NHY4c"

val client by lazy {
    HttpClient(Js) {
        install(ContentNegotiation) { json() }
    }
}
private fun org.w3c.fetch.Headers.mapToKtor(): Headers = buildHeaders {
    this@mapToKtor.asDynamic().forEach { value: String, key: String ->
        append(key, value)
    }

    Unit
}
suspend fun login(username: String, password: String): Pair<Short, String> {
    val userData = User(user = UserData(username = username, password = password))

    val response = fetch(
        Url("$HAZAPP_BACKEND/users/sign_in"),
        HttpMethod.Post,
        userData,
        // I don't think this header makes sense as a request header
        //Headers.build { append(HttpHeaders.AccessControlAllowOrigin, "*") },
//        credentials = RequestCredentials.INCLUDE,
    )
//    return kotlinx.serialization.json.Json.decodeFromString(response.text().await())
//
//    val response: HttpResponse = client.post("$HAZAPP_BACKEND/users/sign_in") {
//        contentType(ContentType.Application.Json)
//        setBody(userData)
//    }

//    console.log(response.headers["Authorization"])
//    console.log(response.headers.authorization)
    val headers = response.headers.mapToKtor()
    console.log(headers)
    console.log(headers.toString())
    console.log(headers.get("authorization"))
//    console.log(headers.authorization)
    console.log(headers.get("Authorization"))
//    console.log(response.headers.authorization)

    return Pair(response.status, response.text().await())
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
    val response = client.post("$HAZAPP_BACKEND/maslas/") {
        // I don't think this header makes sense as a request header
        headers {append(HttpHeaders.Authorization, BEARER_TOKEN)}
        contentType(ContentType.Application.Json)
        setBody(toSend)
    }

    return JSON.parse(response.body())
}

// TODO: Replace return type by response body JSON representing class
suspend inline fun sendDataWithFetch(toSend: SaveData): Unit {
    val response = fetch(
        HAZAPP_BACKEND,
        HttpMethod.Post,
        toSend,
        // I don't think this header makes sense as a request header
        //Headers.build { append(HttpHeaders.AccessControlAllowOrigin, "*") },
        credentials = RequestCredentials.INCLUDE,
    )
    return kotlinx.serialization.json.Json.decodeFromString(response.text().await())
}

suspend fun loadData(id: String): Json {
    val response = client.get(HAZAPP_BACKEND) {
        // I don't think this header makes sense as a request header
        //headers { append(HttpHeaders.AccessControlAllowOrigin, "*") }
        contentType(ContentType.Application.Json)
        setBody(id)
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
