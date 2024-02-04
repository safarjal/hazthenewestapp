import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.browser.document
import kotlinx.browser.localStorage
import kotlinx.datetime.internal.JSJoda.Instant
import kotlinx.html.dom.append
import org.w3c.dom.HTMLElement
import kotlin.js.Json

val HAZAPP_BACKEND = Url("https://hazapp.ztree.pk")
//val HAZAPP_BACKEND = Url("http://localhost:3000/")
const val AUTHORIZATION = "Authorization"
const val AUTHORIZATION_DATE = "Authorization-Date"
var bearerToken
    get() = localStorage.getItem(AUTHORIZATION)
    set(token) = localStorage.setItem(AUTHORIZATION, token.toString())
var tokenDate
    get() = localStorage.getItem(AUTHORIZATION_DATE)?.let { Instant.parse(it) }
    set(date) = localStorage.setItem(AUTHORIZATION_DATE, date.toString())

val client by lazy {
    HttpClient(Js) {
        install(ContentNegotiation) { json() }
    }
}

suspend fun login(username: String, password: String) {
    val userData = User(user = UserData(username = username, password = password))

    val response = client.post("$HAZAPP_BACKEND/users/sign_in") {
        contentType(ContentType.Application.Json)
        setBody(userData)
    }

    val token = response.headers[AUTHORIZATION]
    if (response.status == HttpStatusCode.OK && token != null) {
        bearerToken = token
        tokenDate = Instant.now()
        hazappPage()
    } else {
        val message = response.body<ErrorResponse>()
        document.body!!.errorMessage.innerText = message.error
    }
}

fun logout() {
    localStorage.removeItem(AUTHORIZATION)
    localStorage.removeItem(AUTHORIZATION_DATE)
    if (bearerToken.isNullOrEmpty()) {
        rootHazapp!!.innerHTML = ""
        loginPage()
    }
}

fun loggedIn(): Boolean {
    val dateDiff =
        if (tokenDate != null) Instant.now().minusMillis(tokenDate!!.toEpochMilli()).getMillisLong().getDays()
        else 29

    return !bearerToken.isNullOrEmpty() && dateDiff < 29
}

suspend fun getDataFromInputsAndSend(inputsContainer: HTMLElement): LoadData? {
    with(inputsContainer) {
        val entries = if (isDuration) haizDurationInputDatesRows.map { row ->
            SaveEntries(
                value = row.durationInput.value, type = row.damOrTuhr, startTime = null, endTime = null
            )
        } else haizInputDatesRows.map { row ->
            SaveEntries(
                startTime = row.startTimeInput.value, endTime = row.endTimeInput.value, value = null, type = null
            )
        }

        val toSend = SaveData(
            typeOfMasla = maslaSelect.value,
            typeOfInput = typeSelect.value,
            entries = entries,
            answerEnglish = contentEnglish.textContent,
            answerMMEnglish = contentMMEnglish.textContent,
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

suspend fun sendData(toSend: SaveData): LoadData? {
    val response = client.post("$HAZAPP_BACKEND/maslas/") {
        headers { bearerToken?.let { append(HttpHeaders.Authorization, it) } }
        contentType(ContentType.Application.Json)
        setBody(toSend)
    }

    return if (response.status == HttpStatusCode.Created) {
        return response.body<LoadData>()
    } else null
}

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

fun reInputData(data: LoadData, inputsContainer: HTMLElement) {
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
        handleLoadedEntries(data)
        saailaDetailsInput.value = data.more_infos?.saaila.orEmpty()
        questionTextInput.value = data.more_infos?.question.orEmpty()
        contentContainer.setAttribute("data-saved", "true")
        contentContainer.visibility = true
        contentEnglish.innerHTML = replaceStarWithStarAndBoldTag(data.answerEnglish)
        contentUrdu.innerHTML = replaceStarWithStarAndBoldTag(data.answerUrdu)
        contentContainer.scrollIntoView()
    }
}

fun HTMLElement.handleLoadedEntries(data: LoadData) {
    val entries = data.entries
    if (data.typeOfInput == Vls.Types.DURATION) {
        hazDurationInputTableBody.innerHTML = ""
        entriesToDurationTable(entries, data.typeOfMasla, data.more_infos?.mustabeenUlKhilqat)
    } else {
        hazInputTableBody.innerHTML = ""
        entriesToTable(entries, data.typeOfInput)
    }
}

fun HTMLElement.entriesToTable(entries: List<SaveEntries>, typeOfInput: String) {
    val isDateOnly = typeOfInput == Vls.Types.DATE_ONLY
    hazInputTableBody.append {
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

fun HTMLElement.entriesToDurationTable(entries: List<SaveEntries>, typeOfMasla: String, mustabeenUlKhilqat: Boolean?) {
    hazDurationInputTableBody.append {
        entries.map { entry ->
            val isNifaas = typeOfMasla == Vls.Maslas.NIFAS
            val isMustabeen = mustabeenUlKhilqat == true
            if (entry.value != null && entry.type != null) {
                copyDurationInputRow(
                    entry.value, entry.type, false, isNifaas, isMustabeen
                )
            }
        }
    }
}