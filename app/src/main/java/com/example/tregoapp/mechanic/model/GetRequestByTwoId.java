package com.example.tregoapp.mechanic.model;

import com.google.gson.annotations.SerializedName;

public class GetRequestByTwoId {

    @SerializedName("mechanicId")
    private String mechanicId;

    @SerializedName("shopId")
    private String shopId;

    public GetRequestByTwoId(String mechanicId, String shopId) {
        this.mechanicId = mechanicId;
        this.shopId = shopId;
    }
}