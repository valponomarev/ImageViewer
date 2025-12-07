package com.manzii.imageviewer.domain.usecase

import com.manzii.imageviewer.domain.model.ImageEntity
import com.manzii.imageviewer.domain.repository.ImageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllImagesUseCase @Inject constructor(
    private val repository: ImageRepository
) {
    operator fun invoke(): Flow<List<ImageEntity>> {
        return repository.getAllImages()
    }
}

