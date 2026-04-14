package com.example.tregoapp.mechanic.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.tregoapp.R;
import com.example.tregoapp.customer.listener.OnItemClickListener2;
import com.example.tregoapp.mechanic.adapter.WorkersAdapter;
import com.example.tregoapp.mechanic.utils.EmptyStateHelper;
import com.example.tregoapp.mechanic.utils.LoadFragment;
import com.example.tregoapp.mechanic.utils.LoaderManager;
import com.example.tregoapp.mechanic.viewmodel.ViewModel;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;

public class WorkersFragment extends Fragment implements OnItemClickListener2 {

    private View root;

    private String shopId;
    private RecyclerView recyclerView;
    private ImageView backBtn;

    private ViewModel viewModel;
    private WorkersAdapter adapter;


    public WorkersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_workers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        root = view;
        backBtn = view.findViewById(R.id.backBtn);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemViewCacheSize(20);

        adapter = new WorkersAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        LoadFragment.replaceChildFragment(this, R.id.dashboardBottomContainer, new OwnerBottomNavigationFragment());

        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
        viewModel.loadSavedUser();
        viewModelObserver();

        backBtn.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });
    }

    @Override
    public void onClick(String phoneNumber) {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.CALL_PHONE}, 1);
            return;
        }

        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    private void viewModelObserver() {
        viewModel.getAuthState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) {
                return;
            }
            if (!state.getSuccess()) {
                LoaderManager.hide(this);
            }
        });
        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), currentUser -> {
            if (currentUser == null) {
                return;
            }

            LoaderManager.show(this);
            shopId = currentUser.getShopId();
            viewModel.getShopWorkers(shopId);
        });
        viewModel.getWorkersListLiveData().observe(getViewLifecycleOwner(), workers -> {
            LoaderManager.hide(this);
            if (workers == null || workers.isEmpty()) {
                EmptyStateHelper.show(root, "No workers are found", recyclerView);
                adapter.setWorkerList(null);
            }
            else {
                EmptyStateHelper.hide(root, recyclerView);
                adapter.setWorkerList(new ArrayList<>(workers));
            }
        });
    }
}