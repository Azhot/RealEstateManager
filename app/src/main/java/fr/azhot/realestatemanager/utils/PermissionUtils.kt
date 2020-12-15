package fr.azhot.realestatemanager.utils

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


/**
 * Prompts a dialog inviting the user to provide the app with the specified permission.
 *
 * @param appCompatActivity the [AppCompatActivity] from which this method is called.
 * @param requestCode specific request code to match with the result reported to the
 * [AppCompatActivity.onRequestPermissionsResult] method.
 * @param permission the [Manifest.permission] to be requested
 */
fun getPermission(appCompatActivity: AppCompatActivity, requestCode: Int, permission: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        appCompatActivity.requestPermissions(arrayOf(permission), requestCode)
    }
}

/**
 * Checks whether READ_EXTERNAL_STORAGE permission is provided to the app or calls the
 * [getPermission] method.
 *
 * @param appCompatActivity the [AppCompatActivity] from which this method is called.
 * @return a boolean value which is set to true if permission is already granted or false otherwise.
 */
fun checkPermission(appCompatActivity: AppCompatActivity, requestCode: Int, permission: String): Boolean {
    return if (ContextCompat.checkSelfPermission(
            appCompatActivity,
            permission
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        getPermission(appCompatActivity, requestCode, permission)
        false
    } else {
        true
    }
}