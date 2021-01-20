package fr.azhot.realestatemanager.database.dao

import androidx.room.*
import fr.azhot.realestatemanager.model.Address
import kotlinx.coroutines.flow.Flow

@Dao
interface AddressDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAddress(address: Address)

    @Update
    suspend fun updateAddress(address: Address)

    @Delete
    suspend fun deleteAddress(address: Address)

    @Query("SELECT DISTINCT city FROM address_table")
    fun getCityList(): Flow<List<String>>
}