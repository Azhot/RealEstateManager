package fr.azhot.realestatemanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import fr.azhot.realestatemanager.model.Detail
import fr.azhot.realestatemanager.model.MinMax
import fr.azhot.realestatemanager.model.Property
import fr.azhot.realestatemanager.model.PropertyType
import kotlinx.coroutines.flow.Flow

@Dao
interface DetailDao {

    @Transaction
    @Query(
        """SELECT d.*
                FROM detail_table d
                LEFT JOIN address_table a ON d.addressId = a.addressId
                LEFT JOIN photo_table ph ON d.detailId = ph.detailId
                LEFT JOIN point_of_interest_table poi ON d.detailId = poi.detailId 
                LEFT JOIN realtor_table r ON d.realtorId = r.realtorId
                WHERE (:propertyType IS NULL or d.propertyType = :propertyType)
                AND (:minPrice IS NULL or d.price >= :minPrice)
                AND (:maxPrice IS NULL or d.price <= :maxPrice)
                AND (:minSquareMeters IS NULL or d.squareMeters >= :minSquareMeters)
                AND (:maxSquareMeters IS NULL or d.squareMeters <= :maxSquareMeters)
                AND (:minRooms IS NULL or d.rooms >= :minRooms)
                AND (:maxRooms IS NULL or d.rooms <= :maxRooms)
                GROUP BY d.detailId"""
    )
    fun getPropertyFilterableList(
        propertyType: PropertyType?,
        minPrice: Int?,
        maxPrice: Int?,
        minSquareMeters: Int?,
        maxSquareMeters: Int?,
        minRooms: Int?,
        maxRooms: Int?,
    ): Flow<List<Property>>

    @Query("SELECT MIN(price) as min, MAX(price) as max FROM detail_table")
    fun getPriceBounds(): LiveData<MinMax>

    @Query("SELECT MIN(squareMeters) as min, MAX(squareMeters) as max FROM detail_table")
    fun getSquareMetersBounds(): LiveData<MinMax>

    @Query("SELECT MIN(rooms) as min, MAX(rooms) as max FROM detail_table")
    fun getRoomsBounds(): LiveData<MinMax>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDetail(detail: Detail)

    @Update
    suspend fun updateDetail(detail: Detail)

    @Delete
    suspend fun deleteDetail(detail: Detail)
}