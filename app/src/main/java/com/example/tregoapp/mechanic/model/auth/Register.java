package com.example.tregoapp.mechanic.model.auth;

public class Register {

    private String name;
    private String phoneNumber;
    private String role;
    private String address;
    private String password;
    private double latitude;
    private double longitude;

    public Register() {}

    public Register(String name, String phoneNumber, String role, String address, String password, double latitude, double longitude) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.address = address;
        this.password = password;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getRole(){
        return role;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
