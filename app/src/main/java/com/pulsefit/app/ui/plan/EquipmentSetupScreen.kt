package com.pulsefit.app.ui.plan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pulsefit.app.data.model.DayOfWeek
import com.pulsefit.app.data.model.Equipment
import com.pulsefit.app.data.model.EquipmentCategory
import com.pulsefit.app.data.model.WorkoutEnvironment

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EquipmentSetupScreen(
    onBack: () -> Unit,
    viewModel: EquipmentSetupViewModel = hiltViewModel()
) {
    val environment by viewModel.environment.collectAsState()
    val selectedEquipment by viewModel.selectedEquipment.collectAsState()
    val selectedDays by viewModel.selectedDays.collectAsState()
    val workoutsPerWeek by viewModel.workoutsPerWeek.collectAsState()
    val preferredDuration by viewModel.preferredDuration.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Equipment & Schedule") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Environment selection
            Text(
                "Where do you work out?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                WorkoutEnvironment.entries.forEachIndexed { index, env ->
                    SegmentedButton(
                        selected = environment == env,
                        onClick = { viewModel.setEnvironment(env) },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = WorkoutEnvironment.entries.size
                        )
                    ) {
                        Text(env.label, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Equipment selection by category
            Text(
                "Available Equipment",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            EquipmentCategory.entries.forEach { category ->
                val equipmentInCategory = Equipment.entries.filter { it.category == category }
                if (equipmentInCategory.isNotEmpty()) {
                    Text(
                        category.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        equipmentInCategory.forEach { equip ->
                            FilterChip(
                                selected = equip in selectedEquipment,
                                onClick = { viewModel.toggleEquipment(equip) },
                                label = { Text(equip.label, style = MaterialTheme.typography.bodySmall) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Preferred days
            Text(
                "Preferred Workout Days",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                DayOfWeek.entries.forEach { day ->
                    FilterChip(
                        selected = day in selectedDays,
                        onClick = { viewModel.toggleDay(day) },
                        label = { Text(day.short) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Workouts per week
            Text(
                "Workouts per week: $workoutsPerWeek",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Slider(
                value = workoutsPerWeek.toFloat(),
                onValueChange = { viewModel.setWorkoutsPerWeek(it.toInt()) },
                valueRange = 1f..7f,
                steps = 5
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Preferred duration
            Text(
                "Preferred duration: $preferredDuration min",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Slider(
                value = preferredDuration.toFloat(),
                onValueChange = { viewModel.setPreferredDuration(it.toInt()) },
                valueRange = 10f..60f,
                steps = 9
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.save()
                    onBack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving
            ) {
                Text("Save Equipment Profile")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
