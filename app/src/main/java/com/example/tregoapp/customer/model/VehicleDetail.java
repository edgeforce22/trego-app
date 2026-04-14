package com.example.tregoapp.customer.model;

import com.google.gson.annotations.SerializedName;

public class VehicleDetail {

    @SerializedName("_id")
    private String vehicleId;
    @SerializedName("customerId")
    private String customerId;
    @SerializedName("vehicleType")
    private String vehicleType;
    @SerializedName("vehicleBrand")
    private String vehicleBrand;
    @SerializedName("vehicleModel")
    private String vehicleModel;
    @SerializedName("registrationNumber")
    private String registrationNumber;

    public VehicleDetail(String customerId, String vehicleType, String vehicleBrand, String vehicleModel, String registrationNumber) {
        this.customerId = customerId;
        this.vehicleType = vehicleType;
        this.vehicleBrand = vehicleBrand;
        this.vehicleModel = vehicleModel;
        this.registrationNumber = registrationNumber;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleBrand() {
        return vehicleBrand;
    }

    public void setVehicleBrand(String vehicleBrand) {
        this.vehicleBrand = vehicleBrand;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }
}
