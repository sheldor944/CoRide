package com.example.myapplication.ui.home;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.ChatActivity;
import com.example.myapplication.LocationDB;
import com.example.myapplication.R;
import com.example.myapplication.Register;
import com.example.myapplication.data.model.LocationData;
import com.example.myapplication.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;


    public void joinRide (View view)
    {

    }
    public void scheduleRide (View view)
    {

    }
    public interface TokenCallback {
        void onTokenReceived(String token);
    }

    public void getFCMtoken(Register.TokenCallback callback) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    String token = task.getResult();
                    Log.d("token", "onComplete: " + token);
                    callback.onTokenReceived(token);
                } else {
                    Log.d("token", "onComplete: token generation failed");
                    callback.onTokenReceived(null);
                }
            }
        });
    }



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Assuming the user is already authenticated
        getFCMtoken(new Register.TokenCallback() {
            @Override
            public void onTokenReceived(String token) {
                Log.d(TAG, "onTokenReceived: " + userId + "     kita   " + token);
                // Use the token here
                databaseReference.child(userId).child("fcmToken").setValue(token)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Error updating token", e);
                            }
                        });
            }
        });

        Button button =  (Button)(root.findViewById(R.id.button2));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext() , startRide.class);
                startActivity(i);
            }
        });
        Button button1 =  (Button)(root.findViewById(R.id.button3));
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext() , joinRide.class);
                startActivity(i);
            }
        });

        Button button2 =  (Button)(root.findViewById(R.id.button6));
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext() , scheduleRide.class);
                startActivity(i);
            }
        });
        LocationDB locationDB = new LocationDB();
//
        Button readButton =  (Button)(root.findViewById(R.id.readButton));
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    // null pointer der ota dekha lagbo
                    ArrayList<LocationData> locationDataArrayList = locationDB.getLocation("Rider");

                }
                catch (Exception e){
                    System.out.println(e);
                }
                Intent intent = new Intent(getContext() , ChatActivity.class);
                startActivity(intent);
            }

        });
        Button testButton =  (Button)(root.findViewById(R.id.testButton));
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationDB.updateLocation("" , "Rider");
            }
        });


        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}