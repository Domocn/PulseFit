package com.pulsefit.app.util

import com.pulsefit.app.data.local.entity.WorkoutEntity
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

data class OverloadTrend(
    val avgBurnPointsTrend: List<Float>,
    val avgDurationTrend: List<Float>,
    val avgMaxHrTrend: List<Float>,
    val plateauDetected: Boolean,
    val recommendation: String?
)

@Singleton
class OverloadTracker @Inject constructor() {

    /**
     * Analyze progressive cardio overload from workout history.
     * Groups workouts by week, calculates rolling averages for last 8 weeks.
     * Detects plateaus and suggests progression or deload.
     */
    fun analyze(workouts: List<WorkoutEntity>): OverloadTrend? {
        if (workouts.isEmpty()) return null

        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)

        // Group workouts into 8 weekly buckets
        val weeklyBuckets = (0 until 8).map { weekOffset ->
            val weekStart = today.minusWeeks((7 - weekOffset).toLong())
                .with(java.time.DayOfWeek.MONDAY)
            val weekEnd = weekStart.plusDays(7)
            val startMillis = weekStart.atStartOfDay(zone).toInstant().toEpochMilli()
            val endMillis = weekEnd.atStartOfDay(zone).toInstant().toEpochMilli()

            workouts.filter { it.startTime in startMillis until endMillis && it.endTime != null }
        }

        val avgBpTrend = weeklyBuckets.map { bucket ->
            if (bucket.isEmpty()) 0f else bucket.map { it.burnPoints.toFloat() }.average().toFloat()
        }
        val avgDurationTrend = weeklyBuckets.map { bucket ->
            if (bucket.isEmpty()) 0f else bucket.map { it.durationSeconds.toFloat() }.average().toFloat()
        }
        val avgMaxHrTrend = weeklyBuckets.map { bucket ->
            if (bucket.isEmpty()) 0f else bucket.map { it.maxHeartRate.toFloat() }.average().toFloat()
        }

        // Detect plateau: last 3 weeks' avg burn points within 5% of each other
        val plateauDetected = detectPlateau(avgBpTrend)

        val recommendation = generateRecommendation(avgBpTrend, plateauDetected)

        return OverloadTrend(
            avgBurnPointsTrend = avgBpTrend,
            avgDurationTrend = avgDurationTrend,
            avgMaxHrTrend = avgMaxHrTrend,
            plateauDetected = plateauDetected,
            recommendation = recommendation
        )
    }

    private fun detectPlateau(avgBpTrend: List<Float>): Boolean {
        val recent = avgBpTrend.takeLast(3).filter { it > 0f }
        if (recent.size < 3) return false

        val avg = recent.average().toFloat()
        if (avg == 0f) return false

        return recent.all { kotlin.math.abs(it - avg) / avg < 0.05f }
    }

    private fun generateRecommendation(avgBpTrend: List<Float>, plateauDetected: Boolean): String? {
        val nonZero = avgBpTrend.filter { it > 0f }
        if (nonZero.size < 2) return null

        val recent = avgBpTrend.takeLast(3).filter { it > 0f }
        val earlier = avgBpTrend.dropLast(3).filter { it > 0f }

        if (recent.isEmpty() || earlier.isEmpty()) return null

        val recentAvg = recent.average()
        val earlierAvg = earlier.average()

        return when {
            plateauDetected -> "Your burn points have been consistent - try increasing intensity or duration to break through"
            recentAvg > earlierAvg * 1.15 -> "Great progress! Your cardio fitness is clearly improving"
            recentAvg < earlierAvg * 0.85 -> "Your recent workouts are lighter - consider a deload week if you're feeling fatigued"
            recentAvg > earlierAvg -> "Steady improvement - keep building at this pace"
            else -> null
        }
    }
}
