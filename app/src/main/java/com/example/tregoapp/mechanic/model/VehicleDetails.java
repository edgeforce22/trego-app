package com.example.tregoapp.mechanic.model;

import com.google.gson.annotations.SerializedName;

public class VehicleDetails {

    @SerializedName("_id")
    private String id;

    @SerializedName("vehicleType")
    private String vehicleType;

    @SerializedName("vehicleBrand")
    private String vehicleBrand;

    @SerializedName("vehicleModel")
    private String vehicleModel;

    @SerializedName("registrationNumber")
    private String registrationNumber;

    public String getId() {
        return id;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public String getVehicleBrand() {
        return vehicleBrand;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }
}