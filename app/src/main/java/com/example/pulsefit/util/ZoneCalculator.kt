package com.example.pulsefit.util

import com.example.pulsefit.data.model.HeartRateZone

object ZoneCalculator {

    fun getZone(heartRate: Int, maxHeartRate: Int): HeartRateZone {
        val percentage = (heartRate.toFloat() / maxHeartRate * 100).toInt()
        return when {
            percentage >= 85 -> HeartRateZone.PEAK
            percentage >= 70 -> HeartRateZone.PUSH
            percentage >= 60 -> HeartRateZone.ACTIVE
            percentage >= 50 -> HeartRateZone.WARM_UP
            else -> HeartRateZone.REST
        }
    }

    fun getZonePercentage(heartRate: Int, maxHeartRate: Int): Int {
        return (heartRate.toFloat() / maxHeartRate * 100).toInt().coerceIn(0, 100)
    }
}
