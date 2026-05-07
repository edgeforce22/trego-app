package com.example.tregoapp.mechanic.adapter;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tregoapp.R;
//import com.example.tregoapp.mechanic.listener.OnItemClickListener3;
import com.example.tregoapp.mechanic.model.ServiceDetail;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class ServiceListAdapter extends RecyclerView.Adapter<ServiceListAdapter.ServiceViewHolder> {

    private final List<ServiceDetail> serviceList = new ArrayList<>();
//    private OnItemClickListener3 listener;

    private int selectedPosition = -1;

    public ServiceListAdapter(List<ServiceDetail> list) {
        serviceList.clear();
        serviceList.addAll(list);
//        this.listener = listener;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.service_item_layout, parent, false);

        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {

        ServiceDetail serviceDetail = serviceList.get(position);

        holder.serviceName.setText(serviceDetail.getService().toUpperCase());
        holder.serviceDescription.setText(serviceDetail.getDescription());
        holder.servicePrice.setText(String.valueOf(serviceDetail.getPrice()));

        // Selection UI
        if (selectedPosition == position) {

            holder.cardView.setStrokeColor(Color.parseColor("#FFE429"));
            holder.cardView.setStrokeWidth(3);
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FFFBE6"));

        } else {

            holder.cardView.setStrokeColor(Color.parseColor("#E0E0E0"));
            holder.cardView.setStrokeWidth(1);
            holder.cardView.setCardBackgroundColor(Color.WHITE);
        }

//        holder.itemView.setOnClickListener(v -> {
//
//            int oldPosition = selectedPosition;
//            selectedPosition = holder.getAdapterPosition();
//
//            notifyItemChanged(oldPosition);
//            notifyItemChanged(selectedPosition);
//
//            if (listener != null) {
//                listener.onClickService(serviceDetail);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public ServiceDetail getSelectedVehicle() {
        if (selectedPosition >= 0 && selectedPosition < serviceList.size()) {
            return serviceList.get(selectedPosition);
        }
        return null;
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {

        private MaterialCardView cardView;
        private AppCompatTextView serviceName, serviceDescription, servicePrice;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            serviceName = itemView.findViewById(R.id.serviceName);
            serviceDescription = itemView.findViewById(R.id.serviceDescription);
            servicePrice = itemView.findViewById(R.id.servicePrice);
        }
    }

}