package fr.azhot.realestatemanager.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment


/**
 * Checks whether a permission is granted to the app.
 *
 * @param appCompatActivity the [AppCompatActivity] from which this method is called.
 * @param permission the [Manifest.permission] to be requested.
 *
 * @return true if permission is granted and false otherwise.
 *
 */
fun checkPermission(appCompatActivity: AppCompatActivity, permission: String): Boolean {
    return (ContextCompat.checkSelfPermission(appCompatActivity, permission)
            == PackageManager.PERMISSION_GRANTED)
}

/**
 * Checks whether a permission is granted to the app.
 *
 * @param fragment the [Fragment] from which this method is called.
 * @param permission the [Manifest.permission] to be requested.
 *
 * @return true if permission is granted and false otherwise.
 *
 */
fun checkPermission(fragment: Fragment, permission: String): Boolean {
    return (ContextCompat.checkSelfPermission(fragment.requireContext(), permission)
            == PackageManager.PERMISSION_GRANTED)
}

/**
 * Checks whether permissions are granted to the app.
 *
 * @param context the [Context] from which this method is called.
 * @param permissions the [Manifest.permission] array to be requested
 *
 * @return true if permissions are granted and false otherwise.
 *
 */
fun checkPermissions(
    context: Context,
    permissions: Array<out String>
): Boolean {
    for (permission in permissions) {
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) return false
    }
    return true
}

/**
 * Checks whether an array of [Manifest.permission] are granted to the app.
 * This method should be called within the [AppCompatActivity.onRequestPermissionsResult] or
 * [Fragment.onRequestPermissionsResult] method.
 *
 * @param requestCodeToMatch the request code to be matched.
 * @param requestCode the request code received from onRequestPermissionsResult.
 * @param grantResults the results received from onRequestPermissionsResult.
 *
 * @return true if permissions are granted and false otherwise.
 *
 */
fun checkPermissionsGranted(
    requestCodeToMatch: Int,
    requestCode: Int,
    grantResults: IntArray
): Boolean {
    if (requestCode != requestCodeToMatch) return false
    for (result in grantResults) if (result != PackageManager.PERMISSION_GRANTED) return false
    return true
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
 * Prompts a dialog inviting the user to provide the app with the specified permissions.
 *
 * @param appCompatActivity the [AppCompatActivity] from which this method is called.
 * @param requestCode specific request code to match with the result reported to the
 *        [AppCompatActivity.onRequestPermissionsResult] method.
 * @param permissions the [Manifest.permission] array to be requested
 *
 */
fun requestPermissions(
    appCompatActivity: AppCompatActivity,
    requestCode: Int,
    permissions: Array<out String>
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        appCompatActivity.requestPermissions(permissions, requestCode)
    }
}

/**
 * Prompts a dialog inviting the user to provide the app with the specified permissions.
 *
 * @param fragment the [Fragment] from which this method is called.
 * @param requestCode specific request code to match with the result reported to the
 *        [Fragment.onRequestPermissionsResult] method.
 * @param permissions the [Manifest.permission] array to be requested
 *
 */
fun requestPermissions(fragment: Fragment, requestCode: Int, permissions: Array<out String>) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        fragment.requestPermissions(permissions, requestCode)
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

/**
 * Checks whether permissions are provided to the app or calls the [requestPermissions] method.
 *
 * @param appCompatActivity the [AppCompatActivity] from which this method is called.
 * @param requestCode specific request code to match with the result reported to the
 *        [AppCompatActivity.onRequestPermissionsResult] method.
 * @param permissions the [Manifest.permission] array to be requested
 *
 * @return true if permission is already granted and false otherwise.
 *
 */
fun checkAndRequestPermissions(
    appCompatActivity: AppCompatActivity,
    requestCode: Int,
    permissions: Array<out String>
): Boolean {
    for (permission in permissions) {
        if (ContextCompat.checkSelfPermission(appCompatActivity, permission)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission(appCompatActivity, requestCode, permission)
            return false
        }
    }
    return true
}

/**
 * Checks whether permissions are provided to the app or calls the [requestPermissions] method.
 *
 * @param fragment the [Fragment] from which this method is called.
 * @param requestCode specific request code to match with the result reported to the
 *        [Fragment.onRequestPermissionsResult] method.
 * @param permissions the [Manifest.permission] array to be requested
 *
 * @return true if permission is already granted and false otherwise.
 *
 */
fun checkAndRequestPermissions(
    fragment: Fragment,
    requestCode: Int,
    permissions: Array<out String>
): Boolean {
    for (permission in permissions) {
        if (ContextCompat.checkSelfPermission(fragment.requireContext(), permission)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission(fragment, requestCode, permission)
            return false
        }
    }
    return true
}