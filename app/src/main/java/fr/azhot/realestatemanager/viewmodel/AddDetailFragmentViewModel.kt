package fr.azhot.realestatemanager.viewmodel

import androidx.lifecycle.*
import fr.azhot.realestatemanager.model.*
import fr.azhot.realestatemanager.repository.PropertyRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class AddDetailFragmentViewModel(private val propertyRepository: PropertyRepository) : ViewModel() {

    // variables
    val realtorList: LiveData<List<Realtor>> = propertyRepository.realtorList.asLiveData()


    // functions
    fun insertDetail(detail: Detail) = viewModelScope.launch((IO)) {
        propertyRepository.insertDetail(detail)
    }

    fun updateDetail(detail: Detail) = viewModelScope.launch((IO)) {
        propertyRepository.updateDetail(detail)
    }

    fun insertAddress(address: Address) = viewModelScope.launch(IO) {
        propertyRepository.insertAddress(address)
    }

    fun updateAddress(address: Address) = viewModelScope.launch(IO) {
        propertyRepository.updateAddress(address)
    }

    fun insertPhoto(photo: Photo) = viewModelScope.launch(IO) {
        propertyRepository.insertPhoto(photo)
    }

    fun deletePhoto(photo: Photo) = viewModelScope.launch(IO) {
        propertyRepository.deletePhoto(photo)
    }

    fun insertPointOfInterest(pointOfInterest: PointOfInterest) = viewModelScope.launch(IO) {
        propertyRepository.insertPointOfInterest(pointOfInterest)
    }

    fun deletePointOfInterest(pointOfInterest: PointOfInterest) = viewModelScope.launch(IO) {
        propertyRepository.deletePointOfInterest(pointOfInterest)
    }

    fun insertRealtor(realtor: Realtor) = viewModelScope.launch(IO) {
        propertyRepository.insertRealtor(realtor)
    }

    fun getRealtorById(realtorId: String) =
        propertyRepository.getRealtorById(realtorId).asLiveData()

}

class AddDetailFragmentViewModelFactory(private val propertyRepository: PropertyRepository) :
    ViewModelProvider.Factory {

    // overridden functions
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddDetailFragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddDetailFragmentViewModel(propertyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}