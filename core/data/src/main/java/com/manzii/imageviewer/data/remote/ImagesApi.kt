package com.manzii.imageviewer.data.remote

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Url

interface ImagesApi {
    @GET
    suspend fun downloadImagesFile(@Url url: String): ResponseBody

    @GET
    suspend fun downloadImage(@Url url: String): ResponseBody
}

