package com.example.tregoapp.mechanic.model;

public class FCMTokenRequest {

    private String mechanicId;
    private String fcmToken;

    public FCMTokenRequest(String mechanicId, String fcmToken) {
        this.mechanicId = mechanicId;
        this.fcmToken = fcmToken;
    }
}
