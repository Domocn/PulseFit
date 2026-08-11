package com.pulsefit.app.ui.plan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
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
fun CalendarBlockScreen(
    onBack: () -> Unit = {},
    viewModel: CalendarBlockViewModel = hiltViewModel()
) {
    val openSlots by viewModel.openSlots.collectAsState()
    val blockedMessage by viewModel.blockedMessage.collectAsState()

    LaunchedEffect(Unit) { viewModel.findSlots() }

    if (blockedMessage != null) {
        Snackbar(action = { TextButton(onClick = viewModel::dismissMessage) { Text("OK") } }) {
            Text(blockedMessage!!)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Block Workout Time") },
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
                Text("Find open slots in your calendar and block time for workouts. Protecting workout time helps build consistency.",
                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            items(openSlots) { slot ->
                val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CalendarMonth, contentDescription = null, modifier = Modifier.size(32.dp))
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(timeFormat.format(Date(slot.startMillis)), style = MaterialTheme.typography.titleSmall)
                            Text("30 min available", style = MaterialTheme.typography.bodySmall)
                        }
                        Button(onClick = { viewModel.blockSlot(slot) }) { Text("Block") }
                    }
                }
            }
            if (openSlots.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No open slots found today. Try adjusting your preferred workout times in settings.",
                            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
