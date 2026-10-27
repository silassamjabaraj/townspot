package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val email: String,
    val fullName: String,
    val passwordHash: String,
    val role: String = "Resident", // "Resident", "Business Owner", "Admin"
    val businessName: String? = null,
    val avatarUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
