package com.example.tregoapp.mechanic.model;

import com.google.gson.annotations.SerializedName;

public class GetRequestById {

    @SerializedName("id")
    private String id;

    public GetRequestById(String id) {
        this.id = id;
    }
}