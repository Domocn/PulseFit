package com.pulsefit.app.data.exercise

import com.pulsefit.app.data.model.ChallengeDefinition
import com.pulsefit.app.data.model.ChallengeDuration
import com.pulsefit.app.data.model.ChallengeMetric
import com.pulsefit.app.data.model.ChallengeType
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Registry of all PulseFit challenge definitions.
 * These are the "event" templates — actual instances are tracked in Firestore.
 */
@Singleton
class ChallengeRegistry @Inject constructor() {

    private val challenges: List<ChallengeDefinition> = listOf(

        // --- MULTI-DAY ENDURANCE CHALLENGES ---

        ChallengeDefinition(
            type = ChallengeType.GAUNTLET_WEEK,
            name = "Gauntlet Week",
            tagline = "8 workouts. 8 days. No excuses.",
            description = "Complete 8 workouts in 8 consecutive days. Each day features a specially designed high-intensity template that pushes your limits. Finish all 8 to earn the Gauntlet badge.",
            duration = ChallengeDuration.MULTI_DAY,
            durationDays = 8,
            metric = ChallengeMetric.WORKOUTS_COMPLETED,
            defaultGoal = 8,
            difficulty = 5,
            isGroupChallenge = false,
            specialTemplateIds = listOf(
                "gauntlet_day_1", "gauntlet_day_2", "gauntlet_day_3", "gauntlet_day_4",
                "gauntlet_day_5", "gauntlet_day_6", "gauntlet_day_7", "gauntlet_day_8"
            ),
            badgeId = "badge_gauntlet",
            xpReward = 500
        ),

        ChallengeDefinition(
            type = ChallengeType.OVERDRIVE_WEEK,
            name = "Overdrive Week",
            tagline = "Turn it up to 11.",
            description = "5 themed workouts in 7 days, each targeting a different energy system. Strength, power, endurance, speed, and all-out finisher.",
            duration = ChallengeDuration.WEEK,
            durationDays = 7,
            metric = ChallengeMetric.WORKOUTS_COMPLETED,
            defaultGoal = 5,
            difficulty = 4,
            isGroupChallenge = false,
            specialTemplateIds = listOf(
                "overdrive_strength", "overdrive_power", "overdrive_endurance",
                "overdrive_speed", "overdrive_finisher"
            ),
            badgeId = "badge_overdrive",
            xpReward = 350
        ),

        ChallengeDefinition(
            type = ChallengeType.THE_COUNTDOWN,
            name = "The Countdown",
            tagline = "12 days. 12 workouts. Each one counts.",
            description = "Complete a workout each day for 12 consecutive days. Workouts decrease in duration but increase in intensity as you approach the finish.",
            duration = ChallengeDuration.MULTI_DAY,
            durationDays = 12,
            metric = ChallengeMetric.WORKOUTS_COMPLETED,
            defaultGoal = 12,
            difficulty = 4,
            isGroupChallenge = false,
            badgeId = "badge_countdown",
            xpReward = 400
        ),

        // --- SINGLE-SESSION BENCHMARK CHALLENGES ---

        ChallengeDefinition(
            type = ChallengeType.TRIFECTA,
            name = "The Trifecta",
            tagline = "Row. Run. Floor. One shot.",
            description = "Complete a 2000m row, followed by a 5km tread run, followed by 300 floor reps — all in one session. Sprint format (half distances) available for beginners.",
            duration = ChallengeDuration.SINGLE_SESSION,
            durationDays = 1,
            metric = ChallengeMetric.WORKOUTS_COMPLETED,
            defaultGoal = 1,
            difficulty = 5,
            isGroupChallenge = false,
            specialTemplateIds = listOf("trifecta_full", "trifecta_sprint"),
            badgeId = "badge_trifecta",
            xpReward = 300
        ),

        ChallengeDefinition(
            type = ChallengeType.SUMMIT_CLIMB,
            name = "Summit Climb",
            tagline = "How high can you go?",
            description = "Progressive incline challenge on the tread. Start at 1% and climb 1% every minute. Your score is the highest incline you sustain for a full minute before stepping off.",
            duration = ChallengeDuration.SINGLE_SESSION,
            durationDays = 1,
            metric = ChallengeMetric.TOTAL_MINUTES,
            defaultGoal = 15,
            difficulty = 4,
            isGroupChallenge = false,
            specialTemplateIds = listOf("summit_climb"),
            badgeId = "badge_summit",
            xpReward = 200
        ),

        ChallengeDefinition(
            type = ChallengeType.OUTRUN_THE_CLOCK,
            name = "Outrun the Clock",
            tagline = "Stay ahead or get caught.",
            description = "A pace-based tread challenge. A virtual pacer starts at 4 MPH and accelerates every 2 minutes. Match or exceed the pace to stay in. Your score is total distance before the clock catches you.",
            duration = ChallengeDuration.SINGLE_SESSION,
            durationDays = 1,
            metric = ChallengeMetric.TOTAL_DISTANCE_MILES,
            defaultGoal = 3,
            difficulty = 4,
            isGroupChallenge = false,
            specialTemplateIds = listOf("outrun_the_clock"),
            badgeId = "badge_outrun",
            xpReward = 200
        ),

        ChallengeDefinition(
            type = ChallengeType.CIRCUIT_BLITZ,
            name = "Circuit Blitz",
            tagline = "Rotate. Recover. Repeat.",
            description = "Rapid station rotation: 3 minutes per station (tread, row, floor) cycling through 3 full rounds. Short transitions, high intensity, no rest.",
            duration = ChallengeDuration.SINGLE_SESSION,
            durationDays = 1,
            metric = ChallengeMetric.TOTAL_BURN_POINTS,
            defaultGoal = 20,
            difficulty = 3,
            isGroupChallenge = false,
            specialTemplateIds = listOf("circuit_blitz"),
            badgeId = "badge_blitz",
            xpReward = 150
        ),

        ChallengeDefinition(
            type = ChallengeType.THE_LADDER,
            name = "The Ladder",
            tagline = "Step up, every round.",
            description = "Progressive intervals: 30s all-out / 30s rest, then 45s / 30s, then 60s / 30s, building up to 2 minutes. Climb the full ladder to complete.",
            duration = ChallengeDuration.SINGLE_SESSION,
            durationDays = 1,
            metric = ChallengeMetric.PEAK_ZONE_MINUTES,
            defaultGoal = 10,
            difficulty = 4,
            isGroupChallenge = false,
            specialTemplateIds = listOf("the_ladder"),
            badgeId = "badge_ladder",
            xpReward = 200
        ),

        // --- MONTH-LONG CHALLENGES ---

        ChallengeDefinition(
            type = ChallengeType.MILEAGE_MONTH,
            name = "Mileage Month",
            tagline = "Every mile counts.",
            description = "Accumulate tread miles throughout the month. Track your total distance across all workouts. Bronze: 26.2 mi, Silver: 50 mi, Gold: 100 mi.",
            duration = ChallengeDuration.MONTH,
            durationDays = 30,
            metric = ChallengeMetric.TOTAL_DISTANCE_MILES,
            defaultGoal = 26,
            difficulty = 3,
            isGroupChallenge = false,
            badgeId = "badge_mileage",
            xpReward = 400
        ),

        ChallengeDefinition(
            type = ChallengeType.DISTANCE_EXPEDITION,
            name = "Distance Expedition",
            tagline = "Row your way across the map.",
            description = "Accumulate rowing meters over the month. Milestone checkpoints unlock at 10k, 25k, 50k, and 100k meters.",
            duration = ChallengeDuration.MONTH,
            durationDays = 30,
            metric = ChallengeMetric.ROWING_METERS,
            defaultGoal = 50000,
            difficulty = 3,
            isGroupChallenge = false,
            badgeId = "badge_expedition",
            xpReward = 400
        ),

        // --- BENCHMARK / ASSESSMENT ---

        ChallengeDefinition(
            type = ChallengeType.PULSE_CHECK,
            name = "Pulse Check",
            tagline = "Know your baseline.",
            description = "Quarterly benchmark workout: 12-minute tread distance, 500m row time, and floor rep count. Track your progress over time.",
            duration = ChallengeDuration.SINGLE_SESSION,
            durationDays = 1,
            metric = ChallengeMetric.WORKOUTS_COMPLETED,
            defaultGoal = 1,
            difficulty = 3,
            isGroupChallenge = false,
            specialTemplateIds = listOf("pulse_check"),
            badgeId = "badge_pulse_check",
            xpReward = 100
        ),

        ChallengeDefinition(
            type = ChallengeType.PR_WEEK,
            name = "PR Week",
            tagline = "Beat your best.",
            description = "One week focused on personal records. Each day targets a different PR: fastest mile, longest plank, max row watts, most burn points in a session, and more.",
            duration = ChallengeDuration.WEEK,
            durationDays = 7,
            metric = ChallengeMetric.PERSONAL_RECORDS,
            defaultGoal = 5,
            difficulty = 3,
            isGroupChallenge = false,
            badgeId = "badge_pr_week",
            xpReward = 300
        ),

        ChallengeDefinition(
            type = ChallengeType.EVOLUTION_CHALLENGE,
            name = "Evolution Challenge",
            tagline = "Transform in 30 days.",
            description = "A 30-day structured program: 4 workouts per week with progressive intensity. Benchmark at start and end to measure improvement.",
            duration = ChallengeDuration.MONTH,
            durationDays = 30,
            metric = ChallengeMetric.WORKOUTS_COMPLETED,
            defaultGoal = 16,
            difficulty = 3,
            isGroupChallenge = false,
            badgeId = "badge_evolution",
            xpReward = 500
        ),

        // --- GROUP CHALLENGES ---

        ChallengeDefinition(
            type = ChallengeType.TAG_TEAM,
            name = "Tag Team Challenge",
            tagline = "Stronger together.",
            description = "Group challenge: collectively complete a target number of workouts in one week. Every member's workout counts toward the group total.",
            duration = ChallengeDuration.WEEK,
            durationDays = 7,
            metric = ChallengeMetric.WORKOUTS_COMPLETED,
            defaultGoal = 20,
            difficulty = 2,
            isGroupChallenge = true,
            badgeId = "badge_tag_team",
            xpReward = 200
        )
    )

    fun getAll(): List<ChallengeDefinition> = challenges

    fun getByType(type: ChallengeType): ChallengeDefinition? =
        challenges.find { it.type == type }

    fun getSingleSession(): List<ChallengeDefinition> =
        challenges.filter { it.duration == ChallengeDuration.SINGLE_SESSION }

    fun getMultiDay(): List<ChallengeDefinition> =
        challenges.filter { it.duration != ChallengeDuration.SINGLE_SESSION }

    fun getGroupChallenges(): List<ChallengeDefinition> =
        challenges.filter { it.isGroupChallenge }

    fun getSoloChallenges(): List<ChallengeDefinition> =
        challenges.filter { !it.isGroupChallenge }

    fun getByDifficulty(maxDifficulty: Int): List<ChallengeDefinition> =
        challenges.filter { it.difficulty <= maxDifficulty }
}
