package com.example.pulsefit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulsefit.data.model.AppTheme
import com.example.pulsefit.data.repository.SensoryPreferencesRepository
import com.example.pulsefit.domain.usecase.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val getUserProfile: GetUserProfileUseCase,
    private val sensoryPreferencesRepository: SensoryPreferencesRepository
) : ViewModel() {

    private val _isOnboardingComplete = MutableStateFlow<Boolean?>(null)
    val isOnboardingComplete: StateFlow<Boolean?> = _isOnboardingComplete

    private val _appTheme = MutableStateFlow(AppTheme.MIDNIGHT)
    val appTheme: StateFlow<AppTheme> = _appTheme

    init {
        viewModelScope.launch {
            val profile = getUserProfile.once()
            _isOnboardingComplete.value = profile?.onboardingComplete == true
        }
        viewModelScope.launch {
            sensoryPreferencesRepository.getPreferences()
                .map { it?.appTheme ?: AppTheme.MIDNIGHT }
                .collect { _appTheme.value = it }
        }
    }
}
