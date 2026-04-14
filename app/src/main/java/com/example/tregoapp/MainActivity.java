package com.example.tregoapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.tregoapp.customer.ui.CustomerSideTrackingFragment;
import com.example.tregoapp.customer.utils.NetworkLocationHelper;
import com.example.tregoapp.mechanic.ui.AcceptanceFragment;
import com.example.tregoapp.mechanic.ui.DashboardFragment;
import com.example.tregoapp.mechanic.ui.worker.WorkerDashboardFragment;
import com.example.tregoapp.mechanic.viewmodel.ViewModel;
import com.google.firebase.messaging.FirebaseMessaging;

import org.osmdroid.config.Configuration;

public class MainActivity extends AppCompatActivity {

    private com.example.tregoapp.customer.manager.SessionManager cSessionManager;
    private com.example.tregoapp.mechanic.manager.SessionManager mSessionManager;

    private com.example.tregoapp.customer.viewmodel.ViewModel viewModel;
    private ViewModel viewModel2;

    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private BroadcastReceiver locationReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cSessionManager = new com.example.tregoapp.customer.manager.SessionManager(getApplicationContext());
        mSessionManager = new com.example.tregoapp.mechanic.manager.SessionManager(getApplicationContext());

        setUp();

        locationRequest();
        OSMDroidSetup();

        requestNotificationPermission();
        notificationSetup();

        // Initial checks
        checkInitialStatus();

        // Register listeners
        registerNetworkCallback();
        registerLocationReceiver();

        Log.d("SESSION_DATA", mSessionManager.toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerNetworkCallback();
        registerLocationReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (connectivityManager != null && networkCallback != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }

        if (locationReceiver != null) {
            unregisterReceiver(locationReceiver);
        }
    }

    private void setUp() {
        if (cSessionManager.isLoggedIn()) {
            viewModel = new ViewModelProvider(this).get(com.example.tregoapp.customer.viewmodel.ViewModel.class);

            FirebaseMessaging.getInstance().getToken()
                    .addOnSuccessListener(token -> {

                        viewModel.updateCustomerFcmToken(viewModel.getUserId(), token, null);

                    });

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new com.example.tregoapp.customer.ui.DashboardFragment())
                    .commit();
            return;
        }

        if (mSessionManager.isLoggedIn()) {

            viewModel2 = new ViewModelProvider(this).get(ViewModel.class);

            FirebaseMessaging.getInstance().getToken()
                    .addOnSuccessListener(token -> {
                        viewModel2.updateMechanicFcmToken(viewModel2.getUserId(), token, null);
                    });

            String role = mSessionManager.getUserRole();

            if ("owner".equalsIgnoreCase(role)) {
                Log.d("MainActivity", "Navigating to Owner Dashboard");
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new DashboardFragment())
                        .commit();
            }
            else if ("worker".equalsIgnoreCase(role)) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new WorkerDashboardFragment())
                        .commit();
            }

            return;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new WelcomeFragment())
                .commit();
    }

    private void locationRequest() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100
            );
            return;
        }

    }

    private void OSMDroidSetup() {
        Configuration.getInstance().load(this,
                getSharedPreferences("osmdroid", MODE_PRIVATE));
    }

    private void requestNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        101
                );
            }
        }
    }

    private void notificationSetup() {

        String serviceId = getIntent().getStringExtra("serviceId");
        String requestStatus = getIntent().getStringExtra("requestStatus");

        if(serviceId != null) {

            // If mechanic logged in
            if(mSessionManager.isLoggedIn()) {

//                ServiceRequestedFragment fragment =
//                        ServiceRequestedFragment.newInstance(viewModel2.getShopId());

                if ("owner".equalsIgnoreCase(mSessionManager.getUserRole())) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new DashboardFragment())
                            .commit();
                }
                else if ("worker".equalsIgnoreCase(mSessionManager.getUserRole())) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new WorkerDashboardFragment())
                            .commit();
                }
            }

            // If customer logged in
            else if(cSessionManager.isLoggedIn()) {

                if (requestStatus != null) {

                    if ("accepted".equalsIgnoreCase(requestStatus)) {
                        CustomerSideTrackingFragment fragment =
                                CustomerSideTrackingFragment.newInstance(serviceId);

                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .commit();
                    }
                    else {
                        com.example.tregoapp.customer.ui.DashboardFragment fragment = new
                                com.example.tregoapp.customer.ui.DashboardFragment();

                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .commit();
                    }
                }
            }
        }
    }

    // -------------------------------
    // ✅ Initial Check
    // -------------------------------
    private void checkInitialStatus() {

        if (!NetworkLocationHelper.isInternetAvailable(this)) {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

        if (!NetworkLocationHelper.isLocationEnabled(this)) {
            Toast.makeText(this, "Location is OFF", Toast.LENGTH_SHORT).show();
        }
    }

    // -------------------------------
    // 🌐 Network Listener
    // -------------------------------
    private void registerNetworkCallback() {

        connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "Internet Connected", Toast.LENGTH_SHORT).show()
                );
                Log.d("NETWORK", "Connected");
            }

            @Override
            public void onLost(Network network) {
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "Internet Lost", Toast.LENGTH_SHORT).show()
                );
                Log.d("NETWORK", "Disconnected");
            }
        };

        connectivityManager.registerDefaultNetworkCallback(networkCallback);
    }

    // -------------------------------
    // 📍 Location Listener
    // -------------------------------
    private void registerLocationReceiver() {

        locationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (NetworkLocationHelper.isLocationEnabled(context)) {
                    Toast.makeText(context, "Location ON", Toast.LENGTH_SHORT).show();
                    Log.d("LOCATION", "ON");
                } else {
                    Toast.makeText(context, "Location OFF", Toast.LENGTH_SHORT).show();
                    Log.d("LOCATION", "OFF");
                }
            }
        };

        registerReceiver(
                locationReceiver,
                new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        );
    }
}