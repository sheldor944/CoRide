package com.example.myapplication.ui.dashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.myapplication.R;
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
            String Pname = intent.getStringExtra("name");
            String RiderName = intent.getStringExtra("RiderName");
            String From = intent.getStringExtra("From");
            String To = intent.getStringExtra("To");
            String Fare = intent.getStringExtra("Fare");
            String phone = intent.getStringExtra("phone" );
            binding.detailName.setText(RiderName);
            binding.FromTextView.setText(Pname);
            binding.ToTextView.setText(phone);
            binding.FareTextView.setText(Fare);
//            binding.detailImage.setImageResource(image);
        }
    }
}