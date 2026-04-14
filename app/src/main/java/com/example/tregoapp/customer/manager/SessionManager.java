package com.example.tregoapp.customer.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.tregoapp.customer.model.response.User;

public class SessionManager {

    private static final String PREF_NAME = "customer_session";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_PHONE_NUMBER = "user_phone_number";
    private static final String KEY_USER_ADDRESS = "user_address";

    private SharedPreferences sharedPreferences;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveUser(User user) {
        sharedPreferences.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putString(KEY_USER_ID, user.getId())
                .putString(KEY_USER_NAME, user.getName())
                .putString(KEY_USER_PHONE_NUMBER, user.getPhoneNumber())
                .putString(KEY_USER_ADDRESS, user.getAddress())
                .apply();
    }

    public User getUser() {
        User user = new User();
        user.setId(sharedPreferences.getString(KEY_USER_ID, ""));
        user.setName(sharedPreferences.getString(KEY_USER_NAME, ""));
        user.setPhoneNumber(sharedPreferences.getString(KEY_USER_PHONE_NUMBER, ""));
        user.setAddress(sharedPreferences.getString(KEY_USER_ADDRESS, ""));
        return user;
    }

    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, "");
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void clearPref() {
        sharedPreferences.edit().clear().apply();
    }
}
