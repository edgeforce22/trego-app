package com.example.tregoapp.mechanic.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tregoapp.R;
import com.example.tregoapp.customer.listener.OnItemClickListener2;
import com.example.tregoapp.mechanic.model.response.User;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class WorkersAdapter extends RecyclerView.Adapter<WorkersAdapter.ViewHolder> {

    private List<User> workerList = new ArrayList<>();
    private OnItemClickListener2 listener;

    public WorkersAdapter(OnItemClickListener2 listener2) {
        this.listener = listener2;
    }

    public void setWorkerList(List<User> list) {
        this.workerList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.worker_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        User worker = workerList.get(position);

        holder.tvWorkerName.setText(worker.getName() != null ? worker.getName() : "Unknown");

        String status = worker.getStatus() != null ? worker.getStatus() : "inactive";

        if (status.equalsIgnoreCase("active")) {
            holder.tvStatus.setText("Available");
//            holder.tvStatus.setTextColor(Color.parseColor("#34C759"));
            holder.statusIndicator.setCardBackgroundColor(Color.parseColor("#34C759"));

        } else {
            holder.tvStatus.setText("Unavailable");
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.darker_gray));
            holder.statusIndicator.setCardBackgroundColor(Color.parseColor("#E05252"));
        }

        holder.callButton.setOnClickListener(v -> {
            listener.onClick(worker.getPhoneNumber());
        });
    }

    @Override
    public int getItemCount() {
        return workerList != null ? workerList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvWorkerName, tvStatus;
        MaterialCardView statusIndicator, callButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvWorkerName = itemView.findViewById(R.id.tvWorkerName);
            statusIndicator = itemView.findViewById(R.id.statusIndicator);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            callButton = itemView.findViewById(R.id.callButton);
        }
    }
}