package com.example.pulsefit.ui.workout.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import com.example.pulsefit.ui.theme.PulseSecondary

@Composable
fun BurnPointsDisplay(points: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$points",
            style = MaterialTheme.typography.displaySmall.copy(fontSize = 48.sp),
            color = PulseSecondary
        )
        Text(
            text = "Burn Points",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
