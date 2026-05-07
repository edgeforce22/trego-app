package com.example.tregoapp.customer.adapter;

import static com.example.tregoapp.BuildConfig.BASE_URL;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tregoapp.customer.listener.OnItemClickListener;
import com.example.tregoapp.R;
import com.example.tregoapp.customer.model.ShopDetail;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class NearbyShopsAdapter extends RecyclerView.Adapter<NearbyShopsAdapter.CustomerViewHolder> {

    private final List<ShopDetail> shops = new ArrayList<>();
    private final OnItemClickListener onItemClickListener;

    public NearbyShopsAdapter(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setShopDetailList(List<ShopDetail> data) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return shops.size();
            }

            @Override
            public int getNewListSize() {
                return data.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return shops.get(oldItemPosition).getShopId().equals(data.get(newItemPosition).getShopId());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return shops.get(oldItemPosition).equals(data.get(newItemPosition));
            }
        });
        shops.clear();
        shops.addAll(data);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_item_layout, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        ShopDetail shop = shops.get(position);

        holder.tvShopName.setText(shop.getShopName());
        holder.tvRating.setText(String.valueOf(shop.getRating()));

        String vehicles = "";

        if (shop.getSupportedVehicles() != null
                && !shop.getSupportedVehicles().isEmpty()) {

            vehicles = TextUtils.join(
                    ", ",
                    shop.getSupportedVehicles()
            ) + " vehicle services";

        } else {
            vehicles = "No vehicles available";
        }

        holder.tvService.setText(vehicles);

        holder.tvAddress.setText(shop.getAddress());
        holder.tvDistance.setText(shop.getDistance() + " km");
        holder.tvDuration.setText(shop.getEstimatedTime() + " min");

        /*
         * SHOP IMAGE
         */
        /*
         * SHOP IMAGE
         */
        String imageUrl =
                shop.getShopImage();

        Log.d(
                "SHOP_IMAGE",
                String.valueOf(imageUrl)
        );

        if (
                imageUrl != null
                        &&
                        !imageUrl.isEmpty()
        ) {

            String finalUrl =
                    imageUrl.startsWith("http")
                            ? imageUrl
                            : BASE_URL + "/" + imageUrl;

            Log.d(
                    "FINAL_IMAGE_URL",
                    finalUrl
            );

            Glide.with(holder.itemView.getContext())

                    .load(finalUrl)

                    .placeholder(
                            R.drawable.shop
                    )

                    .error(
                            R.drawable.shop
                    )

                    .centerCrop()

                    .into(holder.shopImage);

        } else {

            holder.shopImage.setImageResource(
                    R.drawable.shop
            );
        }


        holder.selectBtn.setOnClickListener(v -> {
            onItemClickListener.onClick(shop);
        });

        holder.shopCardView.setOnClickListener(v -> {
            onItemClickListener.onClickShop(shop);
        });

    }

    @Override
    public int getItemCount() {
        return shops.size();
    }

    public static class CustomerViewHolder extends RecyclerView.ViewHolder {

        private MaterialCardView shopCardView;
        private ImageView shopImage;
        private TextView tvShopName, tvRating, tvAddress, tvService, tvDistance, tvDuration, selectBtn;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);

            shopCardView = itemView.findViewById(R.id.shopCardView);
            tvShopName = itemView.findViewById(R.id.tvShopName);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvService = itemView.findViewById(R.id.tvService);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            selectBtn = itemView.findViewById(R.id.selectBtn);
            shopImage = itemView.findViewById(R.id.shopImage);
        }
    }
}
