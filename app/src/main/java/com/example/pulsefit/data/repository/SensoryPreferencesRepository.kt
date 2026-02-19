package com.example.pulsefit.data.repository

import com.example.pulsefit.data.local.dao.SensoryPreferencesDao
import com.example.pulsefit.data.local.entity.SensoryPreferencesEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SensoryPreferencesRepository @Inject constructor(
    private val dao: SensoryPreferencesDao
) {
    fun getPreferences(): Flow<SensoryPreferencesEntity?> = dao.getPreferences()

    suspend fun getPreferencesOnce(): SensoryPreferencesEntity {
        return dao.getPreferencesOnce() ?: SensoryPreferencesEntity().also {
            dao.insertOrUpdate(it)
        }
    }

    suspend fun save(prefs: SensoryPreferencesEntity) {
        dao.insertOrUpdate(prefs)
    }
}
