package com.pulsefit.app.domain.model

import java.time.Instant

data class BodyMeasurement(
    val id: Long = 0,
    val timestamp: Instant = Instant.now(),
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
