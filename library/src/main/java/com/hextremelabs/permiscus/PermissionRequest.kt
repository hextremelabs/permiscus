package com.hextremelabs.permiscus

import com.hextremelabs.permiscus.callbacks.OnPermissionDeniedCallback
import com.hextremelabs.permiscus.callbacks.OnPermissionGrantedCallback
import com.hextremelabs.permiscus.callbacks.OnPermissionShowRationaleCallback

class PermissionRequest(
    private val permissionManager: PermissionManager,
    val permissions: Array<String>,
    val requestCode: Int,
    private val grantedCallback: OnPermissionGrantedCallback?,
    private val deniedCallback: OnPermissionDeniedCallback?,
    private val showRationaleCallback: OnPermissionShowRationaleCallback?
) {
    fun acceptPermissionRationale() {
        permissionManager.requestPermission(this)
    }

    fun fireOnPermissionGrantedCallback() {
        grantedCallback?.onPermissionGranted()
    }

    fun fireOnPermissionDeniedCallback(neverAskAgain: Boolean) {
        deniedCallback?.onPermissionDenied(neverAskAgain)
    }

    fun fireOnPermissionShowRationaleCallback() {
        showRationaleCallback?.onPermissionShowRationale(this)
    }
}
