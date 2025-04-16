package com.example.rpomp177;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {
    private ImageView imageView;
    private Button btnPrev, btnNext;
    private TextView tvImageInfo;
    private List<String> imagePaths;
    private int currentIndex = 0;
    private static final int STORAGE_PERMISSION_CODE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        imageView = findViewById(R.id.imageView);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        tvImageInfo = findViewById(R.id.tvImageInfo);

        if (checkStoragePermission()) {
            loadImages();
        } else {
            requestStoragePermission();
        }

        btnPrev.setOnClickListener(v -> showPreviousImage());
        btnNext.setOnClickListener(v -> showNextImage());
    }

    private void loadImages() {
        imagePaths = new ArrayList<>();
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MediaApp");

        if (mediaStorageDir.exists() && mediaStorageDir.isDirectory()) {
            File[] files = mediaStorageDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (isImageFile(file)) {
                        imagePaths.add(file.getAbsolutePath());
                    }
                }
            }
        }

        if (imagePaths.isEmpty()) {
            Toast.makeText(this, "Нет сохраненных фотографий", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            displayCurrentImage();
        }
    }

    private boolean isImageFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png");
    }

    private void displayCurrentImage() {
        if (currentIndex >= 0 && currentIndex < imagePaths.size()) {
            try {
                // Оптимизация загрузки изображения
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2; // Уменьшаем размер в 2 раза для экономии памяти

                Bitmap bitmap = BitmapFactory.decodeFile(imagePaths.get(currentIndex), options);
                imageView.setImageBitmap(bitmap);
                tvImageInfo.setText((currentIndex + 1) + " / " + imagePaths.size());

                // Обновляем состояние кнопок
                btnPrev.setEnabled(currentIndex > 0);
                btnNext.setEnabled(currentIndex < imagePaths.size() - 1);
            } catch (Exception e) {
                Toast.makeText(this, "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show();
                Log.e("Gallery", "Error loading image", e);
            }
        }
    }

    private void showPreviousImage() {
        if (currentIndex > 0) {
            currentIndex--;
            displayCurrentImage();
        }
    }

    private void showNextImage() {
        if (currentIndex < imagePaths.size() - 1) {
            currentIndex++;
            displayCurrentImage();
        }
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadImages();
            } else {
                Toast.makeText(this, "Требуется разрешение для просмотра галереи", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    // Добавляем жесты для масштабирования
    public void onZoomIn(View view) {
        imageView.setScaleX(imageView.getScaleX() * 1.2f);
        imageView.setScaleY(imageView.getScaleY() * 1.2f);
    }

    public void onZoomOut(View view) {
        imageView.setScaleX(imageView.getScaleX() / 1.2f);
        imageView.setScaleY(imageView.getScaleY() / 1.2f);
    }

    public void onResetZoom(View view) {
        imageView.setScaleX(1.0f);
        imageView.setScaleY(1.0f);
    }
}