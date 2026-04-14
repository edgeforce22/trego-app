package com.example.tregoapp.mechanic.ui;

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
import android.widget.TextView;

import com.example.tregoapp.R;
import com.example.tregoapp.customer.navigation.NavigationHelper;
import com.example.tregoapp.mechanic.listener.OnItemClickListener;
import com.example.tregoapp.mechanic.adapter.ActiveServiceRequestsAdapter;
import com.example.tregoapp.mechanic.utils.EmptyStateHelper;
import com.example.tregoapp.mechanic.utils.LoaderManager;
import com.example.tregoapp.mechanic.viewmodel.ViewModel;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;

public class ActiveServicesFragment extends Fragment implements OnItemClickListener {

    private View root;

    private static final String SHOP_ID = "shop_id";
    private String shopId;
    private String mechanicId;

    private TextView tvCustomerName;
    private TextView tvCustomerAddress;
    private TextView tvTotalDistance;
    private TextView tvTotalDuration;
    private TextView tvCreatedAt;

    private RecyclerView recyclerView;
    private ActiveServiceRequestsAdapter adapter;

    private ViewModel viewModel;
    private boolean isActive;

    private Handler handler;
    private Runnable runnable;

    public ActiveServicesFragment() {
        // Required empty public constructor
    }

    public static ActiveServicesFragment newInstance(String shopId) {
        ActiveServicesFragment fragment = new ActiveServicesFragment();
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
        return inflater.inflate(R.layout.fragment_mechanic_active_services, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        root = view;
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemViewCacheSize(20);

        adapter = new ActiveServiceRequestsAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
        LoaderManager.show(this);
        viewModel.loadSavedUser();
        viewModelObserver();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopPolling();
    }

    @Override
    public void onClick(String requestId) {
        MechanicSideTrackingFragment mechanicSideTrackingFragment = MechanicSideTrackingFragment.newInstance(requestId);
        NavigationHelper.navigateTo(requireActivity().getSupportFragmentManager(), mechanicSideTrackingFragment);
    }

    @Override
    public void onClick2(String shopId) {

    }

    private void viewModelObserver() {
        viewModel.getAuthState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) {
                return;
            }
//            Toast.makeText(requireContext(), state.getMessage(), Toast.LENGTH_SHORT).show();
        });

        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), currentUser -> {
            if (currentUser == null) {
                return;
            }
            mechanicId = currentUser.getId();

            isActive = "active".equalsIgnoreCase(currentUser.getStatus());

            if (isActive && shopId != null && mechanicId != null) {
                startPolling();
            } else {
                LoaderManager.hide(this);
                stopPolling();
            }
        });

        viewModel.getActiveServiceRequestsLiveData().observe(getViewLifecycleOwner(), activeServices -> {
            LoaderManager.hide(this);
            if (activeServices == null || activeServices.isEmpty()) {
                EmptyStateHelper.show(root, "No active request is available", recyclerView);
                adapter.submitList(null);
            }
            else {
                EmptyStateHelper.hide(root, recyclerView);
                
                // Fetch customer details for each service request if name is missing
                for (com.example.tregoapp.mechanic.model.ServiceRequest request : activeServices) {
                    if (request.getCustomerName() == null || request.getCustomerName().isEmpty()) {
                        viewModel.fetchCustomerDetails(request.getCustomerId());
                    }
                }
                
                adapter.submitList(new ArrayList<>(activeServices));
            }
        });

        viewModel.getCustomerDetailsLiveData().observe(getViewLifecycleOwner(), customer -> {
            if (customer == null) return;
            
            // Update the adapter's list with the fetched customer name
            java.util.List<com.example.tregoapp.mechanic.model.ServiceRequest> currentList = adapter.getCurrentList();
            for (int i = 0; i < currentList.size(); i++) {
                com.example.tregoapp.mechanic.model.ServiceRequest request = currentList.get(i);
                if (request.getCustomerId().equals(customer.getId())) {
                    request.setCustomerName(customer.getName());
                    adapter.notifyItemChanged(i);
                }
            }
        });
    }

    private void startPolling() {
        if (handler != null) return;
        handler = new Handler(Looper.getMainLooper());

        runnable = new Runnable() {
            @Override
            public void run() {
                if (shopId != null) {
                    viewModel.getActiveServiceRequests(mechanicId, shopId);
                }
                handler.postDelayed(this, 10000);
            }
        };
        handler.post(runnable);
    }

    private void stopPolling() {
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
            handler = null;
        }
    }
}