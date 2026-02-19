package com.example.pulsefit.domain.usecase

import com.example.pulsefit.domain.repository.UserRepository
import com.example.pulsefit.domain.repository.WorkoutRepository
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class CalculateStreakUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Int {
        val days = workoutRepository.getWorkoutDays()
        if (days.isEmpty()) return 0

        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val todayEpochDay = today.toEpochDay()

        val workoutDays = days.map { it }.toSet()

        var streak = 0
        var checkDay = todayEpochDay

        // Allow today or yesterday as starting point
        if (checkDay !in workoutDays) {
            checkDay = todayEpochDay - 1
            if (checkDay !in workoutDays) {
                // Check if shield should save streak
                val profile = userRepository.getUserProfileOnce()
                return if (profile != null && !profile.streakShieldUsedThisWeek && profile.currentStreak > 0) {
                    userRepository.updateStreak(profile.currentStreak, shieldUsed = true)
                    profile.currentStreak
                } else {
                    userRepository.updateStreak(0, shieldUsed = false)
                    0
                }
            }
        }

        while (checkDay in workoutDays) {
            streak++
            checkDay--
        }

        userRepository.updateStreak(streak, shieldUsed = false)
        return streak
    }
}
