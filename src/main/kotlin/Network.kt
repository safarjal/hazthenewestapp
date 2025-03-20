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
import kotlinx.html.ButtonType
import kotlinx.html.classes
import kotlinx.html.dom.append
import kotlinx.html.js.button
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLElement
import kotlin.js.Json

val HAZAPP_BACKEND = Url("https://hazapp.ztree.pk")
//val HAZAPP_BACKEND = Url("http://localhost:3000")

const val USERID = "UserId"
const val USER_ROLE = "UserRole"
const val USER_MASLA_ID = "UserMaslaId"
const val DISPLAY_NAME = "DisplayName"
const val AUTHORIZATION = "Authorization"
const val AUTHORIZATION_DATE = "Authorization-Date"

var userId
    get() = localStorage.getItem(USERID)
    set(id) = localStorage.setItem(USERID, id.orEmpty())

var userRole
    get() = localStorage.getItem(USER_ROLE)
    set(role) = localStorage.setItem(USER_ROLE, role.orEmpty())
val isPersonalApper get() = userRole == "personal_apper"
var userMaslaId
    get() = localStorage.getItem(USER_MASLA_ID)
    set(maslaId) = localStorage.setItem(USER_MASLA_ID, maslaId.orEmpty())
val noUserMaslaId get() = userMaslaId.isNullOrEmpty() || userMaslaId == "null"
var savedDisplayName
    get() = localStorage.getItem(DISPLAY_NAME)
    set(name) = if (!name.isNullOrBlank() || name != "null") localStorage.setItem(DISPLAY_NAME, name.orEmpty())
    else localStorage.removeItem(DISPLAY_NAME)
val noDisplayName get() = savedDisplayName.isNullOrEmpty() || savedDisplayName == "null"

var bearerToken
    get() = localStorage.getItem(AUTHORIZATION)
    set(token) = localStorage.setItem(AUTHORIZATION, token.orEmpty())
var tokenDate
    get() = localStorage.getItem(AUTHORIZATION_DATE)?.let { Instant.parse(it) }
    set(date) = localStorage.setItem(AUTHORIZATION_DATE, date.toString())

val client by lazy {
    HttpClient(Js) {
        install(ContentNegotiation) { json() }
    }
}

suspend fun changeName(displayName: String) {
    document.body!!.errorMessage.innerText = ""
    val userData = User(user = DisplayName(displayname = displayName))
    val response = client.patch("$HAZAPP_BACKEND/users/$userId") {
        headers { bearerToken?.let { append(HttpHeaders.Authorization, it) } }
        contentType(ContentType.Application.Json)
        setBody(userData)
    }
    if (response.status == HttpStatusCode.OK) {
        val returnedUser = response.body<UserLoadData>()
        savedDisplayName = returnedUser.user.displayname
        document.body!!.errorMessage.innerText = returnedUser.message
    } else {
        val message = response.body<ErrorResponse>()
        document.body!!.errorMessage.innerText = message.error
    }
}

suspend fun login(username: String, password: String) {
    val userData = User(user = UsernamePassword(username = username, password = password))

    val response = client.post("$HAZAPP_BACKEND/users/sign_in") {
        contentType(ContentType.Application.Json)
        setBody(userData)
    }

    val token = response.headers[AUTHORIZATION]
    if (response.status == HttpStatusCode.OK && token != null) {
        bearerToken = token
        tokenDate = Instant.now()

        val loadedUser = response.body<UserLoadData>()
        userId = loadedUser.user.id
        savedDisplayName = loadedUser.user.displayname
        userRole = loadedUser.user.roleName
        userMaslaId = loadedUser.user.maslaId
        document.body!!.errorMessage.innerText = loadedUser.message

        hazappPage()
    } else {
        val message = response.body<ErrorResponse>()
        document.body!!.errorMessage.innerText = message.error
    }
}

fun soft_logout() {
    localStorage.removeItem(USERID)
    localStorage.removeItem(DISPLAY_NAME)
    localStorage.removeItem(AUTHORIZATION)
    localStorage.removeItem(AUTHORIZATION_DATE)
}

fun logout() {
    soft_logout()
    if (bearerToken.isNullOrEmpty()) {
        rootHazapp!!.innerHTML = ""
        loginPage()
    }
}

fun loggedIn(): Boolean {
    val maxLoggedInDays = 29
    val dateDiff =
        if (tokenDate != null) Instant.now().minusMillis(tokenDate!!.toEpochMilli()).getMillisLong().getDays()
        else maxLoggedInDays

    return !bearerToken.isNullOrEmpty() && dateDiff < maxLoggedInDays
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
            answerEnglish = formattedAnswers.englishString.replaceHtmlTagsWithStringSafe(), // TODO: This is dangerous.
            answerMMEnglish = formattedAnswers.mmEnglishString.replaceHtmlTagsWithStringSafe(),
            answerUrdu = formattedAnswers.urduString.replaceHtmlTagsWithStringSafe(),
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
                language = languageSelected
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
        headers { bearerToken?.let { append(HttpHeaders.Authorization, it) } }
        contentType(ContentType.Application.Json)
    }

    if (response.status == HttpStatusCode.OK) {
        val loadedMasla = response.body<LoadData>()
        if (isPersonalApper) userMaslaId = loadedMasla.id.toString()
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
        contentContainer.setAttribute("data-saved", data.id.toString())
        contentContainer.visibility = true

        formattedAnswers = OutputStringsLanguages(
            urduString = data.answerUrdu,
            englishString = data.answerEnglish,
            mmEnglishString = data.answerMMEnglish.orEmpty()
        )
        val htmlAnswers = formattedAnswers.replaceStarWithStarAndBoldTag()
        contentEnglish.innerHTML = htmlAnswers.englishString
        contentUrdu.innerHTML = htmlAnswers.urduString
        contentMMEnglish.innerHTML = htmlAnswers.mmEnglishString
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
        entriesToTable(entries, data.typeOfInput, data.more_infos?.timeZone)
    }
    setupRows(inputContainer = this)
}

const val daysAllowedPersonal = 2
fun HTMLElement.entriesToTable(entries: List<SaveEntries>, typeOfInput: String, tz: String?) {
    val isDateOnly = typeOfInput == Vls.Types.DATE_ONLY
    var olderThanThreeMonths = false
    hazInputTableBody.append {
        entries.forEachIndexed { index, entry ->
            inputRow(
                isDateOnly,
                entries.getOrNull(index - 1)?.minLessThanDaysAgo(daysAllowedPersonal, tz, isDateOnly).orEmpty(),
                entries.getOrNull(index + 1)?.startTime.orEmpty(),
                false, isPersonalApper,
                entry, tz
            ){
                if (isPersonalApper && !entry.endTime!!.isLessThanDaysAgo(daysAllowedPersonal)) {
                    classes = setOf(CssC.COLLAPSE, CssC.COLLAPSIBLE)
                }
            }
        }
        if (isPersonalApper && !noUserMaslaId) {
            val minTime = entries.last().endTime

            olderThanThreeMonths = !entries.last().endLessThanDaysAgo(90, tz)
            if (!olderThanThreeMonths && !noUserMaslaId) {
                inputRow(
                    isDateOnly,
                    minTimeInput = minTime ?: daysAllowedPersonal.daysAgo().toDateInputString(isDateOnly),
                    maxTimeInput = tzOffsetNOW.toDateInputString(isDateOnly)
                )
            }
            {
                button {
                    type = ButtonType.button
                    onClickFunction = { event -> addNowRow(event, tz) }
                    +"Add Now"
                }
            }
        }
    }
    if (olderThanThreeMonths) {
        inputsContainerMessage.appendChild { makeSpans(Strings::tooOldMasla) }
    }
    if (isPersonalApper && noUserMaslaId) {
        inputsContainerMessage.appendChild { makeSpans(Strings::noPersonalMasla) }
    }
}

fun HTMLElement.entriesToDurationTable(
    entries: List<SaveEntries>,
    typeOfMasla: String,
    mustabeenUlKhilqat: Boolean?
) {
    hazDurationInputTableBody.append {
        val isNifaas = typeOfMasla == Vls.Maslas.NIFAS
        val isMustabeen = mustabeenUlKhilqat == true
        entries.map { entry ->
            if (entry.value != null && entry.type != null) {
                copyDurationInputRow(
                    entry.value, entry.type, isPersonalApper, isNifaas, isMustabeen, isPersonalApper
                )
            }
        }
        val lastWasDam = entries.last().type != Vls.Opts.TUHR
        durationInputRow(lastWasDam, false, isNifas, isMustabeen)
    }
}
