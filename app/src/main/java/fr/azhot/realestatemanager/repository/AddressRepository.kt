package fr.azhot.realestatemanager.repository

import fr.azhot.realestatemanager.database.dao.AddressDao
import fr.azhot.realestatemanager.model.Address

class AddressRepository(private val addressDao: AddressDao) {

    // functions
    suspend fun insertAddress(address: Address) {
        return addressDao.insertAddress(address)
    }

    suspend fun updateAddress(address: Address) {
        return addressDao.updateAddress(address)
    }

    suspend fun deleteAddress(address: Address) {
        return addressDao.deleteAddress(address)
    }
}