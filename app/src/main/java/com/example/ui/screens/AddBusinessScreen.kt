package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BusinessProfileImage

data class ProfilePhotoPreset(
    val id: String,
    val title: String,
    val resName: String
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddBusinessScreen(
    onBack: () -> Unit,
    onSubmit: (
        name: String,
        category: String,
        tagline: String,
        description: String,
        address: String,
        neighborhood: String,
        phone: String,
        website: String,
        email: String,
        hours: String,
        priceLevel: String,
        specialOffer: String?,
        tags: String,
        imageResName: String?,
        imageUri: String?
    ) -> Unit,
    availableCategories: List<String> = emptyList(),
    availableNeighborhoods: List<String> = emptyList(),
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    val defaultCategories = listOf(
        "Cafes & Bakeries",
        "Restaurants",
        "Gymnasium",
        "Salon & Spa",
        "Hospital & Clinics",
        "Retail & Shopping",
        "Health & Wellness",
        "Home Services",
        "Automotive",
        "Arts & Culture"
    )

    val defaultNeighborhoods = listOf(
        "Downtown",
        "Arts District",
        "Riverside",
        "Northside",
        "West End",
        "Old Town",
        "Port Blair Central",
        "Havelock Island"
    )

    val categories = if (availableCategories.isNotEmpty()) availableCategories else defaultCategories
    val neighborhoods = if (availableNeighborhoods.isNotEmpty()) availableNeighborhoods else defaultNeighborhoods

    val availableTags = listOf(
        "Free WiFi",
        "Outdoor Patio",
        "Pet Friendly",
        "Wheelchair Accessible",
        "Vegan Friendly",
        "Free Parking",
        "Takeout & Delivery",
        "Family Owned",
        "Eco-Friendly"
    )

    val photoPresets = listOf(
        ProfilePhotoPreset("cafe", "Cafe & Roastery", "biz_cafe"),
        ProfilePhotoPreset("bistro", "Dining & Bistro", "biz_bistro"),
        ProfilePhotoPreset("bakery", "Bakehouse", "biz_bakery"),
        ProfilePhotoPreset("woodcraft", "Woodcraft Studio", "biz_woodcraft"),
        ProfilePhotoPreset("wellness", "Yoga & Wellness", "biz_wellness"),
        ProfilePhotoPreset("outdoors", "Outdoor Gear", "biz_outdoors"),
        ProfilePhotoPreset("botanical", "Botanical Plants", "biz_botanical"),
        ProfilePhotoPreset("books", "Books & Vinyl", "biz_books"),
        ProfilePhotoPreset("pizza", "Pizzeria & Bar", "biz_pizza"),
        ProfilePhotoPreset("auto", "Auto Repair", "biz_auto"),
        ProfilePhotoPreset("ceramics", "Ceramic Studio", "biz_ceramics"),
        ProfilePhotoPreset("veterinary", "Pet Clinic", "biz_veterinary")
    )

    var name by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(categories[0]) }
    var tagline by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedNeighborhood by remember { mutableStateOf(neighborhoods[0]) }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var website by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var hours by remember { mutableStateOf("Mon-Sat: 9:00 AM - 6:00 PM | Sun: Closed") }
    var specialOffer by remember { mutableStateOf("") }
    val selectedTags = remember { mutableStateListOf("Free WiFi", "Pet Friendly") }

    // Image state: either a preset drawable or picked URI
    var selectedImageResName by remember { mutableStateOf<String?>("biz_cafe") }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }

    // Android Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri.toString()
            selectedImageResName = null
        }
    }

    var nameError by remember { mutableStateOf(false) }
    var addressError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "List Your Business",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("add_business_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Put your local enterprise on the map. Connect with neighbors, loyal regulars, and visitors in TownSpot.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Business Profile Image Selection Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Business Profile Image",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "A high quality photo helps your business stand out to customers browsing the neighborhood directory.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Image Preview Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(14.dp))
                        ) {
                            BusinessProfileImage(
                                imageResName = selectedImageResName,
                                imageUri = selectedImageUri,
                                contentDescription = "Selected Business Profile Photo",
                                modifier = Modifier.fillMaxSize()
                            )

                            // Clear / Reset button if custom URI picked
                            if (selectedImageUri != null) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.Black.copy(alpha = 0.6f),
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                        .clickable {
                                            selectedImageUri = null
                                            selectedImageResName = "biz_cafe"
                                        }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Close,
                                            contentDescription = "Remove photo",
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Reset",
                                            style = MaterialTheme.typography.labelSmall.copy(color = Color.White)
                                        )
                                    }
                                }
                            }
                        }

                        // Pick Custom Photo from Gallery
                        OutlinedButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("pick_photo_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AddPhotoAlternate,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Upload from Device Photos")
                        }

                        Text(
                            text = "Or choose a matching curated storefront photo:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Preset Photos Horizontal Chips
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            photoPresets.forEach { preset ->
                                val isSelected = selectedImageResName == preset.resName && selectedImageUri == null
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        selectedImageResName = preset.resName
                                        selectedImageUri = null
                                    },
                                    label = { Text(preset.title, fontSize = 12.sp) },
                                    leadingIcon = if (isSelected) {
                                        {
                                            Icon(
                                                imageVector = Icons.Filled.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    } else null
                                )
                            }
                        }
                    }
                }
            }

            // Basic Info Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Business Identity",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it
                                nameError = false
                            },
                            label = { Text("Business Name *") },
                            placeholder = { Text("e.g. Copper Kettle Roastery") },
                            singleLine = true,
                            isError = nameError,
                            supportingText = if (nameError) { { Text("Business name is required") } } else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_business_name")
                        )

                        Text(
                            text = "Primary Category",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            categories.forEach { cat ->
                                FilterChip(
                                    selected = selectedCategory == cat,
                                    onClick = { selectedCategory = cat },
                                    label = { Text(cat, fontSize = 12.sp) },
                                    leadingIcon = if (selectedCategory == cat) {
                                        {
                                            Icon(
                                                imageVector = Icons.Filled.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    } else null
                                )
                            }
                        }

                        OutlinedTextField(
                            value = tagline,
                            onValueChange = { tagline = it },
                            label = { Text("Short Catchphrase / Tagline") },
                            placeholder = { Text("e.g. Organic sourdough & specialty pour-over coffees") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_tagline")
                        )

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("About Your Business") },
                            placeholder = { Text("Describe what makes your local spot unique, your history, your mission...") },
                            minLines = 3,
                            maxLines = 5,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_description")
                        )
                    }
                }
            }

            // Location & Contact Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Location & Contact",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        Text(
                            text = "Neighborhood",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            neighborhoods.forEach { hood ->
                                FilterChip(
                                    selected = selectedNeighborhood == hood,
                                    onClick = { selectedNeighborhood = hood },
                                    label = { Text(hood, fontSize = 12.sp) }
                                )
                            }
                        }

                        OutlinedTextField(
                            value = address,
                            onValueChange = {
                                address = it
                                addressError = false
                            },
                            label = { Text("Street Address *") },
                            placeholder = { Text("e.g. 240 Market Street, Suite 3B") },
                            singleLine = true,
                            isError = addressError,
                            supportingText = if (addressError) { { Text("Address is required") } } else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_address")
                        )

                        OutlinedTextField(
                            value = phone,
                            onValueChange = {
                                phone = it
                                phoneError = false
                            },
                            label = { Text("Phone Number *") },
                            placeholder = { Text("e.g. (555) 123-4567") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            isError = phoneError,
                            supportingText = if (phoneError) { { Text("Phone number is required") } } else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_phone")
                        )

                        OutlinedTextField(
                            value = website,
                            onValueChange = { website = it },
                            label = { Text("Website URL (Optional)") },
                            placeholder = { Text("e.g. https://mybusiness.com") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_website")
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Contact Email (Optional)") },
                            placeholder = { Text("e.g. hello@mybusiness.com") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_email")
                        )
                    }
                }
            }

            // Operations & Details Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Operations & Features",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        OutlinedTextField(
                            value = hours,
                            onValueChange = { hours = it },
                            label = { Text("Hours of Operation") },
                            placeholder = { Text("e.g. Mon-Sat: 8 AM - 6 PM | Sun: 10 AM - 4 PM") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_hours")
                        )

                        Text(
                            text = "Amenities & Features",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            availableTags.forEach { tag ->
                                val isSelected = selectedTags.contains(tag)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        if (isSelected) selectedTags.remove(tag) else selectedTags.add(tag)
                                    },
                                    label = { Text(tag, fontSize = 12.sp) },
                                    leadingIcon = if (isSelected) {
                                        {
                                            Icon(
                                                imageVector = Icons.Filled.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    } else null
                                )
                            }
                        }

                        OutlinedTextField(
                            value = specialOffer,
                            onValueChange = { specialOffer = it },
                            label = { Text("Special Community Offer / Promotion (Optional)") },
                            placeholder = { Text("e.g. 15% off first order for TownSpot neighbors") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_special_offer")
                        )
                    }
                }
            }

            // Submit Button
            item {
                Button(
                    onClick = {
                        var hasError = false
                        if (name.isBlank()) {
                            nameError = true
                            hasError = true
                        }
                        if (address.isBlank()) {
                            addressError = true
                            hasError = true
                        }
                        if (phone.isBlank()) {
                            phoneError = true
                            hasError = true
                        }

                        if (!hasError) {
                            onSubmit(
                                name,
                                selectedCategory,
                                tagline.ifBlank { "$selectedCategory in $selectedNeighborhood" },
                                description.ifBlank { "$name is a premier $selectedCategory located in $selectedNeighborhood." },
                                address,
                                selectedNeighborhood,
                                phone,
                                website,
                                email,
                                hours,
                                "",
                                specialOffer,
                                selectedTags.joinToString(", "),
                                selectedImageResName,
                                selectedImageUri
                            )
                            Toast.makeText(context, "Listing published successfully!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_business_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.AddBusiness,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Publish Business Listing",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
