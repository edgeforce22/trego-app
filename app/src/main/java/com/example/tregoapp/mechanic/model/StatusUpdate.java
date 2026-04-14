package com.example.tregoapp.mechanic.model;

public class StatusUpdate {
    private String mechanicId;
    private String status;

    public StatusUpdate(String mechanicId, String status) {
        this.mechanicId = mechanicId;
        this.status = status;
    }

    public String getMechanicId() {
        return mechanicId;
    }

    public void setMechanicId(String mechanicId) {
        this.mechanicId = mechanicId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
