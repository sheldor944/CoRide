package com.example.myapplication.helper;

import com.example.myapplication.data.model.LocationData;

import java.util.ArrayList;

public interface LocationCallback {
    public void onLocationDataReceived(ArrayList <LocationData> locationDataArrayList);
}
