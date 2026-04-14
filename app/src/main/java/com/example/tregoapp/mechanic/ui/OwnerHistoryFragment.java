package com.example.tregoapp.mechanic.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.tregoapp.R;
import com.example.tregoapp.mechanic.adapter.HistoryAdapter;
import com.example.tregoapp.mechanic.utils.EmptyStateHelper;
import com.example.tregoapp.mechanic.utils.LoadFragment;
import com.example.tregoapp.mechanic.utils.LoaderManager;
import com.example.tregoapp.mechanic.viewmodel.ViewModel;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;

public class OwnerHistoryFragment extends Fragment {

    private View root;

    private String shopId;
    private RecyclerView recyclerView;
    private ImageView backBtn;

    private ViewModel viewModel;
    private HistoryAdapter adapter;


    public OwnerHistoryFragment() {
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
        return inflater.inflate(R.layout.fragment_owner_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        root = view;
        backBtn = view.findViewById(R.id.backBtn);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemViewCacheSize(20);

        adapter = new HistoryAdapter();
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

            shopId = currentUser.getShopId();
            LoaderManager.show(this);
            viewModel.getShopHistoryServiceRequests(shopId);
        });
        viewModel.getShopServiceRequestHistoryLiveData().observe(getViewLifecycleOwner(), historyData -> {
            LoaderManager.hide(this);
            if (historyData == null || historyData.isEmpty()) {
                EmptyStateHelper.show(root, "No history is available", recyclerView);
                adapter.setHistoryList(null);
            }
            else {
                EmptyStateHelper.hide(root, recyclerView);
                adapter.setHistoryList(new ArrayList<>(historyData));
            }
        });
    }
}