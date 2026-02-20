package com.pulsefit.app.nd

import com.pulsefit.app.data.local.entity.SensoryPreferencesEntity
import com.pulsefit.app.data.model.AnimationLevel
import com.pulsefit.app.data.model.CelebrationStyle
import com.pulsefit.app.data.model.ColourIntensity
import com.pulsefit.app.data.model.HapticLevel
import com.pulsefit.app.data.model.NdProfile
import com.pulsefit.app.data.model.SoundLevel
import com.pulsefit.app.data.model.VoiceCoachStyle
import com.pulsefit.app.data.repository.SensoryPreferencesRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NdProfileManager @Inject constructor(
    private val sensoryPreferencesRepository: SensoryPreferencesRepository
) {
    suspend fun applyProfileDefaults(profile: NdProfile) {
        val defaults = getDefaultsForProfile(profile)
        sensoryPreferencesRepository.save(defaults)
    }

    fun getDefaultsForProfile(profile: NdProfile): SensoryPreferencesEntity {
        return when (profile) {
            NdProfile.ADHD -> SensoryPreferencesEntity(
                animationLevel = AnimationLevel.FULL,
                soundLevel = SoundLevel.NORMAL,
                hapticLevel = HapticLevel.STRONG,
                colourIntensity = ColourIntensity.VIVID,
                celebrationStyle = CelebrationStyle.OVERKILL,
                voiceCoachStyle = VoiceCoachStyle.HYPE,
                minimalMode = false,
                uiLocked = false,
                socialPressureShield = false
            )
            NdProfile.ASD -> SensoryPreferencesEntity(
                animationLevel = AnimationLevel.REDUCED,
                soundLevel = SoundLevel.QUIET,
                hapticLevel = HapticLevel.GENTLE,
                colourIntensity = ColourIntensity.MUTED,
                celebrationStyle = CelebrationStyle.CALM,
                voiceCoachStyle = VoiceCoachStyle.LITERAL,
                minimalMode = false,
                uiLocked = true,
                socialPressureShield = true
            )
            NdProfile.AUDHD -> {
                // AuDHD defaults to ASD comfort settings, surfaces ADHD options as toggles
                SensoryPreferencesEntity(
                    animationLevel = AnimationLevel.REDUCED,
                    soundLevel = SoundLevel.QUIET,
                    hapticLevel = HapticLevel.GENTLE,
                    colourIntensity = ColourIntensity.STANDARD,
                    celebrationStyle = CelebrationStyle.STANDARD,
                    voiceCoachStyle = VoiceCoachStyle.STANDARD,
                    minimalMode = false,
                    uiLocked = true,
                    socialPressureShield = false
                )
            }
            NdProfile.STANDARD -> SensoryPreferencesEntity()
        }
    }

    /**
     * For AuDHD: resolve conflicts between ADHD and ASD preferences.
     * Default to ASD comfort settings when in conflict.
     */
    fun resolveAuDhdConflict(
        adhdPref: SensoryPreferencesEntity,
        asdPref: SensoryPreferencesEntity
    ): SensoryPreferencesEntity {
        return asdPref.copy(
            // Allow ADHD celebration style if user explicitly chose it
            celebrationStyle = if (adhdPref.celebrationStyle == CelebrationStyle.OVERKILL)
                CelebrationStyle.STANDARD else asdPref.celebrationStyle
        )
    }
}
