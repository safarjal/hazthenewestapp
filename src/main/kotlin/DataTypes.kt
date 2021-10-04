import kotlin.js.Date

data class Entry(
    val startTime: Date,
    val endTime: Date
)

data class Pregnancy(
    val pregStartTime:Date,
    val birthTime:Date,
    var aadatNifas:Double? = 40.0,
    val mustabeenUlKhilqat:Boolean
)

enum class DurationType {
    DAM, TUHR, TUHREFAASID, ISTIMRAR, TUHR_IN_HAML, NIFAAS, DAM_IN_HAML, DAM_IN_NIFAAS_PERIOD, ISTIHAZA_BEFORE,ISTIHAZA_AFTER,HAIZ
}
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