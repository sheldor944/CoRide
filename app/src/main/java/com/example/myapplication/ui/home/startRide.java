package com.example.myapplication.ui.home;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class startRide extends AppCompatActivity {

    private MapView mapView;
    private IMapController mapController;
    SearchView searchView;
    private void hideKeyboard() {
        // Get the input manager
        InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        // Hide the keyboard
        inputManager.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
    }

    private void displayLocationOnMap(@NonNull String locationName) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocationName(locationName, 1);

            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                double latitude = address.getLatitude();
                double longitude = address.getLongitude();

                // Create a GeoPoint from the obtained coordinates
                GeoPoint geoPoint = new GeoPoint(latitude, longitude);

                // Add a marker to the map at the obtained location
                Marker marker = new Marker(mapView);
                marker.setPosition(geoPoint);
                mapView.getOverlays().add(marker);

                // Center the map at the marker's position
                mapController.setCenter(geoPoint);
            } else {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE));
        setContentView(R.layout.activity_start_ride);

        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        mapController = mapView.getController();
        mapController.setZoom(14); // Zoom level

        // Call the displayLocationOnMap() function to display the initial location.
        displayLocationOnMap("Burhanuddin College");

        searchView = findViewById(R.id.SearchBar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                // Return true to prevent the SearchView from closing.
                hideKeyboard();
                displayLocationOnMap(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // Call the displayLocationOnMap() function with the new query text.


                // Return true to prevent the SearchView from closing.
                return true;
            }
        });

        // Initialize osmdroid configuration

    }

}