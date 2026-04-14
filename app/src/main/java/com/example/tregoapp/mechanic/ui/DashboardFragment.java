package com.example.tregoapp.mechanic.ui;

import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tregoapp.customer.navigation.NavigationHelper;
import com.example.tregoapp.mechanic.utils.DeviceLocationHelper;
import com.example.tregoapp.R;
import com.example.tregoapp.mechanic.utils.LoaderManager;
import com.example.tregoapp.mechanic.utils.LoadFragment;
import com.example.tregoapp.mechanic.viewmodel.ViewModel;
import com.google.android.material.card.MaterialCardView;

public class DashboardFragment extends Fragment {

    private String shopId;
    private boolean isActive = false;

    private double latitude;
    private double longitude;

    private TextView tvDashboardAddress;
    private TextView tvDashboardName;
    private TextView tvStatusTitle;
    private TextView tvStatusSubtitle;
    private MaterialCardView track;
    private MaterialCardView thumb;
    private MaterialCardView requestsBtn, activeBtn;
    private TextView requestsText, activeText;
    private TextView tvRequestsCount, tvActiveCount;

    private ViewModel viewModel;

    private Fragment currentFragment;
    private ServiceRequestedFragment serviceRequestedFragment;
    private ActiveServicesFragment activeServicesFragment;
    private LoadFragment loadFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_mechanic_dashboard, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        tvDashboardAddress = view.findViewById(R.id.tvDashboardAddress);
        tvDashboardName = view.findViewById(R.id.tvDashboardName);
        tvStatusTitle = view.findViewById(R.id.tvStatusTitle);
        tvStatusSubtitle = view.findViewById(R.id.tvStatusSubtitle);
        track = view.findViewById(R.id.switchTrack);
        thumb = view.findViewById(R.id.switchThumb);
        requestsBtn = view.findViewById(R.id.requestsBtn);
        activeBtn = view.findViewById(R.id.activeBtn);
//        earningsBtn = view.findViewById(R.id.earningsBtn);
        requestsText = view.findViewById(R.id.requestsText);
        activeText = view.findViewById(R.id.activeText);
        tvRequestsCount = view.findViewById(R.id.tvRequestsCount);
        tvActiveCount = view.findViewById(R.id.tvActiveCount);
//        earningsText = view.findViewById(R.id.earningsText);
//        earningsLine = view.findViewById(R.id.earningsLine);

        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
        viewModel.loadSavedUser();
        shopId = viewModel.getShopId();
        viewModelObserver();

        loadFragment = new LoadFragment();
        serviceRequestedFragment = ServiceRequestedFragment.newInstance(shopId);
        activeServicesFragment = ActiveServicesFragment.newInstance(shopId);

        setActiveTab(requestsText, requestsBtn);
        loadFragment.replaceChildFragment(this, R.id.dashboardCenterContainer, serviceRequestedFragment);
        loadFragment.replaceChildFragment(this, R.id.dashboardBottomContainer, new OwnerBottomNavigationFragment());

        // Get user location
        DeviceLocationHelper helper = new DeviceLocationHelper(requireContext());

        helper.getCurrentLocation(requireContext(), (lat, lon, address) -> {

            latitude = lat;
            longitude = lon;

            if (lat == 0 || lon == 0) {
                Toast.makeText(requireContext(),
                        "Location fetch failed", Toast.LENGTH_SHORT).show();
            }
        });

        // Toggle switch animation
        track.setOnClickListener(v -> toggleStatus());



        requestsBtn.setOnClickListener(v -> {
                setActiveTab(requestsText, requestsBtn);
                loadFragment.replaceChildFragment(this, R.id.dashboardCenterContainer, serviceRequestedFragment);
        });

        activeBtn.setOnClickListener(v -> {
                setActiveTab(activeText, activeBtn);
                loadFragment.replaceChildFragment(this, R.id.dashboardCenterContainer, activeServicesFragment);
        });

//        earningsBtn.setOnClickListener(v ->
//                setActiveTab(earningsText, earningsLine);
//        )};
    }

    private void toggleStatus() {
        if (track == null || thumb == null) return;
        isActive = !isActive;
        updateStatusUI(isActive, true);
        viewModel.updateStatus(viewModel.getUserId(), isActive ? "active" : "inactive");
    }

    private void setActiveTab(TextView activeTextView, View activeLineView) {

        TextView[] texts = {requestsText, activeText};
        View[] lines = {requestsBtn, activeBtn};

        for (int i = 0; i < texts.length; i++) {

            // Reset text color
            texts[i].setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.text_grey)
            );

            // Reset background
            if (lines[i] instanceof MaterialCardView) {
                ((MaterialCardView) lines[i]).setCardBackgroundColor(
                        Color.parseColor("#F5F5F5")
                );
            }
        }

        // Active text color
        activeTextView.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.black)
        );

        // Active background
        if (activeLineView instanceof MaterialCardView) {
            ((MaterialCardView) activeLineView).setCardBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.primary_color)
            );
        }

        // Animation (optional)
        activeLineView.setScaleX(0.95f);
        activeLineView.animate()
                .scaleX(1f)
                .setDuration(200)
                .start();
    }

    private void updateStatusUI(boolean active, boolean animate) {
        if (track == null || thumb == null) return;

        track.post(() -> {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) thumb.getLayoutParams();
            float moveDistance = track.getWidth() - thumb.getWidth() - params.leftMargin - params.rightMargin;

            if (animate) {
                thumb.animate()
                        .translationX(active ? moveDistance : 0)
                        .setDuration(250)
                        .setInterpolator(active ? new OvershootInterpolator() : new AccelerateDecelerateInterpolator())
                        .start();
            } else {
                thumb.setTranslationX(active ? moveDistance : 0);
            }

            track.setCardBackgroundColor(active ? Color.parseColor("#34C759") : Color.parseColor("#D6D6D6"));

            if (active) {
                tvStatusTitle.setText("ONLINE");
                tvStatusTitle.setTextColor(Color.parseColor("#15FF00"));
                tvStatusSubtitle.setText("Available");
            } else {
                tvStatusTitle.setText("OFFLINE");
                tvStatusTitle.setTextColor(Color.parseColor("#E05252"));
                tvStatusSubtitle.setText("Unavailable");
            }
        });
    }

    private void viewModelObserver() {

        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), currentUser -> {
            if (currentUser == null) return;

            tvDashboardAddress.setText(currentUser.getAddress());
            tvDashboardName.setText(currentUser.getName());

            boolean serverActive = "active".equalsIgnoreCase(currentUser.getStatus());
            if (serverActive != isActive || track.getTag() == null) {
                isActive = serverActive;
                track.setTag("initialized");
                updateStatusUI(isActive, false);
            }
        });

        viewModel.getShopServiceRequestsLiveData().observe(getViewLifecycleOwner(), services -> {
            if (services == null || services.isEmpty()) {
                tvRequestsCount.setVisibility(View.GONE);
            } else {
                tvRequestsCount.setVisibility(View.VISIBLE);
                tvRequestsCount.setText(String.valueOf(services.size()));
            }
        });

        viewModel.getActiveServiceRequestsLiveData().observe(getViewLifecycleOwner(), activeServices -> {
            if (activeServices == null || activeServices.isEmpty()) {
                tvActiveCount.setVisibility(View.GONE);
            } else {
                tvActiveCount.setVisibility(View.VISIBLE);
                tvActiveCount.setText(String.valueOf(activeServices.size()));
            }
        });
    }
}