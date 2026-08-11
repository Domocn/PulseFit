package com.pulsefit.app.data.repository

import com.pulsefit.app.data.local.dao.ExerciseLogDao
import com.pulsefit.app.data.local.dao.MuscleFatigueDao
import com.pulsefit.app.data.local.entity.MuscleFatigueEntity
import com.pulsefit.app.domain.model.MuscleFatigue
import com.pulsefit.app.domain.repository.MuscleFatigueRepository
import com.pulsefit.app.util.MuscleFatigueCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MuscleFatigueRepositoryImpl @Inject constructor(
    private val fatigueDao: MuscleFatigueDao,
    private val exerciseLogDao: ExerciseLogDao,
    private val calculator: MuscleFatigueCalculator
) : MuscleFatigueRepository {

    override suspend fun updateFatigue(fatigue: MuscleFatigue) {
        fatigueDao.insertOrUpdate(fatigue.toEntity())
    }

    override fun getAllFlow(): Flow<List<MuscleFatigue>> {
        return fatigueDao.getAllFlow().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getAll(): List<MuscleFatigue> {
        return fatigueDao.getAll().map { it.toDomain() }
    }

    override suspend fun getByMuscleGroup(muscleGroup: String): MuscleFatigue? {
        return fatigueDao.getByMuscleGroup(muscleGroup)?.toDomain()
    }

    override fun getByMuscleGroupFlow(muscleGroup: String): Flow<MuscleFatigue?> {
        return fatigueDao.getByMuscleGroupFlow(muscleGroup).map { it?.toDomain() }
    }

    override suspend fun getReadyMuscles(threshold: Float): List<MuscleFatigue> {
        return fatigueDao.getReadyMuscles(threshold).map { it.toDomain() }
    }

    override suspend fun getFatiguedMuscles(threshold: Float): List<MuscleFatigue> {
        return fatigueDao.getFatiguedMuscles(threshold).map { it.toDomain() }
    }

    override suspend fun recalculateAll() {
        val now = System.currentTimeMillis()
        val sevenDaysAgo = now - 7 * 24 * 60 * 60 * 1000L
        val recentLogs = exerciseLogDao.getInDateRange(sevenDaysAgo, now)
        val existing = fatigueDao.getAll().associateBy { it.muscleGroup }

        val updated = calculator.calculate(recentLogs, existing)
        updated.forEach { fatigueDao.insertOrUpdate(it) }
    }

    private fun MuscleFatigueEntity.toDomain() = MuscleFatigue(
        muscleGroup = muscleGroup,
        fatigueScore = fatigueScore,
        lastTrainedAt = lastTrainedAt,
        lastVolumeKg = lastVolumeKg,
        recoveryHoursNeeded = recoveryHoursNeeded
    )

    private fun MuscleFatigue.toEntity() = MuscleFatigueEntity(
        muscleGroup = muscleGroup,
        fatigueScore = fatigueScore,
        lastTrainedAt = lastTrainedAt,
        lastVolumeKg = lastVolumeKg,
        recoveryHoursNeeded = recoveryHoursNeeded,
        updatedAt = System.currentTimeMillis()
    )
}
