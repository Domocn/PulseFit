package com.example.pulsefit.health

import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import com.example.pulsefit.domain.model.HeartRateReading
import com.example.pulsefit.domain.model.Workout
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
}
