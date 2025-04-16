package com.example.rpomp208;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String PREFS_NAME = "RoutePrefs";
    private static final String ROUTE_KEY = "saved_route";
    private static final String MARKERS_KEY = "saved_markers";
    private static final long MIN_TIME_BETWEEN_UPDATES = 5000;
    private static final double DEFAULT_ZOOM_LEVEL = 15.0;

    private FusedLocationProviderClient fusedLocationClient;
    private MapView mapView;
    private List<GeoPoint> geoPoints = new ArrayList<>();
    private List<Marker> markers = new ArrayList<>();
    private Polyline polyline;
    private GeoPoint currentLocation;
    private Marker currentLocationMarker;
    private TextView tvLat, tvLon, tvOut, tvDistance, tvDistanceBetweenPoints, tvMarkersCount;
    private TextView tvMarkerTitle, tvMarkerDescription, tvMarkerCoordinates;
    private Button btnAddMarker, btnClearRoute, btnAuthorInfo;
    private CardView cardMarkerInfo;
    private boolean isFirstLocationUpdate = true;
    private double totalDistance = 0.0;
    private double distanceBetweenPoints = 0.0;
    private GestureDetector gestureDetector;
    private Marker selectedMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_main);

        initViews();
        setupMap();
        setupGestureDetector();
        requestLocationPermission();
        restoreRouteAndMarkers();
        setupButtons();
        setupMarkerClickListeners();
    }

    private void initViews() {
        mapView = findViewById(R.id.map);
        tvLat = findViewById(R.id.tvLat);
        tvLon = findViewById(R.id.tvLon);
        tvOut = findViewById(R.id.tvOut);
        tvDistance = findViewById(R.id.tvDistance);
        tvDistanceBetweenPoints = findViewById(R.id.tvDistanceBetweenPoints);
        tvMarkersCount = findViewById(R.id.tvMarkersCount);
        btnAddMarker = findViewById(R.id.btnAddMarker);
        btnClearRoute = findViewById(R.id.btnClearRoute);
        btnAuthorInfo = findViewById(R.id.btnAuthorInfo);
        cardMarkerInfo = findViewById(R.id.cardMarkerInfo);
        tvMarkerTitle = findViewById(R.id.tvMarkerTitle);
        tvMarkerDescription = findViewById(R.id.tvMarkerDescription);
        tvMarkerCoordinates = findViewById(R.id.tvMarkerCoordinates);
    }

    private void setupMap() {
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.getZoomController().setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER);
        mapView.setMultiTouchControls(true);
    }

    private void setupGestureDetector() {
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                handleLongPress(e);
            }
        });

        mapView.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return false;
        });
    }

    private void setupButtons() {
        btnAddMarker.setOnClickListener(v -> {
            if (currentLocation != null) {
                addMarker(currentLocation);
            } else {
                Toast.makeText(this, "Местоположение не определено", Toast.LENGTH_SHORT).show();
            }
        });

        btnClearRoute.setOnClickListener(v -> showClearRouteConfirmationDialog());

        btnAuthorInfo.setOnClickListener(v -> showAuthorInfoDialog());
    }

    private void setupMarkerClickListeners() {
        mapView.getOverlays().add(new org.osmdroid.views.overlay.Overlay() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
                resetMarkerSelection();
                return super.onSingleTapConfirmed(e, mapView);
            }
        });
    }

    private void handleLongPress(MotionEvent event) {
        GeoPoint geoPoint = (GeoPoint) mapView.getProjection().fromPixels((int) event.getX(), (int) event.getY());
        resetMarkerSelection();

        for (Marker marker : markers) {
            GeoPoint markerPosition = (GeoPoint) marker.getPosition();
            double distance = geoPoint.distanceToAsDouble(markerPosition);
            if (distance < 50) {
                selectedMarker = marker;
                showMarkerInfo(marker);
                showMarkerOptionsDialog(marker);
                break;
            }
        }
    }

    private void showMarkerInfo(Marker marker) {
        GeoPoint point = (GeoPoint) marker.getPosition();
        tvMarkerTitle.setText(marker.getTitle());
        tvMarkerDescription.setText(marker.getSnippet());
        tvMarkerCoordinates.setText(String.format(Locale.getDefault(),
                "Координаты: %.6f, %.6f", point.getLatitude(), point.getLongitude()));
        cardMarkerInfo.setVisibility(View.VISIBLE);
    }

    private void resetMarkerSelection() {
        cardMarkerInfo.setVisibility(View.GONE);
        selectedMarker = null;
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Разрешение на доступ к местоположению отклонено", Toast.LENGTH_SHORT).show();
                tvOut.setText("Статус: GPS отключен (нет разрешения)");
            }
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(MIN_TIME_BETWEEN_UPDATES);
        locationRequest.setFastestInterval(MIN_TIME_BETWEEN_UPDATES / 2);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            tvOut.setText("Статус: поиск GPS сигнала...");
        }
    }

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            if (locationResult == null) return;

            for (Location location : locationResult.getLocations()) {
                currentLocation = new GeoPoint(location.getLatitude(), location.getLongitude());

                if (!geoPoints.isEmpty()) {
                    GeoPoint lastPoint = geoPoints.get(geoPoints.size() - 1);
                    distanceBetweenPoints = lastPoint.distanceToAsDouble(currentLocation);
                    totalDistance += distanceBetweenPoints;

                    runOnUiThread(() -> {
                        tvDistance.setText(String.format(Locale.getDefault(), "Пройдено: %.2f м", totalDistance));
                        tvDistanceBetweenPoints.setText(String.format(Locale.getDefault(),
                                "Расстояние между точками: %.2f м", distanceBetweenPoints));
                    });
                }

                geoPoints.add(currentLocation);
                updateRouteOnMap();
                updateCurrentLocationMarker();

                if (isFirstLocationUpdate) {
                    mapView.getController().setZoom(DEFAULT_ZOOM_LEVEL);
                    mapView.getController().setCenter(currentLocation);
                    isFirstLocationUpdate = false;
                }

                runOnUiThread(() -> {
                    tvLat.setText(String.format(Locale.getDefault(), "Широта: %.6f", currentLocation.getLatitude()));
                    tvLon.setText(String.format(Locale.getDefault(), "Долгота: %.6f", currentLocation.getLongitude()));
                    tvOut.setText("Статус: GPS активен");
                });
            }
            saveRouteAndMarkers();
        }
    };

    private void updateRouteOnMap() {
        if (polyline == null) {
            polyline = new Polyline();
            polyline.setWidth(10f);
            polyline.setColor(0xFF3F51B5);
            mapView.getOverlayManager().add(polyline);
        }
        polyline.setPoints(geoPoints);
        mapView.invalidate();
    }

    private void updateCurrentLocationMarker() {
        if (currentLocationMarker == null) {
            currentLocationMarker = new Marker(mapView);
            currentLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            currentLocationMarker.setTitle("Ваше местоположение");
            currentLocationMarker.setIcon(getResources().getDrawable(R.drawable.ic_human_small));
            mapView.getOverlays().add(currentLocationMarker);
        }
        currentLocationMarker.setPosition(currentLocation);
        currentLocationMarker.setSnippet(String.format(Locale.getDefault(),
                "Широта: %.6f\nДолгота: %.6f", currentLocation.getLatitude(), currentLocation.getLongitude()));
    }

    private void addMarker(GeoPoint point) {
        Marker marker = new Marker(mapView);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle("Фиксация " + (markers.size() + 1));
        marker.setSnippet("Добавлено: " + new SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault()).format(new Date()));
        marker.setIcon(getResources().getDrawable(R.drawable.ic_marker));

        marker.setOnMarkerClickListener((m, mapView) -> {
            selectedMarker = m;
            showMarkerInfo(m);
            return true;
        });

        mapView.getOverlays().add(marker);
        markers.add(marker);
        mapView.invalidate();
        saveRouteAndMarkers();
        updateMarkersCount();

        selectedMarker = marker;
        showMarkerInfo(marker);
    }

    private void updateMarkersCount() {
        runOnUiThread(() -> tvMarkersCount.setText(String.format("Маркеров: %d", markers.size())));
    }

    private void showMarkerOptionsDialog(Marker marker) {
        new AlertDialog.Builder(this)
                .setTitle("Выберите действие")
                .setItems(new String[]{"Редактировать", "Удалить"}, (dialog, which) -> {
                    if (which == 0) {
                        editMarker(marker);
                    } else if (which == 1) {
                        removeMarker(marker);
                    }
                })
                .show();
    }

    private void editMarker(Marker marker) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Редактировать маркер");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_marker, null);
        builder.setView(dialogView);

        EditText etMarkerTitle = dialogView.findViewById(R.id.etMarkerTitle);
        EditText etMarkerDescription = dialogView.findViewById(R.id.etMarkerDescription);

        etMarkerTitle.setText(marker.getTitle());
        etMarkerDescription.setText(marker.getSnippet());

        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            marker.setTitle(etMarkerTitle.getText().toString());
            marker.setSnippet(etMarkerDescription.getText().toString());
            mapView.invalidate();
            if (marker.equals(selectedMarker)) {
                showMarkerInfo(marker);
            }
            saveRouteAndMarkers();
        });

        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void removeMarker(Marker marker) {
        mapView.getOverlays().remove(marker);
        markers.remove(marker);
        mapView.invalidate();
        saveRouteAndMarkers();
        updateMarkersCount();
        resetMarkerSelection();
    }

    private void showClearRouteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Очистить маршрут")
                .setMessage("Вы уверены, что хотите удалить весь маршрут и все маркеры?")
                .setPositiveButton("Да", (dialog, which) -> clearRoute())
                .setNegativeButton("Нет", null)
                .show();
    }

    private void clearRoute() {
        geoPoints.clear();
        markers.clear();
        totalDistance = 0.0;
        distanceBetweenPoints = 0.0;
        mapView.getOverlays().clear();

        if (polyline != null) {
            mapView.getOverlayManager().remove(polyline);
            polyline = null;
        }

        if (currentLocationMarker != null) {
            mapView.getOverlays().add(currentLocationMarker);
        }

        runOnUiThread(() -> {
            tvDistance.setText("Пройдено: 0.00 м");
            tvDistanceBetweenPoints.setText("Расстояние между точками: 0.00 м");
            tvMarkersCount.setText("Маркеров: 0");
        });

        mapView.invalidate();
        saveRouteAndMarkers();
        resetMarkerSelection();
    }

    private void showAuthorInfoDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Информация о разработчике")
                .setMessage("ТЕМА 6. ГЕОЛОКАЦИЯ\n\n" +
                        "Лабораторная работа 20. Геолокационные возможности\n\n" +
                        "Приложение демонстрирует:\n" +
                        "1. Отображение карты с текущим местоположением\n" +
                        "2. Построение маршрута движения\n" +
                        "3. Фиксацию посещенных мест с возможностью добавления заметок\n\n" +
                        "Разработчик: Сологуб Анастасия, группа ПО-11")
                .setPositiveButton("OK", null)
                .show();
    }

    private void saveRouteAndMarkers() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        try {
            // Сохраняем маршрут
            StringBuilder routeBuilder = new StringBuilder();
            for (GeoPoint point : geoPoints) {
                routeBuilder.append(point.getLatitude())
                        .append(",")
                        .append(point.getLongitude())
                        .append(";");
            }
            editor.putString(ROUTE_KEY, routeBuilder.toString());

            // Сохраняем маркеры в более надежном формате
            StringBuilder markersBuilder = new StringBuilder();
            for (Marker marker : markers) {
                GeoPoint point = (GeoPoint) marker.getPosition();
                String title = marker.getTitle() != null ?
                        marker.getTitle().replace(";", "|").replace(",", "|") : "";
                String snippet = marker.getSnippet() != null ?
                        marker.getSnippet().replace(";", "|").replace(",", "|") : "";

                markersBuilder.append(point.getLatitude()).append(",")
                        .append(point.getLongitude()).append(",")
                        .append(title).append(",")
                        .append(snippet).append(";");
            }
            editor.putString(MARKERS_KEY, markersBuilder.toString());

            editor.apply();
            Log.d("SAVE", "Маркеры успешно сохранены: " + markers.size() + " шт.");
        } catch (Exception e) {
            Log.e("SAVE", "Ошибка при сохранении маркеров", e);
            Toast.makeText(this, "Ошибка при сохранении маркеров", Toast.LENGTH_SHORT).show();
        }
    }

    private void restoreRouteAndMarkers() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedRoute = preferences.getString(ROUTE_KEY, "");
        String savedMarkers = preferences.getString(MARKERS_KEY, "");

        // Очищаем текущие данные
        geoPoints.clear();
        markers.clear();
        mapView.getOverlays().clear();

        if (polyline != null) {
            mapView.getOverlayManager().remove(polyline);
            polyline = null;
        }

        try {
            // Восстанавливаем маршрут
            if (!savedRoute.isEmpty()) {
                String[] points = savedRoute.split(";");
                for (String point : points) {
                    if (!point.trim().isEmpty()) {
                        String[] latLng = point.split(",");
                        if (latLng.length == 2) {
                            try {
                                double latitude = Double.parseDouble(latLng[0]);
                                double longitude = Double.parseDouble(latLng[1]);
                                geoPoints.add(new GeoPoint(latitude, longitude));
                            } catch (NumberFormatException e) {
                                Log.e("RESTORE", "Ошибка парсинга точки маршрута", e);
                            }
                        }
                    }
                }
                updateRouteOnMap();
            }

            // Восстанавливаем маркеры
            if (!savedMarkers.isEmpty()) {
                String[] markerEntries = savedMarkers.split(";");
                for (String entry : markerEntries) {
                    if (!entry.trim().isEmpty()) {
                        String[] parts = entry.split(",");
                        if (parts.length >= 4) {
                            try {
                                double latitude = Double.parseDouble(parts[0]);
                                double longitude = Double.parseDouble(parts[1]);
                                String title = parts[2].replace("|", ";").replace("|", ",");
                                String snippet = parts[3].replace("|", ";").replace("|", ",");

                                GeoPoint point = new GeoPoint(latitude, longitude);
                                Marker marker = new Marker(mapView);
                                marker.setPosition(point);
                                marker.setTitle(title.isEmpty() ? "Фиксация " + (markers.size() + 1) : title);
                                marker.setSnippet(snippet.isEmpty() ?
                                        "Добавлено: " + new SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault()).format(new Date()) :
                                        snippet);
                                marker.setIcon(getResources().getDrawable(R.drawable.ic_marker));

                                marker.setOnMarkerClickListener((m, mapView) -> {
                                    selectedMarker = m;
                                    showMarkerInfo(m);
                                    return true;
                                });

                                mapView.getOverlays().add(marker);
                                markers.add(marker);
                            } catch (NumberFormatException e) {
                                Log.e("RESTORE", "Ошибка парсинга маркера", e);
                            }
                        }
                    }
                }
                updateMarkersCount();
                Log.d("RESTORE", "Маркеры успешно восстановлены: " + markers.size() + " шт.");
            }
        } catch (Exception e) {
            Log.e("RESTORE", "Ошибка при восстановлении маркеров", e);
            Toast.makeText(this, "Ошибка при восстановлении маркеров", Toast.LENGTH_SHORT).show();
        }

        // Восстанавливаем текущее местоположение если есть
        if (currentLocationMarker != null) {
            mapView.getOverlays().add(currentLocationMarker);
        }

        calculateTotalDistance();
        mapView.invalidate();
    }

    private void calculateTotalDistance() {
        totalDistance = 0.0;
        if (geoPoints.size() > 1) {
            for (int i = 1; i < geoPoints.size(); i++) {
                totalDistance += geoPoints.get(i-1).distanceToAsDouble(geoPoints.get(i));
            }
        }
        runOnUiThread(() -> {
            tvDistance.setText(String.format(Locale.getDefault(), "Пройдено: %.2f м", totalDistance));
            if (geoPoints.size() > 1) {
                distanceBetweenPoints = geoPoints.get(geoPoints.size()-2).distanceToAsDouble(geoPoints.get(geoPoints.size()-1));
                tvDistanceBetweenPoints.setText(String.format(Locale.getDefault(),
                        "Расстояние между точками: %.2f м", distanceBetweenPoints));
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        saveRouteAndMarkers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}