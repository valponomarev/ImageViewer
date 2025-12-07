package com.manzii.imageviewer.feature.viewer.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.manzii.imageviewer.domain.model.ImageEntity
import java.io.File

@Composable
internal fun ZoomableImage(
    image: ImageEntity,
    onTap: () -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    
    var imageSize by remember { mutableStateOf<IntSize?>(null) }
    var containerSize by remember { mutableStateOf<IntSize?>(null) }
    
    val minScale = remember(imageSize, containerSize, screenWidthPx) {
        if (imageSize != null && containerSize != null) {
            val imageWidth = imageSize!!.width.toFloat()
            val containerWidth = containerSize!!.width.toFloat()
            if (imageWidth > 0f && containerWidth > 0f) {
                maxOf(1f, containerWidth / imageWidth)
            } else {
                1f
            }
        } else {
            1f
        }
    }
    
    var scale by remember { mutableFloatStateOf(minScale) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var isImageTapped by remember { mutableStateOf(false) }
    
    LaunchedEffect(minScale) {
        if (scale < minScale) {
            scale = minScale
            offsetX = 0f
            offsetY = 0f
        }
    }

    val transformableState = rememberTransformableState { zoomChange, offsetChange, _ ->
        val newScale = (scale * zoomChange).coerceIn(minScale, 5f)
        
        if (zoomChange != 1f) {
            scale = newScale
            if (newScale <= minScale) {
                offsetX = 0f
                offsetY = 0f
            }
        }
        
        if (scale > minScale && (offsetChange.x != 0f || offsetChange.y != 0f)) {
            if (imageSize != null && containerSize != null) {
                val scaledImageWidth = imageSize!!.width * scale
                val scaledImageHeight = imageSize!!.height * scale
                val maxOffsetX = maxOf(0f, (scaledImageWidth - containerSize!!.width) / 2f)
                val maxOffsetY = maxOf(0f, (scaledImageHeight - containerSize!!.height) / 2f)
                offsetX = (offsetX + offsetChange.x).coerceIn(-maxOffsetX, maxOffsetX)
                offsetY = (offsetY + offsetChange.y).coerceIn(-maxOffsetY, maxOffsetY)
            } else {
                val maxOffset = 500f * scale
                offsetX = (offsetX + offsetChange.x).coerceIn(-maxOffset, maxOffset)
                offsetY = (offsetY + offsetChange.y).coerceIn(-maxOffset, maxOffset)
            }
        }
    }
    
    val context = LocalContext.current
    
    val imageUri = image.originUri ?: image.previewUri
    val imageRequest = if (imageUri != null && File(imageUri).exists()) {
        ImageRequest.Builder(context)
            .data(File(imageUri))
            .build()
    } else if (imageUri != null) {
        ImageRequest.Builder(context)
            .data(imageUri)
            .build()
    } else {
        ImageRequest.Builder(context)
            .data(image.id)
            .build()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                containerSize = size
            }
            .then(
                if (scale > minScale) {
                    Modifier.transformable(state = transformableState)
                } else {
                    Modifier
                }
            )
            .pointerInput(scale, minScale) {
                detectTapGestures(
                    onDoubleTap = { tapOffset ->
                        if (scale > minScale) {
                            scale = minScale
                            offsetX = 0f
                            offsetY = 0f
                        } else {
                            scale = minOf(2.5f, minScale * 2.5f)
                        }
                    },
                    onTap = {
                        isImageTapped = !isImageTapped
                        onTap()
                    }
                )
            }
    ) {
        Image(
            painter = rememberAsyncImagePainter(imageRequest),
            contentDescription = "Изображение ${image.id}",
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { size ->
                    imageSize = size
                }
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY
                ),
            contentScale = ContentScale.Fit
        )
    }
}