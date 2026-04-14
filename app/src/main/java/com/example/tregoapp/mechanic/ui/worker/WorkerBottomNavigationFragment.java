package com.example.tregoapp.mechanic.ui.worker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.tregoapp.R;
import com.example.tregoapp.mechanic.utils.LoadFragment;


public class WorkerBottomNavigationFragment extends Fragment {

    private LinearLayout homeBtn, historyBtn, profileBtn;

    private ImageView homeIcon, historyIcon, profileIcon;
    private TextView homeText, historyText, profileText;

    private static String selectedTab = "home";
    private FragmentManager.OnBackStackChangedListener backStackListener;


    public WorkerBottomNavigationFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_worker_bottom_navigation, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        homeBtn = view.findViewById(R.id.homeBtn);
        historyBtn = view.findViewById(R.id.historyBtn);
        profileBtn = view.findViewById(R.id.profileBtn);

        homeIcon = (ImageView) homeBtn.getChildAt(0);
        homeText = (TextView) homeBtn.getChildAt(1);

        historyIcon = (ImageView) historyBtn.getChildAt(0);
        historyText = (TextView) historyBtn.getChildAt(1);

        profileIcon = (ImageView) profileBtn.getChildAt(0);
        profileText = (TextView) profileBtn.getChildAt(1);

        setActiveTab(selectedTab);

        backStackListener = () -> {
            Fragment current = requireActivity()
                    .getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_container);

            if (current instanceof WorkerDashboardFragment) {
                setActiveTab("home");
                selectedTab = "home";
            } else if (current instanceof WorkerHistoryFragment) {
                setActiveTab("history");
                selectedTab = "history";
            } else if (current instanceof WorkerProfileFragment) {
                setActiveTab("profile");
                selectedTab = "profile";
            }
        };

        requireActivity().getSupportFragmentManager()
                .addOnBackStackChangedListener(backStackListener);

        homeBtn.setOnClickListener(v -> {
            switchTab("home", new WorkerDashboardFragment());
        });

        historyBtn.setOnClickListener(v -> {
            switchTab("history", new WorkerHistoryFragment());
        });

        profileBtn.setOnClickListener(v -> {
            switchTab("profile", new WorkerProfileFragment());
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
        profileIcon.setImageResource(R.drawable.profile_inactive);

        int grey = getResources().getColor(R.color.text_grey);
        int black = getResources().getColor(R.color.black);

        homeText.setTextColor(grey);
        historyText.setTextColor(grey);
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