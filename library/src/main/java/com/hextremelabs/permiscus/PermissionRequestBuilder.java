package com.hextremelabs.permiscus;

import com.hextremelabs.permiscus.callbacks.OnPermissionCallback;
import com.hextremelabs.permiscus.callbacks.OnPermissionDeniedCallback;
import com.hextremelabs.permiscus.callbacks.OnPermissionGrantedCallback;
import com.hextremelabs.permiscus.callbacks.OnPermissionShowRationaleCallback;
import androidx.annotation.NonNull;

public class PermissionRequestBuilder {
    private final @NonNull PermissionManager manager;
    private final @NonNull String[] permissions;
    private int requestCode = -1;

    private OnPermissionGrantedCallback grantedCallback;
    private OnPermissionDeniedCallback deniedCallback;
    private OnPermissionShowRationaleCallback showRationaleCallback;

    PermissionRequestBuilder(@NonNull PermissionManager manager, @NonNull String[] permissions) {
        this.manager = manager;
        this.permissions = permissions;
    }

    public PermissionRequestBuilder usingRequestCode(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    public PermissionRequestBuilder onCallback(OnPermissionCallback callback) {
        this.grantedCallback = callback;
        this.deniedCallback = callback;
        this.showRationaleCallback = callback;
        return this;
    }

    public PermissionRequestBuilder onPermissionGranted(OnPermissionGrantedCallback callback) {
        this.grantedCallback = callback;
        return this;
    }

    public PermissionRequestBuilder onPermissionDenied(OnPermissionDeniedCallback callback) {
        this.deniedCallback = callback;
        return this;
    }

    public PermissionRequestBuilder onPermissionShowRationale(OnPermissionShowRationaleCallback callback) {
        this.showRationaleCallback = callback;
        return this;
    }

    public void request() {
        PermissionRequest permissionRequest = new PermissionRequest(manager, permissions, requestCode, grantedCallback, deniedCallback, showRationaleCallback);
        manager.request(permissionRequest);
    }

    public void check() {
        PermissionRequest permissionRequest = new PermissionRequest(manager, permissions, requestCode, grantedCallback, deniedCallback, showRationaleCallback);
        manager.check(permissionRequest);
    }
}
