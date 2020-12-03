package fr.azhot.realestatemanager.model

import android.content.Context
import android.graphics.BitmapFactory
import fr.azhot.realestatemanager.R
import java.util.*

data class Property(
    val id: Long,
    var type: PropertyType,
    var price: Int,
    var squareMeters: Int,
    var rooms: Int,
    var description: String,
    var photos: MutableList<Photo>,
    var address: Address,
    var nearbyPointsOfInterest: MutableList<Address>,
    var sold: Boolean,
    var entryDate: Date,
    var saleDate: Date?,
    var realtor: Realtor,
) {
    companion object {
        // todo: to be deleted
        @JvmStatic
        fun populatePropertyList(context: Context): List<Property> {
            val addresses = mutableListOf(
                Address(
                    1L,
                    "Apartment",
                    "78480",
                    "Verneuil-sur-seine",
                    "rue du Hameau",
                    "52",
                    "TER",
                ),
                Address(
                    2L,
                    "House",
                    "75116",
                    "Paris",
                    "rue du Bizon",
                    "15",
                    null,
                ),
            )

            val realtors = mutableListOf(
                Realtor(
                    1L,
                    "François",
                    "Jouvelot",
                ),
                Realtor(
                    2L,
                    "Gisèle",
                    "Sellier",
                ),
            )

            return listOf(
                Property(
                    1L,
                    PropertyType.FLAT,
                    1500000,
                    150,
                    5,
                    "This is a big flat",
                    mutableListOf(
                        Photo(
                            BitmapFactory.decodeResource(context.resources, R.drawable.living_room),
                            "Living-room too long"
                        ),
                        Photo(
                            BitmapFactory.decodeResource(context.resources, R.drawable.living_room),
                            "Bathroom"
                        ),
                        Photo(
                            BitmapFactory.decodeResource(context.resources, R.drawable.living_room),
                            "Bedroom"
                        ),
                        Photo(
                            BitmapFactory.decodeResource(context.resources, R.drawable.living_room),
                            "Kitchen"
                        ),
                        Photo(
                            BitmapFactory.decodeResource(context.resources, R.drawable.living_room),
                            "Lobby"
                        ),
                    ),
                    addresses[0],
                    addresses,
                    false,
                    Date(),
                    null,
                    realtors[0],
                ),
                Property(
                    2L,
                    PropertyType.HOUSE,
                    2500000,
                    250,
                    9,
                    "This is a big house",
                    mutableListOf(
                        Photo(
                            BitmapFactory.decodeResource(context.resources, R.drawable.living_room),
                            "Living-room"
                        ),
                        Photo(
                            BitmapFactory.decodeResource(context.resources, R.drawable.living_room),
                            "Living-room"
                        ),
                        Photo(
                            BitmapFactory.decodeResource(context.resources, R.drawable.living_room),
                            "Living-room"
                        ),
                    ),
                    addresses[1],
                    addresses,
                    false,
                    Date(),
                    null,
                    realtors[1],
                )
            )
        }
    }
}