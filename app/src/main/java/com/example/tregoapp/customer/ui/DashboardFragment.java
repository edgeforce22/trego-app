package com.example.tregoapp.customer.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tregoapp.R;
import com.example.tregoapp.customer.adapter.ShopListAdapter;
import com.example.tregoapp.customer.listener.OnItemClickListener;
import com.example.tregoapp.customer.model.ShopDetail;
import com.example.tregoapp.customer.utils.DeviceLocationHelper;
import com.example.tregoapp.customer.utils.EmptyStateHelper;
import com.example.tregoapp.customer.utils.LoaderManager;
import com.example.tregoapp.customer.utils.Utility;
import com.example.tregoapp.customer.viewmodel.ViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import com.example.tregoapp.customer.navigation.NavigationHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import org.json.JSONObject;

public class DashboardFragment extends Fragment implements OnItemClickListener {

    private View root;

    private String customerId;

    private MapView mapView;
    private View card;
    private MaterialCardView profileBtn;
    private Marker customerMarker;
    private TextView tvLiveAddress;
    private FloatingActionButton btnFocusLocation;
    private TextView tvDashboardName;
    private MaterialCardView goToNearbyShops;
    private MaterialCardView goToSOS;
    private RecyclerView shopsRecyclerView;

    private MaterialCardView liveRequestCard;
    private TextView tvLiveReqStatus;
    private TextView tvLiveReqShopName;
    private TextView tvLiveReqService;
    private TextView tvLiveReqTime;

    private TextView tvMechanicName;
    private TextView tvServiceDescription;
    private TextView tvProblemDescription;
    private TextView tvLiveReqPrice;
    private View btnCancelRequest;
    private View btnTrack;

    private double latitude;
    private double longitude;
    private String address;

    private ViewModel viewModel;
    private ShopListAdapter adapter;

    private DeviceLocationHelper locationHelper;
    private Socket socket;

    private static final int LOCATION_PERMISSION_REQUEST = 100;

    public DashboardFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customer_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        LoaderManager.show(this);
        root = view;
        mapView = view.findViewById(R.id.map);
        tvLiveAddress = view.findViewById(R.id.tvLiveAddress);
        btnFocusLocation = view.findViewById(R.id.btnFocusLocation);
        profileBtn = view.findViewById(R.id.profileBtn);
        tvDashboardName = view.findViewById(R.id.tvDashboardName);
        goToNearbyShops = view.findViewById(R.id.goToNearbyShops);
        goToSOS = view.findViewById(R.id.goToSOS);
        shopsRecyclerView = view.findViewById(R.id.shopsRecyclerView);
        tvMechanicName = view.findViewById(R.id.tvMechanicName);
        tvServiceDescription = view.findViewById(R.id.tvServiceDescription);
        tvProblemDescription = view.findViewById(R.id.tvProblemDescription);
        tvLiveReqPrice = view.findViewById(R.id.tvLiveReqPrice);
        card = view.findViewById(R.id.cardLiveAddress);

        liveRequestCard = view.findViewById(R.id.liveRequestCard);
        tvLiveReqStatus = view.findViewById(R.id.tvLiveReqStatus);
        tvLiveReqShopName = view.findViewById(R.id.tvLiveReqShopName);
        tvLiveReqService = view.findViewById(R.id.tvLiveReqService);
        tvLiveReqTime = view.findViewById(R.id.tvLiveReqTime);
        btnCancelRequest = view.findViewById(R.id.btnCancelRequest);
        btnTrack = view.findViewById(R.id.btnTrack);

        adapter = new ShopListAdapter(this);
        shopsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        shopsRecyclerView.setAdapter(adapter);

        locationHelper = new DeviceLocationHelper(requireContext());

        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        viewModel.loadSavedUser();
        viewModelObserver();
        viewModel.getLiveRequestedRequest(customerId);

        setupMap();
        checkPermissionAndFetchLocation();

        goToNearbyShops.setOnClickListener(v -> {
            NavigationHelper.navigateTo(getParentFragmentManager(), new NearbyShopsFragment());
        });

        goToSOS.setOnClickListener(v -> {
            NavigationHelper.navigateTo(getParentFragmentManager(), new SOSOptionsFragment());
        });

        profileBtn.setOnClickListener(v -> {
            NavigationHelper.navigateTo(getParentFragmentManager(), new ProfileFragment());
        });

        btnTrack.setOnClickListener(v -> {
            Object tag = btnTrack.getTag();
            if (tag instanceof String) {
                NavigationHelper.navigateTo(getParentFragmentManager(), CustomerSideTrackingFragment.newInstance((String) tag));
            }
        });

        btnCancelRequest.setOnClickListener(v -> {
            Object tag = btnCancelRequest.getTag();
            viewModel.cancelRequestedService(String.valueOf(tag));
            Toast.makeText(requireContext(), "Request Cancelled", Toast.LENGTH_SHORT).show();
            viewModel.getLiveRequestedRequest(customerId);
        });

        mapMoveListener();
        locationFocusListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

        viewModel.getLiveRequestedRequest(customerId);
        if (latitude != 0 && longitude != 0) {
            mapView.post(() -> showLocationOnMap(latitude, longitude));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();

        viewModel.getLiveRequestedRequest(customerId);
        if (locationHelper != null) {
            locationHelper.stopLocationUpdates();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mapView.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDetach();
        if (socket != null) {
            socket.disconnect();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                fetchLocation();
            }
        }
    }

    @Override
    public void onClick(ShopDetail shopDetail) {
        openShopRequest(shopDetail);
    }

    @Override
    public void onClickShop(ShopDetail shopDetail) {
    }

    private void setupMap() {
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        mapView.getZoomController().setVisibility(
                org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER
        );

        mapView.setMinZoomLevel(5.0);
        mapView.setMaxZoomLevel(20.0);
    }

    private void checkPermissionAndFetchLocation() {

        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST
            );
            return;
        }

        LoaderManager.hide(this);
        fetchLocation();
    }

    private void fetchLocation() {
        locationHelper.getCurrentLocation(requireContext(), (lat, lng, address) -> {

            this.latitude = lat;
            this.longitude = lng;
            this.address = address;

            // API CALL
            if (lat != 0 && lng != 0) {
                viewModel.getNearbyShops(latitude, longitude);
            }

            // Ensure map is ready before updating
            mapView.post(() -> {
                showLocationOnMap(lat, lng);

                // Update the floating card text
                if (address != null && !address.isEmpty()) {
                    tvLiveAddress.setText(address);
                } else {
                    tvLiveAddress.setText("Address not found");
                }
            });
        });
    }

    private void showLocationOnMap(double lat, double lng) {

        GeoPoint point = new GeoPoint(lat, lng);

        if (customerMarker == null) {
            customerMarker = new Marker(mapView);
        }

        mapView.getOverlays().clear();
        mapView.getOverlays().add(customerMarker);

        customerMarker.setPosition(point);
        customerMarker.setTitle("You are here");

        // XML Vector Drawable Fix
        Drawable icon = ContextCompat.getDrawable(requireContext(), R.drawable.pointer);

        Bitmap bitmap = Bitmap.createBitmap(
                icon.getIntrinsicWidth(),
                icon.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888
        );

        Canvas canvas = new Canvas(bitmap);
        icon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        icon.draw(canvas);

        Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, 125, 125, false);

        customerMarker.setIcon(new BitmapDrawable(getResources(), smallMarker));
        customerMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        mapView.getController().animateTo(point);
        mapView.getController().setZoom(16.0);

        mapView.invalidate();
    }

    private void openShopRequest(ShopDetail shopDetail) {
        CreateRequestFragment createRequestFragment = CreateRequestFragment.newInstance(shopDetail);

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, createRequestFragment)
                .addToBackStack(null)
                .commit();
    }

    private void mapMoveListener() {
        mapView.setOnTouchListener((v, event) -> {

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }

            return false; // allow map gestures
        });
    }

    private void locationFocusListener() {

        btnFocusLocation.setOnClickListener(v -> {

            if (latitude != 0 && longitude != 0) {

                GeoPoint point = new GeoPoint(latitude, longitude);

                mapView.getController().animateTo(point);
                mapView.getController().setZoom(18.0);
            }
        });
    }
    private void viewModelObserver() {
        viewModel.getAuthResource().observe(getViewLifecycleOwner(), resource -> {
            LoaderManager.handleResource(this, resource);
            if (resource.status == com.example.tregoapp.customer.network.Resource.Status.SUCCESS && resource.data != null) {
                com.example.tregoapp.customer.model.response.User currentUser = resource.data;
                customerId = currentUser.getId();
                tvDashboardName.setText("Hi, " + currentUser.getName());
                viewModel.getLiveRequestedRequest(customerId);
                initSocket();
            } else if (resource.status == com.example.tregoapp.customer.network.Resource.Status.ERROR) {
                if (resource.message != null) {
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewModel.getNearbyShopsResource().observe(getViewLifecycleOwner(), resource -> {
            LoaderManager.handleResource(this, resource);
            if (resource.status == com.example.tregoapp.customer.network.Resource.Status.SUCCESS) {
                java.util.List<ShopDetail> shops = resource.data;
                if (shops == null || shops.isEmpty()) {
                    EmptyStateHelper.show(root, "No shops available", shopsRecyclerView);
                    adapter.setShopDetailList(null);
                } else {
                    EmptyStateHelper.hide(root, shopsRecyclerView);
                    adapter.setShopDetailList(new ArrayList<>(shops));
                }
            } else if (resource.status == com.example.tregoapp.customer.network.Resource.Status.ERROR) {
                EmptyStateHelper.show(root, resource.message != null ? resource.message : "Error loading shops", shopsRecyclerView);
            }
        });

        viewModel.getLiveRequestResource().observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == com.example.tregoapp.customer.network.Resource.Status.SUCCESS) {
                if (resource.data != null && !resource.data.isEmpty()) {
                    com.example.tregoapp.customer.model.ServiceRequest activeRequest = resource.data.get(0);
                    
                    // If the request is 'pending', don't allow tracking yet
                    boolean isTrackable = !"pending".equalsIgnoreCase(activeRequest.getStatus());

//                    if ("in_progress".equalsIgnoreCase(activeRequest.getStatus()) && !activeRequest.getIsActive()) {
//                        return;
//                    }

                    liveRequestCard.setVisibility(View.VISIBLE);
                    if (activeRequest.getShopName() != null) {
                        tvLiveReqShopName.setText(activeRequest.getShopName());
                    } else if (activeRequest.getShopId() != null) {
                        viewModel.fetchShopDetails(activeRequest.getShopId());
                    }
                    tvLiveReqTime.setText(Utility.getInstance().formatDate2(activeRequest.getCreatedAt()));

                    /*
                     * MECHANIC NAME
                     */
                    tvMechanicName.setText(

                            activeRequest.getMechanicName() != null

                                    ? "Mechanic: " + activeRequest.getMechanicName()

                                    : "Mechanic Assigned"
                    );

                    /*
                     * SOS REQUEST
                     */
                    /*
                     * SOS REQUEST
                     */
                    if (activeRequest.getIsSOS()) {

                        /*
                         * SERVICE NAME
                         */
                        tvLiveReqService.setText(
                                "SOS Service"
                        );

                        tvLiveReqService.setTextColor(
                                Color.parseColor("#FF3B30")
                        );

                        /*
                         * DESCRIPTION
                         */
                        String problemDescription =

                                activeRequest.getProblemDescription();

                        if (
                                problemDescription == null
                                        ||
                                        problemDescription.trim().isEmpty()
                        ) {

                            problemDescription =
                                    "Emergency assistance required";
                        }

                        tvServiceDescription.setText(
                                problemDescription
                        );

                        /*
                         * HIDE EXTRA PROBLEM CARD
                         */
                        tvProblemDescription.setVisibility(
                                View.GONE
                        );
                    }

                    /*
                     * NORMAL REQUEST
                     */
                    else {

                        String serviceName =
                                activeRequest.getServiceName();

                        String serviceDescription =
                                activeRequest.getServiceDescription();

                        tvLiveReqService.setText(

                                serviceName != null
                                        &&
                                        !serviceName.trim().isEmpty()

                                        ? serviceName

                                        : "Vehicle Service"
                        );

                        tvLiveReqService.setTextColor(
                                Color.parseColor("#1F1F1F")
                        );

                        tvServiceDescription.setText(

                                serviceDescription != null
                                        &&
                                        !serviceDescription.trim().isEmpty()

                                        ? serviceDescription

                                        : "No description available"
                        );

                        tvProblemDescription.setVisibility(
                                View.VISIBLE
                        );

                        tvProblemDescription.setText(

                                "Problem: " +

                                        (
                                                activeRequest.getProblemDescription() != null
                                                        &&
                                                        !activeRequest.getProblemDescription().trim().isEmpty()

                                                        ? activeRequest.getProblemDescription()

                                                        : "-"
                                        )
                        );
                    }

                    tvLiveReqPrice.setText(
                            "₹" + activeRequest.getTotalPrice()
                    );
                    
                    if (isTrackable) {
                        if ("requested".equalsIgnoreCase(activeRequest.getStatus())) {
                            btnCancelRequest.setVisibility(View.VISIBLE);
                            btnTrack.setVisibility(View.GONE);
                            btnCancelRequest.setTag(activeRequest.getId());
                        }
                        else {
                            btnTrack.setVisibility(View.VISIBLE);
                            btnCancelRequest.setVisibility(View.GONE);
                            btnTrack.setTag(activeRequest.getId());
                        }
                    } else {
                        btnTrack.setVisibility(View.GONE);
                    }

                    // Update status background
                    updateStatusUI(activeRequest.getStatus());
                } else {
                    liveRequestCard.setVisibility(View.GONE);
                }
            } else {
                liveRequestCard.setVisibility(View.GONE);
            }
        });

        viewModel.getShopDetailsLiveData().observe(getViewLifecycleOwner(), resource -> {
            LoaderManager.handleResource(this, resource, shop -> {
                if (shop != null) {
                    tvLiveReqShopName.setText(shop.getShopName());
                }
            });
        });
    }

    private void updateStatusUI(String status) {
        if (status == null) return;
        switch (status.toLowerCase()) {
            case "pending":
                tvLiveReqStatus.setText("Pending");
                tvLiveReqStatus.setBackgroundResource(R.drawable.status_pending_bg);
                break;
            case "accepted":
                tvLiveReqStatus.setText("Accepted");
                tvLiveReqStatus.setBackgroundResource(R.drawable.status_pending_bg);
                break;
            case "ongoing":
            case "in_progress":
                tvLiveReqStatus.setText("In Progress");
                tvLiveReqStatus.setBackgroundResource(R.drawable.status_pending_bg);
                break;
            case "waiting_for_confirmation":
                tvLiveReqStatus.setText("Wait for Confirmation");
                tvLiveReqStatus.setBackgroundResource(R.drawable.status_pending_bg);
                break;
            default:
                tvLiveReqStatus.setText("Pending");
                tvLiveReqStatus.setBackgroundResource(R.drawable.status_pending_bg);
                break;
        }
    }

    private void initSocket() {
        try {
            socket = IO.socket(com.example.tregoapp.BuildConfig.BASE_URL_ENDPOINT);
            socket.connect();

            socket.on(Socket.EVENT_CONNECT, args -> {
                if (customerId != null) {
                    socket.emit("join_customer_room", customerId);
                }
            });

            socket.on("live_request_update", args -> {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        viewModel.getLiveRequestedRequest(customerId);
                    });
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}