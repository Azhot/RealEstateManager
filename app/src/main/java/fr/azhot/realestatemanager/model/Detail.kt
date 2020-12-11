package fr.azhot.realestatemanager.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "detail_table",
    foreignKeys = [
        ForeignKey(
            entity = Address::class,
            parentColumns = arrayOf("addressId"),
            childColumns = arrayOf("addressId"),
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Realtor::class,
            parentColumns = arrayOf("realtorId"),
            childColumns = arrayOf("realtorId"),
            onDelete = ForeignKey.SET_NULL
        ),
    ]
)
data class Detail(
    @PrimaryKey(autoGenerate = true)
    val detailId: Long,
    var propertyType: PropertyType?,
    var price: Int?,
    var squareMeters: Int?,
    var rooms: Int?,
    var description: String?,
    var addressId: Long?,
    var entryTimeStamp: Long?,
    var saleTimeStamp: Long?,
    var realtorId: Long?,
) {
    constructor(
        propertyType: PropertyType?,
        price: Int?,
        squareMeters: Int?,
        rooms: Int?,
        description: String?,
        addressId: Long?,
        entryTimeStamp: Long?,
        saleTimeStamp: Long?,
        realtorId: Long?,
    ) : this(0,
        propertyType,
        price,
        squareMeters,
        rooms,
        description,
        addressId,
        entryTimeStamp,
        saleTimeStamp,
        realtorId)
}