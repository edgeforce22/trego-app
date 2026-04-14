package com.example.tregoapp.customer.model;

public class FCMTokenRequest {

    private String customerId;
    private String fcmToken;

    public FCMTokenRequest(String customerId, String fcmToken) {
        this.customerId = customerId;
        this.fcmToken = fcmToken;
    }
}
