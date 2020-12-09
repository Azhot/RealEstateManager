package fr.azhot.realestatemanager.viewmodel

import androidx.lifecycle.*
import fr.azhot.realestatemanager.model.Property
import fr.azhot.realestatemanager.repository.DetailRepository

class MainActivityViewModel(detailRepository: DetailRepository) : ViewModel() {

    // variables
    val liveProperty: MutableLiveData<Property> = detailRepository.liveProperty


    // functions
    fun setLiveProperty(property: Property) {
        liveProperty.value = property
    }
}

class MainActivityViewModelFactory(private val detailRepository: DetailRepository) :
    ViewModelProvider.Factory {

    // overridden functions
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainActivityViewModel(detailRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}