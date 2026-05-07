package com.example.tregoapp.mechanic.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tregoapp.R;

import java.util.List;

public class RequestImagesAdapter extends
        RecyclerView.Adapter<RequestImagesAdapter.ImageViewHolder> {

    private final List<String> images;

    public RequestImagesAdapter(
            List<String> images
    ) {

        this.images = images;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(
                                R.layout.request_image_item,
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

        String imageUrl =
                images.get(position);

        Glide.with(holder.itemView.getContext())

                .load(imageUrl)

                .placeholder(R.drawable.shop)

                .error(R.drawable.shop)

                .diskCacheStrategy(
                        com.bumptech.glide.load.engine.DiskCacheStrategy.ALL
                )

                .skipMemoryCache(false)

                .centerCrop()

                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    @Override
    public long getItemId(int position) {
        return images.get(position).hashCode();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class ImageViewHolder
            extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ImageViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            imageView =
                    itemView.findViewById(
                            R.id.imageView
                    );
        }
    }
}