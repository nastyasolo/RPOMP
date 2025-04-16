package com.example.rpomp145;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_WRITE = 1001;
    private boolean permissionGranted;
    private MediaPlayer mPlayer;
    private Button startButton, pauseButton, stopButton, resetButton,speedUpButton,speedDownButton;
    private VideoView videoView;
    private String setType;
    private ImageView imageView;
    private static final int PICKFILE_RESULT_CODE = 1;
    private SeekBar seekBar;
    private boolean isUserSeeking = false; // Глобальная переменная для управления SeekBar
    private float playbackSpeed = 1.0f; // Начальная скорость воспроизведения

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setType = "";
        seekBar = findViewById(R.id.seekBar);
        resetButton = findViewById(R.id.buttonReset);
        speedUpButton = findViewById(R.id.speedUp);
        speedDownButton = findViewById(R.id.speedDown);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mPlayer != null) {
                    mPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isUserSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isUserSeeking = false;
                if (mPlayer != null) {
                    mPlayer.seekTo(seekBar.getProgress());
                }
            }
        });

        if (!permissionGranted) {
            checkPermissions();
        }

        startButton = findViewById(R.id.start);
        pauseButton = findViewById(R.id.pause);
        stopButton = findViewById(R.id.stop);
        videoView = findViewById(R.id.videoView);
        imageView = findViewById(R.id.imageView);

        // Таймер обновления SeekBar
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (mPlayer != null && !isUserSeeking) {
                    seekBar.setProgress(mPlayer.getCurrentPosition());
                }
            }
        }, 0, 1000);


        Button buttonAuthor = findViewById(R.id.buttonAuthor);

        buttonAuthor.setOnClickListener(v -> {
            // Текст задания и информация об авторе
            String taskDescription = "Задание:Создать приложение, обеспечивающее выбор файла во внешнем хранилище с возможностью дальнейшей его обработки в зависимости от расширения: \n" +
                    "- графический файл отобразить с использованием элемента. ImageView\n" +
                    "- аудиофайл воспроизвести с использованием элемента MediaPlayer;\n" +
                    "- видеофайл воспроизвести с использованием элемента VideoView.\n" +
                    "\n" +
                    "2. Загрузить заранее набор медиафайлов (изображения, аудио, видео) для тестирования. \n" +
                    "3. Создать новый проект.\n" +
                    "4. Добавить необходимые элементы интерфейса для реализации всех функций, перечисленных в задании. Для реализации различных функций можно использовать как дополнительные разметки, так и дополнительные активности. Простейший вариант оформления интерфейса приложения приведен в описании работы.\n";

            // Создаем AlertDialog
            new AlertDialog.Builder(this)
                    .setTitle("Разработала Сологуб А.В., ПО-11") // Заголовок
                    .setMessage(taskDescription) // Текст задания
                    .setPositiveButton("OK", null) // Кнопка "OK"
                    .show(); // Показываем диалог
        });
    }

    public void onClFile(View viewButton) {
        if (viewButton.getId() == R.id.buttonAudio) {
            setType = "audio/*";
        } else if (viewButton.getId() == R.id.buttonVideo) {
            setType = "video/*";
        } else if (viewButton.getId() == R.id.buttonImage) {
            setType = "image/*";
        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(setType);
        startActivityForResult(intent, PICKFILE_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICKFILE_RESULT_CODE && resultCode == RESULT_OK) {
            if (setType == null) {
                Toast.makeText(this, "Тип файла не выбран", Toast.LENGTH_SHORT).show();
                return;
            }

            Uri fileUri = data.getData();
            Log.d("FileURI", "Selected file URI: " + fileUri.toString());

            // Перед открытием нового контента останавливаем все медиа
            stopAllMedia();

            if (setType.equals("audio/*")) {
                playAudio(fileUri);
            } else if (setType.equals("video/*")) {
                playVideo(fileUri);
            } else if (setType.equals("image/*")) {
                showImage(fileUri);
            }

            resetButton.setVisibility(View.VISIBLE);
        }
    }

    private void playAudio(Uri fileUri) {
        try {
            mPlayer = new MediaPlayer();
            mPlayer.setDataSource(this, fileUri);
            mPlayer.prepare();
            mPlayer.start();

            startButton.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.VISIBLE);
            stopButton.setVisibility(View.VISIBLE);
            seekBar.setVisibility(View.VISIBLE);

            speedUpButton.setVisibility(View.VISIBLE);
            speedDownButton.setVisibility(View.VISIBLE);

            mPlayer.setOnCompletionListener(mp -> {
                stopPlay();
                Toast.makeText(MainActivity.this, "Аудио завершено", Toast.LENGTH_SHORT).show();
            });

            seekBar.setMax(mPlayer.getDuration());
        } catch (IOException e) {
            Log.e("MediaPlayerError", "Error setting data source", e);
            Toast.makeText(this, "Ошибка при воспроизведении аудио", Toast.LENGTH_SHORT).show();
        }
    }

    private void playVideo(Uri fileUri) {
        videoView.setVideoURI(fileUri);
        videoView.start();
        videoView.setVisibility(View.VISIBLE);
        videoView.setOnCompletionListener(mp -> {
            videoView.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, "Видео завершено", Toast.LENGTH_SHORT).show();
        });
    }

    private void showImage(Uri fileUri) {
        imageView.setImageURI(fileUri);
        imageView.setVisibility(View.VISIBLE);
    }

    private void stopAllMedia() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        if (videoView != null && videoView.isPlaying()) {
            videoView.stopPlayback();
            videoView.setVisibility(View.GONE);
        }
        imageView.setVisibility(View.GONE);
    }

    public void onReset(View view) {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        if (videoView != null && videoView.isPlaying()) {
            videoView.stopPlayback();
        }

        // Скрываем все элементы интерфейса
        imageView.setVisibility(View.GONE);
        videoView.setVisibility(View.GONE);
        seekBar.setVisibility(View.GONE);
        startButton.setVisibility(View.GONE);
        pauseButton.setVisibility(View.GONE);
        stopButton.setVisibility(View.GONE);
        speedUpButton.setVisibility(View.GONE);
        speedDownButton.setVisibility(View.GONE);


        view.setVisibility(View.VISIBLE);

        Toast.makeText(this, "Выбор сброшен", Toast.LENGTH_SHORT).show();
    }


    public void play(View view) {
        if (mPlayer != null) {
            mPlayer.start();
            startButton.setEnabled(false);
            pauseButton.setEnabled(true);
            stopButton.setEnabled(true);
        }
    }

    public void pause(View view) {
        if (mPlayer != null) {
            mPlayer.pause();
            startButton.setEnabled(true);
            pauseButton.setEnabled(false);
            stopButton.setEnabled(true);
        }
    }

    public void stop(View view) {
        stopPlay();
    }

    private void stopPlay() {
        if (mPlayer != null) {
            mPlayer.stop();
            try {
                mPlayer.prepare();
                mPlayer.seekTo(0);
                startButton.setEnabled(true);
                playbackSpeed = 1.0f; // Сброс скорости
            } catch (Throwable t) {
                Toast.makeText(this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkPermissions() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_WRITE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            permissionGranted = true;
            Toast.makeText(this, "Разрешения получены", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Необходимо дать разрешения", Toast.LENGTH_LONG).show();
        }
    }
    public void speedUp(View view) {
        if (mPlayer != null) {
            playbackSpeed += 0.5f; // Увеличиваем скорость на 0.5
            if (playbackSpeed > 3.0f) {
                playbackSpeed = 3.0f; // Максимальная скорость 3x
            }
            mPlayer.setPlaybackParams(mPlayer.getPlaybackParams().setSpeed(playbackSpeed));
            Toast.makeText(this, "Скорость: " + playbackSpeed + "x", Toast.LENGTH_SHORT).show();
        }
    }

    public void speedDown(View view) {
        if (mPlayer != null) {
            playbackSpeed -= 0.5f; // Уменьшаем скорость на 0.5
            if (playbackSpeed < 0.5f) {
                playbackSpeed = 0.5f; // Минимальная скорость 0.5x
            }
            mPlayer.setPlaybackParams(mPlayer.getPlaybackParams().setSpeed(playbackSpeed));
            Toast.makeText(this, "Скорость: " + playbackSpeed + "x", Toast.LENGTH_SHORT).show();
        }
    }
}
