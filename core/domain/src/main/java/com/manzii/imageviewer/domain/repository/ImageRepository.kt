package com.manzii.imageviewer.domain.repository

import com.manzii.imageviewer.domain.model.ImageEntity
import kotlinx.coroutines.flow.Flow

interface ImageRepository {
    suspend fun downloadAndCacheImagesFile(): Result<Unit>
    suspend fun parseAndLoadImages(): Result<Unit>
    suspend fun retryFailedImage(imageId: String): Result<Unit>
    fun getAllImages(): Flow<List<ImageEntity>>
    fun getImageById(id: String): Flow<ImageEntity?>
    suspend fun isImagesFileCached(): Boolean
    suspend fun clearCache(): Result<Unit>
}

