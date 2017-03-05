package com.hextremelabs.permiscus.callbacks;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import com.hextremelabs.permiscus.PermissionRequest;
import com.hextremelabs.permiscus.R;

/**
 * @author ADIO Kingsley O.
 * @since 05 Mar, 2017
 */

public final class SimplePermissionCallback implements OnPermissionCallback {

    private final View anchorView;
    private final CharSequence rationale;
    private final CharSequence instructions;
    private final OnPermissionGrantedCallback permissionGrantedCallback;

    private SimplePermissionCallback(View anchorView, CharSequence rationale,
      CharSequence instructions, OnPermissionGrantedCallback permissionGrantedCallback) {
        this.anchorView = anchorView;
        this.rationale = rationale;
        this.instructions = instructions;
        this.permissionGrantedCallback = permissionGrantedCallback;
    }

    @Override
    public void onPermissionDenied(boolean neverAskAgain) {
        Snackbar snackbar = Snackbar.make(anchorView, R.string.permissions_denied, Snackbar.LENGTH_LONG);
        if (neverAskAgain) {
            snackbar.setDuration(Snackbar.LENGTH_INDEFINITE)
              .setText(R.string.permissions_disabled)
              .setAction(R.string.change_settings, new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      Context context = v.getContext();

                      Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                      Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                      intent.setData(uri);
                      context.startActivity(intent);
                      Toast.makeText(context, instructions, Toast.LENGTH_SHORT).show();
                  }
              });
        }

        snackbar.show();
    }

    @Override
    public void onPermissionGranted() {
        permissionGrantedCallback.onPermissionGranted();
    }

    @Override
    public void onPermissionShowRationale(final PermissionRequest permissionRequest) {
        Snackbar.make(anchorView, rationale, Snackbar.LENGTH_INDEFINITE)
          .setAction(R.string.proceed, new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  permissionRequest.acceptPermissionRationale();
              }
          })
          .show();
    }

    public static Builder with(View anchorView) {
        return new Builder(anchorView);
    }


    public static class Builder {
        private View view;
        private OnPermissionGrantedCallback permissionGrantedCallback;
        private CharSequence rationale;
        private CharSequence instructions;

        private Builder(View view) {
            this.view = view;
        }

        public Builder rationale(@StringRes int rationale) {
            this.rationale = view.getContext().getText(rationale);
            return this;
        }

        public Builder instructions(@StringRes int instructions) {
            this.instructions = view.getContext().getText(instructions);
            return this;
        }

        public Builder onPermissionsGranted(OnPermissionGrantedCallback permissionGrantedCallback) {
            this.permissionGrantedCallback = permissionGrantedCallback;
            return this;
        }

        public Builder rationale(CharSequence rationale) {
            this.rationale = rationale;
            return this;
        }

        public Builder instructions(CharSequence instructions) {
            this.instructions = instructions;
            return this;
        }

        public SimplePermissionCallback create() {
            if (rationale == null)
                throw new IllegalArgumentException("rationale not set");
            else if (instructions == null)
                throw new IllegalArgumentException("instructions not set");
            else if (permissionGrantedCallback == null)
                throw new IllegalArgumentException("permission granted callback not set");

            return new SimplePermissionCallback(view, rationale, instructions, permissionGrantedCallback);
        }
    }
}
