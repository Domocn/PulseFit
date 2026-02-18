package com.example.pulsefit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulsefit.domain.usecase.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val getUserProfile: GetUserProfileUseCase
) : ViewModel() {

    private val _isOnboardingComplete = MutableStateFlow<Boolean?>(null)
    val isOnboardingComplete: StateFlow<Boolean?> = _isOnboardingComplete

    init {
        viewModelScope.launch {
            val profile = getUserProfile.once()
            _isOnboardingComplete.value = profile?.onboardingComplete == true
        }
    }
}
