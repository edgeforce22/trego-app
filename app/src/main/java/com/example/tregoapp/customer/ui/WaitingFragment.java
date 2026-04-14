package com.example.tregoapp.customer.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tregoapp.R;
import com.example.tregoapp.customer.navigation.NavigationHelper;
import com.example.tregoapp.customer.network.Resource;
import com.example.tregoapp.customer.viewmodel.ViewModel;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;


public class WaitingFragment extends Fragment {


    private static final String REQUEST_ID = "request_id";
    private String request_id;

    private TextView tvTimer;
    private TextView tvStatus;
    private TextView tvServiceId;

    private ViewModel viewModel;

    private static final long TIME_LIMIT = 3 * 60 * 1000;
//    private static final long TIME_LIMIT = 1 * 10 * 1000;
    private long timeRemaining = TIME_LIMIT;
    private Handler timerHandler;
    private Runnable timerRunnable;
    private Handler handler;
    private Runnable runnable;

    public WaitingFragment() {
        // Required empty public constructor
    }

    public static WaitingFragment newInstance(String requestId) {
        WaitingFragment fragment = new WaitingFragment();
        Bundle args = new Bundle();
        args.putString(REQUEST_ID, requestId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            request_id = getArguments().getString(REQUEST_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_waiting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstance) {
        tvTimer = view.findViewById(R.id.tvTimer);
        tvStatus = view.findViewById(R.id.tvStatus);
//        tvServiceId = view.findViewById(R.id.tvServiceId);

        tvStatus.setText("Request sent successfully");

        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        viewModelObserver();

        startPolling();
        startTimer();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }

        if (timerHandler != null && timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }
    }

    private void viewModelObserver() {
        viewModel.getServiceRequestResource().observe(getViewLifecycleOwner(), resource -> {
            if (resource.getStatus() == Resource.Status.SUCCESS) {
                var service = resource.getData();
                if (service == null) return;

                Log.d("SERVICE DATA", service.toString());

                if ("accepted".equalsIgnoreCase(service.getStatus()) || "in_progress".equalsIgnoreCase(service.getStatus())) {
                    stopPolling();
                    NavigationHelper.navigateTo(getParentFragmentManager(), AcceptanceFragment.newInstance(request_id), false);
                } else if ("cancelled".equalsIgnoreCase(service.getStatus())) {
                    stopPolling();
                    Toast.makeText(requireContext(), "Request cancelled by Mechanic", Toast.LENGTH_SHORT).show();
                    NavigationHelper.clearBackStackAndNavigate(getParentFragmentManager(), new DashboardFragment());
                }
            } else if (resource.getStatus() == Resource.Status.ERROR) {
                Toast.makeText(requireContext(), resource.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startPolling() {
        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                if (request_id != null) {
                    viewModel.getServiceRequest(request_id);
                }
                handler.postDelayed(this, 10000);
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    private void startTimer() {

        timerHandler = new Handler();

        timerRunnable = new Runnable() {
            @Override
            public void run() {

                if (timeRemaining <= 0) {

                    // stop polling
                    if (handler != null) {
                        handler.removeCallbacks(runnable);
                    }

                    cancelRequest();

                    return;
                }

                int minutes = (int) (timeRemaining / 1000) / 60;
                int seconds = (int) (timeRemaining / 1000) % 60;

                String time = String.format("%02d:%02d", minutes, seconds);
                tvTimer.setText(time);

                timeRemaining -= 1000;

                timerHandler.postDelayed(this, 1000);
            }
        };

        timerHandler.post(timerRunnable);
    }

    private void cancelRequest() {
        Toast.makeText(requireContext(), "Request cancelled", Toast.LENGTH_SHORT).show();
        viewModel.cancelRequestedService(request_id);
        NavigationHelper.clearBackStackAndNavigate(getParentFragmentManager(), new DashboardFragment());
    }

    private void stopPolling() {

        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }

        if (timerHandler != null && timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }
    }
}