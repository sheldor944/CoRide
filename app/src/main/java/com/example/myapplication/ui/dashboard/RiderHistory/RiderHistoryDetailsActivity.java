package com.example.myapplication.ui.dashboard.RiderHistory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityPassengerHistoryDetailsBinding;
import com.example.myapplication.databinding.ActivityRiderHistoryDetailsBinding;

public class RiderHistoryDetailsActivity extends AppCompatActivity {
    ActivityRiderHistoryDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRiderHistoryDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = this.getIntent();
        if (intent != null){
           String riderName = intent.getStringExtra("RiderName");
           String passengerName = intent.getStringExtra("PassengerName");
           String phone = intent.getStringExtra("Phone");
           String Fare = intent.getStringExtra("Fare");
            binding.detailName.setText(riderName);
            binding.FromTextView.setText(passengerName);
            binding.ToTextView.setText(phone);
            binding.FareTextView.setText(Fare);
//            binding.detailImage.setImageResource(image);
        }
    }
}