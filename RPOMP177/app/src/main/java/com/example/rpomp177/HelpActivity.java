package com.example.rpomp177;



import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        TextView tvHelp = findViewById(R.id.tvHelp);
        String helpText = "Мультимедийное приложение\n\n Разработала: Сологуб А.В., ПО-11\n" +
                "1. Камера - позволяет делать фотографии, которые сохраняются в папке Pictures/MediaApp\n\n" +
                "2. Медиаплеер - воспроизводит аудио и видео файлы с возможностью управления воспроизведением\n\n" +
                "3. Галерея - просмотр сделанных фотографий с возможностью масштабирования и перелистывания\n\n" +
                "Управление:\n" +
                "- Жесты влево/вправо для перелистывания фото\n" +
                "- Кнопки +/- для масштабирования";

        tvHelp.setText(helpText);
    }
}