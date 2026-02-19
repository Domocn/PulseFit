package com.example.pulsefit.util

import com.example.pulsefit.data.model.HeartRateZone

object CalorieCalculator {
    /**
     * Estimates calories burned using the Keytel formula.
     * Returns null if insufficient data.
     */
    fun estimate(
        avgHeartRate: Int,
        durationMinutes: Int,
        age: Int,
        weightKg: Float?,
        isMale: Boolean = true // simplified; can be expanded later
    ): Int? {
        val weight = weightKg ?: return null
        if (avgHeartRate <= 0 || durationMinutes <= 0) return null

        val calories = if (isMale) {
            ((-55.0969 + 0.6309 * avgHeartRate + 0.1988 * weight + 0.2017 * age) / 4.184) * durationMinutes
        } else {
            ((-20.4022 + 0.4472 * avgHeartRate - 0.1263 * weight + 0.074 * age) / 4.184) * durationMinutes
        }

        return calories.toInt().coerceAtLeast(0)
    }

    /**
     * Estimates excess post-exercise oxygen consumption (EPOC) in kcal.
     * Based on time spent in high-intensity zones.
     */
    fun estimateEpoc(
        zoneTime: Map<HeartRateZone, Long>,
        avgHeartRate: Int,
        durationMinutes: Int
    ): Int {
        if (durationMinutes <= 0) return 0
        val pushMinutes = (zoneTime[HeartRateZone.PUSH] ?: 0L) / 60.0
        val peakMinutes = (zoneTime[HeartRateZone.PEAK] ?: 0L) / 60.0
        // EPOC roughly 6-15% of total energy expenditure, higher for intense work
        val intensityFactor = (pushMinutes * 1.5 + peakMinutes * 3.0)
        return intensityFactor.toInt().coerceAtLeast(0)
    }
}
