package fr.azhot.realestatemanager.viewmodel

import androidx.lifecycle.*
import fr.azhot.realestatemanager.model.Property
import fr.azhot.realestatemanager.repository.DetailRepository

class PropertyListFragmentViewModel(detailRepository: DetailRepository) :
    ViewModel() {

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