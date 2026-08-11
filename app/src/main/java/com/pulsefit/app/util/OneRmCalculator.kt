package com.pulsefit.app.util

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Calculates estimated 1RM (one-rep max) from submaximal sets.
 * Supports multiple formulas. Default is Brzycki (most accurate for 1-10 rep range).
 */
@Singleton
class OneRmCalculator @Inject constructor() {

    /**
     * Brzycki formula: 1RM = weight × (36 / (37 - reps))
     * Most accurate for 1-10 rep range.
     */
    fun brzycki(weightKg: Float, reps: Int): Float {
        if (reps <= 0 || reps >= 37) return weightKg
        return weightKg * (36f / (37f - reps))
    }

    /**
     * Epley formula: 1RM = weight × (1 + reps/30)
     * Good for higher rep ranges (5-15).
     */
    fun epley(weightKg: Float, reps: Int): Float {
        if (reps <= 0) return weightKg
        return weightKg * (1f + reps / 30f)
    }

    /**
     * Lombardi formula: 1RM = weight × reps^0.10
     */
    fun lombardi(weightKg: Float, reps: Int): Float {
        if (reps <= 0) return weightKg
        return weightKg * Math.pow(reps.toDouble(), 0.10).toFloat()
    }

    /**
     * Calculate 1RM using the default Brzycki formula.
     * Returns null if reps is 1 (already at 1RM) or invalid.
     */
    fun calculate(weightKg: Float, reps: Int, formula: String = "Brzycki"): Float? {
        if (reps <= 0) return null
        if (reps == 1) return weightKg // Already a 1RM

        return when (formula) {
            "Epley" -> epley(weightKg, reps)
            "Lombardi" -> lombardi(weightKg, reps)
            else -> brzycki(weightKg, reps)
        }
    }

    /**
     * Strength level classification based on 1RM relative to bodyweight.
     * Returns a label: Beginner, Novice, Intermediate, Advanced, Elite.
     */
    fun classifyStrengthLevel(
        exerciseId: String,
        oneRmKg: Float,
        bodyWeightKg: Float?
    ): String {
        if (bodyWeightKg == null || bodyWeightKg <= 0) return "Unknown"
        val ratio = oneRmKg / bodyWeightKg

        // Bench press standards (approximate)
        return when {
            exerciseId.startsWith("chest_") -> when {
                ratio < 0.75 -> "Beginner"
                ratio < 1.0 -> "Novice"
                ratio < 1.25 -> "Intermediate"
                ratio < 1.5 -> "Advanced"
                else -> "Elite"
            }
            exerciseId.startsWith("legs_squat") || exerciseId.startsWith("legs_front_squat") -> when {
                ratio < 1.0 -> "Beginner"
                ratio < 1.5 -> "Novice"
                ratio < 2.0 -> "Intermediate"
                ratio < 2.5 -> "Advanced"
                else -> "Elite"
            }
            exerciseId.startsWith("back_deadlift") -> when {
                ratio < 1.25 -> "Beginner"
                ratio < 1.75 -> "Novice"
                ratio < 2.25 -> "Intermediate"
                ratio < 2.75 -> "Advanced"
                else -> "Elite"
            }
            exerciseId.startsWith("shoulders_ohp") -> when {
                ratio < 0.5 -> "Beginner"
                ratio < 0.75 -> "Novice"
                ratio < 1.0 -> "Intermediate"
                ratio < 1.25 -> "Advanced"
                else -> "Elite"
            }
            else -> when {
                ratio < 0.5 -> "Beginner"
                ratio < 0.75 -> "Novice"
                ratio < 1.0 -> "Intermediate"
                ratio < 1.25 -> "Advanced"
                else -> "Elite"
            }
        }
    }
}
