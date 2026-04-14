package com.example.tregoapp.mechanic.ui;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tregoapp.mechanic.ui.worker.WorkerDashboardFragment;
import com.example.tregoapp.customer.navigation.NavigationHelper;
import com.example.tregoapp.mechanic.model.RequestCustomerModel;
import com.example.tregoapp.R;
import com.example.tregoapp.mechanic.model.ServiceRequest;
import com.example.tregoapp.mechanic.utils.LoaderManager;
import com.example.tregoapp.mechanic.viewmodel.ViewModel;

public class AcceptanceFragment extends Fragment {

    private static final String SERVICE_ID = "service_id";
    private String service_id;
    private Handler handler = new Handler();

    private ViewModel viewModel;

    public AcceptanceFragment() {
        // Required empty public constructor
    }

    public static AcceptanceFragment newInstance(String service_id) {
        AcceptanceFragment fragment = new AcceptanceFragment();
        Bundle args = new Bundle();
        args.putString(SERVICE_ID, service_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            service_id = getArguments().getString(SERVICE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mechanic_acceptance, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        LoaderManager.hide(this);
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        navigateToDashboard();
                    }
                }
        );

        handler.postDelayed(() -> {
            if (!isAdded()) return;
            viewModel.startServiceRequest(service_id);
            MechanicSideTrackingFragment mechanicSideTrackingFragment = MechanicSideTrackingFragment.newInstance(service_id);

            NavigationHelper.navigateTo(requireActivity().getSupportFragmentManager(), mechanicSideTrackingFragment, false);

        }, 5000);
    }

    private void navigateToDashboard() {
        if (!isAdded()) return;
        String role = viewModel.getRole();
        Fragment dashboardFragment;
        if ("worker".equalsIgnoreCase(role)) {
            dashboardFragment = new WorkerDashboardFragment();
        } else {
            dashboardFragment = new DashboardFragment();
        }
        NavigationHelper.clearBackStackAndNavigate(getParentFragmentManager(), dashboardFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }
}