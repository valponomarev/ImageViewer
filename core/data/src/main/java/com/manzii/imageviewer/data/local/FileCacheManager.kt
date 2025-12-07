package com.manzii.imageviewer.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileCacheManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val cacheDir: File by lazy {
        File(context.cacheDir, "images_cache").apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    private val imagesFile: File by lazy {
        File(cacheDir, "images.txt")
    }

    suspend fun saveImagesFile(content: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            imagesFile.writeText(content)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun readImagesFile(): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (imagesFile.exists()) {
                Result.success(imagesFile.readText())
            } else {
                Result.failure(IOException("File not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isImagesFileCached(): Boolean = withContext(Dispatchers.IO) {
        imagesFile.exists()
    }

    suspend fun saveImageFile(url: String, data: ByteArray, isPreview: Boolean): Result<File> = withContext(Dispatchers.IO) {
        try {
            val fileName = if (isPreview) {
                "preview_${url.hashCode()}.jpg"
            } else {
                "origin_${url.hashCode()}.jpg"
            }
            val file = File(cacheDir, fileName)
            FileOutputStream(file).use { it.write(data) }
            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearCache(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            cacheDir.listFiles()?.forEach { it.delete() }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

