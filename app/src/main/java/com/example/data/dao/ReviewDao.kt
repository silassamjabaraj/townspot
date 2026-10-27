package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.ReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE businessId = :businessId ORDER BY id DESC")
    fun getReviewsForBusiness(businessId: Long): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews ORDER BY id DESC")
    fun getAllReviews(): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reviews: List<ReviewEntity>)

    @Query("UPDATE reviews SET helpfulCount = helpfulCount + 1 WHERE id = :reviewId")
    suspend fun incrementHelpful(reviewId: Long)

    @Query("SELECT COUNT(*) FROM reviews WHERE businessId = :businessId")
    suspend fun getReviewCountForBusiness(businessId: Long): Int

    @Query("DELETE FROM reviews WHERE id = :id")
    suspend fun deleteReviewById(id: Long)

    @Query("DELETE FROM reviews WHERE businessId = :businessId")
    suspend fun deleteReviewsForBusiness(businessId: Long)

    @Query("DELETE FROM reviews")
    suspend fun deleteAllReviews()
}
