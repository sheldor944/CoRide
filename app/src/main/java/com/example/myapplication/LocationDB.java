package com.example.myapplication;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication.data.model.LocationData;
import com.example.myapplication.helper.BookedPassengerListCallback;
import com.example.myapplication.helper.LocationCallback;
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
import com.google.firebase.ktx.Firebase;
import com.google.protobuf.DescriptorProtos;

import java.util.ArrayList;
import java.util.Random;

import javax.xml.transform.dom.DOMLocator;

import io.grpc.internal.JsonUtil;

public class LocationDB {
    DatabaseReference databaseReference;
    FirebaseDatabase database ;
    String TAG = "LocationDB";

    String userId ; // Assuming the user is already authenticated
//    String location = latitude + "," + longitude;
    FirebaseFirestore db ;

    public LocationDB() {
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        database = FirebaseDatabase.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Assuming the user is already authenticated
//    String location = latitude + "," + longitude;
        db = FirebaseFirestore.getInstance();
        Log.d(TAG, "LocationDB: " +userId);

    }

    public void addToPendingRider(String location)
    {
        Log.d("updateLocation", "updateLocation: " + location);

        try {
            Log.d("updateLocation", "updateLocation: 2  " + location);

//            databaseReference.child(userId).child("location").setValue(location);
//            databaseReference.child(userId).child("PendingRider").setValue(location);
            database.getReference().child("PendingRider").setValue(location);
            System.out.println(userId);
            Log.d("updateLocation", "updateLocation: " + userId + location);

        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    public void insetIntoPassengerRider(String PID , String RID){

        try{
            Log.d(TAG, "insetIntoPassengerRider: "+ PID+" "+ RID);
            database.getReference("bookedPassengerRider").push().setValue(PID+"@" +RID);
//            databaseReference.child("bookedPaasengerRider").setValue(PID + "@" +RID);
        }
        catch (Exception e){
            Log.d(TAG, "insetIntoPassengerRider: " + e) ;
        }

    }

    public void updateLocation(String location , String type )
    {
        Log.d("updateLocation", "updateLocation: " + location);

        try {
            Log.d("updateLocation", "updateLocation: 2  " + location);

//            databaseReference.child(userId).child("location").setValue(location);
            databaseReference.child(userId).child("location").child(type).setValue(location);

            System.out.println(userId);
            Log.d("updateLocation", "updateLocation: " + userId + location);

        }
        catch (Exception e)
        {
            System.out.println(e);
        }
//        getLocation("Rider");
    }

    public void getLocation(String type, LocationCallback callback)
    {
        ArrayList<LocationData> locationDataArrayList = new ArrayList<>();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
//                    String location = userSnapshot.child("location").getValue(String.class);
                    String location = userSnapshot.child("location").child(type).getValue(String.class);
                    
                    System.out.println(userId + " " + location);
                    locationDataArrayList.add(new LocationData(type, location, userId));
                    // Do something with the user's location
                }
                callback.onLocationDataReceived(locationDataArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    public void getBookedPassenger(BookedPassengerListCallback callback) {

        String UID = FirebaseAuth.getInstance().getUid();
        Log.d(TAG, "getBookedPassenger: "+UID);

        database.getReference("bookedPassengerRider").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String passengerId = "as";
                
                Log.d(TAG, "onDataChange: loop er age ");
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
//                    String location = userSnapshot.child("location").getValue(String.class);
                    String location = userSnapshot.getValue(String.class);
                    Log.d(TAG, "onDataChange: hamaise " + location);
                    String[] id = location.split("@");
                    Log.d(TAG, "onDataChange: " + id[1]  + " " + UID);
                    if(id[1].equals(UID)){
                        Log.d(TAG, "onDataChange: userID paisi passenger is " + id[0]);
                        // TODO: 10/22/2023  
//                        userSnapshot.getRef().removeValue();
                        passengerId=id[0];
                        break;
                    }

                   
                   
                    // Do something with the user's location
                }
                callback.onPassengerFound(passengerId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        get passenger id
    }
}
