package com.example.tregoapp.customer.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tregoapp.R;
import com.example.tregoapp.customer.adapter.SelectedImageAdapter;
import com.example.tregoapp.customer.adapter.ServiceListAdapter;
import com.example.tregoapp.customer.adapter.VehicleListAdapter;
import com.example.tregoapp.customer.listener.OnItemClickListener3;
import com.example.tregoapp.customer.model.ServiceDetail;
import com.example.tregoapp.customer.model.ShopDetail;
import com.example.tregoapp.customer.model.VehicleDetail;
import com.example.tregoapp.customer.navigation.NavigationHelper;
import com.example.tregoapp.customer.utils.DeviceLocationHelper;
import com.example.tregoapp.customer.utils.LoaderManager;
import com.example.tregoapp.customer.viewmodel.ViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateRequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateRequestFragment extends Fragment {

    private static final String SHOP_DETAIL = "shop_detail";

    private static final int PICK_IMAGES_CODE = 101;

    private static final int MAX_IMAGES = 3;

    private ShopDetail shopDetail;

    private String customer_id = "";

    private String shop_id = "";

    private String vehicle_id = "";

    private String service_id = "";

    private String address = "";

    private double latitude, longitude;

    private double totalPrice = 0;

    private double totalDistance;

    private double totalDuration;

    private ImageView backBtn;

    private RecyclerView rvVehicleList;

    private RecyclerView rvServiceList;

    private RecyclerView rvSelectedImages;

    private TextInputEditText etProblemDescription;

    private TextView tvTotalDistance;

    private TextView tvTotalPriceView;

    private MaterialButton requestBtn;

    private MaterialButton btnAddImages;

    /*
     * STORE URI ONLY
     * FASTEST APPROACH
     */
    private final List<Uri> selectedImageUris =
            new ArrayList<>();

    private SelectedImageAdapter imageAdapter;

    private ViewModel viewModel;

    private VehicleListAdapter vehicleListAdapter;

    private ServiceListAdapter serviceListAdapter;

    /*
     * VEHICLE + SERVICE LISTENER
     */
    private final OnItemClickListener3 listener =
            new OnItemClickListener3() {

                @Override
                public void onClickVehicle(
                        VehicleDetail vehicleDetail
                ) {

                    vehicle_id =
                            vehicleDetail.getVehicleId();
                }

                @Override
                public void onClickService(
                        ServiceDetail serviceDetail
                ) {

                    service_id =
                            serviceDetail.getServiceId();

                    totalPrice =
                            serviceDetail.getPrice();

                    tvTotalPriceView.setText(

                            String.format(

                                    Locale.getDefault(),

                                    "₹ %.2f",

                                    totalPrice
                            )
                    );
                }
            };

    public CreateRequestFragment() {
    }

    public static CreateRequestFragment newInstance(
            ShopDetail shop
    ) {

        CreateRequestFragment fragment =
                new CreateRequestFragment();

        Bundle args = new Bundle();

        args.putSerializable(
                SHOP_DETAIL,
                shop
        );

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

            shopDetail =
                    (ShopDetail)
                            getArguments()
                                    .getSerializable(
                                            SHOP_DETAIL
                                    );
        }

        if (shopDetail != null) {

            shop_id =
                    shopDetail.getShopId();

            totalDistance =
                    shopDetail.getDistance();

            totalDuration =
                    shopDetail.getEstimatedTime();
        }
    }

    @Override
    public View onCreateView(

            LayoutInflater inflater,

            ViewGroup container,

            Bundle savedInstanceState
    ) {

        return inflater.inflate(

                R.layout.fragment_create_request,

                container,

                false
        );
    }

    @Override
    public void onViewCreated(

            @NonNull View view,

            @Nullable Bundle savedInstanceState
    ) {

        super.onViewCreated(
                view,
                savedInstanceState
        );

        initViews(view);

        setupRecyclerViews();

        setupViewModel();

        setupClickListeners();

        setupObservers();
    }

    /*
     * INIT VIEWS
     */
    private void initViews(View view) {

        backBtn =
                view.findViewById(R.id.backBtn);

        rvVehicleList =
                view.findViewById(R.id.rvVehicleList);

        rvServiceList =
                view.findViewById(R.id.rvServiceList);

        rvSelectedImages =
                view.findViewById(R.id.rvSelectedImages);

        etProblemDescription =
                view.findViewById(
                        R.id.etProblemDescription
                );

        tvTotalDistance =
                view.findViewById(R.id.tvTotalDistance);

        tvTotalPriceView =
                view.findViewById(R.id.tvTotalPrice);

        requestBtn =
                view.findViewById(R.id.requestBtn);

        btnAddImages =
                view.findViewById(R.id.btnAddImages);

        tvTotalDistance.setText(

                String.format(

                        Locale.getDefault(),

                        "%.2f km",

                        totalDistance
                )
        );

        tvTotalPriceView.setText(

                String.format(

                        Locale.getDefault(),

                        "₹ %.2f",

                        totalPrice
                )
        );
    }

    /*
     * SETUP RECYCLER VIEWS
     */
    private void setupRecyclerViews() {

        rvVehicleList.setLayoutManager(

                new LinearLayoutManager(

                        requireContext(),

                        LinearLayoutManager.HORIZONTAL,

                        false
                )
        );

        rvServiceList.setLayoutManager(

                new LinearLayoutManager(

                        requireContext(),

                        LinearLayoutManager.HORIZONTAL,

                        false
                )
        );

        rvSelectedImages.setLayoutManager(

                new LinearLayoutManager(

                        requireContext(),

                        LinearLayoutManager.HORIZONTAL,

                        false
                )
        );

        imageAdapter =
                new SelectedImageAdapter(
                        selectedImageUris
                );

        rvSelectedImages.setAdapter(
                imageAdapter
        );
    }

    /*
     * VIEWMODEL
     */
    private void setupViewModel() {

        viewModel =
                new ViewModelProvider(this)
                        .get(ViewModel.class);

        customer_id =
                viewModel.getUserId();

        viewModel.getVehicles(customer_id);

        viewModel.getShopServices(shop_id);
    }

    /*
     * CLICK LISTENERS
     */
    private void setupClickListeners() {

        /*
         * IMAGE PICKER
         */
        btnAddImages.setOnClickListener(v -> {
            if (
                    selectedImageUris.size()
                            >= MAX_IMAGES
            ) {

                Toast.makeText(

                        requireContext(),

                        "Maximum 3 images allowed",

                        Toast.LENGTH_SHORT

                ).show();

                return;
            }

            Intent intent =
                    new Intent(
                            Intent.ACTION_GET_CONTENT
                    );

            intent.setType("image/*");

            intent.putExtra(
                    Intent.EXTRA_ALLOW_MULTIPLE,
                    true
            );

            startActivityForResult(

                    Intent.createChooser(
                            intent,
                            "Select Images"
                    ),

                    PICK_IMAGES_CODE
            );
        });

        /*
         * BACK
         */
        backBtn.setOnClickListener(v -> {

            if (
                    getParentFragmentManager()
                            .getBackStackEntryCount() > 0
            ) {

                getParentFragmentManager()
                        .popBackStack();
            }
        });

        /*
         * CREATE REQUEST
         */
        requestBtn.setOnClickListener(v -> {

            String problemDescription =
                    etProblemDescription
                            .getText()
                            .toString()
                            .trim();

            DeviceLocationHelper helper =
                    new DeviceLocationHelper(
                            requireContext()
                    );

            helper.getCurrentLocation(

                    requireContext(),

                    (lat, lon, address) -> {

                        this.latitude = lat;

                        this.longitude = lon;

                        this.address =
                                address != null
                                        ? address
                                        : "Unknown location";

                        /*
                         * VALIDATION
                         */
                        if (
                                !validateData(
                                        vehicle_id,
                                        service_id,
                                        problemDescription,
                                        this.address
                                )
                        ) {

                            return;
                        }

                        Log.d(
                                "REQUEST_FLOW",
                                "Calling create request"
                        );

                        LoaderManager.show(this);

                        /*
                         * FAST IMAGE UPLOAD
                         * SEND URI DIRECTLY
                         */
                        viewModel.createServiceRequest(

                                selectedImageUris,

                                customer_id,

                                shop_id,

                                vehicle_id,

                                service_id,

                                problemDescription,

                                this.address,

                                latitude,

                                longitude,

                                totalPrice,

                                totalDistance,

                                totalDuration
                        );
                    }
            );
        });
    }

    /*
     * IMAGE PICKER RESULT
     */
    @Override
    public void onActivityResult(

            int requestCode,

            int resultCode,

            @Nullable Intent data
    ) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (
                requestCode == PICK_IMAGES_CODE
                        &&
                        resultCode == getActivity().RESULT_OK
        ) {

            selectedImageUris.clear();

            if (data != null) {

                /*
                 * MULTIPLE IMAGES
                 */
                if (data.getClipData() != null) {

                    int count =
                            data.getClipData()
                                    .getItemCount();

                    int limit =
                            Math.min(
                                    count,
                                    MAX_IMAGES
                            );

                    for (int i = 0; i < limit; i++) {

                        Uri imageUri =
                                data.getClipData()
                                        .getItemAt(i)
                                        .getUri();

                        selectedImageUris.add(
                                imageUri
                        );
                    }

                    /*
                     * LIMIT MESSAGE
                     */
                    if (count > MAX_IMAGES) {

                        Toast.makeText(

                                requireContext(),

                                "Only 3 images allowed",

                                Toast.LENGTH_SHORT

                        ).show();
                    }
                }

                /*
                 * SINGLE IMAGE
                 */
                else if (data.getData() != null) {

                    Uri imageUri =
                            data.getData();

                    selectedImageUris.add(
                            imageUri
                    );
                }

                imageAdapter.notifyDataSetChanged();

                Log.d(
                        "IMAGE_PICKER",
                        "Selected Images : "
                                + selectedImageUris.size()
                );
            }
        }
    }

    /*
     * VALIDATION
     */
    private boolean validateData(

            String vehicle_id,

            String service_id,

            String problemDescription,

            String address
    ) {

        if (vehicle_id.isEmpty()) {

            Toast.makeText(

                    requireContext(),

                    "Please choose the vehicle",

                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }

        if (service_id.isEmpty()) {

            Toast.makeText(

                    requireContext(),

                    "Please choose the service",

                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }

        if (problemDescription.isEmpty()) {

            Toast.makeText(

                    requireContext(),

                    "Please enter the problem",

                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }

        if (address.isEmpty()) {

            Toast.makeText(

                    requireContext(),

                    "Location not found",

                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }

        return true;
    }

    /*
     * OBSERVERS
     */
    private void setupObservers() {

        /*
         * VEHICLES
         */
        viewModel.getVehicleListResource()
                .observe(

                        getViewLifecycleOwner(),

                        resource -> {

                            LoaderManager.handleResource(

                                    this,

                                    resource,

                                    vehicles -> {

                                        if (vehicles == null)
                                            return;

                                        vehicleListAdapter =
                                                new VehicleListAdapter(

                                                        vehicles,

                                                        listener
                                                );

                                        vehicleListAdapter
                                                .setItemClickable(
                                                        true
                                                );

                                        rvVehicleList.setAdapter(
                                                vehicleListAdapter
                                        );
                                    }
                            );
                        }
                );

        /*
         * SERVICES
         */
        viewModel.getServicesListResource()
                .observe(

                        getViewLifecycleOwner(),

                        resource -> {

                            LoaderManager.handleResource(

                                    this,

                                    resource,

                                    services -> {

                                        if (services == null)
                                            return;

                                        serviceListAdapter =
                                                new ServiceListAdapter(

                                                        services,

                                                        listener
                                                );

                                        rvServiceList.setAdapter(
                                                serviceListAdapter
                                        );
                                    }
                            );
                        }
                );

        /*
         * CREATE REQUEST
         */
        viewModel.getServiceRequestResource()
                .observe(

                        getViewLifecycleOwner(),

                        resource -> {

                            LoaderManager.handleResource(

                                    this,

                                    resource,

                                    service -> {

                                        if (
                                                service != null
                                                        &&
                                                        service.getId() != null
                                        ) {

                                            service_id =
                                                    service.getId();

                                            NavigationHelper.navigateTo(

                                                    getParentFragmentManager(),

                                                    WaitingFragment.newInstance(
                                                            service_id
                                                    ),

                                                    false
                                            );
                                        }
                                    }
                            );
                        }
                );
    }
}