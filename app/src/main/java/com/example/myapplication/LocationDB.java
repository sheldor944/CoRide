package com.example.myapplication;

import android.net.Uri;
import android.provider.ContactsContract;
import android.telecom.Call;
import android.util.Log;
import android.util.Pair;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.myapplication.data.model.LocationData;
import com.example.myapplication.helper.BookedPassengerListCallback;
import com.example.myapplication.helper.Callback;
import com.example.myapplication.helper.GetDataFromCompletedTableCallback;
import com.example.myapplication.helper.GetUserNameCallback;
import com.example.myapplication.helper.LocationCallback;
import com.example.myapplication.helper.RideCheckCallback;
import com.example.myapplication.helper.SaveToCompletedTableCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.protobuf.DescriptorProtos;

import org.checkerframework.checker.guieffect.qual.UI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
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
        if(FirebaseAuth.getInstance() != null) userId = FirebaseAuth.getInstance().getUid();
        db = FirebaseFirestore.getInstance();
    }

    public void deleteFromPendingRider(String riderID )
    {
        try{
            database.getReference().child("PendingRider").child(riderID).removeValue();

        }
        catch (Exception e ){
            Log.d(TAG, "deleteFromPendingRider: error " + e );
        }
    }

    public void addToPendingRider(String startLocation ,String  destinationLocation, int distance)
    {
        Log.d(TAG, "addToPendingRider: "+ startLocation + " "+ destinationLocation) ;
        try {

            database.getReference().child("PendingRider").child(userId).child("start").setValue(startLocation);
            database.getReference().child("PendingRider").child(userId).child("Destination").setValue(destinationLocation);
            database.getReference().child("PendingRider").child(userId).child("distance").setValue(distance);

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
        userId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("PendingRider").child(userId).child("start").setValue(currentLocation);

    }

    public void insertIntoPassengerRider(LocationData passengerData, LocationData riderData){

        try{
            String PID = passengerData.getUserID();
            String RID = riderData.getUserID();
            Log.d(TAG, "insetIntoPassengerRider: "+ PID+" "+ RID);
            database.getReference("bookedPassengerRider")
                    .child(PID+"@"+RID)
                    .child("pickupStatus")
                    .setValue("false");


            database.getReference("bookedPassengerRider")
                    .child(PID+"@"+RID)
                    .child("passengerRoute")
                    .child("Start")
                    .setValue(
                    passengerData.getStartLocation()
            );
            database.getReference("bookedPassengerRider")
                    .child(PID+"@"+RID)
                    .child("passengerRoute")
                    .child("Destination")
                    .setValue(
                    passengerData.getEndLocation()
            );
            database.getReference("bookedPassengerRider")
                    .child(PID+"@"+RID)
                    .child("passengerRoute")
                    .child("Distance")
                    .setValue(
                    passengerData.getDistance()
            );

            database.getReference("bookedPassengerRider")
                    .child(PID+"@"+RID)
                    .child("RiderRoute")
                    .child("Start")
                    .setValue(
                    riderData.getStartLocation()
            );
            database.getReference("bookedPassengerRider")
                    .child(PID+"@"+RID)
                    .child("RiderRoute")
                    .child("Destination")
                    .setValue(
                    riderData.getEndLocation()
            );
            database.getReference("bookedPassengerRider").child(PID+"@"+RID).child("RiderRoute").child("Distance").setValue(
                    riderData.getDistance()
            );
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
                    int distance = userSnapshot.child("distance").getValue(Integer.class);
                    // Do something with the user's location
                    locationDataArrayList.add(new LocationData("Rider" , startLocation , userId , endLocation, distance));
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
                                if(first.equals("type")){
                                    second = "Passenger";
                                }
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
                                if(first.equals("type")){
                                    second = "Rider";
                                }
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
    public void deleteFromBookedPassenger(String passengerID , String riderID )
    {
        DatabaseReference dbRef = database.getReference("bookedPassengerRider");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange: delete Function  ");

                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    String key = dataSnapshot.getKey();
                    Log.d(TAG, "onDataChange: Delete table " + key );
                    try{
                        if(key.equals(passengerID+"@"+riderID))
                        {
                            Log.d(TAG, "onDataChange: found and will be deleted now ");
                            dataSnapshot.getRef().removeValue();
                            Log.d(TAG, "onDataChange: found and deleted  ");

                            break;
                        }
                    }
                    catch (Exception e ){
                        Log.d(TAG, "onDataChange: " + e);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void saveToCompletedTable(String passengerID , String riderID , double fare , SaveToCompletedTableCallback callback)
    {
        Log.d(TAG, "saveToCompletedTable: ");
        checkForOngoingRide(new RideCheckCallback() {
            @Override
            public void onRideCheckCompleted(ArrayList<Pair<String, String>> result) {

                ArrayList<Pair<String , String>> finalResult = result;
                Log.d(TAG, "onRideCheckCompleted: save to tabel o dukse ");
                finalResult.add(new Pair<>("Fair", "" + fare));
                finalResult.add(new Pair<>("RiderID" , riderID));
                finalResult.add(new Pair<>("PassengerID" , passengerID));
                for(Pair<String, String > u : finalResult){
                    System.out.println(u.first + " " + u.second);
                }

//
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
                        result.add(new Pair<>("type" , "Rider"));

                        result.add(new Pair<>("passengerName" , "Passenger"));
                        result.add(new Pair<>("riderName" , "Rider"));

                        result.add(new Pair<>("RiderStart" , RiderStart));
                        result.add(new Pair<>("RiderDestination" , RiderDestination));
                        result.add(new Pair<>("PassengerStart" , PassengerStart));
                        result.add(new Pair<>("PassengerDestination" , PassengerDestination));
                        result.add(new Pair<>("PassengerID" , id[0]));
                        result.add(new Pair<>("RiderID" , id[1]));
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
                        result.add(new Pair<>("PassengerID" , id[0]));
                        result.add(new Pair<>("RiderID" , id[1]));
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

    public void getTOKEN(String ID , Callback<String> callback)
    {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String res = null;
                for( DataSnapshot dataSnapshot : snapshot.getChildren() )
                {
                    if(dataSnapshot.getKey().equals(ID))
                    {
                        res = dataSnapshot.child("fcmToken").getValue(String.class);
                    }
                }
                callback.onComplete(res);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getBookedPassenger(BookedPassengerListCallback callback) {

        String UID = FirebaseAuth.getInstance().getUid();
        Log.d(TAG, "getBookedPassenger: "+UID);

        database.getReference("bookedPassengerRider").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LocationData passengerData = null;
                
                Log.d(TAG, "onDataChange: loop er age ");
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                    // Here, you can access each child of bookedPassengerRider
                    String key = childSnapshot.getKey();
                    Log.d(TAG, "onDataChange: seraching Child " + key);
                    String id[] = key.split("@");
                    Log.d(TAG, "onDataChange: " + id[0] + id[1] );


                    String passengerId, passengerStart, passengerDestination, pickupStatus;
                    int distance = -1;
                    // Check your condition
                    if (UID.equals(id[1])) {
                        Log.d(TAG, "onDataChange: match found");
                        // Condition satisfied, traverse its children
                        passengerId=id[0];
                        pickupStatus = childSnapshot
                                .child("pickupStatus")
                                .getValue(String.class);

                        DataSnapshot passengerRouteSnapshot = childSnapshot.child("passengerRoute");
                        if (passengerRouteSnapshot.exists()) {
                            Log.d(TAG, "onDataChange: data exists in passengerRoute");
                            passengerStart = passengerRouteSnapshot.child("Start").getValue(String.class);
                            passengerDestination = passengerRouteSnapshot.child("Destination").getValue(String.class);
                            distance = passengerRouteSnapshot.child("Distance").getValue(Integer.class);

                            Log.d(TAG, "onDataChange: " + passengerStart + " -> " + passengerDestination);

                            passengerData = new LocationData(
                                    "Passenger",
                                    passengerStart,
                                    passengerId,
                                    passengerDestination,
                                    distance
                            );
                        }
                        break;
//                        for (DataSnapshot grandChildSnapshot : childSnapshot.getChildren()) {
//                            // Access the child's child data here using grandChildSnapshot
//                            String someData = grandChildSnapshot.child("someChildKey").getValue(String.class);
//                            // ... process the data as needed
//                        }
                    }
                }

                callback.onPassengerFound(passengerData);
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


    public void getUserName(String UID , GetUserNameCallback callback)
    {
        DocumentReference userRef = db.collection("users").document(UID);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String  name = "" , phone="";

                if (task.isSuccessful()) {
                    Log.d("entered successful 4" , "successful o dukse 4 ");


                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        // Get the user's data from the DocumentSnapshot
                        Log.d("entered5" , "document exists ");

                        String firstName = documentSnapshot.getString("firstName");
//                        System.out.println(name);
                        String lastName = documentSnapshot.getString("lastName");
                        String email = documentSnapshot.getString("email");
                         phone = documentSnapshot.getString("phone");
                        name = firstName + lastName ;
                        Log.d(TAG, "onComplete: " + firstName + lastName);
                        Log.d(TAG, "onComplete: "+ name);

                    } else {
                        // The user's document does not exist
                        Log.w("document " , " document not found ");
                    }
                } else {
                    // An error occurred while getting the user's data
                }
                callback.onUserNameRecieved(name , phone);
            }
        });

    }

    public void getPickupStatus(String riderId, Callback <Boolean> callback) {
        String UID = riderId;
        Log.d(TAG, "getBookedPassenger: "+UID);

        database.getReference("bookedPassengerRider").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LocationData passengerData = null;
                String pickupStatus = null;

                Log.d(TAG, "onDataChange: loop er age ");
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                    // Here, you can access each child of bookedPassengerRider
                    String key = childSnapshot.getKey();
                    Log.d(TAG, "onDataChange: seraching Child " + key);
                    String id[] = key.split("@");
                    Log.d(TAG, "onDataChange: " + id[0] + id[1] );


                    String passengerId, passengerStart, passengerDestination;
                    int distance = -1;
                    // Check your condition
                    if (UID.equals(id[1])) {
                        Log.d(TAG, "onDataChange: match found");
                        // Condition satisfied, traverse its children
                        passengerId=id[0];
                        pickupStatus = childSnapshot
                                .child("pickupStatus")
                                .getValue(String.class);

                        DataSnapshot passengerRouteSnapshot = childSnapshot.child("passengerRoute");
                        if (passengerRouteSnapshot.exists()) {
                            Log.d(TAG, "onDataChange: data exists in passengerRoute");
                            passengerStart = passengerRouteSnapshot.child("Start").getValue(String.class);
                            passengerDestination = passengerRouteSnapshot.child("Destination").getValue(String.class);
                            distance = passengerRouteSnapshot.child("Distance").getValue(Integer.class);

                            Log.d(TAG, "onDataChange: " + passengerStart + " -> " + passengerDestination);

                            passengerData = new LocationData(
                                    "Passenger",
                                    passengerStart,
                                    passengerId,
                                    passengerDestination,
                                    distance
                            );
                        }
                        break;
//                        for (DataSnapshot grandChildSnapshot : childSnapshot.getChildren()) {
//                            // Access the child's child data here using grandChildSnapshot
//                            String someData = grandChildSnapshot.child("someChildKey").getValue(String.class);
//                            // ... process the data as needed
//                        }
                    }
                }

                callback.onComplete(pickupStatus.equals("true"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onComplete(null);
            }
        });
    }

    public void updatePickupStatus(String passengerId, String riderId) {
        String rideId = passengerId + "@" + riderId;
        database.getReference()
                .child("bookedPassengerRider")
                .child(rideId)
                .child("pickupStatus").setValue("true");
    }

    public void uploadImage(Uri fileUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference userImageRef = storageRef.child("user_images/" + userId );

        UploadTask uploadTask = userImageRef.putFile(fileUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Image uploaded successfully
            Log.d(TAG, "uploadImage: Success");
        }).addOnFailureListener(e -> {
            // Handle any errors
            Log.d(TAG, "uploadImage: error "+ e);
        });
    }
    public void getImageURL(String UID , Callback<Uri> callback)
    {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference userImageRef = storageRef.child("user_images/" + UID );

        userImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // You can set this URL to an ImageView to display the image
            Log.d(TAG, "getImageURL: Successful");
            callback.onComplete(uri);
//            ImageView imageView = findViewById(R.id.yourImageViewId);
//            Glide.with(this).load(uri).into(imageView); // Using Glide library to load the image
        }).addOnFailureListener(exception -> {
            Log.d(TAG, "getImageURL: error " + exception);
            // Handle any errors
        });
    }
    public void getImageURL(Callback<Uri> callback) {

        try
        {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            StorageReference userImageRef = storageRef.child("user_images/" + userId );
            userImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // You can set this URL to an ImageView to display the image
                Log.d(TAG, "getImageURL: Successful");
                callback.onComplete(uri);
//            ImageView imageView = findViewById(R.id.yourImageViewId);
//            Glide.with(this).load(uri).into(imageView); // Using Glide library to load the image
            }).addOnFailureListener(exception -> {
                Log.d(TAG, "getImageURL: error " + exception);
                // Handle any errors
            });
        }
        catch (Exception e )
        {
            Log.d(TAG, "getImageURL: " + e);
        }
    }

    public void updateLiveLocation(String userId, String location) {
//        database.getReference("bookedPassengerRider")
//                .child(PID+"@"+RID)
//                .child("pickupStatus")
//                .setValue("false");
        Log.d(TAG, "updateLiveLocation: updating location for: " + userId);
        try {
            database
                    .getReference("users")
                    .child(userId)
                    .child("live_location")
                    .setValue(location);
        } catch (Exception e) {
            Log.d(TAG, "updateLiveLocation: error: " + e.getMessage());
        }
    }

    public void getLiveLocation(String userId, Callback <String> callback) {
        Log.d(TAG, "getLiveLocation: id: " + userId);
        database
                .getReference("users")
                .child(userId)
                .child("live_location")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String liveLocation = snapshot.getValue(String.class);
                        Log.d(TAG, "onDataChange: live location: " + liveLocation);
                        if(liveLocation != null) {
                            callback.onComplete(liveLocation);
                        }
                        else {
                            Log.d(TAG, "onDataChange: live location is null");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "onCancelled: getLiveLocation failed. error: " + error.getMessage());
                    }
                });
    }
    public String[] getUserNamePhone(String riderID , String passengerID , Callback<String[]> callback)
    {
        String[] res = new String[4];

        getUserName(riderID, new GetUserNameCallback() {
            @Override
            public void onUserNameRecieved(String name, String phone) {
                res[0] = name ;
                res[1]= phone ;
                getUserName(passengerID, new GetUserNameCallback() {
                    @Override
                    public void onUserNameRecieved(String name, String phone) {
                        res[2]=name;
                        res[3] = phone;
                        callback.onComplete(res);
                    }
                });
            }

        });
        return res;
    }

    public void getUserNamePhoneUtil(String UID , Callback<String[]> callback)
    {
        DocumentReference userRef = db.collection("users").document(UID);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

            }
        });
    }
}
