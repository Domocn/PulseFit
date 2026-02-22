package com.pulsefit.app.ui.workout.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.pulsefit.app.data.model.HeartRateZone
import com.pulsefit.app.ui.theme.ZoneActive
import com.pulsefit.app.ui.theme.ZonePeak
import com.pulsefit.app.ui.theme.ZonePush
import com.pulsefit.app.ui.theme.ZoneRest
import com.pulsefit.app.ui.theme.ZoneWarmUp

@Composable
fun ZoneBar(currentZone: HeartRateZone, modifier: Modifier = Modifier) {
    val zones = listOf(
        HeartRateZone.REST to ZoneRest,
        HeartRateZone.WARM_UP to ZoneWarmUp,
        HeartRateZone.ACTIVE to ZoneActive,
        HeartRateZone.PUSH to ZonePush,
        HeartRateZone.PEAK to ZonePeak
    )

    Canvas(modifier = modifier
        .fillMaxSize()
        .semantics { contentDescription = "Heart rate zone: ${currentZone.label}" }
    ) {
        val segmentWidth = size.width / zones.size
        val gap = 4f
        val cornerRadius = 8f

        zones.forEachIndexed { index, (zone, color) ->
            val isActive = zone == currentZone
            val alpha = if (isActive) 1f else 0.3f
            val yOffset = if (isActive) 0f else size.height * 0.15f
            val segHeight = if (isActive) size.height else size.height * 0.7f

            drawRoundRect(
                color = color.copy(alpha = alpha),
                topLeft = Offset(index * segmentWidth + gap / 2, yOffset),
                size = Size(segmentWidth - gap, segHeight),
                cornerRadius = CornerRadius(cornerRadius)
            )
        }
    }
}
