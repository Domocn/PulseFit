package com.pulsefit.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pulsefit.app.data.local.dao.AchievementDao
import com.pulsefit.app.data.local.dao.BodyMeasurementDao
import com.pulsefit.app.data.local.dao.CaregiverLinkDao
import com.pulsefit.app.data.local.dao.DailyQuestDao
import com.pulsefit.app.data.local.dao.ExerciseLogDao
import com.pulsefit.app.data.local.dao.GymProfileDao
import com.pulsefit.app.data.local.dao.HeartRateReadingDao
import com.pulsefit.app.data.local.dao.MuscleFatigueDao
import com.pulsefit.app.data.local.dao.NotificationPreferencesDao
import com.pulsefit.app.data.local.dao.OneRmDao
import com.pulsefit.app.data.local.dao.ReadinessDataDao
import com.pulsefit.app.data.local.dao.RewardAdTrackingDao
import com.pulsefit.app.data.local.dao.RitualStepDao
import com.pulsefit.app.data.local.dao.ScheduledBodyDoubleDao
import com.pulsefit.app.data.local.dao.SensoryPreferencesDao
import com.pulsefit.app.data.local.dao.SpoonBudgetDao
import com.pulsefit.app.data.local.dao.GroupSessionDao
import com.pulsefit.app.data.local.dao.StrengthWorkoutDao
import com.pulsefit.app.data.local.dao.UserProfileDao
import com.pulsefit.app.data.local.dao.WeeklyRoutineDao
import com.pulsefit.app.data.local.dao.WorkoutDao
import com.pulsefit.app.data.local.entity.AchievementEntity
import com.pulsefit.app.data.local.entity.BodyMeasurementEntity
import com.pulsefit.app.data.local.entity.CaregiverLinkEntity
import com.pulsefit.app.data.local.entity.DailyQuestEntity
import com.pulsefit.app.data.local.entity.ExerciseLogEntity
import com.pulsefit.app.data.local.entity.GroupSessionEntity
import com.pulsefit.app.data.local.entity.GroupSessionParticipantEntity
import com.pulsefit.app.data.local.entity.GymProfileEntity
import com.pulsefit.app.data.local.entity.HeartRateReadingEntity
import com.pulsefit.app.data.local.entity.MuscleFatigueEntity
import com.pulsefit.app.data.local.entity.NotificationPreferencesEntity
import com.pulsefit.app.data.local.entity.OneRmEntity
import com.pulsefit.app.data.local.entity.ReadinessDataEntity
import com.pulsefit.app.data.local.entity.RewardAdTrackingEntity
import com.pulsefit.app.data.local.entity.RitualStepEntity
import com.pulsefit.app.data.local.entity.ScheduledBodyDoubleEntity
import com.pulsefit.app.data.local.entity.SensoryPreferencesEntity
import com.pulsefit.app.data.local.entity.SpoonBudgetEntity
import com.pulsefit.app.data.local.entity.StrengthWorkoutEntity
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
        NotificationPreferencesEntity::class,
        RewardAdTrackingEntity::class,
        ReadinessDataEntity::class,
        GymProfileEntity::class,
        RitualStepEntity::class,
        ScheduledBodyDoubleEntity::class,
        SpoonBudgetEntity::class,
        CaregiverLinkEntity::class,
        StrengthWorkoutEntity::class,
        ExerciseLogEntity::class,
        BodyMeasurementEntity::class,
        MuscleFatigueEntity::class,
        OneRmEntity::class,
        GroupSessionEntity::class,
        GroupSessionParticipantEntity::class
    ],
    version = 15,
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
    abstract fun rewardAdTrackingDao(): RewardAdTrackingDao
    abstract fun readinessDataDao(): ReadinessDataDao
    abstract fun gymProfileDao(): GymProfileDao
    abstract fun ritualStepDao(): RitualStepDao
    abstract fun scheduledBodyDoubleDao(): ScheduledBodyDoubleDao
    abstract fun spoonBudgetDao(): SpoonBudgetDao
    abstract fun caregiverLinkDao(): CaregiverLinkDao
    abstract fun strengthWorkoutDao(): StrengthWorkoutDao
    abstract fun exerciseLogDao(): ExerciseLogDao
    abstract fun bodyMeasurementDao(): BodyMeasurementDao
    abstract fun muscleFatigueDao(): MuscleFatigueDao
    abstract fun oneRmDao(): OneRmDao
    abstract fun groupSessionDao(): GroupSessionDao

    companion object {
        val MIGRATION_13_14 = object : Migration(13, 14) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add columns to workouts table
                db.execSQL("ALTER TABLE workouts ADD COLUMN workoutMode TEXT NOT NULL DEFAULT 'CARDIO'")
                db.execSQL("ALTER TABLE workouts ADD COLUMN totalVolumeKg REAL DEFAULT NULL")
                db.execSQL("ALTER TABLE workouts ADD COLUMN totalSets INTEGER DEFAULT NULL")
                db.execSQL("ALTER TABLE workouts ADD COLUMN totalReps INTEGER DEFAULT NULL")
                db.execSQL("ALTER TABLE workouts ADD COLUMN planId TEXT DEFAULT NULL")

                // Create strength_workouts table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS strength_workouts (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        workoutId INTEGER NOT NULL,
                        name TEXT NOT NULL,
                        exercisesJson TEXT NOT NULL DEFAULT '[]',
                        warmupExercisesJson TEXT NOT NULL DEFAULT '[]',
                        cooldownExercisesJson TEXT NOT NULL DEFAULT '[]',
                        notes TEXT,
                        createdAt INTEGER NOT NULL,
                        FOREIGN KEY (workoutId) REFERENCES workouts(id) ON DELETE CASCADE
                    )
                """)

                // Create exercise_logs table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS exercise_logs (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        workoutId INTEGER NOT NULL,
                        exerciseId TEXT NOT NULL,
                        exerciseName TEXT NOT NULL,
                        primaryMuscleGroup TEXT NOT NULL,
                        setsCompleted INTEGER NOT NULL DEFAULT 0,
                        setsPlanned INTEGER NOT NULL DEFAULT 0,
                        maxWeightKg REAL,
                        totalVolumeKg REAL NOT NULL DEFAULT 0,
                        bestSetReps INTEGER,
                        bestSetWeightKg REAL,
                        averageRpe REAL,
                        timestamp INTEGER NOT NULL
                    )
                """)

                // Create body_measurements table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS body_measurements (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        timestamp INTEGER NOT NULL,
                        weightKg REAL,
                        bodyFatPercent REAL,
                        chestCm REAL,
                        waistCm REAL,
                        hipsCm REAL,
                        leftArmCm REAL,
                        rightArmCm REAL,
                        leftThighCm REAL,
                        rightThighCm REAL,
                        leftCalfCm REAL,
                        rightCalfCm REAL,
                        neckCm REAL,
                        photoUri TEXT,
                        notes TEXT
                    )
                """)

                // Create muscle_fatigue table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS muscle_fatigue (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        muscleGroup TEXT NOT NULL,
                        fatigueScore REAL NOT NULL DEFAULT 0,
                        lastTrainedAt INTEGER,
                        lastVolumeKg REAL NOT NULL DEFAULT 0,
                        recoveryHoursNeeded INTEGER NOT NULL DEFAULT 48,
                        updatedAt INTEGER NOT NULL
                    )
                """)

                // Create one_rm_history table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS one_rm_history (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        exerciseId TEXT NOT NULL,
                        exerciseName TEXT NOT NULL,
                        estimatedOneRmKg REAL NOT NULL,
                        basedOnWeightKg REAL NOT NULL,
                        basedOnReps INTEGER NOT NULL,
                        formula TEXT NOT NULL DEFAULT 'Brzycki',
                        timestamp INTEGER NOT NULL
                    )
                """)

                // Create indices
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_strength_workouts_workout ON strength_workouts(workoutId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_exercise_logs_workout ON exercise_logs(workoutId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_exercise_logs_exercise ON exercise_logs(exerciseId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_exercise_logs_timestamp ON exercise_logs(timestamp)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_body_measurements_timestamp ON body_measurements(timestamp)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS idx_muscle_fatigue_group ON muscle_fatigue(muscleGroup)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_one_rm_exercise ON one_rm_history(exerciseId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_one_rm_timestamp ON one_rm_history(timestamp)")
            }
        }

        val MIGRATION_14_15 = object : Migration(14, 15) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS group_sessions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        hostUserId TEXT NOT NULL,
                        hostName TEXT NOT NULL DEFAULT '',
                        scheduledTime INTEGER NOT NULL,
                        workoutTemplateJson TEXT NOT NULL DEFAULT '',
                        templateName TEXT NOT NULL DEFAULT '',
                        status TEXT NOT NULL DEFAULT 'SCHEDULED',
                        maxParticipants INTEGER NOT NULL DEFAULT 10,
                        participantCount INTEGER NOT NULL DEFAULT 0,
                        created INTEGER NOT NULL,
                        notes TEXT
                    )
                """)

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS group_session_participants (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        sessionId INTEGER NOT NULL,
                        userId TEXT NOT NULL,
                        userName TEXT NOT NULL DEFAULT '',
                        status TEXT NOT NULL DEFAULT 'JOINED',
                        joinedAt INTEGER NOT NULL,
                        completedAt INTEGER,
                        workoutId INTEGER,
                        totalVolumeKg REAL,
                        totalSets INTEGER,
                        FOREIGN KEY (sessionId) REFERENCES group_sessions(id) ON DELETE CASCADE
                    )
                """)

                db.execSQL("CREATE INDEX IF NOT EXISTS idx_group_sessions_time ON group_sessions(scheduledTime)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_group_sessions_host ON group_sessions(hostUserId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_group_participants_session ON group_session_participants(sessionId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_group_participants_user ON group_session_participants(userId)")
            }
        }
    }
}
