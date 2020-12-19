package fr.azhot.realestatemanager.utils

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


// todo : javadoc
/**
 * Takes a bitmap and stores it to the internal storage's specified directory (created if
 * non-existing) with the specified name (created if non-existing).
 *
 * @param bitmap the file to store
 * @param fileName the file name of the created bitmap
 * @param directoryName the directory where the bitmap should be stored
 * @param context
 * 
 * @return a String representation of the URI of the newly created bitmap.
 * @throws IOException if file already exists
 */
fun storeBitmap(
    bitmap: Bitmap,
    fileName: String,
    directoryName: String,
    context: Context
): String {
    val directory = context.getDir(directoryName, Context.MODE_PRIVATE)
    val file = File(directory, fileName)
    if (!file.exists()) {
        try {
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (error: IOException) {
            error.printStackTrace()
        }
    } else {
        throw IOException("File ${file.path} already exists")
    }
    return file.toURI().toString()
}
