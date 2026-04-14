package com.example.tregoapp.customer.model;

import com.google.gson.annotations.SerializedName;

public class NearbyRequest {

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    public NearbyRequest(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
