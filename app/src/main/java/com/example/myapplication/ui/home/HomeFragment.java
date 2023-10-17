package com.example.myapplication.ui.home;

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
import com.example.myapplication.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;


    public void joinRide (View view)
    {

    }
    public void scheduleRide (View view)
    {

    }
    public void getFCMtoken()
    {
        final String[] token = new String[1];
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isSuccessful()){
                    token[0] = task.getResult();
                    Log.d("token", "onComplete: " + token[0]);
                }
                else{
                    Log.d("token", "onComplete: token generation faied|");
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

        getFCMtoken();

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
                locationDB.getLocation("Rider");
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