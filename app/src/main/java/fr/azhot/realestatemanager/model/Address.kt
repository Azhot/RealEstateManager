package fr.azhot.realestatemanager.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "address_table")
data class Address(
    @PrimaryKey
    val addressId: String = UUID.randomUUID().toString(),
    var zipCode: String? = null,
    var city: String? = null,
    var roadName: String? = null,
    var number: String? = null,
    var complement: String? = null,
) {
    override fun toString(): String {
        val string = StringBuilder().run {
            for (i in arrayOf(city, number, roadName, zipCode, complement)) {
                if (i?.isNotEmpty() == true) append("$i, ")
            }
            toString()
        }
        return if (string.isNotEmpty()) string.substring(0, string.length - 2) else string
    }
}