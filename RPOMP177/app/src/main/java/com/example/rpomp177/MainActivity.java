package com.example.rpomp177;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCamera = findViewById(R.id.btnCamera);
        Button btnMedia = findViewById(R.id.btnMedia);
        Button btnGallery = findViewById(R.id.btnGallery);
        Button btnHelp = findViewById(R.id.btnHelp);

        checkPermissions();

        btnCamera.setOnClickListener(v -> {
            if (checkCameraPermission()) {
                startActivity(new Intent(MainActivity.this, CameraActivity.class));
            }
        });

        btnMedia.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, MediaActivity.class)));

        btnGallery.setOnClickListener(v -> {
            if (checkStoragePermission()) {
                startActivity(new Intent(MainActivity.this, GalleryActivity.class));
            }
        });

        btnHelp.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, HelpActivity.class)));
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },
                    REQUEST_PERMISSIONS);
        }
    }

    private boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Требуется разрешение на использование камеры", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Требуется разрешение на доступ к хранилищу", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}