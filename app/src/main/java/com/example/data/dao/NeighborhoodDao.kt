package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.NeighborhoodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NeighborhoodDao {
    @Query("SELECT * FROM neighborhoods ORDER BY name ASC")
    fun getAllNeighborhoods(): Flow<List<NeighborhoodEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNeighborhood(neighborhood: NeighborhoodEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(neighborhoods: List<NeighborhoodEntity>)

    @Query("DELETE FROM neighborhoods WHERE id = :id")
    suspend fun deleteNeighborhoodById(id: Long)

    @Query("DELETE FROM neighborhoods WHERE name = :name")
    suspend fun deleteNeighborhoodByName(name: String)

    @Query("SELECT COUNT(*) FROM neighborhoods")
    suspend fun getNeighborhoodCount(): Int

    @Query("DELETE FROM neighborhoods")
    suspend fun deleteAllNeighborhoods()
}
