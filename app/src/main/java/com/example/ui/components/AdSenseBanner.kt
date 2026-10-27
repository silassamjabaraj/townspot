package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.MomoTrustDisplayFontFamily
import com.example.ui.theme.TownSpotOrange
import com.example.util.AdSenseConfig
import com.example.util.AdSenseManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
fun GoogleAdSenseBanner(
    adUnitId: String,
    modifier: Modifier = Modifier,
    adSize: AdSize = AdSize.BANNER,
    placementTag: String = "general_banner",
    fallbackTitle: String = "Sponsored by Google AdSense",
    fallbackSubtitle: String = "Connecting local residents with community partners & services"
) {
    val context = LocalContext.current
    var isAdLoaded by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var adViewInstance by remember { mutableStateOf<AdView?>(null) }

    DisposableEffect(adUnitId) {
        onDispose {
            try {
                adViewInstance?.destroy()
            } catch (e: Exception) {
                Log.e("AdSenseBanner", "Error disposing AdView", e)
            }
        }
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("adsense_container_$placementTag")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Google AdSense Disclosure Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF4285F4).copy(alpha = 0.15f),
                        modifier = Modifier.padding(end = 6.dp)
                    ) {
                        Text(
                            text = "AD",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A73E8),
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = "Google AdSense",
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = MomoTrustDisplayFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        try {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://adsense.google.com/start/")
                            )
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Log.e("AdSenseBanner", "Failed to open AdSense link", e)
                        }
                    }
                ) {
                    Text(
                        text = "Sponsored",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Ad information",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            // Real Google Mobile Ads / AdSense Android View
            // On virtual/headless emulator without /dev/dri, render native Compose AdSense presentation directly
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("adview_$placementTag"),
                contentAlignment = Alignment.Center
            ) {
                if (AdSenseManager.isDriRendernodeAvailable) {
                    AndroidView(
                        modifier = Modifier.fillMaxWidth(),
                        factory = { ctx ->
                            AdView(ctx).apply {
                                try {
                                    setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null)
                                } catch (_: Exception) {}
                                setAdSize(adSize)
                                this.adUnitId = adUnitId
                                adListener = object : AdListener() {
                                    override fun onAdLoaded() {
                                        isAdLoaded = true
                                        hasError = false
                                        Log.d("AdSenseBanner", "Ad loaded successfully: $placementTag")
                                    }

                                    override fun onAdFailedToLoad(error: LoadAdError) {
                                        hasError = true
                                        errorMessage = error.message
                                        Log.w("AdSenseBanner", "Ad failed to load ($placementTag): ${error.message} (code ${error.code})")
                                    }

                                    override fun onAdClicked() {
                                        Log.d("AdSenseBanner", "Ad clicked: $placementTag")
                                    }
                                }
                                try {
                                    loadAd(AdRequest.Builder().build())
                                } catch (e: Exception) {
                                    Log.w("AdSenseBanner", "Ad load exception ($placementTag): ${e.message}")
                                    hasError = true
                                }
                                adViewInstance = this
                            }
                        }
                    )
                }

                // Native Compose AdSense presentation (used on emulators without /dev/dri or when ad is loading/fallback)
                if (!AdSenseManager.isDriRendernodeAvailable || hasError || !isAdLoaded) {
                    AdSensePlaceholderContent(
                        title = fallbackTitle,
                        subtitle = fallbackSubtitle,
                        placementTag = placementTag,
                        isLoading = AdSenseManager.isDriRendernodeAvailable && !hasError && !isAdLoaded
                    )
                }
            }
        }
    }
}

@Composable
private fun AdSensePlaceholderContent(
    title: String,
    subtitle: String,
    placementTag: String,
    isLoading: Boolean
) {
    val context = LocalContext.current
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable {
                try {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://adsense.google.com/start/")
                    )
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Log.e("AdSenseBanner", "Cannot launch partner link", e)
                }
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = TownSpotOrange.copy(alpha = 0.12f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = TownSpotOrange,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Campaign,
                            contentDescription = null,
                            tint = TownSpotOrange,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = androidx.compose.ui.text.TextStyle(
                        fontFamily = MomoTrustDisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Filled.OpenInNew,
                contentDescription = "Visit sponsor",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

/**
 * Resident Section Ad Placement:
 * Displays Google AdSense banner tailored for resident community feed, neighborhood exploration, and town events.
 */
@Composable
fun ResidentAdSenseSection(
    modifier: Modifier = Modifier,
    customTitle: String = "Community Spotlight • Google AdSense",
    customSubtitle: String = "Discover local Andaman island events, verified artisans & neighborhood initiatives"
) {
    val config by AdSenseManager.configState.collectAsState()
    if (!config.isEnabled || !config.isResidentAdsEnabled) return

    val unitId = AdSenseManager.getResidentBannerUnitId()

    GoogleAdSenseBanner(
        adUnitId = unitId,
        placementTag = "resident_section",
        fallbackTitle = customTitle,
        fallbackSubtitle = customSubtitle,
        modifier = modifier.padding(vertical = 8.dp)
    )
}

/**
 * Business Section Ad Placement:
 * Displays Google AdSense banner tailored for merchant listings, local business directories, and commercial deals.
 */
@Composable
fun BusinessAdSenseSection(
    modifier: Modifier = Modifier,
    customTitle: String = "Merchant Partner • Google AdSense",
    customSubtitle: String = "Featured offers, equipment rental & local business services"
) {
    val config by AdSenseManager.configState.collectAsState()
    if (!config.isEnabled || !config.isBusinessAdsEnabled) return

    val unitId = AdSenseManager.getBusinessBannerUnitId()

    GoogleAdSenseBanner(
        adUnitId = unitId,
        placementTag = "business_section",
        fallbackTitle = customTitle,
        fallbackSubtitle = customSubtitle,
        modifier = modifier.padding(vertical = 8.dp)
    )
}
