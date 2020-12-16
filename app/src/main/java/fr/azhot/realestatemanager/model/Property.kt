package fr.azhot.realestatemanager.model

import androidx.room.Embedded
import androidx.room.Relation

data class Property(
    @Embedded
    val detail: Detail,
    @Relation(
        parentColumn = "addressId",
        entityColumn = "addressId"
    )
    var address: Address?,
    @Relation(
        parentColumn = "detailId",
        entityColumn = "detailId"
    )
    val photoList: MutableList<Photo>,
    @Relation(
        parentColumn = "detailId",
        entityColumn = "detailId"
    )
    val pointOfInterestList: MutableList<PointOfInterest>?,
    @Relation(
        parentColumn = "realtorId",
        entityColumn = "realtorId"
    )
    var realtor: Realtor?,
)