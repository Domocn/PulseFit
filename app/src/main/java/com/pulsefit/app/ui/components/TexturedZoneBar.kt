package com.pulsefit.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.dp
import com.pulsefit.app.data.model.HeartRateZone
import com.pulsefit.app.ui.theme.ZoneActive
import com.pulsefit.app.ui.theme.ZonePeak
import com.pulsefit.app.ui.theme.ZonePush
import com.pulsefit.app.ui.theme.ZoneRest
import com.pulsefit.app.ui.theme.ZoneWarmUp

/**
 * F141: Zone bars with distinct patterns for ASD profiles.
 * Dotted (Rest), Lines (Warm-Up), Hatching (Active), Cross-hatch (Push), Solid (Peak)
 */
@Composable
fun TexturedZoneBar(
    zoneTime: Map<HeartRateZone, Long>,
    modifier: Modifier = Modifier
) {
    val totalTime = zoneTime.values.sum().coerceAtLeast(1)
    val zones = HeartRateZone.entries.toList()

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(32.dp)
    ) {
        var xOffset = 0f

        for (zone in zones) {
            val seconds = zoneTime[zone] ?: 0L
            if (seconds == 0L) continue

            val width = (seconds.toFloat() / totalTime) * size.width
            val color = zoneColor(zone)

            // Base fill
            drawRect(
                color = color.copy(alpha = 0.3f),
                topLeft = Offset(xOffset, 0f),
                size = androidx.compose.ui.geometry.Size(width, size.height)
            )

            // Pattern overlay
            when (zone) {
                HeartRateZone.REST -> {
                    // Dotted
                    val dotSpacing = 8f
                    var dx = xOffset + 4f
                    while (dx < xOffset + width) {
                        var dy = 4f
                        while (dy < size.height) {
                            drawCircle(color, radius = 2f, center = Offset(dx, dy))
                            dy += dotSpacing
                        }
                        dx += dotSpacing
                    }
                }
                HeartRateZone.WARM_UP -> {
                    // Horizontal lines
                    val lineSpacing = 6f
                    var dy = lineSpacing
                    while (dy < size.height) {
                        drawLine(
                            color, Offset(xOffset, dy), Offset(xOffset + width, dy),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
                        )
                        dy += lineSpacing
                    }
                }
                HeartRateZone.ACTIVE -> {
                    // Diagonal lines (hatching)
                    val step = 8f
                    var dx = xOffset
                    while (dx < xOffset + width + size.height) {
                        drawLine(color, Offset(dx, 0f), Offset(dx - size.height, size.height))
                        dx += step
                    }
                }
                HeartRateZone.PUSH -> {
                    // Cross-hatch
                    val step = 8f
                    var dx = xOffset
                    while (dx < xOffset + width + size.height) {
                        drawLine(color, Offset(dx, 0f), Offset(dx - size.height, size.height))
                        drawLine(color, Offset(dx - size.height, 0f), Offset(dx, size.height))
                        dx += step
                    }
                }
                HeartRateZone.PEAK -> {
                    // Solid fill
                    drawRect(
                        color = color,
                        topLeft = Offset(xOffset, 0f),
                        size = androidx.compose.ui.geometry.Size(width, size.height)
                    )
                }
            }

            xOffset += width
        }
    }
}

private fun zoneColor(zone: HeartRateZone): Color {
    return when (zone) {
        HeartRateZone.REST -> ZoneRest
        HeartRateZone.WARM_UP -> ZoneWarmUp
        HeartRateZone.ACTIVE -> ZoneActive
        HeartRateZone.PUSH -> ZonePush
        HeartRateZone.PEAK -> ZonePeak
    }
}
