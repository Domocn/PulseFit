package com.example.pulsefit.domain.usecase

import com.example.pulsefit.domain.model.Workout
import com.example.pulsefit.domain.repository.WorkoutRepository
import java.time.Instant
import javax.inject.Inject

class StartWorkoutUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository
) {
    suspend operator fun invoke(): Long {
        val workout = Workout(startTime = Instant.now())
        return workoutRepository.createWorkout(workout)
    }
}
