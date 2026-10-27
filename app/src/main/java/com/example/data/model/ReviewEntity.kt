package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val businessId: Long,
    val authorName: String,
    val rating: Int, // 1 to 5
    val comment: String,
    val dateString: String,
    val helpfulCount: Int = 0
)
