package fr.azhot.realestatemanager.viewmodel

import androidx.lifecycle.*
import fr.azhot.realestatemanager.model.*
import fr.azhot.realestatemanager.repository.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddDetailFragmentViewModel(
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
    fun insertDetail(detail: Detail) = viewModelScope.launch((IO)) {
        detailRepository.insertDetail(detail)
    }

    fun insertAddress(address: Address) = viewModelScope.launch(IO) {
        addressRepository.insertAddress(address)
    }

    fun insertPhoto(photo: Photo) = viewModelScope.launch(IO) {
        photoRepository.insertPhoto(photo)
    }

    fun insertPointOfInterest(pointOfInterest: PointOfInterest) = viewModelScope.launch(IO) {
        pointOfInterestRepository.insertPointOfInterest(pointOfInterest)
    }

    fun insertRealtor(realtor: Realtor) = viewModelScope.launch(IO) {
        realtorRepository.insertRealtor(realtor)
    }

    fun getRealtorById(realtorId: String, doSomethingWithRealtor: (Realtor) -> Unit) =
        viewModelScope.launch(IO) {
            val realtor = realtorRepository.getRealtorById(realtorId)
            withContext(Main) {
                doSomethingWithRealtor(realtor)
            }
        }
}

class AddDetailFragmentViewModelFactory(
    private val detailRepository: DetailRepository,
    private val addressRepository: AddressRepository,
    private val photoRepository: PhotoRepository,
    private val pointOfInterestRepository: PointOfInterestRepository,
    private val realtorRepository: RealtorRepository,
) :
    ViewModelProvider.Factory {

    // overridden functions
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddDetailFragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddDetailFragmentViewModel(
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