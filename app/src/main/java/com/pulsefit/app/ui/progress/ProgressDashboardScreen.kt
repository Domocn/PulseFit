package com.pulsefit.app.ui.progress

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pulsefit.app.data.model.HeartRateZone
import com.pulsefit.app.ui.components.StatCard
import com.pulsefit.app.ui.theme.ZoneActive
import com.pulsefit.app.ui.theme.ZonePeak
import com.pulsefit.app.ui.theme.ZonePush
import com.pulsefit.app.ui.theme.ZoneRest
import com.pulsefit.app.ui.theme.ZoneWarmUp

@Composable
fun ProgressDashboardScreen(viewModel: ProgressDashboardViewModel = hiltViewModel()) {
    val weeklyStats by viewModel.weeklyStats.collectAsState()
    val monthlyStats by viewModel.monthlyStats.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val weeklyBpHistory by viewModel.weeklyBpHistory.collectAsState()
    val zoneDistribution by viewModel.zoneDistribution.collectAsState()
    val targetHitRate by viewModel.targetHitRate.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Progress",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Profile stats
        profile?.let { p ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Level ${p.xpLevel}", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    Text("${p.totalXp} Total XP", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${p.totalWorkouts} Total Workouts", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Longest Streak: ${p.longestStreak} days", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Weekly stats
        Text("This Week", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        weeklyStats?.let { w ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatCard(label = "Workouts", value = "${w.totalWorkouts}")
                StatCard(label = "Burn Pts", value = "${w.totalBurnPoints}")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Monthly stats
        Text("This Month", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        monthlyStats?.let { m ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatCard(label = "Workouts", value = "${m.totalWorkouts}")
                StatCard(label = "Burn Pts", value = "${m.totalBurnPoints}")
            }
        }

        // Weekly BP bar chart
        if (weeklyBpHistory.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("Weekly Burn Points (8 weeks)", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            WeeklyBarChart(
                data = weeklyBpHistory,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )
        }

        // Zone distribution donut
        if (zoneDistribution.isNotEmpty() && zoneDistribution.values.sum() > 0) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("Zone Distribution (30 days)", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ZoneDonutChart(
                    distribution = zoneDistribution,
                    modifier = Modifier.size(140.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                ZoneLegend(distribution = zoneDistribution)
            }
        }

        // Target hit rate
        if (targetHitRate > 0f) {
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Daily Target Hit Rate", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                    Text(
                        text = "${(targetHitRate * 100).toInt()}% of workouts hit daily target",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun WeeklyBarChart(
    data: List<WeeklyBurnPoints>,
    modifier: Modifier = Modifier
) {
    val barColor = MaterialTheme.colorScheme.primary
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant

    Canvas(modifier = modifier) {
        if (data.isEmpty()) return@Canvas

        val maxBp = data.maxOf { it.burnPoints }.coerceAtLeast(1)
        val barWidth = size.width / (data.size * 2f)
        val bottomPadding = 24.dp.toPx()
        val chartHeight = size.height - bottomPadding

        data.forEachIndexed { index, week ->
            val barHeight = if (maxBp > 0) (week.burnPoints.toFloat() / maxBp) * chartHeight else 0f
            val x = (index * 2 + 0.5f) * barWidth

            // Bar
            drawRect(
                color = barColor,
                topLeft = Offset(x, chartHeight - barHeight),
                size = Size(barWidth, barHeight)
            )

            // Label
            drawContext.canvas.nativeCanvas.drawText(
                week.weekLabel,
                x + barWidth / 2,
                size.height - 4.dp.toPx(),
                android.graphics.Paint().apply {
                    color = android.graphics.Color.GRAY
                    textSize = 9.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )

            // Value on top of bar
            if (week.burnPoints > 0) {
                drawContext.canvas.nativeCanvas.drawText(
                    "${week.burnPoints}",
                    x + barWidth / 2,
                    chartHeight - barHeight - 4.dp.toPx(),
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = 10.sp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }
        }
    }
}

@Composable
private fun ZoneDonutChart(
    distribution: Map<HeartRateZone, Long>,
    modifier: Modifier = Modifier
) {
    val total = distribution.values.sum().toFloat().coerceAtLeast(1f)

    Canvas(modifier = modifier) {
        val strokeWidth = 24.dp.toPx()
        val radius = (size.minDimension - strokeWidth) / 2
        val topLeft = Offset(
            (size.width - radius * 2) / 2,
            (size.height - radius * 2) / 2
        )
        val arcSize = Size(radius * 2, radius * 2)

        var startAngle = -90f
        for (zone in HeartRateZone.entries) {
            val seconds = distribution[zone] ?: 0L
            if (seconds == 0L) continue
            val sweep = (seconds / total) * 360f
            val color = zoneToColor(zone)

            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
            )
            startAngle += sweep
        }
    }
}

@Composable
private fun ZoneLegend(distribution: Map<HeartRateZone, Long>) {
    val total = distribution.values.sum().toFloat().coerceAtLeast(1f)

    Column {
        for (zone in HeartRateZone.entries) {
            val seconds = distribution[zone] ?: 0L
            if (seconds == 0L) continue
            val pct = (seconds / total * 100).toInt()
            val minutes = seconds / 60

            Row(verticalAlignment = Alignment.CenterVertically) {
                Canvas(modifier = Modifier.size(10.dp)) {
                    drawCircle(color = zoneToColor(zone))
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${zone.label}: ${minutes}m ($pct%)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

private fun zoneToColor(zone: HeartRateZone): Color {
    return when (zone) {
        HeartRateZone.REST -> ZoneRest
        HeartRateZone.WARM_UP -> ZoneWarmUp
        HeartRateZone.ACTIVE -> ZoneActive
        HeartRateZone.PUSH -> ZonePush
        HeartRateZone.PEAK -> ZonePeak
    }
}
