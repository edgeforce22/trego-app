package com.example.tregoapp.customer.bottomsheet;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.tregoapp.R;
import com.example.tregoapp.customer.adapter.VehicleListAdapter;
import com.example.tregoapp.customer.model.VehicleDetail;
import com.example.tregoapp.customer.navigation.NavigationHelper;
import com.example.tregoapp.customer.ui.VehicleRegistrationFragment;
import com.example.tregoapp.customer.utils.LoaderManager;
import com.example.tregoapp.customer.viewmodel.ViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class VehicleListFragment extends BottomSheetDialogFragment {


    private final static String CUSTOMER_ID = "customer_id";
    private List<VehicleDetail> vehiclesList;
    private String customerId;

    private MaterialCardView addVehicleBtn;
    private RecyclerView rvVehicleList;

    private ViewModel viewModel;

    private VehicleListAdapter vehicleListAdapter;

    public VehicleListFragment() {
    }

    public static VehicleListFragment newInstance(String customerId) {
        VehicleListFragment fragment = new VehicleListFragment();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vehicle_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        addVehicleBtn = view.findViewById(R.id.addVehicleBtn);
        rvVehicleList = view.findViewById(R.id.rvVehicleList);

        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        viewModel.getVehicles(customerId);
        viewModelObserver();

        rvVehicleList.setLayoutManager(
                new LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                )
        );

        addVehicleBtn.setOnClickListener(v -> {
            if (customerId == null || customerId.isEmpty()) {
                Toast.makeText(requireContext(), "Customer Id is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            NavigationHelper.navigateTo(getParentFragmentManager(), VehicleRegistrationFragment.newInstance(customerId));
            dismiss();
        });
    }

    private void viewModelObserver() {
        viewModel.getVehicleListResource().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            if (resource.data != null) {
                vehiclesList = resource.data;
                vehicleListAdapter =
                        new VehicleListAdapter(
                                vehiclesList,
                                null
                        );
                vehicleListAdapter.setItemClickable(false);
                rvVehicleList.setAdapter(vehicleListAdapter);
            }

            if (resource.message != null && !resource.message.isEmpty()) {
                Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetTransparent;
    }
}