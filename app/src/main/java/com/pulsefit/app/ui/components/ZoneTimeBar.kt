package com.pulsefit.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.pulsefit.app.data.model.HeartRateZone
import com.pulsefit.app.ui.theme.ZoneActive
import com.pulsefit.app.ui.theme.ZonePeak
import com.pulsefit.app.ui.theme.ZonePush
import com.pulsefit.app.ui.theme.ZoneRest
import com.pulsefit.app.ui.theme.ZoneWarmUp

@Composable
fun ZoneTimeBar(
    zoneTime: Map<HeartRateZone, Long>,
    modifier: Modifier = Modifier
) {
    val zoneColors = mapOf(
        HeartRateZone.REST to ZoneRest,
        HeartRateZone.WARM_UP to ZoneWarmUp,
        HeartRateZone.ACTIVE to ZoneActive,
        HeartRateZone.PUSH to ZonePush,
        HeartRateZone.PEAK to ZonePeak
    )

    val total = zoneTime.values.sum().coerceAtLeast(1)

    Canvas(modifier = modifier.fillMaxSize()) {
        var xOffset = 0f

        HeartRateZone.entries.forEach { zone ->
            val seconds = zoneTime[zone] ?: 0L
            if (seconds > 0) {
                val width = (seconds.toFloat() / total) * size.width
                drawRoundRect(
                    color = zoneColors[zone] ?: ZoneRest,
                    topLeft = Offset(xOffset, 0f),
                    size = Size(width, size.height),
                    cornerRadius = CornerRadius(4f)
                )
                xOffset += width
            }
        }
    }
}
