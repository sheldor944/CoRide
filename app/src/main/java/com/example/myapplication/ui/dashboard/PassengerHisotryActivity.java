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
import com.example.myapplication.helper.Callback;
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
    String TAG = "PasengerHistoryActivity";
    PassengerListAdapter passengerListAdapter;
    ArrayList<PassengerListData> passengerListDataArrayList = new ArrayList<>();
    PassengerListData passengerListData;
    String type="",riderName="" , passengerName="" , from="" , to="" , fare="" , riderID=""  , phoneNumber="" , passengerID="";
    String UID ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPassengerHisotryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        UID = FirebaseAuth.getInstance().getUid();

        LocationDB locationDB = new LocationDB();
        final int[] RiderCountInCompleteTable = {0};

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
                            Log.d(TAG, "onGetDataFromCompletedTableComplete: " + riderID);

                        }
                        if(p.first.equals("PassengerID"))
                        {
                            passengerID = p.second;
                            Log.d(TAG, "onGetDataFromCompletedTableComplete: " + passengerID);
                        }
                    }
                    if(type.equals("Rider")){
                        RiderCountInCompleteTable[0]++;
                        continue;
                    }
                    Log.d(TAG, "test " + passengerID +" "+ riderID);
                    locationDB.getUserNamePhone(riderID, passengerID, new Callback<String[]>() {
                        String RID = riderID , PID = passengerID;
                        String Fare = fare , From = from , To =to ;
                        @Override
                        public void onComplete(String[] response) {
                            riderName = response[0];
                            passengerName = response[2];
                            phoneNumber = response[1];
                            System.out.println(riderName + passengerName + from + to + fare +" "+ PID +" "+ RID );
                            passengerListData = new PassengerListData (passengerName,riderName, From , To , Fare , phoneNumber , PID , RID);
                            passengerListDataArrayList.add(passengerListData);

                            if(passengerListDataArrayList.size()+ RiderCountInCompleteTable[0] == resultList.size()) {
                                passengerListAdapter = new PassengerListAdapter(PassengerHisotryActivity.this, passengerListDataArrayList);
                                binding.listview.setAdapter(passengerListAdapter);
                                binding.listview.setClickable(true);
                            }
                        }
                    });
                }
            }
        });

        binding.listview.setClickable(true);
        binding.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(PassengerHisotryActivity.this, PassengerHistoryDetailsActivity.class);

                PassengerListData data = passengerListDataArrayList.get(i);
                intent.putExtra("passengerName" , data.passengerName);
                intent.putExtra("riderName" , data.RiderName);
                intent.putExtra("phone" , data.phone);

                intent.putExtra("fare" ,data.Fare);
                startActivity(intent);

            }
        });

    }
}