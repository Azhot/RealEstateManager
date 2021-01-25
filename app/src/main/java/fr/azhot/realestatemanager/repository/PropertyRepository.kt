package fr.azhot.realestatemanager.repository

import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
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
        StringBuilder().run {
            append(
                """
                    SELECT DISTINCT d.*
                    FROM detail_table d
                    LEFT JOIN address_table a ON a.addressId = d.addressId
                    LEFT JOIN (SELECT photos.detailId, COUNT(photos.detailId) photosCount 
                        FROM photo_table photos
                        GROUP BY photos.detailId
                    ) ph ON ph.detailId = d.detailId
                    LEFT JOIN point_of_interest_table poi ON poi.detailId = d.detailId
                    LEFT JOIN realtor_table r ON r.realtorId = d.realtorId
                """
            )
            propertySearch.run {
                append("WHERE ('${propertyType}' = 'null' OR d.propertyType = '${propertyType?.name}') ")
                city?.let { append("AND (a.city = '${city}') ") }
                priceRange?.let {
                    append("AND d.price >= '${it[0].toInt()}' ")
                    append("AND d.price <= '${it[1].toInt()}' ")
                }
                squareMetersRange?.let {
                    append("AND d.squareMeters >= '${it[0].toInt()}' ")
                    append("AND d.squareMeters <= '${it[1].toInt()}' ")
                }
                roomsRange?.let {
                    append("AND d.rooms >= '${it[0].toInt()}' ")
                    append("AND d.rooms <= '${it[1].toInt()}' ")
                }
                entryDateRange?.let {
                    append("AND d.entryTimeStamp >= '${it.first}' ")
                    append("AND d.entryTimeStamp <= '${it.second}' ")
                }
                saleDateRange?.let {
                    append("AND d.saleTimeStamp >= '${it.first}' ")
                    append("AND d.saleTimeStamp <= '${it.second}' ")
                }
                pointOfInterestTypeList?.let {
                    if (it.isNotEmpty()) {
                        append("AND poi.detailId IN (")
                        append(
                            """
                                SELECT poi0.detailId
                                FROM point_of_interest_table as poi0
                                WHERE poi0.pointOfInterestType = '${it[0].name}'
                            """
                        )
                        for (i in 1..it.lastIndex) {
                            append(
                                """
                                    INTERSECT
                                    SELECT poi${i}.detailId
                                    FROM point_of_interest_table as poi${i}
                                    WHERE poi${i}.pointOfInterestType = '${it[i].name}'
                                """
                            )
                        }
                        append(") ")
                    }
                }
                realtor?.let { append("AND (d.realtorId = '${it.realtorId}') ") }
                append("GROUP BY d.detailId ")
                photoListSize?.let { append("""HAVING photosCount >= ${it.toInt()}""") }
            }
            return detailDao.getPropertyFilterableList(SimpleSQLiteQuery(this.toString()))
        }
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