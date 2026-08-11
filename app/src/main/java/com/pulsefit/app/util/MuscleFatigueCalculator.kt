package com.pulsefit.app.util

import com.pulsefit.app.data.local.entity.ExerciseLogEntity
import com.pulsefit.app.data.local.entity.MuscleFatigueEntity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Calculates muscle fatigue scores from recent exercise logs.
 *
 * Fatigue is derived from:
 * 1. Training volume (sets × reps × weight) for each muscle group
 * 2. Time elapsed since last training session
 * 3. A decay function that models recovery over 48-72 hours
 *
 * Score 0-100%: 0 = fully recovered, 100 = maximally fatigued
 */
@Singleton
class MuscleFatigueCalculator @Inject constructor() {

    companion object {
        const val DEFAULT_RECOVERY_HOURS = 48
        const val MAX_FATIGUE_SCORE = 100f
    }

    /**
     * Calculate fatigue for all muscle groups based on recent exercise logs.
     *
     * @param recentLogs Exercise logs from the last 7 days
     * @param existingFatigue Previously stored fatigue data (for decay calculation)
     * @return List of MuscleFatigueEntity with updated scores
     */
    fun calculate(
        recentLogs: List<ExerciseLogEntity>,
        existingFatigue: Map<String, MuscleFatigueEntity> = emptyMap()
    ): List<MuscleFatigueEntity> {
        val now = System.currentTimeMillis()
        val muscleGroups = recentLogs
            .map { it.primaryMuscleGroup }
            .distinct()

        return muscleGroups.map { muscleGroup ->
            val muscleLogs = recentLogs.filter { it.primaryMuscleGroup == muscleGroup }
            val totalVolume = muscleLogs.sumOf { it.totalVolumeKg.toDouble() }.toFloat()
            val lastTrainedAt = muscleLogs.maxOfOrNull { it.timestamp }
            val existing = existingFatigue[muscleGroup]

            val fatigueScore = calculateFatigueScore(
                totalVolume = totalVolume,
                lastTrainedAt = lastTrainedAt,
                now = now,
                previousScore = existing?.fatigueScore ?: 0f,
                previousLastTrainedAt = existing?.lastTrainedAt
            )

            MuscleFatigueEntity(
                muscleGroup = muscleGroup,
                fatigueScore = fatigueScore,
                lastTrainedAt = lastTrainedAt,
                lastVolumeKg = totalVolume,
                recoveryHoursNeeded = estimateRecoveryHours(fatigueScore),
                updatedAt = now
            )
        }
    }

    /**
     * Calculate fatigue score for a single muscle group.
     *
     * Formula:
     * 1. Base fatigue from volume: min(volume / 1000, 1.0) × 60
     * 2. Time decay: fatigue decays linearly over recovery window
     * 3. Final score = max(volumeFatigue × timeDecay, 0)
     */
    fun calculateFatigueScore(
        totalVolume: Float,
        lastTrainedAt: Long?,
        now: Long,
        previousScore: Float = 0f,
        previousLastTrainedAt: Long? = null
    ): Float {
        if (lastTrainedAt == null) return 0f

        // Base fatigue from volume (capped at 60% from volume alone)
        val volumeFatigue = (totalVolume / 1000f).coerceIn(0f, 1f) * 60f

        // Time decay: hours since last trained
        val hoursSinceLastTrained = (now - lastTrainedAt) / (1000f * 60f * 60f)
        val recoveryWindow = DEFAULT_RECOVERY_HOURS.toFloat()

        // Linear decay: 1.0 at time 0, 0.0 at recoveryWindow hours
        val timeDecay = (1f - (hoursSinceLastTrained / recoveryWindow)).coerceIn(0f, 1f)

        // Combine: volume fatigue decays over time
        val currentFatigue = volumeFatigue * timeDecay

        // Blend with previous score for smooth transitions (30% previous, 70% new)
        val blendedScore = if (previousScore > 0f) {
            previousScore * 0.3f + currentFatigue * 0.7f
        } else {
            currentFatigue
        }

        return blendedScore.coerceIn(0f, MAX_FATIGUE_SCORE)
    }

    /**
     * Estimate recovery hours needed based on current fatigue score.
     */
    fun estimateRecoveryHours(fatigueScore: Float): Int {
        return when {
            fatigueScore < 20f -> 12  // Light session, quick recovery
            fatigueScore < 40f -> 24  // Moderate session
            fatigueScore < 60f -> 36  // Hard session
            fatigueScore < 80f -> 48  // Very hard session
            else -> 72                // Maximum effort — full recovery needed
        }
    }

    /**
     * Get a human-readable recovery status label.
     */
    fun getRecoveryStatus(fatigueScore: Float): RecoveryStatus {
        return when {
            fatigueScore < 20f -> RecoveryStatus.READY
            fatigueScore < 40f -> RecoveryStatus.SLIGHTLY_FATIGUED
            fatigueScore < 60f -> RecoveryStatus.FATIGUED
            fatigueScore < 80f -> RecoveryStatus.VERY_FATIGUED
            else -> RecoveryStatus.OVERTRAINED
        }
    }

    enum class RecoveryStatus(val label: String, val colorHex: String) {
        READY("Ready to Train", "#22C55E"),
        SLIGHTLY_FATIGUED("Slightly Fatigued", "#84CC16"),
        FATIGUED("Fatigued — Light Work OK", "#F97316"),
        VERY_FATIGUED("Very Fatigued — Rest Recommended", "#EF4444"),
        OVERTRAINED("Overtrained — Rest Required", "#DC2626")
    }
}
