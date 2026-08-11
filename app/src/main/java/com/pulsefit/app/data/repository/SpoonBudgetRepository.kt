package com.pulsefit.app.data.repository

import com.pulsefit.app.data.local.dao.SpoonBudgetDao
import com.pulsefit.app.data.local.entity.SpoonBudgetEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpoonBudgetRepository @Inject constructor(
    private val dao: SpoonBudgetDao
) {
    fun getBudget(): Flow<SpoonBudgetEntity> = dao.getBudget().map { entity ->
        val budget = entity ?: SpoonBudgetEntity()
        maybeReset(budget)
    }

    suspend fun getBudgetOnce(): SpoonBudgetEntity {
        val budget = dao.getBudgetOnce() ?: SpoonBudgetEntity()
        return maybeReset(budget)
    }

    suspend fun spendSpoons(cost: Float) {
        val budget = getBudgetOnce()
        dao.insertOrUpdate(budget.copy(usedSpoons = (budget.usedSpoons + cost).coerceAtMost(budget.dailySpoons.toFloat())))
    }

    suspend fun setDailySpoons(count: Int) {
        val budget = getBudgetOnce()
        dao.insertOrUpdate(budget.copy(dailySpoons = count))
    }

    private suspend fun maybeReset(budget: SpoonBudgetEntity): SpoonBudgetEntity {
        val now = System.currentTimeMillis()
        if (now >= budget.spoonResetAt) {
            val nextMidnight = nextMidnight()
            val reset = budget.copy(usedSpoons = 0f, spoonResetAt = nextMidnight)
            dao.insertOrUpdate(reset)
            return reset
        }
        return budget
    }

    private fun nextMidnight(): Long {
        val cal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }
}
