package fr.azhot.realestatemanager.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
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
    @PrimaryKey
    val photoId: String = UUID.randomUUID().toString(),
    @ColumnInfo(index = true)
    val detailId: String,
    val uri: String,
    val title: String,
) : Parcelable