package com.example.tregoapp.customer.ui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.tregoapp.R;
import com.example.tregoapp.customer.navigation.NavigationHelper;
import com.example.tregoapp.customer.network.Resource;
import com.example.tregoapp.customer.viewmodel.ViewModel;
import com.google.android.material.button.MaterialButton;


public class ServiceCompletedFragment extends DialogFragment {

    private static final String REQUEST_ID = "request_id";
    private static final String SHOP_ID = "shop_id";
    private String requestId;
    private String shopId;

    private RatingBar ratingBar;
    private float selectedRating = 0;

    private MaterialButton yesBtn;

    private ViewModel viewModel;

    public ServiceCompletedFragment() {
        // Required empty public constructor
    }

    public static ServiceCompletedFragment newInstance(String requestId, String shopId) {
        ServiceCompletedFragment fragment = new ServiceCompletedFragment();
        Bundle args = new Bundle();
        args.putString(REQUEST_ID, requestId);
        args.putString(SHOP_ID, shopId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            requestId = getArguments().getString(REQUEST_ID);
            shopId = getArguments().getString(SHOP_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_completed_service, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ratingBar =
                view.findViewById(
                        R.id.ratingBar
                );
        yesBtn = view.findViewById(R.id.yesBtn);
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        viewModelObserver();

        ratingBar.setOnRatingBarChangeListener(
                (ratingBar, rating, fromUser) -> {

                    selectedRating = rating;
                }
        );

        yesBtn.setOnClickListener(v -> {

            if (selectedRating <= 0) {

                Toast.makeText(
                        getContext(),
                        "Please provide rating",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            yesBtn.setEnabled(false);

            String userId =
                    viewModel.getUserId();

            viewModel.rateShop(
                    shopId,
                    userId,
                    selectedRating
            );
            viewModel.confirmServiceCompletion(
                    requestId
            );
        });
    }

    private void viewModelObserver() {
        viewModel.getRateShopResource().observe(
                getViewLifecycleOwner(),
                resource -> {

                    if (
                            resource.getStatus()
                                    ==
                                    Resource.Status.SUCCESS
                    ) {

                        Toast.makeText(
                                getContext(),
                                "Thanks for your rating!",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                    else if (
                            resource.getStatus()
                                    ==
                                    Resource.Status.ERROR
                    ) {

                        if (!isRemoving()) {

                            yesBtn.setEnabled(true);
                        }

                        Toast.makeText(
                                getContext(),
                                resource.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );

        viewModel.getGenericActionResource().observe(getViewLifecycleOwner(), resource -> {
            if (resource.getStatus() == Resource.Status.SUCCESS) {
                Toast.makeText(getContext(), "Service Confirmed", Toast.LENGTH_SHORT).show();
                dismiss();
                NavigationHelper.clearBackStackAndNavigate(requireActivity().getSupportFragmentManager(), new DashboardFragment());
            } else if (resource.getStatus() == Resource.Status.ERROR) {
                Toast.makeText(getContext(), resource.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}