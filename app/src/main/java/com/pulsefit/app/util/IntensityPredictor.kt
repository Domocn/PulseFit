package com.pulsefit.app.util

import com.pulsefit.app.data.local.entity.WorkoutEntity
import com.pulsefit.app.data.model.HeartRateZone
import com.pulsefit.app.data.model.WorkoutTemplateData
import com.pulsefit.app.domain.model.UserProfile

data class PredictedIntensity(
    val estimatedBurnPoints: IntRange,
    val estimatedCalories: IntRange,
    val estimatedAvgZone: HeartRateZone,
    val difficultyLabel: String
)

object IntensityPredictor {

    /**
     * Predict workout intensity based on template and user history.
     *
     * @param template The workout template to predict for
     * @param profile The user's profile
     * @param recentWorkouts Recent completed workouts for calibration
     */
    fun predict(
        template: WorkoutTemplateData,
        profile: UserProfile,
        recentWorkouts: List<WorkoutEntity>
    ): PredictedIntensity {
        // Filter workouts matching this template or similar category
        val matchingWorkouts = recentWorkouts.filter { it.templateId == template.id && it.endTime != null }
        val categoryWorkouts = recentWorkouts.filter { it.endTime != null }

        return if (matchingWorkouts.size >= 2) {
            predictFromHistory(matchingWorkouts, profile)
        } else {
            predictFromMetadata(template, profile, categoryWorkouts)
        }
    }

    private fun predictFromHistory(
        matchingWorkouts: List<WorkoutEntity>,
        profile: UserProfile
    ): PredictedIntensity {
        val avgBp = matchingWorkouts.map { it.burnPoints }.average().toInt()
        val avgCal = matchingWorkouts.mapNotNull { it.estimatedCalories }.average().toInt()
        val avgMaxHr = matchingWorkouts.map { it.maxHeartRate }.average().toInt()

        val bpRange = (avgBp * 0.8).toInt()..(avgBp * 1.2).toInt()
        val calRange = (avgCal * 0.85).toInt()..(avgCal * 1.15).toInt()

        val maxHr = MaxHrCalibrator.getEffectiveMaxHr(profile.calibratedMaxHr, profile.age)
        val avgZone = estimateZone(avgMaxHr, maxHr)
        val difficulty = getDifficultyLabel(avgBp, profile.dailyTarget)

        return PredictedIntensity(bpRange, calRange, avgZone, difficulty)
    }

    private fun predictFromMetadata(
        template: WorkoutTemplateData,
        profile: UserProfile,
        allWorkouts: List<WorkoutEntity>
    ): PredictedIntensity {
        // Estimate based on template metadata
        val baseBpPerMinute = when (template.difficulty) {
            1 -> 0.15f
            2 -> 0.3f
            3 -> 0.5f
            4 -> 0.7f
            5 -> 0.9f
            else -> 0.4f
        }

        val estimatedBp = (template.durationMinutes * baseBpPerMinute).toInt()
        val bpRange = (estimatedBp * 0.7).toInt()..(estimatedBp * 1.3).toInt()

        // Estimate calories from avg across all workouts or from template duration
        val avgCalPerMin = if (allWorkouts.isNotEmpty()) {
            allWorkouts.filter { it.durationSeconds > 0 && it.estimatedCalories != null }
                .map { it.estimatedCalories!!.toFloat() / (it.durationSeconds / 60f) }
                .takeIf { it.isNotEmpty() }
                ?.average()?.toFloat() ?: 8f
        } else 8f

        val estCal = (template.durationMinutes * avgCalPerMin).toInt()
        val calRange = (estCal * 0.8).toInt()..(estCal * 1.2).toInt()

        val estimatedZone = when (template.difficulty) {
            in 1..2 -> HeartRateZone.WARM_UP
            3 -> HeartRateZone.ACTIVE
            4 -> HeartRateZone.PUSH
            5 -> HeartRateZone.PEAK
            else -> HeartRateZone.ACTIVE
        }

        val difficulty = getDifficultyLabel(estimatedBp, profile.dailyTarget)

        return PredictedIntensity(bpRange, calRange, estimatedZone, difficulty)
    }

    private fun estimateZone(avgMaxHr: Int, maxHr: Int): HeartRateZone {
        if (maxHr == 0) return HeartRateZone.ACTIVE
        val pct = avgMaxHr.toFloat() / maxHr
        return when {
            pct >= 0.85f -> HeartRateZone.PEAK
            pct >= 0.70f -> HeartRateZone.PUSH
            pct >= 0.60f -> HeartRateZone.ACTIVE
            pct >= 0.50f -> HeartRateZone.WARM_UP
            else -> HeartRateZone.REST
        }
    }

    private fun getDifficultyLabel(estimatedBp: Int, dailyTarget: Int): String {
        if (dailyTarget == 0) return "Moderate"
        val ratio = estimatedBp.toFloat() / dailyTarget
        return when {
            ratio >= 1.5f -> "Very tough"
            ratio >= 1.0f -> "Challenging"
            ratio >= 0.5f -> "Moderate"
            else -> "Easy for you"
        }
    }
}
