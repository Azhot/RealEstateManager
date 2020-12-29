package fr.azhot.realestatemanager.database.dao

import androidx.room.*
import fr.azhot.realestatemanager.model.PointOfInterest

@Dao
interface PointOfInterestDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPointOfInterest(pointOfInterest: PointOfInterest)

    @Update
    suspend fun updatePointOfInterest(pointOfInterest: PointOfInterest)

    @Delete
    suspend fun deletePointOfInterest(pointOfInterest: PointOfInterest)
}