package com.example.pulsefit.ui.workout

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pulsefit.ui.theme.ZoneActive
import com.example.pulsefit.ui.theme.ZonePeak
import com.example.pulsefit.ui.theme.ZonePush
import com.example.pulsefit.ui.theme.ZoneRest
import com.example.pulsefit.ui.theme.ZoneWarmUp

/**
 * F143: Visual timeline of all phases before starting a templated workout.
 * For ASD profiles so they know exactly what to expect.
 */
@Composable
fun PreWorkoutScheduleScreen(
    templateName: String,
    phases: List<WorkoutPhase>,
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

        phases.forEachIndexed { index, phase ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
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
                        .height(48.dp)
                        .background(
                            when (phase.zoneName) {
                                "Rest" -> ZoneRest
                                "Warm-Up" -> ZoneWarmUp
                                "Active" -> ZoneActive
                                "Push" -> ZonePush
                                "Peak" -> ZonePeak
                                else -> MaterialTheme.colorScheme.surface
                            },
                            RoundedCornerShape(4.dp)
                        )
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = phase.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${phase.durationMinutes} min - ${phase.zoneName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

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
