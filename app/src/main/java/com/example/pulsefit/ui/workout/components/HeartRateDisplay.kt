package com.example.pulsefit.ui.workout.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import com.example.pulsefit.data.model.HeartRateZone
import com.example.pulsefit.ui.theme.ZoneActive
import com.example.pulsefit.ui.theme.ZonePeak
import com.example.pulsefit.ui.theme.ZonePush
import com.example.pulsefit.ui.theme.ZoneRest
import com.example.pulsefit.ui.theme.ZoneWarmUp

@Composable
fun HeartRateDisplay(heartRate: Int, zone: HeartRateZone) {
    val zoneColor = when (zone) {
        HeartRateZone.REST -> ZoneRest
        HeartRateZone.WARM_UP -> ZoneWarmUp
        HeartRateZone.ACTIVE -> ZoneActive
        HeartRateZone.PUSH -> ZonePush
        HeartRateZone.PEAK -> ZonePeak
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = if (heartRate > 0) "$heartRate" else "--",
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp),
            color = zoneColor
        )
        Text(
            text = "BPM",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = zone.label,
            style = MaterialTheme.typography.titleMedium,
            color = zoneColor
        )
    }
}
