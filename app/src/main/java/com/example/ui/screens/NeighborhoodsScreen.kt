package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusinessEntity
import com.example.data.model.UserEntity
import com.example.ui.components.BusinessCard
import com.example.ui.components.ResidentAdSenseSection
import com.example.ui.theme.BuListioCoral

/**
 * Screen 2: All Listing Screen matching IMG_0109.png:
 * Top bar with back button + "All Listing" centered title
 * Search bar: "Search your listing" with tune filter icon
 * Category horizontal filter chips: "All Categories", "Gymnasium", "Salon", "Hospital", etc.
 * List of listing items with "Claim" badge and bookmark.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeighborhoodsScreen(
    allBusinesses: List<BusinessEntity> = emptyList(),
    businesses: List<BusinessEntity> = allBusinesses,
    currentUser: UserEntity? = null,
    onSelectBusiness: (Long) -> Unit = {},
    onToggleSaved: (Long, Boolean) -> Unit = { _, _ -> },
    onSelectNeighborhood: ((String) -> Unit)? = null,
    onClaimBusiness: ((Long) -> Unit)? = null,
    availableCategories: List<String> = emptyList(),
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val listingSource = if (businesses.isNotEmpty()) businesses else allBusinesses
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All Categories") }

    val categories = remember(availableCategories) {
        val list = mutableListOf("All Categories")
        if (availableCategories.isNotEmpty()) {
            list.addAll(availableCategories)
        } else {
            list.addAll(
                listOf(
                    "Gymnasium",
                    "Salon & Spa",
                    "Hospital & Clinics",
                    "Restaurants",
                    "Cafes & Bakeries",
                    "Retail & Shopping",
                    "Home Services",
                    "Automotive",
                    "Arts & Culture"
                )
            )
        }
        list.distinct()
    }

    val filteredList = listingSource.filter { b ->
        // Unverified listings only appear after admin verifies them!
        val isVisible = if (currentUser?.role == "Admin") true else b.isVerified
        if (!isVisible) return@filter false

        val matchesCategory = if (selectedCategory == "All Categories") true
        else b.category.equals(selectedCategory, ignoreCase = true)

        val matchesSearch = if (searchQuery.isBlank()) true
        else b.name.contains(searchQuery, ignoreCase = true) ||
                b.category.contains(searchQuery, ignoreCase = true) ||
                b.address.contains(searchQuery, ignoreCase = true)

        matchesCategory && matchesSearch
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "All Listing",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                },
                navigationIcon = {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onBack() }
                            .testTag("all_listing_back")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                actions = {
                    // Balancing spacer for centered title
                    Spacer(modifier = Modifier.width(48.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar matching Screen 2
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            "Search your listing",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Filled.Clear, contentDescription = "Clear")
                            }
                        } else {
                            IconButton(onClick = {}) {
                                Icon(
                                    imageVector = Icons.Filled.Tune,
                                    contentDescription = "Filters",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = BuListioCoral,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("all_listing_search")
                )
            }

            // Horizontal Filter Chips Row
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) BuListioCoral.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) BuListioCoral else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedCategory = cat }
                            .testTag("all_listing_chip_${cat.replace(" ", "_")}")
                    ) {
                        Text(
                            text = cat,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp,
                                color = if (isSelected) BuListioCoral else MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Listings List with Claim buttons
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No listings found",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try clearing filters or changing search keywords.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        ResidentAdSenseSection(
                            modifier = Modifier.fillMaxWidth(),
                            customTitle = "Resident Discovery Sponsor • Google AdSense",
                            customSubtitle = "Support local businesses, health & community shops across Andaman"
                        )
                    }

                    items(filteredList, key = { it.id }) { business ->
                        val canClaim = currentUser?.role != "Resident" && (currentUser?.role == "Business Owner" || currentUser?.role == "Admin")
                        BusinessCard(
                            business = business,
                            onClick = { onSelectBusiness(business.id) },
                            onToggleSaved = { onToggleSaved(business.id, !business.isSaved) },
                            showClaimButton = canClaim,
                            currentUserEmail = currentUser?.email,
                            onClaimClick = {
                                if (onClaimBusiness != null) {
                                    onClaimBusiness(business.id)
                                } else {
                                    onSelectBusiness(business.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
