package com.pulsefit.app.asd

import com.pulsefit.app.data.model.HeartRateZone
import javax.inject.Inject

class PredictableRewards @Inject constructor() {
    /**
     * For ASD profiles: show exact expected points before workout starts.
     * No hidden bonuses, no random drops. Running total always matches final.
     */
    fun estimatePointsForDuration(durationMinutes: Int, targetZone: HeartRateZone): Int {
        return targetZone.pointsPerMinute * durationMinutes
    }

    fun formatEstimate(durationMinutes: Int, targetZone: HeartRateZone): String {
        val points = estimatePointsForDuration(durationMinutes, targetZone)
        return "Estimated: $points burn points (${targetZone.label} for $durationMinutes min)"
    }
}
