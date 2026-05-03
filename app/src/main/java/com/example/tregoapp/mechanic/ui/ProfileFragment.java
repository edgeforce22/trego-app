package com.example.tregoapp.mechanic.ui;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.ClipData;
import android.graphics.Color;
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
import com.example.tregoapp.mechanic.utils.LoaderManager;
import com.example.tregoapp.mechanic.utils.LoadFragment;
import com.example.tregoapp.mechanic.viewmodel.ViewModel;

public class ProfileFragment extends Fragment {

    private String shopId;
    private ImageView backBtn, copyShopIdBtn;
    private LinearLayout logoutBtn;
    private TextView tvName;
    private TextView tvPhoneNumber;
    private TextView tvShopStatus;
    private TextView tvAddress;
    private TextView tvShopName;
    private TextView tvShopId;
    private LinearLayout goToCreateService;

    private ViewModel viewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mechanic_profile, container, false);
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
        tvShopId = view.findViewById(R.id.tvShopId);
        goToCreateService = view.findViewById(R.id.goToCreateService);
        copyShopIdBtn = view.findViewById(R.id.copyShopIdBtn);

        // Reset text to avoid showing hardcoded XML values during loading
        tvName.setText("...");
        tvPhoneNumber.setText("...");
        tvShopStatus.setText("...");
        tvAddress.setText("...");
        tvShopName.setText("...");
        tvShopId.setText("...");

        LoadFragment.replaceChildFragment(this, R.id.dashboardBottomContainer, new OwnerBottomNavigationFragment());

        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        LoaderManager.show(this);
        viewModel.loadSavedUser();
        viewModelObserver();

        backBtn.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });

        goToCreateService.setOnClickListener(v -> {
            shopId = viewModel.getShopId();

            if (shopId == null || shopId.isEmpty()) {
                Toast.makeText(requireContext(),
                        "Please register your shop first",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            CreateServiceFragment createServiceFragment = CreateServiceFragment.newInstance(shopId);
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, createServiceFragment)
                    .addToBackStack(null)
                    .commit();
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
                                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                                .replace(R.id.fragment_container, new WelcomeFragment())
                                .commit();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        copyShopIdBtn.setOnClickListener(v -> {
            String idToCopy = tvShopId.getText().toString();
            if (!idToCopy.isEmpty() && !idToCopy.equals("...") && !idToCopy.equals("Not Registered")) {
                ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Shop ID", idToCopy);
                clipboard.setPrimaryClip(clip);
                // Toast.makeText(requireContext(), "Shop ID copied to clipboard", Toast.LENGTH_SHORT).show();
            }
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

            shopId = currentUser.getShopId();
            if (shopId != null && !shopId.isEmpty()) {
                tvShopId.setText(shopId);

                // Try to load from local storage first
                com.example.tregoapp.mechanic.model.ShopDetail savedShop = viewModel.getSavedShopDetails();
                if (savedShop != null && shopId.equals(savedShop.getShopId())) {
                    tvShopName.setText(savedShop.getShopName());
                    LoaderManager.hide(this);
                } else {
                    viewModel.fetchShopDetails(shopId);
                }
            } else {
                tvShopName.setText("Not Registered");
                tvShopId.setText("Not Registered");
                LoaderManager.hide(this);
            }
        });

        viewModel.getShopDetailsLiveData().observe(getViewLifecycleOwner(), shopResource -> {
            if (shopResource == null) return;
            switch (shopResource.status) {
                case LOADING:
                    LoaderManager.show(this);
                    break;
                case SUCCESS:
                    LoaderManager.hide(this);
                    if (shopResource.data != null) {
                        tvShopName.setText(shopResource.data.getShopName());
                    }
                    break;
                case ERROR:
                    LoaderManager.hide(this);
                    tvShopName.setText("Error loading shop");
                    // Toast.makeText(requireContext(), "Error fetching shop details: " + shopResource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }
}