package com.pulsefit.app.ui.routine

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pulsefit.app.data.local.entity.WeeklyRoutineEntity
import kotlin.math.roundToInt

@Composable
fun RoutineBuilderScreen(viewModel: RoutineBuilderViewModel = hiltViewModel()) {
    val routines by viewModel.routines.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = viewModel::addDefaultRoutine,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add routine")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            Text(
                text = "Weekly Routine",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (routines.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No routines set. Tap + to add one.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(routines, key = { it.id }) { routine ->
                        RoutineCard(
                            routine = routine,
                            onUpdate = { dayOfWeek, timeHour, timeMinute, durationMinutes ->
                                viewModel.updateRoutine(routine.id, dayOfWeek, timeHour, timeMinute, durationMinutes)
                            },
                            onDelete = { viewModel.deleteRoutine(routine.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RoutineCard(
    routine: WeeklyRoutineEntity,
    onUpdate: (dayOfWeek: Int, timeHour: Int, timeMinute: Int, durationMinutes: Int) -> Unit,
    onDelete: () -> Unit
) {
    val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    var selectedDay by remember(routine.id, routine.dayOfWeek) { mutableIntStateOf(routine.dayOfWeek) }
    var selectedHour by remember(routine.id, routine.timeHour) { mutableIntStateOf(routine.timeHour) }
    var selectedMinute by remember(routine.id, routine.timeMinute) { mutableIntStateOf(routine.timeMinute) }
    var selectedDuration by remember(routine.id, routine.durationMinutes) { mutableIntStateOf(routine.durationMinutes) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Workout",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Day selection chips
            Text(
                text = "Day",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                dayLabels.forEachIndexed { index, label ->
                    val dayValue = index + 1
                    FilterChip(
                        selected = selectedDay == dayValue,
                        onClick = {
                            selectedDay = dayValue
                            onUpdate(dayValue, selectedHour, selectedMinute, selectedDuration)
                        },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Time selection
            Text(
                text = "Time",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Hour slider
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Hour: ${String.format("%02d", selectedHour)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Slider(
                        value = selectedHour.toFloat(),
                        onValueChange = { selectedHour = it.roundToInt() },
                        onValueChangeFinished = {
                            onUpdate(selectedDay, selectedHour, selectedMinute, selectedDuration)
                        },
                        valueRange = 0f..23f,
                        steps = 22,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = ":",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Minute slider (15-min increments)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Min: ${String.format("%02d", selectedMinute)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Slider(
                        value = selectedMinute.toFloat(),
                        onValueChange = {
                            selectedMinute = (it.roundToInt() / 15) * 15
                        },
                        onValueChangeFinished = {
                            onUpdate(selectedDay, selectedHour, selectedMinute, selectedDuration)
                        },
                        valueRange = 0f..45f,
                        steps = 2,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Duration slider
            Text(
                text = "Duration: ${selectedDuration} min",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Slider(
                value = selectedDuration.toFloat(),
                onValueChange = {
                    selectedDuration = (it.roundToInt() / 5) * 5
                },
                onValueChangeFinished = {
                    onUpdate(selectedDay, selectedHour, selectedMinute, selectedDuration)
                },
                valueRange = 5f..120f,
                steps = 22,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary
                )
            )

            // Summary
            Text(
                text = "${dayLabels.getOrElse(selectedDay - 1) { "" }} at ${String.format("%02d", selectedHour)}:${String.format("%02d", selectedMinute)} for ${selectedDuration} min",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
