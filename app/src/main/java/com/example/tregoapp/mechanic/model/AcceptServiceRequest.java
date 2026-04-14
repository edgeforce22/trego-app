package com.example.tregoapp.mechanic.model;

public class AcceptServiceRequest {
    private String requestId;
    private String mechanicId;
    private Location mechanicLocation;

    public AcceptServiceRequest(String requestId, String mechanicId, Location mechanicLocation) {
        this.requestId = requestId;
        this.mechanicId = mechanicId;
        this.mechanicLocation = mechanicLocation;
    }
}
