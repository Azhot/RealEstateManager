package fr.azhot.realestatemanager.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "realtor_table")
data class Realtor(
    @PrimaryKey
    val realtorId: String = UUID.randomUUID().toString(),
    var firstName: String? = null,
    var lastName: String? = null,
) {

    override fun toString(): String {
        val sb = StringBuilder()
        firstName?.let { sb.append("$it ") }
        lastName?.let { sb.append(it) }
        return sb.toString().trim()
    }
}