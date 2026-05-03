package com.example.tregoapp.customer.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import com.example.tregoapp.R;
import com.example.tregoapp.WelcomeFragment;

import com.example.tregoapp.customer.bottomsheet.VehicleListFragment;
import com.example.tregoapp.customer.navigation.NavigationHelper;
import com.example.tregoapp.customer.utils.LoaderManager;
import com.example.tregoapp.customer.viewmodel.ViewModel;
import com.example.tregoapp.customer.model.response.User;
import com.example.tregoapp.customer.network.Resource;
import com.google.android.material.card.MaterialCardView;

public class ProfileFragment extends Fragment {

    private MaterialCardView backBtn;
    private TextView tvName;
    private TextView tvId;
    private TextView tvPhoneNumber;
    private TextView tvAddress;
    private LinearLayout goToAllVehicles;
    private LinearLayout goToRequestHistory;

    private LinearLayout logoutBtn;
    private ViewModel viewModel;
    private String customerId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        backBtn = view.findViewById(R.id.backBtn);
        logoutBtn = view.findViewById(R.id.logoutBtn);
        tvName = view.findViewById(R.id.tvName);
        tvId = view.findViewById(R.id.tvCustomerId);
        tvPhoneNumber = view.findViewById(R.id.tvPhoneNumber);
        tvAddress = view.findViewById(R.id.tvAddress);
        goToAllVehicles = view.findViewById(R.id.goToAllVehicles);
        goToRequestHistory = view.findViewById(R.id.goToRequestHistory);

        // Debug: Clear hardcoded text to verify code execution
        tvName.setText("...");
        tvId.setText("...");
        tvPhoneNumber.setText("...");
        tvAddress.setText("...");

        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        
        viewModelObserver();

        viewModel.loadSavedUser();

        backBtn.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });

        logoutBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        viewModel.logout();
                        NavigationHelper.clearBackStackAndNavigate(getParentFragmentManager(), new WelcomeFragment());
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        goToAllVehicles.setOnClickListener(v -> {
            if (customerId == null || customerId.isEmpty()) {
                Toast.makeText(requireContext(),
                        "Customer Id is empty",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            VehicleListFragment sheet = VehicleListFragment.newInstance(customerId);
            sheet.show(getParentFragmentManager(), "VehicleListBottomSheet");
        });

        goToRequestHistory.setOnClickListener(v -> {
            if (customerId == null || customerId.isEmpty()) {
                Toast.makeText(requireContext(), "Customer Id is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            NavigationHelper.navigateTo(getParentFragmentManager(), ServiceRequestsHistoryFragment.newInstance(customerId));
        });
    }

    private void viewModelObserver() {
        viewModel.getAuthResource().observe(getViewLifecycleOwner(), this::handleUserResource);
        viewModel.getCustomerDetailsLiveData().observe(getViewLifecycleOwner(), this::handleUserResource);
    }

    private void handleUserResource(Resource<User> resource) {
        if (resource == null) return;

        if (resource.status == Resource.Status.LOADING) {
            LoaderManager.show(this);
        } else if (resource.status == Resource.Status.SUCCESS) {
            LoaderManager.hide(this);
            if (resource.data != null) {
                User user = resource.data;
                customerId = user.getId();
                
                tvName.setText(user.getName() != null && !user.getName().isEmpty() ? user.getName() : "No Name");

                String id = user.getId();
                if (id != null && !id.isEmpty()) {
                    tvId.setText("ID : " + id);
                } else {
                    tvId.setText("TREGOID12345");
                }

                String phone = user.getPhoneNumber();
                if (phone != null && !phone.isEmpty()) {
                    tvPhoneNumber.setText(phone);
                } else {
                    tvPhoneNumber.setText("Mobile Not Provided");
                }

                String address = user.getAddress();
                if (address != null && !address.isEmpty()) {
                    tvAddress.setText(address);
                } else {
                    tvAddress.setText("Address Not Provided");
                }
            }
        } else if (resource.status == Resource.Status.ERROR) {
            LoaderManager.hide(this);
            Toast.makeText(requireContext(), "Error: " + resource.message, Toast.LENGTH_SHORT).show();
        }
    }
}