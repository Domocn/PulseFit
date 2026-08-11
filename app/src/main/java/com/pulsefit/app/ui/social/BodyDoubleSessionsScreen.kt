package com.pulsefit.app.ui.social

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
fun BodyDoubleSessionsScreen(
    onBack: () -> Unit = {},
    viewModel: BodyDoubleSessionsViewModel = hiltViewModel()
) {
    val sessions by viewModel.upcomingSessions.collectAsState()
    val showCreate by viewModel.showCreate.collectAsState()

    if (showCreate) {
        CreateSessionDialog(
            onCreate = viewModel::createSession,
            onDismiss = viewModel::toggleCreate
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Body Double Sessions") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::toggleCreate) {
                        Icon(Icons.Filled.Add, contentDescription = "Create session")
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
                Text("Work out alongside someone at the same time. No interaction required - just parallel presence.",
                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
            }
            items(sessions, key = { it.id }) { session ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(session.title.ifBlank { "Body Double Session" }, style = MaterialTheme.typography.titleSmall)
                            val dateFormat = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
                            Text(dateFormat.format(Date(session.scheduledAt)), style = MaterialTheme.typography.bodySmall)
                            Text("${session.durationMinutes} min", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { viewModel.deleteSession(session.id) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete session")
                        }
                    }
                }
            }
            if (sessions.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No upcoming sessions. Create one to find a workout buddy.",
                            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateSessionDialog(
    onCreate: (String, Long, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var durationMinutes by remember { mutableIntStateOf(30) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Schedule Body Double") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Session title (optional)") }, modifier = Modifier.fillMaxWidth())
                Text("Duration: $durationMinutes min", style = MaterialTheme.typography.bodyMedium)
                Slider(value = durationMinutes.toFloat(), onValueChange = { durationMinutes = it.toInt() }, valueRange = 15f..90f, steps = 4)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val scheduledAt = System.currentTimeMillis() + 3600_000
                onCreate(title, scheduledAt, durationMinutes)
            }) { Text("Create") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
