package com.example.rpomp177;

import androidx.appcompat.app.AppCompatActivity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MediaActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer mediaPlayer;
    private Button btnPlay, btnPause, btnStop, btnNext, btnPrev;
    private SeekBar seekBar;
    private TextView tvCurrentTime, tvTotalTime, tvVideoTitle;
    private Handler handler = new Handler();
    private boolean isSeeking = false;
    private boolean isFullscreen = false;
    private ImageButton btnFullscreen;

    private final int[] videoResources = {R.raw.video1, R.raw.video2, R.raw.video3};
    private final String[] videoTitles = {"Видео 1", "Видео 2", "Видео 3"};
    private int currentVideoIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);
        btnFullscreen = findViewById(R.id.btnFullscreen);
        btnFullscreen.setOnClickListener(v -> toggleFullscreen());
        initViews();
        setupMediaPlayer();
        updateVideoTitle();
    }
    private void toggleFullscreen() {
        if (isFullscreen) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            btnFullscreen.setImageResource(R.drawable.ic_fullscreen);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            btnFullscreen.setImageResource(R.drawable.ic_fullscreen_exit);
        }
        isFullscreen = !isFullscreen;
    }
    private void initViews() {
        surfaceView = findViewById(R.id.surfaceView);
        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        btnStop = findViewById(R.id.btnStop);
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);
        seekBar = findViewById(R.id.seekBar);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        tvVideoTitle = findViewById(R.id.tvVideoTitle);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
    }

    private void setupMediaPlayer() {
        btnPlay.setOnClickListener(v -> playMedia());
        btnPause.setOnClickListener(v -> pauseMedia());
        btnStop.setOnClickListener(v -> stopMedia());
        btnNext.setOnClickListener(v -> playNextVideo());
        btnPrev.setOnClickListener(v -> playPreviousVideo());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    tvCurrentTime.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeeking = true;
                handler.removeCallbacks(updateSeekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
                isSeeking = false;
                handler.postDelayed(updateSeekBar, 100);
            }
        });

        loadVideo(currentVideoIndex);
    }

    private void loadVideo(int index) {
        if (index < 0 || index >= videoResources.length) return;

        currentVideoIndex = index;
        updateVideoTitle();

        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        mediaPlayer = new MediaPlayer();
        try {
            String path = "android.resource://" + getPackageName() + "/" + videoResources[index];
            mediaPlayer.setDataSource(this, Uri.parse(path));
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(mp -> {
                adjustVideoSize();
                mediaPlayer.setDisplay(surfaceHolder);
                seekBar.setMax(mediaPlayer.getDuration());
                tvTotalTime.setText(formatTime(mediaPlayer.getDuration()));
                playMedia();
            });

            mediaPlayer.setOnVideoSizeChangedListener((mp, width, height) -> adjustVideoSize());
            mediaPlayer.setOnCompletionListener(mp -> updatePlaybackControls(false));
            mediaPlayer.setOnSeekCompleteListener(mp -> {
                if (!isSeeking) {
                    handler.postDelayed(updateSeekBar, 100);
                }
            });

        } catch (IOException e) {
            Toast.makeText(this, "Ошибка загрузки видео", Toast.LENGTH_SHORT).show();
        }
    }

    private void adjustVideoSize() {
        if (mediaPlayer != null) {
            int videoWidth = mediaPlayer.getVideoWidth();
            int videoHeight = mediaPlayer.getVideoHeight();
            if (videoWidth > 0 && videoHeight > 0) {
                float aspectRatio = (float) videoHeight / videoWidth;
                int surfaceWidth = surfaceView.getWidth();
                int surfaceHeight = (int) (surfaceWidth * aspectRatio);

                ViewGroup.LayoutParams params = surfaceView.getLayoutParams();
                params.height = surfaceHeight;
                surfaceView.setLayoutParams(params);
            }
        }
    }

    private void updateVideoTitle() {
        tvVideoTitle.setText(videoTitles[currentVideoIndex]);
    }

    private void playNextVideo() {
        currentVideoIndex = (currentVideoIndex + 1) % videoResources.length;
        loadVideo(currentVideoIndex);
    }

    private void playPreviousVideo() {
        currentVideoIndex = (currentVideoIndex - 1 + videoResources.length) % videoResources.length;
        loadVideo(currentVideoIndex);
    }

    private void playMedia() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            updatePlaybackControls(true);
            startPlayback();
        }
    }

    private void pauseMedia() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            updatePlaybackControls(false);
        }
    }

    private void stopMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            try {
                mediaPlayer.prepare();
                mediaPlayer.seekTo(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            seekBar.setProgress(0);
            tvCurrentTime.setText("00:00");
            updatePlaybackControls(false);
        }
    }

    private void updatePlaybackControls(boolean isPlaying) {
        btnPlay.setEnabled(!isPlaying);
        btnPause.setEnabled(isPlaying);
    }

    private void startPlayback() {
        handler.post(updateSeekBar);
    }

    private final Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying() && !isSeeking) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                seekBar.setProgress(currentPosition);
                tvCurrentTime.setText(formatTime(currentPosition));
                handler.postDelayed(this, 100);
            }
        }
    };

    private String formatTime(int milliseconds) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mediaPlayer != null) {
            mediaPlayer.setDisplay(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mediaPlayer != null) {
            mediaPlayer.setDisplay(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateSeekBar);
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}