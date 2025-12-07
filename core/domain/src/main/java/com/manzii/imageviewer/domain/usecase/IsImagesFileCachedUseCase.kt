package com.manzii.imageviewer.domain.usecase

import com.manzii.imageviewer.domain.repository.ImageRepository
import javax.inject.Inject

class IsImagesFileCachedUseCase @Inject constructor(
    private val repository: ImageRepository
) {
    suspend operator fun invoke(): Boolean {
        return repository.isImagesFileCached()
    }
}

