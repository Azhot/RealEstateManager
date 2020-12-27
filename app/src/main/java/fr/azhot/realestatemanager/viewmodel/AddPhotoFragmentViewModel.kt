package fr.azhot.realestatemanager.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.io.File

class AddPhotoFragmentViewModel : ViewModel() {

    // variables
    val liveCameraFile = MutableLiveData<File>()
}

class AddPhotoFragmentViewModelFactory : ViewModelProvider.Factory {

    // overridden functions
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddDetailFragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddPhotoFragmentViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}