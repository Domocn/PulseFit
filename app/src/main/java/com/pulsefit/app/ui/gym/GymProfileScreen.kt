package com.pulsefit.app.ui.gym

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
import com.pulsefit.app.data.local.entity.GymProfileEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GymProfileScreen(
    onBack: () -> Unit = {},
    viewModel: GymProfileViewModel = hiltViewModel()
) {
    val gyms by viewModel.gyms.collectAsState()
    val editingGym by viewModel.editingGym.collectAsState()
    val busyPrediction by viewModel.busyPrediction.collectAsState()

    if (editingGym != null) {
        GymEditDialog(
            gym = editingGym!!,
            onSave = viewModel::saveGym,
            onDismiss = viewModel::cancelEditing
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sensory Gym Map") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.startEditing() }) {
                        Icon(Icons.Filled.Add, contentDescription = "Add gym")
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
            if (busyPrediction != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Current Gym Busyness", style = MaterialTheme.typography.titleSmall)
                            Spacer(Modifier.height(4.dp))
                            Text(busyPrediction!!.label, style = MaterialTheme.typography.bodyLarge)
                            Text(busyPrediction!!.suggestion, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            items(gyms, key = { it.id }) { gym ->
                GymCard(
                    gym = gym,
                    onEdit = { viewModel.startEditing(gym) },
                    onDelete = { viewModel.deleteGym(gym) },
                    onToggleFavorite = { viewModel.toggleFavorite(gym) },
                    onCheckBusyness = { viewModel.predictBusyness(gym.crowdRating) }
                )
            }
            if (gyms.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No gyms added yet. Tap + to add your gym's sensory profile.",
                            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun GymCard(
    gym: GymProfileEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleFavorite: () -> Unit,
    onCheckBusyness: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(gym.name, style = MaterialTheme.typography.titleMedium)
                    if (gym.address.isNotBlank()) Text(gym.address, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = onToggleFavorite) {
                    Icon(if (gym.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = if (gym.isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (gym.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SensoryRating("Noise", gym.noiseRating)
                SensoryRating("Light", gym.lightingRating)
                SensoryRating("Crowd", gym.crowdRating)
                SensoryRating("Music", gym.musicRating)
            }
            if (gym.quietHoursNotes.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text("Quiet hours: ${gym.quietHoursNotes}", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onCheckBusyness, modifier = Modifier.semantics { contentDescription = "Check how busy ${gym.name} is right now" }) {
                    Text("Check Busyness")
                }
                TextButton(onClick = onEdit) { Text("Edit") }
                TextButton(onClick = onDelete) { Text("Delete") }
            }
        }
    }
}

@Composable
private fun SensoryRating(label: String, rating: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.semantics { contentDescription = "$label rating: $rating out of 5" }) {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Text("$rating/5", style = MaterialTheme.typography.bodyMedium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GymEditDialog(
    gym: GymProfileEntity,
    onSave: (GymProfileEntity) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(gym.name) }
    var address by remember { mutableStateOf(gym.address) }
    var noise by remember { mutableIntStateOf(gym.noiseRating) }
    var lighting by remember { mutableIntStateOf(gym.lightingRating) }
    var crowd by remember { mutableIntStateOf(gym.crowdRating) }
    var music by remember { mutableIntStateOf(gym.musicRating) }
    var quietHours by remember { mutableStateOf(gym.quietHoursNotes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (gym.id == 0L) "Add Gym" else "Edit Gym") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
                Text("Sensory Ratings (1=low, 5=high)", style = MaterialTheme.typography.labelMedium)
                RatingSlider("Noise Level", noise) { noise = it }
                RatingSlider("Lighting", lighting) { lighting = it }
                RatingSlider("Crowding", crowd) { crowd = it }
                RatingSlider("Music Volume", music) { music = it }
                OutlinedTextField(value = quietHours, onValueChange = { quietHours = it }, label = { Text("Quiet Hours Notes") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(gym.copy(name = name, address = address, noiseRating = noise, lightingRating = lighting, crowdRating = crowd, musicRating = music, quietHoursNotes = quietHours)) }, enabled = name.isNotBlank()) {
                Text("Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun RatingSlider(label: String, value: Int, onValueChange: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, modifier = Modifier.width(80.dp), style = MaterialTheme.typography.bodySmall)
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 1f..5f,
            steps = 3,
            modifier = Modifier.weight(1f).semantics { contentDescription = "$label: $value out of 5" }
        )
        Text("$value", modifier = Modifier.width(24.dp), style = MaterialTheme.typography.bodySmall)
    }
}
