package fr.azhot.realestatemanager

import android.app.Application
import fr.azhot.realestatemanager.database.AppDatabase
import fr.azhot.realestatemanager.repository.PropertyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class RealEstateManagerApplication : Application() {

    // variables
    private val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val propertyRepository by lazy {
        PropertyRepository(
            database.photoDao(),
            database.addressDao(),
            database.pointOfInterestDao(),
            database.realtorDao(),
            database.detailDao()
        )
    }

}