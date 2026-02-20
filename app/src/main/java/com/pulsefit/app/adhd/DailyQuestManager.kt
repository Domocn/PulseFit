package com.pulsefit.app.adhd

import com.pulsefit.app.data.local.dao.DailyQuestDao
import com.pulsefit.app.data.local.entity.DailyQuestEntity
import com.pulsefit.app.data.model.QuestType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyQuestManager @Inject constructor(
    private val dailyQuestDao: DailyQuestDao
) {
    fun getTodayQuests(): Flow<List<DailyQuestEntity>> {
        return dailyQuestDao.getQuestsForDate(todayMillis())
    }

    suspend fun generateIfNeeded() {
        val today = todayMillis()
        val existing = dailyQuestDao.getQuestsForDateOnce(today)
        if (existing.isNotEmpty()) return

        val quests = listOf(
            DailyQuestEntity(
                date = today,
                questType = QuestType.DURATION,
                title = "Quick Move",
                description = "Complete a 10-minute workout",
                targetValue = 600,
                difficulty = 1,
                xpReward = 50
            ),
            DailyQuestEntity(
                date = today,
                questType = QuestType.BURN_POINTS,
                title = "Point Chaser",
                description = "Earn 8 burn points",
                targetValue = 8,
                difficulty = 2,
                xpReward = 100
            ),
            DailyQuestEntity(
                date = today,
                questType = QuestType.ZONE_TARGET,
                title = "Push Yourself",
                description = "Spend 5 minutes in Push or Peak zone",
                targetValue = 300,
                difficulty = 3,
                xpReward = 200
            )
        )
        dailyQuestDao.insertAll(quests)

        // Clean up old quests (older than 7 days)
        val weekAgo = LocalDate.now(ZoneId.systemDefault()).minusDays(7)
            .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        dailyQuestDao.deleteOldQuests(weekAgo)
    }

    suspend fun evaluateCompletion(
        durationSeconds: Int,
        burnPoints: Int,
        pushPeakSeconds: Long
    ) {
        val today = todayMillis()
        val quests = dailyQuestDao.getQuestsForDateOnce(today)

        for (quest in quests) {
            if (quest.completed) continue

            val currentValue = when (quest.questType) {
                QuestType.DURATION -> durationSeconds
                QuestType.BURN_POINTS -> burnPoints
                QuestType.ZONE_TARGET -> pushPeakSeconds.toInt()
            }

            val newValue = quest.currentValue + currentValue
            dailyQuestDao.update(
                quest.copy(
                    currentValue = newValue,
                    completed = newValue >= quest.targetValue
                )
            )
        }
    }

    private fun todayMillis(): Long {
        return LocalDate.now(ZoneId.systemDefault())
            .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}
