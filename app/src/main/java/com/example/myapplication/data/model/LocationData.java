package com.example.myapplication.data.model;

public class LocationData {
    private String type ;
    private String startLocation ;
    private String userID ;
    private String endLocation;
    private int distance;

    public LocationData(String type, String startLocation, String userID, String endLocation, int distance) {
        this.type = type;
        this.startLocation = startLocation;
        this.userID = userID;
        this.endLocation = endLocation;
        this.distance = distance;
    }

    public String getType() {
        return type;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public String getUserID() {
        return userID;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
