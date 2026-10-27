package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BusinessEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BusinessDao {
    @Query("SELECT * FROM businesses ORDER BY isFeatured DESC, rating DESC, name ASC")
    fun getAllBusinesses(): Flow<List<BusinessEntity>>

    @Query("SELECT * FROM businesses WHERE id = :id")
    fun getBusinessById(id: Long): Flow<BusinessEntity?>

    @Query("SELECT * FROM businesses WHERE id = :id")
    suspend fun getBusinessByIdDirect(id: Long): BusinessEntity?

    @Query("SELECT * FROM businesses WHERE isSaved = 1 ORDER BY name ASC")
    fun getSavedBusinesses(): Flow<List<BusinessEntity>>

    @Query("SELECT * FROM businesses WHERE isFeatured = 1 ORDER BY rating DESC")
    fun getFeaturedBusinesses(): Flow<List<BusinessEntity>>

    @Query("""
        SELECT * FROM businesses 
        WHERE name LIKE '%' || :query || '%' 
           OR category LIKE '%' || :query || '%' 
           OR neighborhood LIKE '%' || :query || '%'
           OR tags LIKE '%' || :query || '%'
           OR description LIKE '%' || :query || '%'
        ORDER BY rating DESC, name ASC
    """)
    fun searchBusinesses(query: String): Flow<List<BusinessEntity>>

    @Query("SELECT * FROM businesses WHERE category = :category ORDER BY rating DESC")
    fun getBusinessesByCategory(category: String): Flow<List<BusinessEntity>>

    @Query("SELECT * FROM businesses WHERE neighborhood = :neighborhood ORDER BY rating DESC")
    fun getBusinessesByNeighborhood(neighborhood: String): Flow<List<BusinessEntity>>

    @Query("SELECT COUNT(*) FROM businesses")
    suspend fun getBusinessCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusiness(business: BusinessEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(businesses: List<BusinessEntity>)

    @Update
    suspend fun updateBusiness(business: BusinessEntity)

    @Query("UPDATE businesses SET isSaved = :isSaved WHERE id = :id")
    suspend fun updateSavedStatus(id: Long, isSaved: Boolean)

    @Query("UPDATE businesses SET isVerified = :isVerified WHERE id = :id")
    suspend fun updateVerifiedStatus(id: Long, isVerified: Boolean)

    @Query("UPDATE businesses SET isFeatured = :isFeatured WHERE id = :id")
    suspend fun updateFeaturedStatus(id: Long, isFeatured: Boolean)

    @Query("UPDATE businesses SET isOpenNow = :isOpenNow WHERE id = :id")
    suspend fun updateOpenStatus(id: Long, isOpenNow: Boolean)

    @Query("UPDATE businesses SET isClaimed = :isClaimed, claimedByOwnerEmail = :ownerEmail, claimedByOwnerName = :ownerName WHERE id = :id")
    suspend fun updateClaimStatus(id: Long, isClaimed: Boolean, ownerEmail: String?, ownerName: String?)

    @Query("UPDATE businesses SET specialOffer = :offer WHERE id = :id")
    suspend fun updateSpecialOffer(id: Long, offer: String?)

    @Query("SELECT * FROM businesses WHERE claimedByOwnerEmail = :ownerEmail ORDER BY name ASC")
    fun getBusinessesByOwner(ownerEmail: String): Flow<List<BusinessEntity>>

    @Query("DELETE FROM businesses WHERE id = :id")
    suspend fun deleteBusinessById(id: Long)

    @Query("DELETE FROM businesses")
    suspend fun deleteAllBusinesses()

    @Delete
    suspend fun deleteBusiness(business: BusinessEntity)

    @Query("""
        UPDATE businesses 
        SET rating = ((rating * reviewCount) + :newRating) / (reviewCount + 1),
            reviewCount = reviewCount + 1
        WHERE id = :businessId
    """)
    suspend fun recalculateRating(businessId: Long, newRating: Float)
}
