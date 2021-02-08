package com.hextremelabs.permiscus

import android.app.Activity
import android.util.SparseArray
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

sealed class PermissionManager {
    private val requestCodeLock = Any()
    private val requests = SparseArray<PermissionRequest>()

    fun with(vararg permissions: String): PermissionRequestBuilder {
        require(permissions.isNotEmpty()) { "Must be called with at least one permission" }
        @Suppress("UNCHECKED_CAST")
        return PermissionRequestBuilder(this, permissions as Array<String>)
    }

    fun handlePermissionResult(requestCode: Int, grantResults: IntArray): Boolean {
        val request = requests[requestCode] ?: return false
        unregisterCallbacks(requestCode)

        if (PermissionUtil.verifyPermissionResults(grantResults)) {
            request.fireOnPermissionGrantedCallback()
        } else {
            val canShowRationale = shouldShowPermissionRationale(request.permissions)
            request.fireOnPermissionDeniedCallback(!canShowRationale)
        }
        return true
    }

    @JvmSynthetic
    internal fun check(permissionRequest: PermissionRequest) {
        if (checkPermissions(permissionRequest.permissions)) {
            permissionRequest.fireOnPermissionGrantedCallback()
        } else {
            val canShowRationale = shouldShowPermissionRationale(permissionRequest.permissions)
            permissionRequest.fireOnPermissionDeniedCallback(!canShowRationale)
        }
    }

    @JvmSynthetic
    internal fun request(permissionRequest: PermissionRequest) {
        if (checkPermissions(permissionRequest.permissions)) {
            permissionRequest.fireOnPermissionGrantedCallback()
            return
        }
        if (shouldShowPermissionRationale(permissionRequest.permissions)) {
            permissionRequest.fireOnPermissionShowRationaleCallback()
        } else {
            requestPermission(permissionRequest)
        }
    }

    @JvmSynthetic
    internal fun requestPermission(permissionRequest: PermissionRequest) {
        val requestCode = registerCallbacks(permissionRequest)
        requestPermission(requestCode, permissionRequest.permissions)
    }

    protected abstract fun requestPermission(requestCode: Int, permissions: Array<String>)
    protected abstract fun checkPermissions(permissions: Array<String>): Boolean
    protected abstract fun shouldShowPermissionRationale(permissions: Array<String>): Boolean

    private fun unregisterCallbacks(requestCode: Int) {
        requests.delete(requestCode)
    }

    private fun registerCallbacks(permissionRequest: PermissionRequest): Int {
        // Register the request with the PermissionManager before requesting the permission(s).
        // The requests map is used by PermissionManager.handlePermissionResult() to act
        // on the permission result, once the user replies to the request.
        // If the activity/fragment is destroyed before the result arrives
        // then the requests map is lost and the library will instead try to
        // restore the callbacks from one of the static callback maps...
        var requestCode: Int
        synchronized(requestCodeLock) {

            // If no request code was supplied by the PermissionRequestBuilder then
            // calculate one...
            val userSuppliedRequestCode = permissionRequest.requestCode
            when {
                userSuppliedRequestCode == -1 -> {
                    requestCode = calculateRequestCode()
                    requests.put(requestCode, permissionRequest)
                }
                requests[userSuppliedRequestCode] == null -> {
                    requestCode = userSuppliedRequestCode
                    requests.put(requestCode, permissionRequest)
                }
                else -> error("The requestCode $userSuppliedRequestCode is already in use")
            }
        }
        return requestCode
    }

    /**
     * The requestCode must be between 0 and 255. This method calculates a new request code by
     * the simple method of looping through all the possible codes and returning the first one
     * that is not in use.
     * @return an unused request code
     */
    private fun calculateRequestCode(): Int {
        for (i in 0 until MAX_REQUEST_CODE) {
            if (requests[i] == null) {
                return i
            }
        }
        error("Unable to calculate request code. Try setting a request code manually by calling PermissionRequestBuilder#usingRequestCode(int)")
    }

    private class ActivityPermissionManager(private val activity: Activity) : PermissionManager() {

        override fun requestPermission(requestCode: Int, permissions: Array<String>) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode)
        }

        override fun checkPermissions(permissions: Array<String>): Boolean {
            return PermissionUtil.checkPermissions(activity, permissions)
        }

        override fun shouldShowPermissionRationale(permissions: Array<String>): Boolean {
            return PermissionUtil.shouldShowPermissionRationale(activity, permissions)
        }
    }

    private class FragmentPermissionManager(private val fragment: Fragment) : PermissionManager() {

        override fun requestPermission(requestCode: Int, permissions: Array<String>) {
            fragment.requestPermissions(permissions, requestCode)
        }

        override fun checkPermissions(permissions: Array<String>): Boolean {
            return PermissionUtil.checkPermissions(fragment.requireActivity(), permissions)
        }

        override fun shouldShowPermissionRationale(permissions: Array<String>): Boolean {
            return PermissionUtil.shouldShowPermissionRationale(fragment, permissions)
        }
    }

    companion object {
        private const val MAX_REQUEST_CODE = 255

        @JvmStatic fun create(activity: Activity): PermissionManager {
            return ActivityPermissionManager(activity)
        }

        @JvmStatic fun create(fragment: Fragment): PermissionManager {
            return FragmentPermissionManager(fragment)
        }
    }
}
