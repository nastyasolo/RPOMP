package com.example.rpomp166;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements GestureOverlayView.OnGesturePerformedListener {

    private GestureLibrary gestureLibrary;
    private TextView textView;
    private Button helpButton;
    private CameraManager cameraManager;
    private String cameraId;
    private boolean isFlashOn = false;
    private String momPhoneNumber = "+375298032232";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        helpButton = findViewById(R.id.helpButton);

        gestureLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!gestureLibrary.load()) {
            Toast.makeText(this, "Не удалось загрузить жесты", Toast.LENGTH_SHORT).show();
            finish();
        }


        GestureOverlayView gestureOverlayView = findViewById(R.id.gestureOverlayView);
        gestureOverlayView.addOnGesturePerformedListener(this);

        helpButton.setOnClickListener(v -> showHelpDialog());

        // Инициализация фонарика
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (Exception e) {
            textView.setText("Фонарик не поддерживается");
        }
    }

    private void showHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Справка");
        builder.setMessage("Это приложение позволяет управлять функциями с помощью жестов:\n\n" +
                "⚡ - Включить/выключить фонарик\n" +
                "□ - Сделать скриншот\n" +
                "♫ - Открыть музыкальный плеер\n" +
                "❤ - Позвонить маме\n\n" +
                "Навигация по лабораторным работам:\n" +
                "1-8 - Открыть соответствующую лабораторную работу\n\n" +
                "Принцип работы: нарисуйте жест в любом месте экрана");
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private void toggleFlashlight() {
        try {
            cameraManager.setTorchMode(cameraId, !isFlashOn);
            isFlashOn = !isFlashOn;
            textView.setText(isFlashOn ? "Фонарик: ВКЛ" : "Фонарик: ВЫКЛ");
        } catch (Exception e) {
            textView.setText("Ошибка фонарика");
        }
    }

    private void takeScreenshot() {
        View rootView = getWindow().getDecorView().getRootView();
        rootView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(rootView.getDrawingCache());
        rootView.setDrawingCacheEnabled(false);

        try {
            MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Screenshot", "");
            textView.setText("Скриншот сохранён в галерею!");
        } catch (Exception e) {
            textView.setText("Ошибка сохранения скриншота");
        }
    }

    private void callMom() {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + momPhoneNumber));
            startActivity(intent);
            textView.setText("Набираю маму...");
        } catch (Exception e) {
            textView.setText("Ошибка вызова");
        }
    }

    private void playMusic() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse("content://media/external/audio/media"), "audio/*");
            startActivity(intent);
            textView.setText("Открываю музыкальный плеер...");
        } catch (Exception e) {
            textView.setText("Ошибка открытия музыки");
        }
    }

    private boolean isAppInstalled(String packageName) {
        try {
            getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void launchLabWork(int labNumber) {
        // Карта соответствия номеров лаб и суффиксов пакетов
        Map<Integer, String> labPackages = new HashMap<Integer, String>() {{
            put(1, "rpomp81");
            put(2, "rpomp92");
            put(3, "rpomp103");
            put(4, "rpomp134");
            put(5, "rpomp145");
            put(6, "rpomp166");
            put(7, "rpomp177");
            put(8, "rpomp208");
        }};

        String basePackage = getPackageName().replace(".rpomp166", "");
        String packageSuffix = labPackages.get(labNumber);
        String packageName = basePackage + "." + packageSuffix;

        if (packageSuffix == null) {
            textView.setText("Неизвестный номер лабы");
            return;
        }

        if (isAppInstalled(packageName)) {
            try {
                Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
                if (intent != null) {
                    startActivity(intent);
                    textView.setText("Открыта лаб.работа " + labNumber);
                } else {
                    textView.setText("Не удалось запустить");
                }
            } catch (Exception e) {
                textView.setText("Ошибка запуска: " + e.getMessage());
            }
        } else {
            textView.setText("Лаб.работа " + labNumber + " не найдена");
            Toast.makeText(this, "Искали пакет: " + packageName, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        ArrayList<Prediction> predictions = gestureLibrary.recognize(gesture);

        if (predictions.size() > 0) {
            Prediction prediction = predictions.get(0);

            if (prediction.score > 1.0) {
                String gestureName = prediction.name;

                switch (gestureName) {
                    case "flashlight":
                        toggleFlashlight();
                        break;
                    case "screenshot":
                        takeScreenshot();
                        break;
                    case "music":
                        playMusic();
                        break;
                    case "heart":
                        callMom();
                        break;
                    case "1": case "2": case "3": case "4":
                    case "5": case "6": case "7": case "8":
                        launchLabWork(Integer.parseInt(gestureName));
                        break;
                    default:
                        textView.setText("Неизвестный жест: " + gestureName);
                }
            }
        }
    }
    public void showAuthorInfo(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Информация о работе");
        builder.setMessage("Лабораторная работа №6\n" +
                "Принципы работы с жестами\n\n" +
                "Выполнила: Сологуб Анастасия\n" +
                "Группа: ПО-11\n\n" +
                "Задание:\n" +
                "1. Разработать связный набор жестов\n" +
                "2. Реализовать навигацию по жестам\n" +
                "3. Добавить информационные сообщения");
        builder.setPositiveButton("OK", null);
        builder.show();
    }
}