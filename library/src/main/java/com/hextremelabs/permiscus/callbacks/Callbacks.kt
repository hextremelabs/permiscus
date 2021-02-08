package com.hextremelabs.permiscus.callbacks

import com.hextremelabs.permiscus.PermissionRequest

fun interface OnPermissionDeniedCallback {
    fun onPermissionDenied(neverAskAgain: Boolean)
}

fun interface OnPermissionGrantedCallback {
    fun onPermissionGranted()
}

fun interface OnPermissionShowRationaleCallback {
    fun onPermissionShowRationale(permissionRequest: PermissionRequest)
}

interface OnPermissionCallback :
    OnPermissionGrantedCallback,
    OnPermissionDeniedCallback,
    OnPermissionShowRationaleCallback
