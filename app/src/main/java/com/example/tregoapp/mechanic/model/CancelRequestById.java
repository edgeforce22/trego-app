package com.example.tregoapp.mechanic.model;

import com.google.gson.annotations.SerializedName;

public class CancelRequestById {

    @SerializedName("requestId")
    private String requestId;

    @SerializedName("mechanicId")
    private String mechanicId;

    public CancelRequestById(String requestId, String mechanicId) {
        this.requestId = requestId;
        this.mechanicId = mechanicId;
    }
}
