package com.pulsefit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gym_profiles")
data class GymProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val address: String = "",
    val noiseRating: Int = 3,
    val lightingRating: Int = 3,
    val crowdRating: Int = 3,
    val musicRating: Int = 3,
    val quietHoursNotes: String = "",
    val isFavorite: Boolean = false
)
