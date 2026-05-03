package com.example.tregoapp.customer.model;

import com.google.gson.annotations.SerializedName;

public class GetRequestById {

    @SerializedName("id")
    private String id;

    public GetRequestById(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}