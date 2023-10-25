package com.example.myapplication;

import android.provider.ContactsContract;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.example.myapplication.data.model.LocationData;
import com.example.myapplication.helper.BookedPassengerListCallback;
import com.example.myapplication.helper.GetDataFromCompletedTableCallback;
import com.example.myapplication.helper.LocationCallback;
import com.example.myapplication.helper.RideCheckCallback;
import com.example.myapplication.helper.SaveToCompletedTableCallback;
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

import org.checkerframework.checker.guieffect.qual.UI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

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
    public void updatePendingDriverLocation(String currentLocation )
    {

        database.getReference().child("PendingRider").child(userId).child("start").setValue(currentLocation);

    }

    public void insertIntoPassengerRider(String PID , String RID){

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


//    public void updateLocation(String location , String type )
//    {
//        Log.d("updateLocation", "updateLocation: " + location);
//
//        try {
//            Log.d("updateLocation", "updateLocation: 2  " + location);
//
////            databaseReference.child(userId).child("location").setValue(location);
//            databaseReference.child(userId).child("location").child(type).setValue(location);
//
//            System.out.println(userId);
//            Log.d("updateLocation", "updateLocation: " + userId + location);
//
//        }
//        catch (Exception e)
//        {
//            System.out.println(e);
//        }
//    }

    public void getLocation(String type, LocationCallback callback)
    {
        ArrayList<LocationData> locationDataArrayList = new ArrayList<>();
        database.getReference("PendingRider").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
//
                    String startLocation = userSnapshot.child("start").getValue(String.class);
                    String endLocation = userSnapshot.child("Destination").getValue(String.class);
                    // Do something with the user's location
                    locationDataArrayList.add(new LocationData("Rider" , startLocation , userId , endLocation));
                    Log.d(TAG, "onDataChange:  gg"+ startLocation);
                }
                callback.onLocationDataReceived(locationDataArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    public void getDataFromCompletedTable(String UID , GetDataFromCompletedTableCallback callback)
    {
//        DatabaseReference ref = database.getReference("CompletedRide").child("IvaNhWsFchZJZcYbr6XGNmHiXGR2@5eEHiNS0mIW9CAC6xqbFVdvplnH3");
            DatabaseReference ref = database.getReference("CompletedRide");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             ArrayList  < ArrayList<Pair<String, String>> > resultList = new ArrayList<>();
                Log.d(TAG, "onDataChange: dukse to re ba ");

                for(DataSnapshot childIdSnapshot : dataSnapshot.getChildren()){
                    String k = childIdSnapshot.getKey();
                    System.out.println(k);
                    String id[] = k.split("@");

                    if(id[0].equals(UID)){
                        for (DataSnapshot recordIdSnapshot : childIdSnapshot.getChildren()) {
                            ArrayList<Pair<String, String>> result = new ArrayList<>();

                            for (DataSnapshot pairSnapshot : recordIdSnapshot.getChildren()) {
                                String first = pairSnapshot.child("first").getValue(String.class);
                                String second = pairSnapshot.child("second").getValue(String.class);
                                System.out.println(first + "  ono " + second);

                                if (first != null && second != null) {
                                    result.add(new Pair<>(first, second));
                                }
                            }
                            resultList.add(result);
                        }
                    }
                    else if (id[1].equals(UID)){
                        System.out.println("as a rider ");
                        for (DataSnapshot recordIdSnapshot : childIdSnapshot.getChildren()) {
                            ArrayList<Pair<String, String>> result = new ArrayList<>();

                            for (DataSnapshot pairSnapshot : recordIdSnapshot.getChildren()) {
                                String first = pairSnapshot.child("first").getValue(String.class);
                                String second = pairSnapshot.child("second").getValue(String.class);
                                System.out.println(first + "  ono " + second);

                                if (first != null && second != null) {
                                    result.add(new Pair<>(first, second));
                                }
                            }
                            resultList.add(result);
                        }
                    }

                }

                // Iterate through the list of saved pairs


                if (callback != null) {
                    callback.onGetDataFromCompletedTableComplete(resultList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error or notify the callback about the failure.
            }
        });
    }

    public void saveToCompletedTable(String passengerID , String riderID , double fare , SaveToCompletedTableCallback callback)
    {
        checkForOngoingRide(new RideCheckCallback() {
            @Override
            public void onRideCheckCompleted(ArrayList<Pair<String, String>> result) {
                ArrayList<Pair<String , String>> finalResult = result;
                Log.d(TAG, "onRideCheckCompleted: save to tabel o dukse ");
                finalResult.add(new Pair<>("Fair", "" + fare));

                database.getReference("CompletedRide").child(passengerID+"@"+riderID)
                        .push().setValue(finalResult);

                // TODO: 10/24/2023
                // bookedPassengerRider theke delete korte hobe
                if (callback != null) {

                    callback.onSaveToCompletedTableComplete(finalResult);
                }
            }

        });

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
                String passengerId = null;
                
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
                callback.onPassengerFound(null);
            }
        });
    }

    public boolean isRiderAvailable(String riderId) {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean isAvailable = new AtomicBoolean(false);

        database.getReference("PendingRider").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    String startLocation = userSnapshot.child("start").getValue(String.class);
                    String endLocation = userSnapshot.child("Destination").getValue(String.class);
                    // Do something with the user's location
                    if (userId.equals(riderId)) { // Use .equals for string comparison
                        // TODO: ২৪/১০/২৩ : rider matched. delete him.
                        isAvailable.set(true);
                        break;
                    }
                }

                // Signal that the database query has finished
                latch.countDown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error

                // Also, signal that the query has finished in case of an error
                latch.countDown();
            }
        });

        try {
            // Wait for the database query to finish (or for an error to occur)
            latch.await();
        } catch (InterruptedException e) {
            Log.d(TAG, "isRiderAvailable: latch await error: " + e.getMessage());
//            Thread.currentThread().interrupt(); // Restore the interrupted status
        }

        return isAvailable.get();
    }

}
