import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.*
import kotlin.js.Date

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

val customInputs = AllTheInputs(
    entries = listOf(
        Entry(startTime = Date(1669852800000), endTime = Date(1670457600000)),
        Entry(startTime = Date(1672185600000), endTime = Date(1672444800000))
    ),
    preMaslaValues = PreMaslaValues(
        inputtedAadatHaiz = null,
        inputtedAadatTuhr = 1296000000,
        inputtedMawjoodahTuhr = 1296000000,
        isMawjoodaFasid = true,
    ),
    typeOfMasla = TypesOfMasla.MUBTADIA,
    pregnancy = Pregnancy(
        mustabeenUlKhilqat = true
    ),
    typeOfInput = TypesOfInputs.DATE_AND_TIME,
)

// TODO: Precook info:
//  1. false to string,
//  2. preg to null unless...,
//  3. uid
suspend fun sendData(): String {
//suspend fun sendData(data: AllTheInputs): String {
    val client = HttpClient(Js) {
        install(ContentNegotiation) { json(Json) }
    }
    val response: HttpResponse = client.post("http://localhost:3000/maslas/") {
        headers {
            append(HttpHeaders.AccessControlAllowOrigin, "*")
        }
        contentType(ContentType.Application.Json)
        setBody(customInputs)
    }
    return response.bodyAsText()
}
