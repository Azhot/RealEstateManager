package fr.azhot.realestatemanager.repository

import fr.azhot.realestatemanager.database.dao.PhotoDao
import fr.azhot.realestatemanager.model.Photo

class PhotoRepository(private val photoDao: PhotoDao) {

    // functions
    suspend fun insertPhoto(photo: Photo) {
        photoDao.insertPhoto(photo)
    }

    suspend fun updatePhoto(photo: Photo) {
        photoDao.updatePhoto(photo)
    }

    suspend fun deletePhoto(photo: Photo) {
        photoDao.deletePhoto(photo)
    }
}