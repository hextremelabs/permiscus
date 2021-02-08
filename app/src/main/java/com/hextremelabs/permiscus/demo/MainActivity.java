package com.hextremelabs.permiscus.demo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.hextremelabs.permiscus.PermissionManager;
import com.hextremelabs.permiscus.callbacks.OnPermissionCallback;
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
        b.setOnClickListener(view -> showCameraPreview());

        // Setup the contact fragment...
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
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
          .onPermissionsGranted(() -> {
              Intent intent = new Intent(MainActivity.this, CameraPreviewActivity.class);
              startActivity(intent);
          })
          .create();

        permissionManager.with(Manifest.permission.CAMERA)
          .usingRequestCode(PERMISSION_REQUEST_CAMERA)
          .onCallback(callback)
          .request();
    }
}
