package fr.azhot.realestatemanager.repository

import fr.azhot.realestatemanager.database.dao.PointOfInterestDao
import fr.azhot.realestatemanager.model.PointOfInterest

class PointOfInterestRepository(private val pointOfInterestDao: PointOfInterestDao) {

    // functions
    suspend fun insertPointOfInterest(pointOfInterest: PointOfInterest) {
        pointOfInterestDao.insertPointOfInterest(pointOfInterest)
    }

    suspend fun updatePointOfInterest(pointOfInterest: PointOfInterest) {
        pointOfInterestDao.updatePointOfInterest(pointOfInterest)
    }

    suspend fun deletePointOfInterest(pointOfInterest: PointOfInterest) {
        pointOfInterestDao.deletePointOfInterest(pointOfInterest)
    }
}