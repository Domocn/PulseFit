package com.pulsefit.app.ui.workout.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Rowing
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.pulsefit.app.data.model.ExerciseStation
import com.pulsefit.app.ui.workout.GuidedState

@Composable
fun ExerciseGuideOverlay(
    guidedState: GuidedState,
    modifier: Modifier = Modifier
) {
    val exercise = guidedState.currentExercise
    val remaining = guidedState.timeRemainingInExercise
    val minutes = remaining / 60
    val seconds = remaining % 60
    val timeText = if (minutes > 0) "${minutes}:%02d".format(seconds) else "${seconds}s"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Station chip + phase info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (guidedState.stationName.isNotEmpty()) {
                val station = ExerciseStation.entries.find { it.label == guidedState.stationName }
                SuggestionChip(
                    onClick = {},
                    label = { Text(guidedState.stationName) },
                    icon = {
                        Icon(
                            imageVector = when (station) {
                                ExerciseStation.TREAD -> Icons.Default.DirectionsRun
                                ExerciseStation.ROW -> Icons.Default.Rowing
                                ExerciseStation.FLOOR -> Icons.Default.FitnessCenter
                                else -> Icons.Default.FitnessCenter
                            },
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
            Text(
                text = "${guidedState.phaseIndex + 1}/${guidedState.totalPhases}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Exercise animation or fallback icon
        AnimatedContent(
            targetState = exercise?.id ?: "",
            transitionSpec = { fadeIn() + slideInVertically() togetherWith fadeOut() + slideOutVertically() },
            label = "exerciseAnim"
        ) { exerciseId ->
            Box(
                modifier = Modifier
                    .size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                val lottieAsset = exercise?.lottieAsset
                if (lottieAsset != null) {
                    val composition by rememberLottieComposition(
                        LottieCompositionSpec.Asset(lottieAsset)
                    )
                    LottieAnimation(
                        composition = composition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier.size(80.dp)
                    )
                } else {
                    // Fallback: station icon
                    val station = exercise?.station
                    Icon(
                        imageVector = when (station) {
                            ExerciseStation.TREAD -> Icons.Default.DirectionsRun
                            ExerciseStation.ROW -> Icons.Default.Rowing
                            ExerciseStation.FLOOR -> Icons.Default.FitnessCenter
                            else -> Icons.Default.FitnessCenter
                        },
                        contentDescription = exercise?.name,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Exercise name
        Text(
            text = exercise?.name ?: guidedState.currentPhaseName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Description
        if (exercise != null) {
            Text(
                text = exercise.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Time remaining
        Text(
            text = timeText,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = if (guidedState.isTransitioning) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary
        )

        // Next up preview
        if (guidedState.nextExercise != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Next: ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = guidedState.nextExercise.name,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
