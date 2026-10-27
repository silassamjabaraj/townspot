package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.PromotionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PromotionDao {
    @Query("SELECT * FROM promotions ORDER BY createdAt DESC")
    fun getAllPromotions(): Flow<List<PromotionEntity>>

    @Query("SELECT * FROM promotions WHERE businessId = :businessId ORDER BY createdAt DESC")
    fun getPromotionsForBusiness(businessId: Long): Flow<List<PromotionEntity>>

    @Query("SELECT * FROM promotions WHERE postedByOwnerEmail = :email ORDER BY createdAt DESC")
    fun getPromotionsByOwner(email: String): Flow<List<PromotionEntity>>

    @Query("SELECT COUNT(*) FROM promotions")
    suspend fun getPromotionCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPromotion(promotion: PromotionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(promotions: List<PromotionEntity>)

    @Query("DELETE FROM promotions WHERE id = :id")
    suspend fun deletePromotion(id: Long)

    @Query("DELETE FROM promotions")
    suspend fun deleteAllPromotions()
}
