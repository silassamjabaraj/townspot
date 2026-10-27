package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.MomoTrustDisplayFontFamily
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TownSpotOrange
import com.example.util.AdSenseConfig
import com.example.util.AdSenseManager

@Composable
fun AdminAdSenseSection(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentConfig by AdSenseManager.configState.collectAsState()

    var isEnabled by remember(currentConfig) { mutableStateOf(currentConfig.isEnabled) }
    var isResidentEnabled by remember(currentConfig) { mutableStateOf(currentConfig.isResidentAdsEnabled) }
    var isBusinessEnabled by remember(currentConfig) { mutableStateOf(currentConfig.isBusinessAdsEnabled) }
    var isTestMode by remember(currentConfig) { mutableStateOf(currentConfig.isTestMode) }

    var publisherId by remember(currentConfig) { mutableStateOf(currentConfig.publisherId) }
    var appId by remember(currentConfig) { mutableStateOf(currentConfig.appId) }
    var residentUnitId by remember(currentConfig) { mutableStateOf(currentConfig.residentBannerUnitId) }
    var businessUnitId by remember(currentConfig) { mutableStateOf(currentConfig.businessBannerUnitId) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header & Status Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A73E8).copy(alpha = 0.08f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1A73E8).copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF1A73E8).copy(alpha = 0.15f),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Filled.Campaign,
                                        contentDescription = null,
                                        tint = Color(0xFF1A73E8),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Google AdSense",
                                    style = androidx.compose.ui.text.TextStyle(
                                        fontFamily = MomoTrustDisplayFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Mobile Ads & Monetization Network",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isEnabled) {
                                if (isTestMode) Color(0xFFFFF7ED) else SuccessGreen.copy(alpha = 0.15f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        ) {
                            Text(
                                text = if (isEnabled) {
                                    if (isTestMode) "Test Mode Active" else "Live Production"
                                } else {
                                    "Ads Inactive"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isEnabled) {
                                    if (isTestMode) TownSpotOrange else SuccessGreen
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AdStatItem(label = "Resident Section", value = if (isEnabled && isResidentEnabled) "Active" else "Off", isGreen = isEnabled && isResidentEnabled)
                        AdStatItem(label = "Business Section", value = if (isEnabled && isBusinessEnabled) "Active" else "Off", isGreen = isEnabled && isBusinessEnabled)
                        AdStatItem(label = "SDK Status", value = "Initialized", isGreen = true)
                    }
                }
            }
        }

        // 2. Section Placement Toggles
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Placement Switches",
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = MomoTrustDisplayFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Master Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Master AdSense Switch",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Globally enables or pauses all Google advertisements",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isEnabled,
                            onCheckedChange = { isEnabled = it },
                            modifier = Modifier.testTag("adsense_master_switch")
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                    // Resident Section Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Public, contentDescription = null, tint = TownSpotOrange, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Resident Section Ads",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                            }
                            Text(
                                text = "Display ads in Resident Home Feed & Neighborhoods Zone directory",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isResidentEnabled,
                            onCheckedChange = { isResidentEnabled = it },
                            enabled = isEnabled,
                            modifier = Modifier.testTag("adsense_resident_switch")
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                    // Business Section Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Store, contentDescription = null, tint = Color(0xFF1A73E8), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Business Section Ads",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                            }
                            Text(
                                text = "Display sponsored ads in Business Details & Merchant management screens",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isBusinessEnabled,
                            onCheckedChange = { isBusinessEnabled = it },
                            enabled = isEnabled,
                            modifier = Modifier.testTag("adsense_business_switch")
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                    // Google Official Test Mode Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Official Google Test Ads Mode",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Always use official Google test unit IDs during testing to avoid account policy strikes",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isTestMode,
                            onCheckedChange = { isTestMode = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = TownSpotOrange),
                            modifier = Modifier.testTag("adsense_test_mode_switch")
                        )
                    }
                }
            }
        }

        // 3. AdSense Configuration Credentials
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Google AdSense & AdMob Configuration",
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = MomoTrustDisplayFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )

                    OutlinedTextField(
                        value = publisherId,
                        onValueChange = { publisherId = it },
                        label = { Text("Google AdSense Publisher ID") },
                        placeholder = { Text("pub-3940256099942544") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("adsense_publisher_id_input")
                    )

                    OutlinedTextField(
                        value = appId,
                        onValueChange = { appId = it },
                        label = { Text("Google Mobile Ads App ID") },
                        placeholder = { Text("ca-app-pub-3940256099942544~3347511713") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("adsense_app_id_input")
                    )

                    OutlinedTextField(
                        value = residentUnitId,
                        onValueChange = { residentUnitId = it },
                        label = { Text("Resident Section Banner Ad Unit ID") },
                        placeholder = { Text(AdSenseManager.TEST_BANNER_UNIT_ID) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("adsense_resident_unit_input")
                    )

                    OutlinedTextField(
                        value = businessUnitId,
                        onValueChange = { businessUnitId = it },
                        label = { Text("Business Section Banner Ad Unit ID") },
                        placeholder = { Text(AdSenseManager.TEST_MEDIUM_RECT_UNIT_ID) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("adsense_business_unit_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = {
                                isTestMode = true
                                publisherId = "pub-3940256099942544"
                                appId = "ca-app-pub-3940256099942544~3347511713"
                                residentUnitId = AdSenseManager.TEST_BANNER_UNIT_ID
                                businessUnitId = AdSenseManager.TEST_MEDIUM_RECT_UNIT_ID
                            },
                            modifier = Modifier.testTag("adsense_reset_defaults_btn")
                        ) {
                            Icon(Icons.Filled.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset Test Defaults")
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Button(
                            onClick = {
                                AdSenseManager.updateConfig(
                                    AdSenseConfig(
                                        isEnabled = isEnabled,
                                        isResidentAdsEnabled = isResidentEnabled,
                                        isBusinessAdsEnabled = isBusinessEnabled,
                                        isTestMode = isTestMode,
                                        publisherId = publisherId.trim(),
                                        appId = appId.trim(),
                                        residentBannerUnitId = residentUnitId.trim(),
                                        businessBannerUnitId = businessUnitId.trim(),
                                        nativeSponsoredUnitId = currentConfig.nativeSponsoredUnitId
                                    )
                                )
                                Toast.makeText(context, "Google AdSense configuration saved!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TownSpotOrange),
                            modifier = Modifier.testTag("adsense_save_btn")
                        ) {
                            Icon(Icons.Filled.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save Changes")
                        }
                    }
                }
            }
        }

        // 4. Live Ad Placements Preview
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Live Placement Previews",
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = MomoTrustDisplayFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                    Text(
                        text = "Preview how sponsored partner cards render in active resident and business app sections",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "1. Resident Section Preview (HomeScreen & Neighborhoods)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = TownSpotOrange
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    ResidentAdSenseSection()

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "2. Business Section Preview (Detail & My Listing)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A73E8)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    BusinessAdSenseSection()
                }
            }
        }
    }
}

@Composable
private fun AdStatItem(label: String, value: String, isGreen: Boolean) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = androidx.compose.ui.text.TextStyle(
                fontFamily = MomoTrustDisplayFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            ),
            color = if (isGreen) SuccessGreen else MaterialTheme.colorScheme.onSurface
        )
    }
}
