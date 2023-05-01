package com.example.androidcameraapplication.repository

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.example.androidcameraapplication.api.Api
import com.example.androidcameraapplication.camera.PhotoViewModel
import com.example.androidcameraapplication.db.PhotosDao
import com.example.androidcameraapplication.db.entities.Photo
import com.example.androidcameraapplication.model.FirebasePhoto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PhotosRepository @Inject constructor(private val api: Api) {
    private val auth: FirebaseAuth = Firebase.auth
    suspend fun uploadPhoto(
        photoUri: Uri,
        photoDesc: String
    ) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            //upload to firebase storage
            val remoteUrl = api.uploadImage(photoUri)
            val photo =
            FirebasePhoto(
                photoDescription = photoDesc,
                userId = userId,
                remoteUrl = remoteUrl.toString()
            )
            coroutineScope {
                api.sync(data = photo, collectionPath = "photos")
            }
        }
    }

    suspend fun getUserPhotos(): Query {
        val userId = auth.currentUser?.uid
        return api.query(
            collectionName = "photos",
            filter = Filter.equalTo("userId", userId)
        )
    }

    suspend fun getUserPhotosByDescription(photoDescription: String): Query {
        println("photo description $photoDescription")
        val userId = auth.currentUser?.uid
        return api.query(
            collectionName = "photos",
            filter = Filter.and(
                Filter.equalTo("userId", userId),
                Filter.equalTo("photoDescription", photoDescription)
            )
        )
    }
}