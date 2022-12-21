import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.*
import org.w3c.dom.HTMLElement
import kotlin.js.Date
import kotlin.random.Random

//  {"entries": [
//      {"startTime":1669852800000,"endTime":1670457600000},
//      {"startTime":1672185600000,"endTime":1672444800000}],
//  "preMaslaValues":{
//      "inputtedAadatHaiz":432000000,
//      "inputtedAadatTuhr":1296000000,
//      "inputtedMawjoodahTuhr":1296000000},
//  "pregnancy":{
//      "pregStartTime":0,
//      "birthTime":0,
//      "aadatNifas":null,
//      "mustabeenUlKhilqat":true},
//  "language":"english"}

// TODO: Precook info:
//  1. false to string???
//  2. uid!!!
suspend fun sendData(inputElement: HTMLElement, outputTexts: OutputTexts): String {
    with(inputElement) {
        val entries = haizInputDatesRows.map { row ->
            Entry(
                startTime = Date(row.startTimeInput.valueAsNumber),
                endTime = Date(row.endTimeInput.valueAsNumber)
            )
        }

        val toSend = SaveData(
            uid = Random.nextInt(0, 100),
            typeOfMasla = maslaSelect.value,
            typeOfInput = typeSelect.value,
            entries = entries,
            answerEnglish = outputTexts.englishText,
            answerUrdu = outputTexts.urduText,
            others = OtherValues(
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

//suspend fun sendData(data: AllTheInputs): String {
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

        return response.bodyAsText()
    }
}
