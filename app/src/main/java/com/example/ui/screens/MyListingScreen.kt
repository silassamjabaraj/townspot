package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import com.example.util.ProvideSafeTextToolbar
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusinessEntity
import com.example.data.model.UserEntity
import com.example.ui.components.BusinessAdSenseSection
import com.example.ui.components.BusinessProfileImage
import com.example.ui.components.RatingDisplay
import com.example.ui.theme.BuListioCoral
import com.example.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyListingScreen(
    currentUser: UserEntity?,
    allBusinesses: List<BusinessEntity>,
    onSelectBusiness: (Long) -> Unit,
    onUpdateBusiness: (BusinessEntity) -> Unit,
    onOpenAddBusiness: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    // Business Owner sees ONLY their own listing(s) and NOT others!
    val myBusinesses = allBusinesses.filter { biz ->
        currentUser?.email != null && biz.claimedByOwnerEmail.equals(currentUser.email, ignoreCase = true)
    }

    var businessToEdit by remember { mutableStateOf<BusinessEntity?>(null) }

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
                                    imageVector = Icons.Filled.Store,
                                    contentDescription = null,
                                    tint = BuListioCoral,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "My Listings",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp
                                )
                            )
                            Text(
                                text = "Manage and edit your business profile",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("my_listing_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onOpenAddBusiness,
                        modifier = Modifier.testTag("my_listing_add_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AddBusiness,
                            contentDescription = "List New Spot",
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

            // Owner Scope Notice Card
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().testTag("owner_scope_info_card")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Filled.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp).padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Owner Dashboard Scope",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "You only see and manage your own listed businesses here. Listings submitted by you must be verified by an administrator before appearing to all community residents.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Business Section Google AdSense Partner Card
            item {
                BusinessAdSenseSection(
                    modifier = Modifier.fillMaxWidth(),
                    customTitle = "Merchant Monetization • Google AdSense",
                    customSubtitle = "Promote commercial services, equipment rentals & island supply partners"
                )
            }

            if (myBusinesses.isEmpty()) {
                // Empty state if owner hasn't listed or claimed a spot yet
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = BuListioCoral.copy(alpha = 0.1f),
                                modifier = Modifier.size(64.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Filled.Store,
                                        contentDescription = null,
                                        tint = BuListioCoral,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "No Business Spot Listed Yet",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "List your local business to connect with neighborhood residents, post deals, and receive customer reviews.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(18.dp))
                            Button(
                                onClick = onOpenAddBusiness,
                                colors = ButtonDefaults.buttonColors(containerColor = BuListioCoral),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().testTag("empty_list_my_spot_btn")
                            ) {
                                Icon(Icons.Filled.AddBusiness, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("List Your Business Spot Now")
                            }
                        }
                    }
                }
            } else {
                items(myBusinesses, key = { it.id }) { business ->
                    MyBusinessListingCard(
                        business = business,
                        onViewDetail = { onSelectBusiness(business.id) },
                        onEdit = { businessToEdit = business }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    // Edit Business Dialog
    businessToEdit?.let { biz ->
        EditBusinessOwnerDialog(
            business = biz,
            onDismiss = { businessToEdit = null },
            onSave = { updated ->
                onUpdateBusiness(updated)
                businessToEdit = null
                Toast.makeText(context, "Listing updated successfully!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun MyBusinessListingCard(
    business: BusinessEntity,
    onViewDetail: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("my_listing_card_${business.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Verification Status Banner
            if (!business.isVerified) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFFF7ED),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDBA74)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF9A3412)
                                )
                            )
                            Text(
                                text = "Awaiting admin review. Will appear to all residents once verified.",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFC2410C)
                            )
                        }
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF0FDF4),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Verified & Live in Directory",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF166534)
                            )
                        )
                    }
                }
            }

            // Main Listing Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onViewDetail)
                ) {
                    BusinessProfileImage(
                        imageResName = business.imageResName,
                        imageUri = business.imageUri,
                        contentDescription = business.name,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = business.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    )
                    Text(
                        text = "${business.category} • ${business.neighborhood}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    RatingDisplay(rating = business.rating, reviewCount = business.reviewCount, starSize = 13.dp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
            Spacer(modifier = Modifier.height(10.dp))

            // Key info details
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = business.address, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = business.hours, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (business.phone.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = business.phone, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons: Edit and View
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onEdit,
                    colors = ButtonDefaults.buttonColors(containerColor = BuListioCoral),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).testTag("edit_my_listing_btn_${business.id}")
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit Listing")
                }

                OutlinedButton(
                    onClick = onViewDetail,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).testTag("view_my_listing_btn_${business.id}")
                ) {
                    Icon(Icons.Filled.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("View Detail")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditBusinessOwnerDialog(
    business: BusinessEntity,
    onDismiss: () -> Unit,
    onSave: (BusinessEntity) -> Unit
) {
    var name by remember { mutableStateOf(business.name) }
    var category by remember { mutableStateOf(business.category) }
    var tagline by remember { mutableStateOf(business.tagline) }
    var description by remember { mutableStateOf(business.description) }
    var address by remember { mutableStateOf(business.address) }
    var neighborhood by remember { mutableStateOf(business.neighborhood) }
    var phone by remember { mutableStateOf(business.phone) }
    var website by remember { mutableStateOf(business.website) }
    var email by remember { mutableStateOf(business.email) }
    var hours by remember { mutableStateOf(business.hours) }
    var priceLevel by remember { mutableStateOf(business.priceLevel) }
    var specialOffer by remember { mutableStateOf(business.specialOffer ?: "") }
    var tags by remember { mutableStateOf(business.tags) }

    val categories = listOf("Restaurant", "Gymnasium", "Salon", "Hospital", "Cafes & Bakeries", "Retail & Shopping", "Home Services")
    var categoryExpanded by remember { mutableStateOf(false) }
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
                Icon(Icons.Filled.Edit, contentDescription = null, tint = BuListioCoral, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Business Details", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Business Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_input_name")
                )

                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = tagline,
                    onValueChange = { tagline = it },
                    label = { Text("Tagline") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_input_tagline")
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth().testTag("edit_input_description")
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_input_address")
                )

                OutlinedTextField(
                    value = neighborhood,
                    onValueChange = { neighborhood = it },
                    label = { Text("Neighborhood District") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_input_neighborhood")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("edit_input_phone")
                    )

                    OutlinedTextField(
                        value = priceLevel,
                        onValueChange = { priceLevel = it },
                        label = { Text("Price ($$)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("edit_input_price")
                    )
                }

                OutlinedTextField(
                    value = hours,
                    onValueChange = { hours = it },
                    label = { Text("Hours of Operation") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_input_hours")
                )

                OutlinedTextField(
                    value = website,
                    onValueChange = { website = it },
                    label = { Text("Website") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_input_website")
                )

                OutlinedTextField(
                    value = specialOffer,
                    onValueChange = { specialOffer = it },
                    label = { Text("Current Special Offer / Highlight") },
                    placeholder = { Text("e.g. 15% off first order") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_input_offer")
                )

                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("Features & Tags (Comma separated)") },
                    placeholder = { Text("WiFi, Parking, Outdoor Seating") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_input_tags")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val updated = business.copy(
                            name = name.trim(),
                            category = category,
                            tagline = tagline.trim(),
                            description = description.trim(),
                            address = address.trim(),
                            neighborhood = neighborhood.trim(),
                            phone = phone.trim(),
                            website = website.trim(),
                            email = email.trim(),
                            hours = hours.trim(),
                            priceLevel = priceLevel,
                            specialOffer = if (specialOffer.isBlank()) null else specialOffer.trim(),
                            tags = tags.trim()
                        )
                        focusManager.clearFocus(force = true)
                        keyboardController?.hide()
                        onSave(updated)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BuListioCoral),
                modifier = Modifier.testTag("save_edit_listing_btn")
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
