package fr.azhot.realestatemanager.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

/**
 * Get pictures through uri and compress them
 *
 * @param ...
 * @return ... return null if an error occurred
 */

// todo : write javadoc

fun getBitmapFromUri(
    uri: Uri,
    maxWidth: Float,
    maxHeight: Float,
    context: Context
): Bitmap? {

    // decode bitmap to retrieve original width and height
    var inputStream: InputStream? = context.contentResolver.openInputStream(uri) ?: return null
    val boundsOptions = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
        inPreferredConfig = Bitmap.Config.ARGB_8888
    }
    BitmapFactory.decodeStream(inputStream, null, boundsOptions)
    inputStream?.close()

    // get bitmap original width and height
    val originalWidth = boundsOptions.outWidth
    val originalHeight = boundsOptions.outHeight
    if ((originalWidth == -1) || (originalHeight == -1)) {
        return null
    }

    // scaling options
    val scaling: Int =
        // original image is wider than height and than required width
        if (originalWidth > originalHeight && originalWidth > maxWidth) {
            (originalWidth / maxWidth).toInt()
            // original image is higher than width and than required height
        } else if (originalWidth < originalHeight && originalHeight > maxHeight) {
            (originalHeight / maxHeight).toInt()
        } else {
            1
        }

    // Proportional compression
    val bitmapOptions = BitmapFactory.Options().apply {
        inSampleSize = scaling
        inPreferredConfig = Bitmap.Config.ARGB_8888
    }

    inputStream = context.contentResolver?.openInputStream(uri)
    val bitmap = BitmapFactory.decodeStream(inputStream, null, bitmapOptions)
    inputStream?.close()

    return bitmap
}

/**
 * Mass compression method
 *
 * @param ...
 * @return ... return null if an error occurred
 */

// todo : write javadoc

fun compressBitmap(bitmap: Bitmap, targetSize: Int): Bitmap? {
    val byteArrayOutputStream = ByteArrayOutputStream()
    // Quality compression method, 100 means no compression, store the compressed data in the ByteArrayOutputStream
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)

    var q = targetSize
    // Cycle to determine if the compressed image is greater than targetSize (in kb), greater than continue compression
    while (byteArrayOutputStream.toByteArray().size / 1024 > targetSize) {
        // Reset the ByteArrayOutputStream to clear it
        byteArrayOutputStream.reset()
        // First parameter: picture format, second parameter: picture quality, 100 is the highest, 0 is the worst, third parameter: save the compressed data stream
        // Here, the compression options are used to store the compressed data in the ByteArrayInputStream
        bitmap.compress(Bitmap.CompressFormat.JPEG, q, byteArrayOutputStream)
        q -= 10 // 10 less each time
    }

    // Store the compressed data in ByteArrayInputStream
    val byteArrayInputStream = ByteArrayInputStream(byteArrayOutputStream.toByteArray())

    // Generate bitmap from ByteArrayInputStream data
    return BitmapFactory.decodeStream(byteArrayInputStream, null, null)
}


fun scaleBitmap(targetWidth: Int, targetHeight: Int, uri: String): Bitmap? {
    val bmOptions = BitmapFactory.Options().apply {
        // Get the dimensions of the bitmap
        inJustDecodeBounds = true

        BitmapFactory.decodeFile(uri, this)

        // Determine how much to scale down the image
        val scaleFactor: Int =
            1.coerceAtLeast((outWidth / targetWidth).coerceAtMost(outHeight / targetHeight))

        // Decode the image file into a Bitmap sized to fill the View
        inJustDecodeBounds = false
        inSampleSize = scaleFactor
    }

    BitmapFactory.decodeFile(uri, bmOptions)?.also { bitmap ->
        return bitmap
    }

    return null
}


fun checkBitmapRotation(path: String): Bitmap {
    val exifInterface = ExifInterface(path)
    val orientation: Int = exifInterface.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_UNDEFINED
    )
    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(BitmapFactory.decodeFile(path), 90)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(BitmapFactory.decodeFile(path), 180)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(BitmapFactory.decodeFile(path), 270)
        else -> BitmapFactory.decodeFile(path)
    }
}

fun rotateBitmap(bitmapToRotate: Bitmap, degreesToRotate: Int): Bitmap {
    try {
        if (degreesToRotate != 0) {
            val matrix = Matrix()
            matrix.setRotate(degreesToRotate.toFloat())
            return Bitmap.createBitmap(
                bitmapToRotate,
                0,
                0,
                bitmapToRotate.width,
                bitmapToRotate.height,
                matrix,
                true
            )
        }
    } catch (e: Exception) {
        Log.e("rotateImage", "Exception when trying to rotate image", e)
    }
    return bitmapToRotate
}


// todo: write javadoc
fun createBitmapWithGlide(
    glide: RequestManager,
    width: Int,
    height: Int,
    uri: Uri,
    doSomethingWithBitmap: (Bitmap) -> Unit
) {

    val requestOptions = RequestOptions()
        .fitCenter()
        .override(width, height)

    glide
        .asBitmap()
        .apply(requestOptions)
        .load(uri)
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(
                resource: Bitmap,
                transition: Transition<in Bitmap>?
            ) {
                doSomethingWithBitmap(resource)
            }

            override fun onLoadCleared(placeholder: Drawable?) {}
        })
}
