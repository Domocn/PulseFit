package com.example.pulsefit.ui.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulsefit.data.local.dao.AchievementDao
import com.example.pulsefit.data.local.entity.AchievementEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AchievementsViewModel @Inject constructor(
    private val achievementDao: AchievementDao
) : ViewModel() {

    val achievements: StateFlow<List<AchievementEntity>> = achievementDao.getAllAchievements()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            seedAchievements()
        }
    }

    private suspend fun seedAchievements() {
        val defaults = listOf(
            AchievementEntity("first_workout", "First Steps", "Complete your first workout", "fitness_center"),
            AchievementEntity("ten_workouts", "Getting Serious", "Complete 10 workouts", "fitness_center"),
            AchievementEntity("fifty_workouts", "Dedicated", "Complete 50 workouts", "fitness_center"),
            AchievementEntity("hundred_workouts", "Centurion", "Complete 100 workouts", "fitness_center"),
            AchievementEntity("streak_3", "On a Roll", "Maintain a 3-day streak", "local_fire_department"),
            AchievementEntity("streak_7", "Week Warrior", "Maintain a 7-day streak", "local_fire_department"),
            AchievementEntity("streak_30", "Monthly Master", "Maintain a 30-day streak", "local_fire_department"),
            AchievementEntity("burn_100", "Point Collector", "Earn 100 total burn points", "star"),
            AchievementEntity("burn_1000", "Point Hoarder", "Earn 1,000 total burn points", "star"),
            AchievementEntity("peak_5min", "Peak Performer", "Stay in Peak zone for 5 minutes", "trending_up"),
            AchievementEntity("push_15min", "Push Master", "Stay in Push zone for 15 minutes", "trending_up"),
            AchievementEntity("level_5", "Rising Star", "Reach level 5", "emoji_events"),
            AchievementEntity("level_10", "Veteran", "Reach level 10", "emoji_events"),
            AchievementEntity("just_five_min", "Tricked Ya", "Start a Just 5 Min workout and go 15+ min", "timer"),
            AchievementEntity("hyperfocus", "Hyperfocus", "15+ min sustained Push/Peak zone", "psychology", category = "adhd")
        )
        achievementDao.insertAll(defaults)
    }
}
