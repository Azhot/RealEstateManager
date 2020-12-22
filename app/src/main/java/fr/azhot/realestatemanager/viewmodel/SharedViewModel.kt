package fr.azhot.realestatemanager.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import fr.azhot.realestatemanager.model.Address
import fr.azhot.realestatemanager.model.Photo
import fr.azhot.realestatemanager.model.Property

class SharedViewModel : ViewModel() {
    val liveProperty = MutableLiveData<Property>()
    val livePhotoMap = MutableLiveData<MutableMap<Bitmap, String>>()
    val liveAddress = MutableLiveData<Address>()
}