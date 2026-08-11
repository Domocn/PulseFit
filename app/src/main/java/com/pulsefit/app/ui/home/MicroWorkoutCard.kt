package com.pulsefit.app.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.pulsefit.app.util.MicroWorkoutEngine

@Composable
fun MicroWorkoutCard(
    microWorkout: MicroWorkoutEngine.MicroWorkout,
    onComplete: () -> Unit,
    onShuffle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().semantics {
            contentDescription = "Micro workout: ${microWorkout.name}, ${microWorkout.durationSeconds / 60} minutes"
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Movement Snack", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Text(microWorkout.name, style = MaterialTheme.typography.titleSmall)
                }
                IconButton(onClick = onShuffle) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Show different micro workout")
                }
            }
            Text(microWorkout.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            microWorkout.steps.forEach { step ->
                Text("- $step", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${microWorkout.durationSeconds / 60} min | +${microWorkout.burnPoints} point",
                    style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f))
                Button(onClick = onComplete) { Text("Done") }
            }
        }
    }
}
