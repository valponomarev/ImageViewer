package com.manzii.imageviewer.feature.viewer.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.manzii.imageviewer.domain.model.ImageEntity
import java.io.File

@Composable
internal fun BlurredBackground(
    image: ImageEntity
) {
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

    Image(
        painter = rememberAsyncImagePainter(imageRequest),
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()
            .blur(radius = 20.dp)
            .graphicsLayer(alpha = 0.2f),
        contentScale = ContentScale.Crop
    )
}