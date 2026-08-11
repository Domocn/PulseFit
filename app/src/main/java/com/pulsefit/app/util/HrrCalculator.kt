package com.pulsefit.app.util

enum class HrrRating(val label: String) {
    EXCELLENT("Excellent"),
    GOOD("Good"),
    FAIR("Fair"),
    POOR("Poor")
}

data class HrrResult(
    val hrrValue: Int,
    val rating: HrrRating,
    val percentile: Int
)

object HrrCalculator {

    /**
     * Calculate Heart Rate Recovery (HRR) score.
     * HRR = peak HR minus HR at ~60 seconds post-exercise.
     *
     * @param peakHr The highest HR recorded during the workout
     * @param hrReadingsAfterStop List of (timestamp millis, heart rate) readings collected after workout end
     * @return HrrResult with the recovery drop, rating, and estimated percentile, or null if insufficient data
     */
    fun calculateHrr(peakHr: Int, hrReadingsAfterStop: List<Pair<Long, Int>>): HrrResult? {
        if (hrReadingsAfterStop.size < 2 || peakHr <= 0) return null

        val startTime = hrReadingsAfterStop.first().first
        // Find the reading closest to 60 seconds after stop
        val targetTime = startTime + 60_000L
        val closestReading = hrReadingsAfterStop
            .filter { it.first >= startTime + 30_000L } // at least 30s into recovery
            .minByOrNull { kotlin.math.abs(it.first - targetTime) }
            ?: hrReadingsAfterStop.last()

        val hrAt60s = closestReading.second
        val hrrValue = peakHr - hrAt60s

        val rating = when {
            hrrValue > 20 -> HrrRating.EXCELLENT
            hrrValue in 16..20 -> HrrRating.GOOD
            hrrValue in 12..15 -> HrrRating.FAIR
            else -> HrrRating.POOR
        }

        val percentile = when {
            hrrValue > 30 -> 95
            hrrValue > 25 -> 85
            hrrValue > 20 -> 75
            hrrValue > 16 -> 60
            hrrValue > 12 -> 40
            hrrValue > 8 -> 25
            else -> 10
        }

        return HrrResult(hrrValue = hrrValue, rating = rating, percentile = percentile)
    }
}
