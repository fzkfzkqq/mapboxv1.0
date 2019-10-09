package com.disastermate.mapbox.other;

public class HistoricfireModel {
    Double latitude;
    Double longitude;
    String power;
    String temperature;
    String month;
    String date;

    public HistoricfireModel(Double latitude, Double longitude, String power, String temperature, String month, String date) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.power = power;
        this.temperature = temperature;
        this.month = month;
        this.date = date;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
