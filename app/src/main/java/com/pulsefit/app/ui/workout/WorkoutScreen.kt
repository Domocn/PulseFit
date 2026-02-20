package com.pulsefit.app.ui.workout

import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pulsefit.app.data.model.HeartRateZone
import com.pulsefit.app.data.model.NdProfile
import com.pulsefit.app.ui.theme.ZoneActive
import com.pulsefit.app.ui.theme.ZonePeak
import com.pulsefit.app.ui.theme.ZonePush
import com.pulsefit.app.ui.theme.ZoneRest
import com.pulsefit.app.ui.theme.ZoneWarmUp
import com.pulsefit.app.ui.workout.components.BurnPointsDisplay
import com.pulsefit.app.ui.workout.components.HeartRateDisplay
import com.pulsefit.app.ui.workout.components.MiniHeartRateGraph
import com.pulsefit.app.ui.workout.components.TimeBlindnessCircle
import com.pulsefit.app.ui.workout.components.ZoneBar
import com.pulsefit.app.util.TimeFormatter
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
    val isPaused by viewModel.isPaused.collectAsState()
    val isMinimalMode by viewModel.isMinimalMode.collectAsState()
    val unlockedAchievements by viewModel.unlockedAchievements.collectAsState()
    val estimatedCalories by viewModel.estimatedCalories.collectAsState()
    val ndProfile by viewModel.ndProfileState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Overlay text for rewards/drops/warnings
    var overlayText by remember { mutableStateOf<String?>(null) }
    var warningText by remember { mutableStateOf<String?>(null) }

    // Keep screen on via FLAG_KEEP_SCREEN_ON
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val window = (context as? android.app.Activity)?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.start(workoutId)
    }

    LaunchedEffect(isFinished) {
        if (isFinished) onEnd(workoutId)
    }

    // Collect micro-reward events
    LaunchedEffect(Unit) {
        viewModel.rewardEvents.collect { event ->
            overlayText = event.message
            delay(1500)
            overlayText = null
        }
    }

    // Collect drop events
    LaunchedEffect(Unit) {
        viewModel.dropEvents.collect { event ->
            overlayText = event.message
            delay(2000)
            overlayText = null
        }
    }

    // Collect transition warnings
    LaunchedEffect(Unit) {
        viewModel.transitionWarnings.collect { warning ->
            warningText = warning.message
            delay(3000)
            warningText = null
        }
    }

    // Show achievement unlock snackbar
    LaunchedEffect(unlockedAchievements) {
        if (unlockedAchievements.isNotEmpty()) {
            val message = if (unlockedAchievements.size == 1) {
                "Achievement unlocked: ${unlockedAchievements.first()}"
            } else {
                "${unlockedAchievements.size} achievements unlocked!"
            }
            snackbarHostState.showSnackbar(message)
        }
    }

    // If minimal mode, render minimal screen
    if (isMinimalMode) {
        MinimalWorkoutScreen(
            heartRate = heartRate,
            zone = zone,
            elapsedSeconds = elapsed,
            burnPoints = burnPoints,
            onEnd = viewModel::endWorkout
        )
        return
    }

    // Zone-based background tint
    val zoneColor = when (zone) {
        HeartRateZone.REST -> ZoneRest
        HeartRateZone.WARM_UP -> ZoneWarmUp
        HeartRateZone.ACTIVE -> ZoneActive
        HeartRateZone.PUSH -> ZonePush
        HeartRateZone.PEAK -> ZonePeak
    }
    val animatedBgColor by animateColorAsState(
        targetValue = zoneColor.copy(alpha = 0.08f),
        label = "zoneBg"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedBgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Elapsed time + chunk indicator + pause
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = viewModel::togglePause) {
                    Icon(
                        if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = if (isPaused) "Resume" else "Pause",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    text = TimeFormatter.formatDuration(elapsed),
                    style = MaterialTheme.typography.displayMedium,
                    color = if (isPaused) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onBackground
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

            if (isPaused) {
                Text(
                    text = "PAUSED",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = "Block $currentChunk",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Transition warning banner
            AnimatedVisibility(visible = warningText != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Text(
                        text = warningText ?: "",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            ZoneBar(
                currentZone = zone,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Time blindness circle for ADHD/AUDHD profiles
            if (ndProfile == NdProfile.ADHD || ndProfile == NdProfile.AUDHD) {
                TimeBlindnessCircle(
                    elapsedSeconds = elapsed,
                    currentZone = zone
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            HeartRateDisplay(heartRate = heartRate, zone = zone)

            Spacer(modifier = Modifier.height(16.dp))

            MiniHeartRateGraph(
                readings = recentReadings,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BurnPointsDisplay(points = burnPoints)
                if (estimatedCalories > 0) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$estimatedCalories",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "kcal",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
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
                        Text("5 minutes done!", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)
                        Text("You made it. Keep going?", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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

            // End button - no confirmation (F140 Safe Exit)
            Button(
                onClick = viewModel::endWorkout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("End Workout", style = MaterialTheme.typography.titleMedium)
            }
        }

        // Reward/drop pop animation overlay
        AnimatedVisibility(
            visible = overlayText != null,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            overlayText?.let { text ->
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

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
