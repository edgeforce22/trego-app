package com.example.tregoapp.customer.model.map;

public class Route {

    private double distance;
    private double duration;
    private Geometry geometry;

    public Route() {}

    public Route(double distance, double duration, Geometry geometry) {
        this.distance = distance;
        this.duration = duration;
        this.geometry = geometry;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
}