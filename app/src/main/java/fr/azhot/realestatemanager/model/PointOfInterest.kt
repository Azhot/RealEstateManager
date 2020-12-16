package fr.azhot.realestatemanager.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

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
    @PrimaryKey
    val pointOfInterestId: String = UUID.randomUUID().toString(),
    val detailId: String,
    val name: String,
    @Embedded
    val address: Address,
)
