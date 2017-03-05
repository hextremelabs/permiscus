package com.hextremelabs.permiscus.demo;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.hextremelabs.permiscus.PermissionManager;
import com.hextremelabs.permiscus.callbacks.OnPermissionCallback;
import com.hextremelabs.permiscus.callbacks.OnPermissionGrantedCallback;
import com.hextremelabs.permiscus.callbacks.SimplePermissionCallback;
import com.hextremelabs.permiscus.demo.camera.CameraPreviewActivity;
import com.hextremelabs.permiscus.demo.contacts.ContactRequestFragment;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_REQUEST_CAMERA = 0;

    private View mLayout;

    private final PermissionManager permissionManager = PermissionManager.create(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayout = findViewById(R.id.main_layout);

        // Register a listener for the 'Show Camera Preview' button...
        Button b = (Button) findViewById(com.hextremelabs.permiscus.demo.R.id.button_open_camera);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCameraPreview();
            }
        });

        // Setup the contact fragment...
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, new ContactRequestFragment());
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_reset) {
            finish();
            startActivity(getIntent());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionManager.handlePermissionResult(requestCode, grantResults);
    }

    private void showCameraPreview() {
        OnPermissionCallback callback = SimplePermissionCallback.with(mLayout)
          .rationale("Camera permission is required to take your pictures")
          .instructions("Open permissions and tap on Camera to enable it")
          .onPermissionsGranted(new CameraPermissionGrantedCallback())
          .create();

        permissionManager.with(Manifest.permission.CAMERA)
          .usingRequestCode(PERMISSION_REQUEST_CAMERA)
          .onCallback(callback)
          .request();
    }

    private class CameraPermissionGrantedCallback implements OnPermissionGrantedCallback {

        @Override
        public void onPermissionGranted() {
            Intent intent = new Intent(MainActivity.this, CameraPreviewActivity.class);
            startActivity(intent);
        }
    }
}
