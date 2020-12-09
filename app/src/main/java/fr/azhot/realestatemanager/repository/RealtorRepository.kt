package fr.azhot.realestatemanager.repository

import fr.azhot.realestatemanager.database.dao.RealtorDao
import fr.azhot.realestatemanager.model.Realtor

class RealtorRepository(private val realtorDao: RealtorDao) {

    // functions
    suspend fun insertRealtor(realtor: Realtor) {
        realtorDao.insertRealtor(realtor)
    }

    suspend fun updateRealtor(realtor: Realtor) {
        realtorDao.updateRealtor(realtor)
    }

    suspend fun deleteRealtor(realtor: Realtor) {
        realtorDao.deleteRealtor(realtor)
    }
}