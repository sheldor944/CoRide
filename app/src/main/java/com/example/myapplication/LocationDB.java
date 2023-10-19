package com.example.myapplication;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication.data.model.LocationData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.protobuf.DescriptorProtos;

import java.util.ArrayList;
import java.util.Random;

import io.grpc.internal.JsonUtil;

public class LocationDB {
    DatabaseReference databaseReference;

    String userId ; // Assuming the user is already authenticated
//    String location = latitude + "," + longitude;
    FirebaseFirestore db ;

    public LocationDB() {
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Assuming the user is already authenticated
//    String location = latitude + "," + longitude;
        db = FirebaseFirestore.getInstance();

    }

    public void updateLocation(String location , String type )
    {

         double MIN_LATITUDE = -90;
        double MAX_LATITUDE = 90;
         double MIN_LONGITUDE = -180;
         double MAX_LONGITUDE = 180;

        Random random = new Random();

        // Generate a random latitude value between -90 and +90
        double lat = MIN_LATITUDE + (MAX_LATITUDE - MIN_LATITUDE) * random.nextDouble();

        // Generate a random longitude value between -180 and +180
        double longi = MIN_LONGITUDE + (MAX_LONGITUDE - MIN_LONGITUDE) * random.nextDouble();

        location = lat + "," + longi;

        try {
//            databaseReference.child(userId).child("location").setValue(location);
            databaseReference.child(userId).child("location").child(type).setValue(location);

            System.out.println(userId);
            Log.d("updateLocation", "updateLocation: " + userId + location);

        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        getLocation("Rider");
    }

    public ArrayList<LocationData> getLocation(String type)
    {
        ArrayList<LocationData> locationDataArrayList = new ArrayList<>();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
//                    String location = userSnapshot.child("location").getValue(String.class);
                    String location = userSnapshot.child("location").child(type).getValue(String.class);


                    DocumentReference userDocRef = db.collection("users").document(userId);
                    userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                                    try{
//                                        String name = document.getString("firstName");
//                                        System.out.println(name + " " + location);
                                        Log.d("addingToarrayList", "onComplete: " + type +"  " + location +" " +userId );
                                        locationDataArrayList.add(new LocationData(type , location , userId));
                                        // Use the document data
                                    }
                                    catch (Exception e)
                                    {
                                        System.out.println(e);
                                    }

                                } else {
                                    Log.d("TAG", "No such document");
                                }
                            } else {
                                Log.d("TAG", "get failed with ", task.getException());
                            }

                        }
                    });
                    System.out.println(userId + " " + location);
                    // Do something with the user's location
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });


        return locationDataArrayList;
    }
}
