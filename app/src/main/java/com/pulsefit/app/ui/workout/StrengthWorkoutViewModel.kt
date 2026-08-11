package com.pulsefit.app.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefit.app.data.exercise.StrengthExerciseRegistry
import com.pulsefit.app.data.model.ExerciseSet
import com.pulsefit.app.data.model.StrengthExercise
import com.pulsefit.app.data.model.WorkoutExercise
import com.pulsefit.app.domain.model.ExerciseLog
import com.pulsefit.app.domain.model.PersonalRecord
import com.pulsefit.app.domain.repository.ExerciseLogRepository
import com.pulsefit.app.domain.repository.MuscleFatigueRepository
import com.pulsefit.app.domain.repository.PersonalRecordRepository
import com.pulsefit.app.domain.repository.WorkoutRepository
import com.pulsefit.app.util.MuscleFatigueCalculator
import com.pulsefit.app.util.OneRmCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

data class StrengthWorkoutState(
    val exercises: List<WorkoutExercise> = emptyList(),
    val exerciseDefs: Map<String, StrengthExercise> = emptyMap(),
    val currentExerciseIndex: Int = 0,
    val currentSetIndex: Int = 0,
    val isResting: Boolean = false,
    val restSecondsRemaining: Int = 0,
    val restTotalSeconds: Int = 90,
    val isWorkoutActive: Boolean = false,
    val workoutId: Long? = null,
    val workoutName: String = "",
    val completedSets: List<LoggedSet> = emptyList(),
    val previousLogs: Map<String, List<ExerciseLog>> = emptyMap(),
    val personalRecords: Map<String, PersonalRecord?> = emptyMap(),
    val totalVolumeKg: Float = 0f,
    val totalSetsCompleted: Int = 0,
    val isFinishing: Boolean = false,
    val error: String? = null
)

data class LoggedSet(
    val exerciseId: String,
    val exerciseName: String,
    val setNumber: Int,
    val reps: Int,
    val weightKg: Float?,
    val rpe: Int?,
    val isWarmup: Boolean,
    val isDropSet: Boolean,
    val timestamp: Instant = Instant.now()
)

@HiltViewModel
class StrengthWorkoutViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val exerciseLogRepository: ExerciseLogRepository,
    private val personalRecordRepository: PersonalRecordRepository,
    private val muscleFatigueRepository: MuscleFatigueRepository,
    private val oneRmCalculator: OneRmCalculator,
    private val fatigueCalculator: MuscleFatigueCalculator,
    private val exerciseRegistry: StrengthExerciseRegistry
) : ViewModel() {

    private val _state = MutableStateFlow(StrengthWorkoutState())
    val state: StateFlow<StrengthWorkoutState> = _state

    private var restTimerJob: Job? = null

    fun startWorkout(exercises: List<WorkoutExercise>, name: String) {
        viewModelScope.launch {
            val defs = mutableMapOf<String, StrengthExercise>()
            for (ex in exercises) {
                exerciseRegistry.getById(ex.exerciseId)?.let { defs[ex.exerciseId] = it }
            }
            _state.value = _state.value.copy(
                exercises = exercises,
                exerciseDefs = defs,
                workoutName = name,
                isWorkoutActive = true,
                currentExerciseIndex = 0,
                currentSetIndex = 0,
                completedSets = emptyList(),
                totalVolumeKg = 0f,
                totalSetsCompleted = 0,
                error = null
            )
            loadPreviousData()
        }
    }

    fun getCurrentExercise(): WorkoutExercise? {
        val s = _state.value
        return s.exercises.getOrNull(s.currentExerciseIndex)
    }

    fun getCurrentExerciseDef(): StrengthExercise? {
        val ex = getCurrentExercise() ?: return null
        return _state.value.exerciseDefs[ex.exerciseId]
    }

    fun getCurrentSet(): ExerciseSet? {
        val ex = getCurrentExercise() ?: return null
        return ex.sets.getOrNull(_state.value.currentSetIndex)
    }

    fun logSet(reps: Int, weightKg: Float?, rpe: Int?, isWarmup: Boolean, isDropSet: Boolean) {
        val s = _state.value
        val exercise = getCurrentExercise() ?: return
        val exerciseDef = getCurrentExerciseDef()

        val loggedSet = LoggedSet(
            exerciseId = exercise.exerciseId,
            exerciseName = exerciseDef?.name ?: exercise.exerciseId,
            setNumber = s.currentSetIndex + 1,
            reps = reps,
            weightKg = weightKg,
            rpe = rpe,
            isWarmup = isWarmup,
            isDropSet = isDropSet
        )

        val volumeFromSet = (weightKg ?: 0f) * reps

        _state.value = s.copy(
            completedSets = s.completedSets + loggedSet,
            totalVolumeKg = s.totalVolumeKg + volumeFromSet,
            totalSetsCompleted = s.totalSetsCompleted + 1
        )

        // Check for PR
        if (weightKg != null && reps in 1..12) {
            viewModelScope.launch {
                val estimatedOneRm = oneRmCalculator.brzycki(weightKg, reps)
                val currentBest = personalRecordRepository.getCurrentBest(exercise.exerciseId)
                if (currentBest == null || estimatedOneRm > currentBest.estimatedOneRmKg) {
                    personalRecordRepository.insert(
                        PersonalRecord(
                            exerciseId = exercise.exerciseId,
                            exerciseName = exerciseDef?.name ?: exercise.exerciseId,
                            estimatedOneRmKg = estimatedOneRm,
                            basedOnWeightKg = weightKg,
                            basedOnReps = reps,
                            formula = "Brzycki",
                            timestamp = Instant.now()
                        )
                    )
                }
            }
        }

        advanceAfterSet()
    }

    fun skipSet() {
        advanceAfterSet()
    }

    private fun advanceAfterSet() {
        val s = _state.value
        val exercise = getCurrentExercise() ?: return

        val nextSetIndex = s.currentSetIndex + 1

        if (nextSetIndex < exercise.sets.size) {
            _state.value = s.copy(
                currentSetIndex = nextSetIndex,
                isResting = true,
                restSecondsRemaining = exercise.restSeconds,
                restTotalSeconds = exercise.restSeconds
            )
            startRestTimer()
        } else {
            val nextExerciseIndex = s.currentExerciseIndex + 1
            if (nextExerciseIndex < s.exercises.size) {
                _state.value = s.copy(
                    currentExerciseIndex = nextExerciseIndex,
                    currentSetIndex = 0,
                    isResting = true,
                    restSecondsRemaining = s.exercises[nextExerciseIndex].restSeconds,
                    restTotalSeconds = s.exercises[nextExerciseIndex].restSeconds
                )
                startRestTimer()
            } else {
                finishWorkout()
            }
        }
    }

    fun skipRest() {
        restTimerJob?.cancel()
        _state.value = _state.value.copy(isResting = false, restSecondsRemaining = 0)
    }

    fun addRestTime(seconds: Int = 30) {
        val s = _state.value
        _state.value = s.copy(
            restSecondsRemaining = s.restSecondsRemaining + seconds,
            restTotalSeconds = s.restTotalSeconds + seconds
        )
    }

    private fun startRestTimer() {
        restTimerJob?.cancel()
        restTimerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                val current = _state.value
                if (current.restSecondsRemaining <= 1) {
                    _state.value = current.copy(isResting = false, restSecondsRemaining = 0)
                    break
                } else {
                    _state.value = current.copy(restSecondsRemaining = current.restSecondsRemaining - 1)
                }
            }
        }
    }

    fun finishWorkout() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isFinishing = true)

            try {
                val s = _state.value

                val workout = com.pulsefit.app.domain.model.Workout(
                    startTime = Instant.now().minusSeconds(s.completedSets.size * 120L),
                    endTime = Instant.now(),
                    durationSeconds = s.completedSets.size * 120,
                    workoutMode = "STRENGTH",
                    totalVolumeKg = s.totalVolumeKg,
                    totalSets = s.totalSetsCompleted,
                    totalReps = s.completedSets.sumOf { it.reps.toInt() }
                )

                val workoutId = workoutRepository.createWorkout(workout)

                val exerciseGroups = s.completedSets.groupBy { it.exerciseId }
                for ((exerciseId, sets) in exerciseGroups) {
                    val exerciseDef = s.exerciseDefs[exerciseId]
                    val log = ExerciseLog(
                        workoutId = workoutId,
                        exerciseId = exerciseId,
                        exerciseName = exerciseDef?.name ?: exerciseId,
                        primaryMuscleGroup = exerciseDef?.primaryMuscleGroup?.name ?: "",
                        setsCompleted = sets.size,
                        setsPlanned = s.exercises
                            .find { it.exerciseId == exerciseId }?.sets?.size ?: sets.size,
                        maxWeightKg = sets.maxOfOrNull { it.weightKg ?: 0f },
                        totalVolumeKg = sets.fold(0f) { acc, set -> acc + (set.weightKg ?: 0f) * set.reps },
                        bestSetReps = sets.maxOfOrNull { it.reps },
                        bestSetWeightKg = sets.maxOfOrNull { it.weightKg ?: 0f },
                        averageRpe = sets.mapNotNull { it.rpe?.toFloat() }.average()
                            .toFloat().takeIf { !it.isNaN() },
                        timestamp = Instant.now()
                    )
                    exerciseLogRepository.insert(log)
                }

                muscleFatigueRepository.recalculateAll()

                _state.value = _state.value.copy(
                    isWorkoutActive = false,
                    isFinishing = false,
                    workoutId = workoutId
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isFinishing = false,
                    error = e.message ?: "Failed to save workout"
                )
            }
        }
    }

    private suspend fun loadPreviousData() {
        val s = _state.value
        val previousLogs = mutableMapOf<String, List<ExerciseLog>>()
        val personalRecords = mutableMapOf<String, PersonalRecord?>()

        for (exercise in s.exercises) {
            val logs = exerciseLogRepository.getByExerciseId(exercise.exerciseId)
            previousLogs[exercise.exerciseId] = logs
            personalRecords[exercise.exerciseId] = personalRecordRepository.getCurrentBest(exercise.exerciseId)
        }

        _state.value = s.copy(
            previousLogs = previousLogs,
            personalRecords = personalRecords
        )
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
