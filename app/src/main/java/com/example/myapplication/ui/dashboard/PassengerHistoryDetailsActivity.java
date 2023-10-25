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
            String name = intent.getStringExtra("name");
            String time = intent.getStringExtra("RiderName");
            String ingredients = intent.getStringExtra("From");
            String desc = intent.getStringExtra("To");
            String image = intent.getStringExtra("Fare");
            binding.detailName.setText(name);
            binding.detailTime.setText(time);
            binding.detailDesc.setText(desc);
            binding.detailIngredients.setText(ingredients);
//            binding.detailImage.setImageResource(image);
        }
    }
}