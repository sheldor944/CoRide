package com.example.myapplication.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.preference.PreferenceManager;

import com.example.myapplication.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class startRide extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_ride);

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        MapView mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);


        // Set the initial map position (example coordinates)
        IMapController mapController = mapView.getController();
        GeoPoint startPoint = new GeoPoint(51.5074, -0.1278); // London
        mapController.setCenter(startPoint);
        mapController.setZoom(10); // Set the initial zoom level
    }
}