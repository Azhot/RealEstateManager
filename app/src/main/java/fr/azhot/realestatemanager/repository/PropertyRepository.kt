package fr.azhot.realestatemanager.repository

import androidx.lifecycle.LiveData
import fr.azhot.realestatemanager.database.dao.*
import fr.azhot.realestatemanager.model.*
import kotlinx.coroutines.flow.Flow

class PropertyRepository(
    private val photoDao: PhotoDao,
    private val addressDao: AddressDao,
    private val pointOfInterestDao: PointOfInterestDao,
    private val realtorDao: RealtorDao,
    private val detailDao: DetailDao,
) {
    // variables
    val realtorList: Flow<List<Realtor>> = realtorDao.getRealtorList()
    val cityList: Flow<List<String>> = addressDao.getCityList()


    // functions
    suspend fun insertPhoto(photo: Photo) {
        photoDao.insertPhoto(photo)
    }

    suspend fun deletePhoto(photo: Photo) {
        photoDao.deletePhoto(photo)
    }

    suspend fun insertAddress(address: Address) {
        return addressDao.insertAddress(address)
    }

    suspend fun updateAddress(address: Address) {
        return addressDao.updateAddress(address)
    }

    suspend fun insertPointOfInterest(pointOfInterest: PointOfInterest) {
        pointOfInterestDao.insertPointOfInterest(pointOfInterest)
    }

    suspend fun deleteAllPointsOfInterest(detailId: String) {
        pointOfInterestDao.deleteAllPointsOfInterest(detailId)
    }

    fun getRealtorById(realtorId: String): Flow<Realtor> {
        return realtorDao.getRealtorById(realtorId)
    }

    suspend fun insertRealtor(realtor: Realtor) {
        realtorDao.insertRealtor(realtor)
    }

    suspend fun insertDetail(detail: Detail) {
        detailDao.insertDetail(detail)
    }

    suspend fun updateDetail(detail: Detail) {
        detailDao.updateDetail(detail)
    }

    fun getPropertyFilterableList(propertySearch: PropertySearch): Flow<List<Property>> {
        return detailDao.getPropertyFilterableList(
            propertySearch.propertyType,
            propertySearch.city,
            propertySearch.priceRange?.get(0)?.toInt(),
            propertySearch.priceRange?.get(1)?.toInt(),
            propertySearch.squareMetersRange?.get(0)?.toInt(),
            propertySearch.squareMetersRange?.get(1)?.toInt(),
            propertySearch.roomsRange?.get(0)?.toInt(),
            propertySearch.roomsRange?.get(1)?.toInt(),
            propertySearch.photoListSize,
            propertySearch.entryDateRange?.first,
            propertySearch.entryDateRange?.second,
            propertySearch.saleDateRange?.first,
            propertySearch.saleDateRange?.second,
            propertySearch.pointOfInterestType,
            propertySearch.realtor?.realtorId,
        )
    }

    fun getPriceBounds(): LiveData<MinMax> {
        return detailDao.getPriceBounds()
    }

    fun getSquareMetersBounds(): LiveData<MinMax> {
        return detailDao.getSquareMetersBounds()
    }

    fun getRoomsBounds(): LiveData<MinMax> {
        return detailDao.getRoomsBounds()
    }

    fun getPhotoListMax(): LiveData<Int> {
        return photoDao.getPhotoListMax()
    }
}