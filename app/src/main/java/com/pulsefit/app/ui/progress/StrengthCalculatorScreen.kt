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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pulsefit.app.util.OneRmCalculator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StrengthCalculatorScreen(
    onBack: () -> Unit
) {
    val calculator = remember { OneRmCalculator() }

    var weight by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var bodyWeight by remember { mutableStateOf("") }

    val weightKg = weight.toFloatOrNull()
    val repCount = reps.toIntOrNull()
    val bodyWeightKg = bodyWeight.toFloatOrNull()

    val brzycki = if (weightKg != null && repCount != null && repCount in 1..36) {
        calculator.brzycki(weightKg, repCount)
    } else null

    val epley = if (weightKg != null && repCount != null && repCount > 0) {
        calculator.epley(weightKg, repCount)
    } else null

    val lombardi = if (weightKg != null && repCount != null && repCount > 0) {
        calculator.lombardi(weightKg, repCount)
    } else null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Strength Calculator", style = MaterialTheme.typography.titleMedium) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // 1RM Calculator
            Text(
                text = "1RM Calculator",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Weight (kg)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = reps,
                    onValueChange = { reps = it },
                    label = { Text("Reps") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (brzycki != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Estimated 1RM",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${"%.1f".format(brzycki)} kg",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "All Formulas",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        FormulaRow("Brzycki", "${"%.1f".format(brzycki)} kg", "Most accurate for 1-10 reps")
                        FormulaRow("Epley", "${"%.1f".format(epley!!)} kg", "Good for 5-15 reps")
                        FormulaRow("Lombardi", "${"%.1f".format(lombardi!!)} kg", "Alternative formula")

                        Spacer(modifier = Modifier.height(8.dp))

                        // Rep max table
                        Text(
                            text = "Rep Max Table (Brzycki)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        val oneRm = brzycki
                        for (r in listOf(1, 3, 5, 8, 10, 12, 15)) {
                            val repMax = if (r == 1) oneRm else {
                                // Reverse Brzycki: weight = 1RM × (37 - reps) / 36
                                oneRm * (37f - r) / 36f
                            }
                            Text(
                                text = "${r}RM: ${"%.1f".format(repMax)} kg",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            } else if (weight.isNotBlank() || reps.isNotBlank()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Enter weight and reps (1-36) to calculate 1RM",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Strength Standards
            Text(
                text = "Strength Standards",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = bodyWeight,
                onValueChange = { bodyWeight = it },
                label = { Text("Your body weight (kg)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (bodyWeightKg != null && bodyWeightKg > 0) {
                Spacer(modifier = Modifier.height(12.dp))

                StrengthStandardCard(
                    title = "Bench Press",
                    ratios = listOf(0.75f, 1.0f, 1.25f, 1.5f),
                    bodyWeightKg = bodyWeightKg
                )
                Spacer(modifier = Modifier.height(8.dp))

                StrengthStandardCard(
                    title = "Squat",
                    ratios = listOf(1.0f, 1.5f, 2.0f, 2.5f),
                    bodyWeightKg = bodyWeightKg
                )
                Spacer(modifier = Modifier.height(8.dp))

                StrengthStandardCard(
                    title = "Deadlift",
                    ratios = listOf(1.25f, 1.75f, 2.25f, 2.75f),
                    bodyWeightKg = bodyWeightKg
                )
                Spacer(modifier = Modifier.height(8.dp))

                StrengthStandardCard(
                    title = "Overhead Press",
                    ratios = listOf(0.5f, 0.75f, 1.0f, 1.25f),
                    bodyWeightKg = bodyWeightKg
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Formula info
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "About the Formulas",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Brzycki: 1RM = weight × 36 / (37 - reps) — most accurate for 1-10 rep range.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Epley: 1RM = weight × (1 + reps/30) — good for 5-15 rep range.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Lombardi: 1RM = weight × reps^0.10 — alternative formula.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Standards are based on 1RM relative to body weight. Actual standards vary by individual factors including age, gender, and training history.",
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
private fun FormulaRow(formula: String, result: String, description: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = formula,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = result,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun StrengthStandardCard(
    title: String,
    ratios: List<Float>,
    bodyWeightKg: Float
) {
    val levels = listOf("Beginner", "Novice", "Intermediate", "Advanced", "Elite")

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Show all 5 levels
            val allRatios = listOf(ratios[0] * 0.67f) + ratios + listOf(ratios.last() * 1.17f)
            levels.forEachIndexed { index, level ->
                val weight = allRatios[index] * bodyWeightKg
                val color = when (index) {
                    0 -> Color(0xFF94A3B8)
                    1 -> Color(0xFF22C55E)
                    2 -> Color(0xFF3B82F6)
                    3 -> Color(0xFFF97316)
                    else -> Color(0xFFEF4444)
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = level,
                        style = MaterialTheme.typography.bodySmall,
                        color = color
                    )
                    Text(
                        text = "${"%.1f".format(weight)} kg",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = color
                    )
                }
            }
        }
    }
}
