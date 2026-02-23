package com.pulsefit.app.ui.shop

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefit.app.data.remote.AuthRepository
import com.pulsefit.app.data.remote.DiscountCode
import com.pulsefit.app.data.remote.DiscountCodeRepository
import com.pulsefit.app.data.remote.RedeemedCode
import com.pulsefit.app.data.repository.RewardCoinRepository
import com.pulsefit.app.domain.repository.UserRepository
import com.pulsefit.app.domain.usecase.GetUserProfileUseCase
import com.pulsefit.app.ui.ads.RewardedAdManager
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
    private val userRepository: UserRepository,
    private val rewardCoinRepository: RewardCoinRepository,
    private val discountCodeRepository: DiscountCodeRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _totalXp = MutableStateFlow(0L)
    val totalXp: StateFlow<Long> = _totalXp

    private val _rewardCoins = MutableStateFlow(0)
    val rewardCoins: StateFlow<Int> = _rewardCoins

    private val _ownedItems = MutableStateFlow<Set<String>>(emptySet())
    val ownedItems: StateFlow<Set<String>> = _ownedItems

    private val _streakShieldsOwned = MutableStateFlow(0)
    val streakShieldsOwned: StateFlow<Int> = _streakShieldsOwned

    private val _adsRemaining = MutableStateFlow(5)
    val adsRemaining: StateFlow<Int> = _adsRemaining

    private val _discountCodes = MutableStateFlow<List<DiscountCode>>(emptyList())
    val discountCodes: StateFlow<List<DiscountCode>> = _discountCodes

    private val _redeemedCodes = MutableStateFlow<List<RedeemedCode>>(emptyList())
    val redeemedCodes: StateFlow<List<RedeemedCode>> = _redeemedCodes

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab

    private val _redeemError = MutableStateFlow<String?>(null)
    val redeemError: StateFlow<String?> = _redeemError

    val rewardedAdManager = RewardedAdManager()

    private val gson = Gson()

    init {
        viewModelScope.launch {
            getUserProfile().collect { profile ->
                _totalXp.value = profile?.totalXp ?: 0
                _rewardCoins.value = profile?.rewardCoins ?: 0
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

        viewModelScope.launch {
            rewardCoinRepository.adsRemainingToday().collect { remaining ->
                _adsRemaining.value = remaining
            }
        }

        loadDiscountCodes()
        loadRedeemedCodes()
    }

    fun selectTab(tab: Int) {
        _selectedTab.value = tab
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

    fun watchAd(activity: Activity) {
        viewModelScope.launch {
            if (!rewardCoinRepository.canWatchAd()) return@launch

            val userId = authRepository.currentUser?.uid
            rewardedAdManager.showAd(activity, userId = userId) {
                viewModelScope.launch {
                    rewardCoinRepository.recordAdWatched()
                }
            }
        }
    }

    fun redeemDiscountCode(code: DiscountCode) {
        viewModelScope.launch {
            _redeemError.value = null

            if (!rewardCoinRepository.spendCoins(code.coinCost)) {
                _redeemError.value = "Not enough Reward Coins"
                return@launch
            }

            val result = discountCodeRepository.redeemCode(code)
            result.fold(
                onSuccess = {
                    loadRedeemedCodes()
                    loadDiscountCodes()
                },
                onFailure = { error ->
                    // Refund coins on failure
                    rewardCoinRepository.spendCoins(-code.coinCost)
                    _redeemError.value = error.message ?: "Redemption failed"
                }
            )
        }
    }

    fun clearRedeemError() {
        _redeemError.value = null
    }

    private fun loadDiscountCodes() {
        viewModelScope.launch {
            _discountCodes.value = discountCodeRepository.getAvailableCodes()
        }
    }

    private fun loadRedeemedCodes() {
        viewModelScope.launch {
            _redeemedCodes.value = discountCodeRepository.getMyRedeemedCodes()
        }
    }
}
