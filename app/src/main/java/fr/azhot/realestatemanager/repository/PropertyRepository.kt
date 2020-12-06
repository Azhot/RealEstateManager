package fr.azhot.realestatemanager.repository

import android.content.Context
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import fr.azhot.realestatemanager.R
import fr.azhot.realestatemanager.model.*
import java.util.*

object PropertyRepository {

    // variables
    private var mProperty: MutableLiveData<Property> = MutableLiveData()


    // functions
    fun getCurrentProperty(): LiveData<Property> = mProperty

    fun setCurrentProperty(property: Property) {
        mProperty.value = property
    }

    // todo: to be deleted
    fun populatePropertyList(context: Context): List<Property> {
        val addresses = mutableListOf(
            Address(
                1L,
                "75116",
                "Paris",
                "rue du Test",
                "15",
                "Ter",
            ),
        )

        val realtors = mutableListOf(
            Realtor(
                1L,
                "Michel",
                "Tropfort",
            ),
        )

        return listOf(
            Property(
                1L,
                PropertyType.FLAT,
                750000,
                150,
                5,
                "This is a big flat",
                mutableListOf(
                    Photo(
                        BitmapFactory.decodeResource(
                            context.resources,
                            R.drawable.thumb_living_room
                        ),
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
                addresses[0],
                addresses,
                false,
                Date(),
                null,
                realtors[0],
            ),
            Property(
                2L,
                PropertyType.DUPLEX,
                1500000,
                200,
                7,
                "This is a big house",
                mutableListOf(
                    Photo(
                        BitmapFactory.decodeResource(context.resources, R.drawable.thumb_kitchen),
                        "Kitchen"
                    ),
                    Photo(
                        BitmapFactory.decodeResource(context.resources, R.drawable.thumb_bathroom),
                        "Bathroom"
                    ),
                    Photo(
                        BitmapFactory.decodeResource(context.resources, R.drawable.thumb_kitchen),
                        "Bedroom"
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
                PropertyType.PENTHOUSE,
                2500000,
                250,
                9,
                "This is a big house",
                mutableListOf(
                    Photo(
                        BitmapFactory.decodeResource(context.resources, R.drawable.thumb_bathroom),
                        "Bathroom"
                    ),
                    Photo(
                        BitmapFactory.decodeResource(context.resources, R.drawable.thumb_kitchen),
                        "Bedroom"
                    ),
                    Photo(
                        BitmapFactory.decodeResource(context.resources, R.drawable.thumb_kitchen),
                        "Kitchen"
                    ),
                ),
                addresses[0],
                addresses,
                false,
                Date(),
                null,
                realtors[0],
            )
        )
    }
}