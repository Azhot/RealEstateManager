package fr.azhot.realestatemanager.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "realtor_table")
data class Realtor(
    @PrimaryKey(autoGenerate = true)
    val realtorId: Long,
    var firstName: String,
    var lastName: String,
) {
    constructor(
        firstName: String,
        lastName: String,
    ) : this(0, firstName, lastName)

    override fun toString(): String {
        return "$firstName $lastName"
    }
}