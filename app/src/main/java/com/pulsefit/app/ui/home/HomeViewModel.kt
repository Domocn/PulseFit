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
import com.pulsefit.app.data.exercise.TemplateRegistry
import com.pulsefit.app.data.local.dao.DailyQuestDao
import com.pulsefit.app.data.local.dao.ReadinessDataDao
import com.pulsefit.app.data.local.entity.SpoonBudgetEntity
import com.pulsefit.app.data.repository.SpoonBudgetRepository
import com.pulsefit.app.nd.PdaLanguage
import com.pulsefit.app.util.DecisionEngine
import com.pulsefit.app.util.GymBusyPredictor
import com.pulsefit.app.util.MicroWorkoutEngine
import com.pulsefit.app.util.SpoonCalculator
import com.pulsefit.app.data.local.entity.DailyQuestEntity
import com.pulsefit.app.data.local.entity.ReadinessDataEntity
import com.pulsefit.app.data.model.GamificationLevel
import com.pulsefit.app.data.model.NdProfile
import com.pulsefit.app.data.repository.SensoryPreferencesRepository
import com.pulsefit.app.domain.model.UserProfile
import com.pulsefit.app.domain.model.Workout
import com.pulsefit.app.domain.repository.WorkoutRepository
import com.pulsefit.app.domain.usecase.CalculateStreakUseCase
import com.pulsefit.app.domain.usecase.GetUserProfileUseCase
import com.pulsefit.app.domain.usecase.GetWorkoutStatsUseCase
import com.pulsefit.app.health.HealthConnectRepository
import com.pulsefit.app.util.ReadinessCalculator
import com.pulsefit.app.util.ReadinessResult
import com.pulsefit.app.util.RecommendedWorkout
import com.pulsefit.app.util.ScaledTarget
import com.pulsefit.app.util.SleepAwareScaler
import com.pulsefit.app.util.WorkoutRecommender
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
    private val noveltyEngine: NoveltyEngine,
    private val healthConnectRepository: HealthConnectRepository,
    private val readinessDataDao: ReadinessDataDao,
    private val templateRegistry: TemplateRegistry,
    private val sensoryPreferencesRepository: SensoryPreferencesRepository,
    private val decisionEngine: DecisionEngine,
    private val spoonBudgetRepository: SpoonBudgetRepository,
    private val spoonCalculator: SpoonCalculator,
    private val gymBusyPredictor: GymBusyPredictor,
    private val microWorkoutEngine: MicroWorkoutEngine
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

    private val _readiness = MutableStateFlow<ReadinessResult?>(null)
    val readiness: StateFlow<ReadinessResult?> = _readiness

    private val _adjustedTarget = MutableStateFlow<ScaledTarget?>(null)
    val adjustedTarget: StateFlow<ScaledTarget?> = _adjustedTarget

    private val _recommendations = MutableStateFlow<List<RecommendedWorkout>>(emptyList())
    val recommendations: StateFlow<List<RecommendedWorkout>> = _recommendations

    private val _showEnergyDialog = MutableStateFlow(false)
    val showEnergyDialog: StateFlow<Boolean> = _showEnergyDialog

    private val _gamificationLevel = MutableStateFlow(GamificationLevel.FULL)
    val gamificationLevel: StateFlow<GamificationLevel> = _gamificationLevel

    // ND Features: Spoon Budget, PDA, Decide for Me, Gym Busyness, Micro Workout
    private val _spoonBudget = MutableStateFlow<SpoonBudgetEntity?>(null)
    val spoonBudget: StateFlow<SpoonBudgetEntity?> = _spoonBudget

    private val _spoonBudgetEnabled = MutableStateFlow(false)
    val spoonBudgetEnabled: StateFlow<Boolean> = _spoonBudgetEnabled

    private val _pdaMode = MutableStateFlow(false)
    val pdaMode: StateFlow<Boolean> = _pdaMode

    private val _decisionResult = MutableStateFlow<DecisionEngine.Decision?>(null)
    val decisionResult: StateFlow<DecisionEngine.Decision?> = _decisionResult

    private val _busyPrediction = MutableStateFlow<GymBusyPredictor.BusyPrediction?>(null)
    val busyPrediction: StateFlow<GymBusyPredictor.BusyPrediction?> = _busyPrediction

    private val _currentMicro = MutableStateFlow<MicroWorkoutEngine.MicroWorkout?>(null)
    val currentMicro: StateFlow<MicroWorkoutEngine.MicroWorkout?> = _currentMicro

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
        // Load gamification level + ND prefs
        viewModelScope.launch {
            val prefs = sensoryPreferencesRepository.getPreferencesOnce()
            _gamificationLevel.value = prefs.gamificationLevel
            _pdaMode.value = prefs.pdaMode
            _spoonBudgetEnabled.value = prefs.spoonBudgetEnabled
            if (prefs.spoonBudgetEnabled) {
                spoonBudgetRepository.getBudget().collect { _spoonBudget.value = it }
            }
        }
        // Gym busyness prediction
        viewModelScope.launch {
            _busyPrediction.value = gymBusyPredictor.predict()
        }
        // Load a random micro workout
        viewModelScope.launch {
            _currentMicro.value = microWorkoutEngine.getRandom()
        }
        // Calculate readiness and sleep-aware target
        viewModelScope.launch {
            val profile = getUserProfile.once() ?: return@launch
            val sleepData = try { healthConnectRepository.readLastNightSleep() } catch (_: Exception) { null }
            val restingHr = try { healthConnectRepository.readRestingHeartRate() } catch (_: Exception) { null }
            val readinessData = readinessDataDao.getReadinessOnce()

            // Save sleep + resting HR to readiness data
            if (sleepData != null || restingHr != null) {
                val existing = readinessData ?: ReadinessDataEntity()
                readinessDataDao.insertOrUpdate(
                    existing.copy(
                        lastSleepHours = sleepData?.totalHours ?: existing.lastSleepHours,
                        lastSleepQuality = sleepData?.quality ?: existing.lastSleepQuality,
                        restingHr = restingHr ?: existing.restingHr
                    )
                )
            }

            val lastHrr = readinessData?.lastHrrScore
            val recentWorkloadScore = calculateWorkloadScore(profile)

            val readinessResult = ReadinessCalculator.calculate(
                sleepHours = sleepData?.totalHours ?: readinessData?.lastSleepHours,
                sleepQuality = sleepData?.quality ?: readinessData?.lastSleepQuality,
                restingHr = restingHr ?: readinessData?.restingHr,
                baselineRestingHr = profile.restingHeartRate,
                lastHrr = lastHrr,
                daysSinceLastWorkout = _daysSinceLastWorkout.value,
                recentWorkloadScore = recentWorkloadScore
            )
            _readiness.value = readinessResult

            // Save readiness score
            val updatedReadiness = (readinessData ?: ReadinessDataEntity()).copy(
                readinessScore = readinessResult.score,
                readinessCalculatedAt = System.currentTimeMillis()
            )
            readinessDataDao.insertOrUpdate(updatedReadiness)

            // Sleep-aware target scaling
            val scaledTarget = SleepAwareScaler.scaleWorkout(
                baseDailyTarget = profile.dailyTarget,
                sleepData = sleepData,
                restingHr = restingHr,
                baselineRestingHr = profile.restingHeartRate,
                readinessScore = readinessResult.score
            )
            if (scaledTarget.scaleFactor < 1.0f) {
                _adjustedTarget.value = scaledTarget
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

    fun showEnergyCheck() {
        _showEnergyDialog.value = true
    }

    fun dismissEnergyCheck() {
        _showEnergyDialog.value = false
    }

    fun onEnergySelected(energy: Int) {
        _showEnergyDialog.value = false
        viewModelScope.launch {
            // Save energy to readiness data
            val existing = readinessDataDao.getReadinessOnce() ?: ReadinessDataEntity()
            readinessDataDao.insertOrUpdate(
                existing.copy(
                    selfReportedEnergy = energy,
                    selfReportedEnergyAt = System.currentTimeMillis()
                )
            )
            // Generate recommendations
            val templates = templateRegistry.getAll()
            val recs = WorkoutRecommender.recommend(energy, _readiness.value, templates)
            _recommendations.value = recs
        }
    }

    fun decideForMe() {
        viewModelScope.launch {
            val energy = _readiness.value?.score ?: 50
            val energyLevel = (energy / 20).coerceIn(1, 5)
            val recentIds = workoutRepository.getCompletedWorkouts().take(3).mapNotNull { it.templateId }
            val decision = decisionEngine.decide(
                readinessScore = energy,
                energyLevel = energyLevel,
                availableMinutes = 60,
                recentTemplateIds = recentIds
            )
            _decisionResult.value = decision
        }
    }

    fun clearDecision() { _decisionResult.value = null }

    fun completeMicro() {
        viewModelScope.launch {
            val micro = _currentMicro.value ?: return@launch
            if (_spoonBudgetEnabled.value) {
                spoonBudgetRepository.spendSpoons(0.5f)
            }
            _currentMicro.value = microWorkoutEngine.getRandom()
        }
    }

    fun shuffleMicro() {
        _currentMicro.value = microWorkoutEngine.getRandom()
    }

    fun pdaTransform(text: String): String = PdaLanguage.transform(text, _pdaMode.value)

    private suspend fun calculateWorkloadScore(profile: UserProfile): Float {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)

        // Acute load: last 7 days
        val acuteStart = today.minusDays(7).atStartOfDay(zone).toInstant().toEpochMilli()
        val acuteEnd = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
        val acuteWorkouts = workoutRepository.getWorkoutsInDateRange(acuteStart, acuteEnd)
        val acuteLoad = acuteWorkouts.sumOf { it.burnPoints }.toFloat()

        // Chronic load: last 28 days (average per week)
        val chronicStart = today.minusDays(28).atStartOfDay(zone).toInstant().toEpochMilli()
        val chronicWorkouts = workoutRepository.getWorkoutsInDateRange(chronicStart, acuteEnd)
        val chronicLoad = chronicWorkouts.sumOf { it.burnPoints }.toFloat() / 4f

        return if (chronicLoad > 0) acuteLoad / chronicLoad else 0f
    }
}
