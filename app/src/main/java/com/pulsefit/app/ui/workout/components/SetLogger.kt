package com.pulsefit.app.ui.workout.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pulsefit.app.data.model.ExerciseSet
import com.pulsefit.app.data.model.NdProfile

/**
 * Set logger for recording reps, weight, and RPE during a strength workout.
 * ND-aware: large touch targets, clear labels, no ambiguous icons.
 */
@Composable
fun SetLogger(
    setNumber: Int,
    totalSets: Int,
    previousWeightKg: Float?,
    previousReps: Int?,
    ndProfile: NdProfile,
    onLogSet: (ExerciseSet) -> Unit,
    onSkipSet: () -> Unit,
    modifier: Modifier = Modifier
) {
    var reps by remember { mutableIntStateOf(previousReps ?: 10) }
    var weightKg by remember { mutableFloatStateOf(previousWeightKg ?: 0f) }
    var rpe by remember { mutableFloatStateOf(7f) }
    var isWarmup by remember { mutableStateOf(false) }
    var isDropSet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Set header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Set $setNumber of $totalSets",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            if (previousWeightKg != null && previousReps != null) {
                Text(
                    text = "Last: ${previousWeightKg.toInt()}kg × $previousReps",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Reps input
        OutlinedTextField(
            value = reps.toString(),
            onValueChange = { it.toIntOrNull()?.let { r -> if (r in 0..100) reps = r } },
            label = { Text("Reps") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Weight input
        OutlinedTextField(
            value = if (weightKg == 0f) "" else String.format("%.1f", weightKg),
            onValueChange = { it.toFloatOrNull()?.let { w -> if (w >= 0f) weightKg = w } },
            label = { Text("Weight (kg)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // RPE slider
        Text(
            text = "RPE: ${rpe.toInt()} — ${rpeLabel(rpe.toInt())}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Slider(
            value = rpe,
            onValueChange = { rpe = it },
            valueRange = 1f..10f,
            steps = 8,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Toggles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = isWarmup,
                    onCheckedChange = { isWarmup = it },
                    colors = CheckboxDefaults.colors(
                        checkmarkColor = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = "Warm-up",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = isDropSet,
                    onCheckedChange = { isDropSet = it },
                    colors = CheckboxDefaults.colors(
                        checkmarkColor = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = "Drop set",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { onSkipSet() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text("Skip")
            }

            Button(
                onClick = {
                    onLogSet(
                        ExerciseSet(
                            reps = reps,
                            weightKg = if (weightKg > 0f) weightKg else null,
                            isWarmup = isWarmup,
                            isDropSet = isDropSet,
                            rpe = rpe.toInt(),
                            completed = true,
                            actualReps = reps,
                            actualWeightKg = if (weightKg > 0f) weightKg else null
                        )
                    )
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Log Set")
            }
        }
    }
}

private fun rpeLabel(rpe: Int): String = when (rpe) {
    1 -> "Very light"
    2 -> "Light"
    3 -> "Moderate"
    4 -> "Somewhat hard"
    5 -> "Hard"
    6 -> "Hard+"
    7 -> "Very hard"
    8 -> "Very hard+"
    9 -> "Near max"
    10 -> "Max effort"
    else -> ""
}
