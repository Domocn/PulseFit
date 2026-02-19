package com.example.pulsefit.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pulsefit.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileOnce(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: UserProfileEntity)

    @Query("UPDATE user_profile SET totalXp = totalXp + :xp, xpLevel = :newLevel WHERE id = 1")
    suspend fun updateXp(xp: Long, newLevel: Int)

    @Query("UPDATE user_profile SET currentStreak = :streak, longestStreak = CASE WHEN :streak > longestStreak THEN :streak ELSE longestStreak END, streakShieldUsedThisWeek = :shieldUsed WHERE id = 1")
    suspend fun updateStreak(streak: Int, shieldUsed: Boolean)

    @Query("UPDATE user_profile SET totalWorkouts = totalWorkouts + 1, totalBurnPoints = totalBurnPoints + :burnPoints, lastWorkoutAt = :workoutTime WHERE id = 1")
    suspend fun incrementWorkoutCount(burnPoints: Int, workoutTime: Long)
}
