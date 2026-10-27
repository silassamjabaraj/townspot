package com.example.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdSenseConfig(
    val isEnabled: Boolean = true,
    val isResidentAdsEnabled: Boolean = true,
    val isBusinessAdsEnabled: Boolean = true,
    val isTestMode: Boolean = true,
    val publisherId: String = "pub-3940256099942544",
    val appId: String = "ca-app-pub-3940256099942544~3347511713",
    val residentBannerUnitId: String = "ca-app-pub-3940256099942544/6300978111",
    val businessBannerUnitId: String = "ca-app-pub-3940256099942544/9214589741",
    val nativeSponsoredUnitId: String = "ca-app-pub-3940256099942544/2247696110"
)

object AdSenseManager {
    private const val TAG = "AdSenseManager"
    private const val PREFS_NAME = "townspot_adsense_prefs"

    // Official Google Mobile Ads / AdSense sample test unit IDs
    const val TEST_BANNER_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    const val TEST_MEDIUM_RECT_UNIT_ID = "ca-app-pub-3940256099942544/9214589741"
    const val TEST_NATIVE_UNIT_ID = "ca-app-pub-3940256099942544/2247696110"

    private var prefs: SharedPreferences? = null
    private val _configState = MutableStateFlow(AdSenseConfig())
    val configState: StateFlow<AdSenseConfig> = _configState.asStateFlow()

    private var isInitialized = false

    /**
     * Checks if Direct Rendering Infrastructure (DRI) rendernode is accessible on the device.
     * In headless cloud emulator/container environments where /dev/dri does not exist,
     * skipping Chromium WebView GPU engine probes prevents MESA "Failed to open rendernode" errors.
     */
    val isDriRendernodeAvailable: Boolean by lazy {
        try {
            java.io.File("/dev/dri/renderD128").exists() || java.io.File("/dev/dri").exists()
        } catch (_: Exception) {
            false
        }
    }

    fun initialize(context: Context) {
        if (isInitialized) return
        try {
            prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            loadFromPrefs()

            if (isDriRendernodeAvailable) {
                // Set test device configuration before initialization
                val requestConfig = RequestConfiguration.Builder()
                    .setTestDeviceIds(listOf(AdRequest.DEVICE_ID_EMULATOR))
                    .build()
                MobileAds.setRequestConfiguration(requestConfig)

                // Initialize MobileAds asynchronously in background to avoid blocking main thread
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        MobileAds.initialize(context) { initStatus ->
                            Log.d(TAG, "Google Mobile Ads / AdSense SDK initialized: ${initStatus.adapterStatusMap}")
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "MobileAds initialization notice: ${e.message}")
                    }
                }
            } else {
                Log.d(TAG, "Headless container environment: Using native Compose AdSense banner presentation")
            }

            isInitialized = true
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Google Mobile Ads SDK", e)
        }
    }

    private fun loadFromPrefs() {
        prefs?.let { p ->
            _configState.value = AdSenseConfig(
                isEnabled = p.getBoolean("is_enabled", true),
                isResidentAdsEnabled = p.getBoolean("is_resident_ads_enabled", true),
                isBusinessAdsEnabled = p.getBoolean("is_business_ads_enabled", true),
                isTestMode = p.getBoolean("is_test_mode", true),
                publisherId = p.getString("publisher_id", "pub-3940256099942544") ?: "pub-3940256099942544",
                appId = p.getString("app_id", "ca-app-pub-3940256099942544~3347511713") ?: "ca-app-pub-3940256099942544~3347511713",
                residentBannerUnitId = p.getString("resident_banner_unit", TEST_BANNER_UNIT_ID) ?: TEST_BANNER_UNIT_ID,
                businessBannerUnitId = p.getString("business_banner_unit", TEST_MEDIUM_RECT_UNIT_ID) ?: TEST_MEDIUM_RECT_UNIT_ID,
                nativeSponsoredUnitId = p.getString("native_unit", TEST_NATIVE_UNIT_ID) ?: TEST_NATIVE_UNIT_ID
            )
        }
    }

    fun updateConfig(newConfig: AdSenseConfig) {
        _configState.value = newConfig
        prefs?.edit()?.apply {
            putBoolean("is_enabled", newConfig.isEnabled)
            putBoolean("is_resident_ads_enabled", newConfig.isResidentAdsEnabled)
            putBoolean("is_business_ads_enabled", newConfig.isBusinessAdsEnabled)
            putBoolean("is_test_mode", newConfig.isTestMode)
            putString("publisher_id", newConfig.publisherId)
            putString("app_id", newConfig.appId)
            putString("resident_banner_unit", newConfig.residentBannerUnitId)
            putString("business_banner_unit", newConfig.businessBannerUnitId)
            putString("native_unit", newConfig.nativeSponsoredUnitId)
            apply()
        }
    }

    fun getResidentBannerUnitId(): String {
        val conf = _configState.value
        return if (conf.isTestMode || conf.residentBannerUnitId.isBlank()) {
            TEST_BANNER_UNIT_ID
        } else {
            conf.residentBannerUnitId
        }
    }

    fun getBusinessBannerUnitId(): String {
        val conf = _configState.value
        return if (conf.isTestMode || conf.businessBannerUnitId.isBlank()) {
            TEST_MEDIUM_RECT_UNIT_ID
        } else {
            conf.businessBannerUnitId
        }
    }

    fun isResidentAdsVisible(): Boolean {
        val conf = _configState.value
        return conf.isEnabled && conf.isResidentAdsEnabled
    }

    fun isBusinessAdsVisible(): Boolean {
        val conf = _configState.value
        return conf.isEnabled && conf.isBusinessAdsEnabled
    }
}
