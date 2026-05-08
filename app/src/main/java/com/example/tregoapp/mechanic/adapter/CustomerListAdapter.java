package com.example.tregoapp.mechanic.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tregoapp.BuildConfig;
import com.example.tregoapp.R;
import com.example.tregoapp.mechanic.listener.OnItemClickListener;
import com.example.tregoapp.mechanic.model.ServiceRequest;
import com.example.tregoapp.mechanic.utils.Utility;
import com.google.android.material.button.MaterialButton;

public class CustomerListAdapter extends ListAdapter<ServiceRequest, CustomerListAdapter.MechanicViewHolder> {

    private final OnItemClickListener listener;
    private final Utility utility = new Utility();

    private final String BASE_URL = BuildConfig.BASE_URL_ENDPOINT;
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_SOS = 1;

    public CustomerListAdapter(OnItemClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        ServiceRequest item = getItem(position);
        if (item != null && "SOS".equalsIgnoreCase(item.getType())) {
            return TYPE_SOS;
        }
        return TYPE_NORMAL;
    }

    @NonNull
    @Override
    public MechanicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutRes = viewType == TYPE_SOS ? R.layout.sos_request_item_layout : R.layout.request_item_layout;
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layoutRes, parent, false);
        return new MechanicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MechanicViewHolder holder, int position) {

        ServiceRequest item = getItem(position);
        if (item == null) return;

        // Display customer name from the model
        String name = item.getCustomerName();
        if (name == null || name.isEmpty() || "N/A".equals(name)) {
            holder.tvCustomerName.setText("SOS".equalsIgnoreCase(item.getType()) ? "Emergency Help Needed" : "Customer");
        } else {
            holder.tvCustomerName.setText(name);
        }

        if (item.getCustomerLocation() != null) {
            holder.tvCustomerAddress.setText(item.getCustomerLocation().getAddress());
            // Normal layout has tvMechanicAddress, SOS layout might not
            if (holder.tvMechanicAddress != null) {
                holder.tvMechanicAddress.setText(item.getCustomerLocation().getAddress());
            }
        } else {
            holder.tvCustomerAddress.setText("N/A");
            if (holder.tvMechanicAddress != null) {
                holder.tvMechanicAddress.setText("N/A");
            }
        }

        if ("SOS".equalsIgnoreCase(item.getType())) {

            holder.tvService.setText(

                    item.getServiceName() != null
                            ? item.getServiceName()
                            : "Emergency Assistance"
            );

            holder.tvProblemDescription.setText(

                    item.getProblemDescription() != null
                            ? item.getProblemDescription()
                            : "Emergency vehicle issue reported"
            );

            holder.tvDistance.setText(

                    item.getTotalDistance() > 0

                            ? String.format(
                            "%.1f km away",
                            item.getTotalDistance()
                    )

                            : "Nearby"
            );

            if (holder.tvDuration != null) {

                holder.tvDuration.setText(

                        String.format(
                                "%.0f mins",
                                item.getTotalDuration()
                        )
                );
            }
        } else {
            String serviceText =
                    item.getServiceName();

            if (
                    item.getServiceDescription() != null
                            &&
                            !item.getServiceDescription().isEmpty()
            ) {

//                serviceText =
//                        serviceText
//                                + " • "
//                                + item.getServiceDescription();
            }

            if (item.getVehicle() != null) {

                holder.tvVehicleType.setText(

                        item.getVehicle()
                                .getVehicleType()
                                .toUpperCase()
                );

                holder.tvVehicleBrandModel.setText(

                        item.getVehicle()
                                .getVehicleBrand()

                                + " "

                                +

                                item.getVehicle()
                                        .getVehicleModel()
                );

                String regNo =
                        item.getVehicle()
                                .getRegistrationNumber();

                if (
                        regNo != null
                                &&
                                !regNo.isEmpty()
                ) {

                    holder.tvRegistrationNumber
                            .setVisibility(View.VISIBLE);

                    holder.tvRegistrationNumber
                            .setText(regNo);

                } else {

                    holder.tvRegistrationNumber
                            .setVisibility(View.GONE);
                }

            } else {

                holder.tvVehicleType.setText("N/A");

                holder.tvVehicleBrandModel.setText("Vehicle");

                holder.tvRegistrationNumber
                        .setVisibility(View.GONE);
            }

            holder.tvService.setText(
                    serviceText != null
                            ? serviceText
                            : "N/A"
            );
            holder.tvDistance.setText(item.getTotalDistance() + " km");
            if (holder.tvDuration != null) {
                holder.tvDuration.setText(item.getTotalDuration() + " min");
            }
        }


        /*
         * REQUEST IMAGE
         */
        if (
                holder.rvRequestImages != null
                        &&
                        item.getRequestImages() != null
                        &&
                        !item.getRequestImages().isEmpty()
        ) {

            holder.rvRequestImages.setVisibility(
                    View.VISIBLE
            );

            holder.rvRequestImages.setLayoutManager(

                    new LinearLayoutManager(

                            holder.itemView.getContext(),

                            LinearLayoutManager.HORIZONTAL,

                            false
                    )
            );

            RequestImagesAdapter imagesAdapter =
                    new RequestImagesAdapter(

                            item.getRequestImages()
                    );

            holder.rvRequestImages.setAdapter(
                    imagesAdapter
            );

        } else {

            if (holder.rvRequestImages != null) {

                holder.rvRequestImages.setVisibility(
                        View.GONE
                );
            }
        }

        if (holder.tvTotalPrice != null) {

            holder.tvTotalPrice.setText(

                    String.format(
                            "₹ %.0f",
                            item.getTotalPrice()
                    )
            );
        }

        if (holder.tvProblemDescription != null) {

            holder.tvProblemDescription.setText(
                    item.getProblemDescription()
            );
        }

        holder.tvCreatedAt.setText(
                item.getCreatedAt() != null
                        ? utility.formatDate(item.getCreatedAt())
                        : "N/A"
        );

        // Reset button state (important for RecyclerView reuse
        holder.acceptBtn.setEnabled(true);
        holder.cancelBtn.setEnabled(true);

        // Accept Click
        holder.acceptBtn.setOnClickListener(v -> {
            holder.acceptBtn.setEnabled(false); // prevent double click
            listener.onClick(item.getId());
        });

        // Cancel Click
        holder.cancelBtn.setOnClickListener(v -> {
            holder.cancelBtn.setEnabled(false);
            listener.onClick2(item.getId());
        });
    }

    // ================= VIEW HOLDER =================

    public static class MechanicViewHolder extends RecyclerView.ViewHolder {

        TextView tvCustomerAddress, tvCustomerName, tvMechanicAddress,
                tvService, tvDistance, tvDuration, tvCreatedAt, tvTotalPrice, tvProblemDescription;

        TextView tvVehicleType,
                tvVehicleBrandModel,
                tvRegistrationNumber;

        MaterialButton acceptBtn, cancelBtn;

        RecyclerView rvRequestImages;

        public MechanicViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTotalPrice =
                    itemView.findViewById(
                            R.id.tvTotalPrice
                    );
            tvProblemDescription = itemView.findViewById(R.id.tvProblemDescription);
            rvRequestImages = itemView.findViewById(R.id.rvRequestImages);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvCustomerAddress = itemView.findViewById(R.id.tvCustomerAddress);
            tvMechanicAddress = itemView.findViewById(R.id.tvMechanicAddress); // Optional in SOS
            tvService = itemView.findViewById(R.id.tvServices);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvDuration = itemView.findViewById(R.id.tvDuration); // Optional in SOS
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            acceptBtn = itemView.findViewById(R.id.acceptBtn);
            cancelBtn = itemView.findViewById(R.id.cancelBtn);
            tvVehicleType =
                    itemView.findViewById(
                            R.id.tvVehicleType
                    );

            tvVehicleBrandModel =
                    itemView.findViewById(
                            R.id.tvVehicleBrandModel
                    );

            tvRegistrationNumber =
                    itemView.findViewById(
                            R.id.tvRegistrationNumber
                    );
        }
    }

    // ================= DIFF UTIL =================

    private static final DiffUtil.ItemCallback<ServiceRequest> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ServiceRequest>() {

                @Override
                public boolean areItemsTheSame(
                        @NonNull ServiceRequest oldItem,
                        @NonNull ServiceRequest newItem) {

                    return java.util.Objects.equals(
                            oldItem.getId(),
                            newItem.getId()
                    );
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull ServiceRequest oldItem,
                        @NonNull ServiceRequest newItem
                ) {

                    return java.util.Objects.equals(
                            oldItem.getId(),
                            newItem.getId()
                    )

                            &&

                            java.util.Objects.equals(
                                    oldItem.getStatus(),
                                    newItem.getStatus()
                            )

                            &&

                            java.util.Objects.equals(
                                    oldItem.getType(),
                                    newItem.getType()
                            )

                            &&

                            java.util.Objects.equals(
                                    oldItem.getProblemDescription(),
                                    newItem.getProblemDescription()
                            )

                            &&

                            java.util.Objects.equals(
                                    oldItem.getRequestImages(),
                                    newItem.getRequestImages()
                            )

                            &&

                            oldItem.getTotalDistance()
                                    ==
                                    newItem.getTotalDistance()

                            &&

                            oldItem.getTotalDuration()
                                    ==
                                    newItem.getTotalDuration();
                }

//                @Override
//                public boolean areContentsTheSame(
//                        @NonNull ServiceRequest oldItem,
//                        @NonNull ServiceRequest newItem) {
//
//                    return java.util.Objects.equals(oldItem.getId(), newItem.getId()) &&
//                            java.util.Objects.equals(oldItem.getStatus(), newItem.getStatus()) &&
//                            java.util.Objects.equals(oldItem.getType(), newItem.getType()) &&
//                            java.util.Objects.equals(oldItem.getProblemDescription(), newItem.getProblemDescription()) &&
//                            oldItem.getTotalDistance() == newItem.getTotalDistance() &&
//                            oldItem.getTotalDuration() == newItem.getTotalDuration();
//                }
            };

//    private static final DiffUtil.ItemCallback<ServiceRequest> DIFF_CALLBACK =
//            new DiffUtil.ItemCallback<ServiceRequest>() {
//
//                @Override
//                public boolean areItemsTheSame(@NonNull ServiceRequest oldItem,
//                                               @NonNull ServiceRequest newItem) {
//                    return oldItem.getId().equals(newItem.getId());
//                }
//
//                @Override
//                public boolean areContentsTheSame(@NonNull ServiceRequest oldItem,
//                                                  @NonNull ServiceRequest newItem) {
//
//                    // Comparing important fields only
//                    return oldItem.getStatus().equals(newItem.getStatus()) &&
//                            oldItem.getTotalDistance() == newItem.getTotalDistance() &&
//                            oldItem.getTotalDuration() == newItem.getTotalDuration() &&
//                            oldItem.getServiceId().equals(newItem.getServiceId()) &&
//                            oldItem.getCustomerId().equals(newItem.getCustomerId());
//                }
//            };
}