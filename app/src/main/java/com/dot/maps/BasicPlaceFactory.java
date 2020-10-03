package com.dot.maps;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.tomtom.online.sdk.common.location.LatLng;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class BasicPlaceFactory extends PlaceFactory {
    private static final int MAX_RESULTS = 20;
    private static final double SEARCH_RADIUS = 50000;
    private final AtomicReference<LatLng> currentLocation;
    private final Activity activity;

    public BasicPlaceFactory(Activity activity, String apiKey) {
        super(activity.getApplicationContext(), apiKey, MAX_RESULTS, SEARCH_RADIUS);

        this.activity = activity;
        currentLocation = new AtomicReference<>();

        setupLocationManager();
    }

    public void request(String userInput, Callback<List<Place>> callback) {
        if (missingPermission())
            setupLocationManager();
        else
            super.request(currentLocation.get(), userInput, callback);
    }

    @SuppressLint("MissingPermission")
    private void setupLocationManager() {
        if (missingPermission())
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        else
            ((LocationManager)activity.getSystemService(Context.LOCATION_SERVICE)).requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, location -> currentLocation.set(new LatLng(location.getLatitude(), location.getLongitude())));
    }

    private boolean missingPermission() {
        return ContextCompat.checkSelfPermission(activity.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
    }
}
