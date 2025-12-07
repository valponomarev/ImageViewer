package com.manzii.imageviewer.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "image_entity")
data class ImageEntity(
    @PrimaryKey
    val id: String,
    val previewUri: String?,
    val originUri: String?
)

