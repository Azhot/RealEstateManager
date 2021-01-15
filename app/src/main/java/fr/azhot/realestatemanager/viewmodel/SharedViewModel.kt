package fr.azhot.realestatemanager.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.azhot.realestatemanager.model.*

class SharedViewModel : ViewModel() {
    val liveProperty = MutableLiveData<Property>()
    var livePropertySearch = MutableLiveData<PropertySearch>()
    val sharedPhotoList: MutableList<Pair<Bitmap, String>> = mutableListOf()
    var sharedAddress: Address = Address()
    var sharedDetail: Detail = Detail()
    val sharedPointOfInterestList: MutableList<PointOfInterest> = mutableListOf()

    init {
        livePropertySearch.value = PropertySearch()
    }

    fun resetNewPropertyData() {
        sharedPhotoList.clear()
        sharedAddress = Address()
        sharedDetail = Detail()
        sharedPointOfInterestList.clear()
    }
}