package com.example.pulsefit.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulsefit.ble.HeartRateSource
import com.example.pulsefit.data.model.HeartRateZone
import com.example.pulsefit.domain.usecase.EndWorkoutUseCase
import com.example.pulsefit.domain.usecase.GetUserProfileUseCase
import com.example.pulsefit.domain.usecase.RecordHeartRateUseCase
import com.example.pulsefit.util.ZoneCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val heartRateSource: HeartRateSource,
    private val getUserProfile: GetUserProfileUseCase,
    private val recordHeartRate: RecordHeartRateUseCase,
    private val endWorkout: EndWorkoutUseCase
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

    private var maxHr = 190
    private var timerJob: Job? = null
    private var workoutId: Long = 0
    private var pointAccumulator = 0.0

    fun start(workoutId: Long) {
        this.workoutId = workoutId
        heartRateSource.connect()

        viewModelScope.launch {
            val profile = getUserProfile.once()
            maxHr = profile?.maxHeartRate ?: 190
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

                    // Accumulate points (fractional per second)
                    pointAccumulator += zone.pointsPerMinute / 60.0
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
                }
            }
        }
    }

    fun endWorkout() {
        timerJob?.cancel()
        heartRateSource.disconnect()
        viewModelScope.launch {
            endWorkout(workoutId, _burnPoints.value, _zoneTime.value)
            _isFinished.value = true
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        heartRateSource.disconnect()
    }
}
