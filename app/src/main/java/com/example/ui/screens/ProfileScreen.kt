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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.CircularProgressIndicator
import com.example.data.model.BusinessEntity
import com.example.data.model.PromotionEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.UserEntity
import com.example.data.remote.supabase.SyncResult
import com.example.data.remote.supabase.SyncUiState
import com.example.ui.components.PostPromotionDialog
import com.example.ui.components.ReviewItem
import com.example.ui.theme.BuListioCoral
import com.example.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    currentUser: UserEntity?,
    allBusinesses: List<BusinessEntity> = emptyList(),
    promotions: List<PromotionEntity> = emptyList(),
    savedCount: Int = 0,
    reviews: List<ReviewEntity> = emptyList(),
    authError: String? = null,
    isAuthLoading: Boolean = false,
    syncState: SyncUiState = SyncUiState(),
    isSupabaseConfigured: Boolean = false,
    onSyncWithSupabase: ((SyncResult) -> Unit) -> Unit = {},
    onSignIn: (email: String, pass: String) -> Unit = { _, _ -> },
    onRegister: (fullName: String, email: String, pass: String, role: String, businessName: String?) -> Unit = { _, _, _, _, _ -> },
    onQuickSignIn: (role: String) -> Unit = {},
    onSignOut: () -> Unit = {},
    onClearAuthError: () -> Unit = {},
    onOpenSaved: () -> Unit = {},
    onOpenAddBusiness: () -> Unit = {},
    onOpenAdmin: () -> Unit = {},
    onOpenMyListing: () -> Unit = {},
    onOpenMyPromotions: () -> Unit = {},
    onSelectBusiness: (Long) -> Unit = {},
    onToggleSaved: (Long, Boolean) -> Unit = { _, _ -> },
    onPostPromotion: (businessId: Long, title: String, discount: String, promoCode: String?, terms: String, validUntil: String) -> Unit = { _, _, _, _, _, _ -> },
    onDeletePromotion: (Long) -> Unit = {},
    onBack: () -> Unit = {},
    onOpenLogin: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    // Business Owner Post Promotion Dialog
    var showPostPromotionDialog by remember { mutableStateOf(false) }

    val userReviewsCount = if (currentUser != null) {
        reviews.count { it.authorName.equals(currentUser.fullName, ignoreCase = true) }
    } else 0

    // Businesses owned/claimed by current owner (only owner's listed spots)
    val ownedBusinesses = remember(currentUser, allBusinesses) {
        if (currentUser?.role == "Business Owner") {
            allBusinesses.filter {
                it.claimedByOwnerEmail.equals(currentUser.email, ignoreCase = true) ||
                        (currentUser.businessName != null && it.name.equals(currentUser.businessName, ignoreCase = true))
            }
        } else emptyList()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when (currentUser?.role) {
                                "Business Owner" -> Icons.Filled.Store
                                "Admin" -> Icons.Filled.AdminPanelSettings
                                else -> if (currentUser != null) Icons.Filled.Person else Icons.Filled.AccountCircle
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (currentUser != null && currentUser.fullName.isNotBlank()) {
                                    currentUser.fullName
                                } else {
                                    "My Profile"
                                },
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            if (currentUser != null) {
                                Text(
                                    text = when (currentUser.role) {
                                        "Business Owner" -> "Merchant Account"
                                        "Admin" -> "Administrator"
                                        else -> "Community Member"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("profile_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (currentUser != null) {
                        IconButton(
                            onClick = onSignOut,
                            modifier = Modifier.testTag("profile_topbar_signout")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Logout,
                                contentDescription = "Sign Out",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // =========================================================================
            // RESIDENT PROFILE VIEW (Saved Places, My Reviews & Account Details)
            // =========================================================================
            if (currentUser != null && currentUser.role != "Business Owner" && currentUser.role != "Admin") {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(4.dp)) }

                    item {
                        ResidentHeroCard(currentUser = currentUser)
                    }

                    item {
                        ResidentStatsRow(
                            savedCount = savedCount,
                            userReviewsCount = userReviewsCount,
                            onOpenSaved = onOpenSaved
                        )
                    }

                    item {
                        ResidentAccountPrivilegesCard()
                    }

                    // Saved Places Quick Navigation Card
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("resident_saved_places_card")
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.Bookmark, contentDescription = null, tint = BuListioCoral, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Saved Places ($savedCount)",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                    TextButton(onClick = onOpenSaved) {
                                        Text("View All", color = BuListioCoral, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Text(
                                    text = "Quickly access your saved local cafes, restaurants, gyms, and favorite community spots.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // My Reviews Section
                    item {
                        val myReviews = reviews.filter {
                            it.authorName.equals(currentUser.fullName, ignoreCase = true)
                        }
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "My Reviews & Community Feedback (${myReviews.size})",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                if (myReviews.isEmpty()) {
                                    Text(
                                        text = "You haven't posted any reviews yet. Share your experience at local spots to support neighborhood businesses!",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                } else {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        myReviews.take(4).forEach { review ->
                                            ReviewItem(review = review, onHelpfulClicked = {})
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Account Sign Out
                    item {
                        AccountSignOutSection(onSignOut = onSignOut)
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            } else if (currentUser != null && currentUser.role == "Business Owner") {
                // =========================================================================
                // BUSINESS OWNER PROFILE VIEW
                // (Removed Saved Places, removed All Listing, removed My Active Promotions)
                // =========================================================================
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(4.dp)) }

                    // Merchant Hero Card
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("profile_merchant_hero_card")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                                                MaterialTheme.colorScheme.surface
                                            )
                                        )
                                    )
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(76.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = currentUser.fullName.take(1).uppercase(),
                                            style = MaterialTheme.typography.headlineMedium.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                color = MaterialTheme.colorScheme.onTertiary
                                            )
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = currentUser.fullName,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                                )
                                Text(
                                    text = currentUser.email,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.tertiaryContainer
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Filled.Store, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiaryContainer, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Verified Business Owner",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onTertiaryContainer
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // My Business Spot Summary Card
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("owner_spot_summary_card")
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "My Business Spot",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                if (ownedBusinesses.isNotEmpty()) {
                                    val myBiz = ownedBusinesses.first()
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onOpenMyListing() }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = if (myBiz.isVerified) Icons.Filled.CheckCircle else Icons.Filled.HourglassTop,
                                                contentDescription = null,
                                                tint = if (myBiz.isVerified) SuccessGreen else Color(0xFFC2410C),
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(text = myBiz.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                                Text(
                                                    text = if (myBiz.isVerified) "${myBiz.category} • Verified" else "Pending Admin Verification",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = if (myBiz.isVerified) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFFC2410C)
                                                )
                                            }
                                            Text(
                                                text = "Edit",
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = BuListioCoral)
                                            )
                                        }
                                    }
                                } else {
                                    Text(
                                        text = "You do not currently manage an active listing. Tap 'List New Business Spot' below to get started.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Merchant Management Navigation
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("merchant_quick_actions_card")
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "Merchant Quick Actions",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )

                                Button(
                                    onClick = onOpenMyListing,
                                    colors = ButtonDefaults.buttonColors(containerColor = BuListioCoral),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("profile_goto_my_listing_btn")
                                ) {
                                    Icon(Icons.Filled.Store, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("View & Edit My Listing", fontWeight = FontWeight.Bold)
                                }

                                val ownerPromosCount = promotions.count { p -> ownedBusinesses.any { it.id == p.businessId } }
                                OutlinedButton(
                                    onClick = onOpenMyPromotions,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("profile_goto_my_promotions_btn")
                                ) {
                                    Icon(Icons.Filled.Campaign, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("My Active Promotions ($ownerPromosCount)")
                                }

                                OutlinedButton(
                                    onClick = onOpenAddBusiness,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("profile_list_new_spot_btn")
                                ) {
                                    Icon(Icons.Filled.AddBusiness, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("List a New Business Spot")
                                }
                            }
                        }
                    }

                    // Ownership Policy Card
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(Icons.Filled.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp).padding(top = 2.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Business Owner Policy",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Each business owner manages only their own verified spot. You cannot claim other owners' listings. New listings must be verified by an administrator before appearing to all community members.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Account Sign Out
                    item {
                        AccountSignOutSection(onSignOut = onSignOut)
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            } else if (currentUser != null && currentUser.role == "Admin") {
                // =========================================================================
                // ADMIN PROFILE VIEW (Can have all features)
                // =========================================================================
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(4.dp)) }
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                                                MaterialTheme.colorScheme.surface
                                            )
                                        )
                                    )
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(76.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = currentUser.fullName.take(1).uppercase(),
                                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onError)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(currentUser.fullName, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold))
                                Text(currentUser.email, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Directory Administration", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = onOpenAdmin,
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("profile_open_admin_btn")
                                ) {
                                    Icon(Icons.Filled.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Open Admin Portal")
                                }
                            }
                        }
                    }

                    item {
                        AccountSignOutSection(onSignOut = onSignOut)
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            } else {
                // =========================================================================
                // GUEST / NOT LOGGED IN VIEW
                // =========================================================================
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(20.dp)) }
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("profile_guest_card")
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(64.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Filled.AccountCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Welcome to TownSpot", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Sign in to access resident privileges or manage your business listings and promotions.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = onOpenLogin,
                                    colors = ButtonDefaults.buttonColors(containerColor = BuListioCoral),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("profile_guest_signin_btn")
                                ) {
                                    Text("Sign In or Register")
                                }
                            }
                        }
                    }



                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }

    // Business Owner Post Promotion Dialog: User can select their listed business only!
    if (showPostPromotionDialog && currentUser?.role == "Business Owner") {
        if (ownedBusinesses.isNotEmpty()) {
            PostPromotionDialog(
                businesses = ownedBusinesses, // Only owner's listed business!
                initialBusinessId = ownedBusinesses.first().id,
                onDismiss = { showPostPromotionDialog = false },
                onSubmit = { businessId, title, discount, promoCode, terms, validUntil ->
                    onPostPromotion(businessId, title, discount, promoCode, terms, validUntil)
                    Toast.makeText(context, "Promotion posted successfully!", Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            Toast.makeText(context, "Please list or claim your business spot before posting a promotion.", Toast.LENGTH_LONG).show()
            showPostPromotionDialog = false
        }
    }
}

// -----------------------------------------------------------------------------------------
// Helper Sub-Composables
// -----------------------------------------------------------------------------------------

@Composable
private fun ResidentHeroCard(currentUser: UserEntity) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("profile_hero_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(76.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = currentUser.fullName.take(1).uppercase(),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = currentUser.fullName,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
            )
            Text(
                text = currentUser.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Filled.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Local Resident & Explorer",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ResidentStatsRow(
    savedCount: Int,
    userReviewsCount: Int,
    onOpenSaved: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
            modifier = Modifier
                .weight(1f)
                .clickable { onOpenSaved() }
                .testTag("profile_saved_stat_card")
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Filled.Bookmark, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "$savedCount", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                Text(text = "Saved Places", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
            modifier = Modifier.weight(1f)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Filled.RateReview, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "$userReviewsCount", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                Text(text = "My Reviews", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun ResidentAccountPrivilegesCard() {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("resident_info_card")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(Icons.Filled.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp).padding(top = 2.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Resident Privileges",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "As a Local Resident, discover neighborhood spots, save favorites, write community reviews, and browse exclusive discounts on the Promotions tab. Merchant management and listing claiming are reserved for verified business owners.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AccountSignOutSection(
    onSignOut: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("account_sign_out_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Account Actions",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = onSignOut,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_sign_out_btn")
            ) {
                Icon(Icons.Filled.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign Out of Account")
            }
        }
    }
}
