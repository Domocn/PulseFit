package com.example.pulsefit.ui.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pulsefit.domain.repository.UserRepository
import com.example.pulsefit.domain.usecase.GetUserProfileUseCase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RewardShopViewModel @Inject constructor(
    private val getUserProfile: GetUserProfileUseCase,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _totalXp = MutableStateFlow(0L)
    val totalXp: StateFlow<Long> = _totalXp

    private val _ownedItems = MutableStateFlow<Set<String>>(emptySet())
    val ownedItems: StateFlow<Set<String>> = _ownedItems

    private val _streakShieldsOwned = MutableStateFlow(0)
    val streakShieldsOwned: StateFlow<Int> = _streakShieldsOwned

    private val gson = Gson()

    init {
        viewModelScope.launch {
            getUserProfile().collect { profile ->
                _totalXp.value = profile?.totalXp ?: 0
                _streakShieldsOwned.value = profile?.streakShieldsOwned ?: 0
                profile?.ownedItems?.let { json ->
                    try {
                        val type = object : TypeToken<List<String>>() {}.type
                        val items: List<String> = gson.fromJson(json, type) ?: emptyList()
                        _ownedItems.value = items.toSet()
                    } catch (_: Exception) {
                        _ownedItems.value = emptySet()
                    }
                }
            }
        }
    }

    fun purchase(item: ShopItem) {
        viewModelScope.launch {
            val profile = getUserProfile.once() ?: return@launch
            if (profile.totalXp < item.xpCost) return@launch

            val newXp = profile.totalXp - item.xpCost

            if (item.id == "streak_shield") {
                userRepository.saveUserProfile(
                    profile.copy(
                        totalXp = newXp,
                        streakShieldsOwned = profile.streakShieldsOwned + 1
                    )
                )
            } else {
                val currentItems = _ownedItems.value.toMutableSet()
                currentItems.add(item.id)
                val itemsJson = gson.toJson(currentItems.toList())
                userRepository.saveUserProfile(
                    profile.copy(
                        totalXp = newXp,
                        ownedItems = itemsJson
                    )
                )
            }
        }
    }
}
