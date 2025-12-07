package com.manzii.imageviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import com.manzii.imageviewer.feature.images.ImagesScreen
import com.manzii.imageviewer.feature.viewer.ImageViewerScreen
import com.manzii.imageviewer.presentation.theme.ImageViewerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            ImageViewerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "images"
                    ) {
                        composable("images") {
                            ImagesScreen(
                                onImageClick = { imageId, images ->
                                    val encodedImageId = URLEncoder.encode(imageId, StandardCharsets.UTF_8.toString())
                                    navController.navigate("viewer/$encodedImageId")
                                }
                            )
                        }
                        composable(
                            route = "viewer/{imageId}",
                            arguments = listOf(
                                navArgument("imageId") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val encodedImageId = backStackEntry.arguments?.getString("imageId") ?: ""
                            val imageId = java.net.URLDecoder.decode(encodedImageId, StandardCharsets.UTF_8.toString())
                            ImageViewerScreen(
                                initialImageId = imageId,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}

