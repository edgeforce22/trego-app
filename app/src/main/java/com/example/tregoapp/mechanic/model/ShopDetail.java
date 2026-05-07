package com.example.tregoapp.mechanic.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ShopDetail {

    @SerializedName("_id")
    private String shopId;
    @SerializedName("ownerId")
    private String ownerId;
    @SerializedName("shopImage")
    private String shopImage;
    @SerializedName("shopName")
    private String shopName;

    @SerializedName("rating")
    private double rating;

    @SerializedName("ratingCount")
    private Integer ratingCount;

    @SerializedName("ratingSum")
    private double ratingSum;
    @SerializedName("phoneNumber")
    private String phoneNumber;
    @SerializedName("address")
    private String address;
    @SerializedName("latitude")
    private double latitude;
    @SerializedName("longitude")
    private double longitude;
    @SerializedName("openingTime")
    private String openingTime;
    @SerializedName("closingTime")
    private String closingTime;
    @SerializedName("workers")
    private List<String> workers;
    @SerializedName("supportedVehicles")
    private List<String> supportedVehicles;

    public ShopDetail(String ownerId, String shopName, String phoneNumber, String address, double latitude, double longitude, String openingTime, String closingTime, List<String> supportedVehicles) {
        this.ownerId = ownerId;
        this.shopName = shopName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.supportedVehicles = supportedVehicles;
    }

    public String getShopId() {
        return shopId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
    }

    public List<String> getWorkers() {
        return workers;
    }

    public void setWorkers(List<String> workers) {
        this.workers = workers;
    }

    public List<String> getSupportedVehicles() {
        return supportedVehicles;
    }

    public void setSupportedVehicles(List<String> supportedVehicles) {
        this.supportedVehicles = supportedVehicles;
    }

    public String getShopImage() {
        return shopImage;
    }

    public double getRating() {
        return rating;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public double getRatingSum() {
        return ratingSum;
    }
}
