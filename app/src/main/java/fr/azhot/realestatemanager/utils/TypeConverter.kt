package fr.azhot.realestatemanager.utils

import androidx.room.TypeConverter
import fr.azhot.realestatemanager.model.PointOfInterestType
import fr.azhot.realestatemanager.model.PropertyType

class TypeConverter {

    @TypeConverter
    fun fromPropertyType(propertyType: PropertyType?): String? {
        return propertyType?.name
    }

    @TypeConverter
    fun toPropertyType(name: String?): PropertyType? {
        return name?.let { PropertyType.valueOf(name) }
    }

    @TypeConverter
    fun fromPointOfInterestType(pointOfInterestType: PointOfInterestType?): String? {
        return pointOfInterestType?.name
    }

    @TypeConverter
    fun toPointOfInterestType(name: String?): PointOfInterestType? {
        return name?.let {
            name.replace(" ", "_")
            PointOfInterestType.valueOf(name)
        }
    }
}