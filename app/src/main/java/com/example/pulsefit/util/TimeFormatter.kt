package com.example.pulsefit.util

object TimeFormatter {

    fun formatDuration(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, secs)
        } else {
            String.format("%02d:%02d", minutes, secs)
        }
    }

    fun formatMinutes(seconds: Long): String {
        val minutes = seconds / 60
        return "${minutes}m"
    }
}
