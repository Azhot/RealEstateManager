package fr.azhot.realestatemanager.viewmodel

import androidx.lifecycle.*
import fr.azhot.realestatemanager.model.*
import fr.azhot.realestatemanager.repository.*
import kotlinx.coroutines.launch

class AddPropertyActivityViewModel(
    private val detailRepository: DetailRepository,
    private val addressRepository: AddressRepository,
    private val photoRepository: PhotoRepository,
    private val pointOfInterestRepository: PointOfInterestRepository,
    private val realtorRepository: RealtorRepository,
) :
    ViewModel() {

    // variables
    val realtorList: LiveData<List<Realtor>> = realtorRepository.realtorList.asLiveData()


    // functions
    fun insertDetail(detail: Detail) = viewModelScope.launch {
        detailRepository.insertDetail(detail)
    }

    fun insertAddress(address: Address) = viewModelScope.launch {
        addressRepository.insertAddress(address)
    }

    fun insertPhoto(photo: Photo) = viewModelScope.launch {
        photoRepository.insertPhoto(photo)
    }

    fun insertPointOfInterest(pointOfInterest: PointOfInterest) = viewModelScope.launch {
        pointOfInterestRepository.insertPointOfInterest(pointOfInterest)
    }

    fun insertRealtor(realtor: Realtor) = viewModelScope.launch {
        realtorRepository.insertRealtor(realtor)
    }
}

class AddPropertyActivityViewModelFactory(
    private val detailRepository: DetailRepository,
    private val addressRepository: AddressRepository,
    private val photoRepository: PhotoRepository,
    private val pointOfInterestRepository: PointOfInterestRepository,
    private val realtorRepository: RealtorRepository,
) :
    ViewModelProvider.Factory {

    // overridden functions
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddPropertyActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddPropertyActivityViewModel(
                detailRepository,
                addressRepository,
                photoRepository,
                pointOfInterestRepository,
                realtorRepository,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}