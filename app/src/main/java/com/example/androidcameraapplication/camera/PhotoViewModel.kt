package com.example.androidcameraapplication.camera

import android.content.Context
import android.graphics.*
import android.graphics.Bitmap.createBitmap
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.example.androidcameraapplication.camera.PhotoViews.GalleryOrientation
import com.example.androidcameraapplication.model.FirebasePhoto
import com.example.androidcameraapplication.repository.PhotosRepository
import com.google.firebase.firestore.ktx.getField
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.lang.Math.abs
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class PhotoViewModel @Inject constructor(private val repo: PhotosRepository): ViewModel() {
    var photos: List<FirebasePhoto> by mutableStateOf(listOf())
    var searchPhotos: List<FirebasePhoto> by mutableStateOf(listOf())
    var photoPreview: Uri? by mutableStateOf(null)
    var imageText: String by mutableStateOf("")
    var isExtractingText: Boolean by mutableStateOf(false)
    var galleryOrientation: GalleryOrientation by mutableStateOf(GalleryOrientation.Vertical)
    var photoDescription: String by mutableStateOf("Sample Description")


    //used for checking if cropped image matches what should've been captured
    var croppedImg: Bitmap? by mutableStateOf(null)
    var photoCropOffsetX: Float? by mutableStateOf(null)
    var photoCropOffsetY: Float? by mutableStateOf(null)

    suspend fun processMLKitImage(imageUri: Uri, context: Context){
        imageText = ""
        isExtractingText = true
        val img = getImageBitmap(context = context, url = imageUri.toString())
        val inputImage = img?.let { InputImage.fromBitmap(it, 0) }
        coroutineScope {
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            inputImage?.let {
                recognizer.process(it)
                    .addOnSuccessListener { visionText ->
                        // Task completed successfully
                        var maxArea = 0
                        var maxAreaText = ""
                        val resultText = visionText.textBlocks
                        for (block in resultText) {
                            for (line in block.lines) {
                                val lineFrame = line.boundingBox
                                if (lineFrame != null) {
                                    val lineWidth = lineFrame.right - lineFrame.left
                                    val lineHeight = lineFrame.top - lineFrame.bottom
                                    val lineArea = abs(lineWidth * lineHeight)
                                    if (lineArea > maxArea) {
                                        maxArea = lineArea
                                        maxAreaText = line.text
                                    }
                                }
                            }
                        }
                        imageText = maxAreaText
                        isExtractingText = false
                    }
                    .addOnFailureListener { e ->
                        // Task failed with an exception
                        Log.d("ML Kit Image error", "processMLKitImage: $e ")
                    }
            }
        }
    }

    //handle image capture
    fun handleImageCapture(uri: Uri, photoDescription: String) {
        photoPreview = uri
        uploadImageToFirebase(uri, photoDescription)
    }

    //convert uri to bitmap
    fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
        return Glide.with(context)
                .asBitmap()
                .load(uri) // sample image
                .placeholder(android.R.drawable.progress_indeterminate_horizontal) // need placeholder to avoid issue like glide annotations
                .error(android.R.drawable.stat_notify_error) // need error to avoid issue like glide annotations
                .submit()
                .get()
    }

    //This function uploads the image to firebase and inserts a photo object to firestore
    fun uploadImageToFirebase(uri: Uri, photoDescription: String){
        viewModelScope.launch {
            repo.uploadPhoto(photoUri = uri, photoDesc = photoDescription)
        }
    }

    //suspend function called on background thread to get image bitmap
    private suspend fun getImageBitmap(context: Context, url: String): Bitmap? {
        return suspendCoroutine { continuation ->
            Thread {
                val bitmap: Bitmap? =
                    Glide.with(context)
                        .asBitmap()
                        .load(url) // sample image
                        .placeholder(android.R.drawable.progress_indeterminate_horizontal) // need placeholder to avoid issue like glide annotations
                        .error(android.R.drawable.stat_notify_error) // need error to avoid issue like glide annotations
                        .submit()
                        .get()

                continuation.resume(bitmap)
            }.start()
        }
    }

    //Mask the bitmap with a rectangle
    fun clipImageFromBitmap(bitmap: Bitmap, x: Int, y: Int, width: Int, height: Int): Bitmap {
        val clippedBitmap = createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(clippedBitmap)
        if (photoCropOffsetX != null && photoCropOffsetY != null) {
            val srcRect = Rect(photoCropOffsetX!!.toInt(), photoCropOffsetY!!.toInt(), photoCropOffsetX!!.toInt() + width, photoCropOffsetY!!.toInt() + height)
            val destRect = Rect(photoCropOffsetX!!.toInt(), photoCropOffsetY!!.toInt(), photoCropOffsetX!!.toInt() + width, photoCropOffsetY!!.toInt() + height)
            canvas.drawBitmap(bitmap, srcRect, destRect, null)
        }
        return clippedBitmap
    }

    fun toggleGalleryOrientation(){
        galleryOrientation = if (galleryOrientation == GalleryOrientation.Vertical){
            GalleryOrientation.Horizontal
        } else {
            GalleryOrientation.Vertical
        }
    }

    suspend fun getLivePhotos(){
        repo.getUserPhotos()
            .addSnapshotListener{ querySnapshot, firebaseFirestoreException ->
                firebaseFirestoreException?.let {
                    return@addSnapshotListener
                }
                querySnapshot?.let {
                    photos = listOf()
                    for(doc in it){
                        val photoUrl = doc.getField<String>("remoteUrl")
                        val photoDescription = doc.getField<String>("photoDescription")
                        val userId = doc.getField<String>("userId")
                        if (photoUrl != null
                            && photoDescription != null
                            && userId != null){
                            photos = photos + FirebasePhoto(
                                photoDescription = photoDescription,
                                remoteUrl = photoUrl,
                                userId = userId
                            )
                        }
                    }
                }
            }
    }

    suspend fun getPhotosByDescription(photoDescription: String) {
        repo.getUserPhotosByDescription(photoDescription)
            .addSnapshotListener{ querySnapshot, firebaseFirestoreException ->
                firebaseFirestoreException?.let {
                    return@addSnapshotListener
                }
                querySnapshot?.let {
                    searchPhotos = listOf()
                    for(doc in it){
                        println("Doc")
                        val photoUrl = doc.getField<String>("remoteUrl")
                        val photoDescription = doc.getField<String>("photoDescription")
                        val userId = doc.getField<String>("userId")
                        if (photoUrl != null
                            && photoDescription != null
                            && userId != null){
                            searchPhotos = searchPhotos + FirebasePhoto(
                                photoDescription = photoDescription,
                                remoteUrl = photoUrl,
                                userId = userId
                            )
                            println("SearchPHotos $searchPhotos")
                        }
                    }
                }
            }
    }
}

