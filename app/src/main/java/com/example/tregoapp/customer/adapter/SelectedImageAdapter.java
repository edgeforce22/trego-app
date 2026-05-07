package com.example.tregoapp.customer.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tregoapp.R;

import java.util.List;

public class SelectedImageAdapter
        extends RecyclerView.Adapter<SelectedImageAdapter.ImageViewHolder> {

    private List<Uri> imageUris;

    public SelectedImageAdapter(List<Uri> imageUris) {
        this.imageUris = imageUris;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_selected_image,
                        parent,
                        false
                );

        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ImageViewHolder holder,
            int position
    ) {

        Glide.with(holder.itemView.getContext())
                .load(imageUris.get(position))
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    static class ImageViewHolder
            extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {

            super(itemView);

            imageView =
                    itemView.findViewById(R.id.imageView);
        }
    }
}