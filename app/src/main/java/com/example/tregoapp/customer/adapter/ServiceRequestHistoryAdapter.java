package com.example.tregoapp.customer.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tregoapp.R;
import com.example.tregoapp.customer.listener.OnItemClickListener3;
import com.example.tregoapp.customer.model.ServiceDetail;
import com.example.tregoapp.customer.model.ServiceRequest;
import com.example.tregoapp.customer.utils.Utility;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class ServiceRequestHistoryAdapter extends RecyclerView.Adapter<ServiceRequestHistoryAdapter.ServiceRequestHistoryViewHolder> {

    private final List<ServiceRequest> serviceRequestsList = new ArrayList<>();
    private int selectedPosition = -1;

    public ServiceRequestHistoryAdapter(List<ServiceRequest> list) {
        serviceRequestsList.clear();
        serviceRequestsList.addAll(list);
        notifyDataSetChanged();
    }

    public void updateList(List<ServiceRequest> list) {
        serviceRequestsList.clear();
        serviceRequestsList.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ServiceRequestHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_item_layout, parent, false);

        return new ServiceRequestHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceRequestHistoryViewHolder holder, int position) {
        ServiceRequest serviceRequest = serviceRequestsList.get(position);

        if (serviceRequest.getIsSOS()) {
            holder.tvShopName.setText("Emergency Request");
            holder.tvServiceName.setText(serviceRequest.getProblemDescription() != null ? serviceRequest.getProblemDescription() : "Fuel Delivery");
        }
        else {
            holder.tvShopName.setText(serviceRequest.getShopName() != null ? serviceRequest.getShopName() : "Shop Name");
            holder.tvServiceName.setText(serviceRequest.getServiceName() != null ? serviceRequest.getServiceName() : "Service Name");
        }
        updateStatusView(holder.tvStatus, serviceRequest.getStatus());
        holder.tvPrice.setText("₹ " + String.valueOf(serviceRequest.getTotalPrice()));
        holder.tvDistance.setText(String.valueOf(serviceRequest.getTotalDistance() + " km"));
        holder.tvDate.setText(
                serviceRequest.getCreatedAt() != null
                        ? Utility.getInstance().formatDate2(serviceRequest.getCreatedAt())
                        : "-"
        );
    }

    @Override
    public int getItemCount() {
        return serviceRequestsList.size();
    }

    public ServiceRequest getSelectedServiceRequest() {
        if (selectedPosition >= 0 && selectedPosition < serviceRequestsList.size()) {
            return serviceRequestsList.get(selectedPosition);
        }
        return null;
    }

    public static class ServiceRequestHistoryViewHolder extends RecyclerView.ViewHolder {

        private TextView tvShopName, tvServiceName, tvStatus, tvPrice, tvDistance, tvDate;

        public ServiceRequestHistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            tvShopName = itemView.findViewById(R.id.tvShopName);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }

    private void updateStatusView(
            TextView textView,
            String status
    ) {

        String text = "Requested";
        String color = "#8B5CF6";

        if (status != null) {

            switch (status.toLowerCase()) {

                case "accepted":
                    text = "Accepted";
                    color = "#3B82F6";
                    break;

                case "cancelled":
                    text = "Cancelled";
                    color = "#EF4444";
                    break;

                case "completed":
                    text = "Completed";
                    color = "#22C55E";
                    break;

                case "ongoing":
                case "in_progress":
                    text = "In Progress";
                    color = "#3B82F6";
                    break;

                case "waiting_for_confirmation":
                    text = "Wait for Confirmation";
                    color = "#F59E0B";
                    break;

                default:
                    text = "Requested";
                    color = "#8B5CF6";
                    break;
            }
        }

        textView.setText(text);
        textView.setTextColor(Color.WHITE);

        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setShape(
                GradientDrawable.RECTANGLE
        );

        drawable.setCornerRadius(100f); // pill style

        drawable.setColor(
                Color.parseColor(color)
        );

        textView.setBackground(drawable);
    }
}