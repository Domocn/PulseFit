package com.pulsefit.app.adhd

import com.pulsefit.app.domain.repository.WorkoutRepository
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AntiBurnoutSystem @Inject constructor(
    private val workoutRepository: WorkoutRepository
) {
    suspend fun shouldSuggestRestDay(): Boolean {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val sevenDaysAgo = today.minusDays(7)
        val startMillis = sevenDaysAgo.atStartOfDay(zone).toInstant().toEpochMilli()
        val endMillis = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()

        val recentWorkouts = workoutRepository.getWorkoutsInDateRange(startMillis, endMillis)
        val totalIntensityMinutes = recentWorkouts.sumOf { workout ->
            val pushPeakSeconds = workout.zoneTime
                .filterKeys { it == com.pulsefit.app.data.model.HeartRateZone.PUSH || it == com.pulsefit.app.data.model.HeartRateZone.PEAK }
                .values.sum()
            pushPeakSeconds / 60
        }

        // Suggest rest if >150 high-intensity minutes in 7 days or 6+ workouts
        return totalIntensityMinutes > 150 || recentWorkouts.size >= 6
    }

    suspend fun get7DayWorkoutCount(): Int {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val sevenDaysAgo = today.minusDays(7)
        val startMillis = sevenDaysAgo.atStartOfDay(zone).toInstant().toEpochMilli()
        val endMillis = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
        return workoutRepository.getWorkoutsInDateRange(startMillis, endMillis).size
    }
}
