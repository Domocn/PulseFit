package com.example.pulsefit.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulsefit.adhd.MicroRewardEngine
import com.example.pulsefit.adhd.MicroRewardEvent
import com.example.pulsefit.ble.HeartRateSource
import com.example.pulsefit.data.model.HeartRateZone
import com.example.pulsefit.data.model.NdProfile
import com.example.pulsefit.domain.repository.WorkoutRepository
import com.example.pulsefit.domain.usecase.AwardXpUseCase
import com.example.pulsefit.domain.usecase.CalculateStreakUseCase
import com.example.pulsefit.domain.usecase.EndWorkoutUseCase
import com.example.pulsefit.domain.usecase.GetUserProfileUseCase
import com.example.pulsefit.domain.usecase.RecordHeartRateUseCase
import com.example.pulsefit.util.ZoneCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val heartRateSource: HeartRateSource,
    private val getUserProfile: GetUserProfileUseCase,
    private val recordHeartRate: RecordHeartRateUseCase,
    private val endWorkoutUseCase: EndWorkoutUseCase,
    private val awardXpUseCase: AwardXpUseCase,
    private val calculateStreakUseCase: CalculateStreakUseCase,
    private val workoutRepository: WorkoutRepository,
    private val microRewardEngine: MicroRewardEngine
) : ViewModel() {

    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds

    private val _currentHeartRate = MutableStateFlow(0)
    val currentHeartRate: StateFlow<Int> = _currentHeartRate

    private val _currentZone = MutableStateFlow(HeartRateZone.REST)
    val currentZone: StateFlow<HeartRateZone> = _currentZone

    private val _burnPoints = MutableStateFlow(0)
    val burnPoints: StateFlow<Int> = _burnPoints

    private val _zoneTime = MutableStateFlow<Map<HeartRateZone, Long>>(
        HeartRateZone.entries.associateWith { 0L }
    )
    val zoneTime: StateFlow<Map<HeartRateZone, Long>> = _zoneTime

    private val _recentReadings = MutableStateFlow<List<Int>>(emptyList())
    val recentReadings: StateFlow<List<Int>> = _recentReadings

    private val _isFinished = MutableStateFlow(false)
    val isFinished: StateFlow<Boolean> = _isFinished

    // Just 5 Min mode
    private val _isJustFiveMin = MutableStateFlow(false)
    val isJustFiveMin: StateFlow<Boolean> = _isJustFiveMin

    private val _justFiveMinPromptShown = MutableStateFlow(false)
    val justFiveMinPromptShown: StateFlow<Boolean> = _justFiveMinPromptShown

    private val _justFiveMinExtended = MutableStateFlow(false)
    val justFiveMinExtended: StateFlow<Boolean> = _justFiveMinExtended

    // Micro-rewards
    private val _rewardEvents = MutableSharedFlow<MicroRewardEvent>(extraBufferCapacity = 10)
    val rewardEvents: SharedFlow<MicroRewardEvent> = _rewardEvents

    // Time blindness - 5-min chunk tracking
    private val _currentChunk = MutableStateFlow(1)
    val currentChunk: StateFlow<Int> = _currentChunk

    // XP earned in this workout
    private val _xpEarned = MutableStateFlow(0)
    val xpEarned: StateFlow<Int> = _xpEarned

    private var maxHr = 190
    private var timerJob: Job? = null
    private var workoutId: Long = 0
    private var pointAccumulator = 0.0
    private var ndProfile = NdProfile.STANDARD
    private var streakMultiplier = 1f

    fun start(workoutId: Long) {
        this.workoutId = workoutId
        heartRateSource.connect()
        microRewardEngine.reset()

        viewModelScope.launch {
            val profile = getUserProfile.once()
            maxHr = profile?.maxHeartRate ?: 190
            ndProfile = profile?.ndProfile ?: NdProfile.STANDARD

            // Load streak multiplier for ADHD profiles
            if (ndProfile == NdProfile.ADHD || ndProfile == NdProfile.AUDHD) {
                val streak = profile?.currentStreak ?: 0
                streakMultiplier = 1f + (streak.coerceAtMost(7) * 0.1f)
            }

            // Check if Just 5 Min workout
            val workout = workoutRepository.getWorkoutById(workoutId)
            _isJustFiveMin.value = workout?.isJustFiveMin ?: false
        }

        // Collect micro-reward events
        viewModelScope.launch {
            microRewardEngine.rewardEvents.collect { event ->
                _rewardEvents.emit(event)
            }
        }

        // Timer tick every second
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                _elapsedSeconds.value++

                val hr = heartRateSource.heartRate.value
                if (hr != null && hr > 0) {
                    _currentHeartRate.value = hr
                    val zone = ZoneCalculator.getZone(hr, maxHr)
                    _currentZone.value = zone

                    // Update zone time
                    val current = _zoneTime.value.toMutableMap()
                    current[zone] = (current[zone] ?: 0L) + 1
                    _zoneTime.value = current

                    // Accumulate points (with streak multiplier for ADHD)
                    val multiplier = if (ndProfile == NdProfile.ADHD || ndProfile == NdProfile.AUDHD) {
                        streakMultiplier
                    } else 1f
                    pointAccumulator += zone.pointsPerMinute / 60.0 * multiplier
                    _burnPoints.value = pointAccumulator.toInt()

                    // Track recent readings (last 60)
                    val readings = _recentReadings.value.toMutableList()
                    readings.add(hr)
                    if (readings.size > 60) readings.removeAt(0)
                    _recentReadings.value = readings

                    // Save reading to DB every 5 seconds
                    if (_elapsedSeconds.value % 5 == 0) {
                        recordHeartRate(workoutId, hr, zone)
                    }

                    // Micro-rewards (ADHD feature)
                    if (ndProfile == NdProfile.ADHD || ndProfile == NdProfile.AUDHD) {
                        microRewardEngine.onTick(_elapsedSeconds.value, zone)
                    }
                }

                // Track 5-min chunks for time blindness timer
                _currentChunk.value = (_elapsedSeconds.value / 300) + 1

                // Just 5 Min: show prompt at 5 minutes
                if (_isJustFiveMin.value && !_justFiveMinPromptShown.value && _elapsedSeconds.value >= 300) {
                    _justFiveMinPromptShown.value = true
                }
            }
        }
    }

    fun continueJustFiveMin() {
        _justFiveMinExtended.value = true
        _justFiveMinPromptShown.value = false
    }

    fun endWorkout() {
        timerJob?.cancel()
        heartRateSource.disconnect()
        viewModelScope.launch {
            endWorkoutUseCase(workoutId, _burnPoints.value, _zoneTime.value)

            // Award XP
            val xp = awardXpUseCase(_burnPoints.value, streakMultiplier)
            _xpEarned.value = xp

            // Update workout with XP
            val workout = workoutRepository.getWorkoutById(workoutId)
            workout?.let {
                workoutRepository.updateWorkout(it.copy(xpEarned = xp))
            }

            // Update streak and stats
            workoutRepository.getWorkoutById(workoutId)?.let { w ->
                val userRepo = getUserProfile.once()
                // incrementWorkoutCount is handled via user repository
            }
            calculateStreakUseCase()

            _isFinished.value = true
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        heartRateSource.disconnect()
    }
}
