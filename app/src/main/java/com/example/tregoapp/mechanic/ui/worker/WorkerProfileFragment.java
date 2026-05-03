package com.example.tregoapp.mechanic.ui.worker;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
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
import com.example.tregoapp.mechanic.ui.OwnerBottomNavigationFragment;
import com.example.tregoapp.mechanic.utils.LoadFragment;
import com.example.tregoapp.mechanic.utils.LoaderManager;
import com.example.tregoapp.mechanic.viewmodel.ViewModel;
import com.google.android.material.card.MaterialCardView;

public class WorkerProfileFragment extends Fragment {

    private String shopId;
    private ImageView backBtn;
    private LinearLayout logoutBtn;
    private TextView tvName;
    private TextView tvPhoneNumber;
    private TextView tvShopStatus;
    private TextView tvAddress;
    private TextView tvShopName;

    private ViewModel viewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_worker_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        backBtn = view.findViewById(R.id.backBtn);
        logoutBtn = view.findViewById(R.id.logoutBtn);
        tvName = view.findViewById(R.id.tvName);
        tvPhoneNumber = view.findViewById(R.id.tvPhoneNumber);
        tvShopStatus = view.findViewById(R.id.tvShopStatus);
        tvAddress = view.findViewById(R.id.tvAddress);
        tvShopName = view.findViewById(R.id.tvShopName);

        // Reset text to avoid showing hardcoded XML values during loading
        tvName.setText("...");
        tvPhoneNumber.setText("...");
        tvShopStatus.setText("...");
        tvAddress.setText("...");
        tvShopName.setText("...");

        LoadFragment.replaceChildFragment(this, R.id.dashboardBottomContainer, new WorkerBottomNavigationFragment());

        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        LoaderManager.show(this);
        viewModel.loadSavedUser();
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
                        LoaderManager.hide(this);
                        viewModel.logout();

                        getParentFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        getParentFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, new WelcomeFragment())
                                .commit();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void viewModelObserver() {
        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), currentUser -> {
            if (currentUser == null) {
                return;
            }

            tvName.setText(currentUser.getName());
            String phone = currentUser.getPhoneNumber();
            if (phone != null && !phone.isEmpty()) {
                String cleanPhone = phone.replace("91+", "").trim();
                tvPhoneNumber.setText("91+ " + cleanPhone);
            }
            if ("active".equalsIgnoreCase(currentUser.getStatus())) {
                tvShopStatus.setText("Active");
                tvShopStatus.setTextColor(Color.parseColor("#16A34A"));
            }
            else {
                tvShopStatus.setText("Inactive");
                tvShopStatus.setTextColor(Color.parseColor("#D32F2F"));
            }
            tvAddress.setText(currentUser.getAddress());

            if (currentUser.getShopId() != null && !currentUser.getShopId().isEmpty()) {
                viewModel.fetchShopDetails(currentUser.getShopId());
            } else {
                tvShopName.setText("Not Assigned");
                LoaderManager.hide(this);
            }
        });

        viewModel.getShopDetailsLiveData().observe(getViewLifecycleOwner(), resource -> {
            if (resource == null) return;

            switch (resource.status) {
                case LOADING:
                    LoaderManager.show(this);
                    break;
                case SUCCESS:
                    LoaderManager.hide(this);
                    if (resource.data != null) {
                        tvShopName.setText(resource.data.getShopName());
                    }
                    break;
                case ERROR:
                    LoaderManager.hide(this);
                    tvShopName.setText("Error loading shop");
                    Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }
}