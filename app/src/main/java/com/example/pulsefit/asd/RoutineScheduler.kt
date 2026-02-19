package com.example.pulsefit.asd

import com.example.pulsefit.data.local.dao.WeeklyRoutineDao
import com.example.pulsefit.data.local.entity.WeeklyRoutineEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoutineScheduler @Inject constructor(
    private val weeklyRoutineDao: WeeklyRoutineDao
) {
    fun getTodayRoutines(): Flow<List<WeeklyRoutineEntity>> {
        val today = LocalDate.now().dayOfWeek.value // 1=Monday..7=Sunday
        return weeklyRoutineDao.getRoutinesForDay(today)
    }

    fun getAllRoutines(): Flow<List<WeeklyRoutineEntity>> {
        return weeklyRoutineDao.getAllRoutines()
    }
}
