package com.pulsefit.app.util

data class ReadinessFactor(
    val name: String,
    val contribution: Int,
    val status: String
)

data class ReadinessResult(
    val score: Int,
    val label: String,
    val factors: List<ReadinessFactor>
)

object ReadinessCalculator {

    /**
     * Calculate a readiness score (0-100) based on multiple recovery factors.
     *
     * Weights:
     * - Sleep: 30% (hours + quality)
     * - Resting HR: 20% (deviation from baseline)
     * - HRR: 15% (last recovery score)
     * - Recovery time: 15% (days since last workout)
     * - Training load: 20% (7-day acute vs 28-day chronic)
     */
    fun calculate(
        sleepHours: Float?,
        sleepQuality: String?,
        restingHr: Int?,
        baselineRestingHr: Int?,
        lastHrr: Float?,
        daysSinceLastWorkout: Int,
        recentWorkloadScore: Float
    ): ReadinessResult {
        val factors = mutableListOf<ReadinessFactor>()

        // Sleep score (0-100, weight 30%)
        val sleepScore = calculateSleepScore(sleepHours, sleepQuality)
        val sleepStatus = when {
            sleepScore >= 80 -> "Good"
            sleepScore >= 50 -> "Fair"
            sleepScore > 0 -> "Poor"
            else -> "No data"
        }
        factors.add(ReadinessFactor("Sleep", (sleepScore * 0.30f).toInt(), sleepStatus))

        // Resting HR score (0-100, weight 20%)
        val rhrScore = calculateRhrScore(restingHr, baselineRestingHr)
        val rhrStatus = when {
            rhrScore >= 80 -> "Normal"
            rhrScore >= 50 -> "Slightly elevated"
            rhrScore > 0 -> "Elevated"
            else -> "No data"
        }
        factors.add(ReadinessFactor("Resting HR", (rhrScore * 0.20f).toInt(), rhrStatus))

        // HRR score (0-100, weight 15%)
        val hrrScore = calculateHrrScore(lastHrr)
        val hrrStatus = when {
            hrrScore >= 80 -> "Excellent"
            hrrScore >= 50 -> "Good"
            hrrScore > 0 -> "Fair"
            else -> "No data"
        }
        factors.add(ReadinessFactor("Recovery", (hrrScore * 0.15f).toInt(), hrrStatus))

        // Recovery time score (0-100, weight 15%)
        val recoveryScore = calculateRecoveryTimeScore(daysSinceLastWorkout)
        val recoveryStatus = when {
            recoveryScore >= 80 -> "Well rested"
            recoveryScore >= 50 -> "Adequate"
            recoveryScore > 0 -> "May need rest"
            else -> "No data"
        }
        factors.add(ReadinessFactor("Rest days", (recoveryScore * 0.15f).toInt(), recoveryStatus))

        // Training load score (0-100, weight 20%)
        val loadScore = calculateLoadScore(recentWorkloadScore)
        val loadStatus = when {
            loadScore >= 80 -> "Balanced"
            loadScore >= 50 -> "Moderate"
            loadScore > 0 -> "High"
            else -> "No data"
        }
        factors.add(ReadinessFactor("Training load", (loadScore * 0.20f).toInt(), loadStatus))

        val totalScore = factors.sumOf { it.contribution }.coerceIn(0, 100)

        val label = when {
            totalScore >= 80 -> "Primed"
            totalScore >= 60 -> "Ready"
            totalScore >= 40 -> "Moderate"
            totalScore >= 20 -> "Fatigued"
            else -> "Rest"
        }

        return ReadinessResult(score = totalScore, label = label, factors = factors)
    }

    private fun calculateSleepScore(hours: Float?, quality: String?): Int {
        if (hours == null && quality == null) return 50 // neutral default

        var score = 50
        if (hours != null) {
            score = when {
                hours >= 7.5f -> 100
                hours >= 7f -> 85
                hours >= 6.5f -> 70
                hours >= 6f -> 55
                hours >= 5f -> 35
                else -> 15
            }
        }
        if (quality != null) {
            val qualityModifier = when (quality) {
                "GOOD" -> 1.0f
                "FAIR" -> 0.8f
                "POOR" -> 0.5f
                else -> 0.9f
            }
            score = (score * qualityModifier).toInt()
        }
        return score.coerceIn(0, 100)
    }

    private fun calculateRhrScore(restingHr: Int?, baselineRestingHr: Int?): Int {
        if (restingHr == null || baselineRestingHr == null || baselineRestingHr == 0) return 50

        val deviation = (restingHr - baselineRestingHr).toFloat() / baselineRestingHr
        return when {
            deviation <= 0f -> 100
            deviation <= 0.05f -> 85
            deviation <= 0.10f -> 65
            deviation <= 0.15f -> 45
            deviation <= 0.20f -> 25
            else -> 10
        }
    }

    private fun calculateHrrScore(lastHrr: Float?): Int {
        if (lastHrr == null) return 50
        return when {
            lastHrr > 20 -> 100
            lastHrr > 16 -> 80
            lastHrr > 12 -> 60
            lastHrr > 8 -> 40
            else -> 20
        }
    }

    private fun calculateRecoveryTimeScore(daysSinceLastWorkout: Int): Int {
        return when (daysSinceLastWorkout) {
            0 -> 30   // same day
            1 -> 70   // 1 day rest
            2 -> 100  // 2 days rest - optimal
            3 -> 90   // 3 days
            in 4..7 -> 70  // starting to lose fitness
            else -> 50
        }
    }

    private fun calculateLoadScore(recentWorkloadScore: Float): Int {
        // recentWorkloadScore represents acute:chronic training load ratio
        // Ideal is 0.8-1.3 (sweet spot for fitness improvement without overtraining)
        return when {
            recentWorkloadScore == 0f -> 50 // no data
            recentWorkloadScore < 0.5f -> 80  // under-training, well recovered
            recentWorkloadScore in 0.5f..0.8f -> 90
            recentWorkloadScore in 0.8f..1.3f -> 100 // sweet spot
            recentWorkloadScore in 1.3f..1.5f -> 60
            recentWorkloadScore > 1.5f -> 30 // overreaching
            else -> 50
        }
    }
}
