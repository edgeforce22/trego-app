package com.example.tregoapp.customer.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tregoapp.R;
import com.example.tregoapp.customer.listener.OnItemClickListener3;
import com.example.tregoapp.customer.model.VehicleDetail;
import com.example.tregoapp.customer.utils.Utility;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class VehicleListAdapter
        extends RecyclerView.Adapter<VehicleListAdapter.VehicleViewHolder> {

    private final List<VehicleDetail> vehicleList = new ArrayList<>();
    private final OnItemClickListener3 listener;

    private int selectedPosition = -1;

    // Enable / Disable item click
    private boolean isItemClickable = true;

    public VehicleListAdapter(List<VehicleDetail> list,
                              OnItemClickListener3 listener) {

        if (list != null) {
            vehicleList.addAll(list);
        }

        this.listener = listener;
    }

    // Control click from fragment
    public void setItemClickable(boolean clickable) {
        isItemClickable = clickable;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vehicle_item_layout, parent, false);

        return new VehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull VehicleViewHolder holder,
            int position
    ) {

        VehicleDetail vehicle = vehicleList.get(position);

        holder.vehicleIcon.setImageResource(
                Utility.getInstance()
                        .vehicleIconConvertor(
                                vehicle.getVehicleType()
                        )
        );

        holder.vehicleType.setText(
                vehicle.getVehicleType().toUpperCase()
        );

        holder.vehicleBrand.setText(
                vehicle.getVehicleBrand()
        );

        holder.vehicleModel.setText(
                vehicle.getVehicleModel()
        );

        // Registration Number
        if (vehicle.getRegistrationNumber() != null &&
                !vehicle.getRegistrationNumber().trim().isEmpty()) {

            holder.vehicleRegistrationNumber
                    .setVisibility(View.VISIBLE);

            holder.vehicleRegistrationNumber
                    .setText(vehicle.getRegistrationNumber());

        } else {

            holder.vehicleRegistrationNumber
                    .setVisibility(View.GONE);
        }

        // Selection UI
        if (isItemClickable &&
                selectedPosition == position) {

            holder.cardView.setStrokeColor(
                    Color.parseColor("#FFE429")
            );

            holder.cardView.setStrokeWidth(3);

            holder.cardView.setCardBackgroundColor(
                    Color.parseColor("#FFFBE6")
            );

        } else {

            holder.cardView.setStrokeColor(
                    Color.parseColor("#E0E0E0")
            );

            holder.cardView.setStrokeWidth(1);

            holder.cardView.setCardBackgroundColor(
                    Color.WHITE
            );
        }

        // Click enable / disable
        if (isItemClickable) {

            holder.itemView.setClickable(true);
            holder.itemView.setFocusable(true);

            holder.itemView.setOnClickListener(v -> {

                int oldPosition = selectedPosition;
                selectedPosition =
                        holder.getAdapterPosition();

                if (oldPosition != -1) {
                    notifyItemChanged(oldPosition);
                }

                notifyItemChanged(selectedPosition);

                if (listener != null) {
                    listener.onClickVehicle(vehicle);
                }
            });

        } else {

            holder.itemView.setClickable(false);
            holder.itemView.setFocusable(false);
            holder.itemView.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    public VehicleDetail getSelectedVehicle() {

        if (selectedPosition >= 0 &&
                selectedPosition < vehicleList.size()) {

            return vehicleList.get(selectedPosition);
        }

        return null;
    }

    public static class VehicleViewHolder
            extends RecyclerView.ViewHolder {

        MaterialCardView cardView;
        AppCompatImageView vehicleIcon;

        AppCompatTextView vehicleType;
        AppCompatTextView vehicleBrand;
        AppCompatTextView vehicleModel;
        AppCompatTextView vehicleRegistrationNumber;

        public VehicleViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            cardView =
                    itemView.findViewById(R.id.cardView);

            vehicleIcon =
                    itemView.findViewById(R.id.vehicleIcon);

            vehicleType =
                    itemView.findViewById(R.id.vehicleType);

            vehicleBrand =
                    itemView.findViewById(R.id.vehicleBrand);

            vehicleModel =
                    itemView.findViewById(R.id.vehicleModel);

            vehicleRegistrationNumber =
                    itemView.findViewById(
                            R.id.vehicleRegistration
                    );
        }
    }
}