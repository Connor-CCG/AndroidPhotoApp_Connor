package com.example.androidcameraapplication.di

import android.content.Context
import androidx.room.Room
import com.example.androidcameraapplication.api.Api
import com.example.androidcameraapplication.db.PhotosDB
import com.example.androidcameraapplication.db.PhotosDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
//dependency injection occurs here
object AppModule {
    //provide the api
    @Singleton
    @Provides
    fun provideApi() = Api()

    //provide the db
    @Provides
    @Singleton
    fun providePhotosDatabase(@ApplicationContext context: Context): PhotosDB
            = Room.databaseBuilder(
                context,
                PhotosDB::class.java,
                "photos_database"
            ).fallbackToDestructiveMigration().build()

    //provide the data access object
    @Singleton
    @Provides
    fun providePhotosDao(photosDb: PhotosDB): PhotosDao = photosDb.photosDao()
}