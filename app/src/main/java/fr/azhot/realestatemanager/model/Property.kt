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
    val address: Address?,
    @Relation(
        parentColumn = "detailId",
        entityColumn = "detailId"
    )
    val photos: List<Photo>,
    @Relation(
        parentColumn = "detailId",
        entityColumn = "detailId"
    )
    val pointsOfInterest: List<PointOfInterest>?,
    @Relation(
        parentColumn = "realtorId",
        entityColumn = "realtorId"
    )
    val realtor: Realtor?,
)