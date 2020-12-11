package fr.azhot.realestatemanager.database.dao

import androidx.room.*
import fr.azhot.realestatemanager.model.Property
import fr.azhot.realestatemanager.model.Realtor
import kotlinx.coroutines.flow.Flow

@Dao
interface RealtorDao {

    @Query("SELECT * FROM realtor_table")
    fun getRealtorList(): Flow<List<Realtor>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRealtor(realtor: Realtor)

    @Update
    suspend fun updateRealtor(realtor: Realtor)

    @Delete
    suspend fun deleteRealtor(realtor: Realtor)
}