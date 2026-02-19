package com.example.pulsefit.data.repository

import com.example.pulsefit.data.local.dao.HeartRateReadingDao
import com.example.pulsefit.data.local.dao.WorkoutDao
import com.example.pulsefit.data.local.entity.HeartRateReadingEntity
import com.example.pulsefit.data.local.entity.WorkoutEntity
import com.example.pulsefit.data.model.HeartRateZone
import com.example.pulsefit.domain.model.HeartRateReading
import com.example.pulsefit.domain.model.Workout
import com.example.pulsefit.domain.repository.WorkoutRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutRepositoryImpl @Inject constructor(
    private val workoutDao: WorkoutDao,
    private val heartRateReadingDao: HeartRateReadingDao
) : WorkoutRepository {

    private val gson = Gson()

    override suspend fun createWorkout(workout: Workout): Long {
        return workoutDao.insert(workout.toEntity())
    }

    override suspend fun updateWorkout(workout: Workout) {
        workoutDao.update(workout.toEntity())
    }

    override suspend fun getWorkoutById(id: Long): Workout? {
        return workoutDao.getById(id)?.toDomain()
    }

    override fun getWorkoutByIdFlow(id: Long): Flow<Workout?> {
        return workoutDao.getByIdFlow(id).map { it?.toDomain() }
    }

    override fun getAllWorkouts(): Flow<List<Workout>> {
        return workoutDao.getAllWorkouts().map { list -> list.map { it.toDomain() } }
    }

    override fun getTodayBurnPoints(): Flow<Int> {
        val today = LocalDate.now(ZoneId.systemDefault())
        val startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return workoutDao.getTodayBurnPoints(startOfDay, endOfDay)
    }

    override suspend fun getWorkoutsInDateRange(startTime: Long, endTime: Long): List<Workout> {
        return workoutDao.getWorkoutsInDateRange(startTime, endTime).map { it.toDomain() }
    }

    override suspend fun getCompletedWorkouts(): List<Workout> {
        return workoutDao.getCompletedWorkouts().map { it.toDomain() }
    }

    override suspend fun deleteWorkout(id: Long) {
        workoutDao.deleteWorkout(id)
    }

    override suspend fun getWorkoutDays(): List<Long> {
        return workoutDao.getWorkoutDays()
    }

    override suspend fun saveHeartRateReading(reading: HeartRateReading) {
        heartRateReadingDao.insert(reading.toEntity())
    }

    override fun getReadingsForWorkout(workoutId: Long): Flow<List<HeartRateReading>> {
        return heartRateReadingDao.getReadingsForWorkout(workoutId)
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getReadingsForWorkoutOnce(workoutId: Long): List<HeartRateReading> {
        return heartRateReadingDao.getReadingsForWorkoutOnce(workoutId).map { it.toDomain() }
    }

    private fun WorkoutEntity.toDomain() = Workout(
        id = id,
        type = type,
        startTime = Instant.ofEpochMilli(startTime),
        endTime = endTime?.let { Instant.ofEpochMilli(it) },
        durationSeconds = durationSeconds,
        burnPoints = burnPoints,
        averageHeartRate = averageHeartRate,
        maxHeartRate = maxHeartRate,
        zoneTime = parseZoneTimeJson(zoneTimeJson),
        xpEarned = xpEarned,
        isQuickStart = isQuickStart,
        isJustFiveMin = isJustFiveMin,
        estimatedCalories = estimatedCalories,
        notes = notes
    )

    private fun Workout.toEntity() = WorkoutEntity(
        id = id,
        type = type,
        startTime = startTime.toEpochMilli(),
        endTime = endTime?.toEpochMilli(),
        durationSeconds = durationSeconds,
        burnPoints = burnPoints,
        averageHeartRate = averageHeartRate,
        maxHeartRate = maxHeartRate,
        zoneTimeJson = gson.toJson(zoneTime.mapKeys { it.key.name }),
        xpEarned = xpEarned,
        isQuickStart = isQuickStart,
        isJustFiveMin = isJustFiveMin,
        estimatedCalories = estimatedCalories,
        notes = notes
    )

    private fun HeartRateReadingEntity.toDomain() = HeartRateReading(
        id = id,
        workoutId = workoutId,
        timestamp = Instant.ofEpochMilli(timestamp),
        heartRate = heartRate,
        zone = zone
    )

    private fun HeartRateReading.toEntity() = HeartRateReadingEntity(
        id = id,
        workoutId = workoutId,
        timestamp = timestamp.toEpochMilli(),
        heartRate = heartRate,
        zone = zone
    )

    private fun parseZoneTimeJson(json: String): Map<HeartRateZone, Long> {
        val type = object : TypeToken<Map<String, Long>>() {}.type
        val stringMap: Map<String, Long> = gson.fromJson(json, type) ?: emptyMap()
        return stringMap.mapKeys { HeartRateZone.valueOf(it.key) }
    }
}
