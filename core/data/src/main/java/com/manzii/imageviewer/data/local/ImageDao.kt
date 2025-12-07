package com.manzii.imageviewer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {
    @Query("SELECT * FROM image_entity ORDER BY id ASC")
    fun getAllImages(): Flow<List<ImageEntity>>

    @Query("SELECT * FROM image_entity WHERE id = :id")
    fun getImageById(id: String): Flow<ImageEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: ImageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImages(images: List<ImageEntity>)

    @Update
    suspend fun updateImage(image: ImageEntity)

    @Query("DELETE FROM image_entity")
    suspend fun clearAll()
}

