package com.example.tregoapp.mechanic.utils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.tregoapp.R;

public class LoaderManager {

    private static ObjectAnimator animator; // prevent multiple animations

    // =========================
    // 🔹 SHOW (Activity)
    // =========================
    public static void show(Activity activity) {
        if (activity == null || activity.isFinishing()) return;

        View loader = activity.findViewById(R.id.globalLoader);
        if (loader == null) return;

        loader.bringToFront();
        loader.setVisibility(View.VISIBLE);

        TextView text = loader.findViewById(R.id.textView);
        if (text == null) return;

        // Prevent multiple animators
        if (animator != null && animator.isRunning()) return;

        animator = ObjectAnimator.ofFloat(text, "alpha", 0.3f, 1f);
        animator.setDuration(800);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.start();
    }

    // =========================
    // 🔹 HIDE (Activity)
    // =========================
    public static void hide(Activity activity) {
        if (activity == null || activity.isFinishing()) return;

        View loader = activity.findViewById(R.id.globalLoader);
        if (loader == null) return;

        loader.setVisibility(View.GONE);

        // Stop animation
        if (animator != null) {
            animator.cancel();
            animator = null;
        }
    }

    // =========================
    // 🔹 SAFE SHOW (Fragment)
    // =========================
    public static void show(Fragment fragment) {
        if (fragment == null || !fragment.isAdded()) return;

        Activity activity = fragment.getActivity();
        if (activity != null) {
            show(activity);
        }
    }

    // =========================
    // 🔹 SAFE HIDE (Fragment)
    // =========================
    public static void hide(Fragment fragment) {
        if (fragment == null || !fragment.isAdded()) return;

        Activity activity = fragment.getActivity();
        if (activity != null) {
            hide(activity);
        }
    }
}