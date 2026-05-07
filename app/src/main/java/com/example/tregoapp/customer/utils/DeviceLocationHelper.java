package com.example.tregoapp.customer.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.tregoapp.customer.dialog.EnableLocationDialogFragment;
import com.google.android.gms.location.*;
import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.Priority;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class DeviceLocationHelper {

    private final FusedLocationProviderClient fusedLocationClient;

    public interface LocationListener {
        void onLocation(double latitude, double longitude, String address);
    }

    public DeviceLocationHelper(Context context) {
        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(context);
    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation(Context context, LocationListener listener) {

        /*
         * CHECK LOCATION ENABLED
         */
        if (!isLocationEnabled(context)) {
            if (context instanceof FragmentActivity) {
                EnableLocationDialogFragment
                        .newInstance()
                        .show(
                                ((FragmentActivity) context)
                                        .getSupportFragmentManager(),
                                "EnableLocation"
                        );
            }

            listener.onLocation(
                    0.0,
                    0.0,
                    "Location disabled"
            );
            return;
        }

        // Use CurrentLocationRequest for a fresh, high-accuracy fix (not stale cache)
        CurrentLocationRequest request = new CurrentLocationRequest.Builder()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setMaxUpdateAgeMillis(10000) // Ensure location is no older than 10 seconds
                .build();

        fusedLocationClient.getCurrentLocation(request, null)
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        double lat = location.getLatitude();
                        double lng = location.getLongitude();
                        String address = getAddressFromLatLng(context, lat, lng);
                        listener.onLocation(lat, lng, address);
                    } else {
                        // Fallback to last known if fresh fix is unavailable
                        fusedLocationClient.getLastLocation().addOnSuccessListener(lastLoc -> {
                            if (lastLoc != null) {
                                double lat = lastLoc.getLatitude();
                                double lng = lastLoc.getLongitude();
                                String address = getAddressFromLatLng(context, lat, lng);
                                listener.onLocation(lat, lng, address);
                            } else {
                                listener.onLocation(0.0, 0.0, "Location unavailable");
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    listener.onLocation(0.0, 0.0, "Error: " + e.getMessage());
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

    private LocationCallback locationCallback;

    public void stopLocationUpdates() {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    public static boolean isLocationEnabled(Context context) {

        android.location.LocationManager locationManager =
                (android.location.LocationManager)
                        context.getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(
                android.location.LocationManager.GPS_PROVIDER
        ) || locationManager.isProviderEnabled(
                android.location.LocationManager.NETWORK_PROVIDER
        );
    }
}
