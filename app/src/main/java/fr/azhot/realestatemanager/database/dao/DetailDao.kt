package fr.azhot.realestatemanager.database.dao

import androidx.room.*
import fr.azhot.realestatemanager.model.Detail
import fr.azhot.realestatemanager.model.Property
import kotlinx.coroutines.flow.Flow

@Dao
interface DetailDao {

    @Transaction
    @Query("SELECT * FROM detail_table")
    fun getPropertyList(): Flow<List<Property>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDetail(detail: Detail)

    @Update
    suspend fun updateDetail(detail: Detail)

    @Delete
    suspend fun deleteDetail(detail: Detail)
}