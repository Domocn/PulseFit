package com.pulsefit.app.ui.workout

import com.pulsefit.app.data.exercise.ExerciseRegistry
import com.pulsefit.app.data.model.Exercise
import com.pulsefit.app.data.model.ExerciseStation
import com.pulsefit.app.data.model.PhaseExercise
import com.pulsefit.app.data.model.TemplatePhase
import com.pulsefit.app.data.model.WorkoutTemplateData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class GuidedState(
    val currentExercise: Exercise? = null,
    val currentPhaseExercise: PhaseExercise? = null,
    val nextExercise: Exercise? = null,
    val timeRemainingInExercise: Int = 0,
    val currentPhase: TemplatePhase? = null,
    val currentPhaseName: String = "",
    val stationName: String = "",
    val isTransitioning: Boolean = false,
    val isComplete: Boolean = false,
    val phaseIndex: Int = 0,
    val totalPhases: Int = 0
)

data class FlatExercise(
    val phase: TemplatePhase,
    val phaseIndex: Int,
    val phaseExercise: PhaseExercise,
    val exercise: Exercise?,
    val startSecond: Int,
    val endSecond: Int
)

class GuidedWorkoutManager(
    private val template: WorkoutTemplateData,
    private val exerciseRegistry: ExerciseRegistry
) {
    private val _state = MutableStateFlow(GuidedState())
    val state: StateFlow<GuidedState> = _state

    val flatExercises: List<FlatExercise>
    private val totalDurationSeconds: Int

    // Track for change detection
    private var lastFlatIndex = -1
    private var lastPhaseIndex = -1

    // Track midpoint firing per exercise
    private var midpointFiredForIndex = -1

    /** Callbacks set by WorkoutViewModel */
    var onExerciseChange: ((Exercise, Int, Boolean) -> Unit)? = null
    var onStationChange: ((String) -> Unit)? = null
    var onExerciseMidpoint: ((FlatExercise) -> Unit)? = null

    init {
        val list = mutableListOf<FlatExercise>()
        var offset = 0
        template.phases.forEachIndexed { pIdx, phase ->
            if (phase.exercises.isEmpty()) {
                val dur = phase.durationMinutes * 60
                list.add(FlatExercise(phase, pIdx, PhaseExercise("", dur), null, offset, offset + dur))
                offset += dur
            } else {
                for (pe in phase.exercises) {
                    val ex = exerciseRegistry.getById(pe.exerciseId)
                    list.add(FlatExercise(phase, pIdx, pe, ex, offset, offset + pe.durationSeconds))
                    offset += pe.durationSeconds
                }
            }
        }
        flatExercises = list
        totalDurationSeconds = offset

        if (flatExercises.isNotEmpty()) {
            val first = flatExercises[0]
            val next = flatExercises.getOrNull(1)
            _state.value = GuidedState(
                currentExercise = first.exercise,
                currentPhaseExercise = first.phaseExercise,
                nextExercise = next?.exercise,
                timeRemainingInExercise = first.phaseExercise.durationSeconds,
                currentPhase = first.phase,
                currentPhaseName = first.phase.name,
                stationName = first.phase.station?.label ?: "",
                phaseIndex = first.phaseIndex,
                totalPhases = template.phases.size
            )
        }
    }

    fun onTick(elapsedSeconds: Int) {
        if (flatExercises.isEmpty()) return

        if (elapsedSeconds >= totalDurationSeconds) {
            _state.value = _state.value.copy(isComplete = true)
            return
        }

        val idx = flatExercises.indexOfLast { elapsedSeconds >= it.startSecond }
            .coerceAtLeast(0)
        val current = flatExercises[idx]
        val next = flatExercises.getOrNull(idx + 1)
        val remaining = (current.endSecond - elapsedSeconds).coerceAtLeast(0)

        // Detect exercise change
        if (idx != lastFlatIndex) {
            lastFlatIndex = idx
            midpointFiredForIndex = -1  // Reset midpoint for new exercise
            current.exercise?.let { ex ->
                val isLast = idx == flatExercises.lastIndex
                onExerciseChange?.invoke(ex, current.phaseExercise.durationSeconds, isLast)
            }
        }

        // Detect exercise midpoint
        val duration = current.phaseExercise.durationSeconds
        val elapsed = elapsedSeconds - current.startSecond
        if (duration >= 30 && elapsed >= duration / 2 && idx != midpointFiredForIndex) {
            midpointFiredForIndex = idx
            onExerciseMidpoint?.invoke(current)
        }

        // Detect station/phase change
        if (current.phaseIndex != lastPhaseIndex) {
            val previousStation = if (lastPhaseIndex >= 0) {
                template.phases.getOrNull(lastPhaseIndex)?.station
            } else null
            lastPhaseIndex = current.phaseIndex
            val newStation = current.phase.station
            if (newStation != null && newStation != previousStation) {
                onStationChange?.invoke(newStation.label)
            }
        }

        _state.value = GuidedState(
            currentExercise = current.exercise,
            currentPhaseExercise = current.phaseExercise,
            nextExercise = next?.exercise,
            timeRemainingInExercise = remaining,
            currentPhase = current.phase,
            currentPhaseName = current.phase.name,
            stationName = current.phase.station?.label ?: "",
            isTransitioning = remaining <= 5 && next != null,
            phaseIndex = current.phaseIndex,
            totalPhases = template.phases.size
        )
    }
}
