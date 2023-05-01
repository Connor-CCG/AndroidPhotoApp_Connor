package com.example.androidcameraapplication.db.entities

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "photos_tbl")
data class Photo(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "user_id")
    val user_id: String,
    @ColumnInfo(name = "photo_uri")
    val photo_uri: String,
    @ColumnInfo(name = "remote_url")
    var remote_url: String? = null,
    @ColumnInfo(name = "photo_description")
    val photo_description: String,
    @ColumnInfo(name = "is_synced")
    var is_synced: Boolean = false
//    @ColumnInfo(name = "sync_date")
//    val sync_date: Date = Date()


){
    fun toMap(): MutableMap<String, Any?>{
        return mutableMapOf(
            "user_id" to this.user_id,
            "description" to this.photo_description,
            "photo_uri" to this.photo_uri,
            "remote_url" to this.remote_url
        )
    }
}

