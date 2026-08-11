package com.pulsefit.app.data.repository

import com.pulsefit.app.data.local.dao.OneRmDao
import com.pulsefit.app.data.local.entity.OneRmEntity
import com.pulsefit.app.domain.model.PersonalRecord
import com.pulsefit.app.domain.repository.PersonalRecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PersonalRecordRepositoryImpl @Inject constructor(
    private val dao: OneRmDao
) : PersonalRecordRepository {

    override suspend fun insert(record: PersonalRecord): Long {
        return dao.insert(record.toEntity())
    }

    override suspend fun getByExerciseId(exerciseId: String): List<PersonalRecord> {
        return dao.getByExerciseId(exerciseId).map { it.toDomain() }
    }

    override fun getByExerciseIdFlow(exerciseId: String): Flow<List<PersonalRecord>> {
        return dao.getByExerciseIdFlow(exerciseId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getCurrentBest(exerciseId: String): PersonalRecord? {
        return dao.getCurrentBest(exerciseId)?.toDomain()
    }

    override fun getAllFlow(): Flow<List<PersonalRecord>> {
        return dao.getAllFlow().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getRecent(limit: Int): List<PersonalRecord> {
        return dao.getRecent(limit).map { it.toDomain() }
    }

    override suspend fun deleteByExerciseId(exerciseId: String) {
        dao.deleteByExerciseId(exerciseId)
    }

    private fun OneRmEntity.toDomain() = PersonalRecord(
        id = id,
        exerciseId = exerciseId,
        exerciseName = exerciseName,
        estimatedOneRmKg = estimatedOneRmKg,
        basedOnWeightKg = basedOnWeightKg,
        basedOnReps = basedOnReps,
        formula = formula,
        timestamp = Instant.ofEpochMilli(timestamp)
    )

    private fun PersonalRecord.toEntity() = OneRmEntity(
        id = id,
        exerciseId = exerciseId,
        exerciseName = exerciseName,
        estimatedOneRmKg = estimatedOneRmKg,
        basedOnWeightKg = basedOnWeightKg,
        basedOnReps = basedOnReps,
        formula = formula,
        timestamp = timestamp.toEpochMilli()
    )
}
