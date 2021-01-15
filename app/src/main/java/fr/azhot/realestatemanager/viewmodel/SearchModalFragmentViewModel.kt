package fr.azhot.realestatemanager.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fr.azhot.realestatemanager.model.MinMax
import fr.azhot.realestatemanager.repository.PropertyRepository

class SearchModalFragmentViewModel(private val propertyRepository: PropertyRepository) :
    ViewModel() {

    // functions
    fun getPriceBounds(): LiveData<MinMax> {
        return propertyRepository.getPriceBounds()
    }

    fun getSquareMetersBounds(): LiveData<MinMax> {
        return propertyRepository.getSquareMetersBounds()
    }

    fun getRoomsBounds(): LiveData<MinMax> {
        return propertyRepository.getRoomsBounds()
    }
}

class SearchModalFragmentViewModelFactory(private val propertyRepository: PropertyRepository) :
    ViewModelProvider.Factory {

    // overridden functions
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchModalFragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchModalFragmentViewModel(propertyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}