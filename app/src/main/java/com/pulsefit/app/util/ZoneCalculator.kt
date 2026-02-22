package com.pulsefit.app.util

import com.pulsefit.app.data.model.HeartRateZone
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class ZoneThresholds(
    val warmUp: Int = 50,
    val active: Int = 60,
    val push: Int = 70,
    val peak: Int = 85
)

object ZoneCalculator {

    private val gson = Gson()

    fun getZone(
        heartRate: Int,
        maxHeartRate: Int,
        customThresholds: ZoneThresholds? = null
    ): HeartRateZone {
        val t = customThresholds ?: ZoneThresholds()
        val percentage = (heartRate.toFloat() / maxHeartRate * 100).toInt()
        return when {
            percentage >= t.peak -> HeartRateZone.PEAK
            percentage >= t.push -> HeartRateZone.PUSH
            percentage >= t.active -> HeartRateZone.ACTIVE
            percentage >= t.warmUp -> HeartRateZone.WARM_UP
            else -> HeartRateZone.REST
        }
    }

    fun getZonePercentage(heartRate: Int, maxHeartRate: Int): Int {
        return (heartRate.toFloat() / maxHeartRate * 100).toInt().coerceIn(0, 100)
    }

    fun parseThresholds(json: String?): ZoneThresholds? {
        if (json == null) return null
        return try {
            val type = object : TypeToken<ZoneThresholds>() {}.type
            gson.fromJson<ZoneThresholds>(json, type)
        } catch (_: Exception) {
            null
        }
    }

    fun toJson(thresholds: ZoneThresholds): String = gson.toJson(thresholds)
}
