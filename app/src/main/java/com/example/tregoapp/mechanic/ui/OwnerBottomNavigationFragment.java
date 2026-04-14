package com.example.tregoapp.mechanic.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tregoapp.R;
import com.example.tregoapp.mechanic.utils.LoadFragment;


public class OwnerBottomNavigationFragment extends Fragment {

    private LinearLayout homeBtn, historyBtn, workersBtn, profileBtn;

    private ImageView homeIcon, historyIcon, workersIcon, profileIcon;
    private TextView homeText, historyText, workersText, profileText;

    private static String selectedTab = "home";
    private FragmentManager.OnBackStackChangedListener backStackListener;


    public OwnerBottomNavigationFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mechanic_bottom_navigation, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        homeBtn = view.findViewById(R.id.homeBtn);
        historyBtn = view.findViewById(R.id.historyBtn);
        workersBtn = view.findViewById(R.id.workerBtn);
        profileBtn = view.findViewById(R.id.profileBtn);

        homeIcon = (ImageView) homeBtn.getChildAt(0);
        homeText = (TextView) homeBtn.getChildAt(1);

        historyIcon = (ImageView) historyBtn.getChildAt(0);
        historyText = (TextView) historyBtn.getChildAt(1);

        workersIcon = (ImageView) workersBtn.getChildAt(0);
        workersText = (TextView) workersBtn.getChildAt(1);

        profileIcon = (ImageView) profileBtn.getChildAt(0);
        profileText = (TextView) profileBtn.getChildAt(1);

        setActiveTab(selectedTab);

        backStackListener = () -> {
            Fragment current = requireActivity()
                    .getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_container);

            if (current instanceof DashboardFragment) {
                setActiveTab("home");
                selectedTab = "home";
            } else if (current instanceof OwnerHistoryFragment) {
                setActiveTab("history");
                selectedTab = "history";
            } else if (current instanceof WorkersFragment) {
                setActiveTab("workers");
                selectedTab = "workers";
            } else if (current instanceof ProfileFragment) {
                setActiveTab("profile");
                selectedTab = "profile";
            }
        };

        requireActivity().getSupportFragmentManager()
                .addOnBackStackChangedListener(backStackListener);

        homeBtn.setOnClickListener(v -> {
            switchTab("home", new DashboardFragment());
        });

        historyBtn.setOnClickListener(v -> {
            switchTab("history", new OwnerHistoryFragment());
        });

        workersBtn.setOnClickListener(v -> {
            switchTab("workers", new WorkersFragment());
        });

        profileBtn.setOnClickListener(v -> {
            switchTab("profile", new ProfileFragment());
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        requireActivity().getSupportFragmentManager()
                .removeOnBackStackChangedListener(backStackListener);
    }

    private void switchTab(String tab, Fragment fragment) {
        selectedTab = tab;
        setActiveTab(tab);

        LoadFragment.replaceFragmentFromFragment(
                this,
                R.id.fragment_container,
                fragment
        );
    }

    private void setActiveTab(String tab) {

        homeIcon.setImageResource(R.drawable.home_inactive);
        historyIcon.setImageResource(R.drawable.history_inactive);
        workersIcon.setImageResource(R.drawable.worker_inactive);
        profileIcon.setImageResource(R.drawable.profile_inactive);

        int grey = getResources().getColor(R.color.text_grey);
        int black = getResources().getColor(R.color.black);

        homeText.setTextColor(grey);
        historyText.setTextColor(grey);
        workersText.setTextColor(grey);
        profileText.setTextColor(grey);

        switch (tab) {
            case "home":
                homeIcon.setImageResource(R.drawable.home_active);
                homeText.setTextColor(black);
                break;

            case "history":
                historyIcon.setImageResource(R.drawable.history_active);
                historyText.setTextColor(black);
                break;

            case "workers":
                workersIcon.setImageResource(R.drawable.worker_active);
                workersText.setTextColor(black);
                break;

            case "profile":
                profileIcon.setImageResource(R.drawable.profile_active);
                profileText.setTextColor(black);
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tab", selectedTab);
    }
}