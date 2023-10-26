package com.example.myapplication.helper;

import com.google.android.gms.maps.model.LatLng;

public interface PlaceFetcherCallback {
    public void onPlaceFetched(LatLng latLng, int distance);
}
