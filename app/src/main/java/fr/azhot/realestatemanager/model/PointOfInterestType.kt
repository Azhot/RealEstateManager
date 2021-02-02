package fr.azhot.realestatemanager.model

import java.util.*

/**
 * An enum representing the type of a [PointOfInterest].
 */
enum class PointOfInterestType {
    SCHOOL,
    TRAIN_STATION,
    BUSINESS,
    PARK;

    override fun toString(): String {
        return super.toString()
            .replace("_", " ")
            .toLowerCase(Locale.ROOT)
            .capitalize(Locale.ROOT)
    }
}