package com.example.tregoapp.mechanic.bottomsheet;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tregoapp.R;
import com.example.tregoapp.mechanic.model.PredefinedService;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddCustomServiceBottomSheetFragment
        extends BottomSheetDialogFragment {

    private TextInputEditText etServiceName;
    private TextInputEditText etDescription;
    private TextInputEditText etPrice;

    private MaterialButton btnAdd;

    public interface OnCustomServiceAdded {
        void onServiceAdded(
                PredefinedService service
        );
    }

    private final OnCustomServiceAdded listener;

    public AddCustomServiceBottomSheetFragment(
            OnCustomServiceAdded listener
    ) {

        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        return inflater.inflate(
                R.layout.fragment_add_custom_service_bottom_sheet,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {

        etServiceName =
                view.findViewById(
                        R.id.etServiceName
                );

        etDescription =
                view.findViewById(
                        R.id.etDescription
                );

        etPrice =
                view.findViewById(
                        R.id.etPrice
                );

        btnAdd =
                view.findViewById(
                        R.id.btnAddService
                );

        btnAdd.setOnClickListener(v -> {

            String service =
                    etServiceName.getText()
                            .toString()
                            .trim();

            String description =
                    etDescription.getText()
                            .toString()
                            .trim();

            String price =
                    etPrice.getText()
                            .toString()
                            .trim();

            if (
                    TextUtils.isEmpty(service)
                            || TextUtils.isEmpty(description)
                            || TextUtils.isEmpty(price)
            ) {

                Toast.makeText(
                        requireContext(),
                        "Enter all fields",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            PredefinedService custom =
                    new PredefinedService(
                            service,
                            description
                    );

            custom.setSelected(true);

            custom.setPrice(price);

            listener.onServiceAdded(custom);

            dismiss();
        });
    }
}