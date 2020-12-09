package fr.azhot.realestatemanager

import android.app.Application
import fr.azhot.realestatemanager.database.AppDatabase
import fr.azhot.realestatemanager.repository.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class RealEstateManagerApplication : Application() {

    // variables
    private val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val detailRepository by lazy { DetailRepository(database.detailDao()) }
    val addressRepository by lazy { AddressRepository(database.addressDao()) }
    val photoRepository by lazy { PhotoRepository(database.photoDao()) }
    val pointOfInterestRepository by lazy { PointOfInterestRepository(database.pointOfInterestDao()) }
    val realtorRepository by lazy { RealtorRepository(database.realtorDao()) }

}