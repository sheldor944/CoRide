package com.example.myapplication.data.model;

public class LocationData {
    private String type ;
    private String startLocation ;
    private String userID ;
    private String endLocation;

    public LocationData(String type, String startLocation, String userID, String endLocation) {
        this.type = type;
        this.startLocation = startLocation;
        this.userID = userID;
        this.endLocation = endLocation;
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
}
