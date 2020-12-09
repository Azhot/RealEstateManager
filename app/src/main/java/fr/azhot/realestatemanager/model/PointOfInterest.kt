package fr.azhot.realestatemanager.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "point_of_interest_table",
    foreignKeys = [
        ForeignKey(
            entity = Detail::class,
            parentColumns = arrayOf("detailId"),
            childColumns = arrayOf("detailId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Address::class,
            parentColumns = arrayOf("addressId"),
            childColumns = arrayOf("addressId"),
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
data class PointOfInterest(
    @PrimaryKey(autoGenerate = true)
    val pointOfInterestId: Long,
    val detailId: Long,
    val addressId: Long,
    val name: String,
) {
    constructor(
        detailId: Long,
        addressId: Long,
        name: String,
    ) : this(0, detailId, addressId, name)
}
