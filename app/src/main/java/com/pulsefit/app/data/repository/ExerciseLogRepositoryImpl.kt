package com.pulsefit.app.data.repository

import com.pulsefit.app.data.local.dao.ExerciseLogDao
import com.pulsefit.app.data.local.entity.ExerciseLogEntity
import com.pulsefit.app.domain.model.ExerciseLog
import com.pulsefit.app.domain.repository.ExerciseLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseLogRepositoryImpl @Inject constructor(
    private val dao: ExerciseLogDao
) : ExerciseLogRepository {

    override suspend fun insert(log: ExerciseLog): Long {
        return dao.insert(log.toEntity())
    }

    override suspend fun insertAll(logs: List<ExerciseLog>) {
        dao.insertAll(logs.map { it.toEntity() })
    }

    override suspend fun getByWorkoutId(workoutId: Long): List<ExerciseLog> {
        return dao.getByWorkoutId(workoutId).map { it.toDomain() }
    }

    override suspend fun getByExerciseId(exerciseId: String): List<ExerciseLog> {
        return dao.getByExerciseId(exerciseId).map { it.toDomain() }
    }

    override fun getByExerciseIdFlow(exerciseId: String): Flow<List<ExerciseLog>> {
        return dao.getByExerciseIdFlow(exerciseId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getLatestForExercise(exerciseId: String): ExerciseLog? {
        return dao.getLatestForExercise(exerciseId)?.toDomain()
    }

    override suspend fun getRecent(limit: Int): List<ExerciseLog> {
        return dao.getRecent(limit).map { it.toDomain() }
    }

    override suspend fun getInDateRange(startTime: Long, endTime: Long): List<ExerciseLog> {
        return dao.getInDateRange(startTime, endTime).map { it.toDomain() }
    }

    override suspend fun getPersonalRecordWeight(exerciseId: String): Float? {
        return dao.getPersonalRecordWeight(exerciseId)
    }

    override suspend fun getPersonalRecordVolume(exerciseId: String): Float? {
        return dao.getPersonalRecordVolume(exerciseId)
    }

    override suspend fun getDistinctExerciseIds(): List<String> {
        return dao.getDistinctExerciseIds()
    }

    override suspend fun getByMuscleGroupSince(muscleGroup: String, since: Long): List<ExerciseLog> {
        return dao.getByMuscleGroupSince(muscleGroup, since).map { it.toDomain() }
    }

    override suspend fun deleteByWorkoutId(workoutId: Long) {
        dao.deleteByWorkoutId(workoutId)
    }

    private fun ExerciseLogEntity.toDomain() = ExerciseLog(
        id = id,
        workoutId = workoutId,
        exerciseId = exerciseId,
        exerciseName = exerciseName,
        primaryMuscleGroup = primaryMuscleGroup,
        setsCompleted = setsCompleted,
        setsPlanned = setsPlanned,
        maxWeightKg = maxWeightKg,
        totalVolumeKg = totalVolumeKg,
        bestSetReps = bestSetReps,
        bestSetWeightKg = bestSetWeightKg,
        averageRpe = averageRpe,
        timestamp = Instant.ofEpochMilli(timestamp)
    )

    private fun ExerciseLog.toEntity() = ExerciseLogEntity(
        id = id,
        workoutId = workoutId,
        exerciseId = exerciseId,
        exerciseName = exerciseName,
        primaryMuscleGroup = primaryMuscleGroup,
        setsCompleted = setsCompleted,
        setsPlanned = setsPlanned,
        maxWeightKg = maxWeightKg,
        totalVolumeKg = totalVolumeKg,
        bestSetReps = bestSetReps,
        bestSetWeightKg = bestSetWeightKg,
        averageRpe = averageRpe,
        timestamp = timestamp.toEpochMilli()
    )
}
