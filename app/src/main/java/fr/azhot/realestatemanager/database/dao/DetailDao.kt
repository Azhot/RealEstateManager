package fr.azhot.realestatemanager.database.dao

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import fr.azhot.realestatemanager.model.Detail
import fr.azhot.realestatemanager.model.MinMax
import fr.azhot.realestatemanager.model.Property
import kotlinx.coroutines.flow.Flow

@Dao
interface DetailDao {

    @Transaction
    @RawQuery
    fun getPropertyFilterableList(query: SimpleSQLiteQuery): Flow<List<Property>>

    @Query(
        """
                   SELECT *
                   FROM detail_table AS d
                   LEFT JOIN address_table AS a ON a.addressId = d.addressId
                   LEFT JOIN point_of_interest_table AS poi ON poi.detailId = d.detailId
                   LEFT JOIN realtor_table AS r ON r.realtorId = d.realtorId
               """
    )
    fun getPropertyList(): Cursor

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