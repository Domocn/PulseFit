package com.example.pulsefit.domain.usecase

import com.example.pulsefit.domain.model.Workout
import com.example.pulsefit.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWorkoutHistoryUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository
) {
    operator fun invoke(): Flow<List<Workout>> = workoutRepository.getAllWorkouts()
}
