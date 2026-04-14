package com.example.tregoapp.customer.model;

import com.google.gson.annotations.SerializedName;

public class CreateRequest {

    @SerializedName("customerId")
    private String customerId;

    @SerializedName("shopId")
    private String mechanicId;

    @SerializedName("vehicleType")
    private String vehicleType;

    @SerializedName("problemDescription")
    private String problemDescription;

    @SerializedName("location")
    private Location location;

    @SerializedName("totalDistance")
    private Double totalDistance;

    @SerializedName("totalDuration")
    private Double totalDuration;

    public CreateRequest(String customerId, String mechanicId, String vehicleType, String problemDescription, Location location, Double totalDistance, Double totalDuration) {
        this.customerId = customerId;
        this.mechanicId = mechanicId;
        this.vehicleType = vehicleType;
        this.problemDescription = problemDescription;
        this.location = location;
        this.totalDistance = totalDistance;
        this.totalDuration = totalDuration;
    }
}
