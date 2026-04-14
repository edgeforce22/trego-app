package com.example.tregoapp.mechanic.utils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class LoadFragment {

    // ✅ Replace Fragment (Activity level)
    public static void replaceFragment(AppCompatActivity activity, int containerId, Fragment fragment) {
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commit();
    }

    // ✅ Replace Fragment (without backstack)
    public static void replaceFragmentNoBack(AppCompatActivity activity, int containerId, Fragment fragment) {
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(containerId, fragment)
                .commit();
    }

    // ✅ Replace Fragment from another Fragment
    public static void replaceFragmentFromFragment(Fragment parent, int containerId, Fragment fragment) {
        parent.requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commit();
    }

    // ✅ Replace Fragment from another Fragment No Back track
    public static void replaceFragmentFromFragmentNoBackTrack(Fragment parent, int containerId, Fragment fragment) {
        parent.requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(containerId, fragment)
                .commit();
    }

    // ✅ Child Fragment (inside fragment layout)
    public static void replaceChildFragment(Fragment parent, int containerId, Fragment fragment) {
        parent.getChildFragmentManager()
                .beginTransaction()
                .replace(containerId, fragment)
                .commit();
    }
}