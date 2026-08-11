package com.pulsefit.app.domain.repository

import com.pulsefit.app.domain.model.BodyMeasurement
import kotlinx.coroutines.flow.Flow

interface BodyMeasurementRepository {
    suspend fun insert(measurement: BodyMeasurement): Long
    fun getAllFlow(): Flow<List<BodyMeasurement>>
    suspend fun getAll(): List<BodyMeasurement>
    suspend fun getLatest(): BodyMeasurement?
    suspend fun getById(id: Long): BodyMeasurement?
    suspend fun getInDateRange(startTime: Long, endTime: Long): List<BodyMeasurement>
    suspend fun getWeightHistory(): List<BodyMeasurement>
    suspend fun getBodyFatHistory(): List<BodyMeasurement>
    suspend fun deleteById(id: Long)
}
