package com.example.tregoapp.mechanic.ui;

import android.app.Service;
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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.example.tregoapp.R;
import com.example.tregoapp.WelcomeFragment;
import com.example.tregoapp.mechanic.bottomsheet.ServicesListBottomSheetFragment;
import com.example.tregoapp.mechanic.model.ServiceDetail;
import com.example.tregoapp.mechanic.model.ShopDetail;
import com.example.tregoapp.mechanic.model.response.User;
import com.example.tregoapp.mechanic.utils.LoaderManager;
import com.example.tregoapp.mechanic.utils.LoadFragment;
import com.example.tregoapp.mechanic.viewmodel.ViewModel;

import java.util.List;

public class ProfileFragment extends Fragment {

    private String mechanicId;
    private String shopId;
    private ImageView backBtn, copyShopIdBtn;
    private LinearLayout logoutBtn;
    private TextView tvName;
    private TextView tvPhoneNumber;
    private TextView tvShopStatus;
    private TextView tvAddress;
    private TextView tvShopName;
    private TextView tvShopId;
    private LinearLayout goToRegisterShop;
    private LinearLayout goToServicesList;
    private ImageView ivGoToRegisterShopArrow;
    private ImageView shopImage;
    private RatingBar ratingBar;
    private TextView tvRatingCount;


    private ViewModel viewModel;

    private User currentUser;
    private ShopDetail shopDetail;
    private List<ServiceDetail> serviceDetailList;


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
        goToServicesList = view.findViewById(R.id.goToServicesList);
        copyShopIdBtn = view.findViewById(R.id.copyShopIdBtn);
        goToRegisterShop = view.findViewById(R.id.goToRegisterShop);
        ivGoToRegisterShopArrow = view.findViewById(R.id.ivGoToRegisterShopArrow);
        shopImage = view.findViewById(R.id.shopImage);
        tvRatingCount = view.findViewById(R.id.tvRatingCount);
        ratingBar =
                view.findViewById(
                        R.id.ratingBar
                );

        // Reset text to avoid showing hardcoded XML values during loading
        tvName.setText("...");
        tvPhoneNumber.setText("...");
        tvShopStatus.setText("...");
        tvAddress.setText("...");
        tvShopName.setText("...");
        tvShopId.setText("...");

        LoadFragment.replaceChildFragment(this, R.id.dashboardBottomContainer, new OwnerBottomNavigationFragment());

        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        viewModelObserver();
        LoaderManager.show(this);

        /*
         * GET USER ID
         */
        mechanicId =
                viewModel.getUserId();

        /*
         * VALIDATION
         */
        if (
                mechanicId == null
                        ||
                        mechanicId.isEmpty()
        ) {

            LoaderManager.hide(this);

            Toast.makeText(
                    requireContext(),
                    "Mechanic ID not found",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        /*
         * API CALL
         */
        viewModel.mechanicCompleteDetails(
                mechanicId
        );

        backBtn.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });

        goToRegisterShop.setOnClickListener(v -> {
            mechanicId = viewModel.getUserId();

            if (mechanicId == null || mechanicId.isEmpty()) {
                Toast.makeText(requireContext(),
                        "User Id is null",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            ShopRegistrationFragment shopRegistrationFragment = ShopRegistrationFragment.newInstance(mechanicId);
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, shopRegistrationFragment)
                    .addToBackStack(null)
                    .commit();
        });

        goToServicesList.setOnClickListener(v -> {
            shopId = viewModel.getShopId();

            if (shopId == null || shopId.isEmpty()) {
                Toast.makeText(requireContext(),
                        "Please register your shop first",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            ServicesListBottomSheetFragment servicesListBottomSheetFragment = ServicesListBottomSheetFragment.newInstance(serviceDetailList, shopId);
            servicesListBottomSheetFragment.show(getParentFragmentManager(), "ServiceListBottomSheet");
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
        viewModel.getAuthState().observe(getViewLifecycleOwner(), authState -> {
            if (authState != null && !authState.getSuccess()) {
                LoaderManager.hide(this);
                // Toast.makeText(requireContext(), authState.getMessage(), Toast.LENGTH_SHORT).show();
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

        viewModel.getCurrentMechanicDetails().observe(getViewLifecycleOwner(), details -> {
            if (details == null) {

                LoaderManager.hide(this);

                return;
            }

            LoaderManager.hide(this);

            currentUser =
                    details.getMechanicDetails();

            shopDetail =
                    details.getShopDetail();

            serviceDetailList =
                    details.getServiceDetail();

            /*
             * NULL CHECK
             */
            if (currentUser == null) {

                LoaderManager.hide(this);

                Toast.makeText(
                        requireContext(),
                        "Failed to load mechanic details",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            mechanicId =
                    currentUser.getId();


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

                if (shopDetail != null) {

                    tvShopName.setText(
                            shopDetail.getShopName()
                    );

                    Double rating =
                            shopDetail.getRating();

                    ratingBar.setRating(

                            rating != null

                                    ? rating.floatValue()

                                    : 0f
                    );

                    Integer ratingCount =
                            shopDetail.getRatingCount();

                    tvRatingCount.setText(

                            "(" +

                                    (ratingCount != null
                                            ? ratingCount
                                            : 0)

                                    + ")"
                    );

                } else {

                    tvShopName.setText(
                            "Not Registered"
                    );
                }

                String profileImage = shopDetail.getShopImage();

                if (profileImage != null && !profileImage.isEmpty()) {

                    Glide.with(requireContext())
                            .load(profileImage)
                            .placeholder(R.drawable.profile)
                            .error(R.drawable.profile)
                            .into(shopImage);
                }

                goToRegisterShop.setClickable(false);
                goToRegisterShop.setFocusable(false);
                ivGoToRegisterShopArrow.setVisibility(View.GONE);
            } else {
                goToRegisterShop.setClickable(true);
                goToRegisterShop.setFocusable(true);
                ivGoToRegisterShopArrow.setVisibility(View.VISIBLE);
                tvShopName.setText("Not Registered");
                tvShopId.setText("Not Registered");
                LoaderManager.hide(this);
            }
        });
    }
}