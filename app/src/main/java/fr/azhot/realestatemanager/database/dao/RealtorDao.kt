package fr.azhot.realestatemanager.database.dao

import androidx.room.*
import fr.azhot.realestatemanager.model.Realtor

@Dao
interface RealtorDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRealtor(realtor: Realtor)

    @Update
    suspend fun updateRealtor(realtor: Realtor)

    @Delete
    suspend fun deleteRealtor(realtor: Realtor)
}