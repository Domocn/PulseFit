package com.pulsefit.app.data.local

import androidx.room.TypeConverter
import com.pulsefit.app.data.model.AnimationLevel
import com.pulsefit.app.data.model.AppTheme
import com.pulsefit.app.data.model.CelebrationStyle
import com.pulsefit.app.data.model.ColourIntensity
import com.pulsefit.app.data.model.HapticLevel
import com.pulsefit.app.data.model.HeartRateZone
import com.pulsefit.app.data.model.NdProfile
import com.pulsefit.app.data.model.QuestType
import com.pulsefit.app.data.model.SoundLevel
import com.pulsefit.app.data.model.VoiceCoachStyle
import com.pulsefit.app.data.model.WorkoutType

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

    @TypeConverter
    fun fromAnimationLevel(value: AnimationLevel): String = value.name

    @TypeConverter
    fun toAnimationLevel(value: String): AnimationLevel = AnimationLevel.valueOf(value)

    @TypeConverter
    fun fromSoundLevel(value: SoundLevel): String = value.name

    @TypeConverter
    fun toSoundLevel(value: String): SoundLevel = SoundLevel.valueOf(value)

    @TypeConverter
    fun fromHapticLevel(value: HapticLevel): String = value.name

    @TypeConverter
    fun toHapticLevel(value: String): HapticLevel = HapticLevel.valueOf(value)

    @TypeConverter
    fun fromColourIntensity(value: ColourIntensity): String = value.name

    @TypeConverter
    fun toColourIntensity(value: String): ColourIntensity = ColourIntensity.valueOf(value)

    @TypeConverter
    fun fromCelebrationStyle(value: CelebrationStyle): String = value.name

    @TypeConverter
    fun toCelebrationStyle(value: String): CelebrationStyle = CelebrationStyle.valueOf(value)

    @TypeConverter
    fun fromVoiceCoachStyle(value: VoiceCoachStyle): String = value.name

    @TypeConverter
    fun toVoiceCoachStyle(value: String): VoiceCoachStyle = VoiceCoachStyle.valueOf(value)

    @TypeConverter
    fun fromQuestType(value: QuestType): String = value.name

    @TypeConverter
    fun toQuestType(value: String): QuestType = QuestType.valueOf(value)

    @TypeConverter
    fun fromAppTheme(value: AppTheme): String = value.name

    @TypeConverter
    fun toAppTheme(value: String): AppTheme = AppTheme.valueOf(value)
}
