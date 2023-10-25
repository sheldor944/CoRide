package com.example.myapplication.ui.dashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityPassengerHisotryBinding;
import com.example.myapplication.databinding.ActivityRiderHistoryBinding;
import com.example.myapplication.ui.dashboard.RiderHistory.RiderHistoryDetailsActivity;
import com.example.myapplication.ui.dashboard.RiderHistory.RiderListAdapter;
import com.example.myapplication.ui.dashboard.RiderHistory.RiderListData;

import java.util.ArrayList;

public class RiderHistoryActivity extends AppCompatActivity {


    ActivityRiderHistoryBinding binding;
    RiderListAdapter riderListAdapter;
    ArrayList<RiderListData> riderListDataArrayList = new ArrayList<>();
    RiderListData riderListData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRiderHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        for (int i = 0; i < 10; i++){
            riderListData = new RiderListData("Rana" , "Messi " , "Amberkhana" , "Rajshahi" , "443");
            riderListDataArrayList.add(riderListData);
        }
        riderListAdapter = new RiderListAdapter(RiderHistoryActivity.this, riderListDataArrayList);

        binding.listview.setAdapter(riderListAdapter);
        binding.listview.setClickable(true);
        binding.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(RiderHistoryActivity.this, RiderHistoryDetailsActivity.class);
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