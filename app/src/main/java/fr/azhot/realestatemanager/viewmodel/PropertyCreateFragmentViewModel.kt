package fr.azhot.realestatemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import fr.azhot.realestatemanager.model.*
import fr.azhot.realestatemanager.repository.*
import kotlinx.coroutines.launch

class PropertyCreateFragmentViewModel(
    private val detailRepository: DetailRepository,
    private val addressRepository: AddressRepository,
    private val photoRepository: PhotoRepository,
    private val pointOfInterestRepository: PointOfInterestRepository,
    private val realtorRepository: RealtorRepository,
) :
    ViewModel() {

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

class PropertyCreateFragmentViewModelFactory(
    private val detailRepository: DetailRepository,
    private val addressRepository: AddressRepository,
    private val photoRepository: PhotoRepository,
    private val pointOfInterestRepository: PointOfInterestRepository,
    private val realtorRepository: RealtorRepository,
) :
    ViewModelProvider.Factory {

    // overridden functions
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PropertyCreateFragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PropertyCreateFragmentViewModel(
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