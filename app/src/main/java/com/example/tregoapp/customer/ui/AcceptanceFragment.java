package com.example.tregoapp.customer.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tregoapp.R;

public class AcceptanceFragment extends Fragment {

    private static final String SERVICE_REQUEST_ID = "param1";
    private String serviceRequestId;
    private Handler handler = new Handler();

    public AcceptanceFragment() {
        // Required empty public constructor
    }

    public static AcceptanceFragment newInstance(String serviceRequestId) {
        AcceptanceFragment fragment = new AcceptanceFragment();
        Bundle args = new Bundle();
        args.putString(SERVICE_REQUEST_ID, serviceRequestId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            serviceRequestId = getArguments().getString(SERVICE_REQUEST_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_acceptance, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        handler.postDelayed(() -> {
            CustomerSideTrackingFragment customerSideTrackingFragment = CustomerSideTrackingFragment.newInstance(serviceRequestId);

            getParentFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.fragment_container, customerSideTrackingFragment)
                    .commit();

        }, 4000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }
}