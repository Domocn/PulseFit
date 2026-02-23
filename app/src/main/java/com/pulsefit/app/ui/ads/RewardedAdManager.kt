package com.pulsefit.app.ui.ads

import android.app.Activity
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewarded.ServerSideVerificationOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class AdState {
    NotLoaded,
    Loading,
    Ready,
    Showing,
    Error
}

class RewardedAdManager {

    companion object {
        private const val TAG = "RewardedAdManager"
        // Test ad unit ID - replace with real ID for production
        private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
    }

    private var rewardedAd: RewardedAd? = null

    private val _adState = MutableStateFlow(AdState.NotLoaded)
    val adState: StateFlow<AdState> = _adState

    fun loadAd(activity: Activity) {
        if (_adState.value == AdState.Loading || _adState.value == AdState.Ready) return

        _adState.value = AdState.Loading

        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(activity, AD_UNIT_ID, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
                rewardedAd = ad
                _adState.value = AdState.Ready
                Log.d(TAG, "Rewarded ad loaded")
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                rewardedAd = null
                _adState.value = AdState.Error
                Log.w(TAG, "Rewarded ad failed to load: ${error.message}")
            }
        })
    }

    /**
     * Show a rewarded ad with Server-Side Verification (SSV).
     *
     * @param activity The hosting activity
     * @param userId The user's Firebase UID or local ID, sent to the SSV callback
     * @param onRewarded Called when the user earns the reward (client-side fallback)
     */
    fun showAd(activity: Activity, userId: String? = null, onRewarded: () -> Unit) {
        val ad = rewardedAd
        if (ad == null) {
            _adState.value = AdState.NotLoaded
            return
        }

        // Attach SSV options so Google forwards user_id + custom_data to our callback
        if (userId != null) {
            val ssvOptions = ServerSideVerificationOptions.Builder()
                .setUserId(userId)
                .setCustomData("reward_coins_10")
                .build()
            ad.setServerSideVerificationOptions(ssvOptions)
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                rewardedAd = null
                _adState.value = AdState.NotLoaded
                // Preload next ad
                loadAd(activity)
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                rewardedAd = null
                _adState.value = AdState.Error
                Log.w(TAG, "Rewarded ad failed to show: ${error.message}")
            }

            override fun onAdShowedFullScreenContent() {
                _adState.value = AdState.Showing
            }
        }

        ad.show(activity) {
            // Client-side callback fires immediately.
            // The SSV callback on the server is the authoritative source of truth,
            // but we still credit locally for responsive UX.
            onRewarded()
        }
    }
}
