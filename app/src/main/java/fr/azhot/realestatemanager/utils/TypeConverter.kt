package fr.azhot.realestatemanager.utils

import androidx.room.TypeConverter
import fr.azhot.realestatemanager.model.PropertyType

class TypeConverter {

    @TypeConverter
    fun fromPropertyType(propertyType: PropertyType): String {
        return propertyType.name
    }

    @TypeConverter
    fun toPropertyType(name: String): PropertyType {
        return PropertyType.valueOf(name)
    }
}