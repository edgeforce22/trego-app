package com.example.tregoapp.customer.dialog;

import static com.example.tregoapp.BuildConfig.BASE_URL;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.drawable.ColorDrawable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
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
        ImageView shopImage = view.findViewById(R.id.shopImage);
//        AppCompatTextView tvSupportedVehicles = view.findViewById(R.id.tvSupportedVehicles);

        if (shop != null) {
            tvShopName.setText(shop.getShopName());
//            tvPhone.setText(shop.getPhoneNumber());
            tvAddress.setText(shop.getAddress());
            tvRating.setText(String.valueOf(shop.getRating()));
            String vehicles = "";

            LinearLayout layoutSupportedVehicles =
                    view.findViewById(R.id.layoutSupportedVehicles);

            layoutSupportedVehicles.removeAllViews();

            if (shop.getSupportedVehicles() != null
                    && !shop.getSupportedVehicles().isEmpty()) {

                for (String vehicle :
                        shop.getSupportedVehicles()) {

                    TextView chip = new TextView(requireContext());

                    chip.setText(vehicle);

                    chip.setTextSize(12);

                    chip.setTextColor(
                            Color.parseColor("#111111")
                    );

                    chip.setPadding(
                            28,
                            12,
                            28,
                            12
                    );

                    LinearLayout.LayoutParams params =
                            new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );

                    params.setMargins(0, 0, 12, 0);

                    chip.setLayoutParams(params);

                    chip.setBackgroundResource(
                            R.drawable.shop_card_childs_bg
                    );

                    layoutSupportedVehicles.addView(chip);
                }

            } else {

                TextView empty = new TextView(requireContext());

                empty.setText("Not Available");
                empty.setGravity(Gravity.CENTER);

                empty.setTextSize(12);

                empty.setTextColor(
                        Color.parseColor("#777777")
                );

                layoutSupportedVehicles.addView(empty);
            }
            tvDistance.setText(shop.getDistance() + " km");
            tvDuration.setText(shop.getEstimatedTime() + " min");
            tvOpen.setText(shop.getOpeningTime());
            tvClose.setText(shop.getClosingTime());

            String imageUrl =
                    shop.getShopImage();

            Log.d(
                    "SHOP_IMAGE",
                    String.valueOf(imageUrl)
            );

            if (
                    imageUrl != null
                            &&
                            !imageUrl.isEmpty()
            ) {

                String finalUrl =
                        imageUrl.startsWith("http")
                                ? imageUrl
                                : BASE_URL + "/" + imageUrl;

                Log.d(
                        "FINAL_IMAGE_URL",
                        finalUrl
                );

                Glide.with(requireContext())

                        .load(finalUrl)

                        .placeholder(
                                R.drawable.shop
                        )

                        .error(
                                R.drawable.shop
                        )

                        .centerCrop()

                        .into(shopImage);

            } else {

                shopImage.setImageResource(
                        R.drawable.shop
                );
            }
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