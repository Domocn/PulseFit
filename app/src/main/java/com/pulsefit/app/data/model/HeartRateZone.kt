package com.pulsefit.app.data.model

enum class HeartRateZone(val label: String, val pointsPerMinute: Int) {
    REST("Rest", 0),
    WARM_UP("Warm-Up", 0),
    ACTIVE("Active", 1),
    PUSH("Push", 2),
    PEAK("Peak", 3)
}
