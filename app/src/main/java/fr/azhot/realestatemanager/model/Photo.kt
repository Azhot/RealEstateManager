package fr.azhot.realestatemanager.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@Entity(
    tableName = "photo_table",
)
data class Photo(
    @PrimaryKey
    val photoId: String = UUID.randomUUID().toString(),
    var detailId: String? = null,
    var uri: String? = null,
    var title: String? = null,
) : Parcelable