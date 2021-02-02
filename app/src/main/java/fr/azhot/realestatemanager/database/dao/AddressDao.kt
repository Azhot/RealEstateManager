package fr.azhot.realestatemanager.database.dao

import androidx.room.*
import fr.azhot.realestatemanager.model.Address
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for [Address] entity.
 */
@Dao
interface AddressDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAddress(address: Address)

    @Update
    suspend fun updateAddress(address: Address)

    @Delete
    suspend fun deleteAddress(address: Address)

    @Query("SELECT DISTINCT city FROM address_table WHERE city IS NOT NULL")
    fun getCityList(): Flow<List<String>>
}