package fr.azhot.realestatemanager.model

import java.util.*

enum class PropertyType {
    DUPLEX,
    LOFT,
    PENTHOUSE,
    MANSION;

    override fun toString(): String {
        return super.toString().toLowerCase(Locale.ROOT).capitalize(Locale.ROOT)
    }

    companion object {
        fun toMutableList() = values().toMutableList()
    }
}