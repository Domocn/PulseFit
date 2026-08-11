package com.pulsefit.app.data.model

enum class GamificationLevel(val label: String, val description: String) {
    FULL("Full", "XP, levels, streaks, quests, achievements, celebrations, leaderboard"),
    MODERATE("Moderate", "XP and levels shown, streaks counted but not emphasized"),
    MINIMAL("Minimal", "Only basic stats - burn points, duration, heart rate"),
    OFF("Off", "Pure workout tracker, no gamification")
}
