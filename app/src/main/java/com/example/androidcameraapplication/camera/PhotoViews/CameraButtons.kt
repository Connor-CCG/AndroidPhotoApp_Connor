package com.example.androidcameraapplication.camera

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.androidcameraapplication.navigation.CameraScreens
import kotlin.reflect.KFunction1

@Composable
fun CameraButtons(
    modifier: Modifier = Modifier
        .padding(5.dp)
        .fillMaxWidth(),
    navController: NavController,
    viewModel: PhotoViewModel,
    description: String

    ){
    var importLoading = false
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uriList ->
            viewModel.uploadImageToFirebase(uriList[0], description)
        }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.padding(10.dp)
    ) {
        CameraButton(
            modifier = Modifier.width(150.dp),
            label = "Gallery",
            loading = importLoading,
            backgroundColor = MaterialTheme.colors.primary,
            color = Color.White,
            icon = Icons.Filled.PhotoAlbum
        ) {
           galleryLauncher.launch("image/*")
            importLoading = true
        }

        CameraButton(
            modifier = Modifier.width(150.dp),
            label = "Camera",
            loading = importLoading,
            backgroundColor = MaterialTheme.colors.primary,
            color = Color.White,
            icon = Icons.Filled.CameraAlt
        ) {
            importLoading = true
            navController.navigate(CameraScreens.CaptureView.name+ "/${description}")
        }
    }
}

@Composable
fun CameraButton(
    modifier: Modifier = Modifier,
    label: String,
    loading: Boolean,
    backgroundColor: Color,
    color: Color,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .padding(3.dp),
        enabled = !loading,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor)
    ){
        if (loading) CircularProgressIndicator(modifier = Modifier.size(25.dp))
        else Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Account Icon",
                    modifier = Modifier
                        .size(25.dp),
                    tint = Color.White
                )
            }

            Text(
                text = label,
                modifier = Modifier
                    .padding(5.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}