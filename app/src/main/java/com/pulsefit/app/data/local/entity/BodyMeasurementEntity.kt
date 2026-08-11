package com.pulsefit.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Timestamped body measurement entry.
 * Tracks weight, body fat %, and circumference measurements over time.
 * Optional photo URI for progress photos.
 */
@Entity(
    tableName = "body_measurements",
    indices = [Index("timestamp")]
)
data class BodyMeasurementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val weightKg: Float? = null,
    val bodyFatPercent: Float? = null,
    val chestCm: Float? = null,
    val waistCm: Float? = null,
    val hipsCm: Float? = null,
    val leftArmCm: Float? = null,
    val rightArmCm: Float? = null,
    val leftThighCm: Float? = null,
    val rightThighCm: Float? = null,
    val leftCalfCm: Float? = null,
    val rightCalfCm: Float? = null,
    val neckCm: Float? = null,
    val photoUri: String? = null,
    val notes: String? = null
)
