package fr.azhot.realestatemanager.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.azhot.realestatemanager.model.*

class SharedViewModel : ViewModel() {
    val liveProperty = MutableLiveData<Property>()
    var livePropertySearch = MutableLiveData<PropertySearch>()
    val sharedPhotoList: MutableList<Pair<Bitmap, String>>
    var sharedAddress: Address
    var sharedDetail: Detail
    val sharedPointOfInterestList: MutableList<PointOfInterest>

    init {
        livePropertySearch.value = PropertySearch()
        sharedPhotoList = mutableListOf()
        sharedAddress = Address()
        sharedDetail = Detail(addressId = sharedAddress.addressId)
        sharedPointOfInterestList = mutableListOf()
    }

    fun resetNewPropertyData() {
        sharedPhotoList.clear()
        sharedAddress = Address()
        sharedDetail = Detail(addressId = sharedAddress.addressId)
        sharedPointOfInterestList.clear()
    }
}