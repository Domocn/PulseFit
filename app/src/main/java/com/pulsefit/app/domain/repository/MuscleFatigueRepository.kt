package com.pulsefit.app.domain.repository

import com.pulsefit.app.domain.model.MuscleFatigue
import kotlinx.coroutines.flow.Flow

interface MuscleFatigueRepository {
    suspend fun updateFatigue(fatigue: MuscleFatigue)
    fun getAllFlow(): Flow<List<MuscleFatigue>>
    suspend fun getAll(): List<MuscleFatigue>
    suspend fun getByMuscleGroup(muscleGroup: String): MuscleFatigue?
    fun getByMuscleGroupFlow(muscleGroup: String): Flow<MuscleFatigue?>
    suspend fun getReadyMuscles(threshold: Float = 30f): List<MuscleFatigue>
    suspend fun getFatiguedMuscles(threshold: Float = 50f): List<MuscleFatigue>
    suspend fun recalculateAll()
}
