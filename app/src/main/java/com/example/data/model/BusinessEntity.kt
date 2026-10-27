package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "businesses")
data class BusinessEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val tagline: String,
    val description: String,
    val address: String,
    val neighborhood: String,
    val phone: String,
    val website: String,
    val email: String,
    val hours: String,
    val isOpenNow: Boolean = true,
    val priceLevel: String = "$$",
    val rating: Float = 4.8f,
    val reviewCount: Int = 18,
    val isVerified: Boolean = true,
    val isFeatured: Boolean = false,
    val isSaved: Boolean = false,
    val latitude: Double = 37.7749,
    val longitude: Double = -122.4194,
    val specialOffer: String? = null,
    val tags: String = "WiFi, Outdoor Seating",
    val categoryIconName: String = "store",
    val accentColorHex: String = "#0F766E",
    val imageResName: String? = null,
    val imageUri: String? = null,
    val claimedByOwnerEmail: String? = null,
    val claimedByOwnerName: String? = null,
    val isClaimed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
