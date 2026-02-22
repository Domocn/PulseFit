package com.pulsefit.app.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefit.app.adhd.CelebrationConfig
import com.pulsefit.app.adhd.CelebrationEngine
import com.pulsefit.app.adhd.CelebrationType
import com.pulsefit.app.data.model.HeartRateZone
import com.pulsefit.app.data.model.NdProfile
import com.pulsefit.app.domain.model.HeartRateReading
import com.pulsefit.app.domain.model.Workout
import com.pulsefit.app.data.remote.CloudProfileRepository
import com.pulsefit.app.data.remote.SharedWorkout
import com.pulsefit.app.domain.repository.WorkoutRepository
import com.pulsefit.app.domain.usecase.GenerateCoachTipUseCase
import com.pulsefit.app.domain.usecase.GetUserProfileUseCase
import com.pulsefit.app.util.CalorieCalculator
import com.google.firebase.auth.FirebaseAuth
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
    private val generateCoachTip: GenerateCoachTipUseCase,
    private val cloudProfileRepository: CloudProfileRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _isShared = MutableStateFlow(false)
    val isShared: StateFlow<Boolean> = _isShared

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

    fun shareWorkout() {
        viewModelScope.launch {
            val w = _workout.value ?: return@launch
            val user = firebaseAuth.currentUser ?: return@launch
            val shared = SharedWorkout(
                uid = user.uid,
                displayName = user.displayName ?: "User",
                durationSeconds = w.durationSeconds,
                burnPoints = w.burnPoints,
                averageHeartRate = w.averageHeartRate,
                maxHeartRate = w.maxHeartRate,
                xpEarned = w.xpEarned,
                zoneTimeSummary = w.zoneTime.mapKeys { it.key.name },
                timestamp = System.currentTimeMillis()
            )
            cloudProfileRepository.shareWorkout(shared)
            _isShared.value = true
        }
    }
}
