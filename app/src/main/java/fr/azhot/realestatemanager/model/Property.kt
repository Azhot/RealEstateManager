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
                    "75116",
                    "Paris",
                    "rue du Bison",
                    "15",
                    null,
                ),
                Address(
                    2L,
                    "House",
                    "78480",
                    "Verneuil-sur-seine",
                    "rue du Hameau",
                    "52",
                    "TER",
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
                            BitmapFactory.decodeResource(context.resources, R.drawable.thumb_living_room),
                            "Living-room too long"
                        ),
                        Photo(
                            BitmapFactory.decodeResource(context.resources, R.drawable.thumb_bathroom),
                            "Bathroom"
                        ),
                        Photo(
                            BitmapFactory.decodeResource(context.resources, R.drawable.thumb_bedroom),
                            "Bedroom"
                        ),
                        Photo(
                            BitmapFactory.decodeResource(context.resources, R.drawable.thumb_kitchen),
                            "Kitchen"
                        ),
                        Photo(
                            BitmapFactory.decodeResource(context.resources, R.drawable.thumb_hall),
                            "Hall"
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
                            BitmapFactory.decodeResource(context.resources, R.drawable.thumb_hall),
                            "Hall"
                        ),
                        Photo(
                            BitmapFactory.decodeResource(context.resources, R.drawable.thumb_bedroom),
                            "Kitchen"
                        ),
                        Photo(
                            BitmapFactory.decodeResource(context.resources, R.drawable.thumb_kitchen),
                            "Bedroom"
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