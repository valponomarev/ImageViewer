package com.manzii.imageviewer.feature.images.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.manzii.imageviewer.domain.model.ImageEntity

@Composable
internal fun ImagesGrid(
    images: List<ImageEntity>,
    onImageClick: (String) -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val cellWidth = 110.dp
    val columns = maxOf(1, (screenWidth / cellWidth).toInt())

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        contentPadding = PaddingValues(
            top = 8.dp + WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
            bottom = 8.dp,
            start = 8.dp,
            end = 8.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(images) { image ->
            ImageItem(
                image = image,
                onClick = { onImageClick(image.id) }
            )
        }
    }
}