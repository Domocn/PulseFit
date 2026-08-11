package com.pulsefit.app.ui.workout

import androidx.lifecycle.ViewModel
import com.pulsefit.app.data.exercise.StrengthExerciseRegistry
import com.pulsefit.app.data.model.BodyRegion
import com.pulsefit.app.data.model.ExerciseSet
import com.pulsefit.app.data.model.MuscleGroup
import com.pulsefit.app.data.model.StrengthExercise
import com.pulsefit.app.data.model.WorkoutExercise
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class WorkoutBuilderState(
    val workoutName: String = "",
    val exercises: List<BuilderExercise> = emptyList(),
    val availableExercises: List<StrengthExercise> = emptyList(),
    val searchQuery: String = "",
    val selectedMuscleGroup: MuscleGroup? = null,
    val selectedBodyRegion: BodyRegion? = null,
    val isSearching: Boolean = false,
    val error: String? = null
)

data class BuilderExercise(
    val exerciseId: String,
    val sets: MutableList<ExerciseSet> = mutableListOf(),
    val restSeconds: Int = 90,
    val notes: String? = null,
    val supersetGroup: Int? = null,
    val exerciseName: String = "",
    val primaryMuscleLabel: String = ""
)

@HiltViewModel
class WorkoutBuilderViewModel @Inject constructor(
    private val exerciseRegistry: StrengthExerciseRegistry
) : ViewModel() {

    private val _state = MutableStateFlow(WorkoutBuilderState())
    val state: StateFlow<WorkoutBuilderState> = _state

    init {
        _state.value = _state.value.copy(
            availableExercises = exerciseRegistry.getAll()
        )
    }

    fun updateName(name: String) {
        _state.value = _state.value.copy(workoutName = name)
    }

    fun updateSearch(query: String) {
        _state.value = _state.value.copy(
            searchQuery = query,
            isSearching = query.isNotBlank()
        )
    }

    fun filterByMuscleGroup(group: MuscleGroup?) {
        _state.value = _state.value.copy(selectedMuscleGroup = group)
    }

    fun filterByBodyRegion(region: BodyRegion?) {
        _state.value = _state.value.copy(selectedBodyRegion = region)
    }

    fun getFilteredExercises(): List<StrengthExercise> {
        val s = _state.value
        var exercises = s.availableExercises

        if (s.searchQuery.isNotBlank()) {
            exercises = exerciseRegistry.search(s.searchQuery)
        }

        s.selectedMuscleGroup?.let { group ->
            exercises = exercises.filter { it.primaryMuscleGroup == group }
        }

        s.selectedBodyRegion?.let { region ->
            exercises = exercises.filter { it.primaryMuscleGroup.bodyRegion == region }
        }

        return exercises
    }

    fun addExercise(exerciseId: String) {
        val s = _state.value
        if (s.exercises.any { it.exerciseId == exerciseId }) return

        val def = exerciseRegistry.getById(exerciseId)
        val builderExercise = BuilderExercise(
            exerciseId = exerciseId,
            sets = mutableListOf(ExerciseSet(reps = 10)),
            exerciseName = def?.name ?: exerciseId,
            primaryMuscleLabel = def?.primaryMuscleGroup?.label ?: ""
        )
        _state.value = s.copy(
            exercises = s.exercises + builderExercise,
            isSearching = false,
            searchQuery = ""
        )
    }

    fun removeExercise(index: Int) {
        val s = _state.value
        _state.value = s.copy(
            exercises = s.exercises.toMutableList().also { it.removeAt(index) }
        )
    }

    fun moveExercise(fromIndex: Int, toIndex: Int) {
        val s = _state.value
        val list = s.exercises.toMutableList()
        val item = list.removeAt(fromIndex)
        list.add(toIndex, item)
        _state.value = s.copy(exercises = list)
    }

    fun addSet(exerciseIndex: Int) {
        val s = _state.value
        val exercises = s.exercises.toMutableList()
        exercises[exerciseIndex].sets.add(ExerciseSet(reps = 10))
        _state.value = s.copy(exercises = exercises)
    }

    fun removeSet(exerciseIndex: Int, setIndex: Int) {
        val s = _state.value
        val exercises = s.exercises.toMutableList()
        if (exercises[exerciseIndex].sets.size > 1) {
            exercises[exerciseIndex].sets.removeAt(setIndex)
            _state.value = s.copy(exercises = exercises)
        }
    }

    fun updateSet(exerciseIndex: Int, setIndex: Int, reps: Int, weightKg: Float?, isWarmup: Boolean) {
        val s = _state.value
        val exercises = s.exercises.toMutableList()
        val set = exercises[exerciseIndex].sets[setIndex]
        exercises[exerciseIndex].sets[setIndex] = set.copy(
            reps = reps,
            weightKg = weightKg,
            isWarmup = isWarmup
        )
        _state.value = s.copy(exercises = exercises)
    }

    fun updateRestSeconds(exerciseIndex: Int, seconds: Int) {
        val s = _state.value
        val exercises = s.exercises.toMutableList()
        exercises[exerciseIndex] = exercises[exerciseIndex].copy(restSeconds = seconds)
        _state.value = s.copy(exercises = exercises)
    }

    fun buildWorkoutExercises(): List<WorkoutExercise> {
        return _state.value.exercises.map { builder ->
            WorkoutExercise(
                exerciseId = builder.exerciseId,
                sets = builder.sets.toList(),
                restSeconds = builder.restSeconds,
                notes = builder.notes,
                supersetGroup = builder.supersetGroup
            )
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
