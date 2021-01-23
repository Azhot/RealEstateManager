package fr.azhot.realestatemanager.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import fr.azhot.realestatemanager.model.Property
import fr.azhot.realestatemanager.model.PropertySearch
import fr.azhot.realestatemanager.repository.PropertyRepository

class MapFragmentViewModel(private val propertyRepository: PropertyRepository) :
    ViewModel() {

    // functions
    fun getPropertyList(): LiveData<List<Property>> {
        return propertyRepository.getPropertyFilterableList(PropertySearch()).asLiveData()
    }
}

class MapFragmentViewModelFactory(private val propertyRepository: PropertyRepository) :
    ViewModelProvider.Factory {

    // overridden functions
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapFragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MapFragmentViewModel(propertyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}