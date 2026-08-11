package com.pulsefit.app.ui.caregiver

import androidx.compose.foundation.layout.*
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
fun CaregiverSetupScreen(
    onBack: () -> Unit = {},
    onNavigateToDashboard: () -> Unit = {},
    viewModel: CaregiverSetupViewModel = hiltViewModel()
) {
    val linkCode by viewModel.linkCode.collectAsState()
    val claimResult by viewModel.claimResult.collectAsState()
    var enteredCode by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Caregiver Link") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Share limited workout data with a caregiver or support person. Only readiness score, streak, and last workout time are shared - never specific workout details.",
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("I'm the Athlete", style = MaterialTheme.typography.titleSmall)
                    Text("Generate a code to share with your caregiver", style = MaterialTheme.typography.bodySmall)
                    Button(onClick = viewModel::generateCode, modifier = Modifier.fillMaxWidth()) {
                        Text("Generate Link Code")
                    }
                    if (linkCode != null) {
                        Text("Your code: $linkCode", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                        Text("Share this code with your caregiver", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("I'm the Caregiver", style = MaterialTheme.typography.titleSmall)
                    Text("Enter the code your athlete shared with you", style = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(value = enteredCode, onValueChange = { enteredCode = it.uppercase() },
                        label = { Text("Link Code") }, modifier = Modifier.fillMaxWidth())
                    Button(onClick = { viewModel.claimCode(enteredCode) }, enabled = enteredCode.length == 8,
                        modifier = Modifier.fillMaxWidth()) {
                        Text("Link")
                    }
                    if (claimResult != null) {
                        Text(claimResult!!, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            OutlinedButton(onClick = onNavigateToDashboard, modifier = Modifier.fillMaxWidth()) {
                Text("Go to Caregiver Dashboard")
            }
        }
    }
}
