package com.pulsefit.app.data.exercise

import com.pulsefit.app.data.local.entity.RitualStepEntity
import com.pulsefit.app.data.model.RitualType

object DefaultRitualTemplates {

    fun getDefaults(type: RitualType): List<RitualStepEntity> = when (type) {
        RitualType.PRE -> listOf(
            RitualStepEntity(ritualType = RitualType.PRE, sortOrder = 0, label = "Fill water bottle"),
            RitualStepEntity(ritualType = RitualType.PRE, sortOrder = 1, label = "Put on workout clothes"),
            RitualStepEntity(ritualType = RitualType.PRE, sortOrder = 2, label = "Choose playlist or podcast"),
            RitualStepEntity(ritualType = RitualType.PRE, sortOrder = 3, label = "Pack headphones"),
            RitualStepEntity(ritualType = RitualType.PRE, sortOrder = 4, label = "Check gym bag essentials"),
            RitualStepEntity(ritualType = RitualType.PRE, sortOrder = 5, label = "Quick stretch (2 min)")
        )
        RitualType.POST -> listOf(
            RitualStepEntity(ritualType = RitualType.POST, sortOrder = 0, label = "Cool-down walk (3 min)"),
            RitualStepEntity(ritualType = RitualType.POST, sortOrder = 1, label = "Stretch major muscle groups"),
            RitualStepEntity(ritualType = RitualType.POST, sortOrder = 2, label = "Drink water"),
            RitualStepEntity(ritualType = RitualType.POST, sortOrder = 3, label = "Log how you feel"),
            RitualStepEntity(ritualType = RitualType.POST, sortOrder = 4, label = "Shower or change clothes"),
            RitualStepEntity(ritualType = RitualType.POST, sortOrder = 5, label = "Eat a snack within 30 min")
        )
    }
}
