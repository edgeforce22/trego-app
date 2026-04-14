package com.example.tregoapp.mechanic.model;

import com.google.gson.annotations.SerializedName;

public class ServiceRequest {

    @SerializedName("_id")
    private String id;

    @SerializedName("customerId")
    private String customerId;

    @SerializedName("shopId")
    private String shopId;

    @SerializedName("mechanicId")
    private String mechanicId;

    @SerializedName("vehicleId")
    private String vehicleId;

    @SerializedName("serviceId")
    private String serviceId;

    @SerializedName("problemDescription")
    private String problemDescription;

    @SerializedName("customerLocation")
    private Location customerLocation;

    @SerializedName("mechanicLocation")
    private Location mechanicLocation;

    @SerializedName("totalPrice")
    private double totalPrice;

    @SerializedName("totalDistance")
    private double totalDistance;

    @SerializedName("totalDuration")
    private double totalDuration;

    @SerializedName("status")
    private String status;

    @SerializedName("paymentStatus")
    private String paymentStatus;

    @SerializedName("type")
    private String type;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("customerName")
    private String customerName;

    public ServiceRequest(String customerId, String shopId, String vehicleId, String serviceId, String problemDescription, Location customerLocation, double totalPrice, double totalDistance) {
        this.customerId = customerId;
        this.shopId = shopId;
        this.vehicleId = vehicleId;
        this.serviceId = serviceId;
        this.problemDescription = problemDescription;
        this.customerLocation = customerLocation;
        this.totalPrice = totalPrice;
        this.totalDistance = totalDistance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getMechanicId() {
        return mechanicId;
    }

    public void setMechanicId(String mechanicId) {
        this.mechanicId = mechanicId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    public Location getCustomerLocation() {
        return customerLocation;
    }

    public void setCustomerLocation(Location customerLocation) {
        this.customerLocation = customerLocation;
    }

    public Location getMechanicLocation() {
        return mechanicLocation;
    }

    public void setMechanicLocation(Location mechanicLocation) {
        this.mechanicLocation = mechanicLocation;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public double getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(double totalDuration) {
        this.totalDuration = totalDuration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
