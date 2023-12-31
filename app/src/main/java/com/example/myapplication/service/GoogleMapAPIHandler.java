package com.example.myapplication.service;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.AutoCompleteTextView;

import com.example.myapplication.helper.DistanceCalculatorCallback;
import com.example.myapplication.helper.PlaceFetcherCallback;
import com.example.myapplication.helper.RouteFetcherListener;
import com.example.myapplication.ui.map.RouteFetcherThread;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GoogleMapAPIHandler {
    public static final double PER_METER_COST = 15.0 / 1000.0;
    private static final double SERVICE_COST = 20.0;
    private static final LatLngBounds BANGLADESH_LAT_LNG_BOUDNS = new LatLngBounds(
            new LatLng(20.743550, 88.043370), // Southwest corner
            new LatLng(26.631450, 92.672720)  // Northeast corner
    );
    private static PlacesClient mPlacesClient;
    private static final String TAG = "GoogleMapAPIHandler";
    private static String API_KEY;

    public static void setPlacesClient(PlacesClient placesClient) {
        mPlacesClient = placesClient;
    }

    public static void setApiKey(String _API_KEY) {
        API_KEY = _API_KEY;
    }

    public static void setAdapter(AutoCompleteTextView mAutoCompleteTextView, PlacesAutoCompleteAdapter mPlacesAutoCompleteAdapter, String s, PlacesClient mPlacesClient) {
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setCountries("BD")
                .setQuery(s.toString())
                .build();

        String query = request.getQuery();
        Log.d(TAG, "setAdapter: Query: " + query);
        mPlacesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
            mPlacesAutoCompleteAdapter.setPredictions(response.getAutocompletePredictions() , mAutoCompleteTextView);
        }).addOnFailureListener(exception -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.d(TAG, "setAdapter: API exception while setting adapter. Error Status Code: " + apiException.getStatusCode());
            } else {
                exception.printStackTrace();
            }
        });
    }

    public static void moveCamera(LatLng latLng, float zoom, String title, GoogleMap googleMap) {
        Log.d(TAG, "moveCamera: moving camera to: lat " + latLng.latitude + " lng " + latLng.longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        GoogleMapAPIHandler.addMarker(googleMap, latLng, title);
    }

    public static void fetchPlaceAndMoveCamera(AutocompletePrediction autocompletePrediction, PlacesClient placesClient,
                                               Location currentLocation, float zoom, GoogleMap googleMap,
                                               PlaceFetcherCallback callback) {
        String placeId = autocompletePrediction.getPlaceId();
        Log.d(TAG, "fetchPlace: fetching place from search bar, ID: " + placeId);
        List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME);

        placesClient.fetchPlace(FetchPlaceRequest.builder(placeId, placeFields).build())
                .addOnSuccessListener(response -> {
                    Place place = response.getPlace();
                    LatLng latLng = place.getLatLng();
                    if (latLng != null) {
                        displayRoute(
                                new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                latLng,
                                googleMap,
                                callback
                        );
                        Log.d(TAG, "fetchPlace: fetched successfully. lat " + latLng.latitude + " lng " + latLng.longitude);
                        GoogleMapAPIHandler.moveCamera(latLng, zoom, "Some Title", googleMap);
                    }
                }).addOnFailureListener(exception -> {
                    Log.d(TAG, "fetchPlaceAndMoveCamera: failed to fetch place. error: " + exception.getMessage());
                });
    }

    public static void displayRoute(LatLng srcLatLng, LatLng destLatLng, GoogleMap googleMap, PlaceFetcherCallback callback) {
        RouteFetcherThread routeFetcherThread = new RouteFetcherThread(
                API_KEY,
                jsonObject -> {
                    try {
                        callback.onPlaceFetched(destLatLng, parseDistanceFromJSON(jsonObject));
                    } catch (JSONException e) {
                        Log.d(TAG, "displayRoute: json error: " + e.getMessage());
                    }
                    Log.d(TAG, "displayRoute: current thread: " + Thread.currentThread().getName());
                    PolylineOptions polylineOptions = new PolylineOptions();
                    Log.d(TAG, "displayRoute: extracting route from json.");
                    try {
                        if (jsonObject.has("routes")) {
                            JSONArray routesArray = jsonObject.getJSONArray("routes");

                            if (routesArray.length() > 0) {
                                JSONObject route = routesArray.getJSONObject(0); // Take the first route

                                if (route.has("overview_polyline")) {
                                    JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                                    String encodedPolyline = overviewPolyline.getString("points");

                                    Log.d(TAG, "displayRoute: extracted points. going to decode.");

                                    // Decode the polyline and add it to the PolylineOptions
                                    List<LatLng> decodedPolyline = PolyUtil.decode(encodedPolyline);
                                    for (LatLng point : decodedPolyline) {
                                        polylineOptions.add(point);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        Log.d(TAG, "displayRoute: Route distance is: " + parseDistanceFromJSON(jsonObject));
                    } catch (JSONException e) {
                        Log.d(TAG, "displayRoute: distance could not be parsed from json. Error: " + e.getMessage());
                    }

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            googleMap.clear();
                            Log.d(TAG, "displayRoute: " + polylineOptions.toString());
                            googleMap.addPolyline(polylineOptions);
                            addMarker(
                                    googleMap,
                                    destLatLng,
                                    "Destination"
                            );
                        }
                    });
                }
        );
        routeFetcherThread.setUrlString(
                "https://maps.googleapis.com/" +
                        "maps/" +
                        "api/" +
                        "directions/" +
                        "json?" +
                        "destination=" +
                        destLatLng.latitude +
                        "," +
                        destLatLng.longitude +
                        "&origin=" +
                        srcLatLng.latitude +
                        "," +
                        srcLatLng.longitude +
                        "&key=" + API_KEY
        );
        routeFetcherThread.start();
    }

    public static void getDistanceThroughWaypoints(String src, String dest, String[] waypoints, DistanceCalculatorCallback callback) {
        RouteFetcherThread routeFetcherThread = new RouteFetcherThread(
                API_KEY,
                new RouteFetcherListener() {
                    @Override
                    public void onRouteFetchComplete(JSONObject jsonObject) {
                        try {
                            int distance = GoogleMapAPIHandler.parseDistanceFromJSON(jsonObject);
//                            do callback here
                            callback.onDistanceCalculated(distance);
                        } catch(JSONException e) {
                            Log.d(TAG, "onRouteFetchComplete: could not parse distance from json. JSONException: " + e.getMessage());
                        }
                    }
                }
        );
        routeFetcherThread.setUrlString(
                "https://maps.googleapis.com/" +
                        "maps/" +
                        "api/" +
                        "directions/" +
                        "json?" +
                        "destination=" +
                        dest +
                        "&origin=" +
                        src +
                        "&waypoints=" +
                        waypoints[0] +
                        "|" +
                        waypoints[1] +
                        "&key=" + API_KEY
        );
        routeFetcherThread.start();
    }

    public static void getDistanceBetweenTwoLatLng(String src, String dest, DistanceCalculatorCallback callback) {
        RouteFetcherThread routeFetcherThread = new RouteFetcherThread(
                API_KEY,
                new RouteFetcherListener() {
                    @Override
                    public void onRouteFetchComplete(JSONObject jsonObject) {
                        try {
                            int distance = GoogleMapAPIHandler.parseDistanceFromJSON(jsonObject);
//                            do callback here
                            callback.onDistanceCalculated(distance);
                        } catch(JSONException e) {
                            Log.d(TAG, "onRouteFetchComplete: could not parse distance from json. JSONException: " + e.getMessage());
                        }
                    }
                }
        );
        routeFetcherThread.setUrlString(
                "https://maps.googleapis.com/" +
                        "maps/" +
                        "api/" +
                        "directions/" +
                        "json?" +
                        "destination=" +
                        dest +
                        "&origin=" +
                        src +
                        "&key=" + API_KEY
        );
        routeFetcherThread.start();
    }

    public static int parseDistanceFromJSON(JSONObject jsonResponseObj) throws JSONException {
        String status = jsonResponseObj.getString("status");
        int distanceValue = -1;
        if ("OK".equals(status)) {
            // Extract the distance from the first route
            JSONArray routesArray = jsonResponseObj.getJSONArray("routes");

            if (routesArray.length() > 0) {
                // Get the first route (assumption: there's at least one route)
                JSONObject route = routesArray.getJSONObject(0);

                // Navigate to the "legs" array
                JSONArray legsArray = route.getJSONArray("legs");

                int totalDistance = 0;

                // Sum up the distances of all legs
                for (int i = 0; i < legsArray.length(); i++) {
                    JSONObject leg = legsArray.getJSONObject(i);
                    JSONObject distance = leg.getJSONObject("distance");
                    int legDistance = distance.getInt("value");
                    totalDistance += legDistance;
                }

                // Print the total distance in meters
                Log.d(TAG, "parseDistanceFromJSON: total distance: " + totalDistance);

                distanceValue = totalDistance;
            } else {
                Log.d(TAG, "parseDistanceFromJSON: error: no routes array found.");
            }
        } else {
            Log.d(TAG, "parseDistanceFromJSON: Status is not OK");
        }
        Log.d(TAG, "parseDistanceFromJSON: distance value: " + distanceValue);
        return distanceValue;
    }

    //    context = the current activity
    public static void geoLocate(String placeName, Context context, GoogleMap googleMap, float zoom) {
        Log.d(TAG, "geoLocate: geolocating place: " + placeName);
        Geocoder geocoder = new Geocoder(context);
        List <Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(placeName, 1);
        } catch (IOException e) {
            Log.d(TAG, "geoLocate: IOException: " + e.getMessage());
        }
        if(list.size() > 0) {
            Address address = list.get(0);
            Log.d(TAG, "geoLocate: found a position: " + address);
            Log.d(TAG, "geoLocate: calling move camera to " + address.getLatitude() + " " + address.getLongitude());
            GoogleMapAPIHandler.moveCamera(
                    new LatLng(address.getLatitude(), address.getLongitude()),
                    zoom,
                    address.getAddressLine(0),
                    googleMap
            );
        }
    }

    public static void addMarker(GoogleMap googleMap, LatLng latLng, String title) {
//        use .icon to add custom image. read documentation for details.
        googleMap.addMarker(
                new MarkerOptions()
                        .position(latLng)
                        .title(title)
        );
    }



    public static void addMarkerWithBikeIcon(GoogleMap googleMap, LatLng latLng,
                                             String title, BitmapDescriptor markerImageBitmapDescriptor) {
        Log.d(TAG, "addMarkerWithBikeIcon: adding marker with bike icon to: " + latLng);
        googleMap.addMarker(
                new MarkerOptions()
                        .position(latLng)
                        .title(title)
                        .icon(markerImageBitmapDescriptor)
        );
    }
    public static LatLng getLatLngFromString(String locationString, String separator) {
        String[] location = locationString.split(separator);
        return new LatLng(
                Double.parseDouble(location[0]),
                Double.parseDouble(location[1])
        );
    }

    public static int getCostFromDistance(int distanceInMeter) {
        double distance = (double) distanceInMeter;
        double cost = SERVICE_COST + (distance * PER_METER_COST);
        int costInt = (int) cost;
        Log.d(TAG, "getCostFromDistance: double: " + cost + " int: " + costInt);
        return costInt;
    }
}