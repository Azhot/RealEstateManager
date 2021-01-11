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
    val sharedPhotoList: MutableList<Pair<Bitmap, String>> = mutableListOf()
    var sharedAddress: Address = Address()
    var sharedDetail: Detail = Detail()
    val sharedPointOfInterestList: MutableList<PointOfInterest> = mutableListOf()

    fun resetNewPropertyData() {
        sharedPhotoList.clear()
        sharedAddress = Address()
        sharedDetail = Detail()
        sharedPointOfInterestList.clear()
    }
}