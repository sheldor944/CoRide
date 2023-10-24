package com.example.myapplication;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.example.myapplication.data.model.LocationData;
import com.example.myapplication.helper.BookedPassengerListCallback;
import com.example.myapplication.helper.LocationCallback;
import com.example.myapplication.helper.RideCheckCallback;
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
    FirebaseFirestore db ;

    public LocationDB() {
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        database = FirebaseDatabase.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Assuming the user is already authenticated
        db = FirebaseFirestore.getInstance();
        Log.d(TAG, "LocationDB: " +userId);

    }

    public void addToPendingRider(String startLocation ,String  destinationLocation)
    {
        Log.d(TAG, "addToPendingRider: "+ startLocation + " "+ destinationLocation) ;
        try {

            database.getReference().child("PendingRider").child(userId).child("start").setValue(startLocation);
            database.getReference().child("PendingRider").child(userId).child("Destination").setValue(destinationLocation);

            System.out.println(userId);
            Log.d("updateLocation", "updateLocation: " + userId + " "+ startLocation + " "+ destinationLocation);

        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    public void insetIntoPassengerRider(String PID , String RID){

        try{
            Log.d(TAG, "insetIntoPassengerRider: "+ PID+" "+ RID);
            database.getReference("bookedPassengerRider").child(PID+"@"+RID).child("passengerRoute").child("Start").setValue("24,23");
            database.getReference("bookedPassengerRider").child(PID+"@"+RID).child("passengerRoute").child("Destination").setValue("24,23.5");
            database.getReference("bookedPassengerRider").child(PID+"@"+RID).child("RiderRoute").child("Start").setValue("24,23");
            database.getReference("bookedPassengerRider").child(PID+"@"+RID).child("RiderRoute").child("Destination").setValue("24,23.5");
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

    public void saveToCompletedTable()
    {

    }

    public void checkForOngoingRide(RideCheckCallback callback) {
        String UID = FirebaseAuth.getInstance().getUid();
        Log.d(TAG, "getBookedPassenger: " + UID);
        database.getReference("bookedPassengerRider").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean hasRide = false;
                ArrayList<Pair<String,String>> result = new ArrayList<>();
                String RiderStart="" , RiderDestination="" , PassengerStart="" , PassengerDestination="";
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String key = childSnapshot.getKey();
                    String id[] = key.split("@");
                    Log.d(TAG, "onDataChange: " + key);

                    if (UID.equals(id[1]) )
                    {
                        DataSnapshot passengerRouteSnapshot = childSnapshot.child("passengerRoute");
                        if (passengerRouteSnapshot.exists()) {
                            PassengerStart = passengerRouteSnapshot.child("Start").getValue(String.class);
                            PassengerDestination = passengerRouteSnapshot.child("Destination").getValue(String.class);
                        }

                        DataSnapshot riderRouteSnapshot = childSnapshot.child("RiderRoute");
                        if (riderRouteSnapshot.exists()) {
                            RiderStart = riderRouteSnapshot.child("Start").getValue(String.class);
                            RiderDestination = riderRouteSnapshot.child("Destination").getValue(String.class);
                        }
                        result.add(new Pair<>("type" , "Passenger"));

                        result.add(new Pair<>("passengerName" , "Passenger"));
                        result.add(new Pair<>("riderName" , "Rider"));

                        result.add(new Pair<>("RiderStart" , RiderStart));
                        result.add(new Pair<>("RiderDestination" , RiderDestination));
                        result.add(new Pair<>("PassengerStart" , PassengerStart));
                        result.add(new Pair<>("PassengerDestination" , PassengerDestination));
                        break;

                    }
                    else if( UID.equals(id[0])) {
                        DataSnapshot passengerRouteSnapshot = childSnapshot.child("passengerRoute");
                        if (passengerRouteSnapshot.exists()) {
                            PassengerStart = passengerRouteSnapshot.child("Start").getValue(String.class);
                            PassengerDestination = passengerRouteSnapshot.child("Destination").getValue(String.class);
                        }

                        DataSnapshot riderRouteSnapshot = childSnapshot.child("RiderRoute");
                        if (riderRouteSnapshot.exists()) {
                            RiderStart = riderRouteSnapshot.child("Start").getValue(String.class);
                            RiderDestination = riderRouteSnapshot.child("Destination").getValue(String.class);
                        }
                        result.add(new Pair<>("type" , "Passenger"));

                        result.add(new Pair<>("passengerName" , "Passenger"));
                        result.add(new Pair<>("riderName" , "Rider"));

                        result.add(new Pair<>("RiderStart" , RiderStart));
                        result.add(new Pair<>("RiderDestination" , RiderDestination));
                        result.add(new Pair<>("PassengerStart" , PassengerStart));
                        result.add(new Pair<>("PassengerDestination" , PassengerDestination));
                        break;
                    }
                }
                if (callback != null) {
                    callback.onRideCheckCompleted(result);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error or notify the callback about the failure.
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
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                    // Here, you can access each child of bookedPassengerRider
                    String key = childSnapshot.getKey();
                    Log.d(TAG, "onDataChange: seraching Child " + key);
                    String id[] = key.split("@");
                    Log.d(TAG, "onDataChange: " + id[0] + id[1] );

                    // Check your condition
                    if (UID.equals(id[1])) {
                        // Condition satisfied, traverse its children
                        passengerId=id[0];
                        break;
//                        for (DataSnapshot grandChildSnapshot : childSnapshot.getChildren()) {
//                            // Access the child's child data here using grandChildSnapshot
//                            String someData = grandChildSnapshot.child("someChildKey").getValue(String.class);
//                            // ... process the data as needed
//                        }
                    }
                }

                callback.onPassengerFound(passengerId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
