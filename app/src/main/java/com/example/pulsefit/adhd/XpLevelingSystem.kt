package com.example.pulsefit.adhd

import com.example.pulsefit.domain.usecase.AwardXpUseCase

object XpLevelingSystem {
    fun xpForLevel(level: Int): Long = AwardXpUseCase.xpForLevel(level)

    fun levelForTotalXp(totalXp: Long): Int = AwardXpUseCase.levelForTotalXp(totalXp)

    fun xpProgressInLevel(totalXp: Long): Float {
        val level = levelForTotalXp(totalXp)
        var accumulated = 0L
        for (i in 0 until level - 1) {
            accumulated += xpForLevel(i + 1)
        }
        val xpInCurrentLevel = totalXp - accumulated
        val xpNeeded = xpForLevel(level)
        return (xpInCurrentLevel.toFloat() / xpNeeded).coerceIn(0f, 1f)
    }
}
