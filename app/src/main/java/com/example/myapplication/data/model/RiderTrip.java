package com.example.myapplication.data.model;

public class RiderTrip {
    private LocationData locationData;
    private int totalDistance;

    public RiderTrip() {}

    public RiderTrip(LocationData locationData, int totalDistance) {
        this.locationData = locationData;
        this.totalDistance = totalDistance;
    }

    public RiderTrip(LocationData locationData) {
        this.locationData = locationData;
    }

    public LocationData getLocationData() {
        return locationData;
    }

    public void setLocationData(LocationData locationData) {
        this.locationData = locationData;
    }

    public int getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(int totalDistance) {
        this.totalDistance = totalDistance;
    }
}
