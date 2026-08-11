package com.pulsefit.app.ui.social

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuddyMatchScreen(
    onBack: () -> Unit = {},
    viewModel: BuddyMatchViewModel = hiltViewModel()
) {
    val matches by viewModel.matches.collectAsState()
    val hasRequest by viewModel.hasActiveRequest.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    var parallelPlayOnly by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buddy Match") },
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
                Text("Find a workout buddy who understands your ND needs. Matched by profile and preferred workout times.",
                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(12.dp))
            }
            if (!hasRequest) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Find a Buddy", style = MaterialTheme.typography.titleMedium)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = parallelPlayOnly, onCheckedChange = { parallelPlayOnly = it })
                                Text("Parallel play only (no required interaction)", style = MaterialTheme.typography.bodyMedium)
                            }
                            Button(
                                onClick = { viewModel.submitRequest("ASD", listOf("morning", "evening"), parallelPlayOnly) },
                                enabled = !isSubmitting,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(if (isSubmitting) "Searching..." else "Find Buddy")
                            }
                        }
                    }
                }
            } else {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Searching for a match...", style = MaterialTheme.typography.titleSmall)
                            Text("We'll match you with someone who has a similar ND profile and schedule.", style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.height(8.dp))
                            OutlinedButton(onClick = viewModel::cancelRequest) { Text("Cancel Search") }
                        }
                    }
                }
            }
            if (matches.isNotEmpty()) {
                item { Text("Your Matches", style = MaterialTheme.typography.titleMedium) }
                items(matches, key = { it.matchId }) { match ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(match.partnerName, style = MaterialTheme.typography.titleSmall)
                            Text("Matched based on ND profile and schedule", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
