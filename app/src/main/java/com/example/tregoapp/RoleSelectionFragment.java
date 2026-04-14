package com.example.tregoapp;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tregoapp.mechanic.ui.LoginFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class RoleSelectionFragment extends Fragment {
    private static String role = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_role_selection, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        MaterialCardView customerBtn = view.findViewById(R.id.customerBtn);
        MaterialCardView mechanicBtn = view.findViewById(R.id.mechanicBtn);
        ImageView cust_checked = view.findViewById(R.id.cust_checked);
        ImageView mech_checked = view.findViewById(R.id.mech_checked);

        MaterialButton continueBtn = view.findViewById(R.id.continueBtn);

        int primaryColor = ContextCompat.getColor(requireContext(), R.color.primary_color);

        customerBtn.setOnClickListener(v -> {
            cust_checked.setImageResource(R.drawable.checked);
            customerBtn.setStrokeColor(ColorStateList.valueOf(primaryColor));
            customerBtn.setStrokeWidth(4);
            role = "customer";

            mech_checked.setImageResource(R.drawable.unchecked);
            mechanicBtn.setStrokeColor(ColorStateList.valueOf(Color.TRANSPARENT));
            mechanicBtn.setStrokeWidth(0);
        });

        mechanicBtn.setOnClickListener(v -> {
            mech_checked.setImageResource(R.drawable.checked);
            mechanicBtn.setStrokeColor(ColorStateList.valueOf(primaryColor));
            mechanicBtn.setStrokeWidth(4);
            role = "mechanic";

            cust_checked.setImageResource(R.drawable.unchecked);
            customerBtn.setStrokeColor(ColorStateList.valueOf(Color.TRANSPARENT));
            customerBtn.setStrokeWidth(0);
        });

        continueBtn.setOnClickListener(v -> {
            if (role.isEmpty()) {
                Toast.makeText(requireContext(), "Please select the role", Toast.LENGTH_SHORT).show();
            }
            else if (role.equals("customer")) {
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new com.example.tregoapp.customer.ui.LoginFragment())
                        .addToBackStack(null)
                        .commit();
            }

            else if (role.equals("mechanic")) {
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new LoginFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        role = "";
    }
}