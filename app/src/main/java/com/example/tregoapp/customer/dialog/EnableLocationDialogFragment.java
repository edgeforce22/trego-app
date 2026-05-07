package com.example.tregoapp.customer.dialog;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tregoapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class EnableLocationDialogFragment extends BottomSheetDialogFragment {

    public EnableLocationDialogFragment() {
    }

    public static EnableLocationDialogFragment newInstance() {
        return new EnableLocationDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_enable_location_dialog,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {

        View btnEnable = view.findViewById(R.id.btnEnableLocation);
        View btnCancel = view.findViewById(R.id.btnCancel);

        btnEnable.setOnClickListener(v -> {

            Intent intent =
                    new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

            startActivity(intent);
            dismiss();
        });

        btnCancel.setOnClickListener(v -> dismiss());
    }

    public static boolean isLocationEnabled(android.content.Context context) {

        LocationManager locationManager =
                (LocationManager) context.getSystemService(
                        android.content.Context.LOCATION_SERVICE
                );

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}