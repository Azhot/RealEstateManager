package fr.azhot.realestatemanager.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.azhot.realestatemanager.model.Address
import fr.azhot.realestatemanager.model.Photo

class SharedViewModel : ViewModel() {
    val mutablePhotoList = MutableLiveData<MutableList<Photo>>()
    val mutableAddress = MutableLiveData<Address>()
}