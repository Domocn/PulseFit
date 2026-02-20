package com.pulsefit.app.domain.usecase

import com.pulsefit.app.domain.model.Workout
import com.pulsefit.app.domain.model.WorkoutStats
import com.pulsefit.app.domain.repository.WorkoutRepository
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class GetWorkoutStatsUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository
) {
    suspend fun getWeeklyStats(): WorkoutStats {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val weekStart = today.minusDays(today.dayOfWeek.value.toLong() - 1)
        val startMillis = weekStart.atStartOfDay(zone).toInstant().toEpochMilli()
        val endMillis = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()

        val workouts = workoutRepository.getWorkoutsInDateRange(startMillis, endMillis)
        return buildStats(workouts)
    }

    suspend fun getMonthlyStats(): WorkoutStats {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val monthStart = today.withDayOfMonth(1)
        val startMillis = monthStart.atStartOfDay(zone).toInstant().toEpochMilli()
        val endMillis = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()

        val workouts = workoutRepository.getWorkoutsInDateRange(startMillis, endMillis)
        return buildStats(workouts)
    }

    private fun buildStats(workouts: List<Workout>): WorkoutStats {
        return WorkoutStats(
            totalWorkouts = workouts.size,
            totalBurnPoints = workouts.sumOf { it.burnPoints },
            totalDurationSeconds = workouts.sumOf { it.durationSeconds },
            averageBurnPoints = if (workouts.isNotEmpty()) workouts.sumOf { it.burnPoints } / workouts.size else 0,
            averageDurationSeconds = if (workouts.isNotEmpty()) workouts.sumOf { it.durationSeconds } / workouts.size else 0
        )
    }
}
