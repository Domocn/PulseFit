package com.pulsefit.app.ui.ritual

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pulsefit.app.data.model.RitualType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RitualScreen(
    onBack: () -> Unit = {},
    viewModel: RitualViewModel = hiltViewModel()
) {
    val selectedType by viewModel.selectedType.collectAsState()
    val steps by viewModel.steps.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddStepDialog(onAdd = { viewModel.addStep(it); showAddDialog = false }, onDismiss = { showAddDialog = false })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transition Rituals") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Filled.Add, contentDescription = "Add step")
                    }
                    IconButton(onClick = viewModel::resetAll) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Reset checkmarks")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = if (selectedType == RitualType.PRE) 0 else 1) {
                Tab(selected = selectedType == RitualType.PRE, onClick = { viewModel.selectType(RitualType.PRE) },
                    text = { Text("Pre-Workout") })
                Tab(selected = selectedType == RitualType.POST, onClick = { viewModel.selectType(RitualType.POST) },
                    text = { Text("Post-Workout") })
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(steps, key = { it.id }) { step ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = step.isCompleted,
                            onCheckedChange = { viewModel.toggleStep(step) },
                            modifier = Modifier.semantics { contentDescription = "${step.label}: ${if (step.isCompleted) "completed" else "not completed"}" }
                        )
                        Text(
                            text = step.label,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        IconButton(onClick = { viewModel.deleteStep(step.id) }) {
                            Icon(Icons.Filled.Close, contentDescription = "Remove ${step.label}", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                if (steps.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("No steps yet", style = MaterialTheme.typography.bodyMedium)
                                TextButton(onClick = viewModel::resetToDefaults) { Text("Load defaults") }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddStepDialog(onAdd: (String) -> Unit, onDismiss: () -> Unit) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Ritual Step") },
        text = {
            OutlinedTextField(value = text, onValueChange = { text = it }, label = { Text("Step description") }, modifier = Modifier.fillMaxWidth())
        },
        confirmButton = { TextButton(onClick = { onAdd(text) }, enabled = text.isNotBlank()) { Text("Add") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
