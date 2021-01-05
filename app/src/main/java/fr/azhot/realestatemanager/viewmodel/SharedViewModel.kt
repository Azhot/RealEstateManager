package fr.azhot.realestatemanager.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.azhot.realestatemanager.model.Address
import fr.azhot.realestatemanager.model.Detail
import fr.azhot.realestatemanager.model.PointOfInterest
import fr.azhot.realestatemanager.model.Property

class SharedViewModel : ViewModel() {
    val liveProperty = MutableLiveData<Property>()
    var livePhotoMap = MutableLiveData<MutableMap<Bitmap, String>>()
    var liveAddress = MutableLiveData<Address>()
    var liveDetail = MutableLiveData<Detail>()
    var livePointOfInterestList = MutableLiveData<MutableList<PointOfInterest>>()

    init {
        liveAddress.value = Address()
        liveDetail.value = Detail()
        livePointOfInterestList.value = mutableListOf()
    }

    fun resetNewPropertyData() {
        livePhotoMap.value?.clear()
        liveAddress.value = Address()
        liveDetail.value = Detail()
        livePointOfInterestList.value?.clear()
    }
}