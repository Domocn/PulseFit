package com.example.pulsefit.domain.usecase

import com.example.pulsefit.domain.repository.UserRepository
import javax.inject.Inject

class AwardXpUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    companion object {
        private val LEVEL_THRESHOLDS = generateSequence(100L) { it + (it * 0.15).toLong() + 50 }
            .take(100)
            .toList()

        fun xpForLevel(level: Int): Long = LEVEL_THRESHOLDS.getOrElse(level - 1) { Long.MAX_VALUE }

        fun levelForTotalXp(totalXp: Long): Int {
            var accumulated = 0L
            for (i in LEVEL_THRESHOLDS.indices) {
                accumulated += LEVEL_THRESHOLDS[i]
                if (totalXp < accumulated) return i + 1
            }
            return LEVEL_THRESHOLDS.size
        }
    }

    suspend operator fun invoke(burnPoints: Int, streakMultiplier: Float = 1f): Int {
        val baseXp = (burnPoints * 10 * streakMultiplier).toInt()
        val profile = userRepository.getUserProfileOnce() ?: return baseXp
        val newTotalXp = profile.totalXp + baseXp
        val newLevel = levelForTotalXp(newTotalXp)
        userRepository.updateXp(baseXp.toLong(), newLevel)
        return baseXp
    }
}
