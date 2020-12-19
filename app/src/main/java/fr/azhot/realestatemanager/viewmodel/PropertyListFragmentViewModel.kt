package fr.azhot.realestatemanager.viewmodel

import androidx.lifecycle.*
import fr.azhot.realestatemanager.model.Property
import fr.azhot.realestatemanager.repository.DetailRepository

class PropertyListFragmentViewModel(detailRepository: DetailRepository) :
    ViewModel() {

    // variables
    // todo : point de débug to check data incoming
    val propertyList: LiveData<List<Property>> = detailRepository.propertyList.asLiveData()
    val liveProperty: MutableLiveData<Property> = detailRepository.liveProperty


    // functions
    fun setLiveProperty(property: Property) {
        liveProperty.value = property
    }
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