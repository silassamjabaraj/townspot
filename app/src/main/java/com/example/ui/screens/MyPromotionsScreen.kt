package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusinessEntity
import com.example.data.model.PromotionEntity
import com.example.data.model.UserEntity
import com.example.ui.components.BusinessAdSenseSection
import com.example.ui.components.PostPromotionDialog
import com.example.ui.components.PromotionCard
import com.example.ui.theme.BuListioCoral

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPromotionsScreen(
    currentUser: UserEntity?,
    allBusinesses: List<BusinessEntity>,
    promotions: List<PromotionEntity>,
    onSelectBusiness: (Long) -> Unit,
    onPostPromotion: (businessId: Long, title: String, discount: String, promoCode: String?, terms: String, validUntil: String) -> Unit,
    onDeletePromotion: (Long) -> Unit,
    onOpenAddBusiness: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    // Owner's listed business(es) only!
    val ownedBusinesses = allBusinesses.filter { biz ->
        currentUser?.email != null && biz.claimedByOwnerEmail.equals(currentUser.email, ignoreCase = true)
    }

    // Owner's active promotions
    val myPromotions = promotions.filter { promo ->
        ownedBusinesses.any { it.id == promo.businessId }
    }

    var showPostPromotionDialog by remember { mutableStateOf(false) }
    var showNoBusinessDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = BuListioCoral.copy(alpha = 0.12f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Campaign,
                                    contentDescription = null,
                                    tint = BuListioCoral,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "My Active Promotions",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp
                                )
                            )
                            Text(
                                text = "Broadcast deals for your listed spot",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("my_promotions_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (ownedBusinesses.isEmpty()) {
                                showNoBusinessDialog = true
                            } else {
                                showPostPromotionDialog = true
                            }
                        },
                        modifier = Modifier.testTag("my_promotions_add_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "New Promo",
                            tint = BuListioCoral
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Promotion action callout card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().testTag("post_promo_banner_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Promote Your Business",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Broadcast special discounts, coupon codes, and limited-time deals directly to local community residents.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                if (ownedBusinesses.isEmpty()) {
                                    showNoBusinessDialog = true
                                } else {
                                    showPostPromotionDialog = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BuListioCoral),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("owner_post_promotion_btn")
                        ) {
                            Icon(Icons.Filled.Campaign, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Post New Promotion / Deal", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Google AdSense Banner for Merchant Deals
            item {
                BusinessAdSenseSection(customTitle = "Merchant Spotlight • Google AdSense")
            }

            // Promotions Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active Live Promotions (${myPromotions.size})",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    if (ownedBusinesses.isNotEmpty()) {
                        Text(
                            text = "For: ${ownedBusinesses.first().name}",
                            style = MaterialTheme.typography.labelSmall,
                            color = BuListioCoral
                        )
                    }
                }
            }

            if (myPromotions.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocalOffer,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No active promotions published yet.",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap 'Post New Promotion / Deal' above to broadcast discounts and coupon codes to local residents.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(myPromotions, key = { it.id }) { promo ->
                    PromotionCard(
                        promotion = promo,
                        onViewBusiness = onSelectBusiness,
                        onDelete = {
                            onDeletePromotion(promo.id)
                            Toast.makeText(context, "Promotion retired and removed.", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    // Post Promotion Dialog - In Post New Promotion user can select their listed business only!
    if (showPostPromotionDialog && ownedBusinesses.isNotEmpty()) {
        PostPromotionDialog(
            businesses = ownedBusinesses, // ONLY owner's listed businesses!
            initialBusinessId = ownedBusinesses.first().id,
            onDismiss = { showPostPromotionDialog = false },
            onSubmit = { bId, title, discount, code, terms, validity ->
                onPostPromotion(bId, title, discount, code, terms, validity)
                showPostPromotionDialog = false
                Toast.makeText(context, "Promotion posted live to residents!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Dialog if owner has not listed a business yet
    if (showNoBusinessDialog) {
        AlertDialog(
            onDismissRequest = { showNoBusinessDialog = false },
            icon = { Icon(Icons.Filled.Store, contentDescription = null, tint = BuListioCoral) },
            title = { Text("List Your Business First") },
            text = { Text("To broadcast promotions and discount codes, you must first list your business spot in the TownSpot directory.") },
            confirmButton = {
                Button(
                    onClick = {
                        showNoBusinessDialog = false
                        onOpenAddBusiness()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BuListioCoral)
                ) {
                    Text("List Business Spot")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNoBusinessDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
