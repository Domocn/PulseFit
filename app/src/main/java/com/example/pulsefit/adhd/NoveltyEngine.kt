package com.example.pulsefit.adhd

import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

data class WeeklyTheme(
    val name: String,
    val primaryHue: Float,
    val accentHue: Float
)

@Singleton
class NoveltyEngine @Inject constructor() {
    private val themes = listOf(
        WeeklyTheme("Default Purple", 263f, 160f),
        WeeklyTheme("Ocean Blue", 210f, 180f),
        WeeklyTheme("Forest Green", 140f, 100f),
        WeeklyTheme("Sunset Orange", 25f, 340f),
        WeeklyTheme("Cherry Red", 350f, 280f),
        WeeklyTheme("Golden Hour", 45f, 20f),
        WeeklyTheme("Arctic", 195f, 220f),
        WeeklyTheme("Neon Pink", 320f, 260f)
    )

    fun getWeeklyTheme(): WeeklyTheme {
        val weekOfYear = LocalDate.now().let {
            it.dayOfYear / 7
        }
        return themes[weekOfYear % themes.size]
    }

    fun getRandomCelebrationIndex(poolSize: Int = 20): Int {
        return (System.currentTimeMillis() % poolSize).toInt()
    }
}
