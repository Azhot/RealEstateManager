package fr.azhot.realestatemanager.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import fr.azhot.realestatemanager.model.Property
import fr.azhot.realestatemanager.repository.DetailRepository

class PropertyListFragmentViewModel(detailRepository: DetailRepository) :
    ViewModel() {

    // variables
    // todo : point de débug to check data incoming
    val propertyList: LiveData<List<Property>> = detailRepository.propertyList.asLiveData()
}

class PropertyListFragmentViewModelFactory(private val detailRepository: DetailRepository) :
    ViewModelProvider.Factory {

    // overridden functions
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PropertyListFragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PropertyListFragmentViewModel(detailRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}