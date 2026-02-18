package com.example.pulsefit.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulsefit.data.model.NdProfile
import com.example.pulsefit.domain.model.UserProfile
import com.example.pulsefit.domain.usecase.GetUserProfileUseCase
import com.example.pulsefit.domain.usecase.SaveUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val saveUserProfile: SaveUserProfileUseCase,
    private val getUserProfile: GetUserProfileUseCase
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _age = MutableStateFlow("")
    val age: StateFlow<String> = _age

    private val _weight = MutableStateFlow("")
    val weight: StateFlow<String> = _weight

    private val _height = MutableStateFlow("")
    val height: StateFlow<String> = _height

    private val _ndProfile = MutableStateFlow(NdProfile.STANDARD)
    val ndProfile: StateFlow<NdProfile> = _ndProfile

    val maxHeartRate: Int
        get() {
            val ageValue = _age.value.toIntOrNull() ?: 25
            return 220 - ageValue
        }

    fun updateName(value: String) { _name.value = value }
    fun updateAge(value: String) { _age.value = value }
    fun updateWeight(value: String) { _weight.value = value }
    fun updateHeight(value: String) { _height.value = value }
    fun updateNdProfile(profile: NdProfile) { _ndProfile.value = profile }

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
                ndProfile = _ndProfile.value,
                onboardingComplete = true
            )
            saveUserProfile(profile)
            onComplete()
        }
    }
}
