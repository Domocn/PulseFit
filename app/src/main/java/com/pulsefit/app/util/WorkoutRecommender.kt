package com.pulsefit.app.util

import com.pulsefit.app.data.model.TemplateCategory
import com.pulsefit.app.data.model.WorkoutTemplateData

data class RecommendedWorkout(
    val template: WorkoutTemplateData,
    val reason: String,
    val matchScore: Int
)

object WorkoutRecommender {

    /**
     * Recommend workouts based on energy level and readiness.
     *
     * @param energy Self-reported energy level (1-5)
     * @param readiness Readiness result (nullable)
     * @param templates Available workout templates
     * @return Top 3 recommended workouts with reasons
     */
    fun recommend(
        energy: Int,
        readiness: ReadinessResult?,
        templates: List<WorkoutTemplateData>
    ): List<RecommendedWorkout> {
        val readinessScore = readiness?.score ?: 50

        val scored = templates
            .filter { it.id != "free_run" }
            .map { template ->
                val score = scoreTemplate(template, energy, readinessScore)
                val reason = generateReason(template, energy, readinessScore)
                RecommendedWorkout(template, reason, score)
            }
            .sortedByDescending { it.matchScore }

        return scored.take(3)
    }

    private fun scoreTemplate(template: WorkoutTemplateData, energy: Int, readiness: Int): Int {
        var score = 50

        // Match difficulty to energy level
        val idealDifficulty = when (energy) {
            1 -> 1
            2 -> 1
            3 -> 2
            4 -> 3
            5 -> 4
            else -> 2
        }
        val diffDelta = kotlin.math.abs(template.difficulty - idealDifficulty)
        score -= diffDelta * 15

        // Match category to energy
        when {
            energy <= 2 && template.category == TemplateCategory.BEGINNER -> score += 20
            energy <= 2 && template.category == TemplateCategory.ADVANCED -> score -= 25
            energy >= 4 && template.category == TemplateCategory.ADVANCED -> score += 20
            energy >= 4 && template.category == TemplateCategory.BEGINNER -> score -= 10
            energy == 3 && template.category == TemplateCategory.STANDARD -> score += 15
        }

        // Match duration to energy
        when {
            energy <= 2 && template.durationMinutes <= 30 -> score += 10
            energy <= 2 && template.durationMinutes > 45 -> score -= 15
            energy >= 4 && template.durationMinutes >= 45 -> score += 10
        }

        // Low readiness shifts preference to easier workouts
        if (readiness < 40) {
            if (template.difficulty <= 2) score += 15
            if (template.difficulty >= 4) score -= 20
        }

        return score.coerceIn(0, 100)
    }

    private fun generateReason(template: WorkoutTemplateData, energy: Int, readiness: Int): String {
        return when {
            energy <= 2 && template.category == TemplateCategory.BEGINNER ->
                "Good match for low energy - gentle pace"
            energy <= 2 && template.durationMinutes <= 30 ->
                "Short session for when energy is low"
            energy == 3 && template.category == TemplateCategory.STANDARD ->
                "Balanced workout matching your energy"
            energy >= 4 && template.category == TemplateCategory.ADVANCED ->
                "Challenge yourself while your energy is high"
            energy >= 4 && template.category == TemplateCategory.SPECIALTY ->
                "Great time to try something different"
            readiness < 40 && template.difficulty <= 2 ->
                "Easy workout recommended - your body needs recovery"
            else -> "Matches your current energy level"
        }
    }
}
