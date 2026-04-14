package com.example.tregoapp.mechanic.ui.worker;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tregoapp.R;
import com.example.tregoapp.mechanic.utils.DeviceLocationHelper;
import com.example.tregoapp.mechanic.utils.LoaderManager;
import com.example.tregoapp.mechanic.viewmodel.ViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class WorkerShopRegFragment extends Fragment {

    private static final String WORKER_ID = "worker_id";
    private String worker_id;
    private String shopId;

    private TextInputEditText etShopId;
    private MaterialButton registerShopBtn;
    private String address;
    private double latitude, longitude;
    private boolean isLocationFetched = false;
    private ViewModel viewModel;

    public WorkerShopRegFragment() {
        // Required empty public constructor
    }

    public static WorkerShopRegFragment newInstance(String workerId) {
        WorkerShopRegFragment fragment = new WorkerShopRegFragment();
        Bundle args = new Bundle();
        args.putString(WORKER_ID, workerId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            worker_id = getArguments().getString(WORKER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_worker_shop_reg, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        etShopId = view.findViewById(R.id.etShopId);
        registerShopBtn = view.findViewById(R.id.registerShopBtn);

        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        worker_id = viewModel.getUserId();
        viewModelObserver();

        registerShopBtn.setOnClickListener(v -> {
            String shopId = etShopId.getText().toString().trim();

            if (!validateData(shopId)) {
                return;
            }

            LoaderManager.show(this);
            viewModel.workerShopRegister(worker_id, shopId);
        });
    }

    private boolean validateData(String shopId) {
        if (shopId.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter the shopId provided by your customer", Toast.LENGTH_SHORT).show();
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
            if (authState.getSuccess()) {
                getParentFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new WorkerDashboardFragment())
                        .commit();
            }
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
            LoaderManager.hide(this);
            shopId = shop.getShopId();
        });
    }
}