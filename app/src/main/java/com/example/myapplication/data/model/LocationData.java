package com.example.myapplication.data.model;

public class LocationData {
    private String type ;
    private String location ;
    private String userID ;

    public LocationData(String type, String location, String userID) {
        this.type = type;
        this.location = location;
        this.userID = userID;
    }

    public String getType() {
        return type;
    }

    public String getLocation() {
        return location;
    }

    public String getUserID() {
        return userID;
    }
}
