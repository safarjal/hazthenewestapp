import kotlin.js.Date

data class Entry(
    val startTime: Date,
    val endTime: Date
)

enum class DurationType {
    DAM, TUHR, TUHREFAASID
}
enum class Soortain {
    A_1, A_2, A_3, B_2, B_3
}

data class Duration(
    var type: DurationType,
    var days: Double,
    var indices: MutableList<Int> = mutableListOf()
)
