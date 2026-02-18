package com.example.pulsefit.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.pulsefit.ui.theme.PulsePrimary
import com.example.pulsefit.ui.theme.PulseSurfaceVariant

@Composable
fun BurnPointsRing(current: Int, target: Int, modifier: Modifier = Modifier) {
    val progress = if (target > 0) (current.toFloat() / target).coerceIn(0f, 1f) else 0f

    Canvas(modifier = modifier) {
        val strokeWidth = 16f
        val radius = (size.minDimension - strokeWidth) / 2
        val topLeft = Offset(
            (size.width - radius * 2) / 2,
            (size.height - radius * 2) / 2
        )
        val arcSize = Size(radius * 2, radius * 2)

        // Background ring
        drawArc(
            color = PulseSurfaceVariant,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Progress ring
        drawArc(
            color = PulsePrimary,
            startAngle = -90f,
            sweepAngle = 360f * progress,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}
