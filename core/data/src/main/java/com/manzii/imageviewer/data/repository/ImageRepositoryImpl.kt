package com.manzii.imageviewer.data.repository

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.manzii.imageviewer.data.local.FileCacheManager
import com.manzii.imageviewer.data.local.ImageDao
import com.manzii.imageviewer.data.local.ImageEntity as LocalImageEntity
import com.manzii.imageviewer.data.mapper.toDomain
import com.manzii.imageviewer.data.remote.ImagesApi
import com.manzii.imageviewer.domain.model.ImageEntity
import com.manzii.imageviewer.domain.repository.ImageRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.net.URL
import javax.inject.Inject
import androidx.core.graphics.scale

class ImageRepositoryImpl @Inject constructor(
    private val api: ImagesApi,
    private val imageDao: ImageDao,
    private val fileCacheManager: FileCacheManager,
    private val connectivityManager: ConnectivityManager
) : ImageRepository {

    companion object {
        private const val IMAGES_FILE_URL = "https://it-link.ru/test/images.txt"
        private const val PREVIEW_WIDTH = 100
        private const val PREVIEW_HEIGHT = 120
    }

    override suspend fun downloadAndCacheImagesFile(): Result<Unit> = withContext(Dispatchers.IO) {
        if (!isNetworkAvailable()) {
            return@withContext Result.failure(Exception("Отсутствует интернет"))
        }

        try {
            val response = api.downloadImagesFile(IMAGES_FILE_URL)
            val content = response.string()
            fileCacheManager.saveImagesFile(content).getOrElse {
                return@withContext Result.failure(it)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun parseAndLoadImages(): Result<Unit> = withContext(Dispatchers.IO) {
        val fileContentResult = fileCacheManager.readImagesFile()
        val fileContent = fileContentResult.getOrElse {
            return@withContext Result.failure(it)
        }

        val lines = fileContent.lines()
        val imageUrls = lines.mapNotNull { line ->
            val trimmed = line.trim()
            if (trimmed.isNotEmpty() && isValidImageUrl(trimmed)) {
                trimmed
            } else {
                null
            }
        }

        coroutineScope {
            imageUrls.map { url ->
                async {
                    try {
                        loadImage(url)
                    } catch (_: Exception) {
                        val entity = LocalImageEntity(
                            id = url,
                            previewUri = null,
                            originUri = null
                        )
                        imageDao.insertImage(entity)
                    }
                }
            }.awaitAll()
        }

        Result.success(Unit)
    }

    override suspend fun retryFailedImage(imageId: String): Result<Unit> = withContext(Dispatchers.IO) {
        if (!isNetworkAvailable()) {
            return@withContext Result.failure(Exception("Отсутствует интернет"))
        }

        try {
            loadImage(imageId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getAllImages(): Flow<List<ImageEntity>> {
        return imageDao.getAllImages().map { localImages ->
            localImages.map { it.toDomain() }
        }
    }

    override fun getImageById(id: String): Flow<ImageEntity?> {
        return imageDao.getImageById(id).map { it?.toDomain() }
    }

    override suspend fun isImagesFileCached(): Boolean {
        return fileCacheManager.isImagesFileCached()
    }

    override suspend fun clearCache(): Result<Unit> {
        return fileCacheManager.clearCache()
    }

    private suspend fun loadImage(url: String) {
        if (!isNetworkAvailable()) {
            throw Exception("Отсутствует интернет")
        }

        val originResponse = api.downloadImage(url)
        val originBytes = originResponse.bytes()

        val originFileResult = fileCacheManager.saveImageFile(url, originBytes, isPreview = false)
        val originFile = originFileResult.getOrElse { throw it }

        val bitmap = BitmapFactory.decodeByteArray(originBytes, 0, originBytes.size)
            ?: throw Exception("Ошибка декодирования")
        val previewBitmap = bitmap.scale(PREVIEW_WIDTH, PREVIEW_HEIGHT)

        val previewBytes = withContext(Dispatchers.Default) {
            val outputStream = java.io.ByteArrayOutputStream()
            previewBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
            outputStream.toByteArray()
        }

        val previewFileResult = fileCacheManager.saveImageFile(url, previewBytes, isPreview = true)
        val previewFile = previewFileResult.getOrElse { throw it }

        val entity = LocalImageEntity(
            id = url,
            previewUri = previewFile.absolutePath,
            originUri = originFile.absolutePath
        )
        imageDao.insertImage(entity)
    }

    private fun isValidImageUrl(url: String): Boolean {
        return try {
            val parsedUrl = URL(url)
            if (parsedUrl.protocol !in listOf("http", "https")) {
                return false
            }
            val path = parsedUrl.path.lowercase()
            path.endsWith(".jpg", ignoreCase = true) ||
                    path.endsWith(".jpeg", ignoreCase = true) ||
                    path.endsWith(".png", ignoreCase = true) ||
                    path.endsWith(".gif", ignoreCase = true) ||
                    path.endsWith(".webp", ignoreCase = true) ||
                    path.contains("image", ignoreCase = true) ||
                    url.contains("image", ignoreCase = true)
        } catch (_: Exception) {
            false
        }
    }

    @SuppressLint("MissingPermission")
    private fun isNetworkAvailable(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}

