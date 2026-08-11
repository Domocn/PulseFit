package com.pulsefit.app.util

import com.pulsefit.app.data.exercise.TemplateRegistry
import com.pulsefit.app.data.model.TemplateCategory
import com.pulsefit.app.data.model.WorkoutTemplateData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DecisionEngine @Inject constructor(
    private val templateRegistry: TemplateRegistry
) {

    data class Decision(
        val template: WorkoutTemplateData,
        val reason: String
    )

    fun decide(
        readinessScore: Int = 50,
        energyLevel: Int = 3,
        availableMinutes: Int = 30,
        recentTemplateIds: List<String> = emptyList()
    ): Decision {
        val candidates = templateRegistry.getAll()
            .filter { it.durationMinutes in 1..availableMinutes }
            .filter { it.id !in recentTemplateIds.take(3) }

        if (candidates.isEmpty()) {
            val fallback = templateRegistry.getById("gentle_start")
                ?: templateRegistry.getAll().first()
            return Decision(fallback, "Only option that fits your time")
        }

        val scored = candidates.map { template ->
            val difficultyFit = when {
                readinessScore < 30 && template.difficulty <= 1 -> 10
                readinessScore < 50 && template.difficulty <= 2 -> 8
                readinessScore in 50..70 && template.difficulty in 2..3 -> 10
                readinessScore > 70 && template.difficulty >= 3 -> 10
                else -> 3
            }
            val energyFit = when {
                energyLevel <= 2 && template.category in listOf(TemplateCategory.RECOVERY, TemplateCategory.BEGINNER) -> 10
                energyLevel == 3 && template.category == TemplateCategory.STANDARD -> 8
                energyLevel >= 4 && template.category in listOf(TemplateCategory.ADVANCED, TemplateCategory.OTF_STYLE) -> 10
                else -> 4
            }
            val durationFit = if (template.durationMinutes <= availableMinutes) 5 else 0
            template to (difficultyFit + energyFit + durationFit)
        }

        val best = scored.maxByOrNull { it.second }?.first ?: candidates.first()

        val reason = when {
            readinessScore < 30 -> "Your body needs something gentle today"
            energyLevel <= 2 -> "Matched to your current energy"
            readinessScore > 70 && energyLevel >= 4 -> "You're ready to push it"
            else -> "Good fit for how you're feeling"
        }

        return Decision(best, reason)
    }
}
