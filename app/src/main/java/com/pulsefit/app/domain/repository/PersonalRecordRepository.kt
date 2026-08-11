package com.pulsefit.app.domain.repository

import com.pulsefit.app.domain.model.PersonalRecord
import kotlinx.coroutines.flow.Flow

interface PersonalRecordRepository {
    suspend fun insert(record: PersonalRecord): Long
    suspend fun getByExerciseId(exerciseId: String): List<PersonalRecord>
    fun getByExerciseIdFlow(exerciseId: String): Flow<List<PersonalRecord>>
    suspend fun getCurrentBest(exerciseId: String): PersonalRecord?
    fun getAllFlow(): Flow<List<PersonalRecord>>
    suspend fun getRecent(limit: Int = 50): List<PersonalRecord>
    suspend fun deleteByExerciseId(exerciseId: String)
}
