package com.example.rpomp81;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Устанавливаем Toolbar как ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Сологуб ПО11");
        }
        try {
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new DogListFragment())
                        .commit();
            }

            Button settingsButton = findViewById(R.id.settingsButton);
            settingsButton.setOnClickListener(v -> {
                startActivity(new Intent(this, SettingsActivity.class));
            });

            Button authorButton = findViewById(R.id.authorButton);
            authorButton.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Разработала Сологуб Анастасия Викторовна, ПО-11");
                builder.setMessage("Формулировка задачи     Отображение списка элементов на Android загруженного с использованием json. Реализовать интерфейс приложения для отображения списка элементов. В качестве данных для списка использовать файл в формате json, загруженный с удаленного сервера. Загрузка выполняется в ходе работы по команде пользователя, например, «Загрузить данные». Приложение в минимальном исполнении должно: отображать список элементов внутри фрагмента, список занимает более одного экрана (прокрутка), список можно пролистать, отдельный элемент списка с пользовательским стилем/дизайном, выполнять запрос на получение данных с удаленного сервера, выполнять преобразование json-структуры в коллекцию объектов, выделение отдельного элемента списка с отображение детальной информации на отдельном экране, отображать детальную информацию об элементе внутри отдельного фрагмента.");
                builder.setPositiveButton("OK", null);
                builder.show();
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка при запуске приложения: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu); // Подключаем меню
        return true; // Возвращаем true, чтобы меню отобразилось
    }


    private void saveDogsToCSV() {
        // Получаем список собак из фрагмента
        DogListFragment dogListFragment = (DogListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (dogListFragment != null) {
            List<Dog> dogs = dogListFragment.getDogList();

            // Путь для сохранения файла
            File file = new File(getExternalFilesDir(null), "dogs.csv");

            try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
                String[] header = {"Name", "Age", "Description", "Image"};
                writer.writeNext(header);

                for (Dog dog : dogs) {
                    String[] data = {dog.getName(), String.valueOf(dog.getAge()), dog.getDescription(), dog.getImage()};
                    writer.writeNext(data);
                }

                Toast.makeText(this, "Данные сохранены в " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Ошибка при сохранении данных: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Фрагмент не найден", Toast.LENGTH_SHORT).show();
        }
    }
    private void sendEmailWithDogs() {
        // Получаем список собак из фрагмента
        DogListFragment dogListFragment = (DogListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (dogListFragment != null) {
            List<Dog> dogs = dogListFragment.getDogList();

            StringBuilder sb = new StringBuilder();
            for (Dog dog : dogs) {
                sb.append("Имя: ").append(dog.getName()).append("\n")
                        .append("Возраст: ").append(dog.getAge()).append("\n")
                        .append("Описание: ").append(dog.getDescription()).append("\n")
                        .append("Изображение: ").append(dog.getImage()).append("\n\n");
            }

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Список собак");
            intent.putExtra(Intent.EXTRA_TEXT, sb.toString());

            startActivity(Intent.createChooser(intent, "Отправить по email"));
        } else {
            Toast.makeText(this, "Фрагмент не найден", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.menu_save_csv) {
            saveDogsToCSV();
            return true;
        } else if (id == R.id.menu_send_email) {
            sendEmailWithDogs();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
