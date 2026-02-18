package com.example.pulsefit.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.example.pulsefit.ui.theme.ZoneActive
import com.example.pulsefit.ui.theme.ZonePeak
import com.example.pulsefit.ui.theme.ZonePush
import com.example.pulsefit.ui.theme.ZoneRest
import com.example.pulsefit.ui.theme.ZoneWarmUp

@Composable
fun ZoneColorBar(modifier: Modifier = Modifier) {
    val colors = listOf(ZoneRest, ZoneWarmUp, ZoneActive, ZonePush, ZonePeak)

    Canvas(modifier = modifier.fillMaxSize()) {
        val segmentWidth = size.width / colors.size
        val gap = 2f

        colors.forEachIndexed { index, color ->
            drawRoundRect(
                color = color,
                topLeft = Offset(index * segmentWidth + gap / 2, 0f),
                size = Size(segmentWidth - gap, size.height),
                cornerRadius = CornerRadius(4f)
            )
        }
    }
}
