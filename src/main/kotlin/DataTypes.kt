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

class Duration(
    var type: DurationType,
    var indices: MutableList<Int> = mutableListOf(),
    timeInMilliseconds: Double
) {
    var days: Double = (timeInMilliseconds / 86400000).toDouble()
}
