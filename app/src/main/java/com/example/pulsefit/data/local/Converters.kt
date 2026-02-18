package com.example.pulsefit.data.local

import androidx.room.TypeConverter
import com.example.pulsefit.data.model.HeartRateZone
import com.example.pulsefit.data.model.NdProfile
import com.example.pulsefit.data.model.WorkoutType

class Converters {
    @TypeConverter
    fun fromNdProfile(value: NdProfile): String = value.name

    @TypeConverter
    fun toNdProfile(value: String): NdProfile = NdProfile.valueOf(value)

    @TypeConverter
    fun fromWorkoutType(value: WorkoutType): String = value.name

    @TypeConverter
    fun toWorkoutType(value: String): WorkoutType = WorkoutType.valueOf(value)

    @TypeConverter
    fun fromHeartRateZone(value: HeartRateZone): String = value.name

    @TypeConverter
    fun toHeartRateZone(value: String): HeartRateZone = HeartRateZone.valueOf(value)
}
