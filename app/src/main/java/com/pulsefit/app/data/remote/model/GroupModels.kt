package com.pulsefit.app.data.remote.model

data class WorkoutGroup(
    val id: String = "",
    val name: String = "",
    val type: GroupType = GroupType.FAMILY,
    val adminUid: String = "",
    val inviteCode: String = "",
    val createdAt: Long = 0L,
    val memberCount: Int = 0,
    val maxMembers: Int = 10,
    val description: String = ""
)

enum class GroupType { FAMILY, CORPORATE }

data class GroupMember(
    val uid: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val role: GroupRole = GroupRole.MEMBER,
    val joinedAt: Long = 0L,
    val department: String? = null
)

enum class GroupRole { ADMIN, MEMBER }

data class GroupEvent(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val scheduledAt: Long = 0L,
    val durationMinutes: Int = 30,
    val templateId: String? = null,
    val createdByUid: String = "",
    val rsvpList: List<String> = emptyList(),
    val status: String = "upcoming"
)

data class GroupChallenge(
    val id: String = "",
    val title: String = "",
    val goalType: ChallengeGoalType = ChallengeGoalType.TOTAL_WORKOUTS,
    val goalTarget: Int = 0,
    val currentProgress: Int = 0,
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val status: String = "active"
)

enum class ChallengeGoalType { TOTAL_WORKOUTS, TOTAL_BURN_POINTS, TOTAL_MINUTES }

data class GroupWeeklyStats(
    val totalWorkouts: Int = 0,
    val totalBurnPoints: Int = 0,
    val topPerformers: List<GroupMemberStat> = emptyList()
)

data class GroupMemberStat(
    val uid: String = "",
    val displayName: String = "",
    val workouts: Int = 0,
    val burnPoints: Int = 0
)
