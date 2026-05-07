package com.example.tregoapp.mechanic.ui;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tregoapp.R;
import com.example.tregoapp.mechanic.bottomsheet.SelectServiceBottomSheetFragment;
import com.example.tregoapp.mechanic.model.PredefinedService;
import com.example.tregoapp.mechanic.model.ServiceDetail;
import com.example.tregoapp.mechanic.utils.LoaderManager;
import com.example.tregoapp.mechanic.viewmodel.ViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CreateServiceFragment extends Fragment {

    private static final String SHOP_ID = "shop_id";

    private String shopId;

    /*
     * NEW UI
     */
    private MaterialCardView selectServiceLayout;
    private LinearLayout layoutSelectedServices;

    private TextView tvSelectedServiceCount;
    private TextView tvSelectedServices;

    private MaterialButton createServiceBtn;

    private ViewModel viewModel;

    /*
     * SELECTED SERVICES
     */
    private final List<PredefinedService>
            selectedServices = new ArrayList<>();

    public CreateServiceFragment() {
    }

    public static CreateServiceFragment newInstance(
            String shopId
    ) {

        CreateServiceFragment fragment =
                new CreateServiceFragment();

        Bundle args = new Bundle();

        args.putString(SHOP_ID, shopId);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

            shopId =
                    getArguments().getString(
                            SHOP_ID
                    );
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {

        return inflater.inflate(
                R.layout.fragment_create_service,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {

        /*
         * INIT VIEWS
         */
        selectServiceLayout =
                view.findViewById(
                        R.id.selectServiceLayout
                );

        layoutSelectedServices =
                view.findViewById(
                        R.id.layoutSelectedServices
                );

        tvSelectedServiceCount =
                view.findViewById(
                        R.id.tvSelectedServiceCount
                );

        tvSelectedServices =
                view.findViewById(
                        R.id.tvSelectedServices
                );

        createServiceBtn =
                view.findViewById(
                        R.id.createServiceBtn
                );

        /*
         * VIEW MODEL
         */
        viewModel =
                new ViewModelProvider(this)
                        .get(ViewModel.class);

        shopId = viewModel.getShopId();

        viewModelObserver();

        /*
         * OPEN BOTTOM SHEET
         */
        selectServiceLayout.setOnClickListener(v -> {

            SelectServiceBottomSheetFragment bottomSheet =
                    new SelectServiceBottomSheetFragment(
                            services -> {

                                selectedServices.clear();

                                for (PredefinedService service :
                                        services) {

                                    if (service.isSelected()) {

                                        selectedServices.add(
                                                service
                                        );
                                    }
                                }

                                updateSelectedServicesUI();
                            });

            bottomSheet.show(
                    getParentFragmentManager(),
                    "SelectServiceBottomSheet"
            );
        });

        /*
         * CREATE SERVICES
         */
        createServiceBtn.setOnClickListener(v -> {

            if (selectedServices.isEmpty()) {

                Toast.makeText(
                        requireContext(),
                        "Please select at least one service",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            /*
             * VALIDATE PRICE
             */
            for (PredefinedService service :
                    selectedServices) {

                if (service.getPrice() == null
                        || service.getPrice().isEmpty()) {

                    Toast.makeText(
                            requireContext(),
                            "Enter price for "
                                    + service.getServiceName(),
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }
            }

            /*
             * CREATE SERVICE LIST
             */
            List<ServiceDetail> serviceItems =
                    new ArrayList<>();

            for (PredefinedService service :
                    selectedServices) {

                serviceItems.add(

                        new ServiceDetail(
                                service.getServiceName(),
                                service.getDescription(),
                                Double.parseDouble(
                                        service.getPrice()
                                )
                        )
                );
            }

            /*
             * API CALL ONLY ONCE
             */
            LoaderManager.show(this);

            viewModel.createService(
                    shopId,
                    serviceItems
            );
        });
    }

    /*
     * UPDATE UI
     */
    private void updateSelectedServicesUI() {

        layoutSelectedServices.removeAllViews();

        if (selectedServices.isEmpty()) {

            tvSelectedServiceCount.setText(
                    "Select predefined services"
            );

            tvSelectedServices.setText(
                    "Oil Change, Puncture..."
            );

            return;
        }

        tvSelectedServiceCount.setText(
                selectedServices.size()
                        + " Services Selected"
        );

        StringBuilder builder =
                new StringBuilder();

        for (int i = 0;
             i < selectedServices.size();
             i++) {

            builder.append(
                    selectedServices
                            .get(i)
                            .getServiceName()
            );

            if (i != selectedServices.size() - 1) {

                builder.append(", ");
            }
        }

        tvSelectedServices.setText(
                builder.toString()
        );

        /*
         * CREATE SERVICE CARDS
         */
        for (PredefinedService service :
                selectedServices) {

            MaterialCardView card =
                    new MaterialCardView(
                            requireContext()
                    );

            card.setRadius(18f);

            card.setCardElevation(0f);

            card.setStrokeWidth(1);

            card.setStrokeColor(
                    android.graphics.Color.parseColor(
                            "#ECECEC"
                    )
            );

            card.setCardBackgroundColor(
                    android.graphics.Color.WHITE
            );

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );

            params.bottomMargin = 16;

            card.setLayoutParams(params);

            LinearLayout container =
                    new LinearLayout(
                            requireContext()
                    );

            container.setOrientation(
                    LinearLayout.VERTICAL
            );

            container.setPadding(
                    32,
                    28,
                    32,
                    28
            );

            TextView name =
                    new TextView(requireContext());

            name.setText(
                    service.getServiceName()
            );

            name.setTextSize(15);

            name.setTextColor(
                    android.graphics.Color.parseColor(
                            "#111111"
                    )
            );

            name.setTypeface(null,
                    android.graphics.Typeface.BOLD);

            TextView description =
                    new TextView(requireContext());

            description.setText(
                    service.getDescription()
            );

            description.setTextSize(12);

            description.setTextColor(
                    android.graphics.Color.parseColor(
                            "#777777"
                    )
            );

            description.setPadding(
                    0,
                    10,
                    0,
                    0
            );

            TextView price =
                    new TextView(requireContext());

            price.setText(
                    "₹ " + service.getPrice()
            );

            price.setTextSize(14);

            price.setTypeface(
                    null,
                    android.graphics.Typeface.BOLD
            );

            price.setTextColor(
                    android.graphics.Color.parseColor(
                            "#111111"
                    )
            );

            price.setPadding(
                    0,
                    16,
                    0,
                    0
            );

            container.addView(name);

            container.addView(description);

            container.addView(price);

            card.addView(container);

            layoutSelectedServices.addView(card);
        }
    }

    /*
     * OBSERVER
     */
    private void viewModelObserver() {

        viewModel.getAuthState().observe(
                getViewLifecycleOwner(),
                authState -> {

                    if (authState == null) {
                        return;
                    }

                    Toast.makeText(
                            getContext(),
                            authState.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();

                    LoaderManager.hide(this);

                    if (authState.getSuccess()) {

                        getParentFragmentManager()
                                .popBackStack(
                                        null,
                                        FragmentManager
                                                .POP_BACK_STACK_INCLUSIVE
                                );

                        getParentFragmentManager()
                                .beginTransaction()
                                .replace(
                                        R.id.fragment_container,
                                        new DashboardFragment()
                                )
                                .commit();
                    }
                });
    }
}


//package com.example.tregoapp.mechanic.ui;
//
//import android.os.Bundle;
//
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.lifecycle.ViewModelProvider;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import com.example.tregoapp.R;
//import com.example.tregoapp.mechanic.utils.LoaderManager;
//import com.example.tregoapp.mechanic.viewmodel.ViewModel;
//import com.google.android.material.button.MaterialButton;
//import com.google.android.material.textfield.TextInputEditText;
//
//import org.jspecify.annotations.NonNull;
//import org.jspecify.annotations.Nullable;
//
//public class CreateServiceFragment extends Fragment {
//
//    private static final String SHOP_ID = "shop_id";
//    private String shopId;
//
//    private TextInputEditText etService;
//    private TextInputEditText etServiceDescription;
//    private TextInputEditText etServicePrice;
//    private MaterialButton createServiceBtn;
//
//    private ViewModel viewModel;
//
//    public CreateServiceFragment() {
//        // Required empty public constructor
//    }
//
//    public static CreateServiceFragment newInstance(String shopId) {
//        CreateServiceFragment fragment = new CreateServiceFragment();
//        Bundle args = new Bundle();
//        args.putString(SHOP_ID, shopId);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            shopId = getArguments().getString(SHOP_ID);
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_create_service, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        etService = view.findViewById(R.id.etService);
//        etServiceDescription = view.findViewById(R.id.etServiceDescription);
//        etServicePrice = view.findViewById(R.id.etServicePrice);
//        createServiceBtn = view.findViewById(R.id.createServiceBtn);
//
//
//        viewModel = new ViewModelProvider(this).get(ViewModel.class);
//        shopId = viewModel.getShopId();
//        viewModelObserver();
//
//        createServiceBtn.setOnClickListener(v -> {
//            String service = etService.getText().toString().trim();
//            String serviceDescription = etServiceDescription.getText().toString().trim();
//            String servicePrice = etServicePrice.getText().toString().trim();
//
//            if (!validateData(service, serviceDescription, servicePrice)) {
//                return;
//            }
//
//            LoaderManager.show(this);
//            viewModel.createService(shopId, service, serviceDescription, Double.parseDouble(servicePrice));
//        });
//    }
//
//    private boolean validateData(String service, String serviceDescription, String servicePrice) {
//        if (service.isEmpty() &&
//                serviceDescription.isEmpty() &&
//                servicePrice.isEmpty()) {
//             Toast.makeText(requireContext(), "Please enter all the required field", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        if (service.isEmpty()) {
//             Toast.makeText(requireContext(), "Please enter the service", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        if (serviceDescription.isEmpty()) {
//             Toast.makeText(requireContext(), "Please enter the service description", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        if (servicePrice.isEmpty()) {
//             Toast.makeText(requireContext(), "Please enter the service price", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        return true;
//    }
//
//    private void viewModelObserver() {
//        viewModel.getAuthState().observe(getViewLifecycleOwner(), authState -> {
//            if (authState == null) {
//                return;
//            }
//             Toast.makeText(getContext(), authState.getMessage(), Toast.LENGTH_SHORT).show();
//
//            LoaderManager.hide(this);
//            if (authState.getSuccess()) {
//                getParentFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//
//                getParentFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.fragment_container, new DashboardFragment())
//                        .commit();
//            }
//        });
//    }
//}