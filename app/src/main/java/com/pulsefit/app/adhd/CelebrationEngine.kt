package com.pulsefit.app.adhd

import com.pulsefit.app.data.model.CelebrationStyle
import com.pulsefit.app.data.model.NdProfile
import com.pulsefit.app.data.repository.SensoryPreferencesRepository
import javax.inject.Inject
import javax.inject.Singleton

enum class CelebrationType {
    WORKOUT_COMPLETE,
    LEVEL_UP,
    ACHIEVEMENT_UNLOCKED,
    QUEST_COMPLETE,
    PERSONAL_RECORD
}

data class CelebrationConfig(
    val style: CelebrationStyle,
    val durationMs: Long,
    val showConfetti: Boolean,
    val showGlow: Boolean,
    val playSound: Boolean
)

@Singleton
class CelebrationEngine @Inject constructor(
    private val sensoryPreferencesRepository: SensoryPreferencesRepository
) {
    suspend fun getCelebrationConfig(
        type: CelebrationType,
        ndProfile: NdProfile
    ): CelebrationConfig {
        val prefs = sensoryPreferencesRepository.getPreferencesOnce()
        val style = prefs.celebrationStyle

        return when (style) {
            CelebrationStyle.CALM -> CelebrationConfig(
                style = style,
                durationMs = 1500,
                showConfetti = false,
                showGlow = true,
                playSound = prefs.soundLevel != com.pulsefit.app.data.model.SoundLevel.OFF
            )
            CelebrationStyle.STANDARD -> CelebrationConfig(
                style = style,
                durationMs = 3000,
                showConfetti = type == CelebrationType.LEVEL_UP || type == CelebrationType.PERSONAL_RECORD,
                showGlow = true,
                playSound = prefs.soundLevel != com.pulsefit.app.data.model.SoundLevel.OFF
            )
            CelebrationStyle.OVERKILL -> CelebrationConfig(
                style = style,
                durationMs = 5000,
                showConfetti = true,
                showGlow = true,
                playSound = true
            )
        }
    }
}
