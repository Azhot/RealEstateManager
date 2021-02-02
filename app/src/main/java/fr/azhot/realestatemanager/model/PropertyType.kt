package fr.azhot.realestatemanager.model

import java.util.*

/**
 * An enum representing the type of a [Property].
 */
enum class PropertyType {
    DUPLEX,
    LOFT,
    PENTHOUSE,
    MANSION;

    override fun toString(): String {
        return super.toString().toLowerCase(Locale.ROOT).capitalize(Locale.ROOT)
    }
}