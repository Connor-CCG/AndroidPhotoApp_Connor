package com.example.androidcameraapplication.navigation

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.androidcameraapplication.camera.*
import com.example.androidcameraapplication.camera.PhotoViews.GalleryOrientation
import com.example.androidcameraapplication.camera.PhotoViews.PhotoView
import com.example.androidcameraapplication.home.HomeView
import com.example.androidcameraapplication.login.Login
import com.example.androidcameraapplication.search.SearchScreen
import com.example.androidcameraapplication.splashscreen.CameraSplashScreen
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ExecutorService

@Composable
fun CameraNavigation(
    outputDirectory: File,
    cameraExecutor: ExecutorService,
    viewModel: PhotoViewModel
) {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()

    NavHost(navController = navController, startDestination = CameraScreens.SplashScreen.name){
        composable(CameraScreens.HomeView.name){
            HomeView(navController = navController, viewModel = viewModel)
        }
        composable(CameraScreens.PhotoView.name + "/{title}/{description}/{orientation}",
            arguments = listOf(navArgument("title"){
                type = NavType.StringType
            })){ backStackEntry ->
            backStackEntry.arguments?.getString("title").let { title ->
                backStackEntry.arguments?.getString("description").let { description ->
                    backStackEntry.arguments?.getString("orientation").let { orientation ->
                        if (title != null && description != null && orientation != null) {
                            PhotoView(
                                navController = navController,
                                viewModel = viewModel,
                                title = title,
                                description = description,
                                orientation = if (orientation == GalleryOrientation.Horizontal.toString())
                                    GalleryOrientation.Horizontal
                                else GalleryOrientation.Vertical
                            )
                        }
                    }
                }
            }
        }

        composable(CameraScreens.CaptureView.name+ "/{description}",
            arguments = listOf(navArgument("description"){
                type = NavType.StringType
            })){ backStackEntry ->
            fun handleImageCapture(uri: Uri, photoDescription: String) {
                viewModel.handleImageCapture(uri, photoDescription)
            }
            backStackEntry.arguments?.getString("description").let { description ->
                if (description != null){
                    CaptureView(
                        outputDirectory = outputDirectory,
                        executor = cameraExecutor,
                        photoDescription = description,
                        onImageCaptured = ::handleImageCapture,
                        viewModel = viewModel,
                        navController = navController,
                        onError = { Log.d("Cam error", "View error:", it) }
                    )
                }
            }
        }

        composable(CameraScreens.SplashScreen.name){
            CameraSplashScreen(navController = navController)
        }

        composable(CameraScreens.Login.name){
            Login(navController = navController)
        }

        composable(CameraScreens.Search.name + "/{description}",
            arguments = listOf(navArgument("description"){
                type = NavType.StringType
            })){ backStackEntry ->
                backStackEntry.arguments?.getString("description").let { description ->
                    if (description != null) {
                        SearchScreen(
                            navController = navController,
                            title = "Photo Results",
                            description = description,
                            viewModel = viewModel
                        ){ text ->
                            coroutineScope.launch {
                                viewModel.getPhotosByDescription(photoDescription = text)
                            }
                        }
                    }
                }
        }
    }
}