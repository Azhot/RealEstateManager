package fr.azhot.realestatemanager.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fr.azhot.realestatemanager.model.Property
import fr.azhot.realestatemanager.repository.DetailRepository

class PropertyDetailsFragmentViewModel(detailRepository: DetailRepository) :
    ViewModel() {

    val liveProperty: MutableLiveData<Property> = detailRepository.liveProperty
}

class PropertyDetailsFragmentViewModelFactory(private val detailRepository: DetailRepository) :
    ViewModelProvider.Factory {

    // overridden functions
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PropertyDetailsFragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PropertyDetailsFragmentViewModel(detailRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}