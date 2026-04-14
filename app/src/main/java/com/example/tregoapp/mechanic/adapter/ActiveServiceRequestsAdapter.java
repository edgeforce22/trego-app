package com.example.tregoapp.mechanic.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tregoapp.mechanic.listener.OnItemClickListener;
import com.example.tregoapp.mechanic.model.RequestCustomerModel;
import com.example.tregoapp.R;
import com.example.tregoapp.mechanic.model.ServiceRequest;
import com.example.tregoapp.mechanic.utils.Utility;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ActiveServiceRequestsAdapter extends ListAdapter<ServiceRequest, ActiveServiceRequestsAdapter.ActiveServiceRequestsViewHolder> {

    private final OnItemClickListener onItemClickListener;
    private Utility utility;

    public ActiveServiceRequestsAdapter(OnItemClickListener listener) {
        super(DIFF_CALLBACK);
        this.onItemClickListener = listener;
        this.utility = new Utility();
    }

    @NonNull
    @Override
    public ActiveServiceRequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.active_service_request_layout, parent, false);
        return new ActiveServiceRequestsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActiveServiceRequestsViewHolder holder, int position) {
        ServiceRequest request = getItem(position);

        holder.tvCustomerName.setText(request.getCustomerName() != null ? request.getCustomerName() : request.getCustomerId());
        holder.tvCustomerAddress.setText(request.getCustomerLocation().getAddress());
        holder.tvService.setText(request.getServiceId());
        holder.tvTotalDistance.setText(request.getTotalDistance() + " km");
        holder.tvTotalDuration.setText(request.getTotalDuration() + " min");
        holder.tvCreatedAt.setText(String.valueOf(utility.formatDate(request.getCreatedAt())));

        holder.cardClick.setOnClickListener(v -> {
            onItemClickListener.onClick(request.getId());
        });
    }

    private void removeItem(int position) {
        List<ServiceRequest> currentList = new ArrayList<>(getCurrentList());
        currentList.remove(position);
        submitList(currentList);
    }

    public static class ActiveServiceRequestsViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvCustomerAddress, tvService, tvTotalDistance, tvTotalDuration, tvCreatedAt;
        LinearLayout cardClick;
        public ActiveServiceRequestsViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvCustomerAddress = itemView.findViewById(R.id.tvCustomerAddress);
            tvService = itemView.findViewById(R.id.tvService);
            tvTotalDistance = itemView.findViewById(R.id.tvTotalDistance);
            tvTotalDuration = itemView.findViewById(R.id.tvTotalDuration);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            cardClick = itemView.findViewById(R.id.cardClick);
        }
    }


    private static final DiffUtil.ItemCallback<ServiceRequest> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ServiceRequest>() {

                @Override
                public boolean areItemsTheSame(@NonNull ServiceRequest oldItem,
                                               @NonNull ServiceRequest newItem) {
                    return oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull ServiceRequest oldItem,
                                                  @NonNull ServiceRequest newItem) {

                    return oldItem.getStatus().equals(newItem.getStatus()) &&
                            oldItem.getTotalDistance() == newItem.getTotalDistance();
                }
            };
}
