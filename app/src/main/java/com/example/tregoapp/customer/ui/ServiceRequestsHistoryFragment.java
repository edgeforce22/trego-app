package com.example.tregoapp.customer.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.tregoapp.R;
import com.example.tregoapp.customer.adapter.ServiceRequestHistoryAdapter;
import com.example.tregoapp.customer.model.ServiceRequest;
import com.example.tregoapp.customer.viewmodel.ViewModel;
import com.google.android.material.card.MaterialCardView;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class ServiceRequestsHistoryFragment extends Fragment {

    private static final String CUSTOMER_ID = "CUSTOMER_ID";
    private String customerId;

    private MaterialCardView backBtn;
    private RecyclerView rvRequestHistory;
    private LinearLayout emptyLayout;

    private ViewModel viewModel;
    private List<ServiceRequest> serviceRequestsList = new ArrayList<>();
    private ServiceRequestHistoryAdapter adapter;


    public ServiceRequestsHistoryFragment() {
    }

    public static ServiceRequestsHistoryFragment newInstance(String customerId) {
        ServiceRequestsHistoryFragment fragment = new ServiceRequestsHistoryFragment();
        Bundle args = new Bundle();
        args.putString(CUSTOMER_ID, customerId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            customerId = getArguments().getString(CUSTOMER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_service_requests_history, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        backBtn = view.findViewById(R.id.backBtn);
        rvRequestHistory = view.findViewById(R.id.rvRequestHistory);
        emptyLayout = view.findViewById(R.id.emptyLayout);

        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        viewModel.getServiceRequestHistory(customerId);
        viewModelObserver();

        adapter = new ServiceRequestHistoryAdapter(new ArrayList<>());
        rvRequestHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvRequestHistory.setAdapter(adapter);

        backBtn.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });
    }

    private void viewModelObserver() {
        viewModel.getServiceRequestHistory().observe(getViewLifecycleOwner(), serviceRequestsList -> {
            if (serviceRequestsList == null) {
                return;
            }

            if (serviceRequestsList.data != null) {
                this.serviceRequestsList.clear();
                this.serviceRequestsList.addAll(serviceRequestsList.data);

                if (this.serviceRequestsList.isEmpty()) {
                    rvRequestHistory.setVisibility(View.GONE);
                    emptyLayout.setVisibility(View.VISIBLE);
                    return;
                }

                rvRequestHistory.setVisibility(View.VISIBLE);
                emptyLayout.setVisibility(View.GONE);

                adapter.updateList(this.serviceRequestsList);
            }
        });
    }
}