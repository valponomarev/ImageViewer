package com.manzii.imageviewer.feature.images

import android.annotation.SuppressLint
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manzii.imageviewer.domain.usecase.DownloadImagesFileUseCase
import com.manzii.imageviewer.domain.usecase.GetAllImagesUseCase
import com.manzii.imageviewer.domain.usecase.IsImagesFileCachedUseCase
import com.manzii.imageviewer.domain.usecase.ParseAndLoadImagesUseCase
import com.manzii.imageviewer.domain.usecase.RetryFailedImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImagesViewModel @Inject constructor(
    private val getAllImagesUseCase: GetAllImagesUseCase,
    private val downloadImagesFileUseCase: DownloadImagesFileUseCase,
    private val parseAndLoadImagesUseCase: ParseAndLoadImagesUseCase,
    private val isImagesFileCachedUseCase: IsImagesFileCachedUseCase,
    private val retryFailedImageUseCase: RetryFailedImageUseCase,
    private val connectivityManager: ConnectivityManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImagesUiState())
    val uiState: StateFlow<ImagesUiState> = _uiState.asStateFlow()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            viewModelScope.launch {
                _uiState.update { it.copy(isNetworkAvailable = true) }
                handleEvent(ImagesUiEvent.OnNetworkAvailable)
            }
        }

        override fun onLost(network: Network) {
            _uiState.update { it.copy(isNetworkAvailable = false) }
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            val isAvailable = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            _uiState.update { it.copy(isNetworkAvailable = isAvailable) }
        }
    }

    init {
        registerNetworkCallback()
        loadInitialData()
        observeImages()
    }

    @SuppressLint("MissingPermission")
    private fun registerNetworkCallback() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isInitialLoading = true, isLoading = true) }

            val isCached = isImagesFileCachedUseCase()
            if (!isCached) {
                downloadAndParseImages()
            } else {
                _uiState.update { it.copy(isInitialLoading = false, isLoading = false) }
            }
        }
    }

    private fun observeImages() {
        viewModelScope.launch {
            getAllImagesUseCase()
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            error = e.message ?: "Неизвестная ошибка",
                            isLoading = false,
                            isInitialLoading = false
                        )
                    }
                }
                .collect { images ->
                    _uiState.update {
                        it.copy(
                            images = images,
                            isLoading = false,
                            isInitialLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    fun handleEvent(event: ImagesUiEvent) {
        when (event) {
            is ImagesUiEvent.Retry -> {
                viewModelScope.launch {
                    _uiState.update { it.copy(isLoading = true, error = null) }
                    downloadAndParseImages()
                }
            }
            is ImagesUiEvent.RetryImage -> {
                viewModelScope.launch {
                    retryFailedImageUseCase(event.imageId)
                        .onFailure { e ->
                            _uiState.update {
                                it.copy(error = e.message ?: "Ошибка повторной загрузки")
                            }
                        }
                }
            }
            is ImagesUiEvent.OnNetworkAvailable -> {
                viewModelScope.launch {
                    val isCached = isImagesFileCachedUseCase()
                    if (!isCached) {
                        downloadAndParseImages()
                    }
                }
            }
        }
    }

    private suspend fun downloadAndParseImages() {
        downloadImagesFileUseCase()
            .onSuccess {
                parseAndLoadImagesUseCase()
                    .onFailure { e ->
                        _uiState.update {
                            it.copy(
                                error = e.message ?: "Ошибка парсинга изображения",
                                isLoading = false
                            )
                        }
                    }
            }
            .onFailure { e ->
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Не удалось загрузить",
                        isLoading = false,
                        isInitialLoading = false
                    )
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}


