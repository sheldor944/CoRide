package com.example.myapplication.helper;

import org.json.JSONObject;

public interface RouteFetcherListener {
    public void onRouteFetchComplete(JSONObject jsonObject);
}