package com.hextremelabs.permiscus.demo.contacts;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hextremelabs.permiscus.PermissionManager;
import com.hextremelabs.permiscus.PermissionRequest;
import com.hextremelabs.permiscus.callbacks.OnPermissionCallback;
import com.hextremelabs.permiscus.demo.R;

public class ContactRationaleFragment extends Fragment implements FragmentCompat.OnRequestPermissionsResultCallback {
    private final PermissionManager permissionManager = PermissionManager.create(this);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts_rationale, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Button cancelButton = (Button) view.findViewById(R.id.button_contacts_rationale_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCancel();
            }
        });

        Button okButton = (Button) view.findViewById(R.id.button_contacts_rationale_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOk();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionManager.handlePermissionResult(requestCode, grantResults);
    }

    private void onCancel() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new ContactRequestFragment());
        fragmentTransaction.commit();
    }

    private void onOk() {
        permissionManager.with(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)
          .onCallback(new PermissionsCallback())
          .request();
    }

    private class PermissionsCallback implements OnPermissionCallback {

        @Override
        public void onPermissionDenied(boolean neverAskAgain) {
            getFragmentManager().beginTransaction()
              .replace(R.id.fragment_container, new ContactRequestFragment())
              .commit();
        }

        @Override
        public void onPermissionGranted() {
            getFragmentManager().beginTransaction()
              .replace(R.id.fragment_container, new ContactResultFragment())
              .commit();
        }

        @Override
        public void onPermissionShowRationale(PermissionRequest permissionRequest) {
            permissionRequest.acceptPermissionRationale();
        }
    }
}
