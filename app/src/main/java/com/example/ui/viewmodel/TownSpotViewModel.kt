package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.BusinessEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.NeighborhoodEntity
import com.example.data.model.PromotionEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.UserEntity
import com.example.data.remote.supabase.SyncResult
import com.example.data.remote.supabase.SyncUiState
import com.example.data.repository.BusinessRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    DETAIL,
    ADD_BUSINESS,
    SAVED,
    NEIGHBORHOODS,
    ADMIN,
    PROFILE,
    LOGIN,
    PROMOTIONS,
    MY_LISTING,
    MY_PROMOTIONS
}

data class FilterState(
    val query: String = "",
    val selectedCategory: String = "All",
    val selectedNeighborhood: String = "All",
    val onlyOpenNow: Boolean = false,
    val onlySpecialOffers: Boolean = false,
    val minRating: Float = 0f
)

class TownSpotViewModel(
    private val repository: BusinessRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            if (repository.supabaseSyncManager?.isConfigured() == true && repository.supabaseSyncManager.isAutoPushEnabled) {
                repository.pushAllDataToSupabase()
            }
        }
    }

    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _selectedBusinessId = MutableStateFlow<Long?>(null)
    val selectedBusinessId: StateFlow<Long?> = _selectedBusinessId.asStateFlow()

    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    val allBusinesses: StateFlow<List<BusinessEntity>> = repository.allBusinesses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedBusinesses: StateFlow<List<BusinessEntity>> = repository.savedBusinesses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val featuredBusinesses: StateFlow<List<BusinessEntity>> = combine(
        repository.featuredBusinesses,
        _currentUser
    ) { businesses, user ->
        val isAdmin = user?.role == "Admin"
        businesses.filter { if (isAdmin) true else it.isVerified }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReviews: StateFlow<List<ReviewEntity>> = repository.allReviews
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPromotions: StateFlow<List<PromotionEntity>> = repository.allPromotions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCategories: StateFlow<List<CategoryEntity>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNeighborhoods: StateFlow<List<NeighborhoodEntity>> = repository.allNeighborhoods
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categoryNames: StateFlow<List<String>> = combine(
        repository.allCategories,
        allBusinesses
    ) { categories, businesses ->
        if (categories.isNotEmpty()) {
            categories.map { it.name }
        } else {
            businesses.map { it.category }.distinct().filter { it.isNotBlank() }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val neighborhoodNames: StateFlow<List<String>> = combine(
        repository.allNeighborhoods,
        allBusinesses
    ) { neighborhoods, businesses ->
        if (neighborhoods.isNotEmpty()) {
            neighborhoods.map { it.name }
        } else {
            businesses.map { it.neighborhood }.distinct().filter { it.isNotBlank() }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Combined filtered list: Non-admin users only see verified businesses publicly!
    val filteredBusinesses: StateFlow<List<BusinessEntity>> = combine(
        allBusinesses,
        _filterState,
        _currentUser
    ) { businesses, filter, user ->
        val isAdmin = user?.role == "Admin"
        businesses.filter { business ->
            // Unverified spots require admin approval before appearing to all!
            val isPubliclyVisible = if (isAdmin) true else business.isVerified
            if (!isPubliclyVisible) return@filter false

            val matchesQuery = filter.query.isBlank() ||
                    business.name.contains(filter.query, ignoreCase = true) ||
                    business.category.contains(filter.query, ignoreCase = true) ||
                    business.neighborhood.contains(filter.query, ignoreCase = true) ||
                    business.tags.contains(filter.query, ignoreCase = true) ||
                    business.description.contains(filter.query, ignoreCase = true)

            val matchesCategory = filter.selectedCategory == "All" ||
                    business.category.equals(filter.selectedCategory, ignoreCase = true)

            val matchesNeighborhood = filter.selectedNeighborhood == "All" ||
                    business.neighborhood.equals(filter.selectedNeighborhood, ignoreCase = true)

            val matchesOpenNow = !filter.onlyOpenNow || business.isOpenNow

            val matchesSpecialOffers = !filter.onlySpecialOffers || !business.specialOffer.isNullOrBlank()

            val matchesRating = business.rating >= filter.minRating

            matchesQuery && matchesCategory && matchesNeighborhood && matchesOpenNow && matchesSpecialOffers && matchesRating
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Currently viewed business entity flow
    val currentBusiness: StateFlow<BusinessEntity?> = combine(
        allBusinesses,
        _selectedBusinessId
    ) { businesses, id ->
        if (id == null) null else businesses.find { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Reviews for the current business
    private val _currentReviews = MutableStateFlow<List<ReviewEntity>>(emptyList())
    val currentReviews: StateFlow<List<ReviewEntity>> = _currentReviews.asStateFlow()

    fun navigateToDetail(businessId: Long) {
        _selectedBusinessId.value = businessId
        _currentScreen.value = AppScreen.DETAIL
        viewModelScope.launch {
            repository.getReviewsForBusiness(businessId).collect { reviews ->
                _currentReviews.value = reviews
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun navigateBack() {
        if (_currentScreen.value != AppScreen.HOME) {
            _currentScreen.value = AppScreen.HOME
        }
    }

    fun updateSearchQuery(query: String) {
        _filterState.value = _filterState.value.copy(query = query)
    }

    fun selectCategory(category: String) {
        val current = _filterState.value.selectedCategory
        _filterState.value = _filterState.value.copy(
            selectedCategory = if (current == category) "All" else category
        )
    }

    fun selectNeighborhood(neighborhood: String) {
        val current = _filterState.value.selectedNeighborhood
        _filterState.value = _filterState.value.copy(
            selectedNeighborhood = if (current == neighborhood) "All" else neighborhood
        )
    }

    fun toggleOpenNow() {
        _filterState.value = _filterState.value.copy(onlyOpenNow = !_filterState.value.onlyOpenNow)
    }

    fun toggleSpecialOffers() {
        _filterState.value = _filterState.value.copy(onlySpecialOffers = !_filterState.value.onlySpecialOffers)
    }

    fun setMinRating(rating: Float) {
        _filterState.value = _filterState.value.copy(
            minRating = if (_filterState.value.minRating == rating) 0f else rating
        )
    }

    fun resetFilters() {
        _filterState.value = FilterState()
    }

    fun toggleSaved(businessId: Long, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleSaved(businessId, currentStatus)
        }
    }

    fun addBusiness(
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
        imageResName: String? = null,
        imageUri: String? = null
    ) {
        viewModelScope.launch {
            val user = _currentUser.value
            val isAdmin = user?.role == "Admin"
            val isOwner = user?.role == "Business Owner"
            // Admin listings are immediately verified; Business Owner listings require Admin verification!
            val isVerified = isAdmin
            val isClaimed = isOwner
            val ownerEmail = if (isOwner) user?.email else null
            val ownerName = if (isOwner) user?.fullName else null

            val entity = BusinessEntity(
                name = name.trim(),
                category = category,
                tagline = tagline.trim(),
                description = description.trim(),
                address = address.trim(),
                neighborhood = neighborhood,
                phone = phone.trim(),
                website = website.trim(),
                email = email.trim(),
                hours = hours.trim(),
                isOpenNow = true,
                priceLevel = priceLevel,
                rating = 5.0f,
                reviewCount = 1,
                isVerified = isVerified,
                isFeatured = false,
                isSaved = false,
                specialOffer = if (specialOffer.isNullOrBlank()) null else specialOffer.trim(),
                tags = tags.trim(),
                accentColorHex = "#0F766E",
                imageResName = imageResName,
                imageUri = imageUri,
                isClaimed = isClaimed,
                claimedByOwnerEmail = ownerEmail,
                claimedByOwnerName = ownerName
            )
            val newId = repository.insertBusiness(entity)
            // Add a welcome review from the community
            repository.addReview(
                ReviewEntity(
                    businessId = newId,
                    authorName = "TownSpot Welcome Team",
                    rating = 5,
                    comment = "Welcome to the TownSpot community! Excited to have $name listed in our local directory.",
                    dateString = "Just now",
                    helpfulCount = 1
                )
            )
            if (isOwner) {
                _claimMessage.value = "Business listed! It is pending admin verification and will appear to all residents once approved."
                _currentScreen.value = AppScreen.MY_LISTING
            } else {
                // Navigate to view the newly created business!
                navigateToDetail(newId)
            }
        }
    }

    fun addReview(businessId: Long, authorName: String, rating: Int, comment: String) {
        viewModelScope.launch {
            repository.addReview(
                ReviewEntity(
                    businessId = businessId,
                    authorName = authorName.ifBlank { _currentUser.value?.fullName ?: "Local Resident" },
                    rating = rating,
                    comment = comment.trim(),
                    dateString = "Just now",
                    helpfulCount = 0
                )
            )
        }
    }

    fun markReviewHelpful(reviewId: Long) {
        viewModelScope.launch {
            repository.incrementHelpful(reviewId)
        }
    }

    // --- Admin Operations ---
    fun toggleVerified(businessId: Long, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleVerified(businessId, currentStatus)
        }
    }

    fun toggleFeatured(businessId: Long, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleFeatured(businessId, currentStatus)
        }
    }

    fun toggleOpenStatus(businessId: Long, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleOpenStatus(businessId, currentStatus)
        }
    }

    fun deleteBusiness(businessId: Long) {
        viewModelScope.launch {
            repository.deleteBusinessById(businessId)
            if (_selectedBusinessId.value == businessId) {
                _selectedBusinessId.value = null
            }
        }
    }

    fun updateBusinessDetails(business: BusinessEntity) {
        viewModelScope.launch {
            repository.updateBusiness(business)
        }
    }

    // --- Claim Operations ---
    private val _claimMessage = MutableStateFlow<String?>(null)
    val claimMessage: StateFlow<String?> = _claimMessage.asStateFlow()

    fun clearClaimMessage() {
        _claimMessage.value = null
    }

    fun claimBusiness(businessId: Long) {
        claimBusiness(businessId) { _, _ -> }
    }

    fun claimBusiness(
        businessId: Long,
        onResult: (success: Boolean, message: String) -> Unit = { _, _ -> }
    ) {
        viewModelScope.launch {
            val user = _currentUser.value
            if (user == null) {
                val msg = "Please sign in to claim a business listing."
                _claimMessage.value = msg
                onResult(false, msg)
                return@launch
            }
            if (user.role == "Resident") {
                val msg = "Residents cannot claim listings. Only verified business owners can claim."
                _claimMessage.value = msg
                onResult(false, msg)
                return@launch
            }

            val targetBusiness = allBusinesses.value.find { it.id == businessId }
            if (targetBusiness == null) {
                val msg = "Listing not found."
                _claimMessage.value = msg
                onResult(false, msg)
                return@launch
            }

            if (targetBusiness.isClaimed) {
                val msg = if (targetBusiness.claimedByOwnerEmail.equals(user.email, ignoreCase = true)) {
                    "You already manage this listing."
                } else {
                    "This listing is already claimed by another owner. Business owners cannot claim other listings."
                }
                _claimMessage.value = msg
                onResult(false, msg)
                return@launch
            }

            // Check if current business owner already has another claimed listing
            val alreadyOwned = allBusinesses.value.firstOrNull {
                it.claimedByOwnerEmail.equals(user.email, ignoreCase = true)
            }
            if (alreadyOwned != null && alreadyOwned.id != businessId) {
                val msg = "You already manage \"${alreadyOwned.name}\". Business owners cannot claim other listings."
                _claimMessage.value = msg
                onResult(false, msg)
                return@launch
            }

            // Successfully claim
            repository.claimBusiness(businessId, user.email, user.fullName)
            val msg = "Successfully claimed \"${targetBusiness.name}\"! You are now the verified owner."
            _claimMessage.value = msg
            onResult(true, msg)
        }
    }

    // --- Promotions Operations ---
    fun postPromotion(
        businessId: Long,
        title: String,
        discountDescription: String,
        promoCode: String?,
        terms: String,
        validUntil: String,
        onResult: (success: Boolean, message: String) -> Unit = { _, _ -> }
    ) {
        viewModelScope.launch {
            val user = _currentUser.value
            if (user == null || (user.role != "Business Owner" && user.role != "Admin")) {
                onResult(false, "Only business owners can post promotions.")
                return@launch
            }
            if (title.isBlank() || discountDescription.isBlank()) {
                onResult(false, "Please provide both an offer title and a discount summary.")
                return@launch
            }
            val targetBusiness = allBusinesses.value.find { it.id == businessId }
            if (targetBusiness == null) {
                onResult(false, "Selected business listing not found.")
                return@launch
            }

            val promo = PromotionEntity(
                businessId = targetBusiness.id,
                businessName = targetBusiness.name,
                category = targetBusiness.category,
                title = title.trim(),
                discountDescription = discountDescription.trim(),
                promoCode = if (promoCode.isNullOrBlank()) null else promoCode.trim().uppercase(),
                terms = if (terms.isBlank()) "Present this offer in-store to redeem." else terms.trim(),
                validUntil = if (validUntil.isBlank()) "Limited time community offer" else validUntil.trim(),
                imageResName = targetBusiness.imageResName,
                postedByOwnerEmail = user.email
            )
            repository.insertPromotion(promo)
            onResult(true, "Promotion posted successfully for ${targetBusiness.name}!")
        }
    }

    fun deletePromotion(promotionId: Long) {
        viewModelScope.launch {
            repository.deletePromotion(promotionId)
        }
    }

    fun deleteReview(reviewId: Long) {
        viewModelScope.launch {
            repository.deleteReview(reviewId)
            // Refresh current business reviews if on detail
            _selectedBusinessId.value?.let { bizId ->
                repository.getReviewsForBusiness(bizId).collect { reviews ->
                    _currentReviews.value = reviews
                }
            }
        }
    }

    fun resetToDefaultSeed() {
        viewModelScope.launch {
            repository.resetToDefaultSeed()
        }
    }

    fun addSampleMerchant() {
        val sampleNames = listOf("Velvet Bean Roastery", "Riverside Gelato Bar", "Foundry Bicycle Atelier", "Zenith Pilates Loft")
        val randomName = sampleNames.random() + " " + (10..99).random()
        addBusiness(
            name = randomName,
            category = "Cafes & Bakeries",
            tagline = "Handcrafted local delicacies & community workspace",
            description = "A newly curated local merchant added via the TownSpot Admin Operations Console.",
            address = "500 Civic Center Boulevard",
            neighborhood = "Downtown",
            phone = "(555) 901-4433",
            website = "https://townspot.local",
            email = "contact@townspot.local",
            hours = "Daily: 8:00 AM - 7:00 PM",
            priceLevel = "$$",
            specialOffer = "Special Welcome Offer: 20% off your first community order.",
            tags = "Free WiFi, Pet Friendly, Wheelchair Accessible",
            imageResName = "biz_cafe",
            imageUri = null
        )
    }

    // --- Category & Neighborhood Operations ---
    fun addCategory(name: String, iconName: String = "storefront", description: String = "") {
        val cleanName = name.trim()
        if (cleanName.isBlank()) return
        viewModelScope.launch {
            repository.addCategory(cleanName, iconName, description.trim())
        }
    }

    fun deleteCategory(id: Long) {
        viewModelScope.launch {
            repository.deleteCategory(id)
        }
    }

    fun addNeighborhood(name: String, description: String = "") {
        val cleanName = name.trim()
        if (cleanName.isBlank()) return
        viewModelScope.launch {
            repository.addNeighborhood(cleanName, description.trim())
        }
    }

    fun deleteNeighborhood(id: Long) {
        viewModelScope.launch {
            repository.deleteNeighborhood(id)
        }
    }

    fun resetCategoriesAndNeighborhoods() {
        viewModelScope.launch {
            repository.resetCategoriesAndNeighborhoods()
        }
    }

    // --- Authentication Operations ---
    fun clearAuthError() {
        _authError.value = null
    }

    fun signIn(email: String, pass: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authError.value = null
            val cleanEmail = email.trim()
            val cleanPass = pass.trim()
            if (cleanEmail.isBlank() || cleanPass.isBlank()) {
                _authError.value = "Please enter both email and password."
                _isAuthLoading.value = false
                return@launch
            }
            val user = repository.findUserByEmail(cleanEmail)
            if (user == null) {
                _authError.value = "Account not found for $cleanEmail. Please register below."
            } else if (user.passwordHash != cleanPass) {
                _authError.value = "Incorrect password. Please try again."
            } else {
                _currentUser.value = user
                onSuccess()
            }
            _isAuthLoading.value = false
        }
    }

    fun register(
        fullName: String,
        email: String,
        pass: String,
        role: String,
        businessName: String? = null,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authError.value = null
            val cleanName = fullName.trim()
            val cleanEmail = email.trim().lowercase()
            val cleanPass = pass.trim()

            if (cleanName.isBlank() || cleanEmail.isBlank() || cleanPass.isBlank()) {
                _authError.value = "All fields are required to register."
                _isAuthLoading.value = false
                return@launch
            }

            if (!cleanEmail.contains("@") || !cleanEmail.contains(".")) {
                _authError.value = "Please enter a valid email address."
                _isAuthLoading.value = false
                return@launch
            }

            if (cleanPass.length < 4) {
                _authError.value = "Password must be at least 4 characters."
                _isAuthLoading.value = false
                return@launch
            }

            val existing = repository.findUserByEmail(cleanEmail)
            if (existing != null) {
                _authError.value = "An account with this email already exists. Please sign in."
                _isAuthLoading.value = false
                return@launch
            }

            val newUser = UserEntity(
                email = cleanEmail,
                fullName = cleanName,
                passwordHash = cleanPass,
                role = role,
                businessName = if (role == "Business Owner") businessName?.trim() else null
            )
            val newId = repository.registerUser(newUser)
            val registeredUser = newUser.copy(id = newId)
            _currentUser.value = registeredUser
            onSuccess()
            _isAuthLoading.value = false
        }
    }

    fun quickSignIn(role: String) {
        // Demo feature access removed. Users must sign in with their registered credentials.
    }

    fun signOut() {
        _currentUser.value = null
        _authError.value = null
    }

    // --- Supabase Cloud Sync Operations ---
    val syncState: StateFlow<SyncUiState> = repository.supabaseSyncManager?.syncState
        ?: MutableStateFlow(SyncUiState()).asStateFlow()

    val isAutoPushEnabled: StateFlow<Boolean> = repository.isAutoPushEnabled
        ?: MutableStateFlow(true).asStateFlow()

    fun setAutoPushEnabled(enabled: Boolean) {
        repository.setAutoPushEnabled(enabled)
    }

    fun syncWithSupabase(onComplete: (SyncResult) -> Unit = {}) {
        viewModelScope.launch {
            val result = repository.syncWithSupabase()
            if (result != null) {
                onComplete(result)
            } else {
                onComplete(SyncResult(false, "Supabase sync manager is not available."))
            }
        }
    }

    fun pushAllDataToSupabase(onComplete: (SyncResult) -> Unit = {}) {
        viewModelScope.launch {
            val result = repository.pushAllDataToSupabase()
            if (result != null) {
                onComplete(result)
            } else {
                onComplete(SyncResult(false, "Supabase sync manager is not available."))
            }
        }
    }

    fun testSupabaseConnection(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val client = repository.supabaseSyncManager?.client
            if (client == null) {
                onResult(false, "Supabase client not initialized.")
                return@launch
            }
            val res = client.testConnection()
            res.fold(
                onSuccess = { msg -> onResult(true, msg) },
                onFailure = { err -> onResult(false, err.message ?: "Connection test failed") }
            )
        }
    }

    fun updateSupabaseConfig(url: String, key: String) {
        repository.supabaseSyncManager?.client?.updateCredentials(url, key)
        // Automatically push all data to newly configured Supabase
        viewModelScope.launch {
            repository.pushAllDataToSupabase()
        }
    }

    fun resetSupabaseConfig() {
        repository.supabaseSyncManager?.client?.resetToDefaults()
        viewModelScope.launch {
            if (repository.supabaseSyncManager?.isConfigured() == true) {
                repository.pushAllDataToSupabase()
            }
        }
    }

    fun getSupabaseUrl(): String = repository.supabaseSyncManager?.client?.currentUrl ?: ""
    fun getSupabaseMaskedKey(): String = repository.supabaseSyncManager?.client?.getMaskedKey() ?: ""
    fun isSupabaseConfigured(): Boolean = repository.supabaseSyncManager?.client?.isConfigured() ?: false
}

