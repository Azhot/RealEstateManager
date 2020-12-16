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
        val sb = StringBuilder()
        city?.let { sb.append("$it, ") }
        number?.let { sb.append("$it, ") }
        roadName?.let { sb.append("$it, ") }
        zipCode?.let { sb.append("$it, ") }
        complement?.let { sb.append(it) }
        return sb.toString()
    }
}