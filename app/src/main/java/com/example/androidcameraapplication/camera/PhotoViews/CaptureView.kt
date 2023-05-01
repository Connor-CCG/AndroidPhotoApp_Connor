package com.example.androidcameraapplication.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Lens
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.KFunction1

private fun takePhoto(
    filenameFormat: String,
    imageCapture: ImageCapture,
    outputDirectory: File,
    executor: Executor,
    photoDescription: String,
    context: Context,
    viewModel: PhotoViewModel,
    onImageCaptured: (Uri, String) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {

    val photoFile = File(
        outputDirectory,
        SimpleDateFormat(filenameFormat, Locale.US).format(System.currentTimeMillis()) + ".jpg"
    )
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()


    imageCapture.takePicture(outputOptions, executor, object: ImageCapture.OnImageSavedCallback {
        override fun onError(exception: ImageCaptureException) {
            Log.e("Take photo error", "Take photo error:", exception)
            onError(exception)
        }

        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            val savedUri = Uri.fromFile(photoFile)

            var bitmap = viewModel.uriToBitmap(context, savedUri)
            if (bitmap != null) {
                viewModel.croppedImg = viewModel.clipImageFromBitmap(
                    bitmap = bitmap,
                    x = 0,
                    y = 0,
                    height = 400,
                    width = 800
                )
            }

            println("bitmap ${bitmap.toString()}")
            //now, cut the bitmap using a path
            onImageCaptured(savedUri, photoDescription)
        }
    })
}


private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { cameraProvider ->
        cameraProvider.addListener({
            continuation.resume(cameraProvider.get())
        }, ContextCompat.getMainExecutor(this))
    }
}

@SuppressLint("RememberReturnType")
@Composable
fun CaptureView(
    outputDirectory: File,
    executor: Executor,
    photoDescription: String,
    onImageCaptured: (Uri, String) -> Unit,
    viewModel: PhotoViewModel,
    navController: NavController,
    onError: (ImageCaptureException) -> Unit
) {
    // config
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()
    var isAnimated by remember { mutableStateOf(false) }

    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {
        AndroidView({ previewView }, modifier = Modifier.fillMaxSize())

        Box(
            contentAlignment = Alignment.BottomStart,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp, start = 3.dp)
        ) {
            PhotoPreview(
                viewModel = viewModel,
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val boxWidth = 800f
            val boxHeight = 400f
            val offsetX = (size.width - boxWidth)/2f
            val offsetY = (size.height - (boxHeight * 3))/2f
            viewModel.photoCropOffsetX = offsetX
            viewModel.photoCropOffsetY = offsetY

            val path = Path().apply {
                addRect(
                    Rect(
                        offset = Offset(
                            x = offsetX,
                            y = offsetY,
                        ),
                        size = Size(boxWidth, boxHeight)
                    )
                )
            }

            clipPath(path, clipOp = ClipOp.Difference) {
                drawRoundRect(
                    color = Color.Black.copy(alpha = 0.6f),
                    cornerRadius = CornerRadius(0f, 0f)
                )
            }

        }
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize().padding(top = 10.dp),
        ) {
            Text(text = "Align reading within the box", color = Color.White,
            fontWeight = FontWeight.Bold)

        }

        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ){
            CameraButton(
                modifier = Modifier.width(100.dp),
                label = "Done",
                loading = false,
                backgroundColor = MaterialTheme.colors.primary,
                color = Color.White
            ) {
                navController.popBackStack()
            }
        }
        val context = LocalContext.current
        IconButton(
            modifier = Modifier.padding(bottom = 20.dp),
            onClick = {
                takePhoto(
                    filenameFormat = "yyyy-MM-dd-HH-mm-ss-SSS",
                    imageCapture = imageCapture,
                    outputDirectory = outputDirectory,
                    executor = executor,
                    photoDescription = photoDescription,
                    context = context,
                    viewModel = viewModel,
                    onImageCaptured = onImageCaptured,
                    onError = onError
                )
            },
            content = {
                Icon(
                    imageVector = Icons.Sharp.Lens,
                    contentDescription = "Take picture",
                    tint = Color.White,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(1.dp)
                        .border(1.dp, Color.White, CircleShape)
                )
            }
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PhotoPreview(
    viewModel: PhotoViewModel
){
    if (viewModel.photoPreview != null) {
        Box(
            contentAlignment = Alignment.TopEnd,
            modifier = Modifier.zIndex(1f)
        ) {
            Text(
                modifier = Modifier
                    .padding(0.dp)
                    .offset(x = 65.dp, y = (-85).dp)
                    .drawBehind {
                        drawCircle(
                            color = Color.Red,
                            radius = this.size.maxDimension
                        )
                    },
                text = "${viewModel.photos.size}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        GlideImage(
            model = viewModel.photoPreview,
            contentDescription = "Image Preview",
            modifier = Modifier
                .width(80.dp)
                .zIndex(0f)
                .clip(RectangleShape)
        )
    }
}