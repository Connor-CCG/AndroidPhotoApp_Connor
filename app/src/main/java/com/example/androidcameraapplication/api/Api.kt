package com.example.androidcameraapplication.api

import android.net.Uri
import android.util.Log
import com.example.androidcameraapplication.model.FirebasePhoto
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.coroutineScope
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class Api {
    suspend fun sync(data: FirebasePhoto, collectionPath: String){
        coroutineScope {
            FirebaseFirestore.getInstance().collection(collectionPath)
                .add(data)
                .addOnSuccessListener {
                    Log.d("Sync", "Success, you have synced the photo")
                }
                .addOnFailureListener {
                    Log.d("Sync","failure")
                }
        }
    }

    suspend fun uploadImage(uri: Uri): Uri?{
        return suspendCoroutine { continuation ->
            val storage = Firebase.storage
            val storageRef = storage.reference
            val imageUUID = UUID.randomUUID()
            val imageRef = storageRef.child("images/$imageUUID")
            imageRef.putFile(uri).addOnSuccessListener { snapshot ->
                snapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                    continuation.resume(uri)
                }
            }.addOnFailureListener{ error ->
                continuation.resumeWithException(error)
            }
        }
    }

    suspend fun query(collectionName: String, filter: Filter): Query {
        return suspendCoroutine { continuation ->
            val ref = Firebase.firestore.collection(collectionName)
                .where(filter)
            continuation.resume(ref)
        }
    }
}
