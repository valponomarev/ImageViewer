package com.manzii.imageviewer.data.mapper

import com.manzii.imageviewer.data.local.ImageEntity as LocalImageEntity
import com.manzii.imageviewer.domain.model.ImageEntity

fun LocalImageEntity.toDomain(): ImageEntity {
    return ImageEntity(
        id = id,
        previewUri = previewUri,
        originUri = originUri
    )
}

fun ImageEntity.toLocal(): LocalImageEntity {
    return LocalImageEntity(
        id = id,
        previewUri = previewUri,
        originUri = originUri
    )
}

