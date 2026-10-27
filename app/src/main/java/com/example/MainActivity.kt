package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AddBusiness
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.LocationCity
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import com.example.util.ProvideSafeTextToolbar
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.database.TownSpotDatabase
import com.example.data.remote.supabase.SupabaseClient
import com.example.data.remote.supabase.SupabaseSyncManager
import com.example.data.repository.BusinessRepository
import com.example.data.seed.DatabaseInitializer
import com.example.ui.components.BuListioBottomBar
import com.example.ui.screens.AddBusinessScreen
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.BusinessDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.NeighborhoodsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.PromotionsScreen
import com.example.ui.screens.MyListingScreen
import com.example.ui.screens.MyPromotionsScreen
import com.example.ui.screens.SavedScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.TownSpotViewModel
import com.example.ui.viewmodel.TownSpotViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: TownSpotViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Google Mobile Ads / AdSense SDK
        com.example.util.AdSenseManager.initialize(applicationContext)

        val database = TownSpotDatabase.getDatabase(applicationContext)
        val supabaseClient = SupabaseClient(applicationContext)
        val supabaseSyncManager = SupabaseSyncManager(
            client = supabaseClient,
            businessDao = database.businessDao(),
            reviewDao = database.reviewDao(),
            promotionDao = database.promotionDao(),
            userDao = database.userDao()
        )
        val repository = BusinessRepository(
            businessDao = database.businessDao(),
            reviewDao = database.reviewDao(),
            userDao = database.userDao(),
            promotionDao = database.promotionDao(),
            categoryDao = database.categoryDao(),
            neighborhoodDao = database.neighborhoodDao(),
            supabaseSyncManager = supabaseSyncManager
        )
        val factory = TownSpotViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[TownSpotViewModel::class.java]

        setContent {
            MyApplicationTheme {
                // Ensure initial seed runs asynchronously on IO dispatcher
                LaunchedEffect(Unit) {
                    withContext(Dispatchers.IO) {
                        DatabaseInitializer.seedIfEmpty(
                            database.businessDao(),
                            database.reviewDao(),
                            database.userDao(),
                            database.promotionDao(),
                            database.categoryDao(),
                            database.neighborhoodDao()
                        )
                        // Automatically push all local data to Supabase if configured & auto-push is enabled
                        repository.pushAllDataToSupabase()
                    }
                }

                ProvideSafeTextToolbar {
                    TownSpotApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun TownSpotApp(viewModel: TownSpotViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val allBusinesses by viewModel.allBusinesses.collectAsStateWithLifecycle()
    val filteredBusinesses by viewModel.filteredBusinesses.collectAsStateWithLifecycle()
    val featuredBusinesses by viewModel.featuredBusinesses.collectAsStateWithLifecycle()
    val savedBusinesses by viewModel.savedBusinesses.collectAsStateWithLifecycle()
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()
    val currentBusiness by viewModel.currentBusiness.collectAsStateWithLifecycle()
    val currentReviews by viewModel.currentReviews.collectAsStateWithLifecycle()
    val allReviews by viewModel.allReviews.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val authError by viewModel.authError.collectAsStateWithLifecycle()
    val isAuthLoading by viewModel.isAuthLoading.collectAsStateWithLifecycle()
    val allPromotions by viewModel.allPromotions.collectAsStateWithLifecycle()
    val allCategories by viewModel.allCategories.collectAsStateWithLifecycle()
    val allNeighborhoods by viewModel.allNeighborhoods.collectAsStateWithLifecycle()
    val categoryNames by viewModel.categoryNames.collectAsStateWithLifecycle()
    val neighborhoodNames by viewModel.neighborhoodNames.collectAsStateWithLifecycle()
    val claimMessage by viewModel.claimMessage.collectAsStateWithLifecycle()
    val syncState by viewModel.syncState.collectAsStateWithLifecycle()
    val isAutoPushEnabled by viewModel.isAutoPushEnabled.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(claimMessage) {
        claimMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            viewModel.clearClaimMessage()
        }
    }

    val showBottomNav = currentScreen != AppScreen.DETAIL && currentScreen != AppScreen.LOGIN

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = androidx.compose.ui.graphics.Color.White,
        bottomBar = {
            if (showBottomNav) {
                BuListioBottomBar(
                    currentScreen = currentScreen,
                    userRole = currentUser?.role,
                    onNavigate = viewModel::navigateTo
                )
            }
        },
        floatingActionButton = {
            val canAddSpot = currentUser?.role == "Business Owner" || currentUser?.role == "Admin"
            AnimatedVisibility(
                visible = currentScreen == AppScreen.HOME && canAddSpot,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FloatingActionButton(
                    onClick = { viewModel.navigateTo(AppScreen.ADD_BUSINESS) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("add_business_fab")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "List Your Business",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.HOME -> {
                    HomeScreen(
                        businesses = filteredBusinesses,
                        featuredBusinesses = featuredBusinesses,
                        savedCount = savedBusinesses.size,
                        filterState = filterState,
                        onSearchChange = viewModel::updateSearchQuery,
                        onCategorySelect = viewModel::selectCategory,
                        onNeighborhoodSelect = viewModel::selectNeighborhood,
                        onToggleOpenNow = viewModel::toggleOpenNow,
                        onToggleSpecialOffers = viewModel::toggleSpecialOffers,
                        onSetMinRating = viewModel::setMinRating,
                        onResetFilters = viewModel::resetFilters,
                        onSelectBusiness = viewModel::navigateToDetail,
                        onToggleSaved = viewModel::toggleSaved,
                        onOpenSaved = { viewModel.navigateTo(AppScreen.SAVED) },
                        onOpenNeighborhoods = { viewModel.navigateTo(AppScreen.NEIGHBORHOODS) },
                        onOpenAddBusiness = { viewModel.navigateTo(AppScreen.ADD_BUSINESS) },
                        onOpenAdmin = { viewModel.navigateTo(AppScreen.ADMIN) },
                        onOpenProfile = { viewModel.navigateTo(AppScreen.PROFILE) },
                        onOpenLogin = { viewModel.navigateTo(AppScreen.LOGIN) },
                        currentUser = currentUser,
                        authError = authError,
                        isAuthLoading = isAuthLoading,
                        onSignIn = viewModel::signIn,
                        onRegister = viewModel::register,
                        onSignOut = viewModel::signOut,
                        onClearAuthError = viewModel::clearAuthError,
                        availableCategories = categoryNames,
                        availableNeighborhoods = neighborhoodNames
                    )
                }

                AppScreen.PROFILE -> {
                    ProfileScreen(
                        currentUser = currentUser,
                        allBusinesses = allBusinesses,
                        promotions = allPromotions,
                        savedCount = savedBusinesses.size,
                        reviews = allReviews,
                        authError = authError,
                        isAuthLoading = isAuthLoading,
                        syncState = syncState,
                        isSupabaseConfigured = viewModel.isSupabaseConfigured(),
                        onSyncWithSupabase = viewModel::syncWithSupabase,
                        onSignIn = viewModel::signIn,
                        onRegister = viewModel::register,
                        onSignOut = viewModel::signOut,
                        onClearAuthError = viewModel::clearAuthError,
                        onOpenSaved = { viewModel.navigateTo(AppScreen.SAVED) },
                        onOpenAddBusiness = { viewModel.navigateTo(AppScreen.ADD_BUSINESS) },
                        onOpenAdmin = { viewModel.navigateTo(AppScreen.ADMIN) },
                        onOpenMyListing = { viewModel.navigateTo(AppScreen.MY_LISTING) },
                        onOpenMyPromotions = { viewModel.navigateTo(AppScreen.MY_PROMOTIONS) },
                        onSelectBusiness = viewModel::navigateToDetail,
                        onToggleSaved = viewModel::toggleSaved,
                        onPostPromotion = viewModel::postPromotion,
                        onDeletePromotion = viewModel::deletePromotion,
                        onBack = viewModel::navigateBack,
                        onOpenLogin = { viewModel.navigateTo(AppScreen.LOGIN) }
                    )
                }

                AppScreen.PROMOTIONS -> {
                    PromotionsScreen(
                        promotions = allPromotions,
                        onSelectBusiness = viewModel::navigateToDetail,
                        onBack = viewModel::navigateBack
                    )
                }

                AppScreen.MY_LISTING -> {
                    MyListingScreen(
                        currentUser = currentUser,
                        allBusinesses = allBusinesses,
                        onSelectBusiness = viewModel::navigateToDetail,
                        onUpdateBusiness = viewModel::updateBusinessDetails,
                        onOpenAddBusiness = { viewModel.navigateTo(AppScreen.ADD_BUSINESS) },
                        onBack = viewModel::navigateBack
                    )
                }

                AppScreen.MY_PROMOTIONS -> {
                    MyPromotionsScreen(
                        currentUser = currentUser,
                        allBusinesses = allBusinesses,
                        promotions = allPromotions,
                        onSelectBusiness = viewModel::navigateToDetail,
                        onPostPromotion = viewModel::postPromotion,
                        onDeletePromotion = viewModel::deletePromotion,
                        onOpenAddBusiness = { viewModel.navigateTo(AppScreen.ADD_BUSINESS) },
                        onBack = viewModel::navigateBack
                    )
                }

                AppScreen.ADMIN -> {
                    AdminScreen(
                        businesses = allBusinesses,
                        reviews = allReviews,
                        categories = allCategories,
                        neighborhoods = allNeighborhoods,
                        onToggleVerified = viewModel::toggleVerified,
                        onToggleFeatured = viewModel::toggleFeatured,
                        onToggleOpenStatus = viewModel::toggleOpenStatus,
                        onDeleteBusiness = viewModel::deleteBusiness,
                        onUpdateBusiness = viewModel::updateBusinessDetails,
                        onDeleteReview = viewModel::deleteReview,
                        onAddCategory = viewModel::addCategory,
                        onDeleteCategory = viewModel::deleteCategory,
                        onAddNeighborhood = viewModel::addNeighborhood,
                        onDeleteNeighborhood = viewModel::deleteNeighborhood,
                        onResetTaxonomy = viewModel::resetCategoriesAndNeighborhoods,
                        onResetDatabase = viewModel::resetToDefaultSeed,
                        onAddSampleBusiness = viewModel::addSampleMerchant,
                        onSelectBusiness = viewModel::navigateToDetail,
                        onBack = viewModel::navigateBack,
                        syncState = syncState,
                        supabaseUrl = viewModel.getSupabaseUrl(),
                        supabaseMaskedKey = viewModel.getSupabaseMaskedKey(),
                        isSupabaseConfigured = viewModel.isSupabaseConfigured(),
                        isAutoPushEnabled = isAutoPushEnabled,
                        onToggleAutoPush = viewModel::setAutoPushEnabled,
                        onSyncWithSupabase = viewModel::syncWithSupabase,
                        onPushAllDataToSupabase = viewModel::pushAllDataToSupabase,
                        onTestConnection = viewModel::testSupabaseConnection,
                        onUpdateSupabaseConfig = viewModel::updateSupabaseConfig,
                        onResetSupabaseConfig = viewModel::resetSupabaseConfig
                    )
                }

                AppScreen.DETAIL -> {
                    BusinessDetailScreen(
                        business = currentBusiness,
                        reviews = currentReviews,
                        currentUser = currentUser,
                        promotions = allPromotions,
                        onBack = viewModel::navigateBack,
                        onToggleSaved = viewModel::toggleSaved,
                        onAddReview = viewModel::addReview,
                        onHelpfulClicked = viewModel::markReviewHelpful,
                        onClaimBusiness = viewModel::claimBusiness,
                        onPostPromotion = viewModel::postPromotion
                    )
                }

                AppScreen.ADD_BUSINESS -> {
                    AddBusinessScreen(
                        onBack = viewModel::navigateBack,
                        availableCategories = categoryNames,
                        availableNeighborhoods = neighborhoodNames,
                        onSubmit = { name, category, tagline, description, address, neighborhood, phone, website, email, hours, priceLevel, specialOffer, tags, imageResName, imageUri ->
                            viewModel.addBusiness(
                                name = name,
                                category = category,
                                tagline = tagline,
                                description = description,
                                address = address,
                                neighborhood = neighborhood,
                                phone = phone,
                                website = website,
                                email = email,
                                hours = hours,
                                priceLevel = priceLevel,
                                specialOffer = specialOffer,
                                tags = tags,
                                imageResName = imageResName,
                                imageUri = imageUri
                            )
                        }
                    )
                }

                AppScreen.SAVED -> {
                    SavedScreen(
                        savedBusinesses = savedBusinesses,
                        onSelectBusiness = viewModel::navigateToDetail,
                        onToggleSaved = viewModel::toggleSaved,
                        onBack = viewModel::navigateBack
                    )
                }

                AppScreen.NEIGHBORHOODS -> {
                    NeighborhoodsScreen(
                        allBusinesses = allBusinesses,
                        businesses = allBusinesses,
                        currentUser = currentUser,
                        availableCategories = categoryNames,
                        onSelectBusiness = viewModel::navigateToDetail,
                        onToggleSaved = viewModel::toggleSaved,
                        onClaimBusiness = viewModel::claimBusiness,
                        onSelectNeighborhood = { neighborhood ->
                            viewModel.selectNeighborhood(neighborhood)
                            viewModel.navigateTo(AppScreen.HOME)
                        },
                        onBack = viewModel::navigateBack
                    )
                }

                AppScreen.LOGIN -> {
                    LoginScreen(
                        currentUser = currentUser,
                        authError = authError,
                        isLoading = isAuthLoading,
                        onSignIn = viewModel::signIn,
                        onRegister = viewModel::register,
                        onClearError = viewModel::clearAuthError,
                        onNavigateBack = viewModel::navigateBack,
                        onSuccessLogin = { viewModel.navigateTo(AppScreen.PROFILE) }
                    )
                }
            }
        }
    }
}
