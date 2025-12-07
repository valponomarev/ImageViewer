package com.manzii.imageviewer.feature.viewer

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.manzii.imageviewer.feature.viewer.compose.BlurredBackground
import com.manzii.imageviewer.feature.viewer.compose.ThumbnailPager
import com.manzii.imageviewer.feature.viewer.compose.ZoomableImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageViewerScreen(
    initialImageId: String,
    onBack: () -> Unit,
    viewModel: ImageViewerViewModel = hiltViewModel()
) {
    val images by viewModel.images.collectAsState()
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { images.size }
    )
    val thumbnailListState = rememberLazyListState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var hasScrolledToInitial by remember(initialImageId) { mutableStateOf(false) }

    LaunchedEffect(images, initialImageId) {
        if (images.isNotEmpty() && !hasScrolledToInitial) {
            val targetIndex =
                images.indexOfFirst { it.id == initialImageId }.takeIf { it >= 0 } ?: 0
            if (pagerState.currentPage != targetIndex) {
                pagerState.animateScrollToPage(targetIndex)
            }
            thumbnailListState.animateScrollToItem(targetIndex)
            hasScrolledToInitial = true
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage < images.size) {
            thumbnailListState.animateScrollToItem(pagerState.currentPage)
        }
    }

    if (images.isEmpty()) {
        return
    }

    val currentImage by remember {
        derivedStateOf {
            val currentPage = pagerState.currentPage
            if (currentPage < images.size) images[currentPage] else null
        }
    }

    var showButtons by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        currentImage?.let { image ->
            BlurredBackground(image = image)
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            if (page < images.size) {
                val image = images[page]
                ZoomableImage(
                    image = image,
                    onTap = {
                        showButtons = !showButtons
                    }
                )
            }
        }

        AnimatedVisibility(
            visible = showButtons,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        currentImage?.let { image ->
            AnimatedVisibility(
                visible = showButtons,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                IconButton(
                    onClick = {
                        val shareUrl = image.id
                        try {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareUrl)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            val chooserIntent =
                                Intent.createChooser(shareIntent, "Поделиться URL изображения")
                            chooserIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(chooserIntent)
                        } catch (_: Exception) {
                        }
                    },
                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "Поделиться",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showButtons,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            ThumbnailPager(
                images = images,
                listState = thumbnailListState,
                selectedIndex = pagerState.currentPage,
                onThumbnailClick = { index ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    }
}







