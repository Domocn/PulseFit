package com.example.pulsefit.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pulsefit.data.local.dao.AchievementDao
import com.example.pulsefit.data.local.dao.DailyQuestDao
import com.example.pulsefit.data.local.dao.HeartRateReadingDao
import com.example.pulsefit.data.local.dao.NotificationPreferencesDao
import com.example.pulsefit.data.local.dao.SensoryPreferencesDao
import com.example.pulsefit.data.local.dao.UserProfileDao
import com.example.pulsefit.data.local.dao.WeeklyRoutineDao
import com.example.pulsefit.data.local.dao.WorkoutDao
import com.example.pulsefit.data.local.entity.AchievementEntity
import com.example.pulsefit.data.local.entity.DailyQuestEntity
import com.example.pulsefit.data.local.entity.HeartRateReadingEntity
import com.example.pulsefit.data.local.entity.NotificationPreferencesEntity
import com.example.pulsefit.data.local.entity.SensoryPreferencesEntity
import com.example.pulsefit.data.local.entity.UserProfileEntity
import com.example.pulsefit.data.local.entity.WeeklyRoutineEntity
import com.example.pulsefit.data.local.entity.WorkoutEntity

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
    version = 5,
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
