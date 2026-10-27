package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import com.example.util.ProvideSafeTextToolbar
import com.example.data.model.BusinessEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.NeighborhoodEntity
import com.example.data.model.ReviewEntity
import com.example.data.remote.supabase.SupabaseSchema
import com.example.data.remote.supabase.SyncResult
import com.example.data.remote.supabase.SyncUiState
import com.example.ui.components.AdminAdSenseSection
import com.example.ui.components.AdminTaxonomySection
import com.example.ui.components.BusinessProfileImage
import com.example.ui.components.RatingDisplay
import com.example.ui.theme.StarGold
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TownSpotOrange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AdminScreen(
    businesses: List<BusinessEntity>,
    reviews: List<ReviewEntity>,
    categories: List<CategoryEntity> = emptyList(),
    neighborhoods: List<NeighborhoodEntity> = emptyList(),
    onToggleVerified: (id: Long, current: Boolean) -> Unit,
    onToggleFeatured: (id: Long, current: Boolean) -> Unit,
    onToggleOpenStatus: (id: Long, current: Boolean) -> Unit,
    onDeleteBusiness: (id: Long) -> Unit,
    onUpdateBusiness: (BusinessEntity) -> Unit,
    onDeleteReview: (reviewId: Long) -> Unit,
    onAddCategory: (name: String, iconName: String, description: String) -> Unit = { _, _, _ -> },
    onDeleteCategory: (Long) -> Unit = {},
    onAddNeighborhood: (name: String, description: String) -> Unit = { _, _ -> },
    onDeleteNeighborhood: (Long) -> Unit = {},
    onResetTaxonomy: () -> Unit = {},
    onResetDatabase: () -> Unit,
    onAddSampleBusiness: () -> Unit,
    onSelectBusiness: (Long) -> Unit,
    onBack: () -> Unit,
    syncState: SyncUiState = SyncUiState(),
    supabaseUrl: String = "",
    supabaseMaskedKey: String = "",
    isSupabaseConfigured: Boolean = false,
    isAutoPushEnabled: Boolean = true,
    onToggleAutoPush: (Boolean) -> Unit = {},
    onSyncWithSupabase: ((SyncResult) -> Unit) -> Unit = {},
    onPushAllDataToSupabase: ((SyncResult) -> Unit) -> Unit = {},
    onTestConnection: ((Boolean, String) -> Unit) -> Unit = {},
    onUpdateSupabaseConfig: (String, String) -> Unit = { _, _ -> },
    onResetSupabaseConfig: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var filterType by remember { mutableStateOf("ALL") } // ALL, PENDING_VERIFICATION, FEATURED, CLOSED

    // Modals
    var businessToEdit by remember { mutableStateOf<BusinessEntity?>(null) }
    var businessToDelete by remember { mutableStateOf<BusinessEntity?>(null) }
    var reviewToDelete by remember { mutableStateOf<ReviewEntity?>(null) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showConfigureSupabaseDialog by remember { mutableStateOf(false) }
    var showSqlSchemaDialog by remember { mutableStateOf(false) }
    var testResultBanner by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    val verifiedCount = businesses.count { it.isVerified }
    val unverifiedCount = businesses.count { !it.isVerified }
    val featuredCount = businesses.count { it.isFeatured }
    val openCount = businesses.count { it.isOpenNow }

    val filteredBusinesses = businesses.filter { biz ->
        val matchesQuery = searchQuery.isBlank() ||
                biz.name.contains(searchQuery, ignoreCase = true) ||
                biz.category.contains(searchQuery, ignoreCase = true) ||
                biz.neighborhood.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (filterType) {
            "PENDING_VERIFICATION" -> !biz.isVerified
            "FEATURED" -> biz.isFeatured
            "CLOSED" -> !biz.isOpenNow
            else -> true
        }

        matchesQuery && matchesFilter
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.AdminPanelSettings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Admin Portal",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Directory Operations & Moderation",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("admin_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Explore"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            onSyncWithSupabase { result ->
                                Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier.testTag("admin_topbar_sync_btn")
                    ) {
                        if (syncState.isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                imageVector = if (isSupabaseConfigured) Icons.Filled.CloudDone else Icons.Filled.CloudSync,
                                contentDescription = "Sync with Supabase",
                                tint = if (isSupabaseConfigured) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                    IconButton(
                        onClick = {
                            onAddSampleBusiness()
                            Toast.makeText(context, "Added sample test merchant!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("admin_add_sample_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Test Spot",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = { showResetDialog = true },
                        modifier = Modifier.testTag("admin_reset_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Restore Default Data",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // High-Level KPI Summary Metrics
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    KpiStat(label = "Listings", value = "${businesses.size}", color = MaterialTheme.colorScheme.primary)
                    KpiStat(label = "Categories", value = "${categories.size}", color = TownSpotOrange)
                    KpiStat(label = "Districts", value = "${neighborhoods.size}", color = MaterialTheme.colorScheme.tertiary)
                    KpiStat(label = "Reviews", value = "${reviews.size}", color = MaterialTheme.colorScheme.secondary)
                    KpiStat(label = "Cloud", value = if (isSupabaseConfigured) "Active" else "Local", color = if (isSupabaseConfigured) SuccessGreen else MaterialTheme.colorScheme.outline)
                }
            }

            // Navigation Tabs (Scrollable across mobile and expanded form factors)
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("Listings (${businesses.size})") },
                    icon = { Icon(Icons.Filled.Storefront, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("admin_tab_listings")
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("Categories & Districts (${categories.size + neighborhoods.size})") },
                    icon = { Icon(Icons.Filled.Category, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("admin_tab_taxonomy")
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = { Text("Reviews (${reviews.size})") },
                    icon = { Icon(Icons.Filled.RateReview, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("admin_tab_reviews")
                )
                Tab(
                    selected = selectedTabIndex == 3,
                    onClick = { selectedTabIndex = 3 },
                    text = { Text("Supabase Cloud") },
                    icon = { Icon(Icons.Filled.CloudSync, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("admin_tab_supabase")
                )
                Tab(
                    selected = selectedTabIndex == 4,
                    onClick = { selectedTabIndex = 4 },
                    text = { Text("System & Stats") },
                    icon = { Icon(Icons.Filled.Security, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("admin_tab_system")
                )
                Tab(
                    selected = selectedTabIndex == 5,
                    onClick = { selectedTabIndex = 5 },
                    text = { Text("Google AdSense") },
                    icon = { Icon(Icons.Filled.Campaign, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("admin_tab_adsense")
                )
            }

            // Tab Content
            when (selectedTabIndex) {
                0 -> {
                    // Business Listings Management Tab
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Search bar within Admin
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Filter by merchant, category, district...") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.Search,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotBlank()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(imageVector = Icons.Filled.Clear, contentDescription = "Clear")
                                        }
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_search_input")
                            )
                        }

                        // Filter Chips Row
                        LazyRow(
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                FilterChip(
                                    selected = filterType == "ALL",
                                    onClick = { filterType = "ALL" },
                                    label = { Text("All (${businesses.size})") }
                                )
                            }
                            item {
                                FilterChip(
                                    selected = filterType == "PENDING_VERIFICATION",
                                    onClick = { filterType = "PENDING_VERIFICATION" },
                                    label = { Text("Pending / Unverified ($unverifiedCount)") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Filled.NotificationsActive,
                                            contentDescription = null,
                                            tint = if (unverifiedCount > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                )
                            }
                            item {
                                FilterChip(
                                    selected = filterType == "FEATURED",
                                    onClick = { filterType = "FEATURED" },
                                    label = { Text("Featured ($featuredCount)") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Filled.Star,
                                            contentDescription = null,
                                            tint = StarGold,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                )
                            }
                            item {
                                FilterChip(
                                    selected = filterType == "CLOSED",
                                    onClick = { filterType = "CLOSED" },
                                    label = { Text("Currently Closed (${businesses.size - openCount})") }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        if (filteredBusinesses.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No business listings match the selected criteria.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(filteredBusinesses, key = { it.id }) { biz ->
                                    AdminBusinessItem(
                                        business = biz,
                                        onViewDetail = { onSelectBusiness(biz.id) },
                                        onToggleVerified = { onToggleVerified(biz.id, biz.isVerified) },
                                        onToggleFeatured = { onToggleFeatured(biz.id, biz.isFeatured) },
                                        onToggleOpenStatus = { onToggleOpenStatus(biz.id, biz.isOpenNow) },
                                        onEdit = { businessToEdit = biz },
                                        onDelete = { businessToDelete = biz }
                                    )
                                }
                                item {
                                    Spacer(modifier = Modifier.height(32.dp))
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Category & Neighborhood Management Tab
                    AdminTaxonomySection(
                        categories = categories,
                        neighborhoods = neighborhoods,
                        businesses = businesses,
                        onAddCategory = onAddCategory,
                        onDeleteCategory = onDeleteCategory,
                        onAddNeighborhood = onAddNeighborhood,
                        onDeleteNeighborhood = onDeleteNeighborhood,
                        onResetTaxonomy = onResetTaxonomy
                    )
                }

                2 -> {
                    // Reviews Moderation Tab
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Community Feedback Moderation",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Monitor and manage feedback across all directory listings to preserve trust and authentic community reviews.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        if (reviews.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No reviews submitted yet.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(reviews, key = { it.id }) { review ->
                                    val targetBiz = businesses.find { it.id == review.businessId }
                                    AdminReviewItem(
                                        review = review,
                                        businessName = targetBiz?.name ?: "Unknown Merchant (#${review.businessId})",
                                        onDelete = { reviewToDelete = review }
                                    )
                                }
                                item {
                                    Spacer(modifier = Modifier.height(32.dp))
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // Supabase Cloud & PostgreSQL Database Tab
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text(
                                text = "Supabase Cloud Database",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Real-time PostgreSQL database sync for spots, deals, reviews, and community users.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Connection Status Card
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Connection Status",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                        )

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = when {
                                                syncState.isSyncing -> MaterialTheme.colorScheme.primaryContainer
                                                isSupabaseConfigured -> SuccessGreen.copy(alpha = 0.15f)
                                                else -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                                            }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = when {
                                                        syncState.isSyncing -> Icons.Filled.Sync
                                                        isSupabaseConfigured -> Icons.Filled.CheckCircle
                                                        else -> Icons.Filled.CloudOff
                                                    },
                                                    contentDescription = null,
                                                    tint = when {
                                                        syncState.isSyncing -> MaterialTheme.colorScheme.primary
                                                        isSupabaseConfigured -> SuccessGreen
                                                        else -> MaterialTheme.colorScheme.error
                                                    },
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = when {
                                                        syncState.isSyncing -> "Syncing..."
                                                        isSupabaseConfigured -> "Connected"
                                                        else -> "Local Mode"
                                                    },
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = when {
                                                        syncState.isSyncing -> MaterialTheme.colorScheme.primary
                                                        isSupabaseConfigured -> SuccessGreen
                                                        else -> MaterialTheme.colorScheme.error
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                            .padding(10.dp)
                                    ) {
                                        Text(
                                            text = "Supabase URL:",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = supabaseUrl.ifBlank { "Not configured" },
                                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Anon API Key:",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = supabaseMaskedKey.ifBlank { "Not configured" },
                                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
                                        )
                                    }

                                    testResultBanner?.let { (isSuccess, message) ->
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSuccess) SuccessGreen.copy(alpha = 0.12f) else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = if (isSuccess) Icons.Filled.CheckCircle else Icons.Filled.CloudOff,
                                                    contentDescription = null,
                                                    tint = if (isSuccess) SuccessGreen else MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = message,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = if (isSuccess) SuccessGreen else MaterialTheme.colorScheme.error
                                                )
                                            }
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                onTestConnection { success, msg ->
                                                    testResultBanner = Pair(success, msg)
                                                    Toast.makeText(context, if (success) "Supabase connection verified!" else "Connection failed: $msg", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            modifier = Modifier.weight(1f).testTag("admin_test_supabase_btn"),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(Icons.Filled.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Test Ping")
                                        }

                                        Button(
                                            onClick = { showConfigureSupabaseDialog = true },
                                            modifier = Modifier.weight(1f).testTag("admin_config_supabase_btn"),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(Icons.Filled.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Configure")
                                        }
                                    }
                                }
                            }
                        }

                        // Automatic Cloud Push Card
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Filled.CloudUpload,
                                                    contentDescription = null,
                                                    tint = if (isAutoPushEnabled) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "Automatic Push to Supabase",
                                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = if (isAutoPushEnabled) "Active: Changes auto-push in real-time" else "Paused: Local changes not pushed automatically",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (isAutoPushEnabled) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        Switch(
                                            checked = isAutoPushEnabled,
                                            onCheckedChange = { onToggleAutoPush(it) },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = Color.White,
                                                checkedTrackColor = SuccessGreen
                                            ),
                                            modifier = Modifier.testTag("admin_auto_push_switch")
                                        )
                                    }

                                    Text(
                                        text = "When active, all local listings, edits, reviews, deals, status changes, and registered users are automatically pushed to Supabase immediately upon creation and during startup.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Button(
                                        onClick = {
                                            onPushAllDataToSupabase { result ->
                                                Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                                            }
                                        },
                                        enabled = !syncState.isSyncing,
                                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                        modifier = Modifier.fillMaxWidth().testTag("admin_push_all_btn"),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        if (syncState.isSyncing) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(18.dp),
                                                strokeWidth = 2.dp,
                                                color = Color.White
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Pushing all data to Supabase...")
                                        } else {
                                            Icon(Icons.Filled.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Push All Local Data to Supabase Now")
                                        }
                                    }
                                }
                            }
                        }

                        // Two-Way Sync Actions Card
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = "Cloud Data Synchronization",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Pushes local Room database updates to Supabase and fetches remote listings, reviews, and deals into local storage.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    syncState.lastSyncMessage?.let { msg ->
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = msg,
                                                style = MaterialTheme.typography.bodySmall,
                                                modifier = Modifier.padding(8.dp)
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            onSyncWithSupabase { result ->
                                                Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                                            }
                                        },
                                        enabled = !syncState.isSyncing,
                                        modifier = Modifier.fillMaxWidth().testTag("admin_sync_now_btn"),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        if (syncState.isSyncing) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(18.dp),
                                                strokeWidth = 2.dp,
                                                color = MaterialTheme.colorScheme.onPrimary
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Synchronizing with Supabase...")
                                        } else {
                                            Icon(Icons.Filled.CloudSync, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Sync All Data Now")
                                        }
                                    }
                                }
                            }
                        }

                        // Database Schema & SQL Setup Card
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.Storage, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "PostgreSQL Tables & Schema",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }

                                    Text(
                                        text = "Configured tables: public.businesses, public.reviews, public.promotions, and public.users with Row Level Security policies.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { showSqlSchemaDialog = true },
                                            modifier = Modifier.weight(1f).testTag("admin_view_sql_btn"),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("View SQL Script")
                                        }

                                        Button(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(SupabaseSchema.SQL_SETUP_SCRIPT))
                                                Toast.makeText(context, "Copied Supabase SQL Setup to clipboard!", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.weight(1f).testTag("admin_copy_sql_btn"),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Copy SQL")
                                        }
                                    }
                                }
                            }
                        }

                        item { Spacer(modifier = Modifier.height(32.dp)) }
                    }
                }

                4 -> {
                    // Insights & System Operations Tab
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text(
                                text = "Directory Health & Diagnostics",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Platform overview, merchant distribution, and database utilities.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Category Breakdown Card
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Listings by Category",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    val categoryGroups = businesses.groupBy { it.category }
                                    categoryGroups.forEach { (cat, list) ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = cat, style = MaterialTheme.typography.bodyMedium)
                                            Text(
                                                text = "${list.size} spots",
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                                    }
                                }
                            }
                        }

                        // Neighborhood Density Card
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Listings by Neighborhood District",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    val districtGroups = businesses.groupBy { it.neighborhood }
                                    districtGroups.forEach { (hood, list) ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = hood, style = MaterialTheme.typography.bodyMedium)
                                            Text(
                                                text = "${list.size} active",
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                        }
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                                    }
                                }
                            }
                        }

                        // Administrative Tools Card
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = "Maintenance Actions",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )

                                    OutlinedButton(
                                        onClick = {
                                            onAddSampleBusiness()
                                            Toast.makeText(context, "Added sample test merchant!", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Generate Test Merchant Spot")
                                    }

                                    Button(
                                        onClick = { showResetDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Filled.LockReset, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Restore Default Community Seed")
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }

                5 -> {
                    // Google AdSense Monetization Tab
                    AdminAdSenseSection()
                }
            }
        }
    }

    // Edit Business Dialog
    businessToEdit?.let { biz ->
        EditBusinessDialog(
            business = biz,
            onDismiss = { businessToEdit = null },
            onSave = { updated ->
                onUpdateBusiness(updated)
                businessToEdit = null
                Toast.makeText(context, "Updated ${updated.name}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Confirm Delete Business Dialog
    businessToDelete?.let { biz ->
        AlertDialog(
            onDismissRequest = { businessToDelete = null },
            icon = { Icon(Icons.Filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Delete Business Listing?") },
            text = { Text("Are you sure you want to permanently delete \"${biz.name}\" from the directory? All associated customer reviews will also be removed.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteBusiness(biz.id)
                        businessToDelete = null
                        Toast.makeText(context, "Deleted ${biz.name}", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("admin_confirm_delete_btn")
                ) {
                    Text("Delete Listing")
                }
            },
            dismissButton = {
                TextButton(onClick = { businessToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Confirm Delete Review Dialog
    reviewToDelete?.let { review ->
        AlertDialog(
            onDismissRequest = { reviewToDelete = null },
            icon = { Icon(Icons.Filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Moderate / Remove Review?") },
            text = { Text("Delete review by \"${review.authorName}\" (${review.rating}★)? \"${review.comment}\"") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteReview(review.id)
                        reviewToDelete = null
                        Toast.makeText(context, "Review removed", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("admin_confirm_delete_review_btn")
                ) {
                    Text("Remove Review")
                }
            },
            dismissButton = {
                TextButton(onClick = { reviewToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Confirm Restore Seed Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            icon = { Icon(Icons.Filled.Refresh, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Reset to Initial Seed?") },
            text = { Text("This will restore the 12 default curated neighborhood businesses and community reviews. Any newly created listings will be refreshed.") },
            confirmButton = {
                Button(
                    onClick = {
                        onResetDatabase()
                        showResetDialog = false
                        Toast.makeText(context, "Database restored to original community seed", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("admin_confirm_reset_btn")
                ) {
                    Text("Restore Default Data")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showConfigureSupabaseDialog) {
        ConfigureSupabaseDialog(
            currentUrl = supabaseUrl,
            currentKey = supabaseMaskedKey,
            onDismiss = { showConfigureSupabaseDialog = false },
            onSave = { newUrl, newKey ->
                onUpdateSupabaseConfig(newUrl, newKey)
                showConfigureSupabaseDialog = false
                testResultBanner = null
                Toast.makeText(context, "Supabase credentials updated!", Toast.LENGTH_SHORT).show()
            },
            onReset = {
                onResetSupabaseConfig()
                showConfigureSupabaseDialog = false
                testResultBanner = null
                Toast.makeText(context, "Supabase credentials reset to defaults.", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showSqlSchemaDialog) {
        SupabaseSqlDialog(
            sqlScript = SupabaseSchema.SQL_SETUP_SCRIPT,
            onDismiss = { showSqlSchemaDialog = false },
            onCopy = {
                clipboardManager.setText(AnnotatedString(SupabaseSchema.SQL_SETUP_SCRIPT))
                Toast.makeText(context, "PostgreSQL schema copied to clipboard!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminBusinessItem(
    business: BusinessEntity,
    onViewDetail: () -> Unit,
    onToggleVerified: () -> Unit,
    onToggleFeatured: () -> Unit,
    onToggleOpenStatus: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("admin_biz_item_${business.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Profile Image Thumbnail
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(onClick = onViewDetail)
                ) {
                    BusinessProfileImage(
                        imageResName = business.imageResName,
                        imageUri = business.imageUri,
                        contentDescription = business.name,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onViewDetail)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = business.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (business.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Verified",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Text(
                        text = "${business.category} • ${business.neighborhood}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    RatingDisplay(rating = business.rating, reviewCount = business.reviewCount, starSize = 13.dp)
                }

                // Edit & Delete actions
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp).testTag("edit_biz_${business.id}")) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit Listing",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp).testTag("delete_biz_${business.id}")) {
                    Icon(
                        imageVector = Icons.Filled.DeleteOutline,
                        contentDescription = "Delete Listing",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(8.dp))

            // Prominent Action Banner for Unverified Listings
            if (!business.isVerified) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFFF7ED),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDBA74)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = Icons.Filled.HourglassTop,
                                contentDescription = null,
                                tint = Color(0xFFC2410C),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Pending Admin Verification",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF9A3412))
                                )
                                Text(
                                    text = "Listed spot will appear to all residents once verified.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFC2410C)
                                )
                            }
                        }
                        Button(
                            onClick = onToggleVerified,
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("admin_verify_approve_btn_${business.id}")
                        ) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Verify & Publish", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Admin Control Toggles
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Verified Badge Toggle
                FilterChip(
                    selected = business.isVerified,
                    onClick = onToggleVerified,
                    label = { Text(if (business.isVerified) "Verified" else "Unverified", fontSize = 11.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = if (business.isVerified) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = if (business.isVerified) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    modifier = Modifier.testTag("toggle_verified_${business.id}")
                )

                // Featured Spot Toggle
                FilterChip(
                    selected = business.isFeatured,
                    onClick = onToggleFeatured,
                    label = { Text(if (business.isFeatured) "Featured Spot" else "Not Featured", fontSize = 11.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = if (business.isFeatured) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = null,
                            tint = if (business.isFeatured) StarGold else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    modifier = Modifier.testTag("toggle_featured_${business.id}")
                )

                // Live Status Toggle
                FilterChip(
                    selected = business.isOpenNow,
                    onClick = onToggleOpenStatus,
                    label = { Text(if (business.isOpenNow) "Open Now" else "Closed", fontSize = 11.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = null,
                            tint = if (business.isOpenNow) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    modifier = Modifier.testTag("toggle_open_${business.id}")
                )
            }
        }
    }
}

@Composable
fun AdminReviewItem(
    review: ReviewEntity,
    businessName: String,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("admin_review_item_${review.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = businessName,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "By ${review.authorName} • ${review.dateString}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                RatingDisplay(rating = review.rating.toFloat(), showNumeric = false, starSize = 13.dp)

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("delete_review_btn_${review.id}")
                ) {
                    Icon(
                        imageVector = Icons.Filled.DeleteOutline,
                        contentDescription = "Remove Review",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun EditBusinessDialog(
    business: BusinessEntity,
    onDismiss: () -> Unit,
    onSave: (BusinessEntity) -> Unit
) {
    var name by remember { mutableStateOf(business.name) }
    var category by remember { mutableStateOf(business.category) }
    var neighborhood by remember { mutableStateOf(business.neighborhood) }
    var tagline by remember { mutableStateOf(business.tagline) }
    var description by remember { mutableStateOf(business.description) }
    var address by remember { mutableStateOf(business.address) }
    var phone by remember { mutableStateOf(business.phone) }
    var website by remember { mutableStateOf(business.website) }
    var email by remember { mutableStateOf(business.email) }
    var hours by remember { mutableStateOf(business.hours) }
    var specialOffer by remember { mutableStateOf(business.specialOffer ?: "") }
    var tags by remember { mutableStateOf(business.tags) }
    var priceLevel by remember { mutableStateOf(business.priceLevel) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val dismissSafely = {
        focusManager.clearFocus(force = true)
        keyboardController?.hide()
        onDismiss()
    }

    ProvideSafeTextToolbar {
        AlertDialog(
            onDismissRequest = dismissSafely,
        title = {
            Text(
                text = "Edit Merchant Listing",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Business Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_name_input")
                    )
                }
                item {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_category_input")
                    )
                }
                item {
                    OutlinedTextField(
                        value = neighborhood,
                        onValueChange = { neighborhood = it },
                        label = { Text("Neighborhood District") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_neighborhood_input")
                    )
                }
                item {
                    OutlinedTextField(
                        value = tagline,
                        onValueChange = { tagline = it },
                        label = { Text("Tagline") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = hours,
                        onValueChange = { hours = it },
                        label = { Text("Hours") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = specialOffer,
                        onValueChange = { specialOffer = it },
                        label = { Text("Special Community Offer") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = tags,
                        onValueChange = { tags = it },
                        label = { Text("Amenity Tags (comma separated)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        business.copy(
                            name = name.trim(),
                            category = category.trim(),
                            neighborhood = neighborhood.trim(),
                            tagline = tagline.trim(),
                            description = description.trim(),
                            address = address.trim(),
                            phone = phone.trim(),
                            website = website.trim(),
                            email = email.trim(),
                            hours = hours.trim(),
                            priceLevel = priceLevel,
                            specialOffer = if (specialOffer.isBlank()) null else specialOffer.trim(),
                            tags = tags.trim()
                        ))
                        focusManager.clearFocus(force = true)
                        keyboardController?.hide()
                    },
                modifier = Modifier.testTag("save_edit_biz_btn")
            ) {
                Text("Save Changes")
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

@Composable
fun KpiStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ConfigureSupabaseDialog(
    currentUrl: String,
    currentKey: String,
    onDismiss: () -> Unit,
    onSave: (url: String, anonKey: String) -> Unit,
    onReset: () -> Unit
) {
    var urlText by remember { mutableStateOf(currentUrl) }
    var keyText by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val dismissSafely = {
        focusManager.clearFocus(force = true)
        keyboardController?.hide()
        onDismiss()
    }

    ProvideSafeTextToolbar {
        AlertDialog(
            onDismissRequest = dismissSafely,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Supabase Configuration")
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Connect TownSpot to your Supabase project (Project Settings -> API).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = urlText,
                        onValueChange = {
                            urlText = it
                            errorMsg = null
                        },
                        label = { Text("Supabase Project URL") },
                        placeholder = { Text("https://your-project.supabase.co") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("supabase_url_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = keyText,
                        onValueChange = {
                            keyText = it
                            errorMsg = null
                        },
                        label = { Text("Supabase Anon Key") },
                        placeholder = { Text("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...") },
                        singleLine = false,
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth().testTag("supabase_key_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    errorMsg?.let { err ->
                        Text(
                            text = err,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    TextButton(
                        onClick = onReset,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Reset to .env Defaults", style = MaterialTheme.typography.labelSmall)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cleanUrl = urlText.trim()
                        val cleanKey = keyText.trim()
                        if (!cleanUrl.startsWith("https://")) {
                            errorMsg = "URL must start with https://"
                            return@Button
                        }
                        if (cleanKey.isBlank() && currentKey.isBlank()) {
                            errorMsg = "Please enter your Supabase anon public key"
                            return@Button
                        }
                        focusManager.clearFocus(force = true)
                        keyboardController?.hide()
                        onSave(cleanUrl, if (cleanKey.isNotBlank()) cleanKey else currentKey)
                    },
                    modifier = Modifier.testTag("supabase_save_btn")
                ) {
                    Text("Save Credentials")
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

@Composable
fun SupabaseSqlDialog(
    sqlScript: String,
    onDismiss: () -> Unit,
    onCopy: () -> Unit
) {
    ProvideSafeTextToolbar {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Storage, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Supabase PostgreSQL Setup")
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Run this SQL script in your Supabase Project -> SQL Editor to initialize the database tables and Row-Level Security policies.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier.padding(10.dp)
                        ) {
                            item {
                                Text(
                                    text = sqlScript,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = onCopy,
                    modifier = Modifier.testTag("dialog_copy_sql_btn")
                ) {
                    Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy SQL Script")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Close")
                }
            }
        )
    }
}
