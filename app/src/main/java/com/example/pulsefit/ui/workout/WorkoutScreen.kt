package com.example.pulsefit.ui.workout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pulsefit.ui.workout.components.BurnPointsDisplay
import com.example.pulsefit.ui.workout.components.HeartRateDisplay
import com.example.pulsefit.ui.workout.components.MiniHeartRateGraph
import com.example.pulsefit.ui.workout.components.ZoneBar
import com.example.pulsefit.util.TimeFormatter
import kotlinx.coroutines.delay

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
    val isFinished by viewModel.isFinished.collectAsState()
    val isJustFiveMin by viewModel.isJustFiveMin.collectAsState()
    val justFiveMinPromptShown by viewModel.justFiveMinPromptShown.collectAsState()
    val justFiveMinExtended by viewModel.justFiveMinExtended.collectAsState()
    val currentChunk by viewModel.currentChunk.collectAsState()

    // Micro-reward animation state
    var rewardText by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.start(workoutId)
    }

    LaunchedEffect(isFinished) {
        if (isFinished) {
            onEnd(workoutId)
        }
    }

    // Collect micro-reward events
    LaunchedEffect(Unit) {
        viewModel.rewardEvents.collect { event ->
            rewardText = event.message
            delay(1500)
            rewardText = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Elapsed time + chunk indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = TimeFormatter.formatDuration(elapsed),
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (isJustFiveMin && !justFiveMinExtended) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.Default.Timer,
                        contentDescription = "5 min mode",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Time chunk indicator (5-min blocks)
            Text(
                text = "Block $currentChunk",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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

            // Just 5 Min continue prompt
            if (justFiveMinPromptShown) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "5 minutes done!",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "You made it. Keep going?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            OutlinedButton(onClick = viewModel::endWorkout) {
                                Text("I'm done", color = MaterialTheme.colorScheme.onSurface)
                            }
                            Button(
                                onClick = viewModel::continueJustFiveMin,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            ) {
                                Text("Keep going!")
                            }
                        }
                    }
                }
            }

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

        // Micro-reward pop animation overlay
        AnimatedVisibility(
            visible = rewardText != null,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            rewardText?.let { text ->
                Text(
                    text = text,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 32.dp, vertical = 16.dp)
                )
            }
        }
    }
}
