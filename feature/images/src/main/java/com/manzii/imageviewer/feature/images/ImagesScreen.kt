package com.manzii.imageviewer.feature.images

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.manzii.imageviewer.domain.model.ImageEntity
import com.manzii.imageviewer.feature.images.compose.ErrorScreen
import com.manzii.imageviewer.feature.images.compose.ImagesGrid
import com.manzii.imageviewer.feature.images.compose.LoadingScreen
import com.manzii.imageviewer.feature.images.compose.NoNetworkScreen

@Composable
fun ImagesScreen(
    viewModel: ImagesViewModel = hiltViewModel(),
    onImageClick: ((String, List<ImageEntity>) -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.showLoader -> {
            LoadingScreen()
        }
        uiState.showNoNetwork -> {
            NoNetworkScreen(
                onRetry = { viewModel.handleEvent(ImagesUiEvent.Retry) }
            )
        }
        uiState.showError -> {
            ErrorScreen(
                error = uiState.error ?: "Unknown error",
                onRetry = { viewModel.handleEvent(ImagesUiEvent.Retry) }
            )
        }
        else -> {
            ImagesGrid(
                images = uiState.images,
                onImageClick = { imageId ->
                    if (onImageClick != null) {
                        onImageClick(imageId, uiState.images)
                    } else {
                        viewModel.handleEvent(ImagesUiEvent.RetryImage(imageId))
                    }
                }
            )
        }
    }
}










