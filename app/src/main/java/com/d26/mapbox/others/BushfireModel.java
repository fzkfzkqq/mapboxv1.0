package com.d26.mapbox.others;

public class BushfireModel {

    String status;
    String location;
    String lastUpdated;

    public BushfireModel(String status, String location, String lastUpdated) {
        this.status = status;
        this.location = location;
        this.lastUpdated = lastUpdated;
    }
}
