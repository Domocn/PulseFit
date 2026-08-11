package com.pulsefit.app.util

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HyperfocusGuard @Inject constructor() {

    data class HyperfocusAlert(
        val shouldAlert: Boolean,
        val elapsedMinutes: Int,
        val thresholdMinutes: Int,
        val message: String,
        val severity: Severity
    )

    enum class Severity { GENTLE, FIRM }

    fun check(
        elapsedSeconds: Int,
        thresholdMinutes: Int,
        averageHeartRate: Int = 0,
        maxHeartRate: Int = 220
    ): HyperfocusAlert {
        val elapsedMinutes = elapsedSeconds / 60

        if (elapsedMinutes < thresholdMinutes) {
            return HyperfocusAlert(false, elapsedMinutes, thresholdMinutes, "", Severity.GENTLE)
        }

        val overMinutes = elapsedMinutes - thresholdMinutes
        val highStrain = averageHeartRate > (maxHeartRate * 0.8)

        val severity = if (overMinutes > 15 || highStrain) Severity.FIRM else Severity.GENTLE
        val message = when {
            highStrain -> "Your heart rate is high and you've been going for $elapsedMinutes minutes. Your body needs you to stop."
            overMinutes > 15 -> "You've been working out for $elapsedMinutes minutes. That's a lot. Time to wrap up and recover."
            else -> "You've been active for $elapsedMinutes minutes. Would you like to start cooling down?"
        }

        return HyperfocusAlert(
            shouldAlert = true,
            elapsedMinutes = elapsedMinutes,
            thresholdMinutes = thresholdMinutes,
            message = message,
            severity = severity
        )
    }
}
