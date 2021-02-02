package fr.azhot.realestatemanager.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.azhot.realestatemanager.database.AppDatabase
import java.util.*

/**
 * POJO representing a realtor.
 * Entity in the [AppDatabase].
 */
@Entity(tableName = "realtor_table")
data class Realtor(
    @PrimaryKey
    val realtorId: String = UUID.randomUUID().toString(),
    val firstName: String,
    val lastName: String,
) {

    override fun toString(): String {
        val sb = StringBuilder().apply {
            append("$firstName ")
            append(lastName)
        }
        return sb.toString().trim()
    }
}