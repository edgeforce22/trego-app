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

import com.example.tregoapp.customer.navigation.NavigationHelper;
import com.example.tregoapp.customer.utils.LoaderManager;
import com.example.tregoapp.customer.viewmodel.ViewModel;
import com.example.tregoapp.customer.model.response.User;
import com.example.tregoapp.customer.network.Resource;
import com.google.android.material.card.MaterialCardView;

public class ProfileFragment extends Fragment {

    private ImageView backBtn;
    private TextView tvName;
//    private TextView tvId;
    private TextView tvPhoneNumber;
    private TextView tvAddress;
    private LinearLayout goToRegisterVehicle;
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
//        tvId = view.findViewById(R.id.tvId);
        tvPhoneNumber = view.findViewById(R.id.tvPhoneNumber);
        tvAddress = view.findViewById(R.id.tvAddress);
        goToRegisterVehicle = view.findViewById(R.id.goToRegisterVehicle);

        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        
        // Use user ID from SharedPreferences if available, otherwise it'll be loaded via loadSavedUser
        String savedUserId = viewModel.getUserId();
        if (savedUserId != null && !savedUserId.isEmpty()) {
            viewModel.getCurrentUser(savedUserId);
        } else {
            viewModel.loadSavedUser();
        }
        
        viewModelObserver();

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

        goToRegisterVehicle.setOnClickListener(v -> {
            if (customerId == null || customerId.isEmpty()) {
                Toast.makeText(requireContext(), "Customer Id is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            NavigationHelper.navigateTo(getParentFragmentManager(), VehicleRegistrationFragment.newInstance(customerId));
        });
    }

    private void viewModelObserver() {
        viewModel.getAuthResource().observe(getViewLifecycleOwner(), resource -> {
            LoaderManager.handleResource(this, resource, new LoaderManager.ResourceCallback<User>() {
                @Override
                public void onSuccess(User user) {
                    if (user != null) {
                        customerId = user.getId();
                        tvName.setText(user.getName());
                        tvPhoneNumber.setText("+91 " + user.getPhoneNumber());
                        tvAddress.setText(user.getAddress());
                    }
                }
            });
        });
    }
}