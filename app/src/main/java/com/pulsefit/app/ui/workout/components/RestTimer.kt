package com.pulsefit.app.ui.workout.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulsefit.app.data.model.AnimationLevel
import com.pulsefit.app.data.model.NdProfile

/**
 * Rest timer with ND-aware display.
 * - ADHD mode: animated countdown ring with pulsing text
 * - ASD mode: exact seconds remaining, no animation surprises
 * - Standard: clean countdown with subtle ring
 */
@Composable
fun RestTimer(
    totalSeconds: Int,
    remainingSeconds: Int,
    isRunning: Boolean,
    ndProfile: NdProfile,
    animationLevel: AnimationLevel,
    onSkip: () -> Unit,
    onAddTime: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (totalSeconds > 0) {
        remainingSeconds.toFloat() / totalSeconds.toFloat()
    } else 0f

    val shouldAnimate = animationLevel != AnimationLevel.OFF && ndProfile != NdProfile.ASD

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = if (shouldAnimate) tween(600) else tween(0),
        label = "restProgress"
    )

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timeText = String.format("%d:%02d", minutes, seconds)

    val ringColor = when {
        progress > 0.5f -> Color(0xFF22C55E) // Green — plenty of rest
        progress > 0.25f -> Color(0xFFF97316) // Orange — halfway
        else -> Color(0xFFEF4444) // Red — rest almost over
    }

    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Countdown ring
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(160.dp)
        ) {
            // Background track
            Canvas(modifier = Modifier.size(160.dp)) {
                val strokeWidth = 10.dp.toPx()
                val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
                val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)

                drawArc(
                    color = trackColor,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Progress arc
                drawArc(
                    color = ringColor,
                    startAngle = -90f,
                    sweepAngle = animatedProgress * 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = timeText,
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = if (ndProfile == NdProfile.ASD) 36.sp else 32.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (isRunning) {
                    Text(
                        text = "REST",
                        style = MaterialTheme.typography.bodySmall,
                        color = ringColor
                    )
                } else {
                    Text(
                        text = "PAUSED",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Halfway warning (ADHD-friendly)
        if (isRunning && progress < 0.55f && progress > 0.45f) {
            Text(
                text = "Halfway — ${timeText} remaining",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFF97316),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Action buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = onSkip,
                modifier = Modifier.weight(1f)
            ) {
                Text("Skip")
            }

            Button(
                onClick = onAddTime,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text("+30s")
            }
        }
    }
}

/**
 * Compact rest timer bar for use in the set logger or exercise view.
 * Shows a thin progress bar with time remaining.
 */
@Composable
fun RestTimerBar(
    totalSeconds: Int,
    remainingSeconds: Int,
    isRunning: Boolean,
    ndProfile: NdProfile,
    modifier: Modifier = Modifier
) {
    val progress = if (totalSeconds > 0) {
        remainingSeconds.toFloat() / totalSeconds.toFloat()
    } else 0f

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timeText = String.format("%d:%02d", minutes, seconds)

    val barColor = when {
        progress > 0.5f -> Color(0xFF22C55E)
        progress > 0.25f -> Color(0xFFF97316)
        else -> Color(0xFFEF4444)
    }

    val barTrackColor = MaterialTheme.colorScheme.surfaceVariant

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isRunning) "Rest" else "Paused",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = timeText,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = barColor
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Progress bar
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
        ) {
            // Track
            drawRoundRect(
                color = barTrackColor,
                size = size,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
            )
            // Progress
            drawRoundRect(
                color = barColor,
                size = Size(size.width * progress, size.height),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
            )
        }
    }
}
