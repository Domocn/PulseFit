package com.example.pulsefit.ui.workout

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pulsefit.data.model.HeartRateZone
import com.example.pulsefit.domain.model.HeartRateReading
import com.example.pulsefit.ui.components.CelebrationOverlay
import com.example.pulsefit.ui.components.StatCard
import com.example.pulsefit.ui.components.ZoneTimeBar
import com.example.pulsefit.ui.theme.ZoneActive
import com.example.pulsefit.ui.theme.ZonePeak
import com.example.pulsefit.ui.theme.ZonePush
import com.example.pulsefit.ui.theme.ZoneRest
import com.example.pulsefit.ui.theme.ZoneWarmUp
import com.example.pulsefit.util.TimeFormatter

@Composable
fun SummaryScreen(
    workoutId: Long,
    onDone: () -> Unit,
    viewModel: SummaryViewModel = hiltViewModel()
) {
    val workout by viewModel.workout.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val epoc by viewModel.epocEstimate.collectAsState()
    val readings by viewModel.readings.collectAsState()
    val targetHit by viewModel.targetHit.collectAsState()
    val dailyTarget by viewModel.dailyTarget.collectAsState()
    val maxHr by viewModel.maxHr.collectAsState()
    val celebrationConfig by viewModel.celebrationConfig.collectAsState()
    val coachTip by viewModel.coachTip.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(workoutId) {
        viewModel.load(workoutId)
    }

    Box(modifier = Modifier.fillMaxSize()) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // F140: Always the same encouraging message
        Text(
            text = "Workout saved. Well done.",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        // Target hit celebration
        if (targetHit) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Target hit",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.padding(start = 8.dp))
                    Text(
                        text = "Daily target of $dailyTarget Burn Points reached!",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        workout?.let { w ->
            // Share button row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = {
                    val shareText = buildString {
                        append("PulseFit Workout\n")
                        append("Duration: ${TimeFormatter.formatDuration(w.durationSeconds)}\n")
                        append("Burn Points: ${w.burnPoints}\n")
                        if (w.averageHeartRate > 0) append("Avg HR: ${w.averageHeartRate} bpm\n")
                        if (w.maxHeartRate > 0) append("Max HR: ${w.maxHeartRate} bpm\n")
                        w.estimatedCalories?.let { append("Calories: $it kcal\n") }
                        if (w.xpEarned > 0) append("XP Earned: +${w.xpEarned}\n")
                        if (targetHit) append("Daily target hit!")
                    }
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "Share Workout"))
                }) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Share workout",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard(label = "Duration", value = TimeFormatter.formatDuration(w.durationSeconds))
                StatCard(label = "Burn Points", value = "${w.burnPoints}")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard(label = "Avg HR", value = if (w.averageHeartRate > 0) "${w.averageHeartRate}" else "--")
                StatCard(label = "Max HR", value = if (w.maxHeartRate > 0) "${w.maxHeartRate}" else "--")
            }

            // XP + Calories row
            if (w.xpEarned > 0 || w.estimatedCalories != null) {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (w.xpEarned > 0) {
                        StatCard(label = "XP Earned", value = "+${w.xpEarned}")
                    }
                    w.estimatedCalories?.let { cal ->
                        StatCard(label = "Est. Calories", value = "$cal kcal")
                    }
                }
            }

            // EPOC estimate
            if (epoc > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("EPOC Estimate", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                        Text(
                            text = "$epoc extra kcal burned over the next 24h",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // HR line chart
            if (readings.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Heart Rate Over Time", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(12.dp))
                HeartRateLineChart(
                    readings = readings,
                    maxHr = maxHr,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Zone Breakdown", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(12.dp))

            ZoneTimeBar(
                zoneTime = w.zoneTime,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Zone detail table
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Zone", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                        Text("Time", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                        Text("Points", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    HeartRateZone.entries.forEach { zone ->
                        val seconds = w.zoneTime[zone] ?: 0L
                        val minutes = seconds / 60
                        val points = (seconds / 60) * zone.pointsPerMinute
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(zone.label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                            Text("${minutes}m", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                            Text("$points", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // Coach tip
            coachTip?.let { tip ->
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Coach Tip", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onTertiaryContainer)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = tip,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Notes text field
            OutlinedTextField(
                value = notes,
                onValueChange = viewModel::updateNotes,
                label = { Text("Notes") },
                placeholder = { Text("How did it feel?") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                viewModel.saveNotes()
                onDone()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Done", style = MaterialTheme.typography.titleMedium)
        }
    }

    // Celebration overlay on top
    CelebrationOverlay(
        config = celebrationConfig,
        message = "Well done!",
        onDismiss = viewModel::dismissCelebration
    )

    } // close Box
}

@Composable
private fun HeartRateLineChart(
    readings: List<HeartRateReading>,
    maxHr: Int,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (readings.isEmpty()) return@Canvas

        val width = size.width
        val height = size.height
        val padding = 4.dp.toPx()

        val chartWidth = width - padding * 2
        val chartHeight = height - padding * 2

        val hrValues = readings.map { it.heartRate }
        val minHrVal = (hrValues.min() - 10).coerceAtLeast(40)
        val maxHrVal = (hrValues.max() + 10).coerceAtMost(220)
        val hrRange = (maxHrVal - minHrVal).toFloat().coerceAtLeast(1f)

        fun hrToY(hr: Float): Float = padding + chartHeight - ((hr - minHrVal) / hrRange * chartHeight)

        // Zone band thresholds
        val warmUpHr = maxHr * 0.50f
        val activeHr = maxHr * 0.60f
        val pushHr = maxHr * 0.70f
        val peakHr = maxHr * 0.85f

        // Draw zone bands (clipped to chart area)
        data class ZoneBand(val low: Float, val high: Float, val color: Color)
        val bands = listOf(
            ZoneBand(minHrVal.toFloat(), warmUpHr, ZoneRest),
            ZoneBand(warmUpHr, activeHr, ZoneWarmUp),
            ZoneBand(activeHr, pushHr, ZoneActive),
            ZoneBand(pushHr, peakHr, ZonePush),
            ZoneBand(peakHr, maxHrVal.toFloat(), ZonePeak)
        )

        for (band in bands) {
            val clippedLow = band.low.coerceIn(minHrVal.toFloat(), maxHrVal.toFloat())
            val clippedHigh = band.high.coerceIn(minHrVal.toFloat(), maxHrVal.toFloat())
            if (clippedHigh <= clippedLow) continue

            val top = hrToY(clippedHigh)
            val bottom = hrToY(clippedLow)
            drawRect(
                color = band.color.copy(alpha = 0.12f),
                topLeft = Offset(padding, top),
                size = Size(chartWidth, bottom - top)
            )
        }

        // Draw HR line
        val path = Path()
        val stepX = chartWidth / (readings.size - 1).coerceAtLeast(1).toFloat()

        readings.forEachIndexed { index, reading ->
            val x = padding + index * stepX
            val y = hrToY(reading.heartRate.toFloat())
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(path, Color.White, style = Stroke(width = 2.dp.toPx()))
    }
}
