package com.pulsefit.app.ui.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pulsefit.app.domain.model.MuscleFatigue
import com.pulsefit.app.ui.components.MuscleBodyMap
import com.pulsefit.app.util.MuscleFatigueCalculator
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuscleFatigueScreen(
    viewModel: MuscleFatigueViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val calculator = MuscleFatigueCalculator()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Muscle Fatigue", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(
                        onClick = { viewModel.recalculate() },
                        enabled = !state.isRecalculating,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(if (state.isRecalculating) "..." else "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Body map
            MuscleBodyMap(
                fatigueData = state.fatigueMap,
                selectedMuscle = state.selectedMuscle,
                onMuscleSelected = { viewModel.selectMuscle(it) },
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Selected muscle detail
            state.selectedFatigue?.let { fatigue ->
                MuscleDetailCard(fatigue = fatigue, calculator = calculator)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Summary sections
            if (state.readyMuscles.isNotEmpty()) {
                Text(
                    text = "Ready to Train",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF22C55E),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                state.readyMuscles.forEach { muscle ->
                    MuscleRow(muscle = muscle, calculator = calculator)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (state.fatiguedMuscles.isNotEmpty()) {
                Text(
                    text = "Needs Recovery",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFFEF4444),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                state.fatiguedMuscles.forEach { muscle ->
                    MuscleRow(muscle = muscle, calculator = calculator)
                }
            }

            if (state.allFatigue.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No training data yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Complete a strength workout to see muscle recovery status",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun MuscleDetailCard(fatigue: MuscleFatigue, calculator: MuscleFatigueCalculator) {
    val status = calculator.getRecoveryStatus(fatigue.fatigueScore)
    val statusColor = Color(android.graphics.Color.parseColor(status.colorHex))
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = fatigue.muscleGroup.replace("_", " ").lowercase()
                        .replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = status.label,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = statusColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(
                        text = "Fatigue",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${fatigue.fatigueScore.toInt()}%",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = statusColor
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Recovery",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "~${fatigue.recoveryHoursNeeded}h needed",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            fatigue.lastTrainedAt?.let { timestamp ->
                Spacer(modifier = Modifier.height(8.dp))
                val dateStr = Instant.ofEpochMilli(timestamp)
                    .atZone(ZoneId.systemDefault()).format(formatter)
                Text(
                    text = "Last trained: $dateStr",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (fatigue.lastVolumeKg > 0f) {
                Text(
                    text = "Last volume: ${"%.0f".format(fatigue.lastVolumeKg)}kg",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MuscleRow(muscle: MuscleFatigue, calculator: MuscleFatigueCalculator) {
    val status = calculator.getRecoveryStatus(muscle.fatigueScore)
    val statusColor = Color(android.graphics.Color.parseColor(status.colorHex))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = muscle.muscleGroup.replace("_", " ").lowercase()
                .replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${muscle.fatigueScore.toInt()}%",
                style = MaterialTheme.typography.labelMedium,
                color = statusColor
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = status.label,
                style = MaterialTheme.typography.labelSmall,
                color = statusColor
            )
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
}
