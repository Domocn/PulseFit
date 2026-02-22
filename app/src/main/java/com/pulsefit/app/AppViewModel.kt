package com.pulsefit.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefit.app.data.model.AppTheme
import com.pulsefit.app.data.model.NdProfile
import com.pulsefit.app.data.model.WorkoutTemplateData
import com.pulsefit.app.data.remote.AuthRepository
import com.pulsefit.app.data.repository.SensoryPreferencesRepository
import com.pulsefit.app.domain.model.Workout
import com.pulsefit.app.domain.repository.UserRepository
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
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
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

    private val _selectedTemplateData = MutableStateFlow<WorkoutTemplateData?>(null)
    val selectedTemplateData: StateFlow<WorkoutTemplateData?> = _selectedTemplateData

    private val _selectedTemplateId = MutableStateFlow<String?>(null)
    val selectedTemplateId: StateFlow<String?> = _selectedTemplateId

    fun setSelectedTemplate(name: String) { _selectedTemplateName.value = name }
    fun clearSelectedTemplate() {
        _selectedTemplateName.value = null
        _selectedTemplateData.value = null
        _selectedTemplateId.value = null
    }

    init {
        viewModelScope.launch {
            authRepository.authState.collect { user ->
                _isAuthenticated.value = user != null
            }
        }
        viewModelScope.launch {
            var profile = getUserProfile.once()
            // If Room is empty but user is authenticated, try restoring from Firestore
            if (profile == null && authRepository.isAuthenticated) {
                profile = userRepository.restoreFromCloud()
            }
            _isOnboardingComplete.value = profile?.onboardingComplete == true
            _ndProfile.value = profile?.ndProfile ?: NdProfile.STANDARD
        }
        viewModelScope.launch {
            sensoryPreferencesRepository.getPreferences()
                .map { it?.appTheme ?: AppTheme.MIDNIGHT }
                .collect { _appTheme.value = it }
        }
    }

    fun resetOnboardingState() {
        _isOnboardingComplete.value = null
    }

    fun refreshOnboardingState() {
        viewModelScope.launch {
            var profile = getUserProfile.once()
            // If Room is empty but user is authenticated, try restoring from Firestore
            if (profile == null && authRepository.isAuthenticated) {
                profile = userRepository.restoreFromCloud()
                if (profile != null) {
                    _ndProfile.value = profile.ndProfile
                }
            }
            _isOnboardingComplete.value = profile?.onboardingComplete == true
        }
    }

    fun createWorkoutFromTemplate(template: WorkoutTemplateData, onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val workout = Workout(startTime = Instant.now(), templateId = template.id)
            val id = workoutRepository.createWorkout(workout)
            _selectedTemplateData.value = template
            _selectedTemplateId.value = template.id
            onCreated(id)
        }
    }
}
