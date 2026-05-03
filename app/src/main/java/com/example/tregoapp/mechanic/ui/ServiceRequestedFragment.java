package com.example.tregoapp.mechanic.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tregoapp.BuildConfig;
import com.example.tregoapp.R;
import com.example.tregoapp.customer.navigation.NavigationHelper;
import com.example.tregoapp.mechanic.model.ServiceRequest;
import com.example.tregoapp.mechanic.network.socket.SocketHandler;
import com.example.tregoapp.mechanic.network.socket.SocketManager;
import com.example.tregoapp.mechanic.utils.EmptyStateHelper;
import com.example.tregoapp.mechanic.utils.LoaderManager;
import com.example.tregoapp.mechanic.adapter.CustomerListAdapter;
import com.example.tregoapp.mechanic.listener.OnItemClickListener;
import com.example.tregoapp.mechanic.utils.DeviceLocationHelper;
import com.example.tregoapp.mechanic.viewmodel.ViewModel;
import com.google.gson.Gson;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;

import io.socket.client.Socket;

public class ServiceRequestedFragment extends Fragment implements OnItemClickListener {

    private View root;

    private static final String SHOP_ID = "shop_id";
    private final String BASE_URL_ENDPOINT = BuildConfig.BASE_URL_ENDPOINT;
    private String shopId;
    private String userId;
    private boolean isActive;
    private String address = new String();
    private double latitude, longitude;

//    private ImageView backBtn;
    private RecyclerView recyclerView;

    private ViewModel viewModel;
    private CustomerListAdapter adapter;

//    private Handler handler;
//    private Runnable runnable;

    public ServiceRequestedFragment() {
        // Required empty public constructor
    }

    public static ServiceRequestedFragment newInstance(String shopId) {
        ServiceRequestedFragment fragment = new ServiceRequestedFragment();
        Bundle args = new Bundle();
        args.putString(SHOP_ID, shopId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            shopId = getArguments().getString(SHOP_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_service_requested, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        root = view;
        LoaderManager.show(this);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemViewCacheSize(20);

        adapter = new CustomerListAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
        viewModel.loadSavedUser();
        viewModelObserver();

        DeviceLocationHelper helper = new DeviceLocationHelper(requireContext());
        helper.getCurrentLocation(requireContext(), (lat, lon, addr) -> {
            this.latitude = lat;
            this.longitude = lon;
            this.address = addr;
        });

        // Socket Setup
        socketSetup();
    }

    @Override
    public void onClick(String serviceRequestId) {
        acceptServiceRequest(serviceRequestId);
    }

    @Override
    public void onClick2(String serviceRequestId) {
        viewModel.cancelServiceRequest(serviceRequestId, userId);
        viewModel.removeRequest(serviceRequestId);
    }

    private void acceptServiceRequest(String serviceRequestId) {
        Log.d("ACCEPT_FLOW", "Accept button clicked for ID: " + serviceRequestId);

        // Fallback: If userId is null, try getting it directly from viewModel
        if (userId == null) {
            userId = viewModel.getUserId();
        }

        if (userId == null) {
            Log.e("ACCEPT_FLOW", "User ID is null even after fallback");
            Toast.makeText(requireContext(), "User ID not found. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("ACCEPT_FLOW", "Using UserID: " + userId);
        DeviceLocationHelper helper = new DeviceLocationHelper(requireContext());
        LoaderManager.show(this);

        helper.getCurrentLocation(requireContext(), (lat, lon, addr) -> {
            Log.d("ACCEPT_FLOW", "Location received: " + lat + ", " + lon);
            this.latitude = lat;
            this.longitude = lon;
            this.address = addr;

            viewModel.acceptServiceRequest(serviceRequestId, userId, latitude, longitude, address);
        });
    }

    private void viewModelObserver() {
        viewModel.getAuthState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) {
                return;
            }
            LoaderManager.hide(this);
//            Toast.makeText(requireContext(), state.getMessage(), Toast.LENGTH_SHORT).show();
        });

        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), currentUser -> {
            if (currentUser == null) {
                return;
            }
            userId = currentUser.getId();

            isActive = "active".equalsIgnoreCase(currentUser.getStatus());

            if (isActive) {
                viewModel.getShopServiceRequests(userId, shopId);
            } else {
                LoaderManager.hide(this);
                adapter.submitList(null);
            }
        });

        viewModel.getShopServiceRequestsLiveData().observe(getViewLifecycleOwner(), services -> {
            LoaderManager.hide(this);
            if (services == null || services.isEmpty()) {
                EmptyStateHelper.show(root, "No request is available", recyclerView);
                adapter.submitList(null);
            }
            else {
                EmptyStateHelper.hide(root, recyclerView);
                adapter.submitList(new ArrayList<>(services));
            }
        });
        viewModel.getAcceptServiceRequestLiveData().observe(getViewLifecycleOwner(), serviceRequest -> {
            LoaderManager.hide(this);
            if (serviceRequest == null) {
                return;
            }

            if ("accepted".equalsIgnoreCase(serviceRequest.getStatus())) {
                AcceptanceFragment acceptanceFragment = AcceptanceFragment.newInstance(serviceRequest.getId());
                NavigationHelper.navigateTo(requireActivity().getSupportFragmentManager(), acceptanceFragment, false);
                viewModel.clearAcceptServiceRequest();
            } else if ("pending".equalsIgnoreCase(serviceRequest.getStatus())) {
                Toast.makeText(requireContext(), "Already accepted by another mechanic", Toast.LENGTH_SHORT).show();
                viewModel.clearAcceptServiceRequest();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Socket socket = SocketManager.getSocket();

        if (socket != null) {
            socket.off("new_request");
            socket.off("request_removed");
            socket.off(Socket.EVENT_CONNECT);
        }
    }

    // Socket

    private void socketSetup() {
        SocketManager.init(BASE_URL_ENDPOINT);

        Socket socket = SocketManager.getSocket();

        if (socket != null) {

            SocketManager.connect();

            SocketHandler handler = new SocketHandler(socket);

            // 🔥 JOIN IMMEDIATELY (FIX)
            socket.emit("join_shop", shopId);
            Log.d("SOCKET_FIX", "Joining shop immediately: " + shopId);

            // 🔁 Rejoin on reconnect
            socket.off(Socket.EVENT_CONNECT);
            socket.on(Socket.EVENT_CONNECT, args -> {
                Log.d("SOCKET_FIX", "Reconnected → joining shop: " + shopId);
                socket.emit("join_shop", shopId);
            });

            // 🔔 New request
            handler.onNewRequest(data -> {
                Log.d("SOCKET", "RAW DATA: " + data.toString());

                requireActivity().runOnUiThread(() -> {
                    try {
                        ServiceRequest request = new Gson().fromJson(data.toString(), ServiceRequest.class);

                        if ("SOS".equalsIgnoreCase(request.getType())) {
                            Log.d("SOS", "🚨 SOS RECEIVED");

                            if (request.getTotalDistance() <= 0 &&
                                    request.getCustomerLocation() != null) {

                                double customerLat =
                                        request.getCustomerLocation().getLatitude();

                                double customerLng =
                                        request.getCustomerLocation().getLongitude();

                                double distance = calculateDistanceKm(
                                        latitude,
                                        longitude,
                                        customerLat,
                                        customerLng
                                );

                                request.setTotalDistance(distance);
                            }
                        }

                        viewModel.addRequest(request);

                    } catch (Exception e) {
                        Log.e("SOCKET", "Parsing error", e);
                    }
                });
            });

            // ❌ Remove request
            handler.onRequestRemoved(requestId -> {
                requireActivity().runOnUiThread(() -> {
                    viewModel.removeRequest(requestId);
                });
            });
        }
    }

    // ================= ADD THIS METHOD INSIDE ServiceRequestedFragment =================

    private double calculateDistanceKm(
            double startLat,
            double startLng,
            double endLat,
            double endLng
    ) {
        float[] results = new float[1];

        android.location.Location.distanceBetween(
                startLat,
                startLng,
                endLat,
                endLng,
                results
        );

        return results[0] / 1000.0;
    }
}