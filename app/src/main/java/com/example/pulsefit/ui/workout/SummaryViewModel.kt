package com.example.pulsefit.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulsefit.adhd.CelebrationConfig
import com.example.pulsefit.adhd.CelebrationEngine
import com.example.pulsefit.adhd.CelebrationType
import com.example.pulsefit.data.model.HeartRateZone
import com.example.pulsefit.data.model.NdProfile
import com.example.pulsefit.domain.model.HeartRateReading
import com.example.pulsefit.domain.model.Workout
import com.example.pulsefit.domain.repository.WorkoutRepository
import com.example.pulsefit.domain.usecase.GenerateCoachTipUseCase
import com.example.pulsefit.domain.usecase.GetUserProfileUseCase
import com.example.pulsefit.util.CalorieCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val getUserProfile: GetUserProfileUseCase,
    private val celebrationEngine: CelebrationEngine,
    private val generateCoachTip: GenerateCoachTipUseCase
) : ViewModel() {

    private val _workout = MutableStateFlow<Workout?>(null)
    val workout: StateFlow<Workout?> = _workout

    private val _readings = MutableStateFlow<List<HeartRateReading>>(emptyList())
    val readings: StateFlow<List<HeartRateReading>> = _readings

    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes

    private val _epocEstimate = MutableStateFlow(0)
    val epocEstimate: StateFlow<Int> = _epocEstimate

    private val _dailyTarget = MutableStateFlow(12)
    val dailyTarget: StateFlow<Int> = _dailyTarget

    private val _targetHit = MutableStateFlow(false)
    val targetHit: StateFlow<Boolean> = _targetHit

    private val _maxHr = MutableStateFlow(190)
    val maxHr: StateFlow<Int> = _maxHr

    private val _celebrationConfig = MutableStateFlow<CelebrationConfig?>(null)
    val celebrationConfig: StateFlow<CelebrationConfig?> = _celebrationConfig

    private val _coachTip = MutableStateFlow<String?>(null)
    val coachTip: StateFlow<String?> = _coachTip

    private var currentWorkoutId: Long = 0

    fun load(workoutId: Long) {
        currentWorkoutId = workoutId
        viewModelScope.launch {
            val w = workoutRepository.getWorkoutById(workoutId)
            _workout.value = w
            _notes.value = w?.notes ?: ""
            _readings.value = workoutRepository.getReadingsForWorkoutOnce(workoutId)

            // Load user profile for daily target and max HR
            val profile = getUserProfile.once()
            if (profile != null) {
                _dailyTarget.value = profile.dailyTarget
                _maxHr.value = profile.maxHeartRate
                _targetHit.value = w != null && w.burnPoints >= profile.dailyTarget
            }

            // Calculate EPOC
            w?.let {
                _epocEstimate.value = CalorieCalculator.estimateEpoc(
                    zoneTime = it.zoneTime,
                    avgHeartRate = it.averageHeartRate,
                    durationMinutes = it.durationSeconds / 60
                )
            }

            // Celebration config
            val ndProfile = profile?.ndProfile ?: NdProfile.STANDARD
            _celebrationConfig.value = celebrationEngine.getCelebrationConfig(
                CelebrationType.WORKOUT_COMPLETE, ndProfile
            )

            // Coach tip
            w?.let {
                _coachTip.value = generateCoachTip(it, profile?.dailyTarget ?: 12)
            }
        }
    }

    fun dismissCelebration() {
        _celebrationConfig.value = null
    }

    fun updateNotes(text: String) {
        _notes.value = text
    }

    fun saveNotes() {
        viewModelScope.launch {
            val w = _workout.value ?: return@launch
            workoutRepository.updateWorkout(w.copy(notes = _notes.value.ifBlank { null }))
        }
    }
}
