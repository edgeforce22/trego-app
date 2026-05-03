package com.example.tregoapp.customer.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.drawable.ColorDrawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;

import com.example.tregoapp.R;
import com.example.tregoapp.customer.model.ShopDetail;
import com.example.tregoapp.customer.ui.CreateRequestFragment;

public class ShopDetailDialogFragment extends DialogFragment {

    private static final String DATA = "shop_data";

    private ShopDetail shop;

    public static ShopDetailDialogFragment newInstance(ShopDetail shop) {
        ShopDetailDialogFragment fragment = new ShopDetailDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(DATA, shop);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getTheme() {
        return androidx.appcompat.R.style.Theme_AppCompat_Dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shop_detail_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            shop = (ShopDetail) getArguments().getSerializable(DATA);
        }

        AppCompatTextView tvShopName = view.findViewById(R.id.tvShopName);
//        AppCompatTextView tvPhone = view.findViewById(R.id.tvPhone);
        AppCompatTextView tvAddress = view.findViewById(R.id.tvAddress);
        AppCompatTextView tvRating = view.findViewById(R.id.tvRating);
        AppCompatTextView tvDistance = view.findViewById(R.id.tvDistance);
        AppCompatTextView tvDuration = view.findViewById(R.id.tvDuration);
        AppCompatTextView tvOpen = view.findViewById(R.id.tvOpen);
        AppCompatTextView tvClose = view.findViewById(R.id.tvClose);

        if (shop != null) {
            tvShopName.setText(shop.getShopName());
//            tvPhone.setText(shop.getPhoneNumber());
            tvAddress.setText(shop.getAddress());
            tvRating.setText(String.valueOf(shop.getRating()));
            tvDistance.setText(shop.getDistance() + " km");
            tvDuration.setText(shop.getEstimatedTime() + " min");
            tvOpen.setText(shop.getOpeningTime());
            tvClose.setText(shop.getClosingTime());
        }

//        view.findViewById(R.id.btnSelect).setOnClickListener(v -> dismiss());

        view.findViewById(R.id.btnSelect).setOnClickListener(v -> {
            CreateRequestFragment createRequestFragment = CreateRequestFragment.newInstance(shop);
            com.example.tregoapp.customer.navigation.NavigationHelper.navigateTo(getParentFragmentManager(), createRequestFragment);
            dismiss();
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() != null && getDialog().getWindow() != null) {

            getDialog().getWindow().setLayout((int) (getResources().getDisplayMetrics().widthPixels * 0.93), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}