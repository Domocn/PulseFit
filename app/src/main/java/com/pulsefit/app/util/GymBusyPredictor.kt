package com.pulsefit.app.util

import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GymBusyPredictor @Inject constructor() {

    data class BusyPrediction(
        val level: BusyLevel,
        val label: String,
        val suggestion: String
    )

    enum class BusyLevel { LOW, MODERATE, HIGH, PEAK }

    fun predict(gymCrowdRating: Int = 3): BusyPrediction {
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val isWeekend = dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY

        val baseLevel = when {
            isWeekend && hour in 9..11 -> BusyLevel.HIGH
            isWeekend -> BusyLevel.MODERATE
            hour in 6..8 -> BusyLevel.HIGH
            hour in 11..13 -> BusyLevel.MODERATE
            hour in 16..19 -> BusyLevel.PEAK
            hour in 20..21 -> BusyLevel.MODERATE
            else -> BusyLevel.LOW
        }

        val adjusted = if (gymCrowdRating >= 4) {
            when (baseLevel) {
                BusyLevel.LOW -> BusyLevel.MODERATE
                BusyLevel.MODERATE -> BusyLevel.HIGH
                else -> baseLevel
            }
        } else if (gymCrowdRating <= 2) {
            when (baseLevel) {
                BusyLevel.PEAK -> BusyLevel.HIGH
                BusyLevel.HIGH -> BusyLevel.MODERATE
                else -> baseLevel
            }
        } else baseLevel

        return BusyPrediction(
            level = adjusted,
            label = when (adjusted) {
                BusyLevel.LOW -> "Quiet right now"
                BusyLevel.MODERATE -> "Moderately busy"
                BusyLevel.HIGH -> "Busy"
                BusyLevel.PEAK -> "Peak hours"
            },
            suggestion = when (adjusted) {
                BusyLevel.LOW -> "Great time to go - fewer people, less stimulation"
                BusyLevel.MODERATE -> "Expect some activity. Headphones recommended"
                BusyLevel.HIGH -> "Busy period. Consider going later if crowds are difficult"
                BusyLevel.PEAK -> "Very crowded right now. Try again after 7pm for a calmer session"
            }
        )
    }
}
