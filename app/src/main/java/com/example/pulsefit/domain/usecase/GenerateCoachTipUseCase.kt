package com.example.pulsefit.domain.usecase

import com.example.pulsefit.data.model.HeartRateZone
import com.example.pulsefit.domain.model.Workout
import com.example.pulsefit.domain.repository.WorkoutRepository
import javax.inject.Inject

class GenerateCoachTipUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository
) {
    suspend operator fun invoke(currentWorkout: Workout, dailyTarget: Int): String? {
        val history = workoutRepository.getCompletedWorkouts()

        // First workout → welcome tip
        if (history.size <= 1) {
            return "Welcome to PulseFit! Every workout earns Burn Points. " +
                    "Push and Peak zones earn the most. Keep showing up!"
        }

        val totalSeconds = currentWorkout.zoneTime.values.sum().toFloat().coerceAtLeast(1f)

        // >80% time in one zone → suggest variety
        for (zone in HeartRateZone.entries) {
            val zoneSeconds = currentWorkout.zoneTime[zone] ?: 0L
            if (zoneSeconds / totalSeconds > 0.80f && zone != HeartRateZone.REST) {
                return "You spent most of this workout in ${zone.label}. " +
                        "Try mixing in some intervals to work different zones."
            }
        }

        // No Push/Peak time → suggest intervals
        val pushPeakSeconds = (currentWorkout.zoneTime[HeartRateZone.PUSH] ?: 0L) +
                (currentWorkout.zoneTime[HeartRateZone.PEAK] ?: 0L)
        if (pushPeakSeconds == 0L && totalSeconds > 300) {
            return "No time in Push or Peak zones today. " +
                    "Try adding short high-intensity bursts for more Burn Points."
        }

        // 5+ consecutive Push-heavy workouts → suggest recovery
        val recentWorkouts = history.takeLast(5)
        if (recentWorkouts.size >= 5) {
            val allPushHeavy = recentWorkouts.all { w ->
                val wTotal = w.zoneTime.values.sum().toFloat().coerceAtLeast(1f)
                val wPushPeak = ((w.zoneTime[HeartRateZone.PUSH] ?: 0L) +
                        (w.zoneTime[HeartRateZone.PEAK] ?: 0L)).toFloat()
                wPushPeak / wTotal > 0.4f
            }
            if (allPushHeavy) {
                return "You've been pushing hard in your last 5 workouts. " +
                        "Consider a lighter recovery session next time."
            }
        }

        // Target hit in <20 min → suggest increasing daily target
        if (currentWorkout.burnPoints >= dailyTarget && currentWorkout.durationSeconds < 1200) {
            return "You hit your daily target in under 20 minutes! " +
                    "Consider increasing your target in Settings for a bigger challenge."
        }

        // Avg HR trending down at same effort → "fitness improving"
        if (history.size >= 5) {
            val recent3 = history.takeLast(3)
            val older3 = history.dropLast(3).takeLast(3)
            if (recent3.size >= 3 && older3.size >= 3) {
                val recentAvgHr = recent3.map { it.averageHeartRate }.average()
                val olderAvgHr = older3.map { it.averageHeartRate }.average()
                if (recentAvgHr > 0 && olderAvgHr > 0 && recentAvgHr < olderAvgHr * 0.95) {
                    return "Your average heart rate is trending down at similar effort levels. " +
                            "Your fitness is improving!"
                }
            }
        }

        return null
    }
}
