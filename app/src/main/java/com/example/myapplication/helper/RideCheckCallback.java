package com.example.myapplication.helper;

import android.util.Pair;

import java.util.ArrayList;

public interface RideCheckCallback {
    void onRideCheckCompleted( ArrayList<Pair<String,String>> result);
}