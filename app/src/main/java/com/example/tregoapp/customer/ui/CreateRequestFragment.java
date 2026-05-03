package com.example.tregoapp.customer.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tregoapp.R;
import com.example.tregoapp.customer.adapter.ServiceListAdapter;
import com.example.tregoapp.customer.adapter.VehicleListAdapter;
import com.example.tregoapp.customer.listener.OnItemClickListener3;
import com.example.tregoapp.customer.model.ServiceDetail;
import com.example.tregoapp.customer.model.ShopDetail;
import com.example.tregoapp.customer.model.VehicleDetail;
import com.example.tregoapp.customer.navigation.NavigationHelper;
import com.example.tregoapp.customer.network.Resource;
import com.example.tregoapp.customer.model.ServiceRequest;
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
    private ShopDetail shopDetail;

    private String customer_id = new String();
    private String shop_id = new String();
    private String vehicle_id = new String();
    private String service_id = new String();
    private String address = new String();
    private double latitude, longitude;
    private double totalPrice = 0;
    private double totalDistance;
    private double totalDuration;

    private ImageView backBtn;
    private RecyclerView rvVehicleList;
    private RecyclerView rvServiceList;
    private TextInputEditText etProblemDescription;
    private TextView tvTotalDistance;
    private TextView tvTotalPriceView;
    private MaterialButton requestBtn;

    private ViewModel viewModel;
    private List<VehicleDetail> vehicleObjects;
    private List<ServiceDetail> serviceObjects;

    private VehicleListAdapter vehicleListAdapter;
    private ServiceListAdapter serviceListAdapter;

    private OnItemClickListener3 listener = new OnItemClickListener3() {
        @Override
        public void onClickVehicle(VehicleDetail vehicleDetail) {
            vehicle_id = vehicleDetail.getVehicleId();
        }

        @Override
        public void onClickService(ServiceDetail serviceDetail) {
            service_id = serviceDetail.getServiceId();
            totalPrice = serviceDetail.getPrice();
            tvTotalPriceView.setText(String.format(Locale.getDefault(), "₹ %.2f", totalPrice));
        }
    };

    public CreateRequestFragment() {
    }
    public static CreateRequestFragment newInstance(ShopDetail shop) {
        CreateRequestFragment fragment = new CreateRequestFragment();
        Bundle args = new Bundle();
        args.putSerializable(SHOP_DETAIL, shop);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            shopDetail = (ShopDetail) getArguments().getSerializable(SHOP_DETAIL);
        }
        shop_id = shopDetail.getShopId();
        totalDistance = shopDetail.getDistance();
        totalDuration = shopDetail.getEstimatedTime();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_request, container, false);
    }

    @Override
    public void onViewCreated(@NonNull  View view, @Nullable Bundle savedInstanceState) {
        backBtn = view.findViewById(R.id.backBtn);
        rvVehicleList = view.findViewById(R.id.rvVehicleList);
        rvServiceList = view.findViewById(R.id.rvServiceList);
        etProblemDescription = view.findViewById(R.id.etProblemDescription);
        tvTotalDistance = view.findViewById(R.id.tvTotalDistance);
        tvTotalPriceView = view.findViewById(R.id.tvTotalPrice);
        requestBtn = view.findViewById(R.id.requestBtn);

        tvTotalDistance.setText(String.format(Locale.getDefault(), "%.2f km", totalDistance));
        tvTotalPriceView.setText(String.format(Locale.getDefault(), "₹ %.2f", totalPrice));

        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        customer_id = viewModel.getUserId();

        viewModel.getVehicles(customer_id);
        viewModel.getShopServices(shop_id);
        viewModelObserver();

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

        backBtn.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });

        requestBtn.setOnClickListener(v -> {
            String problemDescription = etProblemDescription.getText().toString().trim();

            DeviceLocationHelper helper =
                    new DeviceLocationHelper(requireContext());

            helper.getCurrentLocation(requireContext(), (lat, lon, address) -> {

                this.latitude = lat;
                this.longitude = lon;
                this.address = address != null ? address : "Unknown location";

                if (!validateData(vehicle_id, service_id, problemDescription, address)) {
                    return;
                }

                LoaderManager.show(this);
                viewModel.createServiceRequest(
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
            });
        });
    }

    private boolean validateData(String vehicle_id, String service_id, String problemDescription, String  address) {
        if (vehicle_id.isEmpty() &&
            service_id.isEmpty() &&
            problemDescription.isEmpty() &&
            address.isEmpty()) {
             Toast.makeText(requireContext(), "Please fill up all the fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (vehicle_id.isEmpty()) {
             Toast.makeText(requireContext(), "Please choose the vehicle", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (service_id.isEmpty()) {
             Toast.makeText(requireContext(), "Please choose the service", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (problemDescription.isEmpty()) {
             Toast.makeText(requireContext(), "Please enter the problem", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (address.isEmpty()) {
            // Toast.makeText(requireContext(), "Please enter the address", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void viewModelObserver() {

        viewModel.getVehicleListResource().observe(getViewLifecycleOwner(), resource -> {
            LoaderManager.handleResource(this, resource, vehicles -> {
                if (vehicles == null) return;

                vehicleListAdapter = new VehicleListAdapter(vehicles, listener);
                vehicleListAdapter.setItemClickable(true);
                rvVehicleList.setAdapter(vehicleListAdapter);
            });
        });

        viewModel.getServicesListResource().observe(getViewLifecycleOwner(), resource -> {
            LoaderManager.handleResource(this, resource, services -> {
                if (services == null) return;

                serviceListAdapter = new ServiceListAdapter(services, listener);
                rvServiceList.setAdapter(serviceListAdapter);
            });
        });

        viewModel.getServiceRequestResource().observe(getViewLifecycleOwner(), resource -> {
            LoaderManager.handleResource(this, resource, service -> {
                if (service != null && service.getId() != null) {
                    service_id = service.getId();
                    NavigationHelper.navigateTo(getParentFragmentManager(), WaitingFragment.newInstance(service_id), false);
                }
            });
        });
    }

}