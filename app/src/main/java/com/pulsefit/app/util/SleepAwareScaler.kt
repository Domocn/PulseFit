package com.pulsefit.app.util

data class SleepData(
    val totalHours: Float,
    val quality: String? // "GOOD", "FAIR", "POOR"
)

data class ScaledTarget(
    val adjustedTarget: Int,
    val reason: String,
    val scaleFactor: Float
)

object SleepAwareScaler {

    /**
     * Scale the daily burn point target based on sleep quality, resting HR, and readiness.
     *
     * @param baseDailyTarget The user's normal daily target
     * @param sleepData Last night's sleep data (from Health Connect or self-report)
     * @param restingHr Current resting heart rate
     * @param baselineRestingHr User's baseline resting HR for comparison
     * @param readinessScore Overall readiness score (0-100)
     */
    fun scaleWorkout(
        baseDailyTarget: Int,
        sleepData: SleepData?,
        restingHr: Int?,
        baselineRestingHr: Int?,
        readinessScore: Int?
    ): ScaledTarget {
        var scaleFactor = 1.0f
        val reasons = mutableListOf<String>()

        // Sleep-based scaling
        if (sleepData != null) {
            when {
                sleepData.totalHours < 5f || sleepData.quality == "POOR" -> {
                    scaleFactor *= 0.70f
                    if (sleepData.totalHours < 5f) {
                        reasons.add("you slept ${String.format("%.1f", sleepData.totalHours)} hours")
                    } else {
                        reasons.add("poor sleep quality")
                    }
                }
                sleepData.totalHours < 6.5f || sleepData.quality == "FAIR" -> {
                    scaleFactor *= 0.85f
                    reasons.add("light sleep (${String.format("%.1f", sleepData.totalHours)}h)")
                }
            }
        }

        // Elevated resting HR scaling
        if (restingHr != null && baselineRestingHr != null && baselineRestingHr > 0) {
            val hrElevation = (restingHr - baselineRestingHr).toFloat() / baselineRestingHr
            if (hrElevation > 0.15f) {
                scaleFactor *= 0.90f
                reasons.add("elevated resting HR")
            }
        }

        // Low readiness scaling
        if (readinessScore != null && readinessScore < 40) {
            scaleFactor *= 0.60f
            reasons.add("low readiness score")
        }

        val adjustedTarget = (baseDailyTarget * scaleFactor).toInt().coerceAtLeast(1)

        val reason = if (reasons.isEmpty()) {
            "Full target - you're well rested"
        } else {
            "Reduced - ${reasons.joinToString(", ")}"
        }

        return ScaledTarget(
            adjustedTarget = adjustedTarget,
            reason = reason,
            scaleFactor = scaleFactor
        )
    }
}
