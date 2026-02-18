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
import com.example.pulsefit.ui.workout.components.BurnPointsDisplay
import com.example.pulsefit.ui.workout.components.HeartRateDisplay
import com.example.pulsefit.ui.workout.components.MiniHeartRateGraph
import com.example.pulsefit.ui.workout.components.ZoneBar
import com.example.pulsefit.util.TimeFormatter

@Composable
fun WorkoutScreen(
    workoutId: Long,
    onEnd: (Long) -> Unit,
    viewModel: WorkoutViewModel = hiltViewModel()
) {
    val elapsed by viewModel.elapsedSeconds.collectAsState()
    val heartRate by viewModel.currentHeartRate.collectAsState()
    val zone by viewModel.currentZone.collectAsState()
    val burnPoints by viewModel.burnPoints.collectAsState()
    val recentReadings by viewModel.recentReadings.collectAsState()
    val zoneTime by viewModel.zoneTime.collectAsState()
    val isFinished by viewModel.isFinished.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.start(workoutId)
    }

    LaunchedEffect(isFinished) {
        if (isFinished) {
            onEnd(workoutId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Elapsed time
        Text(
            text = TimeFormatter.formatDuration(elapsed),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Zone bar
        ZoneBar(
            currentZone = zone,
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Heart rate display
        HeartRateDisplay(
            heartRate = heartRate,
            zone = zone
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mini graph
        MiniHeartRateGraph(
            readings = recentReadings,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Burn points
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BurnPointsDisplay(points = burnPoints)
        }

        Spacer(modifier = Modifier.weight(1f))

        // End button - always visible, no confirmation (F140 Safe Exit)
        Button(
            onClick = viewModel::endWorkout,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text(
                text = "End Workout",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
