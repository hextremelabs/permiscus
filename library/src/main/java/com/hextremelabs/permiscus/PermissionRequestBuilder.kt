package com.hextremelabs.permiscus

import com.hextremelabs.permiscus.callbacks.OnPermissionCallback
import com.hextremelabs.permiscus.callbacks.OnPermissionDeniedCallback
import com.hextremelabs.permiscus.callbacks.OnPermissionGrantedCallback
import com.hextremelabs.permiscus.callbacks.OnPermissionShowRationaleCallback

class PermissionRequestBuilder internal constructor(
    private val manager: PermissionManager,
    private val permissions: Array<String>
) {
    private var requestCode = -1
    private var grantedCallback: OnPermissionGrantedCallback? = null
    private var deniedCallback: OnPermissionDeniedCallback? = null
    private var showRationaleCallback: OnPermissionShowRationaleCallback? = null

    fun usingRequestCode(requestCode: Int): PermissionRequestBuilder {
        this.requestCode = requestCode
        return this
    }

    fun onCallback(callback: OnPermissionCallback): PermissionRequestBuilder {
        grantedCallback = callback
        deniedCallback = callback
        showRationaleCallback = callback
        return this
    }

    fun onPermissionGranted(callback: OnPermissionGrantedCallback): PermissionRequestBuilder {
        grantedCallback = callback
        return this
    }

    fun onPermissionDenied(callback: OnPermissionDeniedCallback): PermissionRequestBuilder {
        deniedCallback = callback
        return this
    }

    fun onPermissionShowRationale(callback: OnPermissionShowRationaleCallback): PermissionRequestBuilder {
        showRationaleCallback = callback
        return this
    }

    fun request() {
        val permissionRequest = PermissionRequest(
            manager, permissions, requestCode, grantedCallback, deniedCallback, showRationaleCallback
        )
        manager.request(permissionRequest)
    }

    fun check() {
        val permissionRequest = PermissionRequest(
            manager, permissions, requestCode, grantedCallback, deniedCallback, showRationaleCallback
        )
        manager.check(permissionRequest)
    }
}
