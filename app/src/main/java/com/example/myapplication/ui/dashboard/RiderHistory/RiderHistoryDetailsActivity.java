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