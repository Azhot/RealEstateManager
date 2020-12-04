package fr.azhot.realestatemanager.viewmodel

import androidx.lifecycle.ViewModel
import fr.azhot.realestatemanager.model.Property
import fr.azhot.realestatemanager.repository.PropertyRepository

class MainActivityViewModel : ViewModel() {

    fun setCurrentProperty(property: Property) = PropertyRepository.setCurrentProperty(property)

    fun getCurrentProperty() = PropertyRepository.getCurrentProperty()
}