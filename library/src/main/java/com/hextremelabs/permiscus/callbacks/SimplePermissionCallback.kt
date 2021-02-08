package com.hextremelabs.permiscus.callbacks

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import com.hextremelabs.permiscus.PermissionRequest
import com.hextremelabs.permiscus.R

/**
 * @author ADIO Kingsley O.
 * @since 05 Mar, 2017
 */
class SimplePermissionCallback private constructor(
    private val anchorView: View,
    private val rationale: CharSequence,
    private val instructions: CharSequence,
    private val permissionGrantedCallback: OnPermissionGrantedCallback
) : OnPermissionCallback, OnPermissionGrantedCallback by permissionGrantedCallback {

    override fun onPermissionDenied(neverAskAgain: Boolean) {
        val snackbar = Snackbar.make(anchorView, R.string.permissions_denied, Snackbar.LENGTH_LONG)
        if (neverAskAgain) {
            snackbar.setDuration(Snackbar.LENGTH_INDEFINITE)
                .setText(R.string.permissions_disabled)
                .setAction(R.string.change_settings) { v ->
                    val context = v.context
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", context.packageName, null)
                    intent.data = uri
                    context.startActivity(intent)
                    Toast.makeText(context, instructions, Toast.LENGTH_SHORT).show()
                }
        }
        snackbar.show()
    }

    override fun onPermissionShowRationale(permissionRequest: PermissionRequest) {
        Snackbar.make(anchorView, rationale, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.proceed) { permissionRequest.acceptPermissionRationale() }
            .show()
    }

    class Builder internal constructor(private val view: View) {
        private var permissionGrantedCallback: OnPermissionGrantedCallback? = null
        private var rationale: CharSequence? = null
        private var instructions: CharSequence? = null

        fun rationale(@StringRes rationale: Int): Builder {
            this.rationale = view.context.getText(rationale)
            return this
        }

        fun instructions(@StringRes instructions: Int): Builder {
            this.instructions = view.context.getText(instructions)
            return this
        }

        fun onPermissionsGranted(permissionGrantedCallback: OnPermissionGrantedCallback): Builder {
            this.permissionGrantedCallback = permissionGrantedCallback
            return this
        }

        fun rationale(rationale: CharSequence): Builder {
            this.rationale = rationale
            return this
        }

        fun instructions(instructions: CharSequence): Builder {
            this.instructions = instructions
            return this
        }

        fun create(): SimplePermissionCallback {
            requireNotNull(rationale) { "rationale not set" }
            requireNotNull(instructions) { "instructions not set" }
            return SimplePermissionCallback(view, rationale!!, instructions!!, permissionGrantedCallback!!)
        }
    }

    companion object {
        @JvmStatic fun with(anchorView: View): Builder {
            return Builder(anchorView)
        }
    }
}
