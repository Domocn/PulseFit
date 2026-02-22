package com.pulsefit.app.ui.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Rowing
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pulsefit.app.data.model.ExerciseStation
import com.pulsefit.app.data.model.TemplatePhase
import com.pulsefit.app.data.model.WorkoutTemplateData
import com.pulsefit.app.ui.theme.ZoneActive
import com.pulsefit.app.ui.theme.ZonePeak
import com.pulsefit.app.ui.theme.ZonePush
import com.pulsefit.app.ui.theme.ZoneRest
import com.pulsefit.app.ui.theme.ZoneWarmUp

/**
 * F143: Visual timeline of all phases before starting a templated workout.
 * For ASD profiles so they know exactly what to expect.
 */
@Composable
fun PreWorkoutScheduleScreen(
    templateName: String,
    phases: List<TemplatePhase>,
    onStart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = templateName,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Here's what to expect:",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            phases.forEachIndexed { index, phase ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    // Phase number
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(30.dp)
                    )

                    // Color bar
                    Box(
                        modifier = Modifier
                            .width(8.dp)
                            .height(if (phase.exercises.isNotEmpty()) (48 + phase.exercises.size * 20).dp else 48.dp)
                            .background(
                                when (phase.zoneName) {
                                    "Rest" -> ZoneRest
                                    "Warm-Up" -> ZoneWarmUp
                                    "Active" -> ZoneActive
                                    "Push" -> ZonePush
                                    "Peak" -> ZonePeak
                                    else -> ZoneActive
                                },
                                RoundedCornerShape(4.dp)
                            )
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = phase.name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            // Station icon
                            if (phase.station != null) {
                                Icon(
                                    imageVector = when (phase.station) {
                                        ExerciseStation.TREAD -> Icons.Default.DirectionsRun
                                        ExerciseStation.ROW -> Icons.Default.Rowing
                                        ExerciseStation.FLOOR -> Icons.Default.FitnessCenter
                                    },
                                    contentDescription = phase.station.label,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Text(
                            text = "${phase.durationMinutes} min - ${phase.zoneName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        // Exercise names within phase
                        for (exercise in phase.exercises) {
                            if (exercise.exerciseId.isNotEmpty()) {
                                val name = exercise.exerciseId
                                    .replace("tread_", "").replace("row_", "").replace("floor_", "")
                                    .replace("_", " ")
                                    .replaceFirstChar { it.uppercase() }
                                Text(
                                    text = "- $name",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // Total duration
        val totalMinutes = phases.sumOf { it.durationMinutes }
        Text(
            text = "Total: $totalMinutes minutes",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Start Workout", style = MaterialTheme.typography.titleMedium)
        }
    }
}

data class WorkoutPhase(
    val name: String,
    val durationMinutes: Int,
    val zoneName: String
)
