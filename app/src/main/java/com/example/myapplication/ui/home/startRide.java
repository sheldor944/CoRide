package com.example.myapplication.ui.home;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.osmdroid.views.overlay.Polyline;


import com.example.myapplication.LocationDB;
import com.example.myapplication.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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

    public void getRoute(String destination) {
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocationName(destination, 1);
            Address address = addresses.get(0);
            double searchedLocationLatitude = address.getLatitude();
            double searchedLocationLongitude = address.getLongitude();

            String openRouteServiceUrl = "https://api.openrouteservice.org/v2/directions/foot-walking?api_key=YOUR_API_KEY&start=" + String.valueOf(24.8867) + "," + String.valueOf(91.8745) + "&end=" + String.valueOf(searchedLocationLatitude) + "," + String.valueOf(searchedLocationLongitude);

            // Use AsyncTask to execute network operations on a separate thread
            new AsyncTask<String, Void, String>() {
                @Override
                protected String doInBackground(String... strings) {
                    String response = "";
                    try {
                        URL url = new URL(strings[0]);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            response += line;
                        }
                        bufferedReader.close();
                        connection.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return response;
                }

                protected void onPostExecute(String response) {

                    try {
                        Log.d("print" , response);
                        Toast.makeText(startRide.this, "Error ff route", Toast.LENGTH_SHORT).show();
                        JSONObject jsonResponse = null;
                        if (response != null && !response.trim().isEmpty()) {
                            try {
                                jsonResponse = new JSONObject(response);
                                System.out.println(jsonResponse);
                                // Continue with JSON parsing
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
                            }
                        } else {
                            Log.e("API_ERROR", "API Response is null or empty");
                        }
                        Toast.makeText(startRide.this, "Error after route", Toast.LENGTH_SHORT).show();

                        JSONArray jsonCoordinates = jsonResponse.getJSONArray("features")
                                .getJSONObject(0)
                                .getJSONObject("geometry")
                                .getJSONArray("coordinates");

                        List<GeoPoint> geoPoints = new ArrayList<>();
                        for (int i = 0; i < jsonCoordinates.length(); i++) {
                            JSONArray coordinate = jsonCoordinates.getJSONArray(i);
                            double longitude = coordinate.getDouble(0);
                            double latitude = coordinate.getDouble(1);
                            geoPoints.add(new GeoPoint(latitude, longitude));
                        }

                        Polyline line = new Polyline(mapView); // Pass mapView instance when creating a Polyline
                        line.setPoints(geoPoints);
                        mapView.getOverlayManager().add(line);
                        mapView.invalidate();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());

//                        Toast.makeText(startRide.this, "Error drawing route", Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute(openRouteServiceUrl);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
            Toast.makeText(this, "Error fetching route", Toast.LENGTH_SHORT).show();
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

        LocationDB locationDB = new LocationDB() ;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                // Return true to prevent the SearchView from closing.
                hideKeyboard();
                displayLocationOnMap(s);
                locationDB.updateLocation("" , "Rider");
//                getRoute(s);
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