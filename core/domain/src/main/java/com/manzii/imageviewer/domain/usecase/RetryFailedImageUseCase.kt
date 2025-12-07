package com.manzii.imageviewer.domain.usecase

import com.manzii.imageviewer.domain.repository.ImageRepository
import javax.inject.Inject

class RetryFailedImageUseCase @Inject constructor(
    private val repository: ImageRepository
) {
    suspend operator fun invoke(imageId: String): Result<Unit> {
        return repository.retryFailedImage(imageId)
    }
}

