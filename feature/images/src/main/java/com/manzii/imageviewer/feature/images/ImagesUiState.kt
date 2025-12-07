package com.manzii.imageviewer.feature.images

import com.manzii.imageviewer.domain.model.ImageEntity

data class ImagesUiState(
    val images: List<ImageEntity> = emptyList(),
    val isLoading: Boolean = false,
    val isInitialLoading: Boolean = true,
    val error: String? = null,
    val isNetworkAvailable: Boolean = true
) {
    val showError: Boolean get() = error != null && !isLoading
    val showLoader: Boolean get() = isInitialLoading && images.isEmpty()
    val showNoNetwork: Boolean get() = !isNetworkAvailable && images.isEmpty() && !isLoading
}

sealed interface ImagesUiEvent {
    object Retry : ImagesUiEvent
    data class RetryImage(val imageId: String) : ImagesUiEvent
    object OnNetworkAvailable : ImagesUiEvent
}


