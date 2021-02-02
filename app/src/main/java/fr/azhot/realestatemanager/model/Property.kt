package fr.azhot.realestatemanager.model

import androidx.room.Embedded
import androidx.room.Relation

/**
 * POJO representing a real estate property.
 */
data class Property(
    @Embedded
    val detail: Detail,
    @Relation(
        parentColumn = "addressId",
        entityColumn = "addressId"
    )
    val address: Address,
    @Relation(
        parentColumn = "detailId",
        entityColumn = "detailId"
    )
    val photoList: MutableList<Photo>,
    @Relation(
        parentColumn = "detailId",
        entityColumn = "detailId"
    )
    val pointOfInterestList: MutableList<PointOfInterest>,
    @Relation(
        parentColumn = "realtorId",
        entityColumn = "realtorId"
    )
    var realtor: Realtor? = null,
)