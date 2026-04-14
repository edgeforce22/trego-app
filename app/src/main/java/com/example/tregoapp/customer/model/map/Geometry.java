package com.example.tregoapp.customer.model.map;

import java.util.List;

public class Geometry {

    private List<List<Double>> coordinates;

    public Geometry(List<List<Double>> coordinates) {
        this.coordinates = coordinates;
    }

    public List<List<Double>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<List<Double>> coordinates) {
        this.coordinates = coordinates;
    }
}