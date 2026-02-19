package com.example.pulsefit.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulsefit.data.model.AppTheme
import com.example.pulsefit.data.repository.SensoryPreferencesRepository
import com.example.pulsefit.domain.model.UserProfile
import com.example.pulsefit.domain.usecase.GetUserProfileUseCase
import com.example.pulsefit.domain.usecase.SaveUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getUserProfile: GetUserProfileUseCase,
    private val saveUserProfile: SaveUserProfileUseCase,
    private val sensoryPreferencesRepository: SensoryPreferencesRepository
) : ViewModel() {

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile

    private val _useSimulatedHr = MutableStateFlow(false)
    val useSimulatedHr: StateFlow<Boolean> = _useSimulatedHr

    private val _appTheme = MutableStateFlow(AppTheme.MIDNIGHT)
    val appTheme: StateFlow<AppTheme> = _appTheme

    init {
        viewModelScope.launch {
            getUserProfile().collect { _profile.value = it }
        }
        viewModelScope.launch {
            sensoryPreferencesRepository.getPreferences()
                .map { it?.appTheme ?: AppTheme.MIDNIGHT }
                .collect { _appTheme.value = it }
        }
    }

    fun updateName(name: String) {
        _profile.value?.let { p ->
            _profile.value = p.copy(name = name)
        }
    }

    fun updateAge(age: String) {
        val ageInt = age.toIntOrNull() ?: return
        _profile.value?.let { p ->
            _profile.value = p.copy(
                age = ageInt,
                maxHeartRate = 220 - ageInt
            )
        }
    }

    fun updateDailyTarget(target: Int) {
        _profile.value?.let { p ->
            _profile.value = p.copy(dailyTarget = target)
        }
    }

    fun toggleSimulatedHr() {
        _useSimulatedHr.value = !_useSimulatedHr.value
    }

    fun updateTheme(theme: AppTheme) {
        viewModelScope.launch {
            val current = sensoryPreferencesRepository.getPreferencesOnce()
            sensoryPreferencesRepository.save(current.copy(appTheme = theme))
        }
    }

    fun save() {
        viewModelScope.launch {
            _profile.value?.let { saveUserProfile(it) }
        }
    }
}
