package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.BusinessEntity
import com.example.data.model.UserEntity
import com.example.ui.components.BusinessCard
import com.example.ui.components.ResidentAdSenseSection
import com.example.ui.theme.BuListioCoral
import com.example.ui.theme.MomoTrustDisplayFontFamily
import com.example.ui.theme.NunitoFontFamily
import com.example.ui.theme.TownSpotOrange
import com.example.ui.viewmodel.FilterState

data class ListingCategoryItem(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val circleColor: Color,
    val iconColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    businesses: List<BusinessEntity>,
    savedCount: Int,
    filterState: FilterState,
    onSearchChange: (String) -> Unit,
    onCategorySelect: (String) -> Unit,
    onNeighborhoodSelect: (String) -> Unit,
    onToggleOpenNow: () -> Unit,
    onToggleSpecialOffers: () -> Unit,
    onSelectBusiness: (Long) -> Unit,
    onToggleSaved: (Long, Boolean) -> Unit,
    onOpenSaved: () -> Unit,
    onOpenNeighborhoods: () -> Unit,
    onOpenAddBusiness: () -> Unit,
    onOpenAdmin: () -> Unit,
    onOpenProfile: () -> Unit = {},
    onOpenLogin: () -> Unit = {},
    featuredBusinesses: List<BusinessEntity> = emptyList(),
    onSetMinRating: (Float) -> Unit = {},
    onResetFilters: () -> Unit = {},
    currentUser: UserEntity? = null,
    authError: String? = null,
    isAuthLoading: Boolean = false,
    onSignIn: (String, String) -> Unit = { _, _ -> },
    onRegister: (String, String, String, String, String?) -> Unit = { _, _, _, _, _ -> },
    onSignOut: () -> Unit = {},
    onClearAuthError: () -> Unit = {},
    availableCategories: List<String> = emptyList(),
    availableNeighborhoods: List<String> = emptyList(),
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    // Categories matching directory listings
    val listingCategories = remember(availableCategories) {
        if (availableCategories.isNotEmpty()) {
            availableCategories.mapIndexed { idx, catName ->
                val (bg, iconColor) = when (idx % 6) {
                    0 -> Color(0xFFF3E8FF) to Color(0xFF9333EA)
                    1 -> Color(0xFFE0F2FE) to Color(0xFF0284C7)
                    2 -> Color(0xFFFEF3C7) to Color(0xFFD97706)
                    3 -> Color(0xFFEDE9FE) to Color(0xFF7C3AED)
                    4 -> Color(0xFFFFE4E6) to Color(0xFFE11D48)
                    else -> Color(0xFFCCFBF1) to Color(0xFF0F766E)
                }
                val icon = when {
                    catName.contains("Cafe", ignoreCase = true) || catName.contains("Baker", ignoreCase = true) -> Icons.Filled.LocalCafe
                    catName.contains("Restaurant", ignoreCase = true) || catName.contains("Dine", ignoreCase = true) -> Icons.Filled.Restaurant
                    catName.contains("Gym", ignoreCase = true) || catName.contains("Fitness", ignoreCase = true) -> Icons.Filled.FitnessCenter
                    catName.contains("Salon", ignoreCase = true) || catName.contains("Barber", ignoreCase = true) -> Icons.Filled.ContentCut
                    catName.contains("Hospital", ignoreCase = true) || catName.contains("Health", ignoreCase = true) -> Icons.Filled.LocalHospital
                    catName.contains("Shop", ignoreCase = true) || catName.contains("Retail", ignoreCase = true) -> Icons.Filled.ShoppingBag
                    else -> Icons.Filled.ShoppingBag
                }
                ListingCategoryItem(
                    id = catName,
                    name = catName,
                    icon = icon,
                    circleColor = bg,
                    iconColor = iconColor
                )
            }
        } else {
            listOf(
                ListingCategoryItem("Salon", "Salon", Icons.Filled.ContentCut, Color(0xFFF3E8FF), Color(0xFF9333EA)),
                ListingCategoryItem("Hospital", "Hospital", Icons.Filled.LocalHospital, Color(0xFFE0F2FE), Color(0xFF0284C7)),
                ListingCategoryItem("Travel", "Travel", Icons.Filled.Flight, Color(0xFFFEF3C7), Color(0xFFD97706)),
                ListingCategoryItem("Gymnasium", "Gymnasium", Icons.Filled.FitnessCenter, Color(0xFFEDE9FE), Color(0xFF7C3AED)),
                ListingCategoryItem("Restaurant", "Restaurant", Icons.Filled.Restaurant, Color(0xFFFFE4E6), Color(0xFFE11D48)),
                ListingCategoryItem("Cafes & Bakeries", "Cafes", Icons.Filled.LocalCafe, Color(0xFFFEF9C3), Color(0xFFB45309)),
                ListingCategoryItem("Retail & Shopping", "Shopping", Icons.Filled.ShoppingBag, Color(0xFFCCFBF1), Color(0xFF0F766E))
            )
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // 1. BuListio Brand Header matching Screen 1 in IMG_0109.png
        item {
            Surface(
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Brand: Logo + TownSpot Brand in Icon Font
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onCategorySelect("All") }
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.Transparent,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.townspot_app_icon),
                                contentDescription = "TownSpot Brand Icon",
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Town",
                                    style = androidx.compose.ui.text.TextStyle(
                                        fontFamily = MomoTrustDisplayFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp,
                                        letterSpacing = (-0.3).sp
                                    ),
                                    color = Color(0xFFFF841F)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "Spot",
                                    style = androidx.compose.ui.text.TextStyle(
                                        fontFamily = MomoTrustDisplayFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp,
                                        letterSpacing = (-0.3).sp
                                    ),
                                    color = Color(0xFF0284C7)
                                )
                            }
                            Text(
                                text = "Andaman Islands",
                                style = androidx.compose.ui.text.TextStyle(
                                    fontFamily = MomoTrustDisplayFontFamily,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.8.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Right Actions: Notification bell with unread dot & More options
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Notifications Bell
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .clickable { onOpenSaved() }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                BadgedBox(
                                    badge = {
                                        Badge(
                                            containerColor = BuListioCoral,
                                            modifier = Modifier.size(7.dp)
                                        )
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Notifications,
                                        contentDescription = "Notifications",
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // More options menu (3 horizontal dots)
                        Box {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .clickable { showMenu = true }
                                    .testTag("home_more_menu")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Filled.MoreHoriz,
                                        contentDescription = "More Options",
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                if (currentUser == null) {
                                    DropdownMenuItem(
                                        text = { Text("Sign In / Register") },
                                        onClick = {
                                            showMenu = false
                                            onOpenLogin()
                                        }
                                    )
                                }
                                DropdownMenuItem(
                                    text = { Text("My Profile") },
                                    onClick = {
                                        showMenu = false
                                        onOpenProfile()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Saved Places ($savedCount)") },
                                    onClick = {
                                        showMenu = false
                                        onOpenSaved()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("All Listings") },
                                    onClick = {
                                        showMenu = false
                                        onOpenNeighborhoods()
                                    }
                                )
                                if (currentUser?.role == "Admin") {
                                    DropdownMenuItem(
                                        text = { Text("Admin Console") },
                                        onClick = {
                                            showMenu = false
                                            onOpenAdmin()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Search Bar matching Screen 1 in IMG_0109.png
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                OutlinedTextField(
                    value = filterState.query,
                    onValueChange = onSearchChange,
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
                        if (filterState.query.isNotBlank()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(Icons.Filled.Clear, contentDescription = "Clear")
                            }
                        } else {
                            IconButton(onClick = onOpenNeighborhoods) {
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
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = BuListioCoral,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_input")
                )
            }
        }

        // 3. Listing Categories Section matching Screen 1 in IMG_0109.png
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                // Section Header: "Listing Categories" + "See All >"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Listing Categories",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { onOpenNeighborhoods() }
                            .testTag("categories_see_all")
                    ) {
                        Text(
                            text = "See All >",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BuListioCoral,
                                fontSize = 13.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Circular Category Icons Row
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(listingCategories) { cat ->
                        val isSelected = filterState.selectedCategory.equals(cat.id, ignoreCase = true)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable {
                                    if (isSelected) onCategorySelect("All")
                                    else onCategorySelect(cat.id)
                                }
                                .testTag("cat_circle_${cat.id}")
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) BuListioCoral else cat.circleColor,
                                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    Color.Black.copy(alpha = 0.04f)
                                ),
                                shadowElevation = 1.dp,
                                modifier = Modifier.size(54.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = cat.icon,
                                        contentDescription = cat.name,
                                        tint = if (isSelected) Color.White else cat.iconColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = cat.name,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 12.sp,
                                    color = if (isSelected) BuListioCoral else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        }

        // 5. Popular Listing Items Promo Card matching Screen 1 in IMG_0109.png
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .clickable { onOpenNeighborhoods() }
                    .testTag("popular_promo_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(138.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF090E1D),
                                    Color(0xFF141334),
                                    Color(0xFF1C1340)
                                )
                            )
                        )
                ) {
                    // Right illustration background
                    Image(
                        painter = painterResource(id = R.drawable.img_promo_card),
                        contentDescription = "Popular listings banner graphic",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .width(180.dp)
                            .fillMaxSize()
                    )

                    // Subtle dark gradient scrim over text area for crisp contrast
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    0.0f to Color(0xFF090E1D).copy(alpha = 0.95f),
                                    0.6f to Color(0xFF090E1D).copy(alpha = 0.7f),
                                    1.0f to Color.Transparent
                                )
                            )
                    )

                    // Text Content & Pill Button on Left
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Explore Most Popular\nListing Items",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 16.sp,
                                lineHeight = 21.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // "More Listing" Pure White Capsule Button
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { onOpenNeighborhoods() }
                        ) {
                            Text(
                                text = "More Listing",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF090E1D)
                                ),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Resident Section Google AdSense Sponsored Partner Banner
        item {
            ResidentAdSenseSection(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                customTitle = "Resident Community Sponsor • Google AdSense",
                customSubtitle = "Explore verified local services, wellness hubs & community events"
            )
        }

        // 6. Featured Listings Section matching Screen 1 in IMG_0109.png
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Featured Listings",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onOpenNeighborhoods() }
                        .testTag("featured_see_all")
                ) {
                    Text(
                        text = "See All >",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = BuListioCoral,
                            fontSize = 13.sp
                        )
                    )
                }
            }
        }

        // 7. Featured Horizontal Listing Cards
        val displayList = businesses.filter { b ->
            val matchesCategory = if (filterState.selectedCategory == "All") true
            else b.category.equals(filterState.selectedCategory, ignoreCase = true)

            val matchesSearch = if (filterState.query.isBlank()) true
            else b.name.contains(filterState.query, ignoreCase = true) ||
                    b.category.contains(filterState.query, ignoreCase = true) ||
                    b.address.contains(filterState.query, ignoreCase = true)

            matchesCategory && matchesSearch
        }

        if (displayList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No listings match your search.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(displayList, key = { it.id }) { business ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)) {
                    BusinessCard(
                        business = business,
                        onClick = { onSelectBusiness(business.id) },
                        onToggleSaved = { onToggleSaved(business.id, !business.isSaved) },
                        showClaimButton = false
                    )
                }
            }
        }
    }
}
