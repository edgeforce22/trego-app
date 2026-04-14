package com.example.tregoapp.customer.model.response;

import com.google.gson.annotations.SerializedName;

public class ApiError {
    @SerializedName("code")
    private int code;
    @SerializedName("details")
    private String details;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
