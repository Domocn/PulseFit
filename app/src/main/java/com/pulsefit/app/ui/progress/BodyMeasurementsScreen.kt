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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pulsefit.app.domain.model.BodyMeasurement
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyMeasurementsScreen(
    viewModel: BodyMeasurementsViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Log", "History", "Trends")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Body Measurements", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab) {
                0 -> MeasurementForm(
                    isSaving = state.isSaving,
                    onSave = { weight, bf, chest, waist, hips, lArm, rArm, lThigh, rThigh, lCalf, rCalf, neck, notes ->
                        viewModel.saveMeasurement(
                            weight, bf, chest, waist, hips,
                            lArm, rArm, lThigh, rThigh, lCalf, rCalf, neck, null, notes
                        )
                    }
                )
                1 -> MeasurementHistory(
                    measurements = state.measurements,
                    onDelete = { viewModel.deleteMeasurement(it) }
                )
                2 -> TrendsView(
                    weightHistory = state.weightHistory,
                    bodyFatHistory = state.bodyFatHistory
                )
            }
        }
    }
}

@Composable
private fun MeasurementForm(
    isSaving: Boolean,
    onSave: (
        weight: Float?, bf: Float?, chest: Float?, waist: Float?, hips: Float?,
        lArm: Float?, rArm: Float?, lThigh: Float?, rThigh: Float?,
        lCalf: Float?, rCalf: Float?, neck: Float?, notes: String?
    ) -> Unit
) {
    var weight by remember { mutableStateOf("") }
    var bodyFat by remember { mutableStateOf("") }
    var chest by remember { mutableStateOf("") }
    var waist by remember { mutableStateOf("") }
    var hips by remember { mutableStateOf("") }
    var leftArm by remember { mutableStateOf("") }
    var rightArm by remember { mutableStateOf("") }
    var leftThigh by remember { mutableStateOf("") }
    var rightThigh by remember { mutableStateOf("") }
    var leftCalf by remember { mutableStateOf("") }
    var rightCalf by remember { mutableStateOf("") }
    var neck by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Log New Measurement",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Weight & Body Fat row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Weight (kg)") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = bodyFat,
                onValueChange = { bodyFat = it },
                label = { Text("Body Fat %") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Circumferences
        Text(
            text = "Circumferences (cm)",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = chest, onValueChange = { chest = it }, label = { Text("Chest") }, singleLine = true, modifier = Modifier.weight(1f))
            OutlinedTextField(value = waist, onValueChange = { waist = it }, label = { Text("Waist") }, singleLine = true, modifier = Modifier.weight(1f))
            OutlinedTextField(value = hips, onValueChange = { hips = it }, label = { Text("Hips") }, singleLine = true, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = leftArm, onValueChange = { leftArm = it }, label = { Text("L Arm") }, singleLine = true, modifier = Modifier.weight(1f))
            OutlinedTextField(value = rightArm, onValueChange = { rightArm = it }, label = { Text("R Arm") }, singleLine = true, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = leftThigh, onValueChange = { leftThigh = it }, label = { Text("L Thigh") }, singleLine = true, modifier = Modifier.weight(1f))
            OutlinedTextField(value = rightThigh, onValueChange = { rightThigh = it }, label = { Text("R Thigh") }, singleLine = true, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = leftCalf, onValueChange = { leftCalf = it }, label = { Text("L Calf") }, singleLine = true, modifier = Modifier.weight(1f))
            OutlinedTextField(value = rightCalf, onValueChange = { rightCalf = it }, label = { Text("R Calf") }, singleLine = true, modifier = Modifier.weight(1f))
            OutlinedTextField(value = neck, onValueChange = { neck = it }, label = { Text("Neck") }, singleLine = true, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes (optional)") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onSave(
                    weight.toFloatOrNull(), bodyFat.toFloatOrNull(),
                    chest.toFloatOrNull(), waist.toFloatOrNull(), hips.toFloatOrNull(),
                    leftArm.toFloatOrNull(), rightArm.toFloatOrNull(),
                    leftThigh.toFloatOrNull(), rightThigh.toFloatOrNull(),
                    leftCalf.toFloatOrNull(), rightCalf.toFloatOrNull(),
                    neck.toFloatOrNull(), notes.ifEmpty { null }
                )
            },
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isSaving) "Saving..." else "Save Measurement")
        }
    }
}

@Composable
private fun MeasurementHistory(
    measurements: List<BodyMeasurement>,
    onDelete: (Long) -> Unit
) {
    if (measurements.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No measurements yet",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Log your first measurement to start tracking",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            items(measurements) { measurement ->
                MeasurementCard(measurement = measurement, onDelete = { onDelete(measurement.id) })
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun MeasurementCard(measurement: BodyMeasurement, onDelete: () -> Unit) {
    val formatter = remember { DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm") }
    val dateStr = measurement.timestamp.atZone(ZoneId.systemDefault()).format(formatter)

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(onClick = onDelete) {
                    Text("✕", color = MaterialTheme.colorScheme.error)
                }
            }

            val parts = mutableListOf<String>()
            measurement.weightKg?.let { parts.add("${it}kg") }
            measurement.bodyFatPercent?.let { parts.add("${it}% BF") }
            measurement.waistCm?.let { parts.add("Waist: ${it}cm") }
            measurement.chestCm?.let { parts.add("Chest: ${it}cm") }

            if (parts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = parts.joinToString(" · "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            measurement.notes?.let { note ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = note,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TrendsView(
    weightHistory: List<BodyMeasurement>,
    bodyFatHistory: List<BodyMeasurement>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Weight trend
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Weight Trend",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (weightHistory.size < 2) {
                    Text(
                        text = "Need at least 2 measurements to show a trend",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    val first = weightHistory.first().weightKg ?: 0f
                    val last = weightHistory.last().weightKg ?: 0f
                    val change = last - first
                    val direction = when {
                        change < -0.5f -> "↓"
                        change > 0.5f -> "↑"
                        else -> "→"
                    }
                    val changeColor = when {
                        change < -0.5f -> Color(0xFF22C55E)
                        change > 0.5f -> Color(0xFFEF4444)
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }

                    Text(
                        text = "$direction ${"%.1f".format(kotlin.math.abs(change))}kg",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = changeColor
                    )
                    Text(
                        text = "From ${"%.1f".format(first)}kg to ${"%.1f".format(last)}kg",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Simple text-based trend
                    Text(
                        text = "Recent measurements:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    weightHistory.takeLast(10).forEach { m ->
                        val w = m.weightKg?.let { "${"%.1f".format(it)}kg" } ?: "--"
                        Text(
                            text = "  $w",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Body fat trend
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Body Fat % Trend",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (bodyFatHistory.size < 2) {
                    Text(
                        text = "Need at least 2 measurements to show a trend",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    val first = bodyFatHistory.first().bodyFatPercent ?: 0f
                    val last = bodyFatHistory.last().bodyFatPercent ?: 0f
                    val change = last - first
                    val direction = when {
                        change < -0.5f -> "↓"
                        change > 0.5f -> "↑"
                        else -> "→"
                    }
                    val changeColor = when {
                        change < -0.5f -> Color(0xFF22C55E)
                        change > 0.5f -> Color(0xFFEF4444)
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }

                    Text(
                        text = "$direction ${"%.1f".format(kotlin.math.abs(change))}%",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = changeColor
                    )
                    Text(
                        text = "From ${"%.1f".format(first)}% to ${"%.1f".format(last)}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // BMI calculator
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "BMI Calculator",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))

                val latestWeight = weightHistory.lastOrNull()?.weightKg
                if (latestWeight != null) {
                    var heightCm by remember { mutableStateOf("") }
                    val bmi = heightCm.toFloatOrNull()?.let { h ->
                        latestWeight / ((h / 100f) * (h / 100f))
                    }

                    OutlinedTextField(
                        value = heightCm,
                        onValueChange = { heightCm = it },
                        label = { Text("Height (cm)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    bmi?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        val category = when {
                            it < 18.5f -> "Underweight"
                            it < 25f -> "Healthy"
                            it < 30f -> "Overweight"
                            else -> "Obese"
                        }
                        val catColor = when (category) {
                            "Healthy" -> Color(0xFF22C55E)
                            "Underweight", "Overweight" -> Color(0xFFF97316)
                            else -> Color(0xFFEF4444)
                        }
                        Text(
                            text = "BMI: ${"%.1f".format(it)} — $category",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = catColor
                        )
                    }
                } else {
                    Text(
                        text = "Log your weight first to calculate BMI",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
