package com.pulsefit.app.data.repository

import com.pulsefit.app.data.local.dao.GymProfileDao
import com.pulsefit.app.data.local.entity.GymProfileEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GymProfileRepository @Inject constructor(
    private val dao: GymProfileDao
) {
    fun getAll(): Flow<List<GymProfileEntity>> = dao.getAll()

    fun getFavorites(): Flow<List<GymProfileEntity>> = dao.getFavorites()

    suspend fun getById(id: Long): GymProfileEntity? = dao.getById(id)

    suspend fun insert(profile: GymProfileEntity): Long = dao.insert(profile)

    suspend fun update(profile: GymProfileEntity) = dao.update(profile)

    suspend fun delete(profile: GymProfileEntity) = dao.delete(profile)
}
