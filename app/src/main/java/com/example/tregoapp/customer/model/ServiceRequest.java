package com.example.tregoapp.customer.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ServiceRequest {

    @SerializedName("_id")
    private String id;

    @SerializedName("requestImages")
    private List<String> requestImages;

    @SerializedName("customerId")
    private String customerId;

    @SerializedName("customerName")
    private String customerName;

    @SerializedName("customerPhoneNumber")
    private String customerPhoneNumber;

    @SerializedName("shopId")
    private String shopId;

    @SerializedName("mechanicId")
    private String mechanicId;

    @SerializedName("vehicleId")
    private String vehicleId;

    @SerializedName("serviceId")
    private String serviceId;

    @SerializedName("serviceName")
    private String serviceName;

    @SerializedName("serviceDescription")
    private String serviceDescription;

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

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("shopName")
    private String shopName;

    @SerializedName("isActive")
    private boolean isActive;

    @SerializedName("isSOS")
    private boolean isSOS;

    @SerializedName("rejectedCount")
    private String rejectedCount;

    @SerializedName("totalMechanics")
    private String totalMechanics;

    @SerializedName("shopRating")
    private Double shopRating;

    @SerializedName("shopRatingCount")
    private Double shopRatingCount;

    @SerializedName("shopRatingSum")
    private Double shopRatingSum;

    @SerializedName("isCompletelyRejected")
    private boolean isCompletelyRejected;

    @SerializedName("mechanicName")
    private String mechanicName;

    @SerializedName("mechanicPhoneNumber")
    private String mechanicPhoneNumber;


    public ServiceRequest(String customerId, String shopId, String vehicleId, String serviceId, String problemDescription, Location customerLocation, double totalPrice, double totalDistance, double totalDuration) {
        this.customerId = customerId;
        this.shopId = shopId;
        this.vehicleId = vehicleId;
        this.serviceId = serviceId;
        this.problemDescription = problemDescription;
        this.customerLocation = customerLocation;
        this.totalPrice = totalPrice;
        this.totalDistance = totalDistance;
        this.totalDuration = totalDuration;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getRejectedCount() {
        return rejectedCount;
    }

    public String getTotalMechanics() {
        return totalMechanics;
    }

    public boolean getIsCompletelyRejected() {
        return isCompletelyRejected;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public boolean getIsSOS() {
        return isSOS;
    }

    public boolean isCompletelyRejected() {
        return isCompletelyRejected;
    }

    public List<String> getRequestImages() {
        return requestImages;
    }

    public void setRequestImages(List<String> requestImages) {
        this.requestImages = requestImages;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getMechanicName() {
        return mechanicName;
    }

    public Double getShopRating() {
        return shopRating;
    }

    public Double getShopRatingCount() {
        return shopRatingCount;
    }

    public Double getShopRatingSum() {
        return shopRatingSum;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public String getMechanicPhoneNumber() {
        return mechanicPhoneNumber;
    }
}
