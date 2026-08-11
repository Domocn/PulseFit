package com.pulsefit.app.ui.workout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pulsefit.app.data.model.BodyRegion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutBuilderScreen(
    viewModel: WorkoutBuilderViewModel,
    onStartWorkout: (String, List<com.pulsefit.app.data.model.WorkoutExercise>) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showExercisePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Build Workout", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (state.exercises.isNotEmpty()) {
                        Button(
                            onClick = {
                                onStartWorkout(
                                    state.workoutName.ifEmpty { "Custom Workout" },
                                    viewModel.buildWorkoutExercises()
                                )
                            },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text("Start")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        if (showExercisePicker) {
            ExercisePickerSheet(
                viewModel = viewModel,
                onDismiss = { showExercisePicker = false },
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.workoutName,
                        onValueChange = { viewModel.updateName(it) },
                        label = { Text("Workout Name") },
                        placeholder = { Text("e.g., Push Day, Leg Day...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Exercises (${state.exercises.size})",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        OutlinedButton(onClick = { showExercisePicker = true }) {
                            Text("+ Add")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (state.exercises.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No exercises added yet",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Tap '+ Add' to build your workout",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    itemsIndexed(state.exercises) { index, exercise ->
                        BuilderExerciseCard(
                            exercise = exercise,
                            index = index,
                            totalExercises = state.exercises.size,
                            onRemove = { viewModel.removeExercise(index) },
                            onMoveUp = { if (index > 0) viewModel.moveExercise(index, index - 1) },
                            onMoveDown = {
                                if (index < state.exercises.size - 1) {
                                    viewModel.moveExercise(index, index + 1)
                                }
                            },
                            onAddSet = { viewModel.addSet(index) },
                            onRemoveSet = { setIndex -> viewModel.removeSet(index, setIndex) },
                            onUpdateSet = { setIndex, reps, weight, warmup ->
                                viewModel.updateSet(index, setIndex, reps, weight, warmup)
                            },
                            onUpdateRest = { seconds -> viewModel.updateRestSeconds(index, seconds) }
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun BuilderExerciseCard(
    exercise: BuilderExercise,
    index: Int,
    totalExercises: Int,
    onRemove: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onAddSet: () -> Unit,
    onRemoveSet: (Int) -> Unit,
    onUpdateSet: (Int, Int, Float?, Boolean) -> Unit,
    onUpdateRest: (Int) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exercise.exerciseName.ifEmpty { exercise.exerciseId },
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = exercise.primaryMuscleLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Row {
                    IconButton(onClick = onMoveUp, enabled = index > 0) {
                        Text("↑", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = onMoveDown, enabled = index < totalExercises - 1) {
                        Text("↓", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = onRemove) {
                        Text("✕", color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            exercise.sets.forEachIndexed { setIndex, set ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${setIndex + 1}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(20.dp)
                    )

                    var reps by remember(set.reps) { mutableStateOf(set.reps.toString()) }
                    var weight by remember(set.weightKg) {
                        mutableStateOf(
                            if (set.weightKg != null && set.weightKg > 0) set.weightKg.toInt().toString() else ""
                        )
                    }

                    OutlinedTextField(
                        value = reps,
                        onValueChange = {
                            reps = it
                            it.toIntOrNull()?.let { r ->
                                onUpdateSet(setIndex, r, weight.toFloatOrNull(), set.isWarmup)
                            }
                        },
                        label = { Text("Reps") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = weight,
                        onValueChange = {
                            weight = it
                            onUpdateSet(
                                setIndex,
                                reps.toIntOrNull() ?: set.reps,
                                it.toFloatOrNull(),
                                set.isWarmup
                            )
                        },
                        label = { Text("kg") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    if (exercise.sets.size > 1) {
                        IconButton(onClick = { onRemoveSet(setIndex) }) {
                            Text("−", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = onAddSet) {
                    Text("+ Set")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Rest:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    var restText by remember(exercise.restSeconds) {
                        mutableStateOf(exercise.restSeconds.toString())
                    }
                    OutlinedTextField(
                        value = restText,
                        onValueChange = {
                            restText = it
                            it.toIntOrNull()?.let { s -> onUpdateRest(s) }
                        },
                        singleLine = true,
                        modifier = Modifier.width(72.dp)
                    )
                    Text(
                        text = "s",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ExercisePickerSheet(
    viewModel: WorkoutBuilderViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                viewModel.updateSearch(it)
            },
            label = { Text("Search exercises") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = state.selectedBodyRegion == null && state.selectedMuscleGroup == null,
                        onClick = {
                            viewModel.filterByMuscleGroup(null)
                            viewModel.filterByBodyRegion(null)
                        },
                        label = { Text("All") }
                    )
                    BodyRegion.entries.take(3).forEach { region ->
                        FilterChip(
                            selected = state.selectedBodyRegion == region,
                            onClick = {
                                viewModel.filterByBodyRegion(region)
                                viewModel.filterByMuscleGroup(null)
                            },
                            label = { Text(region.label) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            val filteredExercises = viewModel.getFilteredExercises()
            itemsIndexed(filteredExercises) { _, exercise ->
                val alreadyAdded = state.exercises.any { it.exerciseId == exercise.id }
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (alreadyAdded)
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        else MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .clickable(enabled = !alreadyAdded) {
                            viewModel.addExercise(exercise.id)
                            onDismiss()
                        }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = exercise.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (alreadyAdded)
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${exercise.primaryMuscleGroup.label} · ${exercise.equipment.firstOrNull()?.label ?: "None"}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (alreadyAdded) {
                            Text(
                                text = "Added",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
