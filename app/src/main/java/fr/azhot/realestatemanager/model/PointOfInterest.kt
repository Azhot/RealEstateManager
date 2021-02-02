package fr.azhot.realestatemanager.model

import androidx.room.*
import fr.azhot.realestatemanager.database.AppDatabase
import java.util.*

/**
 * POJO representing a point of interest.
 * Entity in the [AppDatabase].
 */
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
    @ColumnInfo(index = true)
    val detailId: String,
    val pointOfInterestType: PointOfInterestType? = null,
    @Embedded
    val address: Address? = null,
) {
    override fun toString(): String {
        return if (address != null) {
            "$pointOfInterestType: $address"
        } else {
            pointOfInterestType?.toString() ?: "-"
        }
    }
}
