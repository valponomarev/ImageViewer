package com.manzii.imageviewer.feature.images.compose

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import java.io.File

@Composable
internal fun ImageItem(
    image: com.manzii.imageviewer.domain.model.ImageEntity,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val previewUri = image.previewUri
    var hasError by remember { mutableStateOf(false) }
    
    val imageRequest = if (previewUri != null && File(previewUri).exists()) {
        ImageRequest.Builder(context)
            .data(File(previewUri))
            .build()
    } else {
        ImageRequest.Builder(context)
            .data(image.id)
            .build()
    }

    Box(
        modifier = Modifier
            .height(120.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                if (hasError) {
                    Toast.makeText(
                        context,
                        "Не удалось загрузить изображение или файл не является изображением",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    onClick()
                }
            }
    ) {
        SubcomposeAsyncImage(
            model = imageRequest,
            contentDescription = "Image ${image.id}",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 3.dp
                    )
                }
            },
            onError = {
                hasError = true
            },
            error = {
                ImagePlaceholder()
            }
        )
    }
}