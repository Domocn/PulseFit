package com.example.pulsefit.ui.workout

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pulsefit.data.model.NdProfile
import kotlinx.coroutines.delay

/**
 * F145: Guided wind-down for ASD profiles.
 * Cool-down text (3s) -> Breathing guide (1 min 4-4-4) -> Stretch prompt (30s) -> Proceed to summary
 * Non-ASD/AUDHD profiles skip straight to onComplete.
 */
@Composable
fun ShutdownRoutineScreen(
    onComplete: () -> Unit,
    viewModel: ShutdownRoutineViewModel = hiltViewModel()
) {
    val ndProfile by viewModel.ndProfile.collectAsState()

    // Skip for non-ASD profiles
    if (ndProfile != null && ndProfile != NdProfile.ASD && ndProfile != NdProfile.AUDHD) {
        LaunchedEffect(Unit) { onComplete() }
        return
    }

    // Wait until we know the profile
    if (ndProfile == null) return

    var phase by remember { mutableIntStateOf(0) }
    var timer by remember { mutableIntStateOf(0) }

    LaunchedEffect(phase) {
        timer = 0
        val duration = when (phase) {
            0 -> 3   // Cool-down text
            1 -> 60  // Breathing (4-4-4 cycles)
            2 -> 30  // Stretch prompt
            else -> 0
        }

        if (duration == 0) {
            onComplete()
            return@LaunchedEffect
        }

        repeat(duration) {
            delay(1000)
            timer++
        }

        phase++
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedContent(targetState = phase, label = "shutdown_phase") { currentPhase ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                when (currentPhase) {
                    0 -> {
                        Text(
                            text = "Workout complete.",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Beginning wind-down...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }

                    1 -> {
                        val cyclePhase = (timer % 12)
                        val instruction = when {
                            cyclePhase < 4 -> "Breathe in..."
                            cyclePhase < 8 -> "Hold..."
                            else -> "Breathe out..."
                        }
                        val countdown = when {
                            cyclePhase < 4 -> 4 - cyclePhase
                            cyclePhase < 8 -> 4 - (cyclePhase - 4)
                            else -> 4 - (cyclePhase - 8)
                        }

                        Text(
                            text = "Breathing Exercise",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                            text = instruction,
                            fontSize = 28.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "$countdown",
                            fontSize = 48.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                            text = "${60 - timer}s remaining",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    2 -> {
                        Text(
                            text = "Stretch",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Take a moment to gently stretch.\nShoulders, arms, legs.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                            text = "${30 - timer}s",
                            fontSize = 32.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
