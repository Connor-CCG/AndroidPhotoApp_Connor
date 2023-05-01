package com.example.androidcameraapplication.camera.PhotoViews

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.WrapText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.androidcameraapplication.camera.CameraButtons
import com.example.androidcameraapplication.camera.PhotoViewModel
import kotlinx.coroutines.launch

@Composable
fun VPhotoView(
    title: String,
    description: String,
    orientation: GalleryOrientation,
    viewModel: PhotoViewModel,
    isSearch: Boolean = false,
    navController: NavController
){
    Box(contentAlignment = Alignment.BottomCenter){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 10.dp,
                    start = 10.dp,
                    end = 10.dp,
                    bottom = 70.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = title,
                style = MaterialTheme.typography.h5,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            //have an HPhotoView and VPhotoView that displays the entire photo view
            //use this view to make the decision on orientation
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,

                modifier = Modifier.height(800.dp)
            ){
                if (viewModel.photos.isNotEmpty()) {
                    PhotoCarousel(
                        viewModel = viewModel,
                        photos = if(isSearch) viewModel.searchPhotos else viewModel.photos,
                        orientation = orientation,
                        navController
                    )
                } else {
                    PhotoPlaceholder()
                }
            }
        }

        CameraButtons(
            navController = navController,
            viewModel = viewModel,
            description = description
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun VPhoto(
    photoUri: String,
    description: String,
    viewModel: PhotoViewModel,
    navController: NavController
){
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .padding(10.dp)
            .height(350.dp)
            .fillMaxWidth(),
        elevation = 5.dp,
        backgroundColor = Color.White,
        border = BorderStroke(1.dp, Color.LightGray),
    ){
        Box(contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .height(400.dp)
                    .padding(0.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .height(300.dp)
                        .padding(0.dp)
                ) {
                    GlideImage(
                        model = photoUri.toUri(),
                        contentDescription = "Carousel Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(5.dp))
                    )
                }

                Text(
                    description,
                    modifier = Modifier.padding(top = 10.dp),
                    fontSize = 16.sp
                )
            }

            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.fillMaxSize().padding(bottom = 10.dp, end = 70.dp)
            ) {
                IconButton(
                    modifier = Modifier.padding(bottom = 35.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colors.primary),
                    onClick = {
                        coroutineScope.launch {
                            viewModel.processMLKitImage(
                                imageUri = photoUri.toUri(),
                                context = context
                            )
                        }
                        navController.popBackStack()
                    }) {
                    Icon(
                        imageVector = Icons.Filled.ChatBubble,
                        contentDescription = "Extract Text",
                        tint = Color.White
                    )

                }
            }
        }
    }
}