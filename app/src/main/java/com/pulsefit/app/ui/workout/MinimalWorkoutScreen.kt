package com.pulsefit.app.ui.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulsefit.app.data.model.HeartRateZone
import com.pulsefit.app.ui.theme.ZoneActive
import com.pulsefit.app.ui.theme.ZonePeak
import com.pulsefit.app.ui.theme.ZonePush
import com.pulsefit.app.ui.theme.ZoneRest
import com.pulsefit.app.ui.theme.ZoneWarmUp
import com.pulsefit.app.util.TimeFormatter

/**
 * F142: Minimal Mode - stripped UI: HR (large center), zone name,
 * elapsed time, burn points only. Black background, max contrast.
 */
@Composable
fun MinimalWorkoutScreen(
    heartRate: Int,
    zone: HeartRateZone,
    elapsedSeconds: Int,
    burnPoints: Int,
    onEnd: () -> Unit
) {
    val zoneColor = when (zone) {
        HeartRateZone.REST -> ZoneRest
        HeartRateZone.WARM_UP -> ZoneWarmUp
        HeartRateZone.ACTIVE -> ZoneActive
        HeartRateZone.PUSH -> ZonePush
        HeartRateZone.PEAK -> ZonePeak
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(32.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Elapsed time
            Text(
                text = TimeFormatter.formatDuration(elapsedSeconds),
                fontSize = 28.sp,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Heart rate - large and center
            Text(
                text = if (heartRate > 0) "$heartRate" else "--",
                fontSize = 96.sp,
                fontWeight = FontWeight.Bold,
                color = zoneColor,
                textAlign = TextAlign.Center
            )
            Text(
                text = "BPM",
                fontSize = 20.sp,
                color = Color.White.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Zone name
            Text(
                text = zone.label,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = zoneColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Burn points
            Text(
                text = "$burnPoints pts",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }

        // End button at bottom
        Button(
            onClick = onEnd,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("End", fontSize = 20.sp)
        }
    }
}
