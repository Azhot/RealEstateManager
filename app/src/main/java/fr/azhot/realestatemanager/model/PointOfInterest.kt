package fr.azhot.realestatemanager.model

import androidx.room.Embedded
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
        )
    ]
)
data class PointOfInterest(
    @PrimaryKey(autoGenerate = true)
    val pointOfInterestId: Long,
    val detailId: Long,
    val name: String,
    @Embedded
    val address: Address,
) {
    constructor(
        detailId: Long,
        name: String,
        address: Address,
    ) : this(0, detailId, name, address)
}
