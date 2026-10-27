package com.example.data.remote.supabase

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseApi {

    // --- Health Check ---
    @GET("rest/v1/")
    suspend fun ping(): Response<ResponseBody>

    // --- Businesses (PostgREST) ---
    @GET("rest/v1/businesses")
    suspend fun getBusinesses(
        @Query("select") select: String = "*"
    ): List<SupabaseBusinessDto>

    @POST("rest/v1/businesses")
    @Headers("Prefer: return=representation, resolution=merge-duplicates")
    suspend fun upsertBusinesses(
        @Body businesses: List<SupabaseBusinessDto>
    ): List<SupabaseBusinessDto>

    @POST("rest/v1/businesses")
    @Headers("Prefer: return=representation, resolution=merge-duplicates")
    suspend fun upsertSingleBusiness(
        @Body business: SupabaseBusinessDto
    ): List<SupabaseBusinessDto>

    @PATCH("rest/v1/businesses")
    @Headers("Prefer: return=representation")
    suspend fun updateBusiness(
        @Query("id") idFilter: String,
        @Body updates: Map<String, @JvmSuppressWildcards Any?>
    ): Response<ResponseBody>

    @DELETE("rest/v1/businesses")
    suspend fun deleteBusiness(
        @Query("id") idFilter: String
    ): Response<ResponseBody>

    // --- Reviews ---
    @GET("rest/v1/reviews")
    suspend fun getReviews(
        @Query("select") select: String = "*"
    ): List<SupabaseReviewDto>

    @POST("rest/v1/reviews")
    @Headers("Prefer: return=representation")
    suspend fun insertReview(
        @Body review: SupabaseReviewDto
    ): List<SupabaseReviewDto>

    @POST("rest/v1/reviews")
    @Headers("Prefer: return=representation, resolution=merge-duplicates")
    suspend fun upsertReviews(
        @Body reviews: List<SupabaseReviewDto>
    ): List<SupabaseReviewDto>

    @DELETE("rest/v1/reviews")
    suspend fun deleteReview(
        @Query("id") idFilter: String
    ): Response<ResponseBody>

    // --- Promotions ---
    @GET("rest/v1/promotions")
    suspend fun getPromotions(
        @Query("select") select: String = "*"
    ): List<SupabasePromotionDto>

    @POST("rest/v1/promotions")
    @Headers("Prefer: return=representation")
    suspend fun insertPromotion(
        @Body promo: SupabasePromotionDto
    ): List<SupabasePromotionDto>

    @POST("rest/v1/promotions")
    @Headers("Prefer: return=representation, resolution=merge-duplicates")
    suspend fun upsertPromotions(
        @Body promos: List<SupabasePromotionDto>
    ): List<SupabasePromotionDto>

    @DELETE("rest/v1/promotions")
    suspend fun deletePromotion(
        @Query("id") idFilter: String
    ): Response<ResponseBody>

    // --- Users ---
    @GET("rest/v1/users")
    suspend fun getUsers(
        @Query("select") select: String = "*"
    ): List<SupabaseUserDto>

    @POST("rest/v1/users")
    @Headers("Prefer: return=representation, resolution=merge-duplicates")
    suspend fun upsertUser(
        @Body user: SupabaseUserDto
    ): List<SupabaseUserDto>

    // --- GoTrue Auth ---
    @POST("auth/v1/signup")
    suspend fun signUp(
        @Body req: SupabaseAuthRequest
    ): Response<SupabaseAuthResponse>

    @POST("auth/v1/token?grant_type=password")
    suspend fun signIn(
        @Body req: SupabaseAuthRequest
    ): Response<SupabaseAuthResponse>
}
