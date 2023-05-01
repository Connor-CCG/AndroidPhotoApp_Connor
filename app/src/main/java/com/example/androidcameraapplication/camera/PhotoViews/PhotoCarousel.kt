package com.example.androidcameraapplication.camera.PhotoViews

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.androidcameraapplication.camera.PhotoViewModel
import com.example.androidcameraapplication.model.FirebasePhoto

@Composable
fun PhotoCarousel(
    viewModel: PhotoViewModel,
    photos: List<FirebasePhoto>,
    orientation: GalleryOrientation,
    navController: NavController
) {
    if (orientation == GalleryOrientation.Horizontal) {
        LazyRow() {
            items(photos) { photo ->
                HPhoto(
                    photoUri = photo.remoteUrl,
                    description = photo.photoDescription,
                    viewModel = viewModel,
                    navController
                )
            }
        }
    } else if (orientation == GalleryOrientation.Vertical){
        LazyColumn() {
            items(photos) { photo ->
                VPhoto(
                    photoUri = photo.remoteUrl,
                    description = photo.photoDescription,
                    viewModel = viewModel,
                    navController
                )
            }
        }
    }

}