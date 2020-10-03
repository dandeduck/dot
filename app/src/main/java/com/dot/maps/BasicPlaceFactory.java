package com.dot.maps;

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

    public BasicPlaceFactory(Activity activity, String apiKey) {
        super(activity.getApplicationContext(), apiKey, MAX_RESULTS, SEARCH_RADIUS);

        currentLocation = new AtomicReference<>();
        setupLocationManager(activity);
    }

    public void request(String userInput, Callback<List<Place>> callback) {
        super.request(currentLocation.get(), userInput, callback);
    }

    private void setupLocationManager(Activity activity) {
        try {
            if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        ((LocationManager)activity.getSystemService(Context.LOCATION_SERVICE)).requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, location -> currentLocation.set(new LatLng(location.getLatitude(), location.getLongitude())));
    }
}
