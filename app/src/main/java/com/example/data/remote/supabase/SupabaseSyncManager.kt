package com.example.data.remote.supabase

import android.util.Log
import com.example.data.dao.BusinessDao
import com.example.data.dao.PromotionDao
import com.example.data.dao.ReviewDao
import com.example.data.dao.UserDao
import com.example.data.model.BusinessEntity
import com.example.data.model.PromotionEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

data class SyncResult(
    val isSuccess: Boolean,
    val message: String,
    val businessesCount: Int = 0,
    val promotionsCount: Int = 0,
    val reviewsCount: Int = 0
)

data class SyncUiState(
    val isSyncing: Boolean = false,
    val lastSyncTime: Long? = null,
    val lastSyncMessage: String? = null,
    val isSuccess: Boolean? = null,
    val businessesCount: Int = 0,
    val promotionsCount: Int = 0
)

class SupabaseSyncManager(
    val client: SupabaseClient,
    private val businessDao: BusinessDao,
    private val reviewDao: ReviewDao,
    private val promotionDao: PromotionDao?,
    private val userDao: UserDao?
) {
    private val _syncState = MutableStateFlow(SyncUiState())
    val syncState: StateFlow<SyncUiState> = _syncState.asStateFlow()

    private val _autoPushEnabled = MutableStateFlow(client.isAutoPushEnabled)
    val autoPushEnabled: StateFlow<Boolean> = _autoPushEnabled.asStateFlow()

    val isAutoPushEnabled: Boolean
        get() = client.isAutoPushEnabled

    fun setAutoPushEnabled(enabled: Boolean) {
        client.isAutoPushEnabled = enabled
        _autoPushEnabled.value = enabled
    }

    fun isConfigured(): Boolean = client.isConfigured()

    /**
     * Automatically pushes all local Room data (businesses, reviews, promotions, users)
     * to the remote Supabase database.
     */
    suspend fun pushAllData(): SyncResult = withContext(Dispatchers.IO) {
        val api = client.getApi()
        if (api == null || !client.isConfigured()) {
            val res = SyncResult(false, "Supabase is not configured yet. Configure URL and Anon Key in Settings.")
            _syncState.value = _syncState.value.copy(
                isSyncing = false,
                lastSyncMessage = res.message,
                isSuccess = false
            )
            return@withContext res
        }

        _syncState.value = _syncState.value.copy(
            isSyncing = true,
            lastSyncMessage = "Automatically pushing all local data to Supabase..."
        )

        try {
            var bCount = 0
            var pCount = 0
            var rCount = 0
            var uCount = 0

            // 1. Push all local businesses
            val localBusinesses = businessDao.getAllBusinesses().first()
            if (localBusinesses.isNotEmpty()) {
                val toPush = localBusinesses.map { it.toSupabaseDto() }
                try {
                    api.upsertBusinesses(toPush)
                    bCount = toPush.size
                    Log.d("SupabaseSyncManager", "Batch pushed ${toPush.size} businesses to Supabase")
                } catch (e: Exception) {
                    Log.w("SupabaseSyncManager", "Batch business push notice, falling back to individual push: ${e.message}")
                    for (b in toPush) {
                        try {
                            api.upsertSingleBusiness(b)
                            bCount++
                        } catch (ie: Exception) {
                            Log.e("SupabaseSyncManager", "Failed pushing business ${b.name}: ${ie.message}")
                        }
                    }
                }
            }

            // 2. Push all local reviews
            val localReviews = reviewDao.getAllReviews().first()
            if (localReviews.isNotEmpty()) {
                val toPush = localReviews.map { it.toSupabaseDto() }
                try {
                    api.upsertReviews(toPush)
                    rCount = toPush.size
                    Log.d("SupabaseSyncManager", "Batch pushed ${toPush.size} reviews to Supabase")
                } catch (e: Exception) {
                    Log.w("SupabaseSyncManager", "Batch review push notice, falling back to individual push: ${e.message}")
                    for (rev in toPush) {
                        try {
                            api.insertReview(rev)
                            rCount++
                        } catch (ie: Exception) {
                            Log.e("SupabaseSyncManager", "Failed pushing review for biz ${rev.businessId}: ${ie.message}")
                        }
                    }
                }
            }

            // 3. Push all local promotions
            if (promotionDao != null) {
                val localPromos = promotionDao.getAllPromotions().first()
                if (localPromos.isNotEmpty()) {
                    val toPush = localPromos.map { it.toSupabaseDto() }
                    try {
                        api.upsertPromotions(toPush)
                        pCount = toPush.size
                        Log.d("SupabaseSyncManager", "Batch pushed ${toPush.size} promotions to Supabase")
                    } catch (e: Exception) {
                        Log.w("SupabaseSyncManager", "Batch promo push notice, falling back to individual push: ${e.message}")
                        for (p in toPush) {
                            try {
                                api.insertPromotion(p)
                                pCount++
                            } catch (ie: Exception) {
                                Log.e("SupabaseSyncManager", "Failed pushing promotion ${p.title}: ${ie.message}")
                            }
                        }
                    }
                }
            }

            // 4. Push all local registered users
            if (userDao != null) {
                val localUsers = userDao.getAllUsers().first()
                for (u in localUsers) {
                    try {
                        api.upsertUser(u.toSupabaseDto())
                        uCount++
                    } catch (e: Exception) {
                        Log.w("SupabaseSyncManager", "User sync notice (${u.email}): ${e.message}")
                    }
                }
            }

            val msg = "Auto-push complete! Pushed $bCount spots, $rCount reviews, $pCount deals to Supabase."
            val result = SyncResult(
                isSuccess = true,
                message = msg,
                businessesCount = bCount,
                promotionsCount = pCount,
                reviewsCount = rCount
            )

            _syncState.value = SyncUiState(
                isSyncing = false,
                lastSyncTime = System.currentTimeMillis(),
                lastSyncMessage = msg,
                isSuccess = true,
                businessesCount = bCount,
                promotionsCount = pCount
            )

            result
        } catch (e: Exception) {
            val errMsg = "Push error: ${e.localizedMessage ?: e.message}"
            Log.e("SupabaseSyncManager", errMsg, e)
            val result = SyncResult(false, errMsg)
            _syncState.value = _syncState.value.copy(
                isSyncing = false,
                lastSyncTime = System.currentTimeMillis(),
                lastSyncMessage = errMsg,
                isSuccess = false
            )
            result
        }
    }

    suspend fun syncAll(): SyncResult = withContext(Dispatchers.IO) {
        val api = client.getApi()
        if (api == null) {
            val res = SyncResult(false, "Supabase client not initialized.")
            _syncState.value = _syncState.value.copy(
                isSyncing = false,
                lastSyncMessage = res.message,
                isSuccess = false
            )
            return@withContext res
        }

        _syncState.value = _syncState.value.copy(isSyncing = true, lastSyncMessage = "Connecting to Supabase...")

        try {
            // 1. Sync Businesses
            var bCount = 0
            val localBusinesses = businessDao.getAllBusinesses().first()
            try {
                val remoteBusinesses = api.getBusinesses()
                if (remoteBusinesses.isNotEmpty()) {
                    businessDao.insertAll(remoteBusinesses.map { it.toEntity() })
                    bCount = remoteBusinesses.size
                }
                // Push local items to cloud to ensure two-way sync
                if (localBusinesses.isNotEmpty()) {
                    val toPush = localBusinesses.map { it.toSupabaseDto() }
                    api.upsertBusinesses(toPush)
                    bCount = maxOf(bCount, localBusinesses.size)
                }
            } catch (e: Exception) {
                Log.w("SupabaseSyncManager", "Businesses sync notice: ${e.message}")
            }

            // 2. Sync Promotions
            var pCount = 0
            if (promotionDao != null) {
                val localPromos = promotionDao.getAllPromotions().first()
                try {
                    val remotePromos = api.getPromotions()
                    if (remotePromos.isNotEmpty()) {
                        promotionDao.insertAll(remotePromos.map { it.toEntity() })
                        pCount = remotePromos.size
                    }
                    if (localPromos.isNotEmpty()) {
                        api.upsertPromotions(localPromos.map { it.toSupabaseDto() })
                        pCount = maxOf(pCount, localPromos.size)
                    }
                } catch (e: Exception) {
                    Log.w("SupabaseSyncManager", "Promotions sync notice: ${e.message}")
                }
            }

            // 3. Sync Reviews
            var rCount = 0
            try {
                val localReviews = reviewDao.getAllReviews().first()
                val remoteReviews = api.getReviews()
                if (remoteReviews.isNotEmpty()) {
                    reviewDao.insertAll(remoteReviews.map { it.toEntity() })
                    rCount = remoteReviews.size
                }
                if (localReviews.isNotEmpty()) {
                    api.upsertReviews(localReviews.map { it.toSupabaseDto() })
                    rCount = maxOf(rCount, localReviews.size)
                }
            } catch (e: Exception) {
                Log.w("SupabaseSyncManager", "Reviews sync notice: ${e.message}")
            }

            // 4. Sync Users
            if (userDao != null) {
                try {
                    val localUsers = userDao.getAllUsers().first()
                    localUsers.forEach { user ->
                        api.upsertUser(user.toSupabaseDto())
                    }
                } catch (e: Exception) {
                    Log.w("SupabaseSyncManager", "Users sync notice: ${e.message}")
                }
            }

            val msg = "Supabase sync complete! Synced $bCount spots, $pCount deals, $rCount reviews."
            val result = SyncResult(
                isSuccess = true,
                message = msg,
                businessesCount = bCount,
                promotionsCount = pCount,
                reviewsCount = rCount
            )

            _syncState.value = SyncUiState(
                isSyncing = false,
                lastSyncTime = System.currentTimeMillis(),
                lastSyncMessage = msg,
                isSuccess = true,
                businessesCount = bCount,
                promotionsCount = pCount
            )

            result
        } catch (e: Exception) {
            val errMsg = "Sync error: ${e.localizedMessage ?: e.message}"
            Log.e("SupabaseSyncManager", errMsg, e)
            val result = SyncResult(false, errMsg)
            _syncState.value = _syncState.value.copy(
                isSyncing = false,
                lastSyncTime = System.currentTimeMillis(),
                lastSyncMessage = errMsg,
                isSuccess = false
            )
            result
        }
    }

    suspend fun pushBusiness(business: BusinessEntity) = withContext(Dispatchers.IO) {
        try {
            client.getApi()?.upsertSingleBusiness(business.toSupabaseDto())
        } catch (e: Exception) {
            Log.w("SupabaseSyncManager", "Could not push business to Supabase: ${e.message}")
        }
    }

    suspend fun pushPromotion(promotion: PromotionEntity) = withContext(Dispatchers.IO) {
        try {
            client.getApi()?.insertPromotion(promotion.toSupabaseDto())
        } catch (e: Exception) {
            Log.w("SupabaseSyncManager", "Could not push promotion to Supabase: ${e.message}")
        }
    }

    suspend fun pushReview(review: ReviewEntity) = withContext(Dispatchers.IO) {
        try {
            client.getApi()?.insertReview(review.toSupabaseDto())
        } catch (e: Exception) {
            Log.w("SupabaseSyncManager", "Could not push review to Supabase: ${e.message}")
        }
    }

    suspend fun pushUser(user: UserEntity) = withContext(Dispatchers.IO) {
        try {
            client.getApi()?.upsertUser(user.toSupabaseDto())
        } catch (e: Exception) {
            Log.w("SupabaseSyncManager", "Could not push user to Supabase: ${e.message}")
        }
    }

    suspend fun deleteBusiness(id: Long) = withContext(Dispatchers.IO) {
        try {
            client.getApi()?.deleteBusiness("eq.$id")
        } catch (e: Exception) {
            Log.w("SupabaseSyncManager", "Could not delete business on Supabase: ${e.message}")
        }
    }

    suspend fun deletePromotion(id: Long) = withContext(Dispatchers.IO) {
        try {
            client.getApi()?.deletePromotion("eq.$id")
        } catch (e: Exception) {
            Log.w("SupabaseSyncManager", "Could not delete promotion on Supabase: ${e.message}")
        }
    }

    suspend fun deleteReview(id: Long) = withContext(Dispatchers.IO) {
        try {
            client.getApi()?.deleteReview("eq.$id")
        } catch (e: Exception) {
            Log.w("SupabaseSyncManager", "Could not delete review on Supabase: ${e.message}")
        }
    }
}
