package com.example.myapplication.ui.map;



import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.loader.content.AsyncTaskLoader;

import com.example.myapplication.ChatActivity;
import com.example.myapplication.LocationDB;
import com.example.myapplication.MainActivity;
import com.example.myapplication.PushNotification;
import com.example.myapplication.R;
import com.example.myapplication.data.model.LocationData;
import com.example.myapplication.data.model.MessageEvent;
import com.example.myapplication.data.model.RiderTrip;
import com.example.myapplication.helper.Callback;
import com.example.myapplication.helper.DistanceCalculatorCallback;
import com.example.myapplication.helper.PermissionCallback;
import com.example.myapplication.helper.PlaceFetcherCallback;
import com.example.myapplication.helper.SaveToCompletedTableCallback;
import com.example.myapplication.testerActivity;
import com.example.myapplication.ui.home.PlacesAutoCompleteAdapter;
import com.example.myapplication.utils.PermissionUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.ktx.Firebase;
import com.google.maps.android.PolyUtil;

import com.example.myapplication.ui.home.GoogleMapAPIHandler;

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

public class RideDetailsOnMapActivity extends testerActivity implements OnMapReadyCallback {
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

    private static final int SEARCH_INTERVAL = 3000;
    private static final String TAG = "RideDetailsOnMapActivity";
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

    private String mUserId;

    private String mPassengerId;
    private String mPassengerStartLocation;
    private String mPassengerEndLocation;

    private String mRiderId;
    private String mRiderStartLocation;
    private String mRiderEndLocation;

    private String mRouteEndLocation;

    private NavigationView mNavigationView;
    private boolean stopThread = false;

    private ActionBarDrawerToggle mToggle;
    private DrawerLayout mDrawerLayout;

    private boolean mPickupStatus = false;

    private BitmapDescriptor mBikeImageBitmapDescriptor;

    private String mPassengerLiveLocation;
    private String mRiderLiveLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_details_on_map);
        mSearchText = (AutoCompleteTextView) findViewById(R.id.searchBar);
        mGPS = (ImageView) findViewById(R.id.ic_gps);
        mNavigationView = findViewById(R.id.ride_nav_view);
        mUserId = FirebaseAuth.getInstance().getUid();

        mBikeImageBitmapDescriptor = BitmapDescriptorFactory
                .fromBitmap(
                        resizeBitmap("ic_rider_bike",72,64)
                );

        mDrawerLayout = findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.nav_open,
                R.string.nav_close
        );

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        getInformationFromIntent();

        if (isServicesOK()) {
            getLocationPermission();
        }
    }

    private void pollCurrentLocationAndUpdateRoute() {
        Log.d(TAG, "displayRouteFromCurrentLocation: displaying route from current location");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(!stopThread) {
                        Log.d(TAG, "run: starting thread to see if the passenger has been picked up");
                        Thread pickupStatusFetcherThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if(mPickupStatus) return;
                                LocationDB locationDB = new LocationDB();
                                locationDB.getPickupStatus(mRiderId, pickupStatus -> {
                                    Log.d(TAG, "run: pickup status: " + pickupStatus);
                                   mPickupStatus = pickupStatus;
                                   if(pickupStatus == true) {
                                       Log.d(TAG, "run: passenger has been picked up");
                                       mNavigationView.getMenu().findItem(R.id.cancel_ride).setEnabled(false);
                                       mNavigationView.getMenu().findItem(R.id.picked_up_passenger).setEnabled(false);
                                       mRouteEndLocation = mPassengerEndLocation;
                                   }
                                   else mRouteEndLocation = mPassengerStartLocation;
                                });
                            }
                        });
                        pickupStatusFetcherThread.start();
                        new LocationDB().getLiveLocation(mRiderId, liveLocation -> {
                            mRiderLiveLocation = liveLocation;
                        });
                        getDeviceLocationAndDisplayRoute();
                        Log.d(TAG, "run: will display updated route after some time");
                        Thread.sleep(SEARCH_INTERVAL);
                    }
                } catch (Exception e) {
                    Log.d(TAG, "run: Thread error: " + e.getMessage());
                }
            }
        }).start();
    }

    public void getDeviceLocationAndDisplayRoute() {
        Log.d(TAG, "getDeviceLocationAndDisplayRoute: ");
        if (ActivityCompat.checkSelfPermission(RideDetailsOnMapActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(RideDetailsOnMapActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Log.d(TAG, "run: calling task");
        Task<Location> task = mFusedLocationProviderClient.getLastLocation();
        task.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task task) {
                Log.d(TAG, "onComplete: Location fetch task completed");
                if(task.isSuccessful()) {
                    currentLocation = (Location) task.getResult();
                    Log.d(TAG, "onComplete: Device location fetched successfully. Location: lat " + currentLocation.getLatitude() + " lng " + currentLocation.getLongitude());
                    LatLng routeEndLatLng = GoogleMapAPIHandler.getLatLngFromString(
                            mRouteEndLocation,
                            ","
                    );
                    Log.d(TAG, "onComplete: route end: " + mPassengerEndLocation);

                    String currentLocationInString = currentLocation.getLatitude() + "," + currentLocation.getLongitude();
                    new LocationDB().updateLiveLocation(
                            mUserId,
                            currentLocationInString
                    );

                    if(mUserId.equals(mPassengerId)) mPassengerLiveLocation = currentLocationInString;
                    else mRiderLiveLocation = currentLocationInString;

                    GoogleMapAPIHandler.displayRoute(
                            GoogleMapAPIHandler.getLatLngFromString(mRiderLiveLocation, ","),
                            routeEndLatLng,
                            mMap,
                            (latLng, distance) -> {}
                    );

                    GoogleMapAPIHandler.addMarkerWithBikeIcon(
                            mMap,
                            GoogleMapAPIHandler.getLatLngFromString(mRiderLiveLocation, ","),
                            "Driver",
                            mBikeImageBitmapDescriptor
                    );
                }
                else {
                    Log.d(TAG, "onComplete: Could not fetch the last location");
                }
            }
        });
    }

    private void getInformationFromIntent() {
        Intent intent = getIntent();
        mPassengerId = intent.getStringExtra("passenger_id");
        mPassengerStartLocation = intent.getStringExtra("passenger_start_location");
        mPassengerEndLocation = intent.getStringExtra("passenger_end_location");

        mRiderId = intent.getStringExtra("rider_id");
        mRiderStartLocation = intent.getStringExtra("rider_start_location");
        mRiderEndLocation = intent.getStringExtra("rider_end_location");

        mRouteEndLocation = mPassengerStartLocation;
        mPassengerLiveLocation = mPassengerStartLocation;
        mRiderLiveLocation = mRiderStartLocation;

        Log.d(TAG, "getInformationFromIntent: passenger: " + mPassengerId + " "
                + mPassengerStartLocation + " " + mPassengerEndLocation);
        Log.d(TAG, "getInformationFromIntent: rider: " + mRiderId + " "
                + mRiderStartLocation + " " + mRiderEndLocation);
    }

    public boolean isServicesOK() {
        Log.d(TAG, "checking google services");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(com.example.myapplication.ui.map.RideDetailsOnMapActivity.this);
        if(available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "Connected successfully!");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "Could not connect but it is resolvable.");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(
                    com.example.myapplication.ui.map.RideDetailsOnMapActivity.this,
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

        mGPS.setOnClickListener(view -> {
            GoogleMapAPIHandler.moveCamera(
                    new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                    DEFAULT_ZOOM,
                    "My Location",
                    mMap
            );
        });

        pollCurrentLocationAndUpdateRoute();
        initMenuItems();
    }

    private void initMenuItems() {
        MenuItem cancelItem = mNavigationView.getMenu().findItem(R.id.cancel_ride);

        LocationDB locationDB = new LocationDB();
        cancelItem.setOnMenuItemClickListener(item -> {
            // TODO: ২৬/১০/২৩ : user clicked on cancel ride, handle it
            Log.d(TAG, "init: user clicked on cancel ride");


            AlertDialog.Builder builder = new AlertDialog.Builder(this);  // 'this' is the current Activity context
            builder.setTitle("Are You Sure ? ");  // Set the title of the dialog
            builder.setMessage("You want to cancel the ride ? ");  // Set the message

            // Add positive button (usually "OK" or "Yes" button)
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Code to execute when the button is clicked
                    // For example:
                    // Toast.makeText(getApplicationContext(), "OK clicked", Toast.LENGTH_SHORT).show();
                    PushNotification pushNotification = new PushNotification() ;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "run: push call hobe");
                            String notificationReceiverID =null;
                            if(mUserId.equals(mRiderId))
                            {
                                notificationReceiverID=mPassengerId;
                            }
                            else{
                                notificationReceiverID=mRiderId;
                            }
                            Log.d(TAG, "run: in cancel  " + mUserId + "  " + mPassengerId + " " + mRiderId);
                            locationDB.getTOKEN(notificationReceiverID, new Callback<String>() {
                                @Override
                                public void onComplete(String response) {
                                    pushNotification.cancelRide(response);
                                    Log.d(TAG, "run: calll oise push ");

                                    // TODO: 10/27/2023 delete from booked and add to complete
                                    locationDB.deleteFromBookedPassenger(mPassengerId , mRiderId);
                                    // maybe there will be a problem if rider cancels the ride 
                                    locationDB.saveToCompletedTable(mPassengerId, mRiderId, 400, new SaveToCompletedTableCallback() {
                                        @Override
                                        public void onSaveToCompletedTableComplete(ArrayList<Pair<String, String>> result) {

                                        }
                                    });
                                    Log.d(TAG, "onComplete: deleted and added as well ");
                                    stopThread = true;
                                    Intent intent = new Intent(getApplicationContext() , MainActivity.class);
                                    startActivity(intent);
                                }
                            });
                           
                        }
                    }).start();
                   
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });


            // Finally, show the dialog
            builder.create().show();
            return true;
        });

        MenuItem pickedUpItem = mNavigationView.getMenu().findItem(R.id.picked_up_passenger);
        pickedUpItem.setOnMenuItemClickListener(item -> {
            Log.d(TAG, "init: user clicked on picked up passenger, updating database.");
            PermissionUtil.askForConfirmation(
                    this,
                    "Are you sure you have picked up the passenger?",
                    () -> {
                        new LocationDB().updatePickupStatus(mPassengerId, mRiderId);
                        mPickupStatus = true;
                        mDrawerLayout.closeDrawers();
                        pickedUpItem.setEnabled(false);
                        cancelItem.setEnabled(false);
                    }
            );
            return true;
        });

        MenuItem chatItem = mNavigationView.getMenu().findItem(R.id.move_to_chat);
        chatItem.setOnMenuItemClickListener(item -> {
            Log.d(TAG, "init: clicked on chat button");
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("passenger_id", mPassengerId);
            intent.putExtra("passenger_start_location", mPassengerStartLocation);
            intent.putExtra("passenger_end_location", mPassengerEndLocation);

            intent.putExtra("rider_id", mRiderId);
            intent.putExtra("rider_start_location", mRiderStartLocation);
            intent.putExtra("rider_end_location", mRiderEndLocation);

            stopThread = true;
            startActivity(intent);
            return true;
        });

        MenuItem completedRideItem = mNavigationView.getMenu().findItem(R.id.complete_ride);
        completedRideItem.setOnMenuItemClickListener(item -> {
            // TODO: ২৬/১০/২৩ : completed ride, now?


            Log.d(TAG, "init: user clicked on completed ride");


            AlertDialog.Builder builder = new AlertDialog.Builder(this);  // 'this' is the current Activity context
            builder.setTitle("Are You Sure?");  // Set the title of the dialog

            // Add positive button (usually "OK" or "Yes" button)
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Code to execute when the button is clicked
                    // For example:
                    // Toast.makeText(getApplicationContext(), "OK clicked", Toast.LENGTH_SHORT).show();
                    PushNotification pushNotification = new PushNotification() ;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "run: push call hobe");
                            locationDB.getTOKEN(mPassengerId, new Callback<String>() {
                                @Override
                                public void onComplete(String response) {
                                    pushNotification.completeRide(response);
                                    Log.d(TAG, "run: calll oise push ");

                                    // TODO: 10/27/2023 delete from booked and add to complete
                                    locationDB.deleteFromBookedPassenger(mPassengerId , mRiderId);
                                    // maybe there will be a problem if rider cancels the ride
                                    locationDB.saveToCompletedTable(mPassengerId, mRiderId, 400, new SaveToCompletedTableCallback() {
                                        @Override
                                        public void onSaveToCompletedTableComplete(ArrayList<Pair<String, String>> result) {

                                        }
                                    });
                                    Log.d(TAG, "onComplete: deleted and added as well ");
                                    stopThread = true;
                                    Intent intent = new Intent(getApplicationContext() , MainActivity.class);
                                    startActivity(intent);
                                }
                            });
//                            pushNotification.completeRide("fbyU3dlwQ56zm-KWgQqyzr:APA91bEoN-I15jP2D2yQjTO7wq3Y_CT4veFjc3cmph5in1IPsTOh9NsXV8VdxTh0BNMZT0NQNnZttLd7Y9-KDEh8fj6Sr9PHThfKKQgEDtTWBAyZK4h7gLQ1R3S3D9A9Tgh8og99wFMc");
                            Log.d(TAG, "run: calll oise push ");
                        }
                    }).start();
                    // TODO: 10/27/2023 delete from booked and add to complete
                    stopThread = true;
                    Intent intent = new Intent(getApplicationContext() , MainActivity.class);
                    startActivity(intent);
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            builder.create().show();


            return true;
        });

        if(mUserId.equals(mPassengerId)) {
            completedRideItem.setEnabled(false);
            pickedUpItem.setEnabled(false);
        }
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

        mapFragment.getMapAsync(com.example.myapplication.ui.map.RideDetailsOnMapActivity.this);
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: trying to get the device location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if(mLocationPermissionsGranted) {
                Log.d(TAG, "getDeviceLocation: permission is granted. fetching location.");
                Task<Location> task = mFusedLocationProviderClient.getLastLocation();
                task.addOnCompleteListener(new OnCompleteListener<Location>() {
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

    @Override
    public void onMessageEvent(MessageEvent event) {
        stopThread = true;
        super.onMessageEvent(event);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: ");
        if(mToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: toggle is clicked");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public Bitmap resizeBitmap(String drawableName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(drawableName, "drawable", getPackageName()));
        return Bitmap.createScaledBitmap(imageBitmap, width, height, false);
    }
}