package com.pulsefit.app.ui.workout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Rowing
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.pulsefit.app.data.exercise.TemplateRegistry
import com.pulsefit.app.data.model.ExerciseStation
import com.pulsefit.app.data.model.TemplateCategory
import com.pulsefit.app.data.model.WorkoutTemplateData

@Composable
fun WorkoutTemplatesScreen(
    onSelectTemplate: ((WorkoutTemplateData) -> Unit)? = null,
    templateRegistry: TemplateRegistry = TemplateRegistry()
) {
    val allTemplates = templateRegistry.getAll()
    val grouped = allTemplates.groupBy { it.category }

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
            for (category in TemplateCategory.entries) {
                val templates = grouped[category] ?: continue

                item(key = "header_${category.name}") {
                    Text(
                        text = category.label,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }

                items(templates, key = { it.id }) { template ->
                    val stations = templateRegistry.getStationsUsed(template)
                    val exerciseCount = templateRegistry.getExerciseCount(template)

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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = template.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                DifficultyDots(difficulty = template.difficulty)
                            }
                            Text(
                                text = template.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (template.durationMinutes > 0) {
                                    Text(
                                        text = "${template.durationMinutes} min",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (exerciseCount > 0) {
                                    Text(
                                        text = "$exerciseCount exercises",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                // Station icons
                                for (station in stations) {
                                    Icon(
                                        imageVector = when (station) {
                                            ExerciseStation.TREAD -> Icons.Default.DirectionsRun
                                            ExerciseStation.ROW -> Icons.Default.Rowing
                                            ExerciseStation.FLOOR -> Icons.Default.FitnessCenter
                                        },
                                        contentDescription = station.label,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DifficultyDots(difficulty: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.semantics(mergeDescendants = true) {
            contentDescription = "Difficulty $difficulty out of 5"
        }
    ) {
        repeat(5) { index ->
            Icon(
                imageVector = Icons.Default.Circle,
                contentDescription = null,
                modifier = Modifier.size(8.dp),
                tint = if (index < difficulty) MaterialTheme.colorScheme.primary
                       else MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}
