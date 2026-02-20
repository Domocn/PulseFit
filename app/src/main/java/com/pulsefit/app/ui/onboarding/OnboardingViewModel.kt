package com.pulsefit.app.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefit.app.data.model.NdProfile
import com.pulsefit.app.domain.model.UserProfile
import com.pulsefit.app.domain.usecase.GetUserProfileUseCase
import com.pulsefit.app.domain.usecase.SaveUserProfileUseCase
import com.pulsefit.app.nd.NdProfileManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val saveUserProfile: SaveUserProfileUseCase,
    private val getUserProfile: GetUserProfileUseCase,
    private val ndProfileManager: NdProfileManager
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _age = MutableStateFlow("")
    val age: StateFlow<String> = _age

    private val _weight = MutableStateFlow("")
    val weight: StateFlow<String> = _weight

    private val _height = MutableStateFlow("")
    val height: StateFlow<String> = _height

    private val _restingHr = MutableStateFlow("")
    val restingHr: StateFlow<String> = _restingHr

    private val _ndProfile = MutableStateFlow(NdProfile.STANDARD)
    val ndProfile: StateFlow<NdProfile> = _ndProfile

    private val _biologicalSex = MutableStateFlow("male")
    val biologicalSex: StateFlow<String> = _biologicalSex

    private val _dailyTarget = MutableStateFlow(12)
    val dailyTarget: StateFlow<Int> = _dailyTarget

    private val _maxHrOverride = MutableStateFlow("")
    val maxHrOverride: StateFlow<String> = _maxHrOverride

    val maxHeartRate: Int
        get() {
            val override = _maxHrOverride.value.toIntOrNull()
            if (override != null && override in 120..220) return override
            val ageValue = _age.value.toIntOrNull() ?: 25
            return 220 - ageValue
        }

    fun updateName(value: String) { _name.value = value }
    fun updateAge(value: String) { _age.value = value }
    fun updateWeight(value: String) { _weight.value = value }
    fun updateHeight(value: String) { _height.value = value }
    fun updateRestingHr(value: String) { _restingHr.value = value }
    fun updateNdProfile(profile: NdProfile) { _ndProfile.value = profile }
    fun updateBiologicalSex(sex: String) { _biologicalSex.value = sex }
    fun updateDailyTarget(target: Int) { _dailyTarget.value = target.coerceIn(8, 30) }
    fun updateMaxHrOverride(value: String) { _maxHrOverride.value = value }

    fun isProfileValid(): Boolean {
        return _name.value.isNotBlank() &&
                _age.value.toIntOrNull() != null &&
                (_age.value.toIntOrNull() ?: 0) in 13..120
    }

    fun saveProfile(onComplete: () -> Unit) {
        viewModelScope.launch {
            val profile = UserProfile(
                name = _name.value.trim(),
                age = _age.value.toIntOrNull() ?: 25,
                maxHeartRate = maxHeartRate,
                weight = _weight.value.toFloatOrNull(),
                height = _height.value.toFloatOrNull(),
                restingHeartRate = _restingHr.value.toIntOrNull(),
                ndProfile = _ndProfile.value,
                dailyTarget = _dailyTarget.value,
                biologicalSex = _biologicalSex.value,
                onboardingComplete = true
            )
            saveUserProfile(profile)
            ndProfileManager.applyProfileDefaults(profile.ndProfile)
            onComplete()
        }
    }
}
