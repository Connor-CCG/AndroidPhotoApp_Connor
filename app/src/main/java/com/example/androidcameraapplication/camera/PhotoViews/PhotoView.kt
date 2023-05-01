package com.example.androidcameraapplication.camera.PhotoViews

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.androidcameraapplication.camera.CameraAppBar
import com.example.androidcameraapplication.camera.PhotoViewModel
import com.example.androidcameraapplication.navigation.CameraScreens


//Camera needs to handle portrait/ landscape mode for photos
@Composable
fun PhotoView(
    navController: NavController,
    title: String,
    description: String,
    orientation: GalleryOrientation,
    viewModel: PhotoViewModel
){
    val context = LocalContext.current
    LaunchedEffect(Unit){
//        viewModel.loadPhotos(description)
        if(viewModel.photos.isNotEmpty()){
            Log.d("PhotoView", "PhotoView: photos not empty")
//            viewModel.processMLKitImage(
//                imageUri = viewModel.photos[0],
//                context = context)
        } else {
            Log.d("PhotoView", "PhotoView: photos empty")
        }
    }
    Scaffold(topBar = {
        CameraAppBar(title = "Photo App", navController = navController,
            icon = Icons.Default.ArrowBack){
            navController.popBackStack()
        }
    }){
        it
        Column {
            if (orientation == GalleryOrientation.Vertical){
                VPhotoView(
                    title = title,
                    description = description,
                    orientation = orientation,
                    viewModel = viewModel,
                    navController = navController
                )
            } else {
                HPhotoView(
                    title = title,
                    description = description,
                    orientation = orientation,
                    viewModel = viewModel,
                    navController = navController
                )
            }
        }
    }


}

enum class GalleryOrientation(value: String) {
    Horizontal("horizontal"),
    Vertical("vertical");
}

