package com.example.androidcameraapplication.model

data class FirebaseUser(
    val userId: String,
    val name: String
){
    fun toMap(): MutableMap<String, Any>{
        return mutableMapOf(
            "user_id" to this.userId,
            "name" to this.name
        )
    }
}