package com.example.ui.components

import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusinessEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.NeighborhoodEntity
import com.example.ui.theme.MomoTrustDisplayFontFamily
import com.example.ui.theme.TownSpotOrange

@Composable
fun AdminTaxonomySection(
    categories: List<CategoryEntity>,
    neighborhoods: List<NeighborhoodEntity>,
    businesses: List<BusinessEntity>,
    onAddCategory: (name: String, iconName: String, description: String) -> Unit,
    onDeleteCategory: (Long) -> Unit,
    onAddNeighborhood: (name: String, description: String) -> Unit,
    onDeleteNeighborhood: (Long) -> Unit,
    onResetTaxonomy: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedSubTab by remember { mutableIntStateOf(0) } // 0: Categories, 1: Neighborhoods
    var searchQuery by remember { mutableStateOf("") }

    // Dialog state
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showAddNeighborhoodDialog by remember { mutableStateOf(false) }
    var categoryToDelete by remember { mutableStateOf<CategoryEntity?>(null) }
    var neighborhoodToDelete by remember { mutableStateOf<NeighborhoodEntity?>(null) }
    var showResetDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        // Sub-tabs: Categories vs Neighborhoods
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            TabRow(
                selectedTabIndex = selectedSubTab,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                containerColor = Color.Transparent,
                divider = {}
            ) {
                Tab(
                    selected = selectedSubTab == 0,
                    onClick = { selectedSubTab = 0 },
                    text = {
                        Text(
                            text = "Categories (${categories.size})",
                            style = androidx.compose.ui.text.TextStyle(
                                fontFamily = MomoTrustDisplayFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        )
                    },
                    icon = { Icon(Icons.Filled.Category, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("admin_subtab_categories")
                )
                Tab(
                    selected = selectedSubTab == 1,
                    onClick = { selectedSubTab = 1 },
                    text = {
                        Text(
                            text = "Neighborhoods (${neighborhoods.size})",
                            style = androidx.compose.ui.text.TextStyle(
                                fontFamily = MomoTrustDisplayFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        )
                    },
                    icon = { Icon(Icons.Filled.Place, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("admin_subtab_neighborhoods")
                )
            }
        }

        // Action Toolbar & Search
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(if (selectedSubTab == 0) "Search categories..." else "Search districts...")
                },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("taxonomy_search_input")
            )

            Spacer(modifier = Modifier.width(10.dp))

            Button(
                onClick = {
                    if (selectedSubTab == 0) {
                        showAddCategoryDialog = true
                    } else {
                        showAddNeighborhoodDialog = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = TownSpotOrange),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .height(52.dp)
                    .testTag(if (selectedSubTab == 0) "add_category_btn" else "add_neighborhood_btn")
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (selectedSubTab == 0) "Add Category" else "Add District",
                    style = androidx.compose.ui.text.TextStyle(
                        fontFamily = MomoTrustDisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                )
            }
        }

        // Quick Preset Restore Strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = { showResetDialog = true },
                modifier = Modifier.testTag("restore_taxonomy_btn")
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Restore Defaults", style = MaterialTheme.typography.labelSmall)
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        // Content List
        if (selectedSubTab == 0) {
            // CATEGORIES LIST
            val filteredCategories = categories.filter {
                searchQuery.isBlank() ||
                        it.name.contains(searchQuery, ignoreCase = true) ||
                        it.description.contains(searchQuery, ignoreCase = true)
            }

            if (filteredCategories.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.Category,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isBlank()) "No categories created yet." else "No categories match '$searchQuery'",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showAddCategoryDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = TownSpotOrange),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Create First Category")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredCategories, key = { it.id }) { cat ->
                        val spotCount = businesses.count { it.category.equals(cat.name, ignoreCase = true) }
                        CategoryCard(
                            category = cat,
                            spotCount = spotCount,
                            onDelete = { categoryToDelete = cat }
                        )
                    }
                }
            }
        } else {
            // NEIGHBORHOODS LIST
            val filteredNeighborhoods = neighborhoods.filter {
                searchQuery.isBlank() ||
                        it.name.contains(searchQuery, ignoreCase = true) ||
                        it.description.contains(searchQuery, ignoreCase = true)
            }

            if (filteredNeighborhoods.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.Place,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isBlank()) "No neighborhoods created yet." else "No districts match '$searchQuery'",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showAddNeighborhoodDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = TownSpotOrange),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Create First District")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredNeighborhoods, key = { it.id }) { hood ->
                        val spotCount = businesses.count { it.neighborhood.equals(hood.name, ignoreCase = true) }
                        NeighborhoodCard(
                            neighborhood = hood,
                            spotCount = spotCount,
                            onDelete = { neighborhoodToDelete = hood }
                        )
                    }
                }
            }
        }
    }

    // --- ADD CATEGORY DIALOG ---
    if (showAddCategoryDialog) {
        var newCatName by remember { mutableStateOf("") }
        var newCatDesc by remember { mutableStateOf("") }
        var selectedIcon by remember { mutableStateOf("storefront") }
        var nameError by remember { mutableStateOf(false) }

        val iconOptions = listOf(
            "storefront" to "Store",
            "local_cafe" to "Cafe",
            "restaurant" to "Dine",
            "fitness_center" to "Gym",
            "content_cut" to "Salon",
            "local_hospital" to "Health",
            "shopping_bag" to "Shop"
        )

        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = {
                Text(
                    text = "Add New Category",
                    style = androidx.compose.ui.text.TextStyle(
                        fontFamily = MomoTrustDisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = newCatName,
                        onValueChange = {
                            newCatName = it
                            if (nameError && it.isNotBlank()) nameError = false
                        },
                        label = { Text("Category Name *") },
                        placeholder = { Text("e.g. Scuba & Watersports") },
                        isError = nameError,
                        supportingText = if (nameError) {
                            { Text("Category name cannot be empty") }
                        } else null,
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_category_name_input")
                    )

                    OutlinedTextField(
                        value = newCatDesc,
                        onValueChange = { newCatDesc = it },
                        label = { Text("Description (Optional)") },
                        placeholder = { Text("e.g. Diving centers, coral reef excursions, and boat charters") },
                        minLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_category_desc_input")
                    )

                    Text(
                        text = "Choose Icon Category",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(iconOptions) { (iconKey, label) ->
                            FilterChip(
                                selected = selectedIcon == iconKey,
                                onClick = { selectedIcon = iconKey },
                                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = getCategoryVector(iconKey),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = TownSpotOrange.copy(alpha = 0.15f),
                                    selectedLabelColor = TownSpotOrange
                                )
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clean = newCatName.trim()
                        if (clean.isBlank()) {
                            nameError = true
                            return@Button
                        }
                        onAddCategory(clean, selectedIcon, newCatDesc.trim())
                        Toast.makeText(context, "Added category '$clean'", Toast.LENGTH_SHORT).show()
                        showAddCategoryDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TownSpotOrange),
                    modifier = Modifier.testTag("confirm_add_category_btn")
                ) {
                    Text("Add Category")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddCategoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // --- ADD NEIGHBORHOOD DIALOG ---
    if (showAddNeighborhoodDialog) {
        var newHoodName by remember { mutableStateOf("") }
        var newHoodDesc by remember { mutableStateOf("") }
        var nameError by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddNeighborhoodDialog = false },
            title = {
                Text(
                    text = "Add New Neighborhood / District",
                    style = androidx.compose.ui.text.TextStyle(
                        fontFamily = MomoTrustDisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = newHoodName,
                        onValueChange = {
                            newHoodName = it
                            if (nameError && it.isNotBlank()) nameError = false
                        },
                        label = { Text("Neighborhood Name *") },
                        placeholder = { Text("e.g. Neil Island Beachside") },
                        isError = nameError,
                        supportingText = if (nameError) {
                            { Text("Neighborhood name cannot be empty") }
                        } else null,
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_neighborhood_name_input")
                    )

                    OutlinedTextField(
                        value = newHoodDesc,
                        onValueChange = { newHoodDesc = it },
                        label = { Text("Area Description (Optional)") },
                        placeholder = { Text("e.g. Pristine white sand beaches, tranquil coconut groves, and artisan cafes") },
                        minLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_neighborhood_desc_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clean = newHoodName.trim()
                        if (clean.isBlank()) {
                            nameError = true
                            return@Button
                        }
                        onAddNeighborhood(clean, newHoodDesc.trim())
                        Toast.makeText(context, "Added district '$clean'", Toast.LENGTH_SHORT).show()
                        showAddNeighborhoodDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TownSpotOrange),
                    modifier = Modifier.testTag("confirm_add_neighborhood_btn")
                ) {
                    Text("Add District")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddNeighborhoodDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // --- DELETE CATEGORY CONFIRMATION DIALOG ---
    categoryToDelete?.let { cat ->
        val usageCount = businesses.count { it.category.equals(cat.name, ignoreCase = true) }
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            icon = {
                Icon(Icons.Filled.WarningAmber, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(32.dp))
            },
            title = {
                Text(
                    text = "Delete Category?",
                    style = androidx.compose.ui.text.TextStyle(
                        fontFamily = MomoTrustDisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Are you sure you want to delete the category \"${cat.name}\"?",
                        fontWeight = FontWeight.SemiBold
                    )
                    if (usageCount > 0) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Notice: $usageCount merchant listing(s) are currently categorized under this name. Their listings will remain safe, but this category chip will be removed from future selection.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    } else {
                        Text(
                            text = "No active listings are currently using this category. It is safe to remove.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteCategory(cat.id)
                        Toast.makeText(context, "Deleted category '${cat.name}'", Toast.LENGTH_SHORT).show()
                        categoryToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_delete_category_btn")
                ) {
                    Text("Delete Category")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { categoryToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // --- DELETE NEIGHBORHOOD CONFIRMATION DIALOG ---
    neighborhoodToDelete?.let { hood ->
        val usageCount = businesses.count { it.neighborhood.equals(hood.name, ignoreCase = true) }
        AlertDialog(
            onDismissRequest = { neighborhoodToDelete = null },
            icon = {
                Icon(Icons.Filled.WarningAmber, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(32.dp))
            },
            title = {
                Text(
                    text = "Delete Neighborhood District?",
                    style = androidx.compose.ui.text.TextStyle(
                        fontFamily = MomoTrustDisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Are you sure you want to delete the neighborhood \"${hood.name}\"?",
                        fontWeight = FontWeight.SemiBold
                    )
                    if (usageCount > 0) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Notice: $usageCount merchant listing(s) are currently located in this neighborhood. Their addresses remain intact, but this area filter will be removed.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    } else {
                        Text(
                            text = "No active listings are currently located in this district. It is safe to remove.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteNeighborhood(hood.id)
                        Toast.makeText(context, "Deleted neighborhood '${hood.name}'", Toast.LENGTH_SHORT).show()
                        neighborhoodToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_delete_neighborhood_btn")
                ) {
                    Text("Delete District")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { neighborhoodToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // --- RESET TAXONOMY DIALOG ---
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Restore Default Taxonomy?") },
            text = {
                Text("This will restore the default curated categories (Cafes, Restaurants, Gymnasium, etc.) and Andaman Island neighborhoods (Downtown, Havelock Island, Port Blair, etc.). Any custom items will be reset.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetTaxonomy()
                        Toast.makeText(context, "Restored default categories and neighborhoods", Toast.LENGTH_SHORT).show()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TownSpotOrange)
                ) {
                    Text("Restore Defaults")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun CategoryCard(
    category: CategoryEntity,
    spotCount: Int,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("category_card_${category.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = TownSpotOrange.copy(alpha = 0.12f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = getCategoryVector(category.iconName),
                        contentDescription = null,
                        tint = TownSpotOrange,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = category.name,
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = MomoTrustDisplayFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = if (spotCount == 1) "1 spot" else "$spotCount spots",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                if (category.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = category.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.testTag("delete_category_btn_${category.id}")
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete category",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun NeighborhoodCard(
    neighborhood: NeighborhoodEntity,
    spotCount: Int,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("neighborhood_card_${neighborhood.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Place,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = neighborhood.name,
                        style = androidx.compose.ui.text.TextStyle(
                            fontFamily = MomoTrustDisplayFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = if (spotCount == 1) "1 spot" else "$spotCount spots",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                if (neighborhood.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = neighborhood.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.testTag("delete_neighborhood_btn_${neighborhood.id}")
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete neighborhood",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

private fun getCategoryVector(iconName: String): ImageVector {
    return when (iconName.lowercase()) {
        "local_cafe", "cafe" -> Icons.Filled.LocalCafe
        "restaurant", "food", "dine" -> Icons.Filled.Restaurant
        "fitness_center", "gym" -> Icons.Filled.FitnessCenter
        "content_cut", "salon", "barber" -> Icons.Filled.ContentCut
        "local_hospital", "hospital", "clinic", "health" -> Icons.Filled.LocalHospital
        "shopping_bag", "retail", "shop" -> Icons.Filled.ShoppingBag
        else -> Icons.Filled.Storefront
    }
}
