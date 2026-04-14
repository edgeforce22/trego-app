package com.example.tregoapp.mechanic.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tregoapp.R;
import com.example.tregoapp.mechanic.utils.LoaderManager;
import com.example.tregoapp.mechanic.model.response.User;
import com.example.tregoapp.mechanic.ui.worker.WorkerDashboardFragment;
import com.example.tregoapp.mechanic.ui.worker.WorkerShopRegFragment;
import com.example.tregoapp.mechanic.utils.DeviceLocationHelper;
import com.example.tregoapp.mechanic.viewmodel.ViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegistrationFragment extends Fragment {

    private final static String[] roles = { "Owner", "Worker" };
    private String userId;
    private String role;
    private ArrayAdapter<String> roleAdapter;
    private TextInputEditText etName;
    private TextInputEditText etPhoneNumber;
    private AutoCompleteTextView etRole;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    private TextView goToSignInBtn;
    private MaterialButton signUpBtn;
    private String address;
    private double latitude, longitude;
    private boolean isLocationFetched = false;
    private ViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mechanic_registration, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        goToSignInBtn = view.findViewById(R.id.goToSignInBtn);
        etName = view.findViewById(R.id.etName);
        etPhoneNumber = view.findViewById(R.id.etPhoneNumber);
        etRole = view.findViewById(R.id.etRole);
        etPassword = view.findViewById(R.id.etPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        signUpBtn = view.findViewById(R.id.signUpBtn);

        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        viewModelObserver();

        // Role Adapter setup
        roleAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                roles
        );
        etRole.setAdapter(roleAdapter);

        goToSignInBtn.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new LoginFragment())
                    .addToBackStack(null)
                    .commit();
        });

        signUpBtn.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phoneNumber = etPhoneNumber.getText().toString().trim();
            String role = etRole.getText().toString().trim().toLowerCase();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            this.role = role;

            if (!validateRegisterCredentials(name, phoneNumber, role, password, confirmPassword)) {
                return;
            }
//
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
                    viewModel.register(
                            name,
                            phoneNumber,
                            role,
                            address,
                            password,
                            latitude,
                            longitude
                    );

//                    Toast.makeText(requireContext(),
//                            "Location fetched successfully",
//                            Toast.LENGTH_SHORT).show();
//
//                    signUpBtn.setText("Sign Up");
                } else {
                    Toast.makeText(requireContext(),
                            "Location fetched failed",
                            Toast.LENGTH_SHORT).show();
                }
            });
//            }
//
//            // STEP 2: Register
//            else {

//            }
        });

    }

    private boolean validateRegisterCredentials(String name, String phoneNumber, String role, String password, String confirmPassword) {
        if (name.isEmpty() &&
                phoneNumber.isEmpty() &&
                role.isEmpty() &&
                password.isEmpty() &&
                confirmPassword.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter all the fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter the name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (phoneNumber.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter the phone number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (role.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter the role", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter the password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6) {
            Toast.makeText(requireContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (confirmPassword.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter the confirm password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(requireContext(), "Confirm password is not same", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!phoneNumber.matches("\\d{10}")) {
            Toast.makeText(requireContext(), "Enter valid phone number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void viewModelObserver() {

        viewModel.getAuthState().observe(getViewLifecycleOwner(), authState -> {
            if (authState == null) return;

            Toast.makeText(getContext(), authState.getMessage(), Toast.LENGTH_SHORT).show();
            LoaderManager.hide(this);
        });

        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), currentUser -> {
            if (currentUser == null) return;

            String userId = currentUser.getId();
            String role = currentUser.getRole();

            LoaderManager.hide(this);
            navigate(userId, role);
        });
    }

    private void navigate(String userId, String role) {
        getParentFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        if ("owner".equalsIgnoreCase(role)) {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, ShopRegistrationFragment.newInstance(userId))
                    .commit();
        } else if ("worker".equalsIgnoreCase(role)) {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, WorkerShopRegFragment.newInstance(userId))
                    .commit();
        }
    }
}