package fr.azhot.realestatemanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import fr.azhot.realestatemanager.model.Photo

/**
 * Data Access Object for [Photo] entity.
 */
@Dao
interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPhoto(photo: Photo)

    @Update
    suspend fun updatePhoto(photo: Photo)

    @Delete
    suspend fun deletePhoto(photo: Photo)

    @Query(
        """SELECT MAX(photoListSize) 
                  FROM (SELECT detailId, COUNT(detailId) photoListSize
                  FROM photo_table
                  GROUP BY detailId)"""
    )
    fun getPhotoListMax(): LiveData<Int>
}