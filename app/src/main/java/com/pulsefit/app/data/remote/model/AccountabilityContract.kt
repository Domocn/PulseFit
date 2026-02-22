package com.pulsefit.app.data.remote.model

data class AccountabilityContract(
    val id: String = "",
    val participants: List<String> = emptyList(),
    val weeklyGoal: Int = 3,
    val createdAt: Long = System.currentTimeMillis(),
    val status: String = "active",
    val partnerName: String = ""
)
