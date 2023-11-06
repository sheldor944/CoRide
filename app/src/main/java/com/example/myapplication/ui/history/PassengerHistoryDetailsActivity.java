package com.example.myapplication.ui.history;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.databinding.ActivityPassengerHistoryDetailsBinding;

public class PassengerHistoryDetailsActivity extends AppCompatActivity {

    ActivityPassengerHistoryDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPassengerHistoryDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = this.getIntent();
        if (intent != null){
            String passenger = intent.getStringExtra("passengerName");
            String RiderName = intent.getStringExtra("riderName");
            String Fare = intent.getStringExtra("fare");
            String phone = intent.getStringExtra("phone" );
            binding.detailName.setText(passenger);
            binding.FromTextView.setText(RiderName);
            binding.ToTextView.setText(phone);
            binding.FareTextView.setText(Fare);
//            binding.detailImage.setImageResource(image);
        }
    }
}