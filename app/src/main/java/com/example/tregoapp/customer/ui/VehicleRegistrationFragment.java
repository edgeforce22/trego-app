package com.example.tregoapp.customer.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.tregoapp.R;
import com.example.tregoapp.customer.navigation.NavigationHelper;
import com.example.tregoapp.customer.network.Resource;
import com.example.tregoapp.customer.utils.LoaderManager;
import com.example.tregoapp.customer.viewmodel.ViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class VehicleRegistrationFragment extends Fragment {

    private final static String CUSTOMER_ID = "customer_id";
    private String customerId;
    private final static String[] vehicles = { "Bicycle", "Scooter", "Bike", "Car", "Heavy" };
    private ArrayAdapter<String> vehicleTypeAdapter;
    private AutoCompleteTextView etVehicleType;
    private TextInputEditText etVehicleBrand;
    private TextInputEditText etVehicleModel;
    private TextInputLayout registrationNoLayout;
    private TextInputEditText etRegistrationNumber;
    private MaterialButton registerVehicleBtn;
    private boolean registrationNumberReq;
    private ViewModel viewModel;

    public VehicleRegistrationFragment() {
    }

    public static VehicleRegistrationFragment newInstance(String customerId) {
        VehicleRegistrationFragment fragment = new VehicleRegistrationFragment();
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
        return inflater.inflate(R.layout.fragment_vehicle_registration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        etVehicleType = view.findViewById(R.id.etVehicleType);
        etVehicleBrand = view.findViewById(R.id.etVehicleBrand);
        etVehicleModel = view.findViewById(R.id.etVehicleModel);
        registrationNoLayout = view.findViewById(R.id.registrationNoLayout);
        etRegistrationNumber = view.findViewById(R.id.etRegistrationNo);
        etRegistrationNumber.setHint("TN00AA0000");
        registerVehicleBtn = view.findViewById(R.id.registerVehicleBtn);

        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        viewModelObserver();

        // Vehicle Type Dropdown setup
        vehicleTypeAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                vehicles
        );
        etVehicleType.setAdapter(vehicleTypeAdapter);
        etVehicleType.setOnItemClickListener((parent, dropdownView, position, id) -> {
            String selectedVehicle = parent.getItemAtPosition(position).toString().trim().toLowerCase();

            if (!selectedVehicle.equalsIgnoreCase("bicycle")) {
                registrationNoLayout.setVisibility(View.VISIBLE);
                registrationNumberReq = true;
            }
            else {
                registrationNoLayout.setVisibility(View.GONE);
                registrationNumberReq = false;
            }
        });

        registerVehicleBtn.setOnClickListener(v -> {
            String vehicleType = etVehicleType.getText().toString().trim().toLowerCase();
            String vehicleBrand = etVehicleBrand.getText().toString().trim();
            String vehicleModel = etVehicleModel.getText().toString().trim();
            String registrationNumber = etRegistrationNumber.getText().toString().trim();

            if (vehicleType.equalsIgnoreCase("bicycle")) {
                registrationNumber = "N/A";
            }
            if (validateVehicleData(vehicleType, vehicleBrand, vehicleModel, registrationNumber)) {
                LoaderManager.show(this);
                viewModel.registerVehicle(customerId, vehicleType, vehicleBrand, vehicleModel, registrationNumber);
            }
        });
    }

    private boolean validateVehicleData(String vehicleType, String vehicleBrand, String vehicleModel, String registrationNumber) {
        if (vehicleType.isEmpty()) {
            // Toast.makeText(requireContext(), "Please select the vehicle type", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (vehicleBrand.isEmpty()) {
            // Toast.makeText(requireContext(), "Please enter the vehicle brand", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (vehicleModel.isEmpty()) {
            // Toast.makeText(requireContext(), "Please enter the vehicle model", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (registrationNumberReq) {
            if (registrationNumber.isEmpty()) {
                // Toast.makeText(requireContext(), "Please enter the registration number", Toast.LENGTH_SHORT).show();
                return false;
            }
            // Regex for TN00AA0000 format: 2 letters, 2 digits, 2 letters, 4 digits
            String regExp = "^[A-Z]{2}\\d{2}[A-Z]{2}\\d{4}$";
            if (!registrationNumber.toUpperCase().matches(regExp)) {
                Toast.makeText(requireContext(), "Invalid registration number. Format: TN00AA0000", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void viewModelObserver() {
        viewModel.getVehicleRegistrationResource().observe(getViewLifecycleOwner(), resource -> {
            LoaderManager.handleResource(this, resource, vehicle -> {
                // Toast.makeText(requireContext(), "Vehicle Registered successfully", Toast.LENGTH_SHORT).show();
                NavigationHelper.clearBackStackAndNavigate(getParentFragmentManager(), new DashboardFragment());
            });
        });
    }
}