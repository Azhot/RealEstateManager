package fr.azhot.realestatemanager.repository

import androidx.lifecycle.MutableLiveData
import fr.azhot.realestatemanager.database.dao.DetailDao
import fr.azhot.realestatemanager.model.Detail
import fr.azhot.realestatemanager.model.Property
import kotlinx.coroutines.flow.Flow

class DetailRepository(private val detailDao: DetailDao) {

    // variables
    val propertyList: Flow<List<Property>> = detailDao.getPropertyList()


    // functions
    suspend fun insertDetail(detail: Detail) {
        detailDao.insertDetail(detail)
    }

    suspend fun updateDetail(detail: Detail) {
        detailDao.updateDetail(detail)
    }

    suspend fun deleteDetail(detail: Detail) {
        detailDao.deleteDetail(detail)
    }
}