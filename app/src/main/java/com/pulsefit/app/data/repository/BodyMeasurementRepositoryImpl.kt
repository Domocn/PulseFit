package com.pulsefit.app.data.repository

import com.pulsefit.app.data.local.dao.BodyMeasurementDao
import com.pulsefit.app.data.local.entity.BodyMeasurementEntity
import com.pulsefit.app.domain.model.BodyMeasurement
import com.pulsefit.app.domain.repository.BodyMeasurementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BodyMeasurementRepositoryImpl @Inject constructor(
    private val dao: BodyMeasurementDao
) : BodyMeasurementRepository {

    override suspend fun insert(measurement: BodyMeasurement): Long {
        return dao.insert(measurement.toEntity())
    }

    override fun getAllFlow(): Flow<List<BodyMeasurement>> {
        return dao.getAllFlow().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getAll(): List<BodyMeasurement> {
        return dao.getAll().map { it.toDomain() }
    }

    override suspend fun getLatest(): BodyMeasurement? {
        return dao.getLatest()?.toDomain()
    }

    override suspend fun getById(id: Long): BodyMeasurement? {
        return dao.getById(id)?.toDomain()
    }

    override suspend fun getInDateRange(startTime: Long, endTime: Long): List<BodyMeasurement> {
        return dao.getInDateRange(startTime, endTime).map { it.toDomain() }
    }

    override suspend fun getWeightHistory(): List<BodyMeasurement> {
        return dao.getWeightHistory().map { it.toDomain() }
    }

    override suspend fun getBodyFatHistory(): List<BodyMeasurement> {
        return dao.getBodyFatHistory().map { it.toDomain() }
    }

    override suspend fun deleteById(id: Long) {
        dao.deleteById(id)
    }

    private fun BodyMeasurementEntity.toDomain() = BodyMeasurement(
        id = id,
        timestamp = Instant.ofEpochMilli(timestamp),
        weightKg = weightKg,
        bodyFatPercent = bodyFatPercent,
        chestCm = chestCm,
        waistCm = waistCm,
        hipsCm = hipsCm,
        leftArmCm = leftArmCm,
        rightArmCm = rightArmCm,
        leftThighCm = leftThighCm,
        rightThighCm = rightThighCm,
        leftCalfCm = leftCalfCm,
        rightCalfCm = rightCalfCm,
        neckCm = neckCm,
        photoUri = photoUri,
        notes = notes
    )

    private fun BodyMeasurement.toEntity() = BodyMeasurementEntity(
        id = id,
        timestamp = timestamp.toEpochMilli(),
        weightKg = weightKg,
        bodyFatPercent = bodyFatPercent,
        chestCm = chestCm,
        waistCm = waistCm,
        hipsCm = hipsCm,
        leftArmCm = leftArmCm,
        rightArmCm = rightArmCm,
        leftThighCm = leftThighCm,
        rightThighCm = rightThighCm,
        leftCalfCm = leftCalfCm,
        rightCalfCm = rightCalfCm,
        neckCm = neckCm,
        photoUri = photoUri,
        notes = notes
    )
}
