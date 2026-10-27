package com.example.data.remote.supabase

import com.example.data.model.BusinessEntity
import com.example.data.model.PromotionEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.UserEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SupabaseBusinessDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "name") val name: String,
    @Json(name = "category") val category: String,
    @Json(name = "tagline") val tagline: String = "",
    @Json(name = "description") val description: String = "",
    @Json(name = "address") val address: String = "",
    @Json(name = "neighborhood") val neighborhood: String = "",
    @Json(name = "phone") val phone: String = "",
    @Json(name = "website") val website: String = "",
    @Json(name = "email") val email: String = "",
    @Json(name = "hours") val hours: String = "",
    @Json(name = "is_open_now") val isOpenNow: Boolean = true,
    @Json(name = "price_level") val priceLevel: String = "$$",
    @Json(name = "rating") val rating: Float = 5.0f,
    @Json(name = "review_count") val reviewCount: Int = 1,
    @Json(name = "is_verified") val isVerified: Boolean = false,
    @Json(name = "is_featured") val isFeatured: Boolean = false,
    @Json(name = "is_saved") val isSaved: Boolean = false,
    @Json(name = "latitude") val latitude: Double = 37.7749,
    @Json(name = "longitude") val longitude: Double = -122.4194,
    @Json(name = "special_offer") val specialOffer: String? = null,
    @Json(name = "tags") val tags: String = "",
    @Json(name = "category_icon_name") val categoryIconName: String = "store",
    @Json(name = "accent_color_hex") val accentColorHex: String = "#0F766E",
    @Json(name = "image_res_name") val imageResName: String? = null,
    @Json(name = "image_uri") val imageUri: String? = null,
    @Json(name = "claimed_by_owner_email") val claimedByOwnerEmail: String? = null,
    @Json(name = "claimed_by_owner_name") val claimedByOwnerName: String? = null,
    @Json(name = "is_claimed") val isClaimed: Boolean = false,
    @Json(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)

fun BusinessEntity.toSupabaseDto(): SupabaseBusinessDto = SupabaseBusinessDto(
    id = if (id > 0) id else null,
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
    isOpenNow = isOpenNow,
    priceLevel = priceLevel,
    rating = rating,
    reviewCount = reviewCount,
    isVerified = isVerified,
    isFeatured = isFeatured,
    isSaved = isSaved,
    latitude = latitude,
    longitude = longitude,
    specialOffer = specialOffer,
    tags = tags,
    categoryIconName = categoryIconName,
    accentColorHex = accentColorHex,
    imageResName = imageResName,
    imageUri = imageUri,
    claimedByOwnerEmail = claimedByOwnerEmail,
    claimedByOwnerName = claimedByOwnerName,
    isClaimed = isClaimed,
    createdAt = createdAt
)

fun SupabaseBusinessDto.toEntity(): BusinessEntity = BusinessEntity(
    id = id ?: 0L,
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
    isOpenNow = isOpenNow,
    priceLevel = priceLevel,
    rating = rating,
    reviewCount = reviewCount,
    isVerified = isVerified,
    isFeatured = isFeatured,
    isSaved = isSaved,
    latitude = latitude,
    longitude = longitude,
    specialOffer = specialOffer,
    tags = tags,
    categoryIconName = categoryIconName,
    accentColorHex = accentColorHex,
    imageResName = imageResName,
    imageUri = imageUri,
    claimedByOwnerEmail = claimedByOwnerEmail,
    claimedByOwnerName = claimedByOwnerName,
    isClaimed = isClaimed,
    createdAt = createdAt
)

@JsonClass(generateAdapter = true)
data class SupabaseReviewDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "business_id") val businessId: Long,
    @Json(name = "author_name") val authorName: String,
    @Json(name = "rating") val rating: Int,
    @Json(name = "comment") val comment: String,
    @Json(name = "date_string") val dateString: String,
    @Json(name = "helpful_count") val helpfulCount: Int = 0
)

fun ReviewEntity.toSupabaseDto(): SupabaseReviewDto = SupabaseReviewDto(
    id = if (id > 0) id else null,
    businessId = businessId,
    authorName = authorName,
    rating = rating,
    comment = comment,
    dateString = dateString,
    helpfulCount = helpfulCount
)

fun SupabaseReviewDto.toEntity(): ReviewEntity = ReviewEntity(
    id = id ?: 0L,
    businessId = businessId,
    authorName = authorName,
    rating = rating,
    comment = comment,
    dateString = dateString,
    helpfulCount = helpfulCount
)

@JsonClass(generateAdapter = true)
data class SupabasePromotionDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "business_id") val businessId: Long,
    @Json(name = "business_name") val businessName: String,
    @Json(name = "category") val category: String,
    @Json(name = "title") val title: String,
    @Json(name = "discount_description") val discountDescription: String,
    @Json(name = "promo_code") val promoCode: String? = null,
    @Json(name = "terms") val terms: String = "Present this code in-store or mention during your visit.",
    @Json(name = "valid_until") val validUntil: String = "Limited Time Community Offer",
    @Json(name = "image_res_name") val imageResName: String? = null,
    @Json(name = "posted_by_owner_email") val postedByOwnerEmail: String,
    @Json(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)

fun PromotionEntity.toSupabaseDto(): SupabasePromotionDto = SupabasePromotionDto(
    id = if (id > 0) id else null,
    businessId = businessId,
    businessName = businessName,
    category = category,
    title = title,
    discountDescription = discountDescription,
    promoCode = promoCode,
    terms = terms,
    validUntil = validUntil,
    imageResName = imageResName,
    postedByOwnerEmail = postedByOwnerEmail,
    createdAt = createdAt
)

fun SupabasePromotionDto.toEntity(): PromotionEntity = PromotionEntity(
    id = id ?: 0L,
    businessId = businessId,
    businessName = businessName,
    category = category,
    title = title,
    discountDescription = discountDescription,
    promoCode = promoCode,
    terms = terms,
    validUntil = validUntil,
    imageResName = imageResName,
    postedByOwnerEmail = postedByOwnerEmail,
    createdAt = createdAt
)

@JsonClass(generateAdapter = true)
data class SupabaseUserDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "email") val email: String,
    @Json(name = "full_name") val fullName: String,
    @Json(name = "password_hash") val passwordHash: String,
    @Json(name = "role") val role: String = "Resident",
    @Json(name = "business_name") val businessName: String? = null,
    @Json(name = "avatar_url") val avatarUrl: String? = null,
    @Json(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)

fun UserEntity.toSupabaseDto(): SupabaseUserDto = SupabaseUserDto(
    id = if (id > 0) id else null,
    email = email,
    fullName = fullName,
    passwordHash = passwordHash,
    role = role,
    businessName = businessName,
    avatarUrl = avatarUrl,
    createdAt = createdAt
)

fun SupabaseUserDto.toEntity(): UserEntity = UserEntity(
    id = id ?: 0L,
    email = email,
    fullName = fullName,
    passwordHash = passwordHash,
    role = role,
    businessName = businessName,
    avatarUrl = avatarUrl,
    createdAt = createdAt
)

@JsonClass(generateAdapter = true)
data class SupabaseAuthRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)

@JsonClass(generateAdapter = true)
data class SupabaseAuthResponse(
    @Json(name = "access_token") val accessToken: String? = null,
    @Json(name = "token_type") val tokenType: String? = null,
    @Json(name = "user") val user: Map<String, Any?>? = null,
    @Json(name = "error_description") val errorDescription: String? = null,
    @Json(name = "msg") val msg: String? = null
)
