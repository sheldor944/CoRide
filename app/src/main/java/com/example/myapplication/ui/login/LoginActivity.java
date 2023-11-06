package com.example.myapplication.ui.login;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityLoginBinding;
import com.example.myapplication.helper.GetDataFromCompletedTableCallback;
import com.example.myapplication.helper.RideCheckCallback;
import com.example.myapplication.service.LocationDB;
import com.example.myapplication.ui.ChatActivity;
import com.example.myapplication.ui.MainActivity;
import com.example.myapplication.ui.Register;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    public void goToRegister(View view) {
        Intent intent = new Intent(getApplicationContext(), Register.class);
        startActivity(intent);
    }

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    public ProgressDialog progressDialog ;



    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post profile.
                    Log.d("permission", "granted: ");
                } else {
                    // TODO: Inform user that that your app will not show profile.
                }
            });

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post profile.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without profile.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
    private static final String TAG = "LoginActivity";
    private void checkForOnGoingRide() {
        LocationDB locationDB = new LocationDB();

        locationDB.checkForOngoingRide(  new RideCheckCallback() {
            @Override
            public void onRideCheckCompleted(ArrayList<Pair<String , String >> result ) {
                Log.d(TAG, "onRideCheckCompleted: starting the check ");
                if (result.size() >0) {
                    // Do something if there's an ongoing ride
                    Log.d(TAG, "onRideCheckCompleted: this person has a ride ");
                    Intent intent = new Intent(getApplicationContext() , ChatActivity.class);

                    String mPassengerId="" , mPassengerStartLocation="" , mPassengerEndLocation="",
                            mRiderId="" , mRiderStartLocation="" , mRiderEndLocation="";
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
                    startActivity(intent);

                    for( Pair<String, String > u : result)
                    {
                        System.out.println(u.first+ " "+ u.second);
                    }
                } else {
                    // Do something else if there's no ongoing ride
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String user = mAuth.getUid();
        locationDB.getDataFromCompletedTable(user, new GetDataFromCompletedTableCallback() {
            @Override
            public void onGetDataFromCompletedTableComplete(ArrayList<ArrayList<Pair<String, String>>> result) {
                for(ArrayList<Pair<String , String >> u : result)
                {
                    for(Pair<String , String> p : u )
                    {
                        System.out.println(" home " + p.first + " -->" +p.second );
                    }
                }
            }
        });


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        EditText emailText = (EditText) findViewById(R.id.username);
        EditText pass = (EditText) findViewById(R.id.password);

        AppCompatButton button = findViewById(R.id.login);
//        progressDialog.findViewById(R.id.loading);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        askNotificationPermission();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("112233", "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        if(mAuth.getCurrentUser() != null){
//            checkForOnGoingRide();
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(intent);
        }

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String email = String.valueOf(emailText.getText());
                String password= String.valueOf(pass.getText());
                if(email.length()>0 && password.length()>0)
                {
                    progressDialog.show();

//                    button.setEnabled(true);
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {
                                        // Login success
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Log.d(TAG, "onComplete: succeesful o aise ");
                                        if(user.isEmailVerified()){
                                            Toast.makeText(LoginActivity.this , "Success " , Toast.LENGTH_SHORT).show();
                                            checkForOnGoingRide();
                                        }
                                        else{
                                            progressDialog.dismiss();

                                            Toast.makeText(LoginActivity.this , "Verify your email! " , Toast.LENGTH_SHORT).show();

                                        }


//                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                                        startActivity(intent);
                                        // Update your UI with the user's information
                                    }
                                    else {
                                        progressDialog.dismiss();

                                        Toast.makeText(LoginActivity.this , "Error with Credentials" , Toast.LENGTH_LONG ).show();
                                        // Login failure
                                        // Handle the failure here
                                    }
                                }
                            });
                }
            }
        });





    }

    @Override
    protected void onResume() {
        super.onResume();

        // Dismiss the progressbar when the new intent has loaded
//        progressDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle the "Yes" button click
                        // Add your logic here to perform the action
                        LoginActivity.this.finishAffinity();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle the "No" button click or simply dismiss the dialog
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}