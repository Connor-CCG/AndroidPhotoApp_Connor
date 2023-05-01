package com.example.androidcameraapplication.db

import androidx.room.*
import com.example.androidcameraapplication.db.entities.Photo
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotosDao {
    @Query("SELECT * from photos_tbl")
    fun getAllPhotos(): Flow<List<Photo>>

    @Query("SELECT * from photos_tbl WHERE user_id = :userId AND photo_description = :description")
    fun getUserPhotosByDescription(userId: String, description: String): Flow<List<Photo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: Photo)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePhoto(photo: Photo)

//
//    @Query("DELETE FROM fav_tbl")
//    suspend fun deleteAllFavorites()
//
//    @Delete()
//    suspend fun deleteFavorite(favorite: Favorite)
//
//    @Query("SELECT * from settings_tbl")
//    fun getUnits(): Flow<List<Settings>>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertUnit(unit: Settings)
//
//    @Update(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun updateUnit(unit: Settings)
//
//    @Query("DELETE FROM settings_tbl")
//    suspend fun deleteAllUnits()
//
//    @Delete()
//    suspend fun deleteUnit(settings: Settings)

}