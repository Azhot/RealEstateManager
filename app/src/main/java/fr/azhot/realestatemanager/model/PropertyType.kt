package fr.azhot.realestatemanager.model

import java.util.*

enum class PropertyType {
    DUPLEX,
    LOFT,
    PENTHOUSE,
    MANSION;

    companion object {
        fun getValuesAsMutableListString(): MutableList<String> {
            val items = mutableListOf<String>()
            for (type in values()) {
                items.add(
                    type.toString()
                        .toLowerCase(Locale.ROOT)
                        .capitalize(Locale.ROOT)
                )
            }
            return items
        }
    }
}