package com.pulsefit.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefit.app.data.model.AppTheme
import com.pulsefit.app.data.model.NdProfile
import com.pulsefit.app.data.remote.AuthRepository
import com.pulsefit.app.data.repository.SensoryPreferencesRepository
import com.pulsefit.app.domain.model.Workout
import com.pulsefit.app.domain.repository.WorkoutRepository
import com.pulsefit.app.domain.usecase.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val getUserProfile: GetUserProfileUseCase,
    private val sensoryPreferencesRepository: SensoryPreferencesRepository,
    private val workoutRepository: WorkoutRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isOnboardingComplete = MutableStateFlow<Boolean?>(null)
    val isOnboardingComplete: StateFlow<Boolean?> = _isOnboardingComplete

    private val _isAuthenticated = MutableStateFlow<Boolean?>(null)
    val isAuthenticated: StateFlow<Boolean?> = _isAuthenticated

    private val _appTheme = MutableStateFlow(AppTheme.MIDNIGHT)
    val appTheme: StateFlow<AppTheme> = _appTheme

    private val _ndProfile = MutableStateFlow(NdProfile.STANDARD)
    val ndProfile: StateFlow<NdProfile> = _ndProfile

    // Transient state for template → PreWorkoutSchedule flow
    private val _selectedTemplateName = MutableStateFlow<String?>(null)
    val selectedTemplateName: StateFlow<String?> = _selectedTemplateName

    fun setSelectedTemplate(name: String) { _selectedTemplateName.value = name }
    fun clearSelectedTemplate() { _selectedTemplateName.value = null }

    init {
        viewModelScope.launch {
            authRepository.authState.collect { user ->
                _isAuthenticated.value = user != null
            }
        }
        viewModelScope.launch {
            val profile = getUserProfile.once()
            _isOnboardingComplete.value = profile?.onboardingComplete == true
            _ndProfile.value = profile?.ndProfile ?: NdProfile.STANDARD
        }
        viewModelScope.launch {
            sensoryPreferencesRepository.getPreferences()
                .map { it?.appTheme ?: AppTheme.MIDNIGHT }
                .collect { _appTheme.value = it }
        }
    }

    fun refreshOnboardingState() {
        viewModelScope.launch {
            val profile = getUserProfile.once()
            _isOnboardingComplete.value = profile?.onboardingComplete == true
        }
    }

    fun createWorkoutFromTemplate(templateName: String, onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val workout = Workout(startTime = Instant.now())
            val id = workoutRepository.createWorkout(workout)
            onCreated(id)
        }
    }
}
