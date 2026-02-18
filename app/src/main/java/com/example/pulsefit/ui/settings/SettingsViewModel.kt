package com.example.pulsefit.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulsefit.domain.model.UserProfile
import com.example.pulsefit.domain.usecase.GetUserProfileUseCase
import com.example.pulsefit.domain.usecase.SaveUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getUserProfile: GetUserProfileUseCase,
    private val saveUserProfile: SaveUserProfileUseCase
) : ViewModel() {

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile

    private val _useSimulatedHr = MutableStateFlow(false)
    val useSimulatedHr: StateFlow<Boolean> = _useSimulatedHr

    init {
        viewModelScope.launch {
            getUserProfile().collect { _profile.value = it }
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

    fun save() {
        viewModelScope.launch {
            _profile.value?.let { saveUserProfile(it) }
        }
    }
}
