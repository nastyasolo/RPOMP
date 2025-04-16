package com.example.rpomp134;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private Button downloadButton, viewButton, deleteButton;
    private String filePath;
    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;

    private final String[] googleDriveUrls = {
            "https://drive.google.com/uc?export=download&id=1YQ6GQzH0IWjTxlzhXNFz5E4Hv7uKVuiQ",
            "https://drive.google.com/uc?export=download&id=12ZOsQ4R_ApHQGPfr9e1pcsbg9g0qG0HU",
            "https://drive.google.com/uc?export=download&id=1OSKQfX1imbP3wX4NFV80xvQnIFNGzjLD",
            "https://drive.google.com/uc?export=download&id=1C0FqWwd4qpjTK9o3K36EmcKCyIOqoDjl",
            "https://drive.google.com/uc?export=download&id=17ROx6X5xG5_484zgSVqPFxmlfJ0I1F9L"

    };

    private final String[] googleDriveFileNames = {
            "Приемы объектно-ориентированного проектирования. Паттерны проектирования.pdf",
            "Совершенный код.pdf",
            "Чистый_код.pdf",
            "Грокаем_алгоритмы.pdf",
            "Алгоритмы. Справочник. С примерами на C, C++, Java и Python.pdf"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        downloadButton = findViewById(R.id.downloadButton);
        viewButton = findViewById(R.id.viewButton);
        deleteButton = findViewById(R.id.deleteButton);
        progressBar = findViewById(R.id.progressBar);

        updateButtonStates();


        sharedPreferences = getSharedPreferences("app_settings", MODE_PRIVATE);
        boolean showPopup = sharedPreferences.getBoolean("show_popup", true);



        if (showPopup) {
            showInstructionPopup();
        }
//        else {
//
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putBoolean("show_popup", true);
//            editor.apply();
//        }

        downloadButton.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                showSourceSelectionDialog();
            } else {
                Toast.makeText(MainActivity.this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
            }
        });

        viewButton.setOnClickListener(v -> openDownloadedFile());
        deleteButton.setOnClickListener(v -> deleteDownloadedFile());

        Button authorButton = findViewById(R.id.authorButton);

        // Устанавливаем обработчик нажатия
        authorButton.setOnClickListener(v -> showAuthorInfoDialog());
    }
    private void updateButtonStates() {
        if (filePath == null) {
            // Если filePath не инициализирован, отключаем кнопки
            viewButton.setEnabled(false);
            deleteButton.setEnabled(false);
            viewButton.setAlpha(0.5f); // Полупрозрачность для визуального отображения недоступности
            deleteButton.setAlpha(0.5f);
            return;
        }

        File file = new File(filePath);
        boolean fileExists = file.exists();

        viewButton.setEnabled(fileExists);
        deleteButton.setEnabled(fileExists);

        // Изменение прозрачности кнопок
        viewButton.setAlpha(fileExists ? 1.0f : 0.5f); // Полная прозрачность, если файла нет
        deleteButton.setAlpha(fileExists ? 1.0f : 0.5f);
    }
    private void showAuthorInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Разработала Сологуб А.В., ПО-11");

        // Формулировка задачи
        String taskDescription = "Задание 1. Реализуйте пример подключения к сети.\n" +
                "Задание 2. Реализуйте коды приложений в примерах из источника (запросы, взаимодействие с сервером через сокеты). \n" +
                "Задание 3. Разработайте мобильное приложение согласно заданию 3 источника, позволяющее пользователю асинхронно скачивать файлы журнала Научно-технический вестник (возможно взять другой источник файлов подобной структуры). \n" +
                "Задание 4. \t. При запуске приложения пользователю должно выводиться всплывающее полупрозрачное уведомление (popupWindow), с краткой инструкцией по использованию приложения (можете написать случайный текст), чекбоксом «Больше не показывать» и кнопкой «ОК».\n" +
                "\n" +
                "Бонус.\n" +
                "Использование собственного источника документов.\n";

        String authorInfo = "Выполнила: Сологуб Анастасия\nГруппа: ПО-11\nЛабораторная работа №13";

        builder.setMessage(taskDescription + "\n\n" + authorInfo);

        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void showInstructionPopup() {
        findViewById(android.R.id.content).post(() -> {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.popup_layout, null);

            PopupWindow popupWindow = new PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true
            );

            CheckBox checkBox = popupView.findViewById(R.id.checkBox);
            Button okButton = popupView.findViewById(R.id.okButton);

            okButton.setOnClickListener(v -> {
                if (checkBox.isChecked()) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("show_popup", false);
                    editor.apply();
                }
                popupWindow.dismiss();
            });

            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        });
    }

    private void showSourceSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выберите источник для загрузки");

        final String[] sourceNames = {
                "Научно-технический вестник",
                "Источник Сологуб А.В., ПО-11"
        };

        builder.setItems(sourceNames, (dialog, which) -> {
            if (which == 0) {
                // Вестник
                showJournalIdDialog();
            } else {
                // Google Drive
                showGoogleDriveIdDialog();
            }
        });

        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }
    private void showGoogleDriveIdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Введите номер ид журнала (1-5)");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String journalId = input.getText().toString().trim();
            if (!journalId.isEmpty()) {
                int id = Integer.parseInt(journalId) - 1; // Преобразуем в индекс массива
                if (id >= 0 && id < googleDriveUrls.length) {
                    new DownloadFile().execute(googleDriveUrls[id]);
                } else {
                    Toast.makeText(MainActivity.this, "Введите корректный номер (1-5)", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Введите номер", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showJournalIdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Введите номер выпуска журнала");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String journalId = input.getText().toString().trim();
            if (!journalId.isEmpty()) {
                String journalUrl = "https://ntv.ifmo.ru/file/journal/" + journalId + ".pdf";

                Log.d("DownloadFile", "Journal URL: " + journalUrl); // Логируем URL
                //new DownloadFile().execute(journalUrl);
                new DownloadFile().execute("https://ntv.ifmo.ru/file/journal/" + journalId + ".pdf");
            } else {
                Toast.makeText(MainActivity.this, "Введите корректный номер", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void openDownloadedFile() {
        File file = new File(filePath);
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri fileUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
            intent.setDataAndType(fileUri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "Нет приложения для открытия PDF", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Файл не найден", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteDownloadedFile() {
        File file = new File(filePath);
        if (file.delete()) {
            Toast.makeText(this, "Файл удален", Toast.LENGTH_SHORT).show();
            updateButtonStates();
//            viewButton.setEnabled(false);
//            deleteButton.setEnabled(false);
        }
    }



    private class DownloadFile extends AsyncTask<String, Integer, Boolean> {
        private int fileLength; // Длина файла в байтах
        private boolean isNtvJournal; // Флаг для определения, является ли источник вестником

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0); // Сбрасываем прогресс

            // Если источник — вестник, переключаем в режим неопределенного прогресса
            if (isNtvJournal) {
                progressBar.setIndeterminate(true);
            }
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            String fileUrl = strings[0];
            String fileName;

            // Определяем имя файла в зависимости от источника
            if (fileUrl.startsWith("https://drive.google.com")) {
                int id = Arrays.asList(googleDriveUrls).indexOf(fileUrl);
                fileName = googleDriveFileNames[id];
            } else {
                fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            }

            File dir = new File(getExternalFilesDir(null), "journals");
            if (!dir.exists()) dir.mkdirs();


            filePath = new File(dir, fileName).getAbsolutePath();

            // Остальная часть метода остается без изменений
            HttpURLConnection connection = null;
            InputStream inputStream = null;
            FileOutputStream outputStream = null;

            try {
                URL url = new URL(fileUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setInstanceFollowRedirects(true);
                connection.connect();

                // Логируем конечный URL после редиректов
                Log.d("DownloadFile", "Final URL: " + connection.getURL());

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.d("DownloadFile", "Response Code: " + connection.getResponseCode());
                    return false;
                }

                fileLength = connection.getContentLength();
                Log.d("DownloadFile", "File Length: " + fileLength);

                inputStream = new BufferedInputStream(connection.getInputStream());
                outputStream = new FileOutputStream(filePath);

                byte[] data = new byte[4096];
                int bytesRead;
                long totalBytesRead = 0;

                while ((bytesRead = inputStream.read(data)) != -1) {
                    outputStream.write(data, 0, bytesRead);
                    totalBytesRead += bytesRead;

                    if (fileLength > 0) {
                        int progress = (int) (totalBytesRead * 100 / fileLength);
                        Log.d("DownloadFile", "Progress: " + progress + "%");
                        publishProgress(progress);
                    }
                }

                return true;
            } catch (Exception e) {
                Log.e("DownloadError", "Ошибка загрузки файла", e);
                return false;
            } finally {
                try {
                    if (outputStream != null) outputStream.close();
                    if (inputStream != null) inputStream.close();
                    if (connection != null) connection.disconnect();
                } catch (IOException ignored) {}
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // Обновляем ProgressBar только если длина файла известна
            if (fileLength > 0) {
                progressBar.setProgress(values[0]);
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            progressBar.setVisibility(View.GONE);
            progressBar.setIndeterminate(false); // Возвращаем обычный режим
            if (success) {
                updateButtonStates();
//                viewButton.setEnabled(true);
//                deleteButton.setEnabled(true);
                Toast.makeText(MainActivity.this, "Файл загружен", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Ошибка загрузки", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
