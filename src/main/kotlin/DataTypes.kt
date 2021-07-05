import kotlin.js.Date

data class Entry(
    val startTime: Date,
    val endTime: Date
)

enum class DurationType {
    DAM, TUHR
}

data class Duration(
    val type: DurationType,
    val timeInMilliseconds: Double
) {
    val days get() = timeInMilliseconds / 86400000
}
