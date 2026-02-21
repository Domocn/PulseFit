package com.pulsefit.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pulsefit.app.data.local.dao.AchievementDao
import com.pulsefit.app.data.local.dao.DailyQuestDao
import com.pulsefit.app.data.local.dao.HeartRateReadingDao
import com.pulsefit.app.data.local.dao.NotificationPreferencesDao
import com.pulsefit.app.data.local.dao.SensoryPreferencesDao
import com.pulsefit.app.data.local.dao.UserProfileDao
import com.pulsefit.app.data.local.dao.WeeklyRoutineDao
import com.pulsefit.app.data.local.dao.WorkoutDao
import com.pulsefit.app.data.local.entity.AchievementEntity
import com.pulsefit.app.data.local.entity.DailyQuestEntity
import com.pulsefit.app.data.local.entity.HeartRateReadingEntity
import com.pulsefit.app.data.local.entity.NotificationPreferencesEntity
import com.pulsefit.app.data.local.entity.SensoryPreferencesEntity
import com.pulsefit.app.data.local.entity.UserProfileEntity
import com.pulsefit.app.data.local.entity.WeeklyRoutineEntity
import com.pulsefit.app.data.local.entity.WorkoutEntity

@Database(
    entities = [
        UserProfileEntity::class,
        WorkoutEntity::class,
        HeartRateReadingEntity::class,
        SensoryPreferencesEntity::class,
        WeeklyRoutineEntity::class,
        DailyQuestEntity::class,
        AchievementEntity::class,
        NotificationPreferencesEntity::class
    ],
    version = 8,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class PulseFitDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun heartRateReadingDao(): HeartRateReadingDao
    abstract fun sensoryPreferencesDao(): SensoryPreferencesDao
    abstract fun weeklyRoutineDao(): WeeklyRoutineDao
    abstract fun dailyQuestDao(): DailyQuestDao
    abstract fun achievementDao(): AchievementDao
    abstract fun notificationPreferencesDao(): NotificationPreferencesDao
}
