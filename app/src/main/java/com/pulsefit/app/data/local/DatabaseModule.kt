package com.pulsefit.app.data.local

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
            .addMigrations(PulseFitDatabase.MIGRATION_13_14, PulseFitDatabase.MIGRATION_14_15)
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

    @Provides
    fun provideRewardAdTrackingDao(db: PulseFitDatabase) = db.rewardAdTrackingDao()

    @Provides
    fun provideReadinessDataDao(db: PulseFitDatabase) = db.readinessDataDao()

    @Provides
    fun provideGymProfileDao(db: PulseFitDatabase) = db.gymProfileDao()

    @Provides
    fun provideRitualStepDao(db: PulseFitDatabase) = db.ritualStepDao()

    @Provides
    fun provideScheduledBodyDoubleDao(db: PulseFitDatabase) = db.scheduledBodyDoubleDao()

    @Provides
    fun provideSpoonBudgetDao(db: PulseFitDatabase) = db.spoonBudgetDao()

    @Provides
    fun provideCaregiverLinkDao(db: PulseFitDatabase) = db.caregiverLinkDao()

    @Provides
    fun provideStrengthWorkoutDao(db: PulseFitDatabase) = db.strengthWorkoutDao()

    @Provides
    fun provideExerciseLogDao(db: PulseFitDatabase) = db.exerciseLogDao()

    @Provides
    fun provideBodyMeasurementDao(db: PulseFitDatabase) = db.bodyMeasurementDao()

    @Provides
    fun provideMuscleFatigueDao(db: PulseFitDatabase) = db.muscleFatigueDao()

    @Provides
    fun provideOneRmDao(db: PulseFitDatabase) = db.oneRmDao()

    @Provides
    fun provideGroupSessionDao(db: PulseFitDatabase) = db.groupSessionDao()
}
