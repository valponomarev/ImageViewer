package com.manzii.imageviewer.domain.usecase

import com.manzii.imageviewer.domain.repository.ImageRepository
import javax.inject.Inject

class DownloadImagesFileUseCase @Inject constructor(
    private val repository: ImageRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.downloadAndCacheImagesFile()
    }
}

