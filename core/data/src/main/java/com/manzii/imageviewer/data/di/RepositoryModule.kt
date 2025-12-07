package com.manzii.imageviewer.data.di

import android.content.Context
import android.net.ConnectivityManager
import com.manzii.imageviewer.data.repository.ImageRepositoryImpl
import com.manzii.imageviewer.domain.repository.ImageRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindImageRepository(
        imageRepositoryImpl: ImageRepositoryImpl
    ): ImageRepository

    companion object {
        @Provides
        @Singleton
        fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager {
            return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }
    }
}

