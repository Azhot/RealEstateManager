package fr.azhot.realestatemanager.model

import java.util.*

enum class PointOfInterestType {
    SCHOOL,
    TRAIN_STATION,
    SHOP,
    POST_OFFICE;

    override fun toString(): String {
        return super.toString()
            .replace("_", " ")
            .toLowerCase(Locale.ROOT)
            .capitalize(Locale.ROOT)
    }
}