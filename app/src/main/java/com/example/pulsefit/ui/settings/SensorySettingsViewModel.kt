package com.example.pulsefit.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulsefit.data.local.entity.SensoryPreferencesEntity
import com.example.pulsefit.data.model.AnimationLevel
import com.example.pulsefit.data.model.AppTheme
import com.example.pulsefit.data.model.CelebrationStyle
import com.example.pulsefit.data.model.ColourIntensity
import com.example.pulsefit.data.model.HapticLevel
import com.example.pulsefit.data.model.SoundLevel
import com.example.pulsefit.data.model.VoiceCoachStyle
import com.example.pulsefit.data.repository.SensoryPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SensorySettingsViewModel @Inject constructor(
    private val sensoryPreferencesRepository: SensoryPreferencesRepository
) : ViewModel() {

    private val _preferences = MutableStateFlow<SensoryPreferencesEntity?>(null)
    val preferences: StateFlow<SensoryPreferencesEntity?> = _preferences

    init {
        viewModelScope.launch {
            sensoryPreferencesRepository.getPreferences().collect { _preferences.value = it }
        }
        viewModelScope.launch {
            // Ensure default prefs exist
            sensoryPreferencesRepository.getPreferencesOnce()
        }
    }

    private fun update(transform: (SensoryPreferencesEntity) -> SensoryPreferencesEntity) {
        viewModelScope.launch {
            val current = _preferences.value ?: sensoryPreferencesRepository.getPreferencesOnce()
            val updated = transform(current)
            sensoryPreferencesRepository.save(updated)
        }
    }

    fun updateAnimationLevel(level: AnimationLevel) = update { it.copy(animationLevel = level) }
    fun updateSoundLevel(level: SoundLevel) = update { it.copy(soundLevel = level) }
    fun updateHapticLevel(level: HapticLevel) = update { it.copy(hapticLevel = level) }
    fun updateColourIntensity(intensity: ColourIntensity) = update { it.copy(colourIntensity = intensity) }
    fun updateCelebrationStyle(style: CelebrationStyle) = update { it.copy(celebrationStyle = style) }
    fun updateVoiceCoachStyle(style: VoiceCoachStyle) = update { it.copy(voiceCoachStyle = style) }
    fun updateMinimalMode(enabled: Boolean) = update { it.copy(minimalMode = enabled) }
    fun updateUiLocked(locked: Boolean) = update { it.copy(uiLocked = locked) }
    fun updateSocialPressureShield(enabled: Boolean) = update { it.copy(socialPressureShield = enabled) }
    fun updateAppTheme(theme: AppTheme) = update { it.copy(appTheme = theme) }
}
