package com.pulsefit.app.ui.workout

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pulsefit.app.data.model.HeartRateZone
import com.pulsefit.app.util.MusicSuggestionEngine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicSuggestionSheet(
    currentZone: HeartRateZone,
    onDismiss: () -> Unit,
    engine: MusicSuggestionEngine = MusicSuggestionEngine()
) {
    val context = LocalContext.current
    val suggestion = engine.suggestForZone(currentZone)

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Filled.MusicNote, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
            Text("Music for ${suggestion.zoneName}", style = MaterialTheme.typography.titleLarge)
            Text("${suggestion.bpmRange.first}-${suggestion.bpmRange.last} BPM", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
            Text(suggestion.genre, style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(8.dp))
            Button(onClick = {
                try { context.startActivity(engine.createSpotifyIntent(suggestion)) }
                catch (_: Exception) { context.startActivity(engine.createGenericMusicSearchIntent(suggestion)) }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Open in Spotify")
            }
            OutlinedButton(onClick = {
                context.startActivity(engine.createGenericMusicSearchIntent(suggestion))
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Search for Music")
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
