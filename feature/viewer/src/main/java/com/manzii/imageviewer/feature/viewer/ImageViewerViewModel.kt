package com.manzii.imageviewer.feature.viewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manzii.imageviewer.domain.model.ImageEntity
import com.manzii.imageviewer.domain.usecase.GetAllImagesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ImageViewerViewModel @Inject constructor(
    private val getAllImagesUseCase: GetAllImagesUseCase
) : ViewModel() {

    private val _images = MutableStateFlow<List<ImageEntity>>(emptyList())
    val images: StateFlow<List<ImageEntity>> = _images.asStateFlow()

    init {
        loadImages()
    }

    private fun loadImages() {
        viewModelScope.launch {
            getAllImagesUseCase().collect { imageList ->
                val validImages = withContext(Dispatchers.IO) {
                    imageList.filter { image ->
                        val hasUri = image.previewUri != null || image.originUri != null
                        if (!hasUri) {
                            false
                        } else {
                            val previewValid = image.previewUri?.let { uri ->
                                if (uri.startsWith("/")) {
                                    try {
                                        File(uri).exists()
                                    } catch (e: Exception) {
                                        false
                                    }
                                } else true
                            } ?: false
                            
                            val originValid = image.originUri?.let { uri ->
                                if (uri.startsWith("/")) {
                                    try {
                                        File(uri).exists()
                                    } catch (e: Exception) {
                                        false
                                    }
                                } else true
                            } ?: false

                            previewValid || originValid
                        }
                    }
                }
                _images.value = validImages
            }
        }
    }
}


