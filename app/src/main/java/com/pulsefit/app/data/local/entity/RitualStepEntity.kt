package com.pulsefit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pulsefit.app.data.model.RitualType

@Entity(tableName = "ritual_steps")
data class RitualStepEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ritualType: RitualType,
    val sortOrder: Int,
    val label: String,
    val isCompleted: Boolean = false
)
