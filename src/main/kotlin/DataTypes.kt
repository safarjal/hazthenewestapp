import kotlin.js.Date

data class Entry(
    val startTime: Date,
    val endTime: Date
)
enum class TypesOfFutureDates { A3_CHANGING_TO_A2, END_OF_AADAT_HAIZ, END_OF_AADAT_TUHR }

class FutureDateType(
    val date:Date,
    val futureDates:TypesOfFutureDates
)
class EndingOutputValues(
    val filHaalPaki:Boolean,
    val aadats:AadatsOfHaizAndTuhr?,
    val futureDateType: FutureDateType?
)
class OutputTexts (
    var englishText:String,
    var urduText: String,
    var haizDatesText:String,
    var hazDatesList: MutableList<Entry>,
    var endingOutputValues:EndingOutputValues
)


class InfoForCompareTable(
    val headerList: List<Date>,
    val listOfColorsOfDaysList: List<List<Int>>,
    val resultColors:List<Int>
)


data class Pregnancy(
    val pregStartTime:Date,
    val birthTime:Date,
    var aadatNifas:Double? = 40.0,
    val mustabeenUlKhilqat:Boolean
)

enum class DurationType {
    DAM, TUHR, TUHREFAASID, ISTIMRAR, TUHR_IN_HAML, NIFAAS, DAM_IN_HAML, DAM_IN_NIFAAS_PERIOD, ISTIHAZA_BEFORE,ISTIHAZA_AFTER,HAIZ, LESS_THAN_3_HAIZ, HAML,WILADAT_ISQAT
}

class DateTypeList (
    val date:Date,
    val type: DateTypes
)
enum class DateTypes {START,END, YAQEENI_PAKI,YAQEENI_NA_PAKI,AYYAAM_E_SHAKK_DUKHOOL, AYYAAM_E_SHAKK_KHUROOJ}

class DurationTypes (
    val startTime: Date,
    val endTime: Date,
    val type: DateTypes
)

enum class Soortain {
    A_1, A_2, A_3, B_2, B_3
}

data class Duration(
    val type: DurationType,
    val timeInMilliseconds: Long,
    val startTime: Date
) {
    val days: Double get() = timeInMilliseconds / MILLISECONDS_IN_A_DAY.toDouble()
    val endDate: Date get() = Date(startTime.getTime().toLong() + (timeInMilliseconds))

}

data class FixedDuration(
    var type: DurationType,
    var timeInMilliseconds: Long,
    var indices: MutableList<Int> = mutableListOf(),
    var istihazaAfter: Long = 0,
    var biggerThanTen: BiggerThanTenDm? = null,
    var biggerThanForty: BiggerThanFortyNifas? = null,
    var startDate: Date = Date(1,1,1),
) {
    val days: Double get() = timeInMilliseconds / MILLISECONDS_IN_A_DAY.toDouble()
    val endDate: Date get() = Date(startDate.getTime().toLong() + (timeInMilliseconds))
}

data class BiggerThanTenDm(
    var mp: Long, //mawjooda paki
    var gp: Long, //aadat of Tuhr before solving this
    var dm: Long, //dam
    var hz: Long, //aadat of haiz before solving this
    var qism: Soortain, //name of that case A-1, A-2, A-3, B-2, B-3
    var istihazaBefore: Long, //number of days of istihaza before haiz
    var haiz:Long, //number of days of haiz
    var istihazaAfter: Long, //number of days of istihaza after haiz
    var aadatHaiz:Long, //aadat of haiz after solving this
    var aadatTuhr:Long, //aadat of tuhur after solving this
    var durationsList: MutableList<Duration>

)
data class BiggerThanFortyNifas(
    var nifas: Long, //muddate nifas
    var istihazaAfter: Long, //number of days of istihaza after nifas
    var aadatHaiz:Long, //aadat of haiz before solving this
    var aadatTuhr:Long, //aadat of tuhur before solving this
    var durationsList: MutableList<Duration>
)

data class IstihazaAfterOutput(
    var aadatHaiz: Long,
    var haizDatesEntries: MutableList<Entry>
)

data class AadatsOfHaizAndTuhr(
    var aadatHaiz: Long,
    var aadatTuhr: Long
)