package com.manzii.imageviewer.feature.viewer.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.manzii.imageviewer.domain.model.ImageEntity
import java.io.File

@Composable
internal fun ThumbnailPager(
    images: List<ImageEntity>,
    listState: LazyListState,
    selectedIndex: Int,
    onThumbnailClick: (Int) -> Unit
) {
    val context = LocalContext.current
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(
                Color.Black.copy(alpha = 0.5f),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .padding(vertical = 8.dp)
    ) {
        LazyRow(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            itemsIndexed(images) { index, image ->
                val isSelected = selectedIndex == index
                
                val imageUri = image.previewUri ?: image.originUri
                val imageRequest = if (imageUri != null && File(imageUri).exists()) {
                    ImageRequest.Builder(context)
                        .data(File(imageUri))
                        .size(100)
                        .build()
                } else if (imageUri != null) {
                    ImageRequest.Builder(context)
                        .data(imageUri)
                        .size(100)
                        .build()
                } else {
                    ImageRequest.Builder(context)
                        .data(image.id)
                        .size(100)
                        .build()
                }
                
                Box(
                    modifier = Modifier
                        .height(84.dp)
                        .width(84.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = if (isSelected) 3.dp else 0.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            onThumbnailClick(index)
                        }
                        .padding(if (isSelected) 0.dp else 3.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(imageRequest),
                        contentDescription = "Миниатюра ${image.id}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}