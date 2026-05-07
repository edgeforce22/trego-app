package com.example.tregoapp.mechanic.bottomsheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tregoapp.R;
import com.example.tregoapp.mechanic.adapter.PredefinedServiceAdapter;
import com.example.tregoapp.mechanic.model.PredefinedService;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class SelectServiceBottomSheetFragment extends BottomSheetDialogFragment {

    private MaterialCardView addCustomServiceLayout;
    private RecyclerView rvServices;
    private MaterialButton btnDone;

    private PredefinedServiceAdapter adapter;

    private List<PredefinedService> services;

    public interface OnServiceSelectedListener {
        void onServicesSelected(
                List<PredefinedService> services
        );
    }

    private OnServiceSelectedListener listener;

    public SelectServiceBottomSheetFragment(
            OnServiceSelectedListener listener
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
                R.layout.fragment_select_service_bottom_sheet,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {

        addCustomServiceLayout =
                view.findViewById(
                        R.id.addCustomServiceLayout
                );
        rvServices =
                view.findViewById(R.id.rvServices);

        btnDone =
                view.findViewById(R.id.btnDone);

        rvServices.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );

        initData();

        adapter =
                new PredefinedServiceAdapter(
                        services
                );

        rvServices.setAdapter(adapter);

        btnDone.setOnClickListener(v -> {

            listener.onServicesSelected(
                    adapter.getSelectedServices()
            );

            dismiss();
        });

        addCustomServiceLayout.setOnClickListener(v -> {

            AddCustomServiceBottomSheetFragment bottomSheet =
                    new AddCustomServiceBottomSheetFragment(
                            service -> {

                                services.add(service);

                                adapter.notifyItemInserted(
                                        services.size() - 1
                                );
                            });

            bottomSheet.show(
                    getParentFragmentManager(),
                    "AddCustomServiceBottomSheet"
            );
        });
    }

    private void initData() {
        services =
                new ArrayList<>();

        services.add(
                new PredefinedService(
                        "Oil Change",
                        "Engine oil replacement"
                )
        );

        services.add(
                new PredefinedService(
                        "Puncture Repair",
                        "Tyre puncture fixing"
                )
        );

        services.add(
                new PredefinedService(
                        "Brake Service",
                        "Brake repair and inspection"
                )
        );

        services.add(
                new PredefinedService(
                        "Battery Check",
                        "Battery inspection service"
                )
        );

        services.add(
                new PredefinedService(
                        "Chain Adjustment",
                        "Chain tightening and lubrication"
                )
        );

        services.add(
                new PredefinedService(
                        "Wheel Alignment",
                        "Wheel balancing and alignment"
                )
        );

        services.add(
                new PredefinedService(
                        "Engine Tuning",
                        "Complete engine performance tuning"
                )
        );

        services.add(
                new PredefinedService(
                        "Clutch Repair",
                        "Clutch inspection and repair"
                )
        );

        services.add(
                new PredefinedService(
                        "Gear Oil Change",
                        "Gear oil replacement service"
                )
        );

        services.add(
                new PredefinedService(
                        "Tyre Replacement",
                        "Old tyre removal and replacement"
                )
        );

        services.add(
                new PredefinedService(
                        "Air Filter Cleaning",
                        "Air filter cleaning and maintenance"
                )
        );

        services.add(
                new PredefinedService(
                        "Coolant Refill",
                        "Engine coolant refill service"
                )
        );

        services.add(
                new PredefinedService(
                        "Suspension Repair",
                        "Front and rear suspension repair"
                )
        );

        services.add(
                new PredefinedService(
                        "Spark Plug Replacement",
                        "Spark plug inspection and replacement"
                )
        );

        services.add(
                new PredefinedService(
                        "Fuel Line Check",
                        "Fuel pipe and leakage inspection"
                )
        );

        services.add(
                new PredefinedService(
                        "Horn Repair",
                        "Horn wiring and replacement"
                )
        );

        services.add(
                new PredefinedService(
                        "Headlight Repair",
                        "Headlight and wiring service"
                )
        );

        services.add(
                new PredefinedService(
                        "Indicator Repair",
                        "Indicator bulb and connection repair"
                )
        );

        services.add(
                new PredefinedService(
                        "Brake Pad Replacement",
                        "Brake pad inspection and replacement"
                )
        );

        services.add(
                new PredefinedService(
                        "Vehicle Wash",
                        "Complete vehicle cleaning service"
                )
        );

        services.add(
                new PredefinedService(
                        "Water Service",
                        "Pressure water wash service"
                )
        );

        services.add(
                new PredefinedService(
                        "AC Service",
                        "Vehicle AC inspection and gas refill"
                )
        );

        services.add(
                new PredefinedService(
                        "Self Motor Repair",
                        "Starter motor inspection and repair"
                )
        );

        services.add(
                new PredefinedService(
                        "Silencer Repair",
                        "Silencer welding and repair"
                )
        );

        services.add(
                new PredefinedService(
                        "General Inspection",
                        "Complete vehicle inspection service"
                )
        );
    }
}