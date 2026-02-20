package com.pulsefit.app.ui.workout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class WorkoutTemplate(
    val name: String,
    val description: String,
    val durationMinutes: Int,
    val type: String
)

val defaultTemplates = listOf(
    WorkoutTemplate("Free Run", "Open-ended workout, end when ready", 0, "FREE"),
    WorkoutTemplate("Quick 15", "15-minute intensity burst", 15, "GUIDED"),
    WorkoutTemplate("Steady State", "30 minutes in Active zone", 30, "GUIDED"),
    WorkoutTemplate("HIIT Intervals", "Alternating Push/Active zones", 20, "GUIDED"),
    WorkoutTemplate("Endurance", "45-minute sustained effort", 45, "GUIDED")
)

@Composable
fun WorkoutTemplatesScreen(
    onSelectTemplate: ((WorkoutTemplate) -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Workout Templates",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(defaultTemplates) { template ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (onSelectTemplate != null) Modifier.clickable { onSelectTemplate(template) }
                            else Modifier
                        ),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = template.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = template.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (template.durationMinutes > 0) {
                            Text(
                                text = "${template.durationMinutes} min",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
