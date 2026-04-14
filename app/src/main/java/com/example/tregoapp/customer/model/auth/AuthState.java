package com.example.tregoapp.customer.model.auth;

public class AuthState {
    private boolean success;
    private String message;

    public AuthState() {}
    public AuthState(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getSuccess() {
        return success;
    }
    public String getMessage() {
        return message;
    }
}
