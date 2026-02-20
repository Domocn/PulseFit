package com.example.pulsefit.ui.workout.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pulsefit.data.model.HeartRateZone
import com.example.pulsefit.ui.theme.ZoneActive
import com.example.pulsefit.ui.theme.ZonePeak
import com.example.pulsefit.ui.theme.ZonePush
import com.example.pulsefit.ui.theme.ZoneRest
import com.example.pulsefit.ui.theme.ZoneWarmUp

/**
 * F115: Circular timer that fills over each 5-minute chunk,
 * making elapsed time concrete for ADHD/AUDHD users.
 */
@Composable
fun TimeBlindnessCircle(
    elapsedSeconds: Int,
    currentZone: HeartRateZone,
    modifier: Modifier = Modifier
) {
    val chunkDuration = 300 // 5 minutes in seconds
    val secondsInChunk = elapsedSeconds % chunkDuration
    val progress = secondsInChunk / chunkDuration.toFloat()
    val currentChunk = (elapsedSeconds / chunkDuration) + 1
    val minutesInChunk = secondsInChunk / 60
    val secondsRemainder = secondsInChunk % 60

    val zoneColor = when (currentZone) {
        HeartRateZone.REST -> ZoneRest
        HeartRateZone.WARM_UP -> ZoneWarmUp
        HeartRateZone.ACTIVE -> ZoneActive
        HeartRateZone.PUSH -> ZonePush
        HeartRateZone.PEAK -> ZonePeak
    }

    Box(modifier = modifier.size(100.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(100.dp)) {
            val strokeWidth = 8.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            val topLeft = androidx.compose.ui.geometry.Offset(
                (size.width - radius * 2) / 2,
                (size.height - radius * 2) / 2
            )
            val arcSize = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)

            // Background track
            drawArc(
                color = Color.White.copy(alpha = 0.1f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Progress arc
            drawArc(
                color = zoneColor,
                startAngle = -90f,
                sweepAngle = progress * 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${minutesInChunk}:${String.format("%02d", secondsRemainder)}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = zoneColor
            )
            Text(
                text = "Block $currentChunk",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
