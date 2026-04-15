package com.example.tregoapp.mechanic.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.tregoapp.mechanic.model.response.User;

public class SessionManager {

    private static final String PREF_NAME = "mechanic_session";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_SHOP_ID = "user_shop_id";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_USER_PHONE_NUMBER = "user_phone_number";
    private static final String KEY_USER_ADDRESS = "user_address";
    private static final String KEY_USER_STATUS = "user_status";
    private static final String KEY_SHOP_DETAILS = "shop_details";

    private SharedPreferences sharedPreferences;
    private final com.google.gson.Gson gson = new com.google.gson.Gson();

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveUser(User user) {
        sharedPreferences.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USER_SHOP_ID, user.getShopId())
            .putString(KEY_USER_ID, user.getId())
            .putString(KEY_USER_NAME, user.getName())
            .putString(KEY_USER_ROLE, user.getRole())
            .putString(KEY_USER_PHONE_NUMBER, user.getPhoneNumber())
            .putString(KEY_USER_ADDRESS, user.getAddress())
            .putString(KEY_USER_STATUS, user.getStatus())
            .apply();
    }

    public void saveShopDetails(com.example.tregoapp.mechanic.model.ShopDetail shopDetail) {
        sharedPreferences.edit()
                .putString(KEY_SHOP_DETAILS, gson.toJson(shopDetail))
                .apply();
    }

    public com.example.tregoapp.mechanic.model.ShopDetail getShopDetails() {
        String shopJson = sharedPreferences.getString(KEY_SHOP_DETAILS, null);
        if (shopJson == null) return null;
        return gson.fromJson(shopJson, com.example.tregoapp.mechanic.model.ShopDetail.class);
    }

    public User getUser() {
        if (!isLoggedIn()) {
            return null;
        }
        User user = new User();
        user.setShopId(sharedPreferences.getString(KEY_USER_SHOP_ID, ""));
        user.setId(sharedPreferences.getString(KEY_USER_ID, ""));
        user.setName(sharedPreferences.getString(KEY_USER_NAME, ""));
        user.setRole(sharedPreferences.getString(KEY_USER_ROLE, ""));
        user.setPhoneNumber(sharedPreferences.getString(KEY_USER_PHONE_NUMBER, ""));
        user.setAddress(sharedPreferences.getString(KEY_USER_ADDRESS, ""));
        user.setStatus(sharedPreferences.getString(KEY_USER_STATUS, ""));
        return user;
    }

    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, "");
    }

    public String getShopId() {
        return sharedPreferences.getString(KEY_USER_SHOP_ID, "");
    }
    public String getUserRole() {
        return sharedPreferences.getString(KEY_USER_ROLE, "");
    }

//    public void updateShopId(String shopId) {
//        sharedPreferences.edit()
//                .putString(KEY_USER_SHOP_ID, shopId)
//                .apply();
//    }
    public void updateStatus(String status) {
        sharedPreferences.edit()
                .putString(KEY_USER_STATUS, status)
                .apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void clearPref() {
        sharedPreferences.edit().clear().apply();
    }

    @Override
    public String toString() {
        return "Session Data: {" +
                "isLoggedIn=" + String.valueOf(sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)) + ", " +
                "userId='" + sharedPreferences.getString(KEY_USER_ID, "") + "', " +
                "shopId='" + sharedPreferences.getString(KEY_USER_SHOP_ID, "") + "', " +
                "userRole='" + sharedPreferences.getString(KEY_USER_ROLE, "") + "', " +
                "userName='" + sharedPreferences.getString(KEY_USER_NAME, "") + "', " +
                "phoneNumber='" + sharedPreferences.getString(KEY_USER_PHONE_NUMBER, "") + "', " +
                "address='" + sharedPreferences.getString(KEY_USER_ADDRESS, "") + "', " +
                "status='" + sharedPreferences.getString(KEY_USER_STATUS, "") + "'" +
                "}";
    }
}
