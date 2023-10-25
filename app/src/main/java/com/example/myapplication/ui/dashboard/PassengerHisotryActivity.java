package com.example.myapplication.ui.dashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.example.myapplication.databinding.ActivityPassengerHisotryBinding;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class PassengerHisotryActivity extends AppCompatActivity {


    private FirebaseDatabase database ;
    ActivityPassengerHisotryBinding binding;
    PassengerListAdapter passengerListAdapter;
    ArrayList<PassengerListData> passengerListDataArrayList = new ArrayList<>();
    PassengerListData passengerListData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPassengerHisotryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        for (int i = 0; i < 10; i++){
            passengerListData = new PassengerListData("Rana" , "Messi " , "Amberkhana" , "Rajshahi" , "443");
            passengerListDataArrayList.add(passengerListData);
        }
        passengerListAdapter = new PassengerListAdapter(PassengerHisotryActivity.this, passengerListDataArrayList);
        binding.listview.setAdapter(passengerListAdapter);
        binding.listview.setClickable(true);
        binding.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(PassengerHisotryActivity.this, PassengerHistoryDetailsActivity.class);
                intent.putExtra("name", "rana");
                intent.putExtra("RiderName", "messi");
                intent.putExtra("From", "Amberkhana");
                intent.putExtra("To", "Rajshahi");
                intent.putExtra("Fare", "433");
                startActivity(intent);
            }
        });

    }
}