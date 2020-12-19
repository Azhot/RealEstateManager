package fr.azhot.realestatemanager.utils

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment


/**
 * Checks whether a permission is provided to the app.
 *
 * @param appCompatActivity the [AppCompatActivity] from which this method is called.
 * @param permission the [Manifest.permission] to be requested.
 *
 * @return true if permission is granted and false otherwise.
 *
 */
fun checkPermission(appCompatActivity: AppCompatActivity, permission: String): Boolean {
    return (ContextCompat.checkSelfPermission(appCompatActivity, permission)
            != PackageManager.PERMISSION_GRANTED)
}

/**
 * Checks whether a permission is provided to the app.
 *
 * @param fragment the [Fragment] from which this method is called.
 * @param permission the [Manifest.permission] to be requested.
 *
 * @return true if permission is granted and false otherwise.
 *
 */
fun checkPermission(fragment: Fragment, permission: String): Boolean {
    return (ContextCompat.checkSelfPermission(fragment.requireContext(), permission)
            != PackageManager.PERMISSION_GRANTED)
}

/**
 * Checks whether a permission is provided to the app.
 * This method should be called within the [AppCompatActivity.onRequestPermissionsResult] or
 * [Fragment.onRequestPermissionsResult] method.
 *
 * @param requestCodeToMatch the request code for the permission to be checked
 * @param permissionToMatch the [Fragment] from which this method is called.
 * @param requestCode the request code received from onRequestPermissionsResult.
 * @param permissions the permissions received from onRequestPermissionsResult.
 * @param grantResults the grant results received from onRequestPermissionsResult.
 *
 * @return true if permission is granted and false otherwise.
 *
 */
fun checkPermission(
    requestCodeToMatch: Int,
    permissionToMatch: String,
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
): Boolean {
    return (requestCode == requestCodeToMatch)
            && permissions[0] == permissionToMatch
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
}

/**
 * Prompts a dialog inviting the user to provide the app with the specified permission.
 *
 * @param appCompatActivity the [AppCompatActivity] from which this method is called.
 * @param requestCode specific request code to match with the result reported to the
 *        [AppCompatActivity.onRequestPermissionsResult] method.
 * @param permission the [Manifest.permission] to be requested
 *
 */
fun requestPermission(appCompatActivity: AppCompatActivity, requestCode: Int, permission: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        appCompatActivity.requestPermissions(arrayOf(permission), requestCode)
    }
}

/**
 * Prompts a dialog inviting the user to provide the app with the specified permission.
 *
 * @param fragment the [Fragment] from which this method is called.
 * @param requestCode specific request code to match with the result reported to the
 *        [Fragment.onRequestPermissionsResult] method.
 * @param permission the [Manifest.permission] to be requested
 *
 */
fun requestPermission(fragment: Fragment, requestCode: Int, permission: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        fragment.requestPermissions(arrayOf(permission), requestCode)
    }
}

/**
 * Checks whether a permission is provided to the app or calls the [requestPermission] method.
 *
 * @param appCompatActivity the [AppCompatActivity] from which this method is called.
 * @param requestCode specific request code to match with the result reported to the
 *        [AppCompatActivity.onRequestPermissionsResult] method.
 * @param permission the [Manifest.permission] to be requested
 *
 * @return true if permission is already granted and false otherwise.
 *
 */
fun checkAndRequestPermission(
    appCompatActivity: AppCompatActivity,
    requestCode: Int,
    permission: String
): Boolean {
    return if (ContextCompat.checkSelfPermission(appCompatActivity, permission)
        != PackageManager.PERMISSION_GRANTED
    ) {
        requestPermission(appCompatActivity, requestCode, permission)
        false
    } else {
        true
    }
}

/**
 * Checks whether a permission is provided to the app or calls the [requestPermission] method.
 *
 * @param fragment the [Fragment] from which this method is called.
 * @param requestCode specific request code to match with the result reported to the
 *        [Fragment.onRequestPermissionsResult] method.
 * @param permission the [Manifest.permission] to be requested
 *
 * @return true if permission is already granted and false otherwise.
 *
 */
fun checkAndRequestPermission(
    fragment: Fragment,
    requestCode: Int,
    permission: String
): Boolean {
    return if (ContextCompat.checkSelfPermission(fragment.requireContext(), permission)
        != PackageManager.PERMISSION_GRANTED
    ) {
        requestPermission(fragment, requestCode, permission)
        false
    } else {
        true
    }
}