package com.example.tregoapp.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MechanicShop implements Serializable {

    @SerializedName("_id")
    private String mechanic_uid;

    @SerializedName("name")
    private String mechanicName;

    @SerializedName("rating")
    private String rating;

    @SerializedName("address")
    private String address;

    @SerializedName("service")
    private String service;

    @SerializedName("distance")
    private String distance;

    public MechanicShop(String mechanic_uid, String mechanicName, String rating, String address, String service, String distance) {
        this.mechanic_uid = mechanic_uid;
        this.mechanicName = mechanicName;
        this.rating = rating;
        this.address = address;
        this.service = service;
        this.distance = distance;
    }

    public String getMechanic_uid() {
        return mechanic_uid;
    }

    public String getMechanicName() {
        return mechanicName;
    }

    public String getRating() {
        return rating;
    }

    public String getAddress() {
        return address;
    }

    public String getService() {
        return service;
    }

    public String getDistance() {
        return distance;
    }

}
