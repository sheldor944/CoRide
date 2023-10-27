package com.example.myapplication.ui.home;

import static java.lang.Math.round;

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
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.data.model.LocationData;
import com.example.myapplication.data.model.RiderTrip;
import com.example.myapplication.helper.DistanceCalculatorCallback;
import com.example.myapplication.helper.PlaceFetcherCallback;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
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

public class StartRideActivity extends AppCompatActivity implements OnMapReadyCallback {
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
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

    private static final String TAG = "StartRideActivity";
    private static final String API_KEY = "AIzaSyDICnj_kc22dTrmOIUJg46B5fOgu6QhxFM";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168),
            new LatLng(71, 136)
    );
    private static final LatLngBounds BANGLADESH_LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(20.743550, 88.043370),
            new LatLng(26.631450, 92.672720)
    );

    private static final int MAX_SEARCH_COUNT = 5;
    private static final int SEARCH_INTERVAL = 10000;
    private static int numberOfTimesSearchedForRiders;

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
    private CardView mFindARideLinearLayout;
    private LinearLayout mSearchingARideLayout;

    private FirebaseDatabase database ;
    private FirebaseAuth currentUser;
    private String mUserId;
    private LatLng destLatLng;
    private int distance;
    private Thread RiderSearchThread;
    private RiderTrip bestRiderTrip = null;
    private TextView mCostTextView;

    private boolean stopThreads = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_ride);
        mSearchText = (AutoCompleteTextView) findViewById(R.id.searchBar);
        mGPS = (ImageView) findViewById(R.id.ic_gps);
        mFindARideButton = (AppCompatButton) findViewById(R.id.findARideButton);
        mConfirmDestinationButton = (AppCompatButton) findViewById(R.id.confirmDestinationButton);
        mConfirmDestinationCardView = (CardView) findViewById(R.id.confirmDestinationCardView);
        mFindARideLinearLayout = findViewById(R.id.findARideLayout);
        mSearchingARideLayout = (LinearLayout) findViewById(R.id.searchingARideLinearLayout);
        mCostTextView = findViewById(R.id.cost_text);

        numberOfTimesSearchedForRiders = 0;
        mUserId = FirebaseAuth.getInstance().getUid();
        mConfirmDestinationButton.setEnabled(false);

        if(isServicesOK()) {
            getLocationPermission();
        }
    }

    public boolean isServicesOK() {
        Log.d(TAG, "checking google services");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(StartRideActivity.this);
        if(available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "Connected successfully!");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "Could not connect but it is resolvable.");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(
                    StartRideActivity.this,
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

    private void init() {
        Log.d(TAG, "init: initializing");
        GoogleMapAPIHandler.setApiKey(API_KEY);

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
                mConfirmDestinationButton.setEnabled(false);
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
                    mMap,
                    new PlaceFetcherCallback() {
                        @Override
                        public void onPlaceFetched(LatLng latLng, int distanceCallback) {
                            destLatLng = latLng;
                            distance = distanceCallback;
                            Log.d(TAG, "onPlaceFetched: distance is: " + distance);
                        }
                    }
            );
            mConfirmDestinationButton.setEnabled(true);
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

        mConfirmDestinationButton.setOnClickListener(view -> {
            Log.d(TAG, "init: destination confirmed");
            mConfirmDestinationCardView.setVisibility(View.GONE);
            mFindARideLinearLayout.setVisibility(View.VISIBLE);

            int cost = GoogleMapAPIHandler.getCostFromDistance(distance);
            mCostTextView.setText("~ " + cost + "৳");
        });

        mFindARideButton.setOnClickListener(view -> {
            Log.d(TAG, "init: clicked on Find a Ride");
            mFindARideLinearLayout.setVisibility(View.GONE);
            mSearchingARideLayout.setVisibility(View.VISIBLE);

//            getRiderInformation();
            pollRiderInfo();
        });
    }

    private void pollRiderInfo() {
        RiderSearchThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "pollRiderInfo: current thread: " + Thread.currentThread().getName());
                try {
                    while(numberOfTimesSearchedForRiders <= MAX_SEARCH_COUNT) {
                        numberOfTimesSearchedForRiders++;
                        if(bestRiderTrip != null) break;
                        getRiderInformation();
                        Log.d(TAG, "pollRiderInfo: going to search again after some time.");
                        Thread.sleep(SEARCH_INTERVAL);
                    }
                    if(numberOfTimesSearchedForRiders > MAX_SEARCH_COUNT) {
                        Log.d(TAG, "pollRiderInfo: no passenger at all. aborting search.");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(StartRideActivity.this, "Unfortunately, no rider found!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(StartRideActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    Log.d(TAG, "pollRiderInfo: InterruptedException: " + e.getMessage());
                }
            }
        });
        RiderSearchThread.start();
    }

    private void insertIntoBookedPassengerRider(LocationData riderData)
    {
        LocationDB locationDB = new LocationDB() ;
        LocationData passengerData = new LocationData(
                "Passenger",
                currentLocation.getLatitude() + "," + currentLocation.getLongitude(),
                mUserId,
                destLatLng.latitude + "," + destLatLng.longitude,
                distance
        );
        locationDB.insertIntoPassengerRider(passengerData, riderData);
    }

    private void onBestRiderFound(String riderUID)
    {
        Log.d(TAG, "onBestRiderFound: going to chat activity.");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);

                intent.putExtra("passenger_id", mUserId);
                intent.putExtra("passenger_start_location", currentLocation.getLatitude() + "," + currentLocation.getLongitude());
                intent.putExtra("passenger_end_location", destLatLng.latitude + "," + destLatLng.longitude);

                intent.putExtra("rider_id", riderUID);
                intent.putExtra("rider_start_location", bestRiderTrip.getLocationData().getStartLocation());
                intent.putExtra("rider_end_location", bestRiderTrip.getLocationData().getEndLocation());

                startActivity(intent);
            }
        });
    }

    private void getRiderInformation() {
        if(bestRiderTrip != null) return;
        LocationDB locationDB = new LocationDB();
        String[] waypoints = {
            currentLocation.getLatitude() + "," + currentLocation.getLongitude(),
            destLatLng.latitude + "," + destLatLng.longitude
        };
        locationDB.getLocation("Rider", locationDataList -> {
            Log.d(TAG, "getRiderInformation: Number of pending riders: " + locationDataList.size());

            if(locationDataList.size() == 0) return;
            ArrayList <RiderTrip> riderTrips = new ArrayList<>();
            for(LocationData locationData : locationDataList) {
                Log.d(TAG, "getRiderInformation: " + locationData.getUserID());
                GoogleMapAPIHandler.getDistanceThroughWaypoints(
                        locationData.getStartLocation(),
                        locationData.getEndLocation(),
                        waypoints,
                        new DistanceCalculatorCallback() {
                            @Override
                            public void onDistanceCalculated(int distance) {
                                riderTrips.add(new RiderTrip(
                                        locationData, distance
                                ));
//                                fix the condition, check if all riders have been added
                                if(riderTrips.size() == locationDataList.size()) {
                                    Log.d(TAG, "onDistanceCalculated: all rider distance cost calculated.");
                                    onRiderTripsFound(riderTrips);
                                }
                            }
                        }
                );
            }
        });
    }

    private void onRiderTripsFound(ArrayList<RiderTrip> riderTrips) {
//        Working on this from another thread, I think
        Log.d(TAG, "onRiderTripsFound: going to sort rider trips");
        Collections.sort(riderTrips, new Comparator<RiderTrip>() {
            @Override
            public int compare(RiderTrip riderTrip, RiderTrip t1) {
                return riderTrip.getTotalDistance() - t1.getTotalDistance();
            }
        });
        for(RiderTrip riderTrip : riderTrips) {
            if(!isFeasibleForRider(riderTrip)) continue;

            LocationDB locationDB = new LocationDB();
            Log.d(TAG, "onRiderTripsFound: checking: " + riderTrip.getLocationData().getUserID()
                + " distance: " + riderTrip.getTotalDistance()
            );
            if(locationDB.isRiderAvailable(riderTrip.getLocationData().getUserID())) {
                bestRiderTrip = riderTrip;
                break;
            }
        }
        if(bestRiderTrip == null) {
            return;
        }
        Log.d(TAG, "onRiderTripsFound: sorted. best match user id: " + bestRiderTrip.getLocationData().getUserID());
        insertIntoBookedPassengerRider(bestRiderTrip.getLocationData());
        onBestRiderFound(bestRiderTrip.getLocationData().getUserID());
    }

    private boolean isFeasibleForRider(RiderTrip riderTrip) {
        if(riderTrip.getTotalDistance() > 2 * riderTrip.getLocationData().getDistance()) return false;
        return true;
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

        mapFragment.getMapAsync(StartRideActivity.this);
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
        view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}