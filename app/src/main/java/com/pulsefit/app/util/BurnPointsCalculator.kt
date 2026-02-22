package com.pulsefit.app.util

import com.pulsefit.app.data.model.HeartRateZone

object BurnPointsCalculator {

    fun calculatePointsForDuration(zone: HeartRateZone, durationSeconds: Long): Int {
        val minutes = durationSeconds / 60.0
        return (zone.pointsPerMinute * minutes).toInt()
    }

    fun calculateTotalPoints(zoneTime: Map<HeartRateZone, Long>): Int {
        return zoneTime.entries.sumOf { (zone, seconds) ->
            calculatePointsForDuration(zone, seconds)
        }
    }
}
