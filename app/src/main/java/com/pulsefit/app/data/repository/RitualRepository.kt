package com.pulsefit.app.data.repository

import com.pulsefit.app.data.exercise.DefaultRitualTemplates
import com.pulsefit.app.data.local.dao.RitualStepDao
import com.pulsefit.app.data.local.entity.RitualStepEntity
import com.pulsefit.app.data.model.RitualType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RitualRepository @Inject constructor(
    private val dao: RitualStepDao
) {
    fun getSteps(type: RitualType): Flow<List<RitualStepEntity>> = dao.getByType(type)

    suspend fun ensureDefaults(type: RitualType) {
        val existing = dao.getByType(type).first()
        if (existing.isEmpty()) {
            val defaults = DefaultRitualTemplates.getDefaults(type)
            dao.insertAll(defaults)
        }
    }

    suspend fun addStep(type: RitualType, label: String) {
        val existing = dao.getByType(type).first()
        val nextOrder = (existing.maxOfOrNull { it.sortOrder } ?: -1) + 1
        dao.insertAll(listOf(RitualStepEntity(ritualType = type, sortOrder = nextOrder, label = label)))
    }

    suspend fun toggleStep(step: RitualStepEntity) {
        dao.update(step.copy(isCompleted = !step.isCompleted))
    }

    suspend fun deleteStep(id: Long) = dao.deleteById(id)

    suspend fun resetAll(type: RitualType) {
        val steps = dao.getByType(type).first()
        steps.forEach { dao.update(it.copy(isCompleted = false)) }
    }

    suspend fun clearAndSeedDefaults(type: RitualType) {
        dao.deleteAll(type)
        dao.insertAll(DefaultRitualTemplates.getDefaults(type))
    }
}
