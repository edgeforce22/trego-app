package com.example.tregoapp.mechanic.ui;

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
import com.example.tregoapp.mechanic.utils.LoaderManager;
import com.example.tregoapp.mechanic.ui.worker.WorkerDashboardFragment;
import com.example.tregoapp.mechanic.viewmodel.ViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginFragment extends Fragment {

    private TextInputEditText etPhoneNumber;
    private TextInputEditText etPassword;
    private ViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mechanic_login, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        TextView goToSignUpBtn = view.findViewById(R.id.goToSignUpBtn);
        etPhoneNumber = view.findViewById(R.id.etPhoneNumber);
        etPassword = view.findViewById(R.id.etPassword);
        MaterialButton signInBtn = view.findViewById(R.id.signInBtn);

        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        viewModelObserver();

        goToSignUpBtn.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new RegistrationFragment())
                    .addToBackStack(null)
                    .commit();
        });

        signInBtn.setOnClickListener(v -> {
            String phoneNumber = etPhoneNumber.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (validateLoginCredentials(phoneNumber, password)) {
                LoaderManager.show(this);
                viewModel.login(phoneNumber, password);
            }
        });
    }

    private boolean validateLoginCredentials(String phoneNumber, String password) {
        if (phoneNumber.isEmpty() && password.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter all the fields", Toast.LENGTH_SHORT).show();
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
        if (phoneNumber.length() != 10) {
            Toast.makeText(requireContext(), "Please enter the valid phone number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void viewModelObserver() {
        viewModel.getAuthState().observe(getViewLifecycleOwner(), authState -> {
            if (authState == null) {
                return;
            }
            Toast.makeText(getContext(), authState.getMessage(), Toast.LENGTH_SHORT).show();
            LoaderManager.hide(this);
        });

        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), currentUser -> {
            if (currentUser == null) {
                return;
            }

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
                    .replace(R.id.fragment_container, new DashboardFragment())
                    .commit();
        } else if ("worker".equalsIgnoreCase(role)) {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new WorkerDashboardFragment())
                    .commit();
        }
    }
}