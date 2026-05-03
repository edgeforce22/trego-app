package com.example.tregoapp.customer.model;

import java.util.List;

public class SOSRequest {

    private String customerId;
    private double latitude;
    private double longitude;
    private String address;
    private List<String> problemTypes;

    public SOSRequest(String customerId, double latitude, double longitude, String address, List<String> problemTypes) {
        this.customerId = customerId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.problemTypes = problemTypes;
    }
}