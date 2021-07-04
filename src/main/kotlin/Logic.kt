import kotlinx.browser.window

fun handleEntries(entries: List<Entry>) {
    val times = entries
        .flatMap { entry -> listOf(entry.startTime, entry.endTime) }
        .map { it.getTime() }
    if (times != times.sorted()) {
        window.alert("Please enter the dates in order")
        return
    }
    var isDam = true
    val durations = times.zipWithNext { firstTime, secondTime ->
        val type = if (isDam) DurationType.DAM else DurationType.TUHR
        isDam = !isDam
        Duration(type, timeInMilliseconds = secondTime - firstTime)
    }
    for (duration in durations) {
        println("duration type = ${duration.type}, duration days = ${duration.days}")
    }
}
