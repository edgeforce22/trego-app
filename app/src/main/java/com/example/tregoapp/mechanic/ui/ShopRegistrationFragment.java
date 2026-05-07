package com.example.tregoapp.mechanic.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tregoapp.R;
import com.example.tregoapp.customer.utils.LoaderManager;
import com.example.tregoapp.mechanic.utils.DeviceLocationHelper;
import com.example.tregoapp.mechanic.utils.FileUtils;
import com.example.tregoapp.mechanic.viewmodel.ViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ShopRegistrationFragment extends Fragment {

    private static final String OWNER_ID = "owner_id";
    private String owner_id;
    private String shopId;

    private TextInputEditText etShopName;
    private TextInputEditText etShopContactNumber;
    private TextInputEditText etShopOpeningTime;
    private TextInputEditText etShopClosingTime;
    private ChipGroup chipVehicleGroup;
    private MaterialButton registerShopBtn;
    private String address;
    private double latitude, longitude;
    private boolean isLocationFetched = false;
    private ViewModel viewModel;


    private ImageView ivShopImage;
    private FrameLayout  btnChooseShopImage;
    private LinearLayout layoutImagePlaceholder;

    private Uri selectedImageUri;
    private String selectedImagePath;

    private static final int PICK_IMAGE_CODE = 100;

    public ShopRegistrationFragment() {
        // Required empty public constructor
    }

    public static ShopRegistrationFragment newInstance(String ownerId) {
        ShopRegistrationFragment fragment = new ShopRegistrationFragment();
        Bundle args = new Bundle();
        args.putString(OWNER_ID, ownerId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            owner_id = getArguments().getString(OWNER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shop_registration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        etShopName = view.findViewById(R.id.etShopName);
        etShopContactNumber = view.findViewById(R.id.etShopContactNumber);
        etShopOpeningTime = view.findViewById(R.id.etOpeningTime);
        etShopClosingTime = view.findViewById(R.id.etClosingTime);
        chipVehicleGroup = view.findViewById(R.id.chipVehicleGroup);
        registerShopBtn = view.findViewById(R.id.registerShopBtn);
        ivShopImage = view.findViewById(R.id.ivShopImage);
        btnChooseShopImage = view.findViewById(R.id.btnChooseShopImage);
        layoutImagePlaceholder =
                view.findViewById(
                        R.id.layoutImagePlaceholder
                );

        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        owner_id = viewModel.getUserId();
        viewModelObserver();

        btnChooseShopImage.setOnClickListener(v -> {

            Intent intent =
                    new Intent(Intent.ACTION_PICK);

            intent.setType("image/*");

            startActivityForResult(
                    intent,
                    PICK_IMAGE_CODE
            );
        });


        // Opening Time and Closing Time Picker Setup Start
        etShopOpeningTime.setOnClickListener(v -> {

            MaterialTimePicker picker =
                    new MaterialTimePicker.Builder()
                            .setTimeFormat(TimeFormat.CLOCK_12H)
                            .setTitleText("Select Opening Time")
                            .build();

            picker.show(getParentFragmentManager(), "OPEN_TIME");

            picker.addOnPositiveButtonClickListener(view1 -> {

                int hour = picker.getHour();
                int minute = picker.getMinute();

                String time = String.format("%02d:%02d", hour, minute);

                etShopOpeningTime.setText(time);
            });
        });

        etShopClosingTime.setOnClickListener(v -> {

            MaterialTimePicker picker =
                    new MaterialTimePicker.Builder()
                            .setTimeFormat(TimeFormat.CLOCK_12H)
                            .setTitleText("Select Closing Time")
                            .build();

            picker.show(getParentFragmentManager(), "CLOSE_TIME");

            picker.addOnPositiveButtonClickListener(view1 -> {

                int hour = picker.getHour();
                int minute = picker.getMinute();

                String time = String.format("%02d:%02d", hour, minute);

                etShopClosingTime.setText(time);
            });
        });
        // Time Picker Setup End

        registerShopBtn.setOnClickListener(v -> {
            String shopName = etShopName.getText().toString().trim();
            String shopContactNumber = etShopContactNumber.getText().toString().trim();
            String shopOpeningTime = etShopOpeningTime.getText().toString().trim();
            String shopClosingTime = etShopClosingTime.getText().toString().trim();
            List<String> supportedVehicles = getSelectedVehicleTypes();

            if (!validateData(shopName, shopContactNumber, shopOpeningTime, shopClosingTime, supportedVehicles)) {
                return;
            }

//            // STEP 1: Fetch Location
//            if (!isLocationFetched) {

            DeviceLocationHelper helper =
                    new DeviceLocationHelper(requireContext());

            helper.getCurrentLocation(requireContext(), (lat, lon, address) -> {

                this.latitude = lat;
                this.longitude = lon;
                this.address = address;

                if (!address.equals("Address not found")) {
                    isLocationFetched = true;

                    LoaderManager.show(this);
                    viewModel.registerShop(
                            selectedImagePath,
                            owner_id,
                            shopName,
                            shopContactNumber,
                            address,
                            latitude,
                            longitude,
                            shopOpeningTime,
                            shopClosingTime,
                            supportedVehicles
                    );

//                    Toast.makeText(requireContext(),
//                            "Location fetched successfully",
//                            Toast.LENGTH_SHORT).show();
//
//                    registerShopBtn.setText("Register Shop");
                } else {
                    // Toast.makeText(requireContext(),
                    //         "Location fetched failed",
                    //         Toast.LENGTH_SHORT).show();
                }
            });
//            }

//            // STEP 2: Register
//            else {


//            }
        });
    }

    @Override
    public void onActivityResult(

            int requestCode,

            int resultCode,

            @Nullable Intent data
    ) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (
                requestCode == PICK_IMAGE_CODE
                        &&
                        resultCode == getActivity().RESULT_OK
                        &&
                        data != null
        ) {

            selectedImageUri =
                    data.getData();

            /*
             * SHOW IMAGE
             */
            ivShopImage.setVisibility(
                    View.VISIBLE
            );

            /*
             * HIDE PLACEHOLDER
             */
            layoutImagePlaceholder.setVisibility(
                    View.GONE
            );

            Glide.with(requireContext())
                    .load(selectedImageUri)
                    .into(ivShopImage);

            /*
             * GET FILE PATH
             */
            selectedImagePath =
                    FileUtils.getPath(
                            requireContext(),
                            selectedImageUri
                    );
        }
    }

    private boolean validateData(String shopName, String shopContactNumber, String shopOpeningTime, String shopClosingTime, List<String> supportedVehicles) {
        if (shopName.isEmpty() &&
        shopContactNumber.isEmpty() &&
        shopOpeningTime.isEmpty() &&
        shopClosingTime.isEmpty()) {
             Toast.makeText(requireContext(), "Please enter all the required field", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (shopName.isEmpty()) {
             Toast.makeText(requireContext(), "Please enter the shop name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (shopContactNumber.isEmpty()) {
             Toast.makeText(requireContext(), "Please enter the shop contact number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (shopOpeningTime.isEmpty()) {
             Toast.makeText(requireContext(), "Please enter the shop opening time", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (shopClosingTime.isEmpty()) {
             Toast.makeText(requireContext(), "Please enter the shop closing time", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (supportedVehicles.isEmpty()) {

            Toast.makeText(
                    requireContext(),
                    "Select at least one vehicle type",
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }
        if (selectedImagePath == null) {

            Toast.makeText(
                    requireContext(),
                    "Please choose shop image",
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }
        return true;
    }

    private void viewModelObserver() {
        viewModel.getAuthState().observe(getViewLifecycleOwner(), authState -> {
            if (authState == null) {
                return;
            }
            LoaderManager.hide(this);
            // Toast.makeText(getContext(), authState.getMessage(), Toast.LENGTH_SHORT).show();
        });

        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), currentUser -> {
            if (currentUser == null) {
                return;
            }
        });

        viewModel.getShopDetail().observe(getViewLifecycleOwner(), shop -> {
            if (shop == null) {
                return;
            }
            shopId = shop.getShopId();

            LoaderManager.hide(this);
            CreateServiceFragment createServiceFragment = CreateServiceFragment.newInstance(shopId);
            getParentFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, createServiceFragment)
                    .commit();
        });
    }

    private List<String> getSelectedVehicleTypes() {

        List<String> selectedVehicles =
                new ArrayList<>();

        for (int i = 0;
             i < chipVehicleGroup.getChildCount();
             i++) {

            View view =
                    chipVehicleGroup.getChildAt(i);

            if (view instanceof Chip) {

                Chip chip = (Chip) view;

                if (chip.isChecked()) {

                    selectedVehicles.add(
                            chip.getText().toString()
                    );
                }
            }
        }

        return selectedVehicles;
    }
}