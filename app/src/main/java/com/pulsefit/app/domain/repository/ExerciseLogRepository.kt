package com.pulsefit.app.domain.repository

import com.pulsefit.app.domain.model.ExerciseLog
import kotlinx.coroutines.flow.Flow

interface ExerciseLogRepository {
    suspend fun insert(log: ExerciseLog): Long
    suspend fun insertAll(logs: List<ExerciseLog>)
    suspend fun getByWorkoutId(workoutId: Long): List<ExerciseLog>
    suspend fun getByExerciseId(exerciseId: String): List<ExerciseLog>
    fun getByExerciseIdFlow(exerciseId: String): Flow<List<ExerciseLog>>
    suspend fun getLatestForExercise(exerciseId: String): ExerciseLog?
    suspend fun getRecent(limit: Int = 50): List<ExerciseLog>
    suspend fun getInDateRange(startTime: Long, endTime: Long): List<ExerciseLog>
    suspend fun getPersonalRecordWeight(exerciseId: String): Float?
    suspend fun getPersonalRecordVolume(exerciseId: String): Float?
    suspend fun getDistinctExerciseIds(): List<String>
    suspend fun getByMuscleGroupSince(muscleGroup: String, since: Long): List<ExerciseLog>
    suspend fun deleteByWorkoutId(workoutId: Long)
}
