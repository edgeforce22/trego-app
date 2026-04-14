package com.example.tregoapp.customer.utils;

import android.view.View;
import android.widget.TextView;

import com.example.tregoapp.R;

public class EmptyStateHelper {

    public static void show(View rootView, String message, View... viewsToHide) {

        View emptyView = rootView.findViewById(R.id.emptyView);
        if (emptyView == null) return; // 🔥 FIX

//        ImageView img = emptyView.findViewById(R.id.imgEmpty);
        TextView tv = emptyView.findViewById(R.id.tvEmptyText);

//        img.setImageResource(imageRes);
        tv.setText(message);

        emptyView.setVisibility(View.VISIBLE);

        if (viewsToHide != null) {
            for (View v : viewsToHide) {
                if (v != null) v.setVisibility(View.GONE);
            }
        }
    }

    public static void hide(
            View rootView,
            View... viewsToShow
    ) {
        View emptyView = rootView.findViewById(R.id.emptyView);

        // Hide empty view
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }

        // Show required views
        if (viewsToShow != null) {
            for (View v : viewsToShow) {
                if (v != null) v.setVisibility(View.VISIBLE);
            }
        }
    }
}