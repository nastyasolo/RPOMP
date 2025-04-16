package com.example.rpomp81;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import java.net.URL;

import java.net.MalformedURLException;

public class SettingsActivity extends AppCompatActivity {
    private static final String DEFAULT_URL = "https://gist.githubusercontent.com/nastyasolo/440d72025dcd6552d030fcead11492b8/raw/e0ca4f644bfea7c2e7b3710a3c012da29620715d/dogs.json";
    private static final int DEFAULT_ROW_COUNT = 35;
    private static final int DEFAULT_AGE_FILTER = 0;

    private EditText serverUrlEditText, rowCountEditText, ageFilterEditText;
    private Spinner urlSpinner;
    private CheckBox showAgeCheckBox, showDescriptionCheckBox;
    private Button saveButton, resetButton;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = getSharedPreferences("AppSettings", MODE_PRIVATE);

        serverUrlEditText = findViewById(R.id.serverUrlEditText);
        rowCountEditText = findViewById(R.id.rowCountEditText);
        ageFilterEditText = findViewById(R.id.ageFilterEditText);
        urlSpinner = findViewById(R.id.urlSpinner);
        showAgeCheckBox = findViewById(R.id.showAgeCheckBox);
        showDescriptionCheckBox = findViewById(R.id.showDescriptionCheckBox);
        saveButton = findViewById(R.id.saveButton);
        resetButton = findViewById(R.id.resetButton);
        Button applyUrlButton = findViewById(R.id.applyUrlButton); // Новая кнопка

        // Настройка Spinner для выбора URL
        ArrayAdapter<CharSequence> urlAdapter = ArrayAdapter.createFromResource(this,
                R.array.url_options, android.R.layout.simple_spinner_item);
        urlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        urlSpinner.setAdapter(urlAdapter);

        // Обработчик для кнопки "Применить"
        applyUrlButton.setOnClickListener(v -> {
            String selectedUrl = urlSpinner.getSelectedItem().toString();
            serverUrlEditText.setText(selectedUrl);
        });

        // Загрузка текущих настроек
        loadSettings();

        // Сохранение настроек
        saveButton.setOnClickListener(v -> saveSettings());

        // Сброс настроек
        resetButton.setOnClickListener(v -> resetSettings());
    }

    private void loadSettings() {
        String currentUrl = preferences.getString("server_url", DEFAULT_URL);
        serverUrlEditText.setText(currentUrl);
        rowCountEditText.setText(String.valueOf(preferences.getInt("row_count", DEFAULT_ROW_COUNT)));
        ageFilterEditText.setText(String.valueOf(preferences.getInt("age_filter", DEFAULT_AGE_FILTER)));
        showAgeCheckBox.setChecked(preferences.getBoolean("show_age", true));
        showDescriptionCheckBox.setChecked(preferences.getBoolean("show_description", true));

        // Установка выбранного URL в Spinner
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) urlSpinner.getAdapter();
        int position = adapter.getPosition(currentUrl);
        if (position >= 0) {
            urlSpinner.setSelection(position);
        }
    }

    private boolean isValidUrl(String url) {
        try {
            new URL(url); // Пытаемся создать объект URL
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    private void saveSettings() {
        try {
            String url = serverUrlEditText.getText().toString();
            if (!isValidUrl(url)) {
                Toast.makeText(this, "Некорректный URL", Toast.LENGTH_SHORT).show();
                return;
            }

            int rowCount = Integer.parseInt(rowCountEditText.getText().toString());
            int ageFilter = Integer.parseInt(ageFilterEditText.getText().toString());

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("server_url", url);
            editor.putInt("row_count", rowCount);
            editor.putInt("age_filter", ageFilter);
            editor.putBoolean("show_age", showAgeCheckBox.isChecked());
            editor.putBoolean("show_description", showDescriptionCheckBox.isChecked());
            editor.apply();
            Toast.makeText(this, "Настройки сохранены", Toast.LENGTH_SHORT).show();
            finish();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(this, "Некорректные числовые значения", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка при сохранении настроек: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void resetSettings() {
        serverUrlEditText.setText(DEFAULT_URL);
        rowCountEditText.setText(String.valueOf(DEFAULT_ROW_COUNT));
        ageFilterEditText.setText(String.valueOf(DEFAULT_AGE_FILTER));
        showAgeCheckBox.setChecked(true);
        showDescriptionCheckBox.setChecked(true);

        // Сброс Spinner на первый элемент
        urlSpinner.setSelection(0);
    }

}