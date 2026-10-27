package com.example.data.repository

import com.example.data.dao.BusinessDao
import com.example.data.dao.CategoryDao
import com.example.data.dao.NeighborhoodDao
import com.example.data.dao.PromotionDao
import com.example.data.dao.ReviewDao
import com.example.data.dao.UserDao
import com.example.data.model.BusinessEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.NeighborhoodEntity
import com.example.data.model.PromotionEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.UserEntity
import com.example.data.remote.supabase.SupabaseSyncManager
import com.example.data.remote.supabase.SyncResult
import com.example.data.seed.DatabaseInitializer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class BusinessRepository(
    private val businessDao: BusinessDao,
    private val reviewDao: ReviewDao,
    private val userDao: UserDao? = null,
    private val promotionDao: PromotionDao? = null,
    private val categoryDao: CategoryDao? = null,
    private val neighborhoodDao: NeighborhoodDao? = null,
    val supabaseSyncManager: SupabaseSyncManager? = null
) {
    val allBusinesses: Flow<List<BusinessEntity>> = businessDao.getAllBusinesses()
    val savedBusinesses: Flow<List<BusinessEntity>> = businessDao.getSavedBusinesses()
    val featuredBusinesses: Flow<List<BusinessEntity>> = businessDao.getFeaturedBusinesses()
    val allReviews: Flow<List<ReviewEntity>> = reviewDao.getAllReviews()
    val allUsers: Flow<List<UserEntity>> = userDao?.getAllUsers() ?: flowOf(emptyList())
    val allPromotions: Flow<List<PromotionEntity>> = promotionDao?.getAllPromotions() ?: flowOf(emptyList())
    val allCategories: Flow<List<CategoryEntity>> = categoryDao?.getAllCategories() ?: flowOf(emptyList())
    val allNeighborhoods: Flow<List<NeighborhoodEntity>> = neighborhoodDao?.getAllNeighborhoods() ?: flowOf(emptyList())

    suspend fun addCategory(name: String, iconName: String = "storefront", description: String = ""): Long {
        val category = CategoryEntity(name = name.trim(), iconName = iconName, description = description.trim())
        return categoryDao?.insertCategory(category) ?: 0L
    }

    suspend fun deleteCategory(id: Long) {
        categoryDao?.deleteCategoryById(id)
    }

    suspend fun addNeighborhood(name: String, description: String = ""): Long {
        val neighborhood = NeighborhoodEntity(name = name.trim(), description = description.trim())
        return neighborhoodDao?.insertNeighborhood(neighborhood) ?: 0L
    }

    suspend fun deleteNeighborhood(id: Long) {
        neighborhoodDao?.deleteNeighborhoodById(id)
    }

    suspend fun resetCategoriesAndNeighborhoods() {
        if (categoryDao != null && neighborhoodDao != null) {
            DatabaseInitializer.seedCategoriesAndNeighborhoods(categoryDao, neighborhoodDao, force = true)
        }
    }

    val isAutoPushEnabled: kotlinx.coroutines.flow.StateFlow<Boolean>? = supabaseSyncManager?.autoPushEnabled

    fun setAutoPushEnabled(enabled: Boolean) {
        supabaseSyncManager?.setAutoPushEnabled(enabled)
    }

    suspend fun syncWithSupabase(): SyncResult? = supabaseSyncManager?.syncAll()

    suspend fun pushAllDataToSupabase(): SyncResult? = supabaseSyncManager?.pushAllData()

    suspend fun findUserByEmail(email: String): UserEntity? = userDao?.getUserByEmail(email)

    suspend fun registerUser(user: UserEntity): Long {
        val id = userDao?.insertUser(user) ?: 0L
        val created = user.copy(id = id)
        supabaseSyncManager?.pushUser(created)
        return id
    }

    suspend fun getUserById(id: Long): UserEntity? = userDao?.getUserById(id)

    fun getBusinessById(id: Long): Flow<BusinessEntity?> = businessDao.getBusinessById(id)

    suspend fun getBusinessByIdDirect(id: Long): BusinessEntity? = businessDao.getBusinessByIdDirect(id)

    fun searchBusinesses(query: String): Flow<List<BusinessEntity>> = businessDao.searchBusinesses(query)

    fun getBusinessesByCategory(category: String): Flow<List<BusinessEntity>> =
        businessDao.getBusinessesByCategory(category)

    fun getBusinessesByNeighborhood(neighborhood: String): Flow<List<BusinessEntity>> =
        businessDao.getBusinessesByNeighborhood(neighborhood)

    fun getBusinessesByOwner(ownerEmail: String): Flow<List<BusinessEntity>> =
        businessDao.getBusinessesByOwner(ownerEmail)

    suspend fun insertBusiness(business: BusinessEntity): Long {
        val id = businessDao.insertBusiness(business)
        val created = business.copy(id = id)
        supabaseSyncManager?.pushBusiness(created)
        return id
    }

    suspend fun updateBusiness(business: BusinessEntity) {
        businessDao.updateBusiness(business)
        supabaseSyncManager?.pushBusiness(business)
    }

    suspend fun toggleSaved(id: Long, currentSavedStatus: Boolean) {
        businessDao.updateSavedStatus(id, !currentSavedStatus)
    }

    suspend fun toggleVerified(id: Long, currentVerifiedStatus: Boolean) {
        val newStatus = !currentVerifiedStatus
        businessDao.updateVerifiedStatus(id, newStatus)
        val updated = businessDao.getBusinessByIdDirect(id)
        if (updated != null) {
            supabaseSyncManager?.pushBusiness(updated)
        }
    }

    suspend fun toggleFeatured(id: Long, currentFeaturedStatus: Boolean) {
        businessDao.updateFeaturedStatus(id, !currentFeaturedStatus)
        val updated = businessDao.getBusinessByIdDirect(id)
        if (updated != null) {
            supabaseSyncManager?.pushBusiness(updated)
        }
    }

    suspend fun toggleOpenStatus(id: Long, currentOpenStatus: Boolean) {
        businessDao.updateOpenStatus(id, !currentOpenStatus)
    }

    suspend fun claimBusiness(id: Long, ownerEmail: String, ownerName: String) {
        businessDao.updateClaimStatus(id, isClaimed = true, ownerEmail = ownerEmail, ownerName = ownerName)
        val updated = businessDao.getBusinessByIdDirect(id)
        if (updated != null) {
            supabaseSyncManager?.pushBusiness(updated)
        }
    }

    suspend fun unclaimBusiness(id: Long) {
        businessDao.updateClaimStatus(id, isClaimed = false, ownerEmail = null, ownerName = null)
        val updated = businessDao.getBusinessByIdDirect(id)
        if (updated != null) {
            supabaseSyncManager?.pushBusiness(updated)
        }
    }

    // --- Promotions ---
    fun getPromotionsForBusiness(businessId: Long): Flow<List<PromotionEntity>> =
        promotionDao?.getPromotionsForBusiness(businessId) ?: flowOf(emptyList())

    suspend fun insertPromotion(promotion: PromotionEntity): Long {
        val id = promotionDao?.insertPromotion(promotion) ?: 0L
        val created = promotion.copy(id = id)
        // Also reflect special offer on business entity
        businessDao.updateSpecialOffer(promotion.businessId, "${promotion.discountDescription}: ${promotion.title}")
        supabaseSyncManager?.pushPromotion(created)
        return id
    }

    suspend fun deletePromotion(id: Long) {
        promotionDao?.deletePromotion(id)
        supabaseSyncManager?.deletePromotion(id)
    }

    suspend fun deleteBusiness(business: BusinessEntity) {
        businessDao.deleteBusiness(business)
        reviewDao.deleteReviewsForBusiness(business.id)
        supabaseSyncManager?.deleteBusiness(business.id)
    }

    suspend fun deleteBusinessById(id: Long) {
        businessDao.deleteBusinessById(id)
        reviewDao.deleteReviewsForBusiness(id)
        supabaseSyncManager?.deleteBusiness(id)
    }

    suspend fun deleteReview(reviewId: Long) {
        reviewDao.deleteReviewById(reviewId)
        supabaseSyncManager?.deleteReview(reviewId)
    }

    suspend fun resetToDefaultSeed() {
        businessDao.deleteAllBusinesses()
        reviewDao.deleteAllReviews()
        promotionDao?.deleteAllPromotions()
        com.example.data.seed.DatabaseInitializer.seedIfEmpty(businessDao, reviewDao, userDao, promotionDao, force = true)
        supabaseSyncManager?.pushAllData()
    }

    fun getReviewsForBusiness(businessId: Long): Flow<List<ReviewEntity>> =
        reviewDao.getReviewsForBusiness(businessId)

    suspend fun addReview(review: ReviewEntity) {
        val id = reviewDao.insertReview(review)
        businessDao.recalculateRating(review.businessId, review.rating.toFloat())
        supabaseSyncManager?.pushReview(review.copy(id = id))
    }

    suspend fun incrementHelpful(reviewId: Long) = reviewDao.incrementHelpful(reviewId)
}

