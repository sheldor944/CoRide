package com.example.myapplication.ui.dashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;

import com.example.myapplication.LocationDB;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityPassengerHisotryBinding;
import com.example.myapplication.databinding.ActivityRiderHistoryBinding;
import com.example.myapplication.helper.Callback;
import com.example.myapplication.helper.GetDataFromCompletedTableCallback;
import com.example.myapplication.helper.GetUserNameCallback;
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
    String TAG = "RiderDetailsActivity";
    String riderName="" , passengerName="" , from="" , to="" , fare=""  , type ="" , passengerID="" , phoneNumber ="" ,
            RiderName="" , RiderID="" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRiderHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        UID = FirebaseAuth.getInstance().getUid();

        LocationDB locationDB = new LocationDB();
        final int[] passengerCountInCompleteTable = {0};
        locationDB.getDataFromCompletedTable(UID, new GetDataFromCompletedTableCallback() {
            @Override
            public void onGetDataFromCompletedTableComplete(ArrayList<ArrayList<Pair<String, String>>> resultList) {
                for(ArrayList<Pair<String, String >> u : resultList)
                {
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
                        if(p.first.equals("PassengerID"))
                        {
                            passengerID = p.second;
                            Log.d("RiderHistoryActivity", "onGetDataFromCompletedTableComplete: "+passengerID  );
                        }
                        if(p.first.equals("RiderID"))
                        {
                         RiderID = p.second;
                        }
                    }
                    if(type.equals("Passenger")){
                        passengerCountInCompleteTable[0]++;

                        continue;
                    }
                    locationDB.getUserNamePhone(RiderID, passengerID, new Callback<String[]>() {
                        String RID = RiderID , PID = passengerID;
                        String Fare = fare , From = from , To =to ;
                        @Override
                        public void onComplete(String[] response) {
                            riderName = response[0];
                            passengerName = response[2];
                            phoneNumber = response[3];

                            riderListData = new RiderListData(riderName , passengerName, From , To , Fare , phoneNumber , RID,PID);
                            riderListDataArrayList.add(riderListData);

                            if(riderListDataArrayList.size() + passengerCountInCompleteTable[0] == resultList.size()){
                                riderListAdapter = new RiderListAdapter(RiderHistoryActivity.this, riderListDataArrayList);

                                binding.listview.setAdapter(riderListAdapter);
                                binding.listview.setClickable(true);
                            }
                        }
                    });
                }

            }
        });
        binding.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(RiderHistoryActivity.this, RiderHistoryDetailsActivity.class);
                RiderListData data = riderListDataArrayList.get(i);
                intent.putExtra("RiderName" , data.riderName);
                Log.d(TAG, "onItemClick: riderName");
                intent.putExtra("PassengerName" , data.passengerName);
                intent.putExtra("Phone" , data.phone);
                intent.putExtra("Fare" , data.Fare);
                startActivity(intent);
            }
        });
    }
}