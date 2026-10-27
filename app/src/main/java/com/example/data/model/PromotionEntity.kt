package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "promotions")
data class PromotionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val businessId: Long,
    val businessName: String,
    val category: String,
    val title: String,
    val discountDescription: String,
    val promoCode: String? = null,
    val terms: String = "Present this code in-store or mention during your visit.",
    val validUntil: String = "Limited Time Community Offer",
    val imageResName: String? = null,
    val postedByOwnerEmail: String,
    val createdAt: Long = System.currentTimeMillis()
)
