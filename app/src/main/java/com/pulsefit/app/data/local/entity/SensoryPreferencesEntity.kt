package com.pulsefit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pulsefit.app.data.model.AnimationLevel
import com.pulsefit.app.data.model.AppTheme
import com.pulsefit.app.data.model.CelebrationStyle
import com.pulsefit.app.data.model.ColourIntensity
import com.pulsefit.app.data.model.HapticLevel
import com.pulsefit.app.data.model.SoundLevel
import com.pulsefit.app.data.model.VoiceCoachStyle

@Entity(tableName = "sensory_preferences")
data class SensoryPreferencesEntity(
    @PrimaryKey val id: Int = 1,
    val animationLevel: AnimationLevel = AnimationLevel.FULL,
    val soundLevel: SoundLevel = SoundLevel.NORMAL,
    val hapticLevel: HapticLevel = HapticLevel.STRONG,
    val colourIntensity: ColourIntensity = ColourIntensity.STANDARD,
    val celebrationStyle: CelebrationStyle = CelebrationStyle.STANDARD,
    val voiceCoachStyle: VoiceCoachStyle = VoiceCoachStyle.STANDARD,
    val minimalMode: Boolean = false,
    val uiLocked: Boolean = false,
    val socialPressureShield: Boolean = false,
    val appTheme: AppTheme = AppTheme.MIDNIGHT
)
