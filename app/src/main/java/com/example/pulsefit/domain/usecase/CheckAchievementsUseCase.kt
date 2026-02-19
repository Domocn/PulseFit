package com.example.pulsefit.domain.usecase

import com.example.pulsefit.data.local.dao.AchievementDao
import com.example.pulsefit.data.model.HeartRateZone
import com.example.pulsefit.domain.model.UserProfile
import com.example.pulsefit.domain.model.Workout
import javax.inject.Inject

class CheckAchievementsUseCase @Inject constructor(
    private val achievementDao: AchievementDao
) {
    suspend operator fun invoke(
        profile: UserProfile,
        workout: Workout
    ): List<String> {
        val unlocked = mutableListOf<String>()
        val now = System.currentTimeMillis()

        // First workout
        unlock("first_workout", profile.totalWorkouts >= 1, now)?.let { unlocked.add(it) }

        // Workout count milestones
        unlock("ten_workouts", profile.totalWorkouts >= 10, now)?.let { unlocked.add(it) }
        unlock("fifty_workouts", profile.totalWorkouts >= 50, now)?.let { unlocked.add(it) }
        unlock("hundred_workouts", profile.totalWorkouts >= 100, now)?.let { unlocked.add(it) }

        // Streak milestones
        unlock("streak_3", profile.currentStreak >= 3, now)?.let { unlocked.add(it) }
        unlock("streak_7", profile.currentStreak >= 7, now)?.let { unlocked.add(it) }
        unlock("streak_30", profile.currentStreak >= 30, now)?.let { unlocked.add(it) }

        // Burn point milestones
        unlock("burn_100", profile.totalBurnPoints >= 100, now)?.let { unlocked.add(it) }
        unlock("burn_1000", profile.totalBurnPoints >= 1000, now)?.let { unlocked.add(it) }

        // Level milestones
        unlock("level_5", profile.xpLevel >= 5, now)?.let { unlocked.add(it) }
        unlock("level_10", profile.xpLevel >= 10, now)?.let { unlocked.add(it) }

        // Zone time achievements (for current workout)
        val peakSeconds = workout.zoneTime[HeartRateZone.PEAK] ?: 0L
        val pushSeconds = workout.zoneTime[HeartRateZone.PUSH] ?: 0L
        unlock("peak_5min", peakSeconds >= 300, now)?.let { unlocked.add(it) }
        unlock("push_15min", pushSeconds >= 900, now)?.let { unlocked.add(it) }

        // F118: Hyperfocus badge - 15+ min sustained Push/Peak
        val pushPeakTotal = peakSeconds + pushSeconds
        unlock("hyperfocus", pushPeakTotal >= 900, now)?.let { unlocked.add(it) }

        // Just 5 Min achievement - started J5M and went 15+ min
        if (workout.isJustFiveMin && workout.durationSeconds >= 900) {
            unlock("just_five_min", true, now)?.let { unlocked.add(it) }
        }

        return unlocked
    }

    private suspend fun unlock(id: String, condition: Boolean, timestamp: Long): String? {
        if (!condition) return null
        val achievement = achievementDao.getById(id) ?: return null
        if (achievement.unlockedAt != null) return null // Already unlocked
        achievementDao.update(achievement.copy(unlockedAt = timestamp))
        return achievement.title
    }
}
