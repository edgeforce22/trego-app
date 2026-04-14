package com.example.tregoapp.customer.model.map;

import java.util.List;

public class RouteResponse {
    private List<Route> routes;

    public RouteResponse(List<Route> routes) {
        this.routes = routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public List<Route> getRoutes() {
        return routes;
    }

}