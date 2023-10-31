package com.example.myapplication.ui.dashboard.RiderHistory;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.myapplication.LocationDB;
import com.example.myapplication.R;
import com.example.myapplication.helper.Callback;
import com.example.myapplication.ui.dashboard.PassengerListData;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RiderListAdapter extends  ArrayAdapter<RiderListData> {

    public RiderListAdapter(@NonNull Context context, ArrayList<RiderListData> dataArrayList) {
        super(context, R.layout.list_item , dataArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        RiderListData listData = getItem(position);
        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.rider_list_item, parent, false);
        }
        CircleImageView listImage = view.findViewById(R.id.listImage);
        TextView listName = view.findViewById(R.id.listName);
        TextView listTime = view.findViewById(R.id.listTime);
        TextView listFare = view.findViewById(R.id.Fare);
        LocationDB locationDB = new LocationDB();
        final String currentPassengerID = listData.passengerID;
        locationDB.getImageURL(currentPassengerID,new Callback<Uri>() {
            @Override
            public void onComplete(Uri response) {
                if (currentPassengerID.equals(listData.passengerID)) {
                    Log.d("TAG", "onComplete: " + currentPassengerID + " "+ response);

                    Glide.with(getContext())
                            .load(response)
                            .skipMemoryCache(true) // You can use this line to test if caching is the issue
                            .into(listImage);
                } else {
                    Log.d("TAG", "onComplete: riderID has changed, skipping Glide load");
                }




            }
        });


        listName.setText(listData.passengerName);
        listTime.setText("Phone: "+ listData.phone);
        listFare.setText("Fare: "+listData.Fare+"৳");
        return view;
    }

}