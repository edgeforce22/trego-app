package com.example.tregoapp.mechanic.model;

import com.example.tregoapp.mechanic.model.ShopDetail;
import com.example.tregoapp.mechanic.model.response.User;

public class RegisterShopResponse {

    private ShopDetail shop;
    private User mechanic;

    public ShopDetail getShop() {
        return shop;
    }

    public User getMechanic() {
        return mechanic;
    }
}