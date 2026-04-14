package com.example.tregoapp.customer.ui;

import android.Manifest;
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

import com.example.tregoapp.R;
import com.example.tregoapp.BuildConfig;
import com.example.tregoapp.customer.navigation.NavigationHelper;
import com.example.tregoapp.customer.viewmodel.ViewModel;
import com.example.tregoapp.customer.utils.LoaderManager;
import com.example.tregoapp.customer.model.ServiceRequest;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONObject;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.socket.client.IO;
import io.socket.client.Socket;

public class CustomerSideTrackingFragment extends Fragment {

    private static final String REQUEST_ID = "request_id";
    private String requestId;
    private String phoneToCall;

    private MapView mapView;
    private TextView tvDuration;
    private TextView tvDistance;
    private TextView tvShopName;
    private TextView tvTotalPrice;
    private MaterialCardView callButton;

    private double customerLat;
    private double customerLng;

    private double mechanicLat;
    private double mechanicLng;

    private Marker customerMarker;
    private Marker mechanicMarker;
    private Polyline routePolyline;

    private ViewModel viewModel;
    private boolean isDialogShown = false;

    private Handler handler;
    private Runnable runnable;

    private Socket socket;


    public CustomerSideTrackingFragment() {
        // Required empty public constructor
    }

    public static CustomerSideTrackingFragment newInstance(String requestId) {
        CustomerSideTrackingFragment fragment = new CustomerSideTrackingFragment();
        Bundle args = new Bundle();
        args.putString(REQUEST_ID, requestId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            requestId = getArguments().getString(REQUEST_ID);
        }

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavigationHelper.clearBackStackAndNavigate(getParentFragmentManager(), new DashboardFragment());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_side_tracking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tvDuration = view.findViewById(R.id.tvDuration);
        tvDistance = view.findViewById(R.id.tvDistance);
        tvShopName = view.findViewById(R.id.tvShopName);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        mapView = view.findViewById(R.id.map);
        callButton = view.findViewById(R.id.callButton);

        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        LoaderManager.show(this);
        viewModel.getServiceRequest(requestId);
        viewModelObserver();

//        startPolling();
        initSocket();

        callButton.setOnClickListener(v -> {

            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.CALL_PHONE}, 1);
                return;
            }

            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phoneToCall));
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mapView.onDetach();
    }

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
////        stopPolling();
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (socket != null) {
            socket.disconnect();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Permission granted → retry call
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + phoneToCall));
                startActivity(intent);

            } else {
                Toast.makeText(requireContext(),
                        "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void viewModelObserver() {
        viewModel.getServiceRequestResource().observe(getViewLifecycleOwner(), resource -> {
            LoaderManager.handleResource(this, resource, serviceRequest -> {
                if (serviceRequest == null) return;

                if ("waiting_for_confirmation".equalsIgnoreCase(serviceRequest.getStatus())) {
                    if (!isDialogShown) {
                        isDialogShown = true;
                        ServiceCompletedFragment dialog =
                                ServiceCompletedFragment.newInstance(requestId);
                        dialog.setCancelable(false);
                        dialog.show(getParentFragmentManager(), "ServiceCompletedDialog");
                    }
                }

                if (serviceRequest.getShopName() != null) {
                    tvShopName.setText(serviceRequest.getShopName());
                } else if (serviceRequest.getShopId() != null) {
                    viewModel.fetchShopDetails(serviceRequest.getShopId());
                }

                tvTotalPrice.setText(String.format(Locale.getDefault(), "₹ %.2f", serviceRequest.getTotalPrice()));

                if(serviceRequest.getCustomerLocation() == null) return;

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
        });

        viewModel.getRouteResource().observe(getViewLifecycleOwner(), resource -> {
            LoaderManager.handleResource(this, resource, route -> {
                if (route == null || route.getGeometry() == null) return;
                drawRoute(route.getGeometry().getCoordinates());
                calculateDistanceMinutes(route.getDistance(), route.getDuration());
            });
        });

        viewModel.getShopDetailsLiveData().observe(getViewLifecycleOwner(), resource -> {
            LoaderManager.handleResource(this, resource, shop -> {
                if (shop != null) {
                    tvShopName.setText(shop.getShopName());
                    phoneToCall = shop.getPhoneNumber();
                }
            });
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
            tvDistance.setText("Mechanic is arrived in your location.");
            return;
        }

        double etaMin = duration / 60.0;
        double distanceKm = distance / 1000.0;

        tvDuration.setText(String.format(Locale.getDefault(), "%.0f mins", etaMin));
        tvDistance.setText(String.format(Locale.getDefault(), "Mechanic is " + "%.2f km" + " away from you. He/She will be arrived within a min.", distanceKm));
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
//                if (requestId != null) {
//                    viewModel.getServiceRequest(requestId); // 🔥 refresh API
//                }
//
//                handler.postDelayed(this, 5000); // every 5 seconds
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

            socket.io().reconnection(true);
            socket.io().reconnectionAttempts(5);
            socket.io().reconnectionDelay(2000);

            socket.on(Socket.EVENT_CONNECT, args -> {
                socket.emit("join_order", requestId);
            });

            socket.on("location_update", args -> {
                JSONObject data = (JSONObject) args[0];

                double lat = data.optDouble("latitude");
                double lng = data.optDouble("longitude");

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        updateMechanicLocation(lat, lng);
                    });
                }
            });

            socket.on("order_status_update", args -> {

                JSONObject data = (JSONObject) args[0];
                String status = data.optString("status");

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {

                        if ("waiting_for_confirmation".equalsIgnoreCase(status)) {

                            if (!isDialogShown) {
                                isDialogShown = true;

                                ServiceCompletedFragment dialog =
                                        ServiceCompletedFragment.newInstance(requestId);

                                dialog.setCancelable(false);
                                dialog.show(getParentFragmentManager(), "ServiceCompletedDialog");
                            }
                        }

                        if ("completed".equalsIgnoreCase(status)) {
                            Toast.makeText(getContext(), "Service Completed ✅", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateMechanicLocation(double lat, double lng) {

        if (customerLat == 0 || customerLng == 0) return;

        mechanicLat = lat;
        mechanicLng = lng;

        GeoPoint point = new GeoPoint(lat, lng);

        if (mechanicMarker == null) {
            addMechanicMarker(point);
        } else {
            mechanicMarker.setPosition(point);
        }

        mapView.invalidate();

        float[] results = new float[1];
        android.location.Location.distanceBetween(
                customerLat, customerLng,
                lat, lng,
                results
        );

        if (results[0] > 30) {
            String start = customerLng + "," + customerLat;
            String end = mechanicLng + "," + mechanicLat;

            viewModel.getRoute(start, end);
        } else if (results[0] <= 10) {
            tvDuration.setText("Arrived");
            tvDistance.setText("Mechanic has arrived at your location.");
        }
    }
}