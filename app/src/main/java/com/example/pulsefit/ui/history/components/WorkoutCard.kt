package com.example.pulsefit.ui.history.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pulsefit.domain.model.Workout
import com.example.pulsefit.ui.components.ZoneTimeBar
import com.example.pulsefit.ui.theme.PulseSecondary
import com.example.pulsefit.ui.theme.PulseSurface
import com.example.pulsefit.util.TimeFormatter
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun WorkoutCard(workout: Workout, onClick: () -> Unit) {
    val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy  HH:mm")
        .withZone(ZoneId.systemDefault())

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PulseSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = dateFormatter.format(workout.startTime),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${workout.burnPoints} pts",
                    style = MaterialTheme.typography.titleMedium,
                    color = PulseSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = TimeFormatter.formatDuration(workout.durationSeconds),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (workout.averageHeartRate > 0) "Avg ${workout.averageHeartRate} bpm" else "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            ZoneTimeBar(
                zoneTime = workout.zoneTime,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
        }
    }
}
