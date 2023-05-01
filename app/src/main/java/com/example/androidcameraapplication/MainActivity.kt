package com.example.androidcameraapplication

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.androidcameraapplication.camera.PhotoViewModel
import com.example.androidcameraapplication.model.FirebasePhoto
import com.example.androidcameraapplication.navigation.CameraNavigation
import com.example.androidcameraapplication.ui.theme.AndroidCameraApplicationTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileDescriptor
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.reflect.KFunction1

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var cameraExecutor: ExecutorService
    private var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            //permission is granted, show the camera app
            shouldShowCamera.value = true
        } else {
            //permission is not granted, show a message
        }
    }

    //TODO takes URI of the image and returns bitmap
    fun uriToBitmap(selectedFileUri: Uri): Bitmap? {
        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(selectedFileUri, "r")
            val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return image
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                shouldShowCamera.value = true
                Log.d("Camera permission", "Permission previously granted")
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.CAMERA
            ) -> Log.d("Camera permission", "Show camera permissions dialog")

            else -> requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

//    private fun subscribeToRealtimeUpdates(photoViewModel: PhotoViewModel){
//        val auth: FirebaseAuth = Firebase.auth
//        println("auth user id ${auth.currentUser?.uid}")
//        val photosCollectionRef = Firebase.firestore.collection("photos")
//            .whereEqualTo("userId", auth.currentUser?.uid)
//        photosCollectionRef.addSnapshotListener{ querySnapshot, firebaseFirestoreException ->
//            firebaseFirestoreException?.let {
//                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
//                return@addSnapshotListener
//            }
//            querySnapshot?.let {
//                photoViewModel.photos = listOf()
//                for(doc in it){
//                    val photoUrl = doc.getField<String>("remoteUrl")
//                    val photoDescription = doc.getField<String>("photoDescription")
//                    val userId = doc.getField<String>("userId")
//                    if (photoUrl != null
//                        && photoDescription != null
//                        && userId != null){
//                        photoViewModel.photos = photoViewModel.photos + FirebasePhoto(
//                            photoDescription = photoDescription,
//                            remoteUrl = photoUrl,
//                            userId = userId
//                        )
//                    }
//
//                }
//            }
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestCameraPermission()
        cameraExecutor = Executors.newSingleThreadExecutor()

        setContent {
            val photoViewModel: PhotoViewModel = hiltViewModel()
            AndroidCameraApplicationTheme {
                CameraApp(
                    outputDirectory = getOutputDirectory(),
                    cameraExecutor = Executors.newSingleThreadExecutor(),
                    viewModel = photoViewModel,
                    uriToBitmap = ::uriToBitmap
                )
            }
        }

    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }

        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}

@Composable
fun CameraApp(
    outputDirectory: File,
    cameraExecutor: ExecutorService,
    viewModel: PhotoViewModel,
    uriToBitmap: KFunction1<Uri, Bitmap?>
){
    Surface() {
        Column {
            CameraNavigation(
                outputDirectory = outputDirectory,
                cameraExecutor = cameraExecutor,
                viewModel = viewModel
            )
        }
    }
}