package com.hextremelabs.permiscus.demo.contacts;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.hextremelabs.permiscus.PermissionManager;
import com.hextremelabs.permiscus.PermissionRequest;
import com.hextremelabs.permiscus.callbacks.OnPermissionCallback;
import com.hextremelabs.permiscus.demo.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class ContactRationaleFragment extends Fragment {
    private final PermissionManager permissionManager = PermissionManager.create(this);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts_rationale, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Button cancelButton = (Button) view.findViewById(R.id.button_contacts_rationale_cancel);
        cancelButton.setOnClickListener(view12 -> onCancel());

        Button okButton = (Button) view.findViewById(R.id.button_contacts_rationale_ok);
        okButton.setOnClickListener(view1 -> onOk());
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionManager.handlePermissionResult(requestCode, grantResults);
    }

    private void onCancel() {
        FragmentTransaction fragmentTransaction = requireFragmentManager().beginTransaction();
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
            requireFragmentManager().beginTransaction()
              .replace(R.id.fragment_container, new ContactRequestFragment())
              .commit();
        }

        @Override
        public void onPermissionGranted() {
            requireFragmentManager().beginTransaction()
              .replace(R.id.fragment_container, new ContactResultFragment())
              .commit();
        }

        @Override
        public void onPermissionShowRationale(PermissionRequest permissionRequest) {
            permissionRequest.acceptPermissionRationale();
        }
    }
}
