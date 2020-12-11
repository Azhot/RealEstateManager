package fr.azhot.realestatemanager.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "address_table")
data class Address(
    @PrimaryKey(autoGenerate = true)
    val addressId: Long,
    var zipCode: String?,
    var city: String?,
    var roadName: String?,
    var number: String?,
    var complement: String? = null,
) {
    constructor(
        zipCode: String?,
        city: String?,
        roadName: String?,
        number: String?,
        complement: String?,
    ) : this(0, zipCode, city, roadName, number, complement)
}