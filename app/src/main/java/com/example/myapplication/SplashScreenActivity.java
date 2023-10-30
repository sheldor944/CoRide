package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.helper.GetDataFromCompletedTableCallback;
import com.example.myapplication.helper.RideCheckCallback;
import com.example.myapplication.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class SplashScreenActivity extends AppCompatActivity {
    private GifImageView gifImageView;
    private String TAG = "SplashScreenActivity";
    private FirebaseAuth  firebaseAuth;

    private final long ANIMATION_TIME = 3000;
    private boolean mRideChecked;
    private boolean mAnimationCompleted;
    private Intent intent;

    private String mPassengerId="" , mPassengerStartLocation="" , mPassengerEndLocation="",
            mRiderId="" , mRiderStartLocation="" , mRiderEndLocation="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        gifImageView = findViewById(R.id.gifImageView);
        firebaseAuth = FirebaseAuth.getInstance();
        mRideChecked = false;
        mAnimationCompleted = false;

         if(firebaseAuth != null) checkForOnGoingRide();
         else {
             mRideChecked = true;
             intent = new Intent(this, LoginActivity.class);
         }

         new Handler().postDelayed(() -> {
            mAnimationCompleted = true;
            Log.d(TAG, "onCreate: animation is completed. ride check status: " + mRideChecked);
            if(mRideChecked) {
                startActivity(intent);
                finish();
            }
            else {
                Toast.makeText(this, "Internet is slow. Check your connection status.", Toast.LENGTH_LONG).show();
                GifDrawable gifDrawable = (GifDrawable) gifImageView.getDrawable();
                gifDrawable.stop();
            }
        }, ANIMATION_TIME);
    }

    private void animation()
    {
        new Handler().postDelayed(() -> {
            Intent mainIntent = new Intent(SplashScreenActivity.this, LoginActivity.class);
            startActivity(mainIntent);
            finish();

        }, 3490);
    }

    private void   checkForOnGoingRide() {
        LocationDB locationDB = new LocationDB();

        locationDB.checkForOngoingRide(  new RideCheckCallback() {
            @Override
            public void onRideCheckCompleted(ArrayList<Pair<String , String >> result ) {
                Log.d(TAG, "onRideCheckCompleted: starting the check ");
                if (result.size() > 0) {
                    // Do something if there's an ongoing ride
                    Log.d(TAG, "onRideCheckCompleted: this person has a ride ");
                    intent = new Intent(getApplicationContext() , ChatActivity.class);

                    for(Pair<String , String> u : result)
                    {
                        if(u.first.equals("PassengerID")){
                            mPassengerId=u.second;
                        }
                        if(u.first.equals("RiderID")){
                            mRiderId = u.second;
                        }
                        if(u.first.equals("RiderStart")){
                            mRiderStartLocation=u.second;
                        }
                        if(u.first.equals("RiderDestination")){
                            mRiderEndLocation = u.second;
                        }
                        if(u.first.equals("PassengerStart")){
                            mPassengerStartLocation=u.second;
                        }
                        if(u.first.equals("PassengerDestination")){
                            mPassengerEndLocation = u.second;
                        }

                    }

                    intent.putExtra("passenger_id", mPassengerId);
                    intent.putExtra("passenger_start_location", mPassengerStartLocation);
                    intent.putExtra("passenger_end_location", mPassengerEndLocation);

                    intent.putExtra("rider_id", mRiderId);
                    intent.putExtra("rider_start_location", mRiderStartLocation);
                    intent.putExtra("rider_end_location", mRiderEndLocation);
                } else {
                    Log.d(TAG, "onRideCheckCompleted:  goign to MainActivity from checkForOngoin");
                    // Do something else if there's no ongoing ride
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                }
                mRideChecked = true;
                if(mAnimationCompleted) {
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}