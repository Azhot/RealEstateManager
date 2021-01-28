package fr.azhot.realestatemanager.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import fr.azhot.realestatemanager.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


class PropertyContentProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "fr.azhot.realestatemanager.provider"
        const val TABLE_NAME = "detail_table"
        val uri: Uri = Uri.parse("content://$AUTHORITY/$TABLE_NAME")
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        context?.let { context ->
            return AppDatabase.getDatabase(context, CoroutineScope(SupervisorJob()))
                .detailDao()
                .getPropertyList().apply {
                    setNotificationUri(context.contentResolver, uri)
                }
        }
        throw IllegalArgumentException("Failed to query property list.")
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return 0
    }
}