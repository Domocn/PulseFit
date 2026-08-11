package com.pulsefit.app.health

import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.RestingHeartRateRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.pulsefit.app.domain.model.HeartRateReading
import com.pulsefit.app.domain.model.Workout
import com.pulsefit.app.util.SleepData
import java.time.Instant
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthConnectRepository @Inject constructor(
    private val healthConnectManager: HealthConnectManager
) {
    @Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
    suspend fun writeWorkout(workout: Workout, readings: List<HeartRateReading>) {
        val client = healthConnectManager.getClient() ?: return
        val endTime = workout.endTime ?: return

        val startZone = ZoneOffset.systemDefault().rules.getOffset(workout.startTime)
        val endZone = ZoneOffset.systemDefault().rules.getOffset(endTime)

        try {
            val metadata = androidx.health.connect.client.records.metadata.Metadata()
            client.insertRecords(
                listOf(
                    ExerciseSessionRecord(
                        startTime = workout.startTime,
                        startZoneOffset = startZone,
                        endTime = endTime,
                        endZoneOffset = endZone,
                        exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_OTHER_WORKOUT,
                        title = "PulseFit Workout",
                        metadata = metadata
                    )
                )
            )
        } catch (_: Exception) {
            // Silently fail if permissions not granted or API not available
        }

        // Write heart rate data
        if (readings.size >= 2) {
            try {
                val metadata = androidx.health.connect.client.records.metadata.Metadata()
                client.insertRecords(
                    listOf(
                        HeartRateRecord(
                            startTime = readings.first().timestamp,
                            startZoneOffset = ZoneOffset.systemDefault().rules.getOffset(readings.first().timestamp),
                            endTime = readings.last().timestamp,
                            endZoneOffset = ZoneOffset.systemDefault().rules.getOffset(readings.last().timestamp),
                            samples = readings.map { reading ->
                                HeartRateRecord.Sample(
                                    time = reading.timestamp,
                                    beatsPerMinute = reading.heartRate.toLong()
                                )
                            },
                            metadata = metadata
                        )
                    )
                )
            } catch (_: Exception) {
                // Silently fail
            }
        }
    }

    /**
     * Read last night's sleep data from Health Connect.
     * Looks for sleep sessions in the last 24 hours.
     */
    suspend fun readLastNightSleep(): SleepData? {
        val client = healthConnectManager.getClient() ?: return null
        return try {
            val now = Instant.now()
            val yesterday = now.minusSeconds(24 * 60 * 60)
            val response = client.readRecords(
                ReadRecordsRequest(
                    recordType = SleepSessionRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(yesterday, now)
                )
            )
            if (response.records.isEmpty()) return null

            val session = response.records.last()
            val durationMs = session.endTime.toEpochMilli() - session.startTime.toEpochMilli()
            val totalHours = durationMs / (1000f * 60f * 60f)

            // Derive quality from stages if available
            val stages = session.stages
            val quality = if (stages.isNotEmpty()) {
                val deepMs = stages.filter {
                    it.stage == SleepSessionRecord.STAGE_TYPE_DEEP
                }.sumOf { it.endTime.toEpochMilli() - it.startTime.toEpochMilli() }
                val remMs = stages.filter {
                    it.stage == SleepSessionRecord.STAGE_TYPE_REM
                }.sumOf { it.endTime.toEpochMilli() - it.startTime.toEpochMilli() }

                val deepPct = if (durationMs > 0) deepMs.toFloat() / durationMs else 0f
                val remPct = if (durationMs > 0) remMs.toFloat() / durationMs else 0f

                when {
                    deepPct >= 0.20f && remPct >= 0.20f -> "GOOD"
                    deepPct >= 0.10f || remPct >= 0.10f -> "FAIR"
                    else -> "POOR"
                }
            } else null

            SleepData(totalHours = totalHours, quality = quality)
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Read the most recent resting heart rate from Health Connect.
     * Looks back up to 7 days.
     */
    suspend fun readRestingHeartRate(): Int? {
        val client = healthConnectManager.getClient() ?: return null
        return try {
            val now = Instant.now()
            val weekAgo = now.minusSeconds(7 * 24 * 60 * 60)
            val response = client.readRecords(
                ReadRecordsRequest(
                    recordType = RestingHeartRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(weekAgo, now)
                )
            )
            response.records.lastOrNull()?.beatsPerMinute?.toInt()
        } catch (_: Exception) {
            null
        }
    }
}
