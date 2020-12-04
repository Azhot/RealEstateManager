package fr.azhot.realestatemanager.viewmodel

import androidx.lifecycle.ViewModel
import fr.azhot.realestatemanager.repository.PropertyRepository

class PropertyDetailsFragmentViewModel : ViewModel() {

    fun getCurrentProperty() = PropertyRepository.getCurrentProperty()
}