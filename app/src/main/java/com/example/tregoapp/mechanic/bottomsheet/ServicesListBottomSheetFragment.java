package com.example.tregoapp.mechanic.bottomsheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tregoapp.R;
import com.example.tregoapp.mechanic.adapter.ServiceListAdapter;
import com.example.tregoapp.mechanic.model.ServiceDetail;
import com.example.tregoapp.mechanic.ui.CreateServiceFragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class ServicesListBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String SHOPID = "SHOPID";
    private String shopId;
    private RecyclerView rvServiceList;
    private MaterialCardView goToCreateService;

    private ServiceListAdapter adapter;

    private final List<ServiceDetail>
            serviceList =
            new ArrayList<>();

    public ServicesListBottomSheetFragment() {
    }

    public static ServicesListBottomSheetFragment newInstance(
            List<ServiceDetail> services, String shopId
    ) {

        ServicesListBottomSheetFragment fragment =
                new ServicesListBottomSheetFragment();

        Bundle args = new Bundle();

        args.putSerializable(
                "services",
                new ArrayList<>(services)
        );
        args.putString(SHOPID, shopId);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(

            LayoutInflater inflater,

            ViewGroup container,

            Bundle savedInstanceState
    ) {

        return inflater.inflate(
                R.layout.fragment_services_list_bottom_sheet,
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

        goToCreateService = view.findViewById(R.id.goToCreateService);
        rvServiceList =
                view.findViewById(
                        R.id.rvServiceList
                );

        /*
         * GET DATA
         */
        if (
                getArguments() != null
        ) {

            List<ServiceDetail> services =
                    (List<ServiceDetail>)
                            getArguments()
                                    .getSerializable(
                                            "services"
                                    );

            if (services != null) {
                serviceList.addAll(services);
            }

            shopId = getArguments().getString(SHOPID);
        }

        /*
         * RECYCLER VIEW
         */
        rvServiceList.setLayoutManager(

                new LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                )
        );

        adapter =
                new ServiceListAdapter(
                        serviceList
                );

        rvServiceList.setAdapter(
                adapter
        );

        goToCreateService.setOnClickListener(v -> {

            if (shopId == null || shopId.isEmpty()) {
                Toast.makeText(requireContext(),
                        "Please register your shop first",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            CreateServiceFragment createServiceFragment = CreateServiceFragment.newInstance(shopId);
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, createServiceFragment)
                    .addToBackStack(null)
                    .commit();

            dismiss();
        });
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetTransparent;
    }
}