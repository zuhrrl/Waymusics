package com.waymusics.android.component.storage

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

object PermissionHelper {
    private const val REQUEST_ID_MULTIPLE_PERMISSIONS = 100
    private var permissionsGranted = false

    fun checkAndRequestPermissions(activity: Activity?, vararg permissions: String): Boolean {
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(activity!!, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission)
            } else {
                permissionsGranted = true
            }
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            // permission granted
            ActivityCompat.requestPermissions(activity!!, listPermissionsNeeded.toTypedArray(), REQUEST_ID_MULTIPLE_PERMISSIONS)
        }
        return permissionsGranted
    }


}