package fr.azhot.realestatemanager.utils

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

/**
 * Creates a [Bitmap] object from an [Uri] then making possible to do some additional work with
 * this object through the [doSomethingWithBitmap] function.
 *
 * @param glide The [RequestManager] to be used
 * @param width The width to be set to the [Bitmap]
 * @param height The height to be set to the [Bitmap]
 * @param uri The [Uri] referencing the source to be decoded as a [Bitmap]
 * @param doSomethingWithBitmap The function which will take the [Bitmap] as an argument to do
 * some additional work with it.
 */
fun createBitmapWithGlide(
    glide: RequestManager,
    width: Int,
    height: Int,
    uri: Uri,
    doSomethingWithBitmap: (Bitmap) -> Unit
) {

    val requestOptions = RequestOptions()
        .override(width, height)
        .centerCrop()

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
