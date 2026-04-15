package com.example.tregoapp.mechanic.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tregoapp.R;
import com.example.tregoapp.mechanic.utils.LoaderManager;
import com.example.tregoapp.mechanic.viewmodel.ViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class CreateServiceFragment extends Fragment {

    private static final String SHOP_ID = "shop_id";
    private String shopId;

    private TextInputEditText etService;
    private TextInputEditText etServiceDescription;
    private TextInputEditText etServicePrice;
    private MaterialButton createServiceBtn;

    private ViewModel viewModel;

    public CreateServiceFragment() {
        // Required empty public constructor
    }

    public static CreateServiceFragment newInstance(String shopId) {
        CreateServiceFragment fragment = new CreateServiceFragment();
        Bundle args = new Bundle();
        args.putString(SHOP_ID, shopId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            shopId = getArguments().getString(SHOP_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_service, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        etService = view.findViewById(R.id.etService);
        etServiceDescription = view.findViewById(R.id.etServiceDescription);
        etServicePrice = view.findViewById(R.id.etServicePrice);
        createServiceBtn = view.findViewById(R.id.createServiceBtn);


        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        shopId = viewModel.getShopId();
        viewModelObserver();

        createServiceBtn.setOnClickListener(v -> {
            String service = etService.getText().toString().trim();
            String serviceDescription = etServiceDescription.getText().toString().trim();
            String servicePrice = etServicePrice.getText().toString().trim();

            if (!validateData(service, serviceDescription, servicePrice)) {
                return;
            }

            LoaderManager.show(this);
            viewModel.createService(shopId, service, serviceDescription, Double.parseDouble(servicePrice));
        });
    }

    private boolean validateData(String service, String serviceDescription, String servicePrice) {
        if (service.isEmpty() &&
                serviceDescription.isEmpty() &&
                servicePrice.isEmpty()) {
            // Toast.makeText(requireContext(), "Please enter all the required field", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (service.isEmpty()) {
            // Toast.makeText(requireContext(), "Please enter the service", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (serviceDescription.isEmpty()) {
            // Toast.makeText(requireContext(), "Please enter the service description", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (servicePrice.isEmpty()) {
            // Toast.makeText(requireContext(), "Please enter the service price", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void viewModelObserver() {
        viewModel.getAuthState().observe(getViewLifecycleOwner(), authState -> {
            if (authState == null) {
                return;
            }
            // Toast.makeText(getContext(), authState.getMessage(), Toast.LENGTH_SHORT).show();

            LoaderManager.hide(this);
            if (authState.getSuccess()) {
                getParentFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new DashboardFragment())
                        .commit();
            }
        });
    }
}