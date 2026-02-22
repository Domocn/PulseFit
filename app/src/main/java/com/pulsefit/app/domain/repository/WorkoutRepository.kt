package com.pulsefit.app.domain.repository

import com.pulsefit.app.domain.model.HeartRateReading
import com.pulsefit.app.domain.model.Workout
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    suspend fun createWorkout(workout: Workout): Long
    suspend fun updateWorkout(workout: Workout)
    suspend fun getWorkoutById(id: Long): Workout?
    fun getWorkoutByIdFlow(id: Long): Flow<Workout?>
    fun getAllWorkouts(): Flow<List<Workout>>
    fun getTodayBurnPoints(): Flow<Int>
    suspend fun getWorkoutsInDateRange(startTime: Long, endTime: Long): List<Workout>
    suspend fun getCompletedWorkouts(): List<Workout>
    suspend fun deleteWorkout(id: Long)
    suspend fun getWorkoutDays(): List<Long>
    suspend fun saveHeartRateReading(reading: HeartRateReading)
    fun getReadingsForWorkout(workoutId: Long): Flow<List<HeartRateReading>>
    suspend fun getReadingsForWorkoutOnce(workoutId: Long): List<HeartRateReading>
}
