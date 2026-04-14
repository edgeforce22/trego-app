package com.example.tregoapp.mechanic.ui;

import static android.location.Location.distanceBetween;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tregoapp.BuildConfig;
import com.example.tregoapp.R;
import com.example.tregoapp.WelcomeFragment;
import com.example.tregoapp.customer.navigation.NavigationHelper;
import com.example.tregoapp.mechanic.ui.worker.WorkerDashboardFragment;
import com.example.tregoapp.mechanic.utils.DeviceLocationHelper;
import com.example.tregoapp.mechanic.utils.LoadFragment;
import com.example.tregoapp.mechanic.utils.LoaderManager;
import com.example.tregoapp.mechanic.viewmodel.ViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONObject;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import io.socket.client.IO;
import io.socket.client.Socket;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MechanicSideTrackingFragment extends Fragment {

    private static final String SERVICE_REQUEST_ID = "request_id";
    private String serviceRequestId;
    private String mechanicId;
    private String role;
    private String customerPhoneNumber;

    private MapView mapView;
    private TextView tvDuration;
    private TextView tvDistance;
    private TextView tvCustomerName;
    private TextView tvTotalPrice;
    private MaterialCardView callButton;
    private MaterialButton completeBtn;

    private double customerLat;
    private double customerLng;

    private double mechanicLat = 0;
    private double mechanicLng = 0;

    private Marker customerMarker;
    private Marker mechanicMarker;
    private Polyline routePolyline;

    private ViewModel viewModel;

    private Handler handler;
    private Runnable runnable;

    private Socket socket;

    private DeviceLocationHelper locationHelper;

    public MechanicSideTrackingFragment() {
        // Required empty public constructor
    }

    public static MechanicSideTrackingFragment newInstance(String serviceRequestId) {
        MechanicSideTrackingFragment fragment = new MechanicSideTrackingFragment();
        Bundle args = new Bundle();
        args.putString(SERVICE_REQUEST_ID, serviceRequestId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            serviceRequestId = getArguments().getString(SERVICE_REQUEST_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mechanic_side_tracking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tvDuration = view.findViewById(R.id.tvDuration);
        tvDistance = view.findViewById(R.id.tvDistance);
        tvCustomerName = view.findViewById(R.id.tvCustomerName);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        mapView = view.findViewById(R.id.map);
        callButton = view.findViewById(R.id.callButton);
        completeBtn = view.findViewById(R.id.completeBtn);

        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
        LoaderManager.show(this);
        viewModel.loadSavedUser();
        viewModel.getAcceptedServiceRequest(serviceRequestId);
        viewModelObserver();

//        startPolling();
        initSocket();

        locationHelper = new DeviceLocationHelper(requireContext());

        locationHelper.startLocationUpdates(requireContext(), (lat, lon, address) -> {

            // 🔥 Call your existing method
            onLocationChanged(lat, lon);

        });

        callButton.setOnClickListener(v -> {

            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.CALL_PHONE}, 1);
                return;
            }

            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + customerPhoneNumber));
            startActivity(intent);
        });

        completeBtn.setOnClickListener(v -> {
            LoaderManager.show(this);
            viewModel.completeServiceRequest(serviceRequestId);
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        showExitPopup();
                    }
                }
        );
    }

    private void showExitPopup() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit to dashboard?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    navigateTo();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void navigateTo() {
        if (!isAdded()) return;
        
        String currentRole = (role != null) ? role : viewModel.getRole();
        Fragment dashboardFragment;
        
        if ("worker".equalsIgnoreCase(currentRole)) {
            dashboardFragment = new WorkerDashboardFragment();
        } else {
            dashboardFragment = new DashboardFragment();
        }
        
        NavigationHelper.clearBackStackAndNavigate(getParentFragmentManager(), dashboardFragment);
    }

    private void viewModelObserver() {
        viewModel.getAuthState().observe(getViewLifecycleOwner(), authState -> {
            if (authState == null) return;
            LoaderManager.hide(this);
            if (!authState.getSuccess()) {
                Toast.makeText(requireContext(), authState.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getShopServiceRequestsLiveData().observe(getViewLifecycleOwner(), service -> {
            if (service == null) {
                return;
            }
        });

        viewModel.getAcceptServiceRequestLiveData().observe(getViewLifecycleOwner(), serviceRequest -> {
            if (serviceRequest == null) return;
            LoaderManager.hide(this);

            if ("completed".equalsIgnoreCase(serviceRequest.getStatus())) {
                navigateTo();
            }
        });

        viewModel.getAcceptedServiceRequestLiveData().observe(getViewLifecycleOwner(), serviceRequest -> {
            LoaderManager.hide(this);
            if (serviceRequest == null) {
                return;
            }

            if ("completed".equalsIgnoreCase(serviceRequest.getStatus())) {
                navigateTo();
            }

            tvTotalPrice.setText(String.format(Locale.getDefault(), "₹ %.2f", serviceRequest.getTotalPrice()));

            if (serviceRequest.getCustomerId() != null) {
                viewModel.fetchCustomerDetails(serviceRequest.getCustomerId());
            }

            if(serviceRequest.getCustomerLocation() == null) {
                return;
            }

            customerLat = serviceRequest.getCustomerLocation().getLatitude();
            customerLng = serviceRequest.getCustomerLocation().getLongitude();

            if(serviceRequest.getMechanicLocation() == null) {
                tvDuration.setText("Waiting for mechanic...");
                tvDistance.setText("--");
                return;
            }

            mechanicLat = serviceRequest.getMechanicLocation().getLatitude();
            mechanicLng = serviceRequest.getMechanicLocation().getLongitude();

            String start = customerLng +  "," + customerLat;
            String end = mechanicLng +  "," + mechanicLat;

            viewModel.getRoute(start, end);
            if(customerMarker == null && mechanicMarker == null){
                mapViewSetUp();
            }
        });
        viewModel.getRouteLiveData().observe(getViewLifecycleOwner(), route -> {
            if (route == null) {
                return;
            }
            if(route.getGeometry() == null) return;

            drawRoute(route.getGeometry().getCoordinates());
            calculateDistanceMinutes(route.getDistance(), route.getDuration());
        });

        viewModel.getCustomerDetailsLiveData().observe(getViewLifecycleOwner(), customer -> {
            if (customer != null) {
                tvCustomerName.setText(customer.getName());
                customerPhoneNumber = customer.getPhoneNumber();
            }
        });

        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), currentUser -> {
            if (currentUser == null) {
                return;
            }
            role = currentUser.getRole();
            mechanicId = currentUser.getId();
        });
    }


    private void mapViewSetUp() {
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        GeoPoint customerPoint = new GeoPoint(customerLat, customerLng);
        GeoPoint mechanicPoint = new GeoPoint(mechanicLat, mechanicLng);

        mapView.getZoomController().setVisibility(
                org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER
        );
        mapView.setMinZoomLevel(5.0);
        mapView.setMaxZoomLevel(20.0);
        mapView.post(() -> zoomToFitRoute(customerPoint, mechanicPoint));

        addCustomerMarker(customerPoint);
        addMechanicMarker(mechanicPoint);
    }

    private void addCustomerMarker(GeoPoint point) {
        if(customerMarker == null){
            customerMarker = new Marker(mapView);
            mapView.getOverlays().add(customerMarker);
        }

        customerMarker.setPosition(point);
        customerMarker.setTitle("Customer Location");

        Drawable icon = requireContext().getDrawable(R.drawable.customer_marker);
        Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();

        Bitmap smallmarker = Bitmap.createScaledBitmap(bitmap, 40, 40, false);

        customerMarker.setIcon(new BitmapDrawable(getResources(), smallmarker));
        customerMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
    }

    private void addMechanicMarker(GeoPoint point) {
        if(mechanicMarker == null){
            mechanicMarker = new Marker(mapView);
            mapView.getOverlays().add(mechanicMarker);
        }

        mechanicMarker.setPosition(point);
        mechanicMarker.setTitle("Mechanic Location");

        Drawable icon = requireContext().getDrawable(R.drawable.mechanic_marker);
        Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();

        Bitmap smallmarker = Bitmap.createScaledBitmap(bitmap, 40, 40, false);

        mechanicMarker.setIcon(new BitmapDrawable(getResources(), smallmarker));
        mechanicMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
    }

    private void drawRoute(List<List<Double>> coords) {

        if(routePolyline != null){
            mapView.getOverlayManager().remove(routePolyline);
        }

        routePolyline = new Polyline();
        List<GeoPoint> points = new ArrayList<>();

        for (int i = 0; i < coords.size(); i ++) {
            List<Double> coord = coords.get(i);
            points.add(new GeoPoint(coord.get(1), coord.get(0)));
        }

        routePolyline.setPoints(points);
        routePolyline.setWidth(8f);

        mapView.getOverlayManager().add(routePolyline);
        mapView.invalidate();

        if(!points.isEmpty()){
            mapView.post(() -> {
                mapView.zoomToBoundingBox(
                        org.osmdroid.util.BoundingBox.fromGeoPoints(points),
                        true,
                        150
                );
            });
        }
    }

    private void calculateDistanceMinutes(double distance, double duration) {

        if (distance <= 0 || duration <= 0) {
            tvDuration.setText("Arrived");
            tvDistance.setText("You reached the customer's location.");
            return;
        }

        double etaMin = duration / 60.0;
        double distanceKm = distance / 1000.0;

        tvDuration.setText(String.format(Locale.getDefault(), "%.0f mins", etaMin));
        tvDistance.setText(String.format(Locale.getDefault(), "You are " + "%.2f km" + " away from the customer's location.", distanceKm));
    }

    private void zoomToFitRoute(GeoPoint start, GeoPoint end) {

        double north = Math.max(start.getLatitude(), end.getLatitude());
        double south = Math.min(start.getLatitude(), end.getLatitude());
        double east = Math.max(start.getLongitude(), end.getLongitude());
        double west = Math.min(start.getLongitude(), end.getLongitude());

        org.osmdroid.util.BoundingBox boundingBox =
                new org.osmdroid.util.BoundingBox(north, east, south, west);

        mapView.zoomToBoundingBox(boundingBox, true, 150);
    }

//    private void startPolling() {
//        if (handler != null) return;
//
//        handler = new Handler(Looper.getMainLooper());
//
//        runnable = new Runnable() {
//            @Override
//            public void run() {
//
//                if (serviceRequestId != null) {
//                    viewModel.getAcceptedServiceRequest(serviceRequestId);
//                }
//
//                handler.postDelayed(this, 5000);
//            }
//        };
//
//        handler.post(runnable);
//    }
//
//    private void stopPolling() {
//        if (handler != null && runnable != null) {
//            handler.removeCallbacks(runnable);
//            handler = null;
//        }
//    }

    private void initSocket() {
        try {
            socket = IO.socket(BuildConfig.BASE_URL_ENDPOINT);

            socket.connect();

            socket.on(Socket.EVENT_CONNECT, args -> {
                socket.emit("join_order", serviceRequestId);
            });

            socket.io().reconnection(true);
            socket.io().reconnectionAttempts(5);
            socket.io().reconnectionDelay(2000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendLocationToServer(double lat, double lng) {
        try {
            JSONObject data = new JSONObject();
            data.put("orderId", serviceRequestId);
            data.put("mechanicId", mechanicId);
            data.put("latitude", lat);
            data.put("longitude", lng);

            if (socket != null && socket.connected()) {
                socket.emit("mechanic_location_update", data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onLocationChanged(double lat, double lng) {
        float[] results = new float[1];
        distanceBetween(mechanicLat, mechanicLng, lat, lng, results);

        if (results[0] < 10 && mechanicLat != 0) return; // distance in meters

        mechanicLat = lat;
        mechanicLng = lng;

        // Update UI marker
        GeoPoint point = new GeoPoint(lat, lng);

        if (mechanicMarker == null) {
            addMechanicMarker(point);
        } else {
            mechanicMarker.setPosition(point);
        }

        mapView.invalidate();

        // Optional: Recalculate route if moved significantly (e.g., > 50m)
        if (results[0] > 50 && customerLat != 0) {
            String start = customerLng + "," + customerLat;
            String end = mechanicLng + "," + mechanicLat;
            viewModel.getRoute(start, end);
        }

        // 🔥 SEND TO SERVER
        sendLocationToServer(lat, lng);
    }

}