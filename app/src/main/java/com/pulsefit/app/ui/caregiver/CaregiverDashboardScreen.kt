package com.pulsefit.app.ui.caregiver

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaregiverDashboardScreen(
    onBack: () -> Unit = {},
    viewModel: CaregiverDashboardViewModel = hiltViewModel()
) {
    val athletes by viewModel.linkedAthletes.collectAsState()
    val readiness by viewModel.selectedReadiness.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Caregiver Dashboard") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                Text("View limited wellness data for your linked athletes. This shows readiness, streak, and last workout only.",
                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            items(athletes, key = { it.linkId }) { athlete ->
                Card(modifier = Modifier.fillMaxWidth(), onClick = { viewModel.loadReadiness(athlete.caregiverUid) }) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(athlete.caregiverName, style = MaterialTheme.typography.titleSmall)
                    }
                }
            }
            if (readiness != null) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Athlete Readiness", style = MaterialTheme.typography.titleSmall)
                            Text("Readiness: ${readiness!!.readinessScore}%", style = MaterialTheme.typography.bodyLarge)
                            Text("Streak: ${readiness!!.streakDays} days", style = MaterialTheme.typography.bodyMedium)
                            val dateFormat = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
                            Text("Last workout: ${dateFormat.format(Date(readiness!!.lastWorkoutAt))}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            if (athletes.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No linked athletes yet. Ask your athlete to generate a link code.",
                            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
