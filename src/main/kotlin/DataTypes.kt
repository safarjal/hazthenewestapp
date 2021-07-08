import kotlin.js.Date

data class Entry(
    val startTime: Date,
    val endTime: Date
)

enum class DurationType {
    DAM, TUHR, TUHREFAASID, ISTIMRAR
}
enum class Soortain {
    A_1, A_2, A_3, B_2, B_3
}

data class Duration(
    val type: DurationType,
    val timeInMilliseconds: Long
) {
    val days: Double get() = timeInMilliseconds / MILLISECONDS_IN_A_DAY
}

data class FixedDuration(
    var type: DurationType,
    var timeInMilliseconds: Long,
    var indices: MutableList<Int> = mutableListOf(),
    var istihazaAfter: Double = 0.0,
    var biggerThanTen: BiggerThanTenDm? = null,
    var startDate: Date? = null,
) {
    val days: Double get() = timeInMilliseconds / MILLISECONDS_IN_A_DAY
}

data class BiggerThanTenDm(
    var mp: Double, //mawjooda paki
    var gp: Double, //aadat of Tuhr before solving this
    var dm: Double, //dam
    var hz: Double, //aadat of haiz before solving this
    var qism: Soortain, //name of that case A-1, A-2, A-3, B-2, B-3
    var istihazaBefore: Double, //number of days of istihaza before haiz
    var haiz:Double, //number of days of haiz
    var istihazaAfter: Double, //number of days of istihaza after haiz
    var aadatHaiz:Double, //aadat of haiz after solving this
    var aadatTuhr:Double //aadat of tuhur after solving this
)