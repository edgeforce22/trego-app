package com.example.tregoapp.mechanic.model;

import java.util.List;

public class CreateServicesRequest {

    private String shopId;

    private List<ServiceDetail> services;

    public CreateServicesRequest(
            String shopId,
            List<ServiceDetail> services
    ) {

        this.shopId = shopId;
        this.services = services;
    }

    public String getShopId() {
        return shopId;
    }

    public List<ServiceDetail> getServices() {
        return services;
    }
}