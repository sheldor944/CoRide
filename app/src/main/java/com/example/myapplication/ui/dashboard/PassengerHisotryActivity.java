package com.example.myapplication.ui.dashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;

import com.example.myapplication.LocationDB;
import com.example.myapplication.databinding.ActivityPassengerHisotryBinding;
import com.example.myapplication.helper.GetDataFromCompletedTableCallback;
import com.example.myapplication.helper.GetUserNameCallback;
import com.example.myapplication.ui.dashboard.RiderHistory.RiderListAdapter;
import com.example.myapplication.ui.dashboard.RiderHistory.RiderListData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class PassengerHisotryActivity extends AppCompatActivity {


    private FirebaseDatabase database ;
    ActivityPassengerHisotryBinding binding;
    String TAG = "PaasengerHistoryActivity";
    PassengerListAdapter passengerListAdapter;
    ArrayList<PassengerListData> passengerListDataArrayList = new ArrayList<>();
    PassengerListData passengerListData;
    String type="",riderName="" , passengerName="" , from="" , to="" , fare="" , riderID="" ;
    String UID ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPassengerHisotryBinding.inflate(getLayoutInflater());
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
                            type = p.second;
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
                        if(p.first.equals("RiderID")){
                            riderID=p.second;
                        }
                    }
                    if(type.equals("Rider")){
                        continue;
                    }
                    locationDB.getUserName(riderID, new GetUserNameCallback() {
                        @Override
                        public void onUserNameRecieved(String name) {
                            riderName = name ;
                            System.out.println(riderName + passengerName + from + to + fare );
                            passengerListData = new PassengerListData(riderName , passengerName, from , to , fare);
                            passengerListDataArrayList.add(passengerListData);

                            if(passengerListDataArrayList.size() == resultList.size()) {
                                passengerListAdapter = new PassengerListAdapter(PassengerHisotryActivity.this, passengerListDataArrayList);
                                binding.listview.setAdapter(passengerListAdapter);
                                binding.listview.setClickable(true);
                            }
                        }
                    });

                }

            }
        });

//        for (int i = 0; i < 10; i++){
//            passengerListData = new PassengerListData("Rana" , "Messi " , "Amberkhana" , "Rajshahi" , "443");
//            passengerListDataArrayList.add(passengerListData);
//        }
//        passengerListAdapter = new PassengerListAdapter(PassengerHisotryActivity.this, passengerListDataArrayList);
//        binding.listview.setAdapter(passengerListAdapter);
        binding.listview.setClickable(true);
        binding.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(PassengerHisotryActivity.this, PassengerHistoryDetailsActivity.class);

                locationDB.getUserName(riderID, new GetUserNameCallback() {
                    @Override
                    public void onUserNameRecieved(String name) {
                        riderName=name ;
                        Log.d(TAG, "onUserNameRecieved: " + name );
                        intent.putExtra("name", passengerName);
                        intent.putExtra("RiderName", riderName);
                        intent.putExtra("From", from);
                        intent.putExtra("To", to);
                        intent.putExtra("Fare", fare);
                        startActivity(intent);
                    }
                });


            }
        });

    }
}