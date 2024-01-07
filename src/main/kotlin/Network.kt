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

suspend fun loadData(id: String, inputsContainer: HTMLElement): Json {
    val response = client.get("$HAZAPP_BACKEND/maslas/$id") {
        contentType(ContentType.Application.Json)
    }

    if (response.status == HttpStatusCode.OK) {
        val loadedMasla = response.body<LoadData>()
        inputsContainer.errorMessage.visibility = false
        reInputData(loadedMasla, inputsContainer)
    } else {
        val message = response.body<ErrorResponse>()
        inputsContainer.errorMessage.visibility = true
        inputsContainer.errorMessage.innerText = message.error
    }

    return JSON.parse(response.body())
}

fun reInputData(data:  LoadData, inputsContainer: HTMLElement) {
    with(inputsContainer) {
        maslaSelect.value = data.typeOfMasla
        maslaChanging(data.typeOfMasla)
        typeSelect.value = data.typeOfInput
        typeChanging(inputsContainer, data.typeOfInput, data.more_infos?.timeZone)
        if (data.typeOfInput == Vls.Types.DATE_TIME && data.more_infos?.timeZone != null) {
            disableTimeZone.checked = true
            timezoneSelect.disabled = false
            timezoneSelect.value = data.more_infos.timeZone
        }

        aadatHaz.value = data.more_infos?.aadatHaiz.orEmpty()
        aadatTuhr.value = data.more_infos?.aadatTuhr.orEmpty()
        mawjoodaTuhr.value = data.more_infos?.mawjoodahTuhr.orEmpty()
        isMawjoodaFasidInput.checked = data.more_infos?.isMawjoodaFasid == true
        pregStartTime.value = data.more_infos?.pregStartTime.orEmpty()
        pregEndTime.value = data.more_infos?.birthTime.orEmpty()
        aadatNifas.value = data.more_infos?.aadatNifas.orEmpty()
        isMustabeenInput.checked = data.more_infos?.mustabeenUlKhilqat == true
        ikhtilaf1Input.checked = data.more_infos?.ghairMustabeenIkhtilaaf == true
        ikhtilaf2Input.checked = data.more_infos?.daurHaizIkhtilaf == true
        ikhtilaf3Input.checked = data.more_infos?.ayyameQabliyyaIkhtilaf == true
        ikhtilaf4Input.checked = data.more_infos?.mubtadiaIkhitilaf == true

        val entries = data.entries
        if (data.typeOfInput == Vls.Types.DURATION) {
            haizDurationInputTable.innerHTML = ""
            haizDurationInputTable.append {
                entries.forEachIndexed { index, entry ->
                    val isPregnancy = data.typeOfMasla == Vls.Maslas.NIFAS
                    val isMustabeen = data.more_infos?.mustabeenUlKhilqat == true
                    durationInputRow(
                        entries.getOrNull(index - 1)?.type == Vls.Opts.DAM,
                        false,
                        isPregnancy,
                        isMustabeen,
                        entry
                    )
                }
            }
        }
        else {
            hazInputTableBody.innerHTML = ""
            hazInputTableBody.append {
                val isDateOnly = data.typeOfInput == Vls.Types.DATE_ONLY
                entries.forEachIndexed { index, entry ->
                    inputRow(
                        isDateOnly,
                        entries.getOrNull(index - 1)?.endTime.orEmpty(),
                        entries.getOrNull(index + 1)?.startTime.orEmpty(),
                        false,
                        entry
                    )
                }
            }
        }

        saailaDetailsInput.value = data.more_infos?.saaila.orEmpty()
        questionTextInput.value = data.more_infos?.question.orEmpty()
        contentContainer.setAttribute("data-saved", "true")
        contentContainer.visibility = true
        contentEnglish.innerHTML = replaceStarWithStarAndBoldTag(data.answerEnglish)
        contentUrdu.innerHTML = replaceStarWithStarAndBoldTag(data.answerUrdu)
        contentContainer.scrollIntoView()
    }
}
