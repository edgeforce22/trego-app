package com.example.tregoapp.mechanic.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tregoapp.R;
import com.example.tregoapp.mechanic.model.PredefinedService;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class PredefinedServiceAdapter extends RecyclerView.Adapter<PredefinedServiceAdapter.ViewHolder> {

    private final List<PredefinedService> services;

    public PredefinedServiceAdapter(List<PredefinedService> services) {
        this.services = services;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_predefined_service,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        PredefinedService service =
                services.get(position);

        /*
         * REMOVE OLD LISTENER
         */
        holder.checkService.setOnCheckedChangeListener(null);

        /*
         * REMOVE OLD TEXTWATCHER
         */
        if (holder.textWatcher != null) {

            holder.etPrice.removeTextChangedListener(
                    holder.textWatcher
            );
        }

        /*
         * SET DATA
         */
        holder.tvServiceName.setText(
                service.getServiceName()
        );

        holder.tvDescription.setText(
                service.getDescription()
        );

        holder.etPrice.setText(
                service.getPrice() != null
                        ? service.getPrice()
                        : ""
        );

        holder.checkService.setChecked(
                service.isSelected()
        );

        holder.priceLayout.setVisibility(
                service.isSelected()
                        ? View.VISIBLE
                        : View.GONE
        );

        /*
         * CHECK CHANGE
         */
        holder.checkService.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    service.setSelected(isChecked);

                    holder.priceLayout.setVisibility(
                            isChecked
                                    ? View.VISIBLE
                                    : View.GONE
                    );
                });

        /*
         * TEXT WATCHER
         */
        holder.textWatcher =
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {

                        service.setPrice(
                                s.toString()
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {
                    }
                };

        holder.etPrice.addTextChangedListener(
                holder.textWatcher
        );
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public List<PredefinedService> getSelectedServices() {

        List<PredefinedService> selected =
                new ArrayList<>();

        for (PredefinedService service
                : services) {

            if (service.isSelected()) {

                selected.add(service);
            }
        }

        return selected;
    }

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        CheckBox checkService;

        TextView tvServiceName;
        TextView tvDescription;

        TextInputLayout priceLayout;

        TextInputEditText etPrice;

        TextWatcher textWatcher;

        public ViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            checkService =
                    itemView.findViewById(
                            R.id.checkService
                    );

            tvServiceName =
                    itemView.findViewById(
                            R.id.tvServiceName
                    );

            tvDescription =
                    itemView.findViewById(
                            R.id.tvDescription
                    );

            priceLayout =
                    itemView.findViewById(
                            R.id.priceLayout
                    );

            etPrice =
                    itemView.findViewById(
                            R.id.etPrice
                    );
        }
    }
}