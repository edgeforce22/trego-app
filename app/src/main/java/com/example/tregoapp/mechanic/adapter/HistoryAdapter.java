package com.example.tregoapp.mechanic.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tregoapp.R;
import com.example.tregoapp.mechanic.model.ServiceRequest;
import com.example.tregoapp.mechanic.utils.Utility;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<ServiceRequest> historyList = new ArrayList<>();
    private Utility utility = new Utility();

    public void setHistoryList(List<ServiceRequest> list) {
        this.historyList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ServiceRequest item = historyList.get(position);

        holder.tvCustomerName.setText(item.getCustomerName() != null ? item.getCustomerName() : (item.getCustomerId() != null ? item.getCustomerId() : "Unknown Customer"));

        if (item.getCustomerLocation() != null) {
            holder.tvCustomerAddress.setText(item.getCustomerLocation().getAddress());
        } else {
            holder.tvCustomerAddress.setText("No Address");
        }

        holder.tvServices.setText(item.getProblemDescription() != null ? item.getProblemDescription() : "No description");

        holder.tvPrice.setText("₹ " + (item.getTotalPrice() != 0 ? item.getTotalPrice() : 0));

        String status = item.getStatus() != null ? item.getStatus() : "unknown";
        holder.tvStatus.setText(status.toUpperCase());

        if (status.equals("completed")) {
            holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
        } else if (status.equals("cancelled")) {
            holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.darker_gray));
        }

        holder.tvCreatedAt.setText(utility.formatDate(item.getCreatedAt()));
    }

    @Override
    public int getItemCount() {
        return historyList != null ? historyList.size() : 0;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvCustomerAddress, tvStatus, tvServices, tvPrice, tvCreatedAt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvCustomerAddress = itemView.findViewById(R.id.tvCustomerAddress);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvServices = itemView.findViewById(R.id.tvServices);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
        }
    }
}