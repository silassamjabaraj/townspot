package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.BusinessDao
import com.example.data.dao.CategoryDao
import com.example.data.dao.NeighborhoodDao
import com.example.data.dao.PromotionDao
import com.example.data.dao.ReviewDao
import com.example.data.dao.UserDao
import com.example.data.model.BusinessEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.NeighborhoodEntity
import com.example.data.model.PromotionEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.UserEntity

@Database(
    entities = [
        BusinessEntity::class,
        ReviewEntity::class,
        UserEntity::class,
        PromotionEntity::class,
        CategoryEntity::class,
        NeighborhoodEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class TownSpotDatabase : RoomDatabase() {
    abstract fun businessDao(): BusinessDao
    abstract fun reviewDao(): ReviewDao
    abstract fun userDao(): UserDao
    abstract fun promotionDao(): PromotionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun neighborhoodDao(): NeighborhoodDao

    companion object {
        @Volatile
        private var INSTANCE: TownSpotDatabase? = null

        fun getDatabase(context: Context): TownSpotDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TownSpotDatabase::class.java,
                    "townspot_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
