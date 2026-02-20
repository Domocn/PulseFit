package com.pulsefit.app.ui.data

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.pulsefit.app.data.model.HeartRateZone
import com.pulsefit.app.domain.model.Workout
import com.pulsefit.app.ui.components.StatCard

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DeepDataScreen(viewModel: DeepDataViewModel = hiltViewModel()) {
    val workouts by viewModel.filteredWorkouts.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val hrStats by viewModel.hrStats.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val compareA by viewModel.compareA.collectAsState()
    val compareB by viewModel.compareB.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Deep Data",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Date range filter chips
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DateFilter.entries.forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { viewModel.setFilter(filter) },
                    label = { Text(filter.label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Summary stats
        stats?.let { s ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard(label = "Total", value = "${s.totalWorkouts}")
                StatCard(label = "Avg BP", value = "${s.averageBurnPoints}")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard(label = "Total BP", value = "${s.totalBurnPoints}")
                StatCard(label = "Avg Dur", value = "${s.averageDurationSeconds / 60}m")
            }
        }

        // HR stats
        hrStats?.let { hr ->
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard(label = "Mean HR", value = "${hr.meanHr}")
                StatCard(label = "Median HR", value = "${hr.medianHr}")
                StatCard(label = "Std Dev", value = "${hr.stdDevHr}")
            }
        }

        // Comparison panel
        if (compareA != null || compareB != null) {
            Spacer(modifier = Modifier.height(12.dp))
            ComparisonPanel(
                workoutA = compareA,
                workoutB = compareB,
                onClear = viewModel::clearComparison
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Tap to expand. Long-press to compare.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(workouts, key = { it.id }) { workout ->
                val isSelected = compareA?.id == workout.id || compareB?.id == workout.id
                ExpandableWorkoutCard(
                    workout = workout,
                    isSelected = isSelected,
                    onCompare = { viewModel.toggleCompare(workout) }
                )
            }
        }
    }
}

@Composable
private fun ExpandableWorkoutCard(
    workout: Workout,
    isSelected: Boolean,
    onCompare: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${workout.durationSeconds / 60}m ${workout.durationSeconds % 60}s",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Avg HR: ${workout.averageHeartRate} | Max HR: ${workout.maxHeartRate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "${workout.burnPoints} bp",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Expandable zone breakdown
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        text = "Zone Breakdown",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    HeartRateZone.entries.forEach { zone ->
                        val seconds = workout.zoneTime[zone] ?: 0L
                        if (seconds > 0) {
                            val minutes = seconds / 60
                            val secs = seconds % 60
                            val points = (seconds / 60) * zone.pointsPerMinute
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 1.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    zone.label,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    "${minutes}m ${secs}s",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    "$points pts",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // XP + calories
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (workout.xpEarned > 0) {
                            Text(
                                "XP: +${workout.xpEarned}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        workout.estimatedCalories?.let { cal ->
                            Text(
                                "$cal kcal",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedButton(onClick = onCompare, modifier = Modifier.fillMaxWidth()) {
                        Text(if (workout.id == 0L) "Compare" else "Select for comparison")
                    }
                }
            }
        }
    }
}

@Composable
private fun ComparisonPanel(
    workoutA: Workout?,
    workoutB: Workout?,
    onClear: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Comparison",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                OutlinedButton(onClick = onClear) {
                    Text("Clear", style = MaterialTheme.typography.labelSmall)
                }
            }

            if (workoutA != null && workoutB != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("", modifier = Modifier.weight(1f))
                    Text("A", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSecondaryContainer)
                    Text("B", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSecondaryContainer)
                }
                CompareRow("Duration", "${workoutA.durationSeconds / 60}m", "${workoutB.durationSeconds / 60}m")
                CompareRow("Burn Pts", "${workoutA.burnPoints}", "${workoutB.burnPoints}")
                CompareRow("Avg HR", "${workoutA.averageHeartRate}", "${workoutB.averageHeartRate}")
                CompareRow("Max HR", "${workoutA.maxHeartRate}", "${workoutB.maxHeartRate}")
            } else {
                Text(
                    "Select 2 workouts to compare",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
private fun CompareRow(label: String, valueA: String, valueB: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSecondaryContainer)
        Text(valueA, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSecondaryContainer)
        Text(valueB, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSecondaryContainer)
    }
}
