package com.example.tregoapp.customer.model;

public class RateShopRequest {

    private String shopId;
    private String userId;
    private float rating;

    public RateShopRequest(
            String shopId,
            String userId,
            float rating
    ) {

        this.shopId = shopId;
        this.userId = userId;
        this.rating = rating;
    }

    public String getShopId() {
        return shopId;
    }

    public String getUserId() {
        return userId;
    }

    public float getRating() {
        return rating;
    }
}