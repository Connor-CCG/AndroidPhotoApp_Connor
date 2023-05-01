package com.example.androidcameraapplication.camera.PhotoViews

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
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
fun HPhotoView(
    title: String,
    description: String,
    orientation: GalleryOrientation,
    viewModel: PhotoViewModel,
    navController: NavController
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
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
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.height(400.dp)
        ){
            if (viewModel.photos.isNotEmpty()) {
                PhotoCarousel(
                    viewModel = viewModel,
                    photos = viewModel.photos,
                    orientation = orientation,
                    navController
                )
            } else {
                PhotoPlaceholder()
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
fun HPhoto(
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
            .width(210.dp)
            .height(250.dp),
        elevation = 5.dp,
        backgroundColor = Color.White,
        border = BorderStroke(1.dp, Color.LightGray),
    ){
        Box(contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .height(250.dp)
                    .padding(0.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .height(180.dp)
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
                    fontSize = 14.sp
                )

            }

            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.fillMaxSize().padding(bottom = 20.dp, end = 45.dp)
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