package com.pulsefit.app.util

import com.pulsefit.app.data.model.WorkoutTemplateData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpoonCalculator @Inject constructor() {

    fun costForTemplate(template: WorkoutTemplateData): Float {
        val durationFactor = template.durationMinutes / 15f
        val difficultyFactor = template.difficulty / 3f
        return (durationFactor * difficultyFactor).coerceIn(0.5f, 8f)
    }

    fun costForMicro(): Float = 0.5f

    fun costDescription(cost: Float): String = when {
        cost <= 1f -> "Very light"
        cost <= 2f -> "Light"
        cost <= 4f -> "Moderate"
        cost <= 6f -> "Heavy"
        else -> "Very demanding"
    }
}
