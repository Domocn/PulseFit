package com.example.pulsefit.ui.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pulsefit.ui.components.StatCard
import com.example.pulsefit.ui.components.ZoneTimeBar
import com.example.pulsefit.ui.theme.PulsePrimary
import com.example.pulsefit.ui.theme.PulseSecondary
import com.example.pulsefit.util.TimeFormatter

@Composable
fun SummaryScreen(
    workoutId: Long,
    onDone: () -> Unit,
    viewModel: SummaryViewModel = hiltViewModel()
) {
    val workout by viewModel.workout.collectAsState()

    LaunchedEffect(workoutId) {
        viewModel.load(workoutId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // F140: Always the same encouraging message
        Text(
            text = "Workout saved. Well done.",
            style = MaterialTheme.typography.headlineMedium,
            color = PulseSecondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        workout?.let { w ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard(
                    label = "Duration",
                    value = TimeFormatter.formatDuration(w.durationSeconds)
                )
                StatCard(
                    label = "Burn Points",
                    value = "${w.burnPoints}"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard(
                    label = "Avg HR",
                    value = if (w.averageHeartRate > 0) "${w.averageHeartRate}" else "--"
                )
                StatCard(
                    label = "Max HR",
                    value = if (w.maxHeartRate > 0) "${w.maxHeartRate}" else "--"
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Zone Breakdown",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            ZoneTimeBar(
                zoneTime = w.zoneTime,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PulsePrimary)
        ) {
            Text(
                text = "Done",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
