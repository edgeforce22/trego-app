package com.example.tregoapp.mechanic.model;

import com.example.tregoapp.mechanic.model.response.User;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MechanicDetails {

    @SerializedName("mechanic")
    private User mechanicDetails;

    @SerializedName("shop")
    private ShopDetail shopDetail;

    @SerializedName("services")
    private List<ServiceDetail> serviceDetail;

    public MechanicDetails(User mechanicDetails, ShopDetail shopDetail, List<ServiceDetail> serviceDetail) {
        this.mechanicDetails = mechanicDetails;
        this.shopDetail = shopDetail;
        this.serviceDetail = serviceDetail;
    }

    public User getMechanicDetails() {
        return mechanicDetails;
    }

    public void setMechanicDetails(User mechanicDetails) {
        this.mechanicDetails = mechanicDetails;
    }

    public ShopDetail getShopDetail() {
        return shopDetail;
    }

    public void setShopDetail(ShopDetail shopDetail) {
        this.shopDetail = shopDetail;
    }

    public List<ServiceDetail> getServiceDetail() {
        return serviceDetail;
    }

    public void setServiceDetail(List<ServiceDetail> serviceDetail) {
        this.serviceDetail = serviceDetail;
    }
}
