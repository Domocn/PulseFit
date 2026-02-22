package com.pulsefit.app.data.remote.model

import com.pulsefit.app.data.model.ChallengeMetric
import com.pulsefit.app.data.model.ChallengeType

/**
 * A user's active or completed challenge instance, stored in Firestore.
 * Path: users/{uid}/challenges/{challengeId}
 */
data class UserChallenge(
    val id: String = "",
    val challengeType: ChallengeType = ChallengeType.PULSE_CHECK,
    val name: String = "",
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val metric: ChallengeMetric = ChallengeMetric.WORKOUTS_COMPLETED,
    val goalTarget: Int = 0,
    val currentProgress: Int = 0,
    val status: ChallengeStatus = ChallengeStatus.ACTIVE,
    val milestones: List<ChallengeMilestone> = emptyList(),
    val calendarEventId: String? = null,
    val xpAwarded: Int = 0,
    val badgeAwarded: Boolean = false,
    val completedAt: Long? = null
)

enum class ChallengeStatus {
    ACTIVE,
    COMPLETED,
    FAILED,
    ABANDONED
}

data class ChallengeMilestone(
    val label: String = "",
    val target: Int = 0,
    val reached: Boolean = false,
    val reachedAt: Long? = null
)
