package com.example.tregoapp.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ShopDetail implements Serializable {

    @SerializedName("_id")
    private String shopId;
    @SerializedName("ownerId")
    private String ownerId;
    @SerializedName("shopImage")
    private String shopImage;
    @SerializedName("shopName")
    private String shopName;
    @SerializedName("phoneNumber")
    private String phoneNumber;
    @SerializedName("rating")
    private double rating;
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
    @SerializedName("distance")
    private double distance;
    @SerializedName("estimatedTime")
    private double estimatedTime;
    @SerializedName("supportedVehicles")
    private List<String> supportedVehicles;

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(double estimatedTime) {
        this.estimatedTime = estimatedTime;
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

    public List<String> getSupportedVehicles() {
        return supportedVehicles;
    }

    public void setSupportedVehicles(List<String> supportedVehicles) {
        this.supportedVehicles = supportedVehicles;
    }

    public String getShopImage() {
        return shopImage;
    }

    public void setShopImage(String shopImage) {
        this.shopImage = shopImage;
    }
}
