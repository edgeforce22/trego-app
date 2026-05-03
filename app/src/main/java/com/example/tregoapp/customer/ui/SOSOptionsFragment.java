package com.example.tregoapp.customer.ui;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tregoapp.R;
import com.example.tregoapp.customer.model.GetRequestById;
import com.example.tregoapp.customer.navigation.NavigationHelper;
import com.example.tregoapp.customer.network.Resource;
import com.example.tregoapp.customer.utils.DeviceLocationHelper;
import com.example.tregoapp.customer.utils.LoaderManager;
import com.example.tregoapp.customer.viewmodel.ViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class SOSOptionsFragment extends Fragment {

    private String customerId;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ImageView backBtn;
    private ChipGroup chipGroup;

    private TextInputLayout tilOtherIssue;
    private TextInputEditText etOtherIssue;
    private Chip otherChip;

    private ViewModel viewModel;

    private double latitude;
    private double longitude;
    private String address;

    private final int[] selectedCount = {0};

    public SOSOptionsFragment() {
        // Required empty public constructor
    }

    public static SOSOptionsFragment newInstance(String param1, String param2) {
        SOSOptionsFragment fragment = new SOSOptionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_s_o_s_options, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        backBtn = view.findViewById(R.id.backBtn);
        chipGroup = view.findViewById(R.id.chipGroupSOS);
        tilOtherIssue = view.findViewById(R.id.tilOtherIssue);
        etOtherIssue = view.findViewById(R.id.etOtherIssue);
        MaterialButton btnRequest = view.findViewById(R.id.btnRequestSOS);

        String[] sosOptions = {
                "Breakdown", "Flat Tyre", "Battery Jumpstart",
                "Fuel Delivery", "Engine Issue", "Towing", "Accident Help", "Other"
        };

        for (String option : sosOptions) {

            Chip chip = new Chip(requireContext());
            chip.setId(View.generateViewId()); // ✅ IMPORTANT

            chip.setText(option);
            chip.setCheckable(true);
            chip.setClickable(true);

            chip.setChipBackgroundColorResource(R.color.chip_selector);
            chip.setChipStrokeWidth(2f);
            chip.setChipStrokeColorResource(R.color.primary_color);
            chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));

            chipGroup.addView(chip);

            if ("Other".equalsIgnoreCase(option)) {
                otherChip = chip;
            }
        }

        if (otherChip != null) {
            otherChip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                tilOtherIssue.setVisibility(isChecked ? View.VISIBLE : View.GONE);

                if (!isChecked) {
                    etOtherIssue.setText("");
                }
            });
        }

        // Limit selection
        for (int i = 0; i < chipGroup.getChildCount(); i++) {

            Chip chip = (Chip) chipGroup.getChildAt(i);

            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {

                if (isChecked) {

                    if (selectedCount[0] >= 3) {
                        chip.setChecked(false);

                        Toast.makeText(
                                requireContext(),
                                "Select maximum 3 issues",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    selectedCount[0]++;
                } else {
                    if (selectedCount[0] > 0) {
                        selectedCount[0]--;
                    }
                }

                // Handle Other chip visibility
                if ("Other".equalsIgnoreCase(chip.getText().toString())) {

                    tilOtherIssue.setVisibility(
                            isChecked ? View.VISIBLE : View.GONE
                    );

                    if (!isChecked) {
                        etOtherIssue.setText("");
                    }
                }
            });
        }

        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        viewModel.loadSavedUser();
        viewModelObserver();

        // Get user location
        DeviceLocationHelper helper =
                new DeviceLocationHelper(requireContext());

        helper.getCurrentLocation(requireContext(), (lat, lon, add) -> {
            latitude = lat;
            longitude = lon;
            address = add;
        });

        backBtn.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });

        // Button click

        btnRequest.setOnClickListener(v -> {

            List<String> selectedServices = new ArrayList<>();

            for (int i = 0; i < chipGroup.getChildCount(); i++) {

                Chip chip = (Chip) chipGroup.getChildAt(i);

                if (chip.isChecked()) {

                    if ("Other".equalsIgnoreCase(chip.getText().toString())) {

                        String otherText = etOtherIssue.getText() != null
                                ? etOtherIssue.getText().toString().trim()
                                : "";

                        if (otherText.isEmpty()) {
                            Toast.makeText(requireContext(),
                                    "Please enter other issue",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        selectedServices.add(otherText);

                    } else {
                        selectedServices.add(chip.getText().toString());
                    }
                }
            }

            if (selectedServices.isEmpty()) {
                Toast.makeText(requireContext(),
                        "Please select at least one issue",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.sendSOS(
                    customerId,
                    latitude,
                    longitude,
                    address,
                    selectedServices
            );
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        clearSelections();
    }

    private void viewModelObserver() {
        viewModel.getSosActionResource().observe(getViewLifecycleOwner(), resource -> {
            LoaderManager.handleResource(this, resource, data -> {

                if (data == null) {
                    Toast.makeText(requireContext(),"Invalid SOS response",Toast.LENGTH_SHORT).show();
                    return;
                }

                String requestId = data.getId();

                if (requestId == null || requestId.isEmpty()) {
                    Toast.makeText(requireContext(),"Request ID missing",Toast.LENGTH_SHORT).show();
                    return;
                }

                NavigationHelper.navigateTo(
                        getParentFragmentManager(),
                        SOSSendingFragment.newInstance(customerId, requestId)
                );
            });
        });

        viewModel.getAuthResource().observe(getViewLifecycleOwner(), resource -> {
            if (resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
                customerId = resource.getData().getId();
            }
        });
    }

// ===================== OPTIONAL clearSelections() UPDATE =====================

    private void clearSelections() {

        if (chipGroup == null) return;

        chipGroup.clearCheck();
        selectedCount[0] = 0;

        if (tilOtherIssue != null) {
            tilOtherIssue.setVisibility(View.GONE);
        }

        if (etOtherIssue != null) {
            etOtherIssue.setText("");
        }
    }
}