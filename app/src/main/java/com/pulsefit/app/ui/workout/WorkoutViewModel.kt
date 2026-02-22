package com.pulsefit.app.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefit.app.adhd.DailyQuestManager
import com.pulsefit.app.adhd.MicroRewardEngine
import com.pulsefit.app.adhd.MicroRewardEvent
import com.pulsefit.app.adhd.DropEvent
import com.pulsefit.app.adhd.VariableDropEngine
import com.pulsefit.app.asd.AudioPalette
import com.pulsefit.app.asd.TransitionWarning
import com.pulsefit.app.asd.TransitionWarningManager
import com.pulsefit.app.ble.BlePreferences
import com.pulsefit.app.ble.HeartRateSource
import com.pulsefit.app.ble.RealHeartRate
import com.pulsefit.app.ble.SimulatedHeartRate
import com.pulsefit.app.data.exercise.ExerciseRegistry
import com.pulsefit.app.data.exercise.TemplateRegistry
import com.pulsefit.app.data.model.AnimationLevel
import com.pulsefit.app.data.model.HeartRateZone
import com.pulsefit.app.data.model.NdProfile
import com.pulsefit.app.data.remote.AccountabilityContractRepository
import com.pulsefit.app.data.remote.BodyDoubleRepository
import com.pulsefit.app.data.remote.GroupChallengeRepository
import com.pulsefit.app.data.repository.SensoryPreferencesRepository
import com.pulsefit.app.domain.repository.WorkoutRepository
import com.pulsefit.app.domain.usecase.AwardXpUseCase
import com.pulsefit.app.domain.usecase.CalculateStreakUseCase
import com.pulsefit.app.domain.usecase.CheckAchievementsUseCase
import com.pulsefit.app.domain.usecase.EndWorkoutUseCase
import com.pulsefit.app.domain.usecase.GetUserProfileUseCase
import com.pulsefit.app.domain.usecase.GetWorkoutStatsUseCase
import com.pulsefit.app.domain.usecase.RecordHeartRateUseCase
import com.pulsefit.app.data.model.TreadMode
import com.pulsefit.app.util.CalorieCalculator
import com.pulsefit.app.util.ZoneCalculator
import com.pulsefit.app.voice.CoachingTargetRegistry
import com.pulsefit.app.voice.VoiceCoachEngine
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
    @RealHeartRate private val realHeartRateSource: HeartRateSource,
    @SimulatedHeartRate private val simulatedHeartRateSource: HeartRateSource,
    private val getUserProfile: GetUserProfileUseCase,
    private val recordHeartRate: RecordHeartRateUseCase,
    private val endWorkoutUseCase: EndWorkoutUseCase,
    private val awardXpUseCase: AwardXpUseCase,
    private val calculateStreakUseCase: CalculateStreakUseCase,
    private val workoutRepository: WorkoutRepository,
    private val microRewardEngine: MicroRewardEngine,
    private val accountabilityContractRepository: AccountabilityContractRepository,
    private val bodyDoubleRepository: BodyDoubleRepository,
    private val sensoryPreferencesRepository: SensoryPreferencesRepository,
    private val variableDropEngine: VariableDropEngine,
    private val transitionWarningManager: TransitionWarningManager,
    private val dailyQuestManager: DailyQuestManager,
    private val checkAchievements: CheckAchievementsUseCase,
    private val blePreferences: BlePreferences,
    private val voiceCoachEngine: VoiceCoachEngine,
    private val audioPalette: AudioPalette,
    private val getWorkoutStats: GetWorkoutStatsUseCase,
    private val templateRegistry: TemplateRegistry,
    private val exerciseRegistry: ExerciseRegistry,
    private val groupChallengeRepository: GroupChallengeRepository,
    private val coachingTargetRegistry: CoachingTargetRegistry
) : ViewModel() {

    private val heartRateSource: HeartRateSource
        get() = if (blePreferences.useSimulatedHr) simulatedHeartRateSource else realHeartRateSource

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

    // Drop events (Variable Drop Engine)
    private val _dropEvents = MutableSharedFlow<DropEvent>(extraBufferCapacity = 10)
    val dropEvents: SharedFlow<DropEvent> = _dropEvents

    // Transition warnings (ASD)
    private val _transitionWarnings = MutableSharedFlow<TransitionWarning>(extraBufferCapacity = 10)
    val transitionWarnings: SharedFlow<TransitionWarning> = _transitionWarnings

    // Time blindness - 5-min chunk tracking
    private val _currentChunk = MutableStateFlow(1)
    val currentChunk: StateFlow<Int> = _currentChunk

    // XP earned in this workout
    private val _xpEarned = MutableStateFlow(0)
    val xpEarned: StateFlow<Int> = _xpEarned

    // Pause/Resume
    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused

    // Minimal mode
    private val _isMinimalMode = MutableStateFlow(false)
    val isMinimalMode: StateFlow<Boolean> = _isMinimalMode

    // Animation level
    private val _animationLevel = MutableStateFlow(AnimationLevel.FULL)
    val animationLevel: StateFlow<AnimationLevel> = _animationLevel

    // Achievement unlocks
    private val _unlockedAchievements = MutableStateFlow<List<String>>(emptyList())
    val unlockedAchievements: StateFlow<List<String>> = _unlockedAchievements

    // Live calorie estimate
    private val _estimatedCalories = MutableStateFlow(0)
    val estimatedCalories: StateFlow<Int> = _estimatedCalories

    // ND profile (exposed for UI conditional rendering)
    private val _ndProfileState = MutableStateFlow(NdProfile.STANDARD)
    val ndProfileState: StateFlow<NdProfile> = _ndProfileState

    // Guided workout
    private var guidedWorkoutManager: GuidedWorkoutManager? = null
    private val _guidedState = MutableStateFlow<GuidedState?>(null)
    val guidedState: StateFlow<GuidedState?> = _guidedState
    private val _isGuidedMode = MutableStateFlow(false)
    val isGuidedMode: StateFlow<Boolean> = _isGuidedMode

    // Body double
    private val _bodyDoubleCount = MutableStateFlow(0)
    val bodyDoubleCount: StateFlow<Int> = _bodyDoubleCount
    private val _bodyDoubleEnabled = MutableStateFlow(false)
    val bodyDoubleEnabled: StateFlow<Boolean> = _bodyDoubleEnabled
    private var bodyDoubleActive = false

    private var maxHr = 190
    private var timerJob: Job? = null
    private var workoutId: Long = 0
    private var pointAccumulator = 0.0
    private var ndProfile = NdProfile.STANDARD
    private var streakMultiplier = 1f
    private var userAge = 25
    private var userWeightKg: Float? = null
    private var userIsMale = true
    private var hrSum = 0L
    private var hrCount = 0
    private var previousZone = HeartRateZone.REST
    private var previousBurnPoints = 0
    private var audioPaletteEnabled = false

    // Voice coach engagement tracking
    private var dailyTarget = 12
    private var targetHitAnnounced = false
    private var lastEncouragementSecond = 0
    private var previousBestBurnPoints = 0
    private var encouragementIntervalSeconds = 180  // ND-adaptive: ASD=300, ADHD=90, default=180
    private var treadMode = TreadMode.RUNNER

    fun start(workoutId: Long, templateId: String? = null) {
        this.workoutId = workoutId
        // Auto-reconnect to last known BLE device
        heartRateSource.connect(blePreferences.lastDeviceAddress)
        microRewardEngine.reset()
        variableDropEngine.reset()
        transitionWarningManager.reset()
        voiceCoachEngine.initialize()

        // Initialize guided workout if template is GUIDED
        if (templateId != null) {
            val template = templateRegistry.getById(templateId)
            if (template != null && template.type == "GUIDED" && template.phases.isNotEmpty()) {
                val manager = GuidedWorkoutManager(template, exerciseRegistry)
                manager.onExerciseChange = { exercise, duration, isLast ->
                    voiceCoachEngine.announceExerciseComposite(
                        exerciseName = exercise.name,
                        exerciseId = exercise.id,
                        station = exercise.station,
                        durationSeconds = duration,
                        isLast = isLast,
                        treadMode = treadMode,
                        coachingTargetRegistry = coachingTargetRegistry
                    )
                }
                manager.onStationChange = { stationName ->
                    voiceCoachEngine.onStationChange(stationName)
                }
                manager.onExerciseMidpoint = midpoint@{ flatExercise ->
                    val ex = flatExercise.exercise ?: return@midpoint
                    if (coachingTargetRegistry.isPushExercise(ex.id)) {
                        voiceCoachEngine.encouragePushHarder(
                            treadMode = treadMode,
                            station = ex.station,
                            exerciseId = ex.id,
                            coachingTargetRegistry = coachingTargetRegistry
                        )
                    }
                }
                guidedWorkoutManager = manager
                _isGuidedMode.value = true
                viewModelScope.launch {
                    manager.state.collect { _guidedState.value = it }
                }
            }
        }

        viewModelScope.launch {
            voiceCoachEngine.updateStyle()

            val profile = getUserProfile.once()
            maxHr = profile?.maxHeartRate ?: 190
            ndProfile = profile?.ndProfile ?: NdProfile.STANDARD
            _ndProfileState.value = ndProfile
            userAge = profile?.age ?: 25
            userWeightKg = profile?.weight
            userIsMale = profile?.biologicalSex != "female"
            audioPaletteEnabled = ndProfile == NdProfile.ASD || ndProfile == NdProfile.AUDHD
            dailyTarget = profile?.dailyTarget ?: 12
            treadMode = profile?.treadMode ?: TreadMode.RUNNER
            voiceCoachEngine.setNdProfile(ndProfile)

            // ND-adaptive encouragement frequency
            encouragementIntervalSeconds = when (ndProfile) {
                NdProfile.ASD -> 300      // 5 min — less frequent for sensory comfort
                NdProfile.ADHD -> 90      // 90s — more frequent for engagement
                NdProfile.AUDHD -> 180    // 3 min — balanced default
                else -> 180
            }

            if (ndProfile == NdProfile.ADHD || ndProfile == NdProfile.AUDHD) {
                val streak = profile?.currentStreak ?: 0
                streakMultiplier = 1f + (streak.coerceAtMost(7) * 0.1f)
            }

            // Load best burn points from completed workouts for PB detection
            val completedWorkouts = workoutRepository.getCompletedWorkouts()
            previousBestBurnPoints = completedWorkouts.maxOfOrNull { it.burnPoints } ?: 0

            val workout = workoutRepository.getWorkoutById(workoutId)
            _isJustFiveMin.value = workout?.isJustFiveMin ?: false

            if (audioPaletteEnabled) {
                audioPalette.play(AudioPalette.SoundEvent.WORKOUT_START)
            }

            // Voice coach greeting based on user state
            profile?.let { voiceCoachEngine.onWorkoutStart(it) }
        }

        // Load minimal mode + body double preference
        viewModelScope.launch {
            val prefs = sensoryPreferencesRepository.getPreferencesOnce()
            _isMinimalMode.value = prefs.minimalMode
            _animationLevel.value = prefs.animationLevel
            bodyDoubleActive = prefs.bodyDoubleEnabled
            _bodyDoubleEnabled.value = prefs.bodyDoubleEnabled
            if (bodyDoubleActive) {
                try {
                    bodyDoubleRepository.joinSession()
                    var bodyDoubleAnnounced = false
                    bodyDoubleRepository.getActiveCount().collect { count ->
                        _bodyDoubleCount.value = count
                        // Voice announce body double count once at workout start
                        if (!bodyDoubleAnnounced) {
                            bodyDoubleAnnounced = true
                            if (count > 0) {
                                val msg = if (count == 1) "1 other person is training right now. You're not alone!"
                                    else "$count others are training right now. Let's go!"
                                voiceCoachEngine.onMilestone(msg)
                            } else {
                                voiceCoachEngine.onMilestone("Solo session today. This one's all yours, own it!")
                            }
                        }
                    }
                } catch (_: Exception) {
                    // Body double is non-critical; don't block workout
                }
            }
        }

        // Collect micro-reward events
        viewModelScope.launch {
            microRewardEngine.rewardEvents.collect { event ->
                _rewardEvents.emit(event)
            }
        }

        // Collect drop events
        viewModelScope.launch {
            variableDropEngine.dropEvents.collect { event ->
                _dropEvents.emit(event)
            }
        }

        // Collect transition warnings
        viewModelScope.launch {
            transitionWarningManager.warnings.collect { warning ->
                _transitionWarnings.emit(warning)
            }
        }

        // Timer tick every second
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)

                if (_isPaused.value) continue

                _elapsedSeconds.value++

                val hr = heartRateSource.heartRate.value
                if (hr != null && hr > 0) {
                    _currentHeartRate.value = hr
                    val zone = ZoneCalculator.getZone(hr, maxHr)
                    _currentZone.value = zone

                    // Voice coach + audio palette on zone change
                    if (zone != previousZone) {
                        voiceCoachEngine.onZoneChange(zone)
                        if (audioPaletteEnabled) {
                            val soundEvent = if (zone.ordinal > previousZone.ordinal)
                                AudioPalette.SoundEvent.ZONE_UP else AudioPalette.SoundEvent.ZONE_DOWN
                            audioPalette.play(soundEvent)
                        }
                        if (bodyDoubleActive) {
                            try { bodyDoubleRepository.updateZone(zone.name) } catch (_: Exception) {}
                        }
                        previousZone = zone
                    }

                    // Voice coach time updates every 5 minutes
                    voiceCoachEngine.onTimeUpdate(_elapsedSeconds.value)

                    // Update zone time
                    val current = _zoneTime.value.toMutableMap()
                    current[zone] = (current[zone] ?: 0L) + 1
                    _zoneTime.value = current

                    // Accumulate points
                    val multiplier = if (ndProfile == NdProfile.ADHD || ndProfile == NdProfile.AUDHD) {
                        streakMultiplier
                    } else 1f
                    pointAccumulator += zone.pointsPerMinute / 60.0 * multiplier
                    val newBurnPoints = pointAccumulator.toInt()

                    // Audio palette on point earned
                    if (audioPaletteEnabled && newBurnPoints > previousBurnPoints) {
                        audioPalette.play(AudioPalette.SoundEvent.POINT_EARNED)
                    }
                    previousBurnPoints = newBurnPoints
                    _burnPoints.value = newBurnPoints

                    // Voice coach: daily target hit announcement
                    if (!targetHitAnnounced && newBurnPoints >= dailyTarget) {
                        targetHitAnnounced = true
                        voiceCoachEngine.onTargetHit()
                    }

                    // Voice coach: encouragement in active zones (ND-adaptive interval)
                    val isActiveZone = zone == HeartRateZone.ACTIVE ||
                            zone == HeartRateZone.PUSH ||
                            zone == HeartRateZone.PEAK
                    if (isActiveZone && _elapsedSeconds.value - lastEncouragementSecond >= encouragementIntervalSeconds) {
                        lastEncouragementSecond = _elapsedSeconds.value
                        voiceCoachEngine.onEncouragement()
                    }

                    // Track recent readings (last 60)
                    val readings = _recentReadings.value.toMutableList()
                    readings.add(hr)
                    if (readings.size > 60) readings.removeAt(0)
                    _recentReadings.value = readings

                    // Save reading to DB every 5 seconds
                    if (_elapsedSeconds.value % 5 == 0) {
                        recordHeartRate(workoutId, hr, zone)
                    }

                    // Update running calorie estimate
                    hrSum += hr
                    hrCount++
                    val avgHr = (hrSum / hrCount).toInt()
                    val elapsedMin = _elapsedSeconds.value / 60
                    if (elapsedMin > 0) {
                        val cal = CalorieCalculator.estimate(avgHr, elapsedMin, userAge, userWeightKg, userIsMale)
                        _estimatedCalories.value = cal ?: 0
                    }

                    // Micro-rewards + drops (ADHD feature)
                    if (ndProfile == NdProfile.ADHD || ndProfile == NdProfile.AUDHD) {
                        microRewardEngine.onTick(_elapsedSeconds.value, zone)
                        variableDropEngine.onTick(_elapsedSeconds.value, zone)
                    }

                    // Transition warnings (ASD/AUDHD)
                    if (ndProfile == NdProfile.ASD || ndProfile == NdProfile.AUDHD) {
                        transitionWarningManager.onTick(zone, _elapsedSeconds.value)
                    }
                }

                // Guided workout tick
                guidedWorkoutManager?.onTick(_elapsedSeconds.value)

                _currentChunk.value = (_elapsedSeconds.value / 300) + 1

                if (_isJustFiveMin.value && !_justFiveMinPromptShown.value && _elapsedSeconds.value >= 300) {
                    _justFiveMinPromptShown.value = true
                }
            }
        }
    }

    fun togglePause() {
        _isPaused.value = !_isPaused.value
    }

    fun continueJustFiveMin() {
        _justFiveMinExtended.value = true
        _justFiveMinPromptShown.value = false
    }

    fun endWorkout() {
        timerJob?.cancel()
        heartRateSource.disconnect()
        if (audioPaletteEnabled) {
            audioPalette.play(AudioPalette.SoundEvent.WORKOUT_END)
        }
        if (bodyDoubleActive) {
            viewModelScope.launch { try { bodyDoubleRepository.leaveSession() } catch (_: Exception) {} }
        }
        // Don't shutdown voice engine yet — we need it for completion clips
        viewModelScope.launch {
            endWorkoutUseCase(workoutId, _burnPoints.value, _zoneTime.value)

            val xp = awardXpUseCase(_burnPoints.value, streakMultiplier)
            _xpEarned.value = xp

            val workout = workoutRepository.getWorkoutById(workoutId)
            workout?.let {
                workoutRepository.updateWorkout(it.copy(xpEarned = xp))
            }

            // Voice coach: workout complete announcement
            val hitTarget = _burnPoints.value >= dailyTarget
            val isPersonalBest = _burnPoints.value > previousBestBurnPoints
            voiceCoachEngine.onWorkoutComplete(hitTarget, isPersonalBest)

            val streak = calculateStreakUseCase()

            // Voice coach: queue ONE additional celebration clip (prioritized)
            // to avoid overwhelming users with back-to-back audio at workout end
            val streakMilestones = setOf(3, 5, 7, 10, 14, 21, 30, 100)
            val updatedProfile = getUserProfile.once()
            val totalWorkouts = updatedProfile?.totalWorkouts ?: 0
            val weeklyStats = getWorkoutStats.getWeeklyStats()

            when {
                streak in streakMilestones -> voiceCoachEngine.onStreakMilestone(streak)
                totalWorkouts == 50 -> voiceCoachEngine.onProgressCallout("progress_total_50")
                totalWorkouts == 10 -> voiceCoachEngine.onProgressCallout("progress_total_10")
                weeklyStats.totalWorkouts >= 5 -> voiceCoachEngine.onProgressCallout("progress_week_great")
            }

            // Evaluate daily quest completion
            val pushPeakSeconds = (_zoneTime.value[HeartRateZone.PUSH] ?: 0L) + (_zoneTime.value[HeartRateZone.PEAK] ?: 0L)
            dailyQuestManager.evaluateCompletion(_elapsedSeconds.value, _burnPoints.value, pushPeakSeconds)

            // Check achievement unlocks
            val finalWorkout = workoutRepository.getWorkoutById(workoutId)
            if (updatedProfile != null && finalWorkout != null) {
                val unlocked = checkAchievements(updatedProfile, finalWorkout)
                if (unlocked.isNotEmpty()) {
                    _unlockedAchievements.value = unlocked
                }
            }

            // Record workout for active accountability contracts
            try {
                val contractIds = accountabilityContractRepository.getActiveContractIds()
                contractIds.forEach { contractId ->
                    accountabilityContractRepository.recordWorkout(contractId)
                }
            } catch (_: Exception) {}

            // Record workout for active group challenges
            try {
                val groupIds = groupChallengeRepository.getActiveGroupIds()
                groupIds.forEach { groupId ->
                    groupChallengeRepository.recordWorkoutForGroup(groupId)
                }
            } catch (_: Exception) {}

            _isFinished.value = true
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        heartRateSource.disconnect()
        voiceCoachEngine.shutdown()
        if (bodyDoubleActive) {
            viewModelScope.launch { try { bodyDoubleRepository.leaveSession() } catch (_: Exception) {} }
        }
    }
}
