package fr.azhot.realestatemanager.utils

import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 * Reads a bitmap and writes it as a [File] to the specified directory.
 *
 * @param bitmap the bitmap to write
 * @param directory the target directory to write the bitmap
 *
 * @return the newly created file.
 */
fun storeBitmap(
    bitmap: Bitmap,
    directory: File?,
): File {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ROOT).format(Date())
    var file = File(directory, "JPEG_${timeStamp}_.jpg")

    if (file.exists()) {
        var i = 2
        while (file.exists()) {
            file = File(directory, "JPEG_${timeStamp}.${i++}_.jpg")
        }
    }

    try {
        val fileOutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()
    } catch (error: IOException) {
        error.printStackTrace()
    }

    return file
}

/**
 * Creates a [File] to the specified directory.
 *
 * @param directory the target directory
 *
 * @return the newly created file.
 */
@Throws(IOException::class)
fun createImageFile(
    directory: File?,
): File {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ROOT).format(Date())
    return File.createTempFile("JPEG_${timeStamp}_", ".jpg", directory)
}