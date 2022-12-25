import com.benasher44.uuid.uuid4
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.*
import org.w3c.dom.HTMLElement

// TODO: Precook info:
//  2. uid!!!
suspend fun sendData(inputElement: HTMLElement): NetworkResponse {
    with(inputElement) {
        val entries = haizInputDatesRows.map { row ->
            SaveEntries(
                startTime = row.startTimeInput.value,
                endTime = row.endTimeInput.value
            )
        }

        val toSend = SaveData(
            uid = uuid4().toString(),
            typeOfMasla = maslaSelect.value,
            typeOfInput = typeSelect.value,
            entries = entries,
            answerEnglish = contentEnglish.textContent,
            answerUrdu = contentUrdu.textContent,
            others = OtherValues(
                question = questionText.value,
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
            )
        )

        val client = HttpClient(Js) {
            install(ContentNegotiation) { json(Json) }
        }
        val response: HttpResponse = client.post("http://localhost:3000/maslas/") {
            headers {
                append(HttpHeaders.AccessControlAllowOrigin, "*")
            }
            contentType(ContentType.Application.Json)
            setBody(toSend)
        }

        return NetworkResponse(response.status.value, response.body())
    }
}
