package com.example.myapplication.ui.home;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.ChatActivity;
import com.example.myapplication.LocationDB;
import com.example.myapplication.R;
import com.example.myapplication.Register;
import com.example.myapplication.data.model.LocationData;
import com.example.myapplication.databinding.FragmentHomeBinding;
import com.example.myapplication.helper.Callback;
import com.example.myapplication.helper.GetDataFromCompletedTableCallback;
import com.example.myapplication.helper.LocationCallback;
import com.example.myapplication.helper.RideCheckCallback;
import com.example.myapplication.helper.SaveToCompletedTableCallback;
import com.example.myapplication.testerActivity;
import com.example.myapplication.ui.dashboard.RiderHistoryActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.rpc.context.AttributeContext;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private final String TAG = "HomeFragment";
    private FragmentHomeBinding binding;
    private CardView mProvideARideCardView;
    private Button mOnGoigRide;


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

        getActivity().setTitle("Home");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        mOnGoigRide = root.findViewById(R.id.OnGoingRide);

        mOnGoigRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationDB locationDB = new LocationDB() ;
                checkForOnGoingRide();
                locationDB.getTOKEN("BZgNkwSJNzWLfZOdkxM1pryCGC92", new Callback<String>() {
                    @Override
                    public void onComplete(String response) {
                        Log.d(TAG, "onComplete: got the id "+ response);
                    }
                });
//
//                locationDB.saveToCompletedTable("5eEHiNS0mIW9CAC6xqbFVdvplnH3", "ItIDT7jlUDOMRheIs4fif8DTc0A2", 500, new SaveToCompletedTableCallback() {
//                    @Override
//                    public void onSaveToCompletedTableComplete(ArrayList<Pair<String, String>> result) {
//                        Log.d(TAG, "onSaveToCompletedTableComplete:  saved ");
//                    }
//                });
//
//                locationDB.insertIntoPassengerRider();
//              locationDB.saveToCompletedTable("5eEHiNS0mIW9CAC6xqbFVdvplnH3" , "ItIDT7jlUDOMRheIs4fif8DTc0A2" , new SaveToCompletedTableCallback());
            }
        });

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
        Button notification = root.findViewById(R.id.notificationTester);

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                locationDB.saveToCompletedTable("IvaNhWsFchZJZcYbr6XGNmHiXGR2", "5eEHiNS0mIW9CAC6xqbFVdvplnH3", 500, new SaveToCompletedTableCallback() {
              

              
                Intent intent = new Intent(getContext() , testerActivity.class);
                startActivity(intent);
            }
        });
//        notification.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getContext() , testerActivity.class);
//                startActivity(intent);
//                LocationDB locationDB = new LocationDB();
//                checkForOnGoingRide();
//                locationDB.getLocation("Rider", new LocationCallback() {
//                    @Override
//                    public void onLocationDataReceived(ArrayList<LocationData> locationDataArrayList) {
//                        System.out.println("printing riders ");
//                        for(LocationData locationData : locationDataArrayList){
//
////                            System.out.println( " " + locationData.getStartLocation() + " " + locationData.getEndLocation());
//                            System.out.println(locationData.getEndLocation());
//
////                            System.out.println(locationData.getUserID() + " " + locationData.getStartLocation() + " " + locationData.getEndLocation());
//                        }
//                    }
//                });

//                locationDB.saveToCompletedTable("IvaNhWsFchZJZcYbr6XGNmHiXGR2", "5eEHiNS0mIW9CAC6xqbFVdvplnH3", 500, new SaveToCompletedTableCallback() {
//                    @Override
//                    public void onSaveToCompletedTableComplete(ArrayList<Pair<String, String>> result) {
//                        for( Pair<String , String > u : result)
//                        {
//                            System.out.println(u.first +" " + u.second);
//                        }
//                    }
//                }) ;
//            }
//        });

        mProvideARideCardView = root.findViewById(R.id.provide_a_ride_cardview);
        mProvideARideCardView.setOnClickListener(view -> {
            Log.d(TAG, "onClick: clicked on Start Ride");
            Intent i = new Intent(getContext() , ProvideARideActivity.class);
            startActivity(i);
        });

//        Button button =  (Button)(root.findViewById(R.id.button2));
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d(TAG, "onClick: clicked on Start Ride");
//                Intent i = new Intent(getContext() , StartRideActivity.class);
//                startActivity(i);
//            }
//        });

        CardView getARideCardView = (CardView) (root.findViewById(R.id.get_a_ride_cardview));
        getARideCardView.setOnClickListener(view -> {
            Log.d(TAG, "onClick: clicked on Start Ride");
            Intent i = new Intent(getContext() , StartRideActivity.class);
            startActivity(i);
        });

        getARideCardView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Change background color and elevation when touched
                        getARideCardView.setCardBackgroundColor(getResources().getColor(R.color.gray)); // Change to desired color
                        getARideCardView.setCardElevation(8); // Adjust the elevation as needed
                        break;
                    case MotionEvent.ACTION_UP:
                        // Restore original background color and elevation when touch is released
                        getARideCardView.setCardBackgroundColor(getResources().getColor(R.color.white));
                        getARideCardView.setCardElevation(4); // Restore the original elevation
                        break;
                }
                return false;
            }
        });

//        Button button1 =  (Button)(root.findViewById(R.id.button3));
//        button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(getContext() , ProvideARideActivity.class);
//                startActivity(i);
//            }
//        });

//        Button button2 =  (Button)(root.findViewById(R.id.button6));
//        button2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(getContext() , scheduleRide.class);
//                startActivity(i);
//            }
//        });
        LocationDB locationDB = new LocationDB();
//
//        Button readButton =  (Button)(root.findViewById(R.id.readButton));
//        readButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    ArrayList<LocationData> locationDataArrayList = locationDB.getLocation("Rider");
//                    Log.d("print in home ", "getLocation: print oibo ekhn ");
//                    for(LocationData u : locationDataArrayList)
//                    {
//                        System.out.println(u.getType() +" type  " + u.getLocation()+" " + u.getUserID());
//                    }
//                    Log.d("print in home ", "getLocation: print oiges ");
//                }
//                catch(Exception e ){
//                    System.out.println(e);
//                }
//                Intent intent = new Intent(getContext() , ChatActivity.class);
//                startActivity(intent);
//            }
//
//        });
//        Button testButton =  (Button)(root.findViewById(R.id.testButton));
//        testButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                locationDB.updateLocation("" , "Rider");
//            }
//        });
        return root;
    }

    private void checkForOnGoingRide() {
        LocationDB locationDB = new LocationDB();

        locationDB.checkForOngoingRide(  new RideCheckCallback() {
            @Override
            public void onRideCheckCompleted(ArrayList<Pair<String , String >> result ) {
                Log.d(TAG, "onRideCheckCompleted: starting the check ");
                if (result.size() >0) {
                    // Do something if there's an ongoing ride
                    Log.d(TAG, "onRideCheckCompleted: this person has a ride ");
                    mOnGoigRide.setEnabled(true);
                    for( Pair<String, String > u : result)
                    {
                        System.out.println(u.first+ " "+ u.second);
                    }
                } else {
                    // Do something else if there's no ongoing ride
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}