package com.example.androidcameraapplication.camera.PhotoViews

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PhotoPlaceholder(){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Icon(
            imageVector = Icons.Filled.CameraAlt,
            contentDescription = "Image Placeholder",
            modifier = Modifier
                .size(150.dp),
            tint = MaterialTheme.colors.primary
        )

        Text(
            "Add photos from gallery or from camera",
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.primary
        )
    }
}