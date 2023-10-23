package com.example.myapplication.ui.home;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.AsyncTaskLoader;

import com.example.myapplication.ChatActivity;
import com.example.myapplication.LocationDB;
import com.example.myapplication.R;
import com.example.myapplication.data.model.LocationData;
import com.example.myapplication.data.model.RiderTrip;
import com.example.myapplication.helper.DistanceCalculatorCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ProvideARideActivity extends AppCompatActivity implements OnMapReadyCallback {
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    private static final String TAG = "ProvideARideActivity";
    private static final String API_KEY = "AIzaSyDICnj_kc22dTrmOIUJg46B5fOgu6QhxFM";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static float DEFAULT_ZOOM = 15f;
    private static LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168),
            new LatLng(71, 136)
    );

    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private AutoCompleteTextView mSearchText;
    private Location currentLocation;
    private ImageView mGPS;
    private PlacesClient mPlacesClient;
    private PlacesAutoCompleteAdapter mPlacesAutoCompleteAdapter;
    private AppCompatButton mFindARideButton;
    private AppCompatButton mConfirmDestinationButton;
    private CardView mConfirmDestinationCardView;
    private LinearLayout mFindARideLinearLayout;
    private LinearLayout mSearchingARideLayout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provide_a_ride);
        mSearchText = (AutoCompleteTextView) findViewById(R.id.searchBar);
        mGPS = (ImageView) findViewById(R.id.ic_gps);

        if(isServicesOK()) {
            getLocationPermission();
        }
    }

    public boolean isServicesOK() {
        Log.d(TAG, "checking google services");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(ProvideARideActivity.this);
        if(available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "Connected successfully!");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "Could not connect but it is resolvable.");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(
                    ProvideARideActivity.this,
                    available,
                    ERROR_DIALOG_REQUEST
            );
            dialog.show();
        }
        else {
            Toast.makeText(this, "Can't connect, sad!", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void addRiderToDB()
    {
        Log.d(TAG, "addRiderToDB: adding rider to DB ");
        LocationDB locationDB = new LocationDB();
//        locationDB.updateLocation(currentLocation.getLatitude()+"," + currentLocation.getLongitude() , "Rider");
        locationDB.addToPendingRider("24.9059,91.8721");
    }



    private void onPassengerFound()
    {

    }

    private void searchPassenger() {
        LocationDB locationDB = new LocationDB();
        locationDB.getBookedPassenger(passengerId -> {
            Log.d(TAG, "searchPassenger: " + passengerId);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext() , ChatActivity.class);
                    intent.putExtra("UID" , passengerId);
                    startActivity(intent);
                }
            });
        });
    }
    private void init() {
        Log.d(TAG, "init: initializing");
        GoogleMapAPIHandler.setApiKey(API_KEY);
        // call after confirm
        addRiderToDB();

        searchPassenger();


        Log.d(TAG, "init: initializing Places");
        if(!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), API_KEY);
        }
        mPlacesClient = Places.createClient(this);
        mPlacesAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this, mPlacesClient);

        Log.d(TAG, "init: Setting adapter");
        mSearchText.setAdapter(mPlacesAutoCompleteAdapter);

        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                GoogleMapAPIHandler.setAdapter(mSearchText, mPlacesAutoCompleteAdapter, s.toString(), mPlacesClient);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mSearchText.setOnItemClickListener((parent, view, position, id) -> {
            AutocompletePrediction autocompletePrediction = mPlacesAutoCompleteAdapter.getItem(position);
            if(autocompletePrediction == null) {
                Log.d(TAG, "init: Selected item is null");
                return;
            }
            mSearchText.setText(autocompletePrediction.getFullText(null));
            mSearchText.dismissDropDown();
            GoogleMapAPIHandler.fetchPlaceAndMoveCamera(
                    autocompletePrediction,
                    mPlacesClient,
                    currentLocation,
                    DEFAULT_ZOOM,
                    mMap
            );
            hideSoftKeyboard(view);
        });

        mGPS.setOnClickListener(view -> {
            GoogleMapAPIHandler.moveCamera(
                    new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                    DEFAULT_ZOOM,
                    "My Location",
                    mMap
            );
        });
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        boolean alreadyPermitted = true;
        for(String permission : permissions) {
            if(checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                alreadyPermitted = false;
            }
        }
        if(alreadyPermitted) {
            Log.d(TAG, "getLocationPermission: Location access permissions are already granted");
            mLocationPermissionsGranted = true;
            initMap();
            return;
        }

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = true;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    initMap();
                    mLocationPermissionsGranted = true;
                }
            }
        }
    }

    private void initMap() {
        Log.d(TAG, "Initializing Map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(ProvideARideActivity.this);
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: trying to get the device location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if(mLocationPermissionsGranted) {
                Log.d(TAG, "getDeviceLocation: permission is granted. fetching location.");
                Task <Location> task = mFusedLocationProviderClient.getLastLocation();
                task.addOnCompleteListener(new OnCompleteListener <Location> () {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        Log.d(TAG, "onComplete: Location fetch task completed");
                        if(task.isSuccessful()) {
                            currentLocation = (Location) task.getResult();
                            Log.d(TAG, "onComplete: Device location fetched successfully. Location: lat " + currentLocation.getLatitude() + " lng " + currentLocation.getLongitude());
                            GoogleMapAPIHandler.moveCamera(
                                    new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,
                                    "My Location",
                                    mMap
                            );
                            init();
                        }
                        else {
                            Log.d(TAG, "onComplete: Could not fetch the last location");
                        }
                    }
                });
            } else {
                Log.d(TAG, "getDeviceLocation: Cant access the location");
            }
        } catch(SecurityException e) {
            Log.d(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void hideSoftKeyboard(View view) {
        Log.d(TAG, "hideSoftKeyboard: hiding soft keyboard");
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}