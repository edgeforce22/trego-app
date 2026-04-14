package com.example.tregoapp.mechanic.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;

import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class DeviceLocationHelper {

    private final FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    public interface LocationListener {
        void onLocation(double latitude, double longitude, String address);
    }

    public DeviceLocationHelper(Context context) {
        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(context);
    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation(Context context, LocationListener listener) {
        // Request a FRESH location fix instead of relying on potentially stale cached data
        CurrentLocationRequest locationRequest = new CurrentLocationRequest.Builder()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setMaxUpdateAgeMillis(10000) // Location must be no older than 10 seconds
                .build();

        fusedLocationClient.getCurrentLocation(locationRequest, null)
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        double lat = location.getLatitude();
                        double lng = location.getLongitude();
                        String address = getAddressFromLatLng(context, lat, lng);
                        listener.onLocation(lat, lng, address);
                    } else {
                        // Fallback to last known ONLY if fresh location fails
                        fusedLocationClient.getLastLocation().addOnSuccessListener(lastLoc -> {
                            if (lastLoc != null) {
                                double lat = lastLoc.getLatitude();
                                double lng = lastLoc.getLongitude();
                                String address = getAddressFromLatLng(context, lat, lng);
                                listener.onLocation(lat, lng, address);
                            } else {
                                listener.onLocation(0.0, 0.0, "Location not available");
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    listener.onLocation(0.0, 0.0, "Location error: " + e.getMessage());
                });
    }

    private String getAddressFromLatLng(Context context,
                                        double lat,
                                        double lng) {

        Geocoder geocoder =
                new Geocoder(context, Locale.getDefault());

        try {
            List<Address> addresses =
                    geocoder.getFromLocation(lat, lng, 1);

            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Address not found";
    }

    @SuppressLint("MissingPermission")
    public void startLocationUpdates(Context context, LocationListener listener) {

        LocationRequest request =
                new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000) // every 3 sec
                        .setMinUpdateDistanceMeters(10) // only if moved 10m
                        .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult result) {
                if (result == null) return;

                Location location = result.getLastLocation();

                if (location != null) {

                    double lat = location.getLatitude();
                    double lng = location.getLongitude();

                    String address = getAddressFromLatLng(context, lat, lng);

                    listener.onLocation(lat, lng, address);
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
        );
    }

    public void stopLocationUpdates() {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}
