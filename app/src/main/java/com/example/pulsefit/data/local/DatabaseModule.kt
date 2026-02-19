package com.example.pulsefit.data.local

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PulseFitDatabase {
        return Room.databaseBuilder(
            context,
            PulseFitDatabase::class.java,
            "pulsefit.db"
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    @Provides
    fun provideUserProfileDao(db: PulseFitDatabase) = db.userProfileDao()

    @Provides
    fun provideWorkoutDao(db: PulseFitDatabase) = db.workoutDao()

    @Provides
    fun provideHeartRateReadingDao(db: PulseFitDatabase) = db.heartRateReadingDao()

    @Provides
    fun provideSensoryPreferencesDao(db: PulseFitDatabase) = db.sensoryPreferencesDao()

    @Provides
    fun provideWeeklyRoutineDao(db: PulseFitDatabase) = db.weeklyRoutineDao()

    @Provides
    fun provideDailyQuestDao(db: PulseFitDatabase) = db.dailyQuestDao()

    @Provides
    fun provideAchievementDao(db: PulseFitDatabase) = db.achievementDao()

    @Provides
    fun provideNotificationPreferencesDao(db: PulseFitDatabase) = db.notificationPreferencesDao()
}
