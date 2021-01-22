package fr.azhot.realestatemanager.model

import java.util.*

enum class PointOfInterestType {
    SCHOOL,
    TRAIN_STATION,
    BUSINESS,
    MEDICAL,
    PARK,
    POST_OFFICE;

    override fun toString(): String {
        return super.toString()
            .replace("_", " ")
            .toLowerCase(Locale.ROOT)
            .capitalize(Locale.ROOT)
    }
}