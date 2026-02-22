package com.pulsefit.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefit.app.adhd.AntiBurnoutSystem
import com.pulsefit.app.adhd.DailyQuestManager
import com.pulsefit.app.adhd.NoveltyEngine
import com.pulsefit.app.ble.BlePreferences
import com.pulsefit.app.ble.ConnectionStatus
import com.pulsefit.app.ble.HeartRateSource
import com.pulsefit.app.ble.RealHeartRate
import com.pulsefit.app.ble.SimulatedHeartRate
import com.pulsefit.app.data.local.dao.DailyQuestDao
import com.pulsefit.app.data.local.entity.DailyQuestEntity
import com.pulsefit.app.data.model.NdProfile
import com.pulsefit.app.domain.model.UserProfile
import com.pulsefit.app.domain.model.Workout
import com.pulsefit.app.domain.repository.WorkoutRepository
import com.pulsefit.app.domain.usecase.CalculateStreakUseCase
import com.pulsefit.app.domain.usecase.GetUserProfileUseCase
import com.pulsefit.app.domain.usecase.GetWorkoutStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserProfile: GetUserProfileUseCase,
    private val workoutRepository: WorkoutRepository,
    @RealHeartRate private val realHeartRateSource: HeartRateSource,
    @SimulatedHeartRate private val simulatedHeartRateSource: HeartRateSource,
    private val blePreferences: BlePreferences,
    private val calculateStreak: CalculateStreakUseCase,
    private val getWorkoutStats: GetWorkoutStatsUseCase,
    private val dailyQuestDao: DailyQuestDao,
    private val antiBurnoutSystem: AntiBurnoutSystem,
    private val dailyQuestManager: DailyQuestManager,
    private val noveltyEngine: NoveltyEngine
) : ViewModel() {

    val userProfile: StateFlow<UserProfile?> = getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val todayBurnPoints: StateFlow<Int> = workoutRepository.getTodayBurnPoints()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val heartRateSource: HeartRateSource
        get() = if (blePreferences.useSimulatedHr) simulatedHeartRateSource else realHeartRateSource

    val connectionStatus: StateFlow<ConnectionStatus> = heartRateSource.connectionStatus

    private val _workoutId = MutableStateFlow<Long?>(null)
    val workoutId: StateFlow<Long?> = _workoutId

    private val _isJustFiveMin = MutableStateFlow(false)
    val isJustFiveMin: StateFlow<Boolean> = _isJustFiveMin

    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak

    private val _weeklyWorkouts = MutableStateFlow(0)
    val weeklyWorkouts: StateFlow<Int> = _weeklyWorkouts

    private val _weeklyBurnPoints = MutableStateFlow(0)
    val weeklyBurnPoints: StateFlow<Int> = _weeklyBurnPoints

    private val _dailyQuests = MutableStateFlow<List<DailyQuestEntity>>(emptyList())
    val dailyQuests: StateFlow<List<DailyQuestEntity>> = _dailyQuests

    private val _shouldRest = MutableStateFlow(false)
    val shouldRest: StateFlow<Boolean> = _shouldRest

    private val _avgBurnPoints = MutableStateFlow(0)
    val avgBurnPoints: StateFlow<Int> = _avgBurnPoints

    private val _weeklyTheme = MutableStateFlow<String?>(null)
    val weeklyTheme: StateFlow<String?> = _weeklyTheme

    private val _daysSinceLastWorkout = MutableStateFlow(0)
    val daysSinceLastWorkout: StateFlow<Int> = _daysSinceLastWorkout

    init {
        viewModelScope.launch {
            _currentStreak.value = calculateStreak()
        }
        viewModelScope.launch {
            val stats = getWorkoutStats.getWeeklyStats()
            _weeklyWorkouts.value = stats.totalWorkouts
            _weeklyBurnPoints.value = stats.totalBurnPoints
        }
        viewModelScope.launch {
            _shouldRest.value = antiBurnoutSystem.shouldSuggestRestDay()
        }
        viewModelScope.launch {
            val profile = getUserProfile.once()
            val nd = profile?.ndProfile ?: NdProfile.STANDARD
            if (nd == NdProfile.ADHD || nd == NdProfile.AUDHD) {
                _weeklyTheme.value = noveltyEngine.getWeeklyTheme().name
            }
        }
        viewModelScope.launch {
            val stats = getWorkoutStats.getWeeklyStats()
            _avgBurnPoints.value = if (stats.totalWorkouts > 0) stats.totalBurnPoints / stats.totalWorkouts else 0
        }
        viewModelScope.launch {
            val profile = getUserProfile.once()
            val lastWorkout = profile?.lastWorkoutAt
            if (lastWorkout != null && lastWorkout > 0) {
                val lastDate = Instant.ofEpochMilli(lastWorkout).atZone(ZoneId.systemDefault()).toLocalDate()
                val today = LocalDate.now()
                _daysSinceLastWorkout.value = java.time.temporal.ChronoUnit.DAYS.between(lastDate, today).toInt()
            }
        }
        viewModelScope.launch {
            dailyQuestManager.generateIfNeeded()
            val todayMillis = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
            dailyQuestDao.getQuestsForDate(todayMillis).collect {
                _dailyQuests.value = it
            }
        }
    }

    fun onStartWorkout() {
        _isJustFiveMin.value = false
        viewModelScope.launch {
            val workout = Workout(startTime = Instant.now())
            val id = workoutRepository.createWorkout(workout)
            _workoutId.value = id
        }
    }

    fun onStartJustFiveMin() {
        _isJustFiveMin.value = true
        viewModelScope.launch {
            val workout = Workout(startTime = Instant.now(), isJustFiveMin = true)
            val id = workoutRepository.createWorkout(workout)
            _workoutId.value = id
        }
    }

    fun onWorkoutNavigated() {
        _workoutId.value = null
    }
}
