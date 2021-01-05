package fr.azhot.realestatemanager.utils

import java.text.SimpleDateFormat
import java.util.*


/**
 * Formats a [Long] representation of a timestamp into a "d MMM yyy" pattern.
 *
 * @param timeStamp The timeStamp to format.
 */
fun formatTimeStamp(timeStamp: Long): String {
    return SimpleDateFormat("d MMM yyyy", Locale.getDefault())
        .format(timeStamp)
}