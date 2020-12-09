package fr.azhot.realestatemanager.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "photo_table",
    foreignKeys = [
        ForeignKey(
            entity = Detail::class,
            parentColumns = arrayOf("detailId"),
            childColumns = arrayOf("detailId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Photo(
    @PrimaryKey(autoGenerate = true)
    val photoId: Long,
    val detailId: Long,
    val uri: String,
    val description: String?,
) {
    constructor(
        detailId: Long,
        uri: String,
        description: String?,
    ) : this(0, detailId, uri, description)
}