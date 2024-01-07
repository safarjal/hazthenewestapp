import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
  import kotlinx.browser.document
import kotlinx.browser.localStorage
import kotlinx.html.dom.append
import org.w3c.dom.HTMLElement
import kotlin.js.Json

val HAZAPP_BACKEND = Url("https://hazapp.ztree.pk")
//val HAZAPP_BACKEND = Url("http://localhost:3000/")
const val AUTHORIZATION = "Authorization"
val bearerToken get() = localStorage.getItem(AUTHORIZATION)

val client by lazy {
    HttpClient(Js) {
        install(ContentNegotiation) { json() }
    }
}

suspend fun login(username: String, password: String) {
    val userData = User(user = UserData(username = username, password = password))

    val response = client.post("$HAZAPP_BACKEND/users/sign_in"){
        contentType(ContentType.Application.Json)
        setBody(userData)
    }

    val token = response.headers[AUTHORIZATION]
    if (response.status == HttpStatusCode.OK && token != null) {
        localStorage.setItem(AUTHORIZATION, token)
        hazappPage()
    } else {
        val message = response.body<ErrorResponse>()
        document.body!!.errorMessage.innerText = message.error
    }
}

fun logout() {
    localStorage.removeItem(AUTHORIZATION)
    if (bearerToken.isNullOrEmpty()) {
        rootHazapp!!.innerHTML = ""
        rootHazapp.loginPage()
    }
}

suspend fun getDataFromInputsAndSend(inputsContainer: HTMLElement): Json? {
    with(inputsContainer) {
        val entries = if (isDuration) haizDurationInputDatesRows.map { row ->
            SaveEntries(
                value = row.durationInput.value,
                type = row.damOrTuhr,
                startTime = null,
                endTime = null
            )
        } else haizInputDatesRows.map { row ->
            SaveEntries(
                startTime = row.startTimeInput.value,
                endTime = row.endTimeInput.value,
                value = null,
                type = null
            )
        }

        val toSend = SaveData(
            typeOfMasla = maslaSelect.value,
            typeOfInput = typeSelect.value,
            entries = entries,
            answerEnglish = contentEnglish.textContent,
            answerUrdu = contentUrdu.textContent,
            others = OtherValues(
                saaila = saailaDetails,
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
        return sendData(toSend)
    }
}

suspend fun sendData(toSend: SaveData): Json? {
    val response = client.post("$HAZAPP_BACKEND/maslas/") {
        headers { bearerToken?.let { append(HttpHeaders.Authorization, it) } }
        contentType(ContentType.Application.Json)
        setBody(toSend)
    }

    return if (response.status == HttpStatusCode.Created) {
        JSON.parse(response.body())
    } else null
}

//suspend inline fun sendDataWithFetch(toSend: SaveData): Unit {
//    val response = fetch(
//        HAZAPP_BACKEND,
//        HttpMethod.Post,
//        toSend,
//        // I don't think this header makes sense as a request header
//        //Headers.build { append(HttpHeaders.AccessControlAllowOrigin, "*") },
//        credentials = RequestCredentials.INCLUDE,
//    )
//    return kotlinx.serialization.json.Json.decodeFromString(response.text().await())
//}

suspend fun loadData(id: String): Json {
    val response = client.get(HAZAPP_BACKEND) {
        // I don't think this header makes sense as a request header
        //headers { append(HttpHeaders.AccessControlAllowOrigin, "*") }
        contentType(ContentType.Application.Json)
        setBody(id)
    }

    return JSON.parse(response.body())
}

//fun reInputData(data: Json, inputsContainer: HTMLElement) {
//    with(inputsContainer) {
//        maslaSelect.value = data["typeOfMasla"].toString()
//        typeSelect.value = data["typeOfInput"].toString()
////        entries = entries,
////        answerEnglish = contentEnglish.textContent,
////        answerUrdu = contentUrdu.textContent,
//    }
////            title = titleText,
////            question = questionText,
////            aadatHaiz = aadatHaz.value,
////            aadatTuhr = aadatTuhr.value,
////            mawjoodahTuhr = mawjoodaTuhr.value,
////            isMawjoodaFasid = isMawjoodaFasid,
////            pregStartTime = if (isNifas) pregStartTime.value else null,
////            birthTime = if (isNifas) pregEndTime.value else null,
////            aadatNifas = if (isNifas) aadatNifas.value else null,
////            mustabeenUlKhilqat = if (isNifas) isMustabeen else null,
////            ghairMustabeenIkhtilaaf = ikhtilaf1,
////            daurHaizIkhtilaf = ikhtilaf2,
////            ayyameQabliyyaIkhtilaf = ikhtilaf3,
////            mubtadiaIkhitilaf = ikhtilaf4,
////            timeZone = if (isDateTime && !timezoneSelect.disabled) timezoneSelect.value else null,
//}
