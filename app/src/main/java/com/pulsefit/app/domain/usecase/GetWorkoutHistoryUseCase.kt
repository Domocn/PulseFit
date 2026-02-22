package com.pulsefit.app.domain.usecase

import com.pulsefit.app.domain.model.Workout
import com.pulsefit.app.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWorkoutHistoryUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository
) {
    operator fun invoke(): Flow<List<Workout>> = workoutRepository.getAllWorkouts()
}
