package fr.azhot.realestatemanager.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "realtor_table")
data class Realtor(
    @PrimaryKey
    val realtorId: String = UUID.randomUUID().toString(),
    var firstName: String,
    var lastName: String,
) {

    override fun toString(): String {
        val sb = StringBuilder().apply {
            append("$firstName ")
            append(lastName)
        }
        return sb.toString().trim()
    }
}