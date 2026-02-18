package com.example.pulsefit.ui.workout.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.pulsefit.ui.theme.PulsePrimary

@Composable
fun MiniHeartRateGraph(readings: List<Int>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        if (readings.size < 2) return@Canvas

        val minHr = (readings.minOrNull() ?: 60) - 10
        val maxHr = (readings.maxOrNull() ?: 180) + 10
        val range = (maxHr - minHr).coerceAtLeast(1)

        val stepX = size.width / (readings.size - 1).coerceAtLeast(1)

        val path = Path()
        readings.forEachIndexed { index, hr ->
            val x = index * stepX
            val y = size.height - ((hr - minHr).toFloat() / range * size.height)
            if (index == 0) path.moveTo(x, y)
            else path.lineTo(x, y)
        }

        drawPath(
            path = path,
            color = PulsePrimary,
            style = Stroke(width = 3f)
        )

        // Draw current point
        if (readings.isNotEmpty()) {
            val lastX = (readings.size - 1) * stepX
            val lastY = size.height - ((readings.last() - minHr).toFloat() / range * size.height)
            drawCircle(
                color = PulsePrimary,
                radius = 6f,
                center = Offset(lastX, lastY)
            )
        }
    }
}
