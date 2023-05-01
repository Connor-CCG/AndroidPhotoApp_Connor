package com.example.androidcameraapplication.model

data class FirebasePhoto(
    val photoDescription: String,
    var remoteUrl: String,
    val userId: String

){
    fun toMap(): MutableMap<String, Any>{
       return mutableMapOf(
           "user_id" to this.userId,
           "photo_description" to this.photoDescription,
           "photo_url" to this.remoteUrl
       )
    }
}