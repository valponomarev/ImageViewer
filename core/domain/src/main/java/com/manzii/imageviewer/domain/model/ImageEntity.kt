package com.manzii.imageviewer.domain.model

data class ImageEntity(
    val id: String,
    val previewUri: String?,
    val originUri: String?
)

