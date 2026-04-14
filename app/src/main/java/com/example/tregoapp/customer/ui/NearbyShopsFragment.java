package com.example.tregoapp.customer.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tregoapp.customer.adapter.NearbyShopsAdapter;
import com.example.tregoapp.customer.listener.OnItemClickListener;
import com.example.tregoapp.R;
import com.example.tregoapp.customer.model.ShopDetail;
import com.example.tregoapp.customer.utils.DeviceLocationHelper;
import com.example.tregoapp.customer.utils.NetworkLocationHelper;
import com.example.tregoapp.customer.viewmodel.ViewModel;
import com.example.tregoapp.customer.utils.LoaderManager;
import com.example.tregoapp.customer.utils.EmptyStateHelper;

import java.util.ArrayList;

public class NearbyShopsFragment extends Fragment implements OnItemClickListener {

    private View root;

    private String customerId;
    private double latitude;
    private double longitude;

    private RecyclerView recyclerView;

    private ViewModel viewModel;
    private NearbyShopsAdapter adapter;

    private Handler handler;
    private Runnable runnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nearby_shops, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        root = view;
        recyclerView = view.findViewById(R.id.recyclerView);

        adapter = new NearbyShopsAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        viewModel.loadSavedUser();
        viewModelObserver();

        // Get user location
        DeviceLocationHelper helper =
                new DeviceLocationHelper(requireContext());

        helper.getCurrentLocation(requireContext(), (lat, lon, address) -> {
            latitude = lat;
            longitude = lon;

            if (lat != 0 && lon != 0) {
                LoaderManager.show(this);
                viewModel.getNearbyShops(latitude, longitude);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        startPolling();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopPolling();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopPolling();
    }

    @Override
    public void onClick(ShopDetail shopDetail) {
        openShopRequest(shopDetail);
    }

    private void openShopRequest(ShopDetail shopDetail) {
        CreateRequestFragment createRequestFragment = CreateRequestFragment.newInstance(shopDetail);
        com.example.tregoapp.customer.navigation.NavigationHelper.navigateTo(getParentFragmentManager(), createRequestFragment);
    }

    private void viewModelObserver() {
        viewModel.getNearbyShopsResource().observe(getViewLifecycleOwner(), resource -> {
            com.example.tregoapp.customer.utils.LoaderManager.handleResource(this, resource);
            if (resource.status == com.example.tregoapp.customer.network.Resource.Status.SUCCESS) {
                java.util.List<ShopDetail> shops = resource.data;
                if (shops == null || shops.isEmpty()) {
                    EmptyStateHelper.show(root, "No history is available", recyclerView);
                    adapter.setShopDetailList(null);
                } else {
                    EmptyStateHelper.hide(root, recyclerView);
                    adapter.setShopDetailList(new ArrayList<>(shops));
                }
            } else if (resource.status == com.example.tregoapp.customer.network.Resource.Status.ERROR) {
                EmptyStateHelper.show(root, resource.message != null ? resource.message : "Error loading shops", recyclerView);
            }
        });
    }

    private void startPolling() {
        if (handler != null && runnable != null) return;
        handler = new Handler(Looper.getMainLooper());

        runnable = new Runnable() {
            @Override
            public void run() {
                if(latitude != 0 && longitude != 0){
                    if (NetworkLocationHelper.isInternetAvailable(requireContext())) {
                        viewModel.getNearbyShops(latitude, longitude);
                    }
                }
                handler.postDelayed(this, 10000);
            }
        };
        handler.postDelayed(runnable, 0);
    }

    private void stopPolling() {
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
            handler = null;
        }
    }
}