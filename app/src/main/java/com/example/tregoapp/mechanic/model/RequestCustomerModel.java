package com.example.tregoapp.mechanic.model;

import java.io.Serializable;

public class RequestCustomerModel implements Serializable {
    private String customerName;
    private String address;
    private String service;
    private String distance;
    private String duration;

    public RequestCustomerModel() {}

    public RequestCustomerModel(String customerName, String address, String service, String distance, String duration) {
        this.customerName = customerName;
        this.address = address;
        this.service = service;
        this.distance = distance;
        this.duration = duration;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCustomerName() {
        return customerName;
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

    public String getDuration() {
        return duration;
    }
}
