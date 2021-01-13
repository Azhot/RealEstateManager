package fr.azhot.realestatemanager.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

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
    @PrimaryKey
    val detailId: String = UUID.randomUUID().toString(),
    var propertyType: PropertyType? = null,
    var price: Int? = null,
    var squareMeters: Int? = null,
    var rooms: Int? = null,
    var description: String? = null,
    @ColumnInfo(index = true)
    var addressId: String? = null,
    var entryTimeStamp: Long? = null,
    var saleTimeStamp: Long? = null,
    @ColumnInfo(index = true)
    var realtorId: String? = null,
)