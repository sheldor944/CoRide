package com.example.myapplication.ui.dashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;

import com.example.myapplication.LocationDB;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityPassengerHisotryBinding;
import com.example.myapplication.databinding.ActivityRiderHistoryBinding;
import com.example.myapplication.helper.GetDataFromCompletedTableCallback;
import com.example.myapplication.ui.dashboard.RiderHistory.RiderHistoryDetailsActivity;
import com.example.myapplication.ui.dashboard.RiderHistory.RiderListAdapter;
import com.example.myapplication.ui.dashboard.RiderHistory.RiderListData;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class RiderHistoryActivity extends AppCompatActivity {


    ActivityRiderHistoryBinding binding;
    RiderListAdapter riderListAdapter;
    ArrayList<RiderListData> riderListDataArrayList = new ArrayList<>();
    RiderListData riderListData;
    String UID ;
    String riderName="" , passengerName="" , from="" , to="" , fare=""  , type ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRiderHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        UID = FirebaseAuth.getInstance().getUid();

        LocationDB locationDB = new LocationDB();
        locationDB.getDataFromCompletedTable(UID, new GetDataFromCompletedTableCallback() {
            @Override
            public void onGetDataFromCompletedTableComplete(ArrayList<ArrayList<Pair<String, String>>> resultList) {
                for(ArrayList<Pair<String, String >> u : resultList)
                {
//                    String riderName="" , passengerName="" , from="" , to="" , fare="" ;
                     for(Pair<String, String> p : u)
                     {
                         if(p.first.equals("type")){
                             type=p.second;
                         }
                         if(p.first.equals("passengerName")){
                             passengerName = p.second;
                         }
                         if(p.first.equals("riderName")){
                             riderName = p.second;
                             System.out.println(" print to oilo " + riderName);
                         }
                         if(p.first.equals("RiderStart")){
                             from = p.second;
                         }
                         if(p.first.equals("RiderDestination")){
                             to = p.second;
                         }
                         if(p.first.equals("Fair")){
                             fare = p.second;
                         }
                     }
                     if(type.equals("Passenger")){
                         continue;
                     }
                    System.out.println(riderName + passengerName + from + to + fare );
                    riderListData = new RiderListData(riderName , passengerName, from , to , fare);
                    riderListDataArrayList.add(riderListData);
                }
                riderListAdapter = new RiderListAdapter(RiderHistoryActivity.this, riderListDataArrayList);

                binding.listview.setAdapter(riderListAdapter);
                binding.listview.setClickable(true);
            }
        });

//        for (int i = 0; i < 10; i++){
//            riderListData = new RiderListData("Rana" , "Messi " , "Amberkhana" , "Rajshahi" , "443");
//            riderListDataArrayList.add(riderListData);
//        }
        System.out.println("database tone baraise ");

        binding.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(RiderHistoryActivity.this, RiderHistoryDetailsActivity.class);
                intent.putExtra("name", passengerName);
                intent.putExtra("RiderName", riderName);
                intent.putExtra("From", from);
                intent.putExtra("To", to);
                intent.putExtra("Fare", fare);
                startActivity(intent);
            }
        });
    }
}