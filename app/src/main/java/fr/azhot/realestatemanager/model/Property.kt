package fr.azhot.realestatemanager.model

import android.graphics.Bitmap
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
)