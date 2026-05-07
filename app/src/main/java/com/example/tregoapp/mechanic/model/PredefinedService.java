package com.example.tregoapp.mechanic.model;

public class PredefinedService {

    private String serviceName;
    private String description;
    private boolean selected;
    private String price;

    public PredefinedService(String serviceName,
                             String description) {

        this.serviceName = serviceName;
        this.description = description;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}