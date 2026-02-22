package com.pulsefit.app.data.model

/**
 * Types of PulseFit challenges (inspired by OTF events, with safe/original names).
 */
enum class ChallengeType(val label: String) {
    GAUNTLET_WEEK("Gauntlet Week"),           // Hell Week equivalent
    TRIFECTA("The Trifecta"),                 // Dri-Tri equivalent
    SUMMIT_CLIMB("Summit Climb"),             // Orange Everest equivalent
    OUTRUN_THE_CLOCK("Outrun the Clock"),     // Catch Me If You Can equivalent
    MILEAGE_MONTH("Mileage Month"),           // Marathon Month equivalent
    OVERDRIVE_WEEK("Overdrive Week"),         // Mayhem equivalent
    THE_COUNTDOWN("The Countdown"),           // 12 Days of Fitness equivalent
    CIRCUIT_BLITZ("Circuit Blitz"),           // Tornado equivalent
    THE_LADDER("The Ladder"),                 // Progressive benchmark
    PR_WEEK("PR Week"),                       // Personal record benchmark
    EVOLUTION_CHALLENGE("Evolution Challenge"), // Transformation challenge
    TAG_TEAM("Tag Team Challenge"),           // Partner/group challenge
    DISTANCE_EXPEDITION("Distance Expedition"), // Cumulative distance challenge
    PULSE_CHECK("Pulse Check")               // Benchmark/assessment
}

/**
 * Duration type for challenges.
 */
enum class ChallengeDuration {
    SINGLE_SESSION,  // One workout (e.g. Trifecta, Summit Climb)
    MULTI_DAY,       // Several days (e.g. Gauntlet Week = 8 workouts in 8 days)
    WEEK,            // One week
    MONTH            // Full month (e.g. Mileage Month)
}

/**
 * What the challenge tracks/measures.
 */
enum class ChallengeMetric {
    WORKOUTS_COMPLETED,
    TOTAL_DISTANCE_MILES,
    TOTAL_BURN_POINTS,
    TOTAL_MINUTES,
    TOTAL_CALORIES,
    PEAK_ZONE_MINUTES,
    PERSONAL_RECORDS,
    ROWING_METERS
}

/**
 * A complete challenge definition with rules, goals, and theming.
 */
data class ChallengeDefinition(
    val type: ChallengeType,
    val name: String,
    val tagline: String,
    val description: String,
    val duration: ChallengeDuration,
    val durationDays: Int,
    val metric: ChallengeMetric,
    val defaultGoal: Int,
    val difficulty: Int,              // 1-5
    val isGroupChallenge: Boolean,
    val specialTemplateIds: List<String> = emptyList(),
    val badgeId: String? = null,
    val xpReward: Int = 0
)
