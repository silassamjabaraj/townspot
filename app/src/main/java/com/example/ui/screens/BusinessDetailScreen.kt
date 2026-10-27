package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import com.example.util.ProvideSafeTextToolbar
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusinessEntity
import com.example.data.model.PromotionEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.UserEntity
import com.example.ui.components.BusinessAdSenseSection
import com.example.ui.components.BusinessProfileImage
import com.example.ui.components.InteractiveRatingBar
import com.example.ui.components.PostPromotionDialog
import com.example.ui.components.PromotionCard
import com.example.ui.components.RatingDisplay
import com.example.ui.components.ReviewItem
import com.example.ui.theme.BuListioCoral
import com.example.ui.theme.StarGold
import com.example.ui.theme.SuccessGreen
import com.example.util.IntentUtils

/**
 * Screen 3: Listing Detail Screen matching IMG_0109.png:
 * Top Media Header with floating back button & preview carousel
 * Category + Star Rating (e.g. ⭐ 4.8 (100 review))
 * Title: "Elite Edge Gymnasium Place"
 * Location: "456 Wellness Blvd, Monroe, NC, USA"
 * Price & Action row ("From: $80–$250.00", Share, Save)
 * Tabs: About, Features, Products, Reviews, FAQ with active coral underline
 * Listing Summary & Description
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BusinessDetailScreen(
    business: BusinessEntity?,
    reviews: List<ReviewEntity>,
    currentUser: UserEntity? = null,
    promotions: List<PromotionEntity> = emptyList(),
    onBack: () -> Unit,
    onToggleSaved: (Long, Boolean) -> Unit,
    onAddReview: (businessId: Long, author: String, rating: Int, comment: String) -> Unit,
    onHelpfulClicked: (Long) -> Unit,
    onClaimBusiness: ((Long) -> Unit)? = null,
    onPostPromotion: ((Long, String, String, String?, String, String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    var showReviewDialog by remember { mutableStateOf(false) }
    var showPostPromoDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedGalleryIndex by remember { mutableIntStateOf(0) }

    if (business == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Listing not found", style = MaterialTheme.typography.titleMedium)
        }
        return
    }

    val detailTabs = listOf("About", "Features", "Products", "Reviews", "FAQ")

    // Gallery image resources for the carousel strip
    val galleryImages = listOf(
        business.imageResName,
        "biz_gymnasium",
        "biz_wellness",
        "biz_cafe"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // 1. Hero Media Carousel matching Screen 3 in IMG_0109.png
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                    ) {
                        BusinessProfileImage(
                            imageResName = galleryImages.getOrNull(selectedGalleryIndex) ?: business.imageResName,
                            imageUri = business.imageUri,
                            contentDescription = business.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Floating Circular Back Button on top-left
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.95f),
                            shadowElevation = 4.dp,
                            modifier = Modifier
                                .statusBarsPadding()
                                .padding(start = 16.dp, top = 8.dp)
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable { onBack() }
                                .testTag("detail_back_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color(0xFF0F172A),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Floating Circular Share/Bookmark Action on top-right
                        Row(
                            modifier = Modifier
                                .statusBarsPadding()
                                .align(Alignment.TopEnd)
                                .padding(end = 16.dp, top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.95f),
                                shadowElevation = 4.dp,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        IntentUtils.shareBusiness(
                                            context,
                                            business
                                        )
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Outlined.Share,
                                        contentDescription = "Share",
                                        tint = Color(0xFF0F172A),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.95f),
                                shadowElevation = 4.dp,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .clickable { onToggleSaved(business.id, !business.isSaved) }
                                    .testTag("detail_save_toggle")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (business.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                        contentDescription = "Save",
                                        tint = if (business.isSaved) BuListioCoral else Color(0xFF0F172A),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Gallery Thumbnail Strip Underneath Hero Image
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        galleryImages.forEachIndexed { index, imgRes ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(
                                        width = if (selectedGalleryIndex == index) 2.dp else 1.dp,
                                        color = if (selectedGalleryIndex == index) BuListioCoral else Color(0xFFE2E8F0),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedGalleryIndex = index }
                            ) {
                                BusinessProfileImage(
                                    imageResName = imgRes,
                                    imageUri = null,
                                    contentDescription = "Thumbnail $index",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }

                    // Carousel Indicator Dots
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(4) { idx ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 3.dp)
                                    .size(if (selectedGalleryIndex == idx) 8.dp else 6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (selectedGalleryIndex == idx) BuListioCoral
                                        else Color(0xFFCBD5E1)
                                    )
                            )
                        }
                    }
                }
            }

            // 2. Listing Title & Metadata matching Screen 3 in IMG_0109.png
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 8.dp)
                ) {
                    // Category & Rating Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = business.category,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = StarGold,
                                modifier = Modifier.size(17.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${String.format("%.1f", business.rating)} (${business.reviewCount} review)",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Title
                    Text(
                        text = "${business.name} Place",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Address with Location Pin Icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            IntentUtils.openDirections(context, business.name, business.address)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = business.address,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Action row: Open Status | Share | Save
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (business.isOpenNow) Color(0xFFDCFCE7) else Color(0xFFF1F5F9)
                        ) {
                            Text(
                                text = if (business.isOpenNow) "Open Now" else "Closed",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (business.isOpenNow) Color(0xFF166534) else Color(0xFF64748B)
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    IntentUtils.shareBusiness(
                                        context,
                                        business
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Share,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Share",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable { onToggleSaved(business.id, !business.isSaved) }
                                    .testTag("detail_save_label_btn")
                            ) {
                                Icon(
                                    imageVector = if (business.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = null,
                                    tint = if (business.isSaved) BuListioCoral else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (business.isSaved) "Saved" else "Save",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (business.isSaved) BuListioCoral else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                    }

                    // Claim & Ownership Status Banner
                    if (business.isClaimed) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Claimed & Managed by ${business.claimedByOwnerName ?: "Verified Business Owner"}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    } else if (currentUser?.role == "Business Owner") {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = BuListioCoral.copy(alpha = 0.1f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BuListioCoral.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Store, contentDescription = null, tint = BuListioCoral, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Unclaimed Business Spot",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = BuListioCoral)
                                    )
                                }
                                Button(
                                    onClick = { onClaimBusiness?.invoke(business.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = BuListioCoral),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp).testTag("claim_business_detail_btn")
                                ) {
                                    Text("Claim Spot", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // 3. Tab Bar with Coral Underline Indicator matching Screen 3 in IMG_0109.png
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 6.dp)) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 18.dp),
                        horizontalArrangement = Arrangement.spacedBy(22.dp)
                    ) {
                        items(detailTabs.size) { idx ->
                            val tabName = detailTabs[idx]
                            val isSelected = selectedTab == idx
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { selectedTab = idx }
                                    .testTag("detail_tab_$tabName")
                            ) {
                                Text(
                                    text = tabName,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) BuListioCoral else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 14.sp
                                    ),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )

                                // Coral indicator line for active tab
                                Box(
                                    modifier = Modifier
                                        .height(3.dp)
                                        .width(36.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(if (isSelected) BuListioCoral else Color.Transparent)
                                )
                            }
                        }
                    }

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                        thickness = 1.dp
                    )
                }
            }

            // 4. Tab Content (About, Features, Products, Reviews, FAQ)
            when (selectedTab) {
                0 -> { // About Tab
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp, vertical = 14.dp)
                        ) {
                            // Listing Summary
                            Text(
                                text = "Listing Summary",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = business.tagline.ifBlank {
                                    "The Advanced Diagnostic Screening is a comprehensive service designed to provide a detailed analysis of your health."
                                },
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 22.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Description
                            Text(
                                text = "Description",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = business.description,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 22.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Operating Hours
                            Text(
                                text = "Hours & Availability",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = business.hours,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Quick Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { IntentUtils.dialBusiness(context, business.phone) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = BuListioCoral),
                                    modifier = Modifier.weight(1f).testTag("detail_call_button")
                                ) {
                                    Icon(Icons.Filled.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Call Now")
                                }

                                OutlinedButton(
                                    onClick = { IntentUtils.openDirections(context, business.name, business.address) },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f).testTag("detail_directions_button")
                                ) {
                                    Icon(Icons.Filled.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Directions")
                                }
                            }
                        }
                    }
                }

                1 -> { // Features Tab
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp, vertical = 14.dp)
                        ) {
                            Text(
                                text = "Amenities & Features",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                business.tags.split(",").map { it.trim() }.filter { it.isNotBlank() }.forEach { tag ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.CheckCircle,
                                                contentDescription = null,
                                                tint = SuccessGreen,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = tag,
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> { // Products / Offers Tab
                    item {
                        val bizPromos = promotions.filter { it.businessId == business.id }
                        val isOwner = currentUser?.role == "Business Owner" &&
                                (business.claimedByOwnerEmail.equals(currentUser.email, ignoreCase = true) ||
                                        (currentUser.businessName != null && business.name.equals(currentUser.businessName, ignoreCase = true)))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp, vertical = 14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Offers & Community Promotions",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                if (isOwner && onPostPromotion != null) {
                                    Button(
                                        onClick = { showPostPromoDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = BuListioCoral),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp).testTag("detail_post_promo_btn")
                                    ) {
                                        Text("+ Post Deal", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (bizPromos.isNotEmpty()) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    bizPromos.forEach { promo ->
                                        PromotionCard(
                                            promotion = promo,
                                            onViewBusiness = {}
                                        )
                                    }
                                }
                            } else if (!business.specialOffer.isNullOrBlank()) {
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = "Featured Promo",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = BuListioCoral)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = business.specialOffer,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            } else {
                                Text(
                                    text = "No active promotions at this time. Standard offerings available on premises.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                3 -> { // Reviews Tab
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp, vertical = 14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Member Reviews (${reviews.size})",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )

                                Button(
                                    onClick = { showReviewDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = BuListioCoral),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("detail_write_review_btn")
                                ) {
                                    Icon(Icons.Filled.RateReview, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Add Review")
                                }
                            }
                        }
                    }

                    if (reviews.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No reviews yet. Be the first to share your experience!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        items(reviews) { r ->
                            Box(modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp)) {
                                ReviewItem(review = r, onHelpfulClicked = { onHelpfulClicked(r.id) })
                            }
                        }
                    }
                }

                4 -> { // FAQ Tab
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp, vertical = 14.dp)
                        ) {
                            Text(
                                text = "Frequently Asked Questions",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Q: How do I book an appointment or visit?\nA: Call directly or visit their official website listed below.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Q: Is parking available?\nA: Free street parking and dedicated guest stalls are accessible on-site.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Business Section Google AdSense Sponsored Merchant Banner
            item {
                BusinessAdSenseSection(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp),
                    customTitle = "Merchant Spotlight • Google AdSense",
                    customSubtitle = "Explore partner promotions, supplies & local Andaman commercial services"
                )
            }

            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }

    // Add Review Dialog
    if (showReviewDialog) {
        var reviewAuthor by remember { mutableStateOf("") }
        var reviewRating by remember { mutableIntStateOf(5) }
        var reviewComment by remember { mutableStateOf("") }
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current
        val dismissSafely = {
            focusManager.clearFocus(force = true)
            keyboardController?.hide()
            showReviewDialog = false
        }

        ProvideSafeTextToolbar {
            AlertDialog(
                onDismissRequest = dismissSafely,
                title = {
                    Text(
                        text = "Review ${business.name}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Rating:", style = MaterialTheme.typography.labelMedium)
                        InteractiveRatingBar(
                            rating = reviewRating,
                            onRatingChanged = { reviewRating = it }
                        )

                        OutlinedTextField(
                            value = reviewAuthor,
                            onValueChange = { reviewAuthor = it },
                            label = { Text("Your Name (Optional)") },
                            placeholder = { Text("e.g. Local Resident") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("review_author_input")
                        )

                        OutlinedTextField(
                            value = reviewComment,
                            onValueChange = { reviewComment = it },
                            label = { Text("Your Experience") },
                            placeholder = { Text("What did you enjoy about this place?") },
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth().testTag("review_comment_input")
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (reviewComment.isNotBlank()) {
                                focusManager.clearFocus(force = true)
                                keyboardController?.hide()
                                onAddReview(business.id, reviewAuthor, reviewRating, reviewComment)
                                showReviewDialog = false
                                Toast.makeText(context, "Review posted! Thank you.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BuListioCoral),
                        modifier = Modifier.testTag("submit_review_button")
                    ) {
                        Text("Post Review")
                    }
                },
                dismissButton = {
                    TextButton(onClick = dismissSafely) {
                        Text("Cancel")
                    }
                }
            )
        }
    }

    if (showPostPromoDialog && onPostPromotion != null) {
        PostPromotionDialog(
            businesses = listOf(business),
            initialBusinessId = business.id,
            onDismiss = { showPostPromoDialog = false },
            onSubmit = { bId, title, discount, code, terms, validity ->
                onPostPromotion(bId, title, discount, code, terms, validity)
                showPostPromoDialog = false
                Toast.makeText(context, "Promotion posted successfully!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
