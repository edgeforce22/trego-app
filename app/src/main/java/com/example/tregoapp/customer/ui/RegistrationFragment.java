package com.example.tregoapp.customer.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tregoapp.R;
import com.example.tregoapp.customer.model.response.User;
import com.example.tregoapp.customer.navigation.NavigationHelper;
import com.example.tregoapp.customer.utils.DeviceLocationHelper;
import com.example.tregoapp.customer.utils.LoaderManager;
import com.example.tregoapp.customer.viewmodel.ViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegistrationFragment extends Fragment {

    private TextInputEditText etName;
    private TextInputEditText etPhoneNumber;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    private TextView goToSignInBtn;
    private MaterialButton signUpBtn;
    private String address;
    private double latitude, longitude;
    private boolean isLocationFetched = false;
    private ViewModel viewModel;
    private String customerId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customer_registration, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        goToSignInBtn = view.findViewById(R.id.goToSignInBtn);
        etName = view.findViewById(R.id.etName);
        etPhoneNumber = view.findViewById(R.id.etPhoneNumber);
        etPassword = view.findViewById(R.id.etPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        signUpBtn = view.findViewById(R.id.signUpBtn);

        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        viewModelObserver();

        goToSignInBtn.setOnClickListener(v -> {
            NavigationHelper.navigateTo(getParentFragmentManager(), new LoginFragment());
        });

        signUpBtn.setOnClickListener(v -> {

            String name = etName.getText().toString().trim();
            String phoneNumber = etPhoneNumber.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (!validateRegisterCredentials(name, phoneNumber, password, confirmPassword)) {
                return;
            }

//            // STEP 1: Fetch Location
//            if (!isLocationFetched) {

            LoaderManager.show(this);

            DeviceLocationHelper helper =
                    new DeviceLocationHelper(requireContext());

            helper.getCurrentLocation(requireContext(), (lat, lon, address) -> {

                this.latitude = lat;
                this.longitude = lon;
                this.address = address;

                if (!address.equals("Address not found")) {
                    isLocationFetched = true;



                    viewModel.register(
                            name,
                            phoneNumber,
                            this.address,
                            password,
                            latitude,
                            longitude
                    );
//                        Toast.makeText(requireContext(),
//                                "Location fetched successfully",
//                                Toast.LENGTH_SHORT).show();

//                        signUpBtn.setText("Sign Up");
                }
                else {
                    LoaderManager.hide(this);
                    Toast.makeText(requireContext(),
                            "Location fetched failed",
                            Toast.LENGTH_SHORT).show();
                }
            });

//            }
//            // STEP 2: Register
//            else {
//
//            }
        });

    }

    private boolean validateRegisterCredentials(String name, String phoneNumber, String password, String confirmPassword) {
        if (name.isEmpty() &&
                phoneNumber.isEmpty() &&
                password.isEmpty() &&
                confirmPassword.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter all the fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter the name", Toast.LENGTH_SHORT).show();
//            etName.setError("Please enter the name");
            return false;
        }
        if (phoneNumber.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter the phone number", Toast.LENGTH_SHORT).show();
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
        viewModel.getAuthResource().observe(getViewLifecycleOwner(), resource -> {
            LoaderManager.handleResource(this, resource, new LoaderManager.ResourceCallback<User>() {
                @Override
                public void onSuccess(User user) {
                    if (user != null) {
                        Toast.makeText(getContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
                        NavigationHelper.clearBackStackAndNavigate(
                                getParentFragmentManager(),
                                VehicleRegistrationFragment.newInstance(user.getId())
                        );
                    }
                }
            });
        });
    }
}