package com.example.pulsefit.adhd

import com.example.pulsefit.data.model.HeartRateZone
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import kotlin.random.Random

data class DropEvent(
    val type: DropType,
    val value: Int,
    val message: String
)

enum class DropType {
    BONUS_XP, BONUS_POINTS, STREAK_SHIELD
}

class VariableDropEngine @Inject constructor() {
    private val _dropEvents = MutableSharedFlow<DropEvent>(extraBufferCapacity = 5)
    val dropEvents: SharedFlow<DropEvent> = _dropEvents

    private var lastCheckSecond = 0
    private val checkIntervalSeconds = 60

    suspend fun onTick(elapsedSeconds: Int, zone: HeartRateZone) {
        if (elapsedSeconds - lastCheckSecond < checkIntervalSeconds) return
        lastCheckSecond = elapsedSeconds

        // 15% chance of drop per minute in Active+ zones
        if (zone.pointsPerMinute > 0 && Random.nextFloat() < 0.15f) {
            val drop = generateDrop()
            _dropEvents.emit(drop)
        }
    }

    private fun generateDrop(): DropEvent {
        val roll = Random.nextFloat()
        return when {
            roll < 0.6f -> DropEvent(
                DropType.BONUS_XP,
                Random.nextInt(10, 50),
                "Bonus XP!"
            )
            roll < 0.9f -> DropEvent(
                DropType.BONUS_POINTS,
                Random.nextInt(1, 3),
                "Bonus Points!"
            )
            else -> DropEvent(
                DropType.STREAK_SHIELD,
                1,
                "Streak Shield found!"
            )
        }
    }

    fun reset() {
        lastCheckSecond = 0
    }
}
