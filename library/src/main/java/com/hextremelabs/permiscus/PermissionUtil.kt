package com.hextremelabs.permiscus

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.collection.SimpleArrayMap
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

internal object PermissionUtil {
    // Map of dangerous permissions introduced in later framework versions.
    // Used to conditionally bypass permission-hold checks on older devices.
    private val MIN_SDK_PERMISSIONS = SimpleArrayMap<String, Int>(8).apply {
        put("com.android.voicemail.permission.ADD_VOICEMAIL", 14)
        put("android.permission.BODY_SENSORS", 20)
        put("android.permission.READ_CALL_LOG", 16)
        put("android.permission.READ_EXTERNAL_STORAGE", 16)
        put("android.permission.USE_SIP", 9)
        put("android.permission.WRITE_CALL_LOG", 16)
        put("android.permission.SYSTEM_ALERT_WINDOW", 23)
        put("android.permission.WRITE_SETTINGS", 23)
    }

    /**
     * Returns true if the permission exists in this SDK version
     *
     * @param permission permission
     * @return returns true if the permission exists in this SDK version
     */
    private fun permissionExists(permission: String?): Boolean {
        // Check if the permission could potentially be missing on this device
        val minVersion = MIN_SDK_PERMISSIONS[permission]
        // If null was returned from the above call, there is no need for a device API level check for the permission;
        // otherwise, we check if its minimum API level requirement is met
        return minVersion == null || Build.VERSION.SDK_INT >= minVersion
    }

    /**
     * Check if that all given permissions are granted.
     */
    fun checkPermissions(context: Context, permissions: Array<String>): Boolean {
        return permissions.none {
            permissionExists(it) && ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Check if at least one of the given permissions should show a permission rationale.
     */
    fun shouldShowPermissionRationale(activity: Activity, permissions: Array<String>): Boolean {
        return permissions.any { ActivityCompat.shouldShowRequestPermissionRationale(activity, it) }
    }

    /**
     * Check if at least one of the given permissions should show a permission rationale.
     */
    fun shouldShowPermissionRationale(fragment: Fragment, permissions: Array<String>): Boolean {
        return permissions.any { fragment.shouldShowRequestPermissionRationale(it) }
    }

    /**
     * Check that all given permissions have been granted by verifying that each entry in the
     * given array is of the value [PackageManager.PERMISSION_GRANTED].
     *
     * @see Activity.onRequestPermissionsResult
     */
    fun verifyPermissionResults(grantResults: IntArray): Boolean {
        if (grantResults.isEmpty()) {
            return false
        }
        return grantResults.none { it != PackageManager.PERMISSION_GRANTED }
    }
}
