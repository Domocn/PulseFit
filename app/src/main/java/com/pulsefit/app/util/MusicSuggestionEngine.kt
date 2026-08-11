package com.pulsefit.app.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.pulsefit.app.data.model.HeartRateZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicSuggestionEngine @Inject constructor() {

    data class MusicSuggestion(
        val zoneName: String,
        val bpmRange: IntRange,
        val genre: String,
        val spotifyQuery: String
    )

    fun suggestForZone(zone: HeartRateZone): MusicSuggestion = when (zone) {
        HeartRateZone.REST -> MusicSuggestion("Rest", 60..80, "Ambient / Lo-fi", "genre:ambient")
        HeartRateZone.WARM_UP -> MusicSuggestion("Warm-Up", 100..120, "Chill Pop", "genre:chill")
        HeartRateZone.ACTIVE -> MusicSuggestion("Active", 120..140, "Pop / Indie", "genre:pop")
        HeartRateZone.PUSH -> MusicSuggestion("Push", 140..160, "EDM / Hip-Hop", "genre:edm")
        HeartRateZone.PEAK -> MusicSuggestion("Peak", 160..180, "Hard EDM / Metal", "genre:electronic")
    }

    fun createSpotifyIntent(suggestion: MusicSuggestion): Intent {
        val uri = Uri.parse("spotify:search:${suggestion.spotifyQuery}")
        return Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.spotify.music")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    fun createGenericMusicSearchIntent(suggestion: MusicSuggestion): Intent {
        val query = "workout music ${suggestion.bpmRange.first}-${suggestion.bpmRange.last} bpm ${suggestion.genre}"
        val uri = Uri.parse("https://www.google.com/search?q=${Uri.encode(query)}")
        return Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
}
