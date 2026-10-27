package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BuListioCoral
import com.example.ui.viewmodel.AppScreen

/**
 * BuListio custom bottom navigation bar configured specifically per user role:
 * - Resident / Guest: Home, Promotions (replaced Listing), Saved, Profile
 * - Business Owner: Home, My Listing, Add Spot, Promos (separate page), Profile (No Saved, No All Listing)
 * - Admin: Home, All Listing, Promotions, Admin, Profile (All features)
 */
@Composable
fun BuListioBottomBar(
    currentScreen: AppScreen,
    userRole: String?,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 12.dp, shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)),
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (userRole) {
                "Business Owner" -> {
                    // 1. Home
                    NavTabItem(
                        isSelected = currentScreen == AppScreen.HOME,
                        selectedIcon = Icons.Filled.Home,
                        unselectedIcon = Icons.Outlined.Home,
                        label = "Home",
                        testTag = "bottom_nav_home",
                        onClick = { onNavigate(AppScreen.HOME) }
                    )

                    // 2. My Listing (Only sees and edits their own listing!)
                    NavTabItem(
                        isSelected = currentScreen == AppScreen.MY_LISTING,
                        selectedIcon = Icons.Filled.Store,
                        unselectedIcon = Icons.Outlined.Store,
                        label = "My Listing",
                        testTag = "bottom_nav_my_listing",
                        onClick = { onNavigate(AppScreen.MY_LISTING) }
                    )

                    // 3. Add Spot
                    NavTabItem(
                        isSelected = currentScreen == AppScreen.ADD_BUSINESS,
                        selectedIcon = Icons.Filled.AddCircleOutline,
                        unselectedIcon = Icons.Outlined.AddCircleOutline,
                        label = "Add Spot",
                        testTag = "bottom_nav_add",
                        onClick = { onNavigate(AppScreen.ADD_BUSINESS) }
                    )

                    // 4. My Active Promotions (Separate page for Business Owner)
                    NavTabItem(
                        isSelected = currentScreen == AppScreen.MY_PROMOTIONS,
                        selectedIcon = Icons.Filled.Campaign,
                        unselectedIcon = Icons.Outlined.Campaign,
                        label = "Promos",
                        testTag = "bottom_nav_my_promos",
                        onClick = { onNavigate(AppScreen.MY_PROMOTIONS) }
                    )

                    // 5. Profile
                    NavTabItem(
                        isSelected = currentScreen == AppScreen.PROFILE,
                        selectedIcon = Icons.Filled.Person,
                        unselectedIcon = Icons.Outlined.Person,
                        label = "Profile",
                        testTag = "bottom_nav_profile",
                        onClick = { onNavigate(AppScreen.PROFILE) }
                    )
                }

                "Admin" -> {
                    // Admin: Can have all features!
                    // 1. Home
                    NavTabItem(
                        isSelected = currentScreen == AppScreen.HOME,
                        selectedIcon = Icons.Filled.Home,
                        unselectedIcon = Icons.Outlined.Home,
                        label = "Home",
                        testTag = "bottom_nav_home",
                        onClick = { onNavigate(AppScreen.HOME) }
                    )

                    // 2. All Listing
                    NavTabItem(
                        isSelected = currentScreen == AppScreen.NEIGHBORHOODS,
                        selectedIcon = Icons.Filled.ListAlt,
                        unselectedIcon = Icons.Outlined.ListAlt,
                        label = "Listing",
                        testTag = "bottom_nav_listing",
                        onClick = { onNavigate(AppScreen.NEIGHBORHOODS) }
                    )

                    // 3. Promotions
                    NavTabItem(
                        isSelected = currentScreen == AppScreen.PROMOTIONS,
                        selectedIcon = Icons.Filled.LocalOffer,
                        unselectedIcon = Icons.Outlined.LocalOffer,
                        label = "Promos",
                        testTag = "bottom_nav_promotions",
                        onClick = { onNavigate(AppScreen.PROMOTIONS) }
                    )

                    // 4. Admin Portal
                    NavTabItem(
                        isSelected = currentScreen == AppScreen.ADMIN,
                        selectedIcon = Icons.Filled.AdminPanelSettings,
                        unselectedIcon = Icons.Outlined.AdminPanelSettings,
                        label = "Admin",
                        testTag = "bottom_nav_admin",
                        onClick = { onNavigate(AppScreen.ADMIN) }
                    )

                    // 5. Profile
                    NavTabItem(
                        isSelected = currentScreen == AppScreen.PROFILE,
                        selectedIcon = Icons.Filled.Person,
                        unselectedIcon = Icons.Outlined.Person,
                        label = "Profile",
                        testTag = "bottom_nav_profile",
                        onClick = { onNavigate(AppScreen.PROFILE) }
                    )
                }

                else -> {
                    // Resident & Guest:
                    // In Resident - remove All Listing page instead of put promotion!
                    // 1. Home
                    NavTabItem(
                        isSelected = currentScreen == AppScreen.HOME,
                        selectedIcon = Icons.Filled.Home,
                        unselectedIcon = Icons.Outlined.Home,
                        label = "Home",
                        testTag = "bottom_nav_home",
                        onClick = { onNavigate(AppScreen.HOME) }
                    )

                    // 2. Promotions (Replaced All Listing with Promotions!)
                    NavTabItem(
                        isSelected = currentScreen == AppScreen.PROMOTIONS,
                        selectedIcon = Icons.Filled.LocalOffer,
                        unselectedIcon = Icons.Outlined.LocalOffer,
                        label = "Promos",
                        testTag = "bottom_nav_promotions",
                        onClick = { onNavigate(AppScreen.PROMOTIONS) }
                    )

                    // 3. Saved Places
                    NavTabItem(
                        isSelected = currentScreen == AppScreen.SAVED,
                        selectedIcon = Icons.Filled.Bookmark,
                        unselectedIcon = Icons.Outlined.BookmarkBorder,
                        label = "Saved",
                        testTag = "bottom_nav_saved",
                        onClick = { onNavigate(AppScreen.SAVED) }
                    )

                    // 4. Profile
                    NavTabItem(
                        isSelected = currentScreen == AppScreen.PROFILE,
                        selectedIcon = Icons.Filled.Person,
                        unselectedIcon = Icons.Outlined.Person,
                        label = "Profile",
                        testTag = "bottom_nav_profile",
                        onClick = { onNavigate(AppScreen.PROFILE) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NavTabItem(
    isSelected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    label: String,
    testTag: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    if (isSelected) {
        // Active Coral Capsule Pill
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = BuListioCoral,
            modifier = Modifier
                .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
                .testTag(testTag)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = selectedIcon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(19.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 12.sp
                    )
                )
            }
        }
    } else {
        // Inactive minimal icon
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .clickable(onClick = onClick)
                .testTag(testTag),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = unselectedIcon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                modifier = Modifier.size(23.dp)
            )
        }
    }
}
