package com.example.tregoapp.mechanic.model;

import com.google.gson.annotations.SerializedName;

public class ServiceDetail {

    @SerializedName("_id")
    private String serviceId;

    private String shopId;
    private String service;
    private String description;
    private double price;

    public ServiceDetail(String shopId, String service, String description, double price) {
        this.shopId = shopId;
        this.service = service;
        this.description = description;
        this.price = price;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
