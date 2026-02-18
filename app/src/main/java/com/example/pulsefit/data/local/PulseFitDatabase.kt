package com.example.pulsefit.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pulsefit.data.local.dao.HeartRateReadingDao
import com.example.pulsefit.data.local.dao.UserProfileDao
import com.example.pulsefit.data.local.dao.WorkoutDao
import com.example.pulsefit.data.local.entity.HeartRateReadingEntity
import com.example.pulsefit.data.local.entity.UserProfileEntity
import com.example.pulsefit.data.local.entity.WorkoutEntity

@Database(
    entities = [
        UserProfileEntity::class,
        WorkoutEntity::class,
        HeartRateReadingEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class PulseFitDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun heartRateReadingDao(): HeartRateReadingDao
}
