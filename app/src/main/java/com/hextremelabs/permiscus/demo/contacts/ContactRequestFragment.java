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

public class ContactRequestFragment extends Fragment {
    private final PermissionManager permissionManager = PermissionManager.create(this);

    private Button contactsButton;
    private View contactsDeniedView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts_request, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        contactsButton = (Button) view.findViewById(R.id.button_open_contacts);
        contactsButton.setOnClickListener(view1 -> showContacts());
        contactsDeniedView = view.findViewById(R.id.contacts_permission_denied);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionManager.handlePermissionResult(requestCode, grantResults);
    }

    private void showContacts() {
        permissionManager.with(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)
          .onCallback(new PermissionsCallback())
          .request();
    }

    private class PermissionsCallback implements OnPermissionCallback {

        @Override
        public void onPermissionDenied(boolean neverAskAgain) {
            contactsDeniedView.setVisibility(View.VISIBLE);
            contactsButton.setEnabled(false);
        }

        @Override
        public void onPermissionGranted() {
            requireFragmentManager().beginTransaction()
              .replace(R.id.fragment_container, new ContactResultFragment())
              .commit();
        }

        @Override
        public void onPermissionShowRationale(PermissionRequest permissionRequest) {
            requireFragmentManager().beginTransaction()
              .replace(R.id.fragment_container, new ContactRationaleFragment())
              .commit();
        }
    }
}
