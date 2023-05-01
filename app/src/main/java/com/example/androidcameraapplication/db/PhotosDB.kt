package com.example.androidcameraapplication.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.androidcameraapplication.db.entities.Photo

@Database(entities = [Photo::class], version = 2, exportSchema = false)
abstract class PhotosDB: RoomDatabase() {
    abstract fun photosDao(): PhotosDao
}